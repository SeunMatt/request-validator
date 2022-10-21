package com.smattme.requestvalidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestValidatorUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(RequestValidatorUnitTest.class);

    @Test
    void initTest() {
        Map<String, String> rules = new HashMap<>();
        rules.put("title", "numeric|length:20");

        Map<String, Object> request = new HashMap<>();
        request.put("title", "This");

        List<String> errors = RequestValidator.validate(request, rules);
        logger.debug("Errors: {}", errors);
        Assertions.assertFalse(errors.isEmpty());
        Assertions.assertTrue(errors.contains("title requires an exact length of 20 chars"));
        Assertions.assertTrue(errors.contains("title parameter requires only numeric values"));
    }


}
