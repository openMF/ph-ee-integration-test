Feature: Zeebe Update Test

  Scenario: Test zeebe-export kafka topic
    When I have tenant as "gorilla"
    And I can start a test workflow n times and verify the output
    Then I listen on zeebe-export topic
    And The number of workflows started should be equal to number of message consumed on kafka topic