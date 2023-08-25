@gov-223 @ops-batch-setup @ops-batch-teardown
Feature: Operations APP related feature

  Scenario: Batches API test vanilla
    Given I have tenant as "rhino"
    When I call the operations-app auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batches endpoint with expected status of 200
    Then I should get non empty response
    And I am able to parse batch paginated response into DTO

  Scenario: Batches API test with batchId
    Given I have tenant as "rhino"
    And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    Then I will sleep for 2000 millisecond
    When I call the batch summary API with expected status of 200
    Then I am able to parse batch summary response
    And I should get non empty response
    Then I add batchId query param
    When I call the batches endpoint with expected status of 200
    Then I should get non empty response
    And I am able to parse batch paginated response into DTO
    And I am able to assert only 1 totalBatches

  # store time
  # hit 5 batches
  # sleep for 10 secs
  # calculate time
  Scenario: Batches API filter
    Given I have tenant as "rhino"
    And I store this time as start time
    # Batch 1 call
    And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    # sleep for 1 sec
    Then I will sleep for 1000 millisecond
  	# Batch 2 call
    And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I have clientCorrelationId as "e3bdffd4-f484-4a06-a8d0-e9e4694635cc"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    # sleep for 1 sec
    Then I will sleep for 1000 millisecond
  	# Batch 3 call
    And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I have clientCorrelationId as "54ecbf00-24b8-4fb4-a587-a138dbe02fb0"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    # sleep for 1 sec
    Then I will sleep for 1000 millisecond
  	# Batch 4 call
    And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I have clientCorrelationId as "cac0a8b6-0ec7-452d-9ef1-9bdc4bc0eb5e"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    # sleep for 1 sec
    Then I will sleep for 1000 millisecond
  	# Batch 5 call
    And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    # sleep for 5 sec
