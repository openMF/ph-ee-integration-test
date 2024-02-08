@amsIntegration @govtodo
Feature: GSMA Outbound Transfer test

  Scenario: GSMA Withdrawal Transfer testx (Payer Debit only)
    Given I have Fineract-Platform-TenantId as "gorilla"
    When I create a set of debit and credit party
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the debit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 100
    Given I have Fineract-Platform-TenantId as "lion"
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the credit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 100
    Given I have BB1 tenant
    Then I call the balance api for payer balance
    Given I have BB1 tenant
    When I can create GSMATransferDTO with different payer and payee
    Then I call the GSMATransfer endpoint with expected status of 200
    And I should be able to parse transactionId from response
    Given I have BB1 tenant
    When I call the transfer query endpoint with transactionId and expected status of 200
    Then I will sleep for 1000 millisecond
    Given I have BB1 tenant
    Then I call the balance api for payer balance after debit

  Scenario: GSMA Deposit Transfer test
    Given I have Fineract-Platform-TenantId as "gorilla"
    When I create a set of debit and credit party
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the debit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 100
    Given I have Fineract-Platform-TenantId as "lion"
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the credit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 100
    Given I have tenant as "lion"
    Then I call the balance api for payee balance
    Given I have tenant as "lion"
    When I can create GSMATransferDTO with different payer and payee
    Then I call the GSMATransfer Deposit endpoint with expected status of 200
    Then I will sleep for 1000 millisecond
    Given I have tenant as "lion"
    Then I call the balance api for payee balance after credit

  Scenario: GSMA Deposit-Withdrawal Transfer test
    Given I have Fineract-Platform-TenantId as "gorilla"
    When I create a set of debit and credit party
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the debit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 100
    Given I have Fineract-Platform-TenantId as "lion"
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the credit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 100
    Given I have BB1 tenant
    Then I call the balance api for payer balance
    Given I have tenant as "lion"
    Then I call the balance api for payee balance
    Given I have BB1 tenant
    When I can create GSMATransferDTO with different payer and payee
    Then I call the GSMATransfer endpoint with expected status of 200
    And I should be able to parse transactionId from response
    Given I have BB1 tenant
    When I call the transfer query endpoint with transactionId and expected status of 200
    Then I will sleep for 5000 millisecond
    Given I have BB1 tenant
    Then I call the balance api for payer balance after debit
    Given I have tenant as "lion"
    Then I call the balance api for payee balance after credit
