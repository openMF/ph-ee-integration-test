Feature: Batch splitting and throttling

  Scenario: Verify throttling of subbatches based on throttle configuration
    Given the system has a configured throttle time of {throttleTime} seconds
    And a batch with multiple subbatches is being processed
    When the second subbatch is being processed
    Then the system should wait for at least {throttleTime} seconds before processing the second subbatch
    And the start time of the second subbatch or the first transaction from the second subbatch should be later than the throttle time

  Scenario: Verify splitting of subbatches based on transaction count
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "gorilla"
    Given the system has a configured subbatch size of 100 transactions
    When I call the batch transactions endpoint with expected status of 200
    Then I should get non empty response with batchId
    Then the subbatch ID for the first transaction should be different from the subbatch ID of the last transaction within the boundary of the subbatch size config
    And the subbatch IDs for transactions within the same subbatch should be the same