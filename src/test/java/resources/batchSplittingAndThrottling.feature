@gov
Feature: Batch splitting and throttling

  Scenario: Verify splitting of batch into sub batches based on transaction count
    Given I have the demo csv file "ph-ee-bulk-demo-20-mojaloop.csv"
    And I have tenant as "gorilla"
    And the system has a configured sub batch size of 5 transactions
    And the transaction count in the batch is greater than sub batch size
    When I call the batch transactions endpoint with expected status of 200
    And I fetch batch ID from batch transaction API's response
    Then I call the batch summary API with expected status of 200
    And I fetch sub batch details from batch summary API response
    And the expected sub batch count is greater than 1

  Scenario: Verify throttling of sub batches based on throttle configuration
    Given I have the demo csv file "ph-ee-bulk-demo-20-mojaloop.csv"
    And I have tenant as "gorilla"
    And the system has a configured throttle time of 30 seconds
    When I call the batch transactions endpoint with expected status of 200
    And I fetch batch ID from batch transaction API's response
    Then I call the batch summary API with expected status of 200
    And I fetch sub batch details from the response
    And the difference between start time of the consecutive sub batches should be greater than or equal to throttle configuration