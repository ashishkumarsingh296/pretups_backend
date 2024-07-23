package com.pageobjects.networkadminpages.geographicaldomain;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author Ayush Abhijeet
 * This class Contains the Page Objects for Adding Geographical Domain Page
 **/

public class AddGeographicalDomainPage {
	
	@ FindBy(name = "grphDomainCode")
	private WebElement grphDomainCode;
	
	@ FindBy(name = "grphDomainName")
	private WebElement grphDomainName;

	@ FindBy(name = "grphDomainShortName")
	private WebElement grphDomainShortName;
	
	@ FindBy(name = "description")
	private WebElement description;

	@ FindBy(id = "isDefault")
	private WebElement isDefault;
	
	@ FindBy(name = "btnAdd")
	private WebElement btnAdd;

	@ FindBy(name = "btnBack")
	private WebElement btnBack;
	
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

	
	WebDriver driver= null;

	public AddGeographicalDomainPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
     }
	
	public void enterGrphDomainCode(String GrphDomainCode) {
		grphDomainCode.sendKeys(GrphDomainCode);
		Log.info("User entered GrphDomainCode: "+GrphDomainCode);
	}
	
	public void enterGrphDomainName(String GrphDomainName) {
		grphDomainName.sendKeys(GrphDomainName);
		Log.info("User entered GrphDomainName: "+GrphDomainName);
	}
	
	public void enterGrphDomainShortName(String GrphDomainShortName) {
		grphDomainShortName.sendKeys(GrphDomainShortName);
		Log.info("User entered GrphDomainShortName: "+GrphDomainShortName);
	}
	
	public void enterDescription(String Description) {
		description.sendKeys("Automated "+Description+" Creation");
		Log.info("User entered Description: "+Description);
	}
	
	public void selectIsDefault() {
		isDefault.click();
		Log.info(" 'Is Default' Selected");
	}
	
	public void clickAddButton() {
		btnAdd.click();
		Log.info("User clicked Add Button.");
	}
	
	public void clickBackButton() {
		btnBack.click();
		Log.info("User clicked Back Button.");
	}
}
