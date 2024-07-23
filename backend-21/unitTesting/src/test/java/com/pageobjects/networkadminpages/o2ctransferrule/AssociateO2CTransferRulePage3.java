package com.pageobjects.networkadminpages.o2ctransferrule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;
import com.utils._masterVO;

public class AssociateO2CTransferRulePage3 {
	
	@FindBy(name = "toCategory")
	WebElement tocategory;
	
	@FindBy(xpath = "//*[@name='transferAllowed' and @value='Y']")
	WebElement transferallowed;
	
	@FindBy(name = "focAllowed")
	WebElement focallowed;
	
	@FindBy(name = "dpAllowed")
	WebElement bulkcommissonpayout;
	
	@FindBy(name = "firstApprovalLimit")
	WebElement firstapprovallimit;
	
	@FindBy(name = "secondApprovalLimit")
	WebElement secondapprovallimit;
	
	@FindBy(xpath = "//*[@name='withdrawAllowed' and @value='Y']")
	WebElement withdrawlallowedyes;
	
	@FindBy(xpath = "//*[@name='withdrawAllowed' and @value='N']")
	WebElement withdrawlallowedno;
	
	@FindBy(xpath = "//*[@name='returnAllowed' and @value='Y']")
	WebElement returnallowedyes;
	
	@FindBy(xpath = "//*[@name='returnAllowed' and @value='N']")
	WebElement returnallowedno;

	@FindBy(name = "btnAdd")
	WebElement Add;
	
	@FindBy(name = "btnModify")
	WebElement Modify;
	
	@FindBy(name = "btnBack")
	WebElement Back;
	
	
	WebDriver driver= null;

	public AssociateO2CTransferRulePage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void selectToCategory(String Tocategory) {
		Log.info("Trying to select To Category");
		Select select1 = new Select(tocategory);
		select1.selectByVisibleText(Tocategory);
		Log.info("To Category Selected successfully");
	}
	
	public void clickTransferAllowed_Yes()
	{
		Log.info("Trying to select Yes option of Transfer Allowed");
		transferallowed.click();
		Log.info("Yes option selected successfully for Transfer Allowed");
	}
	
	public void checkFOCAllowed()
	{
		Log.info("Trying to click on FOC Allowed checkbox");
		focallowed.click();
		Log.info("FOC Allowed checkbox clicked successfully");
	}
	
	public void checkBulkCommissonPayout()
	{
		Log.info("Trying to click Bulk Commission Payout checkbox");
		bulkcommissonpayout.click();
		Log.info("Bulk Commission Payout checked successfully");
	}
	
	public void inputFirstApprovalLimit(String approvallimit)
	{
		Log.info("Trying to enter First Approval Limit");
		firstapprovallimit.clear();
		firstapprovallimit.sendKeys(approvallimit);
		Log.info("First Approval Limit entered successfully: "+approvallimit);
	}
	
	public void inputSecondApprovalLimit(String approvallimit)
	{
		Log.info("Trying to enter Second Approval Limit");
		secondapprovallimit.clear();
		secondapprovallimit.sendKeys(approvallimit);
		Log.info("Second Approval Limit entered successfully: "+approvallimit);
	}
	
	public void clickWithdrawlAllowed_Yes()
	{
		Log.info("Trying to select Yes Option for Withdraw Allowed");
		withdrawlallowedyes.click();
		Log.info("Yes Option for Withdraw Allowed selected successfully");
	}
	
	public void clickWithdrawlAllowed_No()
	{
		Log.info("Trying to select No Option for Withdraw Allowed");
		withdrawlallowedno.click();
		Log.info("No Option for Withdraw Allowed selected successfully");
	}
	
	public void clickReturnAllowed_Yes()
	{
		Log.info("Trying to select Yes option for Return Allowed");
		returnallowedyes.click();
		Log.info("Yes option for Return Allowed selected successfully");
	}
	
	public void clickReturnAllowed_No()
	{
		Log.info("Trying to select No Option for Return Allowed");
		returnallowedno.click();
		Log.info("No Option for Return Allowed selected successfully");
	}
	
	public void selectServices(String services, String O2CApproval1Limit, String O2CApproval2Limit) {
		ArrayList<String> aList = new ArrayList(Arrays.asList(services.split("[ ]*,[ ]*")));
		for (int i=0; i<aList.size(); i++) {
			String serviceCode = aList.get(i).toString().trim();
			String O2CTransferCode = _masterVO.getProperty("O2CTransferCode");
			String FOCCode = _masterVO.getProperty("FOCCode");
			String O2CWithdrawCode = _masterVO.getProperty("O2CWithdrawCode");
			String O2CReturnCode = _masterVO.getProperty("O2CReturnCode");
			if (serviceCode.equals(O2CTransferCode))
				{
				clickTransferAllowed_Yes();
				inputFirstApprovalLimit(O2CApproval1Limit);
				inputSecondApprovalLimit(O2CApproval2Limit);
				}
			else if (serviceCode.equals(FOCCode))
				checkFOCAllowed();
			else if (serviceCode.equals(O2CWithdrawCode))
				clickWithdrawlAllowed_Yes();
			else if (serviceCode.equals(O2CReturnCode))
				clickReturnAllowed_Yes();
			}
		}
	
	public void selectAllProducts() {
		 Log.info("Trying to fetch all available products");
		 List<WebElement> AllProducts = driver.findElements(By.name("productArray"));
		 for (int i = 0; i<AllProducts.size(); i++) {
			 Log.info("Trying to select "+AllProducts.get(i).getText()+" product");
			 AllProducts.get(i).click();
			 Log.info(AllProducts.get(i)+" product selected successfully");
		 }
	}
	
	public void clickAddButton()
	{
		Log.info("Trying to click Add Button");
		Add.click();
		Log.info("Add button clicked successfully");
	}
	
	public void clickBackButton()
	{
		Log.info("Trying to click Back Button");
		Back.click();
		Log.info("Back Button clicked successfully");
	}
	
	public void clickModifyButton()
	{
		Modify.click();
		Log.info("User clicked on Modify.");
	}
	
}
