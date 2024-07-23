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

import restassuredapi.api.modifyTransferCntrlProfileapi.ModifyTransferControlProfileAPI;
import restassuredapi.pojo.addTCPRequestPojo.AddTCPSaveRequestPojo;
import restassuredapi.pojo.addTCPRequestPojo.TransferProfileProductReqVO;
import restassuredapi.pojo.modifyTCPReqPojo.ModifyTCPRequestPojo;
import restassuredapi.pojo.modifyTCPRespPojo.ModifyTCPRespPojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.MODIFY_TCP_RA)
public class ModifyTCPRestAssuredTest extends BaseTest{
	
	DateFormat df = new SimpleDateFormat("dd/MM/YY");
    Date dateobj = new Date();
    String currentDate=df.format(dateobj);   
	static String moduleCode;
	 public ModifyTCPRequestPojo modifyTCPRequestPojo = new ModifyTCPRequestPojo();
	 public ModifyTCPRespPojo modifyTCPRespPojo = new ModifyTCPRespPojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
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
		  for(int m=0;m<=5;m++) {	
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
	public void modifyTransferControlProfilePositive(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "modifyTransferControlProfilePositive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "MODIFYTRANSFCTRLPROFIE01");
		String networkCode =DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
		
		AddTCPRestAssuredTest addTestCase = new AddTCPRestAssuredTest();
		AddTCPSaveRequestPojo addTCPSaveRequestPojo =addTestCase.createAddTCPRequest(categoryCode, networkCode);;
		addTestCase.excecuteAddTCPAPI(200, loginID, password);
		 Log.info(addTCPSaveRequestPojo.getProfileName()  + "got added " );
	     String profileID =DBHandler.AccessHandler.getTProfileIDbyProfileName(addTCPSaveRequestPojo.getProfileName());;
		createmodifyTCPRequest(categoryCode,networkCode);
		modifyTCPRequestPojo.setProfileID(profileID);
		excecuteModifyTCPAPI(200, loginID, password);
		String message = modifyTCPRespPojo.getMessage();
		System.out.println("Response Message ::: --> " +message);
		Assertion.assertEquals(message, "Successfully updated transfer control profile");
		Assert.assertEquals(message, "Successfully updated transfer control profile");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

	
private void createmodifyTCPRequest(String categoryCode,String networkCode) {
		
		TransferProfileProductReqVO transferProfileProductReqVO = new TransferProfileProductReqVO();
		transferProfileProductReqVO.setAllowedMaxPercentage("10");
		transferProfileProductReqVO.setAllowedMaxPercentageInt(0);
		transferProfileProductReqVO.setAltBalance("5");
		transferProfileProductReqVO.setAltBalanceLong(0);
		transferProfileProductReqVO.setC2sMaxTxnAmt("10");
		transferProfileProductReqVO.setC2sMaxTxnAmtAsLong(0);  
		transferProfileProductReqVO.setC2sMinTxnAmt("10");  
		transferProfileProductReqVO.setC2sMinTxnAmtAsLong(0);
		transferProfileProductReqVO.setCurrentBalance("10");
		transferProfileProductReqVO.setMaxBalance("10");  
		transferProfileProductReqVO.setMaxBalanceAsLong(0);  
		transferProfileProductReqVO.setMinBalance("4");  
		transferProfileProductReqVO.setMinResidualBalanceAsLong(0);
		transferProfileProductReqVO.setProductCode("ETOPUP");
		transferProfileProductReqVO.setProductName("Etopup");  
		transferProfileProductReqVO.setProductCode("101");  
	 	List productBalanceList = new ArrayList();
	 	productBalanceList.add(transferProfileProductReqVO); 
	 	modifyTCPRequestPojo.setCategoryCode(categoryCode);
		modifyTCPRequestPojo.setDailyInAltCount("10");
		modifyTCPRequestPojo.setDailyInAltValue("10");
		modifyTCPRequestPojo.setDailyInCount("10");
		modifyTCPRequestPojo.setDailyInValue("10");
		modifyTCPRequestPojo.setDailyOutAltCount("10");
		modifyTCPRequestPojo.setDailyOutAltValue("10");  
		modifyTCPRequestPojo.setDailyOutCount("10");
		modifyTCPRequestPojo.setDailyOutValue("10");
		modifyTCPRequestPojo.setDailySubscriberInAltCount("10");  
		modifyTCPRequestPojo.setDailySubscriberInAltValue("10");  
		modifyTCPRequestPojo.setDailySubscriberInCount("10");
		modifyTCPRequestPojo.setDailySubscriberInValue("10");
		modifyTCPRequestPojo.setDailySubscriberOutAltCount("10"); 
		modifyTCPRequestPojo.setDailySubscriberOutAltValue("10");  
		modifyTCPRequestPojo.setDailySubscriberOutCount("10");
		modifyTCPRequestPojo.setDailySubscriberOutValue("10");
		modifyTCPRequestPojo.setDefaultProfile("N");  
		modifyTCPRequestPojo.setDescription("TEST");  
		modifyTCPRequestPojo.setDomainCode("DIST");
		modifyTCPRequestPojo.setMonthlyInAltCount("10");
		modifyTCPRequestPojo.setMonthlyInAltValue("10");
		modifyTCPRequestPojo.setMonthlyInCount("10");
		modifyTCPRequestPojo.setMonthlyInValue("10");
		modifyTCPRequestPojo.setMonthlyOutAltCount("10");  
		modifyTCPRequestPojo.setMonthlyOutAltValue("10");  
		modifyTCPRequestPojo.setMonthlyOutCount("10");  
		modifyTCPRequestPojo.setMonthlyOutValue("10");  
		modifyTCPRequestPojo.setMonthlySubscriberInAltCount("10");  
		modifyTCPRequestPojo.setMonthlySubscriberInAltValue("10");  
		modifyTCPRequestPojo.setMonthlySubscriberInCount("10");  
		modifyTCPRequestPojo.setMonthlySubscriberInValue("10");  
		modifyTCPRequestPojo.setMonthlySubscriberOutAltCount("10");
		modifyTCPRequestPojo.setMonthlySubscriberOutAltValue("10");  
		modifyTCPRequestPojo.setMonthlySubscriberOutCount("10");  
		modifyTCPRequestPojo.setMonthlySubscriberOutValue("10");  
		modifyTCPRequestPojo.setNetworkCode("NG");
		modifyTCPRequestPojo.setProfileID("851");  
		modifyTCPRequestPojo.setProfileName("TPpro"+ BTSLUtil.getRandomNumber(4));  
		modifyTCPRequestPojo.setShortName("TPS"+BTSLUtil.getRandomNumber(4));  
		modifyTCPRequestPojo.setStatus("Y");
		modifyTCPRequestPojo.setUnctrlDailyInAltCount("10");  
		modifyTCPRequestPojo.setUnctrlDailyInAltValue("10"); 
		modifyTCPRequestPojo.setUnctrlDailyInCount("10");  
		modifyTCPRequestPojo.setUnctrlDailyInValue("10");  
		modifyTCPRequestPojo.setUnctrlDailyOutAltCount("10");  
		modifyTCPRequestPojo.setUnctrlDailyOutAltValue("10");  
		modifyTCPRequestPojo.setUnctrlDailyOutCount("10");  
		modifyTCPRequestPojo.setUnctrlDailyOutValue("10");  
		modifyTCPRequestPojo.setUnctrlDailyOutCount("10");
		modifyTCPRequestPojo.setUnctrlDailyOutValue("10");
		modifyTCPRequestPojo.setUnctrlMonthlyInAltCount("10");
		modifyTCPRequestPojo.setUnctrlMonthlyInAltValue("10");
		modifyTCPRequestPojo.setUnctrlMonthlyInCount("10");
		modifyTCPRequestPojo.setUnctrlMonthlyInValue("10");
		modifyTCPRequestPojo.setUnctrlMonthlyOutAltCount("10");  
		modifyTCPRequestPojo.setUnctrlMonthlyOutAltValue("10");  
		modifyTCPRequestPojo.setUnctrlMonthlyOutCount("10");  
		modifyTCPRequestPojo.setUnctrlMonthlyOutValue("10");  
		modifyTCPRequestPojo.setUnctrlWeeklyInAltCount("10");  
		modifyTCPRequestPojo.setUnctrlWeeklyInAltValue("10");    
		modifyTCPRequestPojo.setUnctrlWeeklyInCount("10");  
		modifyTCPRequestPojo.setUnctrlWeeklyInValue("10");  
		modifyTCPRequestPojo.setUnctrlWeeklyOutAltCount("10");  
		modifyTCPRequestPojo.setUnctrlWeeklyOutAltValue("10");  
		modifyTCPRequestPojo.setUnctrlWeeklyOutCount("10");  
		modifyTCPRequestPojo.setUnctrlWeeklyOutValue("10");  
		modifyTCPRequestPojo.setWeeklyInAltCount("10");  
		modifyTCPRequestPojo.setWeeklyInAltValue("10");  
		modifyTCPRequestPojo.setWeeklyInCount("10");  
		modifyTCPRequestPojo.setWeeklyInValue("10");  
		modifyTCPRequestPojo.setWeeklyOutAltCount("10");  
		modifyTCPRequestPojo.setWeeklyOutAltValue("10");  
		modifyTCPRequestPojo.setWeeklyOutCount("10");  
		modifyTCPRequestPojo.setWeeklyOutValue("10");    
		modifyTCPRequestPojo.setWeeklySubscriberInAltCount("10");		  
		modifyTCPRequestPojo.setWeeklySubscriberInAltValue("10");  
		modifyTCPRequestPojo.setWeeklySubscriberInCount("10");  
		modifyTCPRequestPojo.setWeeklySubscriberInValue("10");  
		modifyTCPRequestPojo.setWeeklySubscriberOutAltCount("10"); 
		modifyTCPRequestPojo.setWeeklySubscriberOutAltValue("10");  
		modifyTCPRequestPojo.setWeeklySubscriberOutCount("10");  
		modifyTCPRequestPojo.setWeeklySubscriberOutValue("10");
		modifyTCPRequestPojo.setProductBalancelist(productBalanceList);
	}

public void excecuteModifyTCPAPI(int statusCode, String username, String password) throws IOException {

	Log.info("Entering excecuteModifyTCPAPI()");
	ModifyTransferControlProfileAPI modifyTCPAPI = new ModifyTransferControlProfileAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
			getAcccessToken(username, password));
	modifyTCPAPI.addBodyParam(modifyTCPRequestPojo);
	modifyTCPAPI.logRequestBody(modifyTCPRespPojo);
	modifyTCPAPI.setExpectedStatusCode(statusCode);
	modifyTCPAPI.setContentType(_masterVO.getProperty("contentType"));
	modifyTCPAPI.perform();
	modifyTCPRespPojo = modifyTCPAPI.getAPIResponseAsPOJO(ModifyTCPRespPojo.class);
	Log.info("Exiting excecuteModifyTCPAPI()");

}


}
