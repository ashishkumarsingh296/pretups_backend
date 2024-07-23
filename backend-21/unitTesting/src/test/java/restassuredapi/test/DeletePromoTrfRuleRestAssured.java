package restassuredapi.test;

import static com.utils.GenerateToken.getAcccessToken;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.BTSLDateUtil;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import groovyjarjarantlr.collections.List;
import restassuredapi.api.addPromoTrfRuleAPI.AddPromoTrfRuleAPI;
import restassuredapi.api.addagentapi.AddagentApi;
import restassuredapi.api.deletePromoTrfRuleAPI.DeletePromoTrfRuleAPI;
import restassuredapi.api.modifyPromoTrfruleAPI.ModifyPromoTrfRuleAPI;
import restassuredapi.pojo.addPromoRuleReqPojo.AddPromoTransferReqVO;
import restassuredapi.pojo.addPromoRuleReqPojo.ReceiverSectionInputs;
import restassuredapi.pojo.addagentresponsepojo.AddagentResponsePojo;
import restassuredapi.pojo.addchanneluserrequestpojo.AddChannelUserDetails;
import restassuredapi.pojo.addpromoRuleRespPojos.AddPromoTransferRuleRespVO;
import restassuredapi.pojo.addpromoRuleRespPojos.CardGroupVO;
import restassuredapi.pojo.deletePromoReqPojo.DeletePromoTransferReqVO;
import restassuredapi.pojo.deletePromoTrfRespPojo.DeletePromoTransferRuleRespVO;
import restassuredapi.pojo.modifyPromoTrfReqPojos.ModifyPromoTrfRequestPojo;
import restassuredapi.pojo.modifyPromoTrfRespVO.ModifyPromoTransferRuleRespVO;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.DELETE_PROMO_TRF_RULEDETAILS)
public class DeletePromoTrfRuleRestAssured extends BaseTest {
	
	DateFormat df = new SimpleDateFormat("dd/MM/YY");
    Date dateobj = new Date();
    String currentDate=df.format(dateobj);   
	static String moduleCode;
	private  AddPromoTransferReqVO addPromoTransferReqVO = new AddPromoTransferReqVO();
	private AddPromoTransferRuleRespVO addPromoResponsePojo = new AddPromoTransferRuleRespVO();
	private  DeletePromoTransferReqVO deletePromoTransferReqVO = new DeletePromoTransferReqVO();
	private DeletePromoTransferRuleRespVO deletePromoResponsePojo = new DeletePromoTransferRuleRespVO();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	AddChannelUserDetails data = new AddChannelUserDetails();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
	Map<String, Object> headerMap = new HashMap<String, Object>();
	
	
	

	
	
	
	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount() - 4;

		Object[][] data = new Object[rowCount][6];
		int j = 0;
		for (int i = 1; i <= rowCount; i++) {
			if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i).equalsIgnoreCase("NWADM")) {
				data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
				data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				data[j][3] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				data[j][4] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			j++;
			break;
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
	public void deletePromoTrfRuleatUserLevel(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "deletePromoTrfRuleatUserLevel";
		String deletePromoRespmessage= null;
		setTestInitialDetails(methodName, loginID, password, categoryName, "DELPROMOTRFRULE01");
		DeleteRecordVO delRecordVO = createAddPromoTrfRuleReq("USR",loginID); //USER LEVEL PROMO
		executeAddPromoTrfRuleAPI(200, loginID, password);
		String message = addPromoResponsePojo.getMessage();
		 if(message.equalsIgnoreCase("Transfer rule is added successfully")) { //Add promo transfer rule successful.,now test modify promot transfer rule.
			 createDeletePromoTrfRuleReq("USR", loginID); //USR LEVEL PROMO
			 executeDeletePromoTrfRuleAPI(200, loginID, password);
			 deletePromoRespmessage = deletePromoResponsePojo.getMessage();
		 }
		Assertion.assertEquals(deletePromoRespmessage, "Transfer rule is deleted successfully");
		Assert.assertEquals(deletePromoRespmessage, "Transfer rule is deleted successfully");
		Assertion.completeAssertions();
		//cleanOperation(delRecordVO.getNetworkCode(), delRecordVO.getSenderSubSType(), delRecordVO.getReceriversubscType(), delRecordVO.getSenderServiceClassId(), delRecordVO.getReceiverServiceClassID(), delRecordVO.getSubService(), delRecordVO.getServiceType(), delRecordVO.getRuleLevel());
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-002")
	public void deletePromoTrfRuleatGradeLevel(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "deletePromoTrfRuleatGradeLevel";
		String deletePromoRespmessage= null;
		DeleteRecordVO delRecordVOforMod=null;
		setTestInitialDetails(methodName, loginID, password, categoryName, "DELPROMOTRFRULE02");
		DeleteRecordVO delRecordVO =  createAddPromoTrfRuleReq("GRD",loginID); //USER LEVEL PROMO
		executeAddPromoTrfRuleAPI(200, loginID, password);
		String message = addPromoResponsePojo.getMessage();
		
		 if(message.equalsIgnoreCase("Transfer rule is added successfully")) { //Add promo transfer rule successful.,now test modify promot transfer rule.
			   createDeletePromoTrfRuleReq("GRD", loginID); //GRD LEVEL PROMO
			 executeDeletePromoTrfRuleAPI(200, loginID, password);
			 deletePromoRespmessage = deletePromoResponsePojo.getMessage();
		 }
		Assertion.assertEquals(deletePromoRespmessage, "Transfer rule is deleted successfully");
		Assert.assertEquals(deletePromoRespmessage, "Transfer rule is deleted successfully");
		Assertion.completeAssertions();
		//cleanOperation(delRecordVO.getNetworkCode(), delRecordVO.getSenderSubSType(), delRecordVO.getReceriversubscType(), delRecordVO.getSenderServiceClassId(), delRecordVO.getReceiverServiceClassID(), delRecordVO.getSubService(), delRecordVO.getServiceType(), delRecordVO.getRuleLevel());
		
		Log.endTestCase(methodName);
	}
	
	
	
	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-003")
	public void deletePromoTrfRuleatGeographyLevel(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "deletePromoTrfRuleatGeographyLevel";
		String deletePromoRespmessage= null;
		DeleteRecordVO delRecordVOforMod =null;
		setTestInitialDetails(methodName, loginID, password, categoryName, "DELPROMOTRFRULE03");
		DeleteRecordVO delRecordVO = createAddPromoTrfRuleReq("GRP",loginID); //USER LEVEL PROMO
		executeAddPromoTrfRuleAPI(200, loginID, password);
		String message = addPromoResponsePojo.getMessage();
		 if(message.equalsIgnoreCase("Transfer rule is added successfully")) { //Add promo transfer rule successful.,now test modify promot transfer rule.
			 createDeletePromoTrfRuleReq("GRP", loginID); //GRP LEVEL PROMO
			 executeDeletePromoTrfRuleAPI(200, loginID, password);
			 deletePromoRespmessage = deletePromoResponsePojo.getMessage();
		 }
		Assertion.assertEquals(deletePromoRespmessage, "Transfer rule is deleted successfully");
		Assert.assertEquals(deletePromoRespmessage, "Transfer rule is deleted successfully");
		Assertion.completeAssertions();
//		cleanOperation(delRecordVO.getNetworkCode(), delRecordVO.getSenderSubSType(), delRecordVO.getReceriversubscType(), delRecordVO.getSenderServiceClassId(), delRecordVO.getReceiverServiceClassID(), delRecordVO.getSubService(), delRecordVO.getServiceType(), delRecordVO.getRuleLevel());
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-004")
	public void deletePromoTrfRuleatCellLevel(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "deletePromoTrfRuleatCellLevel";
		String deletePromoRespmessage= null;
		DeleteRecordVO delRecordVOForMod=null;
		setTestInitialDetails(methodName, loginID, password, categoryName, "DELPROMOTRFRULE04");
		DeleteRecordVO delRecordVO =createAddPromoTrfRuleReq("CEL",loginID); //USER LEVEL PROMO
		executeAddPromoTrfRuleAPI(200, loginID, password);
		String message = addPromoResponsePojo.getMessage();
		 if(message.equalsIgnoreCase("Transfer rule is added successfully")) { //Add promo transfer rule successful.,now test modify promot transfer rule.
			 createDeletePromoTrfRuleReq("CEL", loginID); //CAT LEVEL PROMO
			 executeDeletePromoTrfRuleAPI(200, loginID, password);
			 deletePromoRespmessage = deletePromoResponsePojo.getMessage();
		 }
		Assertion.assertEquals(deletePromoRespmessage, "Transfer rule is deleted successfully");
		Assert.assertEquals(deletePromoRespmessage, "Transfer rule is deleted successfully");
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
 
	
	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-005")
	public void deletePromoTrfRuleatCategoryLevel(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addPromoTrfRuleatCategoryLevel";
		DeleteRecordVO  delRecordVOforMod =null;   
		String deletePromoRespmessage=null;
		setTestInitialDetails(methodName, loginID, password, categoryName, "DELPROMOTRFRULE05");
		DeleteRecordVO delRecordVO =createAddPromoTrfRuleReq("CAT",loginID); //USER LEVEL PROMO
		executeAddPromoTrfRuleAPI(200, loginID, password);
		String message = addPromoResponsePojo.getMessage();
		 if(message.equalsIgnoreCase("Transfer rule is added successfully")) { //Add promo transfer rule successful.,now test modify promot transfer rule.
			 createDeletePromoTrfRuleReq("CAT", loginID); //CAT LEVEL PROMO
			 executeDeletePromoTrfRuleAPI(200, loginID, password);
			 deletePromoRespmessage = deletePromoResponsePojo.getMessage();
		 }
		Assertion.assertEquals(deletePromoRespmessage, "Transfer rule is deleted successfully");
		Assert.assertEquals(deletePromoRespmessage, "Transfer rule is deleted successfully");
		Assertion.completeAssertions();

		Log.endTestCase(methodName);	
		
	}

	
	

	
	
	private DeleteRecordVO createAddPromoTrfRuleReq(String optionTab,String loginID) {
		DeleteRecordVO deleteRecordVO = new DeleteRecordVO();
		ArrayList receiverList = new ArrayList<>();
		addPromoTransferReqVO.setOptionTab(optionTab);
		if(optionTab.equalsIgnoreCase("USR")) {
			addPromoTransferReqVO.setCategoryCode("DIST");
			addPromoTransferReqVO.setDomainCode("DIST");
			addPromoTransferReqVO.setGeoGraphyDomainType("ZO");
			addPromoTransferReqVO.setGeography("DELHI");
			addPromoTransferReqVO.setUserID("TestUserID212232");
			addPromoTransferReqVO.setPromotionalLevel("USR");
			deleteRecordVO.setSenderSubSType("TestUserID212232");
		}else if (optionTab.equalsIgnoreCase("GRD")) {
			addPromoTransferReqVO.setCategoryCode("DIST");
			addPromoTransferReqVO.setDomainCode("DIST");
			addPromoTransferReqVO.setGrade("DIST");
			addPromoTransferReqVO.setPromotionalLevel("GRD");
			deleteRecordVO.setSenderSubSType("DIST");
		}else if (optionTab.equalsIgnoreCase("GRP")) {
			addPromoTransferReqVO.setGeoGraphyDomainType("ZO");
			addPromoTransferReqVO.setGeography("GA");
			addPromoTransferReqVO.setGrade("DIST");
			addPromoTransferReqVO.setPromotionalLevel("GRP");
			deleteRecordVO.setSenderSubSType("GA");
		}else if(optionTab.equalsIgnoreCase("CEL")) {
			addPromoTransferReqVO.setCellGroupID("23424");
			addPromoTransferReqVO.setPromotionalLevel("CEL");
			deleteRecordVO.setSenderSubSType("23424");
		}else if(optionTab.equalsIgnoreCase("CAT")) {
			
			addPromoTransferReqVO.setDomainCode("DIST");
			addPromoTransferReqVO.setCategoryCode("DIST");
			addPromoTransferReqVO.setPromotionalLevel("CAT");
			deleteRecordVO.setSenderSubSType("DIST");
		}
		
		
		Date currentDate = new Date();
		 Calendar calToDate = Calendar.getInstance();
		 Calendar calFomDate = Calendar.getInstance();  
		 calFomDate.setTime(currentDate);
		 calFomDate.add(Calendar.DAY_OF_MONTH, 1);
		ReceiverSectionInputs receiverInputs = new ReceiverSectionInputs();
		
		String dateFormat = DBHandler.AccessHandler.getSystemPreference(PretupsI.SYSTEM_DATE_FORMAT);
		calToDate.setTime(currentDate);
		calToDate.add(Calendar.DAY_OF_MONTH, 20);
	
		try {
		receiverInputs.setApplicableFrom(BTSLDateUtil.getDateStringFromDate(calFomDate.getTime(), dateFormat) );
		receiverInputs.setApplicableTo(BTSLDateUtil.getDateStringFromDate(calToDate.getTime(), dateFormat));
		}catch(ParseException pe) {
			
		}
		receiverInputs.setRowIndex("1");
		receiverInputs.setTimeSlabs("10:30-12:20,13:29-15:30");
		receiverInputs.setType("PRE");
		String networkCode = DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
		CardGroupVO cardGroupVO = DBHandler.AccessHandler.getCardGroupSetVO( networkCode, PretupsI.C2S_MODULE,"P");
		receiverInputs.setCardGroupSet(cardGroupVO.getCardgroupsetid());
		deleteRecordVO.setNetworkCode(networkCode);
		deleteRecordVO.setSenderServiceClassId("ALL");
		
		String serviceCardGroupID =DBHandler.AccessHandler.getServiceCardGroupid(networkCode);
		receiverInputs.setServiceCardGroupID(serviceCardGroupID);
		final String interfaceCategory = "'" + PretupsI.INTERFACE_CATEGORY_PREPAID + "','" + PretupsI.INTERFACE_CATEGORY_POSTPAID + "','" + PretupsI.INTERFACE_CATEGORY_VOMS + "'";
		String serviceClassID =  DBHandler.AccessHandler.getServiceClassIDByInterface(interfaceCategory);
		deleteRecordVO.setReceiverServiceClassID(serviceClassID);
		receiverInputs.setServiceClassID(serviceClassID);
		String serviceType = DBHandler.AccessHandler.getServiceType(networkCode);
		receiverInputs.setServiceType(serviceType);
		receiverInputs.setStatus("Y");
		receiverInputs.setSubscriberStatusValue("ALL");
		receiverInputs.setSubservice("1");
		receiverInputs.setType("PRE");
		receiverList.add(receiverInputs);
		deleteRecordVO.setReceriversubscType("PRE");
		deleteRecordVO.setServiceType(serviceType);
		deleteRecordVO.setSubService("1");
		deleteRecordVO.setRuleLevel(optionTab);
		addPromoTransferReqVO.setList(receiverList);
		cleanOperation(deleteRecordVO.getNetworkCode(), deleteRecordVO.getSenderSubSType(), deleteRecordVO.getReceriversubscType(), deleteRecordVO.getSenderServiceClassId(), deleteRecordVO.getReceiverServiceClassID(), deleteRecordVO.getSubService(), deleteRecordVO.getServiceType(), deleteRecordVO.getRuleLevel());
		
		return deleteRecordVO;
		
	}
	
	
	
	private DeleteRecordVO createDeletePromoTrfRuleReq(String optionTab,String loginID) {
		DeleteRecordVO deleteRecordVO = new DeleteRecordVO();
		ArrayList receiverList = new ArrayList<>();
		deletePromoTransferReqVO.setOptionTab(optionTab);
		if(optionTab.equalsIgnoreCase("USR")) {
			deletePromoTransferReqVO.setCategoryCode("DIST");
			deletePromoTransferReqVO.setDomainCode("DIST");
			deletePromoTransferReqVO.setGeoGraphyDomainType("ZO");
			deletePromoTransferReqVO.setGeography("DELHI");
			deletePromoTransferReqVO.setUserID("TestUserID212232");
			deletePromoTransferReqVO.setPromotionalLevel("USR");
			deleteRecordVO.setSenderSubSType("TestUserID212232");
		}else if (optionTab.equalsIgnoreCase("GRD")) {
			deletePromoTransferReqVO.setCategoryCode("DIST");
			deletePromoTransferReqVO.setDomainCode("DIST");
			deletePromoTransferReqVO.setGrade("DIST");
			deletePromoTransferReqVO.setPromotionalLevel("GRD");
			deleteRecordVO.setSenderSubSType("DIST");
		}else if (optionTab.equalsIgnoreCase("GRP")) {
			deletePromoTransferReqVO.setGeoGraphyDomainType("ZO");
			deletePromoTransferReqVO.setGeography("GA");
			deletePromoTransferReqVO.setGrade("DIST");
			deletePromoTransferReqVO.setPromotionalLevel("GRP");
			deleteRecordVO.setSenderSubSType("GA");
		}else if(optionTab.equalsIgnoreCase("CEL")) {
			deletePromoTransferReqVO.setCellGroupID("23424");
			deletePromoTransferReqVO.setPromotionalLevel("CEL");
			deleteRecordVO.setSenderSubSType("23424");
		}else if(optionTab.equalsIgnoreCase("CAT")) {
			deletePromoTransferReqVO.setDomainCode("DIST");
			deletePromoTransferReqVO.setCategoryCode("DIST");
			deleteRecordVO.setSenderSubSType("DIST");
		}
		
		
		Date currentDate = new Date();
		 Calendar calToDate = Calendar.getInstance();
		 Calendar calFomDate = Calendar.getInstance();  
		 calFomDate.setTime(currentDate);
		 calFomDate.add(Calendar.DAY_OF_MONTH, 1);
		ReceiverSectionInputs receiverInputs = new ReceiverSectionInputs();
		
		String dateFormat = DBHandler.AccessHandler.getSystemPreference(PretupsI.SYSTEM_DATE_FORMAT);
		calToDate.setTime(currentDate);
		calToDate.add(Calendar.DAY_OF_MONTH, 20);
	
		try {
		receiverInputs.setApplicableFrom(BTSLDateUtil.getDateStringFromDate(calFomDate.getTime(), dateFormat) );
		receiverInputs.setApplicableTo(BTSLDateUtil.getDateStringFromDate(calToDate.getTime(), dateFormat));
		}catch(ParseException pe) {
			
		}
		receiverInputs.setRowIndex("1");
		receiverInputs.setTimeSlabs("10:30-5:20");
		receiverInputs.setType("PRE");
		String networkCode = DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
		CardGroupVO cardGroupVO = DBHandler.AccessHandler.getCardGroupSetVO( networkCode, PretupsI.C2S_MODULE,"P");
		receiverInputs.setCardGroupSet(cardGroupVO.getCardgroupsetid());
		deleteRecordVO.setNetworkCode(networkCode);
		deleteRecordVO.setSenderServiceClassId("ALL");
		
		String serviceCardGroupID =DBHandler.AccessHandler.getServiceCardGroupid(networkCode);
		receiverInputs.setServiceCardGroupID(serviceCardGroupID);
		final String interfaceCategory = "'" + PretupsI.INTERFACE_CATEGORY_PREPAID + "','" + PretupsI.INTERFACE_CATEGORY_POSTPAID + "','" + PretupsI.INTERFACE_CATEGORY_VOMS + "'";
		String serviceClassID =  DBHandler.AccessHandler.getServiceClassIDByInterface(interfaceCategory);
		deleteRecordVO.setReceiverServiceClassID(serviceClassID);
		receiverInputs.setServiceClassID(serviceClassID);
		String serviceType = DBHandler.AccessHandler.getServiceType(networkCode);
		receiverInputs.setServiceType(serviceType);
		receiverInputs.setStatus("Y");
		receiverInputs.setSubscriberStatusValue("ALL");
		receiverInputs.setSubservice("1");
		receiverInputs.setType("PRE");
		receiverList.add(receiverInputs);
		deleteRecordVO.setReceriversubscType("PRE");
		deleteRecordVO.setServiceType(serviceType);
		deleteRecordVO.setSubService("1");
		deleteRecordVO.setRuleLevel(optionTab);
		deletePromoTransferReqVO.setList(receiverList);
		
		return deleteRecordVO;
		
	}

	
	
	public void executeAddPromoTrfRuleAPI(int statusCode, String username, String password) throws IOException {

		Log.info("Entering executeAddPromoTrfRuleAPI()");
		AddPromoTrfRuleAPI addPromoTrfRuleAPI = new AddPromoTrfRuleAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				getAcccessToken(username, password));
		addPromoTrfRuleAPI.addBodyParam(addPromoTransferReqVO);
		addPromoTrfRuleAPI.logRequestBody(addPromoTransferReqVO);
		addPromoTrfRuleAPI.setExpectedStatusCode(statusCode);
		addPromoTrfRuleAPI.setContentType(_masterVO.getProperty("contentType"));
		addPromoTrfRuleAPI.perform();
		addPromoResponsePojo = addPromoTrfRuleAPI.getAPIResponseAsPOJO(AddPromoTransferRuleRespVO.class);
		Log.info("Exiting executeAddPromoTrfRuleAPI()");

	}
	
	
	public void executeDeletePromoTrfRuleAPI(int statusCode, String username, String password) throws IOException {

		Log.info("Entering executeDeletePromoTrfRuleAPI()");
		DeletePromoTrfRuleAPI modifyPromoTrfRuleAPI = new DeletePromoTrfRuleAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				getAcccessToken(username, password));
		modifyPromoTrfRuleAPI.addBodyParam(deletePromoTransferReqVO);
		modifyPromoTrfRuleAPI.logRequestBody(deletePromoTransferReqVO);
		modifyPromoTrfRuleAPI.setExpectedStatusCode(statusCode);
		modifyPromoTrfRuleAPI.setContentType(_masterVO.getProperty("contentType"));
		modifyPromoTrfRuleAPI.perform();
		deletePromoResponsePojo = modifyPromoTrfRuleAPI.getAPIResponseAsPOJO(DeletePromoTransferRuleRespVO.class);
		Log.info("Exiting executeDeletePromoTrfRuleAPI()");

	}



	public void cleanOperation(String networkCode,String sendSubcType,String receiverSubcType,String senderServiceClassID,
			String receiverServiceClassID,String subservice,String serviceType,String ruleLevel ) {
		
System.out.println( networkCode +"->" + sendSubcType +"->" + receiverSubcType+"->" + senderServiceClassID
		+"->" + receiverServiceClassID +"->" + subservice+"->" + serviceType +"->" + ruleLevel );
		
		DBHandler.AccessHandler.deleteTransferRule( networkCode, sendSubcType, receiverSubcType, senderServiceClassID,
				 receiverServiceClassID, subservice, serviceType, ruleLevel);
		
		
		


	}
	
	
	

 private class DeleteRecordVO {
	 
	 String networkCode;
	 String senderSubSType;
	 String receriversubscType;
	 String senderServiceClassId;
	 String receiverServiceClassID;
	 String subService;
	 String serviceType;
	 String ruleLevel;
	  DeleteRecordVO(){
		 
	 }
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getSenderSubSType() {
		return senderSubSType;
	}
	public void setSenderSubSType(String senderSubSType) {
		this.senderSubSType = senderSubSType;
	}
	public String getReceriversubscType() {
		return receriversubscType;
	}
	public void setReceriversubscType(String receriversubscType) {
		this.receriversubscType = receriversubscType;
	}
	public String getSenderServiceClassId() {
		return senderServiceClassId;
	}
	public void setSenderServiceClassId(String senderServiceClassId) {
		this.senderServiceClassId = senderServiceClassId;
	}
	public String getReceiverServiceClassID() {
		return receiverServiceClassID;
	}
	public void setReceiverServiceClassID(String receiverServiceClassID) {
		this.receiverServiceClassID = receiverServiceClassID;
	}
	public String getSubService() {
		return subService;
	}
	public void setSubService(String subService) {
		this.subService = subService;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getRuleLevel() {
		return ruleLevel;
	}
	public void setRuleLevel(String ruleLevel) {
		this.ruleLevel = ruleLevel;
	}
	 
	 
		
		
	}

}
