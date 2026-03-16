package com.fplabs.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KarzaResponse {

    private Result result;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("status-code")
    private String statusCode;



    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
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