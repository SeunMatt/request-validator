package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;

import java.util.regex.Pattern;

public class EmailRuleValidator implements RuleValidator {

    protected static final String EMAIL_PATTERN_STR =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    protected static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN_STR);

    @Override
    public ValidationResult isValid(Object email, Rule rule) {
        if (email != null && !email.toString().isEmpty() && emailPattern.matcher(email.toString()).matches()) {
            return ValidationResult.success();
        }
        return ValidationResult.failed(rule.getKey() + " supplied is invalid");
    }
}
