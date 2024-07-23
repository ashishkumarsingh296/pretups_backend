package restassuredapi.test;

import java.io.IOException;
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
import com.classes.Login;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import restassuredapi.api.approvalSuspendChUAPI.ApprovalSuspendChUAPI;
import restassuredapi.api.channelAdmin_CreateChannelUser.ChannelAdmin_CreateChannelUserAPI;
import restassuredapi.api.channelAdmin_DeleteChannelUser.ChannelAdminDeleteChannelUserApi;
import restassuredapi.api.channelUserApproval.ChannelUserApprovalApi;
import restassuredapi.api.channelUserApprovalList.ChannelUserApprovalListApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.approvalSuspendReqpojo.ApprovalSuspendReqpojo;
import restassuredapi.pojo.approvalSuspendRespojo.ApprovalSuspendRespojo;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.ChannelAdminCreateChannelUserReqPojo;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.CreateChannelUserVO;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.CreateChannelUserMsisdn;
import restassuredapi.pojo.channelAdminCreateChannelUserResponsePojo.ChannelAdminCreateChannelUserResPojo;
import restassuredapi.pojo.channelUserApprovalListRequestPojo.ChannelUserApprovalListRequestPojo;
import restassuredapi.pojo.channelUserApprovalListResponsePojo.ChannelUserApprovalListResponsePojo;
import restassuredapi.pojo.channelUserApprovalRequestPojo.ChannelUserApprovalMsisdn;
import restassuredapi.pojo.channelUserApprovalRequestPojo.ChannelUserApprovalRequestPojo;
import restassuredapi.pojo.channelUserApprovalResponsePojo.ChannelUserApprovalResponsePojo;
import restassuredapi.pojo.o2cinitiateoptreqpojo.O2CInitiateByOptRequest;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.userdeleteresponsepojo.UserDeleteResponsePojo;

@ModuleManager(name = Module.DELETE_CHANNEL_USER)
public class ChannelAdmin_DeleteChannelUser extends BaseTest {

	static String moduleCode;
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	ChannelAdminCreateChannelUserReqPojo channelAdminCreateChannelUserReqPojo = new ChannelAdminCreateChannelUserReqPojo();
	ChannelAdminCreateChannelUserResPojo channelAdminCreateChannelUserResPojo = new ChannelAdminCreateChannelUserResPojo();
	ChannelUserApprovalListRequestPojo channelUserApprovalListRequestPojo = new ChannelUserApprovalListRequestPojo();
	ChannelUserApprovalListResponsePojo channelUserApprovalListResponsePojo = new ChannelUserApprovalListResponsePojo();
	ChannelUserApprovalRequestPojo channelUserApprovalRequestPojo = new ChannelUserApprovalRequestPojo();
	ChannelUserApprovalResponsePojo channelUserApprovalResponsePojo = new ChannelUserApprovalResponsePojo();
	UserDeleteResponsePojo userDeleteResponsePojo = new UserDeleteResponsePojo();
	ApprovalSuspendReqpojo approvalSuspendReqpojo = new ApprovalSuspendReqpojo();
	ApprovalSuspendRespojo approvalSuspendRespojo = new ApprovalSuspendRespojo();
	O2CInitiateByOptRequest o2CInitiateByOptRequest = new O2CInitiateByOptRequest();
	Faker faker = new Faker();

	String MasterSheetPath;
	CreateChannelUserVO data = new CreateChannelUserVO();
	restassuredapi.pojo.channelUserApprovalRequestPojo.ChannelUserApprovalVO data1 = new restassuredapi.pojo.channelUserApprovalRequestPojo.ChannelUserApprovalVO();
	Login login = new Login();
	String loginId = null;
	String extCode = null;
	String firstName = null;
	String shortName = null;
	String username = null;
	String emailId = null;
	String phNo = null;
	
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String, String> transferDetails = new HashMap<String, String>();

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		MasterSheetPath = _masterVO.getProperty("DataProvider");
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

	public void setUpChannelUserData() {

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

	public void setUpChannelUserApprovalList(String loginId) {

		channelUserApprovalListRequestPojo.setApprovalLevel(_masterVO.getProperty("ApprovalLevelOne"));
		channelUserApprovalListRequestPojo.setCategory("ALL");
		channelUserApprovalListRequestPojo.setDomain("ALL");
		channelUserApprovalListRequestPojo.setGeography("ALL");
		channelUserApprovalListRequestPojo.setLoggedInUserUserid(DBHandler.AccessHandler.getUserIdLoginID(loginId));
		channelUserApprovalListRequestPojo.setLoginID(channelAdminCreateChannelUserReqPojo.getData().getWebloginid());
		channelUserApprovalListRequestPojo.setMobileNumber("");
		channelUserApprovalListRequestPojo.setReqTab(_masterVO.getProperty("reqTab1"));	
	}

	public void setUpChannelUserApprovalData() {
		channelUserApprovalRequestPojo.setApprovalLevel("");
		channelUserApprovalRequestPojo.setApproveUserID("");
		data1.setAddress1("");
		data1.setAddress2("");
		data1.setAllowedTimeFrom("");
		data1.setAllowedTimeTo("");
		data1.setAlloweddays("");
		data1.setAllowedip("");
		data1.setAppointmentdate("");
		data1.setCity("");
		data1.setCommissionProfileID(_masterVO.getProperty("commissionProfileID"));
		data1.setCompany("");
		data1.setContactNumber("");
		data1.setContactPerson("");
		data1.setControlGroup("N");
		data1.setCountry("");
		data1.setDesignation("");
		data1.setDocumentNo("");
		data1.setDocumentType("");
		data1.setDomain(_masterVO.getProperty("domainCode"));
		data1.setEmailid(_masterVO.getProperty("email"));
		data1.setEmpcode("");
		data1.setExternalCode(data.getExternalCode());
		data1.setExtnwcode("NG");
		data1.setFax("");
		data1.setFirstName(data.getFirstName());
		data1.setGeographicalDomain("DELHI");
		data1.setGeographyCode(_masterVO.getProperty("geography_code"));
		data1.setInsuspend("N");
		data1.setLanguage("en_US");
		data1.setLastName(data.getLastName());
		data1.setLatitude("");
		data1.setLmsProfileId("");
		data1.setLongitude("");
		data1.setLowbalalertother("Y");
		data1.setLowbalalertparent("Y");
		data1.setLowbalalertself("Y");

		ChannelUserApprovalMsisdn msisdnDetails = new ChannelUserApprovalMsisdn();
		msisdnDetails.setPhoneNo(phNo);
		msisdnDetails.setPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setConfirmPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setStkProfile(_masterVO.getProperty("categoryCode"));
		List<ChannelUserApprovalMsisdn> msisdn = new ArrayList<ChannelUserApprovalMsisdn>();
		msisdn.add(msisdnDetails);
		data1.setMsisdn(msisdn);
		data1.setOtherEmail("");
		data1.setOutletCode("");
		data1.setSubOutletCode("");
		data1.setOutletCode("");
		data1.setOwnerUser("");
		data1.setParentCategory(_masterVO.getProperty("domainCode"));
		data1.setParentUser("ROOT");
		data1.setPaymentType(_masterVO.getProperty("paymentType"));
		data1.setRoleType("N");
		data1.setRoles(_masterVO.getProperty("roles"));
		data1.setServices(_masterVO.getProperty("services"));
		data1.setShortName(data.getLastName());
		data1.setSsn("");
		data1.setState("");
		data1.setSubscriberCode("");
		data1.setTransferProfile(_masterVO.getProperty("transferProfile"));
		data1.setTransferRuleType(_masterVO.getProperty("transferRuleType"));
		data1.setUserCatCode(_masterVO.getProperty("domainCode"));
		data1.setUserCode(phNo);
		data1.setUserName(data.getUserName());
		data1.setUserNamePrefix("CMPY");
		data1.setUsergrade(_masterVO.getProperty("usergrade"));
		data1.setVoucherTypes("");
		data1.setWebloginid(loginId);
		data1.setWebpassword(_masterVO.getProperty("NewPassword"));
		data1.setConfirmwebpassword(_masterVO.getProperty("NewPassword"));
		channelUserApprovalRequestPojo.setUserAction("ADD");
	}

	public ApprovalSuspendReqpojo getReqBody(String action, String loginId, String remarks, String RequestType) {
		ApprovalSuspendReqpojo approvalSuspendReqpojo = new ApprovalSuspendReqpojo();
		approvalSuspendReqpojo.setAction(action);
		approvalSuspendReqpojo.setLoginId(loginId);
		approvalSuspendReqpojo.setRemarks(remarks);
		approvalSuspendReqpojo.setRequestType(RequestType);
		return approvalSuspendReqpojo;
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

	public void setTestInitialDetails(String methodName, String loginID, String password, String categoryName,
			String caseId) throws Exception {
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID(caseId);
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
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
	
	public void executeChannelUserApprovalListApi() throws IOException {
		Log.info("Entering executeChannelUserApprovalListApi()");
		ChannelUserApprovalListApi channelUserApprovalListApi = new ChannelUserApprovalListApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelUserApprovalListApi.addBodyParam(channelUserApprovalListRequestPojo);
		channelUserApprovalListApi.logRequestBody(channelUserApprovalListRequestPojo);
		channelUserApprovalListApi.setExpectedStatusCode(200);
		channelUserApprovalListApi.perform();
		channelUserApprovalListResponsePojo = channelUserApprovalListApi
				.getAPIResponseAsPOJO(ChannelUserApprovalListResponsePojo.class);
		Log.info("Exiting executeChannelUserApprovalListApi()");
	}
	
	public void executeChannelUserApprovalApi() throws IOException {
		ChannelUserApprovalApi channelUserApprovalApi = new ChannelUserApprovalApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		channelUserApprovalApi.addBodyParam(channelUserApprovalRequestPojo);
		channelUserApprovalApi.logRequestBody(channelUserApprovalRequestPojo);
		channelUserApprovalApi.setExpectedStatusCode(200);
		channelUserApprovalApi.perform();
		channelUserApprovalResponsePojo = channelUserApprovalApi
				.getAPIResponseAsPOJO(ChannelUserApprovalResponsePojo.class);
	}
	
	@Test(dataProvider = "userData", priority = 1)
	@TestManager(TestKey = "PRETUPS-001")
	public void channelAdmin_DeleteChannelUserViaLoginId_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_DeleteChannelUserViaLoginId_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMDELCHNLUSER01");
		setUpChannelUserData();
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		setUpChannelUserApprovalList(loginID);
		executeChannelUserApprovalListApi();
		String count = DBHandler.AccessHandler
				.getSystemPreferenceDefaultValue(_masterVO.getProperty("approvalLevelCount"));
		int approvalLevelCount = Integer.parseInt(count);
		for (int i = 0; i < approvalLevelCount; i++) {
			setUpChannelUserApprovalData();
			if (i == 0) {
				channelUserApprovalRequestPojo.setApprovalLevel(_masterVO.getProperty("ApprovalLevelOne"));
			} else {
				channelUserApprovalRequestPojo.setApprovalLevel(_masterVO.getProperty("ApprovalLevelTwo"));
			}
			String userId = DBHandler.AccessHandler.getUserIdFromMsisdn(phNo);
			channelUserApprovalRequestPojo.setApproveUserID(userId);
			channelUserApprovalRequestPojo.setData(data1);
			executeChannelUserApprovalApi();
		}

		ChannelAdminDeleteChannelUserApi channelAdminDeleteChannelUserApi = new ChannelAdminDeleteChannelUserApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminDeleteChannelUserApi.setExtnwcode("NG");
		channelAdminDeleteChannelUserApi.setIdType("LOGINID");
		channelAdminDeleteChannelUserApi.setidValue(channelAdminCreateChannelUserReqPojo.getData().getWebloginid());
		channelAdminDeleteChannelUserApi.setRemarks("Deleting this user");
		channelAdminDeleteChannelUserApi.setExpectedStatusCode(200);
		channelAdminDeleteChannelUserApi.perform();
		channelAdminDeleteChannelUserApi.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);
		ApprovalSuspendChUAPI approvalSuspendChUAPI = new ApprovalSuspendChUAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		approvalSuspendReqpojo = getReqBody("A", loginId, "Deleting User", "DELETEAPPROVAL");
		approvalSuspendChUAPI.setContentType(_masterVO.getProperty("contentType"));
		approvalSuspendChUAPI.addBodyParam(approvalSuspendReqpojo);
		ObjectMapper mapper3 = new ObjectMapper();
		String reqBody2 = mapper3.writerWithDefaultPrettyPrinter().writeValueAsString(approvalSuspendReqpojo);
		Log.info("Request body: " + reqBody2);
		approvalSuspendChUAPI.setExpectedStatusCode(200);
		approvalSuspendChUAPI.perform();
		approvalSuspendRespojo = approvalSuspendChUAPI.getAPIResponseAsPOJO(ApprovalSuspendRespojo.class);
		String status = approvalSuspendRespojo.getStatus();

		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void channelAdmin_DeleteChannelUserViaMsisdn_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_DeleteChannelUserViaMsisdn_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMDELCHNLUSER02");
		setUpChannelUserData();
		channelAdminCreateChannelUserReqPojo.setData(data);
		executeCreateChannelUserAPI();
		setUpChannelUserApprovalList(loginID);
		executeChannelUserApprovalListApi();
		ChannelUserApprovalApi channelUserApprovalApi = new ChannelUserApprovalApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		for (int i = 0; i < 2; i++) {
			setUpChannelUserApprovalData();
			if (i == 0) {
				channelUserApprovalRequestPojo.setApprovalLevel(_masterVO.getProperty("ApprovalLevelOne"));
			} else {
				channelUserApprovalRequestPojo.setApprovalLevel(_masterVO.getProperty("ApprovalLevelTwo"));
			}
			String userId = DBHandler.AccessHandler.getUserIdFromMsisdn(phNo);
			channelUserApprovalRequestPojo.setApproveUserID(userId);
			channelUserApprovalRequestPojo.setData(data1);
			channelUserApprovalApi.addBodyParam(channelUserApprovalRequestPojo);
			ObjectMapper mapper2 = new ObjectMapper();
			String reqBody2 = mapper2.writerWithDefaultPrettyPrinter()
					.writeValueAsString(channelUserApprovalRequestPojo);
			Log.info("Request body: " + reqBody2);
			channelUserApprovalApi.setExpectedStatusCode(200);
			channelUserApprovalApi.perform();
			channelUserApprovalResponsePojo = channelUserApprovalApi
					.getAPIResponseAsPOJO(ChannelUserApprovalResponsePojo.class);
		}

		ChannelAdminDeleteChannelUserApi channelAdminDeleteChannelUserApi = new ChannelAdminDeleteChannelUserApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminDeleteChannelUserApi.setExtnwcode("NG");
		channelAdminDeleteChannelUserApi.setIdType("MSISDN");
		channelAdminDeleteChannelUserApi.setRemarks("Deleting this channel user");
		channelAdminDeleteChannelUserApi.setidValue(channelAdminCreateChannelUserReqPojo.getData().getMsisdn().get(0).getPhoneNo());
		channelAdminDeleteChannelUserApi.setExpectedStatusCode(200);
		channelAdminDeleteChannelUserApi.perform();
		channelAdminDeleteChannelUserApi.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);
		ApprovalSuspendChUAPI approvalSuspendChUAPI = new ApprovalSuspendChUAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		approvalSuspendReqpojo = getReqBody("A", loginId, "Deleting User", "DELETEAPPROVAL");
		approvalSuspendChUAPI.setContentType(_masterVO.getProperty("contentType"));
		approvalSuspendChUAPI.addBodyParam(approvalSuspendReqpojo);
		ObjectMapper mapper3 = new ObjectMapper();
		String reqBody3 = mapper3.writerWithDefaultPrettyPrinter().writeValueAsString(approvalSuspendReqpojo);
		Log.info("Request body: " + reqBody3);
		approvalSuspendChUAPI.setExpectedStatusCode(200);
		approvalSuspendChUAPI.perform();
		approvalSuspendRespojo = approvalSuspendChUAPI.getAPIResponseAsPOJO(ApprovalSuspendRespojo.class);
		String status = approvalSuspendRespojo.getStatus();

		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-003")
	public void channelAdmin_DeleteChannelUserViaInvalidMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_DeleteChannelUserViaInvalidMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMDELCHNLUSER03");
		ChannelAdminDeleteChannelUserApi channelAdminDeleteChannelUserApi = new ChannelAdminDeleteChannelUserApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminDeleteChannelUserApi.setExtnwcode("NG");
		channelAdminDeleteChannelUserApi.setIdType("MSISDN");
		channelAdminDeleteChannelUserApi.setRemarks("Deleting this channel user");
		channelAdminDeleteChannelUserApi.setidValue(new RandomGeneration().randomAlphaNumeric(8));
		channelAdminDeleteChannelUserApi.setExpectedStatusCode(400);
		channelAdminDeleteChannelUserApi.perform();
		userDeleteResponsePojo = channelAdminDeleteChannelUserApi.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);
		String status = userDeleteResponsePojo.getStatus();
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void channelAdmin_DeleteChannelUserViaInvalidLoginId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_DeleteChannelUserViaInvalidLoginId";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMDELCHNLUSER04");
		ChannelAdminDeleteChannelUserApi channelAdminDeleteChannelUserApi = new ChannelAdminDeleteChannelUserApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminDeleteChannelUserApi.setExtnwcode("NG");
		channelAdminDeleteChannelUserApi.setIdType("LOGINID");
		channelAdminDeleteChannelUserApi.setRemarks("Deleting this channel user");
		channelAdminDeleteChannelUserApi.setidValue(new RandomGeneration().randomAlphaNumeric(8));
		channelAdminDeleteChannelUserApi.setExpectedStatusCode(400);
		channelAdminDeleteChannelUserApi.perform();
		userDeleteResponsePojo = channelAdminDeleteChannelUserApi.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);
		String status = userDeleteResponsePojo.getStatus();
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-006")
	public void channelAdmin_DeleteChannelUserWithPendingTransaction(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_DeleteChannelUserWithPendingTransaction";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMDELCHNLUSER05");
		ChannelAdminDeleteChannelUserApi channelAdminDeleteChannelUserApi = new ChannelAdminDeleteChannelUserApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminDeleteChannelUserApi.setExtnwcode("NG");
		channelAdminDeleteChannelUserApi.setIdType("LOGINID");
		channelAdminDeleteChannelUserApi.setRemarks("Deleting this channel user");
		channelAdminDeleteChannelUserApi.setidValue("AUT_59271");
		channelAdminDeleteChannelUserApi.setExpectedStatusCode(400);
		channelAdminDeleteChannelUserApi.perform();
		userDeleteResponsePojo = channelAdminDeleteChannelUserApi.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);
		String status = userDeleteResponsePojo.getStatus();
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData", priority = 6)
	@TestManager(TestKey = "PRETUPS-007")
	public void channelAdmin_DeleteParentWithActiveChild(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_DeleteChildUserWithActiveParent";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMDELCHNLUSER06");
		ChannelAdminDeleteChannelUserApi channelAdminDeleteChannelUserApi = new ChannelAdminDeleteChannelUserApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminDeleteChannelUserApi.setExtnwcode("NG");
		channelAdminDeleteChannelUserApi.setIdType("LOGINID");
		channelAdminDeleteChannelUserApi.setRemarks("Deleting this channel user");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		channelAdminDeleteChannelUserApi.setidValue(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 1));
		channelAdminDeleteChannelUserApi.setExpectedStatusCode(400);
		channelAdminDeleteChannelUserApi.perform();
		userDeleteResponsePojo = channelAdminDeleteChannelUserApi.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);
		String status = userDeleteResponsePojo.getStatus();
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData", priority = 7)
	@TestManager(TestKey = "PRETUPS-008")
	public void channelAdmin_DeleteParentWithChildMsisdnRestricted(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_DeleteParentWithChildMsisdnRestricted";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMDELCHNLUSER07");
		ChannelAdminDeleteChannelUserApi channelAdminDeleteChannelUserApi = new ChannelAdminDeleteChannelUserApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminDeleteChannelUserApi.setExtnwcode("NG");
		channelAdminDeleteChannelUserApi.setIdType("LOGINID");
		channelAdminDeleteChannelUserApi.setRemarks("Deleting this channel user");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		channelAdminDeleteChannelUserApi.setidValue(ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 1));
		channelAdminDeleteChannelUserApi.setExpectedStatusCode(400);
		channelAdminDeleteChannelUserApi.perform();
		userDeleteResponsePojo = channelAdminDeleteChannelUserApi.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);
		String status = userDeleteResponsePojo.getStatus();
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData", priority = 8)
	@TestManager(TestKey = "PRETUPS-009")
	public void channelAdmin_DeleteChannelUserWithPendingFOCApproval(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_DeleteChannelUserWithPendingFOCApproval";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMDELCHNLUSER08");
		ChannelAdminDeleteChannelUserApi channelAdminDeleteChannelUserApi = new ChannelAdminDeleteChannelUserApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminDeleteChannelUserApi.setExtnwcode("NG");
		channelAdminDeleteChannelUserApi.setIdType("LOGINID");
		channelAdminDeleteChannelUserApi.setRemarks("Deleting this channel user");
		channelAdminDeleteChannelUserApi.setidValue("AUT_47298");
		channelAdminDeleteChannelUserApi.setExpectedStatusCode(400);
		channelAdminDeleteChannelUserApi.perform();
		userDeleteResponsePojo = channelAdminDeleteChannelUserApi.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);
		String status = userDeleteResponsePojo.getStatus();
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData", priority = 9)
	@TestManager(TestKey = "PRETUPS-010")
	public void channelAdmin_DeleteChannelUserWithScheduledTransfer(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_DeleteChannelUserWithScheduledTransfer";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMDELCHNLUSER09");
		ChannelAdminDeleteChannelUserApi channelAdminDeleteChannelUserApi = new ChannelAdminDeleteChannelUserApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminDeleteChannelUserApi.setExtnwcode("NG");
		channelAdminDeleteChannelUserApi.setIdType("LOGINID");
		channelAdminDeleteChannelUserApi.setRemarks("Deleting this channel user");
		channelAdminDeleteChannelUserApi.setidValue("eric_dist");
		channelAdminDeleteChannelUserApi.setExpectedStatusCode(400);
		channelAdminDeleteChannelUserApi.perform();
		userDeleteResponsePojo = channelAdminDeleteChannelUserApi.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);
		String status = userDeleteResponsePojo.getStatus();
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData", priority = 10)
	@TestManager(TestKey = "PRETUPS-011")
	public void channelAdmin_DeleteChannelUserHavingBatchFOCScheduledTransfer(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_DeleteChannelUserHavingBatchFOCScheduledTransfer";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMDELCHNLUSER10");
		ChannelAdminDeleteChannelUserApi channelAdminDeleteChannelUserApi = new ChannelAdminDeleteChannelUserApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminDeleteChannelUserApi.setExtnwcode("NG");
		channelAdminDeleteChannelUserApi.setIdType("LOGINID");
		channelAdminDeleteChannelUserApi.setRemarks("Deleting this channel user");
		channelAdminDeleteChannelUserApi.setidValue("AUT_91950");
		channelAdminDeleteChannelUserApi.setExpectedStatusCode(400);
		channelAdminDeleteChannelUserApi.perform();
		userDeleteResponsePojo = channelAdminDeleteChannelUserApi.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);
		String status = userDeleteResponsePojo.getStatus();
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
}
