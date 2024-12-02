package com.github.sats17.mockserver.utility;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.github.sats17.mockserver.controller.MockController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class Utility {

	private static final Logger logger = LoggerFactory.getLogger(Utility.class);

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

	@Deprecated
	public static Integer generateHashCode(String... args) {
		// Skip any string with length 0 or null
		args = Arrays.stream(args).filter(s -> s != null && s.length() > 0).map(s -> s.toLowerCase())
				.toArray(String[]::new);

		if (args.length == 0) {
			System.out.println("Values passed to generate hash are either empty or null");
			throw new IllegalArgumentException("Internal server error.");
		}

		String key = String.join("_", args);
		System.out.println("Storage hash key = " + key);
		int hashCode = key.hashCode();
		return Integer.valueOf(hashCode);
	}

	public static String generateQueryParamString(Map<String, String[]> servletQueryParams) {
		if (servletQueryParams.isEmpty()) {
			logger.debug("Did not received any query params, hence will generate hash key with empty query params");
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
			return "";
		}

		// Split the input string by ";" to get individual key-value pairs
		String[] pairs = queryParams.split(";");

		// Create a new HashMap to store the key-value pairs
		HashMap<String, String> sortHelperMap = new HashMap<String, String>();

		// Iterate over the key-value pairs and split them by "="
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			String key = keyValue[0].toLowerCase();
			String value = "";
			if (keyValue.length == 2) {
				value = keyValue[1].toLowerCase().trim();
			}

			// Add the key-value pair to the HashMap
			sortHelperMap.put(key, value);
		}

		// Print the HashMap to verify the key-value pairs were added correctly
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
		});
		return queryParamBuilder.toString();

	}

	// Uses for insert API
	public static HashMap<String, String> generateAPIHeaders(String headers) {
		HashMap<String, String> headersMap = new HashMap<String, String>();
		if (headers.isEmpty()) {
			System.out.println("No Headers Param passed.");
			return headersMap;
		}

		String[] pairs = headers.split(";");

		// Create a new HashMap to store the key-value pairs

		// Iterate over the key-value pairs and split them by "="
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			String key = keyValue[0].toLowerCase();
			String value = "";
			if (keyValue.length == 2) {
				value = keyValue[1].toLowerCase().trim();
			}
			headersMap.put(key, value);
		}

		return headersMap;
	}

	public static Object mapStringBodyToDataType(Object body, MediaType contentType) throws IOException {
		System.out.println("Content Type to map response body "+contentType.toString());
		if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				Object object = mapper.readTree((String) body);
				return object;
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			return body;
		}
	}

	public static MediaType resolveContentType(String contentType) {
		if (contentType == null || contentType.trim().isEmpty()) {
			return MediaType.APPLICATION_JSON;
		}
		try {
			return MediaType.valueOf(contentType);
		} catch (InvalidMediaTypeException e) {
			return MediaType.APPLICATION_JSON;
		}
	}

	public static String generateMockStorageKey(String... args) {
		args = Arrays.stream(args).filter(s -> s != null && !s.isEmpty()).map(String::toLowerCase)
				.toArray(String[]::new);

		if (args.length == 0) {
			System.out.println("Values passed to generate hash are either empty or null");
			throw new IllegalArgumentException("Internal server error.");
		}

		return String.join("_", args);
	}

	public static String generateHashCodeFromString(String data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
		byte[] hashedBytes =  md.digest(bytes);
		StringBuilder hexString = new StringBuilder();
		for (byte b : hashedBytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

}
