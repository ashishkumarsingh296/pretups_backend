package restassuredapi.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2sreversal.C2SReversalApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.prepaidrecharge.PrepaidRechargeApi;
import restassuredapi.pojo.c2sreversalrequestpojo.C2SRechargeDetails;
import restassuredapi.pojo.c2sreversalrequestpojo.C2SReversalRequestPojo;
import restassuredapi.pojo.c2sreversalresponsepojo.C2SReversalResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.prepaidrechargepojo.PrepaidRechargeDetails;
import restassuredapi.pojo.prepaidrechargepojo.PrepaidRechargeRequestPojo;
import restassuredapi.pojo.prepaidrechargeresponsepojo.PrepaidRechargeResponsePojo;

@ModuleManager(name = Module.RECHARGE_REVERSAL)
public class C2SReversal extends BaseTest{
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	C2SReversalRequestPojo c2SReversalRequestPojo = new C2SReversalRequestPojo();
	C2SReversalResponsePojo c2SReversalResponsePojo = new C2SReversalResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	PrepaidRechargeRequestPojo addChannelUserRequestPojo = new PrepaidRechargeRequestPojo();
	PrepaidRechargeResponsePojo addChannelUserResponsePojo = new PrepaidRechargeResponsePojo();
	

	List<C2SRechargeDetails> data = new ArrayList<>();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	PrepaidRechargeDetails rchgdata = new PrepaidRechargeDetails();

	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][9];
		int j=0;
		for(int i=1;i<=rowCount;i++) {
			Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			Data[j][7] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i);
			Data[j][8] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			j++;
		}
		return Data;
	}
	
	
	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
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
	
	public void BeforeMethod(String data1, String data2,String categoryName) throws Exception
	{
		
		final String methodName = "Test_OAuthenticationTest";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));


		currentNode.assignCategory("REST");

		setHeaders();
		setupAuth(data1,data2);
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
	
	public String performRecharge(String pin) {
		String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
		rchgdata.setLanguage1(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		rchgdata.setLanguage2(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
		rchgdata.setMsisdn2(prefix + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		rchgdata.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		rchgdata.setAmount(new RandomGeneration().randomNumberWithoutZero(2));
		rchgdata.setExtrefnum(new RandomGeneration().randomNumeric(7));
		rchgdata.setPin(pin);
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int rownum = 1; rownum <= rowCount; rownum++) {
			String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
			String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
			if (service.equals(CustomerRechargeCode)&& !cardGroupName.isEmpty()) {

				rchgdata.setSelector(DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum),service));
				break;
			}
		}
		rchgdata.setDate(_APIUtil.getCurrentTimeStamp());
		addChannelUserRequestPojo.setData(rchgdata);
		
		
		PrepaidRechargeApi addChannelUserAPI = new PrepaidRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(201);
		addChannelUserAPI.perform();
		
		try {
			addChannelUserResponsePojo = addChannelUserAPI.getAPIResponseAsPOJO(PrepaidRechargeResponsePojo.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String txnid = addChannelUserResponsePojo.getDataObject().getTxnid();
		
		return txnid;
	}
	public void setupData(String pin,String MSISDN) {
		data = new ArrayList<>();
		data.add(new C2SRechargeDetails());
		data.get(0).setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		String txnId=performRecharge(pin);
		data.get(0).setTxnid(txnId);
		c2SReversalRequestPojo.setData(data);
		c2SReversalRequestPojo.setPin(pin);
	}
	
	    

	// Successful data with valid data.
	
	  @Test(dataProvider = "userData")
	  public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode, String domainName) throws Exception { 
		  
	  final String methodName = "A_01_Test_success"; 
	  Log.startTestCase(methodName);
	  if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
	  else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
	  
	  CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RECREV1"); 
	  moduleCode =CaseMaster.getModuleCode();
	  
	  currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
	  currentNode.assignCategory("REST"); 
	  setupData(PIN,msisdn);
	  
	  C2SReversalApi addChannelUserAPI = new C2SReversalApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	  addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
	  addChannelUserAPI.addBodyParam(c2SReversalRequestPojo);
	  addChannelUserAPI.setExpectedStatusCode(201);
	  addChannelUserAPI.perform();
	  
	  c2SReversalResponsePojo = addChannelUserAPI.getAPIResponseAsPOJO(C2SReversalResponsePojo.class); 
	  String txnid =c2SReversalResponsePojo.getSuccessList().get(0).getTransactionId();
	  String status =DBHandler.AccessHandler.getTransactionIDStatus(txnid);
	  
	  if(status == "200")
		  Assert.assertEquals(200, status); 
	  
	  Assertion.assertEquals(status, "200");
	  Assertion.completeAssertions(); 
	  Log.endTestCase(methodName);
	  
	  }
	 
	@Test(dataProvider = "userData")
	public void A_02_Test_invalid_token(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode, String domainName) throws Exception {
		final String methodName = "A_01_Test_invalid_token";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RECREV2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,msisdn);
		
		C2SReversalApi addChannelUserAPI = new C2SReversalApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), new RandomGeneration().randomAlphabets(6)+accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(c2SReversalRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(241018);
		addChannelUserAPI.perform();
		c2SReversalResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(C2SReversalResponsePojo.class);
		String status = c2SReversalResponsePojo.getMessageCode();
		Assert.assertEquals(241018, Integer.parseInt(status));
		Assertion.assertEquals(status, "241018");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	public void A_03_Test_blank_txnid(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode, String domainName) throws Exception {
		final String methodName = "A_03_Test_blank_txnid";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RECREV3");
		moduleCode = CaseMaster.getModuleCode();

		  currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,msisdn);
		
		C2SReversalApi addChannelUserAPI = new C2SReversalApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		c2SReversalRequestPojo.getData().get(0).setTxnid("");
		addChannelUserAPI.addBodyParam(c2SReversalRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(11100);
		addChannelUserAPI.perform();
		c2SReversalResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(C2SReversalResponsePojo.class);
		String status = c2SReversalResponsePojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		Assert.assertEquals(11100, Integer.parseInt(status));
		Assertion.assertEquals(status, "11100");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_04_Test_blank_pin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode, String domainName) throws Exception {
		final String methodName = "A_04_Test_blank_pin";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RECREV4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,msisdn);
		
		C2SReversalApi addChannelUserAPI = new C2SReversalApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		c2SReversalRequestPojo.setPin("");
		addChannelUserAPI.addBodyParam(c2SReversalRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(7060);
		addChannelUserAPI.perform();
		c2SReversalResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(C2SReversalResponsePojo.class);
		String status = c2SReversalResponsePojo.getMessageCode();
		Assert.assertEquals(7060, Integer.parseInt(status));
		Assertion.assertEquals(status, "7060");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	public void A_05_Test_invalid_pin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode, String domainName) throws Exception {
		final String methodName = "A_05_Test_invalid_pin";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RECREV5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,msisdn);
		
		C2SReversalApi addChannelUserAPI = new C2SReversalApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		c2SReversalRequestPojo.setPin(new RandomGeneration().randomAlphabets(4));
		addChannelUserAPI.addBodyParam(c2SReversalRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(1080002);
		addChannelUserAPI.perform();
		c2SReversalResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(C2SReversalResponsePojo.class);
		String status = c2SReversalResponsePojo.getMessageCode();
		Assert.assertEquals(7015, Integer.parseInt(status));
		Assertion.assertEquals(status, "7015");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
}
