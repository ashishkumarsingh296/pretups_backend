package com.pageobjects.channeluserspages.c2ctransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class C2CTransferConfirmPage {

	@FindBy(xpath = "//input[@value='Confirm']")
	public WebElement confirm;

	@FindBy(name = "backButton")
	private WebElement backButton;

	WebDriver driver = null;

	public C2CTransferConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public boolean confirmVisible() {
		boolean flag = false;
		try {
		flag =driver.findElement(By.xpath("//input[@value='Confirm']")).isDisplayed();
		return flag;
		}
		catch (NoSuchElementException e) {
			return flag;
		}
	}

	public void clickConfirm() throws InterruptedException {
		Log.info("Trying to click confirm button on GUI.");
		
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(confirm));
		confirm.click();
		if(confirmVisible()) {
			new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(confirm));
			confirm.click();
		}

		Log.info("User clicked Confirm");
	}

	public void clickBackButton() {
		Log.info("Trying to click back button on GUI.");
		backButton.click();
		Log.info("User clicked Back Button");
	}

}
