package restassuredapi.test;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2STransferCommissionReportDownload.C2STransferCommissionReportDownloadApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2Stransfercommissionreportdownloadrequestpojo.C2STransferCommissionReportDownloadRequestPojo;
import restassuredapi.pojo.c2Stransfercommissionreportdownloadrequestpojo.Data;
import restassuredapi.pojo.c2Stransfercommissionreportdownloadrequestpojo.DispHeaderColumn;
import restassuredapi.pojo.c2Stransfercommissionreportdownloadresponsepojo.C2STransferCommissionReportDownloadResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.C2S_TRANSFER_COMMISSION_REPORT_DOWNLOAD)
public class C2STransferCommissionReportDownload extends BaseTest {

	static String moduleCode;
	C2STransferCommissionReportDownloadRequestPojo c2STransferCommissionReportDownloadRequestPojo = new C2STransferCommissionReportDownloadRequestPojo();
	C2STransferCommissionReportDownloadResponsePojo c2STransferCommissionReportDownloadResponsePojo = new C2STransferCommissionReportDownloadResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();
	Login login = new Login();

	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String, String> transferDetails = new HashMap<String, String>();

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

		DispHeaderColumn dispHeaderColumn = new DispHeaderColumn();
		dispHeaderColumn.setColumnName("senderName");
		dispHeaderColumn.setDisplayName("Sender name");

		DispHeaderColumn dispHeaderColumn1 = new DispHeaderColumn();
		dispHeaderColumn1.setColumnName("senderMobileNumber");
		dispHeaderColumn1.setDisplayName("Sender mobile number");

		DispHeaderColumn dispHeaderColumn2 = new DispHeaderColumn();
		dispHeaderColumn2.setColumnName("senderMobileType");
		dispHeaderColumn2.setDisplayName("Sender mobile type");

		DispHeaderColumn dispHeaderColumn3 = new DispHeaderColumn();
		dispHeaderColumn3.setColumnName("senderCategory");
		dispHeaderColumn3.setDisplayName("Sender category");

		DispHeaderColumn dispHeaderColumn4 = new DispHeaderColumn();
		dispHeaderColumn4.setColumnName("senderGeography");
		dispHeaderColumn4.setDisplayName("Sender geography");

		DispHeaderColumn dispHeaderColumn5 = new DispHeaderColumn();
		dispHeaderColumn5.setColumnName("receiverServiceClass");
		dispHeaderColumn5.setDisplayName("Service class");

		DispHeaderColumn dispHeaderColumn6 = new DispHeaderColumn();
		dispHeaderColumn6.setColumnName("receiverMobileNumber");
		dispHeaderColumn6.setDisplayName("Receiver mobile number");

		DispHeaderColumn dispHeaderColumn7 = new DispHeaderColumn();
		dispHeaderColumn7.setColumnName("parentName");
		dispHeaderColumn7.setDisplayName("Parent name");

		DispHeaderColumn dispHeaderColumn8 = new DispHeaderColumn();
		dispHeaderColumn8.setColumnName("parentMobileNumber");
		dispHeaderColumn8.setDisplayName("Parent mobile number");

		DispHeaderColumn dispHeaderColumn9 = new DispHeaderColumn();
		dispHeaderColumn9.setColumnName("parentCategory");
		dispHeaderColumn9.setDisplayName("Parent category");

		DispHeaderColumn dispHeaderColumn10 = new DispHeaderColumn();
		dispHeaderColumn10.setColumnName("parentGeography");
		dispHeaderColumn10.setDisplayName("Parent geography");

		DispHeaderColumn dispHeaderColumn11 = new DispHeaderColumn();
		dispHeaderColumn11.setColumnName("ownerName");
		dispHeaderColumn11.setDisplayName("Owner name");

		DispHeaderColumn dispHeaderColumn12 = new DispHeaderColumn();
		dispHeaderColumn12.setColumnName("ownerMobileNumber");
		dispHeaderColumn12.setDisplayName("Owner mobile number");

		DispHeaderColumn dispHeaderColumn13 = new DispHeaderColumn();
		dispHeaderColumn13.setColumnName("ownerCategory");
		dispHeaderColumn13.setDisplayName("Owner category");

		DispHeaderColumn dispHeaderColumn14 = new DispHeaderColumn();
		dispHeaderColumn14.setColumnName("ownerGeography");
		dispHeaderColumn14.setDisplayName("Owner geography");

		DispHeaderColumn dispHeaderColumn15 = new DispHeaderColumn();
		dispHeaderColumn15.setColumnName("transactionID");
		dispHeaderColumn15.setDisplayName("Transaction id");

		DispHeaderColumn dispHeaderColumn16 = new DispHeaderColumn();
		dispHeaderColumn16.setColumnName("transdateTime");
		dispHeaderColumn16.setDisplayName("Transfer time");

		DispHeaderColumn dispHeaderColumn17 = new DispHeaderColumn();
		dispHeaderColumn17.setColumnName("transferAmount");
		dispHeaderColumn17.setDisplayName("Transfer amount");

		DispHeaderColumn dispHeaderColumn18 = new DispHeaderColumn();
		dispHeaderColumn18.setColumnName("requestedAmount");
		dispHeaderColumn18.setDisplayName("Requested amount");

		DispHeaderColumn dispHeaderColumn19 = new DispHeaderColumn();
		dispHeaderColumn19.setColumnName("bonus");
		dispHeaderColumn19.setDisplayName("Bonus");

		DispHeaderColumn dispHeaderColumn20 = new DispHeaderColumn();
		dispHeaderColumn20.setColumnName("creditedAmount");
		dispHeaderColumn20.setDisplayName("Credited amount");

		DispHeaderColumn dispHeaderColumn21 = new DispHeaderColumn();
		dispHeaderColumn21.setColumnName("roamPenalty");
		dispHeaderColumn21.setDisplayName("Roam penalty");

		DispHeaderColumn dispHeaderColumn22 = new DispHeaderColumn();
		dispHeaderColumn22.setColumnName("processingFee");
		dispHeaderColumn22.setDisplayName("Processing fee");

		DispHeaderColumn dispHeaderColumn23 = new DispHeaderColumn();
		dispHeaderColumn23.setColumnName("subService");
		dispHeaderColumn23.setDisplayName("Sub service");

		DispHeaderColumn dispHeaderColumn24 = new DispHeaderColumn();
		dispHeaderColumn24.setColumnName("service");
		dispHeaderColumn24.setDisplayName("Service");

		DispHeaderColumn dispHeaderColumn25 = new DispHeaderColumn();
		dispHeaderColumn25.setColumnName("pinSentTo");
		dispHeaderColumn25.setDisplayName("Voucher PIN send to number");

		DispHeaderColumn dispHeaderColumn26 = new DispHeaderColumn();
		dispHeaderColumn26.setColumnName("requestGateway");
		dispHeaderColumn26.setDisplayName("Request Source");

		DispHeaderColumn dispHeaderColumn27 = new DispHeaderColumn();
		dispHeaderColumn27.setColumnName("voucherserialNo");
		dispHeaderColumn27.setDisplayName("Voucher serial numbe");

		DispHeaderColumn dispHeaderColumn28 = new DispHeaderColumn();
		dispHeaderColumn28.setColumnName("adjustmentTransID");
		dispHeaderColumn28.setDisplayName("Adjustment transaction ID");

		DispHeaderColumn dispHeaderColumn29 = new DispHeaderColumn();
		dispHeaderColumn29.setColumnName("externalReferenceID");
		dispHeaderColumn29.setDisplayName("External reference ID");

		DispHeaderColumn dispHeaderColumn30 = new DispHeaderColumn();
		dispHeaderColumn30.setColumnName("previousBalance");
		dispHeaderColumn30.setDisplayName("Previous Balance");

		DispHeaderColumn dispHeaderColumn31 = new DispHeaderColumn();
		dispHeaderColumn31.setColumnName("postBalance");
		dispHeaderColumn31.setDisplayName("Post Balance");

		DispHeaderColumn dispHeaderColumn32 = new DispHeaderColumn();
		dispHeaderColumn32.setColumnName("currencyDetail");
		dispHeaderColumn32.setDisplayName("Currency Details");

		DispHeaderColumn dispHeaderColumn33 = new DispHeaderColumn();
		dispHeaderColumn33.setColumnName("commissionType");
		dispHeaderColumn33.setDisplayName("Additional commission type");

		DispHeaderColumn dispHeaderColumn34 = new DispHeaderColumn();
		dispHeaderColumn34.setColumnName("marginRate");
		dispHeaderColumn34.setDisplayName("Additional commission rate");

		DispHeaderColumn dispHeaderColumn35 = new DispHeaderColumn();
		dispHeaderColumn35.setColumnName("additionalCommission");
		dispHeaderColumn35.setDisplayName("Additional commission amount");

		DispHeaderColumn dispHeaderColumn36 = new DispHeaderColumn();
		dispHeaderColumn36.setColumnName("cacRate");
		dispHeaderColumn36.setDisplayName("Cumulative addition commission type");

		DispHeaderColumn dispHeaderColumn37 = new DispHeaderColumn();
		dispHeaderColumn37.setColumnName("cacType");
		dispHeaderColumn37.setDisplayName("Cumulative addition commission rate");

		DispHeaderColumn dispHeaderColumn38 = new DispHeaderColumn();
		dispHeaderColumn38.setColumnName("cacAmount");
		dispHeaderColumn38.setDisplayName("Cumulative addition commission amount");

		List<DispHeaderColumn> dispHeaderColumnList = new ArrayList<DispHeaderColumn>();

		dispHeaderColumnList.add(dispHeaderColumn);
		dispHeaderColumnList.add(dispHeaderColumn1);
		dispHeaderColumnList.add(dispHeaderColumn2);
		dispHeaderColumnList.add(dispHeaderColumn3);
		dispHeaderColumnList.add(dispHeaderColumn4);
		dispHeaderColumnList.add(dispHeaderColumn5);
		dispHeaderColumnList.add(dispHeaderColumn6);
		dispHeaderColumnList.add(dispHeaderColumn7);
		dispHeaderColumnList.add(dispHeaderColumn8);
		dispHeaderColumnList.add(dispHeaderColumn9);
		dispHeaderColumnList.add(dispHeaderColumn10);
		dispHeaderColumnList.add(dispHeaderColumn11);
		dispHeaderColumnList.add(dispHeaderColumn12);
		dispHeaderColumnList.add(dispHeaderColumn13);
		dispHeaderColumnList.add(dispHeaderColumn14);
		dispHeaderColumnList.add(dispHeaderColumn15);
		dispHeaderColumnList.add(dispHeaderColumn16);
		dispHeaderColumnList.add(dispHeaderColumn17);
		dispHeaderColumnList.add(dispHeaderColumn18);
		dispHeaderColumnList.add(dispHeaderColumn19);
		dispHeaderColumnList.add(dispHeaderColumn20);
		dispHeaderColumnList.add(dispHeaderColumn21);
		dispHeaderColumnList.add(dispHeaderColumn22);
		dispHeaderColumnList.add(dispHeaderColumn23);
		dispHeaderColumnList.add(dispHeaderColumn24);
		dispHeaderColumnList.add(dispHeaderColumn25);
		dispHeaderColumnList.add(dispHeaderColumn26);
		dispHeaderColumnList.add(dispHeaderColumn27);
		dispHeaderColumnList.add(dispHeaderColumn28);
		dispHeaderColumnList.add(dispHeaderColumn29);
		dispHeaderColumnList.add(dispHeaderColumn30);
		dispHeaderColumnList.add(dispHeaderColumn31);
		dispHeaderColumnList.add(dispHeaderColumn32);
		dispHeaderColumnList.add(dispHeaderColumn33);
		dispHeaderColumnList.add(dispHeaderColumn34);
		dispHeaderColumnList.add(dispHeaderColumn35);
		dispHeaderColumnList.add(dispHeaderColumn36);
		dispHeaderColumnList.add(dispHeaderColumn37);
		dispHeaderColumnList.add(dispHeaderColumn38);

		data.setAllowedTimeFrom("00:00");
		data.setAllowedTimeTo("23:59");
		data.setCategoryCode(_masterVO.getProperty("categoryCode1"));
		data.setDispHeaderColumnList(dispHeaderColumnList);
		data.setDomain(_masterVO.getProperty("domainCode"));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setFileType("CSV");
		data.setGeography(_masterVO.getProperty("geography1"));
		data.setMobileNumber("");
		data.setReportDate("25/11/21");
		data.setReqTab(_masterVO.getProperty("reqTabMsisdn"));
		data.setUserType("ALL");
		data.setService("ALL");
		data.setTransStatus("ALL");
		data.setChannelUser("");
		data.setStaffOption("OPTION_LOGIN_ID");
		data.setLoginIDOrMSISDN("");
		c2STransferCommissionReportDownloadRequestPojo.setData(data);
	}

	// Successful data with valid data.

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

	// Successful data with valid data.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-001")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2STCRDOWNLOAD1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2STransferCommissionReportDownloadApi c2STransferCommissionReportDownloadApi = new C2STransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2STransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setMobileNumber(msisdn);
		c2STransferCommissionReportDownloadRequestPojo.setData(data);
		c2STransferCommissionReportDownloadApi.addBodyParam(c2STransferCommissionReportDownloadRequestPojo);
		c2STransferCommissionReportDownloadApi.setExpectedStatusCode(200);
		c2STransferCommissionReportDownloadApi.perform();
		c2STransferCommissionReportDownloadResponsePojo = c2STransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2STransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2STransferCommissionReportDownloadResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-002")
	public void A_02_Test_Negative2_C2STransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_02_Test_Negative2_C2STransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2STCRDOWNLOAD2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2STransferCommissionReportDownloadApi c2STransferCommissionReportDownloadApi = new C2STransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2STransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setMobileNumber("!@#112343");
		c2STransferCommissionReportDownloadRequestPojo.setData(data);

		c2STransferCommissionReportDownloadApi.addBodyParam(c2STransferCommissionReportDownloadRequestPojo);
		c2STransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2STransferCommissionReportDownloadApi.perform();
		c2STransferCommissionReportDownloadResponsePojo = c2STransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2STransferCommissionReportDownloadResponsePojo.class);
		String message = c2STransferCommissionReportDownloadResponsePojo.getMessage();

		Assert.assertEquals(message, "Invalid MSISDN");
		Assertion.assertEquals(message, "Invalid MSISDN");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// blank - fromDate
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-003")
	public void A_03_Test_Negative3_C2STransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_03_Test_Negative3_C2STransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2STCRDOWNLOAD3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2STransferCommissionReportDownloadApi c2STransferCommissionReportDownloadApi = new C2STransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2STransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setMobileNumber("");
		c2STransferCommissionReportDownloadRequestPojo.setData(data);

		c2STransferCommissionReportDownloadApi.addBodyParam(c2STransferCommissionReportDownloadRequestPojo);
		c2STransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2STransferCommissionReportDownloadApi.perform();
		c2STransferCommissionReportDownloadResponsePojo = c2STransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2STransferCommissionReportDownloadResponsePojo.class);
		String message = c2STransferCommissionReportDownloadResponsePojo.getMessage();

		Assert.assertEquals(message, "Invalid MSISDN");
		Assertion.assertEquals(message, "Invalid MSISDN");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-004")
	public void A_04_Test_Negative4_C2STransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_04_Test_Negative4_C2STransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2STCRDOWNLOAD4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2STransferCommissionReportDownloadApi c2STransferCommissionReportDownloadApi = new C2STransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2STransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		SimpleDateFormat s = new SimpleDateFormat("dd/MM/yy");
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, 1);
		data.setMobileNumber(msisdn);
		data.setReportDate(s.format(c.getTime()));
		c2STransferCommissionReportDownloadRequestPojo.setData(data);
		;

		c2STransferCommissionReportDownloadApi.addBodyParam(c2STransferCommissionReportDownloadRequestPojo);
		c2STransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2STransferCommissionReportDownloadApi.perform();
		c2STransferCommissionReportDownloadResponsePojo = c2STransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2STransferCommissionReportDownloadResponsePojo.class);
		String message = c2STransferCommissionReportDownloadResponsePojo.getMessage();

		Assert.assertEquals(message, "From Date is greater than current date.");
		Assertion.assertEquals(message, "From Date is greater than current date.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-005")
	public void A_05_Test_Positive5_C2STransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_05_Test_Negative5_C2STransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2STCRDOWNLOAD5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2STransferCommissionReportDownloadApi c2STransferCommissionReportDownloadApi = new C2STransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2STransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setMobileNumber(msisdn);
		data.setUserType("STAFF");
		data.setLoginIDOrMSISDN("gouri_staff");
		c2STransferCommissionReportDownloadRequestPojo.setData(data);
		c2STransferCommissionReportDownloadApi.addBodyParam(c2STransferCommissionReportDownloadRequestPojo);
		c2STransferCommissionReportDownloadApi.setExpectedStatusCode(200);
		c2STransferCommissionReportDownloadApi.perform();
		c2STransferCommissionReportDownloadResponsePojo = c2STransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2STransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2STransferCommissionReportDownloadResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-006")
	public void A_06_Test_Negative6_C2STransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_06_Test_Negative6_C2STransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2STCRDOWNLOAD6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2STransferCommissionReportDownloadApi c2STransferCommissionReportDownloadApi = new C2STransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2STransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setMobileNumber(msisdn);
		data.setUserType("STAFF");
		data.setLoginIDOrMSISDN("324456");
		c2STransferCommissionReportDownloadRequestPojo.setData(data);

		c2STransferCommissionReportDownloadApi.addBodyParam(c2STransferCommissionReportDownloadRequestPojo);
		c2STransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2STransferCommissionReportDownloadApi.perform();
		c2STransferCommissionReportDownloadResponsePojo = c2STransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2STransferCommissionReportDownloadResponsePojo.class);
		String message = c2STransferCommissionReportDownloadResponsePojo.getMessage();

		Assert.assertEquals(message, "Invalid staff login ID.");
		Assertion.assertEquals(message, "Invalid staff login ID.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-007")
	public void A_07_Test_Negative_C2STransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_07_Test_Negative_C2STransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2STCRDOWNLOAD7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2STransferCommissionReportDownloadApi c2STransferCommissionReportDownloadApi = new C2STransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2STransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setReqTab(_masterVO.getProperty("reqTabAdv"));
		data.setChannelUser("ALL");
		c2STransferCommissionReportDownloadRequestPojo.setData(data);
		c2STransferCommissionReportDownloadApi.addBodyParam(c2STransferCommissionReportDownloadRequestPojo);
		c2STransferCommissionReportDownloadApi.setExpectedStatusCode(200);
		c2STransferCommissionReportDownloadApi.perform();
		c2STransferCommissionReportDownloadResponsePojo = c2STransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2STransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2STransferCommissionReportDownloadResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-004")
	public void A_08_Test_Negative_C2STransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_08_Test_Negative_C2STransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2STCRDOWNLOAD8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2STransferCommissionReportDownloadApi c2STransferCommissionReportDownloadApi = new C2STransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2STransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		SimpleDateFormat s = new SimpleDateFormat("dd/MM/yy");
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, 1);
		data.setReportDate(s.format(c.getTime()));
		data.setReqTab(_masterVO.getProperty("reqTabAdv"));
		data.setChannelUser("ALL");
		c2STransferCommissionReportDownloadRequestPojo.setData(data);
		c2STransferCommissionReportDownloadApi.addBodyParam(c2STransferCommissionReportDownloadRequestPojo);
		c2STransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2STransferCommissionReportDownloadApi.perform();
		c2STransferCommissionReportDownloadResponsePojo = c2STransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2STransferCommissionReportDownloadResponsePojo.class);
		String message = c2STransferCommissionReportDownloadResponsePojo.getMessage();

		Assert.assertEquals(message, "From Date is greater than current date.");
		Assertion.assertEquals(message, "From Date is greater than current date.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-004")
	public void A_09_Test_Positive_C2STransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_09_Test_Positive_C2STransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2STCRDOWNLOAD9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2STransferCommissionReportDownloadApi c2STransferCommissionReportDownloadApi = new C2STransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2STransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setReqTab(_masterVO.getProperty("reqTabAdv"));
		data.setChannelUser("ALL");
		data.setUserType("STAFF");
		data.setLoginIDOrMSISDN("gouri_staff");
		c2STransferCommissionReportDownloadRequestPojo.setData(data);

		c2STransferCommissionReportDownloadApi.addBodyParam(c2STransferCommissionReportDownloadRequestPojo);
		c2STransferCommissionReportDownloadApi.setExpectedStatusCode(200);
		c2STransferCommissionReportDownloadApi.perform();
		c2STransferCommissionReportDownloadResponsePojo = c2STransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2STransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2STransferCommissionReportDownloadResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-004")
	public void A_10_Test_Negative_C2STransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_10_Test_Negative_C2STransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2STCRDOWNLOAD10");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2STransferCommissionReportDownloadApi c2STransferCommissionReportDownloadApi = new C2STransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2STransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setReqTab(_masterVO.getProperty("reqTabAdv"));
		data.setChannelUser("");
		data.setUserType("STAFF");
		data.setLoginIDOrMSISDN("324456");
		c2STransferCommissionReportDownloadRequestPojo.setData(data);

		c2STransferCommissionReportDownloadApi.addBodyParam(c2STransferCommissionReportDownloadRequestPojo);
		c2STransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2STransferCommissionReportDownloadApi.perform();
		c2STransferCommissionReportDownloadResponsePojo = c2STransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2STransferCommissionReportDownloadResponsePojo.class);
		String message = c2STransferCommissionReportDownloadResponsePojo.getMessage();

		Assert.assertEquals(message, "Invalid staff login ID.");
		Assertion.assertEquals(message, "Invalid staff login ID.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}