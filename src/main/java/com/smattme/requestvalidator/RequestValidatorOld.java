/*
 * Created by Seun Matt <smatt382@gmail.com>
 * on 30 - 1 - 2019
 */

package com.smattme.requestvalidator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.smattme.requestvalidator.config.SpringContextAwareObjectMapperFactory;
import com.smattme.requestvalidator.exceptions.RequestValidatorException;
import com.smattme.requestvalidator.validators.LengthRuleValidator;
import com.smattme.requestvalidator.validators.RuleValidator;
import com.smattme.requestvalidator.validators.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

public class RequestValidatorOld {

	private static final String EMAIL_PATTERN_STR = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN_STR);
	private static final Logger logger = LoggerFactory.getLogger(RequestValidatorOld.class);

	protected static Map<String, Class<? extends RuleValidator>> ruleValidatorMap = new HashMap<>();
	static {
		ruleValidatorMap.put("length", LengthRuleValidator.class);
	}


	public static List<String> validate(String jsonRequest, Map<String, String> rules) {

		List<String> errors = new ArrayList<>();

		//convert target into Json Document
		Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonRequest);


		Set<String> keys = rules.keySet();

		keys.forEach(key -> {

			//these are rules to be applied to the key under processing
			String keyRule = rules.get(key);

			if (keyRule == null || keyRule.isEmpty()) //this key doesn't have a rule
				return;

			// split the rule based on separator
			List<String> ruleList = keyRule.contains("regex") ? Arrays.asList(keyRule.split("\\|\\|")) : Arrays.asList(keyRule.split("\\|"));
			logger.debug("rule list: " + ruleList);

			//now evaluate each rule for  the supplied value
			ruleList.parallelStream()
					.forEach(ruleString -> {

						//let's extract the supplied value for the key under-processing
						Object value = getValueForField(document, key);
						logger.debug("key: {} rule: {} value: {}", key, ruleString, value);

						//if optional is part of the ruleList for this key, and the request object does not
						//contain the key, return without performing any validation.
						//otherwise, meaning though it's optional but it was supplied as part of the request,
						//validate it
						if (ruleList.contains("optional") && value == null)
							return;

						Rule rule = parseStringRule(ruleString);
						rule.setKey(key);

						Class<? extends RuleValidator> ruleValidator = ruleValidatorMap.get(rule.getName());
						if(ruleValidator != null) {
							try {
								ValidationResult validationResult = ruleValidator.getConstructor().newInstance().isValid(value, rule);
								if(!validationResult.isValid()) {
									errors.add(validationResult.getError());
								}
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}


						List<String> parameters = rule.getParameters();


						if(rule.nameIs("required") && !validateRequired(value)) {
							errors.add(key + " is required");
						}

						if (rule.nameIs("numeric") && !validateNumeric(value)) {
							errors.add(key + " parameter requires only numeric values");
						}
						if (rule.nameIs("digit") && !validateDigit(value)) {
							errors.add(key + " parameter requires only digit values");
						}
						if (rule.nameIs("integer") && !validateDigit(value)) {
							errors.add(key + " parameter requires integer value");
						}
						if (rule.nameIs("email") && !isValidEmail(value)) {
							errors.add(key + " supplied is invalid");
						}
						if (rule.startsWith("regex") && !validateRegex(parameters, value)) {
							errors.add("Invalid pattern supplied for " + key);
						}
						if (rule.startsWith("min") && !validateMin(Integer.parseInt(parameters.get(0)), value)) {
							errors.add(key + " requires a min length of " + Integer.parseInt(parameters.get(0)) + " chars");
						}

						if (rule.nameIs("length") && !validateLength(value, Integer.parseInt(parameters.get(0)))) {
							errors.add(key + " requires an exact length of " + Integer.parseInt(parameters.get(0)) + " chars");
						}

						if (rule.startsWith("max") && !validateMax(Integer.parseInt(parameters.get(0)), value)) {
							errors.add(key + " requires a maximum length of " + Integer.parseInt(parameters.get(0)) + " chars");
						}

						if (rule.nameIs("in") && !validateIn(value, parameters)) {
							errors.add("The selected " + key + " is invalid. (Valid " + key + ": " + String.join(", ", parameters) + ")");
						}

						if (rule.nameIs("requiredIf") && !validateRequiredIf(value, document, parameters)) {
							errors.add("The " + key + " field is required when if " + parameters + ".");
						}

//						if (rule.nameIs("requiredWith") && !validateRequiredWith(value, parameters, document)) {
//							errors.add("The " + key + " field is required when " + StringUtils.join(parameters, " / ") + " is present.");
//						}
//
//						if (rule.nameIs("requiredWithAny") && !validateRequiredWithAny(value, parameters, document)) {
//							errors.add("The " + key + " field is required when " + StringUtils.join(parameters, " / ") + " is present.");
//						}
//
//						if (rule.nameIs("requiredWithout") && !validateRequiredWithout(value, parameters, document)) {
//							errors.add("The " + key + " field is required when " + StringUtils.join(parameters, " / ") + " is not present.");
//						}
//
//						if (rule.nameIs("requiredWithoutAny") && !validateRequiredWithoutAny(value, parameters, document)) {
//							errors.add("The " + key + " field is required when " + StringUtils.join(parameters, " / ") + " is not present.");
//						}

						if (rule.nameIs("array") && !validateArrayNotEmpty(value)) {
							errors.add(key + " supplied should be a non-empty array");
						}

					});
		});

		return errors;
	}


	public static List<String> validate(Object target, Map<String, String> rules) {
		ObjectMapper objectMapper = SpringContextAwareObjectMapperFactory.getObjectMapper();
		try {
			String jsonRequest = objectMapper.writeValueAsString(target);
			return validate(jsonRequest, rules);
		} catch (JsonProcessingException e) {
			throw new RequestValidatorException(e);
		}
	}

	/**
	 *
	 * @param value value
	 * @param size size
	 * @return boolean
	 */
	public static boolean validateLength(Object value, int size) {
		return value != null && (value.toString()).length() == size;
	}

	/**
	 * validateNumeric accept integer and floating point numbers
	 * @param value value
	 * @return boolean
	 */
	private static boolean validateNumeric(Object value) {
		if (value == null || String.valueOf(value).isEmpty()) return false;
		return value.toString().matches("[0-9]+\\.?[0-9]*");
	}

	/**
	 * validateDigit accepts whole numbers that may begin with zeros
	 * usage: digit
	 * @param value value
	 * @return boolean
	 */
	public static boolean validateDigit(Object value) {
		return value != null && !String.valueOf(value).isEmpty() && String.valueOf(value).matches("[0-9]+");
	}

	private static boolean isValidEmail(Object email) {
		return email != null && !((String) email).isEmpty() && emailPattern.matcher(((String) email)).matches();
	}

	/**
	 * This will validate that the value supplied is part of
	 * the parameters. The rule will be skipped
	 * if the value is null
	 *
	 * Usage => in:foo,bar,...
	 *
	 * @param parameters parameters
	 * @param value      value
	 * @return boolean
	 */
	private static boolean validateIn(Object value, List<String> parameters) {
		if(value == null) return true;
		return parameters.contains(value.toString());
	}

	/**
	 * Validate that a required attribute exists.
	 */
	private static boolean validateRequired(Object value) {
		if (value == null) return false;
		else if ((value instanceof String) && ((String) value).trim().isEmpty()) return false;
		else return !(value instanceof List) || !((List) value).isEmpty();
	}


	/**
	 * this will test that value passed is required if
	 * the conditionalField and conditionalValue evaluates to be true
	 * usage => requiredIf:conditionalField,conditionalValue
	 * @param value to test against
	 * @param document request object as sent from the client
	 * @param parameters containing the conditionalField and optional conditionalValue
	 * @return true if the validation passes or the conditional field is invalid
	 */
	private static boolean validateRequiredIf(Object value, Object document, List<String> parameters) {

		String conditionalFieldKey = parameters.get(0);
		String conditionalFieldValue = parameters.size() > 1 ? parameters.get(1) : "";
		boolean isRequired = true;

		//first validate if the request contains the conditionalFieldKey
		//then optionally validate if the conditionalFieldKey = value
		Object actualConditionalFieldValue = getValueForField(document, conditionalFieldKey);

		if(actualConditionalFieldValue == null) {
			//if the request does not contain the conditional field, no need to run validation
			isRequired = false;
		}
		else if(conditionalFieldValue != null && !conditionalFieldValue.isEmpty() && !actualConditionalFieldValue.equals(conditionalFieldValue)) {
			//if the request does contain the conditional field but its value is not equal to the conditionValue then validation is not required
			isRequired = false;
		}

		if(isRequired) return validateRequired(value);

		//otherwise no need to validate so return true to pass it
		return true;
	}


	/**
	 * this will Validate value exists if any of the attributes exists.
	 * usage => requiredWithAny:field1,field2,fieldN
	 * @param value value to be checked
	 * @param parameters conditional fields
	 * @param document request from client
	 * @return true if validation pass, false if otherwise
	 */
	private static boolean validateRequiredWithAny(Object value, List<String> parameters, Object document) {
		if (!anyFieldExists(parameters, document)) {
			//one or more of the parameters exists run validation
			return validateRequired(value);
		}

		return true;
	}

	/**
	 * this function will validate that value EXIST if ALL the
	 * parameters exist.
	 * Otherwise it will pass it as true without testing
	 * usage => requiredWith:field1,field2,fieldN
	 * @param value to be checked
	 * @param parameters conditional fields. ALL the fields have to pass required for the value to be tested
	 * @param document request as sent from client
	 * @return true if this value pass required test, false otherwise
	 */
	private static boolean validateRequiredWith(Object value, List<String> parameters, Object document) {

		if (allFieldExists(parameters, document)) {
			//all the conditional fields passed the required test so validate this
			return validateRequired(value);
		}

		//one or more of the conditional fields is/are not present. No need to validate this value
		return true;
	}

	private static boolean allFieldExists(List<String> attributes, Object document) {
		return attributes.stream().allMatch(attribute -> validateRequired(getValueForField(document, attribute)));
	}

	private static boolean anyFieldExists(List<String> attributes, Object document) {
		return attributes.stream().anyMatch(attribute -> validateRequired(getValueForField(document, attribute)));
	}

	private static boolean noFieldExists(List<String> attributes, Object document) {
		return attributes.stream().noneMatch(attribute -> validateRequired(getValueForField(document, attribute)));
	}

	/**
	 * this will test that the value is required if ALL
	 * the parameters supplied does not exist
	 * usage => requiredWithout:field1,field2,fieldN
	 * @param value to be tested
	 * @param parameters conditional params that must be missing for value to be required
	 * @param document request as sent from user
	 * @return true if validation passes, false if otherwise
	 */
	private static boolean validateRequiredWithout(Object value, List<String> parameters, Object document) {

		if (noFieldExists(parameters, document)) {
			return validateRequired(value);
		}

		//some field does exist so skip and return true
		return true;
	}

	/**
	 * this will test that the value exist if ONE OR MORE of
	 * the parameters supplied does not exist in the request
	 * usage => requiredWithoutAny:field1,field2,fieldN
	 * @param value to be tested
	 * @param parameters conditional params
	 * @param document request as sent from user
	 * @return true if validation passes, false if otherwise
	 */
	private static boolean validateRequiredWithoutAny(Object value, List<String> parameters, Object document) {

		if (!allFieldExists(parameters, document)) {
			//one or more of the fields are missing so run validation
			return validateRequired(value);
		}

		//skip validation since all of the params does not exist
		return true;
	}

	/**
	 * Parse a string based rule.
	 * Extract the rule name and parameters from a rule.
	 *
	 * @return Rule
	 */
	private static Rule parseStringRule(String rule) {
		List<String> parameters = new ArrayList<>();

		// The format for specifying validation rule and parameters follows an
		// easy {rule}:{parameters} formatting convention. For instance the
		// rule "Max:3" states that the value may only be three letters.

		if (rule.contains(":")) {
			String[] splitRule = rule.split(":", 2);
			rule = splitRule[0];
			String parameter = splitRule[1];

			parameters = parseParameters(rule, parameter);
		}
		return new Rule(rule, parameters);
	}

	/**
	 * Parse a parameter list.
	 */
	private static List<String> parseParameters(String rule, String parameter) {
        /*if (rule.toLowerCase().equals("regex")) {
            List<String> parameters = new ArrayList<>();
            parameters.add(parameter);
            return parameters;
        }*/

		return (Arrays.asList(parameter.split(",")));
	}

	/**
	 * use to validate a value conforms to a
	 * regex
	 * usage ==> required||regex:pattern
	 * it should be separated from other rules using double pipe i.e. ||
	 * @param parameters that contains the pattern
	 * @param value to be checked
	 * @return boolean
	 */
	private static boolean validateRegex(List<String> parameters, Object value) {
		if(value == null || !String.class.isAssignableFrom(value.getClass()) || parameters.isEmpty()) return false;
		logger.debug("extracted pattern: " + parameters.get(0));
		return Pattern.compile(parameters.get(0)).matcher((String)value).matches();
	}


	/**
	 * this method will validate that the value
	 * of a request parameter is an array and the
	 * array is not empty
	 * usage "images" : "array"
	 * @param value the value to check
	 * @return boolean true if the value is a non-empty array
	 */
	public static boolean validateArrayNotEmpty(Object value) {
		return !Objects.isNull(value) && List.class.isAssignableFrom(value.getClass()) && !((List<?>) value).isEmpty();
	}

	/**
	 * usage min:10
	 * @param size min size required
	 * @param value to check against
	 * @return true if the value's length is greater than size, false otherwise
	 */
	private static boolean validateMin(int size, Object value) {
		return value != null && ((String) value).length() >= size;
	}

	/**
	 * usage max:10
	 * @param size max size required
	 * @param value to check against
	 * @return true if the length of the value is less than or equal to size
	 */
	private static boolean validateMax(int size, Object value) {
		return value != null && ((String) value).length() <= size;
	}

	/**
	 * this function will extract the value supplied
	 * for the key in the request object document.
	 * It will transform the key to a valid JSON Path
	 * @param document the request object from client
	 * @param key the field whose value is to be fetched
	 * @return Object which can be null if no value is found for key
	 */
	private static Object getValueForField(Object document, String key) {
		try {
			return JsonPath.read(document, "$." + key);
		}catch (Exception e) {
			logger.error("Exception occurred while reading value for field: {}", key, e);
			return null;
		}
	}


}
