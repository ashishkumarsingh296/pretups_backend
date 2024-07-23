package com.btsl.util;

/*
 * Constants.java
 * 
 * 
 * 
 * Name Date History
 * ------------------------------------------------------------------------
 * Sanjay 07/07/2003 Initial Creation
 * 
 * ------------------------------------------------------------------------
 * Copyright (c) 2003 Bharti Telesoft Ltd.
 */


import com.btsl.pretups.gateway.util.RestAPIStringParser;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

public class Constants implements java.io.Serializable {

	public static final String API_SUCCESS_RESPONSE_CODE = "200";
	public static final String API_SUCCESS_RESPONSE_DESC = "Success";

	public static final String API_BAD_REQ_RESPONSE_CODE = "400";
	public static final String API_BAD_REQ_RESPONSE_DESC = "Bad Request";

	public static final String API_UNAUTH_RESPONSE_CODE = "401";
	public static final String API_UNAUTH_RESPONSE_DESC = "Unauthorized";

	public static final String API_NOT_FOUND_RESPONSE_CODE = "404";
	public static final String API_NOT_FOUND_RESPONSE_DESC = "Not found";

	public static final String API_SECURITY_RESPONSE_CODE = "403";
	public static final String API_SECURITY_RESPONSE_CODE_DESC = "Forbidden";

	public static final String API_MULTI_REQUEST_RESPONSE_CODE = "429";
	public static final String API_MULTI_REQUEST_RESPONSE_CODE_DESC = "Too many requests";

	public static final String API_NA_RESPONSE_CODE = "406";
	public static final String API_NA_RESPONSE_CODE_DESC = "Not applicable";
	public static final String API_DEFAULT_RESPONSE_CODE = "default";
	public static final String API_DEFAULT_RESPONSE_CODE_DESC = "unexpected error";

	public static final String API_INTERNAL_ERROR_RESPONSE_CODE = "500";
	public static final String API_INTERNAL_ERROR_RESPONSE_DESC = "Internal Server Error";


	public static final String KEY = "981AFA8CDEB2A0F7E0A011B557BB08CF";
    public static final String A_KEY = "u/Gu5posvwDsXUnV5Zaq4g==";

	public static final String LANGUAGE = "en";

	public  static final String KEY2 = (Constants.LANGUAGE.equalsIgnoreCase("en")) ? "Val1" : "Val2";
    private static  Properties properties = new Properties(); 
    private static  Properties kafkaProperties = new Properties(); 


	public static final String  getDescription(String tagName){

		Locale locale = new Locale("sp","PE");

		return RestAPIStringParser.getMessage(locale, "15019", null);

	}
    public static void load(String fileName) throws IOException, URISyntaxException {
    	File file = new File(fileName);
    	String canonicalizedString=file.getCanonicalPath();
    	//Handling of Symbolic Linked Based Constants.props Added by Diwakar
    	String parentDirectoryFile = file.getParent();
			boolean isSymbolicLink = Files.isSymbolicLink(file.toPath());
    	if(isSymbolicLink){
    		Path path = Files.readSymbolicLink(file.toPath());    		
    		file = path.toAbsolutePath().toFile();
    		if(!file.exists()){
    			file = new File(parentDirectoryFile+File.separator+path.getFileName().toString());
    		}
    	}
    	if(!isSymbolicLink && !canonicalizedString.equals(fileName))
    	{
//    		throw new URISyntaxException(canonicalizedString, fileName);
    	}
        try(final FileInputStream fileInputStream = new FileInputStream(file);)
        {
        	properties.load(fileInputStream);
	        fileInputStream.close();
        }
    }
    public static String getProperty(String propertyName) {
        return SqlParameterEncoder.encodeParams(properties.getProperty(propertyName));
    }
    
    
    public static void loadKafkaConf(String fileName) throws IOException, URISyntaxException {
    	File file = new File(fileName);
    	String canonicalizedString=file.getCanonicalPath();
    	//Handling of Symbolic Linked Based Constants.props Added by Diwakar
    	String parentDirectoryFile = file.getParent();
    	boolean isSymbolicLink = Files.isSymbolicLink(file.toPath());
    	if(isSymbolicLink){
    		Path path = Files.readSymbolicLink(file.toPath());    		
    		file = path.toAbsolutePath().toFile();
    		if(!file.exists()){
    			file = new File(parentDirectoryFile+File.separator+path.getFileName().toString());
    		}
    	}
    	if(!isSymbolicLink && !canonicalizedString.equals(fileName))
    	{
    		throw new URISyntaxException(canonicalizedString, fileName);
    	}
        try(final FileInputStream fileInputStream = new FileInputStream(file);)
        {
        	kafkaProperties.load(fileInputStream);
        fileInputStream.close();
        }
    }

    public static String getKafkaProperty(String propertyName) {
        return SqlParameterEncoder.encodeParams(kafkaProperties.getProperty(propertyName));
    }
	
	    /**
     * To validate the FileName exist
     * @param fileName Input value for file
     * @return File  file object will be return
     * @throws IOException IOException in case of found
     * @throws URISyntaxException URISyntaxException in case of found
     */
    public static File validateFilePath(String fileName) throws IOException, URISyntaxException {
    	File file = new File(fileName);
    	String canonicalizedString=file.getCanonicalPath();
    	//Handling of Symbolic Linked Based Constants.props Added by Diwakar
    	String parentDirectoryFile = file.getParent();
    	boolean isSymbolicLink = Files.isSymbolicLink(file.toPath());
    	if(isSymbolicLink){
    		Path path = Files.readSymbolicLink(file.toPath());    		
    		file = path.toAbsolutePath().toFile();
    		if(!file.exists()){
    			file = new File(parentDirectoryFile+File.separator+path.getFileName().toString());
    		}
    	}
    	if(!isSymbolicLink && !canonicalizedString.equals(fileName))
    	{
    		throw new URISyntaxException(canonicalizedString, fileName);
    	}
        return file;
    }

}

