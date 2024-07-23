package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.dbrepository.DBHandler;
import com.utils.Log;

public class ViewVoucherDenomination2 {
	WebDriver driver = null;
	public ViewVoucherDenomination2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (name ="backViewSubCat")
	private WebElement backButton;
	
	public void selectBackButton() {
		Log.info("Trying to click Back Button ");
		backButton.click();
		Log.info("Back Button Successfully Clicked");
	}
	
	public boolean checkParticularDenominationAvailable(String denominationName, String shortName, String mrp, String payableAmount, String Description) {
		boolean elementDisplayed = false;
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("//tr/td[normalize-space() = '" + denominationName);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + shortName);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + mrp);
		if("false".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference("PAYAMT_MRP_SAME")))
			TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + payableAmount);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + Description + "']");
		try {
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		}
		catch(NoSuchElementException e){
			return false;
		}
		return elementDisplayed;
	}

}
