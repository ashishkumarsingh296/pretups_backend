package com.pageobjects.networkadminpages.reconciliation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2Creconciliationpage1 {

	@FindBy(name = "fromDate")
	private WebElement fromDate;

	@FindBy(name = "toDate")
	private WebElement toDate;

	@FindBy(name = "btnSubmit")
	private WebElement btnSubmit;

	@FindBy(name = "btnReset")
	private WebElement btnReset;

	@FindBy(name = "btnBack")
	private WebElement btnBack;

	@FindBy(xpath = "//ul/li")
	private WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;

	@FindBy(xpath = "//form/table//table/tbody/tr[2]/td")
	private WebElement noInputMessage;
		
	WebDriver driver = null;

	public O2Creconciliationpage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	public String getnoAmbiguiousMessage()
	{
		String msg = null;
		try {
			msg = noInputMessage.getText();
		} catch (Exception e) {
			Log.info("No  Message found: " + e);
		}
		return msg;
	}
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try {
			errorMsg = errorMessage.getText();
		} catch (Exception e) {
			Log.info("No error Message found.");
		}
		try {
			UIMsg = UIMessage.getText();
		} catch (Exception e) {
			Log.info("No Success Message found.");
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}

	public void EnterfromDate(String value) {
		Log.info("Trying to enter  value in fromDate ");
		fromDate.sendKeys(value);
		Log.info("Data entered  successfully: " + value);
	}

	public void EntertoDate(String value) {
		Log.info("Trying to enter  value in toDate ");
		toDate.sendKeys(value);
		Log.info("Data entered  successfully: " + value);
	}

	public void ClickOnbtnSubmit() {
		Log.info("Trying to click on button  Submit ");
		btnSubmit.click();
		Log.info("Clicked on Submit successfully");
	}

	public void ClickOnbtnReset() {
		Log.info("Trying to click on button  Reset ");
		btnReset.click();
		Log.info("Clicked on Reset successfully");
	}

	public void ClickOnbtnBack() {
		Log.info("Trying to click on button  Back ");
		btnBack.click();
		Log.info("Clicked on  Back successfully");
	}

}
