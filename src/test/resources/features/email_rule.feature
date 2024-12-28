@email_rule
Feature: Email rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | userEmail | email |
    And the following table request body:
      | userEmail | seun@example.com |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | userEmail | email |
    And the following table request body:
      | userEmail | seun_example.com |
    And validate request
    Then the returned errors should be:
      | userEmail supplied is invalid |
