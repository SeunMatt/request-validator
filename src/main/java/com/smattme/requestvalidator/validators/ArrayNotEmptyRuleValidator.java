package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;

import java.util.List;
import java.util.Objects;

public class ArrayNotEmptyRuleValidator implements RuleValidator {

    /**
     * this method will validate that the value
     * of a request parameter is an array and the
     * array is not empty
     * usage: array
     * @param value the value to check
     * @return boolean true if the value is a non-empty array
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {

        if(Objects.nonNull(value) && List.class.isAssignableFrom(value.getClass()) && !((List<?>) value).isEmpty()) {
            return ValidationResult.success();
        }

        return ValidationResult.failed(rule.getKey() + " supplied should be a non-empty array");
    }
}
