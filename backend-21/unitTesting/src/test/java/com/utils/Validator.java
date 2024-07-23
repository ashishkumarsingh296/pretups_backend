/**
 * 
 */
package com.utils;

import java.util.Arrays;

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
		if(actual==null||expected==null){
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
			currentNode.log(Status.FAIL, MarkupHelper.createLabel("Message Validation Failed",ExtentColor.RED));
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		else if (actual.equals(expected)){
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
			currentNode.log(Status.PASS, MarkupHelper.createLabel("Message Validation Successful", ExtentColor.GREEN));}
		else {
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
			currentNode.log(Status.FAIL, MarkupHelper.createLabel("Message Validation Failed",ExtentColor.RED));
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
	}
	
	public static void partialmessageCompare(String actual, String expected){
		if (actual.contains(expected)||expected.contains(actual)){
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
		currentNode.log(Status.PASS, MarkupHelper.createLabel("Message Validation Successful", ExtentColor.GREEN));}
		else {
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
			currentNode.log(Status.FAIL, MarkupHelper.createLabel("Message Validation Failed",ExtentColor.RED));
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
			}
	}
	
	public static void APIMultiErrorCodeComapre(String actual, String expected){
		if (expected.contains(actual)){
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
		currentNode.log(Status.PASS, MarkupHelper.createLabel("Message Validation Successful", ExtentColor.GREEN));}
		else {
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
			currentNode.log(Status.FAIL, MarkupHelper.createLabel("Message Validation Failed",ExtentColor.RED));
			ExtentI.attachCatalinaLogs();
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
	
	public static void manyToOneMessageCompare(String[] actual, String expected){
		int msgloc = 0;
		int count=0;
		if(actual.length==0||expected==null){
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
			currentNode.log(Status.FAIL, MarkupHelper.createLabel("Message Validation Failed",ExtentColor.RED));
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		else{
		for(int i=0;i<=(actual.length-1);i++){
			if(expected.equals(actual[i])){
				msgloc=i;
				count++;
			}}
		if(count>1){
			currentNode.log(Status.INFO, "Same error message is appearing multiple times on WEB screen.");
			currentNode.log(Status.FAIL, MarkupHelper.createLabel("Message Validation Successful", ExtentColor.GREEN));}
		
		else if(count==1){
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual[msgloc] + "</pre>");
			currentNode.log(Status.PASS, MarkupHelper.createLabel("Message Validation Successful", ExtentColor.GREEN));}
		
		else if(count==0){
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + Arrays.toString(actual) + "</pre>");
			currentNode.log(Status.FAIL, MarkupHelper.createLabel("Message Validation Failed",ExtentColor.RED));
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}}
	}
	
	
	public static void manyToManyMessageCompare(String[] actual, String[] expected){
		int msgloc = 0;
		int count=0;
		if(actual.length==0||expected.length==0){
			currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
			currentNode.log(Status.FAIL, MarkupHelper.createLabel("Message Validation Failed",ExtentColor.RED));
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		}
		else{
		for(int i=0;i<=(actual.length-1);i++){
			for(int p=0;p<expected.length;p++){
			if(expected[p].equals(actual[i])){
				currentNode.log(Status.INFO, "<pre><b>Expected: </b>" + expected[p] + "<br><b>Found: </b>" + actual[i] + "</pre>");
				currentNode.log(Status.PASS, MarkupHelper.createLabel("Message Validation Successful", ExtentColor.GREEN));
			}}}
		}}
}
