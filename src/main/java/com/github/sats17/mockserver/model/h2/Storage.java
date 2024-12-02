package com.github.sats17.mockserver.model.h2;

import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;

@Entity
@Table(name = "storage")
public class Storage {

    @Id
    @Column(name = "mock_key", nullable = false, unique = true)
    private String mockKey;

    @Column(name = "query_parameters", nullable = true)
    private String queryParameters;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "api_path", nullable = false)
    private String apiPath;

    @Lob
    @Column(name = "request_body", nullable = true)
    private String requestBody;

    @Lob
    @Column(name = "response_body", nullable = false)
    private String responseBody;

    public String getMockKey() {
        return mockKey;
    }

    public void setMockKey(String mockKey) {
        this.mockKey = mockKey;
    }

    public String getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(String queryParameters) {
        this.queryParameters = queryParameters;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Storage(String mockKey, String queryParameters, String contentType, String apiPath, String requestBody, String responseBody) {
        this.mockKey = mockKey;
        this.queryParameters = queryParameters;
        this.contentType = contentType;
        this.apiPath = apiPath;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
    }

    public Storage() {
    }

    @Override
    public String toString() {
        return "Storage{" +
                "mockKey='" + mockKey + '\'' +
                ", queryParameters='" + queryParameters + '\'' +
                ", contentType='" + contentType + '\'' +
                ", apiPath='" + apiPath + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}
