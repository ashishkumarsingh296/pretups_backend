package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.lmsPages.lmsRedemptionReport.LmsRedemptionReportPageObject;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class LmsRedemptionReportSpring {
	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage CUHomePage;
	LmsRedemptionReportPageObject lmsRedemptionReport;
	

	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;

	ChannelEnquirySubCategories channelEnqSub;
	
	
	
	public LmsRedemptionReportSpring(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		
		CUHomePage = new ChannelUserHomePage(driver);
	//	CU_O2CTransfer = new O2CTransferSubLink(driver);
		lmsRedemptionReport = new LmsRedemptionReportPageObject(driver);
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
	}
	
	public HashMap<String, String> checklmsRedemptionReport(String userType,String domainCode, String... data){
		final String methodname = "checkZeroBalSummReport";
		Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));
		
		if(userType.equalsIgnoreCase("CHANNEL")){
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.LMSREDRPT,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));}
		
		caHomepage.clicklMSReport();
		lmsRedemptionReport.clickLmsRedReportLink();
		if(!(userType.equalsIgnoreCase("CHANNEL"))){
		if(!data[0].equals("")&&data[0]!=null)
		{lmsRedemptionReport.selectService(data[0]);}
		}
		if(!data[1].equals("")&&data[1]!=null)
		{lmsRedemptionReport.enterFromDate(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{lmsRedemptionReport.enterToDate(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{lmsRedemptionReport.enterMsisdn(data[3]);}
	
		
		
		if(lmsRedemptionReport.submitBtnenabled()){
			lmsRedemptionReport.clicksubmitBtn();
		ResultMap.put("submitEnabled", "true");}
		else{ResultMap.put("submitEnabled", "false");}
		Log.methodExit(methodname);
		return (HashMap<String, String>) ResultMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
