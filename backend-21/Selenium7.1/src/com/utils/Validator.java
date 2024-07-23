/**
 * 
 */
package com.utils;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;

/**
 * @author lokesh.kontey
 *
 */
public class Validator extends BaseTest{
	
	public static void messageCompare(String actual, String expected){
		if (actual.equals(expected)){
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
			currentNode.log(Status.PASS, MarkupHelper.createLabel("Message Validation Successful", ExtentColor.GREEN));}
		else {
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
			currentNode.log(Status.FAIL, MarkupHelper.createLabel("Message Validation Failed",ExtentColor.RED));
			ExtentI.attachScreenShot();
			}
	}
	
	public static void partialmessageCompare(String actual, String expected){
		if (actual.contains(expected))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + expected + "] but found [" + actual + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
			ExtentI.attachScreenShot();
			}
	}
	
	public static void nullValidator(String returnedMessage){
		if (returnedMessage==null || returnedMessage.equals(""))
			currentNode.log(Status.FAIL, "Message validation failed.");
		else {
			currentNode.log(Status.PASS,"Message validation successful.");
			}
	}
	
}
