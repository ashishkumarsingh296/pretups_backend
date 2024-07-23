package com.btsl.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @author akanksha
 *
 */
public class FileUtil {
	public static final Log log = LogFactory.getLog(FileUtil.class.getName());


	 /**
     * Default Constructor
     * 
     */
    private FileUtil() {
        super();
    }
    
    /**
     * @param filewriter
     */
    public static void closeQuietly(FileWriter filewriter)
    {
      try{
        if (filewriter!= null){
        	filewriter.close();
        }
      }
      catch (IOException e){
    	  log.error("An error occurred closing filewriter.", e);
      }
    }
    
    /**
     * @param outputStream
     */
    public static void closeQuietly(FileOutputStream outputStream)
    {
      try{
        if (outputStream!= null){
        	outputStream.close();
        }
      }
      catch (IOException e){
    	  log.error("An error occurred closing outputStream.", e);
      }
    }
    
    /**
     * @param printWriter
     */
    public static void closeQuietly(PrintWriter printWriter)
    {
        if (printWriter!= null)
        	printWriter.close();
    } 
    
    /**
     * @param BufferedReader
     */
    public static void closeQuietly(BufferedReader bf)
    {
      try{
        if (bf!= null){
        	bf.close();
        }
      }
      catch (IOException e){
    	  log.error("An error occurred closing BufferedReader.", e);
      }
    }
    /**
     * 
     * @param filePath
     * @param userId
     * @param num
     * @param response
     * @throws IOException
     */
 public static void fileWriter(String filePath, String userId, String num, HttpServletResponse response) throws IOException{
	 String methodName = "fileWriter";
	 InputStream inputStream = null;
	 ServletOutputStream outputStream = null;
	try{
	 String fileName = SqlParameterEncoder.encodeParams(filePath+""+userId+"."+num);
	 inputStream = new FileInputStream(fileName);
     outputStream = response.getOutputStream();
     byte[] buffer = new byte[1024];
     int len;

     	 while ((len = inputStream.read(buffer)) != -1) {
     		 outputStream.write(buffer, 0, len);
          }
	}
	finally{
		try{ outputStream.flush(); } catch(Exception e) { log.error(methodName, "Exception while flushing output stream"); }
		try{ outputStream.close(); } catch(Exception e) { log.error(methodName, "Exception while closing output stream"); }
		try{ inputStream.close(); } catch(Exception e) { log.error(methodName, "Exception while closing input stream"); }

	}
	 
 }
    
}
