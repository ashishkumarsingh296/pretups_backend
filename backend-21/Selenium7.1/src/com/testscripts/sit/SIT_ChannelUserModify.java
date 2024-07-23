package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.DeleteChannelUser;
import com.Features.mapclasses.ChannelUserMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
import com.classes.UniqueChecker;
import com.classes.UserAccess;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 * This class is created to add Channel Users and perform all approval levels. Change first time user password and pin. 
 */
public class SIT_ChannelUserModify extends BaseTest {
	
	static String homepage1;	
	static HashMap<String, String> map, map1 = null;
	HashMap<String, String> channelresultMap;
	HashMap<String, String> channelresultMap1;
	static boolean TestCaseCounter = false;
	HashMap<String, String> userAccessMap;
	static int minPaswdLength;
	static int maxPaswdLength;
	
	
	@Test(dataProvider = "Domain&CategoryProvider_validations")
	public void channelUserModifySIT(int CaseNum,int RowNum, String Domain, String Parent,String Category, String geotype,String Description, HashMap<String, String> mapParam, HashMap<String, String> channelMap) throws IOException, InterruptedException{
	
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Channel User Modification");
			TestCaseCounter = true;
		}
		
		String waterMark = "Since, web access is not allowed, hence the case is skipped.";
		
		ChannelUser channelUserLogic= new ChannelUser(driver);
		userAccessMap = (HashMap<String, String>) UserAccess.getUserWithAccess(RolesI.ADD_CHANNEL_USER_ROLECODE);
		DeleteChannelUser deleteCHNLUser = new DeleteChannelUser(driver);
		AddChannelUserDetailsPage adChnlUserDetailsPage = new AddChannelUserDetailsPage(driver);
		
		currentNode=test.createNode(Description);
		currentNode.assignCategory("SIT");
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(Category);
		
		switch(CaseNum){
		case 0://To verify that error message appear if invalid mobile number is entered in search criteria on Modify Channel user screen.
			try{channelUserLogic.modifyChannelUserDetails(Category, mapParam);
			}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage();
			String expected = MessagesDAO.prepareMessageByKey("user.selectchanneluserforview.error.usermsisdnnotexist", mapParam.get("searchMSISDN"));
			Validator.messageCompare(actual, expected);}
			break;
		
		case 1://To verify that channel user modification is not successful if existing loginID is entered on channel user details page.
			try{mapParam.put("searchMSISDN", channelMap.get("MSISDN"));
				mapParam.put("assgnPhoneNumber", "N");
				String actual=channelUserLogic.modifyChannelUserDetails(Category, mapParam);
				String expected = MessagesDAO.prepareMessageByKey("user.addchanneluser.error.loginallreadyexist");
				Validator.messageCompare(actual, expected);}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage();
			String expected = MessagesDAO.prepareMessageByKey("user.addchanneluser.error.loginallreadyexist");
			Validator.messageCompare(actual, expected);}
			break;
		
		case 2://To verify that channel user modification is not successful if Mobile number entered as blank.
			if(webAccessAllowed.equals("Y")){
			try{mapParam.put("searchMSISDN", channelMap.get("MSISDN"));
			mapParam.put("loginChange", "N");
			channelUserLogic.modifyChannelUserDetails(Category, mapParam);
			String actual = CONSTANT.CU_ASSIGNPHONENO_ERR;
			String expected = MessagesDAO.prepareMessageByKey("user.asignPhone.error.primarynumber","1");
			Validator.messageCompare(actual, expected);
			}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage();
			String expected = MessagesDAO.prepareMessageByKey("user.asignPhone.error.primarynumber","1");
			Validator.messageCompare(actual, expected);}}
			else{Log.info("WebLogin is not allowed.");
			currentNode.log(Status.SKIP, waterMark);}
			break;
		
		case 3://To verify that channel user modification is not successful if loginID is blank.
			try{mapParam.put("searchMSISDN", channelMap.get("MSISDN"));
			mapParam.put("assgnPhoneNumber", "N");
			channelUserLogic.modifyChannelUserDetails(Category, mapParam);
			}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage();
			String expected = MessagesDAO.prepareMessageByKey("user.adduser.error.loginidrequired");
			Validator.messageCompare(actual, expected);}
			break;
		
		case 4://To verify that channel user modification is not successful if Mobile number entered is of existing channel user.
			try{mapParam.put("searchMSISDN", channelMap.get("MSISDN"));
			mapParam.put("loginChange", "N");
			channelUserLogic.modifyChannelUserDetails(Category, mapParam);
			String actual = CONSTANT.CU_ASSIGNPHONENO_ERR;
			String expected = MessagesDAO.prepareMessageByKey("user.assignphone.error.msisdnallreadyexist",mapParam.get("MSISDN"));
			Validator.messageCompare(actual, expected);}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = CONSTANT.CU_ASSIGNPHONENO_ERR;
			String expected = MessagesDAO.prepareMessageByKey("user.assignphone.error.msisdnallreadyexist",mapParam.get("MSISDN"));
			Validator.messageCompare(actual, expected);}
			break;
				
		default: Log.info("No valid data found."); 
		}
		
		if(CaseNum==4){
			String deleteApprovalReq = DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_DLT_APP").toUpperCase();
			String remarks1="Automation Remarks deletion";
			deleteCHNLUser.deletechannelUser_MSISDN(channelMap.get("MSISDN"), remarks1);
			if(deleteApprovalReq.equals("TRUE")){
				deleteCHNLUser.approveDeleteChannelUser_MSISDN(channelMap.get("MSISDN"), remarks1);
			}
		}
		
		
		}
	
	
	@DataProvider(name = "Domain&CategoryProvider_validations")
	public Object[][] DomainCategoryProvider_validations() throws InterruptedException {
	
		String masterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(masterSheetPath, "Channel Users Hierarchy");
		
		ChannelUserMap chnlUserMap = new ChannelUserMap();
		ChannelUser channelUser= new ChannelUser(driver);
		int rowNum=1;
	
		String[] userDetailsHL = new String[5];
		
		userDetailsHL[0] = ExcelUtility.getCellData(0, "DOMAIN_NAME", 1);
		userDetailsHL[1] = ExcelUtility.getCellData(0, "PARENT_CATEGORY_NAME", 1);
		userDetailsHL[2] = ExcelUtility.getCellData(0, "CATEGORY_NAME", 1);
		userDetailsHL[3] = ExcelUtility.getCellData(0, "GRPH_DOMAIN_TYPE", 1);	
	
		channelresultMap = channelUser.channelUserInitiate(rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3]);
		String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(userDetailsHL[2]);
		String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		String applevel = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,"USER_APPROVAL_LEVEL");
	
		if(applevel.equals("2"))
		{channelUser.approveLevel1_ChannelUser();
		 channelUser.approveLevel2_ChannelUser();}
		else if(applevel.equals("1")){channelUser.approveLevel1_ChannelUser();}
		else{Log.info("Approval not required.");}

		String[] description=new String[5];
		description[0]="To verify that error message appear if invalid mobile number is entered in search criteria on Modify Channel user screen.";
		description[1]="To verify that channel user modification is not successful if existing loginID is enterd on channel user details page.";
		description[2]="To verify that channel user modification is not successful if Mobile number entered as blank.";
		description[3]="To verify that channel user modification is not successful if loginID is blank.";
		description[4]="To verify that channel user modification is not successful if Mobile number entered is of existing channel user.";
		
		Object[][] categoryData;
		categoryData = new Object[][]{{0,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[0], chnlUserMap.getChannelUserMap("searchMSISDN", UniqueChecker.UC_MSISDN()),channelresultMap},
									 {1,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[1], chnlUserMap.getChannelUserMap("LoginID", DBHandler.AccessHandler.existingLoginID()),channelresultMap},
									 {2,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[2], chnlUserMap.getChannelUserMap("MSISDN",""),channelresultMap},
									 {3,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[3], chnlUserMap.getChannelUserMap("LoginID",""),channelresultMap},
									 {4,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[4], chnlUserMap.getChannelUserMap("MSISDN",DBHandler.AccessHandler.existingMSISDN()),channelresultMap},
									 };
		
		return categoryData;
	}

}