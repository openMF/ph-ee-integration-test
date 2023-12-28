
Feature: Bill Payment P2G Test

  @gov
   #this is an integration for bill inquiry stage w/o rtp, includes bill inquiry api only from PFI to PBB to Bill Agg and back
  Scenario: BI-001 Bill Inquiry API for orchestration (PFI to PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billInquiry" endpoint for "POST" request with status of 200
    Given I have tenant as "gorilla"
    And I create a new clientCorrelationId
    And I have bill id as "001"
    When I call the get bills api with billid with expected status of 202 and callbackurl as "/billInquiry"
    Then I should get non empty response
    And I should get transactionId in response
    And I will sleep for 5000 millisecond
    When I make the "POST" request to "/billInquiry" endpoint with expected status of 200
    Then I should be able to extract response body from callback for bill pay
    And I can stop mock server

  @gov
        #this is an integration for payment notification, includes api calls from PFI to PBB to Bill Agg and back (tests full flow)
  Scenario: BP-001 Bill Payments API for orchestration (PFI to PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billNotification" endpoint for "POST" request with status of 200
    Given I have tenant as "gorilla"
    And I have bill id as "001"
    And I generate clientCorrelationId
    And I can mock payment notification request
    When I call the payment notification api expected status of 202 and callbackurl as "/billNotification"
    Then I should get non empty response
    And I should get transactionId in response
    When I make the "POST" request to "/billNotification" endpoint with expected status of 200
    And I will sleep for 5000 millisecond
    Then I should be able to extract response body from callback for bill pay
    And I can stop mock server






    #this is an integration for bill inquiry stage w/o rtp, includes bill inquiry api and payment notification from PFI to PBB to Bill Agg and back
  Scenario: Bill Inquiry API for orchestration (PFI to PBB)
    Given I can inject MockServer
    And I can start mock server
    And I can register the stub with "/billInquiry" endpoint for "POST" request with status of 200
    And I can register the stub with "/billNotification" endpoint for "POST" request with status of 200
    Given I have tenant as "gorilla"
    And I create a new clientCorrelationId
    And I have bill id as "001"
    When I call the get bills api with billid with expected status of 202 and callbackurl as "/billInquiry"
    Then I should get non empty response
    And I should get transactionId in response
    Given I have tenant as "gorilla"
    And I have bill id as "001"
    And I generate clientCorrelationId
    And I can mock payment notification request
    When I call the payment notification api expected status of 202 and callbackurl as "/billNotification"
    Then I should get non empty response
    And I should get transactionId in response
    And I will sleep for 5000 millisecond
    When I make the "POST" request to "/billNotification" endpoint with expected status of 200
    Then I should be able to extract response body from callback for bill pay
    When I make the "POST" request to "/billInquiry" endpoint with expected status of 200
    Then I should be able to extract response body from callback for bill pay
    And I can stop mock server

      #this is an component test for bill inquiry stage w/o rtp, includes bill inquiry api from PBB to Bill Agg with mock
      #response for bill inquiry api from Bill Agg to PBB to PFI
  Scenario: Bill Inquiry API for P2G (PBB to Biller/Agg)
    Given I have tenant as "gorilla"
    And I create a new clientCorrelationId
    And I have bill id as "001"
    When I call the mock get bills api from PBB to Biller with billid with expected status of 200
    Then I should get non empty response


   #this is an component test for bill inquiry stage w/o rtp, includes bill inquiry api from PBB to Bill Agg with mock
   #response for bill inquiry api from Bill Agg to PBB to PFI
  Scenario: Bill Payments API for P2G (PBB to Biller/Agg)
    Given I have tenant as "gorilla"
    And I create a new clientCorrelationId
    And I have bill id as "001"
    And I can mock payment notification request
    When I call the mock bills payment api from PBB to Biller with billid with expected status of 200
    Then I should get non empty response
