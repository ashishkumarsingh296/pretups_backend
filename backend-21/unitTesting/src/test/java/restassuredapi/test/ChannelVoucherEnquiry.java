package restassuredapi.test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.channelvoucherenquiry.ChannelVoucherEnquiryAPI;
import restassuredapi.pojo.c2cbuystockinitiateresponsepojo.C2CBuyStockInitiateResponsePojo;
import restassuredapi.pojo.channelvoucherenquiryrequestpojo.ChannelVoucherEnquiryRequestPojo;
import restassuredapi.pojo.channelvoucherenquiryrequestpojo.Data;
import restassuredapi.pojo.channelvoucherenquiryresponsepojo.ChannelVoucherEnquiryResponsePojo;
import restassuredapi.pojo.viewselfcommenquiryresponsepojo.ViewSelfCommEnquiryResponsePojo;

@ModuleManager(name = Module.REST_CHANNEL_VOUCHER_ENQUIRY)
public class ChannelVoucherEnquiry extends BaseTest {
	
	static String moduleCode;
	ChannelVoucherEnquiryRequestPojo channelVoucherEnquiryRequestPojo = new ChannelVoucherEnquiryRequestPojo();
	ChannelVoucherEnquiryResponsePojo channelVoucherEnquiryResponsePojo = new ChannelVoucherEnquiryResponsePojo();
	NetworkAdminHomePage homepage = new NetworkAdminHomePage(driver);
	Data data = new Data();
	
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
        String To_Pin = null;

        for (int i = 1; i <= excelRowSize; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
                ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                To_Pin=ExcelUtility.getCellData(0, ExcelI.PIN, i);
                break;
            }
        }

        for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
                Data[k][0] = alist2.get(j);
                Data[k][1] = alist1.get(j);
                Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                Data[k][3] = ChannelUserMSISDN;
                Data[k][4] = To_Pin;
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
    tranferDetails.put("To_Pin", Data[0][4].toString());
    return tranferDetails;
    
	}
	
	
	
    public void setupdata() {
    	transfer_Details=getExcelData();
    	
		 
		channelVoucherEnquiryRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		channelVoucherEnquiryRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		channelVoucherEnquiryRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		channelVoucherEnquiryRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		channelVoucherEnquiryRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		channelVoucherEnquiryRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setMsisdn(transfer_Details.get("To_MSISDN"));
		data.setPin("6688");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		//DBHandler.AccessHandler.getPinFromSerialNumber(serialNumber)
		data.setLoginid("");
		data.setPassword("");
		data.setExtcode("");
		data.setVouchertype(_masterVO.getProperty("enquiryVoucherType"));
		data.setVouchersegment(_masterVO.getProperty("enquiryVoucherSegment"));
		data.setDenomination(_masterVO.getProperty("enquiryVoucherDenomination"));
		data.setVoucherprofile(_masterVO.getProperty("enquiryVoucherProfile"));
		channelVoucherEnquiryRequestPojo.setData(data);
    }
	//Successful data with valid data.
	@Test
	public void A_01_Test_success() throws Exception
	{
		final String methodName = "Test_ChannelVoucherEnquiryAPI";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTCVENQ1");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupdata();
		ChannelVoucherEnquiryAPI channelVoucherEnquiryAPI=new ChannelVoucherEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		channelVoucherEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		channelVoucherEnquiryAPI.addBodyParam(channelVoucherEnquiryRequestPojo);
		channelVoucherEnquiryAPI.setExpectedStatusCode(200);
		channelVoucherEnquiryAPI.perform();
		channelVoucherEnquiryResponsePojo = channelVoucherEnquiryAPI
				.getAPIResponseAsPOJO(ChannelVoucherEnquiryResponsePojo.class);
		int statusCode = Integer.parseInt(channelVoucherEnquiryResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_02_Test_BlankVoucherType() throws Exception
	{
		final String methodName = "Test_ChannelVoucherEnquiryAPI";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTCVENQ2");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupdata();
		ChannelVoucherEnquiryAPI channelVoucherEnquiryAPI=new ChannelVoucherEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		channelVoucherEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setVouchertype("");
		channelVoucherEnquiryRequestPojo.setData(data);
		
		channelVoucherEnquiryAPI.addBodyParam(channelVoucherEnquiryRequestPojo);
		channelVoucherEnquiryAPI.setExpectedStatusCode(200);
		channelVoucherEnquiryAPI.perform();
		channelVoucherEnquiryResponsePojo =channelVoucherEnquiryAPI.getAPIResponseAsPOJO(ChannelVoucherEnquiryResponsePojo.class);
		
		String message =channelVoucherEnquiryResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "VOUCHERTYPE can not be blank.");
		Assertion.assertEquals(message, "VOUCHERTYPE can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	@Test
	public void A_03_Test_BlankVoucherSegment() throws Exception
	{
		final String methodName = "Test_ChannelVoucherEnquiryAPI";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTCVENQ3");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		
		setupdata();
		ChannelVoucherEnquiryAPI channelVoucherEnquiryAPI=new ChannelVoucherEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		channelVoucherEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setVouchersegment("");
		channelVoucherEnquiryRequestPojo.setData(data);
		
		channelVoucherEnquiryAPI.addBodyParam(channelVoucherEnquiryRequestPojo);
		channelVoucherEnquiryAPI.setExpectedStatusCode(200);
		channelVoucherEnquiryAPI.perform();
		channelVoucherEnquiryResponsePojo =channelVoucherEnquiryAPI.getAPIResponseAsPOJO(ChannelVoucherEnquiryResponsePojo.class);
		String message =channelVoucherEnquiryResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "VOUCHERSEGMENT can not be blank.");
		Assertion.assertEquals(message, "VOUCHERSEGMENT can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	@Test
	public void A_04_Test_BlankDenomination() throws Exception
	{
		final String methodName = "Test_ChannelVoucherEnquiryAPI";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTCVENQ4");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupdata();
		ChannelVoucherEnquiryAPI channelVoucherEnquiryAPI=new ChannelVoucherEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		channelVoucherEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setDenomination("");
		channelVoucherEnquiryRequestPojo.setData(data);
		
		channelVoucherEnquiryAPI.addBodyParam(channelVoucherEnquiryRequestPojo);
		channelVoucherEnquiryAPI.setExpectedStatusCode(200);
		channelVoucherEnquiryAPI.perform();
		channelVoucherEnquiryResponsePojo =channelVoucherEnquiryAPI.getAPIResponseAsPOJO(ChannelVoucherEnquiryResponsePojo.class);
		String message =channelVoucherEnquiryResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "AMOUNT can not be blank.");
		Assertion.assertEquals(message, "AMOUNT can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	@Test
	public void A_05_Test_BlankVoucherProfile() throws Exception
	{
		final String methodName = "Test_ChannelVoucherEnquiryAPI";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTCVENQ5");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupdata();
		ChannelVoucherEnquiryAPI channelVoucherEnquiryAPI=new ChannelVoucherEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		channelVoucherEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setVoucherprofile("");
		channelVoucherEnquiryRequestPojo.setData(data);
		
		channelVoucherEnquiryAPI.addBodyParam(channelVoucherEnquiryRequestPojo);
		channelVoucherEnquiryAPI.setExpectedStatusCode(200);
		channelVoucherEnquiryAPI.perform();
		channelVoucherEnquiryResponsePojo =channelVoucherEnquiryAPI.getAPIResponseAsPOJO(ChannelVoucherEnquiryResponsePojo.class);
		String message =channelVoucherEnquiryResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "200:Successful");
		Assertion.assertEquals(message, "200:Successful");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	@Test
	public void A_06_Test_OptionalLoginIdPassword() throws Exception
	{
		final String methodName = "Test_ChannelVoucherEnquiryAPI";
       Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTCVENQ6");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupdata();
		ChannelVoucherEnquiryAPI channelVoucherEnquiryAPI=new ChannelVoucherEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		channelVoucherEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setLoginid("");
		data.setPassword("");
		channelVoucherEnquiryRequestPojo.setData(data);
		
		channelVoucherEnquiryAPI.addBodyParam(channelVoucherEnquiryRequestPojo);
		channelVoucherEnquiryAPI.setExpectedStatusCode(200);
		channelVoucherEnquiryAPI.perform();
		channelVoucherEnquiryResponsePojo =channelVoucherEnquiryAPI.getAPIResponseAsPOJO(ChannelVoucherEnquiryResponsePojo.class);
		int statusCode =channelVoucherEnquiryResponsePojo.getStatusCode();
		/*String errorMessage = channelVoucherEnquiryResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals("11100",errorMessage);*/
		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	@Test
	public void A_07_Test_BlankGatewayLoginId() throws Exception
	{
		final String methodName = "Test_ChannelVoucherEnquiryAPI";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTCVENQ7");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupdata();
		ChannelVoucherEnquiryAPI channelVoucherEnquiryAPI=new ChannelVoucherEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		channelVoucherEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		
		channelVoucherEnquiryRequestPojo.setReqGatewayLoginId("");
		
		channelVoucherEnquiryAPI.addBodyParam(channelVoucherEnquiryRequestPojo);
		channelVoucherEnquiryAPI.setExpectedStatusCode(200);
		channelVoucherEnquiryAPI.perform();
		channelVoucherEnquiryResponsePojo =channelVoucherEnquiryAPI.getAPIResponseAsPOJO(ChannelVoucherEnquiryResponsePojo.class);

		String message =channelVoucherEnquiryResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
		Assertion.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	@Test
	public void A_08_Test_BlankGatewayPassword() throws Exception
	{
		final String methodName = "Test_ViewSelfCommEnquiryRestAPI";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTCVENQ8");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupdata();
		ChannelVoucherEnquiryAPI channelVoucherEnquiryAPI=new ChannelVoucherEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		channelVoucherEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		
		channelVoucherEnquiryRequestPojo.setReqGatewayPassword("");
		
		channelVoucherEnquiryAPI.addBodyParam(channelVoucherEnquiryRequestPojo);
		channelVoucherEnquiryAPI.setExpectedStatusCode(200);
		channelVoucherEnquiryAPI.perform();
		
		channelVoucherEnquiryResponsePojo =channelVoucherEnquiryAPI.getAPIResponseAsPOJO(ChannelVoucherEnquiryResponsePojo.class);

		String message =channelVoucherEnquiryResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Invalid Password");
		Assertion.assertEquals(message, "Invalid Password");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	@Test
	public void A_09_Test_BlankServicePort() throws Exception
	{
		final String methodName = "Test_ChannelVoucherEnquiryAPI";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTCVENQ9");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupdata();
		ChannelVoucherEnquiryAPI channelVoucherEnquiryAPI=new ChannelVoucherEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		channelVoucherEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		channelVoucherEnquiryRequestPojo.setServicePort("");
		
		channelVoucherEnquiryAPI.addBodyParam(channelVoucherEnquiryRequestPojo);
		channelVoucherEnquiryAPI.setExpectedStatusCode(200);
		channelVoucherEnquiryAPI.perform();
		channelVoucherEnquiryResponsePojo =channelVoucherEnquiryAPI.getAPIResponseAsPOJO(ChannelVoucherEnquiryResponsePojo.class);

		String message =channelVoucherEnquiryResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
		Assertion.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	@Test
	public void A_10_Test_BlankSourceType() throws Exception
	{
		final String methodName = "Test_ChannelVoucherEnquiryAPI";
       Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTCVENQ10");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupdata();
		ChannelVoucherEnquiryAPI channelVoucherEnquiryAPI=new ChannelVoucherEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		channelVoucherEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		
		channelVoucherEnquiryRequestPojo.setSourceType("");
		
		channelVoucherEnquiryAPI.addBodyParam(channelVoucherEnquiryRequestPojo);
		channelVoucherEnquiryAPI.setExpectedStatusCode(200);
		channelVoucherEnquiryAPI.perform();
		channelVoucherEnquiryResponsePojo =channelVoucherEnquiryAPI.getAPIResponseAsPOJO(ChannelVoucherEnquiryResponsePojo.class);

		String message =channelVoucherEnquiryResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Source Type is blank.");
		Assertion.assertEquals(message, "Source Type is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
}
