package com.pageobjects.networkadminpages.c2ctransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;
import com.utils._masterVO;

public class InitiateC2CTransferRulePage1{
	@FindBy(name = "domainCode")
	public WebElement domain;
	
	@FindBy(name = "transferType")
	public WebElement ruleType;
	
	@FindBy(name = "submitButton")
	public WebElement submit;
	
	WebDriver driver= null;
	public InitiateC2CTransferRulePage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void selectDomainName(String domainname) {
		try{Log.info("Trying to Select Domain Name");
		Select select1 = new Select(domain);
		select1.selectByVisibleText(domainname);
		Log.info("Domain Name selected successfully");}
		catch(Exception e){
			Log.info("Damain element not found ");
			Log.writeStackTrace(e);
		}
	}
	
	public void selectRuleType(String RuleType){
		
		if (RuleType.contains(_masterVO.getProperty("C2CTransferCode"))) {
			Log.info("Trying to Select Rule Type");
			Select select1 = new Select(ruleType);
			select1.selectByValue("TRWD");
			WebElement sb = select1.getFirstSelectedOption();
			String rule= sb.getText();
			Log.info("Rule Type selected successfully:: "+rule);
		} else if (RuleType.equals(_masterVO.getProperty("C2CReturnCode"))) {
			Log.info("Trying to Select Rule Type");
			Select select1 = new Select(ruleType);
			select1.selectByValue("RET");
			WebElement sb = select1.getFirstSelectedOption();
			String rule= sb.getText();
			Log.info("Rule Type selected successfully:: "+rule);
		} else {
			Log.info("Rule type not required.");
		}
		
	}
	
	public void clickSubmit() {
		Log.info("Trying to click Submit Button");
		submit.click();
		Log.info("Sumit Button clicked successfully");
	}
	
}
