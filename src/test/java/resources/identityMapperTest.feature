
Feature: Identity Account Mapper Api Test

  Background: I will start mock server and register stub
    Given I will start the mock server
    And I can register the stub with "/registerBeneficiaryApiTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/addPaymentModalityApiTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/updatePaymentModalityApiTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/accountLookupTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/accountLookup" endpoint for "PUT" request with status of 200
    And I can register the stub with "/batchAccountLookup" endpoint for "PUT" request with status of 200
    And I can register the stub with "/updateBeneficiaryApiTest" endpoint for "PUT" request with status of 200
    Then I will update the  mock server and register stub as done

  @gov
  Scenario: IAM-001 Register Beneficiary Api Test
    Given I create an IdentityMapperDTO for Register Beneficiary
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 7000 millisecond
    And I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: IAM-002 Add Payment Modality Api Test
    Given I create an IdentityMapperDTO for Add Payment Modality
    When I call the add payment modality API with expected status of 202 and stub "/addPaymentModalityApiTest"
#    Then I will sleep for 3000 millisecond
    And I should be able to verify that the "PUT" method to "/addPaymentModalityApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: IAM-003 Update Payment Modality Api Test
    Given I create an IdentityMapperDTO for Update Payment Modality
    When I call the update payment modality API with expected status of 202 and stub "/updatePaymentModalityApiTest"
#    Then I will sleep for 3000 millisecond
    And I should be able to verify that the "PUT" method to "/updatePaymentModalityApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: PPV-11/IAM-004 Account Lookup Api Test
    When I call the account lookup API with expected status of 202 and stub "/accountLookupTest"
#    Then I will sleep for 3000 millisecond
    And I should be able to verify that the "PUT" method to "/accountLookupTest" endpoint received a request with same payeeIdentity

  @gov
  Scenario: UB-003 Update a single beneficiary with Payment Modality - Mobile Money
    When I create an IdentityMapperDTO for 1 Register Beneficiary with payment modality as "ACCOUNT_ID"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 1 update Beneficiary with payment modality as "MSISDN"
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: UB-004 Update a single beneficiary with Payment Modality - Voucher
    When I create an IdentityMapperDTO for 1 Register Beneficiary with payment modality as "MSISDN"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 1 update Beneficiary with payment modality as "VOUCHER"
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: UB-006 Update a single beneficiary with Payment Modality - Proxy or Virtual Address
    When I create an IdentityMapperDTO for 1 Register Beneficiary with payment modality as "VOUCHER"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 1 update Beneficiary with payment modality as "VIRTUAL_ADDRESS"
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: UB-005 Update a single beneficiary with Payment Modality - Digital Wallet
    When I create an IdentityMapperDTO for 1 Register Beneficiary with payment modality as "VOUCHER"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 1 update Beneficiary with payment modality as "WALLET_ID"
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: UB-009 Update  multiple beneficiary with Payment Modality - Mobile Money
    When I create an IdentityMapperDTO for 4 Register Beneficiary with payment modality as "ACCOUNT_ID"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 4 update Beneficiary with payment modality as "MSISDN"
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: UB-010 Update  multiple beneficiary with Payment Modality - Voucher
    When I create an IdentityMapperDTO for 4 Register Beneficiary with payment modality as "MSISDN"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 4 update Beneficiary with payment modality as "VOUCHER"
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: UB-011 Update  multiple beneficiary with Payment Modality - Digital Wallet
    When I create an IdentityMapperDTO for 4 Register Beneficiary with payment modality as "VOUCHER"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 4 update Beneficiary with payment modality as "WALLET_ID"
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: UB-012 Update  multiple beneficiary with Payment Modality - Proxy or Virtual Address
    When I create an IdentityMapperDTO for 4 Register Beneficiary with payment modality as "WALLET_ID"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 4 update Beneficiary with payment modality as "VIRTUAL_ADDRESS"
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: Update a single beneficiary with Banking Institution Code
    When I create an IdentityMapperDTO for 1 Register Beneficiary with payment modality as "ACCOUNT_ID"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 1 update Beneficiary with banking institution code as "lion"
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: UB-002 Update a single beneficiary with Financial Address
    When I create an IdentityMapperDTO for 1 Register Beneficiary with payment modality as "ACCOUNT_ID"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 1 update Beneficiary with financial address code as "123456789"
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: UB-008 Update a multiple beneficiary with Financial Address
    When I create an IdentityMapperDTO for 4 Register Beneficiary with payment modality as "ACCOUNT_ID"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 4 update Beneficiary with financial address code as "123456789"
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: RB-001 Register a single beneficiary with Payment Modality - Bank Account
    When I create an IdentityMapperDTO for 1 Register Beneficiary with payment modality as "ACCOUNT_ID"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: RB-002 Register a single beneficiary with Payment Modality - Mobile Money Account
    When I create an IdentityMapperDTO for 1 Register Beneficiary with payment modality as "MSISDN"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: RB-003 Register a single beneficiary with Payment Modality - Proxy or Virtual Address
    When I create an IdentityMapperDTO for 1 Register Beneficiary with payment modality as "VIRTUAL_ADDRESS"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: RB-004 Register a single beneficiary with Payment Modality - Digital Wallet ID
    When I create an IdentityMapperDTO for 1 Register Beneficiary with payment modality as "WALLET_ID"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: RB-005 Register a single beneficiary with Payment Modality - Voucher
    When I create an IdentityMapperDTO for 1 Register Beneficiary with payment modality as "VOUCHER"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: RB-006 Register multiple beneficiary with Payment Modality - Bank Account
    When I create an IdentityMapperDTO for 5 Register Beneficiary with payment modality as "ACCOUNT_ID"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: RB-007 Register multiple beneficiary with Payment Modality - Mobile Money Account
    When I create an IdentityMapperDTO for 5 Register Beneficiary with payment modality as "MSISDN"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: RB-008 Register multiple beneficiary with Payment Modality - Proxy or Virtual Address
    When I create an IdentityMapperDTO for 5 Register Beneficiary with payment modality as "VIRTUAL_ADDRESS"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: RB-009 Register multiple beneficiary with Payment Modality - Digital Wallet ID
    When I create an IdentityMapperDTO for 5 Register Beneficiary with payment modality as "WALLET_ID"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: RB-010 Register multiple beneficiary with Payment Modality - Voucher
    When I create an IdentityMapperDTO for 5 Register Beneficiary with payment modality as "VOUCHER"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
  @gov
  Scenario: PPV-04 Invalid Registering Institution Id
    When I create an IdentityMapperDTO for adding 8 beneficiary
    When I call the register beneficiary API with "SocialWelfare" as registering institution id expected status of 202 and stub "/registerBeneficiaryApiTest"
    And I create an IdentityMapperDTO for adding 2 beneficiary
    When I call the register beneficiary API with "Social" as registering institution id expected status of 202 and stub "/registerBeneficiaryApiTest"
    And I create request body for batch account lookup API
    Then I call bulk account lookup API with these 10 beneficiaries and "Social" as registering institution id and stub "/batchAccountLookup"
#    Then I will sleep for 3000 millisecond
    And I should be able to verify that the "PUT" method to "/batchAccountLookup" receive 2 request
  @gov
  Scenario: PPV-02 Beneficiary not registered
    When I create an IdentityMapperDTO for adding 8 beneficiary
    When I call the register beneficiary API with "SocialWelfare" as registering institution id expected status of 202 and stub "/registerBeneficiaryApiTest"
    And I create an IdentityMapperDTO for adding 2 beneficiary
    And I create request body for batch account lookup API
    Then I call bulk account lookup API with these 10 beneficiaries and "SocialWelfare" as registering institution id and stub "/beneficiaryNotRegistered"
#    Then I will sleep for 3000 millisecond
    And I should be able to verify that the "PUT" method to "/beneficiaryNotRegistered" receive 8 request
  @gov
  Scenario: PPV-02 Batch Account Lookup API
    When I create an IdentityMapperDTO for adding 8 beneficiary
    When I call the register beneficiary API with "SocialWelfare" as registering institution id expected status of 202 and stub "/registerBeneficiaryApiTest"
    And I create request body for batch account lookup API
    Then I call bulk account lookup API with these 8 beneficiaries and "SocialWelfare" as registering institution id and stub "/batchAccountLookup"
#    Then I will sleep for 3000 millisecond
    And I should be able to verify that the "PUT" method to "/batchAccountLookup" receive 8 request
  @gov
  Scenario: PPV-003 Account lookup with GSMA
    Given I have Fineract-Platform-TenantId as "payeefsp1"
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the interop identifier endpoint
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I create an IdentityMapperDTO for Register Beneficiary with identifier from previous step
    When I call the register beneficiary API with expected status of 202 and callback stub "/registerBeneficiaryApiTest"
#    And I will sleep for 3000 millisecond
    Then I call the account lookup API with expected status of 202 and callback stub "/accountLookupTest"
#    And I will sleep for 3000 millisecond
    And I should be able to verify that the "PUT" method to "/accountLookupTest" endpoint received a request with validation

  @gov
  Scenario: Fetch Beneficiaries API Test
    Given I create an IdentityMapperDTO for Register Beneficiary
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    And I will sleep for 3000 millisecond
    Then I will call the fetch beneficiary API with expected status of 200
    And I will assert the fields from fetch beneficiary response
  @gov @ext
  Scenario: Batch Account Lookup Integration Test
    Given I create an IdentityMapperDTO for Register Beneficiary from csv file
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    And I will sleep for 3000 millisecond
    Given I have tenant as "paymentBB1"
    And I have the demo csv file "ph-ee-bulk-demo-7.csv"
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 10000 millisecond
    And I call the payment batch detail API with expected status of 200 with total 3 txns
    Then I am able to parse payment batch detail response
    And I should assert total txn count and successful txn count in payment batch detail response for batch account lookup

  @gov
  Scenario: RB-011 Attempt to Register a 5 Beneficiary - 4 with a valid RegistrationInstitution and 1 with a invalid RegistrationInstitution not available in PayBB
    When I create an IdentityMapperDTO for 4 Register Beneficiary with payment modality as "VOUCHER" and 1 with invalid functionalID
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with no of failed cases as 1
  @gov
  Scenario: RB-012 Attempt to Register a 5 Beneficiary - 4 with a valid format of FunctionalID and 1 with a invalid FunctionalID (not properly formatted)
    When I create an IdentityMapperDTO for 4 Register Beneficiary with payment modality as "VOUCHER" and 1 with invalid functionalID
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with no of failed cases as 1
  @gov
  Scenario: RB-013 Attempt to Register a 5 Beneficiary - 4 with a valid format of FunctionalID and 1 with a invalid FunctionalID (not properly formatted)
    When I create an IdentityMapperDTO for 2 Register Beneficiary with payment modality as "VOUCHER" and 1 with invalid payment modality
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with no of failed cases as 1
  @gov
  Scenario: RB-015 Attempt to register 10 Beneficiary, 8 Beneficiaries have been registered  - 2 of them have been already registered (duplication of beneficiaries functional ID)
    When I create an IdentityMapperDTO for 2 Register Beneficiary with payment modality as "VOUCHER"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 8 Register Beneficiary with payment modality as "VOUCHER" and 2 existing beneficiary
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with no of failed cases as 2
  @gov
  Scenario: UB-014 Attempt to update 10 Beneficiaries, 8 are successfully registered while 2 have invalid financial address. (not in correct format)
    When I create an IdentityMapperDTO for 10 Register Beneficiary with payment modality as "ACCOUNT_ID"
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 8 update Beneficiary with correct financial address code and 2 with incorrect Financial address
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with no of failed cases as 2
  @gov
  Scenario: UB-015 Attempt to update 10 Beneficiaries, 8 are successfully registered while 2 have invalid payment modality (not equal to 01,02,03,04,05).
    When I create an IdentityMapperDTO for 10 Register Beneficiary with no payment modality
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I create an IdentityMapperDTO for 8 update Beneficiary with correct payment modality and 2 with incorrect payment modality
    When I call the update beneficiary API with expected status of 202 and stub "/updateBeneficiaryApiTest"
#    Then I will sleep for 3000 millisecond
    Then I should be able to verify that the "PUT" method to "/updateBeneficiaryApiTest" endpoint received a request with no of failed cases as 2

  @gov
  Scenario: IAM-005 Account Lookup Api Consistency Test
    When I call the account lookup API 10 times with expected status of 202 and stub "/accountLookup"
#    Then I will sleep for 3000 millisecond
    And I can stop mock server



