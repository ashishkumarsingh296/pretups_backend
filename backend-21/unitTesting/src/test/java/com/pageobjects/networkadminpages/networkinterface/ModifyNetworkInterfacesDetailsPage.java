package com.pageobjects.networkadminpages.networkinterface;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyNetworkInterfacesDetailsPage {

	WebDriver driver;

	public ModifyNetworkInterfacesDetailsPage(WebDriver driver) {
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

	@FindBy(name = "save")
	private WebElement save;

	@FindBy(name = "reset")
	private WebElement reset;

	@FindBy(name = "back")
	private WebElement back;

	public void enterQueueSize(String queueSizeValue) {
		Log.info("Trying to enter  value in Queue Size ");
		queueSize.clear();
		queueSize.sendKeys(queueSizeValue);
		Log.info("Queue Size: "+queueSizeValue);
	}

	public void enterQueueTimeOut(String queueTimeOutValue) {
		Log.info("Trying to enter  value in Queue Timeout ");
		queueTimeOut.clear();
		queueTimeOut.sendKeys(queueTimeOutValue);
		Log.info("Queue Time Out: "+queueTimeOutValue);
	}

	public void enterRequestTimeOut(String requestTimeOutValue) {
		Log.info("Trying to enter  value in Request Timeout ");
		requestTimeOut.clear();
		requestTimeOut.sendKeys(requestTimeOutValue);
		Log.info("Request Time Out: "+requestTimeOutValue);
	}

	public void enterNextCheckQueueReqSec(String queueRetryIntervalValue) {
		Log.info("Trying to enter  value in Queue retry interval.");
		nextCheckQueueReqSec.clear();
		nextCheckQueueReqSec.sendKeys(queueRetryIntervalValue);
		Log.info("Queue Retry Interval: "+queueRetryIntervalValue);
	}

	public void clickOnSave() {
		Log.info("Trying to click on button Save ");
		save.click();
		Log.info("Clicked on  Save successfully");
	}

	public void clickOnReset() {
		Log.info("Trying to click on button Reset ");
		reset.click();
		Log.info("Clicked on  Reset successfully");
	}

	public void clickOnBack() {
		Log.info("Trying to click on button Back ");
		back.click();
		Log.info("Clicked on  Back successfully");
	}

}
