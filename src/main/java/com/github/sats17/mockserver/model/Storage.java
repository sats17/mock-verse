package com.github.sats17.mockserver.model;

public class Storage {

	private String apiPath;
	private String queryParameters;
	private String contentType;
	private Object body;

	public Storage() {
		super();
	}

	public Storage(String apiPath, String queryParameters, String contentType, Object body) {
		super();
		this.apiPath = apiPath;
		this.queryParameters = queryParameters;
		this.body = body;
		this.contentType = contentType;
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

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public String toString() {
		return "Storage [apiPath=" + apiPath + ", queryParameters=" + queryParameters + ", contentType=" + contentType
				+ ", body=" + body + "]";
	}

}
