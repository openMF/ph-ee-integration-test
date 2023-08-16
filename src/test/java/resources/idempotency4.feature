Feature: Client Correlation Id Idempotency Test

@application.yaml

  Scenario: IT-004 Inbound Transfer Req API Idempotency Test
    Given I create a new clientCorrelationId
    And I have tenant as "gorilla"
    When I call Inbound transaction Req api with client correlation id expected status 200
    Given I have same clientCorrelationId
    When I call Inbound transaction Req api with client correlation id expected status 400
    Then I should have error as Transaction already Exists
