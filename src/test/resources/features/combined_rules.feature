@combined_rules
Feature: Using one or more rules together to validate request

  Scenario: Validating a user signup form that satisfy the requirements
    Given the following rules:
      | firstName                            | required\|max:250\|                          |
      | lastName                             | required\|max:250\|                          |
      | email                                | required\|max:250\|email                     |
      | dob                                  | required\|\|regex:[0-9]{2}-[0-9]{2}-[0-9]{4} |
      | gender                               | required\|in:MALE,FEMALE                     |
      | interests                            | optional\|array                              |
      | preferences.emailNotificationEnabled | optional\|in:true,false                      |
      | preferences.frequency                | optional\|digit                              |
      | kyc.idType                           | required\|in:BVN,SSN                         |
      | kyc.bvn                              | requiredIf:kyc.idType,BVN\|length:11         |
      | kyc.ssn                              | requiredIf:kyc.idType,SSN\|length:12         |
      | kyc.address                          | optional\|max:250                            |
      | investmentAmount                     | optional\|numeric                            |
      | investmentCurrency                   | requiredWith:investmentAmount\|in:USD,NGN    |
    And the following json request body:
      """json
        {
          "firstName": "Seun",
          "lastName": "Matt",
          "email": "seunmatt@example.com",
          "dob": "23-10-2022",
          "gender": "MALE",
          "interests": [
            "Java",
            "SpringBoot"
          ],
          "preferences": {
            "emailNotificationEnabled": true,
            "frequency": 4
          },
          "kyc": {
            "idType": "SSN",
            "ssn": 123456789034,
            "address": "Lagos, Nigeria"
          },
          "investmentAmount": 1000.50,
          "investmentCurrency": "NGN"
        }
      """
    Then validate request
    Then no errors should be returned


  Scenario: Providing a value that does meets the requirements
    Given the following rules:
      | firstName                            | required\|max:250                          |
      | lastName                             | required\|max:250                          |
      | email                                | required\|max:250\|email                     |
      | dob                                  | required\|\|regex:[0-9]{2}-[0-9]{2}-[0-9]{4} |
      | gender                               | required\|in:MALE,FEMALE                     |
      | interests                            | optional\|array                              |
      | preferences.emailNotificationEnabled | optional\|in:true,false                      |
      | preferences.frequency                | optional\|digit                              |
      | kyc.idType                           | required\|in:BVN,SSN                         |
      | kyc.bvn                              | requiredIf:kyc.idType,BVN\|length:11         |
      | kyc.ssn                              | requiredIf:kyc.idType,SSN\|length:12         |
      | kyc.address                          | optional\|max:5                            |
      | investmentAmount                     | optional\|numeric                            |
      | investmentCurrency                   | requiredWith:investmentAmount\|in:USD,NGN    |
    And the following json request body:
      """json
        {
          "lastName": "Matt",
          "email": "seunmatt",
          "dob": "23-OCT-2022",
          "gender": "MALE",
          "interests": [],
          "preferences": {
            "emailNotificationEnabled": "no",
            "frequency": 4.8
          },
          "kyc": {
            "idType": "SSN",
            "bvn": 123456789034,
            "address": "Lagos, Nigeria"
          },
          "investmentAmount": 1000.50
        }
      """
    Then validate request
    Then the returned errors should be:
      | firstName is required |
      | firstName requires a maximum length of 250 chars |
      | email supplied is invalid |
      | Invalid pattern supplied for dob |
      | The selected preferences.emailNotificationEnabled is invalid. (Valid preferences.emailNotificationEnabled: true, false) |
      | preferences.frequency parameter requires only digit values |
      | kyc.bvn requires an exact length of 11 chars |
      | The kyc.ssn field is required when if kyc.idType = SSN |
      | kyc.address requires a maximum length of 5 chars |
      | The investmentCurrency field is required when investmentAmount is present |
