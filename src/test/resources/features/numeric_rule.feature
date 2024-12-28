@numeric_rule
Feature: Numeric rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | amount | numeric |
    And the following table request body:
      | amount | 10.51 |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | amount | numeric |
    And the following table request body:
      | amount | Gogogaga |
    And validate request
    Then the returned errors should be:
      | amount parameter requires only numeric values |
