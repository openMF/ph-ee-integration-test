@gov
Feature: Channel Collection API test


  Scenario Outline: Post channel collection API with existing BPMN flows
    Given I have tenant as "<tenant>"
    And I have the request body with payer ams identifier keys as "<key1>" and "<key2>"
    When I call the channel collection API with client correlation id and expected status of 200
    Then I should get transaction id in response

    Examples:
      | tenant | key1 | key2 |
      | gorilla | MSISDN | accountid |


  Scenario Outline: Post channel collection API with non-existing BPMN flows
    Given I have tenant as "<tenant>"
    And I have the request body with payer ams identifier keys as "<key1>" and "<key2>"
    When I call the channel collection API with client correlation id and expected status of 200
    Then I should get transactionId with null value in response

    Examples:
      | tenant | key1 | key2 |
      | lion | MSISDN | accountid |


  Scenario:  Post channel collection API for Minimal Mock Transfer Request flow
    Given I have tenant as "rhino"
    And I have the request body with payer ams identifier keys as "MSISDN" and "FOUNDATIONALID"
    When I call the channel collection API with client correlation id and expected status of 200
    Then I should get transaction id in response
    When I call the get txn API in ops app with transactionId as parameter
    Then I should get transaction state as completed and externalId not null