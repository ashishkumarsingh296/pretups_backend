package com.pageobjects.networkadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author Ayush Abhijeet
 * This class Contains the Page Objects for Sub categories of Masters
 **/

public class MastersSubCategories {
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=GRDOM001')]]")
	private WebElement GeographicalDomainManagement;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=IF001')]]")
	private WebElement InterfaceManagement;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=NTWSER001')]]")
	private WebElement NetworkServices;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=NP001')]]")
	private WebElement NetworkPrefix;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=NS001')]]")
	private WebElement NetworkStatus;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=GCELL001')]]")
	private WebElement GeographyCellIdManagement;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=INTNTMAP01')]]")
	private WebElement NetworkInterfaces;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=SRVCPRE01')]]")
	private WebElement ServicePrefixMapping;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CACHE001')]]")
	private WebElement UpdateCache;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=UPTXN1')]]")
	private WebElement UpdateSIMTXNID;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=SLAB001')]]")
	private WebElement SlabManagement;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=NTVASMP001')]]")
	private WebElement NetworkVASmapping;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=UPBKTXID01')]]")
	private WebElement BulkUploadTXNID;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=SSM001')]]")
	private WebElement ServiceTypeSelectorMapping;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=PCUSRNOMGD')]]")
	private WebElement UserWalletConfiguration;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=INTNTPRE01')]]")
	private WebElement InterfacePrefix;
	

	WebDriver driver= null;

	public MastersSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickGeographicalDomainManagement() {
		GeographicalDomainManagement.click();
		Log.info("User clicked Geographical Domain Management.");
	}
	public void clickInterfaceManagement() {
		InterfaceManagement.click();
		Log.info("User clicked Interface Management.");
	}
	
	public void clickNetworkServices() {
		NetworkServices.click();
		Log.info("User clicked Network Services.");
	}
	
	public void clickNetworkPrefix() {
		NetworkPrefix.click();
		Log.info("User clicked Network Prefix.");
	}
	
	public void clickNetworkStatus() {
		NetworkStatus.click();
		Log.info("User clicked Network Status.");
	}
	public void clickGeographyCellIdManagement() {
		GeographyCellIdManagement.click();
		Log.info("User clicked Interface Management.");
	}
	
	public void clickNetworkInterfaces() {
		NetworkInterfaces.click();
		Log.info("User clicked Network Interfaces.");
	}
	
	public void clickServicePrefixMapping() {
		ServicePrefixMapping.click();
		Log.info("User clicked Service Prefix Mapping.");
	}
	
	public void clickUpdateCache() {
		UpdateCache.click();
		Log.info("User clicked UpdateCache.");
	}
	
	public void clickUpdateSIMTXNID() {
		UpdateSIMTXNID.click();
		Log.info("User clicked Update SIM TXN ID.");
	}
	
	
	public void clickSlabManagement() {
		SlabManagement.click();
		Log.info("User clicked Slab Management.");
	}
	public void clickNetworkVASmapping() {
		NetworkVASmapping.click();
		Log.info("User clicked Network VAS mapping.");
	}
	
	public void clickBulkUploadTXNID() {
		BulkUploadTXNID.click();
		Log.info("User clicked Bulk Upload TXN ID.");
	}
	
	public void clickServiceTypeSelectorMapping() {
		ServiceTypeSelectorMapping.click();
		Log.info("User clicked Service Type Selector Mapping.");
	}
	
	public void clickUserWalletConfiguration() {
		UserWalletConfiguration.click();
		Log.info("User clicked User Wallet Configuration.");
	}
	
	public void clickinterfacePrefix() {
		InterfacePrefix.click();
		Log.info("Interface Prefix clicked successfully");
	}
}
