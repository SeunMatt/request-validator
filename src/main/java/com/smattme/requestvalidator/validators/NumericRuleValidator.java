package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;

public class NumericRuleValidator implements RuleValidator {

    /**
     * validateNumeric accept integer and floating point numbers.
     * Usage: numeric
     *
     * @param value the request value to be checked
     * @return ValidationResult
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {
        if (value != null && value.toString().matches("[0-9]+\\.?[0-9]*")) {
            return ValidationResult.success();
        }
        return ValidationResult.failed(rule.getKey() + " parameter requires only numeric values");
    }
}
