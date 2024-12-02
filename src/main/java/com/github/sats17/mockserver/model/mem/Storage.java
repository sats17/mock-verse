package com.github.sats17.mockserver.model.mem;

public class Storage {

	private String apiPath;
	private String queryParameters;
	private String contentType;
	private Object requestBody;
	private Object responseBody;

	public Storage() {
		super();
	}

	public Storage(String apiPath, String queryParameters, String contentType, Object requestBody, Object responseBody) {
		super();
		this.apiPath = apiPath;
		this.queryParameters = queryParameters;
		this.requestBody = requestBody;
		this.contentType = contentType;
		this.responseBody = responseBody;
	}

	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
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

	public Object getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(Object requestBody) {
		this.requestBody = requestBody;
	}

	public Object getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(Object responseBody) {
		this.responseBody = responseBody;
	}

	@Override
	public String toString() {
		return "Storage{" +
				"apiPath='" + apiPath + '\'' +
				", queryParameters='" + queryParameters + '\'' +
				", contentType='" + contentType + '\'' +
				", requestBody=" + requestBody +
				", responseBody=" + responseBody +
				'}';
	}
}
