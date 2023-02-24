Feature: Interop Error Code Test

  Scenario: GSMA Transfer Api PayerNotFound Test
    Given I can create GSMATransactionDTO with incorrect Payer
    And I have tenant as "gorilla"
    When I call the GSMATransaction endpoint with expected status of 200
    And I should be able to parse transactionId from response
    When I call the auth endpoint
    Then I should get a valid token
    Then I should poll the transfer query endpoint with transactionId until errorInformation is populated for the transactionId
    And I should be able to parse "PayerNotFound" Error Code from response

  Scenario: GSMA Transfer Api PayerInsufficientBalance Test
    Given I can create GSMATransactionDTO with Payer Insufficient Balance
    And I have tenant as "gorilla"
    When I call the GSMATransaction endpoint with expected status of 200
    And I should be able to parse transactionId from response
    And I will sleep for 10000 millisecond
    When I call the auth endpoint
    Then I should get a valid token
    Then I should poll the transfer query endpoint with transactionId until errorInformation is populated for the transactionId
    And I should be able to parse "PayerInsufficientBalance" Error Code from response


