@gov
Feature: Authentication test for services routed through kong & keycloak

  @keycloak-user-setup @keycloak-user-teardown @kong-teardown
  Scenario: Unauthorized channel-connector test
    Given I have tenant as "gorilla"
    And I can mock TransactionChannelRequestDTO
    And I register channel service using config
    And I will sleep for 2000 millisecond
    And I register channel route using config
    And I will sleep for 2000 millisecond
    And I enable oidc plugin
    And I will sleep for 5000 millisecond
    When I call the inbound transfer endpoint with expected status of 401 and no authentication
    Then I should get non empty response

  @keycloak-user-setup @keycloak-user-teardown @kong-teardown
  Scenario: Authorized channel-connector test
    Given I have tenant as "gorilla"
    And I can mock TransactionChannelRequestDTO
    And I register channel service using config
    And I will sleep for 2000 millisecond
    And I register channel route using config
    And I will sleep for 2000 millisecond
    And I enable oidc plugin
    And I will sleep for 5000 millisecond
    When I authenticate with new keycloak user
    And I call the inbound transfer endpoint with authentication
    Then I should get non empty response
