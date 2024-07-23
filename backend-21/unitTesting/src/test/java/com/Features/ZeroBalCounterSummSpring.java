package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelreportssuser.ZeroBalCounterSummarySpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class ZeroBalCounterSummSpring {
	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage CUHomePage;
	ZeroBalCounterSummarySpring zeroBalSumm;
	

	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;

	ChannelEnquirySubCategories channelEnqSub;
	
	
	
	public ZeroBalCounterSummSpring(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		
		CUHomePage = new ChannelUserHomePage(driver);
	//	CU_O2CTransfer = new O2CTransferSubLink(driver);
		zeroBalSumm = new ZeroBalCounterSummarySpring(driver);
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
	}
	
	public HashMap<String, String> checkZeroBalSummReport(String userType,String domainCode, String... data){
		final String methodname = "checkZeroBalSummReport";
		Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));
		
		if(userType.equalsIgnoreCase("CHANNEL")){
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.ZERO_BAL_SUMMARY,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));}
		
		caHomepage.clickZeroBalSummReport();
		zeroBalSumm.clickzeroBalSummlink();
		if(!(userType.equalsIgnoreCase("CHANNEL"))){
		if(!data[0].equals("")&&data[0]!=null)
		{zeroBalSumm.selectZone(data[0]);}
		}
		if(!data[1].equals("")&&data[1]!=null)
		{zeroBalSumm.selectDomain(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{zeroBalSumm.selectCategory(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{zeroBalSumm.selectThresholdType(data[3]);}
	
		if(!data[4].equals("")&&data[4]!=null)
		{zeroBalSumm.selectDailyDate(data[4]);}
		
		if(!data[5].equals("")&&data[5]!=null)
		{zeroBalSumm.selectMonthlyDate(data[5]);}
		
		if(!data[6].equals("")&&data[6]!=null)
		{zeroBalSumm.selectDailyRadioButton(data[6]);}
		if(!data[6].equals("")&&data[6]!=null)
		{zeroBalSumm.selectMonthlyRadioButton(data[7]);}
		
		if(zeroBalSumm.submitBtnenabled()){
			zeroBalSumm.clicksubmitBtn();
		ResultMap.put("submitEnabled", "true");}
		else{ResultMap.put("submitEnabled", "false");}
		Log.methodExit(methodname);
		return (HashMap<String, String>) ResultMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
