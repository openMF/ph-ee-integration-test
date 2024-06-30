@common @amsIntegration
Feature: GSMA Transfer API Test for Account Identifier Worker

  Background: I will start mock server and register stub
    Given I will start the mock server
    And I can register the stub with "/depositCallback" endpoint for "POST" request with status of 200
    And I can register the stub with "/loanCallback" endpoint for "POST" request with status of 200

  Scenario: Savings account Creation Test
    Given I have Fineract-Platform-TenantId as "payerfsp1"
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the interop identifier endpoint
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 11
# Savings account deposit using BPMN workflow.
    When I have amsName as "mifos" and acccountHoldingInstitutionId as "wakanda" and amount as 11
    Then I call the channel connector API for savings account with expected status of 200 and stub "/depositCallback"
    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "POST" method to "/depositCallback" endpoint received 1 request


  Scenario: Loan account Creation Test
    Given I have Fineract-Platform-TenantId as "payerfsp1"
    And I call the create loan product endpoint
    When I call the create loan account
    Then I approve the loan account with command "approve" for amount 7800
    When I call the loan disburse endpoint with command "disburse" for amount 7800
    Then I call the loan repayment endpoint for amount 21
# Loan account repayment using BPMN workflow.
    When I have amsName as "mifos" and acccountHoldingInstitutionId as "wakanda" and amount as 11
    Then I call the channel connector API for loan account with expected status of 200 and stub "/loanCallback"
    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "POST" method to "/loanCallback" endpoint received 1 request

# AMS Mock API call integration test
  Scenario: AMS Mifos Deposit Mock API Call Test
    When I call the AMS Mifos Deposit Mock API with expected status of 200

  Scenario: AMS Mifos Loan Repayment Mock API Call Test
    When I call the AMS Mifos Loan Repayment Mock API with expected status of 200
