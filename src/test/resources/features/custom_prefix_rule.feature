@custom_prefix_rule
Feature: Custom prefix rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | customField | customprefix |
    And the following table request body:
      | customField | custom_value |
    Then validate custom request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | customField | customprefix |
    And the following table request body:
      | customField | value |
    And validate custom request
    Then the returned errors should be:
      | customField should start with custom_ |
