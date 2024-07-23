package restassuredapi.test;

import static com.utils.GenerateToken.getAcccessToken;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

import restassuredapi.api.addCategoryAPI.AddCategoryApi;
import restassuredapi.pojo.addCateogryPojo.AddCategoryRespPojo;
import restassuredapi.pojo.addCateogryPojo.AddcategoryReqPojo;
import restassuredapi.pojo.addchanneluserrequestpojo.AddChannelUserDetails;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.ADD_CATEGORY_DETAILS)
public class AddCategoryDetails extends BaseTest{
	DateFormat df = new SimpleDateFormat("dd/MM/YY");
    Date dateobj = new Date();
    String currentDate=df.format(dateobj);   
	static String moduleCode;
public  AddcategoryReqPojo addCatRequestPojo = new AddcategoryReqPojo();
public	  AddCategoryRespPojo addCatResponsePojo = new AddCategoryRespPojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	AddChannelUserDetails data = new AddChannelUserDetails();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
	Map<String, Object> headerMap = new HashMap<String, Object>();
	public  String domainCode,domainName,parentCatCode,parentCatName,previousCategoryCode,previousUserIDPRefix,previousCategoryName=null;
	
	
	
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
	public void addcategoryDetailsP(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addagentetailsP";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDCATEGORY01");
		
		domainCode="DOM"+ BTSLUtil.getRandomNumber(7);		domainName="Domain"+ BTSLUtil.getRandomNumber(4);
		DBHandler.AccessHandler.insertDomain(domainCode, domainName);
		createAddCategorytRequest(domainCode);
		previousUserIDPRefix=addCatRequestPojo.getUserIdPrefix();
		previousCategoryName=addCatRequestPojo.getCategoryName();
		previousCategoryCode=addCatRequestPojo.getCategoryCode();
		executeAddCategoryAPI(200, loginID, password);
		String message = addCatResponsePojo.getMessage();
		System.out.println("Response Message ::: --> " +message);
		Assertion.assertEquals(message, "Category " + addCatRequestPojo.getCategoryName()  + " added successfully");
		Assert.assertEquals(message, "Category " + addCatRequestPojo.getCategoryName()  + " added successfully");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	

	
	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void checkCategoryCodeExist(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "checkCategoryCodeExist";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDCATEGORY02");
		createAddCategorytRequest(domainCode);
		addCatRequestPojo.setCategoryCode(previousCategoryCode.toUpperCase());
		executeAddCategoryAPI(200, loginID, password);
		String message = addCatResponsePojo.getMessage();
		System.out.println("Response Message ::: --> " +message);
		Assertion.assertEquals(message, "Category code already exists");
		Assert.assertEquals(message, "Category code already exists");

		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	

	
	
	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-002")
	public void checkvalidGrphDomain(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "checkvalidGrphDomain";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDCATEGORY03");
		
		domainCode="DOM"+ BTSLUtil.getRandomNumber(7);
		domainName="Domain"+ BTSLUtil.getRandomNumber(4);
		DBHandler.AccessHandler.insertDomain(domainCode, domainName);
		
	
		createAddCategorytRequest(domainCode);
		previousUserIDPRefix=addCatRequestPojo.getUserIdPrefix();
		addCatRequestPojo.setGrphDomainType("JF");
		
		executeAddCategoryAPI(200, loginID, password);
		String message = addCatResponsePojo.getMessage();
		System.out.println("Response Message ::: --> " +message);
		Assertion.assertEquals(message, "Geographical Domain Type is invalid.");
		Assert.assertEquals(message, "Geographical Domain Type is invalid.");

		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	
	


	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void checkDomainCodeMandatory(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "checkDomainCodeMandatory";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDCATEGORY04");
		
		domainCode="DOM"+ BTSLUtil.getRandomNumber(7);
		domainName="Domain"+ BTSLUtil.getRandomNumber(4);
		DBHandler.AccessHandler.insertDomain(domainCode, domainName);
		
		createAddCategorytRequest(domainCode);
		addCatRequestPojo.setDomainCodeforCategory(null);
		
		executeAddCategoryAPI(200, loginID, password);
		String message = addCatResponsePojo.getMessage();
		System.out.println("Response Message ::: --> " +message);
		Assertion.assertEquals(message, "Domain code is mandatory");
		Assert.assertEquals(message, "Domain code is mandatory");

		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}

	

	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-003")
	public void checkCategoryNameMandatory(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "checkCategoryNameMandatory";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDCATEGORY05");
		
		domainCode="DOM"+ BTSLUtil.getRandomNumber(7);
		domainName="Domain"+ BTSLUtil.getRandomNumber(4);
		DBHandler.AccessHandler.insertDomain(domainCode, domainName);
		
		createAddCategorytRequest(domainCode);
		addCatRequestPojo.setCategoryName(null);
		
		executeAddCategoryAPI(200, loginID, password);
		String message = addCatResponsePojo.getMessage();
		System.out.println("Response Message ::: --> " +message);
		Assertion.assertEquals(message, "Category name is mandatory");
		Assert.assertEquals(message, "Category name is mandatory");

		Assertion.completeAssertions();
		cleanOperation();
		Log.endTestCase(methodName);
	}

	
	
	private void createAddCategorytRequest(String domainCode) {
		
		char s[] = {  'I', 'J', 'K', 'L', 'M','N','O','P','Q','L','M','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' } ;
		
		
		addCatRequestPojo.setAgentAllowed("N");
		addCatRequestPojo.setAgentCategoryCode("");
		addCatRequestPojo.setAuthType(null);
		String catCode ="Cat" +RandomStringUtils.random(3, s);
		addCatRequestPojo.setCategoryCode( catCode);
		addCatRequestPojo.setCategoryName(catCode);
		addCatRequestPojo.setCategorySequenceNumber(0);
		addCatRequestPojo.setCategoryStatus("Y");
		addCatRequestPojo.setCategoryType("CHUSR");
		String [] chkArray =new String [] { "EXTGW", "MAPPGW",  "REST","SMSC",   "TPARTYGW", "USSD",  "USSDP",   "VSTK",   "WEB",  "XMLGW"};
		
		addCatRequestPojo.setCheckArray(chkArray);
		addCatRequestPojo.setCp2pPayee("Y");
		addCatRequestPojo.setCp2pPayer("y");
		addCatRequestPojo.setCp2pWithinList("Y");
		addCatRequestPojo.setDisplayAllowed("Y");
		addCatRequestPojo.setDomainCodeforCategory(domainCode);
		addCatRequestPojo.setDomainName("Dom" +RandomStringUtils.random(2, s));
		addCatRequestPojo.setFixedRoles("Y");
		addCatRequestPojo.setGrphDomainType("ZO");
		addCatRequestPojo.setHierarchyAllowed("Y");
		addCatRequestPojo.setLastModifiedTime(0);
		addCatRequestPojo.setListLevelCode("D");
		addCatRequestPojo.setLowBalanceAlertAllow("Y");
		addCatRequestPojo.setMaxTxnMsisdnOld("1");
		addCatRequestPojo.setMaxLoginCount("5");
		addCatRequestPojo.setMessageGatewayList(null);
		addCatRequestPojo.setModifiedMessageGatewayList(null);
		addCatRequestPojo.setModifyAllowed("Y");
		addCatRequestPojo.setMultipleGrphDomains("N");
		addCatRequestPojo.setMultipleLoginAllowed("Y");
		addCatRequestPojo.setNumberOfCategories("1");
		addCatRequestPojo.setOutletsAllowed("N");
		addCatRequestPojo.setParentCategoryCode(catCode);
		addCatRequestPojo.setProductTypeAssociationAllowed("N");
		addCatRequestPojo.setRechargeByParentOnly("Y");
		addCatRequestPojo.setRestrictedMsisdns("y");
		
		String[] roleFlag = new String [] { "PINPWDRPT",
			    "R_APPRV1",
			    "R_APPRV2",
			    "R_APPRV3",
			    "BATCHID",
			    "SCHTRFSTS",
			    "BATCHC2CTRNSFRENQ",
			    "BLKUSERENQ",
			    "MVDTRFENQ",
			    "C2CTRFENQ",
			    "C2STRFANSFERENQ",
			    "VIEWTRFENQ",
			    "OTHERBALANCE",
			    "OTHERCOUNTER",};
		addCatRequestPojo.setRoleFlag(roleFlag);
		addCatRequestPojo.setScheduledTransferAllowed("Y");
		addCatRequestPojo.setServiceAllowed("Y");
		addCatRequestPojo.setTransferToListOnly("Y");
		addCatRequestPojo.setUnctrlTransferAllowed("Y");
		StringBuilder sb = new StringBuilder(); 
	     sb.append(RandomStringUtils.random(2, s));
	    String singleVal= sb.toString().substring(0, sb.toString().length()-1);
	    
	    Random rn = new Random();
	        int singleRandnum = rn.nextInt(9);
	     
		addCatRequestPojo.setUserIdPrefix(singleVal+singleRandnum);
		addCatRequestPojo.setViewOnNetworkBlock("Y");
	}
	
	
	public void executeAddCategoryAPI(int statusCode, String username, String password) throws IOException {

		Log.info("Entering executeAddCategoryAPI()");
		AddCategoryApi addcatAPI = new AddCategoryApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				getAcccessToken(username, password));
		addcatAPI.addBodyParam(addCatRequestPojo);
		addcatAPI.logRequestBody(addCatRequestPojo);
		addcatAPI.setExpectedStatusCode(statusCode);
		addcatAPI.setContentType(_masterVO.getProperty("contentType"));
		addcatAPI.perform();
		addCatResponsePojo = addcatAPI.getAPIResponseAsPOJO(AddCategoryRespPojo.class);
		Log.info("Exiting executeAddCategoryAPI()");

	}



	public void cleanOperation( ) {
		Log.info("deleting category code : " + parentCatCode);
		DBHandler.AccessHandler.deleteCategory(parentCatCode);
		Log.info("deleting domain code : " + domainCode);
		DBHandler.AccessHandler.deleteDomain(domainCode);
		
		


	}

	


}
