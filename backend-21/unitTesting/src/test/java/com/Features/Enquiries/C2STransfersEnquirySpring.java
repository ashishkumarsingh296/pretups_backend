package com.Features.Enquiries;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import com.classes.GetScreenshot;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.channelenquiries.C2STransferEnquiryPageSpring;
import com.pageobjects.channeladminpages.channelenquiries.C2STransfersPageSpring;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;

public class C2STransfersEnquirySpring {
	WebDriver driver;
	ChannelAdminHomePage HomePage;
	ChannelEnquirySubCategories ChannelEnquirySubCategory;
	Login login;
	SelectNetworkPage networkPage;
	C2STransfersPageSpring c2SEnquiry;
	C2STransferEnquiryPageSpring c2sEnqyuiryPage;
	SoftAssert sAssert = new SoftAssert();
	Map<String, String> userAccessMap = new HashMap<String, String>();

	public C2STransfersEnquirySpring(WebDriver driver) {
		this.driver = driver;
		// Page Initialization
		HomePage = new ChannelAdminHomePage(driver);
		login = new Login();
		ChannelEnquirySubCategory = new ChannelEnquirySubCategories(driver);
		networkPage = new SelectNetworkPage(driver);
		c2SEnquiry = new C2STransfersPageSpring(driver);
		c2sEnqyuiryPage =  new C2STransferEnquiryPageSpring(driver);
	}

	public boolean isNumeric(String str){
		for (char c : str.toCharArray())
		{
			if (!Character.isDigit(c)) return false;
		}
		return true;
	}

	public Map<String,String> validateC2STransfersEnquiry(Map<String, String> mapParam, String searchCriteria) throws InterruptedException {

		Map<String, String> resultMap = new HashMap<String, String>();

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_TRANSFERS_ENQUIRY_ROLECODE);
		login.UserLogin(driver, "ChannelUser",mapParam.get("category"));
		networkPage.selectNetwork();
		HomePage.clickChannelEnquiry();
		HomePage.clickChannelEnquiry();
		ChannelEnquirySubCategory.clickC2STransfersEnquiry();

		if (searchCriteria == "transferID") {
			String service = mapParam.get("service");
			String fromDate = mapParam.get("fromDate");
			String transferID = mapParam.get("transferID");
			String toDate = mapParam.get("toDate");
			if(transferID.equals(null) || transferID.equals("")){
				c2SEnquiry.selectServiceTypeTransferId(service);
				c2SEnquiry.enterFromDateTransferID(fromDate);
				c2SEnquiry.enterToDateTransferID(toDate);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorTransferID());
				return resultMap;
			}

			if(fromDate.equals(null) || fromDate.equals("")){
				c2SEnquiry.selectServiceTypeTransferId(service);
				c2SEnquiry.enterToDateTransferID(toDate);
				c2SEnquiry.entertransferID(transferID);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorFromDateTransferId());
				return resultMap;
			}

			if(toDate.equals(null) || toDate.equals("")){
				c2SEnquiry.selectServiceTypeTransferId(service);
				c2SEnquiry.enterFromDateTransferID(fromDate);
				c2SEnquiry.entertransferID(transferID);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorToDateTransferId());
				return resultMap;
			}

			if(service.equals("Select")){
				c2SEnquiry.enterFromDateTransferID(fromDate);
				c2SEnquiry.enterToDateTransferID(toDate);
				c2SEnquiry.entertransferID(transferID);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorServiceTypeTransferId());
				return resultMap;
			}
			c2SEnquiry.selectServiceTypeTransferId(service);
			c2SEnquiry.enterFromDateTransferID(fromDate);
			c2SEnquiry.enterToDateTransferID(toDate);
			c2SEnquiry.entertransferID(transferID);
			c2SEnquiry.clickSubmitTransferID();
			String screenshot = GetScreenshot.getFullScreenshot(driver);
			resultMap.put("screenshot", screenshot);
			c2sEnqyuiryPage.prepareC2SEnquiryValuesByTransferID(transferID, mapParam.get("senderMSISDN"), mapParam.get("receiverMSISDN"));
			return resultMap;

		} 
		else if (searchCriteria == "senderMsisdn") {
			c2SEnquiry.choosePanelSenderMsisdn();
			Thread.sleep(1000);
			String senderMsisdn = mapParam.get("senderMSISDN");
			String fromDate = mapParam.get("fromDate");
			String toDate = mapParam.get("toDate");
			String service = mapParam.get("service");
			if(senderMsisdn == null|| senderMsisdn.equals("") || !isNumeric(senderMsisdn)){
				c2SEnquiry.selectServiceTypeSenderMsisdn(service);
				c2SEnquiry.enterFromDateSenderMsisdn(fromDate);
				c2SEnquiry.enterToDateSenderMsisdn(toDate);
				if(!isNumeric(senderMsisdn))
				c2SEnquiry.enterSenderMsisdn(senderMsisdn);	
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorSenderMsisdn());
				return resultMap;
			}
			
			if(fromDate.equals(null) || fromDate.equals("")){
				c2SEnquiry.selectServiceTypeSenderMsisdn(service);
				c2SEnquiry.enterToDateSenderMsisdn(toDate);
				c2SEnquiry.enterSenderMsisdn(senderMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorFromDateSenderMsisdn());
				return resultMap;
			}
		
			if(toDate.equals(null) || toDate.equals("")){
				c2SEnquiry.selectServiceTypeSenderMsisdn(service);
				c2SEnquiry.enterFromDateSenderMsisdn(fromDate);
				c2SEnquiry.enterSenderMsisdn(senderMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorToDateSenderMsisdn());
				return resultMap;
			}
			
			if(service.equals("Select")){
				c2SEnquiry.enterFromDateSenderMsisdn(fromDate);
				c2SEnquiry.enterToDateSenderMsisdn(toDate);
				c2SEnquiry.enterSenderMsisdn(senderMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorServiceTypeSenderMsisdn());
				return resultMap;
			}
			c2SEnquiry.selectServiceTypeSenderMsisdn(service);
			c2SEnquiry.enterFromDateSenderMsisdn(fromDate);
			c2SEnquiry.enterToDateSenderMsisdn(toDate);
			c2SEnquiry.enterSenderMsisdn(senderMsisdn);
			c2SEnquiry.clickSubmitSenderMsisdn();
			String screenshot = GetScreenshot.getFullScreenshot(driver);
			resultMap.put("screenshot", screenshot);
			c2sEnqyuiryPage.prepareC2SEnquiryValuesBySenderMSISDN(senderMsisdn, mapParam.get("transferID"), mapParam.get("receiverMSISDN"));
			return resultMap;
		} 
		else if (searchCriteria == "receiverMsisdn") {
			c2SEnquiry.choosePanelReceiverMsisdn();
			Thread.sleep(1000);
			String receiverMsisdn = mapParam.get("receiverMSISDN");
			String toDate = mapParam.get("toDate");
			String fromDate = mapParam.get("fromDate");
			String service = mapParam.get("service");
			if(receiverMsisdn.equals(null) || receiverMsisdn.equals("") || !isNumeric(receiverMsisdn)){
				c2SEnquiry.selectServiceTypeReceiverMsisdn(service);
				c2SEnquiry.enterFromDateReceiverMsisdn(fromDate);
				c2SEnquiry.enterToDateReceiverMsisdn(toDate);
				if(!isNumeric(receiverMsisdn))
				c2SEnquiry.enterReceiverMsisdn(receiverMsisdn);	
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorReceiverMsisdn());
				return resultMap;
			}
		
			if(fromDate.equals(null) || fromDate.equals("")){
				c2SEnquiry.selectServiceTypeReceiverMsisdn(service);
				c2SEnquiry.enterToDateReceiverMsisdn(toDate);
				c2SEnquiry.enterReceiverMsisdn(receiverMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorFromDateReceiverMsisdn());
				return resultMap;
			}
		
			if(toDate.equals(null) || toDate.equals("")){
				c2SEnquiry.selectServiceTypeReceiverMsisdn(service);
				c2SEnquiry.enterFromDateReceiverMsisdn(fromDate);
				c2SEnquiry.enterReceiverMsisdn(receiverMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorToDateReceiverMsisdn());
				return resultMap;
			}
			
			if(service.equals("Select")){
				c2SEnquiry.enterFromDateReceiverMsisdn(fromDate);
				c2SEnquiry.enterToDateReceiverMsisdn(toDate);
				c2SEnquiry.enterReceiverMsisdn(receiverMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorServiceTypeReceiverMsisdn());
				return resultMap;
			}
			c2SEnquiry.selectServiceTypeReceiverMsisdn(service);
			c2SEnquiry.enterFromDateReceiverMsisdn(fromDate);
			c2SEnquiry.enterToDateReceiverMsisdn(toDate);
			c2SEnquiry.enterReceiverMsisdn(receiverMsisdn);
			c2SEnquiry.clickSubmitReceiverMsisdn();
			String screenshot = GetScreenshot.getFullScreenshot(driver);
			resultMap.put("screenshot", screenshot);
			c2sEnqyuiryPage.prepareC2SEnquiryValuesByReceiverID(receiverMsisdn, mapParam.get("senderMSISDN"), mapParam.get("transferID"));
			return resultMap;
		}

		String Screenshot = GetScreenshot.getFullScreenshot(driver);
		resultMap.put("screenshot", Screenshot);
		return resultMap;
	}


	public Map<String,String> validateC2STransfersEnquiryForOperatorUser(Map<String, String> mapParam, String searchCriteria) throws InterruptedException {

		Map<String, String> resultMap = new HashMap<String, String>();

		userAccessMap = UserAccess.getUserWithAccess(RolesI.C2S_TRANSFERS_ENQUIRY_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		networkPage.selectNetwork();
		HomePage.clickChannelEnquiry();
		HomePage.clickChannelEnquiry();
		ChannelEnquirySubCategory.clickC2STransfersEnquiry();

		if (searchCriteria == "transferID") {
			String transferID = mapParam.get("transferID");
			String fromDate = mapParam.get("fromDate");
			String toDate = mapParam.get("toDate");
			String service = mapParam.get("service");
			if(transferID.equals(null) || transferID.equals("")){
				c2SEnquiry.selectServiceTypeTransferId(service);
				c2SEnquiry.enterFromDateTransferID(fromDate);
				c2SEnquiry.enterToDateTransferID(toDate);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorTransferID());
				return resultMap;
			}

			if(fromDate.equals(null) || fromDate.equals("")){
				c2SEnquiry.selectServiceTypeTransferId(service);
				c2SEnquiry.enterToDateTransferID(toDate);
				c2SEnquiry.entertransferID(transferID);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorFromDateTransferId());
				return resultMap;
			}

			if(toDate.equals(null) || toDate.equals("")){
				c2SEnquiry.selectServiceTypeTransferId(service);
				c2SEnquiry.enterFromDateTransferID(fromDate);
				c2SEnquiry.entertransferID(transferID);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorToDateTransferId());
				return resultMap;
			}

			if(service.equals("Select")){
				c2SEnquiry.enterFromDateTransferID(fromDate);
				c2SEnquiry.enterToDateTransferID(toDate);
				c2SEnquiry.entertransferID(transferID);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorServiceTypeTransferId());
				return resultMap;
			}
			c2SEnquiry.selectServiceTypeTransferId(service);
			c2SEnquiry.enterFromDateTransferID(fromDate);
			c2SEnquiry.enterToDateTransferID(toDate);
			c2SEnquiry.entertransferID(transferID);
			c2SEnquiry.clickSubmitTransferID();
			String screenshot = GetScreenshot.getFullScreenshot(driver);
			resultMap.put("screenshot", screenshot);
			c2sEnqyuiryPage.prepareC2SEnquiryValuesByTransferIDforOpUser(transferID, mapParam.get("senderMSISDN"), mapParam.get("receiverMSISDN"));
			return resultMap;

		} 
		else if (searchCriteria == "senderMsisdn") {
			c2SEnquiry.choosePanelSenderMsisdn();
			Thread.sleep(1000);
			String senderMsisdn = mapParam.get("senderMSISDN");
			String fromDate = mapParam.get("fromDate");
			String toDate = mapParam.get("toDate");
			String service = mapParam.get("service");
			if(senderMsisdn == null|| senderMsisdn.equals("") || !isNumeric(senderMsisdn)){
				c2SEnquiry.selectServiceTypeSenderMsisdn(service);
				c2SEnquiry.enterFromDateSenderMsisdn(fromDate);
				c2SEnquiry.enterToDateSenderMsisdn(toDate);
				if(!isNumeric(senderMsisdn))
				c2SEnquiry.enterSenderMsisdn(senderMsisdn);	
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorSenderMsisdn());
				return resultMap;
			}
			
			if(fromDate.equals(null) || fromDate.equals("")){
				c2SEnquiry.selectServiceTypeSenderMsisdn(service);
				c2SEnquiry.enterToDateSenderMsisdn(toDate);
				c2SEnquiry.enterSenderMsisdn(senderMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorFromDateSenderMsisdn());
				return resultMap;
			}
			
			if(toDate.equals(null) || toDate.equals("")){
				c2SEnquiry.selectServiceTypeSenderMsisdn(service);
				c2SEnquiry.enterFromDateSenderMsisdn(fromDate);
				c2SEnquiry.enterSenderMsisdn(senderMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorToDateSenderMsisdn());
				return resultMap;
			}
			
			if(service.equals("Select")){
				c2SEnquiry.enterFromDateSenderMsisdn(fromDate);
				c2SEnquiry.enterToDateSenderMsisdn(toDate);
				c2SEnquiry.enterSenderMsisdn(senderMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorServiceTypeSenderMsisdn());
				return resultMap;
			}
			c2SEnquiry.selectServiceTypeSenderMsisdn(service);
			c2SEnquiry.enterFromDateSenderMsisdn(fromDate);
			c2SEnquiry.enterToDateSenderMsisdn(toDate);
			c2SEnquiry.enterSenderMsisdn(senderMsisdn);
			c2SEnquiry.clickSubmitSenderMsisdn();
			String screenshot = GetScreenshot.getFullScreenshot(driver);
			resultMap.put("screenshot", screenshot);
			c2sEnqyuiryPage.prepareC2SEnquiryValuesBySenderMSISDNforOpUser(senderMsisdn, mapParam.get("transferID"), mapParam.get("receiverMSISDN"));
			return resultMap;
		} 
		else if (searchCriteria == "receiverMsisdn") {
			c2SEnquiry.choosePanelReceiverMsisdn();
			Thread.sleep(1000);
			String receiverMsisdn = mapParam.get("receiverMSISDN");
			String fromDate = mapParam.get("fromDate");
			String toDate = mapParam.get("toDate");
			String service = mapParam.get("service");
			if(receiverMsisdn.equals(null) || receiverMsisdn.equals("") || !isNumeric(receiverMsisdn)){
				c2SEnquiry.selectServiceTypeReceiverMsisdn(service);
				c2SEnquiry.enterFromDateReceiverMsisdn(fromDate);
				c2SEnquiry.enterToDateReceiverMsisdn(toDate);
				if(!isNumeric(receiverMsisdn))
				c2SEnquiry.enterReceiverMsisdn(receiverMsisdn);	
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorReceiverMsisdn());
				return resultMap;
			}
		
			if(fromDate.equals(null) || fromDate.equals("")){
				c2SEnquiry.selectServiceTypeReceiverMsisdn(service);
				c2SEnquiry.enterToDateReceiverMsisdn(toDate);
				c2SEnquiry.enterReceiverMsisdn(receiverMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorFromDateReceiverMsisdn());
				return resultMap;
			}
		
			if(toDate.equals(null) || toDate.equals("")){
				c2SEnquiry.selectServiceTypeReceiverMsisdn(service);
				c2SEnquiry.enterFromDateReceiverMsisdn(fromDate);
				c2SEnquiry.enterReceiverMsisdn(receiverMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorToDateReceiverMsisdn());
				return resultMap;
			}
			
			if(service.equals("Select")){
				c2SEnquiry.enterFromDateReceiverMsisdn(fromDate);
				c2SEnquiry.enterToDateReceiverMsisdn(toDate);
				c2SEnquiry.enterReceiverMsisdn(receiverMsisdn);
				resultMap.put("fieldError", c2SEnquiry.getfieldErrorServiceTypeReceiverMsisdn());
				return resultMap;
			}
			c2SEnquiry.selectServiceTypeReceiverMsisdn(service);
			c2SEnquiry.enterFromDateReceiverMsisdn(fromDate);
			c2SEnquiry.enterToDateReceiverMsisdn(toDate);
			c2SEnquiry.enterReceiverMsisdn(receiverMsisdn);
			c2SEnquiry.clickSubmitReceiverMsisdn();
			String screenshot = GetScreenshot.getFullScreenshot(driver);
			resultMap.put("screenshot", screenshot);
			c2sEnqyuiryPage.prepareC2SEnquiryValuesByReceiverIDforOpUser(receiverMsisdn, mapParam.get("senderMSISDN"), mapParam.get("transferID"));
			return resultMap;
		}

		String Screenshot = GetScreenshot.getFullScreenshot(driver);
		resultMap.put("screenshot", Screenshot);
		return resultMap;
	}

}
