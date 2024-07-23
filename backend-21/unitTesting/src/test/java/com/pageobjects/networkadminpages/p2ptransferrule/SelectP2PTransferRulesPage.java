package com.pageobjects.networkadminpages.p2ptransferrule;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class SelectP2PTransferRulesPage {

	WebDriver driver;

	@FindBy(name = "btnMod")
	private WebElement btnMod;

	@FindBy(name = "selectAll")
	private WebElement selectAll;

	@FindBy(xpath = "//tr/td/ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//tr/td/ol/li")
	WebElement errorMessage;

	public SelectP2PTransferRulesPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickoncheckbox(String requestBearer, String senderType, String senderServiceClass, String receiverType,
			String receiverServiceClass, String serviceType, String subService, String cardGroupSet) {
		Log.info("Trying to click on xpath ");
		WebElement element = null;
		String xpath = "";
		xpath = "//tr/td[contains(text(),'" + requestBearer + "')]/following-sibling::td[contains(text(),'" + senderType + "')]/following-sibling::td[contains(text(),'"
				+ senderServiceClass + "')]/following-sibling::td[contains(text(),'" + receiverType
				+ "')]/following-sibling::td[contains(text(),'" + receiverServiceClass
				+ "')]/following-sibling::td[normalize-space() = '" + serviceType
				+ "']/following-sibling::td[normalize-space()='" + subService 
				+ "']/..//input[@type='checkbox']";
		element = driver.findElement(By.xpath(xpath));
		element.click();
		Log.info("Clicked on Xpath successfully");
	}
	
	public void clickoncheckboxVoucher(String requestBearer, String senderType, String senderServiceClass, String receiverType,
			String receiverServiceClass, String serviceType, String subService) {
		Log.info("Trying to click on xpath ");
		WebElement element = null;
		String xpath = "";
		xpath = "//tr/td[contains(text(),'" + requestBearer + "')]/following-sibling::td[contains(text(),'" + senderType + "')]/following-sibling::td[contains(text(),'"
				+ senderServiceClass + "')]/following-sibling::td[contains(text(),'" + receiverType
				+ "')]/following-sibling::td[contains(text(),'" + receiverServiceClass
				+ "')]/following-sibling::td[normalize-space() = '" + serviceType
				+ "']/following-sibling::td[normalize-space()='" + subService
				+ "']/..//input[@type='checkbox']";
		element = driver.findElement(By.xpath(xpath));
		element.click();
		Log.info("Clicked on Xpath successfully");
	}


	public void clickOnModifyButton() {
		Log.info("Trying to click on button  Submit ");
		btnMod.click();
		Log.info("Clicked on Submit successfully");
	}

	public void clickOnSelectAll() {
		Log.info("Trying to click on button  All ");
		selectAll.click();
		Log.info("Clicked on All successfully");
	}

	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try {
			errorMsg = errorMessage.getText();
		} catch (Exception e) {
			Log.info("No error Message found: " + e);
		}
		try {
			UIMsg = UIMessage.getText();
		} catch (Exception e) {
			Log.info("No Success Message found: " + e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}

}
