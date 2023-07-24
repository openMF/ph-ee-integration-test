Feature: Batch Authorization Test

  Scenario: Batch Authorization API test
    Given I have the demo csv file "ph-ee-bulk-demo-6.csv"
    And I have tenant as "gorilla"
    When I call the batch transactions endpoint with expected status of 200
    Then I should get batchId in response
    When I call the batch summary API with expected status of 200
    Then I should get batch status as "FAILED"


