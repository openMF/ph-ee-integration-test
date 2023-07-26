@gov
Feature: Test ability to make payment to individual with bank account

  Scenario: Input CSV file using the batch transaction API and poll batch summary API till we get completed status
    Given the CSV file is available
    When initiate the batch transaction API with the input CSV file with tenant as "gorilla"
    And the batch ID for the submitted CSV file
    And poll the batch summary API using the batch ID and tenant as "rhino"
    Then successful transactions percentage should be greater than or equal to minimum threshold

