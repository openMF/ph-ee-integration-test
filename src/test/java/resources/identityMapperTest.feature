Feature: Identity Account Mapper Api Test

  Scenario: Register Beneficiary Api Test
    When I create an IdentityMapperDTO for Register Beneficiary
    Then I can inject MockServer
    And I can start mock server
    When I call the register beneficiary API with expected status of 200 and stub "/registerBeneficiaryApiTest"
    And I can register the stub with "/registerBeneficiaryApiTest" endpoint for "POST" request with status of 200
    When I make the "POST" request to "/registerBeneficiaryApiTest" endpoint with expected status of 200
    Then I should be able to verify that the "POST" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body
    And I can stop mock server

  Scenario: Add Payment Modality Api Test
    When I create an IdentityMapperDTO for Add Payment Modality
    Then I can inject MockServer
    And I can start mock server
    Then I call the add payment modality API with expected status of 200 and stub "/addPaymentModalityApiTest"
    And I can register the stub with "/addPaymentModalityApiTest" endpoint for "POST" request with status of 200
    When I make the "POST" request to "/addPaymentModalityApiTest" endpoint with expected status of 200
    Then I should be able to verify that the "POST" method to "/addPaymentModalityApiTest" endpoint received a request with required parameter in body
    And I can stop mock server

  Scenario: Update Payment Modality Api Test
    When I create an IdentityMapperDTO for Update Payment Modality
    Then I can inject MockServer
    And I can start mock server
    Then I call the update payment modality API with expected status of 200 and stub "/updatePaymentModalityApiTest"
    And I can register the stub with "/updatePaymentModalityApiTest" endpoint for "POST" request with status of 200
    When I make the "POST" request to "/updatePaymentModalityApiTest" endpoint with expected status of 200
    Then I should be able to verify that the "POST" method to "/updatePaymentModalityApiTest" endpoint received a request with required parameter in body
    And I can stop mock server

  Scenario: Account Lookup Api Test
    When I can inject MockServer
    And I can start mock server
    Then I call the account lookup API with expected status of 200 and stub "/accountLookupTest"
    And I can register the stub with "/accountLookupTest" endpoint for "POST" request with status of 200
    When I make the "POST" request to "/accountLookupTest" endpoint with expected status of 200
    Then I should be able to verify that the "POST" method to "/accountLookupTest" endpoint received a request with same payeeIdentity
    And I can stop mock server

  Scenario: Account Lookup Api Consistency Test
    When I can inject MockServer
    And I can start mock server
    Then I call the account lookup API 100 times with expected status of 200 and stub "/accountLookup"
    And I can register the stub with "/accountLookup" endpoint for "POST" request with status of 200
    When I make the "POST" request to "/accountLookup" endpoint with expected status of 200
    Then I should be able to verify that the "POST" method to "/accountLookup" endpoint received 100 request
    And I can stop mock server

  Scenario: Bulk Processor Inbound Integration Test
    When I create an IdentityMapperDTO for registering beneficiary with "gorilla" as DFSPID
    Then I can inject MockServer
    And I can start mock server
    When I call the register beneficiary API with expected status of 200
    Given I have the demo csv file "ph-ee-bulk-demo-7.csv"
    And I have tenant as "rhino"
    When I call the batch transactions endpoint with expected response status of 200
    Then I should be able to parse batch id from response
    When I call the batch details API with expected response status of 200
    And I can assert the payee DFSPID is same as used to register beneficiary id type from response
