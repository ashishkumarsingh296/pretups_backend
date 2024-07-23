package com.pageobjects.networkadminpages.c2ctransferrule;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class InitiateC2CTransferRulePage3 {
	
	@FindBy(name = "fromCategory")
	WebElement fromcategory;
	
	@FindBy(name = "toCategory")
	WebElement tocategory;
	
	@FindBy(xpath="//*[@name = 'parentAssociationAllowed' and @value='Y']")
	WebElement parentAssociationAllowedYes;
	
	@FindBy(xpath="//*[@name = 'parentAssociationAllowed' and @value='N']")
	WebElement parentAssociationAllowedNo;
	
	@FindBy(xpath = "//*[@id='directTransferAllowed' and @value='Y']")
	WebElement directtransferallowedyes;
	
	@FindBy(xpath = "//*[@id='directTransferAllowed' and @value='N']")
	WebElement directtransferallowedno;
	
	@FindBy(xpath = "//*[@id='idradio' and @value='Y']")
	WebElement channelbypasstrfAllowedYes;
	
	@FindBy(xpath = "//*[@id='idradio' and @value='N']")
	WebElement channelbypasstrfAllowedNo;
	
	@FindBy(id = "cntrlTransferLevel")
	WebElement cntrlTransferLevel;
	
	@FindBy(xpath = "//*[@name='withdrawAllowed' and @value='Y']")
	WebElement withdrawlallowedyes;
	
	@FindBy(xpath = "//*[@name='withdrawAllowed' and @value='N']")
	WebElement withdrawlallowedno;

	@FindBy(xpath = "//*[@id='withdrawRadio' and @value='Y']")
	WebElement channelbypassWithdrawYes;
	
	@FindBy(xpath = "//*[@id='withdrawRadio' and @value='N']")
	WebElement channelbypassWithdrawNo;
	
	@FindBy(id="cntrlWithdrawLevel")
	WebElement cntrlWithdrawLevel;
	
	@FindBy(xpath="//*[@name='productArray']")
	List<WebElement> products;
	
	@FindBy(name = "btnAdd")
	WebElement Add;
	
	@FindBy(name = "btnBack")
	WebElement Back;
	
	
	WebDriver driver= null;

	public InitiateC2CTransferRulePage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectFromCategory(String Fromcategory) {
		Log.info("Trying to select From Category");
		Select select1 = new Select(fromcategory);
		select1.selectByVisibleText(Fromcategory);
		Log.info("From Category Selected successfully:: "+Fromcategory);
	}
	
	public void selectToCategory(String Tocategory) {
		Log.info("Trying to select To Category");
		Select select1 = new Select(tocategory);
		select1.selectByVisibleText(Tocategory);
		Log.info("To Category Selected successfully:: "+Tocategory);
	}
	
	public void parentAssociationAllowed_Yes()
	{try{
		Log.info("Trying to select Yes option of Parent association Allowed");
		parentAssociationAllowedYes.click();
		Log.info("Yes option selected successfully for Parent association Allowed");}
	catch(Exception e){
		Log.info("Parent association Allowed not found");
		Log.writeStackTrace(e);
	}
	}
	
	public void parentAssociationAllowed_No()
	{try{
		Log.info("Trying to select No option of Direct Transfer Allowed");
		parentAssociationAllowedNo.click();
		Log.info("No option selected successfully for Direct Transfer Allowed");}
	catch(Exception e){
		Log.info("Direct Transfer Allowed not found");
		Log.writeStackTrace(e);
	}
	}
	
	//Transfer
	
	public void clickTransferAllowed_Yes()
	{try{
		Log.info("Trying to select Yes option of Direct Transfer Allowed");
		directtransferallowedyes.click();
		Log.info("Yes option selected successfully for Direct Transfer Allowed");}
	catch(Exception e){
		Log.info("Direct Transfer Allowed not found");
		Log.writeStackTrace(e);
	}
	}
	
	public void clickTransferAllowed_No()
	{try{
		Log.info("Trying to select No option of Direct Transfer Allowed");
		directtransferallowedno.click();
		Log.info("No option selected successfully for Direct Transfer Allowed");}
	catch(Exception e){
		Log.info("Direct Transfer Allowed not found");
		Log.writeStackTrace(e);
	}
	}
	
	public void channelByPassTransfer_Yes()
	{try{
		Log.info("Trying to select Yes option of Channel ByPass Transfer Allowed");
		channelbypasstrfAllowedYes.click();
		Log.info("Yes option selected successfully for Channel ByPass Transfer Allowed");}
	catch(Exception e){
		Log.info("Yes option of Channel ByPass Transfer Allowed not found");
		Log.writeStackTrace(e);
	}
	}

	public void channelByPassTransfer_No()
	{try{
		Log.info("Trying to select No option of Channel ByPass Transfer Allowed");
		channelbypasstrfAllowedNo.click();
		Log.info("No option selected successfully for Channel ByPass Transfer Allowed");}
	catch(Exception e){
		Log.info("No option of Channel ByPass Transfer Allowed");
		Log.writeStackTrace(e);
	}
	}
	
	public void controlTrfLevel()
	{try{
		Log.info("Trying to select Control transfer Level");
		Select select1= new Select(cntrlTransferLevel);
		select1.selectByValue("DOMAIN");;
		Log.info("Control trasnfer level selected successfuly");}
	catch(Exception e){
		Log.info("Control trasnfer level not found");
		Log.writeStackTrace(e);
	}
	}
	
	
	//Withdraw
	
	public void clickWithdrawlAllowed_Yes()
	{try{
		Log.info("Trying to select Yes Option for Withdraw Allowed");
		withdrawlallowedyes.click();
		Log.info("Yes Option for Withdraw Allowed selected successfully");}
	catch(Exception e){
		Log.info("Yes Option for Withdraw Allowed not found");
		Log.writeStackTrace(e);
	}
	}
	
	public void clickWithdrawlAllowed_No()
	{try{
		Log.info("Trying to select No Option for Withdraw Allowed");
		withdrawlallowedno.click();
		Log.info("No Option for Withdraw Allowed selected successfully");}
	catch(Exception e){
		Log.info("No Option for Withdraw Allowed not found");
		Log.writeStackTrace(e);
	}
	}

	public void channelByPassWithdraw_Yes()
	{try{
		Log.info("Trying to select Yes option of Channel ByPass in Withdrawal Allowed");
		channelbypassWithdrawYes.click();
		Log.info("Yes option selected successfully for Channel ByPass in Withdrawal Allowed");}
	catch(Exception e){
		Log.info("Yes option of Channel ByPass in Withdrawal Allowed not found");
		Log.writeStackTrace(e);
	}
	}

	public void channelByPassWithdraw_No()
	{try{
		Log.info("Trying to select No option of Channel ByPass in Withdraw Allowed");
		channelbypassWithdrawNo.click();
		Log.info("No option selected successfully for Channel ByPass in Withdrawal Allowed");}
	catch(Exception e){
		Log.info("No option of Channel ByPass in Withdraw Allowed not found");
		Log.writeStackTrace(e);
	}
	}
	
	public void controlWithdrawLevel()
	{try{
		Log.info("Trying to select Control Withdraw Level");
		Select select1= new Select(cntrlWithdrawLevel);
		select1.selectByValue("DOMAIN");
		Log.info("Control Withdraw level selected successfuly");}
	catch(Exception e){
		Log.info("Control Withdraw level not found");
		Log.writeStackTrace(e);
	}
	}
	
	
	//ProductsArray
	
	public void selectAllProducts() {
		 Log.info("Trying to fetch all available products");
		 //List<WebElement> AllProducts = products;
		 for (int i = 0; i<products.size(); i++) {
			 Log.info("Trying to select "+products.get(i).getText()+" product");
			 products.get(i).click();
			 Log.info(products.get(i)+" product selected successfully");
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
	
	
}
