@govtodo
Feature: Mock Flow Test

  Scenario: Test for minimal mock fund transfer flow
    Given I have tenant as "rhino"
    And I create a new clientCorrelationId
    Given I can mock TransactionChannelRequestDTO
    And I create a new clientCorrelationId
    When I call the outbound transfer endpoint with expected status 200
    Then I should get non empty response
    Given I can mock TransactionChannelRequestDTO
    And I have tenant as "rhino"
    When I call the get txn API with expected status of 200 and txnId
    Then I should get non empty response
    And I should have startedAt and completedAt in response
    And I should have PayerFspId as not null

