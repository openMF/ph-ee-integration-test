@gov
Feature: Channel Get Txn by Client Id API test


  Scenario: TS-001 Get Txn based on request type as transfers Test with Auth
    Given I have tenant as "gorilla"
    And I have request type as "transfers"
    When I call the txn State with client correlation id as "f1e22fe3-9740-4fba-97b6-78f43bfa7f2f" expected status of 200
    Then I should get non empty response



  Scenario: TS-002 Get Txn based on request type as transaction request Test with Auth
    Given I have tenant as "gorilla"
    And I have request type as "transactionsReq"
    When I call the txn State with client correlation id as "a2e22fe5-9740-4fba-97b6-78f43bfa7f2f" expected status of 200
    Then I should get non empty response



