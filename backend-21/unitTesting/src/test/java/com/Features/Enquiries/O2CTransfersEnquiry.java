package com.Features.Enquiries;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import com.classes.GetScreenshot;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.channeladminpages.channelenquiries.O2CTransfers;
import com.pageobjects.channeladminpages.homepage.ChannelAdminHomePage;
import com.pageobjects.channeladminpages.homepage.ChannelEnquirySubCategories;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;
import com.utils._masterVO;

public class O2CTransfersEnquiry {
	
	WebDriver driver;
	ChannelAdminHomePage HomePage;
	ChannelEnquirySubCategories ChannelEnquirySubCategory;
	Login login;
	SelectNetworkPage networkPage;
	O2CTransfers O2CEnquiry;
	SoftAssert SAssert = new SoftAssert();
	Map<String, String> userAccessMap = new HashMap<String, String>();
	int ENQUIRY_SENDER_BALANCE;
	
	public O2CTransfersEnquiry(WebDriver driver) {
		this.driver = driver;
		//Page Initialization
		HomePage = new ChannelAdminHomePage(driver);
		login = new Login();
		ChannelEnquirySubCategory = new ChannelEnquirySubCategories(driver);
		networkPage = new SelectNetworkPage(driver);
		O2CEnquiry = new O2CTransfers(driver);
		ENQUIRY_SENDER_BALANCE = Integer.parseInt(_masterVO.getClientDetail("ENQUIRY_SENDER_BALANCE"));
	}
	
	public HashMap<String, String> prepareO2CTransferEnquiryDAO() {
		
		HashMap<String, String> O2CTransfersDAO= new HashMap<String, String>();
		
		String TransferNumber_Key = MessagesDAO.getLabelByKey("channeltransfer.enquirytransferview.label.transfernumber");
		String ProductType_Key = MessagesDAO.getLabelByKey("channeltransfer.enquirytransfersearchuser.label.producttype");
		String SenderPreBalance_Key = MessagesDAO.getLabelByKey("operatortransfer.o2cenquirytransferlist.label.senderprebal");
		String SenderPostBalance_Key = MessagesDAO.getLabelByKey("operatortransfer.o2cenquirytransferlist.label.senderpostbal");
		String ReceiverPreBalance_Key = MessagesDAO.getLabelByKey("operatortransfer.o2cenquirytransferlist.label.receiverprebal");
		String ReceiverPostBalance_Key = MessagesDAO.getLabelByKey("operatortransfer.o2cenquirytransferlist.label.receiverpostbal");
		
		String TransferNumber_Locator = "//tr/td[text()[contains(.,'"+ TransferNumber_Key +"')]]/following-sibling::td";
		String ProductType_Locator = "//tr/td[text()[contains(.,'"+ ProductType_Key +"')]]/following-sibling::td";
		String SenderPreBalance_Locator = "//tr/td[text()[contains(.,'"+ SenderPreBalance_Key +"')]]";
		String SenderPostBalance_Locator = "//tr/td[text()[contains(.,'"+ SenderPostBalance_Key +"')]]";
		String ReceiverPreBalance_Locator = "//tr/td[text()[contains(.,'"+ ReceiverPreBalance_Key +"')]]";
		String ReceiverPostBalance_Locator = "//tr/td[text()[contains(.,'"+ ReceiverPostBalance_Key +"')]]";
		
		O2CTransfersDAO.put("Transfer Number", driver.findElement(By.xpath(TransferNumber_Locator)).getText());
		O2CTransfersDAO.put("Product Type", driver.findElement(By.xpath(ProductType_Locator)).getText());
		if(ENQUIRY_SENDER_BALANCE==1) {
			O2CTransfersDAO.put("Receiver Pre Balance", driver.findElement(By.xpath(ReceiverPreBalance_Locator)).getText());
			O2CTransfersDAO.put("Receiver Post Balance", driver.findElement(By.xpath(ReceiverPostBalance_Locator)).getText());
		}
		else {
			O2CTransfersDAO.put("Sender Pre Balance", driver.findElement(By.xpath(SenderPreBalance_Locator)).getText());
			O2CTransfersDAO.put("Sender Post Balance", driver.findElement(By.xpath(SenderPostBalance_Locator)).getText());
			O2CTransfersDAO.put("Receiver Pre Balance", driver.findElement(By.xpath(ReceiverPreBalance_Locator)).getText());
			O2CTransfersDAO.put("Receiver Post Balance", driver.findElement(By.xpath(ReceiverPostBalance_Locator)).getText());
		}
		
		
		return O2CTransfersDAO;
	}
	
	public String validateO2CTransfersEnquiry(String TransactionNumber, String ProductType) {
		
		HashMap<String, String> O2CTransfersDAO = new HashMap<String, String>();
				
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.O2C_TRANSFERS_ENQUIRY_ROLECODE); //Getting User with Access to O2C Transfers Enquiry
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		HomePage.clickChannelEnquiry();
		ChannelEnquirySubCategory.clickO2CTransfers();
		O2CEnquiry.enterTransferNumber(TransactionNumber);
		O2CEnquiry.clickSubmitButton();
		
		String ProductName = DBHandler.AccessHandler.getProductNameByCode(ProductType);
		
		String Screenshot = GetScreenshot.getFullScreenshot(driver);
		//Enquiry Validator Begins
		O2CTransfersDAO = prepareO2CTransferEnquiryDAO();
		SAssert.assertEquals(O2CTransfersDAO.get("Transfer Number"), TransactionNumber);
		Log.info("Transaction Number - Validator - Success");
		SAssert.assertEquals(O2CTransfersDAO.get("Product Type"), ProductName);
		Log.info("Product Type - Validator - Success");
		SAssert.assertAll();
		
		return Screenshot;
	}
	
}
