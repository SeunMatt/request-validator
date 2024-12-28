package com.smattme.requestvalidator.helpers;

import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ValidationHelper {

    private static final Logger logger = LoggerFactory.getLogger(ValidationHelper.class);

    public static boolean isValidRequired(Object value) {

        if (value == null) {
            return false;
        }

        if (String.class.isAssignableFrom(value.getClass()) && value.toString().trim().isEmpty()) {
            return false;
        }

        if (List.class.isAssignableFrom(value.getClass()) && ((List<?>) value).isEmpty()) {
            return false;
        }

        return true;

    }


    public static Object getValueForField(Object document, String key) {
        try {
            return JsonPath.read(document, "$." + key);
        } catch (Exception e) {
            logger.error("Exception occurred while reading value for field: {}", key, e);
            return null;
        }
    }

    public static boolean anyFieldExists(List<String> attributes, Object document) {
        return attributes.stream().anyMatch(attribute -> isValidRequired(getValueForField(document, attribute)));
    }

    public static boolean allFieldExists(List<String> attributes, Object document) {
        return attributes.stream().allMatch(attribute -> isValidRequired(getValueForField(document, attribute)));
    }

    public static boolean noFieldExists(List<String> attributes, Object document) {
        return attributes.stream().noneMatch(attribute -> isValidRequired(getValueForField(document, attribute)));
    }
}
