package com.pageobjects.networkadminpages.multicurency;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class CurrencyApprovalLevelTwoPage_1 {
	
	WebDriver driver = null;
	
	@FindBy(name = "approval2confirm")
	private WebElement approve;
	
	@FindBy(name = "reject2")
	private WebElement reject;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	public CurrencyApprovalLevelTwoPage_1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	
	public void selectCurrencyForApproval() {
		Log.info("Trying to select currency for approval from the list");
		WebElement radio = driver.findElement(By.xpath("//tbody/tr[2]/td[1]/input[1]"));
		radio.click();
		Log.info("Successfully selected currency for approval2");
	}
	
	public void clickApprove() {
		Log.info("Trying to click approve button");
		approve.click();
		Log.info("Approve button clicked successfully");
	}
	
	public void clickReject() {
		Log.info("Trying to click reject button");
		reject.click();
		Log.info("Reject button clicked successfully");
	}
	
	public String getSuccessMeassage() {
		Log.info("Trying to get success message");
		String message1 = message.getText();
		Log.info("Success message returned: " + message1);
		return message1;
	}
	
	public void acceptAlert() {
		Log.info("Trying to accept alert");
		Alert alert = driver.switchTo().alert();
		alert.accept();
		Log.info("Alert accepted successfully");
	}
	
}
