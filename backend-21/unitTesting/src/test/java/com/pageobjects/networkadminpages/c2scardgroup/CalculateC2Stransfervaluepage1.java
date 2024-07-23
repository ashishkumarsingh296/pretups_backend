package com.pageobjects.networkadminpages.c2scardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class CalculateC2Stransfervaluepage1 {
	WebDriver driver;

	public CalculateC2Stransfervaluepage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "amount")
	private WebElement amount;

	@FindBy(name = "oldValidityDate")
	private WebElement oldValidityDate;

	@FindBy(name = "applicableFromDate")
	private WebElement applicableFromDate;

	@FindBy(name = "applicableFromHour")
	private WebElement applicableFromHour;

	@FindBy(name = "serviceTypeId")
	private WebElement serviceTypeId;

	@FindBy(name = "domainCode")
	private WebElement domainCode;

	@FindBy(name = "cardGroupSubServiceID")
	private WebElement cardGroupSubServiceID;

	@FindBy(name = "categoryId")
	private WebElement categoryId;

	@FindBy(name = "receiverTypeId")
	private WebElement receiverTypeId;

	@FindBy(name = "gradeId")
	private WebElement gradeId;

	@FindBy(name = "receiverClassId")
	private WebElement receiverClassId;

	@FindBy(name = "gatewayId")
	private WebElement gatewayId;

	@FindBy(name = "calculate")
	private WebElement calculate;

	public void Enteramount(String value) {
		Log.info("Trying to enter  value in amount ");
		amount.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnteroldValidityDate(String value) {
		Log.info("Trying to enter  value in oldValidityDate ");
		oldValidityDate.sendKeys(value);
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

	public void SelectserviceTypeId(String value) {
		Log.info("Trying to Select   serviceTypeId ");
		Select select = new Select(serviceTypeId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectdomainCode(String value) {
		Log.info("Trying to Select   domainCode ");
		Select select = new Select(domainCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectcardGroupSubServiceID(String value) {
		Log.info("Trying to Select   cardGroupSubServiceID ");
		Select select = new Select(cardGroupSubServiceID);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectcategoryId(String value) {
		Log.info("Trying to Select   categoryId ");
		Select select = new Select(categoryId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectreceiverTypeId(String value) {
		Log.info("Trying to Select   receiverTypeId ");
		Select select = new Select(receiverTypeId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectgradeId(String value) {
		Log.info("Trying to Select   gradeId ");
		Select select = new Select(gradeId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectreceiverClassId(String value) {
		Log.info("Trying to Select   receiverClassId ");
		Select select = new Select(receiverClassId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectgatewayId(String value) {
		Log.info("Trying to Select   gatewayId ");
		Select select = new Select(gatewayId);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void ClickOncalculate() {
		Log.info("Trying to click on button  Calculate ");
		calculate.click();
		Log.info("Clicked on  Calculate successfully");
	}

}
