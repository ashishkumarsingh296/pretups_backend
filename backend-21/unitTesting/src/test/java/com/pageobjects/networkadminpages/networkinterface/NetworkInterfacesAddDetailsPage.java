package com.pageobjects.networkadminpages.networkinterface;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class NetworkInterfacesAddDetailsPage {

	WebDriver driver;

	public NetworkInterfacesAddDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "queueSize")
	private WebElement queueSize;

	@FindBy(name = "queueTimeOut")
	private WebElement queueTimeOut;

	@FindBy(name = "requestTimeOut")
	private WebElement requestTimeOut;

	@FindBy(name = "nextCheckQueueReqSec")
	private WebElement nextCheckQueueReqSec;

	@FindBy(name = "interfaceCategoryID")
	private WebElement interfaceCategoryID;

	@FindBy(name = "interfaceID")
	private WebElement interfaceID;

	@FindBy(name = "save")
	private WebElement save;

	@FindBy(name = "reset")
	private WebElement reset;

	@FindBy(name = "back")
	private WebElement back;


	public void enterQueueSize(String queueSizeValue) {
		Log.info("Trying to enter  value in Queue Size ");
		queueSize.sendKeys(queueSizeValue);
		Log.info("Queue Size: " + queueSizeValue);
	}

	public void enterQueueTimeOut(String queueTimeOutValue) {
		Log.info("Trying to enter  value in queueTimeOut ");
		queueTimeOut.sendKeys(queueTimeOutValue);
		Log.info("Queue Time Out: " + queueTimeOutValue);
	}

	public void enterRequestTimeOut(String requestTimeOutValue) {
		Log.info("Trying to enter  value in requestTimeOut ");
		requestTimeOut.sendKeys(requestTimeOutValue);
		Log.info("Reuest Time Out: " + requestTimeOutValue);
	}

	public void enterNextCheckQueueReqSec(String nextCheckQueueReqSecValue) {
		Log.info("Trying to enter  value in nextCheckQueueReqSec ");
		nextCheckQueueReqSec.sendKeys(nextCheckQueueReqSecValue);
		Log.info("Next Queue Retry Interval: " + nextCheckQueueReqSecValue);
	}

	public void selectInterfaceCategory(String interfaceCategory) {
		Log.info("Trying to Select   Interface Category ");
		Select select = new Select(interfaceCategoryID);
		select.selectByVisibleText(interfaceCategory);
		Log.info("Interface Category: " + interfaceCategory);
	}

	public void selectInterfaceName(String interfaceName) {
		Log.info("Trying to Select Interface Name ");
		Select select = new Select(interfaceID);
		select.selectByVisibleText(interfaceName);
		Log.info("Interface Name: " + interfaceName);
	}

	public void clickOnSave() {
		Log.info("Trying to click on Save button ");
		save.click();
		Log.info("Clicked on Save successfully");
	}

	public void clickOnReset() {
		Log.info("Trying to click on Reset button ");
		reset.click();
		Log.info("Clicked on  Reset successfully");
	}

	public void clickOnBack() {
		Log.info("Trying to click on Back button");
		back.click();
		Log.info("Clicked on Back successfully");
	}
}
