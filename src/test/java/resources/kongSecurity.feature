@gov-security
Feature: Kong security test

  @kong-teardown
  Scenario: API-KEY authentication test
    Given I have required Kong configuration
    When I create new consumer
    And I am able to create a key for above consumer
    And I register a service in kong
    And I register a route to above service in kong
    And I add the key-auth plugin in above service
    And I wait for 5 seconds
    Then When I call the service endpoint with api key I should get 200
