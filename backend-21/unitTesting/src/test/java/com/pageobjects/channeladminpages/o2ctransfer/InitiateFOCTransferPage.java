package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class InitiateFOCTransferPage {
	@ FindBy(name = "userCode")
	private WebElement mobileNumber;

	@ FindBy(name = "productTypeWithUserCode")
	private WebElement productType1;

	@ FindBy(xpath = "geoDomainCode")
	private WebElement geographyDomain;
	
	@ FindBy(name = "channelDomain")
	private WebElement domain;

	@ FindBy(name = "categoryCode")
	private WebElement category;

	@ FindBy(xpath = "productType")
	private WebElement productType2;
	
	@ FindBy(name = "submitButton")
	private WebElement submitButton;

	@ FindBy(xpath = "resetbutton")
	private WebElement resetbutton;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@ FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	
	WebDriver driver= null;
	
	public InitiateFOCTransferPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterMobileNumber(String MobileNumber) {
		Log.info("Trying to enter Mobile Number");
		mobileNumber.sendKeys(MobileNumber);
		Log.info("User entered Mobile number: "+MobileNumber);
	}
	
	public void selectProductType1(String ProductType) {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		int prodRowCount = ExcelUtility.getRowCount();
		if(prodRowCount>1){
		Select select = new Select(productType1);
		select.selectByValue(ProductType);
		Log.info("User selected Product Type: "+ProductType);}
		else if(prodRowCount==1){
			Log.info("Only single product exists: "+ProductType);
		}
		else{
			Log.info("No product exists.");
		}
	}
	
	public void selectGeographyDomain(String GeographyDomain) {
		Log.info("Trying to select Geographical Domain");
		Select select = new Select(geographyDomain);
		select.selectByVisibleText(GeographyDomain);
		Log.info("Geographical Domain Selected successfully as: " +GeographyDomain);
	}
	
	public void selectDomain(String Domain) {
		Log.info("Trying to select Domain");
		Select select = new Select(domain);
		select.selectByVisibleText(Domain);
		Log.info("Domain selected successfully as: " +Domain);
	}
	
	public void selectCategory(String Category) {
		Log.info("Trying to select Category");
		Select select = new Select(category);
		select.selectByVisibleText(Category);
		Log.info("Category selected successfully as: " +Category);
	}
	
	public void selectProductType2(String ProductType2) {
		Log.info("Trying to select Product");
		Select select = new Select(productType2);
		select.selectByVisibleText(ProductType2);
		Log.info("Product selected successfully as: " +ProductType2);
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click submit button");
		submitButton.click();
		Log.info("Submit button clicked successfully");
	}
	
	public void clickResetButton() {
		Log.info("Trying to click Reset Button");
		resetbutton.click();
		Log.info("Reset Button clicked successfully");
	}
	
	public String getMessage(){
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
			Message = message.getText();
			Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public String getErrorMessage(){
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
			Message = errorMessage.getText();
			Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public boolean isSelectProductTypeVisible() {
		boolean selectDropdownVisible = driver.findElements(By.xpath("//select[@name='productTypeWithUserCode']")).size() > 0 ;
		boolean flag = false ;
		if (selectDropdownVisible == true ){
			flag = true ;
		}
		else {
			flag = false ;
		}
		return flag ;
	}
	

}
