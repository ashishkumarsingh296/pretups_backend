package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.passbookOthersDownloadAPI.PassbookOthersDownloadApi;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.passbookOthersDownloadRequestPojo.Data;
import restassuredapi.pojo.passbookOthersDownloadRequestPojo.DisplayHeaderColumnList;
import restassuredapi.pojo.passbookOthersDownloadRequestPojo.PassbookOthersDownloadRequestPojo;
import restassuredapi.pojo.passbookOthersDownloadResponsePojo.PassbookOthersDownloadResponsePojo;

@ModuleManager(name = Module.PASSBOOK_OTHERS_DOWNLOAD)
public class PassbookOthersDownload extends BaseTest {

	static String moduleCode;

	Data data = new Data();
	List<DisplayHeaderColumnList> dispHeaderColumnList;
	PassbookOthersDownloadRequestPojo passbookOthersDownloadRequestPojo = new PassbookOthersDownloadRequestPojo();
	PassbookOthersDownloadResponsePojo passbookOthersDownloadResponsePojo = new PassbookOthersDownloadResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount() - 4;

		Object[][] Data = new Object[rowCount][8];
		int j = 0;
		for (int i = 1; i <= rowCount; i++) {
			Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			Data[j][7] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i);
			j++;
		}
		return Data;
	}

	Map<String, Object> headerMap = new HashMap<String, Object>();

	public void setHeaders() {
		headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
		headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
		headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
		headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
		headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
		headerMap.put("requestGatewayType", _masterVO.getProperty("requestGatewayType"));
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
	}

	public void setupData() {

		DisplayHeaderColumnList displayHeaderColumnList = new DisplayHeaderColumnList();
		displayHeaderColumnList.setColumnName("transDate");
		displayHeaderColumnList.setDisplayName("Transaction Date");

		DisplayHeaderColumnList displayHeaderColumnList1 = new DisplayHeaderColumnList();
		displayHeaderColumnList1.setColumnName("productName");
		displayHeaderColumnList1.setDisplayName("Product name");

		DisplayHeaderColumnList displayHeaderColumnList2 = new DisplayHeaderColumnList();
		displayHeaderColumnList2.setColumnName("userName");
		displayHeaderColumnList2.setDisplayName("User name");

		DisplayHeaderColumnList displayHeaderColumnList3 = new DisplayHeaderColumnList();
		displayHeaderColumnList3.setColumnName("userMobileNumber");
		displayHeaderColumnList3.setDisplayName("User mobile number");

		DisplayHeaderColumnList displayHeaderColumnList4 = new DisplayHeaderColumnList();
		displayHeaderColumnList4.setColumnName("userGeography");
		displayHeaderColumnList4.setDisplayName("User geography");

		DisplayHeaderColumnList displayHeaderColumnList5 = new DisplayHeaderColumnList();
		displayHeaderColumnList5.setColumnName("userCategory");
		displayHeaderColumnList5.setDisplayName("User category");

		DisplayHeaderColumnList displayHeaderColumnList6 = new DisplayHeaderColumnList();
		displayHeaderColumnList6.setColumnName("externalCode");
		displayHeaderColumnList6.setDisplayName("External code");

		DisplayHeaderColumnList displayHeaderColumnList7 = new DisplayHeaderColumnList();
		displayHeaderColumnList7.setColumnName("parentName");
		displayHeaderColumnList7.setDisplayName("Parent name");

		DisplayHeaderColumnList displayHeaderColumnList8 = new DisplayHeaderColumnList();
		displayHeaderColumnList8.setColumnName("parentMobilenumber");
		displayHeaderColumnList8.setDisplayName("Parent mobile number");

		DisplayHeaderColumnList displayHeaderColumnList9 = new DisplayHeaderColumnList();
		displayHeaderColumnList9.setColumnName("parentCategory");
		displayHeaderColumnList9.setDisplayName("Parent category");

		DisplayHeaderColumnList displayHeaderColumnList10 = new DisplayHeaderColumnList();
		displayHeaderColumnList10.setColumnName("parentGeography");
		displayHeaderColumnList10.setDisplayName("Parent Geography");

		DisplayHeaderColumnList displayHeaderColumnList11 = new DisplayHeaderColumnList();
		displayHeaderColumnList11.setColumnName("ownerName");
		displayHeaderColumnList11.setDisplayName("Owner name");

		DisplayHeaderColumnList displayHeaderColumnList12 = new DisplayHeaderColumnList();
		displayHeaderColumnList12.setColumnName("ownerMobileNumber");
		displayHeaderColumnList12.setDisplayName("Owner mobile number");

		DisplayHeaderColumnList displayHeaderColumnList13 = new DisplayHeaderColumnList();
		displayHeaderColumnList13.setColumnName("ownerCategory");
		displayHeaderColumnList13.setDisplayName("Owner category");

		DisplayHeaderColumnList displayHeaderColumnList14 = new DisplayHeaderColumnList();
		displayHeaderColumnList14.setColumnName("ownerGeography");
		displayHeaderColumnList14.setDisplayName("Owner geography");

		DisplayHeaderColumnList displayHeaderColumnList15 = new DisplayHeaderColumnList();
		displayHeaderColumnList15.setColumnName("openingBalance");
		displayHeaderColumnList15.setDisplayName("Opening Balance");

		DisplayHeaderColumnList displayHeaderColumnList16 = new DisplayHeaderColumnList();
		displayHeaderColumnList16.setColumnName("o2cTransferCount");
		displayHeaderColumnList16.setDisplayName("Operator to channel transfer count");

		DisplayHeaderColumnList displayHeaderColumnList17 = new DisplayHeaderColumnList();
		displayHeaderColumnList17.setColumnName("o2cTransferAmount");
		displayHeaderColumnList17.setDisplayName("Operator to channel transfer amount");

		DisplayHeaderColumnList displayHeaderColumnList18 = new DisplayHeaderColumnList();
		displayHeaderColumnList18.setColumnName("o2cReturnCount");
		displayHeaderColumnList18.setDisplayName("Operator to channel return count");

		DisplayHeaderColumnList displayHeaderColumnList19 = new DisplayHeaderColumnList();
		displayHeaderColumnList19.setColumnName("o2cReturnAmount");
		displayHeaderColumnList19.setDisplayName("Operator to channel return amount");

		DisplayHeaderColumnList displayHeaderColumnList20 = new DisplayHeaderColumnList();
		displayHeaderColumnList20.setColumnName("o2cWithdrawCount");
		displayHeaderColumnList20.setDisplayName("Operator to channel withdrawal count ");

		DisplayHeaderColumnList displayHeaderColumnList21 = new DisplayHeaderColumnList();
		displayHeaderColumnList21.setColumnName("o2cWithdrawAmount");
		displayHeaderColumnList21.setDisplayName("Operator to channel withdrawal amount");

		DisplayHeaderColumnList displayHeaderColumnList22 = new DisplayHeaderColumnList();
		displayHeaderColumnList22.setColumnName("c2cTransfer_InCount");
		displayHeaderColumnList22.setDisplayName("Channel to channel transfer in count");

		DisplayHeaderColumnList displayHeaderColumnList23 = new DisplayHeaderColumnList();
		displayHeaderColumnList23.setColumnName("c2cTransfer_InAmount");
		displayHeaderColumnList23.setDisplayName("Channel to channel transfer in amount");

		DisplayHeaderColumnList displayHeaderColumnList24 = new DisplayHeaderColumnList();
		displayHeaderColumnList24.setColumnName("c2cTransfer_OutCount");
		displayHeaderColumnList24.setDisplayName("Channel to channel transfer out count");

		DisplayHeaderColumnList displayHeaderColumnList25 = new DisplayHeaderColumnList();
		displayHeaderColumnList25.setColumnName("c2cTransfer_OutAmount");
		displayHeaderColumnList25.setDisplayName("Channel to channel transfer out amount");

		DisplayHeaderColumnList displayHeaderColumnList26 = new DisplayHeaderColumnList();
		displayHeaderColumnList26.setColumnName("c2cTransferRet_InCount");
		displayHeaderColumnList26.setDisplayName("Channel to channel transfer return  in count");

		DisplayHeaderColumnList displayHeaderColumnList27 = new DisplayHeaderColumnList();
		displayHeaderColumnList27.setColumnName("c2cTransferRet_InAmount");
		displayHeaderColumnList27.setDisplayName("Channel to channel transfer return in amount");

		DisplayHeaderColumnList displayHeaderColumnList28 = new DisplayHeaderColumnList();
		displayHeaderColumnList28.setColumnName("c2cTransferRet_OUTCount");
		displayHeaderColumnList28.setDisplayName("Channel to channel transfer return out count");

		DisplayHeaderColumnList displayHeaderColumnList29 = new DisplayHeaderColumnList();
		displayHeaderColumnList29.setColumnName("c2cTransferRet_OUTAmount");
		displayHeaderColumnList29.setDisplayName("Channel to channel transfer return out amount");

		DisplayHeaderColumnList displayHeaderColumnList30 = new DisplayHeaderColumnList();
		displayHeaderColumnList30.setColumnName("c2cTransferWithdraw_InCount");
		displayHeaderColumnList30.setDisplayName("Channel to channel transfer withdraw in count");

		DisplayHeaderColumnList displayHeaderColumnList31 = new DisplayHeaderColumnList();
		displayHeaderColumnList31.setColumnName("c2cTransferWithdraw_InAmount");
		displayHeaderColumnList31.setDisplayName("Channel to channel transfer withdraw in amount");

		DisplayHeaderColumnList displayHeaderColumnList32 = new DisplayHeaderColumnList();
		displayHeaderColumnList32.setColumnName("c2cTransferWithdraw_OutCount");
		displayHeaderColumnList32.setDisplayName("Channel to channel transfer withdraw out count");

		DisplayHeaderColumnList displayHeaderColumnList33 = new DisplayHeaderColumnList();
		displayHeaderColumnList33.setColumnName("c2cTransferWithdraw_OutAmount");
		displayHeaderColumnList33.setDisplayName("Channel to channel transfer withdraw out amount");

		DisplayHeaderColumnList displayHeaderColumnList34 = new DisplayHeaderColumnList();
		displayHeaderColumnList34.setColumnName("c2sTransfer_count");
		displayHeaderColumnList34.setDisplayName("Channel to subscriber transfer count");

		DisplayHeaderColumnList displayHeaderColumnList35 = new DisplayHeaderColumnList();
		displayHeaderColumnList35.setColumnName("c2sTransfer_amount");
		displayHeaderColumnList35.setDisplayName("Channel to subscriber transfer amount");

		DisplayHeaderColumnList displayHeaderColumnList36 = new DisplayHeaderColumnList();
		displayHeaderColumnList36.setColumnName("c2sReveral_count");
		displayHeaderColumnList36.setDisplayName("Channel to subscriber reversal count");

		DisplayHeaderColumnList displayHeaderColumnList37 = new DisplayHeaderColumnList();
		displayHeaderColumnList37.setColumnName("c2sReveral_amount");
		displayHeaderColumnList37.setDisplayName("Channel to subscriber reversal amount");

		DisplayHeaderColumnList displayHeaderColumnList38 = new DisplayHeaderColumnList();
		displayHeaderColumnList38.setColumnName("additionalcommissionAmount");
		displayHeaderColumnList38.setDisplayName("Addition commission amount");

		DisplayHeaderColumnList displayHeaderColumnList39 = new DisplayHeaderColumnList();
		displayHeaderColumnList39.setColumnName("closingBalance");
		displayHeaderColumnList39.setDisplayName("Channel to channel transfer return  in count");

		DisplayHeaderColumnList displayHeaderColumnList40 = new DisplayHeaderColumnList();
		displayHeaderColumnList40.setColumnName("reconStatus");
		displayHeaderColumnList40.setDisplayName("Recon status");

		dispHeaderColumnList = new ArrayList<DisplayHeaderColumnList>();

		dispHeaderColumnList.add(displayHeaderColumnList);
		dispHeaderColumnList.add(displayHeaderColumnList1);
		dispHeaderColumnList.add(displayHeaderColumnList2);
		dispHeaderColumnList.add(displayHeaderColumnList3);
		dispHeaderColumnList.add(displayHeaderColumnList4);
		dispHeaderColumnList.add(displayHeaderColumnList5);
		dispHeaderColumnList.add(displayHeaderColumnList6);
		dispHeaderColumnList.add(displayHeaderColumnList7);
		dispHeaderColumnList.add(displayHeaderColumnList8);
		dispHeaderColumnList.add(displayHeaderColumnList9);
		dispHeaderColumnList.add(displayHeaderColumnList10);
		dispHeaderColumnList.add(displayHeaderColumnList11);
		dispHeaderColumnList.add(displayHeaderColumnList12);
		dispHeaderColumnList.add(displayHeaderColumnList13);
		dispHeaderColumnList.add(displayHeaderColumnList14);
		dispHeaderColumnList.add(displayHeaderColumnList15);
		dispHeaderColumnList.add(displayHeaderColumnList16);
		dispHeaderColumnList.add(displayHeaderColumnList17);
		dispHeaderColumnList.add(displayHeaderColumnList18);
		dispHeaderColumnList.add(displayHeaderColumnList19);
		dispHeaderColumnList.add(displayHeaderColumnList20);
		dispHeaderColumnList.add(displayHeaderColumnList21);
		dispHeaderColumnList.add(displayHeaderColumnList22);
		dispHeaderColumnList.add(displayHeaderColumnList23);
		dispHeaderColumnList.add(displayHeaderColumnList24);
		dispHeaderColumnList.add(displayHeaderColumnList25);
		dispHeaderColumnList.add(displayHeaderColumnList26);
		dispHeaderColumnList.add(displayHeaderColumnList27);
		dispHeaderColumnList.add(displayHeaderColumnList28);
		dispHeaderColumnList.add(displayHeaderColumnList29);
		dispHeaderColumnList.add(displayHeaderColumnList30);
		dispHeaderColumnList.add(displayHeaderColumnList31);
		dispHeaderColumnList.add(displayHeaderColumnList32);
		dispHeaderColumnList.add(displayHeaderColumnList33);
		dispHeaderColumnList.add(displayHeaderColumnList34);
		dispHeaderColumnList.add(displayHeaderColumnList35);
		dispHeaderColumnList.add(displayHeaderColumnList36);
		dispHeaderColumnList.add(displayHeaderColumnList37);
		dispHeaderColumnList.add(displayHeaderColumnList38);
		dispHeaderColumnList.add(displayHeaderColumnList39);
		dispHeaderColumnList.add(displayHeaderColumnList40);

		data.setCategory("ALL");
		data.setDispHeaderColumnList(dispHeaderColumnList);
		data.setDomain(_masterVO.getProperty("domainCode"));
		data.setGeography("ALL");
		data.setProduct("ALL");
		data.setFromDate("01/01/21");
		data.setToDate("30/09/21");
		data.setFileType("");
	}

	protected static String accessToken;

	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception {
		// if(accessToken==null) {
		final String methodName = "Test_OAuthenticationTest";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");

		setHeaders();
		setupAuth(data1, data2);
		OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
		oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
		oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
		oAuthenticationAPI.setExpectedStatusCode(200);
		oAuthenticationAPI.perform();
		oAuthenticationResponsePojo = oAuthenticationAPI.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
		long statusCode = oAuthenticationResponsePojo.getStatus();

		accessToken = oAuthenticationResponsePojo.getToken();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Positive Scenario
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-001")
	public void A_01_Test_Passbook_Others_Download_Positive(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_01_Test_Passbook_Others_Download_Positive";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBOTDL01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();
		PassbookOthersDownloadApi passbookOthersDownloadApi = new PassbookOthersDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		passbookOthersDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setUser(loginID);
		passbookOthersDownloadRequestPojo.setData(data);
		passbookOthersDownloadApi.addBodyParam(passbookOthersDownloadRequestPojo);
		passbookOthersDownloadApi.setExpectedStatusCode(200);
		passbookOthersDownloadApi.perform();
		passbookOthersDownloadResponsePojo = passbookOthersDownloadApi
				.getAPIResponseAsPOJO(PassbookOthersDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(passbookOthersDownloadResponsePojo.getStatus());
		String message = passbookOthersDownloadResponsePojo.getMessage();
		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(message, "Offline report processing initiated.");
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.assertEquals(message, "Offline report processing initiated.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-002")
	public void A_02_Test_Passbook_Others_Download_Negative(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_02_Test_Passbook_Others_Download_Negative";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBOTDL02");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();
		PassbookOthersDownloadApi passbookOthersDownloadApi = new PassbookOthersDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		passbookOthersDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setFromDate("01/04/21");
		data.setToDate("24/03/21");
		data.setUser(loginID);
		passbookOthersDownloadRequestPojo.setData(data);
		passbookOthersDownloadApi.addBodyParam(passbookOthersDownloadRequestPojo);
		passbookOthersDownloadApi.setExpectedStatusCode(400);
		passbookOthersDownloadApi.perform();
		passbookOthersDownloadResponsePojo = passbookOthersDownloadApi
				.getAPIResponseAsPOJO(PassbookOthersDownloadResponsePojo.class);
		String messageCode = passbookOthersDownloadResponsePojo.getMessageCode();
		Assert.assertEquals(messageCode, "From Date is greater than to date.");
		Assertion.assertEquals(messageCode, "From Date is greater than to date.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-003")
	public void A_03_Test_Passbook_Others_Download_Negative(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_03_Test_Passbook_Others_Download_Negative";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBOTDL03");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();
		PassbookOthersDownloadApi passbookOthersDownloadApi = new PassbookOthersDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		passbookOthersDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setToDate("");
		data.setUser(loginID);
		passbookOthersDownloadRequestPojo.setData(data);
		passbookOthersDownloadApi.addBodyParam(passbookOthersDownloadRequestPojo);
		passbookOthersDownloadApi.setExpectedStatusCode(400);
		passbookOthersDownloadApi.perform();
		passbookOthersDownloadResponsePojo = passbookOthersDownloadApi
				.getAPIResponseAsPOJO(PassbookOthersDownloadResponsePojo.class);
		String messageCode = passbookOthersDownloadResponsePojo.getMessageCode();
		Assert.assertEquals(messageCode, "Invalid date format.");
		Assertion.assertEquals(messageCode, "Invalid date format.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-004")
	public void A_04_Test_Passbook_Others_Download_Negative(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_04_Test_Passbook_Others_Download_Negative";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBOTDL04");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();
		PassbookOthersDownloadApi passbookOthersDownloadApi = new PassbookOthersDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		passbookOthersDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setUser("");
		passbookOthersDownloadRequestPojo.setData(data);
		passbookOthersDownloadApi.addBodyParam(passbookOthersDownloadRequestPojo);
		passbookOthersDownloadApi.setExpectedStatusCode(400);
		passbookOthersDownloadApi.perform();
		passbookOthersDownloadResponsePojo = passbookOthersDownloadApi
				.getAPIResponseAsPOJO(PassbookOthersDownloadResponsePojo.class);
		String messageCode = passbookOthersDownloadResponsePojo.getMessageCode();
		Assert.assertEquals(messageCode, "Invalid User loginID.");
		Assertion.assertEquals(messageCode, "Invalid User loginID.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-005")
	public void A_05_Test_Passbook_Others_Download_Negative(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_05_Test_Passbook_Others_Download_Negative";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBOTDL05");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();
		PassbookOthersDownloadApi passbookOthersDownloadApi = new PassbookOthersDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		passbookOthersDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setCategory("RET");
		if(categorCode == "RET"){
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			String userId = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 1);
			data.setUser(userId);
		} else {
			data.setUser("deepadist");
		}
		passbookOthersDownloadRequestPojo.setData(data);
		passbookOthersDownloadApi.addBodyParam(passbookOthersDownloadRequestPojo);
		passbookOthersDownloadApi.setExpectedStatusCode(400);
		passbookOthersDownloadApi.perform();
		passbookOthersDownloadResponsePojo = passbookOthersDownloadApi
				.getAPIResponseAsPOJO(PassbookOthersDownloadResponsePojo.class);
		String messageCode = passbookOthersDownloadResponsePojo.getMessageCode();
		Assert.assertEquals(messageCode, "User does not belong to logged in User Category");
		Assertion.assertEquals(messageCode, "User does not belong to logged in User Category");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
