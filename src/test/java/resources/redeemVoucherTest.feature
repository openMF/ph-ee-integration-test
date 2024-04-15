@voucher
Feature: Redeem Voucher Api Test

  @gov @createAndActivateVoucher
  Scenario: Redeem Voucher Api Test
    Given I can create an RedeemVoucherRequestDTO for voucher redemption
    When I call the redeem voucher API with expected status of 200
    Then I can assert that redemption was successful by asserting the status in response