package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;
import com.smattme.requestvalidator.helpers.ValidationHelper;

public class RequiredWithoutRuleValidator implements RuleValidator {


    /**
     * this will test that the value is required if ALL
     * the parameters supplied does not exist
     * usage: requiredWithout:field1,field2,fieldN
     * @param value to be tested
     * @param rule object
     * @return ValidationResult object
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {

        if (ValidationHelper.noFieldExists(rule.getParameters(), rule.getJsonPathObject())) {
            return ValidationHelper.isValidRequired(value) ? ValidationResult.success() :
                    ValidationResult.failed("The " + rule.getKey() + " field is required when " + String.join(" and ", rule.getParameters()) + " is not present");
        }

        //one or more fields do exist so skip and return true
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
