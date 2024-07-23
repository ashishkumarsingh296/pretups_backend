package restassuredapi.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import restassuredapi.pojo.modifychanneluserrequestpojo.ModifyChannelUserDetails;
import restassuredapi.pojo.modifychanneluserrequestpojo.ModifyChannelUserRequestPojo;
import restassuredapi.pojo.modifychanneluserrequestpojo.Msisdn;
import restassuredapi.pojo.modifychanneluserresponsepojo.ModifyChannelUserResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.api.modifychanneluser.ModifyChannelUserApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;


@ModuleManager(name = Module.MODIFY_CHANNEL_USER)
public class ModifyChannelUser extends BaseTest{
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	ModifyChannelUserRequestPojo modifyChannelUserRequestPojo = new ModifyChannelUserRequestPojo();
	ModifyChannelUserResponsePojo modifyChannelUserResponsePojo = new ModifyChannelUserResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	ModifyChannelUserDetails data = new ModifyChannelUserDetails();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	//NetworkAdminHomePage homepage = new NetworkAdminHomePage(driver);
	//Product product= null;
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
                   transferDetails.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
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
		Date date = new Date();
		msisdn.setIsprimary("Y");
		String msisdnExisting = transferDetails.get("MSISDN");
		msisdn.setPhoneNo(msisdnExisting);
		msisdn.setPin(transferDetails.get("PIN"));
		msisdn.setDescription("");
		msisdn.setStkProfile(transferDetails.get("CATEGORY"));
		Msisdn []msisdn1= new Msisdn[1];
		msisdn1[0] = msisdn;
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setShortName(new RandomGeneration().randomAlphabets(4));
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
		data.setNewExternalcode(new RandomGeneration().randomAlphaNumeric(5));
		data.setFax("");
		data.setFirstName("rahul "+new RandomGeneration().randomAlphabets(4));
		data.setLastName(new RandomGeneration().randomAlphabets(4));
		String userID=DBHandler.AccessHandler.getUserIdLoginID(transferDetails.get("LOGIN_ID_ADMIN"));
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
		data.setLmsProfileId("2427");
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
		data.setWebloginid(transferDetails.get("LOGIN_ID"));
		data.setWebpassword(_masterVO.getProperty("NewPassword"));
		data.setOutletCode("");
		data.setSubOutletCode("");
		data.setServices("");
		data.setMsisdn(msisdn1);
		modifyChannelUserRequestPojo.setData(data);
		
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
	@Test
	@TestManager(TestKey="PRETUPS-6536")
	public void A_01_Test_success() throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		
		ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		modChannelUserAPI.setIdentifierType("LOGINID");
		modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
		modChannelUserAPI.setIdtype("USERLOGIID");
		modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
		modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
		modChannelUserAPI.setExpectedStatusCode(200);
		modChannelUserAPI.perform();
		modifyChannelUserResponsePojo = modChannelUserAPI
				.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
		int statusCode = modifyChannelUserResponsePojo.getStatus();

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	/*@Test
	@TestManager(TestKey="PRETUPS-6517")
	public void A_01_Test_invalid_msisdn() throws Exception {
		final String methodName = "A_01_Test_invalid_msisdn";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		Msisdn []msisdn1= data.getMsisdn();
		msisdn1[0].setPhoneNo(new RandomGeneration().randomAlphaNumeric(4));
		ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		modChannelUserAPI.setIdentifierType("LOGINID");
		modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
		modChannelUserAPI.setIdtype("USERLOGIID");
		modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
		modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
		modChannelUserAPI.setExpectedStatusCode(1021015);
		modChannelUserAPI.perform();
		modifyChannelUserResponsePojo = modChannelUserAPI
				.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
		int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

		Assert.assertEquals(1021015, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "1021015");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
*/	@Test
	@TestManager(TestKey="PRETUPS-6517")
		public void A_02_Test_webLoginID_exist() throws Exception {
			final String methodName = "A_01_Test_webLoginID_exist";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setWebloginid(transferDetails.get("LOGIN_ID_ADMIN"));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(4509);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(4509, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "4509");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
	@Test
	@TestManager(TestKey="PRETUPS-6518")
		public void A_03_Test_stk_invalid() throws Exception {
			final String methodName = "A_01_Test_stk_invalid";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY4");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			Msisdn []msisdn1= data.getMsisdn();
			msisdn1[0].setStkProfile(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(9037);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(9037, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "9037");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
	@Test
	@TestManager(TestKey="PRETUPS-6519")
		public void A_04_Test_language_invalid() throws Exception {
			final String methodName = "A_01_Test_language_invalid";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY5");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setLanguage(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(9044);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(9044, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "9044");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
	@Test
	@TestManager(TestKey="PRETUPS-6520")
		public void A_05_Test_Mobile_No_exist() throws Exception {
			final String methodName = "A_01_Test_Mobile_No_exist";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY6");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			Msisdn []msisdn1= data.getMsisdn();
			msisdn1[0].setPhoneNo(transferDetails.get("MSISDN_ADMIN"));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(4550);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(4550, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "4550");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
	@Test
	@TestManager(TestKey="PRETUPS-6521")
		public void A_06_Test_Invalid_appointment_date() throws Exception {
			final String methodName = "A_01_Test_Invalid_appointment_date";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY7");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setAppointmentdate(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(1004093);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004093, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004093");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6522")
		public void A_07_Test_Invalid_ip_address() throws Exception {
			final String methodName = "A_01_Test_Invalid_ip_address";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY8");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setAllowedip(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(1004089);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004089, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004089");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6523")
		public void A_08_Test_Invalid_payment_type() throws Exception {
			final String methodName = "A_01_Test_Invalid_payment_type";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY9");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setPaymentType(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(9042);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(9042, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "9042");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6524")
		public void A_09_Test_Invalid_allowed_Days() throws Exception {
			final String methodName = "A_01_Test_Invalid_allowed_Days";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY10");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setAlloweddays(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(1004090);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004090, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004090");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6525")
		public void A_10_Test_Invalid_username_prefix() throws Exception {
			final String methodName = "A_01_Test_Invalid_username_prefix";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY11");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setUserNamePrefix(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(9041);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(9041, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "9041");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6526")
		public void A_11_Test_Invalid_username_blank() throws Exception {
			final String methodName = "A_01_Test_Invalid_username_blank";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY12");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setUserName("");
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(5000003);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(5000003, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "5000003");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6527")
		public void A_12_Test_Blank_shortname() throws Exception {
			final String methodName = "A_01_Test_Blank_shortname";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY13");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setShortName("");
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(7656);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(7656, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "7656");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6528")
		public void A_13_Test_invalid_emailid() throws Exception {
			final String methodName = "A_01_Test_Blank_emailid";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY14");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setEmailid(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(10007);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(10007, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "10007");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6529")
		public void A_14_Test_invalid_webPass() throws Exception {
			final String methodName = "A_01_Test_invalid_webPass";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY15");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setWebpassword(Decrypt.encryption(new RandomGeneration().randomNumeric(1)));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(1004032);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004032, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004032");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6530")
		public void A_15_Test_invalid_suspend() throws Exception {
			final String methodName = "A_01_Test_invalid_suspend";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY16");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setInsuspend(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(1004099);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004099, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004099");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6531")
		public void A_16_Test_invalid_suspend_out() throws Exception {
			final String methodName = "A_01_Test_invalid_suspend_out";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY17");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setOutsuspend(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(1004032);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004100, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004100");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6532")
		public void A_17_Test_invalid_document_type() throws Exception {
			final String methodName = "A_01_Test_invalid_document_type";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY18");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setDocumentType(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(9043);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(9043, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "9043");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6533")
		public void A_18_Test_invalid_grade_code() throws Exception {
			final String methodName = "A_01_Test_invalid_grade_code";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY19");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setUsergrade(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(1004098);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(1004098, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "1004098");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6534")
		public void A_19_Test_invalid_commission_id() throws Exception {
			final String methodName = "A_01_Test_invalid_commission_id";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY20");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setCommissionProfileID(new RandomGeneration().randomAlphaNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(7660);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(7660, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "7660");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

	@Test
	@TestManager(TestKey="PRETUPS-6535")
		public void A_20_Test_invalid_transfer_profile_id() throws Exception {
			final String methodName = "A_01_Test_invalid_transfer_profile_id";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MODIFY21");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			data.setTransferProfile(new RandomGeneration().randomNumeric(4));
			ModifyChannelUserApi modChannelUserAPI = new ModifyChannelUserApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			modChannelUserAPI.setIdentifierType("LOGINID");
			modChannelUserAPI.setIdentifierValue(transferDetails.get("LOGIN_ID_ADMIN"));
			modChannelUserAPI.setIdtype("USERLOGIID");
			modChannelUserAPI.setId(transferDetails.get("LOGIN_ID"));
			modChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
			modChannelUserAPI.addBodyParam(modifyChannelUserRequestPojo);
			modChannelUserAPI.setExpectedStatusCode(5200);
			modChannelUserAPI.perform();
			modifyChannelUserResponsePojo = modChannelUserAPI
					.getAPIResponseAsPOJO(ModifyChannelUserResponsePojo.class);
			int statusCode = Integer.valueOf(modifyChannelUserResponsePojo.getMessageCode());

			Assert.assertEquals(5200, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "5200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		}
