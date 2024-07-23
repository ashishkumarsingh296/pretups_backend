package com.pageobjects.channeluserspages.c2ctransfer;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CTransferDetailsPageSpring {
	
	@FindBy(id = "refrenceNum")
	private WebElement refrenceNum;

	@FindBy(name = "productList[0].requestedQuantity")
	private WebElement quantityslab0;
	
	@FindBy(name = "productList[1].requestedQuantity")
	private WebElement quantityslab1;

	@FindBy(id = "remarks")
	private WebElement remarks;

	@FindBy(id = "smsPin")
	private WebElement smsPin;

	@FindBy(id = "initiatetransfer")
	public WebElement submitButton;

	@FindBy(id = "reset")
	public WebElement reset;

	@FindBy(id = "back")
	private WebElement backButton;
	
	@FindBy(xpath = "//label[@for='remarks'][@class='error']")
	public WebElement RemarkFieldError;
	
	@FindBy(xpath = "//label[@for='smsPin'][@class='error']")
	public WebElement SMSPINFieldError;

	WebDriver driver = null;
	
	public C2CTransferDetailsPageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterRefNum(String RefNum) {
		try {
		refrenceNum.clear();
		refrenceNum.sendKeys(RefNum);
		Log.info("User entered Reference Number"+RefNum);
		}
		catch (Exception e) {
			Log.info("Reference number field not found.");
		}
	}

	public void enterQuantity0(String Quantityslab0) {
		quantityslab0.sendKeys(Quantityslab0);
		Log.info("User entered Quantityslab0"+Quantityslab0);
	}

	public void enterQuantity1(String Quantityslab1) {
		quantityslab1.sendKeys(Quantityslab1);
		Log.info("User entered Quantityslab1"+Quantityslab1);
	}

	public void enterRemarks(String Remarks) {
		remarks.sendKeys(Remarks);
		Log.info("User entered Remarks"+Remarks);
	}

	public void enterSmsPin(String SmsPin) {
		smsPin.sendKeys(SmsPin);
		Log.info("User entered Sender's smsPin");
	}

	public void clickSubmit() {
		submitButton.click();
		Log.info("User clicked submit");
	}

	public void clickReset() {
		reset.click();
		Log.info("User clicked Reset");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back Button");
	}

/* method to enter quantity for requested quantity field*/
	
	public void enterQuantityforC2C(){
		Log.info("Trying to enter amount.");
		List<WebElement> Qty=driver.findElements(By.xpath("//input[@id[contains(.,'requestedQuantity')]]"));
		for(int countQty=0; countQty < Qty.size(); countQty++){
			WebElement qtyIndex=driver.findElement(By.xpath("//input[@id='productList["+countQty+"].requestedQuantity']"));
			WebElement minTrfAmt=driver.findElement(By.xpath("//*/tbody/tr["+(countQty+1)+"]/td[5]"));
			WebElement maxTrfAmt=driver.findElement(By.xpath("//*/tbody/tr["+(countQty+1)+"]/td[6]"));
			WebElement prtBal=driver.findElement(By.xpath("//*/tbody/tr["+(countQty+1)+"]/td[7]"));
			int prtBalance=(int)Double.parseDouble(prtBal.getText());
			int quantity=(int) (prtBalance*0.2);
			qtyIndex.sendKeys(String.valueOf(quantity));
			Log.info("Amount["+countQty+"] : "+quantity);
		}
	}
	
	public boolean enterBlankValueQuantityforC2C(){
		Log.info("Trying to enter amount.");
		List<WebElement> Qty=driver.findElements(By.xpath("//input[@id[contains(.,'requestedQuantity')]]"));
		for(int countQty=0; countQty < Qty.size(); countQty++){
			WebElement qtyIndex=driver.findElement(By.xpath("//input[@id='productList["+countQty+"].requestedQuantity']"));
			qtyIndex.sendKeys(String.valueOf(""));
			Log.info("Amount["+countQty+"] :" );
		}
		return true;
	}
	
	public void enterQuantityforC2C(String productType, String amount){
			Log.info("Trying to enter amount for "+productType+" .");
			/*WebElement qtyIndex1=driver.findElement(By.xpath("//form/table//table/tbody/tr/td[text()='"+productType+"']/following-sibling::td/input"));*/
			WebElement qtyIndex1=driver.findElement(By.xpath("//span[(text()='"+productType+"')]/../following-sibling::td//input[@type='text']"));
			
			qtyIndex1.sendKeys(amount);
			Log.info("Amount ["+productType+"] : "+amount);
		}

//Calculated amount to be send to all products at a time. 	
	public void enterQuantityforC2C(String[] amount, String[] productype){
			List<WebElement> Qty=driver.findElements(By.xpath("//input[@id[contains(.,'requestedQuantity')]]"));
			for(int countQty=0; countQty < Qty.size(); countQty++){
				List<WebElement> qtyIndex1 = driver.findElements(By.xpath("//input[@name[contains(.,'requestedQuantity')]]"));
				Log.info("Trying to enter amount for "+productype[countQty]+" .");
				qtyIndex1.get(countQty).clear();
				qtyIndex1.get(countQty).sendKeys(amount[countQty]);
				Log.info("Amount ["+productype[countQty]+"] : "+amount[countQty]);
				}
}	
	
	public Boolean getQuantityFieldError(){
		Log.info("Trying to get Quantity field error");
		WebElement element = null;
		String xpath = "//button[@id='alertify-ok']";
		element = driver.findElement(By.xpath(xpath));
		Boolean errorMessage = element.isDisplayed();
		Log.info("Quantity field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getRemarkFieldError(){
		Log.info("Trying to get Remark field error");
		String errorMessage = RemarkFieldError.getText();
		Log.info("Remark field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getSMSPINFieldError(){
		Log.info("Trying to get SMSPIN field error");
		String errorMessage = SMSPINFieldError.getText();
		Log.info("SMSPIN field error: "+errorMessage);
		return errorMessage;
	}
	
	
	

}
