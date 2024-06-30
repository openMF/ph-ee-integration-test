Feature: Batch Details API test

  @commonExtended @gov @batch-teardown
  Scenario: BD-001 Batch transactions API Test
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response

  @commonExtended @gov @batch-teardown
  Scenario: BD-002 Batch transactions API Test with polling callback url
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I should have "PollingPath" and "SuggestedCallbackSeconds" in response

  @commonExtended @gov @batch-teardown
  Scenario: BD-003 Batch summary API Test
    Given I have a batch id from previous scenario
    And I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 5000 millisecond
    When I call the operations-app auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch summary API with expected status of 200 with total 10 txns
    Then I should get non empty response

  @commonExtended @gov @batch-teardown
  Scenario: BD-004 Batch Details API Test
    Given I have a batch id from previous scenario
    And I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    When I call the operations-app auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch details API with expected status of 200 with total 10 txns
    Then I should get non empty response


  	@commonExtended @gov @batch-teardown
    Scenario: BD-005 Batch transaction API Test for Synchronous File Validation with empty file
      Given I have tenant as "paymentBB2"
      And I make sure there is no file
      And I generate clientCorrelationId
      And I have private key
      And I generate signature
      When I call the batch transactions endpoint with expected status of 400 without payload
      Then I should get non empty response
      And I should have "errorInformation" and "File not uploaded" in response

  @commonExtended @gov @batch-teardown
  Scenario: BD-006 Batch transaction API Test for Synchronous File Validation with invalid file
    Given I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-demoErrorSync-6.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 400
    Then I should get non empty response
    And I should have "Error Information" and "Invalid file structure" in response

  @commonExtended @gov @batch-teardown
  Scenario: BD-007 Batch transaction API Test for Asynchronous File Validation
    Given I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-demoErrorAsync-6.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 8000 millisecond
    And I have tenant as "paymentBB2"
    When I call the operations-app auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch summary API with expected status of 200 with total 5 txns
    Then I should get non empty response

  @commonExtended @gov @batch-teardown
  Scenario: BD-008 Batch Phased Callback API Test Success
    Given I have a batch id from previous scenario
    And I have tenant as "paymentBB2"
    And I have callbackUrl as simulated url
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I have retry count as 2
    When I should call callbackUrl api
    Then I should get expected status of 200

  @govtodo @batch-teardown
  Scenario: BD-009 Batch Phased Callback API Test Failure
    Given I have a batch id from previous scenario
    And I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    And I have callbackUrl as "http://httpstat.us/503"
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    Given I have retry count as 2
    When I should call callbackUrl api
    Then I should get expected status of 503

  @commonExtended @gov @batch-teardown
  Scenario: BD-010 Batch summary with failure percent API Test
    Given I have a batch id from previous scenario
    And I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    When I call the operations-app auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch details API with expected status of 200 with total 10 txns
    Then I should get non empty response with failure and success percentage

  @gov @batch-teardown
  Scenario: BD-011 Batch test for payerIdentifier resolution using budgetAccount info
    Given I have tenant as "paymentBB2"
	And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 15000 millisecond
    When I call the batch summary API with expected status of 200 with total 10 txns
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"

  @commonExtended @gov @batch-teardown
  Scenario: BD-012 Batch Transaction REST Api test
    Given I have tenant as "paymentBB2"
    And I create a new clientCorrelationId
	And I can mock the Batch Transaction Request DTO without payer info
    And I have private key
    And I generate signature
    When I call the batch transactions raw endpoint with expected status of 202
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 10000 millisecond
    When I call the batch summary API with expected status of 200 with total 1 txns
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"
    And I should have matching total txn count and successful txn count in response

  @commonExtended @gov @batch-teardown
  Scenario: BD-013,BT-001 Batch aggregate API Test
    Given I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-demo-7.csv"
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 10000 millisecond
    When I call the batch aggregate API with expected status of 200 with total 3 txns
    Then I should get non empty response
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"
    And I should have matching total txn count and successful txn count in response

  @commonExtended @gov @ext @batch-teardown
  Scenario: BD-014 Sub Batch summary API Test
    Given I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-demo-7.csv"
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 10000 millisecond
    And I call the sub batch summary API for sub batch summary with expected status of 200 and total count 3
    Then I am able to parse sub batch summary response
    And I should assert total txn count and successful txn count in response

  @commonExtended @gov @batch-teardown
  Scenario: BD-015 Batch splitting test
    Given I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-splitting.csv"
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 10000 millisecond
    When I call the batch summary API with expected status of 200 with total 12 txns
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"
    And I should have matching total txn count and successful txn count in response
    And I call the sub batch summary API for sub batch summary with expected status of 200 and total count 12
    Then I am able to parse sub batch summary response
    And I should assert total txn count and successful txn count in response
    And Total transaction in batch should add up to total transaction in each sub batch

  @commonExtended @gov @ext
  Scenario: BD-016 Payment Batch Detail API Test
    Given I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-splitting.csv"
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 10000 millisecond
    And I call the payment batch detail API with expected status of 200 with total 12 txns
    Then I am able to parse payment batch detail response
    And I should assert total txn count and successful txn count in payment batch detail response

  @commonExtended @gov @batch-teardown
  Scenario: BD-017 Batch test for de-duplicating payments
    Given I have the demo csv file "deduplication-test.csv"
    And I have tenant as "paymentBB2"
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 10000 millisecond
    And I call the batch details API with expected status of 200 with total 3 txns
    Then I should get transactions with note set as "Duplicate transaction"
    And All the duplicate transaction should have status as Failed

  @commonExtended @gov
  Scenario: BA-001 Batch Authorization API test
    Given I will start the mock server
    And I can register the stub with "/authorization/callback" endpoint for "POST" request with status of 200
    Then I will update the  mock server and register stub as done
    When I create an AuthorizationRequest for Batch Authorization with batch ID as "1234", payerIdentifier as "5678", currency as "USD" and amount as "30"
    And I call the Authorization API with batchId as "1234" and expected status of 202 and stub "/authorization/callback"
#    And I will sleep for 5000 millisecond
    Then I should be able to verify that the "POST" method to "/authorization/callback" endpoint received a request with authorization status

  @commonExtended @gov @ext
  Scenario: BD-018 Batch with callback
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/callback" endpoint for "POST" request with status of 200
    Then I will update the  mock server and register stub as done
    Given I have tenant as "paymentBB2"
    And I have the demo csv file "ph-ee-bulk-demo-7.csv"
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202 and callbackurl as "/callback"
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
#    Then I will sleep for 5000 millisecond
    When I call the batch summary API with expected status of 200 with total 3 txns
#    And I will sleep for 15000 millisecond
    Then I should be able to extract response body from callback for batch
    When I make the "POST" request to "/callback" endpoint with expected status of 200
    Then I should be able to extract response body from callback for batch
    And I can stop mock server
  @commonExtended @gov @batch-teardown
  Scenario: BD-019 Batch summary response result file URL Test
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    When I call the operations-app auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    And I call the sub batch summary API for result file url with expected status of 200
    Then I am able to parse sub batch summary response
    Then I check for result file URL validity

  @gov
  Scenario: APT-001 actuator API test
    When I call the actuator API with Contactpoint "mock-payment-schema.contactpoint" and endpoint "/actuator/health"
    Then I am able to parse actuator response
    And Status of service is "UP"
    When I call the actuator API with Contactpoint "operations-app.contactpoint" and endpoint "/actuator/health"
    Then I am able to parse actuator response
    And Status of service is "UP"
    When I call the actuator API with Contactpoint "bulk-processor.contactpoint" and endpoint "/actuator/health"
    Then I am able to parse actuator response
    And Status of service is "UP"
    When I call the actuator API with Contactpoint "ml-connector.contactpoint" and endpoint "/actuator/health"
    Then I am able to parse actuator response
    And Status of service is "UP"
    When I call the actuator API with Contactpoint "identity-account-mapper.contactpoint" and endpoint "/actuator/health"
    Then I am able to parse actuator response
    And Status of service is "UP"
    When I call the actuator API with Contactpoint "voucher-management.contactpoint" and endpoint "/actuator/health"
    Then I am able to parse actuator response
    And Status of service is "UP"
