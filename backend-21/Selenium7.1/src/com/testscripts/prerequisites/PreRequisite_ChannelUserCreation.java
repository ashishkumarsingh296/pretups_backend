package com.testscripts.prerequisites;

import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils._masterVO;
import com.utils.Log;

/*
 * @author PVG
 * This class is created to add Channel Users
 */
public class PreRequisite_ChannelUserCreation extends BaseTest {
	String LoginID;
	String MSISDN;
	String PASSWORD;
	String EXTCODE;
	String CONFIRMPASSWORD;
	String NEWPASSWORD;
	String UserName;
	String UserName1;
	static String homepage1;	
	static HashMap<String, String> map, map1 = null;
	static boolean TestCaseCounter = false;

	@Test(dataProvider = "Domain&CategoryProvider")
	public void channelUserCreation(int RowNum, String Domain, String Parent, String Category, String geotype) throws InterruptedException {
		
		// Check if Test Case is already available. If not Test Case is created for Extent Report & Counter is updated
		if (TestCaseCounter == false) {
			test = extent.createTest("[Pre-Requisite]Channel User Creation");
			TestCaseCounter = true;
		}
		
		ChannelUser channelUser= new ChannelUser(driver);
		
		currentNode=test.createNode("To verify that Channel Admin is able to initiate "+ Category+" category Channel user with parent category "+Parent+".");
		currentNode.assignCategory("Pre-Requisite");
		channelUser.channelUserInitiate(RowNum, Domain, Parent, Category, geotype);
		
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,"USER_APPROVAL_LEVEL");
		
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(Category);
		if(APPLEVEL.equals("2"))
		{
		currentNode=test.createNode("To verify that Channel Admin is able to approve level 1 " + Category+" category Channel user.");
		currentNode.assignCategory("Pre-Requisite");
		channelUser.approveLevel1_ChannelUser();
		
		currentNode=test.createNode("To verify that Channel Admin is able to approve level 2 " + Category+" category Channel user.");
		currentNode.assignCategory("Pre-Requisite");
		channelUser.approveLevel2_ChannelUser();
		
		}
		else if(APPLEVEL.equals("1")){
		
			currentNode=test.createNode("To verify that Channel Admin is able to approve " + Category+" category Channel user.");
			currentNode.assignCategory("Pre-Requisite");
			channelUser.approveLevel1_ChannelUser();	
		
		}else{
		
			Log.info("Approval not required.");	
		}
		channelUser.writeChannelUserData(RowNum);
		
		if(webAccessAllowed.equals("Y")){
		currentNode=test.createNode("To verify that " + Category+" category Channel user is prompted for change password on first time login and successfuly change the password.");
		currentNode.assignCategory("Pre-Requisite");
		channelUser.changeUserFirstTimePassword();
		channelUser.writeChannelUserData(RowNum);}
		
		currentNode=test.createNode("To verify that Channel Admin change the PIN of " + Category+" category Channel user for processing further transaction.");
		currentNode.assignCategory("Pre-Requisite");
		channelUser.changeUserFirstTimePIN();
		
		channelUser.writeChannelUserData(RowNum);
		
		Log.endTestCase(this.getClass().getName());
		
	}

	@DataProvider(name = "Domain&CategoryProvider")
	public Object[][] DomainCategoryProvider() {
		
		_masterVO.loadGeoDomains();
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[rowCount][5];
		for (int i = 1, j = 0; i <= rowCount; i++, j++) {
			categoryData[j][0] = i;
			categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			categoryData[j][4] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i);
		}
		return categoryData;
	}
	
}