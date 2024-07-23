package com.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;
import com.classes.BaseTest;

public class Log extends BaseTest{
		
	public static void startTestCase(String sTestCaseName) {
		PropertyConfigurator.configure("sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info("------------------ "+"Test Case Execution Started For "+sTestCaseName+" -------------------");
	}

	public static void endTestCase(String sTestCaseName) {
		PropertyConfigurator.configure("sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info(" ----------------- "+" Test Case Execution completed for "+sTestCaseName+" -----------------");
	}

	public static void info(String message) {
		PropertyConfigurator.configure("sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info(message);
		if (currentNode != null)
			currentNode.log(Status.INFO, message);
	}

	public static void fail(ITestResult e) {
		PropertyConfigurator.configure("sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info(e.getThrowable().toString());
		if (currentNode != null)
		currentNode.log(Status.FAIL, e.getThrowable());
	}

	public static void warn(String message) {
		PropertyConfigurator.configure("sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.warn(message);
		if (currentNode != null)
		currentNode.log(Status.WARNING, message);
	}
	
	public static void failNode(String message) {
		PropertyConfigurator.configure("sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.error(message);
		if (currentNode != null)
		currentNode.log(Status.FAIL, message);
	}

	public static void error(String message) {
		PropertyConfigurator.configure("sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.error(message);
		if (currentNode != null)
		currentNode.log(Status.ERROR, message);
	}

	public static void fatal(String message) {
		PropertyConfigurator.configure("sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.fatal(message);
		if (currentNode != null)
		currentNode.log(Status.FATAL, message);
	}

	public static void debug(String message) {
		PropertyConfigurator.configure("sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.debug(message);
		if (currentNode != null)
		currentNode.log(Status.DEBUG, message);
	}
	
    public static void writeStackTrace(Exception e)
    {
    	Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
        StringWriter stack = new StringWriter();
        e.printStackTrace(new PrintWriter(stack));
        Log.info(stack.toString());
        if (currentNode != null)
    		currentNode.log(Status.DEBUG, "<pre>" + stack.toString() + "</pre>");
    }
    
    public static void skip(String message) {
    	PropertyConfigurator.configure("sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info(message);
		currentNode.log(Status.SKIP, message);
    }
	
}
