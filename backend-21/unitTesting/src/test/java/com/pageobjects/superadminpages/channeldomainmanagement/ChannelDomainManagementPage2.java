/**
 * 
 */
package com.pageobjects.superadminpages.channeldomainmanagement;

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
public class ChannelDomainManagementPage2 {
	
	@FindBy(name="categoryCode")
	private WebElement categoryCode;
	
	@FindBy(name="categoryName")
	private WebElement categoryName;

	@FindBy(name="grphDomainType")
	private WebElement geographicalDomain;
	
	@FindBy(name="fixedRoles")
	private WebElement roleType;

	@FindBy(name="userIdPrefix")
	private WebElement userIDPrefix;

	@FindBy(name="maxTxnMsisdn")
	private WebElement maxTxnMsisdn;

	@FindBy(name="confirm1")
	private WebElement submitBtn;
	
	@FindBy(name="confirm")
	private WebElement confirmBtn;
	
		WebDriver driver = null;

		public ChannelDomainManagementPage2(WebDriver driver) {
			this.driver = driver;
			PageFactory.initElements(driver, this);
		}

	public void enterChannelCategoryCode(String cCode){
		Log.info("Trying to enter channel category code.");
		categoryCode.sendKeys(cCode);
		Log.info("Category code entered as :: "+cCode);
	}
	
	public void enterChannelCategoryName(String cName){
		Log.info("Trying to enter channel category name.");
		categoryName.sendKeys(cName);
		Log.info("Category name entered as :: "+cName);
	}
	
	public void selectGeographicalDomain(){
	try{
		Log.info("Trying to select geographical domain.");
		Select geographyDomain= new Select(geographicalDomain);
		geographyDomain.selectByIndex(1);
		Log.info("Geographical domain selected.");
	}catch(Exception e){
	Log.writeStackTrace(e);	
	}
	}
	
	public void selectRoleType(){
	try{
		Log.info("Trying to select role type.");
		Select rType= new Select(roleType);
		rType.selectByIndex(1);
		Log.info("Role type is selected.");
	}catch(Exception e){
	Log.writeStackTrace(e);	
	}
	}
	
	public void enterUserIDPrefix(String uIDPrefix){
		Log.info("Trying to enter user ID Prefix.");
		userIDPrefix.sendKeys(uIDPrefix);
		Log.info("Category name entered as :: "+uIDPrefix);
	}
	
	public void enterMaxTxnMsisdn(){
		Log.info("Trying to enter max transaction msisdn.");
		maxTxnMsisdn.sendKeys("2");
		Log.info("Max transaction msisdn entered successfuly.");
	}
	
	public void clickSubmitButton(){
		Log.info("Trying to click submit button.");
		submitBtn.click();
		Log.info("Submit button clicked auccessfuly.");
	}
	
	public void clickConfirmButton(){
		Log.info("Trying to click submit button.");
		confirmBtn.click();
		Log.info("Submit button clicked auccessfuly.");
	}
	
	public void selectSourceGateway(){
		
		
		Log.info("Trying to select Web Gateway Checkbox" );

		WebElement chkbox= driver.findElement(By.xpath("//input[@type='checkbox' and @value='WEB']"));

		if(!chkbox.isSelected()){
			chkbox.click();
			Log.info("Web Gateway Checkbox checked");
		}
		else{
			Log.info("Web Gateway Checkbox is already selected");
		}
	}
}
