package restassuredapi.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.addchanneluser.AddChannelUserApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.addchanneluserrequestpojo.AddChannelUserDetails;
import restassuredapi.pojo.addchanneluserrequestpojo.AddChannelUserRequestPojo;
import restassuredapi.pojo.addchanneluserrequestpojo.Msisdn;
import restassuredapi.pojo.addchanneluserresponsepojo.AddChannelUserResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.ADD_CHANNEL_USER)
public class AddChannelUser extends BaseTest{
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	AddChannelUserRequestPojo addChannelUserRequestPojo = new AddChannelUserRequestPojo();
	AddChannelUserResponsePojo addChannelUserResponsePojo = new AddChannelUserResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	AddChannelUserDetails data = new AddChannelUserDetails();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	HashMap<String,String> transferDetails=new HashMap<String,String>(); 
	public void getExcelData(){
		

	       String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int OperatorRowCount = ExcelUtility.getRowCount();
        for (int i = 1; i < OperatorRowCount; i++) {
               String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
               String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
               if (CategoryName.equals("DIST") && (!LoginID.equals(null) || !LoginID.equals(""))) {
             	  transferDetails.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
             	  transferDetails.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
                   transferDetails.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
                   transferDetails.put("COMM_PROFILE", ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i));
                   transferDetails.put("TRANS_PROFILE", ExcelUtility.getCellData(0, ExcelI.SA_TCP_PROFILE_ID, i));
                   transferDetails.put("GEOGRAPHY", ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, i));
                   transferDetails.put("CATEGORY", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i));
                   transferDetails.put("GRADE", ExcelUtility.getCellData(0, ExcelI.GRADE, i));
                   transferDetails.put("DOMAIN_CODE", ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, i));
               }
        }
        
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int OperatorRowCountAdmin = ExcelUtility.getRowCount();
        for (int i = 1; i < OperatorRowCountAdmin; i++) {
               String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
               String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
               if (CategoryName.equals("BCU") && (!LoginID.equals(null) || !LoginID.equals(""))) {
             	  transferDetails.put("LOGIN_ID_ADMIN", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
             	  transferDetails.put("PASSWORD_ADMIN", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
             	 transferDetails.put("MSISDN_ADMIN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
             	transferDetails.put("CATEGORY_ADMIN", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i));
             	transferDetails.put("GEO_DOMAIN", ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i));
               }
        }
        
     ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
    	int rowCount = ExcelUtility.getRowCount();
    	rowCount = 1;
    
    	for (int i = 1; i <= rowCount; i++) {
    		transferDetails.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
    		transferDetails.put("type", ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
    	}
		}
	
	
	public void setupData() {
		getExcelData();
		Msisdn msisdn = new Msisdn();
		msisdn.setIsprimary("Y");
		String msisdnExisting = UniqueChecker.UC_MSISDN();
		msisdn.setPhoneNo(msisdnExisting);
		msisdn.setPin(_masterVO.getProperty("PIN"));
		msisdn.setDescription("");
		msisdn.setStkProfile(transferDetails.get("CATEGORY"));
		Msisdn []msisdn1= new Msisdn[1];
		msisdn1[0] = msisdn;
		data.setDomain(DBHandler.AccessHandler.getDomainCodeCatgories(transferDetails.get("CATEGORY")));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setNewExternalcode(UniqueChecker.UC_EXTCODE());
		data.setShortName(new RandomGeneration().randomAlphabets(4));
		data.setUserCatCode(transferDetails.get("CATEGORY"));
		data.setParentCategory(transferDetails.get("CATEGORY"));
		data.setAddress1("");
		data.setAddress2("");
		data.setAlloweddays("");
		data.setAllowedip("");
		data.setCity("");
		data.setState("");
		data.setSsn("");
		data.setCountry("");
		data.setAllowedTimeFrom("");
		data.setAllowedTimeTo("");
		data.setAppointmentdate("");
		data.setCommissionProfileID(DBHandler.AccessHandler.getCommProfileID(transferDetails.get("COMM_PROFILE")));
		data.setCompany("");
		data.setContactNumber("");
		data.setContactPerson("");
		data.setControlGroup("N");
		data.setDesignation("");
		data.setDocumentNo("");
		data.setDocumentType("");
		data.setEmailid(randStr.randomAlphaNumeric(5).toLowerCase() + "@mail.com");
		data.setOtherEmail(randStr.randomAlphaNumeric(5).toLowerCase()+"@mail.com");
		data.setEmpcode(new RandomGeneration().randomAlphaNumeric(5));
		data.setFax("");
		data.setFirstName("rahul "+new RandomGeneration().randomAlphabets(4));
		data.setLastName(new RandomGeneration().randomAlphabets(4));
		String userID=DBHandler.AccessHandler.getUserIdLoginID(transferDetails.get("LOGIN_ID_ADMIN"));
		data.setGeographicalDomain(DBHandler.AccessHandler.getGrpDomainCode(userID));
		if(DBHandler.AccessHandler.getCategoryDetails("grph_domain_type", "BCU").equals(DBHandler.AccessHandler.getCategoryDetails("grph_domain_type", "DIST")))
			data.setGeographyCode(DBHandler.AccessHandler.getGrpDomainCode(userID));
			else 
		data.setGeographyCode(DBHandler.AccessHandler.getGrpDomainCodeFromName(transferDetails.get("GEOGRAPHY")));
		data.setGrouprole("");
		data.setRoleType("N");
		data.setInsuspend("N");
		data.setOutsuspend("N");
		data.setLanguage("");
		data.setLatitude("");
		data.setLongitude("");
		data.setLmsProfileId("");
		data.setLowbalalertother("N");
		data.setLowbalalertparent("N");
		data.setLowbalalertself("N");
		data.setPaymentType("");
		data.setTransferProfile(DBHandler.AccessHandler.getTransferProfileID(_masterVO.getMasterValue(MasterI.NETWORK_CODE),transferDetails.get("CATEGORY"),"USER"));
		if("TRUE".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("TRF_RULE_USER_LEVEL_ALLOW")))
		{
			data.setTransferRuleType(DBHandler.AccessHandler.getLookUpCodeFromType("TRFRU"));
		}
		data.setUserCode("");
		data.setUsergrade(DBHandler.AccessHandler.getGradeCode(transferDetails.get("GRADE")));
		data.setUserName(data.getFirstName());
		data.setUserNamePrefix(DBHandler.AccessHandler.getLookUpCodeFromType("USRPX"));
		data.setVoucherTypes("");
		data.setWebloginid("rah"+UniqueChecker.UC_LOGINID());
		data.setWebpassword(_masterVO.getProperty("NewPassword"));
		data.setOutletCode("");
		data.setSubOutletCode("");
		data.setServices("");
		data.setMsisdn(msisdn1);
		addChannelUserRequestPojo.setData(data);
		addChannelUserRequestPojo.setIdentifierType("loginid");
		addChannelUserRequestPojo.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
		
		oAuthenticationRequestPojo.setIdentifierType("loginid");
		oAuthenticationRequestPojo.setIdentifierValue(transferDetails.get("LOGIN_ID"));
		oAuthenticationRequestPojo.setPasswordOrSmspin(transferDetails.get("PASSWORD"));
		
		}
	
	Map<String, Object> headerMap = new HashMap<String, Object>();


	public void setHeaders() {
		headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
		headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
		headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
		headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
		headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
		headerMap.put("requestGatewayType",_masterVO.getProperty("requestGatewayType") );
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}
	 protected static String accessToken;
	    
	    @BeforeMethod ()
	    public void BeforeMethod() throws Exception
	    {
	    	if(accessToken==null) {
	    	final String methodName = "Test_OAuthenticationTest";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());

			currentNode.assignCategory("REST");
			setupData();
			setHeaders();
			OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),headerMap);
			oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
			oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
			oAuthenticationAPI.setExpectedStatusCode(200);
			oAuthenticationAPI.perform();
			oAuthenticationResponsePojo = oAuthenticationAPI
					.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
			long statusCode = oAuthenticationResponsePojo.getStatus();
			
			accessToken = oAuthenticationResponsePojo.getToken();
			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
	    	}
	
	    }


	// Successful data with valid data.
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6516")
	public void A_01_Test_success() throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(201);
		addChannelUserAPI.perform();
		addChannelUserResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
		int statusCode = addChannelUserResponsePojo.getStatus();

		Assert.assertEquals(201, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "201");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test
	@TestManager(TestKey="PRETUPS-6496")
	public void A_02_Test_invalid_parentCategory() throws Exception {
		final String methodName = "A_01_Test_invalid_parentCategory";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		data.setParentCategory("");
		AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(9033);
		addChannelUserAPI.perform();
		addChannelUserResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
		int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

		Assert.assertEquals(9033, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "9033");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
		@Test
		@TestManager(TestKey="PRETUPS-6497")
		public void A_03_Test_webLoginID_exist() throws Exception {
			final String methodName = "A_01_Test_webLoginID_exist";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setWebloginid(transferDetails.get("LOGIN_ID_ADMIN"));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(1004031);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004031, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004031");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		@Test
		@TestManager(TestKey="PRETUPS-6498")
		public void A_04_Test_extcode_exist() throws Exception {
			final String methodName = "A_01_Test_extcode_exist";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD4");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setNewExternalcode(DBHandler.AccessHandler.getdetailsfromUsersTable(transferDetails.get("LOGIN_ID"),"external_code")[0]);
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(1001011);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1001011, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1001011");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
		@Test
		@TestManager(TestKey="PRETUPS-6499")
		public void A_05_Test_geography_invalid() throws Exception {
			final String methodName = "A_01_Test_geography_invalid";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD5");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setGeographyCode(new RandomGeneration().randomNumeric(4));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(1021024);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1021024, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1021024");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
		@Test
		@TestManager(TestKey="PRETUPS-6500")
		public void A_06_Test_language_invalid() throws Exception {
			final String methodName = "A_01_Test_language_invalid";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD6");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setLanguage(new RandomGeneration().randomNumeric(4));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(9044);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(9044, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "9044");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
		@Test
		@TestManager(TestKey="PRETUPS-6501")
		public void A_07_Test_stk_profile_invalid() throws Exception {
			final String methodName = "A_01_Test_stk_profile_invalid";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD7");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			Msisdn []msisdn1= data.getMsisdn();
			msisdn1[0].setStkProfile(new RandomGeneration().randomNumeric(4));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(9037);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(9037, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "9037");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6502")
		public void A_08_Test_mobile_no_already_exist() throws Exception {
			final String methodName = "A_01_Test_mobile_no_already_exist";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD8");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			Msisdn []msisdn1= data.getMsisdn();
			msisdn1[0].setPhoneNo(transferDetails.get("MSISDN"));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(4550);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(4550, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "4550");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6503")
		public void A_09_Test_mobile_no_not_in_network() throws Exception {
			final String methodName = "A_01_Test_mobile_no_not_in_network";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD9");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			Msisdn []msisdn1= data.getMsisdn();
			msisdn1[0].setPhoneNo(DBHandler.AccessHandler.getNetworkPrefixFromNetwork(_masterVO.getMasterValue(MasterI.NETWORK_CODE),"Y")+new RandomGeneration().randomNumeric(6));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(1004088);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004088, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004088");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6504")
		public void A_10_Test_Invalid_appointment_date() throws Exception {
			final String methodName = "A_01_Test_Invalid_appointment_date";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD10");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setAppointmentdate(new RandomGeneration().randomNumeric(4));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(1004093);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004093, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004093");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6505")
		public void A_11_Test_Invalid_allowed_days() throws Exception {
			final String methodName = "A_01_Test_Invalid_allowed_days";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD11");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setAlloweddays(new RandomGeneration().randomNumeric(4));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(1004090);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004090, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004090");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6506")
		public void A_12_Test_Invalid_Payment_type() throws Exception {
			final String methodName = "A_01_Test_Invalid_Payment_type";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD12");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setPaymentType(new RandomGeneration().randomNumeric(4));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(9042);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(9042, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "9042");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6507")
		public void A_13_Test_Blank_shortname() throws Exception {
			final String methodName = "A_01_Test_Blank_shortname";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD13");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setShortName("");
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setIdentifierType("LOGINID");
			addChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			addChannelUserAPI.setIdtype("USERLOGIID");
			addChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(7656);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(7656, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "7656");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6508")
		public void A_14_Test_invalid_username_prefix() throws Exception {
			final String methodName = "A_01_Test_invalid_username_prefix";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY14");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setUserNamePrefix(new RandomGeneration().randomNumeric(4));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(9041);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(9041, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "9041");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6509")
		public void A_15_Test_invalid_emailid() throws Exception {
			final String methodName = "A_01_Test_invalid_emailid";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY15");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setEmailid(new RandomGeneration().randomNumeric(4));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(10007);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(10007, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "10007");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6510")
		public void A_16_Test_webpass_invalid() throws Exception {
			final String methodName = "A_01_Test_webpass_invalid";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD16");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setWebpassword(Decrypt.encryption(new RandomGeneration().randomAlphabets(1)));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(1004032);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004032, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004032");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6511")
		public void A_17_Test_primary_msisdn_blank() throws Exception {
			final String methodName = "A_01_Test_primary_msisdn_blank";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD17");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			Msisdn []msisdn1= data.getMsisdn();
			msisdn1[0].setPhoneNo("");
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(1004006);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004006, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004006");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6512")
		public void A_18_Test_invalid_contact_no() throws Exception {
			final String methodName = "A_01_Test_invalid_contact_no";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD18");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setContactNumber(new RandomGeneration().randomAlphaNumeric(8));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(1004021);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004021, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004021");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6513")
		public void A_19_Test_invalid_group_role() throws Exception {
			final String methodName = "A_01_Test_invalid_group_role";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD19");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setGrouprole(new RandomGeneration().randomNumeric(4));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(1004096);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004096, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004096");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6514")
		public void A_20_Test_invalid_voucher_type() throws Exception {
			final String methodName = "A_01_Test_invalid_voucher_type";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD20");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setVoucherTypes(new RandomGeneration().randomNumeric(4));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(8055);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(8055, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "8055");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test
		@TestManager(TestKey="PRETUPS-6515")
		public void A_21_Test_invalid_services() throws Exception {
			final String methodName = "A_01_Test_invalid_services";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("ADD21");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setServices(new RandomGeneration().randomNumeric(4));
			AddChannelUserApi addChannelUserAPI = new AddChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
			addChannelUserAPI.setExpectedStatusCode(1004040);
			addChannelUserAPI.perform();
			addChannelUserResponsePojo = addChannelUserAPI
					.getAPIResponseAsPOJO(AddChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(addChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004040, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004040");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		}
