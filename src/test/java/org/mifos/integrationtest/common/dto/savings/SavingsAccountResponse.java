package org.mifos.integrationtest.common.dto.savings;

public class SavingsAccountResponse {
    public String officeId;
    public String clientId;
    public String savingsId;
    public String resourceId;
    public String gsimId;

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSavingsId() {
        return savingsId;
    }

    public void setSavingsId(String savingsId) {
        this.savingsId = savingsId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getGsimId() {
        return gsimId;
    }

    public void setGsimId(String gsimId) {
        this.gsimId = gsimId;
    }
}
