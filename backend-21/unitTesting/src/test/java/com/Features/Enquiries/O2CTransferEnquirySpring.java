package com.Features.Enquiries;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import com.classes.GetScreenshot;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.channeluserspages.channelenquiry.EnquiryChannelUserViewSpringpage;
import com.pageobjects.channeluserspages.channelenquiry.O2CEnquiryTransferViewSpringPage;
import com.pageobjects.channeluserspages.channelenquiry.O2CenquiryTransferListSpringPage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.sublinks.ChannelUserSubLinkPages;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.CommonUtils;
import com.utils.Log;

public class O2CTransferEnquirySpring {

	WebDriver driver=null;
	ChannelUserHomePage CHhomePage;
	Login login;
	Map<String, String> O2CTransEnqMap;
	ChannelUserSubLinkPages chnlSubLink;
	SelectNetworkPage networkPage;
	ChannelEnquirySubCategories channelEnquirySubCategories;
	O2CEnquiryTransferViewSpringPage o2CEnquiryTransferViewSpringPage;
	O2CenquiryTransferListSpringPage o2CenquiryTransferListSpringPage;
	EnquiryChannelUserViewSpringpage enquiryChannelUserViewSpringpage;
	SoftAssert sAssert;
	Map<String, String> resultMap;
	CommonUtils commonUtils;

	public O2CTransferEnquirySpring (WebDriver driver){
		this.driver=driver;
		CHhomePage=new ChannelUserHomePage(driver);
		login=new Login();
		channelEnquirySubCategories=new ChannelEnquirySubCategories(driver);
		o2CEnquiryTransferViewSpringPage= new O2CEnquiryTransferViewSpringPage(driver);
		o2CenquiryTransferListSpringPage=new O2CenquiryTransferListSpringPage(driver);
		enquiryChannelUserViewSpringpage=new EnquiryChannelUserViewSpringpage(driver);
		O2CTransEnqMap=new HashMap<String, String>();
		chnlSubLink= new ChannelUserSubLinkPages(driver);
		resultMap = new HashMap<String, String>();
		sAssert = new SoftAssert();
		commonUtils = new CommonUtils();
	}


	public Map<String, String> o2CTransfer(HashMap<String, String> mapParam , String searchCriteria) throws IOException, InterruptedException{

		Log.info("O2C Transfer Enquiry");
		O2CTransEnqMap = UserAccess.getUserWithAccess(RolesI.O2C_TRANSFERS_ENQUIRY_ROLECODE); 
		login.UserLogin(driver, "ChannelUser", mapParam.get("category"));
		CHhomePage.clickChannelEnquiry();
		
		CHhomePage.clickChannelEnquiry();
		Thread.sleep(1000);
		channelEnquirySubCategories.clickO2CTransfersSpring();

		if(searchCriteria.equalsIgnoreCase("transferNum")){
			String transferNum = mapParam.get("transferNum");
			if(transferNum == null || transferNum == ""){
				enquiryChannelUserViewSpringpage.clickSubmitTrfID();
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.getTransferNumberFieldError());
				return resultMap;
			}
			enquiryChannelUserViewSpringpage.enterTransferNum(transferNum);
			enquiryChannelUserViewSpringpage.clickSubmitTrfID();
			String screenshot = GetScreenshot.getFullScreenshot(driver);
			resultMap.put("screenshot", screenshot);
			sAssert.assertEquals(o2CEnquiryTransferViewSpringPage.prepareO2CEnquiryData().get("transferNumber"), mapParam.get("transferNum"));
			sAssert.assertAll();
			
			return resultMap;
		}


		if(searchCriteria.equalsIgnoreCase("mobileNumber")){
			enquiryChannelUserViewSpringpage.clickcollapseTwo();
			Thread.sleep(1000);

			String msisdn = mapParam.get("msisdn");
			String transferCategoryMsisdnPanel = mapParam.get("transferCategory");
			String fromDateMsisdnPanel  = mapParam.get("fromDate");
			String toDateMsisdnPanel = mapParam.get("toDate");
			
			
			if(msisdn == null || msisdn.equals("")){
				enquiryChannelUserViewSpringpage.selectTransferCategory(transferCategoryMsisdnPanel);
				enquiryChannelUserViewSpringpage.enterfromDateForUserCode(fromDateMsisdnPanel);
				enquiryChannelUserViewSpringpage.enterToDateForUserCode(toDateMsisdnPanel);
				enquiryChannelUserViewSpringpage.clickSubmitMSISDN();
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.getUserCodeFieldError());
				return resultMap;
			}

			if(!commonUtils.isNumeric(msisdn)){
				enquiryChannelUserViewSpringpage.enterUserCode(msisdn);
				enquiryChannelUserViewSpringpage.selectTransferCategory(transferCategoryMsisdnPanel);
				enquiryChannelUserViewSpringpage.enterfromDateForUserCode(fromDateMsisdnPanel);
				enquiryChannelUserViewSpringpage.enterToDateForUserCode(toDateMsisdnPanel);
				enquiryChannelUserViewSpringpage.clickSubmitMSISDN();
				
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.getUserCodeFieldError());
				return resultMap;
			}

			
			if(transferCategoryMsisdnPanel.equalsIgnoreCase("Select")){
				enquiryChannelUserViewSpringpage.enterUserCode(msisdn);
				enquiryChannelUserViewSpringpage.enterfromDateForUserCode(fromDateMsisdnPanel);
				enquiryChannelUserViewSpringpage.enterToDateForUserCode(toDateMsisdnPanel);
				enquiryChannelUserViewSpringpage.clickSubmitMSISDN();
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.getTransferCategoryFieldErrorMsisdnPanel());
				return resultMap;
			}

			if(fromDateMsisdnPanel.equals("") || fromDateMsisdnPanel == null){
				enquiryChannelUserViewSpringpage.enterUserCode(msisdn);
				enquiryChannelUserViewSpringpage.selectTransferCategory(transferCategoryMsisdnPanel);
				enquiryChannelUserViewSpringpage.enterToDateForUserCode(toDateMsisdnPanel);
				enquiryChannelUserViewSpringpage.clickSubmitMSISDN();
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.getfromDateForUserCodeFieldError());
				return resultMap;
			}

			if(toDateMsisdnPanel.equals("") || toDateMsisdnPanel == null){
				enquiryChannelUserViewSpringpage.enterUserCode(msisdn);
				enquiryChannelUserViewSpringpage.selectTransferCategory(transferCategoryMsisdnPanel);
				enquiryChannelUserViewSpringpage.enterfromDateForUserCode(fromDateMsisdnPanel);
				enquiryChannelUserViewSpringpage.clickSubmitMSISDN();
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.gettoDateForUserCodeFieldError());
				return resultMap;
			}

			enquiryChannelUserViewSpringpage.enterUserCode(msisdn);
			enquiryChannelUserViewSpringpage.selectTransferCategory(transferCategoryMsisdnPanel);
			enquiryChannelUserViewSpringpage.enterfromDateForUserCode(fromDateMsisdnPanel);
			enquiryChannelUserViewSpringpage.enterToDateForUserCode(toDateMsisdnPanel);
			enquiryChannelUserViewSpringpage.clickSubmitMSISDN();
			o2CenquiryTransferListSpringPage.clickOnTransferNumberByMSISDN(msisdn);
			String screenshot = GetScreenshot.getFullScreenshot(driver);
			resultMap.put("screenshot", screenshot);
			sAssert.assertEquals(o2CEnquiryTransferViewSpringPage.prepareO2CEnquiryData().get("transferNumber"), mapParam.get("transferNum"));
			sAssert.assertAll();
			return resultMap;
		}

		if(searchCriteria == "geographicalDomain"){
			enquiryChannelUserViewSpringpage.clickcollapseThree();
			Thread.sleep(1000);

			String productType = mapParam.get("productType");
			String categoryCode = mapParam.get("domainCategory");
			String transferCategoryCode = mapParam.get("transferCategory");
			String transferTypeCode = mapParam.get("transferType");
			String fromDateGeographyPanel = mapParam.get("fromDate");
			String toDateGeographyPanel = mapParam.get("toDate");
			String statusCode = mapParam.get("orderstatus");
			String channelCategoryUserName = mapParam.get("userName");
			if(productType.equalsIgnoreCase("Select")){
				enquiryChannelUserViewSpringpage.selectCategory(categoryCode);
				enquiryChannelUserViewSpringpage.selectTransferCategoryCode(transferCategoryCode);
				enquiryChannelUserViewSpringpage.selectTransferTypeCode(transferTypeCode);
				
				enquiryChannelUserViewSpringpage.enterfromDate(fromDateGeographyPanel);
				enquiryChannelUserViewSpringpage.enterToDate(toDateGeographyPanel);
				enquiryChannelUserViewSpringpage.selectStatusCode(statusCode);
				enquiryChannelUserViewSpringpage.enterChannelCategoryUserName(channelCategoryUserName);
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.getProductTypeFieldError());
				return resultMap;
			}

			if(categoryCode.equalsIgnoreCase("Select")){
				enquiryChannelUserViewSpringpage.selectProductType(productType);
				enquiryChannelUserViewSpringpage.selectTransferCategoryCode(transferCategoryCode);
				enquiryChannelUserViewSpringpage.selectTransferTypeCode(transferTypeCode);
				enquiryChannelUserViewSpringpage.selectStatusCode(statusCode);
				enquiryChannelUserViewSpringpage.enterChannelCategoryUserName(channelCategoryUserName);;
				enquiryChannelUserViewSpringpage.enterfromDate(fromDateGeographyPanel);
				enquiryChannelUserViewSpringpage.enterToDate(toDateGeographyPanel);
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.getCategoryFieldError());
				return resultMap;
			}

			if(transferCategoryCode.equalsIgnoreCase("Select")){
				enquiryChannelUserViewSpringpage.selectProductType(productType);
				enquiryChannelUserViewSpringpage.selectCategory(categoryCode);
				enquiryChannelUserViewSpringpage.selectTransferTypeCode(transferTypeCode);
				enquiryChannelUserViewSpringpage.selectStatusCode(statusCode);
				enquiryChannelUserViewSpringpage.enterChannelCategoryUserName(channelCategoryUserName);;
				enquiryChannelUserViewSpringpage.enterfromDate(fromDateGeographyPanel);
				enquiryChannelUserViewSpringpage.enterToDate(toDateGeographyPanel);
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.getTransfercategoryFieldError());
				return resultMap;
			}

			if(transferTypeCode.equalsIgnoreCase("Select")){
				enquiryChannelUserViewSpringpage.selectProductType(productType);
				enquiryChannelUserViewSpringpage.selectCategory(categoryCode);
				enquiryChannelUserViewSpringpage.selectTransferCategoryCode(transferCategoryCode);
				enquiryChannelUserViewSpringpage.selectStatusCode(statusCode);
				enquiryChannelUserViewSpringpage.enterChannelCategoryUserName(channelCategoryUserName);;
				enquiryChannelUserViewSpringpage.enterfromDate(fromDateGeographyPanel);
				enquiryChannelUserViewSpringpage.enterToDate(toDateGeographyPanel);
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.getTransferTypeCodeFieldError());
				return resultMap;
			}

			if(fromDateGeographyPanel.equals("") || fromDateGeographyPanel == null){
				enquiryChannelUserViewSpringpage.selectProductType(productType);
				enquiryChannelUserViewSpringpage.selectCategory(categoryCode);
				enquiryChannelUserViewSpringpage.selectTransferCategoryCode(transferCategoryCode);
				enquiryChannelUserViewSpringpage.selectTransferTypeCode(transferTypeCode);
				enquiryChannelUserViewSpringpage.selectStatusCode(statusCode);
				enquiryChannelUserViewSpringpage.enterChannelCategoryUserName(channelCategoryUserName);
				enquiryChannelUserViewSpringpage.enterToDate(toDateGeographyPanel);
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.getfromDateFieldError());
				return resultMap;
			}

			if(toDateGeographyPanel.equals("") || toDateGeographyPanel == null){
				enquiryChannelUserViewSpringpage.selectProductType(productType);
				enquiryChannelUserViewSpringpage.selectCategory(categoryCode);
				enquiryChannelUserViewSpringpage.selectTransferCategoryCode(transferCategoryCode);
				enquiryChannelUserViewSpringpage.selectTransferTypeCode(transferTypeCode);
				enquiryChannelUserViewSpringpage.selectStatusCode(statusCode);
				enquiryChannelUserViewSpringpage.enterChannelCategoryUserName(channelCategoryUserName);;
				enquiryChannelUserViewSpringpage.enterfromDate(fromDateGeographyPanel);
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.gettoDateFieldError());
				return resultMap;
			}
			
			if(channelCategoryUserName.equals("") || channelCategoryUserName == null){
				enquiryChannelUserViewSpringpage.selectProductType(productType);
				enquiryChannelUserViewSpringpage.selectCategory(categoryCode);
				enquiryChannelUserViewSpringpage.selectTransferCategoryCode(transferCategoryCode);
				enquiryChannelUserViewSpringpage.selectTransferTypeCode(transferTypeCode);
				enquiryChannelUserViewSpringpage.selectStatusCode(statusCode);
				enquiryChannelUserViewSpringpage.enterfromDate(fromDateGeographyPanel);
				enquiryChannelUserViewSpringpage.enterToDate(toDateGeographyPanel);
				resultMap.put("fieldError", enquiryChannelUserViewSpringpage.getChannelCategoryUserNameFieldError());
				return resultMap;
			}

			enquiryChannelUserViewSpringpage.selectProductType(productType);
			enquiryChannelUserViewSpringpage.selectCategory(categoryCode);
			enquiryChannelUserViewSpringpage.selectTransferCategoryCode(transferCategoryCode);
			enquiryChannelUserViewSpringpage.selectTransferTypeCode(transferTypeCode);
			enquiryChannelUserViewSpringpage.selectStatusCode(statusCode);
			enquiryChannelUserViewSpringpage.enterfromDate(fromDateGeographyPanel);
			enquiryChannelUserViewSpringpage.enterToDate(toDateGeographyPanel);
			enquiryChannelUserViewSpringpage.enterChannelCategoryUserName(channelCategoryUserName);;
			enquiryChannelUserViewSpringpage.clickSubmitUserSearch();
			String screenshot = GetScreenshot.getFullScreenshot(driver);
			resultMap.put("screenshot", screenshot);
			sAssert.assertEquals(o2CEnquiryTransferViewSpringPage.prepareO2CEnquiryData().get("transferNumber"), mapParam.get("transferNum"));
			sAssert.assertAll();
			return resultMap;
		}

		return resultMap;
	}

}
