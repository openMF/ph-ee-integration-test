@gov
@cucumberCli
Feature: Get Transfers API test


        Scenario: GT-001 Get Transfers API With default params
                Given I have tenant as "paymentBB2"
                Then I should get a valid token
                When I call the transfer API with expected status of 200
                Then I should get non empty response
                And I should have clientCorrelationId in response


        Scenario: GT-002 Get Transfers API With Page retrieval and size
                Given I have tenant as "paymentBB2"
                Then I should get a valid token
                When I call the transfer API with size 4 and page 2 expecting expected status of 200
                Then I should get non empty response
                And I should have page and size in response

        Scenario: GT-003 Get Transfers API Within specific date range
                Given I have tenant as "paymentBB2"
                Then I should get a valid token
                When I call the transfer API with specific date range expecting expected status of 200
                Then I should get non empty response

        Scenario: GT-004 Get Transfers API With amount and currency
                Given I have tenant as "paymentBB2"
                Then I should get a valid token
                When I call the transfer API with currency "USD" and amount 1 expecting expected status of 200
                Then I should get non empty response
                And I should have currency and amount in response
