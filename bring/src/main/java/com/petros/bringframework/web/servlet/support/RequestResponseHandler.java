package com.petros.bringframework.web.servlet.support;

import com.petros.bringframework.web.servlet.support.utils.Http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Produced by RequestHandlerFactory and is aimed to invoke corresponding method with arguments extracted from the web request
 * @author Serhii Dorodko
 */
public class RequestResponseHandler {
    private final Object controllerBean;
    private final Method method;
    private final Object[] invocationArguments;
    private final MethodParameters parameters;

    public RequestResponseHandler(Method method, MethodParameters parameters, List<String> pathVariables, Object controllerBean) {
        this.controllerBean = controllerBean;
        this.method = method;
        this.parameters = parameters;
        this.invocationArguments = new Object[method.getParameterCount()];
        for (int i = 0; i < pathVariables.size(); i++) {
            invocationArguments[parameters.getPathVariableParamPosition(i)] = pathVariables.get(i);
        }
    }

    public void invoke(HttpServletRequest req, HttpServletResponse resp) {

        for (Map.Entry<String, Integer> entry : parameters.getRequestHeaderToPosition().entrySet()) {
            var header = req.getHeader(entry.getKey());
            invocationArguments[entry.getValue()] = header;
        }

        for (Map.Entry<String, Integer> entry : parameters.getRequestParamsToPosition().entrySet()) {
            var param = req.getParameter(entry.getKey());
            invocationArguments[entry.getValue()] = param;
        }

        if (parameters.getRequestBodyParamPosition() != null)
            invocationArguments[parameters.getRequestBodyParamPosition()] = Http.getBodyAsString(req );

        if (parameters.getServletRequestPosition() != null)
            invocationArguments[parameters.getServletRequestPosition()] = req;

        if (parameters.getServletResponsePosition() != null)
            invocationArguments[parameters.getServletResponsePosition()] = resp;

        Object result;
        try {
            result = method.invoke(controllerBean, invocationArguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        // TODO Add support of custom classes and mapping to json/xml
        if (result instanceof String str){
            Http.writeResult(str, resp);
            return;
        }

        if (result instanceof byte[] bytes) {
            Http.writeResult(bytes, resp);
        }
    }
}
