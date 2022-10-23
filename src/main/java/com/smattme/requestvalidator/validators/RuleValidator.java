package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;

public interface RuleValidator {

    ValidationResult isValid(Object value, Rule rule);

    default boolean isOptionalAllowed() {
        return true;
    }

}
