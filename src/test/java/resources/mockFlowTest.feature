@gov
Feature: Mock Flow Test

  @wip
  Scenario: Test for minimal mock fund transfer account lookup flow
    Given I have Fineract-Platform-TenantId as "gorilla"
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the interop identifier endpoint
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I create an IdentityMapperDTO for Register Beneficiary with identifier from previous step
    When I call the register beneficiary API with expected status of 202 and callback stub "/registerBeneficiaryApiTest"
    And I will sleep for 3000 millisecond
    Then I have tenant as "lion"
    And I create a new clientCorrelationId
    Given I can mock TransactionChannelRequestDTO for account lookup
    And I create a new clientCorrelationId
    When I call the outbound transfer endpoint with expected status 200
    Then I should get non empty response
    Given I can mock TransactionChannelRequestDTO
    And I have tenant as "lion"
    Then I will sleep for 7000 millisecond
    When I call the get txn API with expected status of 200 and txnId
    Then I should get non empty response
    And I should have startedAt and completedAt in response
    And I should have PayeeFspId as "gorilla"

  Scenario: MFT-001 Test for minimal mock fund transfer flow
    Given I have tenant as "rhino"
    And I create a new clientCorrelationId
    Given I can mock TransactionChannelRequestDTO
    And I create a new clientCorrelationId
    When I call the outbound transfer endpoint with expected status 200
    Then I should get non empty response
    Given I can mock TransactionChannelRequestDTO
    And I have tenant as "rhino"
    Then I will sleep for 2000 millisecond
    When I call the get txn API with expected status of 200 and txnId
    Then I should get non empty response
    And I should have startedAt and completedAt in response
    And I should have PayerFspId as not null

  Scenario: MFT-002 Test for minimal mock fund transfer flow with batch transactions
    Given I have tenant as "rhino"
    And I have the demo csv file "ph-ee-bulk-demo-7.csv"
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    Then I will sleep for 35000 millisecond
    When I call the batch summary API with expected status of 200
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"
    And I should have matching total txn count and successful txn count in response


