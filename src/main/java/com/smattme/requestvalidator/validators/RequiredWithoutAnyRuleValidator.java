package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;
import com.smattme.requestvalidator.helpers.ValidationHelper;

public class RequiredWithoutAnyRuleValidator implements RuleValidator {


    /**
     * this will test that the value exist if ONE OR MORE of
     * the parameters supplied does not exist in the request
     * usage: requiredWithoutAny:field1,field2,fieldN
     * @param value to be tested
     * @param rule object
     * @return ValidationResult object
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {

        if (!ValidationHelper.allFieldExists(rule.getParameters(), rule.getJsonPathObject())) {
            //one or more of the fields are missing so run validation
            return ValidationHelper.isValidRequired(value) ? ValidationResult.success()
                    : ValidationResult.failed("The " + rule.getKey() + " field is required when " + String.join("/", rule.getParameters()) + " is not present");
        }

        //skip validation since all of params does not exist
        return ValidationResult.success();
    }

    /**
     * This rule does not honour optional
     * It means, the {@link com.smattme.requestvalidator.RequestValidator} should still
     * execute this rule even when the user supplied optional.
     * @return false
     */
    @Override
    public boolean isOptionalAllowed() {
        return false;
    }
}
