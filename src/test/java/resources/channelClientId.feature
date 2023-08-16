@govtodo
Feature: New Channel API test


  Scenario: TS-001 Get Txn based on request type as transfers Test with Auth
    Given I have tenant as "gorilla"
    And I have request type as "transfers"
    When I call the txn State with client correlation id as 12345 expected status of 200
    Then I should get non empty response



  Scenario: TS-002 Get Txn based on request type as transaction request Test with Auth
    Given I have tenant as "gorilla"
    And I have request type as "transactionsReq"
    When I call the txn State with client correlation id as 123456789 expected status of 200
    Then I should get non empty response



