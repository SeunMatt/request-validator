@in_rule
Feature: In rule validator

  Scenario: Providing a value that satisfy the rules
    Given the following rules:
      | status | in:PENDING,ACTIVE,INACTIVE |
    And the following table request body:
      | status | ACTIVE |
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | status | in:PENDING,ACTIVE,INACTIVE |
    And the following table request body:
      | status | DEACTIVATED |
    And validate request
    Then the returned errors should be:
      | The selected status is invalid. (Valid status: PENDING, ACTIVE, INACTIVE) |
