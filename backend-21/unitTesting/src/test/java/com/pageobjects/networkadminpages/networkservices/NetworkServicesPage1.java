package com.pageobjects.networkadminpages.networkservices;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class NetworkServicesPage1 {
	WebDriver driver;

	public NetworkServicesPage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "module")
	private WebElement module;

	@FindBy(name = "serviceType")
	private WebElement serviceType;

	@FindBy(name = "btnSubmit")
	private WebElement btnSubmit;
	
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

	public void Selectmodule(String value) {
		Log.info("Trying to Select   module ");
		Select select = new Select(module);
		select.selectByValue(value);
		Log.info("Data selected  successfully");
	}

	public void SelectserviceType(String value) {
		Log.info("Trying to Select   serviceType ");
		Select select = new Select(serviceType);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void ClickOnbtnSubmit() {
		Log.info("Trying to click on button  Submit ");
		btnSubmit.click();
		Log.info("Clicked on  Submit successfully");
	}
}
