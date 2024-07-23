package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelreportsO2C.C2STransferSpringPageObject;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.homepages.C2STransferSubLink;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class C2STransferFeatureSpring {

	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage CUHomePage;
	C2STransferSubLink CU_C2STransfer;
	C2STransferSpringPageObject c2STransferSpringPageObject;

	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;

	ChannelEnquirySubCategories channelEnqSub;

	
 	
	public C2STransferFeatureSpring(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		
		CUHomePage = new ChannelUserHomePage(driver);
		CU_C2STransfer = new C2STransferSubLink(driver);
		c2STransferSpringPageObject = new C2STransferSpringPageObject(driver);
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
	}

	public HashMap<String, String> checkC2STransferReport(String userType,String domainCode,String criteria, String... data){
		
		final String methodname = "checkC2STransferReport";
		Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));
		
		if(userType.equalsIgnoreCase("CHANNEL")){
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.C2S_TRANSFER,domainCode);
		login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));}
		
		caHomepage.clickChannelReportsC2STransfer();
		caHomepage.clickChannelReportsC2STransfer();
		
		c2STransferSpringPageObject.clickC2STransferlink();
		
		if(!data[0].equals("")&&data[0]!=null)
		{c2STransferSpringPageObject.selectService(data[0]);}
		
		if(!data[1].equals("")&&data[1]!=null)
		{c2STransferSpringPageObject.selectStatus(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)	
		{c2STransferSpringPageObject.enterDate(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{c2STransferSpringPageObject.enterfromTime(data[3]);}
		
		if(!data[4].equals("")&&data[4]!=null)
		{c2STransferSpringPageObject.entertoTime(data[4]);}
		
		if(!data[5].equals("")&&data[5]!=null)
		{c2STransferSpringPageObject.enterMsisdn(data[5]);}
		
		/*if(!data[1].equals("")&&data[1]!=null)
		{c2STransferSpringPageObject.selectDomain(data[1]);}
		
		if(!data[2].equals("")&&data[2]!=null)
		{c2STransferSpringPageObject.selectCategory(data[2]);}
		
		if(!data[3].equals("")&&data[3]!=null)
		{c2STransferSpringPageObject.enterUserName(data[3]);}*/
												
		if(c2STransferSpringPageObject.submitBtnenabled()){
		   c2STransferSpringPageObject.clicksubmitBtn();
		   ResultMap.put("submitEnabled", "true");
		 }else{ResultMap.put("submitEnabled", "false");}
		Log.methodExit(methodname);
		return (HashMap<String, String>) ResultMap;
	}
}
