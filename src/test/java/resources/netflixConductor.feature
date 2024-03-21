@dpg
Feature: DPGA API test

  Scenario: conductor server health test
    When I make a call to nc server health API with expected status 200
    Then I get the value of Healthy as true in response

  Scenario: DT-001 dpga transfer api test with ams withdrawal verification
    Given I have Fineract-Platform-TenantId as "payerfsp1"
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the interop identifier endpoint
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 11
    When I have amsName as "mifos" and acccountHoldingInstitutionId as "lion" and amount as 11
    Given I have tenant as "lion"
    And I have the request body for transfer
    When I call the channel transfer API with client correlation id and expected status of 200
    Then I should get transaction id in response
    When I call the get workflow API in  with workflow id as path variable
    Then I should get valid status
    Then I will sleep for 3000 millisecond
    When I call the get transfer API in ops app with transactionId as parameter
    Then I should get transfer state as completed
    When I call the savings account endpoint to get the current Balance
    Then I verify that the current balance is 10
