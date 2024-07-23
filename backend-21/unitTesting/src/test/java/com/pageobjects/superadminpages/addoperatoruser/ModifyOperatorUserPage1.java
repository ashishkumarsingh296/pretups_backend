/**
 * 
 */
package com.pageobjects.superadminpages.addoperatoruser;

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
public class ModifyOperatorUserPage1 {

	@FindBy(name="categoryCode")
	private WebElement category;
	
	@FindBy(name="userName")
	private WebElement userName;
	
	@FindBy(name="edit")
	private WebElement submitBtn;
	
	@FindBy(xpath = "//ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
	WebElement errorMessage;
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = errorMessage.getText();
		}catch(Exception e){
			Log.info("No error Message found: "+e);
		}
		try{
		UIMsg = UIMessage.getText();
		}catch(Exception e){
			Log.info("No Success Message found: "+e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}
	
	WebDriver driver = null;
	
	public ModifyOperatorUserPage1(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectCategory(String Category) {
		Log.info("Trying to select Category");
		Select select = new Select(category);
		select.selectByVisibleText(Category);
		Log.info("Category selected successfully");
	}
	
	public void enterOperatorName(String name){
		Log.info("Trying to enter Operator user name: "+name);
		userName.sendKeys(name);
		Log.info("Operator user name entered successfully.");
	}
	
	public void clickSubmitButton(){
		Log.info("Trying to click submit button.");
		submitBtn.click();
		Log.info("Submit button clicked successfully.");
	}
	
}
