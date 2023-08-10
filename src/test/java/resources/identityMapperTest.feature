@gov
Feature: Identity Account Mapper Api Test

  Scenario: I will start mock server and register stub
    Then I can inject MockServer
    And I can start mock server
    And I can register the stub with "/registerBeneficiaryApiTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/addPaymentModalityApiTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/updatePaymentModalityApiTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/accountLookupTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/accountLookup" endpoint for "PUT" request with status of 200


  Scenario: Register Beneficiary Api Test
    When I create an IdentityMapperDTO for Register Beneficiary
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    When I make the "PUT" request to "/registerBeneficiaryApiTest" endpoint with expected status of 200
    Then I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body

  Scenario: Add Payment Modality Api Test
    When I create an IdentityMapperDTO for Add Payment Modality
    Then I call the add payment modality API with expected status of 202 and stub "/addPaymentModalityApiTest"
    When I make the "PUT" request to "/addPaymentModalityApiTest" endpoint with expected status of 200
    Then I should be able to verify that the "PUT" method to "/addPaymentModalityApiTest" endpoint received a request with required parameter in body

  Scenario: Update Payment Modality Api Test
    When I create an IdentityMapperDTO for Update Payment Modality
    Then I call the update payment modality API with expected status of 202 and stub "/updatePaymentModalityApiTest"
    When I make the "PUT" request to "/updatePaymentModalityApiTest" endpoint with expected status of 200
    Then I should be able to verify that the "PUT" method to "/updatePaymentModalityApiTest" endpoint received a request with required parameter in body

  Scenario: Account Lookup Api Test
    Then I call the account lookup API with expected status of 202 and stub "/accountLookupTest"
    When I make the "PUT" request to "/accountLookupTest" endpoint with expected status of 200
    Then I should be able to verify that the "PUT" method to "/accountLookupTest" endpoint received a request with same payeeIdentity

  Scenario: Account Lookup Api Consistency Test
    Then I call the account lookup API 10 times with expected status of 202 and stub "/accountLookup"
    When I make the "PUT" request to "/accountLookup" endpoint with expected status of 200
    Then I should be able to verify that the "PUT" method to "/accountLookup" endpoint received 10 request

    Scenario: i can stop mock server
      And I can stop mock server
