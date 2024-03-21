Feature: Paybill API Tests

  Scenario: PB-001 Paybill Config Test
    Given The mpesaValidateUrl is not null
    Given The mpesaSettlementUrl is not null

  Scenario: PB-002 MPESA Connector Validate Webhook API Test for paygops
    Given I have businessShortCode "24322607" with transactionId "8335b60090979AvUefSR"
    And I have MSISDN "254797668592" and BillRefNo "24322607" for amount "11"
    When I call the mpesa-connector validate webhook api with expected status code of 200
    Then I call the confirmation webhook API with expected status of 200

  Scenario: PB-003 MPESA Connector Validate Webhook API Test for roster
    Given I have businessShortCode "12345678" with transactionId "670d65bd-4efd-4a6c-ae2c-7fdaa8cb4d60"
    And I have MSISDN "2540726839144" and BillRefNo "33272035" for amount "11"
    When I call the mpesa-connector validate webhook api with expected status code of 200
    Then I call the confirmation webhook API with expected status of 200
