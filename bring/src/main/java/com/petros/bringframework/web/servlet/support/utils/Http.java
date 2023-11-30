package com.petros.bringframework.web.servlet.support.utils;

import com.google.gson.Gson;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Util class with static methods to work with HttpServlet request/response
 * @author Serhii Dorodko
 */
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
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }

    public static void sendBadRequest(HttpServletResponse response){
        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeResult(String data, HttpServletResponse response){
        try {
            ServletOutputStream out = response.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeResult(byte[] bytes, HttpServletResponse response){
        try {
            ServletOutputStream out = response.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
