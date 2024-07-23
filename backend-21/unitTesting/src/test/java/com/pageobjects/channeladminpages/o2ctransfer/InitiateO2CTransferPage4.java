package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class InitiateO2CTransferPage4 {
	@FindBy(name = "refrenceNum")
	private WebElement refrenceNumber;
	
	@FindBy(name = "externalTxnNum")
	private WebElement externalTxnNumber;
	
	@FindBy(name = "externalTxnDate")
	private WebElement externalTxnDate;
	
	@FindBy(name = "bundleName")
	private WebElement voucherBundle;
	
	@FindBy(name = "profileQuantity")
	private WebElement quantity;
	
	@FindBy(name = "remarks")
	private WebElement remarks;

	@FindBy(name = "submitO2CPackageButton")
	private WebElement submitO2CPackageButton;

	@FindBy(name = "resetButton")
	private WebElement resetButton;

	@FindBy(name = "backButton")
	private WebElement backButton;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	WebDriver driver = null;
	
	public InitiateO2CTransferPage4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectVoucherBundleByIndex(int index, String type) {
		voucherBundle = driver.findElement(By.name("pckgSlabsListIndexed[" + index + "].bundleName"));
		Select typeEL = new Select(voucherBundle);
		typeEL.selectByVisibleText(type);
		new WebDriverWait(driver, 10);
		Log.info("Selected Voucher Bundle successfully");
	}
	
	public void enterQuantityByIndex(int index, String voucherQuantity) {
		quantity = driver.findElement(By.name("pckgSlabsListIndexed[" + index + "].profileQuantity"));
		Log.info("Trying to enter Voucher Quantity");
		quantity.clear();
		quantity.sendKeys(voucherQuantity);
		Log.info("Voucher quantity set as: "+ quantity);
	}
	
	
	public void enterRefNumber(String RefNumber) {
		refrenceNumber.sendKeys(RefNumber);
		Log.info("User entered Ref number: " + RefNumber);
	}

	public void enterRemarks(String Remarks) {
		remarks.sendKeys(Remarks);
		Log.info("User entered Remarks: " + Remarks);
	}

	public void clickSubmitButton() {
		submitO2CPackageButton.click();
		Log.info("User clicked Submit Button");
	}

	public void clickResetButton() {
		resetButton.click();
		Log.info("User clicked Reset button");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}
	
	public String getMessage(){
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
		Message = message.getText();
		Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully");
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
	
}
