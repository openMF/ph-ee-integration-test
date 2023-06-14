package org.mifos.integrationtest.common;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * { "payer": [ { "key": "MSISDN", "value": "254708374149" }, { "key": "ACCOUNTID", "value": "24450523" } ], "amount": {
 * "amount": "1", "currency": "USD" }, "transactionType": { "scenario": "MPESA", "subScenario": "BUYGOODS", "initiator":
 * "PAYEE", "initiatorType": "BUSINESS" } }
 */
public class CollectionHelper {

    public static JSONObject getCollectionRequestBody(String amount, String msisdn, String accountId) throws JSONException {
        List<Pair<String, String>> payers = new ArrayList<>();
        payers.add(new Pair<>("MSISDN", msisdn));
        payers.add(new Pair<>("ACCOUNTID", accountId));

        JSONObject body = new JSONObject();
        body.put("payer", getPayerArray(payers));
        body.put("amount", getAmountObject(amount));
        body.put("transactionType", getTransactionTypeObject());
        return body;
    }

    private static JSONArray getPayerArray(List<Pair<String, String>> payers) {
        JSONArray payerArray = new JSONArray();
        payers.forEach(payer -> {
            try {
                payerArray.put(getPayerObject(payer.getKey(), payer.getValue()));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        return payerArray;
    }

    private static JSONObject getPayerObject(String key, String value) throws JSONException {
        JSONObject payerObject = new JSONObject();
        payerObject.put("key", key);
        payerObject.put("value", value);
        return payerObject;
    }

    private static JSONObject getAmountObject(String amount) throws JSONException {
        JSONObject amountObject = new JSONObject();
        amountObject.put("currency", "USD");
        amountObject.put("amount", amount);
        return amountObject;
    }

    private static JSONObject getTransactionTypeObject() throws JSONException {
        JSONObject txnType = new JSONObject();
        txnType.put("scenario", "MPESA");
        txnType.put("subScenario", "BUYGOODS");
        txnType.put("initiator", "PAYEE");
        txnType.put("initiatorType", "BUSINESS");
        return txnType;
    }

}
