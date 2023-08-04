@gov
Feature: Identity Account Mapper Api Test

  Scenario: Register Beneficiary Api Test
    When I create an IdentityMapperDTO for Register Beneficiary
    Then I can inject MockServer
    And I can start mock server
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    And I can register the stub with "/registerBeneficiaryApiTest" endpoint for "PUT" request with status of 200
    When I make the "PUT" request to "/registerBeneficiaryApiTest" endpoint with expected status of 200
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
    And I can stop mock server

  Scenario: Add Payment Modality Api Test
    When I create an IdentityMapperDTO for Add Payment Modality
    Then I can inject MockServer
    And I can start mock server
    Then I call the add payment modality API with expected status of 202 and stub "/addPaymentModalityApiTest"
    And I can register the stub with "/addPaymentModalityApiTest" endpoint for "PUT" request with status of 200
    When I make the "PUT" request to "/addPaymentModalityApiTest" endpoint with expected status of 200
    Then I should be able to verify that the "PUT" method to "/addPaymentModalityApiTest" endpoint received a request with required parameter in body
    And I can stop mock server

  Scenario: Update Payment Modality Api Test
    When I create an IdentityMapperDTO for Update Payment Modality
    Then I can inject MockServer
    And I can start mock server
    Then I call the update payment modality API with expected status of 202 and stub "/updatePaymentModalityApiTest"
    And I can register the stub with "/updatePaymentModalityApiTest" endpoint for "PUT" request with status of 200
    When I make the "PUT" request to "/updatePaymentModalityApiTest" endpoint with expected status of 200
    Then I should be able to verify that the "PUT" method to "/updatePaymentModalityApiTest" endpoint received a request with required parameter in body
    And I can stop mock server

  Scenario: Account Lookup Api Test
    When I can inject MockServer
    And I can start mock server
    Then I call the account lookup API with expected status of 202 and stub "/accountLookupTest"
    And I can register the stub with "/accountLookupTest" endpoint for "PUT" request with status of 200
    When I make the "PUT" request to "/accountLookupTest" endpoint with expected status of 200
    Then I should be able to verify that the "PUT" method to "/accountLookupTest" endpoint received a request with same payeeIdentity
    And I can stop mock server

  Scenario: Account Lookup Api Consistency Test
    When I can inject MockServer
    And I can start mock server
    Then I call the account lookup API 10 times with expected status of 202 and stub "/accountLookup"
    And I can register the stub with "/accountLookup" endpoint for "PUT" request with status of 200
    When I make the "PUT" request to "/accountLookup" endpoint with expected status of 200
    Then I should be able to verify that the "PUT" method to "/accountLookup" endpoint received 10 request
    And I can stop mock server

  Scenario: Invalid Registering Institution Id
    When I create an IdentityMapperDTO for adding 8 beneficiary
    When I call the register beneficiary API with "SocialWelfare" as registering institution id expected status of 202
    And I create an IdentityMapperDTO for adding 2 beneficiary
    When I call the register beneficiary API with "Social" as registering institution id expected status of 202
    Then I can inject MockServer
    And I can start mock server
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    And I create request body for batch account lookup API
    Then I call bulk account lookup API with these 10 beneficiaries and "SocialWelfare" as registering institution id and stub "/batchAccountLookup"
    And I can register the stub with "/batchAccountLookup" endpoint for "PUT" request with status of 200
    When I make the "PUT" request to "/batchAccountLookup" endpoint with expected status of 200
    And I should be able to verify that the "PUT" method to "/batchAccountLookup" receive 1 request
    And I can stop mock server

  Scenario: Beneficiary not registered
    Then I can inject MockServer
    And I can start mock server
    When I create an IdentityMapperDTO for adding 8 beneficiary
    When I call the register beneficiary API with "SocialWelfare" as registering institution id expected status of 202
    And I create an IdentityMapperDTO for adding 2 beneficiary
    And I create request body for batch account lookup API
    Then I call bulk account lookup API with these 10 beneficiaries and "SocialWelfare" as registering institution id and stub "/batchAccountLookup"
    And I can register the stub with "/batchAccountLookup" endpoint for "PUT" request with status of 200
    When I make the "PUT" request to "/batchAccountLookup" endpoint with expected status of 200
    And I should be able to verify that the "PUT" method to "/batchAccountLookup" receive 8 request
    And I can stop mock server

  Scenario: Batch Account Lookup API
    Then I can inject MockServer
    And I can start mock server
    When I create an IdentityMapperDTO for adding 8 beneficiary
    When I call the register beneficiary API with "SocialWelfare" as registering institution id expected status of 202
    And I create request body for batch account lookup API
    Then I call bulk account lookup API with these 8 beneficiaries and "SocialWelfare" as registering institution id and stub "/batchAccountLookup"
    And I can register the stub with "/batchAccountLookup" endpoint for "PUT" request with status of 200
    When I make the "PUT" request to "/batchAccountLookup" endpoint with expected status of 200
    And I should be able to verify that the "PUT" method to "/batchAccountLookup" receive 8 request
    And I can stop mock server

  Scenario: Bulk Processor Inbound Integration Test
    When I create an IdentityMapperDTO for registering beneficiary with "gorilla" as DFSPID
    Then I can inject MockServer
    And I can start mock server
    When I call the register beneficiary API with expected status of 202
    Given I have the demo csv file "ph-ee-bulk-demo-7.csv"
    And I have tenant as "rhino"
    When I call the batch transactions endpoint with expected response status of 200
    Then I should be able to parse batch id from response
    When I call the batch details API with expected response status of 200
    And I can assert the payee DFSPID is same as used to register beneficiary id type from response
