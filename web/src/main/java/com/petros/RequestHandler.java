package com.petros;

import com.petros.utils.Http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class RequestHandler {
    private final Object controllerBeanDummy = Main.controllerBeanDummy;
    private final Method method;
    private final Object[] invocationArguments;
    private final MethodParameters parameters;

    public RequestHandler(Method method, MethodParameters parameters, List<String> pathVariables) {
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
            result = method.invoke(controllerBeanDummy, invocationArguments);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        if (result instanceof String){
            Http.writeResultString((String)result, resp);
        }
    }
}
