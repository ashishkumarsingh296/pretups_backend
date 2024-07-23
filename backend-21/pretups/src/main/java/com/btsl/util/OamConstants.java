package com.btsl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/***
 * 
 * @author gaurav.pandey
 *
 */

public class OamConstants {
	
	private static Properties properties =null;
	
	private OamConstants()
	{
	
	}
	
	/***
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	
	 public static void load(String fileName) throws IOException {
	        final File file = new File(fileName);
	    	properties=new Properties();
	        try(final FileInputStream fileInputStream = new FileInputStream(file);)
	        {
	        properties.load(fileInputStream);
	        fileInputStream.close();
	        }
	        }
	 /***
	  * 
	  * @param propertyName
	  * @return
	  */
	 public static String getProperty(String propertyName) {
	        return properties.getProperty(propertyName);
	    }
	

}
