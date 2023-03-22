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

	public static Integer generateHashCode(String apiMethod, String apiPath) {
		String key = apiMethod.toLowerCase() + "_" + apiPath.toLowerCase();
		System.out.println("Storage key = " + key);
		int hashCode = key.hashCode();
		return Integer.valueOf(hashCode);
	}

	public static String generateQueryParamString(Map<String, String[]> servletQueryParams) {
		Map<String, String> sortedHelperMap = new HashMap<>();
		servletQueryParams.forEach((key, value) -> {
			String finalValue = "";
			System.out.println("Query Parameter Key = " + key);
			for (int i = 0; i < value.length; i++) {
				System.out.println("Query Parameter value " + value[i]);
				finalValue = finalValue + value[i];
			}
			sortedHelperMap.put(key, finalValue);
		});
		System.out.println(sortedHelperMap.toString());
		List<String> sortedKeys = new ArrayList<>(sortedHelperMap.keySet());
		Collections.sort(sortedKeys);
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, sortedKeys.size()).forEach(i -> {
			String key = sortedKeys.get(i);
			sb.append(key).append("=").append(sortedHelperMap.get(key));
			if (i < sortedKeys.size() - 1) {
				sb.append("&");
			}
		});
		return sb.toString();
	}
}
