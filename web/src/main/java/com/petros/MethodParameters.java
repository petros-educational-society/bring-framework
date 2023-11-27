package com.petros;

import java.util.HashMap;
import java.util.Map;

public class MethodParameters {
    private Integer requestBodyParamPosition = null;
    private Integer servletRequestPosition = null;
    private Integer servletResponsePosition = null;
    private final Map<String, Integer> requestHeaderToPosition = new HashMap<>();
    private final Map<String, Integer> requestParamsToPosition = new HashMap<>();
    // map between the position of pathVariable in requestMapping and the argument position
    private final Map<Integer, Integer> pathVariablesMapping = new HashMap<>();

    public MethodParameters() {}

    public void addRequestHeader(String header, int position) {
        this.requestHeaderToPosition.put(header, position);
    }

    public void addRequestParam(String name, int position) {
        this.requestParamsToPosition.put(name, position);
    }

    public void addPathVariableMapping(int mappingPosition, int parameterPosition) {
        this.pathVariablesMapping.put(mappingPosition, parameterPosition);
    }

    public int getPathVariableParamPosition(int mappingPosition){
        return pathVariablesMapping.get(mappingPosition);
    }

    public Map<String, Integer> getRequestHeaderToPosition() {
        return requestHeaderToPosition;
    }

    public Map<String, Integer> getRequestParamsToPosition() {
        return requestParamsToPosition;
    }

    public void setRequestBodyParamPosition(Integer requestBodyParamPosition) {
        this.requestBodyParamPosition = requestBodyParamPosition;
    }

    public void setServletRequestPosition(Integer servletRequestPosition) {
        this.servletRequestPosition = servletRequestPosition;
    }

    public void setServletResponsePosition(Integer servletResponsePosition) {
        this.servletResponsePosition = servletResponsePosition;
    }

    public Integer getRequestBodyParamPosition() {
        return requestBodyParamPosition;
    }

    public Integer getServletRequestPosition() {
        return servletRequestPosition;
    }

    public Integer getServletResponsePosition() {
        return servletResponsePosition;
    }
}
