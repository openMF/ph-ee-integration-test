@gov
@cucumberCli
Feature: Get Transfers API test


        Scenario: GT-001 Get Transfers API With Auth
                Given I have tenant as "paymentBB2"
                When I call the operations-app auth endpoint with username: "mifos" and password: "password"
                Then I should get a valid token
                When I call the transfer API with expected status of 200
                Then I should get non empty response
                And I should have clientCorrelationId in response


        Scenario: GT-002 Get Transfers API With Page retrieval and size
                Given I have tenant as "paymentBB2"
                When I call the operations-app auth endpoint with username: "mifos" and password: "password"
                Then I should get a valid token
                When I call the transfer API with size 4 and page 2 expecting expected status of 200
                Then I should get non empty response
