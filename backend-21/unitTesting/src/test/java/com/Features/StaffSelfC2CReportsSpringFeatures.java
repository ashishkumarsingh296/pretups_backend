package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.c2c.staffselfc2creports.StaffSelfC2CReportsSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class StaffSelfC2CReportsSpringFeatures {
	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage CUHomePage;
	
	StaffSelfC2CReportsSpring staffSelfC2C;

	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;

	ChannelEnquirySubCategories channelEnqSub;
	
	
	
	public StaffSelfC2CReportsSpringFeatures(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		
		CUHomePage = new ChannelUserHomePage(driver);
	
		
		staffSelfC2C = new StaffSelfC2CReportsSpring(driver);
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
	}
	
	public HashMap<String, String> checkStaffSelfC2CReport(String userType,String domainCode, String... data) throws InterruptedException{
		final String methodname = "checkStaffSelfC2CReport";
		Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));
		
		if(userType.equalsIgnoreCase("CHANNEL")){
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.STAFF_SELF_REPRT,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));}
		
		 caHomepage.clickChannelReportsC2C();
		 caHomepage.clickChannelReportsC2C();
		 staffSelfC2C.clickStaffSelfC2CReportlink();
		
		
		
		if(!data[0].equals("")&&data[0]!=null)
		{staffSelfC2C.selectTranserSubtype(data[0]);}
		
		if(!data[1].equals("")&&data[1]!=null)
		{staffSelfC2C.enterfromDate(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{staffSelfC2C.entertoDate(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{staffSelfC2C.enterfromTime(data[3]);}
		
		if(!data[4].equals("")&&data[4]!=null)
		{staffSelfC2C.entertoTime(data[4]);}
		
		
		
		if(staffSelfC2C.submitBtnenabled()){
			staffSelfC2C.clicksubmitBtn();
		ResultMap.put("submitEnabled", "true");}
		else{ResultMap.put("submitEnabled", "false");}
		if(staffSelfC2C.inetBtnenabled()){
			staffSelfC2C.clickInetBtn();
		ResultMap.put("inetEnabled", "true");}
		else{ResultMap.put("inetEnabled", "false");}
		
		
		
		Log.methodExit(methodname);
		return (HashMap<String, String>) ResultMap;
	}
	
}
