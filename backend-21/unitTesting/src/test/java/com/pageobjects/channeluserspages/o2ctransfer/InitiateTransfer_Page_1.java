package com.pageobjects.channeluserspages.o2ctransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class InitiateTransfer_Page_1 {

	@FindBy(name = "productType")
	private WebElement productType;
	
	@FindBy(name = "submitButton")
	private WebElement submitButton;
	
	@FindBy(name = "resetButton")
	private WebElement resetButton;
	
	@FindBy(xpath = "//ul/li")
	private WebElement successMessage;
	
	
	WebDriver driver;
	
	public InitiateTransfer_Page_1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public boolean isSelectProductTypeVisible() {
		boolean selectDropdownVisible = driver.findElements(By.xpath("//select[@name='productTypeWithUserCode']")).size() > 0 ;
		boolean flag = false ;
		if (selectDropdownVisible == true ){
			flag = true ;
		}
		else {
			flag = false ;
		}
		return flag ;
	}
	
	
	public void selectProductType(String ProductCode) {
		Log.info("Trying to select Product Type");
		Select productTypeSelect = new Select(productType);
		productTypeSelect.selectByValue(ProductCode);
		Log.info("Product Type selected successfully as: " + ProductCode);
	}
	
	public boolean VisibilityselectProductType() {
		Log.info("Trying to find Product Type");
		boolean flag= true;
		try {
		flag= productType.isDisplayed();
		}
		catch(NoSuchElementException e) {
			Log.info("Product Type dropdown is not visible");
			flag=false;
		}
		return flag;
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public String getMessage() {
		Log.info("Trying to fetch Success Message from WEB");
		String Message = successMessage.getText();
		Log.info("Message Fetched successfully as: " + Message);
		return Message;
	}
}
