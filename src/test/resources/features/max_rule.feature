@max_rule
Feature: Max rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | firstName | max:10 |
    And the following table request body:
      | firstName | Seun Matt |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | firstName | max:10 |
    And the following table request body:
      | firstName | Seun Matt on the Roll |
    And validate request
    Then the returned errors should be:
      | firstName requires a maximum length of 10 chars |


