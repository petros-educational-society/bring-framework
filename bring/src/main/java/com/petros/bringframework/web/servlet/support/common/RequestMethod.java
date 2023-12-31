package com.petros.bringframework.web.servlet.support.common;

/**
 * @author Serhii Dorodko
 */
public enum RequestMethod {
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    public static RequestMethod resolve(String method) {
        return switch (method) {
            case "GET" -> GET;
            case "HEAD" -> HEAD;
            case "POST" -> POST;
            case "PUT" -> PUT;
            case "PATCH" -> PATCH;
            case "DELETE" -> DELETE;
            case "OPTIONS" -> OPTIONS;
            case "TRACE" -> TRACE;
            default -> null;
        };
    }
}
