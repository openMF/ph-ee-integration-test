package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdentityMapperConfig {
    @Value("${identity-account-mapper.contactpoint}")
    public String identityMapperContactPoint;

    @Value("${identity-account-mapper.endpoints.register-beneficiary}")
    public String registerBeneficiaryEndpoint;
    @Value("${identity-account-mapper.endpoints.add-payment-modality}")
    public String addPaymentModalityEndpoint;
    @Value("${identity-account-mapper.endpoints.update-payment-modality}")
    public String updatePaymentModalityEndpoint;
    @Value("${identity-account-mapper.endpoints.account-lookup}")
    public String accountLookupEndpoint;
}
