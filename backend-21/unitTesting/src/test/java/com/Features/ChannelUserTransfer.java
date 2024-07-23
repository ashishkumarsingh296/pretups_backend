package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.channelUserTransfer.ResumeChannelUserHierarchy;
import com.pageobjects.channeladminpages.channelUserTransfer.SuspendChannelUserHierarchy;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelUserTransferSubCategories;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;

public class ChannelUserTransfer {

	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	SuperAdminHomePage saHomePage;
	Map<String, String> userAccessMap;
	ChannelUserTransferSubCategories channelUserTransferSubCategories;
	SuspendChannelUserHierarchy suspendChannelUserHierarchy;
	ResumeChannelUserHierarchy resumeChannelUserHierarchy;
	public ChannelUserTransfer(WebDriver driver)
	{
		this.driver = driver;
		login1 = new Login();
		saHomePage = new SuperAdminHomePage(driver);
		caHomepage = new ChannelAdminHomePage(driver);
		channelUserTransferSubCategories = new ChannelUserTransferSubCategories(driver);
		suspendChannelUserHierarchy = new SuspendChannelUserHierarchy(driver);
		resumeChannelUserHierarchy = new ResumeChannelUserHierarchy(driver);
	}
	
	public String suspendChannelUserLoginID(HashMap<String,String> hm)
	{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_USER_TRANSFER);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	    caHomepage.clickChannelUserTransfer();
	    channelUserTransferSubCategories.clickSuspendChannelUserHierarchy();
	    
	    suspendChannelUserHierarchy.EnterLoginID(hm.get("loginID"));
	    
	    suspendChannelUserHierarchy.ClickonSubmit();
	    suspendChannelUserHierarchy.ClickonConfirm();
	    String message = suspendChannelUserHierarchy.getMessage();
	    return message;
	   
	}
	
	
	public String resumeChannelUserLoginID(HashMap<String,String> hm)
	{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_USER_TRANSFER);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	    caHomepage.clickChannelUserTransfer();
	    channelUserTransferSubCategories.clickResumeChannelUserHierarchy();
	    
	    resumeChannelUserHierarchy.EnterLoginID(hm.get("loginID"));
	    
	    resumeChannelUserHierarchy.ClickonSubmit();
	    resumeChannelUserHierarchy.ClickonConfirm();
	    String message = resumeChannelUserHierarchy.getMessage();
	    return message;
	    
	  
		
	}
	
	
	public String suspendChannelUserGeoDomain(HashMap<String,String> hm)
	{
		userAccessMap = UserAccess.getUserWithAccess(RolesI.CHANNEL_USER_TRANSFER);
		login1.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
	    caHomepage.clickChannelUserTransfer();
	    channelUserTransferSubCategories.clickSuspendChannelUserHierarchy();
	    
	    suspendChannelUserHierarchy.SelectDomain(hm.get("domainName"));
	    suspendChannelUserHierarchy.EnterOwner(hm.get("userName"));
	    suspendChannelUserHierarchy.SelectParentCategoryCode(hm.get("categoryName"));
	    
	    
	    suspendChannelUserHierarchy.ClickonSubmit();
	    suspendChannelUserHierarchy.ClickonConfirm();
	    String message = suspendChannelUserHierarchy.getMessage();
	    return message;
	   
	}
	
}
