package com.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;

public class Log extends BaseTest{
		
	public static void startTestCase(String sTestCaseName, Object ... params) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		log.info("------------------ "+"Test Case Execution Started For "+sTestCaseName+" -------------------");

		StringBuilder _logGen = new StringBuilder("Entered " + sTestCaseName + "(");
		for (int i = 0; i<params.length; i++) {
			if (i != (params.length - 1))
				_logGen.append(params[i] + ", ");
			else
				_logGen.append(params[i]);
		}
		_logGen.append(")");
		log.info(_logGen.toString());
		
	}
		
	public static void methodEntry(String methodname, Object ... params) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		StringBuilder _logGen = new StringBuilder("Entered " + methodname + "(");
		for (int i = 0; i<params.length; i++) {
			if (i != (params.length - 1))
				_logGen.append(params[i] + ", ");
			else
				_logGen.append(params[i]);
		}
		_logGen.append(")");
		Log.info(_logGen.toString());
		if (currentNode != null)
			ExtentI.Markup(ExtentColor.GREY, _logGen.toString());
	}
	
	public static void methodExit(String methodname) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info("Exiting " + methodname);
		if (currentNode != null)
			ExtentI.Markup(ExtentColor.GREY, "Exited " + methodname + "()");
	}

	public static void endTestCase(String sTestCaseName) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info(" ----------------- "+" Test Case Execution completed for "+sTestCaseName+" -----------------");
	}

	public static void info(String message) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info(message);
		if (currentNode != null)
			currentNode.log(Status.INFO, message);
	}
	
	public static void info(ResultSet resultSet) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		
		try {
			if (!resultSet.isBeforeFirst() ) {    
			   	Log.info("ResultSet Returned as NULL"); 
			   	if (currentNode != null)
					currentNode.log(Status.INFO, "ResultSet Returned as NULL");
			} else {
				JSONArray obj = _parser.convertToJSON(resultSet);
				resultSet.beforeFirst();
				Log.info("Database Returned: " + obj.toString());
				if (currentNode != null)
					currentNode.log(Status.INFO, "Database Returned: " + obj.toString());
			}
		} catch (SQLException e) {
			Log.debug("Error :: " + e);
		}
	}
	
	public static void info(Object[][] obj) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info("Database Returns: " + ToStringBuilder.reflectionToString(obj));
		if (currentNode != null)
			currentNode.log(Status.INFO, "Database Returns: " + ToStringBuilder.reflectionToString(obj));
	}
	
	public static void info(String methodname, Object[] obj) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info(methodname + " Returns: " + ToStringBuilder.reflectionToString(obj));
		if (currentNode != null)
			currentNode.log(Status.INFO, "Database Returns: " + ToStringBuilder.reflectionToString(obj));
	}
	
	public static void debug(String methodname, Object[][] obj) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info(methodname + " Returns: " + ToStringBuilder.reflectionToString(obj));
	}

	public static void fail(ITestResult e) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info(e.getThrowable().toString());
		if (currentNode != null)
		currentNode.log(Status.FAIL, e.getThrowable());
	}

	public static void warn(String message) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.warn(message);
		if (currentNode != null)
		currentNode.log(Status.WARNING, message);
	}
	
	public static void failNode(String message) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.error(message);
		if (currentNode != null)
		currentNode.log(Status.FAIL, message);
	}

	public static void error(String message) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.error(message);
		if (currentNode != null)
		currentNode.log(Status.ERROR, message);
	}

	public static void fatal(String message) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.fatal(message);
		if (currentNode != null)
		currentNode.log(Status.FATAL, message);
	}

	public static void debug(String message) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info(message);
	}
	
	public static void debug(ResultSet resultSet) {
		PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		
		try {
			if (!resultSet.isBeforeFirst() ) {    
			   	Log.info("ResultSet Returned as NULL"); 
			} else {
				JSONArray obj = _parser.convertToJSON(resultSet);
				Log.info("Database Returned: " + obj.toString());
				resultSet.beforeFirst();
			}
		} catch (SQLException e) {
		}
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
    	PropertyConfigurator.configure("./src/test/resources/sysconfig/Log4j.properties");
		Logger Log = Logger.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
		Log.info(message);
		currentNode.log(Status.SKIP, MarkupHelper.createLabel(message, ExtentColor.YELLOW));
    }
    
	public static void unexpectedSkip(String message) {
	    BufferedWriter bw = null;
	    try {
	    	bw = new BufferedWriter(new FileWriter(_masterVO.getProperty("ExtentReportPath") + "ModuleStatus_" + System.getProperty("current.date") + ".txt", true));
	    	bw.write(message);
	    	bw.newLine();
	    	bw.flush();
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	    } finally {                       // always close the file
		if (bw != null) try {
		    bw.close();
		} catch (IOException ioe2) {
		  // just ignore it
		 }
	   } // end try/catch/finally

	   } // end test()
	
}
