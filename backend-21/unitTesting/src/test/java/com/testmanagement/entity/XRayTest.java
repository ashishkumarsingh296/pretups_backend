package com.testmanagement.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.testng.ITestResult;

import com.testmanagement.util.JIRAUtil;

public class XRayTest {
	
	public static final String TestKey = "testKey";
	public static final String StartTime = "start";
	public static final String EndTime = "finish";
	public static final String TestStatus = "status";
	public static final String TestComment = "comment";
	
	private String testkey;
	private String starttime;
	private String endtime;
	private String teststatus;
	private String testcomment;
	
	public XRayTest() {
		
	}
	
	public static XRayTest getXRayTest(ITestResult result) {	
		
		DateFormat xrayDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");  
		
		XRayTest XRayTest = new XRayTest();
		XRayTest.setTestkey("" + result.getAttribute("TestKey"));
		XRayTest.setStarttime(xrayDateFormat.format(result.getStartMillis()));
		XRayTest.setEndtime(xrayDateFormat.format(result.getEndMillis()));
		XRayTest.setTeststatus(JIRAUtil.toXrayStatus(result.getStatus()));
		XRayTest.setTestcomment(result.getTestClass().getName());
		
		return XRayTest;
	}
	
	public String getTestkey() {
		return testkey;
	}
	public void setTestkey(String testkey) {
		this.testkey = testkey;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getTeststatus() {
		return teststatus;
	}
	public void setTeststatus(String teststatus) {
		this.teststatus = teststatus;
	}
	public String getTestcomment() {
		return testcomment;
	}
	public void setTestcomment(String testcomment) {
		this.testcomment = testcomment;
	}
}
