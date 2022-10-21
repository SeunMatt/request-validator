package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;

public class DigitRuleValidator implements RuleValidator {

    /**
     * validateDigit accepts ONLY whole numbers that may begin with zeros.
     * To validate numbers that include decimal places see {@link NumericRuleValidator}
     * usage: digit
     * @param value value
     * @return ValidationResult
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {
        if(value != null && value.toString().isEmpty() && value.toString().matches("[0-9]+")) {
            return ValidationResult.success();
        }
        return ValidationResult.failed(rule.getKey() + " parameter requires only digit values");
    }
}
