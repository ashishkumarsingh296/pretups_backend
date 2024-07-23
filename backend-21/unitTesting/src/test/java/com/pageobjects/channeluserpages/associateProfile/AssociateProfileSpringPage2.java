package com.pageobjects.channeluserpages.associateProfile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AssociateProfileSpringPage2 {

	@FindBy(id = "userGradeList")
	private WebElement userGradeList;
	
	@FindBy(id = "commissionProfileList")
	private WebElement commissionProfileList;
	
	@FindBy(id = "trannferProfileList")
	private WebElement trannferProfileList;
	
	@FindBy(id = "trannferRuleTypeList")
	private WebElement trannferRuleTypeList;
	
	@FindBy(id = "submitAssociateProfile")
	private WebElement submitAssociateProfile;
	
	WebDriver driver = null;

	public AssociateProfileSpringPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectGrade(String grade){
		Log.info("Trying to Select Grade");
		Select selectGradeList = new Select(userGradeList);
		selectGradeList.selectByVisibleText(grade);
		Log.info("Selected Grade: "+grade);
	}
	
	public void selectCommissionProfile(String commProfile){
		Log.info("Trying to Select Commission Profile");
		Select selectCommissionProfileList = new Select(commissionProfileList);
		selectCommissionProfileList.selectByVisibleText(commProfile);
		Log.info("Selected Commission Profile: "+commProfile);
	}
	
	public void selectTransferProfile(String transferProfile){
		Log.info("Trying to Select Transfer Profile");
		Select selectTransferProfileList = new Select(trannferProfileList);
		selectTransferProfileList.selectByVisibleText(transferProfile);
		Log.info("Selected Transfer Profile: "+transferProfile);
	}
	
	public void selectTransferRule(String transferRule){
		Log.info("Trying to Select Transfer Rule");
		Select selectTransferRuleList = new Select(trannferRuleTypeList);
		selectTransferRuleList.selectByVisibleText(transferRule);
		Log.info("Selected Transfer Rule: "+transferRule);
	}
	
	public void clickSubmitButton(){
		Log.info("Trying to click Submit button for Associate");
		submitAssociateProfile.click();
		Log.info("Clicked Submit button for Associate");
	}
}
