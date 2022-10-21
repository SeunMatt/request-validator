package com.smattme.requestvalidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rule {

    private String key;
    private String name;
    private List<String> parameters;

    /*
        this holds the entire request object and can be referenced
        from individual validators
     */
    private Object jsonPathObject;

    Rule(String name, List<String> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    Rule(String name, String key, List<String> parameters) {
        this.name = name;
        this.parameters = parameters;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public boolean nameIs(String name) {
        return this.name.equals(name);
    }

    public boolean startsWith(String name) {
        return this.name.startsWith(name);
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public Object getJsonPathObject() {
        return jsonPathObject;
    }

    public void setJsonPathObject(Object jsonPathObject) {
        this.jsonPathObject = jsonPathObject;
    }

    /**
     * This function convert a string based rule like max:3 into the Rule class
     * It extract the rule name and parameters from the string representation of the rule
     * The format for specifying validation rule and parameters follows an
     *  easy {rule}:{parameters} formatting convention. For instance the
     *  rule "max:3" states that the length of the value must not exceed 3 characters.
     * @param rule the string representation of the rule
     * @param requestKey this is the name of the property from the request.
     *                   E.g. {"email": "smatt@example.com"}, email is the request key
     * @return an instance of Rule class
     */
    public static Rule parseRule(String rule, String requestKey) {
        List<String> parameters = new ArrayList<>();
        if (rule.contains(":")) {
            String[] splitRule = rule.split(":", 2);
            rule = splitRule[0];
            String parameter = splitRule[1];
            parameters = Arrays.asList(parameter.split(","));
        }
        return new Rule(rule, requestKey, parameters);
    }
}
