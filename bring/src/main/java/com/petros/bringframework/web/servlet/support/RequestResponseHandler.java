package com.petros.bringframework.web.servlet.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.petros.bringframework.web.servlet.support.mapper.DataMapper;
import com.petros.bringframework.web.servlet.support.utils.Http;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Produced by RequestHandlerFactory and is aimed to invoke corresponding method with arguments extracted from the web request
 *
 * @author Serhii Dorodko
 */
@Log4j2
public class RequestResponseHandler {
    private final DataMapper mapper;
    private final Object controllerBean;
    private final Method method;
    private final Object[] invocationArguments;
    private final MethodParameters parameters;

    public RequestResponseHandler(Method method,
                                  MethodParameters parameters,
                                  List<String> pathVariables,
                                  Object controllerBean,
                                  DataMapper mapper) {
        this.mapper = mapper;
        this.controllerBean = controllerBean;
        this.method = method;
        this.parameters = parameters;
        this.invocationArguments = new Object[method.getParameterCount()];
        for (int i = 0; i < pathVariables.size(); i++) {
            if (parameters.getPathVariableParamPosition(i) != null)
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

        if (parameters.getRequestBodyParamPosition() != null) {
            handleRequestBody(req, resp);
        }

        if (parameters.getServletRequestPosition() != null)
            invocationArguments[parameters.getServletRequestPosition()] = req;

        if (parameters.getServletResponsePosition() != null)
            invocationArguments[parameters.getServletResponsePosition()] = resp;

        Object invocationResult;
        try {
            invocationResult = method.invoke(controllerBean, invocationArguments);
        } catch (IllegalArgumentException e) {
            Http.sendBadRequest(resp);
            return;
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.debug("Exception occurred while invoking method: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        if (invocationResult != null) handleInvocationResult(invocationResult, resp);
    }

    private Class<?> getReqBodyClass(Method method) {
        return method.getParameterTypes()[parameters.getRequestBodyParamPosition()];
    }

    private void handleRequestBody(HttpServletRequest req, HttpServletResponse resp) {
        Class<?> requestBodyType = getReqBodyClass(method);
        String reqBody = Http.getBodyAsString(req);
        var argumentPosition = parameters.getRequestBodyParamPosition();
        Object argument = null;
        if (requestBodyType.isInstance(String.class))
            argument = reqBody;
        else {
            try {
                argument = mapper.readValue(reqBody, requestBodyType);
            } catch (JsonProcessingException e) {
                Http.sendBadRequest(resp);
            }
        }
        invocationArguments[argumentPosition] = argument;
    }

    private void handleInvocationResult(Object invocationResult, HttpServletResponse resp) {
        if (invocationResult instanceof String str) {
            Http.writeResult(str, resp);
            return;
        }

        if (invocationResult.getClass().isPrimitive()) {
            Http.writeResult(invocationResult.toString(), resp);
            return;
        }

        if (invocationResult instanceof byte[] bytes) {
            Http.writeResult(bytes, resp);
            return;
        }

        var clazz = invocationResult.getClass();
        String json;
        try {
            json = mapper.writeValueAsString(clazz.cast(invocationResult));
        } catch (JsonProcessingException e) {
            log.debug("Error while writing controller method invocation result to response body.");
            throw new RuntimeException(e);
        }
        Http.writeResult(json, resp);
    }
}
