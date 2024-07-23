package com.pageobjects.channeladminpages.restrictedList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class uploadConfirmPage {
	
	@FindBy(name = "cofirmBlkRegBtn")
	private WebElement confirm;
	
	@FindBy(name = "btnBack")
	private WebElement back;
	
	@FindBy(name = "btnCncl")
	private WebElement cancel;
	
	@FindBy(xpath = "//a[@href='javaScript:viewErrorLog()']")
	private WebElement  errorLoglink;
	
	@FindBy(xpath = "//a[@href = 'javascript:window.close()']")
	private WebElement closeLink;
	
	@FindBy(xpath = "//tr/td/form/table/tbody/tr[2]/td[3]")
	private WebElement failureReason;
	
	WebDriver driver = null;
	
	public uploadConfirmPage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	
	
	public void clickBack(){
		Log.info("User is trying to click back button");
		back.click();
		Log.info("User clicked back button");
	}
	
	public void clickConfirm(){
		Log.info("User is trying to click confirm button");
		confirm.click();
		Log.info("User clicked confirm button");
	}
	
	public void clickCancel(){
		Log.info("User is trying to click cancel button");
		cancel.click();
		Log.info("User clicked cancel button");
	}
	
	public void clickErrorLogLink(){
		Log.info("User tries to click the error log link");
		errorLoglink.click();
	Log.info("User clicked error log link");
	}
	
	public void clickCloseLink(){
		Log.info("User tries to click the close link");
		closeLink.click();
	Log.info("User clicked close link");
	}
	
	public String getFailureReason(){
		Log.info("user tries to fetch the Failure reason");
		String reason = failureReason.getText();
		
	return reason;
	}
	

}
