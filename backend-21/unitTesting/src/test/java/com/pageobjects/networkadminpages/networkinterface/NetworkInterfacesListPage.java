package com.pageobjects.networkadminpages.networkinterface;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class NetworkInterfacesListPage {

	WebDriver driver;

	public NetworkInterfacesListPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "add")
	private WebElement add;

	@FindBy(name = "delete")
	private WebElement delete;

	@FindBy(name = "edit")
	private WebElement edit;
	
	@FindBy(xpath = "//tr/td/ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//tr/td/ol/li")
	WebElement errorMessage;

	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = errorMessage.getText();
		}catch(Exception e){
			Log.info("No error Message found: "+e);
		}
		try{
		UIMsg = UIMessage.getText();
		}catch(Exception e){
			Log.info("No Success Message found: "+e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}


	public void clickOnAdd() {
		Log.info("Trying to click on button  Add ");
		add.click();
		Log.info("Clicked on  Add successfully");
	}

	public void clickOnDelete() {
		Log.info("Trying to click on button  Delete ");
		delete.click();
		Log.info("Clicked on  Delete successfully");
	}

	public void clickOnModify() {
		Log.info("Trying to click on button Modify ");
		edit.click();
		Log.info("Clicked on Modify successfully");
	}
	

	public void clickOnRadioButton(String interfaceCategory, String interfaceName, String queueSize,
			String queueTimeOut, String requestTimeOut, String queueRetryInterval) {
		Log.info("Trying to click on xpath ");
		WebElement element = null;
		String xpath = "";
		xpath = "//td[text()='" + interfaceCategory + "']/following-sibling::td[contains(text(),'" + interfaceName
				+ "')]/following-sibling::td[contains(text(),'" + queueSize
				+ "')]/following-sibling::td[contains(text(),'" + queueTimeOut
				+ "')]/following-sibling::td[contains(text(),'" + requestTimeOut
				+ "')]/following-sibling::td[contains(text(),'" + queueRetryInterval + "')]/..//input[@type='radio']";

		element = driver.findElement(By.xpath(xpath));
		element.click();
		Log.info("Clicked on Xpath successfully");
	}

}
