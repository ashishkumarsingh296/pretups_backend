package restassuredapi.test;

import static com.utils.GenerateToken.getAcccessToken;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.addagentapi.AddagentApi;
import restassuredapi.pojo.addagentrequestpojo.AddAgentRequestPojo;
import restassuredapi.pojo.addagentresponsepojo.AddagentResponsePojo;
import restassuredapi.pojo.addchanneluserrequestpojo.AddChannelUserDetails;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.ADD_AGENT_DETAILS)
public class Addagentdetails extends BaseTest {
	
	DateFormat df = new SimpleDateFormat("dd/MM/YY");
    Date dateobj = new Date();
    String currentDate=df.format(dateobj);   
	static String moduleCode;
	private  AddAgentRequestPojo addAgentRequestPojo = new AddAgentRequestPojo();
	private AddagentResponsePojo addagentResponsePojo = new AddagentResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	AddChannelUserDetails data = new AddChannelUserDetails();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
	Map<String, Object> headerMap = new HashMap<String, Object>();
	private  String domainCode,domainName,parentCatCode,parentCatName,agentCategoryCode,previousUserIDPRefix=null;
	
	
	
	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount() - 4;

		Object[][] data = new Object[rowCount][6];
		int j = 0;
		for (int i = 1; i <= rowCount; i++) {
			if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i).equalsIgnoreCase("SUADM")) {
				data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
				data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				data[j][3] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				data[j][4] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			j++;
		    }
		}
		Object[][] data2 = new Object[j][6];
		
		for(int k=0;k<data2.length;k++) {
		  for(int m=0;m<5;m++) {	
			  data2[k][m] = data[k][m];
		  }
		}
		
		return data2;
	}

	
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
	
	

	public void setTestInitialDetails(String methodName, String loginID, String password, String categoryName,
			String caseId) throws Exception {
		Log.startTestCase(methodName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID(caseId);
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
	}
	
	
	@Test(dataProvider = "userData", priority = 1)
	@TestManager(TestKey = "PRETUPS-001")
	public void addagentetailsP(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addagentetailsP";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDAGENT01");
		
		domainCode="DOM"+ BTSLUtil.getRandomNumber(7);
		domainName="Domain"+ BTSLUtil.getRandomNumber(4);
		DBHandler.AccessHandler.insertDomain(domainCode, domainName);
		
		parentCatCode="CT"+ BTSLUtil.getRandomNumber(5);
		parentCatName="Cat"+ BTSLUtil.getRandomNumber(5);
		DBHandler.AccessHandler.insertCategory(domainCode,parentCatCode, parentCatName);
		createAddAgentRequest(domainCode,parentCatCode, parentCatName);
		previousUserIDPRefix=addAgentRequestPojo.getUserIDPrefix();
		executeAddAgentAPI(200, loginID, password);
		String message = addagentResponsePojo.getMessage();
		System.out.println("Response Message ::: --> " +message);
		Assertion.assertEquals(message, "Category " + addAgentRequestPojo.getAgentCategoryName()  + " added successfully.");
		Assert.assertEquals(message, "Category " + addAgentRequestPojo.getAgentCategoryName()  + " added successfully.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void addagentetailsN1(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addagentetailsN1";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDAGENT02");
		createAddAgentRequest(domainCode,parentCatCode, parentCatName);
		addAgentRequestPojo.setAgentCategoryCode(agentCategoryCode);
		
		
		executeAddAgentAPI(400, loginID, password);
		String message = addagentResponsePojo.getMessage();
		Assertion.assertEquals(message, "Category code already exists.");
		Assert.assertEquals(message, "Category code already exists.");
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-003")
	public void addagentetailsN3(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addagentetailsN3";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDAGENT03");
		
		
		createAddAgentRequest(domainCode,parentCatCode, parentCatName);
		addAgentRequestPojo.setAgentCategoryCode(parentCatCode+"B");
		char s[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M','N','O','P','Q','L','M' } ;
		addAgentRequestPojo.setUserIDPrefix(RandomStringUtils.random(2, s));
		executeAddAgentAPI(400, loginID, password);
		String message = addagentResponsePojo.getMessage();
		Assertion.assertEquals(message, "Category name already exists.");
		Assert.assertEquals(message, "Category name already exists.");
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void addagentetailsN4(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addagentetailsN4";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDAGENT04");
		
		
		createAddAgentRequest(domainCode,parentCatCode, parentCatName);
		char s[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M','N','O','P','Q','L','M' } ;
		addAgentRequestPojo.setUserIDPrefix(RandomStringUtils.random(2, s));
		addAgentRequestPojo.setAgentCategoryName("Testcat3");
		addAgentRequestPojo.setAgentCategoryCode(parentCatCode+"B");
		addAgentRequestPojo.setGeoDomainType("PA");
		
		executeAddAgentAPI(400, loginID, password);
		String message = addagentResponsePojo.getMessage();
		Assertion.assertEquals(message, "Geographical Domain Type is invalid.");
		Assert.assertEquals(message, "Geographical Domain Type is invalid.");
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-005")
	public void addagentetailsN6(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addagentetailsN6";
		
		
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDAGENT05");
		parentCatCode="CT"+ BTSLUtil.getRandomNumber(5);
		parentCatName="Cat"+ BTSLUtil.getRandomNumber(5);
		DBHandler.AccessHandler.insertCategory(domainCode,parentCatCode, parentCatName);
		createAddAgentRequest(domainCode,parentCatCode, parentCatName);
		addAgentRequestPojo.setUserIDPrefix(previousUserIDPRefix);
		
		String userIdPrefix[] = { previousUserIDPRefix };
		String fomattedMsg = MessageFormat.format("UserID prefix {0} already exists",userIdPrefix);
		
		executeAddAgentAPI(400, loginID, password);
		String message = addagentResponsePojo.getMessage();
		Assertion.assertEquals(message, fomattedMsg);
		Assert.assertEquals(message, fomattedMsg);
		Assertion.completeAssertions();
		cleanOperation();
		Log.endTestCase(methodName);
	}
	
	
	
		



	
	
	private void createAddAgentRequest(String domainCode,String parentCatCode,String parentCatName) {
		
		char s[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M','N','O','P','Q','L','M' } ;
		
		addAgentRequestPojo.setDomainCodeofCategory(domainCode);
		addAgentRequestPojo.setDomainName("Test");
		addAgentRequestPojo.setParentCategoryCode(parentCatCode);
		addAgentRequestPojo.setAgentCategoryCode(parentCatCode+"A");
		agentCategoryCode=parentCatCode+"A";
		addAgentRequestPojo.setAgentCategoryName(parentCatCode+"A");
		addAgentRequestPojo.setGeoDomainType("AR");
		addAgentRequestPojo.setRoleType("Y");
		StringBuilder sb = new StringBuilder(); 
	     sb.append(RandomStringUtils.random(2, s));
		addAgentRequestPojo.setUserIDPrefix(sb.reverse().toString());
		
		addAgentRequestPojo.setOutletAllowed("Y");
		addAgentRequestPojo.setHierarchyAllowed("Y");
		ArrayList<String> roleFlaglist = new ArrayList<String>();
		
		roleFlaglist.add("SCHEDULETOPUP");
		roleFlaglist.add("BATCHID");
		roleFlaglist.add( "CANCELSCHEDULE");
		roleFlaglist.add("CNCLSCHEDULED");
		roleFlaglist.add( "RESCHEDULETOPUP");
		roleFlaglist.add(  "SCHTRFSTS");
		roleFlaglist.add( "VIEWSUBSSCHEDULE");
		roleFlaglist.add("VIEWSCHEDULED");
		roleFlaglist.add("C2CRETURN");
		addAgentRequestPojo.setRoleFlag(roleFlaglist);
		addAgentRequestPojo.setAllowedSources("EXTGW,MAPPGW,REST,SMSC,TPARTYGW,USSD,USSDP,VSTK,WEB,XMLGW");
		addAgentRequestPojo.setMultipleLoginAllowed("Y");
		addAgentRequestPojo.setScheduleTransferAllowed("Y");
		addAgentRequestPojo.setUncontrolledTransferAllowed("Y");
		addAgentRequestPojo.setRestrictedMsisdn("Y");
		addAgentRequestPojo.setServicesAllowed("Y");
		addAgentRequestPojo.setViewonNetworkBlock("Y");
		addAgentRequestPojo.setAllowLowBalanceAlert("Y");
		addAgentRequestPojo.setMaximumLoginCount("7");
		addAgentRequestPojo.setMaximumTransMsisdn("10");
		addAgentRequestPojo.setTransferToListOnly("Y");
		addAgentRequestPojo.setRechargeThruParentOnly("Y");
		addAgentRequestPojo.setCp2pPayee("Y");
		addAgentRequestPojo.setCp2pPayer("Y");
		addAgentRequestPojo.setAgentAllowed("Y");
		addAgentRequestPojo.setCp2pWithinList("Y");
		addAgentRequestPojo.setParentOrOwnerRadioValue("D");
	}
	
	
	public void executeAddAgentAPI(int statusCode, String username, String password) throws IOException {

		Log.info("Entering executeAddAgentAPI()");
		AddagentApi addagentApi = new AddagentApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				getAcccessToken(username, password));
		addagentApi.addBodyParam(addAgentRequestPojo);
		addagentApi.logRequestBody(addAgentRequestPojo);
		addagentApi.setExpectedStatusCode(statusCode);
		addagentApi.setContentType(_masterVO.getProperty("contentType"));
		addagentApi.perform();
		addagentResponsePojo = addagentApi.getAPIResponseAsPOJO(AddagentResponsePojo.class);
		Log.info("Exiting executeAddAgentAPI()");

	}



	public void cleanOperation( ) {
		Log.info("deleting category code : " + parentCatCode);
		DBHandler.AccessHandler.deleteCategory(parentCatCode);
		Log.info("deleting agent category code : " + agentCategoryCode);
		DBHandler.AccessHandler.deleteCategory(agentCategoryCode);
		Log.info("deleting domain code : " + domainCode);
		DBHandler.AccessHandler.deleteCategory(domainCode);
		
		


	}

	

}
