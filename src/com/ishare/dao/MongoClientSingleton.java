package com.ishare.dao;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;

public class MongoClientSingleton {

	private MongoClientSingleton() {

	}

	private static MongoClient mongoClient = null;

	public static MongoClient getMongoClient() throws UnknownHostException {
		if (mongoClient == null) {
			mongoClient = new MongoClient("localhost", 27017);
		}
		return mongoClient;
	}
}
