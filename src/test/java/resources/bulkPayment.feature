@e2e
Feature: Test ability to make payment to individual with bank account

  Scenario:BB-FSP 001 Input CSV file using the batch transaction API and poll batch summary API till we get completed status
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

  Scenario:BB-FSP 002 Bulk Transfer with ClosedLoop and Mojaloop
    Given I have tenant as "paymentBB2"
    And I have the demo csv file "bulk_payment_closedl_mock_mojaloop.csv"
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
    Then I will sleep for 10000 millisecond
    Given I have tenant as "paymentBB2"
    When I call the batch summary API with expected status of 200 with total successfull 8 txns
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"
    And My total txns 8 and successful txn count in response should Match

  Scenario:BB-FSP 003 Bulk Transfer with ClosedLoop and GSMA
    #payer 1 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    And I initialize the payee list
    When I create and setup a "payer" with id "1" and account balance of 100 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "1" balance for combine test cases
    #payee 1 creation
    When I create and setup a "payee" with id "1" and account balance of 10 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "1" balance for combine test cases

    Then Create a csv file with file name "batchTransactionGsmaClosedLoop.csv"
    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 10 and id 0 for combine test cases

    #payer 2 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "2" and account balance of 50 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "2" balance for combine test cases
    #payee 2 creation
    When I create and setup a "payee" with id "2" and account balance of 20 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "2" balance for combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 1 for combine test cases

        #payer 3 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "3" and account balance of 50 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "3" balance
    #payee 2 creation
    When I create and setup a "payee" with id "3" and account balance of 20 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "3" balance for combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 2 for combine test cases

        #payer 4 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "4" and account balance of 50 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "4" balance for combine test cases
    #payee 2 creation
    When I create and setup a "payee" with id "4" and account balance of 20 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "4" balance for combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 3 for combine test cases
    #payer 4 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "5" and account balance of 50 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "5" balance for combine test cases
    #payee 2 creation
    When I create and setup a "payee" with id "5" and account balance of 20 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "5" balance for combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 4 for combine test cases

        #payer 4 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "6" and account balance of 50 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "6" balance for combine test cases
    #payee 2 creation
    When I create and setup a "payee" with id "6" and account balance of 20 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "6" balance for combine test cases

    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 6 and id 5 for combine test cases

        #payer 5 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "7" and account balance of 50 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "7" balance for combine test cases
    #payee 2 creation
    When I create and setup a "payee" with id "7" and account balance of 20 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "7" balance for combine test cases

    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 7 and id 6 for combine test cases
    #payer 6 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "8" and account balance of 30 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "8" balance for combine test cases
    #payee 3 creation
    When I create and setup a "payee" with id "8" and account balance of 30 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "8" balance for combine test cases

    Then add last row to csv with current payer and payee, payment mode as "gsma" and transfer amount 8 and id 7

    When I can inject MockServer
    Then I can start mock server
    And I can register the stub with "/registerBeneficiary" endpoint for "PUT" request with status of 200
    And I create a IdentityMapperDTO for registering beneficiary
    Then I call the register beneficiary API with expected status of 202 and stub "/registerBeneficiary"
    And I should be able to verify that the "PUT" method to "/registerBeneficiary" endpoint received a request with successfull registration
    #batch process
    Given I have tenant as "payerfsp"
    And I have the demo csv file "batchTransactionGsmaClosedLoop.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    Then I will sleep for 10000 millisecond
    When I call the batch summary API with expected status of 200 with total successfull 8 txns
    Then I should get non empty response
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"
    And My total txns 8 and successful txn count in response should Match


  Scenario:BB-FSP 004 Bulk Transfer with Closedloop and Real Mojaloop
      #payer 1 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    And I initialize the payee list
    When I create and setup a "payer" with id "1" and account balance of 100 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "1" balance for combine test cases
    #payee 1 creation
    When I create and setup a "payee" with id "1" and account balance of 10 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "1" balance for combine test cases

    Then Create a csv file with file name "batchTransactioClosedLoopMojaloopFundTransfer.csv"
    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 10 and id 0 for combine test cases

    #payer 2 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "2" and account balance of 50 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "2" balance for combine test cases
    #payee 2 creation
    When I create and setup a "payee" with id "2" and account balance of 20 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "2" balance for combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 1 for combine test cases

        #payer 3 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "3" and account balance of 50 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "3" balance
    #payee 3 creation
    When I create and setup a "payee" with id "3" and account balance of 20 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "3" balance for combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 2 for combine test cases

        #payer 4 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "4" and account balance of 50 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "4" balance for combine test cases
    #payee 4 creation
    When I create and setup a "payee" with id "4" and account balance of 20 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "4" balance for combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 3 for combine test cases
    #payer 5 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "5" and account balance of 50 for combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "5" balance for combine test cases
    #payee 5 creation
    When I create and setup a "payee" with id "5" and account balance of 20 for combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "5" balance for combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 4 for combine test cases
    #Mojaloop
    Given I am setting up Mojaloop
    #payer and payee 6 for mojaloop [1]
    When I create and setup a "payer" with account balance of 12
    When I create and setup a "payee" with account balance of 10
    Then I add "payer" to als
    Then I add "payee" to als
    Then add row to csv with current payer and payee, payment mode as "mojaloop" and transfer amount 3 and id 5 for combine test cases

    #payer and payee 7 for mojaloop [2]
    Then I add "payer" to als
    When I create and setup a "payer" with account balance of 120
    Then add row to csv with current payer and payee, payment mode as "mojaloop" and transfer amount 2 and id 6 for combine test cases

    When I create and setup a "payer" with account balance of 66
    When I create and setup a "payee" with account balance of 10
    Then I add "payer" to als
    Then I add "payee" to als
    Then add last row to csv with current payer and payee, payment mode as "mojaloop" and transfer amount 1 and id 7

    Given I have Fineract-Platform-TenantId for "payer"
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    Then I will sleep for 10000 millisecond
    Given I have tenant as "payerfsp"
    When I call the batch summary API with expected status of 200 with total successfull 8 txns
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"
    And My total txns 8 and successful txn count in response should Match


  Scenario:BB-FSP 005 Bulk Transfer with ClosedLoop, Real mojaloop and Real GSMA
    #payer 1 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    And I initialize the payee list
    When I create and setup a "payer" with id "1" and account balance of 100 for all combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "1" balance for all combine test cases
    #payee 1 creation
    When I create and setup a "payee" with id "1" and account balance of 10 for all combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "1" balance for all combine test cases

    Then Create a csv file with file name "batchTransactionGsmaClosedLoopMojaloop.csv"
    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 10 and id 0 for all combine test cases

    #payer 2 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "2" and account balance of 50 for all combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "2" balance for all combine test cases
    #payee 2 creation
    When I create and setup a "payee" with id "2" and account balance of 20 for all combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "2" balance for all combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 1 for all combine test cases

        #payer 3 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "3" and account balance of 50 for all combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "3" balance for all combine test cases
    #payee 3 creation
    When I create and setup a "payee" with id "3" and account balance of 20 for all combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "3" balance for all combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 2 for all combine test cases

        #payer 4 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "4" and account balance of 50
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "4" balance for all combine test cases
    #payee 4 creation
    When I create and setup a "payee" with id "4" and account balance of 20 for all combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "4" balance for all combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 3 for all combine test cases
    #payer 5 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "5" and account balance of 50 for all combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "5" balance for all combine test cases
    #payee 5 creation
    When I create and setup a "payee" with id "5" and account balance of 20 for all combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "5" balance for all combine test cases

    Then add row to csv with current payer and payee, payment mode as "closedloop" and transfer amount 5 and id 4 for all combine test cases

        #payer 6 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "6" and account balance of 50 for all combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "6" balance for all combine test cases
    #payee 6 creation
    When I create and setup a "payee" with id "6" and account balance of 20 for all combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "6" balance for all combine test cases

    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 6 and id 5 for all combine test cases

        #payer 7 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "7" and account balance of 50 for all combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "7" balance for all combine test cases
    #payee 7 creation
    When I create and setup a "payee" with id "7" and account balance of 20 for all combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "7" balance for all combine test cases

    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 7 and id 6 for all combine test cases
    #payer 8 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "8" and account balance of 30 for all combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "8" balance for all combine test cases
    #payee 8 creation
    When I create and setup a "payee" with id "8" and account balance of 30 for all combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "8" balance for all combine test cases

    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 8 and id 7 for all combine test cases

            #payer 9 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "9" and account balance of 50 for all combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "9" balance for all combine test cases
    #payee 9 creation
    When I create and setup a "payee" with id "9" and account balance of 20 for all combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "9" balance for all combine test cases

    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 9 and id 8 for all combine test cases
    #payer 10 creation
    Given I have Fineract-Platform-TenantId as "payerfsp2"
    When I create and setup a "payer" with id "10" and account balance of 30 for all combine test cases
    Given I have tenant as "payerfsp"
    Then I call the balance api for payer "10" balance for all combine test cases
    #payee 10 creation
    When I create and setup a "payee" with id "10" and account balance of 30 for all combine test cases
    Given I have tenant as "payeefsp3"
    Then I call the balance api for payee "10" balance for all combine test cases

    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 10 and id 9 for all combine test cases

    #Mojaloop batch setup
    Given I am setting up Mojaloop
    #payer and payee 10 for mojaloop [1]
    When I create and setup a "payer" with account balance of 12
    When I create and setup a "payee" with account balance of 10
    Then I add "payer" to als
    Then I add "payee" to als
    Then add row to csv with current payer and payee, payment mode as "mojaloop" and transfer amount 3 and id 10 for all combine test cases

    #payer and payee 11 for mojaloop [2]
    Then I add "payer" to als
    When I create and setup a "payer" with account balance of 120
    Then add row to csv with current payer and payee, payment mode as "mojaloop" and transfer amount 2 and id 11 for all combine test cases

    #payer and payee 12 for mojaloop [3]
    When I create and setup a "payer" with account balance of 66
    When I create and setup a "payee" with account balance of 10
    Then I add "payer" to als
    Then I add "payee" to als
    Then add last row to csv with current payer and payee, payment mode as "mojaloop" and transfer amount 1 and id 12

    #batch process
    Given I have tenant as "payerfsp"
    And I have the demo csv file "batchTransactionGsmaClosedLoopMojaloop.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    Then I will sleep for 10000 millisecond
    When I call the batch summary API with expected status of 200 with total successfull 13 txns
    Then I should get non empty response
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"
    And My total txns 13 and successful txn count in response should Match
