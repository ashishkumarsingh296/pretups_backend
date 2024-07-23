package com.pageobjects.channeladminpages.TransactionReverse;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CTxnReverseTransactionSelectPage {
	
	@ FindBy(name = "saveRevTrx")
	private WebElement reverseTxnButton;

	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	
	@ FindBy(name = "submitButton")
	private WebElement submitButton;
	
	
	
	WebDriver driver= null;

	public C2CTxnReverseTransactionSelectPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	/*public void selectTransferNum(String TransferNumber) {

		int rowCount = driver.findElements(By.xpath("//table/tbody/tr[1]/td/table/tbody/tr[2]/td[2]/form/table/tbody/tr")).size();

		for (int i=1; i<=rowCount; i++){
			System.out.println("trying to select TransferNum");
			String transferName = driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/form/table/tbody/tr/td/table/tbody/tr[1]/td/table[1]/tbody/tr["+i+"]/td[2]")).getText();
			if(transferName.equals(TransferNumber)){
				driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/form/table/tbody/tr/td/table/tbody/tr[1]/td/table[1]/tbody/tr["+i+"]/td[1]")).click();
				break;
			}
		}
	}*/
	public static boolean submitRequired=false;
	public void selectTransferNum(String TransferNumber) {
		int rowCount = 0;
		rowCount = driver.findElements(By.xpath("//tr/td[normalize-space() = '']/ancestor::tr/td/input[@type='radio']")).size();
		if(rowCount!=0){
		Log.info("Trying to click on Radio Button for specific Transaction ID");
		driver.findElement(By.xpath("//tr/td[normalize-space() = '"+ TransferNumber +"']/ancestor::tr/td/input[@type='radio']")).click();
		Log.info("Radio Button for Transaction ID: " + TransferNumber + " clicked successfully");
		submitRequired=true;}
	}
	
	
	public void clickSubmitBtn() {
		if(submitRequired){
		submitButton.click();
		Log.info("User clicked submit Button.");}
	}
	
	
	
	public void clickreverseTxnButton() {
		reverseTxnButton.click();
		Log.info("User clicked reverseTxn Button.");
	}

	public void clickBackBtn() {
		backButton.click();
		Log.info("User clicked back Button.");
	}

}
