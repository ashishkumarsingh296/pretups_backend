package com.pageobjects.channeladminpages.restrictedList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class DeleteRestrictedList {
	
	@FindBy(name = "geoDomainCode")
	private WebElement geoDomain;
	
	@FindBy(name = "domainCode")
	private WebElement domainCode;
	
	@FindBy(name = "categoryCode")
	private WebElement categoryCode;
	
	@FindBy(name = "userName")
	private WebElement userName;
	
	@FindBy(name = "msisdnTextArea")
	private WebElement subscribersMSISDN;
	
	@FindBy(name = "selectAllSubs")
	private WebElement selectAllSubscribers;
	
	@FindBy(name = "submitFromDelete")
	private WebElement submitDelete;
	
	@FindBy(name = "confirmFromDelete")
	private WebElement confirmDelete;
	
	@FindBy(name = "confirmAllDelete")
	private WebElement confirmAllDelete;
	
	@FindBy(name = "submit1")
	private WebElement submit;
	
	@FindBy(name = "newSubVOIndexedForDelete[0].checkBoxVal")
	private WebElement singleMsisdnChkBox;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement ErrorMessage;
	
	WebDriver driver = null;

	public DeleteRestrictedList(WebDriver driver) {
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
	
	public void enterMSISDN(String msisdn){
		
		subscribersMSISDN.sendKeys(msisdn);
		
		
	}

	
	public void selectAllSubChkBox(){
		Log.info("User is trying to select checkbox for the All subscribers");
		selectAllSubscribers.click();
		Log.info("User clicked the checkbox for the all uploaded subscribers");
		
	}
	
	public void selectSingleMSISDNChkBox(){
		Log.info("User is trying to select checkbox for the selected MSISDN");
		singleMsisdnChkBox.click();
		Log.info("User clicked the checkbox for the selected MSISDN");
		
	}
	
	public String getMessage(){
		String msg = message.getText();
		Log.info("The message fetched as : " +msg);
		
		return msg;
	}
	
	public String getErrorMessage(){
		String msg = ErrorMessage.getText();
		Log.info("The message fetched as : " +msg);
		
		return msg;
	}
	
	public void clickSubmit(){
		submit.click();
	}
	
	public void clickSubmitDelete(){
		submitDelete.click();
	}
	
	public void clickConfirmDelete(){
		confirmDelete.click();
	}
	
	public void clickConfirmAllDelete(){
		confirmAllDelete.click();
	}
}
