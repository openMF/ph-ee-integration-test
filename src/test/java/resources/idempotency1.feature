Feature: Client Correlation Id Idempotency Test

@application.yaml

  Scenario: IT-001 Collection API Idempotency Test
    Given I create a new clientCorrelationId
    And I have tenant as "gorilla"
    When I call collection api with client correlation id expected status 200
    Given I have same clientCorrelationId
    When I call collection api to fail with client correlation id expected status 400
    Then I should have error as Transaction already Exists
