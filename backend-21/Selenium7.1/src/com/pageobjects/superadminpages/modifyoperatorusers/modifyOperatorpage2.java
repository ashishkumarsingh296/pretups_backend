package com.pageobjects.superadminpages.modifyoperatorusers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class modifyOperatorpage2 {

	WebDriver driver = null;

	public modifyOperatorpage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "firstName")
	private WebElement firstName;

	@FindBy(name = "lastName")
	private WebElement lastName;

	@FindBy(name = "shortName")
	private WebElement shortName;

	@FindBy(name = "externalCode")
	private WebElement externalCode;

	@FindBy(name = "empCode")
	private WebElement empCode;

	@FindBy(name = "msisdn")
	private WebElement msisdn;

	@FindBy(name = "ssn")
	private WebElement ssn;

	@FindBy(name = "contactNo")
	private WebElement contactNo;

	@FindBy(name = "designation")
	private WebElement designation;

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
	private WebElement email;

	@FindBy(name = "appointmentDate")
	private WebElement appointmentDate;

	@FindBy(name = "webLoginID")
	private WebElement webLoginID;

	@FindBy(name = "allowedFormTime")
	private WebElement allowedFormTime;

	@FindBy(name = "allowedToTime")
	private WebElement allowedToTime;

	@FindBy(name = "userNamePrefixCode")
	private WebElement userNamePrefixCode;

	@FindBy(name = "status")
	private WebElement status;

	@FindBy(name = "divisionCode")
	private WebElement divisionCode;

	@FindBy(name = "departmentCode")
	private WebElement departmentCode;

	@FindBy(name = "save")
	private WebElement save;

	@FindBy(name = "delete")
	private WebElement delete;

	@FindBy(name = "reset")
	private WebElement reset;

	@FindBy(name = "back")
	private WebElement back;

	public void EnterfirstName(String value) {
		Log.info("Trying to enter  value in firstName ");
		firstName.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterlastName(String value) {
		Log.info("Trying to enter  value in lastName ");
		lastName.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EntershortName(String value) {
		Log.info("Trying to enter  value in shortName ");
		shortName.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterexternalCode(String value) {
		Log.info("Trying to enter  value in externalCode ");
		externalCode.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterempCode(String value) {
		Log.info("Trying to enter  value in empCode ");
		empCode.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void Entermsisdn(String value) {
		Log.info("Trying to enter  value in msisdn ");
		msisdn.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void Enterssn(String value) {
		Log.info("Trying to enter  value in ssn ");
		ssn.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EntercontactNo(String value) {
		Log.info("Trying to enter  value in contactNo ");
		contactNo.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void Enterdesignation(String value) {
		Log.info("Trying to enter  value in designation ");
		designation.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void Enteraddress1(String value) {
		Log.info("Trying to enter  value in address1 ");
		address1.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void Enteraddress2(String value) {
		Log.info("Trying to enter  value in address2 ");
		address2.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void Entercity(String value) {
		Log.info("Trying to enter  value in city ");
		city.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void Enterstate(String value) {
		Log.info("Trying to enter  value in state ");
		state.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void Entercountry(String value) {
		Log.info("Trying to enter  value in country ");
		country.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void Enteremail(String value) {
		Log.info("Trying to enter  value in email ");
		email.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterappointmentDate(String value) {
		Log.info("Trying to enter  value in appointmentDate ");
		appointmentDate.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterwebLoginID(String value) {
		Log.info("Trying to enter  value in webLoginID ");
		webLoginID.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterallowedFormTime(String value) {
		Log.info("Trying to enter  value in allowedFormTime ");
		allowedFormTime.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterallowedToTime(String value) {
		Log.info("Trying to enter  value in allowedToTime ");
		allowedToTime.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void SelectuserNamePrefixCode(String value) {
		Log.info("Trying to Select   userNamePrefixCode ");
		Select select = new Select(userNamePrefixCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void Selectstatus(String value) {
		Log.info("Trying to Select   status ");
		Select select = new Select(status);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectdivisionCode(String value) {
		Log.info("Trying to Select   divisionCode ");
		Select select = new Select(divisionCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectdepartmentCode(String value) {
		Log.info("Trying to Select   departmentCode ");
		Select select = new Select(departmentCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void ClickOnsave() {
		Log.info("Trying to click on button  Modify ");
		save.click();
		Log.info("Clicked on  Modify successfully");
	}

	public void ClickOndelete() {
		Log.info("Trying to click on button  Delete ");
		delete.click();
		Log.info("Clicked on  Delete successfully");
	}

	public void ClickOnreset() {
		Log.info("Trying to click on button  Reset ");
		reset.click();
		Log.info("Clicked on  Reset successfully");
	}

	public void ClickOnback() {
		Log.info("Trying to click on button  Back ");
		back.click();
		Log.info("Clicked on  Back successfully");
	}

}
