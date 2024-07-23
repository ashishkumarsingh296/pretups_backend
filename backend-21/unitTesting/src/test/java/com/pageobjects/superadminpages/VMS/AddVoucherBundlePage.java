package com.pageobjects.superadminpages.VMS;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
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
public class AddVoucherBundlePage {
	
	WebDriver driver = null;
	public AddVoucherBundlePage(WebDriver driver) {
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
	
	@FindBy(name = "value")
	WebElement voucherValue;
	
	@FindBy(name = "totalValue")
	WebElement BundleValue;

	@FindBy(name = "addVoucherBundleSubmit")
	WebElement Submit;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	String VBI = "voucherBundleIndexed", VT = "voucherType", VS = "segment", 
			VD = "denomination", VP = "voucherProfile", QT="quantity", VAL="val"; 
	Alert alert;
	
	
	public void enterBundleName(String bundle) {
		Log.info("Trying to enter bundle name as: " + bundle);
		BundleName.sendKeys(bundle);
		Log.info("Bundle Name entered successfully");
	}
	
	public void enterBundlePrefix(String prefix) {
		Log.info("Trying to enter bundle prefix as: " + prefix);
		BundlePrefix.sendKeys(prefix);
		Log.info("Bundle Prefix entered successfully");
	}
	
	/*public void selectVoucherTypeByIndex(int index, String type) {
		voucherType = driver.findElement(By.name(VBI + "[" + index + "]" + "." + VT));
		Select typeEL = new Select(voucherType);
		typeEL.selectByVisibleText(type);
		new WebDriverWait(driver, 10);
		Log.info("Selected Voucher Type successfully");
	}*/
	public void selectVoucherTypeByIndex(int index, String type) {
		voucherType = driver.findElement(By.name(VBI + "[" + index + "]" + "." + VT));
		Select typeEL = new Select(voucherType);
		typeEL.selectByValue(type);
		new WebDriverWait(driver, 10);
		Log.info("Selected Voucher Type successfully");
	}
	
	
	public void selectVoucherSegmentByIndex(int index, String segment) {
		voucherSegment = driver.findElement(By.name(VBI + "[" + index + "]" + "." + VS));
		Select segmentEL = new Select(voucherSegment);
		segmentEL.selectByVisibleText(segment);
		Log.info("Selected Voucher Segment successfully");
	}
	
	public void selectVoucherDenominationByIndex(int index, String denomination) {
		voucherDenomination = driver.findElement(By.name(VBI + "[" + index + "]" + "." + VD));
		Select denominationEL = new Select(voucherDenomination);
		denominationEL.selectByVisibleText(denomination);
		Log.info("Selected Voucher Denomination successfully");
	}
	
	public void selectVoucherProfileByIndex(int index, String profile) {
		voucherProfile = driver.findElement(By.name(VBI + "[" + index + "]" + "." + VP));
		Select profileEL = new Select(voucherProfile);
		profileEL.selectByVisibleText(profile);
		Log.info("Selected Voucher Profile successfully");
	}
	
	public void enterVoucherQuantityByIndex(int index, String quantity) {
		voucherQuantity = driver.findElement(By.name(VBI + "[" + index + "]" + "." + QT));
		Log.info("Trying to enter Voucher Quantity");
		voucherQuantity.clear();
		voucherQuantity.sendKeys(quantity);
		Log.info("Voucher quantity set as: "+ quantity);
	}
	
	public void clickVoucherValueByIndex(int index) {
		voucherValue = driver.findElement(By.name(VBI + "[" + index + "]" + "." +VAL));
		Log.info("Trying to click Value Field");
		voucherValue.click();
		Log.info("Voucher value field clicked");
	}
	
	public String getTotalValue() {
		String totalValue = BundleValue.getAttribute("value");
		Log.info("Total Bundle Value obtained as: "+ totalValue);
		return totalValue;
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
	
	public String getAlertText() {
		String alertText = null;
		Log.info("Reading Alert Text");
		try {
		alert = driver.switchTo().alert();
		alertText = alert.getText();
		alert.accept();
		Log.info("Alert Text is: " + alertText );
		}catch(org.openqa.selenium.NoSuchElementException e) {
			Log.info("Alert Not Found");
		}
		return alertText;
	}
	
	

}
