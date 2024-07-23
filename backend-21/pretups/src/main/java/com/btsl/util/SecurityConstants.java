package com.btsl.util;

/*
 * SecurityConstants.java
 * 
 * 
 * 
 * Name Date History
 * ------------------------------------------------------------------------
 * Shashi 30/08/2015 Initial Creation
 * 
 * ------------------------------------------------------------------------
 * Copyright (c) 2003 Bharti Telesoft Ltd.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SecurityConstants implements java.io.Serializable {
    
	private static final long serialVersionUID = 1L;
	public static final Properties properties = new Properties(); // to keep the value

    // of properties

    public static void load(String fileName) throws IOException {
        final File file = new File(fileName);
        try(final FileInputStream fileInputStream = new FileInputStream(file);)
        {
        properties.load(fileInputStream);
        fileInputStream.close();
        }
    }// end of load

    /*
    writing another fucntion to accept input stream as parameter
     */
    public static void load(InputStream in) throws IOException {
        try{
            properties.load(in);
            in.close();
        }finally {
            if(in!=null){
                in.close();
            }
        }
    }

    public static String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }// end of getProperty
}// end of SecurityConstants

