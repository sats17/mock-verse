package com.github.sats17.mockserver.model;

import java.util.Map;

public class Storage {

	private String apiPath;
	private String queryParameters;
	private Object body;
	
	public String getApiPath() {
		return apiPath;
	}
	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}
	public Map<String, String> getQueryParameters() {
		return queryParameters;
	}
	public void setQueryParameters(Map<String, String> queryParameters) {
		this.queryParameters = queryParameters;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	
}
