package restassuredapi.test;

import static com.utils.GenerateToken.getAcccessToken;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
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
import com.utils.Log;
import com.utils.RandomData;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.channelAdminAddStaffUser.ChannelAdminAddStaffUserAPI;
import restassuredapi.pojo.channelAdminAddStaffUserRequestPojo.AddStaffUserMsisdn;
import restassuredapi.pojo.channelAdminAddStaffUserRequestPojo.AddStaffUserVO;
import restassuredapi.pojo.channelAdminAddStaffUserRequestPojo.BtchadmLoginInputData;
import restassuredapi.pojo.channelAdminAddStaffUserRequestPojo.ChannelAdminAddStaffUserRequestPojo;
import restassuredapi.pojo.channelAdminAddStaffUserResponsePojo.ChannelAdminAddStaffUserResponsePojo;

@ModuleManager(name = Module.ADD_STAFF_USER)
public final class ChannelAdmin_AddStaffUser extends BaseTest {

	private ChannelAdmin_AddStaffUser() {
	}

	Map<String, String> channelUserData = new HashMap<String, String>();

	@BeforeClass
	public void getChannelUserData() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			int rowNum = i + 1;
			String catCode = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, rowNum);
			String parentCatName = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, rowNum);
			if (parentCatName.equalsIgnoreCase("Dealer")) {
				channelUserData.put(catCode.toLowerCase() + "_loginId1",
						ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, rowNum));
			}
			channelUserData.put(catCode.toLowerCase() + "_loginId",
					ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, rowNum));
		}
	}

	Faker faker = new Faker();
	static String moduleCode;
	ChannelAdminAddStaffUserRequestPojo channelAdminAddStaffUserRequestPojo = new ChannelAdminAddStaffUserRequestPojo();
	ChannelAdminAddStaffUserResponsePojo channelAdminAddStaffUserResponsePojo = new ChannelAdminAddStaffUserResponsePojo();

	AddStaffUserVO data = new AddStaffUserVO();
	BtchadmLoginInputData btchAdmInputData = new BtchadmLoginInputData();
	Login login = new Login();
	String loginId = null;
	String phNo = null;

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

	public void setUpBatchAdminInputLoginData(String catCode, String channelUser, String geography, String ownerUser,
			String parentCatCode, String parentUser) {
		btchAdmInputData.setCategoryCode(catCode);
		btchAdmInputData.setChannelUser(channelUser);
		btchAdmInputData.setDomainCode(_masterVO.getProperty("domainCode"));
		btchAdmInputData.setGeography(geography);
		btchAdmInputData.setOwnerUser(ownerUser);
		btchAdmInputData.setParentCategory(parentCatCode);
		btchAdmInputData.setParentUser(parentUser);
	}

	public void createRequestPayload(String catCode, String channelUser, String geography, String ownerUser,
			String parentCatCode, String parentUser) {
		setUpBatchAdminInputLoginData(catCode, channelUser, geography, ownerUser, parentCatCode, parentUser);
		data.setAddress1("");
		data.setAddress2("");
		data.setAllowedTimeFrom("");
		data.setAllowedTimeTo("");
		data.setAlloweddays("");
		data.setAllowedip("");
		data.setAppointmentdate("");
		data.setCity("");
		data.setConfirmwebpassword(Integer.parseInt(_masterVO.getProperty("PIN")));
		phNo = UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		data.setContactNumber(phNo);
		data.setCountry(RandomData.generateCountryName());
		data.setDesignation("");
		data.setEmailid(_masterVO.getProperty("email"));
		String firstName = faker.name().firstName();
		data.setFirstName(firstName);
		data.setLanguage("en_US");
		String lastName = faker.name().lastName();
		data.setLastName(lastName);

		AddStaffUserMsisdn msisdnDetails = new AddStaffUserMsisdn();
		msisdnDetails.setPhoneNo(phNo);
		msisdnDetails.setPin(Integer.parseInt(_masterVO.getProperty("PIN")));
		msisdnDetails.setConfirmpin(Integer.parseInt(_masterVO.getProperty("PIN")));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		ArrayList<AddStaffUserMsisdn> msisdn = new ArrayList<AddStaffUserMsisdn>();
		msisdn.add(msisdnDetails);
		data.setMsisdn(msisdn);
		data.setRoles("");
		data.setServices("");
		data.setShortName(faker.name().lastName());
		data.setState("");
		data.setSubscriberCode(1232);
		data.setUserName(firstName + " " + lastName);
		data.setUserNamePrefix("CMPY");
		loginId = UniqueChecker.UC_LOGINID();
		data.setWebloginid(loginId);
		data.setWebpassword(Integer.parseInt(_masterVO.getProperty("PIN")));

	}

	public void setTestInitialDetails(String methodName, String loginID, String password, String categoryName,
			String caseId) throws Exception {
		Log.startTestCase(methodName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID(caseId);
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
	}

	public void setMsisdnDetails(String phNumber) {
		AddStaffUserMsisdn msisdnDetails = new AddStaffUserMsisdn();
		msisdnDetails.setPhoneNo(phNumber);
		msisdnDetails.setPin(Integer.parseInt(_masterVO.getProperty("PIN")));
		msisdnDetails.setConfirmpin(Integer.parseInt(_masterVO.getProperty("PIN")));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");

		ArrayList<AddStaffUserMsisdn> msisdn = new ArrayList<AddStaffUserMsisdn>();
		msisdn.add(msisdnDetails);
		data.setMsisdn(msisdn);
	}

	public void setMsisdnDetailsWithIncorrectPin(String pin) {
		AddStaffUserMsisdn msisdnDetails = new AddStaffUserMsisdn();
		msisdnDetails.setPhoneNo(phNo);
		msisdnDetails.setPin(Integer.parseInt(_masterVO.getProperty("PIN")));
		msisdnDetails.setConfirmpin(Integer.parseInt(pin));
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");

		ArrayList<AddStaffUserMsisdn> msisdn = new ArrayList<AddStaffUserMsisdn>();
		msisdn.add(msisdnDetails);
		data.setMsisdn(msisdn);
	}

	public void executeAddStaffUserAPI(int statusCode, String username, String password) throws IOException {
		Log.info("Entering executeAddStaffUserAPI()");
		ChannelAdminAddStaffUserAPI channelAdminAddStaffUserAPI = new ChannelAdminAddStaffUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), getAcccessToken(username, password));
		channelAdminAddStaffUserAPI.addBodyParam(channelAdminAddStaffUserRequestPojo);
		channelAdminAddStaffUserAPI.logRequestBody(channelAdminAddStaffUserRequestPojo);
		channelAdminAddStaffUserAPI.setExpectedStatusCode(statusCode);
		channelAdminAddStaffUserAPI.perform();
		channelAdminAddStaffUserResponsePojo = channelAdminAddStaffUserAPI
				.getAPIResponseAsPOJO(ChannelAdminAddStaffUserResponsePojo.class);
		Log.info("Exiting executeAddStaffUserAPI()");
	}

	public static String getParentRoles(String parentLoginId, String parentCatCode) {
		String userId = DBHandler.AccessHandler.getUserIdLoginID(parentLoginId);
		Log.info("Printing UserId : " + userId);
		List<String> actualRoles = DBHandler.AccessHandler.getUserRoles(userId, parentCatCode).stream().sorted()
				.collect(Collectors.toList());
		List<String> alteredList = actualRoles.stream().map(s -> s.concat(",")).collect(Collectors.toList());
		alteredList.set(alteredList.size() - 1, alteredList.get(alteredList.size() - 1).replace(",", ""));
		String listRoles = String.join("", alteredList);
		Log.info("Altered Roles List " + listRoles);
		return listRoles;
	}

	public static String getParentServices(String parentLoginId) {
		String userId = DBHandler.AccessHandler.getUserIdLoginID(parentLoginId);
		Log.info("Printing UserId : " + userId);
		List<String> actualRoles = DBHandler.AccessHandler.fetchParentServicesTypes(userId).stream().sorted()
				.collect(Collectors.toList());
		List<String> alteredList = actualRoles.stream().map(s -> s.concat(",")).collect(Collectors.toList());
		alteredList.set(alteredList.size() - 1, alteredList.get(alteredList.size() - 1).replace(",", ""));
		String listRoles = String.join("", alteredList);
		Log.info("Parent services: " + listRoles);
		return listRoles;
	}

	@Test(dataProvider = "userData", priority = 1)
	@TestManager(TestKey = "PRETUPS-001")
	public void channelAdmin_AddStaffUser_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER01");
		createRequestPayload(_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), "", _masterVO.getProperty("distributorCatCode"), "");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("dist_loginId"), _masterVO.getProperty("distributorCatCode")));
		data.setServices(getParentServices(channelUserData.get("dist_loginId")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void channelAdmin_AddStaffUser_ExistingLoginId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUser_ExistingLoginId";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER02");
		createRequestPayload(_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		String staffLoginId = DBHandler.AccessHandler.getUserInfo("LOGIN_ID", "DIST", "STAFF");
		Log.info(staffLoginId);
		data.setWebloginid(staffLoginId);
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Staff USer loginId already exist.");
		Assertion.assertEquals(message, "Staff USer loginId already exist.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-003")
	public void channelAdmin_AddStaffUser_blankLoginId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUser_blankLoginId";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER03");
		createRequestPayload(_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		data.setWebloginid("");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Staff user login ID is mandatory.");
		Assertion.assertEquals(message, "Staff user login ID is mandatory.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void channelAdmin_AddStaffUser_existingMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUser_existingMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER04");
		createRequestPayload(_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		String phoneNumber = DBHandler.AccessHandler.getUserInfo("MSISDN", "DIST", "STAFF");
		setMsisdnDetails(phoneNumber);
		data.setRoles(getParentRoles(channelUserData.get("dist_loginId"), _masterVO.getProperty("distributorCatCode")));
		data.setServices(getParentServices(channelUserData.get("dist_loginId")));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Mobile number already exist in the system.");
		Assertion.assertEquals(message, "Mobile number already exist in the system.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-010")
	public void channelAdmin_AddStaffUser_blankEmailId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUser_blankEmailId";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER05");
		createRequestPayload(_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		data.setEmailid("");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Email id is invalid or blank.");
		Assertion.assertEquals(message, "Email id is invalid or blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 6)
	@TestManager(TestKey = "PRETUPS-011")
	public void channelAdmin_AddStaffUser_invalidEmailId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUser_invalidEmailId";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER06");
		createRequestPayload(_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		data.setEmailid(new RandomGeneration().randomAlphabets(8));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Email id is invalid or blank.");
		Assertion.assertEquals(message, "Email id is invalid or blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 7)
	@TestManager(TestKey = "PRETUPS-012")
	public void channelAdmin_AddStaffUser_RolesSpecific(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER07");
		createRequestPayload(_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		data.setRoles(_masterVO.getProperty("addRoles"));
		data.setServices(_masterVO.getProperty("addServices"));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		String userId = DBHandler.AccessHandler.getUserIdLoginID(loginId);
		Log.info("Printing UserId : " + userId);
		List<String> actualRoles = DBHandler.AccessHandler.getUserRoles(userId, _masterVO.getProperty("distributorCatCode")).stream().sorted()
				.collect(Collectors.toList());
		List<String> actualUserServices = DBHandler.AccessHandler.fetchParentServicesTypes(userId).stream().sorted()
				.collect(Collectors.toList());
		Assert.assertEquals(statusCode, 200);
		ArrayList<String> roles = new ArrayList<String>(
				Arrays.asList("C2CRETURN", "UNBARUSER", "VIEWCUSERSELF", "C2SREV"));
		List<String> expectedRoles = roles.stream().sorted().collect(Collectors.toList());
		ArrayList<String> expectedUserServices = new ArrayList<String>(Arrays.asList("DVD", "EVD"));
		Assert.assertEquals(actualRoles, expectedRoles);
		Assert.assertEquals(actualUserServices, expectedUserServices);
		Assertions.assertEquals(actualRoles, expectedRoles);
		Assertions.assertEquals(actualUserServices, expectedUserServices);
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 8)
	@TestManager(TestKey = "PRETUPS-008")
	public void channelAdmin_AddStaffUserAsDistWithNoChannelInfo(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserAsDistWithNoChannelAndOwnerInfo";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER08");
		createRequestPayload(_masterVO.getProperty("distributorCatCode"), "",
				_masterVO.getProperty("geographicalDomain"), "", _masterVO.getProperty("distributorCatCode"), "");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Please enter channel user details");
		Assertion.assertEquals(message, "Please enter channel user details");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 9)
	@TestManager(TestKey = "PRETUPS-009")
	public void channelAdmin_AddStaffUserAsDealerWithNoParentChanneUserInfo(String loginID, String password,
			String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserAsDealerWithNoParentChanneUserInfo";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER09");
		createRequestPayload(_masterVO.getProperty("dealerCatCode"), "", _masterVO.getProperty("geographicalDomain"),
				"", _masterVO.getProperty("distributorCatCode"), "");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Please enter channel user details");
		Assertion.assertEquals(message, "Please enter channel user details");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 10)
	@TestManager(TestKey = "PRETUPS-010")
	public void channelAdmin_AddStaffUserAsAgentWithNoParentChannelOwnerUserInfo(String loginID, String password,
			String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserAsDealerWithNoParentChannelOwnerUserInfo";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER10");
		createRequestPayload(_masterVO.getProperty("agentCatCode"), "", _masterVO.getProperty("geographicalDomain"), "",
				_masterVO.getProperty("dealerCatCode"), "");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Please enter staff user's owner details");
		Assertion.assertEquals(message, "Please enter staff user's owner details");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 11)
	@TestManager(TestKey = "PRETUPS-011")
	public void channelAdmin_AddStaffUserAsRetailerWithNoParentChannelOwnerUserInfo(String loginID, String password,
			String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserAsRetailerWithNoParentChannelOwnerUserInfo";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER11");
		createRequestPayload(_masterVO.getProperty("retailerCatCode"), "", _masterVO.getProperty("geographicalDomain"),
				"", _masterVO.getProperty("agentCatCode"), "");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Please enter staff user's owner details");
		Assertion.assertEquals(message, "Please enter staff user's owner details");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 12)
	@TestManager(TestKey = "PRETUPS-012")
	public void channelAdmin_AddStaffUserWithParentAndOwnerHierarchyMismatch(String loginID, String password,
			String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithParentAndOwnerHierarchyMismatch";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER12");
		createRequestPayload(_masterVO.getProperty("agentCatCode"), channelUserData.get("ag_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("dealerCatCode"), "roshan_dealer");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Selected Channel user not under Parent User.");
		Assertion.assertEquals(message, "Selected Channel user not under Parent User.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 13)
	@TestManager(TestKey = "PRETUPS-013")
	public void channelAdmin_AddStaffUserAsDealer(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithParentAndOwnerHierarchyMismatch";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER13");
		createRequestPayload(_masterVO.getProperty("dealerCatCode"), channelUserData.get("se_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("se_loginId"), _masterVO.getProperty("dealerCatCode")));
		data.setServices(getParentServices(channelUserData.get("se_loginId")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(message, "Staff user " + data.getUserName() + " successfully accepted.");
		Assertion.assertEquals(message, "Staff user " + data.getUserName() + " successfully accepted.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 14)
	@TestManager(TestKey = "PRETUPS-014")
	public void channelAdmin_AddStaffUserAsAgent(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserAsAgent";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER14");
		createRequestPayload(_masterVO.getProperty("agentCatCode"), channelUserData.get("ag_loginId1"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("dealerCatCode"), channelUserData.get("se_loginId"));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("ag_loginId1"), _masterVO.getProperty("agentCatCode")));
//		data.setServices(getParentServices(channelUserData.get("dist_loginId")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(message, "Staff user " + data.getUserName() + " successfully accepted.");
		Assertion.assertEquals(message, "Staff user " + data.getUserName() + " successfully accepted.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 15)
	@TestManager(TestKey = "PRETUPS-015")
	public void channelAdmin_AddStaffUserAsRetailer(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserAsRetailer";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER15");
		createRequestPayload(_masterVO.getProperty("retailerCatCode"), channelUserData.get("ret_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("dealerCatCode"), channelUserData.get("se_loginId"));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("ret_loginId"),_masterVO.getProperty("retailerCatCode")));
//		data.setServices(getParentServices(channelUserData.get("dist_loginId")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(message, "Staff user " + data.getUserName() + " successfully accepted.");
		Assertion.assertEquals(message, "Staff user " + data.getUserName() + " successfully accepted.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 16)
	@TestManager(TestKey = "PRETUPS-016")
	public void channelAdmin_AddStaffUserWithDistLoginIdAsParentWithParentCatAsDealer(String loginID, String password,
			String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithDistLoginIdAsParentWithParentCatAsDealer";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER16");
		createRequestPayload(_masterVO.getProperty("agentCatCode"), channelUserData.get("ag_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("dealerCatCode"), channelUserData.get("dist_loginId"));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 17)
	@TestManager(TestKey = "PRETUPS-017")
	public void channelAdmin_AddStaffUserWithAgentLoginIdAsParentWithParentCatAsDealer(String loginID, String password,
			String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithAgentLoginIdAsParentWithParentCatAsDealer";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER17");
		createRequestPayload(_masterVO.getProperty("agentCatCode"), channelUserData.get("ag_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("dealerCatCode"), channelUserData.get("ag_loginId"));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 18)
	@TestManager(TestKey = "PRETUPS-018")
	public void channelAdmin_AddStaffUserWithRetailerLoginIdAsParentWithParentCatAsDealer(String loginID,
			String password, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithRetailerLoginIdAsParentWithParentCatAsDealer";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER18");
		createRequestPayload(_masterVO.getProperty("agentCatCode"), channelUserData.get("ag_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("dealerCatCode"), channelUserData.get("ret_loginId"));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 19)
	@TestManager(TestKey = "PRETUPS-019")
	public void channelAdmin_AddStaffUserWithChannelUserLoginIdAsDistWithCatCodeAsDealer(String loginID,
			String password, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithChannelUserLoginIdAsDistWithCatCodeAsDealer";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER19");
		createRequestPayload(_masterVO.getProperty("dealerCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 20)
	@TestManager(TestKey = "PRETUPS-020")
	public void channelAdmin_AddStaffUserWithChannelUserLoginIdAsDistWithCatCodeAsAgent(String loginID, String password,
			String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithChannelUserLoginIdAsDistWithCatCodeAsAgent";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER20");
		createRequestPayload(_masterVO.getProperty("agentCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 21)
	@TestManager(TestKey = "PRETUPS-021")
	public void channelAdmin_AddStaffUserWithChannelUserLoginIdAsDistWithCatCodeAsRetailer(String loginID,
			String password, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithChannelUserLoginIdAsDistWithCatCodeAsRetailer";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER21");
		createRequestPayload(_masterVO.getProperty("retailerCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 22)
	@TestManager(TestKey = "PRETUPS-022")
	public void channelAdmin_AddStaffUserWithPinMismatch(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithBlankMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER22");
		createRequestPayload(_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		setMsisdnDetailsWithIncorrectPin(new RandomGeneration().randomNumeric(4));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 23)
	@TestManager(TestKey = "PRETUPS-023")
	public void channelAdmin_AddStaffUserWithAlphaNumericalMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithBlankMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER23");
		createRequestPayload(_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		setMsisdnDetails(new RandomGeneration().randomAlphabets(8));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.assertEquals(message, "Only number allowed in msisdn for row number 1.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 24)
	@TestManager(TestKey = "PRETUPS-023")
	public void channelAdmin_AddStaffUserWithInvalidIpAddress(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithInvalidIpAddress";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER24");
		createRequestPayload(_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), "");
		data.setAllowedip(new RandomGeneration().randomAlphabets(8));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 25)
	@TestManager(TestKey = "PRETUPS-025")
	public void channelAdmin_AddStaffUserWithNoParentInfo(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddStaffUserWithNoParentInfo";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMADDSTAFFUSER25");
		createRequestPayload(_masterVO.getProperty("agentCatCode"), channelUserData.get("ag_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("dealerCatCode"), "");
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		String message = channelAdminAddStaffUserResponsePojo.getMessage();
		int statusCode = channelAdminAddStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Please enter staff user's parent details");
		Assertion.assertEquals(message, "Please enter staff user's parent details");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@AfterMethod
	public void deleteStaffUser() {
		if (StringUtils.isEmpty(channelAdminAddStaffUserResponsePojo.getMessage()) || channelAdminAddStaffUserResponsePojo.getStatus() >= 400) {

		} else {
			String userId = DBHandler.AccessHandler.getUserIdLoginID(data.getWebloginid());
			DBHandler.AccessHandler.deleteChannelUser(userId);
		}
	}
}
