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
import com.pageobjects.channeluserspages.c2ctransfer.C2CTransferDetailsReportSpring;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.homepages.O2CTransferSubLink;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class C2CTransferDetailsSpring {


	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage CUHomePage;
	O2CTransferSubLink CU_O2CTransfer;
	C2CTransferDetailsReportSpring c2ctrfdetails;

	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;

	ChannelEnquirySubCategories channelEnqSub;

	public C2CTransferDetailsSpring(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		
		CUHomePage = new ChannelUserHomePage(driver);
		CU_O2CTransfer = new O2CTransferSubLink(driver);
		c2ctrfdetails = new C2CTransferDetailsReportSpring(driver);
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
	}
	
	public HashMap<String, String> checkC2CtransferReport(String userType,String domainCode,String searchCriteria, String... data){
		final String methodname = "checkC2CtransferReport";
		Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));
		
		if(userType.equalsIgnoreCase("CHANNEL")){
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.C2C_TRANSFER_DETAILS,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));}
		
		CUHomePage.clickChannelTrfC2CReport();
	//	c2ctrfdetails.clickC2CTransferDetailslink();
		
		if(searchCriteria.equals("MOBILE_NO"))
		
		{
			if(!data[0].equals("")&&data[0]!=null)
		{c2ctrfdetails.selectTransferSubTypeMOB(data[0]);}
		
		if(!data[1].equals("")&&data[1]!=null)
		{c2ctrfdetails.selectTransferInOutMOB(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{c2ctrfdetails.enterFromUserMobileNumber(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{c2ctrfdetails.enterToUserMobileNumber(data[3]);}
		
		if(!data[4].equals("")&&data[4]!=null)
		{c2ctrfdetails.enterfromDateMobileNumber(data[4]);}
		
		if(!data[5].equals("")&&data[5]!=null)
		{c2ctrfdetails.enterfromTimeMobileNumber(data[5]);}
		
		if(!data[6].equals("")&&data[6]!=null)
		{c2ctrfdetails.entertoDateMobileNumber(data[6]);}
		
		if(!data[7].equals("")&&data[7]!=null)
		{c2ctrfdetails.entertoTimeMobileNumber(data[7]);}
		
		c2ctrfdetails.clicksubmitBtnMOB();
		ResultMap.put("submitEnabled", "true");
		}
		
		else if(searchCriteria.equals("USER_NAME"))
		{
			c2ctrfdetails.clickcollapseTwo();
			if(!data[0].equals("")&&data[0]!=null)
			{c2ctrfdetails.selectTransferSubTypeUSR(data[0]);}
			
			if(!data[1].equals("")&&data[1]!=null)
			{c2ctrfdetails.selectTransferInOutUSR(data[1]);}
			
			if(!data[2].equals("")&&data[2]!=null)
			{c2ctrfdetails.selectZone(data[2]);}
			
			if(!data[3].equals("")&&data[3]!=null)
			{c2ctrfdetails.selectDomain(data[3]);}
			
			if(!data[4].equals("")&&data[4]!=null)
			{c2ctrfdetails.enterfromDateUserName(data[4]);}
			
			if(!data[5].equals("")&&data[5]!=null)
			{c2ctrfdetails.enterfromTimeUserName(data[5]);}
			
			if(!data[6].equals("")&&data[6]!=null)
			{c2ctrfdetails.entertoDateUserName(data[6]);}
			
			if(!data[7].equals("")&&data[7]!=null)
			{c2ctrfdetails.entertoTimeUserName(data[7]);}
			
			if(!data[8].equals("")&&data[8]!=null)
			{c2ctrfdetails.selectSearchCategoryUserName(data[8]);}
			
			if(!data[9].equals("")&&data[9]!=null)
			{c2ctrfdetails.enterSearchUser(data[9]);}
			
			if(!data[10].equals("")&&data[10]!=null)
			{c2ctrfdetails.selectTransferUserCategoryUserName(data[10]);}
			
			if(!data[11].equals("")&&data[11]!=null)
			{c2ctrfdetails.enterTransferUser(data[11]);}
			
			c2ctrfdetails.clicksubmitBtnUSR();
			ResultMap.put("submitEnabled", "true");
		}
		
		
		return (HashMap<String, String>) ResultMap;
	}
}