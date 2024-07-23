package com.pageobjects.channeladminpages.restrictedList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class restListMngmtSubCategoriesPage {
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=BLKREG001')]]")
	private WebElement uploadList;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=RESAPPR01')]]")
	private WebElement approveList;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=DELSUB001')]]")
	private WebElement deleteList;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=BLKLST01')]]")
	private WebElement blackList;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=VEWRESU01')]]")
	private WebElement viewList;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=UNBLKLST01')]]")
	private WebElement UnBlackList;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=RPTOWN001')]]")
	private WebElement restrictedMSISDNReport;
	
	
	
	WebDriver driver = null;

	public restListMngmtSubCategoriesPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void click_UploadListLink(){
		Log.info("Trying to click Upload restricted List Link");
		uploadList.click();
		Log.info("User clicked Upload restricted List Link successfully ");
		
	}
	
	public void click_approveListLink(){
		Log.info("Trying to click approve restricted list Link");
		approveList.click();
		Log.info("User clicked approve restricted list Link successfully ");
		
	}
	
	
	public void click_DeleteRestMSISDNLink(){
		Log.info("Trying to click Delete restricted MSISDN Link");
		deleteList.click();
		Log.info("User clicked Delete restricted MSISDN Link successfully ");
		
	}
	
	public void click_blackSubsLink(){
		Log.info("Trying to click blacklist subscribers list Link");
		blackList.click();
		Log.info("User clicked blacklist subscribers Link successfully ");
		
	}
	
	public void click_viewRestListLink(){
		Log.info("Trying to click view restricted subscribers Link");
		viewList.click();
		Log.info("User clicked view restricted List Link successfully ");
		
	}
	
	public void click_unBlackListLink(){
		Log.info("Trying to click UnBlackList subscribers list Link");
		UnBlackList.click();
		Log.info("User clicked  UnBlackList subscribers Link successfully ");
		
	}
	
	
	
	public void click_restrictedMSISDNReportLink(){
		Log.info("Trying to click restrictedMSISDNReport Link");
		restrictedMSISDNReport.click();
		Log.info("User clicked  restrictedMSISDNReport Link successfully ");
		
	}
	

}
