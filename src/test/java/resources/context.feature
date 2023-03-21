Feature: Testing if context is loaded or not

  Scenario: Checking if application context is starter or not
    Given I can autowire the object mapper bean
    Then Application context should be not null
