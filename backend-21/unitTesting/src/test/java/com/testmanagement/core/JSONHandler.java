package com.testmanagement.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import com.aventstack.extentreports.gherkin.model.Given;
import com.classes.CaseMaster;
import com.commons.MasterI;
import com.testmanagement.entity.XRayTest;
import com.testmanagement.util.JIRAUtil;
import com.utils.Assertion;
import com.utils._masterVO;

import io.restassured.internal.util.IOUtils;
import jline.internal.Log;
import restassuredapi.api.xrayjira.XrayJiraAPI;
import restassuredapi.pojo.fetchuserdetailsresponsepojo.FetchUserDetailsResponsePojo;

public class JSONHandler {

    private static HashMap<String, XRayTest> TestCaseMapper = new HashMap<String, XRayTest>();

    public static void addJSONTest(ITestResult result) {

        String TestKey = (String) result.getAttribute("TestKey");
        String TestExecutionStatus = JIRAUtil.toXrayStatus(result.getStatus());

        if (TestKey != null) {
            if (TestCaseMapper.get(TestKey) != null) {
                if (TestExecutionStatus.equalsIgnoreCase("Fail")) {
                    TestCaseMapper.get(TestKey).setTeststatus(TestExecutionStatus);
                }
            } else {
                XRayTest test = XRayTest.getXRayTest(result);
                TestCaseMapper.put(TestKey, test);
            }
        }
    }

    public static void buildJSON(String category) {

        /*
         * JSON Format for XRay:
         *
         * {
         *		"tests":[
         *			{
         *				"start":"2014-08-30T11:51:00+01:00",
         *				"finish":"2014-08-30T11:51:00+01:00",
         *				"comment":"org.testng.internal.TestResult",
         *				"testKey":"PRETUPS-169",
         *				"status":"Executing"
         *			}
         *		]
         *	}
         *
         */
        JSONObject jsonObject = JIRAUtil.mapToJSON(TestCaseMapper,category);

        try {
            FileWriter fileWriter = new FileWriter(_masterVO.getProperty("TestReports.XRay") + "XRayTestResult"+category+".json");
            fileWriter.write(JIRAUtil.prettyJSON(jsonObject.toString()));
            fileWriter.close();
                    	
            	String jsonBody = new String(Files.readAllBytes(Paths.get(_masterVO.getProperty("TestReports.XRay") + "XRayTestResult"+category+".json")));        
             	
            	XrayJiraAPI xrayJiraAPI = new XrayJiraAPI("https://plan.comviva.com");

            	xrayJiraAPI.setContentType(_masterVO.getProperty("contentType"));
            	//xrayJiraAPI.addBody(FileUtils.readFileToString(new File(_masterVO.getProperty("TestReports.XRay") + "XRayTestResult"+category+".json"), StandardCharsets.UTF_8));
            	// String jsonBody = new String(Files.readAllBytes(Paths.get(_masterVO.getProperty("TestReports.XRay") + "XRayTestResult"+category+".json")));        
            	xrayJiraAPI.addBody(jsonBody);
            	 xrayJiraAPI.setExpectedStatusCode(200);
            	xrayJiraAPI.perform();
            	Assertion.completeAssertions();
  
        } catch (Exception ex) {
            Log.info("JSON Write Failure -----> " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
