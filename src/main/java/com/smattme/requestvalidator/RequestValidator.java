/*
 * Created by Seun Matt <smatt382@gmail.com>
 * on 30 - 1 - 2019
 * Refactored to be an opensource library on
 * 20-10-2022
 */

package com.smattme.requestvalidator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.smattme.requestvalidator.config.SpringContextAwareObjectMapperFactory;
import com.smattme.requestvalidator.exceptions.RequestValidatorException;
import com.smattme.requestvalidator.helpers.ValidationHelper;
import com.smattme.requestvalidator.validators.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class RequestValidator {

	private static final Logger logger = LoggerFactory.getLogger(RequestValidator.class);

	protected static Map<String, Class<? extends RuleValidator>> ruleValidatorMap = new HashMap<>();
	static {
		ruleValidatorMap.put("required", RequiredRuleValidator.class);
		ruleValidatorMap.put("numeric", NumericRuleValidator.class);
		ruleValidatorMap.put("digit", DigitRuleValidator.class);
		ruleValidatorMap.put("integer", DigitRuleValidator.class);
		ruleValidatorMap.put("email", EmailRuleValidator.class);
		ruleValidatorMap.put("regex", RegexRuleValidator.class);
		ruleValidatorMap.put("min", MinRuleValidator.class);
		ruleValidatorMap.put("length", LengthRuleValidator.class);
		ruleValidatorMap.put("max", MaxRuleValidator.class);
		ruleValidatorMap.put("in", InRuleValidator.class);
		ruleValidatorMap.put("requiredIf", RequiredIfRuleValidator.class);
		ruleValidatorMap.put("requiredWith", RequiredWithRuleValidator.class);
		ruleValidatorMap.put("requiredWithAny", RequiredWithAnyRuleValidator.class);
		ruleValidatorMap.put("requiredWithout", RequiredWithoutRuleValidator.class);
		ruleValidatorMap.put("requiredWithoutAny", RequiredWithoutAnyRuleValidator.class);
		ruleValidatorMap.put("array", ArrayNotEmptyRuleValidator.class);
		ruleValidatorMap.put("optional", OptionalRuleValidator.class);
	}


	public static List<String> validate(Object target, Map<String, String> rules) {
		String jsonRequest = convertObjectRequestToJsonString(target);
		return validate(jsonRequest, rules, ruleValidatorMap);
	}

	public static List<String> validate(String jsonRequest, Map<String, String> rules, Map<String, Class<? extends RuleValidator>> ruleValidatorMap) {

		//convert target into Json Document
		Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonRequest);
		Set<String> keys = rules.keySet();

		return keys.parallelStream()
				.map(key -> evaluateAllRulesForAKey(document, rules, key, ruleValidatorMap))
				.filter(errors -> !errors.isEmpty())
				.reduce((errorList1, errorList2) -> {
					errorList1.addAll(errorList2);
					return errorList1;
				}).orElse(Collections.emptyList());
	}

	protected static String convertObjectRequestToJsonString(Object requestBody) {
		ObjectMapper objectMapper = SpringContextAwareObjectMapperFactory.getObjectMapper();
		try {
			return objectMapper.writeValueAsString(requestBody);
		} catch (JsonProcessingException e) {
			throw new RequestValidatorException(e);
		}
	}


	/**
	 * This function will evaluate all the rules in a rulesMap provided for a single key
	 * Let's assume we have a rules map as follows:
	 <pre>
	 {@code
	 	Map<String, String> rules = new HashMap<>();
	 	rules.put("title", "required|max:200");
	 	rules.put("email", "required|email");
	 }</pre>
	 * This function will process all the rules that applies to title when
	 * invoked with the key = title
	 * That is, it will validate that title is present and not null
	 * and does not contain more than 200 characters
	 * Notice how the title key itself has 2 rules: required and max:200.
	 * This function will delegate to {@link #evaluateSingleRule} to
	 * process each rule for a single key
	 * @param document object representing the entire HTTP request body
	 * @param rulesMap containing what rules to apply to what key
	 * @param key the key under processing
	 * @param ruleValidatorMap this is a map of {@code rule => validatorClass}
	 * @return a list of errors and violations if any
	 */
	protected static List<String> evaluateAllRulesForAKey(Object document, Map<String, String> rulesMap, String key, Map<String, Class<? extends RuleValidator>> ruleValidatorMap) {

		/*
			these are rulesMap (e.g. max:250|min:10) to be applied to the key (e.g. firstName) under processing.
			It can be more than one, in which case it'll be separate by the pipe character |
		 */
		String commaSeparatedRule = rulesMap.get(key);

		if (commaSeparatedRule == null || commaSeparatedRule.isEmpty()) {
			//this key doesn't have a rule. Might be a misconfiguration. Let's log a warning
			logger.warn("Key: {} does not have any rule set mapped", key);
			return Collections.emptyList();
		}

			/*
				split the rule based on separator.
				if the rule set contains `regex` rule, then double pipe character is the separator || otherwise, single | is used
			 */
		List<String> ruleList = commaSeparatedRule.contains("regex") ? Arrays.asList(commaSeparatedRule.split("\\|\\|"))
				: Arrays.asList(commaSeparatedRule.split("\\|"));
		logger.trace("Evaluating rule list: {} for Key: {}", ruleList, key);

		boolean optional = evaluateOptionalForRuleList(ruleList);

		//now evaluate each rule for  the supplied value
		return ruleList
				.stream() //using parallelStream() here means the ruleList may be processed out of order, which is not desirable
				.map(ruleString -> evaluateSingleRule(document, key, ruleString, optional, ruleValidatorMap))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}


	/**
	 * This function will evaluate a single rule in a rulesMap provided for a single key
	 * Let's assume we have a rules map as follows:
		<pre>
	 	{@code
		 Map<String, String> rules = new HashMap<>();
		 rules.put("title", "required|max:200");
		 rules.put("email", "required|email");
	 	}
	 	</pre>
	 * This function will process the rule represented by the ruleString ONLY for the provided key
	 * If this method is called with key = title and ruleString = max:200
	 * Then, it will validate that the value of title in the request
	 * is a String and does not have more than 200 characters
	 * It
	 * @param document object representing the entire HTTP request body
	 * @param ruleString a representation of the rule and its parameters separated by colon (:) e.g max:200
	 * @param key the key under processing
	 * @param optional a boolean indicating if this rule is a must or not.
	 *                   If optional == true and the document does not contain a non-null value for this
	 *                 	 key, then the function will return success since it's optional.
	 *                 	optional keys are typically defined as rules.put("title", "optional|max:200");
	 * @param ruleValidatorMap this is a map of {@code rule => validatorClass}
	 * @return an error message if there's a violation, NULL otherwise
	 */
	protected static String evaluateSingleRule(Object document, String key, String ruleString, boolean optional, Map<String, Class<? extends RuleValidator>> ruleValidatorMap) {

		//let's extract the supplied value for the key under-processing
		Object value = getValueForField(document, key);
		logger.trace("Evaluating rule: {} for Key: {}. Value: {}, Optional: {}", ruleString, key, value, optional);

		//convert ruleString to Rule object
		Rule rule = Rule.parseRule(ruleString, key);
		rule.setJsonPathObject(document); //this will make the entire request available to validators that may want to check the value of other fields

		//get the validator class for the rule under processing
		Class<? extends RuleValidator> ruleValidator = ruleValidatorMap.get(rule.getName());


		if (ruleValidator == null) {
			logger.warn("Rule: {} does not have a configured validator", rule.getName());
			return null;
		}


		try {

			//construct an instance of the validator
			RuleValidator validatorInstance = ruleValidator.getConstructor().newInstance();

			/*
				if optional is part of the ruleList for this key, and the request object does not
				have a non-null value for the key, return without executing the validator.
				If the validator does not allow optional, then the validator will still execute.
				otherwise, proceed with the validation
			 */
			if (optional && validatorInstance.isOptionalAllowed() && !ValidationHelper.isValidRequired(value)) {
				logger.trace("Skipping validation for key: {} because the value is null and it's optional", key);
				return null;
			}

			ValidationResult validationResult = validatorInstance.isValid(value, rule);
			if (!validationResult.isValid()) {
				//if validation fails, add the error to the list
				return validationResult.getError();
			}


		} catch (Exception e) {
			throw new RequestValidatorException(e);
		}

		return null;
	}


	/**
	 * this function will extract the value supplied
	 * for the key in the request object document.
	 * It will transform the key to a valid JSON Path
	 * @param document the request object from client
	 * @param key the field whose value is to be fetched
	 * @return Object which can be null if no value is found for key
	 */
	protected static Object getValueForField(Object document, String key) {
		try {
			return JsonPath.read(document, "$." + key);
		}catch (Exception e) {
			logger.error("Exception occurred while reading value for field: {}", key, e);
			return null;
		}
	}


	/**
	 * 	optional is a boolean flag that will cause {@link #evaluateSingleRule} to not execute
	 * 	the validator for a rule.
	 * 	If optional = true AND the key has a null/empty value AND the validator allows optional then
	 * 	the validator will not be executed.
	 * 	requiredIf/requiredWithAny/requiredWith/requiredWithout/requiredWithoutAny
	 * 	are included here because their presence is conditional and subsequent rules should only
	 * 	be evaluated if the required condition is satisfied.
	 * @param ruleList for a key
	 * @return true or false
	 */
	protected static boolean evaluateOptionalForRuleList(List<String> ruleList) {
		return ruleList.parallelStream().anyMatch(ruleString ->
				ruleString.startsWith("optional") ||
						ruleString.startsWith("requiredIf") ||
						ruleString.startsWith("requiredWith") ||
						ruleString.startsWith("requiredWithAny") ||
						ruleString.startsWith("requiredWithout") ||
						ruleString.startsWith("requiredWithoutAny"));
	}


}
