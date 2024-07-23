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

import restassuredapi.api.addTransferControlProfile.AddTPcontrolAPI;
import restassuredapi.pojo.MasterError;
import restassuredapi.pojo.MasterErrorList;
import restassuredapi.pojo.addTCPRequestPojo.AddTCPSaveRequestPojo;
import restassuredapi.pojo.addTCPRequestPojo.TransferProfileProductReqVO;
import restassuredapi.pojo.addTCPRespPojo.AddTCPResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.ADD_TCP_RA)
public class AddTCPRestAssuredTest extends BaseTest {
	
	DateFormat df = new SimpleDateFormat("dd/MM/YY");
    Date dateobj = new Date();
    String currentDate=df.format(dateobj);   
	static String moduleCode;
	 public AddTCPSaveRequestPojo addTCPSaveRequestPojo = new AddTCPSaveRequestPojo();
	 public AddTCPResponsePojo addTCPResponsePojo = new AddTCPResponsePojo();
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
	public void addTransferControlProfilePositive(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addTransferControlProfilePositive";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDTRANSFCTRLPROFIE01");
		String networkCode =DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
		createAddTCPRequest(categoryCode,networkCode);
		excecuteAddTCPAPI(200, loginID, password);
		String message = addTCPResponsePojo.getMessage();
		System.out.println("Response Message ::: --> " +message);
		Assertion.assertEquals(message, "Successfully inserted transfer profile");
		Assert.assertEquals(message, "Successfully inserted transfer profile");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

	
	@Test(dataProvider = "userData", priority = 2)
	@TestManager(TestKey = "PRETUPS-0012")
	public void addTCPProfileShortnameValidation(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addagentetailsP";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDTRANSFCTRLPROFIE02");
		
		String networkCode =DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
		createAddTCPRequest(categoryCode,networkCode);
		addTCPSaveRequestPojo.setShortName( "Test Profile 2 "); // short name with space .
		excecuteAddTCPAPI(400, loginID, password);
		String message = addTCPResponsePojo.getMessage();
		 if(addTCPResponsePojo.getErrorMap()!=null && addTCPResponsePojo.getErrorMap().getMasterErrorList() !=null && addTCPResponsePojo.getErrorMap().getMasterErrorList().size()>0) {
			 for(int i=0;i<=addTCPResponsePojo.getErrorMap().getMasterErrorList().size();i++) {
				 MasterErrorList mstrErrlist =  addTCPResponsePojo.getErrorMap().getMasterErrorList().get(i);
				if(mstrErrlist.getErrorMsg().equalsIgnoreCase("Short name should be a single word")) {
					message=mstrErrlist.getErrorMsg();
					break;
				}
				  
				
			 }
		 }
		 
		System.out.println("Response Message ::: --> " +message);
		Assertion.assertEquals(message, "Short name should be a single word");
		Assert.assertEquals(message, "Short name should be a single word");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	
	
	
	@Test(dataProvider = "userData", priority = 3)
	@TestManager(TestKey = "PRETUPS-0012")
	public void addTCPProductMinBalGrtMaxBal(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addagentetailsP";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDTRANSFCTRLPROFIE03");
		
		String networkCode =DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
		createAddTCPRequest(categoryCode,networkCode);
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
		transferProfileProductReqVO.setMinBalance("14");  
		transferProfileProductReqVO.setMinResidualBalanceAsLong(0);
		transferProfileProductReqVO.setProductCode("ETOPUP");
		transferProfileProductReqVO.setProductName("Etopup");  
		transferProfileProductReqVO.setProductCode("101");  
		
		List productList = new ArrayList<TransferProfileProductReqVO>();
		productList.add(transferProfileProductReqVO);
		addTCPSaveRequestPojo.setShortName( "Test Profile 2 "); // short name with space .
		addTCPSaveRequestPojo.getProductBalancelist().clear();
		addTCPSaveRequestPojo.setProductBalancelist(productList);
		excecuteAddTCPAPI(400, loginID, password);
		
		String productName[] = { transferProfileProductReqVO.getProductName() };
		String fomattedMsg = MessageFormat.format("Minimum balance cannot be greater than maximum balance for product {0}",productName);
		String message = addTCPResponsePojo.getMessage();
				 if(addTCPResponsePojo.getErrorMap()!=null && addTCPResponsePojo.getErrorMap().getMasterErrorList() !=null && addTCPResponsePojo.getErrorMap().getMasterErrorList().size()>0) {
			 for(int i=0;i<=addTCPResponsePojo.getErrorMap().getMasterErrorList().size();i++) {
				 MasterErrorList mstrErrlist =  addTCPResponsePojo.getErrorMap().getMasterErrorList().get(i);
				if(mstrErrlist.getErrorMsg().equalsIgnoreCase(fomattedMsg)) {
					message=mstrErrlist.getErrorMsg();
					break;
				}
				  
				
			 }
		 }

		
		
		
		
		Assertion.assertEquals(message, fomattedMsg);
		Assert.assertEquals(message, fomattedMsg);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	} 
	
	
	@Test(dataProvider = "userData", priority = 4)
	@TestManager(TestKey = "PRETUPS-0012")
	public void addTCPC2SminTransAmtGrtrThanc2sMaxTransAmount(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addagentetailsP";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDTRANSFCTRLPROFIE04");
		
		String networkCode =DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
		createAddTCPRequest(categoryCode,networkCode);
		TransferProfileProductReqVO transferProfileProductReqVO = new TransferProfileProductReqVO();
		transferProfileProductReqVO.setAllowedMaxPercentage("10");
		transferProfileProductReqVO.setAllowedMaxPercentageInt(0);
		transferProfileProductReqVO.setAltBalance("5");
		transferProfileProductReqVO.setAltBalanceLong(0);
		transferProfileProductReqVO.setC2sMaxTxnAmt("10");
		transferProfileProductReqVO.setC2sMaxTxnAmtAsLong(0);  
		transferProfileProductReqVO.setC2sMinTxnAmt("12");  
		transferProfileProductReqVO.setC2sMinTxnAmtAsLong(0);
		transferProfileProductReqVO.setCurrentBalance("10");
		transferProfileProductReqVO.setMaxBalance("10");  
		transferProfileProductReqVO.setMaxBalanceAsLong(0);  
		transferProfileProductReqVO.setMinBalance("14");  
		transferProfileProductReqVO.setMinResidualBalanceAsLong(0);
		transferProfileProductReqVO.setProductCode("ETOPUP");
		transferProfileProductReqVO.setProductName("Etopup");  
		transferProfileProductReqVO.setProductCode("101");  
	 	List productBalanceList = new ArrayList();
	 	productBalanceList.add(transferProfileProductReqVO); 
	 	addTCPSaveRequestPojo.setProductBalancelist(productBalanceList);

		
		excecuteAddTCPAPI(400, loginID, password);
		TransferProfileProductReqVO transferProfileProductReqVO1=  addTCPSaveRequestPojo.getProductBalancelist().get(0);
		String productName[] = { transferProfileProductReqVO1.getProductName() };
		String fomattedMsg = MessageFormat.format("Per C2S minimum transaction amount cannot be greater than maximum transaction amount for product {0}",productName);
		String message = addTCPResponsePojo.getMessage();
		 if(addTCPResponsePojo.getErrorMap()!=null && addTCPResponsePojo.getErrorMap().getMasterErrorList() !=null && addTCPResponsePojo.getErrorMap().getMasterErrorList().size()>0) {
	 for(int i=0;i<=addTCPResponsePojo.getErrorMap().getMasterErrorList().size();i++) {
		 MasterErrorList mstrErrlist =  addTCPResponsePojo.getErrorMap().getMasterErrorList().get(i);
		if(mstrErrlist.getErrorMsg().equalsIgnoreCase(fomattedMsg)) {
			message=mstrErrlist.getErrorMsg();
			break;
		}
	 	}
		 }

		Assertion.assertEquals(message, fomattedMsg);
		Assert.assertEquals(message, fomattedMsg);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	

	


	@Test(dataProvider = "userData", priority = 5)
	@TestManager(TestKey = "PRETUPS-0012")
	public void alertbalanceLessthanMinBal(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "addagentetailsP";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDTRANSFCTRLPROFIE05");
		
		String networkCode =DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
		createAddTCPRequest(categoryCode,networkCode);

		TransferProfileProductReqVO transferProfileProductReqVO = new TransferProfileProductReqVO();	
	
	transferProfileProductReqVO.setAllowedMaxPercentage("10");
	transferProfileProductReqVO.setAllowedMaxPercentageInt(0);
	transferProfileProductReqVO.setAltBalance("2");
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
 	addTCPSaveRequestPojo.setProductBalancelist(productBalanceList);
	excecuteAddTCPAPI(400, loginID, password);
	TransferProfileProductReqVO transferProfileProductReqVO1=  addTCPSaveRequestPojo.getProductBalancelist().get(0);
	String productName[] = { transferProfileProductReqVO1.getProductName() };
	String fomattedMsg = "Alerting balance should be greater than or equal to minimum residual balance";
	String message = addTCPResponsePojo.getMessage();
	 if(addTCPResponsePojo.getErrorMap()!=null && addTCPResponsePojo.getErrorMap().getMasterErrorList() !=null && addTCPResponsePojo.getErrorMap().getMasterErrorList().size()>0) {
 for(int i=0;i<=addTCPResponsePojo.getErrorMap().getMasterErrorList().size();i++) {
	 MasterErrorList mstrErrlist =  addTCPResponsePojo.getErrorMap().getMasterErrorList().get(i);
	if(mstrErrlist.getErrorMsg().equalsIgnoreCase(fomattedMsg)) {
		message=mstrErrlist.getErrorMsg();
		break;
	}
 	}
	 }

	Assertion.assertEquals(message, fomattedMsg);
	Assert.assertEquals(message, fomattedMsg);
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
}

	
	
	
	
//	Allowed maximum percentage should be in between 1 to 100

	@Test(dataProvider = "userData", priority = 6)
	@TestManager(TestKey = "PRETUPS-0012")
	public void allowemaxpercentage(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "allowemaxpercentage";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDTRANSFCTRLPROFIE06");
		
		String networkCode =DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
		createAddTCPRequest(categoryCode,networkCode);

		TransferProfileProductReqVO transferProfileProductReqVO = new TransferProfileProductReqVO();	
	
	transferProfileProductReqVO.setAllowedMaxPercentage("1000");
	transferProfileProductReqVO.setAllowedMaxPercentageInt(0);
	transferProfileProductReqVO.setAltBalance("7");
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
 	addTCPSaveRequestPojo.setProductBalancelist(productBalanceList);
	excecuteAddTCPAPI(400, loginID, password);
	TransferProfileProductReqVO transferProfileProductReqVO1=  addTCPSaveRequestPojo.getProductBalancelist().get(0);
	String productName[] = { transferProfileProductReqVO1.getProductName() };
	String fomattedMsg = "Allowed maximum percentage should be in between 1 to 100";
	String message = addTCPResponsePojo.getMessage();
	 if(addTCPResponsePojo.getErrorMap()!=null && addTCPResponsePojo.getErrorMap().getMasterErrorList() !=null && addTCPResponsePojo.getErrorMap().getMasterErrorList().size()>0) {
 for(int i=0;i<=addTCPResponsePojo.getErrorMap().getMasterErrorList().size();i++) {
	 MasterErrorList mstrErrlist =  addTCPResponsePojo.getErrorMap().getMasterErrorList().get(i);
	if(mstrErrlist.getErrorMsg().equalsIgnoreCase(fomattedMsg)) {
		message=mstrErrlist.getErrorMsg();
		break;
	}
 	}
	 }

	Assertion.assertEquals(message, fomattedMsg);
	Assert.assertEquals(message, fomattedMsg);
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
}
	
	
//Daily in count cannot be greater than weekly in count
	
	
	@Test(dataProvider = "userData", priority = 7)
	@TestManager(TestKey = "PRETUPS-0012")
	public void checkDailyinCountGrtWeeklyincount(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "checkDailyinCountGrtWeeklyincount";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDTRANSFCTRLPROFIE07");
		
		String networkCode =DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
		createAddTCPRequest(categoryCode,networkCode);
		addTCPSaveRequestPojo.setDailyInCount("100");
		addTCPSaveRequestPojo.setWeeklyInCount("90");
	excecuteAddTCPAPI(400, loginID, password);
	TransferProfileProductReqVO transferProfileProductReqVO1=  addTCPSaveRequestPojo.getProductBalancelist().get(0);
	String productName[] = { transferProfileProductReqVO1.getProductName() };
	String fomattedMsg = "Daily in count cannot be greater than weekly in count";
	String message = addTCPResponsePojo.getMessage();
	 if(addTCPResponsePojo.getErrorMap()!=null && addTCPResponsePojo.getErrorMap().getMasterErrorList() !=null && addTCPResponsePojo.getErrorMap().getMasterErrorList().size()>0) {
 for(int i=0;i<=addTCPResponsePojo.getErrorMap().getMasterErrorList().size();i++) {
	 MasterErrorList mstrErrlist =  addTCPResponsePojo.getErrorMap().getMasterErrorList().get(i);
	if(mstrErrlist.getErrorMsg().equalsIgnoreCase(fomattedMsg)) {
		message=mstrErrlist.getErrorMsg();
		break;
	}
 	}
	 }

	Assertion.assertEquals(message, fomattedMsg);
	Assert.assertEquals(message, fomattedMsg);
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
}

	
//Daily in count cannot be greater than monthly in count
	@Test(dataProvider = "userData", priority = 8)
	@TestManager(TestKey = "PRETUPS-0012")
	public void checkDailyinCountGrtMonthlyincount(String loginID, String password, String pin, String parentName, String categoryName,
			String categoryCode) throws Exception {
		final String methodName = "checkDailyinCountGrtWeeklyincount";
		setTestInitialDetails(methodName, loginID, password, categoryName, "ADDTRANSFCTRLPROFIE08");
		
		String networkCode =DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
		createAddTCPRequest(categoryCode,networkCode);
		addTCPSaveRequestPojo.setDailyInCount("100");
		addTCPSaveRequestPojo.setMonthlyInCount("90");
	excecuteAddTCPAPI(400, loginID, password);
	TransferProfileProductReqVO transferProfileProductReqVO1=  addTCPSaveRequestPojo.getProductBalancelist().get(0);
	String productName[] = { transferProfileProductReqVO1.getProductName() };
	String fomattedMsg = "Daily in count cannot be greater than monthly in count";
	String message = addTCPResponsePojo.getMessage();
	 if(addTCPResponsePojo.getErrorMap()!=null && addTCPResponsePojo.getErrorMap().getMasterErrorList() !=null && addTCPResponsePojo.getErrorMap().getMasterErrorList().size()>0) {
 for(int i=0;i<=addTCPResponsePojo.getErrorMap().getMasterErrorList().size();i++) {
	 MasterErrorList mstrErrlist =  addTCPResponsePojo.getErrorMap().getMasterErrorList().get(i);
	if(mstrErrlist.getErrorMsg().equalsIgnoreCase(fomattedMsg)) {
		message=mstrErrlist.getErrorMsg();
		break;
	}
 	}
	 }

	Assertion.assertEquals(message, fomattedMsg);
	Assert.assertEquals(message, fomattedMsg);
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
}


//	Weekly in count cannot be greater than monthly in count
		@Test(dataProvider = "userData", priority = 8)
		@TestManager(TestKey = "PRETUPS-0012")
		public void checkWeeklyCountGrtMonthlyincount(String loginID, String password, String pin, String parentName, String categoryName,
				String categoryCode) throws Exception {
			final String methodName = "checkDailyinCountGrtWeeklyincount";
			setTestInitialDetails(methodName, loginID, password, categoryName, "ADDTRANSFCTRLPROFIE06");
			
			String networkCode =DBHandler.AccessHandler.getUserNetworkByLoginID(loginID);
			createAddTCPRequest(categoryCode,networkCode);
			addTCPSaveRequestPojo.setWeeklyInCount("15");
			addTCPSaveRequestPojo.setMonthlyInCount("10");
		excecuteAddTCPAPI(400, loginID, password);
		TransferProfileProductReqVO transferProfileProductReqVO1=  addTCPSaveRequestPojo.getProductBalancelist().get(0);
		String productName[] = { transferProfileProductReqVO1.getProductName() };
		String fomattedMsg = "Weekly in count cannot be greater than monthly in count";
		String message = addTCPResponsePojo.getMessage();
		 if(addTCPResponsePojo.getErrorMap()!=null && addTCPResponsePojo.getErrorMap().getMasterErrorList() !=null && addTCPResponsePojo.getErrorMap().getMasterErrorList().size()>0) {
	 for(int i=0;i<=addTCPResponsePojo.getErrorMap().getMasterErrorList().size();i++) {
		 MasterErrorList mstrErrlist =  addTCPResponsePojo.getErrorMap().getMasterErrorList().get(i);
		if(mstrErrlist.getErrorMsg().equalsIgnoreCase(fomattedMsg)) {
			message=mstrErrlist.getErrorMsg();
			break;
		}
	 	}
		 }

		Assertion.assertEquals(message, fomattedMsg);
		Assert.assertEquals(message, fomattedMsg);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	

	
	
	public AddTCPSaveRequestPojo createAddTCPRequest(String categoryCode,String networkCode) {
		
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
	 	addTCPSaveRequestPojo.setCategoryCode(categoryCode);
		addTCPSaveRequestPojo.setDailyInAltCount("10");
		addTCPSaveRequestPojo.setDailyInAltValue("10");
		addTCPSaveRequestPojo.setDailyInCount("10");
		addTCPSaveRequestPojo.setDailyInValue("10");
		addTCPSaveRequestPojo.setDailyOutAltCount("10");
		addTCPSaveRequestPojo.setDailyOutAltValue("10");  
		addTCPSaveRequestPojo.setDailyOutCount("10");
		addTCPSaveRequestPojo.setDailyOutValue("10");
		addTCPSaveRequestPojo.setDailySubscriberInAltCount("10");  
		addTCPSaveRequestPojo.setDailySubscriberInAltValue("10");  
		addTCPSaveRequestPojo.setDailySubscriberInCount("10");
		addTCPSaveRequestPojo.setDailySubscriberInValue("10");
		addTCPSaveRequestPojo.setDailySubscriberOutAltCount("10"); 
		addTCPSaveRequestPojo.setDailySubscriberOutAltValue("10");  
		addTCPSaveRequestPojo.setDailySubscriberOutCount("10");
		addTCPSaveRequestPojo.setDailySubscriberOutValue("10");
		addTCPSaveRequestPojo.setDefaultProfile("N");  
		addTCPSaveRequestPojo.setDescription("TEST");  
		addTCPSaveRequestPojo.setDomainCode("DIST");
		addTCPSaveRequestPojo.setMonthlyInAltCount("10");
		addTCPSaveRequestPojo.setMonthlyInAltValue("10");
		addTCPSaveRequestPojo.setMonthlyInCount("10");
		addTCPSaveRequestPojo.setMonthlyInValue("10");
		addTCPSaveRequestPojo.setMonthlyOutAltCount("10");  
		addTCPSaveRequestPojo.setMonthlyOutAltValue("10");  
		addTCPSaveRequestPojo.setMonthlyOutCount("10");  
		addTCPSaveRequestPojo.setMonthlyOutValue("10");  
		addTCPSaveRequestPojo.setMonthlySubscriberInAltCount("10");  
		addTCPSaveRequestPojo.setMonthlySubscriberInAltValue("10");  
		addTCPSaveRequestPojo.setMonthlySubscriberInCount("10");  
		addTCPSaveRequestPojo.setMonthlySubscriberInValue("10");  
		addTCPSaveRequestPojo.setMonthlySubscriberOutAltCount("10");
		addTCPSaveRequestPojo.setMonthlySubscriberOutAltValue("10");  
		addTCPSaveRequestPojo.setMonthlySubscriberOutCount("10");  
		addTCPSaveRequestPojo.setMonthlySubscriberOutValue("10");  
		addTCPSaveRequestPojo.setNetworkCode("NG");
		addTCPSaveRequestPojo.setProfileID("851");  
		addTCPSaveRequestPojo.setProfileName("TPpro"+ BTSLUtil.getRandomNumber(4));  
		addTCPSaveRequestPojo.setShortName("TPS"+BTSLUtil.getRandomNumber(4));  
		addTCPSaveRequestPojo.setStatus("Y");
		addTCPSaveRequestPojo.setUnctrlDailyInAltCount("10");  
		addTCPSaveRequestPojo.setUnctrlDailyInAltValue("10"); 
		addTCPSaveRequestPojo.setUnctrlDailyInCount("10");  
		addTCPSaveRequestPojo.setUnctrlDailyInValue("10");  
		addTCPSaveRequestPojo.setUnctrlDailyOutAltCount("10");  
		addTCPSaveRequestPojo.setUnctrlDailyOutAltValue("10");  
		addTCPSaveRequestPojo.setUnctrlDailyOutCount("10");  
		addTCPSaveRequestPojo.setUnctrlDailyOutValue("10");  
		addTCPSaveRequestPojo.setUnctrlDailyOutCount("10");
		addTCPSaveRequestPojo.setUnctrlDailyOutValue("10");
		addTCPSaveRequestPojo.setUnctrlMonthlyInAltCount("10");
		addTCPSaveRequestPojo.setUnctrlMonthlyInAltValue("10");
		addTCPSaveRequestPojo.setUnctrlMonthlyInCount("10");
		addTCPSaveRequestPojo.setUnctrlMonthlyInValue("10");
		addTCPSaveRequestPojo.setUnctrlMonthlyOutAltCount("10");  
		addTCPSaveRequestPojo.setUnctrlMonthlyOutAltValue("10");  
		addTCPSaveRequestPojo.setUnctrlMonthlyOutCount("10");  
		addTCPSaveRequestPojo.setUnctrlMonthlyOutValue("10");  
		addTCPSaveRequestPojo.setUnctrlWeeklyInAltCount("10");  
		addTCPSaveRequestPojo.setUnctrlWeeklyInAltValue("10");    
		addTCPSaveRequestPojo.setUnctrlWeeklyInCount("10");  
		addTCPSaveRequestPojo.setUnctrlWeeklyInValue("10");  
		addTCPSaveRequestPojo.setUnctrlWeeklyOutAltCount("10");  
		addTCPSaveRequestPojo.setUnctrlWeeklyOutAltValue("10");  
		addTCPSaveRequestPojo.setUnctrlWeeklyOutCount("10");  
		addTCPSaveRequestPojo.setUnctrlWeeklyOutValue("10");  
		addTCPSaveRequestPojo.setWeeklyInAltCount("10");  
		addTCPSaveRequestPojo.setWeeklyInAltValue("10");  
		addTCPSaveRequestPojo.setWeeklyInCount("10");  
		addTCPSaveRequestPojo.setWeeklyInValue("10");  
		addTCPSaveRequestPojo.setWeeklyOutAltCount("10");  
		addTCPSaveRequestPojo.setWeeklyOutAltValue("10");  
		addTCPSaveRequestPojo.setWeeklyOutCount("10");  
		addTCPSaveRequestPojo.setWeeklyOutValue("10");    
		addTCPSaveRequestPojo.setWeeklySubscriberInAltCount("10");		  
		addTCPSaveRequestPojo.setWeeklySubscriberInAltValue("10");  
		addTCPSaveRequestPojo.setWeeklySubscriberInCount("10");  
		addTCPSaveRequestPojo.setWeeklySubscriberInValue("10");  
		addTCPSaveRequestPojo.setWeeklySubscriberOutAltCount("10"); 
		addTCPSaveRequestPojo.setWeeklySubscriberOutAltValue("10");  
		addTCPSaveRequestPojo.setWeeklySubscriberOutCount("10");  
		addTCPSaveRequestPojo.setWeeklySubscriberOutValue("10");
		addTCPSaveRequestPojo.setProductBalancelist(productBalanceList);
		return addTCPSaveRequestPojo;
	}
	
	
	public void excecuteAddTCPAPI(int statusCode, String username, String password) throws IOException {

		Log.info("Entering excecuteAddTCPAPI()");
		AddTPcontrolAPI addagentApi = new AddTPcontrolAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				getAcccessToken(username, password));
		addagentApi.addBodyParam(addTCPSaveRequestPojo);
		addagentApi.logRequestBody(addTCPSaveRequestPojo);
		addagentApi.setExpectedStatusCode(statusCode);
		addagentApi.setContentType(_masterVO.getProperty("contentType"));
		addagentApi.perform();
		addTCPResponsePojo = addagentApi.getAPIResponseAsPOJO(AddTCPResponsePojo.class);
		Log.info("Exiting executeAddAgentAPI()" +  addTCPResponsePojo.getMessage()+ " -> " +  addTCPResponsePojo.getMessage() );

	}

	

}
