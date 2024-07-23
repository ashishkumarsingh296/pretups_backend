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
import com.utils.SwitchWindow;

/**
 * @author lokesh.kontey
 *
 */
public class DeleteChannelUserPage1 {

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
	
	@FindBy(name="submitDelete")
	private WebElement submit;
	
	@FindBy(name="saveDelete")
	private WebElement deleteBtn;
	
	@FindBy(name="submitParent")
	private WebElement submitParent;
	
	@FindBy(name="searchTextArrayIndexed[0]")
	private WebElement channelName;
	
	@FindBy(xpath="//ul/li")
	private WebElement webMessage;
	
	WebDriver driver=null;
	
	public DeleteChannelUserPage1(WebDriver driver){
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
	
	public void clickDeleteBtn(){
		Log.info("Trying to click delete button.");
		deleteBtn.click();
		Log.info("Delete button clicked successfully.");
	}
	

	public void selectChannelUserName(String channeluserName) throws InterruptedException {
			Log.info("Trying to select channel user Name");
			channelName.sendKeys(channeluserName);
			Log.info("Channel user Name selected successfully");
			SwitchWindow.switchwindow(driver);
			SwitchWindow.backwindow(driver);
	}
	
	public void clickSubmitParentBtn(){
		Log.info("Trying to click submit button.");
		submitParent.click();
		Log.info("Submit button clicked successfully.");
	}
	
	public String getMessage(){
		Log.info("Trying to fetch message from WEB.");
		String fetchMessage = webMessage.getText();
		Log.info("WEB message fetched as: "+fetchMessage);
		return fetchMessage;
	}
	
}
