package org.mifos.integrationtest.common.dto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OperationsHelper {

    public static JSONObject getBulkFilterRequestBodyForExternalId(List<String> externalIds) throws JSONException {
        JSONObject body = new JSONObject();
        body.put("externalId", getJSONArray(externalIds));
        return body;
    }

    public static JSONObject getBulkFilterRequestBodyForExternalId(String externalId) throws JSONException {
        List<String> externalIds = new ArrayList<>();
        externalIds.add(externalId);
        return getBulkFilterRequestBodyForExternalId(externalIds);
    }

    private static <T> JSONArray getJSONArray(List<T> externalIds) {
        JSONArray array = new JSONArray();
        externalIds.forEach(array::put);
        return array;
    }

}
