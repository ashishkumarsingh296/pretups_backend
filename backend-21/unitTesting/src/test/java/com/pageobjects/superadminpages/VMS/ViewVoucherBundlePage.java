package com.pageobjects.superadminpages.VMS;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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

public class ViewVoucherBundlePage {

	WebDriver driver = null;
	public ViewVoucherBundlePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	WebElement radioButton;
	WebElement bundleNameEL, bundleIdEL;
	
	String bundleName, bundleId;
	
	public boolean checkParticularVoucherBundle(String VBName, String VBPrefix) {
		Log.info("Trying to find Bundle: " + VBName);
		boolean elementDisplayed = false;
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("//td[normalize-space() = '" + VBName);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + VBPrefix + "']");
		try {
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		}catch(NoSuchElementException e) {
			return false;
		}
		Log.info("Found Bundle: " + VBName);
		return elementDisplayed;
	}
	
	public boolean clickVoucherBundle(String VBName, String VBPrefix) {
		Log.info("Trying to find Bundle: " + VBName);
		boolean elementDisplayed = false;
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("//td[normalize-space() = '" + VBName);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + VBPrefix + "']");
		try {
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		}catch(NoSuchElementException e) {
			elementDisplayed = false;
		}
		if(elementDisplayed)
			Log.info("Found Bundle: " + VBName);
		
		List<WebElement> rows = driver.findElements(By.xpath("//table[@class='back']/tbody/tr/td[1]"));
		int rowCount = rows.size();
		Log.info("Number of rows in table: " + rowCount);
		boolean bundleInRow = false ;
		for(int i=2; i <= rowCount ; i++ ) {
			bundleNameEL = driver.findElement(By.xpath("//table[@class='back']/tbody/tr[" + i + "]/td[2]"));
			bundleName = bundleNameEL.getText().trim();
			bundleIdEL = driver.findElement(By.xpath("//table[@class='back']/tbody/tr[" + i + "]/td[3]"));
			bundleId = bundleIdEL.getText().trim();
			
			if(bundleName.equals(VBName) && bundleId.equals(VBPrefix)) {
				Log.info("Found Bundle in Row: " + i);
				 driver.findElement(By.xpath("//table[@class='back']/tbody/tr[" + i + "]/td[1]")).click();
				 Log.info("Clicked Bundle Row successfully");
				 bundleInRow = true;
				 break;
			}
		}
		return bundleInRow;
	}
	
}
