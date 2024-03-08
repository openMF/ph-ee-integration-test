@gov
Feature: Voucher Management Api Test

  @createAndActivateVoucher @redeemVoucherSuccess
  Scenario: Reactivate Voucher Api Test
    Given I can create an VoucherRequestDTO for voucher suspension
    And I can register the stub with "/suspendVoucher" endpoint for "PUT" request with status of 200
    When I call the suspend voucher API with expected status of 202 and stub "/suspendVoucher"
    And I can create an VoucherRequestDTO for voucher reactivation
    And I can register the stub with "/reactivateVoucher" endpoint for "PUT" request with status of 200
    When I call the activate voucher API with expected status of 202 and stub "/reactivateVoucher"
#    Then I will sleep for 2000 millisecond

  @createAndActivateVoucher
  Scenario: Validity Check Voucher Api Test
    When I can register the stub with "/validity" endpoint for "PUT" request with status of 200
    And I call the validity check API with expected status of 202 and stub "/validity"
#    And I will sleep for 3000 millisecond
    Then I can extract result from validation callback and assert if validation is successful on "/validity"

   Scenario: Create Voucher Api Test
     When I can inject MockServer
     Then I can start mock server
     And I can register the stub with "/createVoucher" endpoint for "PUT" request with status of 200
     Given I can create an VoucherRequestDTO for voucher creation
    When I call the create voucher API with expected status of 202 and stub "/createVoucher"
#     Then I will sleep for 10000 millisecond
     Then I should be able to extract response body from callback

  @createVoucher
  Scenario: Activate Voucher Api Test
    Given I can create an VoucherRequestDTO for voucher activation
    And I can register the stub with "/activateVoucher" endpoint for "PUT" request with status of 200
    When I call the activate voucher API with expected status of 202 and stub "/activateVoucher"
#    Then I will sleep for 5000 millisecond
    Then I should be able to assert response body from callback on "/activateVoucher"

  @createAndActivateVoucher
  Scenario: Redeem Voucher Api Test
    Given I can create an RedeemVoucherRequestDTO for voucher redemption
    When I call the redeem voucher API with expected status of 200
    Then I can assert that redemption was successful by asserting the status in response

  @createAndActivateVoucher @redeemVoucherFailure
  Scenario: VR-006 Cancel Voucher Api Test
    Given I can create an VoucherRequestDTO for voucher cancellation
    And I can register the stub with "/cancelVoucher" endpoint for "PUT" request with status of 200
    When I call the cancel voucher API with expected status of 202 and stub "/cancelVoucher"
#    Then I will sleep for 3000 millisecond

  @createAndActivateVoucher @redeemVoucherFailure
  Scenario: VR-002 Suspend Voucher Api Test
    Given I can create an VoucherRequestDTO for voucher suspension
    And I can register the stub with "/suspendVoucher" endpoint for "PUT" request with status of 200
    When I call the suspend voucher API with expected status of 202 and stub "/suspendVoucher"
#    Then I will sleep for 2000 millisecond


  @createAndActivateVoucher
  Scenario: Fetch Voucher Api Test
#    When I will sleep for 3000 millisecond
    Then I will call the fetch voucher API with expected status of 200
    And I will assert the fields from fetch voucher response
    And I can stop mock server

  Scenario: VC-001,002,003,004,005 Error Validity check for Create Voucher API for negative request body
    Given I can create an negative VoucherRequestDTO for voucher creation
    When I call the create voucher API with expected status of 400 and stub "/createVoucher"
    Then I should be able to assert the create voucher validation for negative response

  Scenario: VR-003 Error Validity check for Redeem Voucher API for negative request body
    Given I can create an negative RedeemVoucherRequestDTO to redeem a voucher
    When I call the redeem voucher API with expected status of 400
    And I should be able to assert the redeem voucher validation for negative response

  @createAndActivateVoucher @redeemVoucherFailure
  Scenario: VR-005 Redeem Voucher Api Negative Test when voucher is already redeemed
    Given I can create an RedeemVoucherRequestDTO for voucher redemption
    When I call the redeem voucher API with expected status of 200

  @Ignore
  Scenario: Create a csv file for voucher number and voucher serial number
    When I can inject MockServer
    Then I can start mock server
    And I can register the stub with "/createVoucher" endpoint for "PUT" request with status of 200
    And I can register the stub with "/activateVoucher" endpoint for "PUT" request with status of 200
    When I call the create, Activate voucher API and store it in "vouchertest/loadTest_demo.csv"
