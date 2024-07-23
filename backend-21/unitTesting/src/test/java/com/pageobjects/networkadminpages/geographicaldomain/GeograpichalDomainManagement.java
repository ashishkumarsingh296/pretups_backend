package com.pageobjects.networkadminpages.geographicaldomain;

import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author Ayush Abhijeet
 * This class Contains the Page Objects for Geographical Domain Management Page
 **/

public class GeograpichalDomainManagement {

	@ FindBy(name = "grphDomainType")
	private WebElement grphDomainType;

	@ FindBy(name = "submitButton")
	private WebElement submitButton;
	
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

	public GeograpichalDomainManagement(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public boolean isDefault(HashMap<String, String> mapParam) {
		Log.info("Trying to click on xpath ");
		boolean element = false;
		String xpath = "";
		String isDefault = _masterVO.getProperty("isDefault");
		xpath = "//tr/td[contains(text(),'" + mapParam.get("domainName") + "')]/following-sibling::td[contains(text(),'"
				+ mapParam.get("domainShortName") + "')]/following-sibling::td[contains(text(),'" + isDefault
				+ "')]";

		element = driver.findElement(By.xpath(xpath)).isDisplayed();
		return element;
	}
	
	public boolean notModified(HashMap<String, String> mapParam) {
		Log.info("Trying to click on xpath ");
		boolean element = false;
		String xpath = "";
		String lookupStatus = DBHandler.AccessHandler.getLookUpName(PretupsI.STATUS_ACTIVE_LOOKUPS, PretupsI.DEF_LOOKUP);
		xpath = "//tr/td[contains(text(),'" + mapParam.get("domainName") + "')]/following-sibling::td[contains(text(),'"
				+ mapParam.get("domainShortName") + "')]/following-sibling::td[contains(text(),'" + lookupStatus
				+ "')]";

		element = driver.findElement(By.xpath(xpath)).isDisplayed();
		return element;
	}
	
	public boolean isModified(HashMap<String, String> mapParam) {
		Log.info("Trying to click on xpath ");
		boolean element = false;
		String xpath = "";
		String lookupStatus = DBHandler.AccessHandler.getLookUpName(PretupsI.STATUS_ACTIVE_LOOKUPS, PretupsI.DEF_LOOKUP);
		xpath = "//tr/td[contains(text(),'" + mapParam.get("domainName") + "')]/following-sibling::td[contains(text(),'"
				+ mapParam.get("domainShortName") + "')]/following-sibling::td[contains(text(),'" + lookupStatus
				+ "')]";

		element = driver.findElement(By.xpath(xpath)).isDisplayed();
		return element;
	}
	
	public void selectDomain(String Domain) {
		Select grphdomain = new Select(grphDomainType);
		grphdomain.selectByVisibleText(Domain);
		Log.info("User selected Domain:"+Domain);
	}
	public void clicksubmitButton() {
		submitButton.click();
		Log.info("User clicked submit Button");
	}
	
	
	
}
