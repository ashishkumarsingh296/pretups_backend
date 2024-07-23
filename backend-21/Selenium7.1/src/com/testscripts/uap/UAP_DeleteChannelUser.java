/**
 * 
 */
package com.testscripts.uap;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.DeleteChannelUser;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class UAP_DeleteChannelUser extends BaseTest{
	
	HashMap<String, String> channelresultMap;
	HashMap<String, String> deleteMap;
	static boolean TestCaseCounter = false;
	
	@Test(dataProvider="Domain&CategoryProvider")
	public void deleteChnlUser(int RowNum, String Domain, String Parent, String Category, String geotype) throws InterruptedException{
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[UAP]Delete Channel User");
			TestCaseCounter = true;
		}
		
		DeleteChannelUser deleteChnlUser = new DeleteChannelUser(driver);
		ChannelUser channelUserLogic= new ChannelUser(driver);
		String deleteApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_DLT_APP").toUpperCase();
		
		
		String Remarks="Automation Remarks";
		//Test Case
		currentNode=test.createNode("To verify that Operator user is able to delete " + Category+" category Channel user using MSISDN.");
		currentNode.assignCategory("UAP");
		channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype);
		
		String APPLEVEL = DBHandler.AccessHandler.getSystemPreference("USER_APPROVAL_LEVEL");
		if(APPLEVEL.equals("2"))
		{
		channelUserLogic.approveLevel1_ChannelUser();
		channelUserLogic.approveLevel2_ChannelUser();
		}
		else if(APPLEVEL.equals("1")){
			channelUserLogic.approveLevel1_ChannelUser();	
		}else{
			Log.info("Approval not required.");	
		}
		deleteMap = deleteChnlUser.deletechannelUser_MSISDN(channelresultMap.get("MSISDN"), Remarks);
		
		if(deleteApprovalReq.equals("TRUE")){
		//Test Case
		currentNode=test.createNode("To verify that Operator user is able to discard request for deletion of " + Category+" category Channel user using MSISDN.");
		currentNode.assignCategory("UAP");
		deleteChnlUser.discardDeleteChannelUser_MSISDN(channelresultMap.get("MSISDN"), Remarks);
		
		//Test Case
		currentNode=test.createNode("To verify that Operator user is able to reject request for deletion of " + Category+" category Channel user using MSISDN.");
		currentNode.assignCategory("UAP");
		deleteChnlUser.rejectDeleteChannelUser_MSISDN(channelresultMap.get("MSISDN"), Remarks);
		
		//Test Case
		currentNode=test.createNode("To verify that Operator user is able to approve request for deletion of " + Category+" category Channel user using MSISDN.");
		currentNode.assignCategory("UAP");
		deleteChnlUser.deletechannelUser_MSISDN(channelresultMap.get("MSISDN"), Remarks);
		deleteChnlUser.approveDeleteChannelUser_MSISDN(channelresultMap.get("MSISDN"), Remarks);
		}
		else{
			currentNode=test.createNode("To verify that proper message appears after user deletion.");
			currentNode.assignCategory("UAP");
			String expectedMsg = MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.deletesuccessmessage", channelresultMap.get("uName"));
			String actualMsg = deleteMap.get("DeletionMsg");
			Validator.messageCompare(actualMsg, expectedMsg);
		}
	}
	
	@DataProvider(name = "Domain&CategoryProvider")
	public Object[][] DomainCategoryProvider_positive() {
		
		_masterVO.loadGeoDomains();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		//int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[1][5];
	
			categoryData[0][0] = 1;
			categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
			categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, 1);
			categoryData[0][3] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
			categoryData[0][4] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, 1);
		
		return categoryData;
	}
	
}
