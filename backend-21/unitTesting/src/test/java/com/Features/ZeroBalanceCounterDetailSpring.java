package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelreportsO2C.ZeroBalanceCounterDetailSpringPageObject;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.homepages.ZeroBalanceCounterDetailSubLink;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class ZeroBalanceCounterDetailSpring {

	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage CUHomePage;
	ZeroBalanceCounterDetailSubLink CU_ZeroBalanceCounterDetail;
	ZeroBalanceCounterDetailSpringPageObject zeroBalanceCounterDetailSpringPageObject;

	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;

	ChannelEnquirySubCategories channelEnqSub;

	
	
	public ZeroBalanceCounterDetailSpring(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		
		CUHomePage = new ChannelUserHomePage(driver);
		CU_ZeroBalanceCounterDetail = new ZeroBalanceCounterDetailSubLink(driver);
		zeroBalanceCounterDetailSpringPageObject = new ZeroBalanceCounterDetailSpringPageObject(driver);
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
	}

	public HashMap<String, String> checkZeroBalanceCounterDetailReport(String userType,String domainCode,String criteria, String... data){
		
		final String methodname = "checkZeroBalanceCounterDetailReport";
		Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));
		
		if(userType.equalsIgnoreCase("CHANNEL")){
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.ZERO_BALANCE_COUNTER_DETAILS,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));
		}
		
		caHomepage.clickChannelReportsUser();
		zeroBalanceCounterDetailSpringPageObject.clickZeroBalanceCounterDetailslink();
		
		if(criteria=="ByMobileno")	
		{
		if(!data[0].equals("")&&data[0]!=null)
		{zeroBalanceCounterDetailSpringPageObject.selectThreshold(data[0]);}
		
		if(!data[1].equals("")&&data[1]!=null)	
		{zeroBalanceCounterDetailSpringPageObject.enterfromDate(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{zeroBalanceCounterDetailSpringPageObject.entertoDate(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{zeroBalanceCounterDetailSpringPageObject.enterMsisdn(data[3]);}
	  }
		
		
		//for panel two
		if(criteria=="ByCategory")	
		{
		if(!data[0].equals("")&&data[0]!=null)
		{zeroBalanceCounterDetailSpringPageObject.selectThreshold(data[0]);}
		
		if(!data[1].equals("")&&data[1]!=null)	
		{zeroBalanceCounterDetailSpringPageObject.enterfromDate(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{zeroBalanceCounterDetailSpringPageObject.entertoDate(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{zeroBalanceCounterDetailSpringPageObject.selectZone(data[3]);}
		
		if(!data[4].equals("")&&data[4]!=null)
		{zeroBalanceCounterDetailSpringPageObject.selectDomain(data[4]);}
		
		if(!data[5].equals("")&&data[5]!=null)
		{zeroBalanceCounterDetailSpringPageObject.selectCategory(data[5]);}
		
		if(!data[6].equals("")&&data[6]!=null)
		{zeroBalanceCounterDetailSpringPageObject.enterUserName(data[6]);}
	 }
										
		if(zeroBalanceCounterDetailSpringPageObject.submitBtnenabled()){
		zeroBalanceCounterDetailSpringPageObject.clicksubmitBtn();
		ResultMap.put("submitEnabled", "true");}
		else{ResultMap.put("submitEnabled", "false");}
		Log.methodExit(methodname);
		return (HashMap<String, String>) ResultMap;
	}
}
