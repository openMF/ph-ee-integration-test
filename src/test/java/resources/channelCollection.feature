Feature: Channel Collection API test


  Scenario Outline: Post channel collection API with existing BPMN flows
    Given I have tenant as "<tenant>"
    And I have the request body with payer ams identifier keys as "<key1>" and "<key2>"
    When I call the channel collection API with client correlation id as 12345 expected status of 200
    Then I should get transaction id in response

    Examples:
      | tenant | key1 | key2 |
      | gorilla | MSISDN | FOUNDATIONALID |
      | rhino | MSISDN | FOUNDATIONALID |


  Scenario Outline: Post channel collection API with non-existing BPMN flows
    Given I have tenant as "<tenant>"
    And I have the request body with payer ams identifier keys as "<key1>" and "<key2>"
    When I call the channel collection API with client correlation id as 12345 expected status of 200
    Then I should get transactionId with null value in response

    Examples:
      | tenant | key1 | key2 |
      | rhino | MSISDN | accountid |
      | lion | MSISDN | accountid |