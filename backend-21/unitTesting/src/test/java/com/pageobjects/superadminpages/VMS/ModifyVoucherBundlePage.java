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

//jj
public class ModifyVoucherBundlePage {
	
	WebDriver driver = null;
	public ModifyVoucherBundlePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	//WebDriverWait wait = new WebDriverWait(driver, 10).until(E);
	
	@FindBy(name = "bundleName")
	WebElement BundleName;
	
	@FindBy(name = "prefixID")
	WebElement BundlePrefix;
	
	@FindBy(name = "voucherType")
	WebElement voucherType;
	
	@FindBy(name = "segment")
	WebElement voucherSegment;
	
	@FindBy(name = "denomination")
	WebElement voucherDenomination;
	
	@FindBy(name = "voucherProfile")
	WebElement voucherProfile;
	
	@FindBy(name = "quantity")
	WebElement voucherQuantity;
	
	@FindBy(name = "totalValue")
	WebElement BundleValue;

	@FindBy(name = "selectSubmitMod")
	WebElement Submit;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	WebElement radioButton;
	WebElement bundleNameEL, bundleIdEL;
	String bundleName, bundleId;
	
	String VBI = "voucherBundleIndexed", VT = "voucherType", VS = "segment", 
			VD = "denomination", VP = "voucherProfile", QT="quantity" ; 
	
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
				 driver.findElement(By.xpath("//table[@class='back']/tbody/tr[" + i + "]/td[1]//input[@name='radioVal']")).click();
				 Log.info("Clicked Bundle Row successfully");
				 bundleInRow = true;
				 break;
			}
		}
		return bundleInRow;
	}
	
	
	public void clickSubmit() {
		Log.info("Trying to click Submit button");
		Submit.click();
		Log.info("Submit clicked successfully");
	}
	
	public String getMessage(){
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(message));
		Message = message.getText();
		Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(errorMessage));
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully as:"+Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
	
	public void dismissAlert() {
		driver.switchTo().alert().dismiss();
	}

	public void waitDefault() {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
	}
	

}
