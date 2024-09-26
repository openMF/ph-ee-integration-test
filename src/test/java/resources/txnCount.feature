@amsIntegration
Feature: Check reliability by testing inbound transfer APIs

  Scenario: Checking all records for transfer APIs
    Given I create a clientCorrelationId and tenant "gorilla"
    When I can mock TransactionChannelRequestDTO
    Then I call transfer api for 1000 times and store transaction Ids
    When I will sleep for 120000 millisecond
    Then I call operations-app api with expected status 200 and match it with stored transaction Ids