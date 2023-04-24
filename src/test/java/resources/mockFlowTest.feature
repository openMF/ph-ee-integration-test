@cucumberCli
Feature: Mock Flow Test


  Scenario: Test for mock fund transfer flow
    Given I have tenant as "gorilla"
    And I create a new clientCorrelationId
    Given I can mock TransactionChannelRequestDTO
    When I call the outbound transfer endpoint with expected status 200
    Then I should get non empty response
    Given I can mock TransactionChannelRequestDTO
    When I call the get txn API with expected status of 200 and clientCorrelationId
    Then I should get non empty response
    And I should have startedAt and completedAt in response


    Scenario: Get Txn Req API with Params
      Given I have tenant as "rhino"
      And I create a new clientCorrelationId
      Given I can mock TransactionChannelRequestDTO
      When I call the outbound transfer endpoint with expected status 200
      Then I should get non empty response
      Given I can mock TransactionChannelRequestDTO
      When I call the get txn API with expected status of 200 and clientCorrelationId
      Then I should get non empty response
      And I should have startedAt and completedAt in response