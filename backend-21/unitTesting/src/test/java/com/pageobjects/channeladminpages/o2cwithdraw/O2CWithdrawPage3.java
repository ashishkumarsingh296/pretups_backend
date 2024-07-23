package com.pageobjects.channeladminpages.o2cwithdraw;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CWithdrawPage3 {
	@ FindBy(name = "returnedProductListIndexed[0].requestedQuantity")
	private WebElement quantity;

	@ FindBy(name = "remarks")
	private WebElement remarks;
	
	@ FindBy(id = "smsPin")
	private WebElement pin;

	@ FindBy(name = "submitButton")
	private WebElement submitButton;
	
	@ FindBy(name = "backButton")
	private WebElement backButton;

	WebDriver driver= null;

	public O2CWithdrawPage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	public long enterQuantityforO2CWithdraw1(String productName) {
		Log.info("Trying to enter quantity for O2C Withdraw");
	  
	  /*List<WebElement> Qty=driver.findElements(By.xpath("//input[@name[contains(.,'requestedQuantity')]]"));
		for(int countQty=0; countQty < Qty.size(); countQty++){
			WebElement qtyIndex=driver.findElement(By.xpath("//input[@name='returnedProductListIndexed["+countQty+"].requestedQuantity']"));
			WebElement balance=driver.findElement(By.xpath("//input[@name='returnedProductListIndexed["+countQty+"].requestedQuantity']/preceding::td[1]"));
			String productBalance=balance.getText();
			int prBalance= (int) Double.parseDouble(productBalance);
			int quantity=(int) (prBalance*0.005);
			qtyIndex.sendKeys(String.valueOf(quantity));
			Log.info("User entered Quantity: "+quantity);
		}*/
		String sf1=String.format("//tr//td[text()='%s']/following-sibling::td/input",productName);  
		String sf2=String.format("//tr//td[text()='%s']/following-sibling::td/input/preceding::td[1]",productName);  
		WebElement qtyIndex=driver.findElement(By.xpath(sf1));
		WebElement balance=driver.findElement(By.xpath(sf2));
		String productBalance=balance.getText();
		long prBalance= (long) Double.parseDouble(productBalance);
		long quantity=(long) (prBalance*0.005);
		qtyIndex.sendKeys(String.valueOf(quantity));
		Log.info("Quantity entered successfully as: " + quantity);
		return quantity;
	}
	
	public void enterRemarks(String Remarks) {
		remarks.sendKeys(Remarks);
		Log.info("User entered Remarks: "+Remarks);
	}
	
	public void enterPIN(String PIN) {
		pin.sendKeys(PIN);
		Log.info("User entered PIN: "+PIN);
	}
	
	public void clickSubmitBtn() {
		submitButton.click();
		Log.info("User clicked submit Button.");
	}

	public void clickBackBtn() {
		backButton.click();
		Log.info("User clicked back Button.");
	}

	public boolean pinVisibility(){
		boolean result=false;
		try{
		if(pin.isDisplayed())
			result= true;
		}
		catch(Exception e){
			result= false;
		}
		return result;
	}
	
	public void enterQuantityforO2CWithdraw(String amount) {
		Log.info("Trying to enter quantity for O2C Withdraw");
		WebElement qtyIndex=driver.findElement(By.xpath("//input[@name='returnedProductListIndexed[0].requestedQuantity']"));
		qtyIndex.sendKeys(amount);
		Log.info("Quantity entered successfully as: " + quantity);
	}
}
