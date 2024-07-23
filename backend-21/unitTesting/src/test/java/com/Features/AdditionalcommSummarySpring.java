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
import com.pageobjects.channeluserspages.channelReportC2S.AdditionalCommSummaryFirstPageSpring;
import com.pageobjects.channeluserspages.homepages.ChannelReportsC2SSubLinksPage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class AdditionalcommSummarySpring {
	
	WebDriver driver;
	Login login1;
	ChannelAdminHomePage caHomepage;
	ChannelUserHomePage CUHomePage;
	AdditionalCommSummaryFirstPageSpring additionalCommSummaryFirstPageSpring;
	SelectNetworkPage ntwrkPage;
	Map<String, String> userInfo;
	Map<String, String> ResultMap;
	ChannelEnquirySubCategories channelEnqSub;
	ChannelReportsC2SSubLinksPage channelReportsC2SSubLinksPage;



	public AdditionalcommSummarySpring(WebDriver driver) {
		this.driver = driver;
		login1 = new Login();
		caHomepage = new ChannelAdminHomePage(driver);

		CUHomePage = new ChannelUserHomePage(driver);
		additionalCommSummaryFirstPageSpring = new AdditionalCommSummaryFirstPageSpring(driver);
		channelEnqSub = new ChannelEnquirySubCategories(driver);
		ntwrkPage = new SelectNetworkPage(driver);
		userInfo= new HashMap<String, String>();
		ResultMap = new HashMap<String, String>();
		channelReportsC2SSubLinksPage =  new ChannelReportsC2SSubLinksPage(driver);
	}

	public HashMap<String, String> checkAdditionalCommDetailSummary(String userType,String domainCode,String criteria,String... data){
		final String methodname = "checkAdditionalCommDetailReport";
		Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));

		if("CHANNEL".equalsIgnoreCase(userType)){
			Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.ADDITIONAL_COMMN_SUMMARY,domainCode);
			login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));}

		caHomepage.clickAdditionalCommDetailReport();
		channelReportsC2SSubLinksPage.clickAddCommSmryRptSpring();

		if(!data[0].equals("")&&data[0]!=null)
		{additionalCommSummaryFirstPageSpring.selectCategory(data[0]);}

		if(!data[1].equals("")&&data[1]!=null)
		{additionalCommSummaryFirstPageSpring.selectServiceType(data[1]);}

		if("DAILY".equalsIgnoreCase(criteria)){
			additionalCommSummaryFirstPageSpring.selectDailyRadio();
			if(!data[2].equals("")&&data[2]!=null)
			{additionalCommSummaryFirstPageSpring.selectFromDate(data[2]);}

			if(!data[3].equals("")&&data[3]!=null)
			{additionalCommSummaryFirstPageSpring.selectToDate(data[3]);}
		}

		if("MONTHLY".equalsIgnoreCase(criteria)){
			additionalCommSummaryFirstPageSpring.selectMonthlyRadio();
			if(!data[4].equals("")&&data[2]!=null)
			{additionalCommSummaryFirstPageSpring.enterFromMonth(data[4]);}

			if(!data[5].equals("")&&data[3]!=null)
			{additionalCommSummaryFirstPageSpring.enterToMonth(data[5]);}
		}



		if(additionalCommSummaryFirstPageSpring.submitBtnEnabled()){
			additionalCommSummaryFirstPageSpring.clickSubmitBtn();
			ResultMap.put("submitEnabled", "true");}
		else{ResultMap.put("submitEnabled", "false");}

		Log.methodExit(methodname);
		return (HashMap<String, String>) ResultMap;
	}
}
