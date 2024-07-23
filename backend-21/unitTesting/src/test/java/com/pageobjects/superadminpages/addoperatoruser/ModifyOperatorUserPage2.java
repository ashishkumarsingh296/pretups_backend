/**
 * 
 */
package com.pageobjects.superadminpages.addoperatoruser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class ModifyOperatorUserPage2 {

	@FindBy(name = "status")
	private WebElement status;
	
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
	
	@FindBy(name="save")
	private WebElement modifyBtn;
	
	@FindBy(name="delete")
	private WebElement deleteBtn;
	
	@FindBy(name="reset")
	private WebElement reset;
	
	@FindBy(name="back")
	private WebElement backBtn;
	
	@FindBy(name="confirm")
	private WebElement confirmBtn;
	
	WebDriver driver = null;
	
	public ModifyOperatorUserPage2(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectStatus(String Status) {
		Select opStatus = new Select(status);
		opStatus.selectByValue(Status);
		Log.info("User selected :"+Status);
	}
	
	public void modifyAddress1(String Address1) {
		Log.info("Trying to enter Address1");
		address1.clear();
		address1.sendKeys(Address1);
		Log.info("Address1 entered as: " + Address1);
	}

	public void modifyAddress2(String Address2) {
		Log.info("Trying to enter Address2");
		address2.clear();
		address2.sendKeys(Address2);
		Log.info("Address2 entered as: " + Address2);
	}

	public void modifyCity(String City) {
		Log.info("Trying to enter City");
		city.clear();
		city.sendKeys(City);
		Log.info("City entered as: " + City);
	}

	public void modifyState(String State) {
		Log.info("Trying to enter State");
		state.clear();
		state.sendKeys(State);
		Log.info("State entered as: " + State);
	}

	public void modifyCountry(String Country) {
		Log.info("Trying to enter Country");
		country.clear();
		country.sendKeys(Country);
		Log.info("Country entered as: " + Country);
	}

	public void modifyEmailID(String EmailID) {
		Log.info("Trying to enter Email ID");
		emailID.clear();
		emailID.sendKeys(EmailID);
		Log.info("Email ID entered as: " + EmailID);
	}

	public void modifyLoginID(String LoginID) {
		Log.info("Trying to enter Login ID");
		loginID.clear();
		loginID.sendKeys(LoginID);
		Log.info("Login ID entered as: " + LoginID);
	}
	
	public void clickModifyButton(){
		Log.info("Trying to click modify button.");
		modifyBtn.click();
		Log.info("Modify button clicked successfully");
	} 
	
	public void clickDeleteButton(){
		Log.info("Trying to click delete button.");
		deleteBtn.click();
		Log.info("Delete button clicked successfully");
	} 
	
	public void clickResetButton(){
		Log.info("Trying to click reset button.");
		reset.click();
		Log.info("Reset button clicked successfully");
	} 
	
	public void clickConfirmButton(){
		Log.info("Trying to click confirm button.");
		confirmBtn.click();
		Log.info("Confirm button clicked successfully");
	} 
	
}
