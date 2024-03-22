@gov
Feature: Test ability to make payment to individual with bank account

  Scenario: Input CSV file using the batch transaction API and poll batch summary API till we get completed status
    Given I have tenant as "paymentbb1"
    And I have the demo csv file "bulk_payment.csv"
    And I create a list of payee identifiers from csv file
    When I can inject MockServer
    Then I can start mock server
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

  @batch-teardown
  Scenario: Bulk Transfer with GSMA and Closedloop
    Given I have Fineract-Platform-TenantId as "gorilla"
    When I create and setup a "payer" with account balance of 100
    Given I have tenant as "paymentBB2"
    Then I call the balance api for payer balance
    When I create and setup a "payee" with id "1" and account balance of 10
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee "1" balance
    Then Create a csv file with file name "batchTransactionGsmaAndClosedLoop.csv"
    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 10 and id 0
    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 11 and id 1
    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 12 and id 2
    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 13 and id 3
    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 14 and id 4
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee "2" balance
    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 15 and id 5
    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 16 and id 6
    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 17 and id 7
    Then add last row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 18 and id 8
    Given I have tenant as "paymentBB1"
    And I have the demo csv file "batchTransactionGsmaAndClosedLoop.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    When I call the batch summary API for gsma with expected status of 200 with total 9 txns
    Then I should get non empty response
    Then I am able to parse batch summary response
    And I should have matching total txn count and successful txn count in response
