package com.pageobjects.networkadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class PromotionalTransferRuleSubCategories {

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=PRADDTR001')]]")
	private WebElement AddPromotionalTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=PRMODTR001')]]")
	private WebElement ModifyPromotionalTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=PRVIWTR001')]]")
	private WebElement ViewPromotionalTransferRule;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=PRMOBTR001')]]")
	private WebElement AddBatchPromotionalTransferRule;
	
	WebDriver driver= null;

	public PromotionalTransferRuleSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickAddPromotionalTransferRule() {
		AddPromotionalTransferRule.click();
		Log.info("User clicked Add Promotional Transfer Rule.");
	}
	
	public void clickModifyPromotionalTransferRule() {
		ModifyPromotionalTransferRule.click();
		Log.info("User clicked Modify Promotional Transfer Rule.");
	}
	
	public void clickViewPromotionalTransferRule() {
		ViewPromotionalTransferRule.click();
		Log.info("User clicked View Promotional Transfer Rule.");
	}
	
	public void clickAddBatchPromotionalTransferRule() {
		AddBatchPromotionalTransferRule.click();
		Log.info("User clicked Add Batch Promotional Transfer Rule.");
	}

}
