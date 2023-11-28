package com.petros.bringframework.web.servlet.support.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Serhii Dorodko
 */
public class RequestMappingParser {
    private static final String PLACE_HOLDER_PATTERN = "\\{(.+?)\\}";

    public static Map<String, Integer> getPlaceHolders(String input){
        Map<String, Integer> placeHolders = new HashMap<>();
        Pattern pattern = Pattern.compile(PLACE_HOLDER_PATTERN);
        Matcher matcher = pattern.matcher(input);

        int i = 0;
        while (matcher.find()) {
            placeHolders.put(matcher.group(1), i++);
        }
        return placeHolders;
    }

    // user/{id}/books/{bookId} -> user/(\\+w)/book/(\\+w)
    public static String replacePlaceHolders(String input) {
        return input.replaceAll(PLACE_HOLDER_PATTERN, "(\\\\w+)");
    }
}
