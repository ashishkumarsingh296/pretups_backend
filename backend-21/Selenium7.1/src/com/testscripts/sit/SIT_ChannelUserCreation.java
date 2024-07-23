package com.testscripts.sit;

import java.io.IOException;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.ChannelUser;
import com.Features.DeleteChannelUser;
import com.Features.mapclasses.ChannelUserMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.MasterI;
import com.commons.RolesI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils.Validator;
import com.utils._masterVO;

/**
 * @author Lokesh
 * This class is created to add Channel Users and perform all approval levels. Change first time user password and pin. 
 */
public class SIT_ChannelUserCreation extends BaseTest {
	
	static String homepage1;	
	static HashMap<String, String> map, map1 = null;
	HashMap<String, String> channelresultMap;
	HashMap<String, String> channelresultMap1;
	static boolean TestCaseCounter = false;
	HashMap<String, String> userAccessMap;
	static int minPaswdLength;
	static int maxPaswdLength;
	
	
	@Test(dataProvider = "Domain&CategoryProvider_validations")
	public void channelUserAddSIT(int CaseNum,int RowNum, String Domain, String Parent,String Category, String geotype,String Description, HashMap<String, String> mapParam) throws IOException{
	
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Channel User Creation");
			TestCaseCounter = true;
		}
		
		String remarks = "Automation deletion Remarks";
		String waterMark = "Since, web access is not allowed, hence the case is skipped.";
		
		ChannelUser channelUserLogic= new ChannelUser(driver);
		userAccessMap = (HashMap<String, String>) UserAccess.getUserWithAccess(RolesI.ADD_CHANNEL_USER_ROLECODE);
		DeleteChannelUser deleteCHNLUser = new DeleteChannelUser(driver);
		AddChannelUserDetailsPage adChnlUserDetailsPage = new AddChannelUserDetailsPage(driver);
		
		currentNode=test.createNode(Description);
		currentNode.assignCategory("SIT");
		String webAccessAllowed = DBHandler.AccessHandler.webInterface(Category);
		
		switch(CaseNum){
		case 0://To verify that channel user addition is not successful if First Name is blank
			try{channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage();
			String expected = MessagesDAO.prepareMessageByKey("errors.required", MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName"));
			Validator.messageCompare(actual, expected);}
			break;
		
		case 1://To verify that channel user addition is not successful if MSISDN is blank
			try{channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage();
			String expected = MessagesDAO.prepareMessageByKey("user.adduser.error.msisdnnotassigned");
			Validator.messageCompare(actual, expected);}
			break;
		
		case 2://To verify that channel user addition is not successful if Password is blank
			if(webAccessAllowed.equals("Y")){
			try{mapParam.put("CONFIRMPASSWORD", "");
			channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage().replaceAll("\\r|\\n", " ");
			String expected = MessagesDAO.prepareMessageByKey("viewedituser.editoperatoruser.error.webpasswordrequired")+" "+MessagesDAO.prepareMessageByKey("user.adduser.error.confirmpasswordrequired");
			Validator.messageCompare(actual, expected);}}
			else{Log.info("WebLogin is not allowed.");
			currentNode.log(Status.SKIP, waterMark);}
			break;
		
		case 3://To verify that channel user addition is not successful if geography is not assigned to user
			try{channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage();
			String expected = MessagesDAO.prepareMessageByKey("viewedituser.editoperatoruser.error.geographiesnotassigned");
			Validator.messageCompare(actual, expected);}
			break;
		
		case 4://To verify that channel user addition is not successful when phone number is not assigned to user
			try{channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage();
			String expected = MessagesDAO.prepareMessageByKey("viewedituser.editoperatoruser.error.msisdnnotassigned");
			Validator.messageCompare(actual, expected);}
			break;
			
		case 5://To verify that channel user addition is not successful if password entered is ****
			if(webAccessAllowed.equals("Y")){
			try{mapParam.put("CONFIRMPASSWORD","****");
			channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){String actual = driver.switchTo().alert().getText();driver.switchTo().alert().accept();Log.writeStackTrace(e);
			String expected = MessagesDAO.prepareMessageByKey("user.addchanneluser.label.webpassword.invalid");
			Validator.messageCompare(actual, expected);}}
			else{Log.info("WebLogin is not allowed.");
			currentNode.log(Status.SKIP, waterMark);}
			break;
		
		case 6://To validate sql injection for password, enter password as 'or 1=1'
			if(webAccessAllowed.equals("Y")){
			try{mapParam.put("CONFIRMPASSWORD","or 1=1");
			channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual= driver.findElement(By.xpath("//table[3]/tbody/tr/td/b[1]")).getText()+driver.findElement(By.xpath("//table[3]/tbody/tr/td")).getText()+driver.findElement(By.xpath("//table[3]/tbody/tr/td/b[2]")).getText();
			String expected = "SECURITY";
			Validator.partialmessageCompare(actual, expected);}}
			else{Log.info("WebLogin is not allowed.");
			currentNode.log(Status.SKIP, waterMark);}
			break;
		
		case 7://To verify that channel user addition is successful when phone number of deleted channel user is entered.
			try{channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			String actual = channelresultMap.get("channelInitiateMsg");
			
			String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
			String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String applevel = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,"USER_APPROVAL_LEVEL");
			
			String expected;
			if(applevel.equals("0"))
			{expected = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessage", mapParam.get("uName"));}	
			else{expected = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessageforrequest", mapParam.get("uName"));
			}
			Validator.messageCompare(actual, expected);
			}
			catch(Exception e){Log.writeStackTrace(e);ExtentI.attachScreenShot();}
			break;
		
		case 8://To verify that channel user addition is successful when LoginID assigned to deleted channel user prior to its deletion is entered.
			if(webAccessAllowed.equals("Y")){
			try{
			channelresultMap1 = channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype);
			
			String[] catCode = DBHandler.AccessHandler.fetchCategoryCodeAndGeographicalDomainType(Category);
			String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			String applevel = DBHandler.AccessHandler.getPreference(catCode[0],networkCode,"USER_APPROVAL_LEVEL");
			
			if(applevel.equals("2")){
			channelUserLogic.approveLevel1_ChannelUser();
			channelUserLogic.approveLevel2_ChannelUser();}
			else if(applevel.equals("1")){
			channelUserLogic.approveLevel1_ChannelUser();}
			else{Log.info("Approval not required.");}
			
			deleteCHNLUser.deletechannelUser_MSISDN(channelresultMap1.get("MSISDN"), remarks);
			deleteCHNLUser.approveDeleteChannelUser_MSISDN(channelresultMap1.get("MSISDN"), remarks);
			
			mapParam.put("LoginID", channelresultMap1.get("LOGIN_ID"));
			channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			String actual = channelresultMap.get("channelInitiateMsg");
			String expected;
			if(applevel.equals("0"))
			{expected = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessage", mapParam.get("uName"));}	
			else{expected = MessagesDAO.prepareMessageByKey("user.addchanneluser.addsuccessmessageforrequest", mapParam.get("uName"));
			}
			Validator.messageCompare(actual, expected);
			}
			catch(Exception e){Log.writeStackTrace(e);ExtentI.attachScreenShot();}
			}
			else{Log.info("Web Access is not allowed");
			currentNode.log(Status.SKIP, waterMark);}
			break;
			
		case 9://To verify that channel user addition is not successful if LoginID entered characters are less than min login id length
			if(webAccessAllowed.equals("Y")){
			try{channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage();
			String expected = "";
			Validator.messageCompare(actual, expected);}}
			else{Log.info("WebLogin is not allowed.");
			currentNode.log(Status.SKIP, waterMark);}
			break;
			
		case 10://To verify that channel user addition is not successful if LoginID entered characters are less than maximum login id length
			try{channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage();
			String expected = "";Validator.messageCompare(actual, expected);}
			break;
		
		case 11://To verify that channel user addition is not successful if Password entered characters are less than minimum password length
			if(webAccessAllowed.equals("Y")){
			try{mapParam.put("CONFIRMPASSWORD", mapParam.get("PASSWORD"));
			channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage().replaceAll("\\r|\\n", " ");
			String expected= MessagesDAO.prepareMessageByKey("operatorutil.validatepassword.error.passwordlenerr",String.valueOf(minPaswdLength),String.valueOf(maxPaswdLength))+" "+MessagesDAO.prepareMessageByKey("user.adduser.error.invalidconfirmpasswordlength",String.valueOf(minPaswdLength));
			Validator.messageCompare(actual, expected);}}
			else{Log.info("WebLogin is not allowed.");
			currentNode.log(Status.SKIP, waterMark);}
			break;
			
		case 12://To verify that channel user addition is not successful if LoginID is blank
			if(webAccessAllowed.equals("Y")){
			try{
			channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual = adChnlUserDetailsPage.getActualMessage();
			String expected= MessagesDAO.getLabelByKey("user.adduser.error.loginidrequired");
			Validator.messageCompare(actual, expected);}}
			else{Log.info("WebLogin is not allowed.");
			currentNode.log(Status.SKIP, waterMark);}
			break;
		
		case 13://To verify that channel user addition is not successful if MSISDN entered is less than minimum MSISDN length
			try{channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual=CONSTANT.CU_ASSIGNPHONENO_ERR;
			String expected = MessagesDAO.prepareMessageByKey("user.asignPhone.error.msisdn.invalid","1");
			Validator.messageCompare(actual, expected);}
			break;
			
		case 14://To verify that channel user addition screen is not visible if category is not selected
			try{
			channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain,"", "", "",mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual=adChnlUserDetailsPage.getActualMessage().replaceAll("\\r|\\n", " ");
			String expected = MessagesDAO.prepareMessageByKey("user.adduser.error.category.required")+" "+MessagesDAO.prepareMessageByKey("user.adduser.error.parentcategory.required")+" "+MessagesDAO.prepareMessageByKey("user.adduser.error.geographicaldomain.required");
			Validator.messageCompare(actual, expected);}
			break;
		
		case 15://To verify that channel user addition is not successful if Password and Confirm Password are different.
			if(webAccessAllowed.equals("Y")){
			try{channelresultMap=channelUserLogic.channelUserInitiate(RowNum, Domain, Parent, Category, geotype,mapParam);
			channelresultMap.get("channelInitiateMsg");}
			catch(Exception e){Log.writeStackTrace(e);
			String actual=adChnlUserDetailsPage.getActualMessage();
			String expected = MessagesDAO.prepareMessageByKey("user.adduser.error.confirmpasswordnotvalid");
			Validator.messageCompare(actual, expected);}}
			else{Log.info("WebLogin is not allowed.");
			currentNode.log(Status.SKIP, waterMark);}
			break;
			
		default: Log.info("No valid data found."); 
		}
		}
	
	
	@DataProvider(name = "Domain&CategoryProvider_validations")
	public Object[][] DomainCategoryProvider_validations() {
	
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "Channel Users Hierarchy");
		
		ChannelUserMap chnlUserMap = new ChannelUserMap();
		RandomGeneration randGen = new RandomGeneration();
		int rowNum=1;
		String loginID=null;
		String mobileNumber=null;
		
		String[] userDetailsHL = new String[5];
		
		userDetailsHL[0] = ExcelUtility.getCellData(0, "DOMAIN_NAME", 1);
		userDetailsHL[1] = ExcelUtility.getCellData(0, "PARENT_CATEGORY_NAME", 1);
		userDetailsHL[2] = ExcelUtility.getCellData(0, "CATEGORY_NAME", 1);
		userDetailsHL[3] = ExcelUtility.getCellData(0, "GRPH_DOMAIN_TYPE", 1);	
	
		String[] description=new String[16];
		description[0]="To verify that channel user addition is not successful if First Name is blank";
		description[1]="To verify that channel user addition is not successful if MSISDN is blank";
		description[2]="To verify that channel user addition is not successful if Password is blank";
		description[3]="To verify that channel user addition is not successful if geography is not assigned to user";
		description[4]="To verify that channel user addition is not successful when phone number is not assigned to user";
		description[5]="To verify that channel user addition is not successful if password entered is **** ";
		description[6]="To validate sql injection for password, enter password as 'or 1=1'";
		description[7]="To verify that channel user addition is successful when phone number of deleted channel user is entered.";
		description[8]="To verify that channel user addition is successful when LoginID assigned to deleted channel user prior to its deletion is entered.";
		description[9]="To verify that channel user addition is not successful if LoginID entered characters are less than min login id length";
		description[10]="To verify that channel user addition is not successful if LoginID entered characters are less than maximum login id length";
		description[11]="To verify that channel user addition is not successful if Password entered characters are less than minimum password length";
		description[12]="To verify that channel user addition is not successful if LoginID is blank";
		description[13]="To verify that channel user addition is not successful if MSISDN entered is less than minimum MSISDN length";
		description[14]="To verify that channel user addition screen is not visible if Category is not selected.";
		description[15]="To verify that channel user addition is not successful if Password and Confirm Password are different.";
		
		mobileNumber=DBHandler.AccessHandler.deletedMSISDN();
		 minPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_LOGIN_PWD_LENGTH"));
		 maxPaswdLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MAX_LOGIN_PWD_LENGTH"));
		int minMSISDNLength =Integer.parseInt(DBHandler.AccessHandler.getSystemPreference("MIN_MSISDN_LENGTH"));
		int remainingMSISDN = minMSISDNLength-SystemPreferences.MSISDN_PREFIX_LENGTH-1;
		
		String minPaswd = generatePassword();
		
		String prefix = _masterVO.getMasterValue("Prepaid MSISDN Prefix");
		String minMSISDN = prefix + randGen.randomNumberWithoutZero(remainingMSISDN);
		
		Object[][] categoryData;
		
				   categoryData = new Object[][]{{0,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[0], chnlUserMap.getChannelUserMap("fName", "")},
												 {1,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[1], chnlUserMap.getChannelUserMap("MSISDN", "")},
												 {2,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[2],chnlUserMap.getChannelUserMap("PASSWORD","")},
												 {3,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[3],chnlUserMap.getChannelUserMap("assignGeography","N")},
												 {4,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[4],chnlUserMap.getChannelUserMap("assgnPhoneNumber","N")},
												 {5,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[5],chnlUserMap.getChannelUserMap("PASSWORD","****")},
												 {6,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[6],chnlUserMap.getChannelUserMap("PASSWORD","or 1=1")},
												 {7,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[7],chnlUserMap.getChannelUserMap("MSISDN",mobileNumber)},
												 {8,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[8],chnlUserMap.getChannelUserMap("LoginID",loginID)},
												 {9,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[9],chnlUserMap.getChannelUserMap("LoginID",loginID)},
												 {10,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[10],chnlUserMap.getChannelUserMap("LoginID",loginID)},
												 {11,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[11],chnlUserMap.getChannelUserMap("PASSWORD",minPaswd)},
												 {12,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[12],chnlUserMap.getChannelUserMap("LoginID","")},
												 {13,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[13],chnlUserMap.getChannelUserMap("MSISDN",minMSISDN)},
												 {14,rowNum,userDetailsHL[0],userDetailsHL[1],"",userDetailsHL[3],description[14],chnlUserMap.getChannelUserMap("LoginID","")},
												 {15,rowNum,userDetailsHL[0],userDetailsHL[1],userDetailsHL[2],userDetailsHL[3],description[15],chnlUserMap.getChannelUserMap("CONFIRMPASSWORD","@"+_masterVO.getProperty("ConfirmPassword"))}
												 };
		
		return categoryData;
	}

	public static String generatePassword() {
		RandomGeneration randStr = new RandomGeneration();
        int j;
        char pos1 = 0;
        char pos ;
        int result =1;
        String passwordGen = null;
        // iterates thru the p_smsId and validates that the number is neither in
        // 444444 or 123456 format 121212
        while(result!=0){
        int count=0, ctr = 0;
        passwordGen=randStr.randomAlphaNumeric(minPaswdLength-1);
        for (int i = 0;i < passwordGen.length(); i++) {
            pos = passwordGen.charAt(i);

            if (i < passwordGen.length() - 1) {
                pos1 = passwordGen.charAt(i + 1);
            }

            j = pos1;
            if (pos == pos1) {
                count++;
            } else if (j == pos + 1 || j == pos - 1) {
                ctr++;
            }
        }

        if (count == passwordGen.length()) {
            result = -1;Log.info("Password is same digit: "+passwordGen);
        } else if (ctr == (passwordGen.length() - 1)) {
            result = 1;Log.info("Password is consecutive: "+passwordGen);
        } else {
            result =0;Log.info("Password is Valid: " +passwordGen);
        }}
        return passwordGen;
    }
	
}