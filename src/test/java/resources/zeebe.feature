@ZeebeExport
Feature: Zeebe exporter test

  Scenario: Test zeebe-export kafka topic
    Given I have tenant as "gorilla"
    When I upload the BPMN file to zeebe
    And I can start test workflow n times with message "Hello World"
    Then I listen on kafka topic
    And The number of workflows started should be equal to number of message consumed on kafka topic