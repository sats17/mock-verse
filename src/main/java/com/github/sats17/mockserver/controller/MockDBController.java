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
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.InsertOneResult;
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
	public Object insertData(@RequestParam String apiPath, @RequestParam String apiMethod, @RequestBody String body) {
		if (!Utility.isValidPath(apiPath)) {
			return ResponseEntity.ok("Query parameter apiPath should starts with / as it represent API Path.");
		}
		if (!Utility.isValidAPIMethod(apiMethod)) {
			return ResponseEntity.ok("Query parameter apiMethod is not valid.");
		}
		MongoClient mongoClient = mongoConfig.mongoClient();
		MongoDatabase database = mongoClient.getDatabase(dbName);
		MongoCollection<Document> collection = database.getCollection(collectionName);
		
		Document bodyDocument = Document.parse(body);
		
//		Document datadocument =new Document("data", bodyDocument);
//		datadocument.put("_id", apiPath);
//		InsertOneResult result = collection.insertOne(datadocument);
		
		Document query = new Document("_id", apiPath);
		Document document = new Document("_id", apiPath)
		                        .append("data", bodyDocument);
		
		ReplaceOptions options = new ReplaceOptions().upsert(true);

		// Execute the replace operation
		collection.replaceOne(query, document, options);
		
		
		FindIterable<Document> resp = collection.find();
		Object response;
		resp.forEach(doc -> {
			System.out.println(doc);
			System.out.println(doc.get("data"));
			//response = doc.get("data");
		});
		return resp.first().get("data");
		//return ResponseEntity.ok("Inserted successfully!");
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
