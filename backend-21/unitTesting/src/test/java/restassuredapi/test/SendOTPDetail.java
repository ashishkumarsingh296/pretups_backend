package restassuredapi.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.sendotp.SendOTPAPI;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Product;
import restassuredapi.pojo.sendotprequestpojo.Data;
import restassuredapi.pojo.sendotprequestpojo.SendOTPRequestPojo;
import restassuredapi.pojo.sendotpresponsepojo.SendOTPResponsePojo;

@ModuleManager(name = Module.REST_SEND_OTP_DETAIL)
public class SendOTPDetail extends BaseTest{
	
	static String moduleCode;
	
	SendOTPRequestPojo sendOTPRequestPojo = new SendOTPRequestPojo();
	SendOTPResponsePojo sendOTPResponsePojo = new SendOTPResponsePojo();
	Data data = new Data();
	Login login = new Login();
	//NetworkAdminHomePage homepage = new NetworkAdminHomePage(driver);
	Product product= null;
	
	HashMap<String,String> transfer_Details=new HashMap<String,String>(); 

	public HashMap<String,String> getExcelData(){
		HashMap<String,String> tranferDetails=new HashMap<String,String>();    
		String C2CTransferCode = _masterVO.getProperty("C2CBuyVoucherTransferCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		ArrayList<String> categorySize = new ArrayList<String>();

		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CTransferCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}
		// int rowCount = ExcelUtility.getRowCount();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

		int totalObjectCounter = 0;
		for (int i = 0; i < alist2.size(); i++) {
		    int categorySizeCounter = 0;
        for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(i))) {
                categorySizeCounter++;
            }
        }
        categorySize.add("" + categorySizeCounter);
        totalObjectCounter = totalObjectCounter + categorySizeCounter;
    }

    Object[][] Data = new Object[totalObjectCounter][9];

    for (int j = 0, k = 0; j < alist2.size(); j++) {

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int excelRowSize = ExcelUtility.getRowCount();
        String ChannelUserMSISDN = null;
        for (int i = 1; i <= excelRowSize; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
                ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                break;
            }
        }

        for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
                Data[k][0] = alist2.get(j);
                Data[k][1] = alist1.get(j);
                Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                Data[k][3] = ChannelUserMSISDN;
                Data[k][4] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, excelCounter);
                Data[k][5] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
                Data[k][6] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
                Data[k][8] = excelCounter;
               
                k++;
                
            }
        }

    }
    
    tranferDetails.put("From_MSISDN", Data[0][2].toString());
    tranferDetails.put("To_MSISDN", Data[0][3].toString());
    tranferDetails.put("From_Pin", Data[0][6].toString());
    return tranferDetails;
    
	}
	
	
	public void setupData() {
		transfer_Details=getExcelData();
		sendOTPRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		sendOTPRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		sendOTPRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		sendOTPRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		sendOTPRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		sendOTPRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLoginid("");
		data.setPassword("");
		data.setMsisdn(transfer_Details.get("From_MSISDN"));
		data.setPin(transfer_Details.get("From_Pin"));
		data.setExtcode("");
		data.setLanguage1("");
		data.setLanguage2("");
		
		sendOTPRequestPojo.setData(data);
		
		DBHandler.AccessHandler.updateAnyColumnValue("USER_OTP", "OTP_COUNT", 
				"0", "MSISDN", transfer_Details.get("From_MSISDN"));
	}

	// Successful data with valid data.
	@Test
	public void A_01_Test_success() throws Exception {
		final String methodName = "Test_SendOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SENDOTP1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());

		currentNode.assignCategory("REST");
		setupData();
		SendOTPAPI sendOTPAPI = new SendOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		sendOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		sendOTPAPI.addBodyParam(sendOTPRequestPojo);
		sendOTPAPI.setExpectedStatusCode(200);
		sendOTPAPI.perform();
		sendOTPResponsePojo = sendOTPAPI
				.getAPIResponseAsPOJO(SendOTPResponsePojo.class);
		int statusCode = Integer.parseInt(sendOTPResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test
	public void A_02_Test_BlankMsisdn() throws Exception {
		final String methodName = "Test_SendOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SENDOTP2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setMsisdn("");
		sendOTPRequestPojo.setData(data);
		
		SendOTPAPI sendOTPAPI = new SendOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		sendOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		sendOTPAPI.addBodyParam(sendOTPRequestPojo);
		sendOTPAPI.setExpectedStatusCode(200);
		sendOTPAPI.perform();
		sendOTPResponsePojo = sendOTPAPI
				.getAPIResponseAsPOJO(SendOTPResponsePojo.class);
		

		String message =sendOTPResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_03_Success_Test_BlankPin() throws Exception {
		final String methodName = "Test_SendOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SENDOTP3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setPin("");

		SendOTPAPI sendOTPAPI = new SendOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		sendOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		sendOTPAPI.addBodyParam(sendOTPRequestPojo);
		sendOTPAPI.setExpectedStatusCode(200);
		sendOTPAPI.perform();
		sendOTPResponsePojo = sendOTPAPI
				.getAPIResponseAsPOJO(SendOTPResponsePojo.class);

		int statusCode = Integer.parseInt(sendOTPResponsePojo.getDataObject().getTxnstatus());
		
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_04_Test_BlankExtnwCode() throws Exception {
		final String methodName = "Test_SendOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SENDOTP4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setExtnwcode("");
		sendOTPRequestPojo.setData(data);
		
		SendOTPAPI sendOTPAPI = new SendOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		sendOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		sendOTPAPI.addBodyParam(sendOTPRequestPojo);
		sendOTPAPI.setExpectedStatusCode(200);
		sendOTPAPI.perform();
		sendOTPResponsePojo = sendOTPAPI
				.getAPIResponseAsPOJO(SendOTPResponsePojo.class);
		
		String message =sendOTPResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	
	@Test
	public void A_05_Test_BlankReqGatewayLoginId() throws Exception {
		final String methodName = "Test_SendOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SENDOTP5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		sendOTPRequestPojo.setReqGatewayLoginId("");
		
		SendOTPAPI sendOTPAPI = new SendOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		sendOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		sendOTPAPI.addBodyParam(sendOTPRequestPojo);
		sendOTPAPI.setExpectedStatusCode(200);
		sendOTPAPI.perform();
		
		sendOTPResponsePojo = sendOTPAPI
				.getAPIResponseAsPOJO(SendOTPResponsePojo.class);
	
		String message =sendOTPResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
		Assertion.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_06_Test_BlankReqGatewayPassword() throws Exception {
		final String methodName = "Test_SendOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SENDOTP6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		sendOTPRequestPojo.setReqGatewayPassword("");
		
		SendOTPAPI sendOTPAPI = new SendOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		sendOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		sendOTPAPI.addBodyParam(sendOTPRequestPojo);
		sendOTPAPI.setExpectedStatusCode(200);
		sendOTPAPI.perform();
		
		sendOTPResponsePojo = sendOTPAPI
				.getAPIResponseAsPOJO(SendOTPResponsePojo.class);
		
		String message =sendOTPResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Invalid Password");
		Assertion.assertEquals(message, "Invalid Password");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}	
	@Test
	public void A_07_Test_BlankServicePort() throws Exception {
		
		final String methodName = "Test_SendOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SENDOTP7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		
		setupData();
		sendOTPRequestPojo.setServicePort("");
	
		SendOTPAPI sendOTPAPI = new SendOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		sendOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		sendOTPAPI.addBodyParam(sendOTPRequestPojo);
		sendOTPAPI.setExpectedStatusCode(200);
		sendOTPAPI.perform();
		
		sendOTPResponsePojo = sendOTPAPI
				.getAPIResponseAsPOJO(SendOTPResponsePojo.class);
		
		String message =sendOTPResponsePojo.getDataObject().getMessage();	
		
		Assert.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
		Assertion.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test
	public void A_08_Test_BlankSourceType() throws Exception {
		
		final String methodName = "Test_SendOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SENDOTP8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		sendOTPRequestPojo.setSourceType("");
		
		SendOTPAPI sendOTPAPI = new SendOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		sendOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		sendOTPAPI.addBodyParam(sendOTPRequestPojo);
		sendOTPAPI.setExpectedStatusCode(200);
		sendOTPAPI.perform();
		
		sendOTPResponsePojo = sendOTPAPI
				.getAPIResponseAsPOJO(SendOTPResponsePojo.class);
		
		String message =sendOTPResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Source Type is blank.");
		Assertion.assertEquals(message, "Source Type is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	
	@Test
	public void A_09_Test_InvalidCount() throws Exception {
		
		final String methodName = "Test_SendOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SENDOTP9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		String maxTimes = DBHandler.AccessHandler.getSystemPreference("OTP_RESEND_TIMES");
		
		
		DBHandler.AccessHandler.updateAnyColumnValue("USER_OTP", "OTP_COUNT", 
				maxTimes, "MSISDN", transfer_Details.get("From_MSISDN"));
		
		SendOTPAPI sendOTPAPI = new SendOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		sendOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		sendOTPAPI.addBodyParam(sendOTPRequestPojo);
		sendOTPAPI.setExpectedStatusCode(200);
		sendOTPAPI.perform();
		
		sendOTPResponsePojo = sendOTPAPI
				.getAPIResponseAsPOJO(SendOTPResponsePojo.class);
		
		int statusCode = Integer.parseInt(sendOTPResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(Integer.toString(statusCode), "206");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	
	
	
	
}
