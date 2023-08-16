@govtodo
Feature: Client Correlation Id Idempotency Test

@application.yaml

  Scenario: IT-003 Inbound Transfer API Idempotency Test
    Given I create a new clientCorrelationId
    And I have tenant as "gorilla"
    Given I can mock TransactionChannelRequestDTO
    When I call inbound transfer api with client correlation id expected status 200
    Given I have same clientCorrelationId
    When I call inbound transfer api with client correlation id expected status 400
    Then I should have error as Transaction already Exists