package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;

public class OptionalRuleValidator implements RuleValidator {


    /**
     * This is just a placeholder for the optional rule.
     * The optional rule is a mere indicator used by other rules
     * therefore, there's nothing to validate and this method will
     * always return success.
     *
     * @param value for the field
     * @param rule  the rule to validate
     * @return ValidationResult.success() all the time
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {
        return ValidationResult.success();
    }
}
