Feature: GSMA API test

  Scenario: GSMA International Remmittance Payee API Test
    Given I can create GSMATransactionDTO
    And I have tenant as "gorilla"
    When I call the GSMATransaction endpoint with expected status of 200
    Then I should get non empty response