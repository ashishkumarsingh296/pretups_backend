package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.apicontrollers.extgw.o2ctransfer.EXTGWO2CAPI;
import com.classes.CaseMaster;
import com.commons.GatewayI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.SystemPreferences;
import com.pretupsControllers.BTSLUtil;
import com.sshmanager.SSHService;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class _APIUtil {
	
	public static String[] EXTENSIONS = { "doc", "docx" };
	final static String documentPath_EXTGW = ".//src//test//resources//config//Documents//EXTGW";
	final static String documentPath_USSD = ".//src//test//resources//config//Documents//USSD";
	final static String documentPath_USSD_PLAIN = ".//src//test//resources//config//Documents//USSD_PLAIN";
	public static int issueCounter = 1;
	public static StringBuilder documentIssueMinor = new StringBuilder();
	public static StringBuilder documentIssueMajor = new StringBuilder();
	
	public static final String API_SUCCESS = "200";
	

	/**
	 * This method filters out the available files in Documents directory for API execution.
	 * @param directory
	 * @param extensions
	 * @author krishan.chawla
	 * @return
	 */
	
    public Collection<File> searchFilesWithCaseInsensitiveExtensions(final File directory, final String[] extensions) {
        IOFileFilter fileFilter = new SuffixFileFilter(extensions, IOCase.INSENSITIVE);
        return FileUtils.listFiles(directory,
                fileFilter,
                DirectoryFileFilter.INSTANCE);
    }

    /**
     * This method fetches all the available services as per the provided EXTGW Document. This method fetches
     * data in <TYPE> tag which is further processed using duplicate & special character filtering.
     * @param Gateway
     * @return
     * @throws IOException
     * @author krishan.chawla
     */
    public static ArrayList<String> getAPIServices(String gateway) throws IOException {
    	final String methodname = "getAPIServices";
    	Log.debug("Entered " + methodname + "(" + gateway + ")");
    	
    	ArrayList<String> APIServiceList = new ArrayList<String>();
    	String APIDocumentRoot = null;
    	
    	if (gateway.equalsIgnoreCase(GatewayI.EXTGW))
    		APIDocumentRoot = documentPath_EXTGW;
    	else if (gateway.equalsIgnoreCase(GatewayI.USSD))
    		APIDocumentRoot = documentPath_USSD;
    	else if (gateway.equalsIgnoreCase(GatewayI.USSD_PLAIN))
    		APIDocumentRoot = documentPath_USSD_PLAIN;
    	
        Collection<File> caseInsensitiveDocs = new _APIUtil().searchFilesWithCaseInsensitiveExtensions(new File(APIDocumentRoot), _APIUtil.EXTENSIONS);
        Pattern patt;
        if (gateway.equalsIgnoreCase(GatewayI.USSD_PLAIN)) {
        	patt = Pattern.compile("TYPE=(.+?)&", Pattern.CASE_INSENSITIVE);	
        } else {
        	patt = Pattern.compile("<TYPE>(.+?)</TYPE>", Pattern.CASE_INSENSITIVE);
        }
            
        
        for (File document: caseInsensitiveDocs) {
        	
        	FileInputStream fistream = new FileInputStream(APIDocumentRoot + "//" + document.getName());
        	XWPFDocument document1 = new XWPFDocument(fistream);

        	List<XWPFParagraph> paragraphs = document1.getParagraphs();
        	StringBuilder docStr = new StringBuilder();

            for (XWPFParagraph para : paragraphs) {
            	docStr.append(para.getText());
            }
            
            document1.close();
        	
        	final Matcher matcher = patt.matcher(docStr.toString());
        	while (matcher.find()) {
        	    //System.out.println("Full match: " + matcher.group(0));
        	    for (int i = 1; i <= matcher.groupCount(); i++) {
        	    	
        	    	// Removing Special characters from the <TYPE> value & Adding non repetitive elements to ArrayList
        	    	
        	    	Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        	    	Matcher m = p.matcher(matcher.group(i));
        	    	if (m.find()) {
        	    		documentIssueMinor.append(issueCounter++ + ".	" + matcher.group(i) + " - Special Charater Usage in <TYPE></TYPE> tag." );
        	    		documentIssueMinor.append(System.getProperty("line.separator"));
        	    	}
        	    	
        	    	String serviceKey = matcher.group(i).trim().replaceAll("[\\-\\+\\.\\^\\:\\,\\<\\>]", "");
        	    	if (!APIServiceList.contains(serviceKey))
        	    		APIServiceList.add(serviceKey);
        	    }
        	}    	 
        }
        Log.debug("Exiting " + methodname + "(" + APIServiceList + ")");
		return APIServiceList;
    }
    
    public static String buildAPI(String api, HashMap<String, String> dataMap) {
    	final String methodname = "buildAPI";
    	Log.debug("Entered " + methodname + "(" + api + ", " + Arrays.asList(dataMap) + ")");
		
    	Document doc = null;
    		try {
    			doc = DocumentBuilderFactory.newInstance()
				        .newDocumentBuilder()
				        .parse(new InputSource(new StringReader(api)));
    			
    			doc.getDocumentElement().normalize();
    			
			} catch (Exception e) {
				Log.info("Error while building API. ");
				Log.writeStackTrace(e);
			}

    		for ( String key : dataMap.keySet() ) {
    			try {
    			Node mapNode = doc.getElementsByTagName(key).item(0);
    			mapNode.setTextContent(dataMap.get(key).toString());
    			} catch (Exception e) {}
    		}
        
    	Log.debug("Exiting " + methodname + "()");
    	return convertDocumentToString(doc);
    }
    
    private static String convertDocumentToString(Document doc) {
    	                                                                                       
/*    	TransformerFactory transformerFactory = TransformerFactory.newInstance();   
    	Transformer transformer = null;
    	StringWriter writer = new StringWriter();
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		}              
    	transformer.setOutputProperty(OutputKeys.METHOD, "html");                   
    	DOMSource source = new DOMSource(doc);                                                         
    	try {
			transformer.transform(source, new StreamResult(writer));
			String output = writer.getBuffer().toString();
            return output;
		} catch (TransformerException e) {
			e.printStackTrace();
		} */  
    	
    	
    	
    	
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.setOutputProperty(OutputKeys.METHOD, "html"); 
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return "<?xml version=\"1.0\"?>" + output;
        } catch (TransformerException e) {
            Log.info("Error while converting XML to String");
            Log.writeStackTrace(e);
        }
        
        return null;
    }
    
    public static String[] executeAPI(String gatewayType, String serviceName, String api) {
    	final String methodname = "executeAPI";
    	Log.debug("Entered " + methodname + "(" + gatewayType + ", " + serviceName + ", " + api + ")");
    	
		SSHService.startMessageSentLogMonitor();
    	
    	String[] apiObject = new String[5];
    	
		String appURL = _masterVO.getMasterValue(MasterI.WEB_URL);
		appURL = appURL.split("/pretups")[0];
		Log.info("API Base URI: " + appURL);
		RestAssured.baseURI = appURL;
		RequestSpecification request = RestAssured.given();
		
		request.body(api);
		Log.info("<b>Request Content Type:</b> text/xml");
		request.contentType("text/xml");
		Log.info("<pre><b>Request:</b><br><xmp>" + format(api) + "</xmp></pre>");
		String PostURL = GatewayI.getPOSTURL(gatewayType, serviceName);
		Log.info("<b>Post Request URL:</b> " + PostURL);
		
		Response response = request.post(PostURL);
		Log.info("<pre><b>Response ::</b><xmp>" + format(response.body().asString()) + "</xmp></pre>");	
		String SMS = SSHService.stopMessageSentLogMonitor();
		Log.info("<pre><b>Message in MessageSentLog:</b><br><xmp>" + SMS + "</xmp></pre>");
		apiObject[0] = format(api);
		apiObject[1] = format(response.body().asString());
		
		try {
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, response.body().asString());
		apiObject[2] = xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString();
		} catch (Exception e) {
			apiObject[2] = null;
		}
		apiObject[3] = SMS;
		apiObject[4] = _masterVO.getC2SMessage(apiObject[2]);
		
		Log.debug("Exiting " + methodname + "()");
		return apiObject;
    }
    
    public static String[] executePlainAPI(String gatewayType, String serviceName, String api) {
    	final String methodname = "executePlainAPI";
    	Log.debug("Entered " + methodname + "(" + gatewayType + ", " + serviceName + ", " + api + ")");
    	
		SSHService.startMessageSentLogMonitor();
    	
    	String[] apiObject = new String[5];
    	
		String appURL = _masterVO.getMasterValue(MasterI.WEB_URL);
		appURL = appURL.split("/pretups")[0];
		Log.info("API Base URI: " + appURL);
		RestAssured.baseURI = appURL;
		RequestSpecification request = RestAssured.given();
		
		request.body(api);
		Log.info("<b>Request Content Type:</b> text/plain");
		request.contentType("text/plain");
		Log.info("<pre><b>Request:</b><br><xmp>" + api + "</xmp></pre>");
		String PostURL = GatewayI.getPOSTURL(gatewayType, serviceName);
		Log.info("<b>Post Request URL:</b> " + PostURL);
		
		Response response = request.post(PostURL);
		Log.info("<pre><b>Response ::</b><xmp>" + response.body().asString() + "</xmp></pre>");	
		String SMS = SSHService.stopMessageSentLogMonitor();
		Log.info("<pre><b>Message in MessageSentLog:</b><br><xmp>" + SMS + "</xmp></pre>");
		apiObject[0] = api;
		String responseBody = response.body().asString();
		apiObject[1] = responseBody != null ? responseBody.trim() : "";
		Map<String, String> queryMap = BTSLUtil.getQueryMap(apiObject[1]);
		apiObject[2] = queryMap.get("TXNSTATUS") != null ? queryMap.get("TXNSTATUS").trim() : "" ;
		apiObject[3] = SMS;
		apiObject[4] = _masterVO.getC2SMessage(apiObject[2]);
		
		Log.debug("Exiting " + methodname + "()");
		return apiObject;
    }
    
    
    public static String[] executePlainSMSCAPI(String gatewayType, String serviceName, String api) {
    	final String methodname = "executePlainSMSCAPI";
    	Log.debug("Entered " + methodname + "(" + gatewayType + ", " + serviceName + ", " + api + ")");
    	
		SSHService.startMessageSentLogMonitor();
    	
    	String[] apiObject = new String[5];
    	
		String appURL = _masterVO.getMasterValue(MasterI.WEB_URL);
		appURL = appURL.split("/pretups")[0];
		Log.info("API Base URI: " + appURL);
		RestAssured.baseURI = appURL;
		RequestSpecification request = RestAssured.given();
		
		//request.body(api);
		Log.info("<b>Request Content Type:</b> text/plain");
		request.contentType("text/plain");
		Log.info("<pre><b>Request:</b><br><xmp>" + api + "</xmp></pre>");
		String PostURL = GatewayI.getPOSTURL(gatewayType, serviceName);
		Log.info("<b>Post Request URL:</b> " + PostURL);
		
		Response response = request.get(PostURL + "&" + api);
		Log.info("<pre><b>Response ::</b><xmp>" + response.body().asString() + "</xmp></pre>");	
		String SMS = SSHService.stopMessageSentLogMonitor();
		Log.info("<pre><b>Message in MessageSentLog:</b><br><xmp>" + SMS + "</xmp></pre>");
		apiObject[0] = api;
		apiObject[1] = response.body().asString();
		Map<String, String> queryMap = BTSLUtil.getSMSCQueryMap(apiObject[1]);
		apiObject[2] = queryMap.get("TXNSTATUS");
		apiObject[3] = SMS;
		apiObject[4] = _masterVO.getC2SMessage(apiObject[2]);
		
		Log.debug("Exiting " + methodname + "()");
		return apiObject;
    }
    
    public static String[] executeHttpReq(String gatewayType, String serviceName, String api) {
    	final String methodname = "executeAPI";
    	Log.debug("Entered " + methodname + "(" + gatewayType + ", " + serviceName + ", " + api + ")");
    	
    	SSHService.startMessageSentLogMonitor();
    	
    	String[] apiObject = new String[5];
    	
		String appURL = _masterVO.getMasterValue(MasterI.WEB_URL);
		appURL = appURL.split("/pretups")[0];
		Log.info("API Base URI: " + appURL);
		RestAssured.baseURI = appURL;
		RequestSpecification request = RestAssured.given();
		
		request.body(api);
		Log.info("<b>Request Content Type:</b> application/x-www-form-urlencoded");
		request.contentType("application/x-www-form-urlencoded");
		Log.info("<pre><b>Request:</b><br><xmp>" + format(api) + "</xmp></pre>");
		String PostURL = GatewayI.getPOSTURL(gatewayType, serviceName);
		Log.info("<b>Post Request URL:</b> " + PostURL);
		
		Response response = request.post(PostURL);
		Log.info("<pre><b>Response ::</b><xmp>" + format(response.body().asString()) + "</xmp></pre>");	
		String SMS = SSHService.stopMessageSentLogMonitor();
		
		apiObject[0] = format(api);
		apiObject[1] = format(response.body().asString());
		
		try {
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, response.body().asString());
		apiObject[2] = xmlPath.get(EXTGWO2CAPI.TXNSTATUS).toString();
		} catch (Exception e) {
			apiObject[2] = null;
		}
		apiObject[3] = SMS;
		apiObject[4] = _masterVO.getC2SMessage(apiObject[2]);
		
		Log.debug("Exiting " + methodname + "()");
		return apiObject;
    }
    
    
    public static String format(String xml) {
    	
    	try {
    	Pattern p = Pattern.compile("<\\?xml version=\"1\\.0\"\\?><!DOCTYPE COMMAND PUBLIC \"-\\/\\/Ocam\\/\\/DTD XML Command 1\\.0\\/\\/EN\" \"xml\\/command\\.dtd\">",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        xml =  p.matcher(xml).replaceAll("");
    	DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    	Document doc = db.parse(new InputSource(new StringReader(xml.toString())));
    	 
    	OutputFormat format = new OutputFormat(doc);
    	format.setIndenting(true);
    	format.setIndent(1);
    	format.setOmitXMLDeclaration(true);
    	format.setLineWidth(Integer.MAX_VALUE);
    	Writer outxml = new StringWriter();
    	XMLSerializer serializer = new XMLSerializer(outxml, format);
    	serializer.serialize(doc);
    	return outxml.toString();
    	} catch(Exception e) {
    		
    	}
    	return null;
    	}
    
    public static String buildModuleCode(String moduleCode, int counter) {
    	return moduleCode + (counter < 10 ? "0" : "") + counter;
    }
    
    public static void buildGatewayMasterFile() {
    	try {
	    	String filename = ".//Output//[PreTUPSGW]" +_masterVO.getMasterValue(MasterI.CLIENT_NAME) + "_" + _masterVO.getMasterValue(MasterI.APPLICATION_VERSION) + "_" + System.getProperty("current.date") + ".xlsx" ;
	        XSSFWorkbook workbook = new XSSFWorkbook();
	        workbook.createSheet("EXTGW"); 
	        FileOutputStream fileOut = new FileOutputStream(filename);
	        workbook.write(fileOut);
	        fileOut.close();
	        workbook.close();
	    	
	    	ExcelUtility.setExcelFile(filename, "EXTGW");
	    	ExcelUtility.createHeader("Test Case Code", "Module Code", "Test Case Description", "Expected Error Code", "Test Status", "Actual Error Code", "Message" , "Request", "Response", "SMS", "Decoded SMS");
    	} catch (Exception e) {
    		Log.info("Error while building Gateway Master File");
    	}
    }
    
    public static void addExecutionRecord(CaseMaster CaseMaster, String[] object) {
    	ExcelUtility.setExcelFile(".//Output//[PreTUPSGW]" +_masterVO.getMasterValue(MasterI.CLIENT_NAME) + "_" + _masterVO.getMasterValue(MasterI.APPLICATION_VERSION) + "_" + System.getProperty("current.date") + ".xlsx", "EXTGW");
    	int rowCounter = ExcelUtility.getRowCount();
    	ExcelUtility.setCellData(CaseMaster.getTestCaseCode(), rowCounter+1, 0);
    	ExcelUtility.setCellData(CaseMaster.getModuleCode(), rowCounter+1, 1);
    	ExcelUtility.setCellData(CaseMaster.getDescription(), rowCounter+1, 2);
    	ExcelUtility.setCellData(CaseMaster.getErrorCode(), rowCounter+1, 3);
    	
    	if (CaseMaster.getErrorCode().equalsIgnoreCase(object[2]))
    		ExcelUtility.setCellData("Pass", rowCounter+1, 4, IndexedColors.BRIGHT_GREEN, IndexedColors.BLACK);
    	else
    		ExcelUtility.setCellData("Fail", rowCounter+1, 4, IndexedColors.RED, IndexedColors.WHITE);
    	
    	ExcelUtility.setCellData(object[2], rowCounter+1, 5);
    	ExcelUtility.setCellData(object[4], rowCounter+1, 6);
    	ExcelUtility.setCellData(object[0], rowCounter+1, 7);
    	ExcelUtility.setCellData(object[1], rowCounter+1, 8);
    	ExcelUtility.setCellData(object[3], rowCounter+1, 9);
    	try {
			ExcelUtility.setCellData(URLDecoder.decode(object[3], "UTF-8"), rowCounter+1, 10);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static String getCurrentTimeStamp() {
    	if (BTSLUtil.isNullString(SystemPreferences.DATE_FORMAT_CAL_JAVA))
    		return new SimpleDateFormat(SystemPreferences.EXTERNAL_DATE_FORMAT).format(new Date());
    	else {
    		String date = new SimpleDateFormat(PretupsI.DATE_FORMAT).format(new Date());
    		return BTSLDateUtil.getSystemLocaleDate(date);
    	}
    }
    
    public static String getCurrentTimeStampXML() {
    	if (BTSLUtil.isNullString(SystemPreferences.DATE_FORMAT_CAL_JAVA))
    		return new SimpleDateFormat(_masterVO.getProperty("CCE_XML_EXTERNAL_DATE_FORMAT")).format(new Date());
    	else {
    		String date = new SimpleDateFormat(PretupsI.DATE_FORMAT).format(new Date());
    		return BTSLDateUtil.getSystemLocaleDate(date.toString());
    	}
    }
    
	public static String removeTagsfromAPI(String API,String... tags){
		for(int i=0;i<tags.length;i++){
		API = API.replaceAll("<("+tags[i]+")>(.)*<\\/("+tags[i]+")>", "");}
		return API;
	}
	
	public static String implementEncryption(String PIN_OR_PASSWORD) {
		final String methodname = "implementEncryption";
		Log.debug("Entered " + methodname + "(" + PIN_OR_PASSWORD + ")");
		String APIEncryptionStatus = _masterVO.getClientDetail("API_ENCRYPTION_ALLOW");
		if (APIEncryptionStatus.equalsIgnoreCase("0")){ 
			Log.debug("API_ENCRYPTION_ALLOW = 0, returns encrypted value: " + PIN_OR_PASSWORD);
			Log.debug("Exiting " + methodname + "()");
			return PIN_OR_PASSWORD;
		}
		else if (APIEncryptionStatus.equalsIgnoreCase("1")) {
			Log.debug("API_ENCRYPTION_ALLOW = 1, returns encrypted value: " + PIN_OR_PASSWORD);
			Log.debug("Exiting " + methodname + "()");
			return Decrypt.APIEncryption(PIN_OR_PASSWORD);
		}
		else {
			Log.info("Invalid Parameter Value defined in Client LIB For APIEncryptionStatus.");
			Log.debug("Exiting " + methodname + "()");
			return null;
		}		
	}
}
