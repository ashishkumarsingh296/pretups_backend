package com.pageobjects.superadminpages.addoperatoruser;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AddOperatorUserDetailsPage {

	@FindBy(name = "firstName")
	private WebElement firstName;

	@FindBy(name = "lastName")
	private WebElement lastName;

	@FindBy(name = "shortName")
	private WebElement shortName;

	@FindBy(name = "userName")
	private WebElement userName;

	@FindBy(name = "userNamePrefixCode")
	private WebElement userNamePrefix;

	@FindBy(name = "externalCode")
	private WebElement externalCode;

	@FindBy(name = "empCode")
	private WebElement subscriberCode;

	@FindBy(name = "msisdn")
	private WebElement mobileNumber;

	@FindBy(name = "ssn")
	private WebElement ssn;

	@FindBy(name = "contactNo")
	private WebElement contactNo;

	@FindBy(name = "designation")
	private WebElement designation;

	@FindBy(name = "divisionCode")
	private WebElement division;

	@FindBy(name = "departmentCode")
	private WebElement department;

	@FindBy(name = "address1")
	private WebElement address1;

	@FindBy(name = "address2")
	private WebElement address2;

	@FindBy(name = "city")
	private WebElement city;

	@FindBy(name = "state")
	private WebElement state;

	@FindBy(name = "country")
	private WebElement country;

	@FindBy(name = "email")
	private WebElement emailID;

	@FindBy(name = "appointmentDate")
	private WebElement appointmentDate;

	@FindBy(name = "webLoginID")
	private WebElement loginID;

	@FindBy(name = "showPassword")
	private WebElement password;

	@FindBy(name = "confirmPassword")
	private WebElement confirmPassword;

	@FindBy(name = "allowedIPs")
	private WebElement allowedIPs;

	@FindBy(name = "allowedFormTime")
	private WebElement allowedFormTime;

	@FindBy(name = "allowedToTime")
	private WebElement allowedToTime;

	@FindBy(linkText = "Assign Network")
	private WebElement assignNetwork;

	@FindBy(linkText = "Assign domains")
	private WebElement assigndomains;

	@FindBy(linkText = "Assign phone numbers")
	private WebElement assignPhoneNumbers;

	@FindBy(linkText = "Assign products")
	private WebElement assignProducts;

	@FindBy(linkText = "Assign geographies")
	private WebElement assignGeographies;

	@FindBy(linkText = "Assign roles")
	private WebElement assignRoles;
	
	@FindBy(linkText = "Assign services")
	private WebElement assignServices;

	@FindBy(name = "save")
	private WebElement saveButton;

	@FindBy(name = "reset")
	private WebElement resetButton;

	@FindBy(name = "back")
	private WebElement backButton;

	@FindBy(name = "status")
	private WebElement Status;

	/*
	 * Assign Geography Page objects.
	 */
	@FindBy(name = "checkNetwork")
	private WebElement checkNetwork;

	@FindBy(name = "geographicalCode")
	private WebElement geographicalCode;

	@FindBy(name = "addGeography")
	private WebElement addGeographyButton;

	@FindBy(name = "reset")
	private WebElement resetGeographyButton;
	
	@FindBy(name="geographicalCodeArray")
	private WebElement geographicalCodeArray;

	// Assign domains page objects
	@FindBy(name = "checkall")
	private WebElement checkAlldomains;

	@FindBy(name = "addDomain")
	private WebElement addDomainButton;

	@FindBy(name = "reset")
	private WebElement resetDomainButton;

	// Assign products page Objects
	@FindBy(name = "checkall")
	private WebElement checkAllProducts;

	@FindBy(name = "addProducts")
	private WebElement addProductsButton;

	@FindBy(name = "reset")
	private WebElement resetProductsButton;

	// Assign MSISDN page object
	@FindBy(name = "msisdnListIndexed[0].msisdn")
	private WebElement enterPhoneNumber;

	@FindBy(name = "msisdnListIndexed[0].showSmsPin")
	private WebElement enterSMSPIN;

	@FindBy(name = "msisdnListIndexed[0].confirmSmsPin")
	private WebElement enterConfirmPIN;

	@FindBy(name = "msisdnListIndexed[0].phoneProfile")
	private WebElement selectphoneProfile;

	@FindBy(name = "msisdnListIndexed[0].description")
	private WebElement enterDescription;

	@FindBy(name = "addPhone")
	private WebElement addPhoneButton;

	/*
	 * Assign services
	 */
	@FindBy(name = "checkall")
	private WebElement checkAllServices;

	@FindBy(name = "addServices")
	private WebElement addServicesButton;
	
	// To close new popup window
	@FindBy(xpath = "//a [@href='javascript:window.close()']")
	private WebElement CloseWindow;

	// Assign roles Page objects.
	@FindBy(name = "checkall")
	private WebElement checkAllRoles;

	@FindBy(name = "checkNetwork")
	private WebElement checkAllNetworks;

	@FindBy(name = "addRoles")
	private WebElement addRolesButton;

	@FindBy(name = "reset")
	private WebElement resetRolesButton;

	@FindBy(name = "confirm")
	private WebElement confirmButton;
	
	@FindBy(xpath="//ul/li")
	private WebElement actualMessage;
	
	@FindBy(name = "msisdnListIndexed[0].pinRequired")
	private WebElement checkSMSPIN;
	
	//Assign vouchers
	@FindBy(linkText = "assign voucher type")
	private WebElement assignVoucher;
	
	@FindBy(name="checkall")
	private WebElement checkAllVoucherTypes;
	
	@FindBy(name="addVoucherType")
	private WebElement addButtonforVouchertype;
	
	WebDriver driver = null;
	public boolean w1, w2, w3, w4, w5, w6, w7, w8, vouchertypelinkexist;

	public AddOperatorUserDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterFirstName(String FirstName) {
		try {
			Log.info("Trying to enter First Name");
			firstName.sendKeys(FirstName);
			Log.info("First Name entered as: " + FirstName);
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("First Name field not found");
		}
	}

	public void enterLastName(String LastName) {
		try {
			Log.info("Trying to enter Last Name");
			lastName.sendKeys(LastName);
			Log.info("Last Name entered as: " + LastName);
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Last Name field not found");
		}
	}

	public void enterShortName(String ShortName) {
		Log.info("Trying to enter Short Name");
		shortName.sendKeys(ShortName);
		Log.info("Short Name entered as: " + ShortName);
	}

	public void selectUserNamePrefix(int index) {
		Log.info("Trying to select User Name Prefix");
		Select select = new Select(userNamePrefix);
		select.selectByIndex(index);
		Log.info("User Name prefix selected successfully");
	}

	public void selectStatus(String status) {
		try {
			Log.info("Trying to select Status");
			Select select = new Select(Status);
			select.selectByValue(status);
			Log.info("Status selected as: " + status);
		} catch (Exception e) {
			Log.info("Status drop down not found");
		}
	}

	public void enterExternalCode(String ExternalCode) {
		Log.info("Trying to enter External Code");
		externalCode.sendKeys(ExternalCode);
		Log.info("External Code entered as: " + ExternalCode);
	}

	public void enterSubscriberCode(String SubscriberCode) {
		Log.info("Trying to enter Subscriber Code");
		subscriberCode.sendKeys(SubscriberCode);
		Log.info("Subscriber Code entered as: " + SubscriberCode);
	}

	public void enterMobileNumber(String MobileNumber) {
		try {
			Log.info("Trying to enter MobileNumber");
			mobileNumber.sendKeys(MobileNumber);
			Log.info("Mobile Number entered as: " + MobileNumber);
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Mobile Number field not found");
		}
	}

	public void assignPhoneNumber() {
		try {
			Log.info("Trying to check if Assign Phone Number link exist");
			w1 = assignPhoneNumbers.isDisplayed();
			Log.info("Assign Phone Number link found");
			Log.info("Trying to click Assign Phone number link");
			assignPhoneNumbers.click();
			Log.info("Assign Phone number link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign phone number link not found");
		}

	}

	/*public void assignPhoneNumber1(String MobileNumber, String PIN) {

		if (w1 == true) {
			try {
				Log.info("Trying to enter Mobile Number");
				enterPhoneNumber.sendKeys(MobileNumber);
				Log.info("Mobile Number entered as: " + MobileNumber);
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Mobile Number field not found");
			}
			try {
				Log.info("Trying to enter PIN");
				enterSMSPIN.sendKeys(PIN);
				Log.info("PIN Number entered as: " + PIN);
				Log.info("Trying to enter Confirm PIN");
				enterConfirmPIN.sendKeys(PIN);
				Log.info("Confirm PIN entered as: " + PIN);
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("PIN field not found");
			}
			try {
				Log.info("Trying to select Phone Profile");
				Select select = new Select(selectphoneProfile);
				select.selectByIndex(1);
				Log.info("Phone Profile selected successfully.");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Phone Profile not found");
			}
			try {
				Log.info("Trying to Enter Description");
				enterDescription.sendKeys("Phone Number");
				Log.info("Description Entered successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Description field not found");
			}
			try {
				Log.info("Trying to click Add Button for Phone Number");
				addPhoneButton.click();
				Log.info("Add button for phone number clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Add Phone button not found");
			}
			try {	driver.close();
					//CloseWindow.isDisplayed();
					Log.info("Trying to click Close on Phone Number Popup Window");
					CloseWindow.click();
					Log.info("Close link clicked successfully");
				
			} catch (Exception e) {
				Log.info("Window already closed or close link not exist");
			}
		} else {
			Log.info("Phone Number link not found");
		}
	}*/
	
	public void assignPhoneNumber1(String MobileNumber, String PIN) {
		if (w1 == true) {
			try {
				Log.info("Trying to enter Mobile Number");
				enterPhoneNumber.sendKeys(MobileNumber);
				Log.info("Mobile Number entered as: " + MobileNumber);
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Mobile Number field not found");
			}
			try {
				String autoPINallowed=DBHandler.AccessHandler.getSystemPreference("AUTO_PIN_GENERATE_ALLOW").toUpperCase();
				System.out.println("value of autoPINallowed:: "+autoPINallowed);
				if(autoPINallowed.equals("FALSE"))
				{
				Log.info("Trying to enter PIN");
				enterSMSPIN.sendKeys(PIN);
				Log.info("PIN Number entered as: " + PIN);
				Log.info("Trying to Enter Confirm PIN");
				enterConfirmPIN.sendKeys(PIN);
				Log.info("Confirm PIN Number entered as: " + PIN);
				}
				else if(autoPINallowed.equals("TRUE")){
				Log.info("Trying to check SMS PIN required.");
				checkSMSPIN.click();
				Log.info("SMS PIN required checked successfuly" );
				}
			} catch (Exception e) {
				Log.info("PIN field not found");
			}
			try {
				Log.info("Trying to select Phone Profile");
				Select select = new Select(selectphoneProfile);
				select.selectByIndex(1);
				Log.info("Phone Profile selected successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Phone Profile not found");
			}
			try {
				Log.info("Trying to enter description");
				enterDescription.sendKeys("Phone Number");
				Log.info("Description entered successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Description field not found");
			}
			try {
				Log.info("Trying to click add button for phone number");
				addPhoneButton.click();
				Log.info("Add button for phone number clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Add Phone button not found");
			}
			try {	
					driver.close();
					Log.info("Trying to Close Phone Number Popup Window");
					CloseWindow.click();
					Log.info("Popup window closed successfully");
				
			} catch (Exception e) {
				Log.info("Window is already closed or close link not exist");
			}
		} else {
			Log.info("Assign Phone Numbers link not found");
		}
	}

	public void enterSSN(String SSN) {
		Log.info("Trying to enter SSN");
		ssn.sendKeys(SSN);
		Log.info("SSN entered as: " + SSN);
	}

	public void enterContactNo(String ContactNo) {
		Log.info("Trying to enter Contact Number");
		contactNo.sendKeys(ContactNo);
		Log.info("Contact No. entered as: " + ContactNo);
	}

	public void enterDesignation(String Designation) {
		Log.info("Trying to enter Designation");
		designation.sendKeys(Designation);
		Log.info("Designation entered as: " + Designation);
	}

	public void selectDivision() {
		Log.info("Trying to select Division");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.DIVISION_DEPT_SHEET);
		String DivisionName = ExcelUtility.getCellData(0, ExcelI.DIVISION, 1);
		Select select = new Select(division);
		select.selectByVisibleText(DivisionName);
		Log.info("Division selected as: " + DivisionName);
	}

	public void selectDepartment() {
		Log.info("Trying to select Department");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.DIVISION_DEPT_SHEET);
		String DepartmentName = ExcelUtility.getCellData(0, ExcelI.DEPARTMENT, 1);
		Select select = new Select(department);
		select.selectByVisibleText(DepartmentName);
		Log.info("Department selected as: " + DepartmentName);
	}

	public void enterAddress1(String Address1) {
		Log.info("Trying to enter Address1");
		address1.sendKeys(Address1);
		Log.info("Address1 entered as: " + Address1);
	}

	public void enterAddress2(String Address2) {
		Log.info("Trying to enter Address2");
		address2.sendKeys(Address2);
		Log.info("Address2 entered as: " + Address2);
	}

	public void enterCity(String City) {
		Log.info("Trying to enter City");
		city.sendKeys(City);
		Log.info("City entered as: " + City);
	}

	public void enterState(String State) {
		Log.info("Trying to enter State");
		state.sendKeys(State);
		Log.info("State entered as: " + State);
	}

	public void enterCountry(String Country) {
		Log.info("Trying to enter Country");
		country.sendKeys(Country);
		Log.info("Country entered as: " + Country);
	}

	public void enterEmailID(String EmailID) {
		Log.info("Trying to enter Email ID");
		emailID.sendKeys(EmailID);
		Log.info("Email ID entered as: " + EmailID);
	}

	public void enterAppointmentDate(String AppointmentDate) {
		Log.info("Trying to enter Appointment Date");
		appointmentDate.sendKeys(AppointmentDate);
		Log.info("Appointment Date entered as: " + AppointmentDate);
	}

	public void enterLoginID(String LoginID) {
		Log.info("Trying to enter Login ID");
		loginID.sendKeys(LoginID);
		Log.info("Login ID entered as: " + LoginID);
	}

	public void enterPassword(String Password) {
		try {
			Log.info("Trying to check if Enter Password field exist");
			w7 = password.isDisplayed();
			Log.info("Enter Password field found");
			Log.info("Trying to enter Password");
			password.sendKeys(Password);
			Log.info("Password Entered as: " + Password);
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Password field not found");
		}
	}

	public void enterConfirmPassword(String ConfirmPassword) {
		if (w7 == true) {
			try {
				Log.info("Trying to enter Confirm Password");
				confirmPassword.sendKeys(ConfirmPassword);
				Log.info("Confirm Password Entered as: " + ConfirmPassword);
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Confirm Password field not found");
			}
		}
	}

	public void enterAllowedIPs(String AllowedIPs) {
		Log.info("Trying to enter allowed IPs");
		allowedIPs.sendKeys(AllowedIPs);
		Log.info("Allowed IP entered as: " + AllowedIPs);
	}

	public void enterAllowedFormTime(String AllowedFormTime) {
		Log.info("Trying to enter Allowed from time");
		allowedFormTime.sendKeys(AllowedFormTime);
		Log.info("Allowed from time entered as: " + AllowedFormTime);
	}

	public void enterAllowedToTime(String AllowedToTime) {
		Log.info("Trying to enter Allowed To Time");
		allowedToTime.sendKeys(AllowedToTime);
		Log.info("Allowed To Time entered as: " + AllowedToTime);
	}

	/*
	 * public void clickAssignRoles() throws InterruptedException {
	 * Log.info("Trying to click AssignRoles"); try{ assignRoles.click();
	 * Log.info("User clicked Assign Roles.");} catch(NoSuchElementException e){
	 * Log.info("Assign Roles link not exist");
	 * 
	 * } }
	 */
	public void clickSaveButton() {
		Log.info("Trying to click Save button");
		saveButton.click();
		Log.info("Save Button clicked successfully");
	}

	public void clickResetButton() {
		Log.info("Trying to click Reset button");
		resetButton.click();
		Log.info("Reset Button clicked successfully");
	}

	public void clickBackButton() {
		Log.info("Trying to click Back button");
		backButton.click();
		Log.info("Back Button clicked successfully");
	}

	public void assignGeographies() {
		try {
			Log.info("Trying to check if Assign Geographies link exist");
			w2 = assignGeographies.isDisplayed();
			Log.info("Assign Geographies link found");
			Log.info("Trying to click Assign Geographies link");
			assignGeographies.click();
			Log.info("Assign Geographies link clicked successfully");
			
			// Added 3 Seconds Sleep after clicking Assign Geographies Link to let Popup Load the geographies.
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign geographies link not found");
		}
	}

	public void assignGeographies1() {
		if (w2 == true) {
			try {
				if (geographicalCode.isDisplayed()) {
					Log.info("Trying to select Geography");
					geographicalCode.click();
					Log.info("Geography selected successfully");
				}
			} catch (Exception e) {
				if (checkNetwork.isDisplayed()) {
					Log.info("Trying to select Geography");
					checkNetwork.click();
					Log.info("Multiple geogrpahical codes clicked");
				} else {
					Log.info("Not able to select geography");
				}
			}

			try {
				Log.info("Trying to click Add button");
				addGeographyButton.click();
				Log.info("Add button for geography clicked successfully");
				Log.info("Geographies has been assigned to the user.");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on Assign Geographies Popup Window");
			}
			/*
			 * try { if(CloseWindow.isDisplayed()){
			 * Log.info("Trying to click Close on popup Window");
			 * 
			 * CloseWindow.click();
			 * Log.info("Close link clicked and window closed"); } }catch
			 * (Exception e) {
			 * Log.info("Window already closed or close link not exist"); }
			 */
		} else {
			Log.info("Assign Geographies link not found");
		}
	}

	public void assignRoles() {
		try {
			Log.info("Trying to check if Assign Roles link exist");
			w3 = assignRoles.isDisplayed();
			Log.info("Trying to click Assign Roles");
			assignRoles.click();
			Log.info("Assign Roles clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign Roles link not found");
		}
	}

	public void assignRoles1() throws InterruptedException {
		if (w3 == true) {
			try {
				Log.info("Trying to check ALL option for assign Roles");
				Thread.sleep(2000);
				checkAllRoles.click();
				Log.info("ALL option selected for Assign Roles");
				Log.info("Trying to click Add button");
				Thread.sleep(2000);
				addRolesButton.click();
				Log.info("Add button clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on Assign Roles Popup Window");
			}
		} else {
			Log.info("Assign Roles link not found");
		}
	}

	public String msisdnPREFIX() {
		String Prefix = null;
		Log.info("Trying to fetch MSISDN Prefix from DataProvider");
		//String MasterSheetPath = _masterVO.getProperty("DataProvider");
		//ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.MASTER_SHEET_NAME);
		Prefix = _masterVO.getMasterValue(MasterI.PREPAID_MSISDN_PREFIX);//ExcelUtility.getCellData(13, 1);
		Log.info("MSISDN Prefix fetched successfully");
		return Prefix;
	}

	public void assignNetwork() {
		try {
			Log.info("Trying to check if Assign Network link exists");
			w4 = assignNetwork.isDisplayed();
			Log.info("Assign Network link found");
			Log.info("Trying to click Assign Network");
			assignNetwork.click();
			Log.info("User clicked Assign Network.");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign Network link not found");
		}
	}

	public void assignNetwork1() {
		if (w4 == true) {
			try {
				Log.info("Trying to select ALL option for assign network");
				checkAllNetworks.click();
				Log.info("ALL option for Assign Networks selected successfully");
				Log.info("Trying to click Add button");
				addGeographyButton.click();
				Log.info("Add button clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on Assign Network Popup Window");
			}
		} else {
			Log.info("Assign Network Link not found");
		}
	}

	public void assignDomains() {
		try {
			Log.info("Trying to check if Assign Domain link exists");
			w5 = assigndomains.isDisplayed();
			Log.info("Assign Domains link found");
			Log.info("Trying to click Assign Domains");
			assigndomains.click();
			Log.info("Assign Domains link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign Domains link not found");
		}
	}

	public void assignDomains1() {
		if (w5 == true) {
			try {
				Log.info("Trying to check ALL option for assign domain");
				checkAlldomains.click();
				Log.info("ALL option for Assign Domains selected successfully");
				Log.info("Trying to click Add button");
				addDomainButton.click();
				Log.info("Add button clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on Assign Domains Popup Window");
			}
		} else {
			Log.info("Assign Domains link not found");
		}
	}

	public void assignProducts() throws InterruptedException {
		try {
			Log.info("Trying to check if Assign Products link exist");
			w6 = assignProducts.isDisplayed();
			Log.info("Assign Products link found");
			Log.info("Trying to click Assign Products");
			Thread.sleep(2000);
			assignProducts.click();
			Log.info("Assign Products clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign Products link not found");
		}
	}

	public void assignProducts1() throws InterruptedException {
		if (w6 == true) {
			try {
				Log.info("Trying to check ALL option for assign Products");
				checkAllProducts.click();
				Log.info("ALL option for Assign Products selected successfully");
				Log.info("Trying to click Add button");
				Thread.sleep(2000);
				addProductsButton.click();
				Log.info("Add button clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on Assign Products Popup Window");
			}
		} else {
			Log.info("Assign Products link not found");
		}
	}

	public void assignServices() {
		try {
			Log.info("Trying to check if Assign Services exists");
			w8 = assignServices.isDisplayed();
			Log.info("Assign Services link found");
			Log.info("Trying to click Assign Services");
			assignServices.click();
			Log.info("Assign Services link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign Services link not found");
		}
	}

	public void assignServices1() {
		if (w8 == true) {
			try {
				Log.info("Trying to click ALL assign services");
				checkAllServices.click();
				Log.info("All services clicked successfully for Assign Services");
				Log.info("Trying to click Add button for Assign Services");
				addServicesButton.click();
				Log.info("Add button for assign services clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				driver.close();
				Log.info("Elements not found on Assign Services Popup Window");
			}
		} else {
			Log.info("Assign Services link not found");
		}
	}
	
	public void enterUserName(String UserName) {
		try {
			Log.info("Trying to enter User Name");
			userName.sendKeys(UserName);
			Log.info("User Name entered as: " + UserName);
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("User Name field not found");
		}
	}

	public void clickConfirmButton() {
		Log.info("Trying to click Confirm button.");
		confirmButton.click();
		Log.info("Confirm Button clicked successfully");
	}
	
	public String getActualMessage(){
		String message=null;
		try{
		Log.info("Trying to fetch success or reject message.");
		message=actualMessage.getText();
		Log.info("Message fetched as :: "+message);
		}
		catch(Exception e){
			Log.info("Message not found.");
			Log.writeStackTrace(e);
		}
		return message;
	}
	
	public void assignGeographies2(String geoValue) {
		if (w2 == true) {
			 
				if (checkNetwork.isDisplayed()) {
					Log.info("Trying to select Geography");
					WebDriverWait wait=new WebDriverWait(driver,10);
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='checkbox' and @value='"+geoValue+"']")));
					driver.findElement(By.xpath("//input[@type='checkbox' and @value='"+geoValue+"']")).click();
					Log.info("Geogrpahical codes checked: "+geoValue);
				} else {
					Log.info("Not able to select geography");
				}
			
			try {
				Log.info("Trying to click Add button");
				addGeographyButton.click();
				Log.info("Add button for geography clicked successfully");
				Log.info("Geographies has been assigned to the user.");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on Assign Geographies Popup Window");
			}

		} else {
			Log.info("Assign Geographies link not found");
		}
	}
	
	public void assignVouchers(){
		vouchertypelinkexist=false;
		Log.info("Trying to click 'assign voucher type' link.");
		try{assignVoucher.click();
		vouchertypelinkexist=true;
		Log.info("'assign voucher type' link clicked successfully ");}
		catch(Exception e){
			Log.info("'assign voucher type' link not found.");
		}
	}
	public void assignVouchers1(){
		if (vouchertypelinkexist == true) {
			try {
				Log.info("Trying to check ALL option for voucher type.");
				checkAllVoucherTypes.click();
				Log.info("ALL option for Voucher types selected successfully");
				Log.info("Trying to click Add button");
				addButtonforVouchertype.click();
				Log.info("Add button clicked successfully");
			} catch (Exception e) {
				Log.info("Elements not found on Assign voucher type Popup Window");
			}
		}
		else{
			Log.info("Link not found for 'assign voucher type'");
		}
	}
	
	public void selectTypeVoucher(String value){
		if (vouchertypelinkexist == true) {
			try {
				Log.info("Trying to check '"+value+"' option for voucher type.");
				driver.findElement(By.xpath("//input[@type='checkbox'][@value='"+value+"']")).click();
				Log.info("'"+value+"' option for Voucher type selected successfully");
				Log.info("Trying to click Add button");
				addButtonforVouchertype.click();
				Log.info("Add button clicked successfully");
			} catch (Exception e) {
				Log.info("Elements not found on Assign voucher type Popup Window");
			}
		}
		else{
			Log.info("Link not found for 'assign voucher type'");
		}
	}
}
