@gov
Feature: Client Correlation Id Idempotency Test

@application.yaml

  Scenario: Collection API Idempotency Test
    Given I create a new clientCorrelationId
    And I have tenant as "gorilla"
    When I call collection api with client correlation id expected status 200
    Given I have same clientCorrelationId
    When I call collection api to fail with client correlation id expected status 400
    Then I should have error as Transaction already Exists

  Scenario: Gsma Transaction API Idempotency Test
    Given I create a new clientCorrelationId
    And I have tenant as "gorilla"
    When I call gsma transaction api with client correlation id expected status 200
    Given I have same clientCorrelationId
    When I call gsma transaction to fail with client correlation id expected status 400
    Then I should have error as Transaction already Exists

  Scenario: Inbound Transfer API Idempotency Test
    Given I create a new clientCorrelationId
    And I have tenant as "gorilla"
    Given I can mock TransactionChannelRequestDTO
    When I call inbound transfer api with client correlation id expected status 200
    Given I have same clientCorrelationId
    When I call inbound transfer api with client correlation id expected status 400
    Then I should have error as Transaction already Exists

  Scenario: Inbound Transfer Req API Idempotency Test
    Given I create a new clientCorrelationId
    And I have tenant as "gorilla"
    When I call Inbound Transfer Req api with client correlation id expected status 200
    Given I have same clientCorrelationId
    When I call Inbound Transfer Req api with client correlation id expected status 400
    Then I should have error as Transaction already Exists
