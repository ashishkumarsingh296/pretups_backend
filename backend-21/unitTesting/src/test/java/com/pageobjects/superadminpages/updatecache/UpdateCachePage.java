package com.pageobjects.superadminpages.updatecache;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;
import com.utils._masterVO;

public class UpdateCachePage {
	
	WebDriver driver = null;
	public UpdateCachePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "cacheAll")
	private WebElement AllCache;

	@FindBy(name = "submitButton" )
	private WebElement submitButton;
	
	@FindBy(xpath = "//table[4]/tbody/tr[1]/td/table")
	private WebElement Message;
	
	@FindBy(name="instanceAll")
	private WebElement AllInstances;
	
	public void checkAllCache(){
	Log.info("Trying to check 'All' option in Update Cache");
	AllCache.click();
	Log.info("'All' option clicked successfully in Update Cache");
	}
	
	public void checkAllInstances(){
	Log.info("Trying to check 'All' option for Instances in Update Cache");
	AllInstances.click();
	Log.info("'All' option for Instances clicked successfully in Update Cache");
	}
	
	public void selectCacheParams(String cacheParams[]) {
		for (int objCounter = 0; objCounter < cacheParams.length; objCounter++) {
			Log.info("Trying to select Cache Param ID: " + cacheParams[objCounter]);
			String cacheType = null;
			
			if (_masterVO.getClientDetail("UPDATECACHE_VER").equalsIgnoreCase("0")) {
				cacheType = driver.findElement(By.xpath("//input[@name='cacheParam' and @value='"+cacheParams[objCounter]+"']/ancestor::td[1]")).getText();
				driver.findElement(By.xpath("//input[@name='cacheParam' and @value='"+cacheParams[objCounter]+"']")).click();
			} else if (_masterVO.getClientDetail("UPDATECACHE_VER").equalsIgnoreCase("1")) {
				cacheType = driver.findElement(By.xpath("//input[@name='cacheParam' and @id='"+cacheParams[objCounter]+"']/ancestor::td[1]")).getText();
				driver.findElement(By.xpath("//input[@name='cacheParam' and @id='"+cacheParams[objCounter]+"']")).click();
			}
			 else if (_masterVO.getClientDetail("UPDATECACHE_VER").equalsIgnoreCase("2")) {
					cacheType = driver.findElement(By.xpath("//input[@name='cacheParam' and @id='"+cacheParams[objCounter]+"']/ancestor::td[1]")).getText();
					driver.findElement(By.xpath("//input[@name='cacheParam' and @id='"+cacheParams[objCounter]+"']")).click();
				}
			Log.info("Cache Param '" + cacheType + "' selected successfully");
		}
	}

	public void clickSubmitButton(){
	Log.info("Trying to click Submit Button");
	submitButton.click();
	Log.info("Submit Button clicked successfully");
	}
	
	public String getMessage() {
		Log.info("Trying to fetch Message from Update Cache");
		String MessageText = Message.getText();
		Log.info("Message fetched successfully");
		return MessageText;
	}

}
