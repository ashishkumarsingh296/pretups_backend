/**
 * 
 */
package com.pageobjects.channeladminpages.deletechanneluser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class ApprovalDeleteChannelUserPage1 {

	
	@FindBy(name="searchMsisdn")
	private WebElement msisdn;
	
	@FindBy(name="searchLoginId")
	private WebElement loginID;
	
	@FindBy(name="domainCode")
	private WebElement domainCode;
	
	@FindBy(name="channelCategoryCode")
	private WebElement categoryCode;
	
	@FindBy(name="parentDomainCode")
	private WebElement geographicalDomain;
	
	@FindBy(name="eventRemarks")
	private WebElement remarks;
	
	@FindBy(name="submitDSApproval")
	private WebElement submit;
	
	
	WebDriver driver=null;
	
	public ApprovalDeleteChannelUserPage1(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterMSISDN(String MSISDN){
		Log.info("Trying to enter MSISDN.");
		msisdn.sendKeys(MSISDN);
		Log.info("MSISDN entered successfully as : "+MSISDN);
	}
	
	public void enterLoginID(String LOGINID){
		Log.info("Trying to enter LOGINID.");
		loginID.sendKeys(LOGINID);
		Log.info("LOGINID entered successfully as : "+LOGINID);
	}
	
	public void enterRemarks(String Remarks){
		Log.info("Trying to enter Remarks.");
		remarks.sendKeys(Remarks);
		Log.info("Remarks entered successfully as : "+Remarks);
	}
	
	public void selectDomainCode(String DOMAIN){
		Log.info("Trying to select DOMAIN.");
		Select domain=new Select(domainCode);
		domain.selectByVisibleText(DOMAIN);
		Log.info("DOMAIN selected successfully : "+DOMAIN);
	}
	
	public void selectCategoryCode(String CATEGORY){
		Log.info("Trying to select CATEGORYCODE.");
		Select category=new Select(categoryCode);
		category.selectByVisibleText(CATEGORY);
		Log.info("CATEGORY selected successfully : "+CATEGORY);
	}
	
	public void selectGeoDomain(String GEODOMAIN){
		Log.info("Trying to select geographical domain.");
		Select geographicaldomain=new Select(geographicalDomain);
		geographicaldomain.selectByVisibleText(GEODOMAIN);
		Log.info("Geographical Domain selected successfully : "+GEODOMAIN);
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click submit button.");
		submit.click();
		Log.info("Submit button clicked successfully.");
	}

	
	
}
