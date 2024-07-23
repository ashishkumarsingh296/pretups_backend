package com.pageobjects.channeluserpages.associateProfile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AssociateProfile2 {

	@FindBy(name = "userGradeId")
	private WebElement userGradeId;
	
	@FindBy(name = "commissionProfileSetId")
	private WebElement commissionProfileSetId;
	
	@FindBy(name = "saveAssociate")
	private WebElement saveAssociate;
	
	@FindBy(name = "confirmAssociate")
	private WebElement confirmAssociate;
	
	WebDriver driver = null;

	public AssociateProfile2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectGrade(String grade){
		Log.info("Trying to Select Grade");
		Select selectGradeList = new Select(userGradeId);
		selectGradeList.selectByVisibleText(grade);
		Log.info("Selected Grade: "+grade);
	}
	
	public void selectCommissionProfile(String commProfile){
		Log.info("Trying to Select Commission Profile");
		Select selectCommissionProfileList = new Select(commissionProfileSetId);
		selectCommissionProfileList.selectByVisibleText(commProfile);
		Log.info("Selected Commission Profile: "+commProfile);
	}
	
	
	
	public void clickSubmitButton(){
		Log.info("Trying to click Submit button for Associate");
		saveAssociate.click();
		Log.info("Clicked Submit button for Associate");
	}
	
	public void clickConfirmButton(){
		Log.info("Trying to click Submit button for Associate");
		confirmAssociate.click();
		Log.info("Clicked Submit button for Associate");
	}
}
