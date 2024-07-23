package com.pageobjects.channeladminpages.autoC2Ccreditlimit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AutoC2Cpage {

	@FindBy(xpath="//a[@href[contains(.,'pageCode=ATOC2CM001')]]")
	private WebElement autoc2ccreditlimitlink;
	
	@FindBy(xpath="//input[@name='associationMode' and @value='S']")
	private WebElement associationmodesingle;
	
	@FindBy(name="submit1")
	private WebElement submitBtn1;
	
	@FindBy(name="msisdn")
	private WebElement msisdn;
	
	@FindBy(name="submit1")
	private WebElement addmodifyBtn1;
	
	@FindBy(xpath="//input[@name='autoc2callowed' and @value='Y']")
	private WebElement autoc2callowed_Y;
	
	@FindBy(xpath="//input[@name='autoc2callowed' and @value='N']")
	private WebElement autoc2callowed_N;
	
	@FindBy(name="maxTxnAmount")
	private WebElement amount;
	
	@FindBy(name="dailyCount")
	private WebElement dailycount;
	
	@FindBy(name="weeklyCount")
	private WebElement weeklycount;
	
	@FindBy(name="monthlyCount")
	private WebElement monthlycount;
	
	@FindBy(name="submit")
	private WebElement addmodifyBtn2;
	
	@FindBy(name="submit1")
	private WebElement confirmBtn;
	
	WebDriver driver=null;
	
	public AutoC2Cpage(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}

	public void clickAutoC2Clink(){
		Log.info("Trying to click 'Auto C2C Credit Limit' link");
		autoc2ccreditlimitlink.click();
		Log.info("'Auto C2C Credit Limit' link clicked successfully.");		
	}
	
	public void selectAssociationmode(){
		Log.info("Trying to select association mode.");
		associationmodesingle.click();
		Log.info("Single association mode selected.");
	}
	
	public void clicksubmit(){
		Log.info("Trying to click submit button.");
		submitBtn1.click();
		Log.info("Submit button clicked successfully.");
	}
	
	public void enterMSISDN(String msisdn){
		Log.info("Trying to enter MSISDN.");
		this.msisdn.sendKeys(msisdn);
		Log.info("MSISDN entered successfully as: "+msisdn);
	}
	
	public void clickaddmodifybtn1(){
		Log.info("Trying to click 'add/modify' button.");
		addmodifyBtn1.click();
		Log.info("Button clicked successfully.");
	}
	
	public void selectAutoC2CallowedY(){
		Log.info("Trying to select autoC2C allowed: Yes");
		autoc2callowed_Y.click();
		Log.info("'Yes' selected successfully");
	}
	
	public void enterMaxTxnAmt(String amount){
		Log.info("Trying to enter amount: "+amount);
		this.amount.clear();
		this.amount.sendKeys(amount);
		Log.info("Amount entered successfully.");
	}
	
	public void clickaddmodifybtn2(){
		Log.info("Trying to click 'add/modify' button.");
		addmodifyBtn2.click();
		Log.info("Button clicked successfully.");
	}
	
	public void clickconfirm(){
		Log.info("Trying to click confirm button.");
		confirmBtn.click();
		Log.info("Confirm button clicked successfully.");
	}
	
	public void enterDailyCount(String count){
		Log.info("Trying to enter dailycount : "+count);
		this.dailycount.clear();
		this.dailycount.sendKeys(count);
		Log.info("Daily count entered successfully.");
	}
	
	public void enterWeeklyCount(String count){
		Log.info("Trying to enter weeklycount : "+count);
		this.weeklycount.clear();
		this.weeklycount.sendKeys(count);
		Log.info("Weekly count entered successfully.");
	}
	
	public void enterMonthlyCount(String count){
		Log.info("Trying to enter monthlycount : "+count);
		this.monthlycount.clear();
		this.monthlycount.sendKeys(count);
		Log.info("Monthly count entered successfully.");
	}
}