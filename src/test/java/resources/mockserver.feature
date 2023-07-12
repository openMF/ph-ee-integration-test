@gov
Feature: Testing the startup and working of mockserver

  Scenario: Mockserver config test
    Given I can inject MockServer
    And I can start mock server
    Then I should be able to get instance of mock server
    And I can stop mock server

  Scenario: Mockserver stub test
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/test" endpoint for "POST" request with status of 200
    When I make the "POST" request to "/test" endpoint with expected status of 200
    Then I should be able to verify that the "POST" method to "/test" endpoint received 1 request
    And I can stop mock server
