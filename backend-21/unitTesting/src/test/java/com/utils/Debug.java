package com.utils;

import org.apache.log4j.Logger;

public class Debug {
	
	public static void info(String message){
		Logger DebugLogger = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		DebugLogger.info(message);
	}

	public static void warn(String message) {
		Logger DebugLogger = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		DebugLogger.warn(message);
	}

	public static void error(String message) {
		Logger DebugLogger = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		DebugLogger.error(message);

	}

	public static void debug(String message) {
		Logger DebugLogger = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		DebugLogger.debug(message);
	}

}
