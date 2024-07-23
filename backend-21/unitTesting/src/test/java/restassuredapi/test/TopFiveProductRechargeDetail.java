package restassuredapi.test;

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
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.topfiveproductrechargedetail.TopFiveProductDetailAPI;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Product;
import restassuredapi.pojo.topfiveproductrechargedetailrequestpojo.Data;
import restassuredapi.pojo.topfiveproductrechargedetailrequestpojo.TopFiveProdductRechargeDetailRequestPojo;
import restassuredapi.pojo.topfiveproductrechargedetailresponsepojo.TopFiveProductRechargeDetailResponsePojo;

@ModuleManager(name = Module.REST_TOP_FIVE_PRODUCT_RECHARGE_DETAIL)
public class TopFiveProductRechargeDetail extends BaseTest{
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     
     Date yesterday = DateUtils.addDays(new Date(), -1);
     Date threeDaysAgo = DateUtils.addDays(new Date(), -3);
     
     String yesterdayDate = df.format(yesterday);
     String threeDaysAgoDateStr = df.format(threeDaysAgo);
     
     String currentDate=df.format(dateobj);   
	
     static String moduleCode;
	
	TopFiveProdductRechargeDetailRequestPojo topFiveProdductRechargeDetailRequestPojo = new TopFiveProdductRechargeDetailRequestPojo();
	TopFiveProductRechargeDetailResponsePojo topFiveProductRechargeDetailResponsePojo = new TopFiveProductRechargeDetailResponsePojo();
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
		topFiveProdductRechargeDetailRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		topFiveProdductRechargeDetailRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		topFiveProdductRechargeDetailRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		topFiveProdductRechargeDetailRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		topFiveProdductRechargeDetailRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		topFiveProdductRechargeDetailRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLoginid("");
		data.setPassword("");
		data.setMsisdn(transfer_Details.get("From_MSISDN"));
		data.setPin(transfer_Details.get("From_Pin"));
		data.setExtcode("");
		data.setLanguage1("");
		data.setLanguage2("");
		data.setFromDate(threeDaysAgoDateStr);
		data.setToDate(yesterdayDate);
		data.setServiceType("RC");
		data.setTopProducts("Y");
		data.setNumberOfProdOrDeno("5");
		topFiveProdductRechargeDetailRequestPojo.setData(data);
	}

	// Successful data with valid data.
	@Test
	public void A_01_Test_success() throws Exception {
		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());

		currentNode.assignCategory("REST");
		setupData();
		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);
		int statusCode = Integer.parseInt(topFiveProductRechargeDetailResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test
	public void A_02_Test_BlankMsisdn() throws Exception {
		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setMsisdn("");
		topFiveProdductRechargeDetailRequestPojo.setData(data);
		
		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);
		

		String message =topFiveProductRechargeDetailResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_03_Test_BlankPin() throws Exception {
		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setPin("");

		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);

		String message =topFiveProductRechargeDetailResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "PIN can not be blank.");
		Assertion.assertEquals(message, "PIN can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_04_Test_BlankExtnwCode() throws Exception {
		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setExtnwcode("");
		topFiveProdductRechargeDetailRequestPojo.setData(data);
		
		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);
		
		String message =topFiveProductRechargeDetailResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	
	@Test
	public void A_05_Test_BlankReqGatewayLoginId() throws Exception {
		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		topFiveProdductRechargeDetailRequestPojo.setReqGatewayLoginId("");
		
		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);
	
		String message =topFiveProductRechargeDetailResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
		Assertion.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_06_Test_BlankReqGatewayPassword() throws Exception {
		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		topFiveProdductRechargeDetailRequestPojo.setReqGatewayPassword("");
		
		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);
		
		String message =topFiveProductRechargeDetailResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Invalid Password");
		Assertion.assertEquals(message, "Invalid Password");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}	
	@Test
	public void A_07_Test_BlankServicePort() throws Exception {
		
		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		
		setupData();
		topFiveProdductRechargeDetailRequestPojo.setServicePort("");
	
		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);
		
		String message =topFiveProductRechargeDetailResponsePojo.getDataObject().getMessage();	
		
		Assert.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
		Assertion.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test
	public void A_08_Test_BlankSourceType() throws Exception {
		
		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		topFiveProdductRechargeDetailRequestPojo.setSourceType("");
		
		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);
		
		String message =topFiveProductRechargeDetailResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Source Type is blank.");
		Assertion.assertEquals(message, "Source Type is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	
	@Test
	public void A_09_Test_InvalidDateTest() throws Exception {

		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setFromDate("10/01/20");
		data.setToDate("01/01/20");
		
		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);
		
		String message =topFiveProductRechargeDetailResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "From Date is greater than to date.");
		Assertion.assertEquals(message, "From Date is greater than to date.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_10_Test_InvalidDateFormatTest() throws Exception {
		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setFromDate("10/01/20234");
		
		topFiveProdductRechargeDetailRequestPojo.setData(data);;
		
		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);
		
		String message =topFiveProductRechargeDetailResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Invalid fromDate date format.");
		Assertion.assertEquals(message, "Invalid fromDate date format.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_11_Test_InvalidNumberOfProducts() throws Exception {
		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE11");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setNumberOfProdOrDeno("AB");
		
		topFiveProdductRechargeDetailRequestPojo.setData(data);
		
		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);
		
		String message =topFiveProductRechargeDetailResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "numberOfProdOrDeno is not number or decimal.");
		Assertion.assertEquals(message, "numberOfProdOrDeno is not number or decimal.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_12_Test_InvalidTopProducts() throws Exception {
		final String methodName = "Test_Top5ProductDetails";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTTOPFIVE12");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setTopProducts("YN");
		
		topFiveProdductRechargeDetailRequestPojo.setData(data);;
		
		TopFiveProductDetailAPI tppFiveProductDetailAPI = new TopFiveProductDetailAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		tppFiveProductDetailAPI.setContentType(_masterVO.getProperty("contentType"));
		tppFiveProductDetailAPI.addBodyParam(topFiveProdductRechargeDetailRequestPojo);
		tppFiveProductDetailAPI.setExpectedStatusCode(200);
		tppFiveProductDetailAPI.perform();
		topFiveProductRechargeDetailResponsePojo = tppFiveProductDetailAPI
				.getAPIResponseAsPOJO(TopFiveProductRechargeDetailResponsePojo.class);
		
		String message =topFiveProductRechargeDetailResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "topProducts should only contain Y or N.");
		Assertion.assertEquals(message, "topProducts should only contain Y or N.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}
