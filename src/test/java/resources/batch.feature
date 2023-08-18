
Feature: Batch Details API test

  @gov
  Scenario: BD-001 Batch transactions API Test
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "gorilla"
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response

  @gov
  Scenario: BD-002 Batch transactions API Test with polling callback url
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "gorilla"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    And I should have "PollingPath" and "SuggestedCallbackSeconds" in response

  @gov
  Scenario: BD-003 Batch summary API Test
    Given I have a batch id from previous scenario
    And I have tenant as "gorilla"
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    When I call the auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch summary API with expected status of 200
    Then I should get non empty response

  @gov
  Scenario: BD-004 Batch Details API Test
    Given I have a batch id from previous scenario
    And I have tenant as "gorilla"
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    When I call the auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch details API with expected status of 200
    Then I should get non empty response


  @gov
    Scenario: BD-005 Batch transaction API Test for Synchronous File Validation with empty file
      Given I have tenant as "gorilla"
      And I make sure there is no file
      And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
      And I have private key
      And I generate signature
      When I call the batch transactions endpoint with expected status of 400 without payload
      Then I should get non empty response
      And I should have "Error Information" and "File not uploaded" in response

  @gov
  Scenario: BD-006 Batch transaction API Test for Synchronous File Validation with invalid file
    Given I have tenant as "gorilla"
    And I have the demo csv file "ph-ee-bulk-demoErrorSync-6.csv"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 400
    Then I should get non empty response
    And I should have "Error Information" and "Invalid file structure" in response

  @gov
  Scenario: BD-007 Batch transaction API Test for Asynchronous File Validation
    Given I have tenant as "gorilla"
    And I have the demo csv file "ph-ee-bulk-demoErrorAsync-6.csv"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    And I should get batchId in response
    Given I have a batch id from previous scenario
    And I have tenant as "gorilla"
    When I call the auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch summary API with expected status of 200
    Then I should get non empty response

  @gov
  Scenario: BD-008 Batch Phased Callback API Test Success
    Given I have a batch id from previous scenario
    And I have tenant as "gorilla"
    And I have callbackUrl as simulated url
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    And I have retry count as 2
    When I should call callbackUrl api
    Then I should get expected status of 200

  @govtodo
  Scenario: BD-009 Batch Phased Callback API Test Failure
    Given I have a batch id from previous scenario
    And I have tenant as "gorilla"
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    And I have callbackUrl as "http://httpstat.us/503"
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    Given I have retry count as 2
    When I should call callbackUrl api
    Then I should get expected status of 503

  @gov
  Scenario: BD-010 Batch summary with failure percent API Test
    Given I have a batch id from previous scenario
    And I have tenant as "gorilla"
    And I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    When I call the auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch details API with expected status of 200
    Then I should get non empty response with failure and success percentage
