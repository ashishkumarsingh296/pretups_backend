package com.Features;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.autoC2Ccreditlimit.AutoC2Cpage;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;

public class AutoC2Ccreditlimit {

	AutoC2Cpage autoc2cpage;
	ChannelAdminHomePage chadminhomepage;
	SelectNetworkPage selectnetwork;
	Map<String,String> userAccessMap;
	Login login1;
	
	WebDriver driver;
	public AutoC2Ccreditlimit(WebDriver driver){
		this.driver=driver;
		autoc2cpage=new AutoC2Cpage(driver);
		chadminhomepage=new ChannelAdminHomePage(driver);
		selectnetwork=new SelectNetworkPage(driver);
		login1=new Login();
	}
	
	
	public void performautoc2ccreditlimit(String msisdn, String amount, String... count){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.AUTO_C2C_CREDIT_LIMIT_ROLECODE); 
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectnetwork.selectNetwork();
		chadminhomepage.clickMasters();
		autoc2cpage.clickAutoC2Clink();
		autoc2cpage.selectAssociationmode();
		autoc2cpage.clicksubmit();
		autoc2cpage.enterMSISDN(msisdn);
		autoc2cpage.clickaddmodifybtn1();
		autoc2cpage.selectAutoC2CallowedY();
		autoc2cpage.enterMaxTxnAmt(amount);
		autoc2cpage.enterDailyCount(count[0]);
		autoc2cpage.enterWeeklyCount(count[1]);
		autoc2cpage.enterMonthlyCount(count[2]);
		autoc2cpage.clickaddmodifybtn2();
		autoc2cpage.clickconfirm();
	}
	
}
