Feature: Collection Payment Scheme Test

  Scenario Outline: Collection request with payment scheme
    Given I have tenant as "gorilla"
    When I call the collection endpoint with payment scheme "<ps>" and status is 200
    Then I should get non empty response
    And I should be able to parse transactionId

    Examples:
      | ps    |
      | MPESA |
      |       |
