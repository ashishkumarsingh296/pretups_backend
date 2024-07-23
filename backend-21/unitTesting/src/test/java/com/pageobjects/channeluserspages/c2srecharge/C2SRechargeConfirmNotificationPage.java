package com.pageobjects.channeluserspages.c2srecharge;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class C2SRechargeConfirmNotificationPage {

	//@FindBy(xpath = "//table/tbody/tr[2]/td[2]/form/table/tbody/tr/td/table/tbody/tr[3]/td/a")
	// @FindBy(linkText = "Click here for final notification message.")
	@FindBy(xpath = "//form/table//table//tr[3]//a")
	private WebElement notificationMsgLink;

	@FindBy(name = "btnBack")
	private WebElement backButton;

	@FindBy(xpath = "//form//table//td[1][text()[contains(.,'status')]]/../td[2]")
	private WebElement transferStatus;
	
	@FindBy(xpath = "//form//table//td[1][text()[contains(.,'id')] or text()[contains(.,'ID')]]/../td[2]")
	private WebElement transferID;
	
	WebDriver driver = null;

	public C2SRechargeConfirmNotificationPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickNotificationMsgLink() {
		WebDriverWait wait = new WebDriverWait(driver, 5);
		
		wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//table/tbody/tr[2]/td[2]/form/table/tbody/tr/td/table/tbody/tr[3]/td/a")));
		
		notificationMsgLink.click();
		Log.info("User clicked Notification Msg link to display Success Message");
	}

	public boolean notificationMsgLinkVisibility() {
		boolean result = false;
		try {
			if (notificationMsgLink.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}

	public String transferStatus(){
		Log.info("Trying to get transfer Status.");
		String trfStatus = transferStatus.getText();
		Log.info("Transfer status fetched as : "+trfStatus);
		return trfStatus;
	}
	
	public String transferID(){
		Log.info("Trying to get transfer ID.");
		String trfID = transferID.getText();
		Log.info("Transfer ID fetched as : "+trfID);
		return trfID;
	}
	
}
