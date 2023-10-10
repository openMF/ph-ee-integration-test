package org.mifos.integrationtest.common;

import org.json.JSONException;
import org.json.JSONObject;

public class TransferHelper {

    public static JSONObject getTransferRequestBody() throws JSONException {

        JSONObject body = new JSONObject();
        body.put("payer", getPartyObject("27710101999"));
        body.put("payee", getPartyObject("27710102999"));
        body.put("amount", getAmountObject("1"));
        return body;
    }

    public static JSONObject getTransferRequestBody(String payerIdentifier) throws JSONException {

        JSONObject body = new JSONObject();
        body.put("payer", getPartyObject(payerIdentifier));
        body.put("payee", getPartyObject(payerIdentifier));
        body.put("amount", getAmountObject("1"));
        return body;
    }

    public static JSONObject getTransferRequestBody(String payerIdentifier, String payeeIdentifier, String amount) throws JSONException {

        JSONObject body = new JSONObject();
        body.put("payer", getPartyObject(payerIdentifier));
        body.put("payee", getPartyObject(payeeIdentifier));
        body.put("amount", getAmountObject(amount));
        return body;
    }

    private static JSONObject getPartyObject(String value) throws JSONException {

        JSONObject partyIdObject = new JSONObject();
        partyIdObject.put("partyIdType", "MSISDN");
        partyIdObject.put("partyIdentifier", value);

        JSONObject partyObject = new JSONObject();
        partyObject.put("partyIdInfo", partyIdObject);
        return partyObject;
    }

    private static JSONObject getAmountObject(String amount) throws JSONException {
        JSONObject amountObject = new JSONObject();
        amountObject.put("currency", "USD");
        amountObject.put("amount", amount);
        return amountObject;
    }
}
