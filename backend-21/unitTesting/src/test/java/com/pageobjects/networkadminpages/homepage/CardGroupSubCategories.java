package com.pageobjects.networkadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CardGroupSubCategories	{
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=C2SCR0001')]]")
	private WebElement addC2SCardGroup;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMSCR0001')]]")
	private WebElement addVoucherCardGroup;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CR0001')]]")
	private WebElement addP2PCardGroup;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=SELC2SCR01')]]")
	private WebElement modifyC2SCardGroup;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=C2SCRTR001')]]")
	private WebElement calculateC2STransferRule;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=C2SSCR0001')]]")
	private WebElement C2SCardGroupStatus;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=SCR0001')]]")
	private WebElement P2PCardGroupStatus;
	
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMSSCR0001')]]")
	private WebElement VoucherCardGroupStatus;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=SELCR001')]]")
	private WebElement modifyP2PCardGroup;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VSELCR001')]]")
	private WebElement modifyVoucherCardGroup;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VC2SCR0001')]]")
	private WebElement viewC2SCardGroup;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VCR0001')]]")
	private WebElement viewP2PCardGroup;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMSVCR0001')]]")
	private WebElement viewVoucherCardGroup;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=BCGSMOD001')]]")
	private WebElement batchModifyC2SCardGroup;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=SELC2SDF01')]]")
	private WebElement defaultC2SCardGroup;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=SELP2PDF01')]]")
	private WebElement defaultP2PCardGroup;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=VMSSELDF01')]]")
	private WebElement defaultVoucherCardGroup;

	@FindBy(xpath = "//a[@href[contains(.,'pageCode=VMSCRTR001')]]")
	private WebElement calculateVoucherCardGroup;
	
	WebDriver driver= null;

	public CardGroupSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	
	public void clickAddC2SCardGroup() {
		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(addC2SCardGroup));
		addC2SCardGroup.click();
		Log.info("User clicked Add C2S Card Group.");
	}
	
	public void clickAddP2PCardGroup() {
		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(addP2PCardGroup));
		addP2PCardGroup.click();
		Log.info("User clicked Add P2P Card Group.");
	}
	public void clickAddP2PVoucherCardGroup() {
		addVoucherCardGroup.click();
		Log.info("User clicked Add P2P Voucher Card Group.");
	}
	
	public void clickModifyC2SCardGroup() {
		modifyC2SCardGroup.click();
		Log.info("User clicked Modify C2S Card Group.");
	}
	
	public void clickModifyP2PCardGroup() {
		modifyP2PCardGroup.click();
		Log.info("User clicked Modify P2P Card Group.");
	}
	
	public void clickModifyVoucherCardGroup() {
		modifyVoucherCardGroup.click();
		Log.info("User clicked Modify Voucher Card Group.");
	}
	
	public void clickCalculateC2STransferRule() {
		calculateC2STransferRule.click();
		Log.info("User clicked Calculate C2S Transfer Rule.");
	}
	public void clickC2SCardGroupStatus() {
		C2SCardGroupStatus.click();
		Log.info("User clicked C2S Card Group Status.");
	}
	public void clickViewC2SCardGroup() {
		viewC2SCardGroup.click();
		Log.info("User clicked View C2S Card Group.");
	}
	public void clickBatchModifyC2SCardGroup() {
		batchModifyC2SCardGroup.click();
		Log.info("User clicked Batch Modify C2S Card Group.");
	}
	public void clickDefaultC2SCardGroup() {
		defaultC2SCardGroup.click();
		Log.info("User clicked Default C2S Card Group.");
	}
	
	
	public void clickDefaultP2PCardGroup() {
		defaultP2PCardGroup.click();
		Log.info("User clicked Default P2P Card Group.");
	}
	
	public void clickDefaultVoucherCardGroup() {
		defaultVoucherCardGroup.click();
		Log.info("User clicked Default Voucher Card Group.");
	}
	
	
	public void clickViewP2PCardGroup() {
		viewP2PCardGroup.click();
		Log.info("User clicked View P2P Card Group.");
	}
	
	public void clickViewVoucherCardGroup() {
		viewVoucherCardGroup.click();
		Log.info("User clicked View Voucher Card Group.");
	}
	
	public void clickP2PCardGroupStatus() {
		P2PCardGroupStatus.click();
		Log.info("User clicked P2P Card Group Status.");
	}
		
	public void clickVoucherCardGroupStatus() {
		VoucherCardGroupStatus.click();
		Log.info("User clicked P2P Card Group Status.");
	}
	public void clickCalculateVoucherCardGroup() {
		calculateVoucherCardGroup.click();
		Log.info("User clicked Add Calculate Card Group.");
	}
	
	
}
