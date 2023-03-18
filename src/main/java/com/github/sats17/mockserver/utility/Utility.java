package com.github.sats17.mockserver.utility;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

public class Utility {

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

}
