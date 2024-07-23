package com.pageobjects.networkadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class PreferenceSubCategories {
	
	WebDriver driver;
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=PRF004')]]")
    private WebElement networkPreference;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=PRF007')]]")
    private WebElement serviceClassPreference;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CONTPRF001')]]")
    private WebElement controlPreference;
	
	public PreferenceSubCategories(WebDriver driver) {
    	this.driver = driver;
    	PageFactory.initElements(driver, this);
        }
    
    public void clickNetworkPreferenceLink() {
    	Log.info("Trying to click Network Preference link");
    	networkPreference.click();
    	Log.info("Network Preference link clicked successfully");
        }
    
    public void clickServiceClassPreferencelink() {
    	Log.info("Trying to click Service Class Preference link");
    	serviceClassPreference.click();
    	Log.info("Service Class Preference link clicked successfully");
        }
    
    public void clickControlPreferencelink() {
    	Log.info("Trying to click Control Preference link");
    	controlPreference.click();
    	Log.info("Controls Preference link clicked successfully");
        }
	

}
