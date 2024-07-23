package com.pageobjects.channeladminpages.autoO2CTransfer;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InitiateAutoO2CTransferPageIDEA {
	
	@ FindBy(name = "submit1")
	private WebElement submit1;
	
WebDriver driver= null;
	
	public InitiateAutoO2CTransferPageIDEA(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickOnSubmit() {
		Log.info("Trying to click on button  Submit ");
		submit1.click();
		Log.info("Clicked on  Submit successfully");
	}

	public void selectType() {
		Log.info("Trying to click on radio button");
		List<WebElement> radioList = driver
				.findElements(By.xpath("//td[text()[contains(.,'S')]]/input[@type='radio']"));
		 radioList.get(0).click();
	}

}
