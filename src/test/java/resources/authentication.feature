@gov
Feature: Authentication test for services routed through kong & keycloak

  Scenario: Unauthorized channel-connector test
    Given I can mock TransactionChannelRequestDTO
    And I have tenant as "gorilla"
    When I call the inbound transfer endpoint with expected status of 401 and no authentication
    Then I should get non empty response

  Scenario: Authorized channel-connector test
    Given I can mock TransactionChannelRequestDTO
    And I have tenant as "gorilla"
    When I call the keycloak auth api with "test" username and "password" password
    And I call the inbound transfer endpoint with authentication
