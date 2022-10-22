@required_with_any_rule
Feature: RequiredWithAny rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | firstName | optional                             |
      | lastName  | requiredWithAny:firstName,middleName |
    And the following table request body:
      | firstName | Seun |
      | lastName  | Matt |
    Then validate request
    Then no errors should be returned

    Given the following rules:
      | firstName | optional                             |
      | lastName  | requiredWithAny:firstName |
    And the following table request body:
      | username | Seun |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | firstName | optional                             |
      | lastName  | requiredWithAny:firstName,middleName |
    And the following table request body:
      | firstName | Seun |
      | lastName  |  |
    And validate request
    Then the returned errors should be:
      | The lastName field is required when firstName/middleName is present |

    Given the following rules:
      | firstName | optional                             |
      | lastName  | requiredWithAny:firstName,middleName |
    And the following table request body:
      | firstName | Seun |
      | middleName  | M  |
    And validate request
    Then the returned errors should be:
      | The lastName field is required when firstName/middleName is present |


