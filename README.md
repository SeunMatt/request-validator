Request Validator
=================

This is a utility library for validating JSON HTTP request body for Java web applications. 

Request validator allows you to specify one or more rules that a request body must satisfy before 

it can be processed further in the controller.

Major Advantages of Request Validator includes:

- You can validate deeply nested data using simple dot notations 
- You'll get ALL the violations at a time and not one after the other
- You can provide more than one rule at the same time without littering your code with annotations 
- It's a simple plug-and-play that can work with any web framework, just invoke the validate method and you're good to go


Usage
======

Installation
------------
```xml
<dependency>
    <groupId>com.smattme</groupId>
    <artifactId>request-validator</artifactId>
    <version>0.0.1</version>
</dependency>
```

External Dependencies
---------------------------
- [JsonPath](https://github.com/json-path/JsonPath)
- [Jackson's ObjectMapper](https://github.com/FasterXML/jackson)

How it works
------------
The main entry point is the static `RequestValidator.validate()` method. This method does not raise an exception, rather it returns a

`List<String>` of errors. If the list is empty, it means the request data is valid, otherwise, you should return the errors to your 

user in a response format suitable for your app.

Let's say we expect the following JSON request body in our controller:

```json
  {
          "firstName": "Seun",
          "lastName": "Matt",
          "email": "seunmatt@example.com",
          "dob": "23-10-2022",
          "gender": "MALE",
          "interests": [
            "Java",
            "SpringBoot"
          ],
          "preferences": {
            "emailNotificationEnabled": true,
            "frequency": 4
          },
          "kyc": {
            "idType": "SSN",
            "ssn": 123456789034,
            "address": "Lagos, Nigeria"
          },
          "investmentAmount": 1000.50,
          "investmentCurrency": "NGN"
    }
```

1. We will create a `Map<String, String>` with all the rules for the fields we want to validate:
2. We will then invoke `RequestValidator.validate()` to get possible list of errors
3. If the returned errors list is empty then, there's no exception. Otherwise, we will return 401 response with the errors

```java
@RestController
public class UserController {


    @PostMapping("/auth/signup")
    public ResponseEntity<GenericResponse> signUp(@RequestBody Map<String, Object> request) {

        Map<String, String> rules = new HashMap<>();
        rules.put("firstName", "required|max:250");
        rules.put("lastName", "required|max:250");
        rules.put("email", "required|max:250|email");
        rules.put("dob", "required||regex:[0-9]{2}-[0-9]{2}-[0-9]{4}");
        rules.put("gender", "required|in:MALE,FEMALE");
        rules.put("interests", "optional|array");
        rules.put("preferences.emailNotificationEnabled", "optional|in:true,false");
        rules.put("preferences.frequency", "optional|digit");
        rules.put("kyc.idType", "required|in:BVN,SSN");
        rules.put("kyc.bvn", "requiredIf:kyc.idType,BVN|length:11");
        rules.put("kyc.ssn", "requiredIf:kyc.idType,SSN|length:12");
        rules.put("kyc.address", "optional|max:250");
        rules.put("investmentAmount", "optional|numeric");
        rules.put("investmentCurrency", "requiredWith:investmentAmount|in:USD,NGN");

        List<String> errors = RequestValidator.validate(request, rules);
        if(!errors.isEmpty()) return ResponseEntity.badRequest().body(GenericResponse.genericValidationErrorsObj(errors));

        //otherwise all is well, process the request
        //userService.signUp()

        return ResponseEntity.ok(GenericResponse.generic200ResponseObj("Sing up successful"));

    }
}
```

The request body can also be a plain JSON string or a POJO:

```java
@RestController
public class LoginController {


    @PostMapping("/auth/login")
    public ResponseEntity<GenericResponse> login(@RequestBody LoginRequest request) {

        Map<String, String> rules = new HashMap<>();
        rules.put("email", "required|email");
        rules.put("password", "required");

        List<String> errors = RequestValidator.validate(request, rules);
        if(!errors.isEmpty()) return ResponseEntity.badRequest().body(GenericResponse.genericValidationErrorsObj(errors));

        //otherwise all is well, process the request
        //userService.signUp()

        return ResponseEntity.ok(GenericResponse.generic200ResponseObj("Login successful"));

    }
}
```


The `RequestValidator.validate()` method uses the [JsonPath](https://github.com/json-path/JsonPath) library to navigate the JSON request body

and retrieve the user provided value. The field's value is then pass through all the specified rules to check for violations if any.

Because the project uses JsonPath, all the [json path operators](https://github.com/json-path/JsonPath#operators) also applies which makes this

library to be even more powerful.

Combining Rules
---------------
You can specify more than one rule by combining the rules with a pipe `|` separator. For example, in the snippet above, the `email`

field is `required` and can't be more than 250 chars and should be a valid `email` address. All those rules are combined using the `|` as 

in `required|max:250|email`  


**Please note that the `regex` rule requires a double pipe char `||` as a separator and should be the last rule when combined with others.
This is to accommodate the potential presence of `|` in the regex pattern.**

Spring Boot Environment
------------------
If the library is used in a Spring Boot (or generally a Spring Framework) environment, 

the library will attempt to use the Jackson's `ObjectMapper` bean in the Spring's context if any. 

If none is found, it will simply instantiate another one.


Validation Rules
------------------
Below is a list of currently available rules for your use:


| Rule                             | Description                                                                                                                                                                                                                                                                                                                                                                            |
|----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| required                         | This is a very basic rule that'll ensure that the user provides a non-null/non-empty value for a field in the HTTP request body                                                                                                                                                                                                                                                        |
| array                            | This will validate that the request data is a non-empty array of elements.                                                                                                                                                                                                                                                                                                             |
| digit                            | This will validate that the request data is an integer with no decimal places                                                                                                                                                                                                                                                                                                          |
| email                            | This will validate that the request data is a valida email                                                                                                                                                                                                                                                                                                                             |
| in:VALUE1,VALUE2,VALUE3...VALUEX | This rule will validate that the user provided value for its field is among the list of comma-separated values. For example, `status => in:PENDING,ACTIVE,INACTIVE` will return an error if the user provide other value for `status` in the request that is not `PENDING`, `ACTIVE` or `INACTIVE`                                                                                     |
| length:limit                     | Where `limit` is the number of characters expected. This will ensure the request data is a String and has exactly `limit` number of characters                                                                                                                                                                                                                                         |
| max:limit                        | Where `limit` is the maximum number of characters expected. This will ensure the request data is a String and has at most `limit` number of characters                                                                                                                                                                                                                                 |
| min:limit                        | Where `limit` is the minimum number of characters expected. This will ensure the request data is a String and has at least `limit` number of characters                                                                                                                                                                                                                                |
| numeric                          | This rule will ensure the supplied data is a number, with or withouth decimal places                                                                                                                                                                                                                                                                                                   |
| regex:pattern                    | Where `pattern` is the regex pattern to match the user supplied value against. This rule will ensure the supplied data matches the provided regex pattern                                                                                                                                                                                                                              |
| requiredIf:key,value             | Where `key` is a field expected in the request body and `value` is the user provided value. This rule will ensure that a value is provided for its own field, when other field `key` == `value`                                                                                                                                                                                        |
| requiredWith:field1,field2       | This rule will validate that a non-null/non-empty value is provided for its field when ALL the comma-separated list of fields in its parameters have non-null/non-empty values. For example, `accountName => requiredWith:accountNumber,bankCode` will ensure the user also supply `accountName` field when the user provides an `accountNumber` and `bankCode`                        |
| requiredWithAny:field1,field2    | This rule will validate that a non-null/non-empty value is provided for its field when AT LEAST ONE of the comma-separated list of fields in its parameters have non-null/non-empty values. For example, `accountName => requiredWithAny:accountNumber,bankCode` will ensure the user also supply `accountName` field when the user provides either an `accountNumber` OR a `bankCode` |
| requiredWithout:field1,field2    | This rule will validate that a non-null/non-empty value is provided for its field when ALL of the comma-separated list of fields in its parameters is NOT available. For example, `fullName => requiredWithout:firstName,lastName` will validate that the user provides a `fullName` when both `firstName` and `lastName` is not provided                                              |
| requiredWithoutAny:field1,field2 | This rule will validate that a non-null/non-empty value is provided for its field when AT LEAST ONE of the comma-separated list of fields in its parameters is NOT available. For example, `fullName => requiredWithoutAny:firstName,lastName` will validate that the user provides a `fullName` when either of `firstName` or `lastName` is not provided                              |



Adding custom rule validators
-----------------------------
- Create a class that'll implement the `RuleValidator` interface and return a `ValidationResult`
- Create a subclass of the `RequestValidator` class and append your rule to the `ruleValidatorMap` of the parent class
- Use the custom sub-class in your applications

Limitations
-----------
This library only works with JSON request body. Other Content-Types like XML or plain text are not yet supported


Architecture Diagram
--------------------
- TODO

Contributions
=============
Contributions are welcome. Please create a PR that targets the development branch. Also, do let the author know of what you're working on to 
avoid duplicate effort

Please use the Issues on Github to report any bugs you encountered while using the library or suggest improvements


LICENSE
=======
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

Contributors
============
[Nero](https://github.com/nero990)

Author
=======
[Seun Matt](https://github.com/SeunMatt)
