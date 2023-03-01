Feature: Batch Details API test

  Scenario: Batch transactions API Test
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "gorilla"
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response

  Scenario: Batch summary API Test
    Given I have a batch id from previous scenario
    And I have tenant as "gorilla"
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    When I call the auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch summary API with expected status of 200
    Then I should get non empty response

  Scenario: Batch summary API Test
    Given I have a batch id from previous scenario
    And I have tenant as "gorilla"
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response
    When I call the auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch details API with expected status of 200
    Then I should get non empty response
