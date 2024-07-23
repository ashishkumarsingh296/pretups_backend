package com.ctmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.classes.BaseTest;
import com.commons.MasterI;
import com.utils._masterVO;

import freemarker.template.utility.StringUtil;

public class CTManager {

	private static String ACTIONID = null;
	private static String CTServer_Host = null;
	
	public CTManager() {
		CTServer_Host = _masterVO.getProperty("CTMode.Server");
		
		if (_masterVO.getProperty("CTMode.Output.clean").equalsIgnoreCase("true")) {
			File outputDir = new File(_masterVO.getProperty("ExtentReportPath"));
			CTHelper.cleanOutputDirectory(outputDir);
		}
	}
	
	public void createThread() {
		
		String hostname = "Unknown";
		
		try {
		    InetAddress addr;
		    addr = InetAddress.getLocalHost();
		    hostname = addr.getHostName();
		} catch (UnknownHostException ex) {
		}
		
		String str =  System.getProperty("user.home").replace("\\", "\\\\");
		String[] strbuffX = str.split("\\\\");
		int len = strbuffX.length;

		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(CTServer_Host + "ctreporting/api/createThread/");
		NameValuePair[] data = { new NameValuePair("db", _masterVO.getMasterValue(MasterI.DB_INTERFACE_TYPE)),
				new NameValuePair("initiator", hostname + " (" + strbuffX[len - 1] + ")"),
				new NameValuePair("appurl", _masterVO.getMasterValue(MasterI.WEB_URL)),
				new NameValuePair("clientname", _masterVO.getMasterValue(MasterI.CLIENT_NAME)),
				new NameValuePair("leadname", _masterVO.getMasterValue(MasterI.LEAD_NAME)),
				new NameValuePair("ctmode", _masterVO.getProperty("CTMode.Status")),
				new NameValuePair("teamcode", _masterVO.getProperty("CTMode.TeamID"))
				};
		post.setRequestBody(data);
		BufferedReader br = null;
		// execute method and handle any error responses.
		try {
			int returnCode = client.executeMethod(post);

			if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				System.err.println("The Post method is not implemented by this URI");
				post.getResponseBodyAsString();
			} else {
				ACTIONID = post.getResponseBodyAsString();
			}
		} catch (Exception e) {
		} finally {
			post.releaseConnection();
			if (br != null)
				try {
					br.close();
				} catch (Exception fe) {
				}
		}	
	}
	
	public void closeThread() throws IOException {
		
		File outputDir = new File(System.getProperty("user.dir") + "\\Output\\");
		CTHelper.uploadOutput(outputDir, ACTIONID);
		
		ExtentReader HTMLReader = new ExtentReader();
		String FileContent = HTMLReader.readFile(BaseTest.extentReportPath);
	    Document doc = Jsoup.parse(FileContent, "", Parser.xmlParser());
	    String executionTimeInMs = null;
	    
		Elements es = doc.select("div[class='card-panel']");
		StringBuilder testCaseString = new StringBuilder();
		try {
		    for (Element e : es.select("tr")) {
		    	Element TestType = e.nextElementSibling().select("td").first();
		    	testCaseString.append(TestType.text());
		    	Element PassCount = TestType.nextElementSibling();
		    	testCaseString.append(":" + PassCount.text());
		    	Element FailCount = PassCount.nextElementSibling();
		    	testCaseString.append(":" + FailCount.text());
		    	Element SkipCount = FailCount.nextElementSibling();
		    	testCaseString.append(":" + SkipCount.text() + "|");
		    	}
		} catch (Exception e) {
			Elements ExTimeCards = doc.select("div[class='card-panel r']");
		    Element exetimeCard = ExTimeCards.last();
		    Elements executionTime = exetimeCard.select("div[class='panel-lead']");
		    String orgExeTime = executionTime.text();
		    String executionTimeMS = orgExeTime.replace("ms","");
		    executionTimeInMs = executionTimeMS.replace(",","");
		}
		
		Elements es2 = doc.select("ul[class='test-collection']");
		StringBuilder moduleBuilder = new StringBuilder();
		for (Element test : es2.select("div[class='test-heading']")) {
			Element ModuleName = test.select("span[class='test-name']").first();
			Element ExecutionTime = ModuleName.nextElementSibling();
			Element OverallStatus = ExecutionTime.nextElementSibling();
	    	moduleBuilder.append(ModuleName.text());
			moduleBuilder.append("@" + ExecutionTime.text());
			moduleBuilder.append("@" + StringUtil.capitalize(OverallStatus.text()) + "|");
	    	}
		
		String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(Long.parseLong(executionTimeInMs)),
			    TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(executionTimeInMs)) % TimeUnit.HOURS.toMinutes(1),
			    TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(executionTimeInMs)) % TimeUnit.MINUTES.toSeconds(1));
		
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(CTServer_Host + "ctreporting/api/closeThread/");
		NameValuePair[] data = {
			new NameValuePair("actionid", ACTIONID),
		    new NameValuePair("execution_time", hms),
		    new NameValuePair("executed_modules", moduleBuilder.toString()),
		    new NameValuePair("test_details", testCaseString.toString()),
		};
		post.setRequestBody(data);
		BufferedReader br = null;
		// execute method and handle any error responses.
		try{
		      int returnCode = client.executeMethod(post);

		      if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
		        System.err.println("The Post method is not implemented by this URI");
		        // still consume the response body
		        post.getResponseBodyAsString();
		      } else {

		      }
		    } catch (Exception e) {
		      System.err.println(e);
		    } finally {
		      post.releaseConnection();
		      if(br != null) try { br.close(); } catch (Exception fe) {}
		    }
		
		if (_masterVO.getProperty("CTMode.DataProvider.clean").equalsIgnoreCase("true")) {
			CTHelper.cleanDataProvider();
		}
	}
}
