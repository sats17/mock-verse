package com.github.sats17.mockserver.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import com.github.sats17.mockserver.model.InputDTO;
import com.github.sats17.mockserver.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.sats17.mockserver.model.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.sats17.mockserver.model.mem.Storage;
import com.github.sats17.mockserver.utility.Utility;

@RestController
@RequestMapping("")
public class MockController {

    private static final Logger logger = LoggerFactory.getLogger(MockController.class);

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    StorageService storageService;

    public static Map<String, Storage> map = new HashMap<>();

    @PostMapping(path = "/api/mem/insert")
    public ResponseEntity<Object> insertDataToInMemoryStorage(@RequestParam String apiPath, @RequestParam String apiMethod,
                                                  @RequestParam Optional<String> apiQueryParams, @RequestParam Optional<String> apiHeaders,
                                                  @RequestBody Input body) throws IOException {
        logger.debug("Request received to insert mock behaviour in mem storage");
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
            logger.debug("Received mock behaviour with request body, mock verse will try to hash body request");
            String normalizedRequestBody = objectMapper.writeValueAsString(body.getRequest());
            logger.debug("Normalized Request JSON Body: {}", normalizedRequestBody);
            String hashedRequestBody;
            try {
                hashedRequestBody = Utility.generateHashCodeFromString(normalizedRequestBody);
            } catch (NoSuchAlgorithmException e) {
                logger.debug("Error occurred while generating hash from request body: {}", e.getMessage());
                return new ResponseEntity<Object>("Unable to generate hash from request body.", responseHeaders, 500);
            }
            hashKey = Utility.generateMockStorageKey(apiMethod, apiPath, structuredQueryParams, hashedRequestBody);
        } else {
            hashKey = Utility.generateMockStorageKey(apiMethod, apiPath, structuredQueryParams);
        }


        Storage storage = new Storage(apiPath, structuredQueryParams, contentType.toString(), body.getRequest(), body.getResponse());
        logger.debug("Hash Key: {}, Storage: {}", hashKey, storage);
        map.put(hashKey, storage);
        return new ResponseEntity<Object>("Mock added successfully", responseHeaders, 200);
    }

    @PostMapping("/api/disk/insert")
    public ResponseEntity<Object> insertDataToDisk(@RequestParam String apiPath, @RequestParam String apiMethod,
                                   @RequestParam Optional<String> apiQueryParams, @RequestParam Optional<String> apiHeaders,
                                   @RequestBody Input body) throws JsonProcessingException {
        if (!Utility.isValidPath(apiPath)) {
            return ResponseEntity.ok("Query parameter apiPath should starts with / as it represent API Path.");
        }
        if (!Utility.isValidAPIMethod(apiMethod)) {
            return ResponseEntity.ok("Query parameter apiMethod is not valid.");
        }

        InputDTO inputDTO = new InputDTO(apiPath, apiMethod, apiQueryParams.orElse(""), apiHeaders.orElse(""), body);
        boolean result = storageService.saveStorageToDB(inputDTO);

        if(result) {
            return ResponseEntity.ok("Mock created");
        }

        return ResponseEntity.ok("Mock creation failed");
    }

    @RequestMapping(value = "/**", produces = "application/json")
    public Object getMockResponse(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String structuredQueryParams = Utility.generateQueryParamString(request.getParameterMap());
        String requestMethod = request.getMethod();

        String hashedRequestBody = null;
        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if (!body.isEmpty()) {
                logger.debug("Request received with request body, mock verse will try to create hash from request body.");
                Object obj = objectMapper.readValue(body, Object.class);
                String normalizedRequestBody = objectMapper.writeValueAsString(obj);
                logger.debug("Normalized Request JSON Body: {}", normalizedRequestBody);

                try {
                    hashedRequestBody = Utility.generateHashCodeFromString(normalizedRequestBody);
                } catch (NoSuchAlgorithmException e) {
                    logger.debug("Error occurred while generating hash from request body: {}", e.getMessage());
                    return ResponseEntity.internalServerError();
                }
            }
        } catch (IOException e) {
            logger.debug("Error occurred while reading request body from servlet request: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        String key;
        if (hashedRequestBody != null) {
            key = Utility.generateMockStorageKey(requestMethod, requestPath, structuredQueryParams, hashedRequestBody);
        } else {
            key = Utility.generateMockStorageKey(requestMethod, requestPath, structuredQueryParams);
        }

        logger.debug("Hash Key: {}", key);


        Storage result = fetchDataFromMap(key);
        if (result != null) {
            logger.debug("Mock data found for key {} in HashMap", key);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-type", result.getContentType());
            return new ResponseEntity<Object>(result.getResponseBody(), responseHeaders, 200);
        } else {
            logger.debug("Data not found for key {} in Hashmap", key);
        }

        com.github.sats17.mockserver.model.h2.Storage storage = storageService.getStorageByKey(key);
        if (storage != null) {
            logger.debug("Mock data found for key {} in disk", key);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-type", storage.getContentType());
            return new ResponseEntity<Object>(storage.getResponseBody(), responseHeaders, 200);
        }
        return "Data for requestPath " + requestPath + " and requestMethod " + requestMethod
                + " not present anywhere inside mockserver";

    }

    private Storage fetchDataFromMap(String key) {
        logger.debug("Fetching key {} from hashmap", key);
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
