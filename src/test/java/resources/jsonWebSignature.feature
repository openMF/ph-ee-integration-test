@gov
Feature: Json Web Signature test

  Scenario: JWS-001 Test the jws for batchTransactions
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "gorilla"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response

  Scenario: JWS-002 Test the jws in response for batchTransactions
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "gorilla"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And The response should have non empty header X-SIGNATURE
    And The signature should be able successfully validated against certificate
