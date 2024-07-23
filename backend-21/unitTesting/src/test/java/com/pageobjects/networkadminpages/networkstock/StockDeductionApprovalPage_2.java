package com.pageobjects.networkadminpages.networkstock;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author krishan.chawla
 * This class is created for Approving Network Stock
 **/

public class StockDeductionApprovalPage_2 {

	WebDriver driver= null;
	long approvalLimit;
	
	@ FindBy(name="btnReject")
	private WebElement RejectButton;
	
	@FindBy(name="btnOk")
	private WebElement ApproveButton;
	
	@FindBy(name="btnSubmit")
	private WebElement confirmButton;
	
	@ FindBy(name="firstLevelRemarks")
	private WebElement firstLevelRemarks;
	
	@FindBy(how=How.XPATH,using="//tr/td/ul")
	private WebElement SuccessMessage;
		
	public StockDeductionApprovalPage_2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterApproval1Remarks(String remarks) {
		Log.info("Trying to Enter Approval Level 1 Remarks");
		firstLevelRemarks.sendKeys(remarks);
		Log.info("Approval Level 1 Remarks entered successfully");
	}
	
	public void clickReject() {
		Log.info("Trying to click Reject Transaction Button");
		RejectButton.click();
		Log.info("Reject Transaction Button clicked successfully");
	}
	
	public void clickApprove() {
		Log.info("Trying to click Approve Button");
		ApproveButton.click();
		Log.info("Approve Button clicked successfully");
	}
	
	public void clickConfirmButton() {
		Log.info("Trying to click Confirm button");
		confirmButton.click();
		Log.info("Confirm Button clicked successfully");
	}
	
	public void PressOkOnConfirmRejectDialog() {
		Log.info("Alert: "+driver.switchTo().alert().getText());
		Log.info("Trying to click OK on Alert");
		driver.switchTo().alert().accept();
		Log.info("Alert accepted successfully");
	}
	
	public String getMessage() {
		String Message = null;
		try {
		Message = SuccessMessage.getText();	
		Log.info("Level Approval Message is: "+Message);
		}
		catch (NoSuchElementException e)
		{ Log.writeStackTrace(e); }
		catch (Exception e)
		{ Log.writeStackTrace(e); }
		return Message;
	}
	
	//Initializing Elements dynamically according to listSize
	public int inputProductsAmount(int stockAmount) {
		List<WebElement> productListSize;
		int totalStockInititated = 0;
		productListSize = driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.approvedQuantityStr')]]"));
		Log.info("Number of Products found as: "+productListSize.size());
		for (int i=0;i<productListSize.size();i++){
			WebElement Product = driver.findElement(By.xpath("//input[@type='text' and @name[contains(.,'["+i+"].approvedQuantityStr')]]"));
			Product.clear();
			Product.sendKeys(""+stockAmount);
			totalStockInititated = totalStockInititated + stockAmount;
		}
		Log.info("Entered Amount as: "+stockAmount+" for all products and application returned "+totalStockInititated+" as total initiated amount");
		return totalStockInititated;
	}
	
}