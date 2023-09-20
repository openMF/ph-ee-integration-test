Feature: netflixConductor API test

  Scenario: conductor server health test
    When I make a call to nc server health API with expected status 200
    Then I get the value of Healthy as true in response