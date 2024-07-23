package com.pageobjects.channeluserspages.o2creturn;

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

public class O2CReturn_Page_1 {

	@FindBy(name = "productType")
	private WebElement productType;
	
	@FindBy(name = "submitButton")
	private WebElement submitButton;
	
	@FindBy(name = "resetButton")
	private WebElement resetButton;
	
	@FindBy(xpath = "//ul/li")
	private WebElement successMessage;
	
	
	WebDriver driver;
	
	public O2CReturn_Page_1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectProductType(String ProductCode) {
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		int prodRowCount = ExcelUtility.getRowCount();
		if(prodRowCount>1){
			Select productTypeSelect = new Select(productType);
			productTypeSelect.selectByValue(ProductCode);
		Log.info("User selected Product Type: " + ProductCode);}
		else if(prodRowCount==1){
			Log.info("Only single product exists: "+ ProductCode);
		}
		else{
			Log.info("No product exists.");
		}
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public String getMessage() {
		Log.info("Trying to fetch Success Message from WEB");
		String Message = successMessage.getText();
		Log.info("Message Fetched successfully as: " + Message);
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
