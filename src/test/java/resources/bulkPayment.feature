@bulk
Feature: Test ability to make payment to individual with bank account

  Scenario: Input CSV file using the batch transaction API and poll batch summary API till we get completed status
    Given I have tenant as "lion"
    And I have the demo csv file "bulk_payment.csv"
    And I create a new clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    Then I should get non empty response
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    Then I will sleep for 10000 millisecond
    Given I have tenant as "lion"
    When I call the batch summary API with expected status of 200
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"
    And I should have matching total txn count and successful txn count in response