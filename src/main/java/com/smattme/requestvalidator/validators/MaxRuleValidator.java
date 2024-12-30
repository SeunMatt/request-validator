package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;

public class MaxRuleValidator implements RuleValidator {

    /**
     * This will validate that the value is a String and does not contain
     * more than max chars.
     * Usage: max:10
     *
     * @param rule  object containing the max size required
     * @param value to check against
     * @return VaValidationResult object
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {
        int size = Integer.parseInt(rule.getParameters().get(0));
        return value != null && String.class.isAssignableFrom(value.getClass()) && value.toString().length() <= size
               ? ValidationResult.success()
               : ValidationResult.failed(rule.getKey() + " requires a maximum length of " + size + " chars");
    }
}
