@required_rule
Feature: Required rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | lastName | required |
    And the following table request body:
      | lastName | Matt |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | lastName | required |
    And the following table request body:
      | firstName | Matt |
    And validate request
    Then the returned errors should be:
      | lastName is required |

    Given the following rules:
      | lastName | required |
    And the following table request body:
      | lastName |  |
    And validate request
    Then the returned errors should be:
      | lastName is required |


