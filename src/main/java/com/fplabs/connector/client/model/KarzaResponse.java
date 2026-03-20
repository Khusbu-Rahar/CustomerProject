package com.fplabs.connector.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KarzaResponse {

    private KarzaResult result;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("status-code")
    private String statusCode;



    public KarzaResult getResult() {
        return result;
    }

    public void setResult(KarzaResult result) {
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }


}