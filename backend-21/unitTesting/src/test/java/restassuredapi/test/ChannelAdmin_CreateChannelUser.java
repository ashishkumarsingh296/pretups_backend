package restassuredapi.test;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.github.javafaker.Faker;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.channelAdmin_CreateChannelUser.ChannelAdmin_CreateChannelUserAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.ChannelAdminCreateChannelUserReqPojo;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.CreateChannelUserMsisdn;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.CreateChannelUserVO;
import restassuredapi.pojo.channelAdminCreateChannelUserResponsePojo.ChannelAdminCreateChannelUserResPojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.CREATE_CHANNEL_USER)
public class ChannelAdmin_CreateChannelUser extends BaseTest {

	Map<String, String> channelUserData = new HashMap<String, String>();

	@BeforeClass
	public void getChannelUserData() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			int rowNum = i + 1;
			String catCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, rowNum);
			channelUserData.put(catCode.toLowerCase() + "_loginId",
					ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, rowNum));
		}
	}

	Faker faker = new Faker();
	static String moduleCode;
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	ChannelAdminCreateChannelUserReqPojo channelAdminCreateChannelUserReqPojo = new ChannelAdminCreateChannelUserReqPojo();
	ChannelAdminCreateChannelUserResPojo channelAdminCreateChannelUserResPojo = new ChannelAdminCreateChannelUserResPojo();

	CreateChannelUserVO data = new CreateChannelUserVO();
	Login login = new Login();
	String loginId = null;
	String phNo = null;

	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String, String> transferDetails = new HashMap<String, String>();

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);

		Object[][] Data = new Object[1][5];
		int j = 0;
		int i = 11;
		Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
		Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
		Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
		Data[j][3] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
		Data[j][4] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
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

	public void setUpData() {

		data.setAddress1("");
		data.setAddress2("");
		data.setAllowedTimeFrom("");
		data.setAllowedTimeTo("");
		data.setAlloweddays("");
		data.setAllowedip("");
		data.setAppointmentdate("");
		data.setCity("");
		data.setCommissionProfileID("");
		data.setCompany("");
		data.setContactNumber("");
		data.setContactPerson("");
		data.setControlGroup("N");
		data.setCountry("");
		data.setDesignation("");
		data.setDocumentNo("");
		data.setDocumentType("");
		data.setDomain(_masterVO.getProperty("domainCode"));
		data.setEmailid(_masterVO.getProperty("email"));
		data.setEmpcode("");
		data.setExternalCode(UniqueChecker.UC_EXTCODE());
		data.setExtnwcode(_masterVO.getProperty("networkCode"));
		data.setFax("");
		String firstName = faker.name().firstName();
		data.setFirstName(firstName);
		data.setGeographicalDomain(_masterVO.getProperty("geographicalDomain"));
		data.setGeographyCode(_masterVO.getProperty("geography_code"));
		data.setInsuspend("N");
		data.setLanguage("en_US");
		String lastName = faker.name().lastName();
		data.setLastName(lastName);
		data.setLatitude("");
		data.setLmsProfileId("");
		data.setLongitude("");
		data.setLowbalalertother("Y");
		data.setLowbalalertparent("Y");
		data.setLowbalalertself("Y");

		CreateChannelUserMsisdn msisdnDetails = new CreateChannelUserMsisdn();
		phNo = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		msisdnDetails.setPhoneNo(phNo);
		msisdnDetails.setPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setConfirmPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setStkProfile(_masterVO.getProperty("domainCode"));
		List<CreateChannelUserMsisdn> msisdn = new ArrayList<CreateChannelUserMsisdn>();
		msisdn.add(msisdnDetails);
		data.setMsisdn(msisdn);
		data.setOtherEmail("");
		data.setOutletCode("");
		data.setSubOutletCode("");
		data.setOutletCode("");
		data.setOwnerUser("");
		data.setParentCategory(_masterVO.getProperty("domainCode"));
		data.setParentUser("");
		data.setPaymentType(_masterVO.getProperty("paymentType"));
		data.setRoleType("N");
		data.setRoles("");
		data.setServices(_masterVO.getProperty("services"));
		data.setShortName(faker.name().lastName());
		data.setSsn("");
		data.setState("");
		data.setSubscriberCode("");
		data.setTransferProfile("");
		data.setTransferRuleType("");
		data.setUserCatCode(_masterVO.getProperty("domainCode"));
		data.setUserCode(phNo);
		data.setUserName(firstName + " " + lastName);
		data.setUserNamePrefix("CMPY");
		data.setUsergrade("");
		data.setVoucherTypes("");
		loginId = UniqueChecker.UC_LOGINID();
		data.setWebloginid(loginId);
		data.setWebpassword(_masterVO.getProperty("NewPassword"));
		data.setConfirmwebpassword(_masterVO.getProperty("NewPassword"));

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

	public void setTestInitialDetails(String methodName, String loginID, String password, String categoryName, String caseId)
			throws Exception {
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID(caseId);
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setUpData();
	}

	public void setMsisdnDetails(String phNumber, String stkProfile) {
		CreateChannelUserMsisdn msisdnDetails = new CreateChannelUserMsisdn();
		msisdnDetails.setPhoneNo(phNumber);
		msisdnDetails.setPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setConfirmPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setStkProfile(stkProfile);

		List<CreateChannelUserMsisdn> msisdn = new ArrayList<CreateChannelUserMsisdn>();
		msisdn.add(msisdnDetails);
		data.setMsisdn(msisdn);
	}
	
	public void setMultipleMsisdnDetails(String stkProfile) {
		CreateChannelUserMsisdn msisdnDetails = new CreateChannelUserMsisdn();
		msisdnDetails.setPhoneNo(UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
		msisdnDetails.setPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setConfirmPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setStkProfile(stkProfile);
		
		CreateChannelUserMsisdn msisdnDetails1 = new CreateChannelUserMsisdn();
		msisdnDetails1.setPhoneNo(UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
		msisdnDetails1.setPin(_masterVO.getProperty("PIN"));
		msisdnDetails1.setConfirmPin(_masterVO.getProperty("PIN"));
		msisdnDetails1.setDescription("");
		msisdnDetails1.setIsprimary("N");
		msisdnDetails1.setStkProfile(stkProfile);

		List<CreateChannelUserMsisdn> msisdn = new ArrayList<CreateChannelUserMsisdn>();
		msisdn.add(msisdnDetails);
		msisdn.add(msisdnDetails1);
		data.setMsisdn(msisdn);
	}

	public void executeCreateChannelUserAPI() throws IOException {
		Log.info("Entering executeCreateChannelUserAPI()");
		ChannelAdmin_CreateChannelUserAPI channelAdminCreateChannelUserAPI = new ChannelAdmin_CreateChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminCreateChannelUserAPI.addBodyParam(channelAdminCreateChannelUserReqPojo);
		channelAdminCreateChannelUserAPI.logRequestBody(channelAdminCreateChannelUserReqPojo);
		channelAdminCreateChannelUserAPI.setExpectedStatusCode(200);
		channelAdminCreateChannelUserAPI.perform();
		channelAdminCreateChannelUserResPojo = channelAdminCreateChannelUserAPI
				.getAPIResponseAsPOJO(ChannelAdminCreateChannelUserResPojo.class);
		Log.info("Exiting executeCreateChannelUserAPI()");
	}
	
	@Test(dataProvider = "userData", priority = 1)
	@TestManager(TestKey = "PRETUPS-001")
	public void channelAdmin_AddChannelUser_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER01");
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		// String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void channelAdmin_AddChannelUser_ExistingLoginId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_ExistingLoginId";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER02");
		data.setWebloginid(channelUserData.get("dist_loginId"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "User's WEBLOGINID already exist.");
		Assertion.assertEquals(message, "User's WEBLOGINID already exist.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-003")
	public void channelAdmin_AddChannelUser_blankLoginId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_blankLoginId";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER03");
		data.setWebloginid("");
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void channelAdmin_AddChannelUser_existingMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_existingMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER04");
		setMsisdnDetails(DBHandler.AccessHandler.getMSISDN("DIST"), _masterVO.getProperty("distributorCatCode"));
		data.setUserCode(DBHandler.AccessHandler.getMSISDN("DIST"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Mobile number already exist in the system.");
		Assertion.assertEquals(message, "Mobile number already exist in the system.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-005")
	public void channelAdmin_AddChannelUser_blankMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_blankMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER05");
		setMsisdnDetails("", _masterVO.getProperty("distributorCatCode"));
		data.setUserCode("");
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Primary MSISDN is blank.");
		Assertion.assertEquals(message, "Primary MSISDN is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 6)
	@TestManager(TestKey = "PRETUPS-006")
	public void channelAdmin_AddChannelUser_AlphaNumMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_blankMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER06");
		setMsisdnDetails("LPO23SD32", _masterVO.getProperty("distributorCatCode"));
		data.setUserCode("LPO23SD32");
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Invalid MSISDN");
		Assertion.assertEquals(message, "Invalid MSISDN");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 7)
	@TestManager(TestKey = "PRETUPS-007")
	public void channelAdmin_AddChannelUser_existingEXTCode(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_existingEXTCode";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER07");
		String extCode = DBHandler.AccessHandler.getMSISDN("DIST");
		data.setExternalCode(DBHandler.AccessHandler.getExternalCodeFromMsisdn(extCode));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "User already exists with this external Code.");
		Assertion.assertEquals(message, "User already exists with this external Code.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 8)
	@TestManager(TestKey = "PRETUPS-008")
	public void channelAdmin_AddChannelUser_blankEXTCode(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_blankEXTCode";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER08");
		data.setExternalCode("");
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "User's EXTERNALCODE is either blank or length is more than allowed.");
		Assertion.assertEquals(message, "User's EXTERNALCODE is either blank or length is more than allowed.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 9)
	@TestManager(TestKey = "PRETUPS-009")
	public void channelAdmin_AddChannelUser_specCharsEXTCode(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_blankEXTCode";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER09");
		data.setExternalCode(new RandomGeneration().randomAlphabets(4) + "!@#$%^%%#$");
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Special chars not allowed in external code.");
		Assertion.assertEquals(message, "Special chars not allowed in external code.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 10)
	@TestManager(TestKey = "PRETUPS-010")
	public void channelAdmin_AddChannelUser_blankEmailId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_blankEmailId";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER10");
		data.setEmailid("");
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Email id is mandatory.");
		Assertion.assertEquals(message, "Email id is mandatory.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 11)
	@TestManager(TestKey = "PRETUPS-011")
	public void channelAdmin_AddChannelUser_invalidEmailId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_invalidEmailId";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER11");
		data.setEmailid(new RandomGeneration().randomAlphaNumeric(9));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Invalid Email ID");
		Assertion.assertEquals(message, "Invalid Email ID");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 12)
	@TestManager(TestKey = "PRETUPS-012")
	public void channelAdmin_AddChannelUser_RolesSpecific(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_RolesSpecific";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER12");
		data.setPaymentType("CASH");
		data.setRoles(_masterVO.getProperty("addRoles"));
		data.setServices(_masterVO.getProperty("addServices"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		String userId = DBHandler.AccessHandler.getUserIdLoginID(loginId);
		Log.info("Printing UserId : " + userId);
		List<String> actualRoles = DBHandler.AccessHandler.getUserRoles(userId, _masterVO.getProperty("distributorCatCode")).stream().sorted()
				.collect(Collectors.toList());
		List<String> actualUserServices = DBHandler.AccessHandler.fetchUserServicesTypes(userId).stream().sorted()
				.collect(Collectors.toList());
		Assert.assertEquals(statusCode, 200);
		ArrayList<String> roles = new ArrayList<String>(
				Arrays.asList("C2CRETURN", "UNBARUSER", "VIEWCUSERSELF", "C2SREV"));
		List<String> expectedRoles = roles.stream().sorted().collect(Collectors.toList());
		ArrayList<String> expectedUserServices = new ArrayList<String>(Arrays.asList("DVD", "EVD"));
		Assert.assertEquals(actualRoles, expectedRoles);
		Assert.assertEquals(actualUserServices, expectedUserServices);
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 13)
	@TestManager(TestKey = "PRETUPS-013")
	public void channelAdmin_AddChannelUser_blankParentAndUserInfo(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_blankParentAndUserInfo";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER13");
		setMsisdnDetails("72" + randStr.randomNumberWithoutZero(8), _masterVO.getProperty("agentCatCode"));
		data.setParentCategory(_masterVO.getProperty("dealerCatCode"));
		data.setUserCatCode(_masterVO.getProperty("agentCatCode"));
		data.setOwnerUser("");
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 14)
	@TestManager(TestKey = "PRETUPS-014")
	public void channelAdmin_AddChannelUser_childNotUnderParent(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_childNotUnderParent";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER14");
		setMsisdnDetails("72" + randStr.randomNumberWithoutZero(8), _masterVO.getProperty("agentCatCode"));
		data.setParentCategory(_masterVO.getProperty("dealerCatCode"));
		data.setUserCatCode(_masterVO.getProperty("agentCatCode"));
		data.setOwnerUser(channelUserData.get("dist_loginId"));
		data.setParentUser("roshan_dealer");
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Parent Category not found.");
		Assertion.assertEquals(message, "Parent Category not found.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 15)
	@TestManager(TestKey = "PRETUPS-015")
	public void channelAdmin_AddDealerChannelUser(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_childNotUnderParent";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER15");
		data.setGeographyCode(_masterVO.getProperty("dealer_geography_code"));
		data.setUserCatCode(_masterVO.getProperty("dealerCatCode"));
		data.setOwnerUser(channelUserData.get("dist_loginId"));
		setMsisdnDetails("78" + randStr.randomNumberWithoutZero(8), _masterVO.getProperty("dealerCatCode"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		// String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	@Test(dataProvider = "userData", priority = 16)
	@TestManager(TestKey = "PRETUPS-016")
	public void channelAdmin_AddAgentChannelUserWithParentAsDealer(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddAgentChannelUserWithParentAsDealer";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER16");
		data.setGeographyCode(_masterVO.getProperty("agent_geography_code"));
		data.setUserCatCode(_masterVO.getProperty("agentCatCode"));
		data.setOwnerUser(channelUserData.get("dist_loginId"));
		data.setParentUser(channelUserData.get("se_loginId"));
		data.setParentCategory(_masterVO.getProperty("dealerCatCode"));
		setMsisdnDetails("78" + randStr.randomNumberWithoutZero(8), _masterVO.getProperty("agentCatCode"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		// String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData", priority = 17)
	@TestManager(TestKey = "PRETUPS-017")
	public void channelAdmin_AddAgentChannelUserWithParentAsDistributor(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddAgentChannelUserWithParentAsDistributor";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER17");
		data.setGeographyCode(_masterVO.getProperty("agent_geography_code"));
		data.setUserCatCode(_masterVO.getProperty("agentCatCode"));
		data.setOwnerUser(channelUserData.get("dist_loginId"));
		data.setParentCategory(_masterVO.getProperty("distributorCatCode"));
		setMsisdnDetails("78" + randStr.randomNumberWithoutZero(8), _masterVO.getProperty("agentCatCode"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		// String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData", priority = 18)
	@TestManager(TestKey = "PRETUPS-018")
	public void channelAdmin_AddRetailerWithParentAsDistributor(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddRetailerWithParentAsDistributor";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER18");
		data.setGeographyCode(_masterVO.getProperty("retailer_geography_code"));
		data.setUserCatCode(_masterVO.getProperty("retailerCatCode"));
		data.setOwnerUser(channelUserData.get("dist_loginId"));
		data.setParentCategory(_masterVO.getProperty("distributorCatCode"));
		data.setOutletCode(_masterVO.getProperty("outletCode"));
		data.setSubOutletCode(_masterVO.getProperty("subOutletCode"));
		setMsisdnDetails("78" + randStr.randomNumberWithoutZero(8), _masterVO.getProperty("retailerCatCode"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		// String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData", priority = 19)
	@TestManager(TestKey = "PRETUPS-019")
	public void channelAdmin_AddRetailerWithParentAsDealer(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddRetailerWithParentAsDistributor";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER19");
		data.setGeographyCode(_masterVO.getProperty("retailer_geography_code"));
		data.setUserCatCode(_masterVO.getProperty("retailerCatCode"));
		data.setOwnerUser(channelUserData.get("dist_loginId"));
		data.setParentCategory(_masterVO.getProperty("dealerCatCode"));
		data.setParentUser(channelUserData.get("se_loginId"));
		data.setOutletCode(_masterVO.getProperty("outletCode"));
		data.setSubOutletCode(_masterVO.getProperty("subOutletCode"));
		setMsisdnDetails("78" + randStr.randomNumberWithoutZero(8), _masterVO.getProperty("retailerCatCode"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		// String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData", priority = 20)
	@TestManager(TestKey = "PRETUPS-020")
	public void channelAdmin_AddRetailerWithParentAsAgent(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddRetailerWithParentAsAgent";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER20");
		data.setGeographyCode(_masterVO.getProperty("retailer_geography_code"));
		data.setUserCatCode(_masterVO.getProperty("retailerCatCode"));
		data.setOwnerUser(channelUserData.get("dist_loginId"));
		data.setParentCategory(_masterVO.getProperty("agentCatCode"));
		data.setParentUser(channelUserData.get("ag_loginId"));
		data.setOutletCode(_masterVO.getProperty("outletCode"));
		data.setSubOutletCode(_masterVO.getProperty("subOutletCode"));
		setMsisdnDetails("78" + randStr.randomNumberWithoutZero(8), _masterVO.getProperty("retailerCatCode"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		// String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData", priority = 21)
	@TestManager(TestKey = "PRETUPS-021")
	public void channelAdmin_AddDealerWithMultiMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddDealerWithMultiMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER21");
		data.setGeographyCode(_masterVO.getProperty("dealer_geography_code"));
		data.setUserCatCode(_masterVO.getProperty("dealerCatCode"));
		data.setOwnerUser(channelUserData.get("dist_loginId"));
		setMultipleMsisdnDetails(_masterVO.getProperty("dealerCatCode"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		// String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData", priority = 22)
	@TestManager(TestKey = "PRETUPS-022")
	public void channelAdmin_AddAgentWithMultiMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddAgentWithMultiMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER22");
		data.setGeographyCode(_masterVO.getProperty("agent_geography_code"));
		data.setUserCatCode(_masterVO.getProperty("agentCatCode"));
		data.setOwnerUser(channelUserData.get("dist_loginId"));
		data.setParentCategory(_masterVO.getProperty("distributorCatCode"));
		setMultipleMsisdnDetails(_masterVO.getProperty("agentCatCode"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		// String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData", priority = 23)
	@TestManager(TestKey = "PRETUPS-023")
	public void channelAdmin_AddRetailerWithMultipleMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddRetailerWithMultipleMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName,
				"CADMADDCHNLUSER23");
		data.setGeographyCode(_masterVO.getProperty("retailer_geography_code"));
		data.setUserCatCode(_masterVO.getProperty("retailerCatCode"));
		data.setOwnerUser(channelUserData.get("dist_loginId"));
		data.setParentCategory(_masterVO.getProperty("distributorCatCode"));
		setMultipleMsisdnDetails(_masterVO.getProperty("retailerCatCode"));
		data.setOutletCode(_masterVO.getProperty("outletCode"));
		data.setSubOutletCode(_masterVO.getProperty("subOutletCode"));
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		// String message = channelAdminCreateChannelUserResPojo.getMessage();
		int statusCode = channelAdminCreateChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
