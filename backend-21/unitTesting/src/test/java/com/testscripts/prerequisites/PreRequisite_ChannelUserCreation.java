package com.testscripts.prerequisites;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

/*
 * @author PVG
 * This class is created to add Channel Users
 */
@ModuleManager(name = Module.PREREQUISITE_CHANNEL_USER_CREATION)
public class PreRequisite_ChannelUserCreation extends BaseTest {

	private HashMap<String, String> channelMap;

	@Test(dataProvider = "Domain&CategoryProvider")
	@TestManager(TestKey = "PRETUPS-444") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void Test_ChannelUserCreation(int RowNum, String Domain, String Parent, String Category, String geotype) throws InterruptedException {
		final String methodName = "Test_ChannelUserCreation";
		Log.startTestCase(methodName, RowNum, Domain, Parent, Category, geotype);
		
		CaseMaster CaseMaster1= _masterVO.getCaseMasterByID("PCHNLCREATION1");
		CaseMaster CaseMaster2= _masterVO.getCaseMasterByID("PCHNLCREATION2");
		CaseMaster CaseMaster3= _masterVO.getCaseMasterByID("PCHNLCREATION3");
		CaseMaster CaseMaster4= _masterVO.getCaseMasterByID("PCHNLCREATION4");
		CaseMaster CaseMaster5= _masterVO.getCaseMasterByID("PCHNLCREATION5");
		CaseMaster CaseMaster6= _masterVO.getCaseMasterByID("PCHNLCREATION6");

		ChannelUser channelUser= new ChannelUser(driver);
		
		currentNode=test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(),Category,Parent)).assignCategory(TestCategory.PREREQUISITE);
		channelUser.channelUserInitiate(RowNum, Domain, Parent, Category, geotype);
		
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String APPLEVEL = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,UserAccess.userapplevelpreference());
		if (APPLEVEL == null)
			APPLEVEL = DBHandler.AccessHandler.getSystemPreference("USRLEVELAPPROVAL");
		
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(Category);
		if(APPLEVEL.equals("2")) {
			currentNode=test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(),Category)).assignCategory(TestCategory.PREREQUISITE);
			channelUser.approveLevel1_ChannelUser();
			
			currentNode=test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(),Category)).assignCategory(TestCategory.PREREQUISITE);
			channelUser.approveLevel2_ChannelUser();
		} else if(APPLEVEL.equals("1")) {
			currentNode=test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(),Category)).assignCategory(TestCategory.PREREQUISITE);
			channelUser.approveLevel1_ChannelUser();	
		} else
			Log.info("Approval not required.");	
		
		channelUser.writeChannelUserData(RowNum);
		
		if(webAccessAllowed.equals("Y")){
			currentNode=test.createNode(MessageFormat.format(CaseMaster5.getExtentCase(),Category)).assignCategory(TestCategory.PREREQUISITE);
			channelUser.changeUserFirstTimePassword();
			String actual = new AddChannelUserDetailsPage(driver).getActualMessage();
			String expected = MessagesDAO.getLabelByKey("login.changeCommonLoginPassword.updatesuccessmessage");
			boolean isChannelUserCreated = Assertion.assertEquals(actual, expected);

			if(isChannelUserCreated){
				channelUser.writeChannelUserData(RowNum);	
			}
			
		}
		
		currentNode=test.createNode(MessageFormat.format(CaseMaster6.getExtentCase(),Category)).assignCategory(TestCategory.PREREQUISITE);
		channelMap=channelUser.changeUserFirstTimePIN();
		channelUser.writeChannelUserData(RowNum);
		
		String intChnlChangePINMsg = MessagesDAO.prepareMessageByKey("user.changepin.msg.updatesuccess");
		Validator.messageCompare(channelMap.get("changePINMsg"), intChnlChangePINMsg);

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	/* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
	/* ------------------------------------------------------------------------------------------------- */

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

	/* ------------------------------------------------------------------------------------------------- */
}