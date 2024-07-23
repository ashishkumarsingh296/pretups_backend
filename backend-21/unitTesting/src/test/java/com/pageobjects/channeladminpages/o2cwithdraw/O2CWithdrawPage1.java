package com.pageobjects.channeladminpages.o2cwithdraw;

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

public class O2CWithdrawPage1 {
	@ FindBy(name = "userCode")
	private WebElement mobileNumber;

	@ FindBy(name = "productTypeWithUserCode")
	private WebElement productType;
	
	@ FindBy(name = "submitButton")
	private WebElement submitButton;
	
	@ FindBy(xpath = "//table/tbody/tr[2]/td[2]/ul/li")
	private WebElement message;
	
	WebDriver driver= null;

	public O2CWithdrawPage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterMobileNumber(String MobileNumber) {
		mobileNumber.sendKeys(MobileNumber);
		Log.info("User entered Mobile number: "+MobileNumber);
	}
	public void selectProductType(String index) {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
		int prodRowCount = ExcelUtility.getRowCount();
		if(prodRowCount>1){
		Select select = new Select(productType);
		select.selectByValue(index);
		Log.info("User selected Product Type: "+index);}
		else if(prodRowCount==1){
			Log.info("Only single product exists: "+index);
		}
		else{
			Log.info("No product exists.");
		}
	}
	
	public void clickSubmitBtn() {
		submitButton.click();
		Log.info("User clicked submit Button.");
	}

	public String getMessage(){
		return message.getText();
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
