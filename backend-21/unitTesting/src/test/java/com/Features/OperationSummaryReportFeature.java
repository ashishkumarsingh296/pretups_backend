package com.Features;


	import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.ChannelReportsSummary.OperationSummaryReportSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

	public class OperationSummaryReportFeature {


		WebDriver driver;

		Login login1;
		ChannelAdminHomePage caHomepage;
		ChannelUserHomePage CUHomePage;
		//O2CTransferSubLink CU_O2CTransfer;
		OperationSummaryReportSpring operationSummaryReport;

		SelectNetworkPage ntwrkPage;
		Map<String, String> userInfo;
		Map<String, String> ResultMap;

		ChannelEnquirySubCategories channelEnqSub;

		public OperationSummaryReportFeature(WebDriver driver) {
			this.driver = driver;
			login1 = new Login();
			caHomepage = new ChannelAdminHomePage(driver);
			
			CUHomePage = new ChannelUserHomePage(driver);
			//CU_O2CTransfer = new O2CTransferSubLink(driver);
			operationSummaryReport = new OperationSummaryReportSpring(driver);
			channelEnqSub = new ChannelEnquirySubCategories(driver);
			ntwrkPage = new SelectNetworkPage(driver);
			userInfo= new HashMap<String, String>();
			ResultMap = new HashMap<String, String>();
		}
		
		public HashMap<String, String> checkOperationSummaryReport(String userType,String domainCode, String... data){
			final String methodname = "checkOperationSummaryReport";
			Log.methodEntry(methodname,userType,domainCode,ReflectionToStringBuilder.toString(data));
			
			if(userType.equalsIgnoreCase("CHANNEL")){
			Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.OPERATION_SUMMARY_REPORT,domainCode);
			login1.LoginAsUser(driver, String.valueOf(login[0][0]), String.valueOf(login[0][1]));}
			
			caHomepage.clickChannelReportsSummary();
			//operationSummaryReport.clickOperationSummaryReportlink();
			
			if(!data[0].equals("")&&data[0]!=null)
			{operationSummaryReport.selectZone(data[0]);}
			
			if(!data[1].equals("")&&data[1]!=null)
			{operationSummaryReport.selectDomain(data[1]);}
			
			if(!data[2].equals("")&&data[2]!=null)
			{operationSummaryReport.selectCategory(data[2]);}
			
			if(!data[3].equals("")&&data[3]!=null)
			{operationSummaryReport.enterUserName(data[3]);}
			
			if(!data[4].equals("")&&data[4]!=null)
			{operationSummaryReport.selectMainRadioButton(data[4]);}
			
			if(!data[5].equals("")&&data[5]!=null)
			{operationSummaryReport.selectTotalRadioButton(data[5]);}
			
           if(!data[6].equals("")&&data[6]!=null)	
			{operationSummaryReport.enterfromDate(data[6]);}
				
			if(!data[7].equals("")&&data[7]!=null)
			{operationSummaryReport.entertoDate(data[7]);}
				
			
			
			
			if(operationSummaryReport.submitBtnEnabled()){
				operationSummaryReport.clicksubmitBtn();
			ResultMap.put("submitEnabled", "true");}
			else{ResultMap.put("submitEnabled", "false");}
			Log.methodExit(methodname);
			return (HashMap<String, String>) ResultMap;
		}
	}


