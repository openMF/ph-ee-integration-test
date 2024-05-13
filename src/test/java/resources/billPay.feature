@billPay
Feature: Bill Payment P2G Test

  @gov
   #this is an integration for bill inquiry stage w/o rtp, includes bill inquiry api only from PFI to PBB to Bill Agg and back
  Scenario: BI-001 Bill Inquiry API for orchestration (PFI to PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billInquiry" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I have bill id as "001"
    When I call the get bills api with billid with expected status of 202 and callbackurl as "/billInquiry"
    Then I should get non empty response
    And I should get transactionId in response
#    And I will sleep for 5000 millisecond
    Then I should be able to extract response body from callback for bill pay

  @gov
        #this is an integration for payment notification, includes api calls from PFI to PBB to Bill Agg and back (tests full flow)
  Scenario: BP-001 Bill Payments API for orchestration (PFI to PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billNotification" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I have bill id as "001"
    And I generate clientCorrelationId
    And I can mock payment notification request
    When I call the payment notification api expected status of 202 and callbackurl as "/billNotification"
    Then I should get non empty response
    And I should get transactionId in response
#    And I will sleep for 5000 millisecond
    Then I should be able to extract response body from callback for bill notification


    #this is an integration for bill inquiry stage w/o rtp, includes bill inquiry api and payment notification from PFI to PBB to Bill Agg and back
  Scenario: Bill Inquiry API for orchestration (PFI to PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billInquiry" endpoint for "POST" request with status of 200
    And I can register the stub with "/billNotification" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I have bill id as "001"
    When I call the get bills api with billid with expected status of 202 and callbackurl as "/billInquiry"
    Then I should get non empty response
    And I should get transactionId in response
    Given I have tenant as "paymentBB2"
    And I have bill id as "001"
    And I generate clientCorrelationId
    And I can mock payment notification request
    When I call the payment notification api expected status of 202 and callbackurl as "/billNotification"
    Then I should get non empty response
    And I should get transactionId in response
#    And I will sleep for 5000 millisecond
    When I make the "POST" request to "/billNotification" endpoint with expected status of 200
    Then I should be able to extract response body from callback for bill pay
    When I make the "POST" request to "/billInquiry" endpoint with expected status of 200
    Then I should be able to extract response body from callback for bill pay

      #this is an component test for bill inquiry stage w/o rtp, includes bill inquiry api from PBB to Bill Agg with mock
      #response for bill inquiry api from Bill Agg to PBB to PFI
  Scenario: Bill Inquiry API for P2G (PBB to Biller/Agg)
    Given I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I have bill id as "001"
    When I call the mock get bills api from PBB to Biller with billid with expected status of 202
    Then I should get non empty response


   #this is an component test for bill inquiry stage w/o rtp, includes bill inquiry api from PBB to Bill Agg with mock
   #response for bill inquiry api from Bill Agg to PBB to PFI
  Scenario: Bill Payments API for P2G (PBB to Biller/Agg)
    Given I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I have bill id as "001"
    And I can mock payment notification request
    When I call the mock bills payment api from PBB to Biller with billid with expected status of 202
    Then I should get non empty response

  @gov
  Scenario: RTP-001 RTP Integration test
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/testRTP" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I have bill id as "1008"
    And I generate clientCorrelationId
    And I generate clientCorrelationId
    Then I can create DTO for Biller RTP Request
    And I can call the biller RTP request API with expected status of 202 and "/testRTP" endpoint
#    Then I will sleep for 8000 millisecond
    And I can extract the callback body and assert the rtpStatus

  @gov
  Scenario: BI-002 Bill Inquiry API for orchestration fails due to invalid prefix (PFI to PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billInquiry" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I have bill id as "101"
    When I call the get bills api with billid with expected status of 202 and callbackurl as "/billInquiryPrefixInvalid"
    Then I should get non empty response
    And I should get transactionId in response
#    And I will sleep for 5000 millisecond
    Then I should be able to extract response body from callback for biller unidentified

  @gov
  Scenario: BI-003A: Bill Inquiry API for orchestration fails due to invalid bill (PBB TO BA)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billInquiry" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I have bill id as "002"
    When I call the get bills api with billid with expected status of 202 and callbackurl as "/invalidbillInquiry"
    Then I should get non empty response
    And I should get transactionId in response
#    And I will sleep for 5000 millisecond
    Then I should be able to extract response body from callback for bill invalid

  @gov
  Scenario: BI-003B: Bill Inquiry API for orchestration fails due to payer fsp not onboarded (PFI TO PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billInquiryInvalid" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I have bill id as "003"
    When I call the get bills api with billid with expected status of 404 and callbackurl as "/billInquiryInvalid"
    Then I should get non empty response
    And I should get Payer FSP not found in response

  @gov
  Scenario: BI-004: Bill Inquiry API for orchestration fails due to empty bill (PBB TO BA)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billInquiryEmpty" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I have bill id as "004"
    When I call the get bills api with billid with expected status of 202 and callbackurl as "/billInquiryEmpty"
    Then I should get non empty response
    And I should get transactionId in response
#    And I will sleep for 5000 millisecond
    Then I should be able to extract response body from callback for empty bill id

  @gov
  Scenario: BP-003 Bill Payments API fails due to mandatory fields missing (PFI to PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billNotificationMissing" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I have bill id as "001"
    And I generate clientCorrelationId
    And I can mock payment notification request with missing values
    When I call the payment notification api expected status of 400 and callbackurl as "/billNotificationMissing"
    Then I should get non empty response
    And I should be able to assert negative response body

  @gov
  Scenario: BP-003A Bill Payments API fails due to mandatory fields missing -Bill Id(PFI to PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billNotificationMissing" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I can mock payment notification request with missing bill id
    When I call the payment notification api expected status of 400 and callbackurl as "/billNotificationMissing"
    Then I should get non empty response
    And I should be able to assert negative response body
    Then I will assert that response contains "error.msg.schema.bill.id.cannot.be.null.or.empty"

  @gov
  Scenario: BP-004A Bill Payments API fails due to bill already marked paid (PFI to PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billNotificationPaid" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I have bill id as "003"
    And I generate clientCorrelationId
    And I can mock payment notification request
    When I call the payment notification api expected status of 202 and callbackurl as "/billNotificationPaid"
    Then I should get non empty response
    And I should get transactionId in response
#    And I will sleep for 5000 millisecond
    Then I should be able to extract response body from callback for bill already paid
    Then I can stop mock server

  @gov
  Scenario: BP-004B Bill Payments API fails due to bill marked as paid after a timeout (PFI to PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billNotificationsTimeout" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I have bill id as "005"
    And I generate clientCorrelationId
    And I can mock payment notification request
    When I call the payment notification api expected status of 202 and callbackurl as "/billNotificationsTimeout"
    Then I should get non empty response
    And I should get transactionId in response
    And I should remove all server events
#    And I will sleep for 1000 millisecond
    Then I should not get a response from callback for bill
#    And I will sleep for 5000 millisecond
    Then I should be able to extract response body from callback for bill paid after timeout


  @gov
  Scenario: RTP-002 Request to Pay is unsuccessful because RtP type of Alias is not specified
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I have bill id as "1009"
    And I generate clientCorrelationId
    And I generate clientCorrelationId
    Then I can create DTO for Biller RTP Request without alias details
    And I can call the biller RTP request API with expected status of 400 and "/aliasSpecification" endpoint
    And I should be able to assert negative response body
    Then I will assert that response contains "error.msg.schema.payer.fsp.details.cannot.be.null.or.empty"

  @gov
  Scenario: RTP-003 Request to Pay is unsuccessful because RtP type does not match with the information provided
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I generate clientCorrelationId
    And I create a new clientCorrelationId
    And I have bill id as "1010"
    And I generate clientCorrelationId
    Then I can create DTO for Biller RTP Request with incorrect rtp information
    And I can call the biller RTP request API with expected status of 400 and "/informationMismatch" endpoint
    And I should be able to assert negative response body
    Then I will assert that response contains "error.msg.schema.alias.cannot.be.null.or.empty"

  @gov
  Scenario: RTP-004 Request to Pay is unsuccessful because of invalid RTP type (alias or bank account)
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I generate clientCorrelationId
    And I create a new clientCorrelationId
    And I have bill id as "1011"
    And I generate clientCorrelationId
    Then I can create DTO for Biller RTP Request with incorrect rtp type
    And I can call the biller RTP request API with expected status of 400 and "/invalidRtp" endpoint
    And I should be able to assert negative response body
    Then I will assert that response contains "error.msg.schema.request.type.is.invalid"

  @gov
  Scenario: RTP-005 Request to Pay is unsuccessful because of  Alias type and Alias information mismatch
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I generate clientCorrelationId
    And I create a new clientCorrelationId
    And I have bill id as "1012"
    And I generate clientCorrelationId
    Then I can create DTO for Biller RTP Request with incorrect alias details
    And I can call the biller RTP request API with expected status of 400 and "/aliasMismatch" endpoint
    And I should be able to assert negative response body
    Then I will assert that response contains "error.msg.schema.request.type.is.invalid"

  @gov
  Scenario: RTP-006 Request to Pay is unsuccessful because of invalid/incorrect BIC
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I generate clientCorrelationId
    And I create a new clientCorrelationId
    And I have bill id as "1013"
    And I generate clientCorrelationId
    Then I can create DTO for Biller RTP Request without alias details
    And I can call the biller RTP request API with expected status of 400 and "/invalidBic" endpoint
    And I should be able to assert negative response body
    Then I will assert that response contains "error.msg.schema.payer.fsp.details.cannot.be.null.or.empty"

  @gov
  Scenario: RTP-008 Request to Pay is unsuccessful because the specified account of the Payer FI was unreachable - did not respond (Txn timed out)

    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/payerUnreachable" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I generate clientCorrelationId
    And I create a new clientCorrelationId
    And I have bill id as "1014"
    And I generate clientCorrelationId
    Then I can create DTO for Biller RTP Request to mock payer fi unreachable
    And I can call the biller RTP request API with expected status of 202 and "/payerUnreachable" endpoint
#    Then I will sleep for 3000 millisecond
    And I can extract the error from callback body and assert error message as "Payer FI was unreachable"

  @gov
  Scenario: RTP-009 Request to Pay is unsuccessful because the Payer FSP is unable to debit amount (insufficient amount/ blocked account/ account hold etc)
  (Payer FSP declines)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/debitFailed" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I generate clientCorrelationId
    And I create a new clientCorrelationId
    And I have bill id as "1015"
    Then I can create DTO for Biller RTP Request to mock payer fsp failed to debit amount
    And I can call the biller RTP request API with expected status of 202 and "/debitFailed" endpoint
#    Then I will sleep for 3000 millisecond
    And I can extract the error from callback body and assert error message as "Payer FSP is unable to debit amount"

  @gov
  Scenario: BS-001 Bill RTP Status API to get COMPLETED
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/test1" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I generate clientCorrelationId
    And I have bill id as "001"
    Then I can create DTO for Biller RTP Request
    And I can call the biller RTP request API with expected status of 202 and "/test1" endpoint
    And I can extract the callback body and assert the rtpStatus
    Given I can create a request for status api
    And I can call the biller RTP status API with expected status of 200 until I get the rtpStatus as "COM"

  @gov
  Scenario: BS-002 Bill RTP Status API to get PENDING
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/debitFailed" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I generate clientCorrelationId
    And I have bill id as "011"
    Then I can create DTO for Biller RTP Request to mock payer fsp failed to debit amount
    And I can call the biller RTP request API with expected status of 202 and "/debitFailed" endpoint
    Given I can create a request for status api
    And I can call the biller RTP status API with expected status of 200 until I get the rtpStatus as "PND"

  @gov
  Scenario: BS-003 Bill RTP Status API to get req accepted
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/test1" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I generate clientCorrelationId
    And I have bill id as "123"
    Then I can create DTO for Biller RTP Request
    And I can call the biller RTP request API with expected status of 202 and "/test1" endpoint
    Given I can create a request for status api
    And I can call the biller RTP status API with expected status of 200 until I get the rtpStatus as "REQUEST_ACCEPTED"

  @gov
  Scenario: BS-004 Bill RTP Status API to get accepted
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/test1" endpoint for "POST" request with status of 200
    Given I have tenant as "paymentBB2"
    And I have a billerId as "GovBill"
    And I generate clientCorrelationId
    And I have bill id as "1234"
    Then I can create DTO for Biller RTP Request
    And I can call the biller RTP request API with expected status of 202 and "/test1" endpoint
    Given I can create a request for status api
    And I can call the biller RTP status API with expected status of 200 until I get the rtpStatus as "ACCEPTED"

  @gov
  Scenario: Unsupported header validation for Bill Payments API
    Given I have tenant as "paymentBB2"
    And I generate clientCorrelationId
    And I have bill id as "001"
    And I can mock payment notification request
    When I call the mock bills payment api with invalid header from PBB to Biller with billid with expected status of 400
    Then I should get non empty response
    Then I will assert that response contains "error.msg.header.validation.errors"
