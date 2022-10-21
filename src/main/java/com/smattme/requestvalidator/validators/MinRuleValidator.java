package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;

public class MinRuleValidator implements RuleValidator {

    /**
     * This function checks the length of value
     * usage min:10
     * @param rule containing the min size required
     * @param value to check against
     * @return ValidationResult object
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {
        int size = Integer.parseInt(rule.getParameters().get(0));
        return value != null && String.class.isAssignableFrom (value.getClass()) && value.toString().length() >= size
                ? ValidationResult.success()
                : ValidationResult.failed(rule.getKey() + " requires a min length of " + size + " chars");
    }
}
