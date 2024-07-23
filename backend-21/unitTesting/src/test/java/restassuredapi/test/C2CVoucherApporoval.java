//
//package restassuredapi.test;
//
//import java.text.DateFormat; 
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.testng.Assert;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//
//import com.classes.BaseTest;
//
//import com.classes.CaseMaster;
//import com.commons.ExcelI;
//import com.commons.MasterI;
//import com.commons.PretupsI;
//import com.dbrepository.DBHandler;
//import com.reporting.extent.entity.ModuleManager;
//import com.utils.Assertion;
//import com.utils.ExcelUtility;
//import com.utils.Log;
//import com.utils._masterVO;
//import com.utils.constants.Module;
//
//import restassuredapi.api.c2cvouchertransfer.C2CVoucherApprovalAPI;
//import restassuredapi.pojo.c2cvoucherapprovalrequestpojo.C2CVoucherApprovalRequestPojo;
//import restassuredapi.pojo.c2cvoucherapprovalrequestpojo.Data;
//import restassuredapi.pojo.c2cvoucherapprovalrequestpojo.VoucherDetail;
//import restassuredapi.pojo.c2cvoucherapprovalresponsepojo.C2CVoucherApprovalResponsePojo;
//
//@ModuleManager(name = Module.REST_C2C_VOUCHER_APPROVAL)
//public class C2CVoucherApporoval extends BaseTest {
//
//	static String moduleCode;
//	C2CVoucherApprovalRequestPojo c2cVoucherApprovalRequestPojo = new C2CVoucherApprovalRequestPojo();
//	C2CVoucherApprovalResponsePojo c2cVoucherApprovalResponsePojo = new C2CVoucherApprovalResponsePojo();
//	Data data = new Data();
//	VoucherDetail voucher = new VoucherDetail();
//	ArrayList<VoucherDetail> voucherDetails = new ArrayList<VoucherDetail>();
//
//	HashMap<String, String> transfer_Details = new HashMap<String, String>();
//
//	public HashMap<String, String> getExcelData() {
//		HashMap<String, String> tranferDetails = new HashMap<String, String>();
//		String C2CTransferCode = _masterVO.getProperty("C2CBuyVoucherTransferCode");
//		String MasterSheetPath = _masterVO.getProperty("DataProvider");
//		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
//		int rowCount = ExcelUtility.getRowCount();
//
//		ArrayList<String> alist1 = new ArrayList<String>();
//		ArrayList<String> alist2 = new ArrayList<String>();
//		ArrayList<String> categorySize = new ArrayList<String>();
//
//		for (int i = 1; i <= rowCount; i++) {
//			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
//			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
//			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
//			if (aList.contains(C2CTransferCode)) {
//				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
//				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
//				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
//			}
//		}
//		// int rowCount = ExcelUtility.getRowCount();
//		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
//		int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();
//
//		int totalObjectCounter = 0;
//		for (int i = 0; i < alist2.size(); i++) {
//			int categorySizeCounter = 0;
//			for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
//				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(i))) {
//					categorySizeCounter++;
//				}
//			}
//			categorySize.add("" + categorySizeCounter);
//			totalObjectCounter = totalObjectCounter + categorySizeCounter;
//		}
//
//		Object[][] Data = new Object[totalObjectCounter][9];
//
//		for (int j = 0, k = 0; j < alist2.size(); j++) {
//
//			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
//			int excelRowSize = ExcelUtility.getRowCount();
//			String ChannelUserMSISDN = null;
//			for (int i = 1; i <= excelRowSize; i++) {
//				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
//					ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
//					break;
//				}
//			}
//
//			for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
//				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
//					Data[k][0] = alist2.get(j);
//					Data[k][1] = alist1.get(j);
//					Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
//					Data[k][3] = ChannelUserMSISDN;
//					Data[k][4] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, excelCounter);
//					Data[k][5] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
//					Data[k][6] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
//					Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
//					Data[k][8] = excelCounter;
//
//					k++;
//
//				}
//			}
//
//		}
//
//		tranferDetails.put("From_MSISDN", Data[0][2].toString());
//		tranferDetails.put("To_MSISDN", Data[0][3].toString());
//		tranferDetails.put("From_Pin", Data[0][6].toString());
//		return tranferDetails;
//
//	}
//
//	///
//
//	public void setupData() {
//		String transferID = DBHandler.AccessHandler.fetchTransferIdWithStatus("NEW",
//				_masterVO.getProperty("transferTypeVoucher"), PretupsI.C2C);
//		String msisdn=transfer_Details.get("To_MSISDN");
//    	String userId=   DBHandler.AccessHandler.getUserIdFromMsisdn(msisdn);
//    	//String transferId =  DBHandler.AccessHandler.getTransferIdByUserId(userId);
//    	String serialNumber = DBHandler.AccessHandler.getSerialNumberFromStatusAndUserId("EN", userId);
//		
//		transfer_Details = getExcelData();
//		
//		Map<String, String> tempData1 = new HashMap<String, String>();
//		tempData1.put("fromSerialNo", "9901130000000015");
//		tempData1.put("toSerialNo", "9901130000000015");
//	
//		data.setLoginid("");
//		data.setPassword("");
//		data.setLanguage1(_masterVO.getProperty("languageCode0"));
//		data.setLanguage2(_masterVO.getProperty("languageCode0"));
//
//		data.setMsisdn(transfer_Details.get("From_MSISDN"));
//		data.setPin(transfer_Details.get("From_Pin"));
//		data.setRemarks("");
//		data.setStatus("Y");
//		data.setExtcode("");
//		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
//		
//
//		data.setTransferId(transferID);
//		data.setType("C2CVOUCHERAPPROVAL");
//
//		voucher.setFromSerialNum(tempData1.get("fromSerialNo"));
//		voucher.setToSerialNum(tempData1.get("toSerialNo"));
//		voucherDetails.add(voucher);
//		data.setVoucherDetails(voucherDetails);
//
//		c2cVoucherApprovalRequestPojo.setData(data);
//
//	}
//
//	// Successful data with valid data.
//	@Test
//	public void A_01_Test_success() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT1");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//		int statusCode = Integer.parseInt(c2cVoucherApprovalResponsePojo.getDataObject().getTxnstatus());
//
//		Assert.assertEquals(200, statusCode);
//		Assertion.assertEquals(Integer.toString(statusCode), "200");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test
//	public void A_02_Test_BlankMsisdn() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT2");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setMsisdn("");
//		c2cVoucherApprovalRequestPojo.setData(data);
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//
//		Assertion.assertEquals(message,
//				"Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test
//	public void A_03_Test_BlankPin() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT3");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setMsisdn("723000000");
//		data.setPin("");
//		c2cVoucherApprovalRequestPojo.setData(data);
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//
//		Assertion.assertEquals(message, "PIN can not be blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	// even at statu =N trxn is completed
//	@Test
//	public void A_04_Test_StatusN() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT4");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setStatus("N");
//		c2cVoucherApprovalRequestPojo.setData(data);
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//
//		Assertion.assertEquals(message, "Transaction has been completed");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test
//	public void A_05_Test_BlankTransferId() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT5");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setTransferId("");
//		c2cVoucherApprovalRequestPojo.setData(data);
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//
//		Assertion.assertEquals(message, "Transaction ID does not exist with status APPRV1aass.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test
//	public void A_06_Test_BlankExtnCode() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT6");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//
//		data.setExtcode("");
//		c2cVoucherApprovalRequestPojo.setData(data);
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//
//		Assertion.assertEquals(message, "External network code value is blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test
//	public void A_07_Test_BlankRequestGatewayLoginId() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT7");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//
//		c2cVoucherApprovalRequestPojo.setReqGatewayLoginId("");
//
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//
//		Assertion.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test
//	public void A_08_Test_BlankRequestGatewayPassword() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT8");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//
//		c2cVoucherApprovalRequestPojo.setReqGatewayPassword("");
//
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//		Assertion.assertEquals(message, "Invalid Password");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	
//
//	@Test
//	public void A_09_Test_BlankServicePort() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT9");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//
//		c2cVoucherApprovalRequestPojo.setServicePort("");
//
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//		Assertion.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test
//	public void A_10_Test_BlankSourceTpe() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT10");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//
//		c2cVoucherApprovalRequestPojo.setSourceType("");
//
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//		Assertion.assertEquals(message, "Invalid XML Request.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	public void A_11_Test_InvalidMsisdn() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT11");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setMsisdn("554854565");
//		c2cVoucherApprovalRequestPojo.setData(data);
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//		Assertion.assertEquals(message, "Invalid user");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	public void A_12_Test_AlphanumericMsisdn() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT12");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setMsisdn("76543210AD");
//		c2cVoucherApprovalRequestPojo.setData(data);
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//		Assertion.assertEquals(message, "MSISDN is not numeric.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test
//	public void A_13_Test_InvalidRequestGatewayLoginId() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT13");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//
//		c2cVoucherApprovalRequestPojo.setReqGatewayLoginId("afdasfafd");
//
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//		Assertion.assertEquals(message, "Gateway login ID is either blank or incorrect, please enter correct details.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test
//	public void A_14_Test_InvalidRequestGatewayPasswrd() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT14");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//
//		c2cVoucherApprovalRequestPojo.setReqGatewayPassword("afdas");
//
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//		Assertion.assertEquals(message, "Invalid Password");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	// global error
//	@Test
//	public void A_15_Test_InvalidRequestGatewayCode() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT15");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//
//		c2cVoucherApprovalRequestPojo.setReqGatewayCode("asdfasdf");
//
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//		Assertion.assertEquals(message, "Transaction ID does not exist with status APPRV1aass.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test
//	public void A_16_Test_InvalidRequestGatewayTpe() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT16");
//		moduleCode = CaseMaster.getModuleCode();
//		setupData();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//
//		c2cVoucherApprovalRequestPojo.setReqGatewayType("sfsafsasd");
//
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//		Assertion.assertEquals(message, "Invalid request interface, please contact customer care.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test
//	public void A_17_Test_InvalidServicePort() throws Exception {
//		final String methodName = "Test_C2CVoucherApprovalAPI";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVT17");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(CaseMaster.getExtentCase());
//		currentNode.assignCategory("REST");
//		setupData();
//		C2CVoucherApprovalAPI c2CVoucherApprovalAPI = new C2CVoucherApprovalAPI(
//				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
//		c2CVoucherApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//
//		c2cVoucherApprovalRequestPojo.setServicePort("afdasd");
//
//		c2CVoucherApprovalAPI.addBodyParam(c2cVoucherApprovalRequestPojo);
//		c2CVoucherApprovalAPI.setExpectedStatusCode(200);
//		c2CVoucherApprovalAPI.perform();
//		c2cVoucherApprovalResponsePojo = c2CVoucherApprovalAPI
//				.getAPIResponseAsPOJO(C2CVoucherApprovalResponsePojo.class);
//
//		String message = c2cVoucherApprovalResponsePojo.getDataObject().getMessage();
//		Assertion.assertEquals(message, "Service Port is either blank or incorrect, please enter correct details.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//}