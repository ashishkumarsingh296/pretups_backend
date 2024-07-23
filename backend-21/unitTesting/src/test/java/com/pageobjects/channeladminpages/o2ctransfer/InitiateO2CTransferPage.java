package com.pageobjects.channeladminpages.o2ctransfer;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class InitiateO2CTransferPage {
	
	@ FindBy(name = "distributorType")
	private WebElement DistributionType;
	
	@FindBy(name="distributorMode")
	private WebElement distributionMode;
	
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
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	
	WebDriver driver= null;
	
	public InitiateO2CTransferPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public boolean selectDistributionType(String DistributionType) {
		Log.info("Trying to select Distribution Type");
		boolean status = false;
		try {
		Select select = new Select(this.DistributionType);
		select.selectByValue(DistributionType);
		Log.info("Distribution Type selected successfully as: " + DistributionType);
		status = true;
		}
		catch (Exception e) {
			Log.info("Distribution Type Dropdown not found");
		}
		return status;
	}
	
	public boolean selectDistributionMode(String DistributionMode) {
		Log.info("Trying to select Distribution Type");
		boolean status = false;
		try {
		if(distributionMode.isDisplayed()) {
			Select select = new Select(this.distributionMode);
			select.selectByValue(DistributionMode);
		}
		Log.info("Distribution Mode selected successfully as: " + DistributionMode);
		status = true;
		}
		catch (Exception e) {
			Log.info("Distribution Mode Dropdown not found");
		}
		return status;
	}
	
	public boolean DistributionTypeIsDisplayed() {
		Log.info("Trying to get Distribution Type Dropdown Status");
		boolean status = false;
		try {
		status = DistributionType.isDisplayed();
		Log.info("Distribution Type Dropdown available in system");
		}
		catch (Exception e) {
			Log.info("Distribution Type Dropdown not found");
		}
		return status;
	}
	
	public void enterMobileNumber(String MobileNumber) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mobileNumber.sendKeys(MobileNumber);
		Log.info("User entered Mobile number: "+MobileNumber);
	}
	public void selectProductType1(String index) {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		int prodRowCount = ExcelUtility.getRowCount();

		/** -- Added By Krishan on 31/12/2018 in order to Handle Dropdown in case of Voucher Services available **/
		String VOUCHER_TRACKING_ALLOWED = DBHandler.AccessHandler.getPreference(null, _masterVO.getProperty(MasterI.NETWORK_CODE), "VOUCHER_TRACKING_ALLOWED");
		if (VOUCHER_TRACKING_ALLOWED == null)
			VOUCHER_TRACKING_ALLOWED = "FALSE";

		if (prodRowCount > 1 || VOUCHER_TRACKING_ALLOWED.equalsIgnoreCase("TRUE")) {
			Select select = new Select(productType1);
			select.selectByValue(index);
			Log.info("User selected Product Type: " + index);
		} else if (prodRowCount == 1) {
			Log.info("Only single product exists: " + index);
		} else {
			Log.info("No product exists.");
		}
	}
	public void selectGeographyDomain(String GeographyDomain) {
		Select select = new Select(geographyDomain);
		select.selectByVisibleText(GeographyDomain);
		Log.info("User selected Geography Domain." +GeographyDomain);
	}
	public void selectDomain(String Domain) {
		Select select = new Select(domain);
		select.selectByVisibleText(Domain);
		Log.info("User selected Domain." +Domain);
	}
	public void selectCategory(String Category) {
		Select select = new Select(category);
		select.selectByVisibleText(Category);
		Log.info("User selected Category." +Category);
	}
	public void selectProductType2(String ProductType2) {
		Select select = new Select(productType2);
		select.selectByVisibleText(ProductType2);
		Log.info("User selected Product Type2." +ProductType2);
	}
	
	public void clickSubmitButton() {
		submitButton.click();
		Log.info("User clicked Submit button");
	}
	
	public void clickResetButton() {
		resetbutton.click();
		Log.info("User clicked Reset button");
	}
	
	public int getProductTypeIndex(){
		Select select = new Select(productType1);
		ArrayList <WebElement> productType= (ArrayList<WebElement>) select.getOptions();
		int size= productType.size();
		return --size;
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
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully");
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
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
