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

Major External Dependencies
---------------------------
- [JsonPath](https://github.com/json-path/JsonPath)
- [Jackson's ObjectMapper](https://github.com/FasterXML/jackson)

How it works
------------
The main entry point is the static `RequestValidator.validate()` method. This method does not raise an exception, rather it returns a

`List<String>` of errors. If the list is empty, it means the request data is valid, otherwise, you should return the errors to your 

user in a response format suitable for your app.

```java

```

The `RequestValidator.validate()` method uses the [JsonPath](https://github.com/json-path/JsonPath) library to navigate the JSON request body

and retrieve the user provided value. The field's value is then pass through all the specified rules to check for violations if any.

Because the project uses JsonPath, all the [json path operators](https://github.com/json-path/JsonPath#operators) also applies which makes this 

library to be even more powerful.

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
This library only works with JSON request body. Other Content-Types like XML are not yet supported


Architecture Diagram
--------------------


Contributions
=============
Contributions are welcome. Please create a PR that targets the development branch. Also, do let the author know of what you're working on to 
avoid duplicate effort

Please use the Issues on Github to report any bugs you encountered while using the library or suggest improvements


LICENSE
=======
[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Contributors
============
[Nero](https://github.com/nero990)

Author
=======
[Seun Matt](https://github.com/SeunMatt)
