package com.Features;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;

import com.classes.Login;
import com.commons.AutomationException;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.o2ctransfer.InitiateO2CTransferPage;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;
import com.pageobjects.channeluserspages.voucherOrderRequest.voucherOrderRequestPage1;
import com.pageobjects.channeluserspages.voucherOrderRequest.voucherOrderRequestPage2;
import com.pageobjects.channeluserspages.voucherOrderRequest.voucherOrderRequestPage3;
import com.pageobjects.channeluserspages.voucherOrderRequest.voucherOrderRequestPage4;
import com.pageobjects.superadminpages.VMS.AddVoucherDenomination;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.homepage.VoucherDenomination;
import com.pageobjects.superadminpages.homepage.VoucherGeneration;
import com.pageobjects.superadminpages.homepage.VoucherProfile;
import com.utils._masterVO;

public class VoucherOrderInitiate {
	WebDriver driver;
	Login login;
	ChannelAdminHomePage caHomepage;
	SuperAdminHomePage saHomePage;
	VoucherDenomination voucherDenomination;
	VoucherProfile voucherProfile;
	VoucherGeneration voucherGeneration;
	ChannelUserHomePage CHhomePage;
	voucherOrderRequestPage1 VoucherOrderRequestPage1;
	voucherOrderRequestPage2 VoucherOrderRequestPage2;
	voucherOrderRequestPage3 VoucherOrderRequestPage3;
	voucherOrderRequestPage4 VoucherOrderRequestPage4;
	InitiateO2CTransferPage initiateO2CPage;
	AddVoucherDenomination addVoucherDenomination;
	int Nation_Voucher;
	
	public VoucherOrderInitiate(WebDriver driver) {
		this.driver = driver;
		login = new Login();
		saHomePage = new SuperAdminHomePage(driver);
		caHomepage = new ChannelAdminHomePage(driver);
		voucherDenomination = new VoucherDenomination(driver);
		voucherProfile = new VoucherProfile(driver);
		voucherGeneration = new VoucherGeneration(driver);
		CHhomePage = new ChannelUserHomePage(driver);
		VoucherOrderRequestPage1 = new voucherOrderRequestPage1(driver);
		VoucherOrderRequestPage2 = new voucherOrderRequestPage2(driver);
		VoucherOrderRequestPage3 = new voucherOrderRequestPage3(driver);
		VoucherOrderRequestPage4 = new voucherOrderRequestPage4(driver);
		initiateO2CPage = new InitiateO2CTransferPage(driver);
		Nation_Voucher = Integer.parseInt(_masterVO.getClientDetail("Nation_Voucher"));
		addVoucherDenomination = new AddVoucherDenomination(driver);
	}
	
	
	public void VoucherOrderRequest(HashMap<String, String> channelresultMap, HashMap<String, String> initiateMap,HashMap<String, String> mapParam) throws InterruptedException {
		
		login.LoginAsUser(driver, channelresultMap.get("LOGIN_ID"), channelresultMap.get("PASSWORD"));
		CHhomePage.clickVoucherOrderRequest();
		CHhomePage.clickVoucherOrderRequestInitiate();
		VoucherOrderRequestPage1.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment("LC");
		}
		VoucherOrderRequestPage1.ClickonSubmit();
		VoucherOrderRequestPage2.enterReference("12344");
		VoucherOrderRequestPage2.SelectDenomination(initiateMap.get("denomination"));
		VoucherOrderRequestPage2.enterQuantity(initiateMap.get("quantity"));
		VoucherOrderRequestPage2.enterRemarks("Automation Testing");
		VoucherOrderRequestPage2.ClickonSubmit();
		VoucherOrderRequestPage3.enterVoucherQuanity(initiateMap.get("quantity"));
		VoucherOrderRequestPage3.SelectPaymentInstType(mapParam.get("paymentType"));
		VoucherOrderRequestPage3.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
		VoucherOrderRequestPage3.enterPaymentInstrumentDate(CHhomePage.getDate());
		VoucherOrderRequestPage3.enterPin(mapParam.get("PIN"));
		VoucherOrderRequestPage3.ClickonSubmit();
		VoucherOrderRequestPage4.ClickonConfirm();
		String message= initiateO2CPage.getMessage();
		
		initiateMap.put("INITIATE_MESSAGE", message);
		
		int index =message.indexOf("OT");
		initiateMap.put("TRANSACTION_ID", message.substring(index).replaceAll("[.]$",""));
		
	}
	
public void VoucherOrderRequestMRPCheck(HashMap<String, String> channelresultMap, HashMap<String, String> initiateMap,HashMap<String, String> mapParam) throws AutomationException {
		
		login.LoginAsUser(driver, channelresultMap.get("LOGIN_ID"), channelresultMap.get("PASSWORD"));
		CHhomePage.clickVoucherOrderRequest();
		CHhomePage.clickVoucherOrderRequestInitiate();
		VoucherOrderRequestPage1.SelectVoucherType(initiateMap.get("voucherType"));
		if (Nation_Voucher == 1) {
			if(addVoucherDenomination.isSegmentAvailable())
				addVoucherDenomination.SelectVoucherSegment("LC");
		}
		VoucherOrderRequestPage1.ClickonSubmit();
		VoucherOrderRequestPage2.enterReference("12344");
		
		if(initiateMap.get("mrpopposite")!="")
		{
				try {
					VoucherOrderRequestPage2.SelectDenomination(initiateMap.get("mrpopposite"));
					initiateMap.put("MessageStatus", "N");
				} catch(Exception ex) {
					throw new AutomationException("MRP not found, hence test case succesful.", ex);
				}
				
		}	
	}


public void VoucherOrderRequestNoVoucher(HashMap<String, String> channelresultMap, HashMap<String, String> initiateMap,HashMap<String, String> mapParam) throws AutomationException {
	
	login.LoginAsUser(driver, channelresultMap.get("LOGIN_ID"), channelresultMap.get("PASSWORD"));
	CHhomePage.clickVoucherOrderRequest();
	CHhomePage.clickVoucherOrderRequestInitiate();
	
	
	try {
	VoucherOrderRequestPage1.SelectVoucherType(initiateMap.get("voucherType"));
	} catch(Exception ex) {
		throw new AutomationException("Voucher Type not found, hence test case succesful.", ex);
	}
	
}

public void VoucherOrderRequestNoOrderRequest(HashMap<String, String> channelresultMap, HashMap<String, String> initiateMap,HashMap<String, String> mapParam) throws AutomationException {
	
	login.LoginAsUser(driver, channelresultMap.get("LOGIN_ID"), channelresultMap.get("PASSWORD"));
	try {
	CHhomePage.clickVoucherOrderRequest();
	} catch(Exception ex) {
		throw new AutomationException("Voucher Order Request Link not found, hence test case succesful.", ex);
	}
	
}

public void VoucherOrderRequestNoData(HashMap<String, String> channelresultMap, HashMap<String, String> initiateMap,HashMap<String, String> mapParam) {
	
	login.LoginAsUser(driver, channelresultMap.get("LOGIN_ID"), channelresultMap.get("PASSWORD"));
	CHhomePage.clickVoucherOrderRequest();
	CHhomePage.clickVoucherOrderRequestInitiate();
	VoucherOrderRequestPage1.SelectVoucherType(initiateMap.get("voucherType"));
	if (Nation_Voucher == 1) {
		if(addVoucherDenomination.isSegmentAvailable())
			addVoucherDenomination.SelectVoucherSegment("LC");
	}
	VoucherOrderRequestPage1.ClickonSubmit();
	VoucherOrderRequestPage2.ClickonSubmit();
	String errorMessage = VoucherOrderRequestPage2.getErrorMessage();
	if(errorMessage!=null)
	{
		initiateMap.put("MessageStatus", "N");
		initiateMap.put("Message", errorMessage);
	}
	else {
		initiateMap.put("MessageStatus", "Y");
	}
	
}

public void VoucherOrderRequestNoRemark(HashMap<String, String> channelresultMap, HashMap<String, String> initiateMap,HashMap<String, String> mapParam) {
	
	login.LoginAsUser(driver, channelresultMap.get("LOGIN_ID"), channelresultMap.get("PASSWORD"));
	CHhomePage.clickVoucherOrderRequest();
	CHhomePage.clickVoucherOrderRequestInitiate();
	VoucherOrderRequestPage1.SelectVoucherType(initiateMap.get("voucherType"));
	if (Nation_Voucher == 1) {
		if(addVoucherDenomination.isSegmentAvailable())
			addVoucherDenomination.SelectVoucherSegment("LC");
	}
	VoucherOrderRequestPage1.ClickonSubmit();
	VoucherOrderRequestPage2.enterReference("12344");
	VoucherOrderRequestPage2.SelectDenomination(initiateMap.get("denomination"));
	VoucherOrderRequestPage2.enterQuantity(initiateMap.get("quantity"));
	VoucherOrderRequestPage2.ClickonSubmit();
	String errorMessage = VoucherOrderRequestPage2.getErrorMessage();
	if(errorMessage!=null)
	{
		initiateMap.put("MessageStatus", "N");
		initiateMap.put("Message", errorMessage);
	}
	else {
		initiateMap.put("MessageStatus", "Y");
	}
	
}

public void VoucherOrderRequestNoPayINST(HashMap<String, String> channelresultMap, HashMap<String, String> initiateMap,HashMap<String, String> mapParam) {
	
	login.LoginAsUser(driver, channelresultMap.get("LOGIN_ID"), channelresultMap.get("PASSWORD"));
	CHhomePage.clickVoucherOrderRequest();
	CHhomePage.clickVoucherOrderRequestInitiate();
	VoucherOrderRequestPage1.SelectVoucherType(initiateMap.get("voucherType"));
	if (Nation_Voucher == 1) {
		if(addVoucherDenomination.isSegmentAvailable())
			addVoucherDenomination.SelectVoucherSegment("LC");
	}
	VoucherOrderRequestPage1.ClickonSubmit();
	VoucherOrderRequestPage2.enterReference("12344");
	VoucherOrderRequestPage2.SelectDenomination(initiateMap.get("denomination"));
	VoucherOrderRequestPage2.enterQuantity(initiateMap.get("quantity"));
	VoucherOrderRequestPage2.enterRemarks("Automation Testing");
	VoucherOrderRequestPage2.ClickonSubmit();
	VoucherOrderRequestPage3.enterVoucherQuanity(initiateMap.get("quantity"));
	VoucherOrderRequestPage3.ClickonSubmit();

	
	String errorMessage = VoucherOrderRequestPage3.getErrorMessage();
	if(errorMessage!=null)
	{
		initiateMap.put("MessageStatus", "N");
		initiateMap.put("Message", errorMessage);
	}
	else {
		initiateMap.put("MessageStatus", "Y");
	}
	
}

public void VoucherOrderRequestNoPayINSTDATE(HashMap<String, String> channelresultMap, HashMap<String, String> initiateMap,HashMap<String, String> mapParam) {
	
	login.LoginAsUser(driver, channelresultMap.get("LOGIN_ID"), channelresultMap.get("PASSWORD"));
	CHhomePage.clickVoucherOrderRequest();
	CHhomePage.clickVoucherOrderRequestInitiate();
	VoucherOrderRequestPage1.SelectVoucherType(initiateMap.get("voucherType"));
	if (Nation_Voucher == 1) {
		if(addVoucherDenomination.isSegmentAvailable())
			addVoucherDenomination.SelectVoucherSegment("LC");
	}
	VoucherOrderRequestPage1.ClickonSubmit();
	VoucherOrderRequestPage2.enterReference("12344");
	VoucherOrderRequestPage2.SelectDenomination(initiateMap.get("denomination"));
	VoucherOrderRequestPage2.enterQuantity(initiateMap.get("quantity"));
	VoucherOrderRequestPage2.enterRemarks("Automation Testing");
	VoucherOrderRequestPage2.ClickonSubmit();
	VoucherOrderRequestPage3.enterVoucherQuanity(initiateMap.get("quantity"));
	VoucherOrderRequestPage3.SelectPaymentInstType(mapParam.get("paymentType"));
	VoucherOrderRequestPage3.enterPaymentInstNum(_masterVO.getProperty("PaymentInstNum"));
	VoucherOrderRequestPage3.ClickonSubmit();

	
	String errorMessage = VoucherOrderRequestPage3.getErrorMessage();
	if(errorMessage!=null)
	{
		initiateMap.put("MessageStatus", "N");
		initiateMap.put("Message", errorMessage);
	}
	else {
		initiateMap.put("MessageStatus", "Y");
	}
	
}


}
