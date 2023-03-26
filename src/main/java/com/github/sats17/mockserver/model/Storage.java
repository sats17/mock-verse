package com.github.sats17.mockserver.model;

public class Storage {

	private String apiPath;
	private String queryParameters;
	private Object body;

	
	public Storage(String apiPath, String queryParameters, Object body) {
		super();
		this.apiPath = apiPath;
		this.queryParameters = queryParameters;
		this.body = body;
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
	
}
