package com.smattme.requestvalidator.validators;

import com.smattme.requestvalidator.Rule;
import com.smattme.requestvalidator.helpers.ValidationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RequiredIfRuleValidator implements RuleValidator {

    private static final Logger logger = LoggerFactory.getLogger(RequiredIfRuleValidator.class);


    /**
     * this will test that value passed is required if
     * the conditionalField and conditionalValue evaluates to be true
     * usage: requiredIf:conditionalField,conditionalValue
     * @param value to validate against
     * @param rule object
     * @return ValidationResult object
     */
    @Override
    public ValidationResult isValid(Object value, Rule rule) {

        List<String> parameters = rule.getParameters();
        String conditionalFieldKey = parameters.get(0);
        String conditionalFieldValue = parameters.size() > 1 ? parameters.get(1) : "";
        boolean isRequired = true;

        //first validate if the request contains the conditionalFieldKey
        //then optionally validate if the conditionalFieldKey = value
        Object actualConditionalFieldValue = ValidationHelper.getValueForField(rule.getJsonPathObject(), conditionalFieldKey);

        if(actualConditionalFieldValue == null) {
            //if the request does not contain the conditional field, no need to run validation
            isRequired = false;
        }
        else if(conditionalFieldValue != null && !conditionalFieldValue.isEmpty() && !actualConditionalFieldValue.equals(conditionalFieldValue)) {
            //if the request does contain the conditional field but its value is not equal to the conditionValue then validation is not required
            isRequired = false;
        }

        if(isRequired) {
            return ValidationHelper.isValidRequired(value) ? ValidationResult.success() :
                    ValidationResult.failed("The " + rule.getKey() + " field is required when if " + String.join(" = ", parameters));
        }

        //otherwise no need to validate so return true to pass it
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
