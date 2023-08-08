Feature: Get Txn Req API test


  Scenario: GTX-001 Get Txn Req API Test With Auth
    Given I have tenant as "gorilla"
    When I call the operations-app auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the get txn API with expected status of 200
    Then I should get non empty response
    And I should have clientCorrelationId in response


    Scenario: GTX-002 Get Txn Req API with Params
      Given I have tenant as "gorilla"
      And I call collection api with expected status 200
      When I call the operations-app auth endpoint with username: "mifos" and password: "password"
      Then I should get a valid token
      When I call the get txn API with date "2023-03-24 00:00:00" and "2023-03-24 23:59:59" expected status of 200
      Then I should get non empty response
      And I should have startedAt and completedAt in response
