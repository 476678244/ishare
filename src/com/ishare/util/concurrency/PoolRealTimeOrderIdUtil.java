package com.ishare.util.concurrency;

public class PoolRealTimeOrderIdUtil {

	private static long idCursor = 0;
	
	public static synchronized long generateId() {
		idCursor ++;
		return idCursor;
	}
}
