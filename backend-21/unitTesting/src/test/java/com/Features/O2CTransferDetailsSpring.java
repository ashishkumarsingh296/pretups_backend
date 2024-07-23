package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelreportsO2C.O2CtransferdetailsSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.homepages.O2CTransferSubLink;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class O2CTransferDetailsSpring {


	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage CUHomePage;
	O2CTransferSubLink CU_O2CTransfer;
	O2CtransferdetailsSpring o2ctrfdetails;

	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;

	ChannelEnquirySubCategories channelEnqSub;

	public O2CTransferDetailsSpring(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		
		CUHomePage = new ChannelUserHomePage(driver);
		CU_O2CTransfer = new O2CTransferSubLink(driver);
		o2ctrfdetails = new O2CtransferdetailsSpring(driver);
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
	}
	
	public HashMap<String, String> checkO2CtransferReport(String userType,String domainCode, String... data){
		final String methodname = "checkO2CtransferReport";
		Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));
		
		if(userType.equalsIgnoreCase("CHANNEL")){
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.O2C_TRANSFER_DETAILS,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));}
		
		caHomepage.clickChannelTrfO2CReport();
		o2ctrfdetails.clickO2CTransferDetailslink();
		
		if(!data[0].equals("")&&data[0]!=null)
		{o2ctrfdetails.selectZone(data[0]);}
		
		if(!data[1].equals("")&&data[1]!=null)
		{o2ctrfdetails.selectDomain(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{o2ctrfdetails.selectCategory(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{o2ctrfdetails.enterUserName(data[3]);}
		
		if(!data[4].equals("")&&data[4]!=null)
		{o2ctrfdetails.selectTranserSubtype(data[4]);}
		
		if(!data[5].equals("")&&data[5]!=null)
		{o2ctrfdetails.selectTransferCategory(data[5]);}
		
		if(data[10].equalsIgnoreCase("true")){
			if(!data[6].equals("")&&data[6]!=null)	
				{o2ctrfdetails.enterfromDate(data[6]);}
			if(!data[7].equals("")&&data[7]!=null)
				{o2ctrfdetails.enterfromTime(data[7]);}
			if(!data[8].equals("")&&data[8]!=null)
				{o2ctrfdetails.entertoDate(data[8]);}
			if(!data[9].equals("")&&data[9]!=null)
				{o2ctrfdetails.entertoTime(data[9]);}
		}
		else o2ctrfdetails.checkCurrentDate();
		
		if(o2ctrfdetails.submitBtnenabled()){
		o2ctrfdetails.clicksubmitBtn();
		ResultMap.put("submitEnabled", "true");}
		else{ResultMap.put("submitEnabled", "false");}
		Log.methodExit(methodname);
		return (HashMap<String, String>) ResultMap;
	}
}
