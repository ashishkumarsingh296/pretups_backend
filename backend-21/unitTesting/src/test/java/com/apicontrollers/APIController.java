package com.apicontrollers;

import com.commons.MasterI;
import com.utils.Log;
import com.utils._masterVO;

import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class APIController {

	public static String execute(String xmlData) {
		Log.info("Entered: APIController.execute()");
		String appURL = _masterVO.getMasterValue(MasterI.WEB_URL);
		appURL = appURL.split("/pretups")[0];
		Log.info("API Base URI: " + appURL);
		RestAssured.baseURI = appURL;
		RequestSpecification request = RestAssured.given();
		
		request.body(xmlData);
		Log.info("Request Content Type: text/xml");
		request.contentType("text/xml");
		
		StringBuilder PostRequestURL = new StringBuilder("/pretups/");
		PostRequestURL.append(_masterVO.getProperty("EXTGW_SERVICE_NAME"));
		PostRequestURL.append("?REQUEST_GATEWAY_CODE=" + _masterVO.getProperty("EXTGW_REQUEST_GATEWAY_CODE"));
		PostRequestURL.append("&REQUEST_GATEWAY_TYPE=" + _masterVO.getProperty("EXTGW_REQUEST_GATEWAY_TYPE"));
		PostRequestURL.append("&LOGIN=" + _masterVO.getProperty("EXTGW_LOGIN"));
		PostRequestURL.append("&PASSWORD=" + _masterVO.getProperty("EXTGW_PASSWORD"));
		PostRequestURL.append("&SOURCE_TYPE=" + _masterVO.getProperty("EXTGW_SOURCE_TYPE"));
		PostRequestURL.append("&SERVICE_PORT=" + _masterVO.getProperty("EXTGW_SERVICE_PORT"));
		Log.info("Post Request URL: " + PostRequestURL.toString());
		
		Response response = request.post(PostRequestURL.toString());
		Log.info("<pre><b>Response ::</b><br> <xmp>" + response.body().asString() + "</xmp></pre>");
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML,response.body().asString());
		
		System.out.println("TXN ID: " + xmlPath.get("COMMAND.TXNID"));
		System.out.println("TXN STATUS: " + xmlPath.get("COMMAND.TXNSTATUS"));
		
		
		return response.body().asString();
	}
}
