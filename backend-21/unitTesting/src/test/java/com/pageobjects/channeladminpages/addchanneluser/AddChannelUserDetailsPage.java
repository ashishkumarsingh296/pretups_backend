package com.pageobjects.channeladminpages.addchanneluser;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils.SwitchWindow;
import com.utils._masterVO;

public class AddChannelUserDetailsPage extends BaseTest{
	
	@FindBy(name = "firstName")
	private WebElement firstName;

	@FindBy(name = "lastName")
	private WebElement lastName;

	@FindBy(name="paymentTypes")
    private WebElement paymentTypes;
	
	@FindBy(name = "shortName")
	private WebElement shortName;

	@FindBy(name = "channelUserName")
	private WebElement userName;

	@FindBy(name="documentType")
	private WebElement documentType;
	
	@FindBy(name="documentNo")
	private WebElement documentNumber;
	
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

	@FindBy(name = "contactPerson")
	private WebElement contactPerson;

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

	@FindBy(name = "userLanguage")
	private WebElement userLanguage;

	@FindBy(name = "longitude")
	private WebElement longitude;

	@FindBy(name = "latitude")
	private WebElement latitude;

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

	@FindBy(linkText = "Assign services")
	private WebElement assignServices;

	@FindBy(xpath = "//input[@type='radio' and @name ='roleType' and @value='Y']")
	private WebElement GroupRole;
	
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

	@FindBy(name = "save")
	private WebElement saveButton;

	@FindBy(name = "reset")
	private WebElement resetButton;

	@FindBy(name = "back")
	private WebElement backButton;

	@FindBy(name = "status")
	private WebElement userStatus;
	
	@FindBy(name = "outsuspend")
	private WebElement outSuspendChkBox;
	
	@FindBy(name = "insuspend")
	private WebElement inSuspendChkBox;

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
	
	@FindBy(xpath="//input[@type='text'][@name[contains(.,'searchDomainTextArrayIndexed[0]')]]")
	private WebElement searchIndex;

	/*
	 * Assign domains page objects
	 */
	@FindBy(name = "checkall")
	private WebElement checkAlldomains;

	@FindBy(name = "addDomain")
	private WebElement addDomainButton;

	@FindBy(name = "reset")
	private WebElement resetDomainButton;

	/*
	 * Assign products page Objects
	 */
	@FindBy(name = "checkall")
	private WebElement checkAllProducts;

	@FindBy(name = "addProducts")
	private WebElement addProductsButton;

	@FindBy(name = "reset")
	private WebElement resetProductsButton;

	/*
	 * Assign MSISDN page object
	 */
	@FindBy(name = "msisdnListIndexed[0].msisdn")
	private WebElement enterPhoneNumber;

	@FindBy(name = "msisdnListIndexed[0].showSmsPin")
	private WebElement enterSMSPIN;

	@FindBy(name = "msisdnListIndexed[0].pinRequired")
	private WebElement checkSMSPIN;
	
	@FindBy(name = "msisdnListIndexed[0].confirmSmsPin")
	private WebElement enterConfirmPIN;

	@FindBy(name = "msisdnListIndexed[0].phoneProfile")
	private WebElement selectphoneProfile;

	@FindBy(name = "msisdnListIndexed[0].description")
	private WebElement enterDescription;

	@FindBy(name = "addPhone")
	private WebElement addPhoneButton;

	/*
	 * To close new popup window
	 */
	@FindBy(xpath = "//a [@href='javascript:window.close()']")
	private WebElement CloseWindow;

	/*
	 * Assign roles Page objects.
	 */

	@FindBy(name = "checkall")
	private WebElement checkAllRoles;
	
	@FindBy(xpath = "//input[@value='INITVOMSOREQ']")
	private WebElement voucherOrderRequest;

	@FindBy(name = "checkNetwork")
	private WebElement checkAllNetworks;

	@FindBy(name = "addRoles")
	private WebElement addRolesButton;

	@FindBy(name = "reset")
	private WebElement resetRolesButton;

	@FindBy(name = "confirm")
	private WebElement confirmButton;

	/*
	 * Assign services
	 */
	@FindBy(name = "checkall")
	private WebElement checkAllServices;

	@FindBy(name = "addServices")
	private WebElement addServicesButton;
	
	@FindBy(xpath="//ul/li")
	private WebElement actualMessage;
	
	@FindBy(xpath="//ol")
	private WebElement errorMessage;
	
	//Outlets
	@FindBy(name="outletCode")
	private WebElement outletCode;
	
	@FindBy(name="subOutletCode")
	private WebElement subOutletCode;
	
	//Staff user
	@FindBy(linkText = "Assign phone")
	private WebElement assignPhone;
    //Assign  voucher Types
	@FindBy(name="checkall")
	private WebElement checkAllVoucherType;
	
	@FindBy(name="addVoucherType")
	private WebElement addVoucherType;
	
	@FindBy(name="addVoucherSegment")
	private WebElement addVoucherSegment;
	
	@FindBy(linkText = "assign voucher type")
	private WebElement assignVoucherType;
	
	@FindBy(linkText = "assign voucher segment")
	private WebElement assignVoucherSegment;
	
	WebDriver driver = null;
	WebDriverWait wait=null;
	public boolean w1, w2, w3, w4, w5, w6, w7, w8,w9,w10;
	private static String MasterSheetPath;

	public AddChannelUserDetailsPage(WebDriver driver) {
		this.driver = driver;
		wait= new WebDriverWait(driver, 50);
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
		try {
		Log.info("Trying to enter Short Name");
		shortName.sendKeys(ShortName);
		Log.info("Short Name entered as: " + ShortName);
		}
		catch (Exception e){ 
			Log.info("Short Name field not found: ");
			Log.writeStackTrace(e);
		}
	}

	public void selectUserNamePrefix(int index) {
		Log.info("Trying to select User Name Prefix");
		Select select = new Select(userNamePrefix);
		select.selectByIndex(index);
		Log.info("User Name Prefix selected successfully.");
	}
	
	public void selectDocumentType(String value) {
		Log.info("Trying to select User Name Prefix");
		Select select = new Select(documentType);
		select.selectByValue(value);
		Log.info("Document Type selected selected successfully.");
	}
	public void enterDocumentNumber(String value) {
		try {
			Log.info("Trying to enter Document Number");
			documentNumber.sendKeys(value);
			Log.info("Document Number entered as: " + value);
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Document Number field not found");
		}
	}
	public void selectPaymentType(String value)
	{
		try {
		List<WebElement> e = driver.findElements(By.xpath("//input[@type='checkbox' and @name ='paymentTypes']"));
		
		int size = e.size();	
		Log.info("Trying to click payment mode");
		if(value.equals("ALL"))
		{
		//	String[] str ={"DD","CHQ","OTH","CASH","ONLINE"};
			for(int i=1;i<=size;i++)
			{//	WebElement checkBox = driver.findElement(By.xpath("//input[@type='checkbox' and @value ='"+str[i]+"']"));
			
			WebElement checkBox = driver.findElement(By.xpath("//input[@type='checkbox' and @name ='paymentTypes']["+i+"]"));
			checkBox.click();
			}
			}
		else {
			WebElement checkBox = driver.findElement(By.xpath("//input[@type='checkbox' and @value = '"+value+"']"));
			checkBox.click();}
			Log.info(value+"Payment mode is clicked successfully");
			
		}catch(NoSuchElementException e) {
			Log.info("PaymentType checkbox not found");
		}
		
	}
	
	
	public void selectStatus(String status) {
		try {
			Log.info("Trying to select Status");
			Select select = new Select(userStatus);
			select.selectByValue(status);
			Log.info("Status selected as: " + status);
		} catch (Exception e) {
			Log.info("Status drop down not found");
		}
	}
   
	public void selectLanguage(String language) {
		try {
			Log.info("Trying to select Language");
			Select select = new Select(userLanguage);
			select.selectByVisibleText(language);
			Log.info("Language selected as: " + language);
		} catch (Exception e) {
			Log.info("Language drop down not found");
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
			Log.info("Trying to enter Mobile Number");
			mobileNumber.sendKeys(MobileNumber);
			Log.info("Mobile Number entered as: " + MobileNumber);
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("MobileNumber field not found");
		}
	}
	public void assignVoucherType() {
		try {
			Log.info("Trying to click Assign Voucher Type link");
			w9 = assignVoucherType.isDisplayed();
			assignVoucherType.click();
			Log.info("Assign voucher Type link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign voucher Type link not exist");
		}

	}
	public void assignVoucherType1() {
		if (w9 == true) {
			try {
				Log.info("Trying to click ALL Voucher Type");
				checkAllVoucherType.click();
				Log.info("All Voucher Types clicked successfully for Assign Voucher Type");
				Log.info("Trying to click Add button for Assign Voucher Type");
				addVoucherType.click();
				Log.info("Add button for assign voucher Type clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				driver.close();
				Log.info("Elements not found on Assign Voucher Type Popup Window");
			}
		} else {
			Log.info("Assign Voucher Type link not found");
		}
	}
	
	
	public void assignVoucherSegment() {
		try {
			Log.info("Trying to click Voucher Segment Type link");
			w10 = assignVoucherSegment.isDisplayed();
			assignVoucherSegment.click();
			Log.info("Assign voucher Segment link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign voucher Segment link not exist");
		}

	}
	
	public void assignVoucherSegment1() {
		if (w10 == true) {
			try {
				Log.info("Trying to click ALL Voucher Segment");
				checkAllVoucherType.click();
				Log.info("All Voucher Segments clicked successfully for Assign Voucher Type");
				Log.info("Trying to click Add button for Assign Voucher Type");
				addVoucherSegment.click();
				Log.info("Add button for assign voucher Segment clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				driver.close();
				Log.info("Elements not found on Assign Voucher Segment Popup Window");
			}
		} else {
			Log.info("Assign Voucher Segment link not found");
		}
	}
	
	public void assignPhoneNumber() {
		try {
			Log.info("Trying to click Assign Phone number link");
			w1 = assignPhoneNumbers.isDisplayed();
			assignPhoneNumbers.click();
			Log.info("Assign phone number link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign phone number link not exist");
		}

	}

	
	String windowID, windowID_new;
	public void assignPhoneNumber1(String MobileNumber, String PIN) {
		String errorMsg_Phone=null;
		if (w1 == true) {
			try {
				Log.info("Trying to enter Mobile Number");
				enterPhoneNumber.clear(); enterPhoneNumber.sendKeys(MobileNumber);
				Log.info("Mobile Number entered as: " + MobileNumber);
				windowID=SwitchWindow.getCurrentWindowID(driver);
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Mobile Number field not found");
			}
			try {
				String autoPINallowed=DBHandler.AccessHandler.getSystemPreference("AUTO_PIN_GENERATE_ALLOW").toUpperCase();
				System.out.println("value of autoPINallowed:: "+autoPINallowed);
				if(autoPINallowed.equals("FALSE"))
				{
				Log.info("Trying to enter PIN");
				enterSMSPIN.clear(); enterSMSPIN.sendKeys(PIN);
				Log.info("PIN Number entered as: " + PIN);
				Log.info("Trying to Enter Confirm PIN");
				enterConfirmPIN.clear(); enterConfirmPIN.sendKeys(PIN);
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
				enterDescription.clear(); enterDescription.sendKeys("Phone Number");
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
			try {	windowID_new=SwitchWindow.getCurrentWindowID(driver);
					Log.info("WindowID captured previously:: "+windowID+" || currentWindowID:: "+windowID_new);
					if(windowID_new.equals(windowID)){
					errorMsg_Phone = errorMessage.getText();
					ExtentI.attachScreenShot();
					CONSTANT.CU_ASSIGNPHONENO_ERR = errorMsg_Phone;
					currentNode.log(Status.INFO, MarkupHelper.createLabel(errorMsg_Phone, ExtentColor.RED));
					Log.info("Trying to Close Phone Number Popup Window");
					CloseWindow.click();
					Log.info("Popup window closed successfully");}
					else{driver.close();}
				
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

	public void selectDivision() throws InterruptedException, IOException {
		Log.info("Trying to select Division");
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.DIVISION_DEPT_SHEET);
		String DivisionName = ExcelUtility.getCellData(1, 0);
		Select select = new Select(division);
		select.selectByVisibleText(DivisionName);
		Log.info("Division selected successfully");
	}

	public void selectDepartment() throws InterruptedException, IOException {
		Log.info("Trying to select Department");
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.DIVISION_DEPT_SHEET);
		String DepartmentName = ExcelUtility.getCellData(1, 1);
		Select select = new Select(department);
		select.selectByVisibleText(DepartmentName);
		Log.info("Department selected successfully");
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
		Log.info("Country entered as:" + Country);
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
			Log.info("Trying to check if Password field exists");
			w7 = password.isDisplayed();
			Log.info("Password field found");
			Log.info("Trying to enter Password");
			password.sendKeys(Password);
			Log.info("Password entered as: " + Password);
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Password field not found");
		}
	}

	public void enterConfirmPassword(String ConfirmPassword) {
		if (w7 == true) {
			try {
				Log.info("Trying to enter Confirm Password");
				confirmPassword.sendKeys(ConfirmPassword);
				Log.info("Confirm Password entered as: " + ConfirmPassword);
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Confirm password field not found");
			}
		}
	}

	public void enterAllowedIPs(String AllowedIPs) {
		Log.info("Trying to select allowed IPs");
		allowedIPs.sendKeys(AllowedIPs);
		Log.info("Allowed IPs entered as: " + AllowedIPs);
	}

	public void enterAllowedFormTime(String AllowedFormTime) {
		Log.info("Trying to enter Allowed from time");
		allowedFormTime.sendKeys(AllowedFormTime);
		Log.info("Allowed From Time entered as: " + AllowedFormTime);
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
			Log.info("Checking if Assign Geographies link exists");
			w2 = assignGeographies.isDisplayed();
			Log.info("Assign Geographies link found");
			Log.info("Trying to click Assign Geographies link");
			assignGeographies.click();
			Log.info("Assign Geography link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign geographies link not found");
		}
	}

	public void assignGeographies1(String geoRow, String geo) {
		if (w2 == true) {
			try{
				int searchIndexSize = driver.findElements(By.xpath("//input[@type='text'][@name[contains(.,'searchDomainTextArrayIndexed')]]")).size();
				if(searchIndexSize>0){
				String MasterSheetPath = _masterVO.getProperty("DataProvider");
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
				int geoRowNum = Integer.parseInt(geoRow);
				for(int i=searchIndexSize,j=0; i>=1 ;i--,j++){
					int rowToTrace = geoRowNum-i;
					String geoDomainName = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, rowToTrace);
					WebElement geoWebElement = driver.findElement(By.xpath("//input[@type='text'][@name[contains(.,'searchDomainTextArrayIndexed["+j+"]')]]"));	
					WebElement geoSearchImg = driver.findElement(By.xpath("//a[@href[contains(.,'"+j+"')]]/img[@src='/pretups/jsp/common/images/search.gif']"));
					geoWebElement.sendKeys(geoDomainName);
					geoSearchImg.click();
					String homepage1;
					Set<String> windows = driver.getWindowHandles();
					homepage1 = driver.getWindowHandle();
					Iterator iterator = windows.iterator();
					String currentWindowID;
					while (iterator.hasNext()) {
						currentWindowID = iterator.next().toString();
						if (!currentWindowID.equals(homepage1))
							driver.switchTo().window(currentWindowID);
					}
					Log.info("Trying to switch window.");
					driver.switchTo().window(homepage1);
					driver.switchTo().frame(0);
					Log.info("CurrentURl : " + driver.getCurrentUrl());
					Log.info("Window Switched.");
					Thread.sleep(2000);
					
				}
			}
				}
			catch(Exception e){
				Log.writeStackTrace(e);
			}
			try {
				Log.info("Trying to select Geography");
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@value='"+geo.toUpperCase()+"']")));
				WebElement geography = driver.findElement(By.xpath("//input[@value='"+geo.toUpperCase()+"']"));
				geography.click();
				Log.info("Geography selected successfully");
			} catch (Exception e) {
				Log.writeStackTrace(e);
				boolean exist=false;
				try{exist = checkNetwork.isDisplayed();
				exist=true;}
				catch(Exception e1){Log.info("name = 'checkNetwork' element not found.");}

				if (exist) {
					Log.info("Trying to select Geography");
					checkNetwork.click();
					Log.info("Mulitple geographical codes found and selected");
				} else {
					Log.info("Not able to select geography");
				}
			}

			try {
				Log.info("Trying to click Add button");
				addGeographyButton.click();
				Log.info("Add button for geography clicked successfully");
				Log.info("Geographies has been assigned to the user successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on new popup window");
			}
		} else {
			Log.info("Assign Geographies link not found");
		}
	}

	public void selectGroupRoleRadioButton(){
		Log.info("trying to select Radio button for Group Role");
		if(!GroupRole.isSelected()){		
		GroupRole.click();
		Log.info("Group Role Radio button is now selected");
		} else
		{
			Log.info("Group Role is already selected");
		}
				
				
	}
	public void assignRoles() {
		Log.info("Trying to check if Assign Roles link exists");
		try {
			w3 = assignRoles.isDisplayed();
			Log.info("Assign Roles link found");
			Log.info("Trying to click assign roles link");
			assignRoles.click();
			Log.info("Assign Roles link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign Roles link not found");
		}

	}

	public void assignRoles1() {
		if (w3 == true) {
			try {
				Log.info("Trying to select All for assign Roles");
				checkAllRoles.click();
				Log.info("All selected in Assign Roles");
				Log.info("Trying to click Add button for roles");
				addRolesButton.click();
				Log.info("Add button clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on Assign Roles Popup window");
			}
		} else {
			Log.info("Assign Roles link not found");
		}
	}
	
	public void uncheckOrderRequest() {
		if (voucherOrderRequest.isSelected()) {
			try {
				Log.info("Trying to un select order request");
				voucherOrderRequest.click();
				Log.info("unselected order request");
				addRolesButton.click();
				Log.info("Add button clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on Assign Roles Popup window");
			}
		} else {
			Log.info("Voucher Order Request Roles link is not selected");
		}
	}
	
	public boolean groupRoleExistenceCheck(String GroupRole){
		try{
			if(driver.findElement(By.xpath("//input[@value='"+GroupRole+"']")).isDisplayed())
				Log.info("Group Role already exists as: " +GroupRole);
			return true;
		}
		catch(NoSuchElementException e){
			Log.info("Group Role doesn't exist..");
			return false;
		}

	}
	
	
	public void assignGroupRole(String roleName) {
		if (w3 == true) {
			try {
				Log.info("Trying to select group role as:" +roleName);
				WebElement radio = driver.findElement(By.xpath("//input[@type='radio' and @value = '"+roleName+"']"));
				radio.click();
				Log.info("Group Role selected");
				Log.info("Trying to click Add button for roles");
				addRolesButton.click();
				Log.info("Add button clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on Assign Roles Popup window");
			}
		} else {
			Log.info("Assign Roles link not found");
		}
	}

	public String msisdnPREFIX() {
		String Prefix = null;
			Log.info("Trying to fetch MSISDN Prefix from DataProvider");
			//MasterSheetPath = _masterVO.getProperty("DataProvider");
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
			Log.info("Trying to click on Assign Network link");
			assignNetwork.click();
			Log.info("Assign Network link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign Network link not found");
		}
	}

	public void assignNetwork1() {
		if (w4 == true) {
			try {
				Log.info("Trying to click ALL for assign network");
				checkAllNetworks.click();
				Log.info("ALL Network Assigned successfully");
				Log.info("Trying to click Add button for assign Network");
				addGeographyButton.click();
				Log.info("Add button for assign network clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on Assign Network Popup window");
			}
		} else {
			Log.info("Assign Network link not found");
		}
	}

	public void assignDomains() {
		try {
			Log.info("Trying to check if Assign Domains");
			w5 = assigndomains.isDisplayed();
			assigndomains.click();
			Log.info("Assign Domain link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign Domains link not found");
		}

	}

	public void assignDomains1() {
		if (w5 == true) {
			Log.info("Trying to click ALL for assign domains");
			try {
				checkAlldomains.click();
				Log.info("ALL selected successfully for domains");
				Log.info("Trying to click Add button for assign domains");
				addDomainButton.click();
				Log.info("Add button for assign domains clicked successfully");
			} catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Elements not found on new Assign Domain Popup Window");
			}
		} else {
			Log.info("Assign Domains link not found");
		}
	}

	public void assignProducts() {
		try {
			Log.info("Trying to check if Assign Products link exists");
			w6 = assignProducts.isDisplayed();
			Log.info("Assign Products link found");
			Log.info("Trying to click Assign Products link");
			assignProducts.click();
			Log.info("Assign Products link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign Products link not found");
		}
	}

	public void assignProducts1() {
		if (w6 == true) {
			try {
				Log.info("Trying to click ALL for assign Products");
				checkAllProducts.click();
				Log.info("ALL clicked successfully for assign products");
				Log.info("Trying to click Add button for assign products");
				addProductsButton.click();
				Log.info("Add button for assign Products clicked successfully");
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
		Log.info("Trying to click Confirm button");
		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(confirmButton));
		confirmButton.click();
		Log.info("Confirm Button clicked successfully");
	}
	
	public void selectOutlet() {
		try{
		Log.info("Trying to select outlet.");
		Select select = new Select(outletCode);
		select.selectByIndex(1);
		Log.info("Outlet selected successfully. ");}
		catch(Exception e){
			Log.info("Outlet field does not exist.");
		}
	}
	
	public void selectSubOutlet() {
		try{
		Log.info("Trying to select Sub-Outlet.");
		Select select = new Select(subOutletCode);
		select.selectByIndex(1);
		Log.info("Sub-Outlet selected successfully. ");}
		catch(Exception e){
			Log.info("Sub-Outlet field does not exist.");
		}
	}
	
	public void checkOutSuspend(){
		Log.info("Trying to check checkbox Out Suspend.");
		if(!outSuspendChkBox.isSelected())
		{outSuspendChkBox.click();
		Log.info("Out Suspend checkbox clicked successfully.");}
		Log.info("Out Suspend checkbox already checked");
	}
	
	public void inSuspended(String status){
		Log.info("Trying to set InSuspended Checkbox status to : " + status);
		if (status.equals("true") && !inSuspendChkBox.isSelected()) {
			inSuspendChkBox.click();
			Log.info("In Suspend checkbox clicked successfully.");
		}
		else if (status.equals("false") && inSuspendChkBox.isSelected()) {
			inSuspendChkBox.click();
			Log.info("In Suspend checkbox clicked successfully.");
		}
	}
	
	public void uncheckOutSuspend(){
		Log.info("Trying to uncheck checkbox Out Suspend.");
		if(outSuspendChkBox.isSelected())
		{outSuspendChkBox.click();
		Log.info("Out Suspend checkbox unchecked successfully.");}
		Log.info("Out Suspend checkbox already unchecked");
	}
	
	public String getActualMessage(){
		String message=null;
		try{
		Log.info("Trying to fetch success message.");
		message=actualMessage.getText();
		Log.info("Message fetched as :: "+message);
		}
		catch(Exception e){
			try{Log.info("Success message not found.");
			message=errorMessage.getText();
			Log.info("Message fetched as : "+message);} catch(Exception e1){Log.info("No message found on screen.");}
			
		}
		return message;
	}
	
	public void assignStaffPhone() {
		try {
			Log.info("Trying to click Assign Phone link");
			w1 = assignPhone.isDisplayed();
			assignPhone.click();
			Log.info("Assign phone link clicked successfully");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Assign phone link not exist");
		}

	}
}
