package restassuredapi.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;
import restassuredapi.api.c2sgetamountservicewiselist.C2SGetAmountServiceWiseListAPI;
import restassuredapi.pojo.c2sgetamountservicewiselistrequestpojo.Data;
import restassuredapi.pojo.c2sgetamountservicewiselistresponsepojo.C2SGetAmountServiceWiseListResponsePojo;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Product;
import restassuredapi.pojo.c2sgetamountservicewiselistrequestpojo.C2SGetAmountServiceWiseListRequestPojo;


@ModuleManager(name = Module.REST_C2S_GET_AMOUNT_SERVICE_WISE_LIST)
public class C2SGetAmountServiceWiseList extends BaseTest{	
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     Date yesterday = DateUtils.addDays(new Date(), -1);
     Date threeDaysAgo = DateUtils.addDays(new Date(), -3);
     String currentDate=df.format(dateobj);   
     String yesterdayDate = df.format(yesterday);
     String threeDaysAgoDateStr = df.format(threeDaysAgo);
	static String moduleCode;
	C2SGetAmountServiceWiseListRequestPojo c2SGetAmountServiceWiseListRequestPojo = new C2SGetAmountServiceWiseListRequestPojo();
	C2SGetAmountServiceWiseListResponsePojo c2SGetAmountServiceWiseListResponsePojo = new C2SGetAmountServiceWiseListResponsePojo();
	Data data = new Data();
	
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
		c2SGetAmountServiceWiseListRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		c2SGetAmountServiceWiseListRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		c2SGetAmountServiceWiseListRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		c2SGetAmountServiceWiseListRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		c2SGetAmountServiceWiseListRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		c2SGetAmountServiceWiseListRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLoginid("");
		data.setPassword("");
		data.setMsisdn(transfer_Details.get("From_MSISDN"));
		data.setPin(transfer_Details.get("From_Pin"));
		data.setExtcode("");
		data.setLanguage1(_masterVO.getProperty("languageCode0"));
		data.setLanguage2(_masterVO.getProperty("languageCode0"));
		data.setFromDate(threeDaysAgoDateStr);
		data.setToDate(yesterdayDate);
		c2SGetAmountServiceWiseListRequestPojo.setData(data);

	}

	// Successful data with valid data.
	@Test
	public void A_01_Test_success() throws Exception {
		final String methodName = "Test_C2SGETAMOUNTSERVICEWISELISTAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGASWL1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		C2SGetAmountServiceWiseListAPI c2SGetAmountServiceWiseListAPI = new C2SGetAmountServiceWiseListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2SGetAmountServiceWiseListAPI.setContentType(_masterVO.getProperty("contentType"));
		c2SGetAmountServiceWiseListAPI.addBodyParam(c2SGetAmountServiceWiseListRequestPojo);
		c2SGetAmountServiceWiseListAPI.setExpectedStatusCode(200);
		c2SGetAmountServiceWiseListAPI.perform();
		c2SGetAmountServiceWiseListResponsePojo = c2SGetAmountServiceWiseListAPI
				.getAPIResponseAsPOJO(C2SGetAmountServiceWiseListResponsePojo.class);
		int statusCode = Integer.parseInt(c2SGetAmountServiceWiseListResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test
	public void A_02_Test_BlankMsisdn() throws Exception {
		final String methodName = "Test_C2SGETAMOUNTSERVICEWISELISTAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGASWL2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		C2SGetAmountServiceWiseListAPI c2SGetAmountServiceWiseListAPI = new C2SGetAmountServiceWiseListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2SGetAmountServiceWiseListAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setMsisdn("");
		c2SGetAmountServiceWiseListRequestPojo.setData(data);
		c2SGetAmountServiceWiseListAPI.addBodyParam(c2SGetAmountServiceWiseListRequestPojo);
		c2SGetAmountServiceWiseListAPI.setExpectedStatusCode(200);
		c2SGetAmountServiceWiseListAPI.perform();
		c2SGetAmountServiceWiseListResponsePojo = c2SGetAmountServiceWiseListAPI
				.getAPIResponseAsPOJO(C2SGetAmountServiceWiseListResponsePojo.class);
		String message =c2SGetAmountServiceWiseListResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_03_Test_BlankPin() throws Exception {
	final String methodName = "Test_C2SGETAMOUNTSERVICEWISELISTAPI";
	Log.startTestCase(methodName);

	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGASWL3");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(CaseMaster.getExtentCase());
	currentNode.assignCategory("REST");
	setupData();
	C2SGetAmountServiceWiseListAPI c2SGetAmountServiceWiseListAPI = new C2SGetAmountServiceWiseListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
	c2SGetAmountServiceWiseListAPI.setContentType(_masterVO.getProperty("contentType"));
	data.setPin("");
	c2SGetAmountServiceWiseListRequestPojo.setData(data);
	c2SGetAmountServiceWiseListAPI.addBodyParam(c2SGetAmountServiceWiseListRequestPojo);
	c2SGetAmountServiceWiseListAPI.setExpectedStatusCode(200);
	c2SGetAmountServiceWiseListAPI.perform();
	c2SGetAmountServiceWiseListResponsePojo = c2SGetAmountServiceWiseListAPI
			.getAPIResponseAsPOJO(C2SGetAmountServiceWiseListResponsePojo.class);
	String message =c2SGetAmountServiceWiseListResponsePojo.getDataObject().getMessage();
	
	Assert.assertEquals(message, "PIN can not be blank.");
	Assertion.assertEquals(message, "PIN can not be blank.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
}
	
	@Test
	public void A_04_Test_BlankExtnwCode() throws Exception {
		final String methodName = "Test_C2SGETAMOUNTSERVICEWISELISTAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGASWL4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		C2SGetAmountServiceWiseListAPI c2SGetAmountServiceWiseListAPI = new C2SGetAmountServiceWiseListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2SGetAmountServiceWiseListAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setExtnwcode("");
		c2SGetAmountServiceWiseListRequestPojo.setData(data);
		c2SGetAmountServiceWiseListAPI.addBodyParam(c2SGetAmountServiceWiseListRequestPojo);
		c2SGetAmountServiceWiseListAPI.setExpectedStatusCode(200);
		c2SGetAmountServiceWiseListAPI.perform();
		c2SGetAmountServiceWiseListResponsePojo = c2SGetAmountServiceWiseListAPI
				.getAPIResponseAsPOJO(C2SGetAmountServiceWiseListResponsePojo.class);
		String message =c2SGetAmountServiceWiseListResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_05_Test_BlankReqGatewayLoginId() throws Exception {
		final String methodName = "Test_C2SGETAMOUNTSERVICEWISELISTAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGASWL5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		C2SGetAmountServiceWiseListAPI c2SGetAmountServiceWiseListAPI = new C2SGetAmountServiceWiseListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2SGetAmountServiceWiseListAPI.setContentType(_masterVO.getProperty("contentType"));
		
		c2SGetAmountServiceWiseListRequestPojo.setReqGatewayLoginId("");
		c2SGetAmountServiceWiseListAPI.addBodyParam(c2SGetAmountServiceWiseListRequestPojo);
		c2SGetAmountServiceWiseListAPI.setExpectedStatusCode(200);
		c2SGetAmountServiceWiseListAPI.perform();
		c2SGetAmountServiceWiseListResponsePojo = c2SGetAmountServiceWiseListAPI
				.getAPIResponseAsPOJO(C2SGetAmountServiceWiseListResponsePojo.class);
		String message =c2SGetAmountServiceWiseListResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
		Assertion.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_06_Test_BlankReqGatewayPassword() throws Exception {
		final String methodName = "Test_C2SGETAMOUNTSERVICEWISELISTAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGASWL6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		C2SGetAmountServiceWiseListAPI c2SGetAmountServiceWiseListAPI = new C2SGetAmountServiceWiseListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2SGetAmountServiceWiseListAPI.setContentType(_masterVO.getProperty("contentType"));
		
		c2SGetAmountServiceWiseListRequestPojo.setReqGatewayPassword("");
		c2SGetAmountServiceWiseListAPI.addBodyParam(c2SGetAmountServiceWiseListRequestPojo);
		c2SGetAmountServiceWiseListAPI.setExpectedStatusCode(200);
		c2SGetAmountServiceWiseListAPI.perform();
		c2SGetAmountServiceWiseListResponsePojo = c2SGetAmountServiceWiseListAPI
				.getAPIResponseAsPOJO(C2SGetAmountServiceWiseListResponsePojo.class);
		String message =c2SGetAmountServiceWiseListResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Invalid Password");
		Assertion.assertEquals(message, "Invalid Password");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_07_Test_BlankServicePort() throws Exception {
		final String methodName = "Test_C2SGETAMOUNTSERVICEWISELISTAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGASWL7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		C2SGetAmountServiceWiseListAPI c2SGetAmountServiceWiseListAPI = new C2SGetAmountServiceWiseListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2SGetAmountServiceWiseListAPI.setContentType(_masterVO.getProperty("contentType"));
		
		c2SGetAmountServiceWiseListRequestPojo.setServicePort("");
		c2SGetAmountServiceWiseListAPI.addBodyParam(c2SGetAmountServiceWiseListRequestPojo);
		c2SGetAmountServiceWiseListAPI.setExpectedStatusCode(200);
		c2SGetAmountServiceWiseListAPI.perform();
		c2SGetAmountServiceWiseListResponsePojo = c2SGetAmountServiceWiseListAPI
				.getAPIResponseAsPOJO(C2SGetAmountServiceWiseListResponsePojo.class);
		String message =c2SGetAmountServiceWiseListResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
		Assertion.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test
	public void A_08_Test_BlankSourceType() throws Exception {
		final String methodName = "Test_C2SGETAMOUNTSERVICEWISELISTAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGASWL8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		C2SGetAmountServiceWiseListAPI c2SGetAmountServiceWiseListAPI = new C2SGetAmountServiceWiseListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2SGetAmountServiceWiseListAPI.setContentType(_masterVO.getProperty("contentType"));
		
		c2SGetAmountServiceWiseListRequestPojo.setSourceType("");
		c2SGetAmountServiceWiseListAPI.addBodyParam(c2SGetAmountServiceWiseListRequestPojo);
		c2SGetAmountServiceWiseListAPI.setExpectedStatusCode(200);
		c2SGetAmountServiceWiseListAPI.perform();
		c2SGetAmountServiceWiseListResponsePojo = c2SGetAmountServiceWiseListAPI
				.getAPIResponseAsPOJO(C2SGetAmountServiceWiseListResponsePojo.class);
		String message =c2SGetAmountServiceWiseListResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Source Type is blank.");
		Assertion.assertEquals(message, "Source Type is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_09_Test_InvalidDateTest() throws Exception {
		final String methodName = "Test_C2SGETAMOUNTSERVICEWISELISTAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGASWL9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		C2SGetAmountServiceWiseListAPI c2SGetAmountServiceWiseListAPI = new C2SGetAmountServiceWiseListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2SGetAmountServiceWiseListAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setFromDate("10/01/20");
		data.setToDate("01/01/20");
		c2SGetAmountServiceWiseListRequestPojo.setData(data);;
		c2SGetAmountServiceWiseListAPI.addBodyParam(c2SGetAmountServiceWiseListRequestPojo);
		c2SGetAmountServiceWiseListAPI.setExpectedStatusCode(200);
		c2SGetAmountServiceWiseListAPI.perform();
		c2SGetAmountServiceWiseListResponsePojo = c2SGetAmountServiceWiseListAPI
				.getAPIResponseAsPOJO(C2SGetAmountServiceWiseListResponsePojo.class);
		String message =c2SGetAmountServiceWiseListResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "From Date is greater than to date.");
		Assertion.assertEquals(message, "From Date is greater than to date.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_10_Test_InvalidDateFormatTest() throws Exception {
		final String methodName = "Test_C2SGETAMOUNTSERVICEWISELISTAPI";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGASWL10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		C2SGetAmountServiceWiseListAPI c2SGetAmountServiceWiseListAPI = new C2SGetAmountServiceWiseListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2SGetAmountServiceWiseListAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setFromDate("10/01/2010");
		c2SGetAmountServiceWiseListRequestPojo.setData(data);;
		c2SGetAmountServiceWiseListAPI.addBodyParam(c2SGetAmountServiceWiseListRequestPojo);
		c2SGetAmountServiceWiseListAPI.setExpectedStatusCode(200);
		c2SGetAmountServiceWiseListAPI.perform();
		c2SGetAmountServiceWiseListResponsePojo = c2SGetAmountServiceWiseListAPI
				.getAPIResponseAsPOJO(C2SGetAmountServiceWiseListResponsePojo.class);
		String message =c2SGetAmountServiceWiseListResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Invalid fromDate date format.");
		Assertion.assertEquals(message, "Invalid fromDate date format.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
}
