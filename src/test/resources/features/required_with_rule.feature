@required_with_rule
Feature: RequiredWith rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | accountNumber | required                            |
      | bankCode      | optional                            |
      | accountName   | requiredWith:accountNumber,bankCode |
    And the following table request body:
      | accountNumber | 0123456789 |
      | bankCode      | 044        |
      | accountName   | Seun Matt  |
    Then validate request
    Then no errors should be returned

    Given the following rules:
      | accountNumber | required                            |
      | bankCode      | optional                            |
      | accountName   | requiredWith:accountNumber,bankCode |
    And the following table request body:
      | accountNumber | 0123456789 |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | accountNumber | required                            |
      | bankCode      | optional                            |
      | accountName   | requiredWith:accountNumber,bankCode |
    And the following table request body:
      | accountNumber | 0123456789 |
      | bankCode      | 044        |
      | accountName   |            |
    And validate request
    Then the returned errors should be:
      | The accountName field is required when accountNumber and bankCode is present |

    Given the following rules:
      | accountNumber | required                            |
      | bankCode      | optional                            |
      | accountName   | requiredWith:accountNumber,bankCode |
    And the following table request body:
      | accountNumber | 0123456789 |
      | bankCode      | 044        |
    And validate request
    Then the returned errors should be:
      | The accountName field is required when accountNumber and bankCode is present |
