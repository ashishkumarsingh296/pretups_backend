package restassuredapi.test;

import static com.utils.GenerateToken.getAcccessToken;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
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

import restassuredapi.api.superAdminAddOperatorUser.SuperAdminAddOperatorUserAPI;
import restassuredapi.pojo.superAdminAddOperatorResponsePojo.SuperAdminAddOptResponsePojo;
import restassuredapi.pojo.superAdminAddOperatorUserRequestPojo.CreateOperatorUserMsisdn;
import restassuredapi.pojo.superAdminAddOperatorUserRequestPojo.CreateOperatorUserVO;
import restassuredapi.pojo.superAdminAddOperatorUserRequestPojo.SuperAdminAddOptUserRequestPojo;

@ModuleManager(name = Module.ADD_OPERATOR_USER)
public final class SuperAdminAddOperatorUser extends BaseTest {

	Calendar cal = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

	public SuperAdminAddOperatorUser() {
	}

	SuperAdminAddOptUserRequestPojo superAdminAddOptUserRequestPojo = new SuperAdminAddOptUserRequestPojo();
	SuperAdminAddOptResponsePojo superAdminAddOptUserResponsePojo = new SuperAdminAddOptResponsePojo();
	CreateOperatorUserVO createOperatorUserVO = new CreateOperatorUserVO();
	Faker faker = new Faker();
	static String moduleCode;
	HashMap<String, String> optCatCode = new HashMap<String, String>();

	@BeforeClass
	public void loadOperatorCategoryCode() {
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		for (int i = 1; i < rowCount; i++) {
			if (optCatCode.containsKey(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {

			} else {
				optCatCode.put(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i),
						ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i));
			}
		}

		optCatCode.forEach((key, value) -> System.out.println(key + " " + value));
	}

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);

		Object[][] Data = new Object[1][5];
		int j = 0;
		int i = 1;
		Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
		Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
		Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
		Data[j][3] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
		Data[j][4] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
		return Data;
	}

	public void executeAddOperatorUserAPI(int statusCode, String username, String password) throws IOException {
		Log.info("Entering executeAddOperatorUserAPI()");
		SuperAdminAddOperatorUserAPI superAdminAddOperatorUserAPI = new SuperAdminAddOperatorUserAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), getAcccessToken(username, password));
		superAdminAddOperatorUserAPI.addBodyParam(superAdminAddOptUserRequestPojo);
		superAdminAddOperatorUserAPI.logRequestBody(superAdminAddOptUserRequestPojo);
		superAdminAddOperatorUserAPI.setExpectedStatusCode(statusCode);
		superAdminAddOperatorUserAPI.perform();
		superAdminAddOptUserResponsePojo = superAdminAddOperatorUserAPI
				.getAPIResponseAsPOJO(SuperAdminAddOptResponsePojo.class);
		Log.info("Exiting executeAddOperatorUserAPI()");
	}

	public void setTestInitialDetails(String methodName, String categoryName, String caseId) throws Exception {
		Log.startTestCase(methodName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID(caseId);
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
	}

	public void createAddOperatorRequestPayload() {

		createOperatorUserVO.setAddress1(faker.address().streetAddress());
		createOperatorUserVO.setAddress2("");
		createOperatorUserVO.setAllowedTimeFrom("00:00");
		createOperatorUserVO.setAllowedTimeTo("23:59");
		createOperatorUserVO.setAlloweddays("1,2,3,4,5,6,7");
		createOperatorUserVO.setAllowedip("");
		createOperatorUserVO.setAppointmentdate("");
		createOperatorUserVO.setCity(faker.address().city());
		createOperatorUserVO.setCommissionProfileID("");
		createOperatorUserVO.setCompany(faker.company().name());
		createOperatorUserVO.setContactNumber("");
		createOperatorUserVO.setContactPerson("");
		createOperatorUserVO.setControlGroup("");
		createOperatorUserVO.setCountry(RandomData.generateCountryName());
		createOperatorUserVO.setDepartmentCode(_masterVO.getProperty("departmentCode"));
		createOperatorUserVO.setDesignation("");
		createOperatorUserVO.setDivisionCode(_masterVO.getProperty("divisionCode"));
		createOperatorUserVO.setDocumentNo("");
		createOperatorUserVO.setDocumentType("");
		createOperatorUserVO.setDomain(_masterVO.getProperty("optDomain"));
		createOperatorUserVO.setEmailid(_masterVO.getProperty("optEmailAddress"));
		createOperatorUserVO.setEmpcode("");
		createOperatorUserVO.setExternalCode(UniqueChecker.UC_EXTCODE());
		createOperatorUserVO.setExtnwcode(_masterVO.getProperty("networkCode"));
		createOperatorUserVO.setFax("");
		String firstName = faker.name().firstName();
		createOperatorUserVO.setFirstName(firstName);
		createOperatorUserVO.setGeographicalDomain("");
		createOperatorUserVO.setGeographyCode("N");
		createOperatorUserVO.setInsuspend("N");
		createOperatorUserVO.setLanguage("");
		createOperatorUserVO.setLastName("");
		createOperatorUserVO.setLatitude("");
		createOperatorUserVO.setLmsProfileId("");
		createOperatorUserVO.setLongitude("");
		createOperatorUserVO.setLowbalalertother("N");
		createOperatorUserVO.setLowbalalertparent("N");
		createOperatorUserVO.setLowbalalertself("N");
		createOperatorUserVO.setMobileNumber("");

		CreateOperatorUserMsisdn createOperatorUserMsisdn = new CreateOperatorUserMsisdn();
		ArrayList<CreateOperatorUserMsisdn> operatorMsisdn = new ArrayList<CreateOperatorUserMsisdn>();
	//	operatorMsisdn.add(createOperatorUserMsisdn);
		createOperatorUserVO.setMsisdn(operatorMsisdn);

		createOperatorUserVO.setMultipleGeographyLoc("");
		createOperatorUserVO.setOtherEmail("");
		createOperatorUserVO.setOutletCode("");
		createOperatorUserVO.setSubOutletCode("");
		createOperatorUserVO.setOutsuspend("N");
		createOperatorUserVO.setParentUser(_masterVO.getProperty("parentUser"));
		createOperatorUserVO.setPaymentType("");
		createOperatorUserVO.setRoleType("N");
		createOperatorUserVO.setRoles("");
		createOperatorUserVO.setServices("");
		String shortName = faker.name().lastName();
		createOperatorUserVO.setShortName(shortName);
		createOperatorUserVO.setSsn("");
		createOperatorUserVO.setState(faker.address().state());
		createOperatorUserVO.setSubscriberCode(faker.number().digits(6));
		createOperatorUserVO.setTransferProfile("");
		createOperatorUserVO.setTransferRuleType("");
		createOperatorUserVO.setUserCatCode("");
		createOperatorUserVO.setUserCode("");
		createOperatorUserVO.setUserDomainCodes("");
		createOperatorUserVO.setParentCategory("");
		createOperatorUserVO.setUserName(firstName + " " + shortName);
		createOperatorUserVO.setUserNamePrefix("MR");
		createOperatorUserVO.setVoucherSegments(_masterVO.getProperty("voucherSegments"));
		createOperatorUserVO.setVoucherTypes("");
		createOperatorUserVO.setWebloginid(UniqueChecker.UC_LOGINID());
		createOperatorUserVO.setWebpassword(_masterVO.getProperty("password"));
		createOperatorUserVO.setConfirmwebpassword(_masterVO.getProperty("password"));
		createOperatorUserVO.setUsergrade("");
		createOperatorUserVO.setUserProducts("");
		createOperatorUserVO.setOwnerUser(_masterVO.getProperty("ownerUser"));
	}

	public void setMsisdnForOperatorUserBasedOnFlagValue(String catCode, String setMsisdn) {

		if (setValuesOnlyOnCondition("SMS_INTERFACE_ALLOWED", catCode).equals("Y")) {
			CreateOperatorUserMsisdn createOperatorUserMsisdn = new CreateOperatorUserMsisdn();
			ArrayList<CreateOperatorUserMsisdn> operatorMsisdn = new ArrayList<CreateOperatorUserMsisdn>();
			createOperatorUserMsisdn.setPhoneNo(setMsisdn);
			createOperatorUserMsisdn.setPin(_masterVO.getProperty("pin"));
			createOperatorUserMsisdn.setConfirmPin(_masterVO.getProperty("confirmPin"));
			createOperatorUserMsisdn.setDescription("");
			createOperatorUserMsisdn.setIsprimary("Y");
			operatorMsisdn.add(createOperatorUserMsisdn);
			createOperatorUserVO.setMsisdn(operatorMsisdn);
		} else {
			createOperatorUserVO.setMobileNumber(setMsisdn);
		}

	}

	public String assignGeographies(String geoDomainType) {
		List<String> geoDomainCodelist = DBHandler.AccessHandler
				.getGeographicalDomainCodeListBasedOnGeoType(geoDomainType);
		geoDomainCodelist.forEach(s -> System.out.println("Printing code list: " + s));
		if (geoDomainCodelist.contains("NG")) {
			String geoDomainCode = "NG";
			return geoDomainCode;
		} else {
			String geoDomainCodes = String.join(",", geoDomainCodelist);
			return geoDomainCodes;
		}

	}

	public String setValuesOnlyOnCondition(String columnName, String categoryCode) {
		return DBHandler.AccessHandler.getCategoryDetail(columnName, categoryCode);
	}

	public static String getOperatorRoles(String optCatCode) {
		List<String> actualRoles = DBHandler.AccessHandler.getOperatorRoles(optCatCode).stream().sorted()
				.collect(Collectors.toList());
		List<String> alteredList = actualRoles.stream().map(s -> s.concat(",")).collect(Collectors.toList());
		alteredList.set(alteredList.size() - 1, alteredList.get(alteredList.size() - 1).replace(",", ""));
		String listRoles = String.join("", alteredList);
		Log.info("Altered Roles List " + listRoles);
		return listRoles;
	}

	public void setSuperChannelAdminServicesBasedOnFlagValue(String moduleCode, String senderNtwCode,
			String receiverNtwCode, String catCode) {
		if (setValuesOnlyOnCondition("SERVICES_ALLOWED", catCode).equals("Y")) {
			List<String> actualRoles = DBHandler.AccessHandler
					.fetchSuperChannelAdminServices(moduleCode, senderNtwCode, receiverNtwCode, catCode).stream()
					.sorted().collect(Collectors.toList());
			List<String> alteredList = actualRoles.stream().map(s -> s.concat(",")).collect(Collectors.toList());
			alteredList.set(alteredList.size() - 1, alteredList.get(alteredList.size() - 1).replace(",", ""));
			String listRoles = String.join("", alteredList);
			Log.info("Altered Roles List " + listRoles);
			createOperatorUserVO.setServices(listRoles);
		} else {
			createOperatorUserVO.setServices("");
		}

	}

	public void setOperatorServicesBasedOnFlagValue(String senderNtwCode, String receiverNtwCode, String catCode) {
		if (setValuesOnlyOnCondition("SERVICES_ALLOWED", catCode).equals("Y")) {
			List<String> actualRoles = DBHandler.AccessHandler.fetchOperatorServices(senderNtwCode, receiverNtwCode)
					.stream().sorted().collect(Collectors.toList());
			List<String> alteredList = actualRoles.stream().map(s -> s.concat(",")).collect(Collectors.toList());
			alteredList.set(alteredList.size() - 1, alteredList.get(alteredList.size() - 1).replace(",", ""));
			String listRoles = String.join("", alteredList);
			Log.info("Altered Roles List " + listRoles);
			createOperatorUserVO.setServices(listRoles);
		} else {
			createOperatorUserVO.setServices("");
		}
	}

	public void setProductTypeBasedOnFlagValue(String catCode) {
		if (setValuesOnlyOnCondition("PRODUCT_TYPES_ALLOWED", catCode).equals("Y")) {
			createOperatorUserVO.setUserProducts("PREPROD");
		}
	}

	public void setUserDomainsBasedOnFlagValue(String catCode) {
		if (setValuesOnlyOnCondition("DOMAIN_ALLOWED", catCode).equals("Y")) {
			List<String> domainCodes = DBHandler.AccessHandler.getDomainCodes("OPERATOR");
			createOperatorUserVO.setUserDomainCodes(String.join(",", domainCodes));
		} else {
			createOperatorUserVO.setUserDomainCodes("");
		}
	}

	@Test(dataProvider = "userData", priority = 1)
	@TestManager(TestKey = "PRETUPS-001")
	public void superAdmin_AddSubSuperAdminUser_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddSubSuperAdminUser_Positive";
		String subSuperAdmCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER01");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(subSuperAdmCatCode);
		createOperatorUserVO.setRoles(getOperatorRoles(subSuperAdmCatCode));
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdmCatCode, UniqueChecker.UC_MSISDN());
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdmCatCode);
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		setProductTypeBasedOnFlagValue(subSuperAdmCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void superAdmin_AddNetworkAdmin_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddNetworkAdmin_Positive";
		String ntwAdmCatCode = optCatCode.get("Network Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER02");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(ntwAdmCatCode);
		createOperatorUserVO.setRoles(getOperatorRoles(ntwAdmCatCode));
		setMsisdnForOperatorUserBasedOnFlagValue(ntwAdmCatCode, UniqueChecker.UC_MSISDN());
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-003")
	public void superAdmin_AddSuperNetworkAdmin_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddSuperNetworkAdmin_Positive";
		String superNtwAdmCatCode = optCatCode.get("Super Network Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER03");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(superNtwAdmCatCode);
		createOperatorUserVO.setRoles(getOperatorRoles(superNtwAdmCatCode));
		setMsisdnForOperatorUserBasedOnFlagValue(superNtwAdmCatCode, UniqueChecker.UC_MSISDN());
		setOperatorServicesBasedOnFlagValue("NG", "NG", superNtwAdmCatCode);
		setProductTypeBasedOnFlagValue(superNtwAdmCatCode);
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void superAdmin_AddSuperCustomerCare_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddSuperCustomerCare_Positive";
		String superCustomerCareCatCode = optCatCode.get("Super Customer Care");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER04");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(superCustomerCareCatCode);
		createOperatorUserVO.setRoles(getOperatorRoles(superCustomerCareCatCode));
		setMsisdnForOperatorUserBasedOnFlagValue(superCustomerCareCatCode, UniqueChecker.UC_MSISDN());
		setOperatorServicesBasedOnFlagValue("NG", "NG", superCustomerCareCatCode);
		setProductTypeBasedOnFlagValue(superCustomerCareCatCode);
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-005")
	public void superAdmin_AddSuperChannelAdmin_Positive(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddSuperChannelAdmin_Positive";
		String superChannelAdminCatCode = optCatCode.get("Super Channel Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER05");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(optCatCode.get("Super Channel Admin"));
		createOperatorUserVO.setRoles(getOperatorRoles(superChannelAdminCatCode));
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("ZO"));
		setSuperChannelAdminServicesBasedOnFlagValue("OPT", "NG", "NG", superChannelAdminCatCode);
		setMsisdnForOperatorUserBasedOnFlagValue(superChannelAdminCatCode, UniqueChecker.UC_MSISDN());
		setProductTypeBasedOnFlagValue(superChannelAdminCatCode);
		setUserDomainsBasedOnFlagValue(superChannelAdminCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(String.valueOf(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 6)
	@TestManager(TestKey = "PRETUPS-006")
	public void superAdmin_AddOperatorWithoutFirstName(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOperatorWithoutMandatoryInformation";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER06");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(subSuperAdminCatCode);
		createOperatorUserVO.setFirstName("");
		createOperatorUserVO.setRoles(getOperatorRoles(subSuperAdminCatCode));
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 7)
	@TestManager(TestKey = "PRETUPS-007")
	public void superAdmin_AddOptWithInvalidContactNumber(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOptWithInvalidContactNumber";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER07");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(optCatCode.get(subSuperAdminCatCode));
		createOperatorUserVO.setContactNumber("434SW@#!QA21");
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 8)
	@TestManager(TestKey = "PRETUPS-008")
	public void superAdmin_AddOptWithOldAppointmentDate(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOptWithOldAppointmentDate";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER08");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(optCatCode.get(subSuperAdminCatCode));
		cal.add(Calendar.DATE, -1);
		createOperatorUserVO.setAppointmentdate(dateFormat.format(cal.getTime()));
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 9)
	@TestManager(TestKey = "PRETUPS-009")
	public void superAdmin_AddOptWithExistingMsisdn(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOptWithExistingMsisdn";
		String supChnlAdminCatCode = optCatCode.get("Super Channel Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER09");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(optCatCode.get(supChnlAdminCatCode));
		String existingMsisdn = DBHandler.AccessHandler.getUserInfo("MSISDN", supChnlAdminCatCode, "OPERATOR");
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("ZO"));
		setSuperChannelAdminServicesBasedOnFlagValue("OPT", "NG", "NG", supChnlAdminCatCode);
		setMsisdnForOperatorUserBasedOnFlagValue(supChnlAdminCatCode, existingMsisdn);
		setProductTypeBasedOnFlagValue(supChnlAdminCatCode);
		setUserDomainsBasedOnFlagValue(supChnlAdminCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 10)
	@TestManager(TestKey = "PRETUPS-010")
	public void superAdmin_AddOptWithExistingExternalCode(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOptWithExistingExternalCode";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER10");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setExternalCode(
				DBHandler.AccessHandler.getUserInfo("EXTERNAL_CODE", subSuperAdminCatCode, "OPERATOR"));
		createOperatorUserVO.setUserCatCode(subSuperAdminCatCode);
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		String message = superAdminAddOptUserResponsePojo.getMessage();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "User already exists with this external Code.");
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.assertEquals(message, "User already exists with this external Code.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 11)
	@TestManager(TestKey = "PRETUPS-011")
	public void superAdmin_AddOptWithInvalidEmailId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOptWithEInvalidEmailId";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER11");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(subSuperAdminCatCode);
		createOperatorUserVO.setEmailid("$#sds12AFa*(");
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		String message = superAdminAddOptUserResponsePojo.getMessage();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Invalid Email ID");
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 12)
	@TestManager(TestKey = "PRETUPS-012")
	public void superAdmin_AddOptWithExistingWebLoginId(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOptWithExistingWebLoginId";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER12");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(subSuperAdminCatCode);
		createOperatorUserVO
				.setWebloginid(DBHandler.AccessHandler.getUserInfo("LOGIN_ID", subSuperAdminCatCode, "OPERATOR"));
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		String message = superAdminAddOptUserResponsePojo.getMessage();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "User's WEBLOGINID already exist.");
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 13)
	@TestManager(TestKey = "PRETUPS-013")
	public void superAdmin_AddOptWithToTimeLessThenFromTime(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOptWithToTimeLessThenFromTime";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER13");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setRoles(getOperatorRoles(subSuperAdminCatCode));
		createOperatorUserVO.setUserCatCode(subSuperAdminCatCode);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		createOperatorUserVO.setAllowedTimeFrom(dateFormat.format(cal.getTime()));
		cal.add(Calendar.HOUR, -1);
		createOperatorUserVO.setAllowedTimeTo(dateFormat.format(cal.getTime()));
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 14)
	@TestManager(TestKey = "PRETUPS-014")
	public void superAdmin_AddOptWithInvalidIpAddress(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOptWithInvalidIpAddress";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER14");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setRoles(getOperatorRoles(subSuperAdminCatCode));
		createOperatorUserVO.setUserCatCode(subSuperAdminCatCode);
		createOperatorUserVO.setAllowedip(new RandomGeneration().randomAlphaNumeric(7));
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 15)
	@TestManager(TestKey = "PRETUPS-015")
	public void superAdmin_AddOperatorWithoutSubscriberCode(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOperatorWithoutSubscriberCode";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER15");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(subSuperAdminCatCode);
		createOperatorUserVO.setSubscriberCode("");
		createOperatorUserVO.setRoles(getOperatorRoles(subSuperAdminCatCode));
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 16)
	@TestManager(TestKey = "PRETUPS-016")
	public void superAdmin_AddOperatorWithoutMobileNumber(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOperatorWithoutMobileNumber";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER16");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(subSuperAdminCatCode);
		createOperatorUserVO.setRoles(getOperatorRoles(subSuperAdminCatCode));
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		createOperatorUserVO.setMobileNumber("");
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 17)
	@TestManager(TestKey = "PRETUPS-017")
	public void superAdmin_AddOperatorWithoutDivision(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOperatorWithoutDivision";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER17");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(subSuperAdminCatCode);
		createOperatorUserVO.setDivisionCode("");
		createOperatorUserVO.setRoles(getOperatorRoles(subSuperAdminCatCode));
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 18)
	@TestManager(TestKey = "PRETUPS-018")
	public void superAdmin_AddOperatorWithoutDepartment(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOperatorWithoutDepartment";
		String subSuperAdminCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER18");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(subSuperAdminCatCode);
		createOperatorUserVO.setDepartmentCode("");
		createOperatorUserVO.setRoles(getOperatorRoles(subSuperAdminCatCode));
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdminCatCode);
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdminCatCode, UniqueChecker.UC_MSISDN());
		setProductTypeBasedOnFlagValue(subSuperAdminCatCode);
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 19)
	@TestManager(TestKey = "PRETUPS-019")
	public void superAdmin_AddOperatorWithoutGeographies(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOperatorWithoutGeographies";
		String subSuperAdmCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER19");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(subSuperAdmCatCode);
		createOperatorUserVO.setRoles(getOperatorRoles(subSuperAdmCatCode));
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdmCatCode, UniqueChecker.UC_MSISDN());
		setOperatorServicesBasedOnFlagValue("NG", "NG", subSuperAdmCatCode);
		setProductTypeBasedOnFlagValue(subSuperAdmCatCode);
		createOperatorUserVO.setMultipleGeographyLoc("");
		cal.add(Calendar.DATE, 4);
		createOperatorUserVO.setAppointmentdate(dateFormat.format(cal.getTime()));
		setProductTypeBasedOnFlagValue(subSuperAdmCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 20)
	@TestManager(TestKey = "PRETUPS-020")
	public void superAdmin_AddOperatorWithoutServices(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOperatorWithoutServices";
		String subSuperAdmCatCode = optCatCode.get("Sub Super Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER20");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(subSuperAdmCatCode);
		createOperatorUserVO.setRoles(getOperatorRoles(subSuperAdmCatCode));
		setMsisdnForOperatorUserBasedOnFlagValue(subSuperAdmCatCode, UniqueChecker.UC_MSISDN());
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("NW"));
		createOperatorUserVO.setServices("");
		setProductTypeBasedOnFlagValue(subSuperAdmCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 21)
	@TestManager(TestKey = "PRETUPS-021")
	public void superAdmin_AddOperatorWithoutProducts(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOperatorWithoutProducts";
		String superChannelAdminCatCode = optCatCode.get("Super Channel Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER21");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(optCatCode.get("Super Channel Admin"));
		createOperatorUserVO.setRoles(getOperatorRoles(superChannelAdminCatCode));
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("ZO"));
		setSuperChannelAdminServicesBasedOnFlagValue("OPT", "NG", "NG", superChannelAdminCatCode);
		setMsisdnForOperatorUserBasedOnFlagValue(superChannelAdminCatCode, UniqueChecker.UC_MSISDN());
		createOperatorUserVO.setUserProducts("");
		setUserDomainsBasedOnFlagValue(superChannelAdminCatCode);
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData", priority = 22)
	@TestManager(TestKey = "PRETUPS-022")
	public void superAdmin_AddOperatorWithoutDomains(String loginID, String password, String parentName,
			String categoryName, String categorCode) throws Exception {
		final String methodName = "superAdmin_AddOperatorWithoutDomains";
		String superChannelAdminCatCode = optCatCode.get("Super Channel Admin");
		setTestInitialDetails(methodName, categoryName, "SADMADDOPTUSER22");
		createAddOperatorRequestPayload();
		createOperatorUserVO.setUserCatCode(optCatCode.get("Super Channel Admin"));
		createOperatorUserVO.setRoles(getOperatorRoles(superChannelAdminCatCode));
		createOperatorUserVO.setMultipleGeographyLoc(assignGeographies("ZO"));
		setSuperChannelAdminServicesBasedOnFlagValue("OPT", "NG", "NG", superChannelAdminCatCode);
		setMsisdnForOperatorUserBasedOnFlagValue(superChannelAdminCatCode, UniqueChecker.UC_MSISDN());
		setProductTypeBasedOnFlagValue(superChannelAdminCatCode);
		createOperatorUserVO.setUserDomainCodes("");
		superAdminAddOptUserRequestPojo.setData(createOperatorUserVO);
		executeAddOperatorUserAPI(200, loginID, password);
		int statusCode = superAdminAddOptUserResponsePojo.getStatus();
		String message = superAdminAddOptUserResponsePojo.getMessage();
		Assert.assertEquals(statusCode, 400);
		Assert.assertEquals(message, "Domain cannot be blank.");
		Assertion.assertEquals(String.valueOf(statusCode), "400");
		Assertion.assertEquals(message, "Domain cannot be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
