package com.pageobjects.channeladminpages.adddetails;



import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.CommonUtils;
import com.utils.Log;

public class AdditionalCommDetailSpring {
	
	
	@FindBy(xpath="//a[@href='#collapseOne']")
    public WebElement byMobileNo;
    
    @FindBy(xpath="//a[@href='#collapseTwo']")
    public WebElement byCategory;
    
	@FindBy(xpath="//a[@href [contains(.,'pageCode=RPTVACP001A')]]")
	private WebElement addCommDetLink;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=RPTVACP001')]]")
	private WebElement addCommDetLinkSpring;
	
	@FindBy(id="currentDate1")
	private WebElement currentDate1;
	
	@FindBy(id="fromTime")
	private WebElement fromTime;
	
	@FindBy(id="toTime")
	private WebElement toTime;
	
	@FindBy(id="fromTime1")
	private WebElement fromTimeCat;
	
	@FindBy(id="toTime1")
	private WebElement toTimeCat;
	
	@FindBy(id="msisdn")
	private WebElement msisdn;
	
	
	
	@FindBy(id="currentDateRptChkBox1")
	private WebElement currentDateRptChkBox1;
	
	@FindBy(id="submitMsisdn")
	private WebElement submit;
	
	@FindBy(id="currentDate")
	private WebElement currentDate;
	
	@FindBy(id="zoneList")
	private WebElement zoneList;
	
	@FindBy(id="domainList")
	private WebElement domainList;
	
	@FindBy(id="parentCategoryList")
	private WebElement parentCategoryList;
	
	@FindBy(id="user")
	private WebElement user;
	
	@FindBy(id="currentDateRptChkBox")
	private WebElement currentDateRptChkBox;
	
	@FindBy(id="submitUser")
	private WebElement submitUser;
	
	
	
	@FindBy(xpath="//input[@id='currentDate1']/following-sibling::label")
	private WebElement currentDate1Msg;
	
	@FindBy(xpath="//input[@id='currentDate']/following-sibling::label")
	private WebElement currentDateMsg;
	
	@FindBy(xpath="//input[@id='fromTime']/following-sibling::label")
	private WebElement fromTimeMsg;
	
	@FindBy(xpath="//input[@id='toTime']/following-sibling::label")
	private WebElement toTimeMsg;
	
	@FindBy(xpath="//input[@id='msisdn']/following-sibling::label")
	private WebElement msisdnMsg;
	
	@FindBy(xpath="//input[@id='fromTime1']/following-sibling::label")
	private WebElement fromTimecatMsg;
	
	@FindBy(xpath="//input[@id='toTime1']/following-sibling::label")
	private WebElement toTimecatMsg;
	
	@FindBy(xpath="//input[@id='parentCategoryList']/following-sibling::label")
	private WebElement categoryMsg;
	
	@FindBy(xpath="//input[@id='user']/following-sibling::label")
	private WebElement userMsg;
	@FindBy(xpath="//*[@id='submitMsisdn']")
	private WebElement submitbtnenabled;
	
	@FindBy(xpath="//*[@id='submitUser']")
	private WebElement submituserbtnenabled;
	
	WebDriver driver=null;
	public AdditionalCommDetailSpring(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectCurrentDate1(String cDate){
		Log.info("Trying to select current date: "+cDate);
		CommonUtils.selectDateInSpring(currentDate1,cDate,driver);
		Log.info("Current Date selected successfully.");
	}
	
	public void selectfromTime(String fTime){
		Log.info("Trying to select from time: "+fTime);
		fromTime.clear();
		fromTime.sendKeys(fTime);
		Log.info("from time selected successfully.");
	}
	
	public void selecttoTime(String tTime){
		Log.info("Trying to select toTime: "+tTime);
		toTime.clear();
		toTime.sendKeys(tTime);
		Log.info("toTime selected successfully.");
	}
	
	public void selectmsisdn(String msisd){
		Log.info("Trying to select from time: "+msisd);
		msisdn.clear();
		msisdn.sendKeys(msisd);
		Log.info("from time selected successfully.");
	}
	
	public void selectcheckbox1(String chk1){
		Log.info("Trying to select from time: "+chk1);
		Select select = new Select(currentDateRptChkBox1);
		select.selectByVisibleText(chk1);
		Log.info("from time selected successfully.");
	}
	
	public void clicksubmitBtn(){
		Log.info("Trying to click submit button.");
		submit.click();
		Log.info("Submit button clicked successfuly.");
	}
	public void selectCurrentDate(String cuDate){
		Log.info("Trying to select current date: "+cuDate);
		CommonUtils.selectDateInSpring(currentDate,cuDate,driver);
		Log.info("Current Date selected successfully.");
	}
	
	
	public void selectfromTimecat(String frTime){
		Log.info("Trying to select from time: "+frTime);
		fromTimeCat.clear();
		fromTimeCat.sendKeys(frTime);
		Log.info("from time selected successfully.");
	}
	
	public void selecttoTimecat(String t2Time){
		Log.info("Trying to select toTime: "+t2Time);
		toTimeCat.clear();
		toTimeCat.sendKeys(t2Time);
		Log.info("toTime selected successfully.");
	}
	
	
	public void selectZone(String zone){
		Log.info("Trying to select zone: "+zone);
		try{
		Select select = new Select(zoneList);
		select.selectByVisibleText(zone);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Zone selected successfully.");
	}
	
	public void selectDomain(String domain){
		Log.info("Trying to select domain: "+domain);
		try{
		Select select = new Select(domainList);
		select.selectByVisibleText(domain);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Domain selected successfully.");
	}
	
	public void selectCategory(String category){
		Log.info("Trying to select category: "+category);
		try{
		Select select = new Select(parentCategoryList);
		select.selectByVisibleText(category);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Category selected successfully.");
	}
	
	
	
	public void selectUser(String user1){
		Log.info("Trying to select user: "+user1);
		try{
			user.clear();
			user.sendKeys(user1);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Category selected successfully.");
	}
	
	public void selectcheckbox2(String chk2){
		Log.info("Trying to select from time: "+chk2);
		Select select = new Select(currentDateRptChkBox);
		select.selectByVisibleText(chk2);
		Log.info("from time selected successfully.");
	}
	
	public void clicksubmituserBtn(){
		Log.info("Trying to click submit button.");
		submitUser.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	
	public String fetcherrormessage(String attribute){
		String errormessage = null;
		Log.info("Trying to get error message from screen for: "+attribute );
		if(attribute.equalsIgnoreCase("fromTime")){
			errormessage=fromTimeMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("toTime")){
			errormessage=toTimeMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("currentDate1")){
			errormessage=currentDate1Msg.getText();
		}
		else if(attribute.equalsIgnoreCase("currentDate")){
			errormessage=currentDateMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("msisdn")){
			errormessage=msisdnMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("fromTimecat")){
			errormessage=fromTimecatMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("toTimecat")){
			errormessage=toTimecatMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("parentCategoryList")){
			errormessage=categoryMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("user")){
			errormessage=userMsg.getText();
		}
	
else {Log.info("Issue with attribute ["+attribute+"] passed in method.");}
		
		Log.info("Message successfuly fetched for : "+attribute);
		return errormessage;
	}
	
	
	
	
	
	
	
	
	
	
	
	public void clickaddCommDetailLinkStruts() {
		Log.info("Trying to click Additional commission details link");
		addCommDetLink.click();
		Log.info("Additional commission details link clicked successfully");
	}
	
	
	public void clickaddCommDetailLink() {
		Log.info("Trying to click Additional commission details link");
		addCommDetLinkSpring.click();
		Log.info("Additional commission details link clicked successfully");
	}
	
	public boolean choosePanelMsisdn() {
		Log.info("Trying to select  Mobile No.Panel");
		byMobileNo.click();
		Log.info("User Selected  Mobile No");
		return true;
	}
	
	public void choosePanelCategory() {
		Log.info("Trying to select Category Panel");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Log.info("Exception:"+e);
		}
		byCategory.click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Log.info("Exception:"+e);
		}
		Log.info("User Selected By Category No");
	}
	
	
	public boolean submitBtnenabled(){
		boolean enabled=false;
		try{if(submitbtnenabled.isEnabled()){
			enabled=true;
			Log.info("Submit button is enabled.");
		}}catch(Exception e){enabled = false;
		Log.info("Submit button is not enabled.");}
		return enabled;
	}
	
	public boolean submituserBtnenabled(){
		boolean enabled=false;
		try{if(submituserbtnenabled.isEnabled()){
			enabled=true;
			Log.info("Submit button is enabled.");
		}}catch(Exception e){enabled = false;
		Log.info("Submit button is not enabled.");}
		return enabled;
	}
	
	
	
	
	
	
	
}
