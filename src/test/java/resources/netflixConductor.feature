Feature: DPGA API test

  Scenario: conductor server health test
    When I make a call to nc server health API with expected status 200
    Then I get the value of Healthy as true in response

  Scenario: dpga transfer api trigger test
    Given I have tenant as "lion"
    And I have the request body for transfer
    When I call the channel transfer API with client correlation id and expected status of 200
    Then I should get transaction id in response
    When I call the get workflow API in  with workflow id as path variable
    Then I should get valid status