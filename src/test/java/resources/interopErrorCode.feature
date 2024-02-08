@amsintegrationtest
Feature: Interop Error Code Test

  Scenario: IEC-001 GSMA Transfer Api PayerNotFound Test
    Given I can create GSMATransferDTO with incorrect Payer
    Given I have BB1 tenant
    When I call the GSMATransfer endpoint with expected status of 200
    And I should be able to parse transactionId from response
    Then I should poll the transfer query endpoint with transactionId until errorInformation is populated for the transactionId
    And I should be able to parse "PayerNotFound" Error Code from response

  Scenario: IEC-002 GSMA Transfer Api PayerInsufficientBalance Test
    Given I can create GSMATransferDTO with Payer Insufficient Balance
    Given I have BB1 tenant
    When I call the GSMATransfer endpoint with expected status of 200
    And I should be able to parse transactionId from response
    Then I should poll the transfer query endpoint with transactionId until errorInformation is populated for the transactionId
    And I should be able to parse "PayerInsufficientBalance" Error Code from response


