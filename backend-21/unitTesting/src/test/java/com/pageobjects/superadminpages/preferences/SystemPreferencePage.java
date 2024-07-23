/**
 * 
 */
package com.pageobjects.superadminpages.preferences;

import org.openqa.selenium.By;
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
public class SystemPreferencePage {

	@FindBy(name = "module")
	private WebElement modulename;
	
	@FindBy(name="preferenceType")
	private WebElement preferenceType;
	
	@FindBy(name="submitButton")
	private WebElement submitBtn;
	
	@FindBy(name="btnSubmit")
	private WebElement modifyBtn;
	
	@FindBy(name="btnCnf")
	private WebElement confirmBtn;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=PRF013')]]")
	private WebElement systemPreference;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	WebDriver driver = null;
	
	public SystemPreferencePage(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectModule(String modulevalue){
		Log.info("Trying to select module.");
		Select mValue = new Select(modulename);
		mValue.selectByValue(modulevalue);
		Log.info("Module selected as: "+modulevalue);
		}
	
	public void selectSystemPreference(){
		Log.info("Trying to select preference.");
		Select pValue = new Select(preferenceType);
		pValue.selectByValue("SYSTEMPRF");
		Log.info("Preference selected as: SYSTEMPRF");
	}
	
	public void clickSubmitButton(){
		Log.info("Trying to click submit button.");
		submitBtn.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void setValueofSystemPreference(String preferenceCode, String valuetoSet){
		Log.info("Trying to select modify btn to modify system preference: "+preferenceCode);
		driver.findElement(By.xpath("//td[text()='"+preferenceCode+"']/preceding-sibling::td/input[@value='M']")).click();
		Log.info("Modify radio button selected successfully");
		Log.info("Trying to set preference value as : "+valuetoSet);
		WebElement value = driver.findElement(By.xpath("//td[text()='"+preferenceCode+"']/following-sibling::td/input[@type='text']"));
		value.clear();
		value.sendKeys(valuetoSet);
		Log.info("Preference value set successfully as:"+ valuetoSet);
	}
	
	public void clickModifyBtn(){
		Log.info("Trying to click modify button.");
		modifyBtn.click();
		Log.info("Modify button clicked successfuly.");
	}
	
	public void clickConfirmBtn(){
		Log.info("Trying to click confirm button.");
		confirmBtn.click();
		Log.info("Confirm button clicked successfuly.");		
	}
	
	public void selectServicePreference(){
		Log.info("Trying to select preference.");
		Select pValue = new Select(preferenceType);
		pValue.selectByValue("SVCCLSPRF");
		Log.info("Preference selected as: SVCCLSPRF");
	}
	
	public void clickSystemPrefernce(){
		Log.info("Trying to click System Preference link.");
		systemPreference.click();
		Log.info("System Preference link clicked successfuly.");
	}
	
	public String getMessage(){
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
		Message = message.getText();
		Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully as:"+ Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
	
	public void selectPreferenceType(String type){
		Log.info("Trying to select preference.");
		Select pValue = new Select(preferenceType);
		pValue.selectByValue(type);
		Log.info("Preference selected as:" + type);
	}
	
	public boolean isPreferenceDisplayed(String preference)
	{
		Log.info("Trying to check is preference displayed.");
		boolean isDisplayed = driver.findElement(By.xpath("//td[@class='tabcol' and (text() ='"+preference+"')]")).isDisplayed();
		if(isDisplayed)
			return true;
		else
			return false;
	}
	
	public boolean isModifiedAllowed(String preference)
	{
		Log.info("Trying to check is preference allowed to modify.");
		boolean isModifiedAllowed = driver.findElement(By.xpath("//td[@class='tabcol' and (text() ='"+preference+"')]/../td/input[@type='radio'][@value='M']")).isDisplayed();
		if(isModifiedAllowed)
			return true;
		else
			return false;
	}

	public void selectCategoryPreference(){
		Log.info("Trying to select Category preference.");
		Select pValue = new Select(preferenceType);
		pValue.selectByValue("CATPRF");
		Log.info("Preference selected as: CATPRF");
	}
}
