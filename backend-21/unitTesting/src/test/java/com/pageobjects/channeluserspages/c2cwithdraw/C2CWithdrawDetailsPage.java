package com.pageobjects.channeluserspages.c2cwithdraw;

import java.util.List;

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

public class C2CWithdrawDetailsPage {

	@FindBy(name = "userCode")
	public WebElement mobileNumber;

	@FindBy(name = "toCategoryCode")
	public WebElement categoryCode;

	@FindBy(name = "toUserName")
	public WebElement toUserName;

	@FindBy(xpath = "//table/tbody/tr/td/table/tbody/tr[1]/td/table/tbody/tr[7]/td[2]/a/img")
	public WebElement searchToUserName;

	@FindBy(name = "submitButton")
	public WebElement submitButton;

	@FindBy(name = "resetButton")
	public WebElement reset;

	WebDriver driver = null;

	public C2CWithdrawDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public String enterMobileNo(String msisdn) {
		mobileNumber.sendKeys(msisdn);
		Log.info("User entered Mobile Number");
		return msisdn;
	}

	public void selectCategoryCode(String Category) {
		Select CategoryCode = new Select(categoryCode);
		CategoryCode.selectByVisibleText(Category);
		Log.info("User selected Category.");
	}

	public void enterToUserName(String ToUser) {
		toUserName.sendKeys(ToUser);
		Log.info("User selected To user");
	}

	public void clickSubmit() {
		submitButton.click();
		Log.info("User clicked submit button");
	}

	public void clickReset() {
		reset.click();
		Log.info("User clicked reset button");
	}
	
	public String enterQuantityforC2C(){
		Log.info("Trying to initiate amounts");
		StringBuilder initiatedQuantities = new StringBuilder();
		List<WebElement> Qty=driver.findElements(By.xpath("//input[@name[contains(.,'requestedQuantity')]]"));
		for(int countQty=0; countQty < Qty.size(); countQty++){
			WebElement qtyIndex=driver.findElement(By.xpath("//input[@name='productListIndexed["+countQty+"].requestedQuantity']"));
			WebElement balance=driver.findElement(By.xpath("//form//table//table/tbody/tr["+(countQty+2)+"]/td[5]"));
			String productBalance=balance.getText();
			
			String productShortCode = driver.findElement(By.xpath("//input[@name='productListIndexed["+countQty+"].requestedQuantity']/parent::td/parent::tr/child::td[1]")).getText();
			String productCode = null;
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			for (int i = 0; i <= rowCount; i++) {
				String sheetShortCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i);
				if (sheetShortCode.equals(productShortCode)) {
					productCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
					break;
				}
			}
			
			int prBalance= (int) Double.parseDouble(productBalance);
			int quantity=(int) (prBalance*0.2);
			qtyIndex.sendKeys(String.valueOf(quantity));
			
			initiatedQuantities.append(productCode + ":" + quantity);
			if (countQty != Qty.size())
				initiatedQuantities.append("|");				
		}
		
		Log.info("Entered Quantities: " + initiatedQuantities.toString());
		return initiatedQuantities.toString();
	}
	
	public void enterQuantityforC2CW(String productType, String amount){
		Log.info("Trying to enter amount for "+productType+" .");
		WebElement qtyIndex1=driver.findElement(By.xpath("//form/table//table/tbody/tr/td[text()='"+productType+"']/following-sibling::td/input"));
		qtyIndex1.sendKeys(amount);
		Log.info("Amount ["+productType+"] : "+amount);}

}
