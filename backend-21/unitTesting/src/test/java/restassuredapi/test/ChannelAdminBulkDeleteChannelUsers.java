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
import com.classes.Login;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.FileOperations;
import com.utils.FileToBase64;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.approvalSuspendChUAPI.ApprovalSuspendChUAPI;
import restassuredapi.api.channelAdminBulkDeleteChannelUsers.ChannelAdminBulkDeleteChannelUsersAPI;
import restassuredapi.api.channelAdmin_CreateChannelUser.ChannelAdmin_CreateChannelUserAPI;
import restassuredapi.api.channelAdmin_DeleteChannelUser.ChannelAdminDeleteChannelUserApi;
import restassuredapi.api.channelUserApproval.ChannelUserApprovalApi;
import restassuredapi.api.channelUserApprovalList.ChannelUserApprovalListApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.approvalSuspendRespojo.ApprovalSuspendRespojo;
import restassuredapi.pojo.channelAdminBulkDeleteResponsePojo.ChannelAdminBulkDeleteResponsePojo;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.ChannelAdminCreateChannelUserReqPojo;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.CreateChannelUserVO;
import restassuredapi.pojo.channelAdminCreateChannelUserRequestPojo.CreateChannelUserMsisdn;
import restassuredapi.pojo.channelAdminCreateChannelUserResponsePojo.ChannelAdminCreateChannelUserResPojo;
import restassuredapi.pojo.channelUserApprovalListRequestPojo.ChannelUserApprovalListRequestPojo;
import restassuredapi.pojo.channelUserApprovalListResponsePojo.ChannelUserApprovalListResponsePojo;
import restassuredapi.pojo.channelUserApprovalRequestPojo.ChannelUserApprovalRequestPojo;
import restassuredapi.pojo.channelUserApprovalResponsePojo.ChannelUserApprovalResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.userdeleteresponsepojo.UserDeleteResponsePojo;

@ModuleManager(name = Module.BULK_DELETE_CHANNEL_USER)
public class ChannelAdminBulkDeleteChannelUsers extends BaseTest {

	static String moduleCode;
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	ChannelAdminCreateChannelUserReqPojo channelAdminCreateChannelUserReqPojo = new ChannelAdminCreateChannelUserReqPojo();
	ChannelAdminCreateChannelUserResPojo channelAdminCreateChannelUserResPojo = new ChannelAdminCreateChannelUserResPojo();
	ChannelUserApprovalListRequestPojo channelUserApprovalListRequestPojo = new ChannelUserApprovalListRequestPojo();
	ChannelUserApprovalListResponsePojo channelUserApprovalListResponsePojo = new ChannelUserApprovalListResponsePojo();
	ChannelUserApprovalRequestPojo channelUserApprovalRequestPojo = new ChannelUserApprovalRequestPojo();
	ChannelUserApprovalResponsePojo channelUserApprovalResponsePojo = new ChannelUserApprovalResponsePojo();
	ChannelAdminBulkDeleteResponsePojo channelAdminBulkDeleteResponsePojo = new ChannelAdminBulkDeleteResponsePojo();

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
	String MasterSheetPath;

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
		data.setCommissionProfileID(_masterVO.getProperty("commissionProfileID"));
		data.setCompany("");
		data.setContactNumber("");
		data.setContactPerson("");
		data.setControlGroup("N");
		data.setCountry("");
		data.setDesignation("");
		data.setDocumentNo("");
		data.setDocumentType("");
		data.setDomain("DIST");
		emailId = randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com";
		data.setEmailid(emailId);
		data.setEmpcode("");
		extCode = UniqueChecker.UC_EXTCODE();
		data.setExternalCode(extCode);
		data.setExtnwcode("NG");
		data.setFax("");
		firstName = randStr.randomAlphabets(5).toUpperCase();
		data.setFirstName(firstName);
		data.setGeographicalDomain("DELHI");
		data.setGeographyCode("HARYANA");
		data.setInsuspend("N");
		data.setLanguage("en_US");
		data.setLastName("");
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
		msisdnDetails.setStkProfile("DIST");
		List<CreateChannelUserMsisdn> msisdn = new ArrayList<CreateChannelUserMsisdn>();
		msisdn.add(msisdnDetails);
		data.setMsisdn(msisdn);
		data.setOtherEmail("");
		data.setOutletCode("");
		data.setSubOutletCode("");
		data.setOutletCode("");
		data.setOwnerUser("");
		data.setParentCategory("DIST");
		data.setParentUser("ROOT");
		data.setPaymentType("");
		data.setRoleType("N");
		data.setRoles(_masterVO.getProperty("roles"));
		data.setServices(_masterVO.getProperty("services"));
		shortName = new RandomGeneration().randomAlphabets(4);
		data.setShortName(shortName);
		data.setSsn("");
		data.setState("");
		data.setSubscriberCode("");
		data.setTransferProfile(_masterVO.getProperty("transferProfile"));
		data.setTransferRuleType(_masterVO.getProperty("transferRuleType"));
		data.setUserCatCode("DIST");
		data.setUserCode(phNo);
		username = randStr.randomAlphabets(5);
		data.setUserName(username);
		data.setUserNamePrefix("CMPY");
		data.setUsergrade(_masterVO.getProperty("usergrade"));
		data.setVoucherTypes("");
		loginId = UniqueChecker.UC_LOGINID();
		data.setWebloginid(loginId);
		data.setWebpassword(_masterVO.getProperty("NewPassword"));
		data.setConfirmwebpassword(_masterVO.getProperty("NewPassword"));
	}

	public void setUpChannelUserApprovalList(String loginId) {

		channelUserApprovalListRequestPojo.setApprovalLevel("");
		channelUserApprovalListRequestPojo.setCategory("ALL");
		channelUserApprovalListRequestPojo.setDomain("ALL");
		channelUserApprovalListRequestPojo.setGeography("ALL");
		channelUserApprovalListRequestPojo.setLoggedInUserUserid(DBHandler.AccessHandler.getUserIdLoginID(loginId));
		channelUserApprovalListRequestPojo.setLoginID("");
		channelUserApprovalListRequestPojo.setMobileNumber("");
		channelUserApprovalListRequestPojo.setReqTab("");
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
		data1.setEmailid(emailId);
		data1.setEmpcode("");
		data1.setExternalCode(extCode);
		data1.setExtnwcode("NG");
		data1.setFax("");
		data1.setFirstName(firstName);
		data1.setGeographicalDomain("DELHI");
		data1.setGeographyCode(_masterVO.getProperty("geography_code"));
		data1.setInsuspend("N");
		data1.setLanguage("en_US");
		data1.setLastName("");
		data1.setLatitude("");
		data1.setLmsProfileId("");
		data1.setLongitude("");
		data1.setLowbalalertother("Y");
		data1.setLowbalalertparent("Y");
		data1.setLowbalalertself("Y");

		restassuredapi.pojo.channelUserApprovalRequestPojo.ChannelUserApprovalMsisdn msisdnDetails = new restassuredapi.pojo.channelUserApprovalRequestPojo.ChannelUserApprovalMsisdn();
		msisdnDetails.setPhoneNo(phNo);
		msisdnDetails.setPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setConfirmPin(_masterVO.getProperty("PIN"));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setStkProfile("DIST");
		List<restassuredapi.pojo.channelUserApprovalRequestPojo.ChannelUserApprovalMsisdn> msisdn = new ArrayList<restassuredapi.pojo.channelUserApprovalRequestPojo.ChannelUserApprovalMsisdn>();
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
		data1.setShortName(shortName);
		data1.setSsn("");
		data1.setState("");
		data1.setSubscriberCode("");
		data1.setTransferProfile(_masterVO.getProperty("transferProfile"));
		data1.setTransferRuleType(_masterVO.getProperty("transferRuleType"));
		data1.setUserCatCode(_masterVO.getProperty("domainCode"));
		data1.setUserCode(phNo);
		data1.setUserName(username);
		data1.setUserNamePrefix("CMPY");
		data1.setUsergrade(_masterVO.getProperty("usergrade"));
		data1.setVoucherTypes("");
		data1.setWebloginid(loginId);
		data1.setWebpassword(_masterVO.getProperty("NewPassword"));
		data1.setConfirmwebpassword(_masterVO.getProperty("NewPassword"));
		channelUserApprovalRequestPojo.setUserAction("ADD");
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

	@Test(dataProvider = "userData", priority = 1)
	@TestManager(TestKey = "PRETUPS-001")
	public void channelAdmin_BulkDeleteChannelUsersViaLoginId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_BulkDeleteChannelUsersViaLoginId";
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CADMBULKDELCHNLUSER01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		List<String> loginIds = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			setUpChannelUserData();
			channelAdminCreateChannelUserReqPojo.setData(data);
			ChannelAdmin_CreateChannelUserAPI channelAdminCreateChannelUserAPI = new ChannelAdmin_CreateChannelUserAPI(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			channelAdminCreateChannelUserAPI.addBodyParam(channelAdminCreateChannelUserReqPojo);
			ObjectMapper mapper = new ObjectMapper();
			String reqBody = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(channelAdminCreateChannelUserReqPojo);
			Log.info("Request body: " + reqBody);
			channelAdminCreateChannelUserAPI.setExpectedStatusCode(200);
			channelAdminCreateChannelUserAPI.perform();
			channelAdminCreateChannelUserResPojo = channelAdminCreateChannelUserAPI
					.getAPIResponseAsPOJO(ChannelAdminCreateChannelUserResPojo.class);
			Log.info("Entering ChannelUserApprovalListApi Class");
			ChannelUserApprovalListApi channelUserApprovalListApi = new ChannelUserApprovalListApi(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			setUpChannelUserApprovalList(loginID);
			channelUserApprovalListRequestPojo.setApprovalLevel(_masterVO.getProperty("ApprovalLevelOne"));
			channelUserApprovalListRequestPojo
					.setLoginID(channelAdminCreateChannelUserReqPojo.getData().getWebloginid());
			channelUserApprovalListRequestPojo.setReqTab(_masterVO.getProperty("reqTab1"));
			channelUserApprovalListApi.addBodyParam(channelUserApprovalListRequestPojo);
			ObjectMapper mapper1 = new ObjectMapper();
			String reqBody1 = mapper1.writerWithDefaultPrettyPrinter()
					.writeValueAsString(channelUserApprovalListRequestPojo);
			Log.info("Request body: " + reqBody1);
			channelUserApprovalListApi.setExpectedStatusCode(200);
			channelUserApprovalListApi.perform();
			channelUserApprovalListApi.getAPIResponseAsPOJO(ChannelUserApprovalListResponsePojo.class);
			Log.info("Entering ChannelUserApprovalApi Class");
			ChannelUserApprovalApi channelUserApprovalApi = new ChannelUserApprovalApi(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			for (int j = 0; j < 2; j++) {
				setUpChannelUserApprovalData();
				if (j == 0) {
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
			loginIds.add(channelUserApprovalRequestPojo.getData().getWebloginid());
		}
		String path = ".\\src\\test\\resources\\adminTestData\\myOutFile.txt";
		FileOperations.createFile(loginIds, path);
		ChannelAdminBulkDeleteChannelUsersAPI channelAdminBulkDeleteAPI = new ChannelAdminBulkDeleteChannelUsersAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminBulkDeleteAPI.setFileAttachment(FileToBase64.fileToBase64(path));
		channelAdminBulkDeleteAPI.setFileName("myOutFile.txt");
		channelAdminBulkDeleteAPI.setFileType("txt");
		channelAdminBulkDeleteAPI.setIdType("LOGIN");
		channelAdminBulkDeleteAPI.setUserAction("DR");
		channelAdminBulkDeleteAPI.perform();
		channelAdminBulkDeleteResponsePojo = channelAdminBulkDeleteAPI
				.getAPIResponseAsPOJO(ChannelAdminBulkDeleteResponsePojo.class);
		Log.info("Printing Login Ids: " + loginIds.toString());
		FileOperations.deleteFile(path);
		String status = channelAdminBulkDeleteResponsePojo.getStatus();
		Assert.assertEquals(status, "200");
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void channelAdmin_BulkDeleteChannelUsersViaMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_BulkDeleteChannelUsersViaMsisdn";
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CADMBULKDELCHNLUSER02");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		List<String> msisdn = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			setUpChannelUserData();
			channelAdminCreateChannelUserReqPojo.setData(data);
			ChannelAdmin_CreateChannelUserAPI channelAdminCreateChannelUserAPI = new ChannelAdmin_CreateChannelUserAPI(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			channelAdminCreateChannelUserAPI.addBodyParam(channelAdminCreateChannelUserReqPojo);
			ObjectMapper mapper = new ObjectMapper();
			String reqBody = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(channelAdminCreateChannelUserReqPojo);
			Log.info("Request body: " + reqBody);
			channelAdminCreateChannelUserAPI.setExpectedStatusCode(200);
			channelAdminCreateChannelUserAPI.perform();
			channelAdminCreateChannelUserResPojo = channelAdminCreateChannelUserAPI
					.getAPIResponseAsPOJO(ChannelAdminCreateChannelUserResPojo.class);
			Log.info("Entering ChannelUserApprovalListApi Class");
			ChannelUserApprovalListApi channelUserApprovalListApi = new ChannelUserApprovalListApi(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			setUpChannelUserApprovalList(loginID);
			channelUserApprovalListRequestPojo.setApprovalLevel(_masterVO.getProperty("ApprovalLevelOne"));
			channelUserApprovalListRequestPojo
					.setLoginID(channelAdminCreateChannelUserReqPojo.getData().getWebloginid());
			channelUserApprovalListRequestPojo.setReqTab(_masterVO.getProperty("reqTab1"));
			channelUserApprovalListApi.addBodyParam(channelUserApprovalListRequestPojo);
			ObjectMapper mapper1 = new ObjectMapper();
			String reqBody1 = mapper1.writerWithDefaultPrettyPrinter()
					.writeValueAsString(channelUserApprovalListRequestPojo);
			Log.info("Request body: " + reqBody1);
			channelUserApprovalListApi.setExpectedStatusCode(200);
			channelUserApprovalListApi.perform();
			channelUserApprovalListApi.getAPIResponseAsPOJO(ChannelUserApprovalListResponsePojo.class);
			Log.info("Entering ChannelUserApprovalApi Class");
			ChannelUserApprovalApi channelUserApprovalApi = new ChannelUserApprovalApi(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			for (int j = 0; j < 2; j++) {
				setUpChannelUserApprovalData();
				if (j == 0) {
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
			msisdn.add(channelUserApprovalRequestPojo.getData().getMsisdn().get(0).getPhoneNo());
		}
		String path = ".\\src\\test\\resources\\adminTestData\\myOutFile.txt";
		FileOperations.createFile(msisdn, path);
		ChannelAdminBulkDeleteChannelUsersAPI channelAdminBulkDeleteAPI = new ChannelAdminBulkDeleteChannelUsersAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminBulkDeleteAPI.setFileAttachment(FileToBase64.fileToBase64(path));
		channelAdminBulkDeleteAPI.setFileName("myOutFile.txt");
		channelAdminBulkDeleteAPI.setFileType("txt");
		channelAdminBulkDeleteAPI.setIdType("MSISDN");
		channelAdminBulkDeleteAPI.setUserAction("DR");
		channelAdminBulkDeleteAPI.perform();
		channelAdminBulkDeleteResponsePojo = channelAdminBulkDeleteAPI
				.getAPIResponseAsPOJO(ChannelAdminBulkDeleteResponsePojo.class);
		Log.info("Printing Msisdn: " + msisdn.toString());
		FileOperations.deleteFile(path);
		String status = channelAdminBulkDeleteResponsePojo.getStatus();
		Assert.assertEquals(status, "200");
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-003")
	public void channelAdmin_BulkDeleteChannelUsersOneInactiveUser(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_BulkDeleteChannelUsersOneInactiveUser";
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CADMBULKDELCHNLUSER03");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		List<String> loginIds = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			setUpChannelUserData();
			channelAdminCreateChannelUserReqPojo.setData(data);
			ChannelAdmin_CreateChannelUserAPI channelAdminCreateChannelUserAPI = new ChannelAdmin_CreateChannelUserAPI(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			channelAdminCreateChannelUserAPI.addBodyParam(channelAdminCreateChannelUserReqPojo);
			ObjectMapper mapper = new ObjectMapper();
			String reqBody = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(channelAdminCreateChannelUserReqPojo);
			Log.info("Request body: " + reqBody);
			channelAdminCreateChannelUserAPI.setExpectedStatusCode(200);
			channelAdminCreateChannelUserAPI.perform();
			channelAdminCreateChannelUserResPojo = channelAdminCreateChannelUserAPI
					.getAPIResponseAsPOJO(ChannelAdminCreateChannelUserResPojo.class);
			Log.info("Entering ChannelUserApprovalListApi Class");
			ChannelUserApprovalListApi channelUserApprovalListApi = new ChannelUserApprovalListApi(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			setUpChannelUserApprovalList(loginID);
			channelUserApprovalListRequestPojo.setApprovalLevel(_masterVO.getProperty("ApprovalLevelOne"));
			channelUserApprovalListRequestPojo
					.setLoginID(channelAdminCreateChannelUserReqPojo.getData().getWebloginid());
			channelUserApprovalListRequestPojo.setReqTab(_masterVO.getProperty("reqTab1"));
			channelUserApprovalListApi.addBodyParam(channelUserApprovalListRequestPojo);
			ObjectMapper mapper1 = new ObjectMapper();
			String reqBody1 = mapper1.writerWithDefaultPrettyPrinter()
					.writeValueAsString(channelUserApprovalListRequestPojo);
			Log.info("Request body: " + reqBody1);
			channelUserApprovalListApi.setExpectedStatusCode(200);
			channelUserApprovalListApi.perform();
			channelUserApprovalListApi.getAPIResponseAsPOJO(ChannelUserApprovalListResponsePojo.class);
			Log.info("Entering ChannelUserApprovalApi Class");
			ChannelUserApprovalApi channelUserApprovalApi = new ChannelUserApprovalApi(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			if (i == 2) {

			} else {
				for (int j = 0; j < 2; j++) {
					setUpChannelUserApprovalData();
					if (j == 0) {
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
			}
			loginIds.add(channelAdminCreateChannelUserReqPojo.getData().getWebloginid());
		}
		String path = ".\\src\\test\\resources\\adminTestData\\myOutFile.txt";
		FileOperations.createFile(loginIds, path);
		ChannelAdminBulkDeleteChannelUsersAPI channelAdminBulkDeleteAPI = new ChannelAdminBulkDeleteChannelUsersAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminBulkDeleteAPI.setFileAttachment(FileToBase64.fileToBase64(path));
		channelAdminBulkDeleteAPI.setFileName("myOutFile.txt");
		channelAdminBulkDeleteAPI.setFileType("txt");
		channelAdminBulkDeleteAPI.setIdType("LOGIN");
		channelAdminBulkDeleteAPI.setUserAction("DR");
		channelAdminBulkDeleteAPI.perform();
		channelAdminBulkDeleteResponsePojo = channelAdminBulkDeleteAPI
				.getAPIResponseAsPOJO(ChannelAdminBulkDeleteResponsePojo.class);
		Log.info("Printing Msisdn: " + loginIds.toString());
		FileOperations.deleteFile(path);
		String status = channelAdminBulkDeleteResponsePojo.getStatus();
		Assert.assertEquals(status, "400");
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void channelAdmin_BulkDeleteChannelUsersHavingActivChildUsers(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_BulkDeleteChannelUsersWithActiveParent";
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CADMBULKDELCHNLUSER04");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		List<String> loginIds = new ArrayList<String>();
		List<String> parentUsers = DBHandler.AccessHandler.getParentLoginIdsHavingActiveChildUsers();
		loginIds.addAll(parentUsers.subList(parentUsers.size() - 3, parentUsers.size()));
		Log.info("Printing limited Id's: " + loginIds.toString());
		String path = ".\\src\\test\\resources\\adminTestData\\myOutFile.txt";
		FileOperations.createFile(loginIds, path);
		ChannelAdminBulkDeleteChannelUsersAPI channelAdminBulkDeleteAPI = new ChannelAdminBulkDeleteChannelUsersAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminBulkDeleteAPI.setFileAttachment(FileToBase64.fileToBase64(path));
		channelAdminBulkDeleteAPI.setFileName("myOutFile.txt");
		channelAdminBulkDeleteAPI.setFileType("txt");
		channelAdminBulkDeleteAPI.setIdType("LOGIN");
		channelAdminBulkDeleteAPI.setUserAction("DR");
		channelAdminBulkDeleteAPI.perform();
		channelAdminBulkDeleteResponsePojo = channelAdminBulkDeleteAPI
				.getAPIResponseAsPOJO(ChannelAdminBulkDeleteResponsePojo.class);
		Log.info("Printing Login Ids: " + loginIds.toString());
		FileOperations.deleteFile(path);
		String status = channelAdminBulkDeleteResponsePojo.getStatus();
		Assert.assertEquals(status, "400");
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-005")
	public void channelAdmin_BulkDeleteChannelUsersHavingPendingTtransactions(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_BulkDeleteChannelUsersHavingPendingTtransactions";
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CADMBULKDELCHNLUSER05");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		List<String> loginIds = new ArrayList<String>();
		List<String> loginIdList = DBHandler.AccessHandler.getLoginIdsHavingPendingTransactions();
		loginIds.addAll(loginIdList.subList(loginIdList.size() - 3, loginIdList.size()));
		Log.info("Printing limited Id's: " + loginIds.toString());
		String path = ".\\src\\test\\resources\\adminTestData\\myOutFile.txt";
		FileOperations.createFile(loginIds, path);
		ChannelAdminBulkDeleteChannelUsersAPI channelAdminBulkDeleteAPI = new ChannelAdminBulkDeleteChannelUsersAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminBulkDeleteAPI.setFileAttachment(FileToBase64.fileToBase64(path));
		channelAdminBulkDeleteAPI.setFileName("myOutFile.txt");
		channelAdminBulkDeleteAPI.setFileType("txt");
		channelAdminBulkDeleteAPI.setIdType("LOGIN");
		channelAdminBulkDeleteAPI.setUserAction("DR");
		channelAdminBulkDeleteAPI.perform();
		channelAdminBulkDeleteResponsePojo = channelAdminBulkDeleteAPI
				.getAPIResponseAsPOJO(ChannelAdminBulkDeleteResponsePojo.class);
		Log.info("Printing Login Ids: " + loginIds.toString());
		FileOperations.deleteFile(path);
		String status = channelAdminBulkDeleteResponsePojo.getStatus();
		Assert.assertEquals(status, "400");
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData", priority = 6)
	@TestManager(TestKey = "PRETUPS-006")
	public void channelAdmin_BulkDeleteChannelUsersRestrictedMsisdnList(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_BulkDeleteChannelUsersRestrictedMsisdnList";
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CADMBULKDELCHNLUSER06");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		List<String> loginIds = new ArrayList<String>();
		List<String> loginIdList = DBHandler.AccessHandler.getLoginIdsHavingAssociatedRestrictedMsisdnList();
		loginIds.addAll(loginIdList.subList(loginIdList.size() - 3, loginIdList.size()));
		Log.info("Printing limited Id's: " + loginIds.toString());
		String path = ".\\src\\test\\resources\\adminTestData\\myOutFile.txt";
		FileOperations.createFile(loginIds, path);
		ChannelAdminBulkDeleteChannelUsersAPI channelAdminBulkDeleteAPI = new ChannelAdminBulkDeleteChannelUsersAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminBulkDeleteAPI.setFileAttachment(FileToBase64.fileToBase64(path));
		channelAdminBulkDeleteAPI.setFileName("myOutFile.txt");
		channelAdminBulkDeleteAPI.setFileType("txt");
		channelAdminBulkDeleteAPI.setIdType("LOGIN");
		channelAdminBulkDeleteAPI.setUserAction("DR");
		channelAdminBulkDeleteAPI.perform();
		channelAdminBulkDeleteResponsePojo = channelAdminBulkDeleteAPI
				.getAPIResponseAsPOJO(ChannelAdminBulkDeleteResponsePojo.class);
		Log.info("Printing Login Ids: " + loginIds.toString());
		FileOperations.deleteFile(path);
		String status = channelAdminBulkDeleteResponsePojo.getStatus();
		Assert.assertEquals(status, "400");
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	@Test(dataProvider = "userData", priority = 7)
	@TestManager(TestKey = "PRETUPS-007")
	public void channelAdmin_BulkDeleteChannelUsersPendingFOC(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_BulkDeleteChannelUsersPendingFOC";
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CADMBULKDELCHNLUSER07");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		List<String> loginIds = new ArrayList<String>();
		List<String> loginIdList = DBHandler.AccessHandler.getLoginIdsHavingPendingFOCtransactions();
		loginIds.addAll(loginIdList.subList(loginIdList.size() - 3, loginIdList.size()));
		Log.info("Printing limited Id's: " + loginIds.toString());
		String path = ".\\src\\test\\resources\\adminTestData\\myOutFile.txt";
		FileOperations.createFile(loginIds, path);
		ChannelAdminBulkDeleteChannelUsersAPI channelAdminBulkDeleteAPI = new ChannelAdminBulkDeleteChannelUsersAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminBulkDeleteAPI.setFileAttachment(FileToBase64.fileToBase64(path));
		channelAdminBulkDeleteAPI.setFileName("myOutFile.txt");
		channelAdminBulkDeleteAPI.setFileType("txt");
		channelAdminBulkDeleteAPI.setIdType("LOGIN");
		channelAdminBulkDeleteAPI.setUserAction("DR");
		channelAdminBulkDeleteAPI.perform();
		channelAdminBulkDeleteResponsePojo = channelAdminBulkDeleteAPI
				.getAPIResponseAsPOJO(ChannelAdminBulkDeleteResponsePojo.class);
		Log.info("Printing Login Ids: " + loginIds.toString());
		FileOperations.deleteFile(path);
		String status = channelAdminBulkDeleteResponsePojo.getStatus();
		Assert.assertEquals(status, "400");
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
//	@Test(dataProvider = "userData", priority = 8)
	@TestManager(TestKey = "PRETUPS-008")
	public void channelAdmin_BulkDeleteChannelUsersOngoingSchdlRecharge(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_BulkDeleteChannelUsersOngoingSchdlRecharge";
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CADMBULKDELCHNLUSER08");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		List<String> msisdn = new ArrayList<String>();
		List<String> msisdnList = DBHandler.AccessHandler.getMsisdnHavingOnGoingBatchRechargeScheduled();
		msisdn.addAll(msisdnList.subList(msisdnList.size() - 3, msisdnList.size()));
		Log.info("Printing limited Id's: " + msisdn.toString());
		String path = ".\\src\\test\\resources\\adminTestData\\myOutFile.txt";
		FileOperations.createFile(msisdn, path);
		ChannelAdminBulkDeleteChannelUsersAPI channelAdminBulkDeleteAPI = new ChannelAdminBulkDeleteChannelUsersAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminBulkDeleteAPI.setFileAttachment(FileToBase64.fileToBase64(path));
		channelAdminBulkDeleteAPI.setFileName("myOutFile.txt");
		channelAdminBulkDeleteAPI.setFileType("txt");
		channelAdminBulkDeleteAPI.setIdType("LOGIN");
		channelAdminBulkDeleteAPI.setUserAction("DR");
		channelAdminBulkDeleteAPI.perform();
		channelAdminBulkDeleteResponsePojo = channelAdminBulkDeleteAPI
				.getAPIResponseAsPOJO(ChannelAdminBulkDeleteResponsePojo.class);
		Log.info("Printing Login Ids: " + msisdn.toString());
		FileOperations.deleteFile(path);
		String status = channelAdminBulkDeleteResponsePojo.getStatus();
		Assert.assertEquals(status, "400");
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData", priority = 9)
	@TestManager(TestKey = "PRETUPS-009")
	public void channelAdmin_BulkDeleteChannelUsersPendingBatchFOCApprval(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_BulkDeleteChannelUsersPendingBatchFOCApprval";
		Log.startTestCase(methodName);
		BeforeMethod(loginID, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CADMBULKDELCHNLUSER09");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		List<String> msisdn = new ArrayList<String>();
		List<String> msisdnList = DBHandler.AccessHandler.getMsisdnWithPendingBatchFOCApproval();
		msisdn.addAll(msisdnList.subList(msisdnList.size() - 3, msisdnList.size()));
		Log.info("Printing limited Id's: " + msisdn.toString());
		String path = ".\\src\\test\\resources\\adminTestData\\myOutFile.txt";
		FileOperations.createFile(msisdn, path);
		ChannelAdminBulkDeleteChannelUsersAPI channelAdminBulkDeleteAPI = new ChannelAdminBulkDeleteChannelUsersAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		channelAdminBulkDeleteAPI.setFileAttachment(FileToBase64.fileToBase64(path));
		channelAdminBulkDeleteAPI.setFileName("myOutFile.txt");
		channelAdminBulkDeleteAPI.setFileType("txt");
		channelAdminBulkDeleteAPI.setIdType("LOGIN");
		channelAdminBulkDeleteAPI.setUserAction("DR");
		channelAdminBulkDeleteAPI.perform();
		channelAdminBulkDeleteResponsePojo = channelAdminBulkDeleteAPI
				.getAPIResponseAsPOJO(ChannelAdminBulkDeleteResponsePojo.class);
		Log.info("Printing Login Ids: " + msisdn.toString());
		FileOperations.deleteFile(path);
		String status = channelAdminBulkDeleteResponsePojo.getStatus();
		Assert.assertEquals(status, "400");
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
