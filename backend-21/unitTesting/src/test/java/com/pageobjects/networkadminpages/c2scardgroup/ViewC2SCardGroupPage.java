package com.pageobjects.networkadminpages.c2scardgroup;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ViewC2SCardGroupPage {
	@ FindBy(name = "serviceTypeId")
	private WebElement serviceType;

	@ FindBy(name = "cardGroupSubServiceID")
	private WebElement c2sCardGroupSubService;

	@ FindBy(name = "selectCardGroupSetId")
	private WebElement c2sCardGroupSetName;
	
	@ FindBy(name = "numberOfDays")
	private WebElement lastDays;
	
	@ FindBy(name = "view")
	private WebElement submitButton;

	WebDriver driver= null;
	
	public ViewC2SCardGroupPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectServiceType(String ServiceType) throws InterruptedException {
		Select serviceType1 = new Select(serviceType);
		serviceType1.selectByVisibleText(ServiceType);
		Log.info("User selected Service Type.");
	}
	
	public void selectC2SCardGroupSubService(String C2SCardGroupSubService) throws InterruptedException {
		Select c2sCardGroupSubService1 = new Select(c2sCardGroupSubService);
		c2sCardGroupSubService1.selectByVisibleText(C2SCardGroupSubService);
		Log.info("User selected C2S Card Group Sub Service.");
	}
	
	public boolean selectC2SCardGroupSetName(String C2SCardGroupSetName) throws InterruptedException {
		Select c2sCardGroupSetName1 = new Select(c2sCardGroupSetName);
		try{
		c2sCardGroupSetName1.selectByVisibleText(C2SCardGroupSetName);
		Log.info("User selected C2S Card Group Set Name.");
		return true;
		}
		catch(NoSuchElementException e){
			return false;
		}
	}
	
	public void enterLastDays(String LastDays) throws InterruptedException {
		lastDays.sendKeys(LastDays);
		Log.info("User entered last days.");
	}
	
	public void clickSubmitButton() throws InterruptedException {
		submitButton.click();
		Log.info("User clicked Submit Button.");
	}
}
