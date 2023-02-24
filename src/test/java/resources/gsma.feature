Feature: GSMA Transfer API Test for Account Identifier Worker

  Scenario: Savings account Creation Test
    Given I have Fineract-Platform-TenantId as "gorilla"
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint

  Scenario: Loan account Creation Test
    Given I have Fineract-Platform-TenantId as "gorilla"
    When I call the create loan product endpoint
    Then I call the create loan account
    When I approve the loan account with command "approve"
    Then I call the loan repayment endpoint

  Scenario: Deposit Savings Worker Test
    Given I have a GSMA Transfer payload body with accountId "S7741025618"
    When I have amsName as "mifos" and acccountHoldingInstitutionId as "gorilla"
    Then I call the channel connector API with expected status of 200

  Scenario: Loan repayment Worker Test
    Given I have a GSMA Transfer payload body with accountId "L000000003"
    When I have amsName as "mifos" and acccountHoldingInstitutionId as "gorilla"
    Then I call the channel connector API with expected status of 200