package com.github.sats17.mockserver.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.mockserver.utility.Utility;

@RestController
@RequestMapping("")
public class MockController {

	@Autowired
	ResourceLoader resourceLoader;

//	@Autowired
//	public MongoConfiguration mongoConfig;

	public static Map<Integer, Object> map = new HashMap<>();

	public static ObjectMapper objectMapper = new ObjectMapper();

	public final static String dbName = "mockdb";
	public final static String collectionName = "mockcollection";

	// DB Storage is disabled
//	@PostMapping("/api/db/insert")
//	public Object insertDataToDB(@RequestParam String apiPath, @RequestParam String apiMethod,
//			@RequestBody String body) {
//		if (!Utility.isValidPath(apiPath)) {
//			return ResponseEntity.ok("Query parameter apiPath should starts with / as it represent API Path.");
//		}
//		if (!Utility.isValidAPIMethod(apiMethod)) {
//			return ResponseEntity.ok("Query parameter apiMethod is not valid.");
//		}
//
//		Integer hashCode = Utility.generateHashCode(apiMethod, apiPath);
//
//		MongoClient mongoClient = mongoConfig.mongoClient();
//		MongoDatabase database = mongoClient.getDatabase(dbName);
//		MongoCollection<Document> collection = database.getCollection(collectionName);
//
//		Document bodyDocument = Document.parse(body);
//
//		Document query = new Document("_id", hashCode);
//		Document document = new Document("_id", hashCode).append("data", bodyDocument);
//
//		ReplaceOptions options = new ReplaceOptions().upsert(true);
//
//		// Execute the replace operation
//		collection.replaceOne(query, document, options);
//
//		FindIterable<Document> resp = collection.find();
//		return resp.first().get("data");
//	}

	@PostMapping("/api/map/insert")
	public Object insertDataToMap(@RequestParam String apiPath, @RequestParam String apiMethod,
			@RequestBody String body) {
		if (!Utility.isValidPath(apiPath)) {
			return ResponseEntity.ok("Query parameter apiPath should starts with / as it represent API Path.");
		}
		if (!Utility.isValidAPIMethod(apiMethod)) {
			return ResponseEntity.ok("Query parameter apiMethod is not valid.");
		}
		Integer hashCode = Utility.generateHashCode(apiMethod, apiPath);
		map.put(hashCode, body);
		return map.get(hashCode);
	}

	@PostMapping("/api/file/insert")
	public Object insertDataToFile(@RequestParam String apiPath, @RequestParam String apiMethod,
			@RequestBody String body) {
		if (!Utility.isValidPath(apiPath)) {
			return ResponseEntity.ok("Query parameter apiPath should starts with / as it represent API Path.");
		}
		if (!Utility.isValidAPIMethod(apiMethod)) {
			return ResponseEntity.ok("Query parameter apiMethod is not valid.");
		}
		Integer hashCode = Utility.generateHashCode(apiMethod, apiPath);
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			File directory = new File("mock-responses");
		    if (!directory.exists()) {
		        directory.mkdir();
		    }
			objectMapper.writeValue(new File("mock-responses/" + hashCode.toString() + ".json"), body);
		} catch (IOException e) {
			e.printStackTrace();
			return "Something went wrong while writing response to file";
		}
		return "Inserted data to file";
	}

	@RequestMapping(value = "/**", produces = "application/json")
	public Object getURLValue(HttpServletRequest request) {
		String requestPath = request.getRequestURI();
		String requestMethod = request.getMethod();
		System.out.println("Path is " + requestPath);
		System.out.println("Method is " + requestMethod);

		Integer key = Utility.generateHashCode(requestMethod, requestPath);

//		Object result = fetchDataFromDB(key);
//		if (result != null) {
//			System.out.println(((Document) result).get("data"));
//			System.out.println("Data returned from DB");
//			return ((Document) result).get("data");
//		}

		Object result = fetchDataFromMap(key);
		if (result != null) {
			System.out.println(result);
			System.out.println("Data returned from Map");
			return result;
		}

		result = fetchDataFromFile(key);
		if (result != null) {
			System.out.println(result);
			System.out.println("Data returned from FIle");
			return result;
		}
		System.out.println("Data for key " + key + " not present anywhere inside mockserver");
		return "Data for requestPath " + requestPath + " and requestMethod " + requestMethod + " not present anywhere inside mockserver";

	}

//	private Object fetchDataFromDB(Integer key) {
//		MongoClient mongoClient = mongoConfig.mongoClient();
//		MongoDatabase database = mongoClient.getDatabase(dbName);
//		MongoCollection<Document> collection = database.getCollection(collectionName);
//
//		Document query = new Document("_id", key);
//		Document result = collection.find(query).first();
//		return result;
//	}

	private Object fetchDataFromMap(Integer key) {
		return map.get(key);
	}

	private Object fetchDataFromFile(Integer key) {
		try {
			InputStream inputStream = new FileSystemResource("mock-responses/" + key.toString() + ".json")
					.getInputStream();

			String output = objectMapper.readValue(inputStream, String.class);
			return output;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
