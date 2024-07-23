package restassuredapi.test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.BTSLDateUtil;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.o2cinitiate.O2CInitiateAPI;
import restassuredapi.api.selfregistrationapi.SelfRegistrationAPI;
import restassuredapi.pojo.o2cinitiateresponsepojo.O2CInitiateResponsePojo;
import restassuredapi.pojo.selfregistrationresponsepojo.SelfRegistrationResponse;


@ModuleManager(name = Module.O2C_INITIATE_API)
public class O2CInitiate extends BaseTest {
	
	static String moduleCode;
	String[] details;
	
	O2CInitiateResponsePojo o2CInitiateResponsePojo = new O2CInitiateResponsePojo();
	//GetChannelUsersListAPI getChannelUsersListAPI=new GetChannelUsersListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
	HashMap<String,String> transfer_Details=new HashMap<String,String>(); 
	HashMap<String, String> returnMap = new HashMap<String, String>();
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
        String ChannelUserLogin = null;
        String ChannelUserPassword = null;
        
        for (int i = 1; i <= excelRowSize; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
                ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                ChannelUserLogin = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                ChannelUserPassword = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                break;
            }
        }

        for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
                Data[k][0] = alist2.get(j);
                Data[k][1] = alist1.get(j);
                Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                Data[k][3] = ChannelUserMSISDN;
                Data[k][4] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                Data[k][5] = ChannelUserLogin;
                Data[k][6] = ExcelUtility.getCellData(0, ExcelI.USER_NAME, excelCounter);
                Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter);
                Data[k][8] = excelCounter;
               
                k++;
                
            }
        }

    }
    
    tranferDetails.put("Login_Id", Data[0][5].toString());
    tranferDetails.put("catCode", Data[0][7].toString());
    tranferDetails.put("Msisdn", Data[0][2].toString());
    tranferDetails.put("userName", Data[0][6].toString());
    tranferDetails.put("pin", Data[0][4].toString());
    
    
    return tranferDetails;
    
	}
	
	public void setupData() throws ParseException
	{	transfer_Details=getExcelData();
	    //list of coloumns whose vaues we want to get from db
		String[] colNames= {"IMEI","MHASH","TOKEN","SMS_PIN"};
		
		Date currentDate = new Date();
		
		String date1= BTSLDateUtil.getDateStringFromDate(currentDate, "yyyy-MM-dd HH:mm:ss");
		Date date=BTSLDateUtil.getDateFromDateString(date1, "yyyy-MM-dd HH:mm:ss");
		//inserting token,imei,mhash,token_last_used
		DBHandler.AccessHandler.updateAnyColumnValue("USER_PHONES", "IMEI",_masterVO.getProperty("imei"), "MSISDN", transfer_Details.get("Msisdn"));
		DBHandler.AccessHandler.updateAnyColumnValue("USER_PHONES", "MHASH",_masterVO.getProperty("mhash"), "MSISDN", transfer_Details.get("Msisdn"));
		DBHandler.AccessHandler.updateAnyColumnValue("USER_PHONES", "TOKEN",_masterVO.getProperty("token"), "MSISDN", transfer_Details.get("Msisdn"));
		DBHandler.AccessHandler.updateAnyColumnDateValue("USER_PHONES", "TOKEN_LASTUSED_DATE",date, "MSISDN", transfer_Details.get("Msisdn"));

		details=DBHandler.AccessHandler.getUserDetailsFromUserPhones(transfer_Details.get("Msisdn"),colNames);
		
	}
	
	
	@Test
	@TestManager(TestKey="PRETUPS-6289")
	public void A_01_Test_Success() throws Exception {

		final String methodName = "A_01_Test_Success";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINI1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		setupData();
		transfer_Details=getExcelData();
		O2CInitiateAPI o2CInitiateAPI = new O2CInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CInitiateAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CInitiateAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CInitiateAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CInitiateAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CInitiateAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CInitiateAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CInitiateAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "O2CINICU");
		returnMap.put("MSISDN", transfer_Details.get("Msisdn"));
		returnMap.put("IMEI", details[0]);
		
		returnMap.put("PIN",  Decrypt.decryption(details[3]));
		returnMap.put("LANGUAGE1", _masterVO.getProperty("languageCode0"));
		returnMap.put("MHASH", details[1]);
		returnMap.put("TOKEN",details[2] );
		returnMap.put("PRODUCTS","10:101" );
		returnMap.put("TRFCATEGORY","SALE" );
		o2CInitiateAPI.setBodyParam(returnMap);
		o2CInitiateAPI.setExpectedStatusCode(200);
		o2CInitiateAPI.perform();

		o2CInitiateResponsePojo = o2CInitiateAPI.getAPIResponseAsPOJO(O2CInitiateResponsePojo.class);

		long statusCode = Long.valueOf(o2CInitiateResponsePojo.getTxnStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6290")
	public void A_02_Test_InvalidPin() throws Exception {

		final String methodName = "A_02_Test_InvalidPin";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINI2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		setupData();
		transfer_Details=getExcelData();
		O2CInitiateAPI o2CInitiateAPI = new O2CInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CInitiateAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CInitiateAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CInitiateAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CInitiateAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CInitiateAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CInitiateAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CInitiateAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "O2CINICU");
		returnMap.put("MSISDN", transfer_Details.get("Msisdn"));
		returnMap.put("IMEI", details[0]);
		returnMap.put("PIN", "1785");
		returnMap.put("LANGUAGE1", _masterVO.getProperty("languageCode0"));
		returnMap.put("MHASH", details[1]);
		returnMap.put("TOKEN",details[2] );
		returnMap.put("PRODUCTS","10:101" );
		returnMap.put("TRFCATEGORY","SALE" );
		o2CInitiateAPI.setBodyParam(returnMap);
		o2CInitiateAPI.setExpectedStatusCode(200);
		o2CInitiateAPI.perform();

		o2CInitiateResponsePojo = o2CInitiateAPI.getAPIResponseAsPOJO(O2CInitiateResponsePojo.class);

		long statusCode = Long.valueOf(o2CInitiateResponsePojo.getTxnStatus());
		String message=o2CInitiateResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid Pin.");
		Assertion.assertEquals(message, "Invalid Pin.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}
		
	@Test
	@TestManager(TestKey="PRETUPS-6291")
	public void A_03_Test_InvalidMsisdn() throws Exception {

		final String methodName = "A_03_Test_InvalidMsisdn";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINI3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		setupData();
		transfer_Details=getExcelData();
		O2CInitiateAPI o2CInitiateAPI = new O2CInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CInitiateAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CInitiateAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CInitiateAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CInitiateAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CInitiateAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CInitiateAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CInitiateAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "O2CINICU");
		returnMap.put("MSISDN", "12344321");
		returnMap.put("IMEI", details[0]);
		returnMap.put("PIN",  Decrypt.decryption(details[3]));
		returnMap.put("LANGUAGE1", _masterVO.getProperty("languageCode0"));
		returnMap.put("MHASH", details[1]);
		returnMap.put("TOKEN",details[2] );
		returnMap.put("PRODUCTS","10:101" );
		returnMap.put("TRFCATEGORY","SALE" );
		o2CInitiateAPI.setBodyParam(returnMap);
		o2CInitiateAPI.setExpectedStatusCode(200);
		o2CInitiateAPI.perform();

		o2CInitiateResponsePojo = o2CInitiateAPI.getAPIResponseAsPOJO(O2CInitiateResponsePojo.class);

		long statusCode = Long.valueOf(o2CInitiateResponsePojo.getTxnStatus());
		String message=o2CInitiateResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid user.");
		Assertion.assertEquals(message, "Invalid user.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6292")
	public void A_04_Test_InvalidTrfcategory() throws Exception {

		final String methodName = "A_04_Test_InvalidTrfcategory";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINI4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		setupData();
		transfer_Details=getExcelData();
		O2CInitiateAPI o2CInitiateAPI = new O2CInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CInitiateAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CInitiateAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CInitiateAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CInitiateAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CInitiateAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CInitiateAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CInitiateAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "O2CINICU");
		returnMap.put("MSISDN", transfer_Details.get("Msisdn"));
		returnMap.put("IMEI", details[0]);
		returnMap.put("PIN",  Decrypt.decryption(details[3]));
		returnMap.put("LANGUAGE1", _masterVO.getProperty("languageCode0"));
		returnMap.put("MHASH", details[1]);
		returnMap.put("TOKEN",details[2] );
		returnMap.put("PRODUCTS","10:101" );
		returnMap.put("TRFCATEGORY","SAL" );
		o2CInitiateAPI.setBodyParam(returnMap);
		o2CInitiateAPI.setExpectedStatusCode(200);
		o2CInitiateAPI.perform();

		o2CInitiateResponsePojo = o2CInitiateAPI.getAPIResponseAsPOJO(O2CInitiateResponsePojo.class);

		long statusCode = Long.valueOf(o2CInitiateResponsePojo.getTxnStatus());
		String message=o2CInitiateResponsePojo.getMessage();
		Assert.assertEquals(message, "Transfer category not allowed.");
		Assertion.assertEquals(message, "Transfer category not allowed.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6293")
	public void A_05_Test_InvalidAmount() throws Exception {

		final String methodName = "A_05_Test_InvalidAmount";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINI5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		setupData();
		transfer_Details=getExcelData();
		O2CInitiateAPI o2CInitiateAPI = new O2CInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CInitiateAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CInitiateAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CInitiateAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CInitiateAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CInitiateAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CInitiateAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CInitiateAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "O2CINICU");
		returnMap.put("MSISDN", transfer_Details.get("Msisdn"));
		returnMap.put("IMEI", details[0]);
		returnMap.put("PIN",  Decrypt.decryption(details[3]));
		returnMap.put("LANGUAGE1", _masterVO.getProperty("languageCode0"));
		returnMap.put("MHASH", details[1]);
		returnMap.put("TOKEN",details[2] );
		returnMap.put("PRODUCTS","0:101" );
		returnMap.put("TRFCATEGORY","SALE" );
		o2CInitiateAPI.setBodyParam(returnMap);
		o2CInitiateAPI.setExpectedStatusCode(200);
		o2CInitiateAPI.perform();

		o2CInitiateResponsePojo = o2CInitiateAPI.getAPIResponseAsPOJO(O2CInitiateResponsePojo.class);

		long statusCode = Long.valueOf(o2CInitiateResponsePojo.getTxnStatus());
		String message=o2CInitiateResponsePojo.getMessage();
		Assert.assertEquals(message, "Commission slab is not defined for product and requested quantity eTopUP:0.");
		Assertion.assertEquals(message, "Commission slab is not defined for product and requested quantity eTopUP:0.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}
	
	
	@Test
	@TestManager(TestKey="PRETUPS-6294")
	public void A_06_Test_InvalidProductCode() throws Exception {

		final String methodName = "A_06_Test_InvalidAmount";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINI6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		setupData();
		transfer_Details=getExcelData();
		O2CInitiateAPI o2CInitiateAPI = new O2CInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CInitiateAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CInitiateAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CInitiateAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CInitiateAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CInitiateAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CInitiateAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CInitiateAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "O2CINICU");
		returnMap.put("MSISDN", transfer_Details.get("Msisdn"));
		returnMap.put("IMEI", details[0]);
		returnMap.put("PIN",  Decrypt.decryption(details[3]));
		returnMap.put("LANGUAGE1", _masterVO.getProperty("languageCode0"));
		returnMap.put("MHASH", details[1]);
		returnMap.put("TOKEN",details[2] );
		returnMap.put("PRODUCTS","10:111" );
		returnMap.put("TRFCATEGORY","SALE" );
		o2CInitiateAPI.setBodyParam(returnMap);
		o2CInitiateAPI.setExpectedStatusCode(200);
		o2CInitiateAPI.perform();

		o2CInitiateResponsePojo = o2CInitiateAPI.getAPIResponseAsPOJO(O2CInitiateResponsePojo.class);

		long statusCode = Long.valueOf(o2CInitiateResponsePojo.getTxnStatus());
		String message=o2CInitiateResponsePojo.getMessage();
		Assert.assertEquals(message, "Product entered is not a valid product.");
		Assert.assertEquals(message, "Product entered is not a valid product.");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6295")
	public void A_07_Test_InvalidProductCodeFormat() throws Exception {

		final String methodName = "A_07_Test_InvalidProductCodeFormat";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINI7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		setupData();
		transfer_Details=getExcelData();
		O2CInitiateAPI o2CInitiateAPI = new O2CInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CInitiateAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CInitiateAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CInitiateAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CInitiateAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CInitiateAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CInitiateAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CInitiateAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "O2CINICU");
		returnMap.put("MSISDN", transfer_Details.get("Msisdn"));
		returnMap.put("IMEI", details[0]);
		returnMap.put("PIN",  Decrypt.decryption(details[3]));
		returnMap.put("LANGUAGE1", _masterVO.getProperty("languageCode0"));
		returnMap.put("MHASH", details[1]);
		returnMap.put("TOKEN",details[2] );
		returnMap.put("PRODUCTS","10:111,10" );
		returnMap.put("TRFCATEGORY","SALE" );
		o2CInitiateAPI.setBodyParam(returnMap);
		o2CInitiateAPI.setExpectedStatusCode(200);
		o2CInitiateAPI.perform();

		o2CInitiateResponsePojo = o2CInitiateAPI.getAPIResponseAsPOJO(O2CInitiateResponsePojo.class);

		long statusCode = Long.valueOf(o2CInitiateResponsePojo.getTxnStatus());
		String message=o2CInitiateResponsePojo.getMessage();
		Assert.assertEquals(message, "Product(s) are given in wrong format.");
			
		Assert.assertEquals(message, "Product(s) are given in wrong format.");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}
	
	
	@Test
	@TestManager(TestKey="PRETUPS-6296")
	public void A_08_Test_InvalidToken() throws Exception {

		final String methodName = "A_08_Test_InvalidToken";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINI8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		setupData();
		transfer_Details=getExcelData();
		O2CInitiateAPI o2CInitiateAPI = new O2CInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CInitiateAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CInitiateAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CInitiateAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CInitiateAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CInitiateAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CInitiateAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CInitiateAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "O2CINICU");
		returnMap.put("MSISDN", transfer_Details.get("Msisdn"));
		returnMap.put("IMEI", details[0]);
		returnMap.put("PIN",  Decrypt.decryption(details[3]));
		returnMap.put("LANGUAGE1", _masterVO.getProperty("languageCode0"));
		returnMap.put("MHASH", details[1]);
		returnMap.put("TOKEN","1234567" );
		returnMap.put("PRODUCTS","10:111" );
		returnMap.put("TRFCATEGORY","SALE" );
		o2CInitiateAPI.setBodyParam(returnMap);
		o2CInitiateAPI.setExpectedStatusCode(200);
		o2CInitiateAPI.perform();

		o2CInitiateResponsePojo = o2CInitiateAPI.getAPIResponseAsPOJO(O2CInitiateResponsePojo.class);

		long statusCode = Long.valueOf(o2CInitiateResponsePojo.getTxnStatus());
		String message=o2CInitiateResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid Token.");
			
		Assert.assertEquals(message, "Invalid Token.");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6297")
	public void A_09_Test_InvalidImei() throws Exception {

		final String methodName = "A_09_Test_InvalidIMEI";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINI9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		setupData();
		transfer_Details=getExcelData();
		O2CInitiateAPI o2CInitiateAPI = new O2CInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CInitiateAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CInitiateAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CInitiateAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CInitiateAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CInitiateAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CInitiateAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CInitiateAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "O2CINICU");
		returnMap.put("MSISDN", transfer_Details.get("Msisdn"));
		returnMap.put("IMEI", "1234");
		returnMap.put("PIN",  Decrypt.decryption(details[3]));
		returnMap.put("LANGUAGE1", _masterVO.getProperty("languageCode0"));
		returnMap.put("MHASH", details[1]);
		returnMap.put("TOKEN",details[2] );
		returnMap.put("PRODUCTS","10:111" );
		returnMap.put("TRFCATEGORY","SALE" );
		o2CInitiateAPI.setBodyParam(returnMap);
		o2CInitiateAPI.setExpectedStatusCode(200);
		o2CInitiateAPI.perform();

		o2CInitiateResponsePojo = o2CInitiateAPI.getAPIResponseAsPOJO(O2CInitiateResponsePojo.class);

		long statusCode = Long.valueOf(o2CInitiateResponsePojo.getTxnStatus());
		String message=o2CInitiateResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid IMEI.");
			
		Assert.assertEquals(message, "Invalid IMEI.");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-6298")
	public void A_10_Test_InvalidMhash() throws Exception {

		final String methodName = "A_09_Test_InvalidMhash";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINI10");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("MAPPGW");
		setupData();
		transfer_Details=getExcelData();
		O2CInitiateAPI o2CInitiateAPI = new O2CInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL));

		o2CInitiateAPI.setContentType(_masterVO.getProperty("contentTypeMapp"));
		o2CInitiateAPI.setRequestGateCode(_masterVO.getProperty("requestGatewayCodeMapp"));
		o2CInitiateAPI.setRequestGateType(_masterVO.getProperty("requestGatewayTypeMapp"));
		o2CInitiateAPI.setLogin(_masterVO.getProperty("requestGatewayLoginIDMapp"));
		o2CInitiateAPI.setPassword(_masterVO.getProperty("requestGatewayPasswordMapp"));
		o2CInitiateAPI.setServicePort(_masterVO.getProperty("servicePortMapp"));
		o2CInitiateAPI.setSourceType(_masterVO.getProperty("sourceTypeMapp"));
		returnMap.put("TYPE", "O2CINICU");
		returnMap.put("MSISDN", transfer_Details.get("Msisdn"));
		returnMap.put("IMEI", details[0]);
		returnMap.put("PIN",  Decrypt.decryption(details[3]));
		returnMap.put("LANGUAGE1", _masterVO.getProperty("languageCode0"));
		returnMap.put("MHASH", "12345");
		returnMap.put("TOKEN",details[2] );
		returnMap.put("PRODUCTS","10:111" );
		returnMap.put("TRFCATEGORY","SALE" );
		o2CInitiateAPI.setBodyParam(returnMap);
		o2CInitiateAPI.setExpectedStatusCode(200);
		o2CInitiateAPI.perform();

		o2CInitiateResponsePojo = o2CInitiateAPI.getAPIResponseAsPOJO(O2CInitiateResponsePojo.class);

		long statusCode = Long.valueOf(o2CInitiateResponsePojo.getTxnStatus());
		String message=o2CInitiateResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid MHASH.");
			
		Assert.assertEquals(message, "Invalid MHASH.");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}
	

		

	

}
