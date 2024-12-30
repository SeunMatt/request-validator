@required_without_any_rule
Feature: RequiredWithoutAny rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | firstName | optional                              |
      | lastName  | optional                              |
      | fullName  | requiredWithoutAny:firstName,lastName |
    And the following table request body:
      | firstName |           |
      | lastName  |           |
      | fullName  | Seun Matt |
    Then validate request
    Then no errors should be returned

    Given the following rules:
      | firstName | optional                              |
      | lastName  | optional                              |
      | fullName  | requiredWithoutAny:firstName,lastName |
    And the following table request body:
      | fullName  | Seun Matt |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | firstName | optional                              |
      | lastName  | optional                              |
      | fullName  | requiredWithoutAny:firstName,lastName |
    And the following table request body:
      | firstName | Seun |
      | fullName  |      |
    And validate request
    Then the returned errors should be:
      | The fullName field is required when firstName/lastName is not present |

    Given the following rules:
      | firstName | optional                              |
      | lastName  | optional                              |
      | fullName  | requiredWithoutAny:firstName,lastName |
    And the following table request body:
      | fullName  |      |
    And validate request
    Then the returned errors should be:
      | The fullName field is required when firstName/lastName is not present |
