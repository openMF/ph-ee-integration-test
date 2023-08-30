@gov
Feature: Operations APP related feature

  @ops-batch-setup @ops-batch-teardown
  Scenario: Batches API no filter test
    Given I have tenant as "rhino"
    When I call the operations-app auth endpoint with username: "mifos" and password: "password"
    Then I should get a valid token
    When I call the batches endpoint with expected status of 200
    Then I should get non empty response
    And I am able to parse batch paginated response into DTO

  @ops-batch-setup @ops-batch-teardown
  Scenario: Batches API batchId filter test
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
    And I am able to assert 1 totalBatches

  # create 2 batch txn
  # set offset to 0 and limit to 1
  # assert single batch is returned
  # set offset to 1 and limit to 1
  @ops-batch-setup @ops-batch-teardown
  Scenario: Batches API pagination test
    # Batch 1 call
    Given I have tenant as "rhino"
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
    And I add limit filter 1
    And I add offset filter 0
    When I call the batches endpoint with expected status of 200
    Then I should get non empty response
    And I am able to parse batch paginated response into DTO
    And The count of batches should be 1
    When I add limit filter 1
    And I add offset filter 1
	And I call the batches endpoint with expected status of 200
    Then I should get non empty response
    And I am able to parse batch paginated response into DTO
    And The count of batches should be 1

  # store startTime
  # hit 5 batches
  # sleep for some secs
  # calculate endTime
  # query using startTime and endTime ->> then I should get 5 txn
  @ops-batch-setup @ops-batch-teardown
  Scenario: Batches API date filter test
    Given I will sleep for 5000 millisecond
    And I have tenant as "rhino"
    And I store this time as start time
    # Batch 1 call
    And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    Then I call the batch transactions endpoint with expected status of 202
    # sleep for 1 sec
    And I will sleep for 2000 millisecond
  	# Batch 2 call
    And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I have clientCorrelationId as "e3bdffd4-f484-4a06-a8d0-e9e4694635cc"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    # sleep for 1 sec
    Then I will sleep for 2000 millisecond
  	# Batch 3 call
    And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I have clientCorrelationId as "54ecbf00-24b8-4fb4-a587-a138dbe02fb0"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    # sleep for 1 sec
    Then I will sleep for 2000 millisecond
  	# Batch 4 call
    And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I have clientCorrelationId as "cac0a8b6-0ec7-452d-9ef1-9bdc4bc0eb5e"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    # sleep for 1 sec
    Then I will sleep for 2000 millisecond
  	# Batch 5 call
    And I have the demo csv file "payerIdentifier-resolution-using-budgetAccount.csv"
    And I have the registeringInstituteId "123"
    And I have the programId "SocialWelfare"
    And I have clientCorrelationId as "9051df83-e13c-4d6c-a850-220874db737a"
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    # sleep for 5 sec
    Then I will sleep for 5000 millisecond
  	And I add date from filter
    And I add date to filter
    When I call the batches endpoint with expected status of 200
    Then I should get non empty response
    And I am able to parse batch paginated response into DTO
    And I am able to assert 5 totalBatches
