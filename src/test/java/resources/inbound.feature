Feature: Inbound transaction test

  Scenario: IBT-001 Payer inbound transfer request
    Given I can mock TransactionChannelRequestDTO
    And I have tenant as "gorilla"
    When I call the inbound transfer endpoint with expected status of 200
    Then I should get non empty response
    And I should be able to parse transactionId

  Scenario: IAM-005 Bulk Processor Inbound Integration Test
    When I create an IdentityMapperDTO for registering beneficiary with "gorilla" as DFSPID
    Then I can inject MockServer
    And I can start mock server
    When I call the register beneficiary API with expected status of 202
    Given I have the demo csv file "ph-ee-bulk-demo-7.csv"
    And I have tenant as "rhino"
    When I call the batch transactions endpoint with expected response status of 200
    Then I should be able to parse batch id from response
    When I call the batch details API with expected response status of 200
    And I can assert the payee DFSPID is same as used to register beneficiary id type from response
