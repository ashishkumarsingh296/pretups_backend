package com.pageobjects.networkadminpages.promotionaltransferrule;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddPromotionalTransferRule {

	WebDriver driver;

	public AddPromotionalTransferRule(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "promotionCode")
	private WebElement promotionCode;

	@FindBy(name = "btnSubSelProLev")
	private WebElement btnSubSelProLev;
	
	@FindBy(xpath = "//ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
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

	public void selectPromotionCode(String value) {
		Log.info("Trying to Select promotionCode ");
		Select select = new Select(promotionCode);
		select.selectByValue(value);
		Log.info("Data selected  successfully");
	}

	public void clickOnSubmit() {
		Log.info("Trying to click on button  Submit ");
		btnSubSelProLev.click();
		Log.info("Clicked on  Submit successfully");
	}

	public void selectType(String typeValue, String slabType) {
		Log.info("Trying to click on radio button");
		List<WebElement> radioList = driver
				.findElements(By.xpath("//td[text()[contains(.,'"+typeValue+"')]]/input[@type='radio']"));
		
		if (typeValue.equalsIgnoreCase("Date range")){
			radioList.get(0).click();
			Log.info("Clicked on radio button: " + typeValue);
		}
		else{
			radioList.get(1).click();
		Log.info("Clicked click on radio button: " + typeValue);
		List<WebElement> radioSubList = driver
				.findElements(By.xpath("//td[text()[contains(.,'"+slabType+"')]]/input[@type='radio']"));
		if(slabType.equalsIgnoreCase("Single")){
			radioSubList.get(0).click();
		Log.info("Clicked on radio button:" + slabType);
		}
		else{
			radioSubList.get(1).click();
			Log.info("Clicked on radio button:"+ slabType);
		}
		}
	}

	//td[text()[contains(.,'"+slabType+"')]]/input[@type='radio']
}
