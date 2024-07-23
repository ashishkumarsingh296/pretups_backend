package com.testscripts.sit;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUserTransfer;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;


@ModuleManager(name = Module.SIT_ChannelUserTransfer)
public class SIT_ChannelUserTransfer extends BaseTest {
 
	String MasterSheetPath;
	static String moduleCode;
	
	/*@DataProvider(name="ChannelTransferUser")
	public Object[][] ChannelTransferUserDP() {
		
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] dataObj = new Object[1][1];
		int objCounter = 0;
		
		for (int i = 1; i <= 1; i++) {
			HashMap<String, String> chnlUserTransfer = new HashMap<String, String>();
			chnlUserTransfer.put("domainName", ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i));
			chnlUserTransfer.put("parentCategory", ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i));
			chnlUserTransfer.put("categoryName", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i));
			chnlUserTransfer.put("geoDoamain", ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i));
			chnlUserTransfer.put("userName", ExcelUtility.getCellData(0, ExcelI.USER_NAME, i));
			chnlUserTransfer.put("msisdn", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
			chnlUserTransfer.put("categoryCode", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i));
			chnlUserTransfer.put("loginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
			
			
			
			dataObj[objCounter][0] = chnlUserTransfer.clone();
			dataObj[objCounter][1] = ++objCounter;
		}
		
		return dataObj;
	}	
	*/
	
	
	@DataProvider(name="ChannelTransferUser")
	public Object[][] ChannelTransferUserDP(){
		
		String MasterSheetPath=_masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		
		HashMap<String, String> chnlUserTransfer = new HashMap<String, String>();
		chnlUserTransfer.put("domainName", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, 1));
		chnlUserTransfer.put("parentCategory", ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, 1));
		chnlUserTransfer.put("categoryName", ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1));
		chnlUserTransfer.put("geoDoamain", ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, 1));
		chnlUserTransfer.put("userName", ExcelUtility.getCellData(0, ExcelI.USER_NAME, 1));
		chnlUserTransfer.put("msisdn", ExcelUtility.getCellData(0, ExcelI.MSISDN, 1));
		chnlUserTransfer.put("categoryCode", ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, 1));
		chnlUserTransfer.put("loginID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, 1));
		
		Object[][] paramData= new Object[][]{
				{chnlUserTransfer}
		};
		return paramData;
	}
	
	
	 @TestManager(TestKey = "PRETUPS-1846") 
		@Test(dataProvider="ChannelTransferUser")// TO BE UNCOMMENTED WITH JIRA TEST ID
		public void A_SuspendChannelUserByLoginId(HashMap<String, String> initiateMap) {
			
			final String methodName = "A_SuspendChannelUserByLoginId";
	        Log.startTestCase(methodName);
			String result;
			currentNode=test.createNode(_masterVO.getCaseMasterByID("SITCHANNELUSERTRANSFER1").getExtentCase());
			currentNode.assignCategory("SIT");
			
			ChannelUserTransfer channelUserTransfer = new ChannelUserTransfer(driver);
			result = channelUserTransfer.suspendChannelUserLoginID(initiateMap);
			String Message = MessagesDAO.prepareMessageByKey("channeluser.userhierarchyaction.msg.suspend");
   		    Assertion.assertEquals(result, Message);
			Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
	 
	 @TestManager(TestKey = "PRETUPS-1847") 
		@Test(dataProvider="ChannelTransferUser")// TO BE UNCOMMENTED WITH JIRA TEST ID
	 public void B_ResumeChannelUserByLoginId(HashMap<String, String> initiateMap) {
			
			final String methodName = "A_ResumeChannelUserByLoginId";
	        Log.startTestCase(methodName);
			String result;
			currentNode=test.createNode(_masterVO.getCaseMasterByID("SITCHANNELUSERTRANSFER2").getExtentCase());
			currentNode.assignCategory("SIT");
			
			ChannelUserTransfer channelUserTransfer = new ChannelUserTransfer(driver);
			result = channelUserTransfer.resumeChannelUserLoginID(initiateMap);
			String Message = MessagesDAO.prepareMessageByKey("channeluser.userhierarchyaction.msg.resume");
		    Assertion.assertEquals(result, Message);
			Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
	
	// @TestManager(TestKey = "PRETUPS-000") 
	//	@Test(dataProvider="ChannelTransferUser")// TO BE UNCOMMENTED WITH JIRA TEST ID
		public void A_SuspendChannelUserByGeoDomain(HashMap<String, String> initiateMap) {
			
			final String methodName = "A_SuspendChannelUserByGeoDomain";
	        Log.startTestCase(methodName);
			String result;
			currentNode=test.createNode(_masterVO.getCaseMasterByID("SITCHANNELUSERTRANSFER3").getExtentCase());
			currentNode.assignCategory("SIT");
			
			ChannelUserTransfer channelUserTransfer = new ChannelUserTransfer(driver);
			result = channelUserTransfer.suspendChannelUserGeoDomain(initiateMap);
			String Message = MessagesDAO.prepareMessageByKey("channeluser.userhierarchyaction.msg.suspend");
		    Assertion.assertEquals(result, Message);
			Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
	
	 
	 
}
