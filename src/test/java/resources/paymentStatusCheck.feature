@govToDo
Feature: Payment Status Check API

  Scenario: Component Test for Payment Status Check API
    Given I have tenant as "rhino"
    And I create a new clientCorrelationId
    Given I can mock TransactionChannelRequestDTO
    And I create a new clientCorrelationId
    When I call the outbound transfer endpoint with expected status 200
    Then I should get non empty response
    When I should have clean request id list
    And I extracted clientCorrelationId from response
    Given I can mock TransactionChannelRequestDTO
    And I create a new clientCorrelationId
    When I call the outbound transfer endpoint with expected status 200
    Then I should get non empty response
    And I extracted clientCorrelationId from response
    Given I can create a mock request body from above clientCorrelationIds
    When I call the payment status check endpoint with expected status 200
    Then I should get non empty response
    And I should have startedAt and completedAt in response
    And I should have PayerFspId as not null

  Scenario: Integration Test for Payment Status Check API with batch transactions
    Given I have tenant as "rhino"
    And I have the demo csv file "ph-ee-bulk-demo-7.csv"
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    And I extracted clientCorrelationId from the demo csv file "ph-ee-bulk-demo-7.csv"
    Given I can create a mock request body from above clientCorrelationIds
    When I call the payment status check endpoint with expected status 200
    Then I should get non empty response
    And I should have startedAt and completedAt in response
