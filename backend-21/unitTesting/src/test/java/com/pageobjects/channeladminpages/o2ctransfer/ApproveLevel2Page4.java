package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class ApproveLevel2Page4 {
	
	@ FindBy(name = "confirm")
	private WebElement confirmButton;

	@ FindBy(name = "cancel")
	private WebElement cancelButton;
	
	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	@ FindBy(name = "confirmO2CVoucherProdButton")
	private WebElement confirmButtonVoucher;

	WebDriver driver= null;

	public ApproveLevel2Page4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickConfirmButtonVoucher() {
		confirmButtonVoucher.click();
		Log.info("User clicked confirm button.");
	}

	public void clickConfirmButton() {
		new WebDriverWait(driver, 120).until(ExpectedConditions.visibilityOf(confirmButton));
		confirmButton.click();
		Log.info("User clicked confirm button.");
	}
	
	public void clickCancelButton() {
		cancelButton.click();
		Log.info("User clicked cancel button.");
	}
	
	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked back button.");
	}
}
