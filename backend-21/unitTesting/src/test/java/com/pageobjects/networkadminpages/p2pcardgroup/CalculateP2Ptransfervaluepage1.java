package com.pageobjects.networkadminpages.p2pcardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class CalculateP2Ptransfervaluepage1 {
	WebDriver driver;

	public CalculateP2Ptransfervaluepage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "amount")
	private WebElement amount;

	@FindBy(name = "oldValidityDate")
	private WebElement oldValidityDate;

	@FindBy(name = "applicableFromDate")
	private WebElement applicableFromDate;

	@FindBy(name = "applicableFromHour")
	private WebElement applicableFromHour;

	@FindBy(name = "serviceTypeId")
	private WebElement serviceTypeId;

	@FindBy(name = "cardGroupSubServiceID")
	private WebElement cardGroupSubServiceID;

	@FindBy(name = "senderTypeId")
	private WebElement senderTypeId;

	@FindBy(name = "senderClassId")
	private WebElement senderClassId;

	@FindBy(name = "receiverTypeId")
	private WebElement receiverTypeId;

	@FindBy(name = "receiverClassId")
	private WebElement receiverClassId;

	@FindBy(name = "calculate")
	private WebElement calculate;
	
	@FindBy(name = "gatewayId")
	private WebElement gatewayId;
	
	@FindBy(xpath = "//ol/li")
	private WebElement ErrMessage;
	
	
	public void Enteramount(String value) {
		Log.info("Trying to enter  value in amount ");
		amount.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnteroldValidityDate(String value) {
		Log.info("Trying to enter  value in oldValidityDate ");
		oldValidityDate.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterapplicableFromDate(String value) {
		Log.info("Trying to enter  value in applicableFromDate ");
		applicableFromDate.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterapplicableFromHour(String value) {
		Log.info("Trying to enter  value in applicableFromHour ");
		applicableFromHour.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void SelectGatewayId(String value) {
		Log.info("Trying to Select   gatewayId ");
		Select select = new Select(gatewayId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectserviceTypeId(String value) {
		Log.info("Trying to Select   serviceTypeId ");
		Select select = new Select(serviceTypeId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	public void SelectcardGroupSubServiceID(String value) {
		Log.info("Trying to Select   cardGroupSubServiceID ");
		Select select = new Select(cardGroupSubServiceID);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectsenderTypeId(String value) {
		Log.info("Trying to Select   senderTypeId ");
		Select select = new Select(senderTypeId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectsenderClassId(String value) {
		Log.info("Trying to Select   senderClassId ");
		Select select = new Select(senderClassId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectreceiverTypeId(String value) {
		Log.info("Trying to Select receiverTypeId ");
		Select select = new Select(receiverTypeId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}
	
	public void SelectreceiverTypeIdValue(String value) {
		Log.info("Trying to Select receiverTypeId ");
		Select select = new Select(receiverTypeId);
		select.selectByValue(value);
		Log.info("Data selected  successfully");
	}

	public void SelectreceiverClassId(String value) {
		Log.info("Trying to Select   receiverClassId ");
		Select select = new Select(receiverClassId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void ClickOncalculate() {
		Log.info("Trying to click on button  Calculate ");
		calculate.click();
		Log.info("Clicked on  Calculate successfully");
	}
	
	public String getErrorMessage() throws InterruptedException {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = ErrMessage.getText();
		Log.info("Error Message fetched successfully as:" + Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;	 
	}
	
	public boolean getErrorMessageVisible() throws InterruptedException {
		 boolean msg =ErrMessage.isDisplayed();
		 return msg;
		 
	}
	
	

}
