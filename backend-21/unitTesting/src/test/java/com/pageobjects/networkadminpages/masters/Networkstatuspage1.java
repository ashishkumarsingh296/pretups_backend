package com.pageobjects.networkadminpages.masters;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class Networkstatuspage1 {
	WebDriver driver;

	public Networkstatuspage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "dataListIndexed[0].language1Message")
	private WebElement language1Message;

	@FindBy(name = "dataListIndexed[0].language2Message")
	private WebElement language2Message;

	@FindBy(name = "saveStatus")
	private WebElement saveStatus;

	@FindBy(name = "reset")
	private WebElement reset;

	@FindBy(id = "saveNetworkStatus")
	private WebElement saveButtonID;

	public void Enterdatalanguage1Message(String NetworkCode, String Language1Message) {
		Log.info("Trying to enter language 1 Message");
		WebElement Language1Element = driver.findElement(By.xpath("//tr/td[contains(text(),'" + NetworkCode
				+ "')]/following-sibling::td/textarea[@name[contains(.,'language1Message')]]"));
		Language1Element.clear();
		Language1Element.sendKeys(Language1Message);
		Log.info("Language 1 Message entered successfully as: " + Language1Message);
	}

	public void Enterdatalanguage2Message(String NetworkCode, String Language2Message) {
		Log.info("Trying to enter language 1 Message");
		WebElement Language2Element = driver.findElement(By.xpath("//tr/td[contains(text(),'" + NetworkCode
				+ "')]/following-sibling::td/textarea[@name[contains(.,'language2Message')]]"));
		Language2Element.clear();
		Language2Element.sendKeys(Language2Message);
		Log.info("Language 1 Message entered successfully as: " + Language2Message);
	}

	public void clickoncheckbox(String value) {
		Log.info("Trying to select check box for " + value);
		driver.findElement(
				By.xpath("//tr/td[contains(text(),'" + value + "')]/following-sibling::td/input[@type='checkbox']"))
				.click();
		Log.info("Check box selected successfully");
	}

	public void Enterdatalanguage2Message(String value) {
		Log.info("Trying to enter Language 2 Message");
		language2Message.sendKeys(value);
		Log.info("Language 2 Message entered successfully as: " + value);
	}

	public void ClickOnsaveStatus() {
		Log.info("Trying to click on Save button");
		try {
			saveStatus.click();
		} catch (NoSuchElementException e) {
			Log.info("Error: " + e);
		}
		try {
			saveButtonID.click();
		} catch (NoSuchElementException e) {
			Log.info("Error: " + e);
		}
		Log.info("Save button clicked successfully");
	}

	public void ClickOnreset() {
		Log.info("Trying to click on Reset Button");
		reset.click();
		Log.info("Resey Button clicked successfully");
	}

}
