package com.github.sats17.mockserver.model;

public class InputDTO {

    private String apiPath;
    private String apiMethod;
    private String apiQueryParameters;
    private String apiHeaders;
    private Input body;

    public InputDTO(String apiPath, String apiMethod, String apiQueryParameters, String apiHeaders, Input body ) {
        this.apiPath = apiPath;
        this.apiMethod = apiMethod;
        this.apiQueryParameters = apiQueryParameters;
        this.body = body;
        this.apiHeaders = apiHeaders;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getApiQueryParameters() {
        return apiQueryParameters;
    }

    public void setApiQueryParameters(String apiQueryParameters) {
        this.apiQueryParameters = apiQueryParameters;
    }

    public String getApiHeaders() {
        return apiHeaders;
    }

    public void setApiHeaders(String apiHeaders) {
        this.apiHeaders = apiHeaders;
    }

    public Input getBody() {
        return body;
    }

    public void setBody(Input body) {
        this.body = body;
    }
}
