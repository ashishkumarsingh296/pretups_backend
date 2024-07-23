package com.pageobjects.networkadminpages.commissionprofile;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.dbrepository.DBHandler;
import com.utils.Log;

public class AddCommissionProfilePage {

	@FindBy(name = "profileName")
	private WebElement profileName;

	@FindBy(name = "shortCode")
	private WebElement shortCode;
	
	@FindBy(name = "dualCommType")
	private WebElement CommissionType;

	@FindBy(name = "applicableFromDate")
	private WebElement applicableFromDate;

	@FindBy(name = "applicableFromHour")
	private WebElement applicableFromHour;
	
	@FindBy(name = "commissionType")
	private WebElement commTypeSel;
	
	@FindBy(name = "commissionTypeValue")
	private WebElement commTypeValueSel;
	
	@FindBy(name = "otherCommissionProfile")
	private WebElement otherCommProfileSel;

	@FindBy(xpath = "//a[contains(@onclick, \"addCommissionSlabs('addModifyCommissionProfile\")]")
	private WebElement assignCommissionSlabs;
	
	@FindBy(xpath = "//a[contains(@onclick, \"addCommissionSlabs('addModifyOtfProfile\")]")
	private WebElement assignOtfSlabs;

	@FindBy(xpath = "//a[contains(@onclick, \"addCommissionSlabs('addModifyAdditionalProfile\")]")
	private WebElement assignAdditionalCommissionSlabs;

	@FindBy(name = "save")
	private WebElement saveButton;

	@FindBy(name = "reset")
	private WebElement resetButton;

	@FindBy(name = "back")
	private WebElement backButton;
	
	@FindBy(xpath ="//table/tbody/tr[2]/td[2]/ol/li")
	private WebElement message;
	
	@FindBy(xpath ="//span[@class='calImgSpan calendars-trigger']//img[@class='trigger']")
	private WebElement DatePicker;	
	
	@FindBy(xpath ="//a[contains(@class,'calendars-today')]")
	private WebElement applicabledate;	

	WebDriver driver = null;

	public AddCommissionProfilePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterProfileName(String ProfileName) {
		Log.info("Trying to enter Profile Name as: " + ProfileName);
		profileName.sendKeys(ProfileName);
		Log.info("Profile Name entered successfully");
	}

	public void enterShortCode(String ShortCode) {
		shortCode.sendKeys(ShortCode);
		Log.info("User entered Short Code.");
	}
	
	public void selectCommissionType(String type){
		
		int applicableCommissioningType = DBHandler.AccessHandler.getLookUpSize("COMMT");
		
		if (applicableCommissioningType > 1) {
			Log.info("Trying to select Commission Type");
			Select commType = new Select(CommissionType); 
			commType.selectByValue(type);
			Log.info("Commission Type selected as : " +type);
		} else {
			Log.info("Single Commission exists in system");
		}
		
	}
	
	
	public boolean CommissionTypeVisibility() {
		Log.info("Trying to check Commission Type DropDown exists");
		boolean result = false;
		try {
			if (CommissionType.isDisplayed()) {
				Log.info("Commission Type Dowp down exists");
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
			Log.info("Commission Type Dowp down does not exist");
		}
		return result;

	}
	
	public void enterApplicableDate()
	{
		Log.info("Trying to open Calender");
		DatePicker.click();
		Log.info("Clicking on current date :");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		applicabledate.click();
		Log.info("User clicked current date :");
	}
	

	public void enterApplicableFromDate(String ApplicableFromDate) {
		applicableFromDate.clear();
		applicableFromDate.sendKeys(ApplicableFromDate);

		Log.info("User entered Applicable From Date as : " +ApplicableFromDate);

	}

	public void enterApplicableFromHour(String ApplicableFromHour) {
		applicableFromHour.clear();
		applicableFromHour.sendKeys(ApplicableFromHour);

		Log.info("User entered Applicable From Hour as :" +ApplicableFromHour);

	}

	public void clickAssignCommissionSlabs() {
		assignCommissionSlabs.click();
		Log.info("User clicked Assign Commission Slabs.");
	}
	
	public void clickAssignOtfSlabs() {
		assignOtfSlabs.click();
		Log.info("User clicked Otf Commission Slabs.");
	}
	
	public boolean clickAssignOtfSlabsvisible() {
	boolean flag=	assignOtfSlabs.isDisplayed();
	return flag;
	}

	public void clickAssignAdditionalCommissionSlabs() {
		assignAdditionalCommissionSlabs.click();
		Log.info("User clicked Assign Additional Commission Slabs.");
	}

	public void clickSaveButton() {
		saveButton.click();
		Log.info("User clicked Save Button.");
	}

	public void clickResetButton() {
		resetButton.click();
		Log.info("User clicked Reset Button.");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back Button.");
	}
	
	public String getMessage(){
		String msg = message.getText();
		Log.info("The error message is :"+msg);
		return msg;
	}

	public boolean addAddititionalCommissionVisibility() {
		Log.info("Trying to check Additional Commission Slab Link exists");
		boolean result = false;
		try {
			if (assignAdditionalCommissionSlabs.isDisplayed()) {
				Log.info("Additional Commission Slab Link exists");
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
			Log.info("Additional Commission Slab Link does not exist");
		}
		return result;

	}
	
	
	/**
	 * 
	 * @param ApplicableFromHour
	 */
	public String modifyApplicableFromHour(String ApplicableFromHour) {
		applicableFromHour.clear();
		applicableFromHour.sendKeys(ApplicableFromHour);
		Log.info("User entered Applicable From Hour :" +ApplicableFromHour);
		return ApplicableFromHour;
	}
	
	
	public boolean CBCVisibility() { 
        Log.info("Trying to check Assign CBC Link exists"); 
        boolean result = false; 
        try { 
                if (assignOtfSlabs.isDisplayed()) { 
                        Log.info("Assign CBC Link exists"); 
                        result = true; 
                } 
        } catch (NoSuchElementException e) { 
                result = false; 
                Log.info("Assign CBC Link does not exist"); 
        } 
        return result; 
} 
	
	public void selectOtherCommissionType(String value) {
		Log.info("Trying to select other commission type: " + value);
		try {	
			Select ocpType = new Select(commTypeSel);
			ocpType.selectByVisibleText(value);
			Log.info("Selected other commission type: " + value);
		}catch(Exception e) {
			Log.info(e.getMessage());
		}	
	}
	
	public void selectOtherCommissionTypeValue(String value) {
		Log.info("Trying to select other commission type value: " + value);
		try {
			Select ocpValue = new Select(commTypeValueSel);
			ocpValue.selectByVisibleText(value);
			Log.info("Selected other commission type value: " + value);
		}catch(Exception e) {
			Log.info(e.getMessage());
		}			
	}
	
	public void selectOtherCommissionProfile(String value) {
		Log.info("Trying to select other commission profile: " + value);
		try {
			Select ocpName = new Select(otherCommProfileSel);
			ocpName.selectByVisibleText(value);
			Log.info("Selected other commission profile: " + value);
		}catch(Exception e) {
			Log.info(e.getMessage());
		}		
	}
	
}
