package com.pageobjects.superadminpages.addoperatoruser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddOperatorUserPage {
	@FindBy(name = "categoryCode")
	private WebElement category;

	@FindBy(name = "add")
	private WebElement submitButton;

	@FindBy(name = "firstName")
	private WebElement firstName;

	@FindBy(name = "lastName")
	private WebElement lastName;

	@FindBy(name = "shortName")
	private WebElement shortName;

	@FindBy(name = "userNamePrefixCode")
	private WebElement userNamePrefix;

	@FindBy(name = "externalCode")
	private WebElement externalCode;

	@FindBy(name = "empCode")
	private WebElement subscriberCode;

	@FindBy(name = "msisdn")
	private WebElement msisdn;

	@FindBy(name = "ssn")
	private WebElement ssn;

	@FindBy(name = "contactNo")
	private WebElement contactNo;

	@FindBy(name = "designation")
	private WebElement designation;

	@FindBy(name = "divisionCode")
	private WebElement divisionCode;

	@FindBy(name = "departmentCode")
	private WebElement departmentCode;

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

	@FindBy(id = "showPassword")
	private WebElement showPassword;

	@FindBy(id = "confirmPassword")
	private WebElement confirmPassword;

	@FindBy(name = "allowedIPs")
	private WebElement allowedIPs;

	@FindBy(name = "allowedDays")
	private WebElement allowedDays;

	@FindBy(name = "allowedFormTime")
	private WebElement allowedFormTime;

	@FindBy(name = "allowedToTime")
	private WebElement allowedToTime;

	@FindBy(linkText = "Assign geographies")
	private WebElement assignGeographies;

	@FindBy(linkText = "Assign roles")
	private WebElement assignRoles;

	@FindBy(name = "save")
	private WebElement save;

	@FindBy(name = "reset")
	private WebElement reset;

	@FindBy(name = "back")
	private WebElement back;

	@FindBy(name = "confirm")
	private WebElement confirm;

	@FindBy(name = "cancel")
	private WebElement cancel;
		
	@FindBy(xpath="//ul/li")
	private WebElement actualMessage;
	
	@FindBy(xpath="//ol/li")
	private WebElement errorMessage;
	
	WebDriver driver= null;
	
	public AddOperatorUserPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectCategory(String Category) {
		Log.info("Trying to select Category");
		Select select = new Select(category);
		select.selectByVisibleText(Category);
		Log.info("Category selected successfully");
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public String getActualMessage(){
		String message=null;
		try{
		Log.info("Trying to fetch error message.");
		message=errorMessage.getText();
		Log.info("Message fetched as :: "+message);
		}
		catch(Exception e){
			Log.info("Message not found.");
			Log.writeStackTrace(e);
			message=null;
		}
		return message;
	}
}
