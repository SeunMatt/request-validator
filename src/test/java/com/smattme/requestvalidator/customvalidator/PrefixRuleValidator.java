package com.smattme.requestvalidator.customvalidator;

import com.smattme.requestvalidator.Rule;
import com.smattme.requestvalidator.validators.RuleValidator;
import com.smattme.requestvalidator.validators.ValidationResult;

public class PrefixRuleValidator implements RuleValidator {
    private static final String CUSTOM_PREFIX = "custom_";
    @Override
    public ValidationResult isValid(Object value, Rule rule) {
        return value != null && String.class.isAssignableFrom(value.getClass()) &&
                value.toString().startsWith(CUSTOM_PREFIX)
                ? ValidationResult.success()
                : ValidationResult.failed(rule.getKey() + " should start with " + CUSTOM_PREFIX);
    }
}
