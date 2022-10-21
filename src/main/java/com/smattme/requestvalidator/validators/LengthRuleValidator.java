package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;

public class LengthRuleValidator implements RuleValidator {

    @Override
    public ValidationResult isValid(Object value, Rule rule) {
        int size = Integer.parseInt(rule.getParameters().get(0));
        if(value != null && (value.toString()).length() == size) {
            return ValidationResult.success();
        }
        return ValidationResult.failed(rule.getKey() + " requires an exact length of " + size + " chars");
    }

}
