Feature: Account Status and Account Name Check api

  @common @amsIntegration
  Scenario: Savings account Status Test
    Given I have Fineract-Platform-TenantId as "payerfsp1"
    When I create a set of debit and credit party
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the debit interop identifier endpoint with MSISDN
    Given I have tenant as "payerfsp"
    Then I call the account status api
    And I can assert "submitted" status in response
    Then I approve the deposit with command "approve"
    Given I have tenant as "payerfsp"
    Then I call the account status api
    And I can assert "approved" status in response
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 11
    Given I have tenant as "payerfsp"
    Then I call the account status api
    And I can assert "active" status in response

  @common @amsIntegration
  Scenario: Savings account Name Test
    Given I have Fineract-Platform-TenantId as "payerfsp1"
    When I create a set of debit and credit party
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the debit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 100
    Given I have tenant as "payerfsp"
    Then I call the account name api
    Then I can assert name in response