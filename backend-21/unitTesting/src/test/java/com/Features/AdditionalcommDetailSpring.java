package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.adddetails.AdditionalCommDetailSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class AdditionalcommDetailSpring {
	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage CUHomePage;
	AdditionalCommDetailSpring addDetail;
	

	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;

	ChannelEnquirySubCategories channelEnqSub;
	
	
	
	public AdditionalcommDetailSpring(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		
		CUHomePage = new ChannelUserHomePage(driver);
	//	CU_O2CTransfer = new O2CTransferSubLink(driver);
		addDetail = new AdditionalCommDetailSpring(driver);
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
	}
	
	public HashMap<String, String> checkAdditionalCommDetailReport(String userType,String domainCode,String criteria,String... data){
		final String methodname = "checkAdditionalCommDetailReport";
		Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));
		
		if(userType.equalsIgnoreCase("CHANNEL")){
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.ADDITIONAL_COMMN_DETAIL,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));}
		
		caHomepage.clickAdditionalCommDetailReport();
		addDetail.clickaddCommDetailLink();
	if(criteria=="ByMobileno")	
	{
		if(!data[0].equals("")&&data[0]!=null)
		{addDetail.selectCurrentDate1(data[0]);}
		
		if(!data[1].equals("")&&data[1]!=null)
		{addDetail.selectfromTime(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{addDetail.selecttoTime(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{addDetail.selectmsisdn(data[3]);}
		
		
		if(addDetail.submitBtnenabled()){
			addDetail.clicksubmitBtn();
		ResultMap.put("submitEnabled", "true");}
		else{ResultMap.put("submitEnabled", "false");}
	}

	if(criteria=="ByCategory")	
	{
		addDetail.choosePanelCategory();
	if(!data[0].equals("")&&data[0]!=null)
		{addDetail.selectCurrentDate(data[0]);}
		
		if(!data[1].equals("")&&data[1]!=null)
		{addDetail.selectfromTimecat(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{addDetail.selecttoTimecat(data[2]);}
		
		if(!(userType.equalsIgnoreCase("CHANNEL"))){
			if(!data[3].equals("")&&data[3]!=null)
			{addDetail.selectZone(data[3]);}
			
			if(!data[4].equals("")&&data[4]!=null)
			{addDetail.selectDomain(data[4]);}
		}
		
				
		if(!data[5].equals("")&&data[5]!=null)
		{addDetail.selectCategory(data[5]);}
		
		if(data[5] != "ALL"){
			if(!data[6].equals("")&&data[6]!=null)
			{addDetail.selectUser(data[6]);}
		}
		
		
		if(addDetail.submituserBtnenabled()){
			addDetail.clicksubmituserBtn();
		ResultMap.put("submitEnabled", "true");}
		else{ResultMap.put("submitEnabled", "false");}
		
	}
		
		Log.methodExit(methodname);
		return (HashMap<String, String>) ResultMap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
