package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;
import com.smattme.requestvalidator.helpers.ValidationHelper;

public class RequiredWithAnyRuleValidator implements RuleValidator {

    /**
     * this will Validate value exists if any of the attributes in the parameters exists.
     * usage: requiredWithAny:field1,field2,fieldN
     * @param value value to be checked
     * @param rule the Rule object
     * @return validationResult object
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {

        if (ValidationHelper.anyFieldExists(rule.getParameters(), rule.getJsonPathObject())) {
            //one or more of the parameters exists run validation
            return ValidationHelper.isValidRequired(value) ? ValidationResult.success() :
                    ValidationResult.failed("The " + rule.getKey() + " field is required when " + String.join("/", rule.getParameters()) + " is present");
        }

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
