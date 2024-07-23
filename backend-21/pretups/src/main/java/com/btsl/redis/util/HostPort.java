package com.btsl.redis.util;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class HostPort {

 private static String defaultHost = "localhost";

 private static Integer defaultPort = 6379;

 private static String password = null;
 
 private static Integer connectionTimeout = 20000;
 
 private static Integer operationTimeout = 20000;

 private static Integer maxConnection = 200;
 

public static String getPassword() {
	return password;
}

public static Integer getConnectionTimeout() {
	return connectionTimeout;
}

public static Integer getOperationTimeout() {
	return operationTimeout;
}

public static String getRedisHost() {
    return defaultHost;
}

public static Integer getRedisPort() {
	return defaultPort;
}


public static Integer getMaxConnection() {
	return maxConnection;
}

public static void loadRedisServerDetailsAtStartup(){
	if(!BTSLUtil.isNullString(Constants.getProperty("REDISPORT"))){
		defaultPort = Integer.parseInt(Constants.getProperty("REDISPORT"));
	}
	
	if(!BTSLUtil.isNullString(Constants.getProperty("REDISHOST"))){
		defaultHost = Constants.getProperty("REDISHOST");
	}
	
	if(!BTSLUtil.isNullString(Constants.getProperty("CONNECTIONTIMEOUT"))){
		connectionTimeout = Integer.parseInt(Constants.getProperty("CONNECTIONTIMEOUT"));
	}
	
	if(!BTSLUtil.isNullString(Constants.getProperty("OPERATIONTIMEOUT"))){
		operationTimeout = Integer.parseInt(Constants.getProperty("OPERATIONTIMEOUT"));
	}
	
	if(!BTSLUtil.isNullString(Constants.getProperty("REDISMAXCONNECTION"))){
		maxConnection = Integer.parseInt(Constants.getProperty("REDISMAXCONNECTION"));
	}
	if(!BTSLUtil.isNullString(Constants.getProperty("REDISPASSWORD"))){
		password = Constants.getProperty("REDISPASSWORD").trim();
	}
}
}