package org.mifos.integrationtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWSKeyConfig {

    @Value("${json-web-signature.privateKey}")
    public String privateKey;

    @Value("${json-web-signature.x509Certificate}")
    public String x509Certificate;

}
