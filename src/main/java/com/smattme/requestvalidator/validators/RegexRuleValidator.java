package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class RegexRuleValidator implements RuleValidator {

    private static final Logger logger = LoggerFactory.getLogger(RegexRuleValidator.class);


    /**
     * Use to validate a value conforms to a regex pattern
     * usage: required||regex:pattern
     * It should be separated from other rules using double pipe i.e. ||
     * The reason is that at times, the pattern itself can contain a single pipe | char. Thus
     * splitting it lead to undesired results
     *
     * @param rule  object
     * @param value to be checked
     * @return ValidationResult object
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {

        String pattern = rule.getParameters().isEmpty() ? "" : rule.getParameters().get(0);
        logger.trace("Supplied regex pattern: {}", pattern);
        if (value != null && String.class.isAssignableFrom(value.getClass()) && Pattern.compile(pattern).matcher(value.toString())
                .matches()) {
            return ValidationResult.success();
        }
        return ValidationResult.failed("Invalid pattern supplied for " + rule.getKey());
    }
}
