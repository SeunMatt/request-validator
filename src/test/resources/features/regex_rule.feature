@regex_rule
Feature: Regex rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | birthDate | regex:[0-9]{2}-[0-9]{2}-[0-9]{4} |
    And the following table request body:
      | birthDate | 22-10-2022 |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | birthDate | regex:[0-9]{2}-[0-9]{2}-[0-9]{4} |
    And the following table request body:
      | birthDate | 22-OCT-2022 |
    And validate request
    Then the returned errors should be:
      | Invalid pattern supplied for birthDate |


