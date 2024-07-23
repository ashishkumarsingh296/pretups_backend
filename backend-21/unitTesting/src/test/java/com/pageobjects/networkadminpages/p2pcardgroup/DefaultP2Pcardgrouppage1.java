package com.pageobjects.networkadminpages.p2pcardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class DefaultP2Pcardgrouppage1 {
	WebDriver driver;

	public DefaultP2Pcardgrouppage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "serviceTypeId")
	private WebElement serviceTypeId;

	@FindBy(name = "cardGroupSubServiceID")
	private WebElement cardGroupSubServiceID;

	@FindBy(name = "selectCardGroupSetId")
	private WebElement selectCardGroupSetId;

	@FindBy(name = "selectCardGroupSetVersionId")
	private WebElement selectCardGroupSetVersionId;

	@FindBy(name = "editDefault")
	private WebElement editDefault;

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

	public void SelectselectCardGroupSetVersionId(String value) {
		Log.info("Trying to Select   selectCardGroupSetVersionId ");
		Select select = new Select(selectCardGroupSetVersionId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void ClickOneditDefault() {
		Log.info("Trying to click on button  Default ");
		editDefault.click();
		Log.info("Clicked on  Default successfully");
	}
}
