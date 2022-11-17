Feature: Inbound transaction test

  Scenario: Payer inbound transfer request
    Given I can mock TransactionChannelRequestDTO
    And I have tenant as "gorilla"
    When I call the inbound transfer endpoint with expected status of 200
    Then I should get non empty response
    And I should be able to parse transactionId
