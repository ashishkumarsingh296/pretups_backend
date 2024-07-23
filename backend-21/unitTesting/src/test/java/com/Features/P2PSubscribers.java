package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.customercarepages.homepage.CustomerCarehomepage;
import com.pageobjects.customercarepages.p2psubscriberpages.DeRegisterSubscriber;
import com.pageobjects.customercarepages.p2psubscriberpages.P2PPinMgmt;
import com.pageobjects.customercarepages.p2psubscriberpages.ResumeServices;
import com.pageobjects.customercarepages.p2psubscriberpages.SuspendServices;

public class P2PSubscribers {

	WebDriver driver;
	Login login;
	Map<String, String> userAccessMap;
	P2PPinMgmt p2ppinmgmt;
	DeRegisterSubscriber dereg;
	CustomerCarehomepage cceHome;
	SuspendServices suspendserv;
	ResumeServices resumeserv;
	
	public P2PSubscribers(WebDriver driver){
		this.driver=driver;
		login = new Login();
		userAccessMap = new HashMap<String, String>();
		p2ppinmgmt = new P2PPinMgmt(driver);
		dereg = new DeRegisterSubscriber(driver);
		cceHome = new CustomerCarehomepage(driver);
		suspendserv = new SuspendServices(driver);
		resumeserv = new ResumeServices(driver);
	}
	
	public void p2pSendPin(String subsMsisdn){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.UNBLOCKPIN);
		new Login().LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		cceHome.clickP2PSubscribersLink();
		cceHome.clickP2PPinMgmtLink();
		p2ppinmgmt.enterMSISDN(subsMsisdn);
		p2ppinmgmt.clicksubmitBtn();
		p2ppinmgmt.clickSendPinBtn();
		driver.switchTo().alert().accept();
	}
	
	public void p2pResetPin(String subsMsisdn){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.UNBLOCKPIN);
		new Login().LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		
		cceHome.clickP2PSubscribersLink();
		cceHome.clickP2PPinMgmtLink();
		p2ppinmgmt.enterMSISDN(subsMsisdn);
		p2ppinmgmt.clicksubmitBtn();
		p2ppinmgmt.clickResetPinBtn();
		driver.switchTo().alert().accept();
	}
	
	public void p2pderegistersubscriber(String subsMsisdn){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.DELETEREGSUBSCRIBER);
		new Login().LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	
		cceHome.clickP2PSubscribersLink();
		cceHome.clickP2PDeregisterSubsLink();
		dereg.enterMSISDN(subsMsisdn);
		dereg.clicksubmitBtn();
		dereg.clickDeRegisterBtn();
	}
	
	public void suspendService(String subsMsisdn){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.SUSPENDSERVICE);
		new Login().LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	
		cceHome.clickP2PSubscribersLink();
		cceHome.clickP2PSuspendServiceLink();
		suspendserv.enterMSISDN(subsMsisdn);
		suspendserv.clicksubmitBtn();
		suspendserv.clickSuspendBtn();
	}
	
	public void resumeService(String subsMsisdn){
		userAccessMap = UserAccess.getUserWithAccess(RolesI.RESUMESERVICE);
		new Login().LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	
		cceHome.clickP2PSubscribersLink();
		cceHome.clickP2PResumeServiceLink();
		resumeserv.enterMSISDN(subsMsisdn);
		resumeserv.clicksubmitBtn();
		resumeserv.clickResumeBtn();
		}
}
