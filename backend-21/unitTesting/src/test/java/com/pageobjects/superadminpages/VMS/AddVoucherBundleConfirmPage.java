package com.pageobjects.superadminpages.VMS;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.utils.Log;

//jj
public class AddVoucherBundleConfirmPage {
	
	WebDriver driver = null;
	public AddVoucherBundleConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	//WebDriverWait wait = new WebDriverWait(driver, 10).until(E);

	@FindBy(name = "confirmAddSubCat")
	WebElement Submit;
	
	@FindBy(name = "backAddVoucherBundle")
	WebElement backButton;
	
	@FindBy(name = "totalValue")
	WebElement totalValue;
	
	
	public void clickSubmit() {
		Log.info("Trying to click Submit button");
		Submit.click();
		Log.info("Submit clicked successfully");
	}
	
	public void clickBackButton() {
		Log.info("Trying to click back button");
		backButton.click();
		Log.info("back clicked successfully");
	}
	
	public String getRetailPrice() {
		Log.info("Trying to get retailPrice");
		return totalValue.getText().trim();
	}
	
	public void dismissAlert() {
		driver.switchTo().alert().dismiss();
	}

	public void waitDefault() {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
	}
	

}
