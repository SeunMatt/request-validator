package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;
import com.smattme.requestvalidator.helpers.ValidationHelper;

public class RequiredRuleValidator implements RuleValidator {

    /**
     * Validate that the value is required and not null.
     * Usage: required
     *
     * @param rule  object
     * @param value coming from the user/client
     * @return ValidationResult
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {
        boolean validRequired = ValidationHelper.isValidRequired(value);
        return validRequired ? ValidationResult.success() : ValidationResult.failed(rule.getKey() + " is required");
    }
}
