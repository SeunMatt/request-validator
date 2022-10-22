@min_rule
Feature: Min rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | firstName | min:5 |
    And the following table request body:
      | firstName | Seun Matt |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | firstName | min:20 |
    And the following table request body:
      | firstName | Seun Matt |
    And validate request
    Then the returned errors should be:
      | firstName requires a min length of 20 chars |


