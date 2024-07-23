package com.pageobjects.networkadminpages.geographicaldomain;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyGeograpichalDomainManagement {
	
	@ FindBy(name = "grphDomainName")
	private WebElement grphDomainName;

	@ FindBy(name = "grphDomainShortName")
	private WebElement grphDomainShortName;
	
	@ FindBy(name = "status")
	private WebElement status;

	@ FindBy(name = "description")
	private WebElement description;

	@ FindBy(id = "isDefault")
	private WebElement isDefault;
	
	
	@ FindBy(name = "btnModify")
	private WebElement btnModify;

	@ FindBy(name = "btnBack")
	private WebElement btnBack;
	
	@FindBy(xpath = "//ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
	WebElement errorMessage;
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = errorMessage.getText();
		}catch(Exception e){
			Log.info("No error Message found: "+e);
		}
		try{
		UIMsg = UIMessage.getText();
		}catch(Exception e){
			Log.info("No Success Message found: "+e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}

	
	WebDriver driver= null;

	public ModifyGeograpichalDomainManagement(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
     }
	
	public void enterGrphDomainName(String GrphDomainName) {
		grphDomainName.sendKeys(GrphDomainName);
		Log.info("User entered GrphDomainName: "+GrphDomainName);
	}
	
	public void enterGrphDomainShortName(String GrphDomainShortName) {
		grphDomainShortName.sendKeys(GrphDomainShortName);
		Log.info("User entered GrphDomainShortName: "+GrphDomainShortName);
	}
	
	public void selectStatus(String Status) {
		Select grphdomain = new Select(status);
		grphdomain.selectByValue(Status);
		Log.info("User selected Domain:"+Status);
	}
	
	public void enterDescription(String Description) {
		description.sendKeys("Automated "+Description+" Creation");
		Log.info("User entered Description: "+Description);
	}
	
	public void selectIsDefault() {
		isDefault.click();
		Log.info(" 'Is Default' Selected");
	}
	
	public boolean checkIsDefaultStatus() {
		boolean status = isDefault.isSelected();
		return status;
	}
	
	
	public void clickModifyButton() {
		btnModify.click();
		Log.info("User clicked Modify Button.");
	}
	
	public void clickBackButton() {
		btnBack.click();
		Log.info("User clicked Back Button.");
	}
	
}
