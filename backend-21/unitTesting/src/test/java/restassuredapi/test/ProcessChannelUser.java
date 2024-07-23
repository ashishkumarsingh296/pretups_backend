package restassuredapi.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.channeluserservices.ProcessChannelUserAPI;
import restassuredapi.pojo.processchanneluserrequestpojo.Data;
import restassuredapi.pojo.processchanneluserrequestpojo.ProcessChannelUserRequestPojo;
import restassuredapi.pojo.processchanneluserresponsepojo.ProcessChannelUserResponsePojo;

@ModuleManager(name = Module.REST_PROCESS_CHANNEL_USER)
public class ProcessChannelUser extends BaseTest {

	static String moduleCode;
	ProcessChannelUserRequestPojo processChannelUserRequestPojo = new ProcessChannelUserRequestPojo();
	ProcessChannelUserResponsePojo processChannelUserResponsePojo = new ProcessChannelUserResponsePojo();

	Data processChannelUserRequestData = new Data();

	HashMap<String, String> transfer_Details = new HashMap<String, String>();
	
	public HashMap<String, String> getExcelData() {
		HashMap<String, String> tranferDetails = new HashMap<String, String>();
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

	@BeforeMethod
	public void setupData() {
		transfer_Details=getExcelData();
		
		processChannelUserRequestData.setAmount("104");
		processChannelUserRequestData.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		processChannelUserRequestData.setLanguage1(_masterVO.getProperty("languageCode0"));
		processChannelUserRequestData.setLanguage2(_masterVO.getProperty("languageCode0"));
		processChannelUserRequestData.setMsisdn(transfer_Details.get("From_MSISDN"));
		processChannelUserRequestData.setMsisdn2(transfer_Details.get("To_MSISDN"));
		processChannelUserRequestData.setPin(transfer_Details.get("From_Pin"));
		processChannelUserRequestData
				.setQuantity(DBHandler.AccessHandler.getSystemPreference(CONSTANT.ONLINE_DVD_LIMIT));
		processChannelUserRequestData.setSelector("1");
		processChannelUserRequestData.setVoucherprofile("3208");
		processChannelUserRequestData.setVouchersegment("LC");
		processChannelUserRequestData.setVouchertype("electronic");

		// Added
		processChannelUserRequestData.setLoginid("");
		processChannelUserRequestData.setPassword("");
		processChannelUserRequestData.setExtcode("");

		processChannelUserRequestPojo.setData(processChannelUserRequestData);
		processChannelUserRequestPojo.setReqGatewayCode(_masterVO.getProperty("requestGatewayCode"));
		processChannelUserRequestPojo.setReqGatewayLoginId(_masterVO.getProperty("requestGatewayLoginID"));
		processChannelUserRequestPojo.setReqGatewayPassword(_masterVO.getProperty("requestGatewayPassword"));
		processChannelUserRequestPojo.setReqGatewayType(_masterVO.getProperty("requestGatewayType"));
		processChannelUserRequestPojo.setServicePort(_masterVO.getProperty("servicePort"));
		processChannelUserRequestPojo.setSourceType(_masterVO.getProperty("sourceType"));

	}

	@Test
	public void A_01_Test_processChannelUser_sanity() throws IOException {
		final String methodName = "Test_ProcessChannelUserRestAPI";
		Log.startTestCase(methodName);

		ProcessChannelUserAPI processChannelUserAPI = new ProcessChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		processChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTPCU1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");

		processChannelUserAPI.addBodyParam(processChannelUserRequestPojo);
		processChannelUserAPI.setExpectedStatusCode(200);
		processChannelUserAPI.perform();

		processChannelUserResponsePojo = processChannelUserAPI
				.getAPIResponseAsPOJO(ProcessChannelUserResponsePojo.class);
		int statusCode = processChannelUserResponsePojo.getStatusCode();

		Assert.assertEquals(200, statusCode);
		Log.endTestCase(methodName);
	}

	@Test
	public void A_02_Test_processChannelUser_loginInvalid() throws IOException {
		final String methodName = "Test_ProcessChannelUserRestAPI";
		Log.startTestCase(methodName);

		ProcessChannelUserAPI processChannelUserAPI = new ProcessChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		processChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTPCU2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");

		processChannelUserRequestPojo.setReqGatewayLoginId("pretups11");

		processChannelUserAPI.addBodyParam(processChannelUserRequestPojo);
		processChannelUserAPI.setExpectedStatusCode(200);
		processChannelUserAPI.perform();

		processChannelUserResponsePojo = processChannelUserAPI
				.getAPIResponseAsPOJO(processChannelUserResponsePojo.getClass());

		Assert.assertEquals("206", processChannelUserResponsePojo.getDataObject().getTxnstatus());
		Log.endTestCase(methodName);
	}

	@Test
	public void A_03_Test_processChannelUser_ValidCase_OptionalValues() throws IOException {
		final String methodName = "Test_ProcessChannelUserRestAPI";
		Log.startTestCase(methodName);

		ProcessChannelUserAPI processChannelUserAPI = new ProcessChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		processChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTPCU3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");

		processChannelUserRequestData.setLanguage1("");
		processChannelUserRequestData.setLanguage2("");

		processChannelUserAPI.addBodyParam(processChannelUserRequestPojo);
		processChannelUserAPI.setExpectedStatusCode(200);
		processChannelUserAPI.perform();

		processChannelUserResponsePojo = processChannelUserAPI
				.getAPIResponseAsPOJO(processChannelUserResponsePojo.getClass());

		Assert.assertEquals("200", processChannelUserResponsePojo.getDataObject().getTxnstatus());
		Log.endTestCase(methodName);
	}

	@Test
	public void A_04_Test_processChannelUser_ValidCase_ProfileZero() throws IOException {
		final String methodName = "Test_ProcessChannelUserRestAPI";
		Log.startTestCase(methodName);

		ProcessChannelUserAPI processChannelUserAPI = new ProcessChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		processChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTPCU4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");

		processChannelUserRequestData.setVoucherprofile("0");

		processChannelUserAPI.addBodyParam(processChannelUserRequestPojo);
		processChannelUserAPI.setExpectedStatusCode(200);
		processChannelUserAPI.perform();

		processChannelUserResponsePojo = processChannelUserAPI
				.getAPIResponseAsPOJO(processChannelUserResponsePojo.getClass());

		Assert.assertEquals("200", processChannelUserResponsePojo.getDataObject().getTxnstatus());
		Log.endTestCase(methodName);
	}

	@Test
	public void A_05_Test_processChannelUser_msisdnEmpty() throws IOException {
		final String methodName = "Test_ProcessChannelUserRestAPI";
		Log.startTestCase(methodName);

		ProcessChannelUserAPI processChannelUserAPI = new ProcessChannelUserAPI(
				"http://172.30.24.113:9879/pretups/rest");
		processChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTPCU5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");

		processChannelUserRequestData.setMsisdn("");
		processChannelUserRequestData.setMsisdn2("");

		processChannelUserAPI.addBodyParam(processChannelUserRequestPojo);
		processChannelUserAPI.setExpectedStatusCode(200);
		processChannelUserAPI.perform();

		processChannelUserResponsePojo = processChannelUserAPI
				.getAPIResponseAsPOJO(processChannelUserResponsePojo.getClass());

		Assert.assertEquals("206", processChannelUserResponsePojo.getDataObject().getTxnstatus());
		Log.endTestCase(methodName);
	}

	@Test
	public void A_06_Test_processChannelUser_invalidCount() throws IOException {
		final String methodName = "Test_ProcessChannelUserRestAPI";
		Log.startTestCase(methodName);

		ProcessChannelUserAPI processChannelUserAPI = new ProcessChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		processChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTPCU6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");

		processChannelUserRequestData
				.setQuantity(String.valueOf(Integer.parseInt(processChannelUserRequestData.getQuantity()) + 1));

		processChannelUserAPI.addBodyParam(processChannelUserRequestPojo);
		processChannelUserAPI.setExpectedStatusCode(200);
		processChannelUserAPI.perform();

		processChannelUserResponsePojo = processChannelUserAPI
				.getAPIResponseAsPOJO(processChannelUserResponsePojo.getClass());

		Assert.assertEquals("206", processChannelUserResponsePojo.getDataObject().getTxnstatus());
		Log.endTestCase(methodName);
	}

	@Test
	public void A_07_Test_processChannelUser_amountEmpty() throws IOException {
		final String methodName = "Test_ProcessChannelUserRestAPI";
		Log.startTestCase(methodName);

		ProcessChannelUserAPI processChannelUserAPI = new ProcessChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		processChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTPCU7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");

		processChannelUserRequestData.setAmount("");

		processChannelUserAPI.addBodyParam(processChannelUserRequestPojo);
		processChannelUserAPI.setExpectedStatusCode(200);
		processChannelUserAPI.perform();

		processChannelUserResponsePojo = processChannelUserAPI
				.getAPIResponseAsPOJO(processChannelUserResponsePojo.getClass());

		Assert.assertNotEquals(206, processChannelUserResponsePojo.getDataObject().getTxnstatus());
		Log.endTestCase(methodName);
	}

	@Test
	public void A_08_Test_processChannelUser_pinEmpty() throws IOException {
		final String methodName = "Test_ProcessChannelUserRestAPI";
		Log.startTestCase(methodName);

		ProcessChannelUserAPI processChannelUserAPI = new ProcessChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		processChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTPCU8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");

		processChannelUserRequestData.setPin("");

		processChannelUserAPI.addBodyParam(processChannelUserRequestPojo);
		processChannelUserAPI.setExpectedStatusCode(200);
		processChannelUserAPI.perform();

		processChannelUserResponsePojo = processChannelUserAPI
				.getAPIResponseAsPOJO(processChannelUserResponsePojo.getClass());

		Assert.assertNotEquals(206, processChannelUserResponsePojo.getDataObject().getTxnstatus());
		Log.endTestCase(methodName);
	}

	@Test
	public void A_09_Test_processChannelUser_selectorEmpty() throws IOException {
		final String methodName = "Test_ProcessChannelUserRestAPI";
		Log.startTestCase(methodName);

		ProcessChannelUserAPI processChannelUserAPI = new ProcessChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		processChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTPCU9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");

		processChannelUserRequestData.setSelector("");
		;

		processChannelUserAPI.addBodyParam(processChannelUserRequestPojo);
		processChannelUserAPI.setExpectedStatusCode(200);
		processChannelUserAPI.perform();

		processChannelUserResponsePojo = processChannelUserAPI
				.getAPIResponseAsPOJO(processChannelUserResponsePojo.getClass());

		Assert.assertNotEquals(206, processChannelUserResponsePojo.getDataObject().getTxnstatus());
		Log.endTestCase(methodName);
	}

	@Test
	public void A10_Test_processChannelUser_ValidCase_voucherType() throws IOException {
		final String methodName = "Test_ProcessChannelUserRestAPI";
		Log.startTestCase(methodName);

		ProcessChannelUserAPI processChannelUserAPI = new ProcessChannelUserAPI(
				"http://172.30.24.113:9879/pretups/rest");
		processChannelUserAPI.setContentType("application/json");

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTPCU10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");

		processChannelUserRequestData.setVouchertype("EXEVDRESP");

		processChannelUserAPI.addBodyParam(processChannelUserRequestPojo);
		processChannelUserAPI.setExpectedStatusCode(200);
		processChannelUserAPI.perform();

		processChannelUserResponsePojo = processChannelUserAPI
				.getAPIResponseAsPOJO(processChannelUserResponsePojo.getClass());

		Assert.assertNotEquals(200, processChannelUserResponsePojo.getDataObject().getTxnstatus());
		Log.endTestCase(methodName);
	}
}
