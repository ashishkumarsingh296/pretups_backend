package com.pageobjects.networkadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class P2PPromotionalTransferRuleSubCategories {

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=P2PPRAD001')]]")
	private WebElement AddP2PPromotionalTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=P2PPRMD001')]]")
	private WebElement ModifyP2PPromotionalTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=P2PPRV001')]]")
	private WebElement ViewP2PPromotionalTransferRule;
	
	WebDriver driver= null;

	public P2PPromotionalTransferRuleSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickAddP2PPromotionalTransferRule() {
		AddP2PPromotionalTransferRule.click();
		Log.info("User clicked Add Promotional Transfer Rule.");
	}
	
	public void clickModifyP2PPromotionalTransferRule() {
		ModifyP2PPromotionalTransferRule.click();
		Log.info("User clicked Modify Promotional Transfer Rule.");
	}
	
	public void clickViewP2PPromotionalTransferRule() {
		ViewP2PPromotionalTransferRule.click();
		Log.info("User clicked View Promotional Transfer Rule.");
	}
}
