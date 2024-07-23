package com.pageobjects.networkadminpages.transfercontrolprofile;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class TransferControlProfileDetailsPage {
	


	@FindBy(name = "save")
	private WebElement addButton;

	@FindBy(name = "update")
	private WebElement modifyButton;

	@FindBy(name = "btnDel")
	private WebElement deleteButton;

	@FindBy(name = "back")
	private WebElement backButton;
	
	

	@FindBy(xpath = "//table/tbody/tr[2]/td[2]/ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement defaultProfileMessage;

	@FindBy(xpath = "//table/tbody/tr[2]/td[2]/table[3]/tbody/tr/td/form/table")
	private WebElement table;

	WebDriver driver = null;

	public TransferControlProfileDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickAddButton() {
		addButton.click();
		Log.info("User clicked Add Button.");
	}

	public void clickModifyButton() {
		modifyButton.click();
		Log.info("User clicked Modify Button.");
	}

	public void clickDeleteButton() {
		deleteButton.click();
		Log.info("User clicked Delete Button.");
	}

	public void clickbBackButton() {
		backButton.click();
		Log.info("User clicked Back Button.");
	}
	
	public String getMessage() {
		return message.getText();
	}
	
	public String getDeleteErrorMessage(){
		return defaultProfileMessage.getText();
	}

	public boolean verifyMessage() {
		String expected = "Successfully inserted transfer profile";
		String actual = message.getText();
		if (expected.equals(actual))
			return true;
		else
			return false;

	}

	public boolean tableExist() {
		boolean result = false;
		if (table.isDisplayed()) {
			result = true;
		}
		return result;
	}

	/**
	 * added by lokesh.kontey
	 * @param channeltcpID
	 */
	public void selectTCP(String channeltcpID){
		Log.info("Trying to select TCP with TCP_ID: "+channeltcpID);
		WebElement radioButton = driver.findElement(By.xpath("//input[@name='code'][@value='"+channeltcpID+"']"));
		radioButton.click();
		Log.info("TCP selected : "+channeltcpID );
	}
	
}
