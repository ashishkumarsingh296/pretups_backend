package com.pageobjects.networkadminpages.p2pcardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ViewP2Pcardgrouppage1 {
	WebDriver driver;

	public ViewP2Pcardgrouppage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "numberOfDays")
	private WebElement numberOfDays;

	@FindBy(name = "serviceTypeId")
	private WebElement serviceTypeId;

	@FindBy(name = "cardGroupSubServiceID")
	private WebElement cardGroupSubServiceID;

	@FindBy(name = "selectCardGroupSetId")
	private WebElement selectCardGroupSetId;

	@FindBy(name = "view")
	private WebElement view;

	public void EnternumberOfDays(String value) {
		Log.info("Trying to enter  value in numberOfDays ");
		numberOfDays.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void SelectserviceTypeId(String value) {
		Log.info("Trying to Select   serviceTypeId ");
		Select select = new Select(serviceTypeId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectcardGroupSubServiceID(String value) {
		Log.info("Trying to Select   cardGroupSubServiceID ");
		Select select = new Select(cardGroupSubServiceID);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectselectCardGroupSetId(String value) {
		Log.info("Trying to Select   selectCardGroupSetId ");
		Select select = new Select(selectCardGroupSetId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void ClickOnview() {
		Log.info("Trying to click on button  Submit ");
		view.click();
		Log.info("Clicked on  Submit successfully");
	}
}
