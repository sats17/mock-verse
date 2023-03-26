package com.github.sats17.mockserver.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

public class Utility {

	private static List<String> apiMethods = Arrays.asList("GET", "POST", "PUT", "PATCH", "OPTIONS", "DELETE", "HEAD");

	public static String readJsonFile(String path) throws IOException {
		ClassPathResource resource = new ClassPathResource(path);
		return new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
	}

	public static boolean isValidPath(String path) {
		if (path.startsWith("/")) {
			return true;
		}
		return false;
	}

	public static boolean isValidAPIMethod(String method) {
		if (apiMethods.contains(method.toUpperCase())) {
			return true;
		}
		return false;
	}

	public static Integer generateHashCode(String... args) {
		// Skip any string with length 0 or null
	    args = Arrays.stream(args)
	            .filter(s -> s != null && s.length() > 0)
	            .map(s -> s.toLowerCase())
	            .toArray(String[]::new);
	    
	    if (args.length == 0) {
	    	System.out.println("Values passed to generate hash are either empty or null");
	        throw new IllegalArgumentException("Internal server error.");
	    }
	    
		String key = String.join("_", args);
		System.out.println("Storage key = " + key);
		int hashCode = key.hashCode();
		return Integer.valueOf(hashCode);
	}

	public static String generateQueryParamString(Map<String, String[]> servletQueryParams) {
		if(servletQueryParams.isEmpty()) {
			System.out.println("No query params passed, hence structured query params will be empty.");
			return "";
		}
		Map<String, String> sortHelperMap = new HashMap<>();

		// Sort query parameters
		servletQueryParams.forEach((key, value) -> {
			StringBuilder queryValueBuilder = new StringBuilder();
			for (int i = 0; i < value.length; i++) {
				queryValueBuilder.append(value[i]);
			}
			sortHelperMap.put(key.toLowerCase(), queryValueBuilder.toString().toLowerCase().trim());
		});
		List<String> sortedKeys = new ArrayList<>(sortHelperMap.keySet());
		Collections.sort(sortedKeys);

		// Generate query parameters string to support storage format
		StringBuilder queryParamBuilder = new StringBuilder();
		IntStream.range(0, sortedKeys.size()).forEach(i -> {
			String key = sortedKeys.get(i);
			queryParamBuilder.append(key).append("=").append(sortHelperMap.get(key).toLowerCase());
			if (i < sortedKeys.size() - 1) {
				queryParamBuilder.append("&");
			}
			System.out.println(queryParamBuilder.toString());
		});
		System.out.println("Key generated to store query params => " + queryParamBuilder.toString());
		return queryParamBuilder.toString();
	}

	// Uses for insert API
	public static String generateQueryParamString(String queryParams) {
		if (queryParams.startsWith("?")) {
			queryParams = queryParams.substring(1);
		}
		
		if (queryParams.isEmpty()) {
			System.out.println("No query params passed, hence structured query params will be empty.");
			return "";
		}

		// Split the input string by "&" to get individual key-value pairs
		String[] pairs = queryParams.split(",");

		// Create a new HashMap to store the key-value pairs
		HashMap<String, String> sortHelperMap = new HashMap<String, String>();

		// Iterate over the key-value pairs and split them by "="
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			String key = keyValue[0].toLowerCase();
			String value = "";
			if(keyValue.length == 2) {
				value = keyValue[1].toLowerCase().trim();
			}

			// Add the key-value pair to the HashMap
			sortHelperMap.put(key, value);
		}

		// Print the HashMap to verify the key-value pairs were added correctly
		System.out.println(sortHelperMap);
		List<String> sortedKeys = new ArrayList<>(sortHelperMap.keySet());
		Collections.sort(sortedKeys);

		// Generate query parameters string to support storage format
		StringBuilder queryParamBuilder = new StringBuilder();
		IntStream.range(0, sortedKeys.size()).forEach(i -> {
			String key = sortedKeys.get(i);
			queryParamBuilder.append(key).append("=").append(sortHelperMap.get(key).toLowerCase());
			if (i < sortedKeys.size() - 1) {
				queryParamBuilder.append("&");
			}
			System.out.println(queryParamBuilder.toString());
		});
		System.out.println("Key generated to store query params => " + queryParamBuilder.toString());
		return queryParamBuilder.toString();

	}
}
