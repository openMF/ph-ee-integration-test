@gov
Feature: Test ability to make payment to individual with bank account

  Scenario: Input CSV file using the batch transaction API and poll batch summary API till we get completed status
    Given I have tenant as "paymentbb1"
    And I have the demo csv file "bulk_payment.csv"
    And I create a list of payee identifiers from csv file
    #Given I will assign a port to mock server
    #When I can inject MockServer
    #Then I can start mock server
    And I can register the stub with "/registerBeneficiary" endpoint for "PUT" request with status of 200
    And I create a IdentityMapperDTO for registering beneficiary
    Then I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiary"
    And I should be able to verify that the "PUT" method to "/registerBeneficiary" endpoint received a request with successfull registration
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 10000 millisecond
    Given I have tenant as "paymentbb1"
    When I call the batch summary API with expected status of 200 with total 6 txns
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"
    And I should have matching total txn count and successful txn count in response
