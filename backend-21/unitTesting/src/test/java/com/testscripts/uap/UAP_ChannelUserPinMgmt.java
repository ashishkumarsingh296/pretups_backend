/**
 * 
 */
package com.testscripts.uap;

import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChangeChannelUserPIN;
import com.Features.ChannelUserPinManagement;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

/**
 * @author lokesh.kontey
 *
 */

@ModuleManager(name =Module.UAP_CHANNEL_USER_PIN_MANAGEMENT)
public class UAP_ChannelUserPinMgmt extends BaseTest {

	HashMap<String, String> pinresultMap,channelPINMap;
	static boolean TestCaseCounter = false;
	HashMap<String, String> userAccessMap;
	
	@Test(dataProvider="pinMgmtData")
	@TestManager(TestKey = "PRETUPS-405") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void chnlPINMgmt(String mobileNo, String LoginID,String Remarks) throws InterruptedException, IOException{
		
		final String methodName = "Test_chnlPINMgmt";
        Log.startTestCase(methodName);
				
		ChannelUserPinManagement chnlPinMgmt = new ChannelUserPinManagement(driver);
		ChangeChannelUserPIN changePIN = new ChangeChannelUserPIN(driver);
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("UCHNLPINMGMT1");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("UCHNLPINMGMT2");
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("UCHNLPINMGMT3");
		CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("UCHNLPINMGMT4");
		CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("UCHNLPINMGMT5");
		CaseMaster CaseMaster6 = _masterVO.getCaseMasterByID("UCHNLPINMGMT6");
	
		HashMap<String, String> pinMgmtMap = new HashMap<String, String>();
		pinMgmtMap.put("mobileNumber", mobileNo);
		pinMgmtMap.put("Remarks", Remarks);
		pinMgmtMap.put("LoginID",LoginID);
	
		String value = DBHandler.AccessHandler.getPreference("", _masterVO.getMasterValue("Network Code"), CONSTANT.DISSABLE_BUTTON_LIST);
		/*
		 * Test case1
		 */
		currentNode=test.createNode(CaseMaster1.getExtentCase());
		currentNode.assignCategory("UAP");
		if(!DBHandler.AccessHandler.getSystemPreference(CONSTANT.PINPAS_EN_DE_CRYPTION_TYPE).equalsIgnoreCase("SHA")){
		if(value.contains("SEND_PIN")){
			Assertion.assertSkip("'SEND PIN' button is disabled in the system, hence the case is skipped.");
		}else{
			pinresultMap=chnlPinMgmt.channelUserPinMgmt_sendPIN(pinMgmtMap);

			/*
			 * Test case2
			 */
			currentNode=test.createNode(CaseMaster2.getExtentCase());
			currentNode.assignCategory("UAP");
			String sendPINMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpin.msg.sendmsg.success", "");
			Assertion.assertEquals(pinresultMap.get("sendPinMsg"), sendPINMsg);	
		}}else{Log.skip("Case Skipped as in SHA type encryption Send PIN is not allowed.");}
		
		/*
		 * Test case3
		 */
		currentNode=test.createNode(CaseMaster3.getExtentCase());
		currentNode.assignCategory("UAP");
		pinresultMap=chnlPinMgmt.channelUserPinMgmt_ResetPIN(pinMgmtMap);
		
		/*
		 * Test case4
		 */
		currentNode=test.createNode(CaseMaster4.getExtentCase());
		currentNode.assignCategory("UAP");
		String resetPINMsg=MessagesDAO.prepareMessageByKey("channeluser.unblockpin.msg.resetsuccess", "");
		Assertion.assertEquals(pinresultMap.get("resetPinMsg"), resetPINMsg);
	
		/*
		 * Test case5
		 */
		currentNode=test.createNode(CaseMaster5.getExtentCase());
		currentNode.assignCategory("UAP");
		channelPINMap=changePIN.changePINafterReset(pinMgmtMap.get("LoginID"),pinMgmtMap.get("mobileNumber"));
		
		/*
		 * Test case6
		 */
		currentNode=test.createNode(CaseMaster6.getExtentCase());
		currentNode.assignCategory("UAP");
		String changePINMsg=MessagesDAO.prepareMessageByKey("user.changepin.msg.updatesuccess", "");
		Assertion.assertEquals(channelPINMap.get("changePINMsg"), changePINMsg);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@DataProvider(name="pinMgmtData")
	public Object[][] pinManagementData(){
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowNum=1;
		RandomGeneration randStr = new RandomGeneration();
		
		String userDetailsHL[] = {ExcelUtility.getCellData(0, ExcelI.MSISDN, rowNum),ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, rowNum)};	
		
		//sequence:: MSISDN, LoginID, Remarks
		Object[][] categoryData = new Object[][]{
				{userDetailsHL[0],userDetailsHL[1],"AUT"+randStr.randomAlphabets(8)+":: Automated Remarks"}
			};
		
		return categoryData;
		
	}
}
