package com.github.sats17.mockserver.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

public class Utility {
	
	private static List<String> apiMethods = Arrays.asList("GET", "POST", "PUT", "PATCH", "OPTIONS", 
			"DELETE", "HEAD");

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

}
