package com.smattme.requestvalidator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smattme.requestvalidator.customvalidator.CustomRequestValidator;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.DocStringType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleTestStepDefinitions {

    private static final Logger logger = LoggerFactory.getLogger(RuleTestStepDefinitions.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, String> rules = new HashMap<>();
    private Map<String, Object> request = new HashMap<>();
    private List<String> errors = new ArrayList<>();


    @DocStringType(contentType = "json")
    public Map<String, Object> convertToMap(String docString) throws IOException {
        return objectMapper.readValue(docString, new TypeReference<Map<String, Object>>() {});
    }


    @Given("the following rules:")
    public void theFollowingRules(DataTable dataTable) {
        rules = dataTable.asMap();
    }

    @And("the following json request body:")
    public void theFollowingJsonRequestBody(Map<String, Object> request) {
        this.request = request;
    }

    @And("the following table request body:")
    public void theFollowingRequestBody(DataTable dataTable) {
        request = dataTable.asMap(String.class, Object.class);
    }

    @And("validate request")
    public void validateRequest() {
       errors = RequestValidator.validate(request, rules);
    }

    @And("validate custom request")
    public void validateCustomRequest() {
        errors = CustomRequestValidator.validate(request, rules);
    }

    @Then("no errors should be returned")
    public void noErrorsShouldBeReturned() {
        Assertions.assertTrue(errors.isEmpty(), "Expected no errors but the following errors were returned: " + errors);
    }

    @Then("the returned errors should be:")
    public void noErrorsShouldBeReturned(DataTable dataTable) {
        List<String> expectedErrors = dataTable.asList();
        Assertions.assertIterableEquals(expectedErrors, errors,
                "Returned errors: " + errors + " does not contain all of the expected errors: " + expectedErrors);
    }


}
