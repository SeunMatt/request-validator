@required_if_rule
Feature: RequiredIf rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | idType | optional              |
      | bvn    | requiredIf:idType,BVN |
    And the following table request body:
      | idType | BVN         |
      | bvn    | 12345678901 |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | idType | optional              |
      | bvn    | requiredIf:idType,BVN |
    And the following table request body:
      | idType | BVN         |
      | SSN    | 12345678901 |
    And validate request
    Then the returned errors should be:
      | The bvn field is required when if idType = BVN |

    Given the following rules:
      | idType | optional              |
      | bvn    | requiredIf:idType,BVN |
    And the following table request body:
      | idType | BVN |
      | BVN    |     |
    And validate request
    Then the returned errors should be:
      | The bvn field is required when if idType = BVN |


