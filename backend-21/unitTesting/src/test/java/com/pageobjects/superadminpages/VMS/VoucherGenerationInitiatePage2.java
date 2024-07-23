package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class VoucherGenerationInitiatePage2 {

	WebDriver driver = null;
	public VoucherGenerationInitiatePage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "slabsListIndexed[0].denomination" )
	private WebElement denomination0;
	
	@FindBy(name = "slabsListIndexed[1].denomination" )
	private WebElement denomination1;
	
	@FindBy(name = "slabsListIndexed[0].quantity" )
	private WebElement quantity0;
	
	@FindBy(name = "slabsListIndexed[0].remarks" )
	private WebElement remarks0;
	
	@FindBy(name = "slabsListIndexed[1].quantity" )
	private WebElement quantity1;
	
	@FindBy(name = "slabsListIndexed[1].remarks" )
	private WebElement remarks1;
	
	@FindBy(name = "submitOrderInit" )
	private WebElement Submit;
	
	@FindBy(name = "back" )
	private WebElement back;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void SelectDenomination(String value){
		Log.info("Trying to Select Denomination");
		Select select = new Select(denomination0);
		select.selectByVisibleText(value);
		Log.info("Denomaination selected  successfully as:"+ value);
		}
	
	public void SelectDenomination2(String value){
		Log.info("Trying to Select Denomination");
		Select select = new Select(denomination1);
		select.selectByVisibleText(value);
		Log.info("Denomaination selected  successfully as:"+ value);
		}
	
	public void EnterQuantity(String value){
		Log.info("Trying to enter Quantity");
		quantity0.sendKeys(value);
		Log.info("Quantity entered  successfully as:"+ value);
		}
	
	public void EnterRemarks(String value){
		Log.info("Trying to enter Remarks");
		remarks0.sendKeys(value);
		Log.info("Remarks entered  successfully as:"+ value);
		}
	
	public void EnterQuantity2(String value){
		Log.info("Trying to enter Quantity");
		quantity1.sendKeys(value);
		Log.info("Quantity entered  successfully as:"+ value);
		}
	
	public void EnterRemarks2(String value){
		Log.info("Trying to enter Remarks");
		remarks1.sendKeys(value);
		Log.info("Remarks entered  successfully as:"+ value);
		}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		Submit.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		back.click();
		Log.info("Clicked on Back Button successfully");
		}
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully as:"+ Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
}
