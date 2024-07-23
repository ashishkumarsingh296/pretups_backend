package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelReportsUser.UserClosingBalanceSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class UserClosingBalanceSpringFeatures {

	
	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage cuHomePage;
	UserClosingBalanceSpring userClosingBalance;
	
	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;
	
	public UserClosingBalanceSpringFeatures(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		
		cuHomePage = new ChannelUserHomePage(driver);
		
		userClosingBalance = new UserClosingBalanceSpring(driver);
		
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
	}
	
	public HashMap<String, String> checkUserClosingBalanceReport(String userType,String domainCode, String... data){
		final String methodname = "checkUserClosingBalanceReport";
		Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));
		
		if(userType.equalsIgnoreCase("CHANNEL")){
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.USER_CLOSING_BALANCE,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));}
		
		caHomepage.clickChannelReportsUser();
		userClosingBalance.clickUserClosingBalancelink();
		
		if(!data[0].equals("")&&data[0]!=null)
		{userClosingBalance.selectZone(data[0]);}
		
		if(!data[1].equals("")&&data[1]!=null)
		{userClosingBalance.selectDomain(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{userClosingBalance.selectCategory(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{userClosingBalance.enterUserName(data[3]);}
		
		if(!data[4].equals("")&&data[4]!=null)	
		{userClosingBalance.enterfromDate(data[4]);}
		
		if(!data[5].equals("")&&data[5]!=null)
		{userClosingBalance.entertoDate(data[5]);}
		
		if(!data[6].equals("")&&data[6]!=null)
		{userClosingBalance.enterFromAmount(data[6]);}
		
		if(!data[7].equals("")&&data[7]!=null)
		{userClosingBalance.enterToAmount(data[7]);}
		
		if(userClosingBalance.submitBtnEnabled()){
			userClosingBalance.clicksubmitBtn();
		    ResultMap.put("submitEnabled", "true");}
		else{ResultMap.put("submitEnabled", "false");}
		
		Log.methodExit(methodname);
		return (HashMap<String, String>) ResultMap;
		
	}
	
}
