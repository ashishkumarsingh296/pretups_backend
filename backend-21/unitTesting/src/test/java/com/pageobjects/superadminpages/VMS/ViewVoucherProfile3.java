package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewVoucherProfile3 {

	WebDriver driver = null;
	public ViewVoucherProfile3(WebDriver driver) {
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (name ="backViewSubCat")
	private WebElement backButton;
	
	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("Back button clicked successfully");
	}
	
	public boolean checkParticularProfileAvailable(String profileName) {
		boolean elementDisplayed = false;
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("//td[normalize-space() = '" + profileName + "']");
		try {
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		}catch(NoSuchElementException e) {
			return false;
		}
		return elementDisplayed;
	}

}
