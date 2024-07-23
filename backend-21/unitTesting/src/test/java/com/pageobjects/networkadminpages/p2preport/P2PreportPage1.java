package com.pageobjects.networkadminpages.p2preport;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class P2PreportPage1 {
	WebDriver driver;

	public P2PreportPage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "serviceType")
	private WebElement serviceType;

	@FindBy(name = "senderType")
	private WebElement senderType;

	@FindBy(name = "senderServiceClass")
	private WebElement senderServiceClass;

	@FindBy(name = "receiverType")
	private WebElement receiverType;

	@FindBy(name = "receiverServiceClass")
	private WebElement receiverServiceClass;

	@FindBy(name = "subService")
	private WebElement subService;

	@FindBy(name = "btnsubmit")
	private WebElement btnsubmit;

	public void SelectserviceType(String value) {
		Log.info("Trying to Select   serviceType ");
		Select select = new Select(serviceType);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectsenderType(String value) {
		Log.info("Trying to Select   senderType ");
		Select select = new Select(senderType);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectsenderServiceClass(String value) {
		Log.info("Trying to Select   senderServiceClass ");
		Select select = new Select(senderServiceClass);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectreceiverType(String value) {
		Log.info("Trying to Select   receiverType ");
		Select select = new Select(receiverType);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectreceiverServiceClass(String value) {
		Log.info("Trying to Select   receiverServiceClass ");
		Select select = new Select(receiverServiceClass);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectsubService(String value) {
		Log.info("Trying to Select   subService ");
		Select select = new Select(subService);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void ClickOnbtnsubmit() {
		Log.info("Trying to click on button  Submit ");
		btnsubmit.click();
		Log.info("Clicked on  Submit successfully");
	}
}
