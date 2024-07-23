package com.pageobjects.networkadminpages.homepage;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.classes.BaseTest;
import com.utils.Log;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author krishan.chawla
 *
 */
public class TransferRulesSubCategories extends BaseTest {

	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=OPINTRF001')]]")
	private WebElement InitiateO2CTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=OPAPTRF001')]]")
	private WebElement ApproveO2CTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=OPTRTRF001')]]")
	private WebElement AssociateO2CTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=TRFRUL006')]]")
	private WebElement AddC2STransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=TRFRUL001')]]")
	private WebElement AddP2PTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CHINTRF001')]]")
	private WebElement InitiateC2CTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CHAPTRF001')]]")
	private WebElement ApproveC2CTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CHNLTRF001')]]")
	private WebElement AssociateC2CTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=TRFRUL008')]]")
	private WebElement ModifyC2CTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=TRFRUL013')]]")
	private WebElement ModifyC2CTransferRuleOrderBy;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=TRFRUL003')]]")
	private WebElement ModifyP2PTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=TRFRUL011')]]")
	private WebElement viewP2PTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=TRFRUL012')]]")
	private WebElement viewC2STransferRule;
	
	boolean AssociateC2CLinkStatus;
	
	WebDriver driver= null;

	public TransferRulesSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickO2CInitiateTR() {
		Log.info("Trying to Click Initiate O2C Transfer Rule link");
		InitiateO2CTransferRule.click();
		Log.info("Initiate O2C Transfer Rule link Clicked Successfully");
	}
	
	public boolean checkIfAssociateO2CTransferRuleLinkExists () {
		boolean LinkStatus;
		try {
			Log.info("Trying to check if Associate O2C Transfer Rule link exists");
			LinkStatus = AssociateO2CTransferRule.isDisplayed();
			Log.info("Associate O2C Transfer Rule link found");
		}
		catch (NoSuchElementException e) {
			Log.info("Associate O2C Transfer Rule link not found");
			LinkStatus = false;
		}
		return LinkStatus;
	}
	
	public void clickO2CTransferRuleApproval() {
		Log.info("Trying to click O2C Transfer Rule Approval link");
		ApproveO2CTransferRule.click();
		Log.info("O2C Transfer Rule Approval link clicked successfully");
	}
	
	public void clickAssociateO2CTransferRule() {
		Log.info("Trying to click Associate O2C Transfer Rule link");
		AssociateO2CTransferRule.click();
		Log.info("Associate O2C Transfer Rule link clicked successfully");
	}
	
	public void clickAddC2STransferRule() {
		Log.info("Trying to click Add C2S Transfer Rules link");
		AddC2STransferRule.click();
		Log.info("Add C2S Transfer Rules link clicked successfully");
	}
	
	public void clickAddP2PTransferRule() {
		Log.info("Trying to click Add P2P Transfer Rules link");
		AddP2PTransferRule.click();
		Log.info("Add P2P Transfer Rules link clicked successfully");
	}
	
	public void clickC2CInitiateTR() {
		Log.info("Trying to Click Initiate C2C Transfer Rule link");
		WebDriverWait wait= new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(InitiateC2CTransferRule));
		InitiateC2CTransferRule.click();
		Log.info("Initiate C2C Transfer Rule link Clicked Successfully");
	}
	
	public void clickC2CTransferRuleApproval() {
		Log.info("Trying to click C2C Transfer Rule Approval link");
		ApproveC2CTransferRule.click();
		Log.info("C2C Transfer Rule Approval link clicked successfully");
	}
	
	public boolean checkIfAssociateC2CTransferRuleLinkExists () {
		
		try {
			Log.info("Trying to check if Associate C2C Transfer Rule link exists");
			AssociateC2CLinkStatus = AssociateC2CTransferRule.isDisplayed();
			if(AssociateC2CLinkStatus==true){
			Log.info("Associate C2C Transfer Rule link found");}
		}
		catch (NoSuchElementException e) {
			Log.info("Associate C2C Transfer Rule link not found");
			//AssociateC2CLinkStatus = false;
		}
		return AssociateC2CLinkStatus;
	}
	
	public void clickAssociateC2CTransferRule() {
		if (AssociateC2CLinkStatus == true) {
			Log.info("Trying to click Associate C2C Transfer Rule link");
			WebDriverWait wait=new WebDriverWait(driver,10);
			wait.until(ExpectedConditions.visibilityOf(AssociateC2CTransferRule));
			AssociateC2CTransferRule.click();
			Log.info("Associate C2C Transfer Rule link clicked successfully");
		}
	}
	public void clickC2CTransferRuleModification() {
		Log.info("Trying to click C2C Transfer Rule Modify link");
		ModifyC2CTransferRule.click();
		Log.info("C2C Transfer Rule Modification link clicked successfully");
	}
	
	public void clickC2CTransferRuleModificationOrderBy() {
		Log.info("Trying to click C2C Transfer Rule Modify link");
		ModifyC2CTransferRuleOrderBy.click();
		Log.info("C2C Transfer Rule Modification link clicked successfully");
	}
	
	public void clickP2PTransferRuleModification() {
		Log.info("Trying to click P2P Transfer Rule Modify link");
		ModifyP2PTransferRule.click();
		Log.info("P2P Transfer Rule Modification link clicked successfully");
	}
	
	public void clickViewP2PTransferRule() {
		Log.info("Trying to click P2P Transfer Rule View link");
		viewP2PTransferRule.click();
		Log.info("C2C Transfer Rule View link clicked successfully");
	}
	
	public void clickViewC2STransferRule() {
		Log.info("Trying to click C2S Transfer Rule View link");
		viewC2STransferRule.click();
		Log.info("C2S Transfer Rule View link clicked successfully");
	}
}
