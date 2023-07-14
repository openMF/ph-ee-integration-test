Feature: Batch splitting and throttling

  Scenario: Verify splitting of batch into sub batches based on transaction count
    Given the csv file "ph-ee-bulk-demo-20.csv" is available
    And I have tenant as "gorilla"
    And the system has a configured subbatch size of 5 transactions
    And the first and last transactions from the CSV file are fetched
    When the batch transaction API is initiated with the uploaded file
    Then the sub batch IDs for the given request ID are retrieved
    And the sub batch IDs for the first and last transactions should be different

  Scenario: Verify throttling of sub batches based on throttle configuration
    Given the csv file "ph-ee-bulk-demo-20.csv" is available
    And I have tenant as "gorilla"
    And the system has a configured throttle time of 30 seconds
    And the first transactions are fetched from consecutive sub batches based on sub batch size of 5 transactions
    When the batch transaction API is initiated with the uploaded file
    Then the start time for the consecutive sub batch IDs are retrieved
    And the difference between start time of first sub batch and second sub batch should be greater than or equal to throttle configuration