@wip
Feature: Mojaloop test

  Scenario: ML connector partial payee party lookup test
    Given I am setting up Mojaloop
    Given I have Fineract-Platform-TenantId for "payee"
    When I call the create client endpoint for "payee"
    Then I call the create savings product endpoint for "payee"
    When I call the create savings account endpoint for "payee"
    Then I call the interop identifier endpoint for "payee"
    Then I approve the deposit with command "approve" for "payee"
    When I activate the account with command "activate" for "payee"
    When I can inject MockServer
    Then I can start mock server
    Then I can register the stub for callback endpoint of party lookup
    Then I call the get parties api in ml connector for "payee"
    Then I will sleep for 5000 millisecond
    Then I should be able to verify the callback for lookup
    Then I can stop mock server

  Scenario: ML connector partial payee quotation test
    Given I am setting up Mojaloop
    Given I have Fineract-Platform-TenantId for "payee"
    When I call the create client endpoint for "payee"
    Then I call the create savings product endpoint for "payee"
    When I call the create savings account endpoint for "payee"
    Then I call the interop identifier endpoint for "payee"
    Then I approve the deposit with command "approve" for "payee"
    When I activate the account with command "activate" for "payee"
    When I can inject MockServer
    Then I can start mock server
    Then I can register the stub for callback endpoint of quotation
    Then I call the get quotation api in ml connector for "payee"
    Then I will sleep for 5000 millisecond
    Then I should be able to verify the callback for quotation
    Then I can stop mock server

  Scenario: ML connector partial payee transfer test
    Given I am setting up Mojaloop
    Given I have Fineract-Platform-TenantId for "payee"
    When I call the create client endpoint for "payee"
    Then I call the create savings product endpoint for "payee"
    When I call the create savings account endpoint for "payee"
    Then I call the interop identifier endpoint for "payee"
    Then I approve the deposit with command "approve" for "payee"
    When I activate the account with command "activate" for "payee"
    When I can inject MockServer
    Then I can start mock server
    Then I can register the stub for callback endpoint of quotation
    Then I call the get quotation api in ml connector for "payee"
    Then I will sleep for 5000 millisecond
    Then I should be able to verify the callback for quotation
    Then I can register the stub for callback endpoint of transfer
    Then I call the transfer api in ml connector for "payee"
    Then I will sleep for 5000 millisecond
    Then I should be able to verify the callback for transfer
    Then I can stop mock server

  Scenario: Payer Fund Transfer Flow test
    Given I am setting up Mojaloop
    Given I have Fineract-Platform-TenantId for "payer"
    When I call the create client endpoint for "payer"
    Then I call the create savings product endpoint for "payer"
    When I call the create savings account endpoint for "payer"
    Then I call the interop identifier endpoint for "payer"
    Then I approve the deposit with command "approve" for "payer"
    When I activate the account with command "activate" for "payer"
    Then I call the deposit account endpoint with command "deposit" for amount 12 for "payer"

    Given I have Fineract-Platform-TenantId for "payee"
    When I call the create client endpoint for "payee"
    Then I call the create savings product endpoint for "payee"
    When I call the create savings account endpoint for "payee"
    Then I call the interop identifier endpoint for "payee"
    Then I approve the deposit with command "approve" for "payee"
    When I activate the account with command "activate" for "payee"
    Then I call the deposit account endpoint with command "deposit" for amount 10 for "payee"

    Then I add "payer" to als
    Then I add "payee" to als

    Then I call the payer fund transfer api to transfer amount "1" from payer to payee
    Then I should get transaction id in response

    Then I will sleep for 10000 millisecond

    When I call the transfer API in ops app with transactionId as parameter

    Then I check for error related to lookup
    And I assert the partyLookupFailed is false
    And I assert the partyLookupRetryCount is 0

    Then I check for error related to quote
    And I assert the quoteFailed is false
    And I assert the quoteRetryCount is 0

    Then I check for error related to transfer
    And I assert the transferFailed is false
    And I assert the transferRetryCount is 0

    Then I assert "payer" balance to be 11
    Then I assert "payee" balance to be 11

