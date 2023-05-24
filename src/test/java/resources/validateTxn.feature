@govtodo
 Feature: Validate Txn Data

 Scenario: VT-001 GSMA Transfer Api Payer Invalid Test

    Given I can create GSMATransferDTO with invalid payer information
    And I have tenant as "gorilla"
    When I call the GSMATransfer endpoint with expected status of 200
    And I should be able to parse transactionId from response
    Then I should poll the transfer query endpoint with transactionId until status is populated for the transactionId


    Scenario: VT-002 Inbound Transfer API Payer Invalid Test

       Given I create a new clientCorrelationId
       And I have tenant as "gorilla"
       Given I can mock TransactionChannelRequestDTO with wrong msisdn
       When I call inbound transfer api with client correlation id expected status 200
       And I should be able to parse transactionId from response
       Then I should poll the transfer query endpoint with transactionId until status is populated for the transactionId