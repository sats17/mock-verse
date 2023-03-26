package com.github.sats17.mockserver.model;

import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

public class Storage {

	private String apiPath;
	private String queryParameters;
	private Object body;
	private MediaType contentType;

	public Storage(String apiPath, String queryParameters, MediaType contentType, Object body) {
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

	public MediaType getContentType() {
		return contentType;
	}

	public void setContentType(MediaType contentType) {
		this.contentType = contentType;
	}



}
