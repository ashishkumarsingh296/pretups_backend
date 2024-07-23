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
import restassuredapi.api.channelAdmin_ModifyChannelUser.ChannelAdmin_ModifyChannelUserAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.ChannelAdminCreateChannelUserReqPojo;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.CreateChannelUserMsisdn;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.CreateChannelUserVO;
import restassuredapi.pojo.channelAdminCreateChannelUserResponsePojo.ChannelAdminCreateChannelUserResPojo;
import restassuredapi.pojo.channelAdminModifyChannelUserRequestPojo.ChannelAdminModifyChannelUserReqPojo;
import restassuredapi.pojo.channelAdminModifyChannelUserRequestPojo.ModifyChannelUserMsisdn;
import restassuredapi.pojo.channelAdminModifyChannelUserRequestPojo.ModifyChannelUserVO;
import restassuredapi.pojo.channelAdminModifyChannelUserResponsePojo.ChannelAdminModifyChannelUserResPojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.UPDATE_CHANNEL_USER)
public class ChannelAdmin_ModifyChannelUser extends BaseTest {

	static String moduleCode;
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	ChannelAdminCreateChannelUserReqPojo channelAdminCreateChannelUserReqPojo = new ChannelAdminCreateChannelUserReqPojo();
	ChannelAdminCreateChannelUserResPojo channelAdminCreateChannelUserResPojo = new ChannelAdminCreateChannelUserResPojo();
	ChannelAdminModifyChannelUserReqPojo channelAdminModifyChannelUserReqPojo = new ChannelAdminModifyChannelUserReqPojo();
	ChannelAdminModifyChannelUserResPojo channelAdminModifyChannelUserResPojo = new ChannelAdminModifyChannelUserResPojo();

	ModifyChannelUserVO data = new ModifyChannelUserVO();
	CreateChannelUserVO data1 = new CreateChannelUserVO();
	Faker faker = new Faker();

	Login login = new Login();
	String loginId = null;
	String phNo = null;
	String extCode = null;

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

	Map<String, String> channelUsers = new HashMap<String, String>();

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

	public void createChannelUserData(String geographyCode, String userCatCode, String ownerUser, String stkProfile) {

		data1.setAddress1(faker.address().streetAddress());
		data1.setAddress2("");
		data1.setAllowedTimeFrom("");
		data1.setAllowedTimeTo("");
		data1.setAlloweddays("");
		data1.setAllowedip("");
		data1.setAppointmentdate("");
		data1.setCity("");
		data1.setCommissionProfileID("");
		data1.setCompany("");
		data1.setContactNumber("");
		data1.setContactPerson(faker.name().fullName());
		data1.setControlGroup("N");
		data1.setCountry("");
		data1.setDesignation("");
		data1.setDocumentNo("");
		data1.setDocumentType("");
		data1.setDomain(_masterVO.getProperty("domainCode"));
		data1.setEmailid(_masterVO.getProperty("email"));
		data1.setEmpcode("");
		extCode = UniqueChecker.UC_EXTCODE();
		data1.setExternalCode(extCode);
		data1.setExtnwcode(_masterVO.getProperty("networkCode"));
		data1.setFax("");
		String firstName = faker.name().firstName();
		data1.setFirstName(firstName);
		data1.setGeographicalDomain(_masterVO.getProperty("geographicalDomain"));
		data1.setGeographyCode(geographyCode);
		data1.setInsuspend("N");
		data1.setLanguage("en_US");
		String lastName = faker.name().lastName();
		data1.setLastName(lastName);
		data1.setLatitude("");
		data1.setLmsProfileId("");
		data1.setLongitude("");
		data1.setLowbalalertother("Y");
		data1.setLowbalalertparent("Y");
		data1.setLowbalalertself("Y");

		CreateChannelUserMsisdn msisdnDetails = new CreateChannelUserMsisdn();
		phNo = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		msisdnDetails.setPhoneNo(phNo);
		msisdnDetails.setPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setConfirmPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setStkProfile(stkProfile);
		List<CreateChannelUserMsisdn> msisdn = new ArrayList<CreateChannelUserMsisdn>();
		msisdn.add(msisdnDetails);
		data1.setMsisdn(msisdn);
		data1.setOtherEmail("");
		data1.setOutletCode("");
		data1.setSubOutletCode("");
		data1.setOutletCode("");
		data1.setOwnerUser(ownerUser);
		data1.setParentCategory(_masterVO.getProperty("domainCode"));
		data1.setParentUser("");
		data1.setPaymentType(_masterVO.getProperty("paymentType"));
		data1.setRoleType("N");
		data1.setRoles("");
		data1.setServices(_masterVO.getProperty("services"));
		data1.setShortName(faker.name().lastName());
		data1.setSsn("");
		data1.setState("");
		data1.setSubscriberCode("");
		data1.setTransferProfile("");
		data1.setTransferRuleType("");
		data1.setUserCatCode(userCatCode);
		data1.setUserCode(phNo);
		data1.setUserName(firstName + " " + lastName);
		data1.setUserNamePrefix("CMPY");
		data1.setUsergrade("");
		data1.setVoucherTypes("");
		loginId = UniqueChecker.UC_LOGINID();
		data1.setWebloginid(loginId);
		data1.setWebpassword(_masterVO.getProperty("NewPassword"));
		data1.setConfirmwebpassword(_masterVO.getProperty("NewPassword"));
	}

	public void setUpData(String geographyCode, String stkProfile) {

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
		data.setEmailid(data1.getEmailid());
		data.setEmpcode("");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setFax("");
		data.setFirstName(data1.getFirstName());
		data.setGeographyCode(geographyCode);
		data.setInsuspend("N");
		data.setLanguage("en_US");
		data.setLastName(data1.getLastName());
		data.setLatitude("");
		data.setLmsProfileId("");
		data.setLongitude("");
		data.setLowbalalertother("Y");
		data.setLowbalalertparent("Y");
		data.setLowbalalertself("Y");

		ModifyChannelUserMsisdn msisdnDetails = new ModifyChannelUserMsisdn();
		msisdnDetails.setConfirmpin(_masterVO.getProperty("PIN"));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setPhoneNo(phNo);
		msisdnDetails.setPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setStkProfile(stkProfile);
		List<ModifyChannelUserMsisdn> msisdn = new ArrayList<ModifyChannelUserMsisdn>();
		msisdn.add(msisdnDetails);
		data.setMsisdn(msisdn);
		data.setNewExternalcode(extCode);
		data.setOtherEmail("");
		data.setOutletCode("");
		data.setOutsuspend("");
		data.setPaymentType("");
		data.setRoleType("N");
		data.setRoles("");
		data.setServices("");
		data.setShortName(data1.getShortName());
		data.setSsn("");
		data.setState("");
		data.setSubOutletCode("");
		data.setSubscriberCode("");
		data.setTransferProfile("");
		data.setTransferRuleType("");
		data.setUserCode("");
		data.setUserName(data1.getUserName());
		data.setUserNamePrefix("CMPY");
		data.setUsergrade("");
		data.setVoucherTypes("");
		data.setWebloginid(data1.getWebloginid());
		data.setWebpassword("");
		data.setConfirmwebpassword("");

	}

	public void updateMsisdnDetails(String phNumber, String stkProfile) {
		ModifyChannelUserMsisdn msisdnDetails = new ModifyChannelUserMsisdn();
		msisdnDetails.setPhoneNo(phNumber);
		msisdnDetails.setPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setConfirmpin(_masterVO.getProperty("PIN"));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setStkProfile(stkProfile);

		List<ModifyChannelUserMsisdn> msisdn = new ArrayList<ModifyChannelUserMsisdn>();
		msisdn.add(msisdnDetails);
		data.setMsisdn(msisdn);
	}

	public void setMultipleMsisdnDetails(String stkProfile) {
		ModifyChannelUserMsisdn msisdnDetails = new ModifyChannelUserMsisdn();
		msisdnDetails.setPhoneNo(phNo);
		msisdnDetails.setPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setConfirmpin(_masterVO.getProperty("PIN"));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setStkProfile(stkProfile);

		ModifyChannelUserMsisdn msisdnDetails1 = new ModifyChannelUserMsisdn();
		msisdnDetails1.setPhoneNo(UniqueChecker.generate_subscriber_MSISDN("Prepaid"));
		msisdnDetails1.setPin(_masterVO.getProperty("PIN"));
		msisdnDetails1.setConfirmpin(_masterVO.getProperty("PIN"));
		msisdnDetails1.setDescription("");
		msisdnDetails1.setIsprimary("N");
		msisdnDetails1.setStkProfile(stkProfile);

		List<ModifyChannelUserMsisdn> msisdn = new ArrayList<ModifyChannelUserMsisdn>();
		msisdn.add(msisdnDetails);
		msisdn.add(msisdnDetails1);
		data.setMsisdn(msisdn);
	}

	protected static String accessToken;

	public void callingCreateChannelUserAPI(String geographyCode, String userCatCode, String ownerUser,
			String stkProfile) {
		createChannelUserData(geographyCode, userCatCode, ownerUser, stkProfile);
		channelAdminCreateChannelUserReqPojo.setData(data1);
		ChannelAdmin_CreateChannelUserAPI channelAdminCreateChannelUserAPI = new ChannelAdmin_CreateChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminCreateChannelUserAPI.addBodyParam(channelAdminCreateChannelUserReqPojo);
		channelAdminCreateChannelUserAPI.logRequestBody(channelAdminCreateChannelUserReqPojo);
		channelAdminCreateChannelUserAPI.setExpectedStatusCode(200);
		channelAdminCreateChannelUserAPI.perform();
		try {
			channelAdminCreateChannelUserResPojo = channelAdminCreateChannelUserAPI
					.getAPIResponseAsPOJO(ChannelAdminCreateChannelUserResPojo.class);
		} catch (IOException e) {
			e.printStackTrace();
			Log.info(e.getMessage());
		}
		Log.info("Exiting from callingCreateChannelUserAPI() method");
	}

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

	public void setTestInitialDetails(String methodName, String loginID, String password, String categoryName,
			String caseId) throws Exception {
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID(caseId);
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
	}

	public void executeModifyChannelUserAPI(int expectedStatusCode) throws IOException {
		Log.info("Entering executeModifyChannelUserAPI()");
		ChannelAdmin_ModifyChannelUserAPI channelAdminModifyChannelUserAPI = new ChannelAdmin_ModifyChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminModifyChannelUserAPI.setIdType("USERLOGIID");
		channelAdminModifyChannelUserAPI.setIdValue(loginId);
		channelAdminModifyChannelUserAPI.addBodyParam(channelAdminModifyChannelUserReqPojo);
		channelAdminModifyChannelUserAPI.logRequestBody(channelAdminModifyChannelUserReqPojo);
		channelAdminModifyChannelUserAPI.setExpectedStatusCode(expectedStatusCode);
		channelAdminModifyChannelUserAPI.perform();
		channelAdminModifyChannelUserResPojo = channelAdminModifyChannelUserAPI
				.getAPIResponseAsPOJO(ChannelAdminModifyChannelUserResPojo.class);
		Log.info("Exiting from executeModifyChannelUserAPI()");
	}

	@Test(dataProvider = "userData", priority = 1)
	@TestManager(TestKey = "PRETUPS-001")
	public void channelAdmin_ModifyChannelUser_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyChannelUser_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER01");
		callingCreateChannelUserAPI(_masterVO.getProperty("geography_code"),
				_masterVO.getProperty("distributorCatCode"), "", _masterVO.getProperty("distributorCatCode"));
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		data.setFirstName(faker.name().firstName());
		data.setShortName(faker.name().lastName());
		data.setUserName(data.getFirstName() + " " + data.getLastName());
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(200);
		String expectedFirstName = DBHandler.AccessHandler.getFirstNameByLoginId(loginId);
		String expectedShortName = DBHandler.AccessHandler.getShortNameByLoginId(loginId);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(data.getFirstName(), expectedFirstName);
		Assert.assertEquals(data.getShortName(), expectedShortName);
		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(message, "User " + data.getUserName() + " has been successfully updated.");
		Assertion.assertEquals(message, "User " + data.getUserName() + " has been successfully updated.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void channelAdmin_ModifyChannelUser_BlankWebLoginId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyChannelUser_BlankWebLoginId";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER02");
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		data.setWebloginid("");
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(200);
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-003")
	public void channelAdmin_ModifyChannelUser_BlankMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyChannelUser_BlankMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER03");
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		updateMsisdnDetails("", _masterVO.getProperty("domainCode"));
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "User Registration failed.");
		Assertion.assertEquals(message, "User Registration failed.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void channelAdmin_ModifyChannelUser_AlphaMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyChannelUser_AlphaMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER04");
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		updateMsisdnDetails(new RandomGeneration().randomAlphaNumeric(10), _masterVO.getProperty("domainCode"));
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Invalid MSISDN");
		Assertion.assertEquals(message, "Invalid MSISDN");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-005")
	public void channelAdmin_ModifyChannelUser_BlankExtNumber(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyChannelUser_BlankExtNumber";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER05");
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		data.setNewExternalcode("");
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "User's EXTERNALCODE is either blank or length is more than allowed.");
		Assertion.assertEquals(message, "User's EXTERNALCODE is either blank or length is more than allowed.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 6)
	@TestManager(TestKey = "PRETUPS-006")
	public void channelAdmin_ModifyChannelUser_SpecialCharExtNumber(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyChannelUser_SpecialCharExtNumber";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER06");
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		data.setNewExternalcode("@#%#$%#");
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Special chars not allowed in external code.");
		Assertion.assertEquals(message, "Special chars not allowed in external code.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 7)
	@TestManager(TestKey = "PRETUPS-007")
	public void channelAdmin_ModifyChannelUser_BlankEmailId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyChannelUser_BlankEmailId";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER07");
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		data.setEmailid("");
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Email id is mandatory.");
		Assertion.assertEquals(message, "Email id is mandatory.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 8)
	@TestManager(TestKey = "PRETUPS-008")
	public void channelAdmin_ModifyChannelUser_InvalidEmailId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyChannelUser_InvalidEmailId";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER08");
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		data.setEmailid(new RandomGeneration().randomAlphaNumeric(14));
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Invalid Email ID");
		Assertion.assertEquals(message, "Invalid Email ID");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 9)
	@TestManager(TestKey = "PRETUPS-009")
	public void channelAdmin_ModifyChannelUser_BlankFirstName(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyChannelUser_BlankFirstName";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER09");
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		data.setFirstName("");
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "First Name is mandatory.");
		Assertion.assertEquals(message, "First Name is mandatory.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 10)
	@TestManager(TestKey = "PRETUPS-010")
	public void channelAdmin_ModifyChannelUser_BlankShortName(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyChannelUser_BlankShortName";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER10");
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		data.setShortName("");
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Short Name is mandatory.");
		Assertion.assertEquals(message, "Short Name is mandatory.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 11)
	@TestManager(TestKey = "PRETUPS-011")
	public void channelAdmin_ModifyDealerUser_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyDealerUser_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER11");
		callingCreateChannelUserAPI(_masterVO.getProperty("dealer_geography_code"),
				_masterVO.getProperty("dealerCatCode"), _masterVO.getProperty("ownerUser"),
				_masterVO.getProperty("dealerCatCode"));
		setUpData(_masterVO.getProperty("dealer_geography_code"), _masterVO.getProperty("dealerCatCode"));
		data.setFirstName(faker.name().firstName());
		data.setShortName(faker.name().lastName());
		data.setUserName(data.getFirstName() + " " + data.getLastName());
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String expectedFirstName = DBHandler.AccessHandler.getFirstNameByLoginId(loginId);
		String expectedShortName = DBHandler.AccessHandler.getShortNameByLoginId(loginId);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(data.getFirstName(), expectedFirstName);
		Assert.assertEquals(data.getShortName(), expectedShortName);
		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(message, "User " + data.getUserName() + " has been successfully updated.");
		Assertion.assertEquals(message, "User " + data.getUserName() + " has been successfully updated.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 12)
	@TestManager(TestKey = "PRETUPS-012")
	public void channelAdmin_ModifyAgentUser_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyAgentUser_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER12");
		callingCreateChannelUserAPI(_masterVO.getProperty("agent_geography_code"),
				_masterVO.getProperty("agentCatCode"), _masterVO.getProperty("ownerUser"),
				_masterVO.getProperty("agentCatCode"));
		setUpData(_masterVO.getProperty("agent_geography_code"), _masterVO.getProperty("agentCatCode"));
		data.setFirstName(faker.name().firstName());
		data.setShortName(faker.name().lastName());
		data.setUserName(data.getFirstName() + " " + data.getLastName());
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String expectedFirstName = DBHandler.AccessHandler.getFirstNameByLoginId(loginId);
		String expectedShortName = DBHandler.AccessHandler.getShortNameByLoginId(loginId);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(data.getFirstName(), expectedFirstName);
		Assert.assertEquals(data.getShortName(), expectedShortName);
		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(message, "User " + data.getUserName() + " has been successfully updated.");
		Assertion.assertEquals(message, "User " + data.getUserName() + " has been successfully updated.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 13)
	@TestManager(TestKey = "PRETUPS-011")
	public void channelAdmin_ModifyRetailerUser_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyDealerUser_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER13");
		createChannelUserData(_masterVO.getProperty("retailer_geography_code"),
				_masterVO.getProperty("retailerCatCode"), _masterVO.getProperty("ownerUser"),
				_masterVO.getProperty("retailerCatCode"));
		data1.setOutletCode(_masterVO.getProperty("outletCode"));
		data1.setSubOutletCode(_masterVO.getProperty("subOutletCode"));
		channelAdminCreateChannelUserReqPojo.setData(data1);
		ChannelAdmin_CreateChannelUserAPI channelAdminCreateChannelUserAPI = new ChannelAdmin_CreateChannelUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminCreateChannelUserAPI.addBodyParam(channelAdminCreateChannelUserReqPojo);
		channelAdminCreateChannelUserAPI.logRequestBody(channelAdminCreateChannelUserReqPojo);
		channelAdminCreateChannelUserAPI.setExpectedStatusCode(200);
		channelAdminCreateChannelUserAPI.perform();
		channelAdminCreateChannelUserResPojo = channelAdminCreateChannelUserAPI
				.getAPIResponseAsPOJO(ChannelAdminCreateChannelUserResPojo.class);
		setUpData(_masterVO.getProperty("retailer_geography_code"), _masterVO.getProperty("retailerCatCode"));
		data.setFirstName(faker.name().firstName());
		data.setShortName(faker.name().lastName());
		data.setUserName(data.getFirstName() + " " + data.getLastName());
		data.setOutletCode(_masterVO.getProperty("outletCode"));
		data.setSubOutletCode(_masterVO.getProperty("subOutletCode"));
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String expectedFirstName = DBHandler.AccessHandler.getFirstNameByLoginId(loginId);
		String expectedShortName = DBHandler.AccessHandler.getShortNameByLoginId(loginId);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(data.getFirstName(), expectedFirstName);
		Assert.assertEquals(data.getShortName(), expectedShortName);
		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(message, "User " + data.getUserName() + " has been successfully updated.");
		Assertion.assertEquals(message, "User " + data.getUserName() + " has been successfully updated.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 14)
	@TestManager(TestKey = "PRETUPS-014")
	public void channelAdmin_UpdateMultipleMsisdn_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_UpdateMultipleMsisdn_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER14");
		callingCreateChannelUserAPI(_masterVO.getProperty("geography_code"),
				_masterVO.getProperty("distributorCatCode"), "", _masterVO.getProperty("distributorCatCode"));
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		setMultipleMsisdnDetails(_masterVO.getProperty("distributorCatCode"));
		data.setUserName(data.getFirstName() + " " + data.getLastName());
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		String message = channelAdminModifyChannelUserResPojo.getMessage();
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(message, "User " + data.getUserName() + " has been successfully updated.");
		Assertion.assertEquals(message, "User " + data.getUserName() + " has been successfully updated.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 15)
	@TestManager(TestKey = "PRETUPS-015")
	public void channelAdmin_UpdateRoles_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_UpdateRoles_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMMODCHNLUSER15");
		callingCreateChannelUserAPI(_masterVO.getProperty("geography_code"),
				_masterVO.getProperty("distributorCatCode"), "", _masterVO.getProperty("distributorCatCode"));
		setUpData(_masterVO.getProperty("geography_code"), _masterVO.getProperty("distributorCatCode"));
		data.setRoles(_masterVO.getProperty("addRoles"));
		data.setServices(_masterVO.getProperty("addServices"));
		channelAdminModifyChannelUserReqPojo.setData(data);
		executeModifyChannelUserAPI(400);
		int statusCode = channelAdminModifyChannelUserResPojo.getStatus();
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
}