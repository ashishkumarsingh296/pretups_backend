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
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.validateotp.ValidateOTPAPI;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Product;
import restassuredapi.pojo.validateotprequestpojo.Data;
import restassuredapi.pojo.validateotprequestpojo.ValidateOtpRequestPojo;
import restassuredapi.pojo.validateotpresponsepojo.ValidateOtpResponsePojo;

@ModuleManager(name = Module.REST_VALIDATE_OTP_DETAIL)
public class ValidateOTPDetail extends BaseTest{
	
	static String moduleCode;
	
	ValidateOtpRequestPojo validateOTPRequestPojo = new ValidateOtpRequestPojo();
	ValidateOtpResponsePojo validateOTPResponsePojo = new ValidateOtpResponsePojo();
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
                Data[k][4] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
                Data[k][5] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, excelCounter);
                Data[k][6] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
                Data[k][8] = excelCounter;
               
                k++;
                
            }
        }

    }
    
    tranferDetails.put("From_MSISDN", Data[0][2].toString());
    tranferDetails.put("To_MSISDN", Data[0][3].toString());
    tranferDetails.put("LOGINID", Data[0][4].toString());
    tranferDetails.put("PASSWORD", Data[0][5].toString());
    tranferDetails.put("From_Pin", Data[0][6].toString());
    return tranferDetails;
    
	}
	
	
	public void setupData() {
		transfer_Details=getExcelData();
		validateOTPRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		validateOTPRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		validateOTPRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		validateOTPRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		validateOTPRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		validateOTPRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLoginid(transfer_Details.get("LOGINID"));
		data.setMsisdn("");
		data.setExtcode("");
		data.setLanguage1("");
		data.setLanguage2("");
		
		
		String otp = DBHandler.AccessHandler.getLatestOTP(transfer_Details.get("From_MSISDN"));
		data.setOtp(Decrypt.decryption(otp));
		
		String pin = new RandomGeneration().randomNumeric(4);
		
		data.setNewpin(pin);
		data.setConfirmpin(pin);
		
		validateOTPRequestPojo.setData(data);
		
		
	}

	@Test
	public void A_00_Test_Pin_Diff() throws Exception {
		
		final String methodName = "Test_ValidateOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VALIDATEOTP10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		
		
		
		setupData();
		validateOTPRequestPojo.getData().setConfirmpin("8888");
		
		ValidateOTPAPI validateOTPAPI = new ValidateOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		validateOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		validateOTPAPI.addBodyParam(validateOTPRequestPojo);
		validateOTPAPI.setExpectedStatusCode(200);
		validateOTPAPI.perform();
		validateOTPResponsePojo = validateOTPAPI
				.getAPIResponseAsPOJO(ValidateOtpResponsePojo.class);
		
		String message =validateOTPResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "New PIN and confirmed PIN should be same");
		Assertion.assertEquals(message, "New PIN and confirmed PIN should be same");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// Successful data with valid data.
	@Test
	public void A_01_Test_success() throws Exception {
		final String methodName = "Test_ValidateOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VALIDATEOTP1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());

		currentNode.assignCategory("REST");
		setupData();
		ValidateOTPAPI validateOTPAPI = new ValidateOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		validateOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		validateOTPAPI.addBodyParam(validateOTPRequestPojo);
		validateOTPAPI.setExpectedStatusCode(200);
		validateOTPAPI.perform();
		validateOTPResponsePojo = validateOTPAPI
				.getAPIResponseAsPOJO(ValidateOtpResponsePojo.class);
		
		int statusCode = Integer.parseInt(validateOTPResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
		DBHandler.AccessHandler.updateAnyColumnValue("USER_PHONES", "SMS_PIN",
				Decrypt.APIEncryption(transfer_Details.get("From_Pin")), "MSISDN", transfer_Details.get("From_MSISDN"));
		
	}
	
	@Test
	public void A_02_Test_BlankLoginId() throws Exception {
		final String methodName = "Test_ValidateOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VALIDATEOTP2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setLoginid("");
		validateOTPRequestPojo.setData(data);
		
		ValidateOTPAPI validateOTPAPI = new ValidateOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		validateOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		validateOTPAPI.addBodyParam(validateOTPRequestPojo);
		validateOTPAPI.setExpectedStatusCode(200);
		validateOTPAPI.perform();
		validateOTPResponsePojo = validateOTPAPI
				.getAPIResponseAsPOJO(ValidateOtpResponsePojo.class);
		

		String message =validateOTPResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test
	public void A_04_Test_BlankExtnwCode() throws Exception {
		final String methodName = "Test_ValidateOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VALIDATEOTP4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setExtnwcode("");
		validateOTPRequestPojo.setData(data);
		
		ValidateOTPAPI validateOTPAPI = new ValidateOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		validateOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		validateOTPAPI.addBodyParam(validateOTPRequestPojo);
		validateOTPAPI.setExpectedStatusCode(200);
		validateOTPAPI.perform();
		validateOTPResponsePojo = validateOTPAPI
				.getAPIResponseAsPOJO(ValidateOtpResponsePojo.class);
		
		String message =validateOTPResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	
	@Test
	public void A_05_Test_BlankReqGatewayLoginId() throws Exception {
		final String methodName = "Test_ValidateOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VALIDATEOTP5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		validateOTPRequestPojo.setReqGatewayLoginId("");
		
		ValidateOTPAPI validateOTPAPI = new ValidateOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		validateOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		validateOTPAPI.addBodyParam(validateOTPRequestPojo);
		validateOTPAPI.setExpectedStatusCode(200);
		validateOTPAPI.perform();
		validateOTPResponsePojo = validateOTPAPI
				.getAPIResponseAsPOJO(ValidateOtpResponsePojo.class);
	
		String message =validateOTPResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
		Assertion.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_06_Test_BlankReqGatewayPassword() throws Exception {
		final String methodName = "Test_ValidateOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VALIDATEOTP6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		validateOTPRequestPojo.setReqGatewayPassword("");
		
		ValidateOTPAPI validateOTPAPI = new ValidateOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		validateOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		validateOTPAPI.addBodyParam(validateOTPRequestPojo);
		validateOTPAPI.setExpectedStatusCode(200);
		validateOTPAPI.perform();
		validateOTPResponsePojo = validateOTPAPI
				.getAPIResponseAsPOJO(ValidateOtpResponsePojo.class);
		
		String message =validateOTPResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Invalid Password");
		Assertion.assertEquals(message, "Invalid Password");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}	
	@Test
	public void A_07_Test_BlankServicePort() throws Exception {
		
		final String methodName = "Test_ValidateOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VALIDATEOTP7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		
		setupData();
		validateOTPRequestPojo.setServicePort("");
	
		ValidateOTPAPI validateOTPAPI = new ValidateOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		validateOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		validateOTPAPI.addBodyParam(validateOTPRequestPojo);
		validateOTPAPI.setExpectedStatusCode(200);
		validateOTPAPI.perform();
		validateOTPResponsePojo = validateOTPAPI
				.getAPIResponseAsPOJO(ValidateOtpResponsePojo.class);
		
		String message =validateOTPResponsePojo.getDataObject().getMessage();	
		
		Assert.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
		Assertion.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test
	public void A_08_Test_BlankSourceType() throws Exception {
		
		final String methodName = "Test_ValidateOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VALIDATEOTP8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		validateOTPRequestPojo.setSourceType("");
		
		ValidateOTPAPI validateOTPAPI = new ValidateOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		validateOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		validateOTPAPI.addBodyParam(validateOTPRequestPojo);
		validateOTPAPI.setExpectedStatusCode(200);
		validateOTPAPI.perform();
		validateOTPResponsePojo = validateOTPAPI
				.getAPIResponseAsPOJO(ValidateOtpResponsePojo.class);
		
		String message =validateOTPResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Source Type is blank.");
		Assertion.assertEquals(message, "Source Type is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	
	@Test
	public void A_09_Test_Otp_Invalid() throws Exception {
		
		final String methodName = "Test_ValidateOTPDetail";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("VALIDATEOTP9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		String otp = validateOTPRequestPojo.getData().getOtp();
		
		validateOTPRequestPojo.getData().setOtp(otp.substring(0, otp.length()-2)+"11");
		
		ValidateOTPAPI validateOTPAPI = new ValidateOTPAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		validateOTPAPI.setContentType(_masterVO.getProperty("contentType"));
		validateOTPAPI.addBodyParam(validateOTPRequestPojo);
		validateOTPAPI.setExpectedStatusCode(200);
		validateOTPAPI.perform();
		validateOTPResponsePojo = validateOTPAPI
				.getAPIResponseAsPOJO(ValidateOtpResponsePojo.class);
		
		String message =validateOTPResponsePojo.getDataObject().getMessage();
		
		String maxTimes = DBHandler.AccessHandler.getSystemPreference("MAX_INVALID_OTP");
		
		int maxTimesLessOne = Integer.parseInt(maxTimes)-1;
		
		Assert.assertEquals(message, "Otp is invalid, "+String.valueOf(maxTimesLessOne)+" attempt(s) left.");
		Assertion.assertEquals(message, "Otp is invalid, "+String.valueOf(maxTimesLessOne)+" attempt(s) left.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	

	
	
	
	
}
