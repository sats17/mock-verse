package com.github.sats17.mockserver.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.sats17.mockserver.model.Input;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
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

    @Autowired
    ObjectMapper objectMapper;

    public static Map<String, Storage> map = new HashMap<>();


    public final static String dbName = "mockdb";
    public final static String collectionName = "mockcollection";


    @PostMapping(path = "/api/map/insert")
    public ResponseEntity<Object> insertDataToMap(@RequestParam String apiPath, @RequestParam String apiMethod,
                                                  @RequestParam Optional<String> apiQueryParams, @RequestParam Optional<String> apiHeaders,

                                                  @RequestBody Input body) throws IOException {
        if (!Utility.isValidPath(apiPath)) {
            return ResponseEntity.ok("Query parameter apiPath should starts with / as it represent API Path.");
        }
        if (!Utility.isValidAPIMethod(apiMethod)) {
            return ResponseEntity.ok("Query parameter apiMethod is not valid.");
        }
        String structuredQueryParams = Utility.generateQueryParamString(apiQueryParams.orElse(""));
        HashMap<String, String> headers = Utility.generateAPIHeaders(apiHeaders.orElse(""));
        MediaType contentType = Utility.resolveContentType(headers.get("content-type"));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-type", contentType.toString());

        String hashKey;
        if (body.getRequest() != null) {
            objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
            String normalizedRequestBody = objectMapper.writeValueAsString(body.getRequest());
            System.out.println(normalizedRequestBody);
			String hashedRequestBody;
            try {

                hashedRequestBody = Utility.generateHashCodeFromString(normalizedRequestBody);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return new ResponseEntity<Object>("Unable to generate hash from request body.", responseHeaders, 500);
            }

            hashKey = Utility.generateMockStorageKey(apiMethod, apiPath, structuredQueryParams, hashedRequestBody);
        } else {
            hashKey = Utility.generateMockStorageKey(apiMethod, apiPath, structuredQueryParams);
        }


        Storage storage = new Storage(apiPath, structuredQueryParams, contentType.toString(), body.getResponse());
        System.out.println("Storage -> " + storage);
        map.put(hashKey, storage);
		System.out.println(map);


        return new ResponseEntity<Object>("Mock added successfully", responseHeaders, 200);
    }

    @PostMapping("/api/file/insert")
    public Object insertDataToFile(@RequestParam String apiPath, @RequestParam String apiMethod,
                                   @RequestParam Optional<String> apiQueryParams, @RequestParam Optional<String> apiHeaders, @RequestBody String body) {
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

        Storage storage = new Storage(apiPath, structuredQueryParams, contentType.toString(), body);
        String storageJson;
        try {
            storageJson = objectMapper.writeValueAsString(storage);
            System.out.println("Storage -> " + storageJson);
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

        String hashedRequestBody = null;
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if(body != null && !body.isEmpty()) {
                objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
                Object obj = objectMapper.readValue(body, Object.class);
                String normalizedRequestBody = objectMapper.writeValueAsString(obj);
                System.out.println(normalizedRequestBody);

                try {

                    hashedRequestBody = Utility.generateHashCodeFromString(normalizedRequestBody);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

		String key;

		if(hashedRequestBody != null) {
            key = Utility.generateMockStorageKey(requestMethod, requestPath, structuredQueryParams, hashedRequestBody);
        } else {
            key = Utility.generateMockStorageKey(requestMethod, requestPath, structuredQueryParams);
        }


        Storage result = fetchDataFromMap(key);
        if (result != null) {
            System.out.println(result);
            System.out.println("Data returned from Map");
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-type", result.getContentType());
            System.out.println(result.getBody());
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

    private Storage fetchDataFromMap(String key) {
        System.out.println(map);
        return map.get(key);
    }

    private Storage fetchDataFromFile(String key) {
        try {
            InputStream inputStream = new FileSystemResource("mock-responses/" + key + ".json")
                    .getInputStream();

            Storage output = objectMapper.readValue(inputStream, Storage.class);
            return output;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


// Not using this method, as we are not sorting array while normalizing json. If needed then use this json in future.
//	public static JsonNode normalizeJson(JsonNode node) {
//		// If it's an object, sort keys and normalize nested objects or arrays
//		if (node.isObject()) {
//			ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
//			Iterator<String> fieldNames = node.fieldNames();
//			while (fieldNames.hasNext()) {
//				String fieldName = fieldNames.next();
//				JsonNode fieldValue = node.get(fieldName);
//				objectNode.set(fieldName, normalizeJson(fieldValue));  // Recursively normalize
//			}
//			return objectNode;
//		}
//
//		// If it's an array, sort the elements and normalize nested objects or arrays inside
//		if (node.isArray()) {
//			ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
//			node.forEach(element -> {
//				arrayNode.add(normalizeJson(element));  // Recursively normalize each element
//			});
//
//			// Sorting the array elements based on natural order (lexicographical or numerical)
//			arrayNode.sort((n1, n2) -> {
//				// Compare values (integers, strings, etc.) using natural ordering
//				if (n1.isNumber() && n2.isNumber()) {
//					return Long.compare(n1.asLong(), n2.asLong());
//				} else if (n1.isTextual() && n2.isTextual()) {
//					return n1.asText().compareTo(n2.asText());
//				}
//				// If types differ or non-comparable types, treat them as equal for sorting (or you can handle exceptions)
//				return 0;
//			});
//			return arrayNode;
//		}
//
//		// If it's not an object or array (i.e., it's a simple value like String, Number, etc.), return the node as-is
//		return node;
//	}

}
