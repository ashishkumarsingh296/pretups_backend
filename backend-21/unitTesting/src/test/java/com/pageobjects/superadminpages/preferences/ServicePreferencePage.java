/**
 * 
 */
package com.pageobjects.superadminpages.preferences;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class ServicePreferencePage {

	@FindBy(name="btnSubmit")
	private WebElement modifyBtn;
	
	@FindBy(name="btnCnf")
	private WebElement confirmBtn;
	
	WebDriver driver = null;
	
	public ServicePreferencePage(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void setValueofServicePreference(String preferenceCode, String valuetoSet){
		Log.info("Trying to select modify btn to modify service preference: "+preferenceCode);
		driver.findElement(By.xpath("//td[text()='"+preferenceCode+"']/preceding-sibling::td/input[@value='M']")).click();
		Log.info("Modify radio button selected successfully");
		Log.info("Trying to set service preference value as : "+valuetoSet);
		WebElement value = driver.findElement(By.xpath("//td[text()='"+preferenceCode+"']/following-sibling::td/input[@type='text']"));
		value.clear();
		value.sendKeys(valuetoSet);
		Log.info("Service Preference value set successfuly.");
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
	
}
