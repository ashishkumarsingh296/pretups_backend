package restassuredapi.test;

import static com.utils.GenerateToken.getAcccessToken;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
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
import restassuredapi.api.channelAdminEditStaffUser.ChannelAdminEditStaffUserAPI;
import restassuredapi.pojo.channelAdminAddStaffUserRequestPojo.AddStaffUserMsisdn;
import restassuredapi.pojo.channelAdminAddStaffUserRequestPojo.AddStaffUserVO;
import restassuredapi.pojo.channelAdminAddStaffUserRequestPojo.BtchadmLoginInputData;
import restassuredapi.pojo.channelAdminAddStaffUserRequestPojo.ChannelAdminAddStaffUserRequestPojo;
import restassuredapi.pojo.channelAdminAddStaffUserResponsePojo.ChannelAdminAddStaffUserResponsePojo;
import restassuredapi.pojo.channelAdminEditStaffUserRequestPojo.ChannelAdminEditStaffUserRequestPojo;
import restassuredapi.pojo.channelAdminEditStaffUserRequestPojo.EditStaffUserMsisdn;
import restassuredapi.pojo.channelAdminEditStaffUserRequestPojo.EditStaffUserVO;
import restassuredapi.pojo.channelAdminEditStaffUserResponsePojo.ChannelAdminEditStaffUserResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.EDIT_STAFF_USER)
public final class ChannelAdminEditStaffUser extends BaseTest {

	private ChannelAdminEditStaffUser() {
	}

	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	ChannelAdminAddStaffUserRequestPojo channelAdminAddStaffUserRequestPojo = new ChannelAdminAddStaffUserRequestPojo();
	ChannelAdminAddStaffUserResponsePojo channelAdminAddStaffUserResponsePojo = new ChannelAdminAddStaffUserResponsePojo();
	ChannelAdminEditStaffUserRequestPojo channelAdminEditStaffUserRequestPojo = new ChannelAdminEditStaffUserRequestPojo();
	ChannelAdminEditStaffUserResponsePojo channelAdminEditStaffUserResponsePojo = new ChannelAdminEditStaffUserResponsePojo();

	AddStaffUserVO data = new AddStaffUserVO();
	EditStaffUserVO editStaffUser = new EditStaffUserVO();
	BtchadmLoginInputData btchAdmInputData = new BtchadmLoginInputData();
	Faker faker = new Faker();
	Login login = new Login();
	Map<String, String> channelUserData = new HashMap<String, String>();
	HashMap<String, String> transferDetails = new HashMap<String, String>();
	static String moduleCode;
	String loginId = null;
	String phNo = null;

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

	public void createAddStaffUserRequestPayload(String catCode, String channelUser, String geography, String ownerUser,
			String parentCatCode, String parentUser) {
		setUpBatchAdminInputLoginData(catCode, channelUser, geography, ownerUser, parentCatCode, parentUser);
		data.setAddress1("");
		data.setAddress2("");
		data.setAllowedTimeFrom("00:00");
		data.setAllowedTimeTo("23:59");
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
		EditStaffUserMsisdn msisdnDetails = new EditStaffUserMsisdn();
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setOldPhoneNo(phNo); // Long.parseLong(phNo)
		msisdnDetails.setOldPin(Integer.parseInt(_masterVO.getProperty("PIN")));
		msisdnDetails.setOpType("U");
		msisdnDetails.setPhoneNo(phNumber);
		msisdnDetails.setPin(Integer.parseInt(_masterVO.getProperty("PIN")));

		ArrayList<EditStaffUserMsisdn> msisdn = new ArrayList<EditStaffUserMsisdn>();
		msisdn.add(msisdnDetails);
		editStaffUser.setMsisdn(msisdn);
	}

	public void setMultipleMsisdnDetails(String stkProfile) {
		EditStaffUserMsisdn msisdnDetails = new EditStaffUserMsisdn();
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setOldPhoneNo(phNo);
		msisdnDetails.setOldPin(Integer.parseInt(_masterVO.getProperty("PIN")));
		msisdnDetails.setOpType("U");
		msisdnDetails.setPhoneNo(phNo);
		msisdnDetails.setPin(Integer.parseInt(_masterVO.getProperty("PIN")));

		EditStaffUserMsisdn msisdnDetails1 = new EditStaffUserMsisdn();
		msisdnDetails1.setDescription("");
		msisdnDetails1.setIsprimary("N");
		msisdnDetails1.setOldPhoneNo(phNo);
		msisdnDetails1.setOldPin(Integer.parseInt(_masterVO.getProperty("PIN")));
		msisdnDetails1.setOpType("U");
		msisdnDetails1.setPhoneNo(phNo);
		msisdnDetails1.setPin(Integer.parseInt(_masterVO.getProperty("PIN")));
		ArrayList<EditStaffUserMsisdn> msisdn = new ArrayList<EditStaffUserMsisdn>();
		msisdn.add(msisdnDetails);
		msisdn.add(msisdnDetails1);
		editStaffUser.setMsisdn(msisdn);
	}

	public void createEditStaffUserRequestPayload() {
		editStaffUser.setAddress1("");
		editStaffUser.setAddress2("");
		editStaffUser.setAllowedTimeFrom("00:00");
		editStaffUser.setAllowedTimeTo("23:59");
		editStaffUser.setAlloweddays("");
		editStaffUser.setAllowedip("");
		editStaffUser.setAppointmentdate("");
		editStaffUser.setCity("");
		editStaffUser.setContactNumber(channelAdminAddStaffUserRequestPojo.getData().getContactNumber());
		editStaffUser.setCountry(RandomData.generateCountryName());
		editStaffUser.setDesignation("");
		editStaffUser.setEmailid(channelAdminAddStaffUserRequestPojo.getData().getEmailid());
		editStaffUser.setFirstName("");
		editStaffUser.setLanguage(channelAdminAddStaffUserRequestPojo.getData().getLanguage());
		editStaffUser.setLastName("");

		EditStaffUserMsisdn msisdnDetails = new EditStaffUserMsisdn();
		msisdnDetails.setDescription("");
		msisdnDetails.setIsprimary("Y");
		msisdnDetails.setOldPhoneNo(phNo);
		msisdnDetails.setOldPin(Integer.parseInt(_masterVO.getProperty("PIN")));
		msisdnDetails.setOpType("U");
		msisdnDetails.setPhoneNo(phNo);
		msisdnDetails.setPin(Integer.parseInt(_masterVO.getProperty("PIN")));

		ArrayList<EditStaffUserMsisdn> msisdn = new ArrayList<EditStaffUserMsisdn>();
		msisdn.add(msisdnDetails);
		editStaffUser.setMsisdn(msisdn);
		editStaffUser.setOldWebloginid(channelAdminAddStaffUserRequestPojo.getData().getWebloginid());
		editStaffUser.setRoles("");
		editStaffUser.setServices(_masterVO.getProperty("addServices"));
		editStaffUser.setShortName("");
		editStaffUser.setState("");
		editStaffUser.setSubscriberCode(Integer.parseInt(new RandomGeneration().randomNumeric(5)));
		editStaffUser.setUserName(channelAdminAddStaffUserRequestPojo.getData().getUserName());
		editStaffUser.setUserNamePrefix(channelAdminAddStaffUserRequestPojo.getData().getUserNamePrefix());
		editStaffUser.setWebloginid(channelAdminAddStaffUserRequestPojo.getData().getWebloginid());
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

	public void executeModifyStaffUserAPI(int statusCode, String username, String password) throws IOException {
		Log.info("Entering executeEditStaffUserAPI()");
		ChannelAdminEditStaffUserAPI channelAdminEditStaffUserAPI = new ChannelAdminEditStaffUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), getAcccessToken(username, password));
		channelAdminEditStaffUserAPI.addBodyParam(channelAdminEditStaffUserRequestPojo);
		channelAdminEditStaffUserAPI.logRequestBody(channelAdminEditStaffUserRequestPojo);
		channelAdminEditStaffUserAPI.setExpectedStatusCode(statusCode);
		channelAdminEditStaffUserAPI.perform();
		channelAdminEditStaffUserResponsePojo = channelAdminEditStaffUserAPI
				.getAPIResponseAsPOJO(ChannelAdminEditStaffUserResponsePojo.class);
		Log.info("Exiting executeEditStaffUserAPI()");
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
	public void channelAdmin_ModifyStaffUser_Distributor(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_AddChannelUser_Positive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMEDITSTAFFUSER01");
		createAddStaffUserRequestPayload(_masterVO.getProperty("distributorCatCode"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("geographicalDomain"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("distributorCatCode"), null);
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("dist_loginId"),_masterVO.getProperty("distributorCatCode")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		createEditStaffUserRequestPayload();
		editStaffUser.setRoles(getParentRoles(channelUserData.get("dist_loginId"),_masterVO.getProperty("distributorCatCode")));
		editStaffUser.setServices(getParentServices(channelUserData.get("dist_loginId")));
		channelAdminEditStaffUserRequestPojo.setData(editStaffUser);
		executeModifyStaffUserAPI(200, loginID, password);
		int statusCode = channelAdminEditStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void channelAdmin_ModifyStaffUser_Dealer(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyStaffUser_Dealer";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMEDITSTAFFUSER02");
		createAddStaffUserRequestPayload(_masterVO.getProperty("dealerCatCode"), channelUserData.get("se_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("distributorCatCode"), channelUserData.get("dist_loginId"));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("se_loginId"),_masterVO.getProperty("dealerCatCode")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		createEditStaffUserRequestPayload();
		editStaffUser.setCountry(faker.address().country());
		editStaffUser.setCity(faker.address().cityName());
		editStaffUser.setRoles(getParentRoles(channelUserData.get("se_loginId"),_masterVO.getProperty("dealerCatCode")));
		editStaffUser.setServices(getParentServices(channelUserData.get("se_loginId")));
		channelAdminEditStaffUserRequestPojo.setData(editStaffUser);
		executeModifyStaffUserAPI(200, loginID, password);
		int statusCode = channelAdminEditStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-003")
	public void channelAdmin_ModifyStaffUser_Agent(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyStaffUser_Agent";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMEDITSTAFFUSER03");
		createAddStaffUserRequestPayload(_masterVO.getProperty("agentCatCode"), channelUserData.get("ag_loginId1"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("dealerCatCode"), channelUserData.get("se_loginId"));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("ag_loginId1"),_masterVO.getProperty("agentCatCode")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		createEditStaffUserRequestPayload();
		editStaffUser.setCountry("");
		editStaffUser.setCity(faker.address().cityName());
		editStaffUser.setRoles(getParentRoles(channelUserData.get("ag_loginId1"),_masterVO.getProperty("agentCatCode")));
		editStaffUser.setServices(getParentServices(channelUserData.get("ag_loginId1")));
		channelAdminEditStaffUserRequestPojo.setData(editStaffUser);
		executeModifyStaffUserAPI(200, loginID, password);
		int statusCode = channelAdminEditStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void channelAdmin_ModifyStaffUser_Retailer(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyStaffUser_Retailer";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMEDITSTAFFUSER04");
		createAddStaffUserRequestPayload(_masterVO.getProperty("retailerCatCode"), channelUserData.get("ret_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("dealerCatCode"), channelUserData.get("se_loginId"));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("ret_loginId"),_masterVO.getProperty("retailerCatCode")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		createEditStaffUserRequestPayload();
		editStaffUser.setCountry(faker.address().country());
		editStaffUser.setCity(faker.address().cityName());
		editStaffUser.setRoles(getParentRoles(channelUserData.get("ret_loginId"),_masterVO.getProperty("retailerCatCode")));
		editStaffUser.setServices(getParentServices(channelUserData.get("ret_loginId")));
		channelAdminEditStaffUserRequestPojo.setData(editStaffUser);
		executeModifyStaffUserAPI(200, loginID, password);
		int statusCode = channelAdminEditStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-005")
	public void channelAdmin_ModifyStaffUser_BlankLoginId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyStaffUser_BlankLoginId";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMEDITSTAFFUSER05");
		createAddStaffUserRequestPayload(_masterVO.getProperty("retailerCatCode"), channelUserData.get("ret_loginId"),
				_masterVO.getProperty("geographicalDomain"), channelUserData.get("dist_loginId"),
				_masterVO.getProperty("dealerCatCode"), channelUserData.get("se_loginId"));
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("ret_loginId"),_masterVO.getProperty("retailerCatCode")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		createEditStaffUserRequestPayload();
		editStaffUser.setWebloginid("");
		channelAdminEditStaffUserRequestPojo.setData(editStaffUser);
		executeModifyStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminEditStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 6)
	@TestManager(TestKey = "PRETUPS-006")
	public void channelAdmin_ModifyStaffUser_AlphaMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyStaffUser_AlphaMsisdn";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMEDITSTAFFUSER06");
		createAddStaffUserRequestPayload(_masterVO.getProperty("distributorCatCode"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("geographicalDomain"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("distributorCatCode"), null);
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("dist_loginId"),_masterVO.getProperty("distributorCatCode")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		createEditStaffUserRequestPayload();
		setMsisdnDetails("12as53fstwfs");
		channelAdminEditStaffUserRequestPojo.setData(editStaffUser);
		executeModifyStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminEditStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 7)
	@TestManager(TestKey = "PRETUPS-007")
	public void channelAdmin_ModifyStaffUser_BlankEmail(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyStaffUser_BlankEmail";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMEDITSTAFFUSER07");
		createAddStaffUserRequestPayload(_masterVO.getProperty("distributorCatCode"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("geographicalDomain"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("distributorCatCode"), null);
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("dist_loginId"),_masterVO.getProperty("distributorCatCode")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		createEditStaffUserRequestPayload();
		editStaffUser.setEmailid("");
		channelAdminEditStaffUserRequestPojo.setData(editStaffUser);
		executeModifyStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminEditStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 8)
	@TestManager(TestKey = "PRETUPS-008")
	public void channelAdmin_ModifyStaffUser_InvalidEmail(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyStaffUser_InvalidEmail";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMEDITSTAFFUSER08");
		createAddStaffUserRequestPayload(_masterVO.getProperty("distributorCatCode"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("geographicalDomain"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("distributorCatCode"), null);
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("dist_loginId"),_masterVO.getProperty("distributorCatCode")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		createEditStaffUserRequestPayload();
		editStaffUser.setEmailid(new RandomGeneration().randomAlphaNumeric(9));
		channelAdminEditStaffUserRequestPojo.setData(editStaffUser);
		executeModifyStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminEditStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 9)
	@TestManager(TestKey = "PRETUPS-009")
	public void channelAdmin_ModifyStaffUser_BlankUsername(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyStaffUser_BlankUsername";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMEDITSTAFFUSER09");
		createAddStaffUserRequestPayload(_masterVO.getProperty("distributorCatCode"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("geographicalDomain"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("distributorCatCode"), null);
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("dist_loginId"),_masterVO.getProperty("distributorCatCode")));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(200, loginID, password);
		createEditStaffUserRequestPayload();
		editStaffUser.setFirstName("");
		editStaffUser.setUserName("");
		channelAdminEditStaffUserRequestPojo.setData(editStaffUser);
		executeModifyStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminEditStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 10)
	@TestManager(TestKey = "PRETUPS-010")
	public void channelAdmin_ModifyStaffUser_InvalidIp(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "channelAdmin_ModifyStaffUser_InvalidIp";
		setTestInitialDetails(methodName, loginID, password, categoryName, "CADMEDITSTAFFUSER10");
		createAddStaffUserRequestPayload(_masterVO.getProperty("distributorCatCode"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("geographicalDomain"),
				channelUserData.get("dist_loginId"), _masterVO.getProperty("distributorCatCode"), null);
		channelAdminAddStaffUserRequestPojo.setBtchadmLoginInputData(btchAdmInputData);
		data.setRoles(getParentRoles(channelUserData.get("dist_loginId"),_masterVO.getProperty("distributorCatCode")));
		data.setAllowedip(new RandomGeneration().randomAlphaNumeric(8));
		channelAdminAddStaffUserRequestPojo.setData(data);
		executeAddStaffUserAPI(400, loginID, password);
		createEditStaffUserRequestPayload();
		editStaffUser.setAllowedip(new RandomGeneration().randomAlphaNumeric(9));
		channelAdminEditStaffUserRequestPojo.setData(editStaffUser);
		executeModifyStaffUserAPI(400, loginID, password);
		int statusCode = channelAdminEditStaffUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@AfterMethod
	public void deleteStaffUser() {
		if (StringUtils.isEmpty(channelAdminEditStaffUserResponsePojo.getMessage()) || channelAdminEditStaffUserResponsePojo.getStatus() >= 400) {
		
		} else {
			String userId = DBHandler.AccessHandler.getUserIdLoginID(data.getWebloginid());
			DBHandler.AccessHandler.deleteChannelUser(userId);
		}
	}
}
