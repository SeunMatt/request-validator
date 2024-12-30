package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;
import com.smattme.requestvalidator.helpers.ValidationHelper;

public class RequiredWithRuleValidator implements RuleValidator {

    /**
     * This function will validate that value EXIST if ALL the
     * parameters exist. Otherwise, it will return ValidationResult.success() without evaluating the expression
     * For example: requiredWith:field1,field2
     * It'll return ValidationResult.success() if field1 and field2 are present and value is valid
     * It'll return ValidationResult.success() if any of field1 and field2 is not present
     * It'll return ValidationResult.failed() if field1 and field2 are present and value is not valid
     * usage: requiredWith:field1,field2,fieldN
     * If field1,field
     *
     * @param value to be checked
     * @param rule  conditional fields. ALL the fields have to pass required for the value to be tested
     * @return validationResult object
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {

        if (ValidationHelper.allFieldExists(rule.getParameters(), rule.getJsonPathObject())) {
            //all the conditional fields passed the required test
            return ValidationHelper.isValidRequired(value) ? ValidationResult.success() :
                   ValidationResult.failed(
                           "The " + rule.getKey() + " field is required when " + String.join(" and ", rule.getParameters())
                                   + " is present");
        }

        //one or more of the conditional fields is/are not present. No need to validate this value
        return ValidationResult.success();
    }

    /**
     * This rule does not honour optional
     * It means, the {@link com.smattme.requestvalidator.RequestValidator} should still
     * execute this rule even when the user supplied optional.
     *
     * @return false
     */
    @Override
    public boolean isOptionalAllowed() {
        return false;
    }
}
