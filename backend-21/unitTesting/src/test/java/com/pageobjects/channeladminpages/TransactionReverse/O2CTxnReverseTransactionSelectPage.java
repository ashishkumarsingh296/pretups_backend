package com.pageobjects.channeladminpages.TransactionReverse;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CTxnReverseTransactionSelectPage {
	
	@FindBy(xpath = "//table[2]/tbody/tr[2]/td/div")
	private WebElement PageHeader;
	
	@ FindBy(name = "saveRevTrx")
	private WebElement reverseTxnButton;

	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	@FindBy(name = "selectedIndex")
	private WebElement index;
	
	
	
	
	WebDriver driver= null;

	public O2CTxnReverseTransactionSelectPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	/*public void selectTransferNum(String TransferNumber) {

		int rowCount = driver.findElements(By.xpath("//table/tbody/tr[2]/td[2]/form/table/tbody/tr[1]/td/table/tbody/tr")).size();

		for (int i=4; i<=rowCount; i++){
			String transferName = driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/form/table/tbody/tr/td/table/tbody/tr[1]/td/table/tbody/tr["+i+"]/td[2]")).getText();
			if(transferName.equals(TransferNumber)){
				driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/form/table/tbody/tr/td/table/tbody/tr[1]/td/table/tbody/tr["+i+"]/td[1]")).click();
				break;
			}
		}
	}*/
	
	
	
	public static boolean submitReqO2C = false;
	public void selectTransferNum(String TransferNumber) {
		int rowCount = 0;
		rowCount = driver.findElements(By.xpath("//tr/td[normalize-space() = '']/ancestor::tr/td/input[@type='radio']")).size();
		if(rowCount!=0){
		Log.info("Trying to click on Radio Button for specific Transaction ID");
		driver.findElement(By.xpath("//tr/td[normalize-space() = '"+ TransferNumber +"']/ancestor::tr/td/input[@type='radio']")).click();
		Log.info("Radio Button for Transaction ID: " + TransferNumber + " clicked successfully");
		submitReqO2C=true;}
	}

	
	
	public boolean txnlistExists(){
		
		boolean listExist;
		
		boolean radio = driver.findElement(By.name("selectedIndex")).isDisplayed();
		
		if(radio==true){
			listExist = true;
			Log.info("List of transactions Exists");
			
		}
		else{
			listExist= false;
			Log.info("Single Transaction exists for this User, Hence navigating to Reverse Page directly");
		}
		return listExist;
		
	
	}
	
	
	
	public boolean txnListVisibility() {
		Log.info("Trying to check Transaction list exists");
		boolean result = false;
		try {
			if (index.isDisplayed()) {
				Log.info("Multiple transactions exist");
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
			Log.info("Single transaction exists for this User");
		}
		return result;

	}
	
	
	
	
	
	
	public void clickreverseTxnButton() {
		if(submitReqO2C){
		reverseTxnButton.click();
		Log.info("User clicked reverseTxn Button.");}
	}

	public void clickBackBtn() {
		backButton.click();
		Log.info("User clicked back Button.");
	}





public String getPageHeading() {
	String Heading =PageHeader.getText();
	Log.info("PageHeader: "+Heading);
	return Heading;
}

}