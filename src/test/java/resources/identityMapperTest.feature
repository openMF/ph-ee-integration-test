@gov
Feature: Identity Account Mapper Api Test

  Background: I will start mock server and register stub
    Given I will start the mock server
    And I can register the stub with "/registerBeneficiaryApiTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/addPaymentModalityApiTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/updatePaymentModalityApiTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/accountLookupTest" endpoint for "PUT" request with status of 200
    And I can register the stub with "/accountLookup" endpoint for "PUT" request with status of 200
    And I can register the stub with "/batchAccountLookup" endpoint for "PUT" request with status of 200
    Then I will update the  mock server and register stub as done


  Scenario: IAM-001 Register Beneficiary Api Test
    Given I create an IdentityMapperDTO for Register Beneficiary
    When I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiaryApiTest"
    Then I make the "PUT" request to "/registerBeneficiaryApiTest" endpoint with expected status of 200
    And I should be able to verify that the "PUT" method to "/registerBeneficiaryApiTest" endpoint received a request with required parameter in body

  Scenario: IAM-002 Add Payment Modality Api Test
    Given I create an IdentityMapperDTO for Add Payment Modality
    When I call the add payment modality API with expected status of 202 and stub "/addPaymentModalityApiTest"
    Then I make the "PUT" request to "/addPaymentModalityApiTest" endpoint with expected status of 200
    And I should be able to verify that the "PUT" method to "/addPaymentModalityApiTest" endpoint received a request with required parameter in body

  Scenario: IAM-003 Update Payment Modality Api Test
    Given I create an IdentityMapperDTO for Update Payment Modality
    When I call the update payment modality API with expected status of 202 and stub "/updatePaymentModalityApiTest"
    Then I make the "PUT" request to "/updatePaymentModalityApiTest" endpoint with expected status of 200
    And I should be able to verify that the "PUT" method to "/updatePaymentModalityApiTest" endpoint received a request with required parameter in body

  Scenario: IAM-003 Account Lookup Api Test
    When I call the account lookup API with expected status of 202 and stub "/accountLookupTest"
    Then I make the "PUT" request to "/accountLookupTest" endpoint with expected status of 200
    And I should be able to verify that the "PUT" method to "/accountLookupTest" endpoint received a request with same payeeIdentity

  Scenario: IAM-004 Account Lookup Api Consistency Test
    When I call the account lookup API 10 times with expected status of 202 and stub "/accountLookup"
    And I make the "PUT" request to "/accountLookup" endpoint with expected status of 200
    Then I should be able to verify that the "PUT" method to "/accountLookup" endpoint received 10 request
    And I can stop mock server

