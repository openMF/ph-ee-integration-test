Feature: Batch Details API test

  Scenario: Batch summary API Test
    Given I have a batch with id "e33b23e4-895d-485b-96d2-26b7af8268a1"
    And I have tenant as "gorilla"
    When I call the auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch summary API with expected status of 200
    Then I should get non empty response


  Scenario: Batch summary API Test
    Given I have a batch with id "e33b23e4-895d-485b-96d2-26b7af8268a1"
    And I have tenant as "gorilla"
    When I call the auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batch details API with expected status of 200
    Then I should get non empty response
