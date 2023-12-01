package com.petros.bringframework.web.servlet.support;

import com.petros.bringframework.web.context.annotation.PathVariable;
import com.petros.bringframework.web.context.annotation.RequestBody;
import com.petros.bringframework.web.context.annotation.RequestHeader;
import com.petros.bringframework.web.context.annotation.RequestMapping;
import com.petros.bringframework.web.context.annotation.RequestParam;
import com.petros.bringframework.web.servlet.support.common.RequestMethod;
import com.petros.bringframework.web.servlet.support.utils.RequestMappingParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a method within the Controller that processes a particular RequestMapping
 * @author Serhii Dorodko
 */
public class RequestHandlerFactory {
    private final Method controllerMethod;
    private final Object controllerBean;
    private final RequestMethod requestMethod;
    private final String requestMapping;
    private final Pattern pattern;
    private final MethodParameters methodParameters = new MethodParameters();

    public boolean isMatching(RequestMethod requestMethod, String path){
        Matcher matcher = pattern.matcher(path);
        return requestMethod == this.requestMethod && matcher.matches();
    }

    public RequestHandlerFactory(Method controllerMethod, Object controllerBean) {
        if (!controllerMethod.isAnnotationPresent(RequestMapping.class)) throw new IllegalArgumentException();
        var methodAnnotation = controllerMethod.getAnnotation(RequestMapping.class);

        this.requestMethod = methodAnnotation.method();
        this.controllerBean = controllerBean;
        this.requestMapping = methodAnnotation.path();
        this.controllerMethod = controllerMethod;
        this.pattern = Pattern.compile(RequestMappingParser.replacePlaceHolders(requestMapping));

        var params = controllerMethod.getParameters();
        var placeHolders = RequestMappingParser.getPlaceHolders(methodAnnotation.path());
        for (int position = 0; position < params.length; position++) {
            var param = params[position];
            if (param.isAnnotationPresent(RequestHeader.class))
                methodParameters.addRequestHeader(param.getAnnotation(RequestHeader.class).name(), position);
            else if (param.isAnnotationPresent(RequestParam.class))
                methodParameters.addRequestParam(param.getAnnotation(RequestParam.class).name(), position);
            else if (param.isAnnotationPresent(PathVariable.class)){
                String requestVarName = param.getAnnotation(PathVariable.class).name();
                var placeHolderOrder = placeHolders.get(requestVarName); // TODO validate that place-holders and declared parameters as @PathVariable DO match
                methodParameters.addPathVariableMapping(placeHolderOrder, position);
            }
            else if (param.isAnnotationPresent(RequestBody.class))
                this.methodParameters.setRequestBodyParamPosition(position);
            else if (param.getType().equals(HttpServletRequest.class))
                this.methodParameters.setServletRequestPosition(position);
            else if (param.getType().equals(HttpServletResponse.class))
                this.methodParameters.setServletResponsePosition(position);
            else continue; // TODO insert the default value for unAnnotated primitive parameters
            // TODO consider adding request & response params
        }
    }


    public RequestResponseHandler getHandler(String path) {
        return new RequestResponseHandler(controllerMethod, methodParameters, extractPathVariables(path), controllerBean);
    }

    private List<String> extractPathVariables(String path){
        Matcher matcher = pattern.matcher(path);
        List<String> pathVariables = new ArrayList<>();
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                pathVariables.add(matcher.group(i));
            }
        }
        return pathVariables;
    }

    public String getRequestMapping() {
        return requestMapping;
    }
}
