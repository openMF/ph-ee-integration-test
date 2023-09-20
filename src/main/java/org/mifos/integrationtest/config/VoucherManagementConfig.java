package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VoucherManagementConfig {
    @Value("${voucher-management.contactpoint}")
    public String voucherManagementContactPoint;

    @Value("${voucher-management.endpoints.create-voucher}")
    public String createVoucherEndpoint;
    @Value("${voucher-management.endpoints.voucher-lifecycle}")
    public String voucherLifecycleEndpoint;
    @Value("${voucher-management.endpoints.voucher-validity}")
    public String voucherValidityEndpoint;
    @Value("${voucher-management.endpoints.fetch}")
    public String fetchVoucherEndpoint;
}
