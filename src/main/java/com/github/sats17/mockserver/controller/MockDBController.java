package com.github.sats17.mockserver.controller;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.sats17.mockserver.configuration.MongoConfiguration;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import com.github.sats17.mockserver.utility.Utility;

@RestController
@RequestMapping("")
public class MockDBController {

	@Autowired
	public MongoConfiguration mongoConfig;

	public final static String dbName = "mockdb";
	public final static String collectionName = "mockcollection";

	/**
	 * Create id like method+path
	 * and data in db should be 
	 * { "id" : id, "data": {bla bla}}
	 */
	@PostMapping("/api/insert")
	public ResponseEntity<String> insertData(@RequestParam String key, @RequestBody String body) {
		Document document = Document.parse(body);
		if (!Utility.isValidPath(key)) {
			return ResponseEntity.ok("Query parameter key should starts with / as it represent API Path.");
		}
		document.put("_id", key);
		MongoClient mongoClient = mongoConfig.mongoClient();
		MongoDatabase database = mongoClient.getDatabase(dbName);
		MongoCollection<Document> collection = database.getCollection(collectionName);
		collection.insertOne(document);
		FindIterable<Document> resp = collection.find();
		resp.forEach(doc -> {
			System.out.println(doc);
		});
		return ResponseEntity.ok("Inserted successfully!");
	}

	@RequestMapping(value = "/**", produces = "application/json")
	public Object getURLValue(HttpServletRequest request) {
		String requestPath = request.getRequestURI();
		MongoClient mongoClient = mongoConfig.mongoClient();
		MongoDatabase database = mongoClient.getDatabase(dbName);
		MongoCollection<Document> collection = database.getCollection(collectionName);
		System.out.println("Path is " + requestPath);
//        ObjectId id = new ObjectId(requestPath);
		Document query = new Document("_id", requestPath);
		Document result = collection.find(query).first();
		System.out.println(result);
		return requestPath;
	}

}
