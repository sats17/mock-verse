//package com.github.sats17.mockserver.configuration;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
//
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//
//@Configuration
//public class MongoConfiguration extends AbstractMongoClientConfiguration {
//
//   @Value("${spring.data.mongodb.host}")
//   private String host;
//
//   @Value("${spring.data.mongodb.port}")
//   private int port;
//
//   @Override
//   protected String getDatabaseName() {
//      return "testdb";
//   }
//
//   @Override
//   public MongoClient mongoClient() {
//      return MongoClients.create("mongodb://" + host + ":" + port);
//   }
//
//   @Override
//   protected boolean autoIndexCreation() {
//      return true;
//   }
//}
//
