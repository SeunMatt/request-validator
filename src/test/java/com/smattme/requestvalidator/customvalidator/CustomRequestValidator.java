package com.smattme.requestvalidator.customvalidator;

import com.smattme.requestvalidator.RequestValidator;

import java.util.List;
import java.util.Map;

public class CustomRequestValidator extends RequestValidator {

    static {
        ruleValidatorMap.put("customprefix", PrefixRuleValidator.class);
    }

    public static List<String> validate(Object target, Map<String, String> rules) {
        String jsonRequest = convertObjectRequestToJsonString(target);
        return validate(jsonRequest, rules, ruleValidatorMap);
    }


}
