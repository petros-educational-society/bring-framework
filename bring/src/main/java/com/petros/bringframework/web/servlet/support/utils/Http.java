package com.petros.bringframework.web.servlet.support.utils;

import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Util class with static methods to work with HttpServlet request/response
 * @author Serhii Dorodko
 */
@Log4j2
public class Http {
    public static String getBodyAsString(HttpServletRequest request){
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            char[] charBuffer = new char[128];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            log.debug("An exception occurred while reading the input stream: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }

    public static void sendBadRequest(HttpServletResponse response){
        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            log.debug("An exception occurred while sending the error response: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static void writeResultString(String data, HttpServletResponse response){
        try {
            ServletOutputStream out = response.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            log.debug("An exception occurred while writing to the servlet output stream: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
