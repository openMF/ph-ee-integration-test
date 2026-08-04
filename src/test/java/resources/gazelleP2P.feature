@gazelle
Feature: Gazelle P2P Transfer

  Scenario: Successful P2P transfer from greenbank to bluebank
    Given I have payer in tenant "greenbank"
    And I have payee in tenant "bluebank"
    When I make a transfer of amount "10" in "USD"
    Then the transfer response status should be 200
    And the response should contain a valid transaction ID
