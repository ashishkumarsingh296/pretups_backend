package com.pageobjects.networkadminpages.c2ctransferrule;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AssociateC2CTransferRulePage3 {
	
	@FindBy(name = "fromCategory")
	WebElement fromcategory;
	
	@FindBy(name = "toCategory")
	WebElement tocategory;
	
	@FindBy(name = "transferType")
	WebElement transferType;
	
	@FindBy(name = "parentAssociationAllowed")
	WebElement parentAssociationAllowed;
	
	@FindBy(name = "cntrlTransferLevel")
	WebElement cntrlTransferLevel;
	
	@FindBy(xpath = "//*[@name='transferChnlBypassAllowed' and @value='Y']")
	WebElement transferChnlBypassAllowedYes;
	
	@FindBy(xpath = "//*[@name='withdrawChnlBypassAllowed' and @value='Y']")
	WebElement withdrawChnlBypassAllowedYes;
	
	@FindBy(xpath = "//*[@name='returnChnlBypassAllowed' and @value='Y']")
	WebElement returnChnlBypassAllowedYes;
	
	@FindBy(xpath = "//*[@name='directTransferAllowed' and @value='Y']")
	WebElement transferallowed;
	
	@FindBy(xpath = "//*[@name='withdrawAllowed' and @value='Y']")
	WebElement withdrawlallowedyes;
	
	@FindBy(xpath = "//*[@name='withdrawAllowed' and @value='N']")
	WebElement withdrawlallowedno;
	
	@FindBy(name = "cntrlWithdrawLevel")
	WebElement cntrlWithdrawLevel;
	
	@FindBy(xpath = "//*[@name='returnAllowed' and @value='Y']")
	WebElement returnallowedyes;
	
	@FindBy(xpath = "//*[@name='returnAllowed' and @value='N']")
	WebElement returnallowedno;
	
	@FindBy(name = "cntrlReturnLevel")
	WebElement cntrlReturnLevel;
	
	@FindBy(name="productArray")
	List<WebElement> productArray;

	@FindBy(name = "btnAdd")
	WebElement Add;
	
	@FindBy(name = "btnModify")
	WebElement Modify;
	
	@FindBy(name = "btnBack")
	WebElement Back;
	
	
	WebDriver driver= null;

	public AssociateC2CTransferRulePage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectFromCategory(String Fromcategory) {
		Log.info("Trying to select From Category");
		Select select1 = new Select(fromcategory);
		select1.selectByVisibleText(Fromcategory);
		Log.info("From Category Selected successfully");
	}
	
	public void selectToCategory(String Tocategory) {
		Log.info("Trying to select To Category");
		Select select1 = new Select(tocategory);
		select1.selectByVisibleText(Tocategory);
		Log.info("To Category Selected successfully");
	}
	
	public void clickParentAssociationAllowed_Yes()
	{
		Log.info("Trying to select Yes option of Parent association Allowed");
		parentAssociationAllowed.click();
		Log.info("Yes option selected successfully for Parent association Allowed");
	}
	
	public void selectTransferType()
	{
		Log.info("Trying to select Transer type");
		Select select1=new Select(transferType);
		select1.selectByVisibleText("Transfer");
		Log.info("Transfer type selected successfuly");
	}
	
	public void clickTransferAllowed_Yes()
	{
		Log.info("Trying to select Yes option of Transfer Allowed");
		transferallowed.click();
		Log.info("Yes option selected successfully for Transfer Allowed");
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
	
	public void controlTrfLevel()
	{
		Log.info("Trying to select Control Transer level");
		Select select1=new Select(cntrlTransferLevel);
		select1.selectByValue("DOMAIN");
		Log.info("Control transfer level selected successfuly");
	}
	
	public void controlWithDrawLevel()
	{
		Log.info("Trying to select Control Withdraw level");
		Select select1=new Select(cntrlWithdrawLevel);
		select1.selectByValue("DOMAIN");
		Log.info("Control Withdraw level selected successfuly");
	}
	
	public void controlRtrLevel()
	{
		Log.info("Trying to select Control Return level");
		Select select1=new Select(cntrlReturnLevel);
		select1.selectByValue("DOMAIN");
		Log.info("Control Return level selected successfuly");
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
	
	public void chnlbypassTransferAllowed_Yes()
	{
		Log.info("Trying to select Yes option for Channel transfer bypass Allowed");
		transferChnlBypassAllowedYes.click();
		Log.info("Yes option for Channel transfer bypass Allowed selected successfully");
	}
	
	public void chnlbypassWithdrawAllowed_Yes()
	{
		Log.info("Trying to select Yes option for Channel withdraw bypass Allowed");
		withdrawChnlBypassAllowedYes.click();
		Log.info("Yes option for Channel withdraw bypass Allowed selected successfully");
	}
	
	public void chnlbypassReturnAllowed_Yes()
	{
		Log.info("Trying to select Yes option for Channel return bypass Allowed");
		returnChnlBypassAllowedYes.click();
		Log.info("Yes option for Channel return bypass Allowed selected successfully");
	}
	
	public void selectAllProducts() {
		 Log.info("Trying to fetch all available products");
		 for (int i = 0; i<productArray.size(); i++) {
			 Log.info("Trying to select "+productArray.get(i).getText()+" product");
			 productArray.get(i).click();
			 Log.info(productArray.get(i)+" product selected successfully");
		 }
	}
	
	public void clickAddButton()
	{
		Log.info("Trying to click Add Button");
		Add.click();
		Log.info("Add button clicked successfully");
	}
	
	public void Back()
	{
		Log.info("Trying to click Back Button");
		Back.click();
		Log.info("Back Button clicked successfully");
	}
	
	public void Modify()
	{
		Modify.click();
		Log.info("User clicked on Modify.");
	}
	
}
