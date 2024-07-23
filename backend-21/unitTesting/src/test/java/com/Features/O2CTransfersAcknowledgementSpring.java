package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelreportO2C.O2CTransferAcknowledgementSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.homepages.O2CTransferSubLink;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class O2CTransfersAcknowledgementSpring {
	WebDriver driver;

	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage CUHomePage;
	O2CTransferSubLink CU_O2CTransfer;
	O2CTransferAcknowledgementSpring o2cTransferAck;

	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;

	ChannelEnquirySubCategories channelEnqSub;

	public O2CTransfersAcknowledgementSpring(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);
		
		CUHomePage = new ChannelUserHomePage(driver);
		CU_O2CTransfer = new O2CTransferSubLink(driver);
		o2cTransferAck = new O2CTransferAcknowledgementSpring(driver);
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
		o2cTransferAck.clickO2CTransferDetailslink();
		
		if(!data[0].equals("")&&data[0]!=null)
		{o2cTransferAck.selectTransferNumber(data[0]);}

		if(o2cTransferAck.submitBtnenabled()){
			o2cTransferAck.clicksubmitBtn();
		ResultMap.put("submitEnabled", "true");}
		else{ResultMap.put("submitEnabled", "false");}
		Log.methodExit(methodname);
		return (HashMap<String, String>) ResultMap;
	}
}
