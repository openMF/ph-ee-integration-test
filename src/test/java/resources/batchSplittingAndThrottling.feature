@abdc
Feature: Batch splitting and throttling

  Scenario: Verify splitting of batch into sub batches based on transaction count
    Given the csv file "ph-ee-bulk-demo-20-mojaloop.csv" is available
    And I have tenant as "gorilla"
    And the system has a configured sub batch size of 5 transactions
    When the batch transaction API is initiated with the uploaded file
    And the expected sub batch count is calculated
    Then the actual sub batch count is calculated from the response
    And the expected sub batch count and actual sub batch count should be equal

  Scenario: Verify throttling of sub batches based on throttle configuration
    Given the csv file "ph-ee-bulk-demo-20.csv" is available
    And I have tenant as "gorilla"
    And the system has a configured throttle time of 30 seconds
    When the batch transaction API is initiated with the uploaded file
    Then the start time for the sub batches are retrieved
    And the difference between start time of the consecutive sub batches should be greater than or equal to throttle configuration