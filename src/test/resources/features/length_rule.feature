@length_rule
Feature: Length rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | accountNumber | length:10 |
    And the following table request body:
      | accountNumber | 0123456789 |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | accountNumber | length:10 |
    And the following table request body:
      | accountNumber | 123456 |
    And validate request
    Then the returned errors should be:
      | accountNumber requires an exact length of 10 chars |

    Given the following rules:
      | accountNumber | length:10 |
    And the following table request body:
      | accountNumber | 12345678910 |
    And validate request
    Then the returned errors should be:
      | accountNumber requires an exact length of 10 chars |


