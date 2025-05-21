package org.mifos.integrationtest.common;

import org.json.JSONException;
import org.json.JSONObject;

public final class TransferHelper {

    private TransferHelper() {}

    public static JSONObject getTransferRequestBody() throws JSONException {

        JSONObject body = new JSONObject();
        body.put("payer", getPartyObject("0487959491")); //greenbank 
        body.put("payee", getPartyObject("0437113124")); //bluebank
        body.put("amount", getAmountObject("11"));
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
