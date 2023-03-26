package com.github.sats17.mockserver.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.mockserver.model.Storage;
import com.github.sats17.mockserver.utility.Utility;

@RestController
@RequestMapping("")
public class MockController {

	@Autowired
	ResourceLoader resourceLoader;

	public static Map<Integer, Storage> map = new HashMap<>();

	public static ObjectMapper objectMapper = new ObjectMapper();

	public final static String dbName = "mockdb";
	public final static String collectionName = "mockcollection";


	@PostMapping(path="/api/map/insert")
	public ResponseEntity<Object> insertDataToMap(@RequestParam String apiPath, @RequestParam String apiMethod,
			@RequestParam Optional<String> apiQueryParams, @RequestParam Optional<String> apiHeaders, @RequestBody String body, HttpServletRequest request) throws IOException {
		if (!Utility.isValidPath(apiPath)) {
			return ResponseEntity.ok("Query parameter apiPath should starts with / as it represent API Path.");
		}
		if (!Utility.isValidAPIMethod(apiMethod)) {
			return ResponseEntity.ok("Query parameter apiMethod is not valid.");
		}
		String structuredQueryParams = Utility.generateQueryParamString(apiQueryParams.orElse(""));	
		HashMap<String, String> headers = Utility.generateAPIHeaders(apiHeaders.orElse(""));
		MediaType contentType = Utility.resolveContentType(headers.get("content-type"));
		
		Integer hashCode = Utility.generateHashCode(apiMethod, apiPath, structuredQueryParams);
		Storage storage = new Storage(apiPath, structuredQueryParams, contentType.toString() ,body);
		System.out.println("Storage -> "+storage.toString());
		map.put(hashCode, storage);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("content-type", contentType.toString());
		
		return new ResponseEntity<Object>(map.get(hashCode).getBody(), responseHeaders, 200);
	}

	@PostMapping("/api/file/insert")
	public Object insertDataToFile(@RequestParam String apiPath, @RequestParam String apiMethod,
			@RequestParam Optional<String> apiQueryParams,  @RequestParam Optional<String> apiHeaders, @RequestBody String body) {
		if (!Utility.isValidPath(apiPath)) {
			return ResponseEntity.ok("Query parameter apiPath should starts with / as it represent API Path.");
		}
		if (!Utility.isValidAPIMethod(apiMethod)) {
			return ResponseEntity.ok("Query parameter apiMethod is not valid.");
		}
		String structuredQueryParams = Utility.generateQueryParamString(apiQueryParams.orElse(""));
		HashMap<String, String> headers = Utility.generateAPIHeaders(apiHeaders.orElse(""));
		MediaType contentType = Utility.resolveContentType(headers.get("content-type"));
		
		Integer hashCode = Utility.generateHashCode(apiMethod, apiPath, structuredQueryParams);
		
		Storage storage = new Storage(apiPath, structuredQueryParams, contentType.toString() ,body);
		String storageJson;
		try {
			storageJson = objectMapper.writeValueAsString(storage);
			System.out.println("Storage -> "+storageJson);
		} catch (JsonProcessingException e1) {
			System.out.println("Error occured while converting Storage Class to JSON String.\n");
			e1.printStackTrace();
			return "Something went wrong, please check logs";
		}
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			File directory = new File("mock-responses");
			if (!directory.exists()) {
				directory.mkdir();
			}
			objectMapper.writeValue(new File("mock-responses/" + hashCode.toString() + ".json"), storage);
		} catch (IOException e) {
			e.printStackTrace();
			return "Something went wrong while writing response to file";
		}
		return "Inserted data to file";
	}

	@RequestMapping(value = "/**", produces = "application/json")
	public Object getURLValue(HttpServletRequest request) {
		String requestPath = request.getRequestURI();
		System.out.println("Request Path " + requestPath);

		String structuredQueryParams = Utility.generateQueryParamString(request.getParameterMap());
		String requestMethod = request.getMethod();
		System.out.println("Path is " + requestPath);
		System.out.println("Method is " + requestMethod);

		Integer key = Utility.generateHashCode(requestMethod, requestPath, structuredQueryParams);

		Storage result = fetchDataFromMap(key);
		if (result != null) {
			System.out.println(result);
			System.out.println("Data returned from Map");
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("content-type", result.getContentType());
			return new ResponseEntity<Object>(result.getBody(), responseHeaders, 200);
		}

		result = fetchDataFromFile(key);
		if (result != null) {
			System.out.println(result);
			System.out.println("Data returned from File");
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("content-type", result.getContentType());
			return new ResponseEntity<Object>(result.getBody(), responseHeaders, 200);
		}
		System.out.println("Data for key " + key + " not present anywhere inside mockserver");
		return "Data for requestPath " + requestPath + " and requestMethod " + requestMethod
				+ " not present anywhere inside mockserver";

	}

	private Storage fetchDataFromMap(Integer key) {
		return map.get(key);
	}

	private Storage fetchDataFromFile(Integer key) {
		try {
			InputStream inputStream = new FileSystemResource("mock-responses/" + key.toString() + ".json")
					.getInputStream();

			Storage output = objectMapper.readValue(inputStream, Storage.class);
			return output;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
