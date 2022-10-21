package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;

public class InRuleValidator implements RuleValidator {


    /**
     * This will validate that the value supplied is part of
     * the parameters.
     * The validation will fail if the value is null
     * Usage: in:foo,bar,...
     * @param rule the rule object
     * @param value      value
     * @return boolean
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {
        if(value != null && rule.getParameters().contains(value.toString())) {
            return ValidationResult.success();
        }

        return ValidationResult.failed("The selected " + rule.getKey() + " is invalid. " +
                "(Valid " + rule.getKey() + ": " + String.join(", ", rule.getParameters()) + ")");
    }
}
