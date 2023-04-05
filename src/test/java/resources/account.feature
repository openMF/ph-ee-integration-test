Feature: Account Management test

  Scenario: Account status API Test
    Given I have tenant as "gorilla"
    When I call accountStatus endpoint for "835322416" MSISDN, with status of 200
    Then I should get non empty response
