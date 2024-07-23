/**
 * 
 */
package com.pageobjects.channeluserspages.c2creturn;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author lokesh.kontey
 *
 */
public class C2CReturnPage2 {

	@FindBy(name="remarks")
	private WebElement remarks;
	
	@FindBy(name="smsPin")
	private WebElement smsPin;
	
	@FindBy(name="submitButton")
	private WebElement submitBtn;
	
	@FindBy(name="resetButton")
	private WebElement resetBtn;
	
	@FindBy(name="backButton")
	private WebElement backBtn;
	
	@FindBy(xpath="//input[@name='submitButton'][@value='Confirm']")//(name="submitButton")
	private WebElement confirmBtn;
	
	@FindBy(xpath="//ul/li")
	private WebElement SuccessMessage;
	
	@FindBy(xpath="//ol/li")
	private WebElement ErrorMessage;
	
	WebDriver driver=null;
	
	public C2CReturnPage2(WebDriver driver){
	this.driver=driver;
	PageFactory.initElements(driver, this);
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click submit button.");
		submitBtn.click();
		Log.info("Submit button clicked successfuly.");
	}

	public void enterSMSPin(String PIN){
		Log.info("Trying to enter PIN.");
		smsPin.sendKeys(PIN);
		Log.info("PIN entered successfully as: "+PIN);
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
	
	
	
	
	public void enterQuantityforC2C1(String productType, String amount){
		Log.info("Trying to enter amount for "+productType+" .");
		WebElement qtyIndex1=driver.findElement(By.xpath("//form/table//table/tbody/tr/td[text()='"+productType+"']/following-sibling::td/input"));
		qtyIndex1.sendKeys(amount);
		Log.info("Amount ["+productType+"] : "+amount);
		
		List<WebElement> Qty=driver.findElements(By.xpath("//input[@name[contains(.,'requestedQuantity')]]"));
		for(int countQty=0; countQty < Qty.size(); countQty++){
			WebElement qtyIndex=driver.findElement(By.xpath("//input[@name='productListIndexed["+countQty+"].requestedQuantity']"));
			WebElement balance=driver.findElement(By.xpath("//form//table//table/tbody/tr["+(countQty+2)+"]/td[5]"));
			
			Log.info("["+countQty + "] "+qtyIndex.getLocation()+" | "+qtyIndex1.getLocation());
			if(!qtyIndex.getLocation().equals(qtyIndex1.getLocation())){
			Log.info("Trying to enter amount to other products.");			
			String productBalance=balance.getText();
			int prBalance= (int) Double.parseDouble(productBalance);
			int quantity=(int) (prBalance*0.2);
			qtyIndex.sendKeys(String.valueOf(quantity));
			Log.info("Amount["+countQty+"] : "+quantity);}
		}
	}
	
	
	//Calculated amount to be send to all products at a time. 	
		public void enterQuantityforC2CAllProducts(String[] amount, String[] productype){
				List<WebElement> Qty=driver.findElements(By.xpath("//input[@name[contains(.,'requestedQuantity')]]"));
				for(int countQty=0; countQty < Qty.size(); countQty++){
					WebElement qtyIndex1=driver.findElement(By.xpath("//form/table//table/tbody/tr/td[text()='"+productype[countQty]+"']/following-sibling::td/input"));
					Log.info("Trying to enter amount for "+productype[countQty]+" .");
					qtyIndex1.sendKeys(amount[countQty]);
					Log.info("Amount ["+productype[countQty]+"] : "+amount[countQty]);}
	}
	
	public void clickConfirmBtn(){
		Log.info("Trying to click confirm button.");
		confirmBtn.click();
		Log.info("Confirm button clicked successfuly.");
	}
	
	public void enterRemarks(String Remarks){
		Log.info("Trying to enter remarks.");
		remarks.sendKeys(Remarks);
		Log.info("Remarks entered successfully as: "+Remarks);
	}
	
	public String getTransactionID() {
		String TransactionMessage[] = new String[2];
		try {
		TransactionMessage[0] = SuccessMessage.getText();
		Log.info("Initiate Message is: "+TransactionMessage[0]);
		TransactionMessage[1] = TransactionMessage[0].substring(TransactionMessage[0].lastIndexOf("CR"),TransactionMessage[0].length()).replaceAll("[.]$","");
		Log.info("Transaction ID Extracted as : "+TransactionMessage[1]);
		}
		catch (NoSuchElementException e)
		{ Log.writeStackTrace(e); }
		catch (Exception e)
		{ Log.writeStackTrace(e); }
		return TransactionMessage[1];
	}
	
	public String getMessage() {
		Log.info("Trying to get Message on GUI.");
		String message=SuccessMessage.getText();
		Log.info("Message fetched successfuly.");
		return message;
	}
	
	public String getErrorMessage() {
		Log.info("Trying to get Message on GUI.");
		String message=ErrorMessage.getText();
		Log.info("ErrorMessage fetched successfuly.");
		return message;
	}
	
}
