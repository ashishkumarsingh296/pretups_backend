package com.Features.Enquiries;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import com.classes.GetScreenshot;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.dbrepository.DBHandler;
import com.pageobjects.channeluserspages.homepages.ChannelEnquirySubLinks;
import com.pageobjects.channeluserspages.homepages.ChannelUserHomePage;

public class ViewSelfDetailsEnquiry {
	
	WebDriver driver;
	ChannelUserHomePage HomePage;
	ChannelEnquirySubLinks ChannelEnquirySubCategory;
	Login login;
	SoftAssert SAssert = new SoftAssert();
	Map<String, String> userAccessMap = new HashMap<String, String>();
	
	String IS_FNAME_LNAME_ALLOWED = null;
	String WEB_INTERFACE_ALLOWED = null;
	String SMS_INTERFACE_ALLOWED = null;
	
	public ViewSelfDetailsEnquiry(WebDriver driver) {
		this.driver = driver;
		//Page Initialization
		HomePage = new ChannelUserHomePage(driver);
		login = new Login();
		ChannelEnquirySubCategory = new ChannelEnquirySubLinks(driver);
	}
	
	public HashMap<String, String> prepareSelfDetailsEnquiryDAO(HashMap<String, String> ChannelUserMap) {
		
		String FirstName_Key = null;
		String LastName_Key = null;
		String LoginID_Key = null;
		String MSISDN_Key = null;
		String MSISDN_Description_Key = null;
		
		HashMap<String, String> ChannelUserEnquiryDAO= new HashMap<String, String>();
		
		String Status_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.status");
		String UserName_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.username");
		
		IS_FNAME_LNAME_ALLOWED = DBHandler.AccessHandler.getSystemPreference("IS_FNAME_LNAME_ALLOWED");
		if (IS_FNAME_LNAME_ALLOWED.equalsIgnoreCase("true")) {
			FirstName_Key = MessagesDAO.getLabelByKey("user.addchanneluser.label.firstName");
			LastName_Key = MessagesDAO.getLabelByKey("user.addchanneluser.label.lastName");
		}
		
		String ShortName_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.shortname");
		String UserNamePreFix_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.usernameprefix");
		String EmployeeCode_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.empcode");
		String ExternalCode_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.externalcode");
		String ContactPerson_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.contactperson");
		String ContactNo_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.contactno");
		String SSN_Key = MessagesDAO.getLabelByKey("user.addchanneluser.label.ssn");
		String Designation_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.designation");
		
		LoginID_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.webloginid");
		
		String Address1_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.address1");
		String Address2_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.address2");
		String City_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.city");
		String State_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.state");
		String Country_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.country");
		String Email_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.email");
		String Company_Key = MessagesDAO.getLabelByKey("user.addchanneluser.label.company");
		String Fax_Key = MessagesDAO.getLabelByKey("user.addchanneluser.label.fax");
		String AppointmentDate_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.appointmentdate");
		String Language_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.language");
		String OtherEmail_Key = MessagesDAO.getLabelByKey("user.addchanneluser.label.alternateemail");
		String Longitude_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.longitude");
		String Latitude_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.latitude");
		
		WEB_INTERFACE_ALLOWED = DBHandler.AccessHandler.getCategoryDetail("WEB_INTERFACE_ALLOWED", ChannelUserMap.get("Category"));		
		if (WEB_INTERFACE_ALLOWED.equalsIgnoreCase("Y")) {
			LoginID_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.webloginid"); 
		}
		
		String Email_Main_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.email");
		
		SMS_INTERFACE_ALLOWED = DBHandler.AccessHandler.getCategoryDetail("SMS_INTERFACE_ALLOWED", ChannelUserMap.get("Category"));
		if (SMS_INTERFACE_ALLOWED.equalsIgnoreCase("Y")) {
			MSISDN_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.number");
			MSISDN_Description_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.description");
		}
		
		String Grade_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.usergrade");
		String CommissionProfile_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.commissionprofile");
		String TCP_Key = MessagesDAO.getLabelByKey("user.addchanneluserview.label.transferprofile");
		
		/*
		 * Element Locators for Enquiry Validation
		 */
		String UserName_Locator = "//tr/td[text()[contains(.,'"+ UserName_Key +"')]]/following-sibling::td[1]";
		String FirstName_Locator = "//tr/td[text()[contains(.,'"+ FirstName_Key +"')]]/following-sibling::td[1]";
		String LastName_Locator = "//tr/td[text()[contains(.,'"+ LastName_Key +"')]]/following-sibling::td[1]";
		String ShortName_Locator = "//tr/td[text()[contains(.,'"+ ShortName_Key +"')]]/following-sibling::td[1]";
		String UserNamePreFix_Locator = "//tr/td[text()[contains(.,'"+ UserNamePreFix_Key +"')]]/following-sibling::td[1]";
		String EmployeeCode_Locator = "//tr/td[text()[contains(.,'"+ EmployeeCode_Key +"')]]/following-sibling::td[1]";
		String ExternalCode_Locator = "//tr/td[text()[contains(.,'"+ ExternalCode_Key +"')]]/following-sibling::td[1]";
		String ContactPerson_Locator = "//tr/td[text()[contains(.,'"+ ContactPerson_Key +"')]]/following-sibling::td[1]";
		String ContactNo_Locator = "//tr/td[text()[contains(.,'"+ ContactNo_Key +"')]]/following-sibling::td[1]";
		//String SSN_Locator = "//tr/td[text()[contains(.,'"+ SSN_Key +"')]]/following-sibling::td[1]";
		String Designation_Locator = "//tr/td[text()[contains(.,'"+ Designation_Key +"')]]/following-sibling::td[1]";
		String Address1_Locator = "//tr/td[text()[contains(.,'"+ Address1_Key +"')]]/following-sibling::td[1]";
		
		String Address2_Locator = "//tr/td[text()[contains(.,'"+ Address2_Key +"')]]/following-sibling::td[1]";
		String City_Locator = "//tr/td[text()[contains(.,'"+ City_Key +"')]]/following-sibling::td[1]";
		String State_Locator = "//tr/td[text()[contains(.,'"+ State_Key +"')]]/following-sibling::td[1]";
		String Country_Locator = "//tr/td[text()[contains(.,'"+ Country_Key +"')]]/following-sibling::td[1]";
		String Email_Locator = "//tr/td[text()[contains(.,'"+ Email_Key +"')]]/following-sibling::td[1]";
		String Company_Locator = "//tr/td[text()[contains(.,'"+ Company_Key +"')]]/following-sibling::td[1]";
		String Fax_Locator = "//tr/td[text()[contains(.,'"+ Fax_Key +"')]]/following-sibling::td[1]";
		String AppointmentDate_Locator = "//tr/td[text()[contains(.,'"+ AppointmentDate_Key +"')]]/following-sibling::td[1]";
		String Language_Locator = "//tr/td[text()[contains(.,'"+ Language_Key +"')]]/following-sibling::td[1]";
		String OtherEmail_Locator = "//tr/td[text()[contains(.,'"+ OtherEmail_Key +"')]]/following-sibling::td[1]";
		String Longitude_Locator = "//tr/td[text()[contains(.,'"+ Longitude_Key +"')]]/following-sibling::td[1]";
		String Latitude_Locator = "//tr/td[text()[contains(.,'"+ Latitude_Key +"')]]/following-sibling::td[1]";
		
		String LoginID_Locator = "//tr/td[text()[contains(.,'"+ LoginID_Key +"')]]/following-sibling::td[1]";
		String EmailMain_Locator = "//tr/td[text()[contains(.,'"+ Email_Main_Key +"')]]/following-sibling::td[1]";
			
		String Grade_Locator = "//tr/td[text()[contains(.,'"+ Grade_Key +"')]]/following-sibling::td[1]";
		String CommissionProfile_Locator = "//tr/td[text()[contains(.,'"+ CommissionProfile_Key +"')]]/following-sibling::td[1]";
		String TCP_Locator = "//tr/td[text()[contains(.,'"+ TCP_Key +"')]]/following-sibling::td[1]";
		
		ChannelUserEnquiryDAO.put("User Name", driver.findElement(By.xpath(UserName_Locator)).getText());
		ChannelUserEnquiryDAO.put("IS_FNAME_LNAME_ALLOWED", IS_FNAME_LNAME_ALLOWED);
		
		if (IS_FNAME_LNAME_ALLOWED.equalsIgnoreCase("true")) {
			ChannelUserEnquiryDAO.put("First Name", driver.findElement(By.xpath(FirstName_Locator)).getText());
			ChannelUserEnquiryDAO.put("Last Name", driver.findElement(By.xpath(LastName_Locator)).getText());	
		}
		ChannelUserEnquiryDAO.put("Short Name", driver.findElement(By.xpath(ShortName_Locator)).getText());
		ChannelUserEnquiryDAO.put("User Name PreFix", driver.findElement(By.xpath(UserNamePreFix_Locator)).getText());
		ChannelUserEnquiryDAO.put("Employee Code", driver.findElement(By.xpath(EmployeeCode_Locator)).getText());
		ChannelUserEnquiryDAO.put("External Code", driver.findElement(By.xpath(ExternalCode_Locator)).getText());
		ChannelUserEnquiryDAO.put("Contact Person", driver.findElement(By.xpath(ContactPerson_Locator)).getText());
		ChannelUserEnquiryDAO.put("Contact No", driver.findElement(By.xpath(ContactNo_Locator)).getText());
		// Commented it until a governing preference is found for SSN
		/*ChannelUserEnquiryDAO.put("SSN", driver.findElement(By.xpath(SSN_Locator)).getText());*/
		ChannelUserEnquiryDAO.put("Designation", driver.findElement(By.xpath(Designation_Locator)).getText());
		ChannelUserEnquiryDAO.put("Address1", driver.findElement(By.xpath(Address1_Locator)).getText());
		ChannelUserEnquiryDAO.put("Address2", driver.findElement(By.xpath(Address2_Locator)).getText());
		ChannelUserEnquiryDAO.put("City", driver.findElement(By.xpath(City_Locator)).getText());
		ChannelUserEnquiryDAO.put("State", driver.findElement(By.xpath(State_Locator)).getText());
		ChannelUserEnquiryDAO.put("Country", driver.findElement(By.xpath(Country_Locator)).getText());
		ChannelUserEnquiryDAO.put("Email", driver.findElement(By.xpath(Email_Locator)).getText());
		ChannelUserEnquiryDAO.put("Company", driver.findElement(By.xpath(Company_Locator)).getText());
		ChannelUserEnquiryDAO.put("Fax", driver.findElement(By.xpath(Fax_Locator)).getText());
		ChannelUserEnquiryDAO.put("Appointment Date", driver.findElement(By.xpath(AppointmentDate_Locator)).getText());
		ChannelUserEnquiryDAO.put("Language", driver.findElement(By.xpath(Language_Locator)).getText());
		ChannelUserEnquiryDAO.put("Other Email", driver.findElement(By.xpath(OtherEmail_Locator)).getText());
		ChannelUserEnquiryDAO.put("Longitude", driver.findElement(By.xpath(Longitude_Locator)).getText());
		ChannelUserEnquiryDAO.put("Latitude", driver.findElement(By.xpath(Latitude_Locator)).getText());
		ChannelUserEnquiryDAO.put("Login ID", driver.findElement(By.xpath(LoginID_Locator)).getText());
		ChannelUserEnquiryDAO.put("Email Main", driver.findElement(By.xpath(EmailMain_Locator)).getText());
		ChannelUserEnquiryDAO.put("Grade", driver.findElement(By.xpath(Grade_Locator)).getText());
		ChannelUserEnquiryDAO.put("Commission Profile", driver.findElement(By.xpath(CommissionProfile_Locator)).getText());
		ChannelUserEnquiryDAO.put("TCP", driver.findElement(By.xpath(TCP_Locator)).getText());
		
		return ChannelUserEnquiryDAO;
	}
	
	public String validateSelfDetailsEnquiry(HashMap<String, String> ChannelUserMap) {
		
		HashMap<String, String> ChannelUsersEnquiryDAO = new HashMap<String, String>();
		
		login.LoginAsUser(driver, ChannelUserMap.get("LOGIN_ID"), ChannelUserMap.get("PASSWORD"));
		
		HomePage.clickChannelEnquiry();
		ChannelEnquirySubCategory.clickViewSelfDetails();
		
		String Screenshot = GetScreenshot.getFullScreenshot(driver);
		//Enquiry Validator Begins
		ChannelUsersEnquiryDAO = prepareSelfDetailsEnquiryDAO(ChannelUserMap);
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("User Name"), ChannelUserMap.get("uName"));
		if (ChannelUsersEnquiryDAO.get("IS_FNAME_LNAME_ALLOWED").equalsIgnoreCase("true")) {
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("First Name"), ChannelUserMap.get("fName"));
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("Last Name"), ChannelUserMap.get("lName"));
		}
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("Short Name"), ChannelUserMap.get("shortName"));
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("External Code"), ChannelUserMap.get("EXTCODE"));
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("Contact No"), ChannelUserMap.get("ContactNo"));
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("Address1"), ChannelUserMap.get("Address1"));
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("Address2"), ChannelUserMap.get("Address2"));
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("City"), ChannelUserMap.get("City"));
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("State"), ChannelUserMap.get("State"));
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("Country"), ChannelUserMap.get("Country"));
		SAssert.assertEquals(ChannelUsersEnquiryDAO.get("Email"), ChannelUserMap.get("Email"));
		
		SAssert.assertAll();
		
		return Screenshot;
	}
	
}
