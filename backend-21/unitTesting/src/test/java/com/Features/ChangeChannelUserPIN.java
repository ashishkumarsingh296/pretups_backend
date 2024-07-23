package com.Features;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserPage;
import com.pageobjects.channeladminpages.addchanneluser.ApproveChannelUserPage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUsersSubCategories;
import com.pageobjects.loginpages.ChangePINForNewUser;
import com.pageobjects.loginpages.ChangePasswordForNewUser;
import com.pageobjects.superadminpages.homepage.OperatorUsersSubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class ChangeChannelUserPIN extends BaseTest {
	
	ChannelAdminHomePage homePage;
	SelectNetworkPage networkPage;
	Login login;
	RandomGeneration randStr;
	ChangePasswordForNewUser changenewpwd;
	OperatorUsersSubCategories operatorSubLink;
	ApproveChannelUserPage apprvChannelUsrPage;
	ChannelUsersSubCategories channelUserSubCategories;
	AddChannelUserPage addChrUserPage;
	AddChannelUserDetailsPage addChrUserDetailsPage;
	ChangePINForNewUser changeUsrPIN;
	
	public String autoPassword = null;
	public int RowNum;
	public String NEWPASSWORD;
	HashMap<String, String> channelPINMap;
	String UserName;
	String UserName1;
	static String NewPin;
	public String autoPIN = null;
	String APPLEVEL;
	Map<String, String> userAccessMap;
	
	WebDriver driver=null;
	
	public ChangeChannelUserPIN(WebDriver driver) {
		this.driver=driver;
		homePage = new ChannelAdminHomePage(driver);
		networkPage = new SelectNetworkPage(driver);
		login = new Login();
		randStr = new RandomGeneration();
		changenewpwd = new ChangePasswordForNewUser(driver);
		operatorSubLink = new OperatorUsersSubCategories(driver);
		channelPINMap = new HashMap<String, String>();
		apprvChannelUsrPage= new ApproveChannelUserPage(driver);
		channelUserSubCategories = new ChannelUsersSubCategories(driver);
		addChrUserPage = new AddChannelUserPage(driver);
		addChrUserDetailsPage = new AddChannelUserDetailsPage(driver);
		apprvChannelUsrPage = new ApproveChannelUserPage(driver);
		changeUsrPIN = new ChangePINForNewUser(driver);
		userAccessMap = new HashMap<String, String>();
	}
	
	public HashMap<String, String> changePINafterReset(String LoginID, String MSISDN) throws IOException{
		String MasterSheetPath=_masterVO.getProperty("DataProvider");
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANGEPIN_ROLECODE); //Getting User with Access to Change First Time PIN
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickChangePIN();
		String Pin= DBHandler.AccessHandler.fetchUserPIN(LoginID,MSISDN);	
		changeUsrPIN.enterLoginIDandRemarks(LoginID);
		
		//NewPin=_masterVO.getProperty("NewPIN");
		//NewPin = randStr.randomNumeric(4);
		//String ConfirmPin=_masterVO.getProperty("ConfirmPIN");
		String ConfirmPin= _masterVO.getProperty("ResetPIN");
		
		changeUsrPIN.changePIN(Pin, ConfirmPin, ConfirmPin);
		
		int rowNo=ExcelUtility.searchStringRowNum(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET,LoginID);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		ExcelUtility.setCellData(0, ExcelI.PIN, rowNo, ConfirmPin);
		
		channelPINMap.put("changePINMsg", addChrUserDetailsPage.getActualMessage());
		return channelPINMap;
	}
	
	public HashMap<String, String> changeSelfPIN() throws IOException{
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANGESELFPIN_ROLECODE); //Getting User with Access to Change First Time PIN
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		networkPage.selectNetwork();
		homePage.clickChannelUsers();
		channelUserSubCategories.clickChangeSelfPIN();
		String MasterSheetPath=_masterVO.getProperty("DataProvider");
		NewPin=_masterVO.getProperty("ResetPIN");//isSMSPinValid();
		String ConfirmPin=NewPin;
		
		Log.info("Changing Self PIN.");
		String Pin= ExtentI.getValueofCorrespondingColumns(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.PIN,new String[]{ExcelI.LOGIN_ID}, new String[]{userAccessMap.get("LOGIN_ID")});//DBHandler.AccessHandler.fetchUserPIN(userAccessMap.get("LOGIN_ID"),"");	
		
		changeUsrPIN.changePIN(Pin, NewPin, ConfirmPin);
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		
		int rowNo=ExcelUtility.searchStringRowNum(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET,userAccessMap.get("LOGIN_ID"));
		
		ExcelUtility.setCellData(0, ExcelI.PIN, rowNo, NewPin);
		
		channelPINMap.put("changeSelfPINMsg", addChrUserDetailsPage.getActualMessage());
		
		return channelPINMap;
	}	
	
	public static String isSMSPinValid() {
		RandomGeneration randStr = new RandomGeneration();
        int j;
        char pos1 = 0;
        char pos ;
        int result =1;
        String p_smsPin = null;
        // iterates thru the p_smsId and validates that the number is neither in
        // 444444 or 123456 format 121212
        while(result!=0){
        int count=0, ctr = 0;
        p_smsPin=randStr.randomNumeric(4);
        for (int i = 0;i < p_smsPin.length(); i++) {
            pos = p_smsPin.charAt(i);

            if (i < p_smsPin.length() - 1) {
                pos1 = p_smsPin.charAt(i + 1);
            }

            j = pos1;
            if (pos == pos1) {
                count++;
            } else if (j == pos + 1 || j == pos - 1) {
                ctr++;
            }
        }

        if (count == p_smsPin.length()) {
            result = -1;Log.info("PIN is same digit: "+p_smsPin);
        } else if (ctr == (p_smsPin.length() - 1)) {
            result = 1;Log.info("PIN is consecutive: "+p_smsPin);
        } else {
            result =0;Log.info("PIN is Valid: " +p_smsPin);
        }}
        return p_smsPin;
    }

	
}
