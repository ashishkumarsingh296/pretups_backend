package com.pageobjects.channeladminpages.restrictedList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ApproveRestList {
	
	
	@FindBy(name = "geoDomainCode")
	private WebElement geoDomain;
	
	@FindBy(name = "domainCode")
	private WebElement domainCode;
	
	@FindBy(name = "categoryCode")
	private WebElement categoryCode;
	
	@FindBy(name = "userName")
	private WebElement userName;
	
	@FindBy(name = "submit1")
	private WebElement submit;
	
	
	
	WebDriver driver = null;

	public ApproveRestList(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void selectGeoDomain(String geo){
		Select select = new Select(geoDomain);
		select.selectByVisibleText(geo);
		Log.info("User selected Geographical Domain." +geo);
	}
	
	
	public void selectDomainCode(String domain){
		Select select = new Select(domainCode);
		select.selectByVisibleText(domain);
		Log.info("User selected Domain." +domain);
	}
	
	public void selectCategory(String category){
		Select select = new Select(categoryCode);
		select.selectByVisibleText(category);
		Log.info("User selected Category." +category);
	}
	
	
	public void enterUserName(String usr) {
		userName.sendKeys(usr);
		Log.info("User Name entered : "+usr);
	}
	
	public void clickSubmit(){
		submit.click();
	}
	
	
	
	

}
