package com.btsl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



public class ClientBLConstants implements java.io.Serializable
{
	
	private static final long serialVersionUID = 1L;
	private static Properties properties = new Properties(); 
    public static void load(String fileName) throws IOException
    {
    	File file = new File(fileName);
    	try(FileInputStream fileInputStream = new FileInputStream(file);)
    	{
    		properties.load(fileInputStream);
    		fileInputStream.close();
    	}
    }

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
	public static String getProperty(String propertyName)
	{
		return properties.getProperty(propertyName);
	}
 }

