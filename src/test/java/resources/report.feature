
Feature: Report Management API Integration Test
  @amsIntegration
  Scenario: Manage Reports Workflow With Valid Data
    Given  Have tenant as "paymentbb2"
    When I call the get list of reports API with expected status of 200
    Then the response should contain a list of reports

    When I call the create report API with valid data with expected status of 201
    Then the response should contain the created report details
    And the response should contain a unique report ID

    When I call the get list of reports API with expected status of 200
    Then the response should contain a list of reports

    Given I have a report ID
    When I call the update report API with valid data with expected status of 200
    Then the response should contain the updated report details

    Given I have a report ID
    When I call the get single report API with expected status of 200
    Then the response should contain the details of the requested report

  Scenario: Manage Reports Workflow With Invalid Data
    Given Have tenant as "paymentbb2"
    When I call the create report API with invalid data
    Then I should receive a response with status 400




