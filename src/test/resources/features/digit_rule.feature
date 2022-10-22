@digit_rule
Feature: Digit rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | size | digit |
    And the following table request body:
      | size | 1000 |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | size | digit |
    And the following table request body:
      | size | 1.5 |
    And validate request
    Then the returned errors should be:
      | size parameter requires only digit values |

    Given the following rules:
      | size | digit |
    And the following table request body:
      | size | blahblah |
    And validate request
    Then the returned errors should be:
      | size parameter requires only digit values |


