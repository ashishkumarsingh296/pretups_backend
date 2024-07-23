package com.pageobjects.networkadminpages.preferences;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ControlPreferencePage3 {
	
	@FindBy(name="btnSubmit")
	private WebElement submit;
	
	 WebDriver driver = null;
		
	public ControlPreferencePage3(WebDriver driver){
			this.driver=driver;
			PageFactory.initElements(driver, this);
		}
	
	public void setValueofControlPreference(String preferenceCode, String valuetoSet){
		Log.info("Trying to select modify btn to modify control preference: "+preferenceCode);
		driver.findElement(By.xpath("//td[text()='"+preferenceCode+"']/preceding-sibling::td/input[@value='M']")).click();
		Log.info("Modify radio button selected successfully");
		Log.info("Trying to set control preference value as : "+valuetoSet);
		WebElement value = driver.findElement(By.xpath("//td[text()='"+preferenceCode+"']/following-sibling::td/input[@type='text']"));
		value.clear();
		value.sendKeys(valuetoSet);
		Log.info("Control Preference value set successfuly.");
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click confirm button.");
		submit.click();
		Log.info("Confirm button clicked successfuly.");		
	}

	
}
