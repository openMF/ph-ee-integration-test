@common @gov
Feature: Testing the startup and working of mockserver

  Scenario: MS-001 Mockserver config test
    Given I can inject MockServer
    And I can start mock server
    Then I should be able to get instance of mock server
    And I can stop mock server

  Scenario: MS-002 Mockserver stub test
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/testMockServer" endpoint for "POST" request with status of 200
    When I make the "POST" request to "/testMockServer" endpoint with expected status of 200
    Then I should be able to verify that the "POST" method to "/testMockServer" endpoint received 1 request
    And I can stop mock server
