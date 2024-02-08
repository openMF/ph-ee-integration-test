package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("HideUtilityClassConstructor")
public class TenantConfig {

    @Value("${PaymentBB2}")
    private static String PaymentBB2;

    public static String getPaymentBB2() {
        return PaymentBB2;
    }
}
