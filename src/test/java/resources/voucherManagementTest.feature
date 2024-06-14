
Feature: Voucher Management Api Test

  @gov
  Scenario: VCR-001 Create Voucher Api Test
    When I can inject MockServer
    Then I can start mock server
    And I can register the stub with "/createVoucher" endpoint for "PUT" request with status of 200
    Given I can create an VoucherRequestDTO for voucher creation
    When I call the create voucher API with expected status of 202 and stub "/createVoucher"
    Then I should be able to extract response body from callback

  @gov @createVoucher
  Scenario: VA-001 Activate Voucher Api Test
    Given I can create a voucher
    Given I can create an VoucherRequestDTO for voucher activation
    And I can register the stub with "/activateVoucher" endpoint for "PUT" request with status of 200
    When I call the activate voucher API with expected status of 202 and stub "/activateVoucher"
    Then I should be able to assert response body from callback on "/activateVoucher"

  @gov @createAndActivateVoucher
  Scenario: VR-001 Redeem Voucher API Test
    Then I check for redeem voucher success

  @gov @createAndActivateVoucher
  Scenario: VRE-001 Reactivate Voucher Api Test
    Given I can create an VoucherRequestDTO for voucher suspension
    And I can register the stub with "/suspendVoucher" endpoint for "PUT" request with status of 200
    When I call the suspend voucher API with expected status of 202 and stub "/suspendVoucher"
    And I can create an VoucherRequestDTO for voucher reactivation
    And I can register the stub with "/reactivateVoucher" endpoint for "PUT" request with status of 200
    When I call the activate voucher API with expected status of 202 and stub "/reactivateVoucher"
    Then I check for redeem voucher success

  @gov @createAndActivateVoucher
  Scenario: VVA-001 Validity Check Voucher Api Test
    When I can register the stub with "/validity" endpoint for "PUT" request with status of 200
    And I call the validity check API with expected status of 202 and stub "/validity"
    Then I can extract result from validation callback and assert if validation is successful on "/validity"

  @gov @createAndActivateVoucher
  Scenario: VR-006 Cancel Voucher Api Test
    Given I can create an VoucherRequestDTO for voucher cancellation
    And I can register the stub with "/cancelVoucher" endpoint for "PUT" request with status of 200
    When I call the cancel voucher API with expected status of 202 and stub "/cancelVoucher"
    Then I check for redeem voucher failure

  @gov @createAndActivateVoucher
  Scenario: VR-002 Suspend Voucher Api Test
    Given I can create an VoucherRequestDTO for voucher suspension
    And I can register the stub with "/suspendVoucher" endpoint for "PUT" request with status of 200
    When I call the suspend voucher API with expected status of 202 and stub "/suspendVoucher"
    Then I check for redeem voucher failure

  @gov @createAndActivateVoucher
  Scenario: VF-001 Fetch Voucher Api Test
    Then I will call the fetch voucher API with expected status of 200
    And I will assert the fields from fetch voucher response
    And I can stop mock server

  @gov
  Scenario: VC-001,002,003,004,005 Error Validity check for Create Voucher API for negative request body
    Given I can create an negative VoucherRequestDTO for voucher creation
    When I call the create voucher API with expected status of 400 and stub "/createVoucher"
    Then I should be able to assert the create voucher validation for negative response

  @gov
  Scenario: VR-003 Error Validity check for Redeem Voucher API for negative request body
    Given I can create an negative RedeemVoucherRequestDTO to redeem a voucher
    Then I will add the required headers
    When I call the redeem voucher API with expected status of 400
    And I should be able to assert the redeem voucher validation for negative response

  @gov @createAndActivateVoucher
  Scenario: VR-005 Redeem Voucher Api Negative Test when voucher is already redeemed
    Given I can create an RedeemVoucherRequestDTO for voucher redemption
    When I call the redeem voucher API with expected status of 200
    Then I check for redeem voucher failure

 @gov
  Scenario: VCR-002 Unsupported Parameter Validation for Create Voucher API test
    Given I can create an VoucherRequestDTO for voucher creation with unsupported parameter parameter
    When I call the create voucher API with expected status of 400 and stub "/createVoucher"
    Then I should be able to assert the create voucher validation for negative response

  @gov @createAndActivateVoucher
   Scenario: VCR-003 Conflicting/unique data validations check for Create Voucher API
    Then I will call the fetch voucher API with expected status of 200
    When I call the create voucher API with expected status of 409 and stub "/createVoucher"

  @gov
  Scenario: VCR-004 Unsupported header validation for Create Voucher Api Test
    Given I can create an VoucherRequestDTO for voucher creation
    When I call the create voucher API having invalid header with expected status of 400 and stub "/createVoucher"
    Then I should get non empty response
    Then I will assert that response body contains "error.msg.header.validation.errors"

  @Ignore
  Scenario: Create a csv file for voucher number and voucher serial number
    When I can inject MockServer
    Then I can start mock server
    And I can register the stub with "/createVoucher" endpoint for "PUT" request with status of 200
    And I can register the stub with "/activateVoucher" endpoint for "PUT" request with status of 200
    When I call the create, Activate voucher API and store it in "vouchertest/loadTest_demo.csv"

    @gov
    Scenario: VS-001 Voucher Status Check for inactive voucher, active voucher and redeemed voucher
      When I can inject MockServer
      Then I can start mock server
      And I can register the stub with "/createVoucher" endpoint for "PUT" request with status of 200
      Given I can create an VoucherRequestDTO for voucher creation
      When I call the create voucher API with expected status of 202 and stub "/createVoucher"
#     Then I will sleep for 10000 millisecond
      Then I should be able to extract response body from callback
      And I can call the voucher status API with expected status of 200 until I get the status as "01"
      Given I can create an VoucherRequestDTO for voucher activation
      And I can register the stub with "/activateVoucher" endpoint for "PUT" request with status of 200
      When I call the activate voucher API with expected status of 202 and stub "/activateVoucher"
#    Then I will sleep for 5000 millisecond
      Then I should be able to assert response body from callback on "/activateVoucher"
      And I can call the voucher status API with expected status of 200 until I get the status as "02"
      Given I can create an RedeemVoucherRequestDTO for voucher redemption
      When I call the redeem voucher API with expected status of 200
      Then I can assert that redemption was successful by asserting the status in response
      And I can call the voucher status API with expected status of 200 until I get the status as "05"



  @createAndActivateVoucher @gov
  Scenario: VS-002 Voucher Status Check for inactive voucher, active voucher, suspended and cancelled voucher
    Given I can create an VoucherRequestDTO for voucher suspension
    And I can register the stub with "/suspendVoucher" endpoint for "PUT" request with status of 200
    When I call the suspend voucher API with expected status of 202 and stub "/suspendVoucher"
    And I can call the voucher status API with expected status of 200 until I get the status as "06"
    And I can create an VoucherRequestDTO for voucher reactivation
    And I can register the stub with "/reactivateVoucher" endpoint for "PUT" request with status of 200
    When I call the activate voucher API with expected status of 202 and stub "/reactivateVoucher"
    And I can call the voucher status API with expected status of 200 until I get the status as "02"
    Given I can create an VoucherRequestDTO for voucher cancellation
    And I can register the stub with "/cancelVoucher" endpoint for "PUT" request with status of 200
    When I call the cancel voucher API with expected status of 202 and stub "/cancelVoucher"
    And I can call the voucher status API with expected status of 200 until I get the status as "03"

