package com.pageobjects.networkadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ReconciliationSubCategories {
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=C2SRECON01')]]")
	private WebElement C2SReconciliation;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=P2PRECON01')]]")
	private WebElement P2PReconciliation;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=O2CREC101')]]")
	private WebElement O2CReconciliation;
	
	WebDriver driver= null;

	public ReconciliationSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickC2SReconciliation() {
		C2SReconciliation.click();
		Log.info("User clicked C2S Reconciliation.");
	}
	
	public void clickP2PReconciliation() {
		P2PReconciliation.click();
		Log.info("User clicked P2P Reconciliation.");
	}
	
	public void clickO2CReconciliation() {
		O2CReconciliation.click();
		Log.info("User clicked O2C Reconciliation.");
	}

}
