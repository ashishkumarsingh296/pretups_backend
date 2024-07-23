package com.pageobjects.networkadminpages.p2pcardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyCardGroupPage2 {
	WebDriver driver;

	public ModifyCardGroupPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "cardGroupSetName")
	private WebElement cardGroupSetName;

	@FindBy(name = "applicableFromDate")
	private WebElement applicableFromDate;

	@FindBy(name = "applicableFromHour")
	private WebElement applicableFromHour;

	@FindBy(name = "save")
	private WebElement save;

	@FindBy(name = "reset")
	private WebElement reset;

	@FindBy(name = "delete")
	private WebElement delete;

	@FindBy(name = "back")
	private WebElement back;

	public void EntercardGroupSetName(String value) {
		Log.info("Trying to enter  value in cardGroupSetName ");
		cardGroupSetName.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterapplicableFromDate(String value) {
		Log.info("Trying to enter  value in applicableFromDate ");
		applicableFromDate.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterapplicableFromHour(String value) {
		Log.info("Trying to enter  value in applicableFromHour ");
		applicableFromHour.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void ClickOnsave() {
		Log.info("Trying to click on button  Save ");
		save.click();
		Log.info("Clicked on  Save successfully");
	}

	public void ClickOnreset() {
		Log.info("Trying to click on button  Reset ");
		reset.click();
		Log.info("Clicked on  Reset successfully");
	}

	public void ClickOndelete() {
		Log.info("Trying to click on button  Delete ");
		delete.click();
		Log.info("Clicked on  Delete successfully");
	}

	public void ClickOnback() {
		Log.info("Trying to click on button  Back ");
		back.click();
		Log.info("Clicked on  Back successfully");
	}

}
