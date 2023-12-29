@gov-security
Feature: Kong security test

  @kong-teardown
  Scenario: API-KEY authentication test
    Given I have required Kong configuration
    When I create new consumer
    And I wait for 2 seconds
    And I am able to create a key for above consumer
    And I wait for 2 seconds
    And I register a service in kong
    And I wait for 2 seconds
    And I register a route to above service in kong
    And I wait for 2 seconds
    And I add the key-auth plugin in above service
    And I wait for 5 seconds
    Then When I call the service endpoint with api key I should get 200


  @GOV-410
  @kong-teardown
  Scenario: Kong Ratelimiter test
    Given I have required Kong configuration
    When I create new consumer
    And I wait for 2 seconds
    And I am able to create a key for above consumer
    And I wait for 2 seconds
    And I register a service in kong
    And I wait for 2 seconds
    And I register a route to above service in kong
    And I wait for 2 seconds
    And I add the ratelimiter plugin in kong
    And I wait for 5 seconds
    Then When I call the service endpoint with api key I should get 200
    And I wait for 1 seconds
    Then When I call the service endpoint with api key I should get 200
    And I wait for 1 seconds
    Then When I call the service endpoint with api key I should get 200
    And I wait for 1 seconds
    Then When I call the service endpoint with api key I should get 200
    And I wait for 1 seconds
    Then When I call the service endpoint with api key I should get 200
    And I wait for 1 seconds
    Then When I call the service endpoint with api key I should get 429
    And I should have "API rate limit exceeded" in response body
