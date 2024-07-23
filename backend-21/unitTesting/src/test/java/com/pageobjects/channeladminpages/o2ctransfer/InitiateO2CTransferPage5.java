package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class InitiateO2CTransferPage5 {

	@FindBy(name = "paymentInstCode")
	private WebElement paymentInstrumntType;

	@FindBy(name = "paymentInstNum")
	private WebElement paymentInstNum;

	@FindBy(name = "paymentInstDate")
	private WebElement paymentInstDate;

	@FindBy(name = "submitO2CVoucherProdButton")
	private WebElement submitO2CVoucherProdButton;

	@FindBy(name = "resetButton")
	private WebElement resetButton;

	@FindBy(name = "backButton")
	private WebElement backButton;

	WebDriver driver = null;

	public InitiateO2CTransferPage5(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectPaymentInstrumntType(String PaymentInstrumntType) {
		Select select = new Select(paymentInstrumntType);
		select.selectByValue(PaymentInstrumntType);
		Log.info("User selected Payment Instrumnt Type: " + PaymentInstrumntType);
	}

	public void enterPaymentInstNum(String PaymentInstNum) {
		paymentInstNum.sendKeys(PaymentInstNum);
		Log.info("User entered PaymentInstNum: " + PaymentInstNum);
	}

	public void enterPaymentInstDate(String PaymentInstDate) {
		paymentInstDate.sendKeys(PaymentInstDate);
		Log.info("User entered PaymentInstDate: " + PaymentInstDate);
	}

	public void clickSubmitButton() {
		submitO2CVoucherProdButton.click();
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

}
