package com.pageobjects.superadminpages.addoperatoruser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ViewOperatorUser {
	WebDriver driver;

	public ViewOperatorUser(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "userName")
	private WebElement userName;

	@FindBy(name = "categoryCode")
	private WebElement categoryCode;

	@FindBy(name = "view")
	private WebElement view;

	@FindBy(name = "back")
	private WebElement back;

	public void ClickOnback() {
		Log.info("Trying to click on button  Back ");
		back.click();
		Log.info("Clicked on  Back successfully");
	}

	public void EnteruserName(String value) {
		Log.info("Trying to enter  value in userName ");
		userName.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void SelectcategoryCode(String value) {
		Log.info("Trying to Select   categoryCode ");
		Select select = new Select(categoryCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void ClickOnview() {
		Log.info("Trying to click on button  Submit ");
		view.click();
		Log.info("Clicked on  Submit successfully");
	}
}
