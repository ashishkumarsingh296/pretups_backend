package com.testscripts.sit;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.StaffUser;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;

/*
 * @author PVG
 * This class is created to add Channel Users
 */
public class SIT_StaffUser extends BaseTest {
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
	public void staffUserCreation(int RowNum, String Domain, String Parent, String Category, String geotype) throws InterruptedException, IOException {
		final String methodname = "channelUserCreation";
		Log.startTestCase(methodname, RowNum, Domain, Parent, Category, geotype);
		
		CaseMaster CaseMaster1=_masterVO.getCaseMasterByID("SITSTAFFCREATION1");
		CaseMaster CaseMaster2=_masterVO.getCaseMasterByID("SITSTAFFCREATION2");
		CaseMaster CaseMaster3=_masterVO.getCaseMasterByID("SITSTAFFCREATION3");
		CaseMaster CaseMaster4=_masterVO.getCaseMasterByID("SITSTAFFCREATION4");
		CaseMaster CaseMaster5=_masterVO.getCaseMasterByID("SITSTAFFCREATION5");
		CaseMaster CaseMaster6=_masterVO.getCaseMasterByID("SITSTAFFCREATION6");
		
		// Check if Test Case is already available. If not Test Case is created for Extent Report & Counter is updated
		if (TestCaseCounter == false) {
			test = extent.createTest("[SIT]"+CaseMaster1.getModuleCode());
			TestCaseCounter = true;
		}
		boolean data=false;
		StaffUser channelUser= new StaffUser(driver);
		HashMap<String, String> staffMap=null;
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), Category,Parent));
		currentNode.assignCategory("SIT");
		try{staffMap = channelUser.staffUserInitiate(RowNum, Domain, Parent, Category, geotype);
		data=true;
		}catch(Exception e){
			currentNode.log(Status.FAIL, "Staff user initiation failed.");
			ExtentI.attachCatalinaLogs();ExtentI.attachScreenShot();
			data=false;
		}
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,"STAFF_USER_APRL_LEVEL");
		
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(Category);
		if(APPLEVEL.equals("2")) {
			
			currentNode=test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), Category));
			currentNode.assignCategory("SIT");
			try{channelUser.approveLevel1_StaffUser();}catch(Exception e){
				currentNode.log(Status.FAIL, "Approval level 1 failed.");
				ExtentI.attachCatalinaLogs();ExtentI.attachScreenShot();
			}
			
			currentNode=test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), Category));
			currentNode.assignCategory("SIT");
			try{channelUser.approveLevel2_StaffUser();}
			catch(Exception e){currentNode.log(Status.FAIL, "Approval level 2 failed.");
			ExtentI.attachCatalinaLogs();ExtentI.attachScreenShot();}
			
		} else if(APPLEVEL.equals("1")) {
		
			currentNode=test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), Category));
			currentNode.assignCategory("SIT");
			try{channelUser.approveLevel1_StaffUser();}catch(Exception e){
				currentNode.log(Status.FAIL, "Approval level 1 failed.");
				ExtentI.attachCatalinaLogs();ExtentI.attachScreenShot();
			}	
		} else
			Log.info("Approval not required.");	
		
		channelUser.writeChannelUserData(RowNum);
		
		if(webAccessAllowed.equals("Y")){
			currentNode=test.createNode(MessageFormat.format(CaseMaster5.getExtentCase(), Category));
			currentNode.assignCategory("SIT");
			try{staffMap = channelUser.changeUserFirstTimePassword();}
			catch(Exception e){currentNode.log(Status.FAIL, "Change Password failed.");
			ExtentI.attachCatalinaLogs();ExtentI.attachScreenShot();}
			if(data){ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.STAFF_USERS_SHEET);
			ExcelUtility.setCellData(0, ExcelI.STAFF_PASSWORD, RowNum,staffMap.get("PASSWORD"));}
		}
		
		String isMSISDNrequired = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,"IS_REQ_MSISDN_FOR_STAFF");
		if(isMSISDNrequired.equalsIgnoreCase("true")){
		currentNode=test.createNode(MessageFormat.format(CaseMaster6.getExtentCase(), Category));
		currentNode.assignCategory("SIT");
		try{channelUser.changeUserFirstTimePIN();}
		catch(Exception e){currentNode.log(Status.FAIL, "Change PIN failed.");
		ExtentI.attachCatalinaLogs();ExtentI.attachScreenShot();}
		if(data){ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.STAFF_USERS_SHEET);
		ExcelUtility.setCellData(0, ExcelI.STAFF_PIN, RowNum,staffMap.get("PIN"));}
		}
		Log.info("Map Values: ["+staffMap+"]");
		Log.endTestCase(methodname);
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