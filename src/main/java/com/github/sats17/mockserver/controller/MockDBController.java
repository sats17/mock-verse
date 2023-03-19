package com.github.sats17.mockserver.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.sats17.mockserver.configuration.MongoConfiguration;
import com.github.sats17.mockserver.utility.Utility;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

@RestController
@RequestMapping("")
public class MockDBController {

	@Autowired
	public MongoConfiguration mongoConfig;
	
	public static Map<Integer, Object> map = new HashMap<>();

	public final static String dbName = "mockdb";
	public final static String collectionName = "mockcollection";


	@PostMapping("/api/db/insert")
	public Object insertDataToDB(@RequestParam String apiPath, @RequestParam String apiMethod, @RequestBody String body) {
		if (!Utility.isValidPath(apiPath)) {
			return ResponseEntity.ok("Query parameter apiPath should starts with / as it represent API Path.");
		}
		if (!Utility.isValidAPIMethod(apiMethod)) {
			return ResponseEntity.ok("Query parameter apiMethod is not valid.");
		}
		
		Integer hashCode = Utility.generateHashCode(apiMethod, apiPath);
		
		MongoClient mongoClient = mongoConfig.mongoClient();
		MongoDatabase database = mongoClient.getDatabase(dbName);
		MongoCollection<Document> collection = database.getCollection(collectionName);
		
		Document bodyDocument = Document.parse(body);
		
		Document query = new Document("_id", hashCode);
		Document document = new Document("_id", hashCode)
		                        .append("data", bodyDocument);
		
		ReplaceOptions options = new ReplaceOptions().upsert(true);

		// Execute the replace operation
		collection.replaceOne(query, document, options);
		
		
		FindIterable<Document> resp = collection.find();
		return resp.first().get("data");
	}
	
	@PostMapping("/api/map/insert")
	public Object insertDataToMap(@RequestParam String apiPath, @RequestParam String apiMethod, @RequestBody String body) {
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
	public Object insertDataToFile(@RequestParam String apiPath, @RequestParam String apiMethod, @RequestBody String body) {
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

	@RequestMapping(value = "/**", produces = "application/json")
	public Object getURLValue(HttpServletRequest request) {
		String requestPath = request.getRequestURI();
		String requestMethod = request.getMethod();
		System.out.println("Path is " + requestPath);
		System.out.println("Method is "+ requestMethod);
		
		Integer key = Utility.generateHashCode(requestMethod, requestPath);
		
		Object result = fetchDataFromDB(key);
		if(result != null) {
			System.out.println(((Document) result).get("data"));
			return ((Document) result).get("data");
		}
		
		result = fetchDataFromMap(key);
		if (result != null) {
			System.out.println(result);
			return result;
		}
		return "Data not found";

	}
	
	private Object fetchDataFromDB(Integer key) {
		MongoClient mongoClient = mongoConfig.mongoClient();
		MongoDatabase database = mongoClient.getDatabase(dbName);
		MongoCollection<Document> collection = database.getCollection(collectionName);

		Document query = new Document("_id", key);
		Document result = collection.find(query).first();
		return result;	
	}
	
	private Object fetchDataFromMap(Integer key) {
		return map.get(key);
	}

}
