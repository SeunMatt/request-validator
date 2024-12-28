@array_not_empty
Feature: ArrayNotEmpty rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | currencies | array |
    And the following json request body:
      """json
      {"currencies":  ["NGN", "USD"]}
      """
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | currencies | array |
    And the following json request body:
      """json
      {"currencies":  []}
      """
    And validate request
    Then the returned errors should be:
    | currencies supplied should be a non-empty array |

    Given the following rules:
      | currencies | array |
    And the following json request body:
      """json
      {"currencies":  "NGN"}
      """
    And validate request
    Then the returned errors should be:
      | currencies supplied should be a non-empty array |
