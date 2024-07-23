package com.testscripts.sit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.AutoO2CTransfer;
import com.Features.TransferControlProfile;
import com.Features.mapclasses.AutoO2CMap;
import com.apicontrollers.extgw.c2ctransfer.EXTGWC2CAPI;
import com.apicontrollers.extgw.c2sTransfer.customerRecharge.EXTGWC2SAPI;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.GatewayI;
import com.commons.PretupsI;
import com.commons.ServicesControllerI;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils.Validator;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils._parser;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class SIT_AutoO2C_IDEA extends BaseTest {

	static boolean testCaseCounter = false;
	static String masterSheetPath;
	static String autoO2CAllowed;
	static String controlPreference;
	static String systemPreferenceType;
	static String networkPreference;
	static String networkCode;
	static Object[][] data;
	String productCode = null;
	String userId;
	HashMap<String, String> transferMap;
	String type;
	String processID;
	String executedDate = null;
	String currentDate = null;
	HashMap<String, String> tcpDetails;
	String profileID = null;
	int alertingBalance;
	String assignCategory="SIT";
	
	@Test
	public void A_loadPreRequisites() {
		String O2CTransferCode = _masterVO.getProperty("O2CTransferCode");
		AutoO2CMap autoO2CMap = new AutoO2CMap();
		transferMap = autoO2CMap.getAutoOperatorToChannelMap(O2CTransferCode);
		String userId = DBHandler.AccessHandler.getUserId(transferMap.get("ChannelUser"));
		String preferenceCode = "AUTO_O2C_TRANSFER_ALLOWED";
		networkCode = _masterVO.getMasterValue("Network Code");
		autoO2CAllowed = DBHandler.AccessHandler.getPreference(transferMap.get("CONTROL_CODE"), networkCode,
				preferenceCode);
		controlPreference = DBHandler.AccessHandler.getValuefromControlPreference(preferenceCode);
		systemPreferenceType = DBHandler.AccessHandler.getTypefromSystemPreference(preferenceCode);
		networkPreference = DBHandler.AccessHandler.getValuefromNetworkPreference(preferenceCode);
		type = "OPT";

		data = DBHandler.AccessHandler.getProductDetails(networkCode,
				autoO2CMap.getAutoO2CMap("DOMAIN_CODE", O2CTransferCode),
				autoO2CMap.getAutoO2CMap("FROM_CATEGORY_CODE", O2CTransferCode),
				autoO2CMap.getAutoO2CMap("TO_CATEGORY_CODE", O2CTransferCode), type);
		processID = "AUTOO2CPROCESS";
	}

	
  @Test public void CASEA_MSISDNNull() {
	  
	  Log.startTestCase(this.getClass().getName());
	  
	  if (testCaseCounter == false) { 
		  test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode()); 
		  testCaseCounter = true; }
	  
	  AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver); AutoO2CMap
	  _mapgenerator = new AutoO2CMap(); currentNode = test.createNode(
	  _masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getExtentCase()
	  ); currentNode.assignCategory(assignCategory); if
	  (autoO2CAllowed.equalsIgnoreCase("true")) { HashMap<String, String>
	  transferMap = _mapgenerator
	  .getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
	  transferMap.put("MSISDN", ""); try { transferMap =
	  autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
	  } catch (Exception e) { String actualMessage =
	  autoO2CTransfer.getErrorMessage(); String expectedMessage = MessagesDAO
	  .prepareMessageByKey("autoo2c.associatesubscriberdetails.msg.selectUser")
	  ; Validator.messageCompare(actualMessage, expectedMessage); } } else {
	  Log.skip("Auto O2C Module not available in system."); } }
	  
	  @Test public void CASEB_maxTxnAmountNull() {
	  
	  Log.startTestCase(this.getClass().getName());
	  
	  if (testCaseCounter == false) { test = extent.createTest(
	  "["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode()); testCaseCounter = true; }
	  
	  AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver); AutoO2CMap
	  _mapgenerator = new AutoO2CMap(); currentNode = test.createNode(
			  _masterVO.getCaseMasterByID("SITAUTOO2CIDEA2").getExtentCase()
	  ); currentNode.assignCategory(assignCategory); if
	  (autoO2CAllowed.equalsIgnoreCase("true")) { HashMap<String, String>
	  transferMap = _mapgenerator
	  .getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
	  transferMap.put("maxTxnAmount", ""); try { transferMap =
	  autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
	  } catch (Exception e) { String actualMessage =
	  autoO2CTransfer.getErrorMessage(); String expectedMessage =
	  MessagesDAO.prepareMessageByKey(
	  "autoo2c.associatesubscriberdetails.msg.maxtrxamtnull");
	  Validator.messageCompare(actualMessage, expectedMessage); } } else {
	  Log.skip("Auto O2C Module not available in system."); } }
	  
	  @Test public void CASEC() throws InterruptedException {
	  
	  Log.startTestCase(this.getClass().getName());
	  
	  if (testCaseCounter == false) { test = extent.createTest(
	  "["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode()); testCaseCounter = true; } String valueToSet =
	  "true"; AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
	  AutoO2CMap _mapgenerator = new AutoO2CMap(); currentNode =
	  test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA3").getExtentCase());
	  currentNode.assignCategory(assignCategory); HashMap<String, String> transferMap =
	  _mapgenerator
	  .getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
	  if (!controlPreference.equalsIgnoreCase("true"))
	  autoO2CTransfer.changePreference(controlPreference, networkPreference,
	  systemPreferenceType, transferMap.get("controlName"), valueToSet); else
	  Log.skip(
	  "Control Preference for Auto O2C is already true.No need to chaange.");
	  
	  }
	 

	@Test
	public void CASED() throws InterruptedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}
		
		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator
				.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
		String loginID = transferMap.get("Login_ID");
		String channelUser = transferMap.get("ChannelUser");
		String b = DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID);
		int balance = Integer.parseInt(b);
		profileID = transferMap.get("PROFILE_ID");
		tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, AutoO2CMap.c2cMap.get("PRODUCT"),
				"ALERTING_BALANCE");
		alertingBalance = Integer.parseInt(tcpDetails.get("ALERTING_BALANCE"));
		int diff = balance - alertingBalance;
		int newAlert = balance - 100;
		String newAlertingBalance = Integer.toString(balance-100);
		if (diff > 0) {
			String[][] valuesToModfiy = new String[][]{ {"enterETopUpAlertingBalance", newAlertingBalance}};
			tcpchange.modifyAnyTCPValue(valuesToModfiy, AutoO2CMap.userData,"channel");
			if (transferMap.get("c2cAllowed") != null && transferMap.get("c2cAllowed").equalsIgnoreCase("true")) {
				String API = C2CTransferAPI.prepareAPI(AutoO2CMap.c2cMap);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get("COMMAND.TXNSTATUS").toString(), _APIUtil.API_SUCCESS);
			    int newBalance = Integer
					.parseInt(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
			if (newBalance > newAlert)
				currentNode.log(Status.PASS, "Balance Validation Successful");
			else
				currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
		} else {
			int newBalance = Integer
					.parseInt(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
			if (newBalance > balance)
				currentNode.log(Status.PASS, "Balance Validation Successful");
			else
				currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
			

	}

	@Test
	public void CASEE() throws InterruptedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator
				.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);

		String loginID = transferMap.get("Login_ID");
		String channelUser = transferMap.get("ChannelUser");
		long preBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
		profileID = transferMap.get("PROFILE_ID");
		tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, AutoO2CMap.c2cMap.get("PRODUCT"),
				"ALERTING_BALANCE");
		alertingBalance = Integer.parseInt(tcpDetails.get("ALERTING_BALANCE"));
		long diff = preBalance - _parser.getSystemAmount(alertingBalance);
		String newAlertingBalance = _parser.getDisplayAmount(preBalance - 100);
		if (diff > 0) {
			String[][] valuesToModfiy = new String[][]{ {"enterETopUpAlertingBalance", newAlertingBalance}};
			tcpchange.modifyAnyTCPValue(valuesToModfiy, AutoO2CMap.userData,"channel");
			if (transferMap.get("c2sAllowed") != null && transferMap.get("c2sAllowed").equalsIgnoreCase("true")) {
				String API = C2STransferAPI.prepareAPI(AutoO2CMap.c2sMap);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get("COMMAND.TXNSTATUS").toString(), _APIUtil.API_SUCCESS);
				long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
				if (newBalance > _parser.getSystemAmount(newAlertingBalance))
					currentNode.log(Status.PASS, "Balance Validation Successful");
				else
					currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
		} else {
			long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
			if (newBalance > preBalance)
				currentNode.log(Status.PASS, "Balance Validation Successful");
			else
				currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
	}

	@Test
	public void CASEF() throws InterruptedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator
				.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, "false");

		String loginID = transferMap.get("Login_ID");
		String channelUser = transferMap.get("ChannelUser");
		long preBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
		profileID = transferMap.get("PROFILE_ID");
		tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, AutoO2CMap.c2cMap.get("PRODUCT"),
				"ALERTING_BALANCE");
		alertingBalance = Integer.parseInt(tcpDetails.get("ALERTING_BALANCE"));
		long diff = preBalance - _parser.getSystemAmount(alertingBalance);
		String newAlertingBalance = _parser.getDisplayAmount(preBalance - 100);
		if (diff > 0) {
			String[][] valuesToModfiy = new String[][]{ {"enterETopUpAlertingBalance", newAlertingBalance}};
			tcpchange.modifyAnyTCPValue(valuesToModfiy, AutoO2CMap.userData,"channel");
			if (transferMap.get("c2cAllowed") != null && transferMap.get("c2cAllowed").equalsIgnoreCase("true")) {
				String API = C2CTransferAPI.prepareAPI(AutoO2CMap.c2cMap);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get("COMMAND.TXNSTATUS").toString(), _APIUtil.API_SUCCESS);
				long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
				long expectedBalance = preBalance - _parser.getSystemAmount(AutoO2CMap.c2cMap.get("QTY"));
				if (newBalance == expectedBalance)
					currentNode.log(Status.PASS, "Balance Validation Successful");
				else
					currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
		} 
		else {
			long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
			if (newBalance == preBalance)
				currentNode.log(Status.PASS, "Balance Validation Successful");
			else
				currentNode.log(Status.FAIL, "Balance Validation Failed");
			}

	}

	@Test
	public void CASEG() throws InterruptedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator
				.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, "false");

		String loginID = transferMap.get("Login_ID");
		String channelUser = transferMap.get("ChannelUser");
		long preBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2sMap.get("PRODUCT"), loginID));
		profileID = transferMap.get("PROFILE_ID");
		tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, AutoO2CMap.c2cMap.get("PRODUCT"),
				"ALERTING_BALANCE");
		alertingBalance = Integer.parseInt(tcpDetails.get("ALERTING_BALANCE"));
		long diff = preBalance - alertingBalance;
		String newAlertingBalance = Long.toString(preBalance-100);
		if (diff > 0) {
			String[][] valuesToModfiy = new String[][]{ {"enterETopUpAlertingBalance", newAlertingBalance}};
			tcpchange.modifyAnyTCPValue(valuesToModfiy, AutoO2CMap.userData,"channel");
			if (transferMap.get("c2sAllowed") != null && transferMap.get("c2sAllowed").equalsIgnoreCase("true")) {
				String API = C2STransferAPI.prepareAPI(AutoO2CMap.c2sMap);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get("COMMAND.TXNSTATUS").toString(), _APIUtil.API_SUCCESS);
				long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
				long expectedBalance = preBalance - _parser.getSystemAmount(AutoO2CMap.c2cMap.get("QTY"));
				if (newBalance == expectedBalance)
					currentNode.log(Status.PASS, "Balance Validation Successful");
				else
					currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
		} 
		else {
			long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
			if (newBalance == preBalance)
				currentNode.log(Status.PASS, "Balance Validation Successful");
			else
				currentNode.log(Status.FAIL, "Balance Validation Failed");
			}

	}

	@Test
	public void CASEH() throws InterruptedException {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}
		String valueToSet = "false";
		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator
				.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		if (!controlPreference.equalsIgnoreCase("false"))
			autoO2CTransfer.changePreference(controlPreference, networkPreference, systemPreferenceType,
					transferMap.get("controlName"), valueToSet);
		else
			Log.skip("Control Preference for Auto O2C is already false.No need to chaange.");
	}

	@Test
	public void CASEI() throws InterruptedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator
				.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, "true");

		String loginID = transferMap.get("Login_ID");
		String channelUser = transferMap.get("ChannelUser");
		long preBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
		profileID = transferMap.get("PROFILE_ID");
		tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, AutoO2CMap.c2cMap.get("PRODUCT"),
				"ALERTING_BALANCE");
		alertingBalance = Integer.parseInt(tcpDetails.get("ALERTING_BALANCE"));
		long diff = preBalance - _parser.getSystemAmount(alertingBalance);
		String newAlertingBalance = Long.toString(preBalance-100);
		if (diff > 0) {
			String[][] valuesToModfiy = new String[][]{ {"enterETopUpAlertingBalance", newAlertingBalance}};
			tcpchange.modifyAnyTCPValue(valuesToModfiy, AutoO2CMap.userData,"channel");
			if (transferMap.get("c2cAllowed") != null && transferMap.get("c2cAllowed").equalsIgnoreCase("true")) {
				String API = C2CTransferAPI.prepareAPI(AutoO2CMap.c2cMap);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get("COMMAND.TXNSTATUS").toString(), _APIUtil.API_SUCCESS);
				int newBalance = Integer
						.parseInt(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
				long expectedBalance = preBalance - _parser.getSystemAmount(AutoO2CMap.c2cMap.get("QTY"));
				if (newBalance == expectedBalance)
					currentNode.log(Status.PASS, "Balance Validation Successful");
				else
					currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
		} 
		else {
			long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
			if (newBalance == preBalance)
				currentNode.log(Status.PASS, "Balance Validation Successful");
			else
				currentNode.log(Status.FAIL, "Balance Validation Failed");
			}


	}

	@Test
	public void CASEJ() throws InterruptedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator
				.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, "true");

		String loginID = transferMap.get("Login_ID");
		String channelUser = transferMap.get("ChannelUser");
		long preBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
		profileID = transferMap.get("PROFILE_ID");
		tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, AutoO2CMap.c2cMap.get("PRODUCT"),
				"ALERTING_BALANCE");
		alertingBalance = Integer.parseInt(tcpDetails.get("ALERTING_BALANCE"));
		long diff = preBalance - _parser.getSystemAmount(alertingBalance);
		String newAlertingBalance = Long.toString(preBalance-100);
		if (diff > 0) {
			String[][] valuesToModfiy = new String[][]{ {"enterETopUpAlertingBalance", newAlertingBalance}};
			tcpchange.modifyAnyTCPValue(valuesToModfiy, AutoO2CMap.userData,"channel");
			if (transferMap.get("c2sAllowed") != null && transferMap.get("c2sAllowed").equalsIgnoreCase("true")) {
				String API = C2STransferAPI.prepareAPI(AutoO2CMap.c2sMap);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get("COMMAND.TXNSTATUS").toString(), _APIUtil.API_SUCCESS);
				long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
				long expectedBalance = preBalance - _parser.getSystemAmount(AutoO2CMap.c2cMap.get("QTY"));
				if (newBalance == expectedBalance)
					currentNode.log(Status.PASS, "Balance Validation Successful");
				else
					currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
		} 
		else {
			long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
			if (newBalance == preBalance)
				currentNode.log(Status.PASS, "Balance Validation Successful");
			else
				currentNode.log(Status.FAIL, "Balance Validation Failed");
			}

	}

	@Test
	public void CASEK() throws InterruptedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		EXTGWC2CAPI C2CTransferAPI = new EXTGWC2CAPI();
		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA11").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator
				.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, "false");

		String loginID = transferMap.get("Login_ID");
		String channelUser = transferMap.get("ChannelUser");
		long preBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
		profileID = transferMap.get("PROFILE_ID");
		tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, AutoO2CMap.c2cMap.get("PRODUCT"),
				"ALERTING_BALANCE");
		alertingBalance = Integer.parseInt(tcpDetails.get("ALERTING_BALANCE"));
		long diff = preBalance - _parser.getSystemAmount(alertingBalance);
		String newAlertingBalance = Long.toString(preBalance-100);
		if (diff > 0) {
			String[][] valuesToModfiy = new String[][]{ {"enterETopUpAlertingBalance", newAlertingBalance}};
			tcpchange.modifyAnyTCPValue(valuesToModfiy, AutoO2CMap.userData,"channel");
			if (transferMap.get("c2cAllowed") != null && transferMap.get("c2cAllowed").equalsIgnoreCase("true")) {
				String API = C2CTransferAPI.prepareAPI(AutoO2CMap.c2cMap);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get("COMMAND.TXNSTATUS").toString(), _APIUtil.API_SUCCESS);
				long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
				long expectedBalance = preBalance - _parser.getSystemAmount(AutoO2CMap.c2cMap.get("QTY"));
				if (newBalance == expectedBalance)
					currentNode.log(Status.PASS, "Balance Validation Successful");
				else
					currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
		} 
		else {
			long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
			if (newBalance == preBalance)
				currentNode.log(Status.PASS, "Balance Validation Successful");
			else
				currentNode.log(Status.FAIL, "Balance Validation Failed");
			}

	}

	@Test
	public void CASEL() throws InterruptedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		EXTGWC2SAPI C2STransferAPI = new EXTGWC2SAPI();
		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		TransferControlProfile tcpchange = new TransferControlProfile(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA12").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator
				.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, "false");

		String loginID = transferMap.get("Login_ID");
		String channelUser = transferMap.get("ChannelUser");
		long preBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
		profileID = transferMap.get("PROFILE_ID");
		tcpDetails = DBHandler.AccessHandler.getTCPDetails(profileID, PretupsI.PARENT_PROFILE_ID_CATEGORY, AutoO2CMap.c2cMap.get("PRODUCT"),
				"ALERTING_BALANCE");
		alertingBalance = Integer.parseInt(tcpDetails.get("ALERTING_BALANCE"));
		long diff = preBalance - _parser.getSystemAmount(alertingBalance);
		String newAlertingBalance = Long.toString(preBalance-100);
		if (diff > 0) {
			String[][] valuesToModfiy = new String[][]{ {"enterETopUpAlertingBalance", newAlertingBalance}};
			tcpchange.modifyAnyTCPValue(valuesToModfiy, AutoO2CMap.userData,"channel");
			if (transferMap.get("c2sAllowed") != null && transferMap.get("c2sAllowed").equalsIgnoreCase("true")) {
				String API = C2STransferAPI.prepareAPI(AutoO2CMap.c2sMap);
				String[] APIResponse = _APIUtil.executeAPI(GatewayI.EXTGW, ServicesControllerI.C2SReceiver, API);
				XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, APIResponse[1]);
				Validator.messageCompare(xmlPath.get("COMMAND.TXNSTATUS").toString(), _APIUtil.API_SUCCESS);
				long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
				long expectedBalance = preBalance - _parser.getSystemAmount(AutoO2CMap.c2cMap.get("QTY"));
				if (newBalance == expectedBalance)
					currentNode.log(Status.PASS, "Balance Validation Successful");
				else
					currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
		} 
		else {
			long newBalance = Long.parseLong(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
			if (newBalance == preBalance)
				currentNode.log(Status.PASS, "Balance Validation Successful");
			else
				currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
	}

	@Test
	public void CASEM() throws InterruptedException {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}
		String valueToSet = "true";
		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA13").getExtentCase());
		currentNode.assignCategory(assignCategory);
		HashMap<String, String> transferMap = _mapgenerator
				.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
		if (!controlPreference.equalsIgnoreCase("true"))
			autoO2CTransfer.changePreference(controlPreference, networkPreference, systemPreferenceType,
					transferMap.get("controlName"), valueToSet);
		else
			Log.skip("Control Preference for Auto O2C is already true.No need to chaange.");

	}

	@Test
	public void CASEN_dailyCountNull() {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA14").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (autoO2CAllowed.equalsIgnoreCase("true")) {
			HashMap<String, String> transferMap = _mapgenerator
					.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("dailyCount", "");
			try {
				transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
			} catch (Exception e) {
				String actualMessage = autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO
						.prepareMessageByKey("autoo2c.associatesubscriberdetails.msg.dailycountnull");
				Validator.messageCompare(actualMessage, expectedMessage);
			}
		} else {
			Log.skip("Auto O2C Module not available in system.");
		}
	}

	@Test
	public void CASEO_weeklyCountNull() {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test
				.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA15").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (autoO2CAllowed.equalsIgnoreCase("true")) {
			HashMap<String, String> transferMap = _mapgenerator
					.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("weeklyCount", "");
			try {
				transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
			} catch (Exception e) {
				String actualMessage = autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO
						.prepareMessageByKey("autoo2c.associatesubscriberdetails.msg.weeklycountnull");
				Validator.messageCompare(actualMessage, expectedMessage);
			}
		} else {
			Log.skip("Auto O2C Module not available in system.");
		}
	}

	@Test
	public void CASEP_monthlyCountNull() {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test
				.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA16").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (autoO2CAllowed.equalsIgnoreCase("true")) {
			HashMap<String, String> transferMap = _mapgenerator
					.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("monthlyCount", "");
			try {
				transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
			} catch (Exception e) {
				String actualMessage = autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO
						.prepareMessageByKey("autoo2c.associatesubscriberdetails.msg.monthlycountnull");
				Validator.messageCompare(actualMessage, expectedMessage);
			}
		} else {
			Log.skip("Auto O2C Module not available in system.");
		}
	}

	@Test
	public void CASEQ_dailyCountAlphabetic() {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA17").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (autoO2CAllowed.equalsIgnoreCase("true")) {
			HashMap<String, String> transferMap = _mapgenerator
					.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("dailyCount", "abc");
			try {
				transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
			} catch (Exception e) {
				String actualMessage = autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO
						.prepareMessageByKey("autoo2c.associatesubscriberdetails.msg.dailycountnonnumeric");
				Validator.messageCompare(actualMessage, expectedMessage);
			}
		} else {
			Log.skip("Auto O2C Module not available in system.");
		}
	}

	@Test
	public void CASER_weeklyCountAlphabetic() {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test
				.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA18").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (autoO2CAllowed.equalsIgnoreCase("true")) {
			HashMap<String, String> transferMap = _mapgenerator
					.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("weeklyCount", "abc");
			try {
				transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
			} catch (Exception e) {
				String actualMessage = autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO
						.prepareMessageByKey("autoo2c.associatesubscriberdetails.msg.weeklycountnonnumeric");
				Validator.messageCompare(actualMessage, expectedMessage);
			}
		} else {
			Log.skip("Auto O2C Module not available in system.");
		}
	}

	@Test
	public void CASES_monthlyCountAlphabetic() {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test
				.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA19").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (autoO2CAllowed.equalsIgnoreCase("true")) {
			HashMap<String, String> transferMap = _mapgenerator
					.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("monthlyCount", "abc");
			try {
				transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
			} catch (Exception e) {
				String actualMessage = autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO
						.prepareMessageByKey("autoo2c.associatesubscriberdetails.msg.monthlycountnonnumeric");
				Validator.messageCompare(actualMessage, expectedMessage);
			}
		} else {
			Log.skip("Auto O2C Module not available in system.");
		}
	}

	@Test
	public void CASET_dailyCountNegative() {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA20").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (autoO2CAllowed.equalsIgnoreCase("true")) {
			HashMap<String, String> transferMap = _mapgenerator
					.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("dailyCount", "-1");
			try {
				transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
			} catch (Exception e) {
				String actualMessage = autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO.getLabelByKey("autoo2c.associatesubscriberdetails.msg.dailycountnonnumeric");
				Validator.messageCompare(actualMessage, expectedMessage);
			}
		} else {
			Log.skip("Auto O2C Module not available in system.");
		}
	}

	@Test
	public void CASEU_weeklyCountNegative() {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test
				.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA21").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (autoO2CAllowed.equalsIgnoreCase("true")) {
			HashMap<String, String> transferMap = _mapgenerator
					.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("weeklyCount", "-1");
			try {
				transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
			} catch (Exception e) {
				String actualMessage = autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO.getLabelByKey("autoo2c.associatesubscriberdetails.msg.weeklycountnonnumeric");
				Validator.messageCompare(actualMessage, expectedMessage);
			}
		} else {
			Log.skip("Auto O2C Module not available in system.");
		}
	}

	@Test
	public void CASEV_monthlyCountNegative() {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test
				.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA22").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (autoO2CAllowed.equalsIgnoreCase("true")) {
			HashMap<String, String> transferMap = _mapgenerator
					.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
			transferMap.put("monthlyCount", "-1");
			try {
				transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
			} catch (Exception e) {
				String actualMessage = autoO2CTransfer.getErrorMessage();
				String expectedMessage = MessagesDAO.getLabelByKey("autoo2c.associatesubscriberdetails.msg.monthlycountnonnumeric");
				Validator.messageCompare(actualMessage, expectedMessage);
			}
		} else {
			Log.skip("Auto O2C Module not available in system.");
		}
	}

	@Test
	public void CASEW_dailyCountPositiveCase() throws InterruptedException {

		Log.startTestCase(this.getClass().getName());

		if (testCaseCounter == false) {
			test = extent.createTest("["+assignCategory+"]"+_masterVO.getCaseMasterByID("SITAUTOO2CIDEA1").getModuleCode());
			testCaseCounter = true;
		}

		AutoO2CTransfer autoO2CTransfer = new AutoO2CTransfer(driver);
		AutoO2CMap _mapgenerator = new AutoO2CMap();
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SITAUTOO2CIDEA23").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String loginID = transferMap.get("Login_ID");
		String[] userId = DBHandler.AccessHandler.getUserDetails(loginID,"USER_ID");
		String dailyLimitCount = DBHandler.AccessHandler.getDailyLimitForAutoO2C(userId[0]);
		if (dailyLimitCount.equalsIgnoreCase(transferMap.get("dailyCount"))) {
			if (autoO2CAllowed.equalsIgnoreCase("true")) {
				HashMap<String, String> transferMap = _mapgenerator
						.getAutoOperatorToChannelMap(_masterVO.getProperty("O2CTransferCode"));
				String channelUser = transferMap.get("ChannelUser");
				String b = DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID);
				int balance = Integer.parseInt(b);

				transferMap = autoO2CTransfer.initiateAutoO2CTransferIDEA(transferMap, autoO2CAllowed);
				int newBalance = Integer
						.parseInt(DBHandler.AccessHandler.getUserBalance(AutoO2CMap.c2cMap.get("PRODUCT"), loginID));
				if (newBalance == balance)
					currentNode.log(Status.PASS, "Balance Validation Successful");
				else
					currentNode.log(Status.FAIL, "Balance Validation Failed");
			}
		} else {
			Log.skip("Daily Limit less than defined.");
		}
	}
}
