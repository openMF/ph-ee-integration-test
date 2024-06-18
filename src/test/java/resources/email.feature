Feature: Send Email

  @govtodo
  Scenario: Sending an email to the recipient with success
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/sendMail" endpoint for "POST" request with status of 200
    And the email service is running
    And I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    When I send an email to the following recipients with subject "Test Email" and body "This is a test email" with callbackurl as "/sendMail" and get 202
      | recipient1@example.com |
    And I can verify callback received with success
    Then the email should be sent to all recipients with subject "Test Email" and body "This is a test email"

  @gov
  Scenario: Sending an email to the recipient for validation issues
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/sendMail" endpoint for "POST" request with status of 200
    And the email service is running
    And I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    When I send an email to the following recipients with subject "" and body "This is a test email" with callbackurl as "/sendMail" and get 400
      | recipient1@example.com |
    Then I should be able to extract error from response

  @gov
  Scenario: Sending an email to the recipient with failure in callback
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/sendMail" endpoint for "POST" request with status of 200
    And the email service is running
    And I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    When I send an email to the following recipients with subject "Test Email" and body "This is a test email" with callbackurl as "/sendMail" and get 202
      | recipient1@example.com |
    And I can verify callback received with failure