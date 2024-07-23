package com.pageobjects.superadminpages.divisionmanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewDivisionDetailsPage {
	
	@FindBy(name = "add")
	private WebElement addButton;

	@FindBy(name = "modify")
	private WebElement modifyButton;

	@FindBy(name = "delete")
	private WebElement deleteButton;

	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement ErrorMessage;

	WebDriver driver = null;

	public ViewDivisionDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickAddButton() {
		Log.info("Trying to click Add Button");
		addButton.click();
		Log.info("Add Button clicked successfully");
	}

	public void clickModifyButton() {
		Log.info("Trying to click Modify Button");
		modifyButton.click();
		Log.info("Modify Button clicked successfully");
	}

	public void clickDeleteButton() {
		Log.info("Trying to click Delete Button");
		deleteButton.click();
		Log.info("Delete Button clicked successfully");
	}

	public String getMessage() {
		Log.info("Trying to fetch Success Message");
		String message1 = message.getText();
		Log.info("Success Message Returned: " + message1);
		return message1;
	}

	
	public String getErrorMessage() {
		Log.info("Trying to fetch Error Message");
		String message1 = ErrorMessage.getText();
		Log.info("Error Message Returned: " + message1);
		return message1;
	}
	
	
	public void selectDivision(String div){

		Log.info("User is trying to select division");

		WebElement divisionSelectButton = driver.findElement(By.xpath("//tr/td[contains(.,'"+div+"')]/../td/input[@type='radio']"));
		divisionSelectButton.click();

		Log.info("User selects division");

	}
	
}
