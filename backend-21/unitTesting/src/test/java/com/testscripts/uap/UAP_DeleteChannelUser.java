/**
 * 
 */
package com.testscripts.uap;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.DeleteChannelUser;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

/**
 * @author lokesh.kontey
 *
 */
@ModuleManager(name =Module.UAP_DELETE_CHANNEL_USER)
public class UAP_DeleteChannelUser extends BaseTest{
	
	HashMap<String, String> channelresultMap;
	HashMap<String, String> deleteMap;
	static boolean TestCaseCounter = false;
	
	@Test(dataProvider="Domain&CategoryProvider")
	@TestManager(TestKey = "PRETUPS-387") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void deleteChnlUser(int RowNum, String Domain, String Parent, String Category, String geotype) throws InterruptedException{

		final String methodName = "Test_deleteChnlUsers";
        Log.startTestCase(methodName);
       
		
		CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("UCHNLUSRDELETE1");
		CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("UCHNLUSRDELETE2");
		CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("UCHNLUSRDELETE3");
		CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("UCHNLUSRDELETE4");
		CaseMaster CaseMaster5 = _masterVO.getCaseMasterByID("UCHNLUSRDELETE5");
		
		DeleteChannelUser deleteChnlUser = new DeleteChannelUser(driver);
		ChannelUser channelUserLogic= new ChannelUser(driver);
		String deleteApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_DLT_APP").toUpperCase();
		
		
		String Remarks="Automation Remarks";
		//Test Case
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),Category));
		currentNode.assignCategory("UAP");
		channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype);
		
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		if(APPLEVEL.equals("2")) {
			channelUserLogic.approveLevel1_ChannelUser();
			channelUserLogic.approveLevel2_ChannelUser();
		} else if(APPLEVEL.equals("1")){
			channelUserLogic.approveLevel1_ChannelUser();	
		} else {
			Log.info("Approval not required.");	
		}
		
		deleteMap = deleteChnlUser.deletechannelUser_MSISDN(channelresultMap.get("MSISDN"), Remarks);
		
		if(deleteApprovalReq.equals("TRUE")) {
		//Test Case
		currentNode=test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(),Category));
		currentNode.assignCategory("UAP");
		deleteChnlUser.discardDeleteChannelUser_MSISDN(channelresultMap.get("MSISDN"), Remarks);
		
		//Test Case
		currentNode=test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(),Category));
		currentNode.assignCategory("UAP");
		deleteChnlUser.rejectDeleteChannelUser_MSISDN(channelresultMap.get("MSISDN"), Remarks);
		
		//Test Case
		currentNode=test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(),Category));
		currentNode.assignCategory("UAP");
		deleteChnlUser.deletechannelUser_MSISDN(channelresultMap.get("MSISDN"), Remarks);
		deleteChnlUser.approveDeleteChannelUser_MSISDN(channelresultMap.get("MSISDN"), Remarks);
		}
		else{
			currentNode=test.createNode(CaseMaster5.getExtentCase());
			currentNode.assignCategory("UAP");
			String expectedMsg = MessagesDAO.prepareMessageByKey("user.deletesuspendchanneluser.deletesuccessmessage", channelresultMap.get("uName"));
			String actualMsg = deleteMap.get("DeletionMsg");
			Assertion.assertEquals(actualMsg, expectedMsg);
			}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
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
