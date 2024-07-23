package com.pageobjects.networkadminpages.geographicaldomain;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author Ayush Abhijeet This class Contains the Page Objects for Confirm Page
 *         of Adding Geographical Domain
 **/

public class AddGeographicalDomainConfirmPage {

	@FindBy(name = "btnAdd")
	private WebElement btnAdd;
	
	@FindBy(name = "btnModify")
	private WebElement btnModify;

	@FindBy(name = "btnCncl")
	private WebElement btnCncl;

	@FindBy(name = "btnAddBack")
	private WebElement btnAddBack;
	
	@FindBy(name = "btnModifyBack")
	private WebElement btnModifyBack;

	@FindBy(xpath = "//tr/td/ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//tr/td/ol/li")
	WebElement errorMessage;

	WebDriver driver = null;

	public AddGeographicalDomainConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickConfirmButton() {
		btnAdd.click();
		Log.info("User clicked Confirm Button.");
	}
	
	public void clickConfirmModifyButton() {
		btnModify.click();
		Log.info("User clicked Confirm Button.");
	}

	public void clickCancelButton() {
		btnCncl.click();
		Log.info("User clicked Cancel Button.");
	}

	public void clickBackButton() {
		btnAddBack.click();
		Log.info("User clicked Back Button.");
	}

	public void clickModifyBackButton() {
		btnModify.click();
		Log.info("User clicked Back Button.");
	}

	
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

}
