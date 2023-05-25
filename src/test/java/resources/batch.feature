@gov
Feature: Batch Details API test

  Scenario: Batch transactions API Test
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "gorilla"
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response

  Scenario: Batch transactions API Test with polling callback url
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "gorilla"
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    And I should have "PollingPath" and "SuggestedCallbackSeconds" in response


  Scenario: Batch summary API Test
    Given I have a batch id from previous scenario
    And I have tenant as "gorilla"
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    When I call the auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch summary API with expected status of 200
    Then I should get non empty response

  Scenario: Batch Details API Test
    Given I have a batch id from previous scenario
    And I have tenant as "gorilla"
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    When I call the auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch details API with expected status of 200
    Then I should get non empty response

    Scenario: Batch transaction API Test for Synchronous File Validation with empty file
      Given I have tenant as "gorilla"
      When I call the batch transactions endpoint with expected status of 400 without payload
      Then I should get non empty response
      And I should have "Error Information" and "File not uploaded" in response

  Scenario: Batch transaction API Test for Synchronous File Validation with invalid file
    Given I have tenant as "gorilla"
    And I have the demo csv file "ph-ee-bulk-demoErrorSync-6.csv"
    When I call the batch transactions endpoint with expected status of 400
    Then I should get non empty response
    And I should have "Error Information" and "Invalid file structure" in response

  Scenario: Batch transaction API Test for Asynchronous File Validation
    Given I have tenant as "gorilla"
    And I have the demo csv file "ph-ee-bulk-demoErrorAsync-6.csv"
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    And I should get batchId in response
    Given I have a batch id from previous scenario
    And I have tenant as "gorilla"
    When I call the auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch summary API with expected status of 200
    Then I should get non empty response

  Scenario: Batch Phased Callback API Test Success
    Given I have a batch with id "e33b23e4-895d-485b-96d2-26b7af8268a1"
    And I have tenant as "gorilla"
    And I have callbackUrl as "http://localhost:5001/simulate"
    And I have retry count as 2
    When I should call callbackUrl api
    Then I should get expected status of 200

  Scenario: Batch Phased Callback API Test Failure
    Given I have a batch with id "e33b23e4-895d-485b-96d2-26b7af8268a1"
    And I have tenant as "gorilla"
    And I have callbackUrl as "http://httpstat.us/503"
    And I have retry count as 2
    When I should call callbackUrl api
    Then I should get expected status of 500
