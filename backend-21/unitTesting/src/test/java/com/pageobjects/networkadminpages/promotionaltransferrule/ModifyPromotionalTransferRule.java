package com.pageobjects.networkadminpages.promotionaltransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyPromotionalTransferRule {
	
	WebDriver driver;

	public ModifyPromotionalTransferRule(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "domainCodeforDomain")
	private WebElement domainCodeforDomain;
	
	@FindBy(name = "categoryCode")
	private WebElement categoryCode;
	
	@FindBy(name = "geoTypeCode")
	private WebElement geoTypeCode;
	
	@FindBy(name = "geoDomainCode")
	private WebElement geoDomainCode;
	
	@FindBy(name = "userName")
	private WebElement userName;
	
	@FindBy(name = "gradeCode")
	private WebElement gradeCode;
	
	@FindBy(name = "btnAddSubmit")
	private WebElement btnAddSubmit;
	
	@FindBy(name = "btnBackMod")
	private WebElement btnBackMod;
	
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
	
	public void selectdomainCodeforDomain(String value) {
		Log.info("Trying to Select domainCodeforDomain ");
		Select select = new Select(domainCodeforDomain);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void SelectcategoryCode(String value) {
		Log.info("Trying to Select categoryCode ");
		Select select = new Select(categoryCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void selectGeoTypeCode(String value) {
		Log.info("Trying to Select geoTypeCode ");
		Select select = new Select(geoTypeCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully: "+value);
	}
	
	public void selectGeoDomainCode(String value) {
		Log.info("Trying to Select geoDomainCode ");
		Select select = new Select(geoDomainCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void enterUserName(String value){
		Log.info("Trying to enter  value in userName ");
		userName.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}
	
	public void selectGradeCode(String value) {
		Log.info("Trying to Select gradeCode ");
		Select select = new Select(gradeCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully:"+ value);
	}
	
	public void ClickOnSubmit() {
		Log.info("Trying to click on Submit Button ");
		btnAddSubmit.click();
		Log.info("Clicked on  Submit successfully");
	}
	
	public void ClickOnBack() {
		Log.info("Trying to click on Back Button ");
		btnBackMod.click();
		Log.info("Clicked on  Back successfully");
	}

}
