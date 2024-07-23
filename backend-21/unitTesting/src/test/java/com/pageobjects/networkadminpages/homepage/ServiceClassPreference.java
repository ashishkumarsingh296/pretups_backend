package com.pageobjects.networkadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ServiceClassPreference {
	
	WebDriver driver;
	@ FindBy(name = "serviceCode")
    private WebElement serviceClass;
	
	@ FindBy(name = "submitButton")
    private WebElement submitButton;
	
	
	public ServiceClassPreference(WebDriver driver) {
    	this.driver = driver;
    	PageFactory.initElements(driver, this);
        }
    
    public void selectServiceClass(String service){
    	Log.info("Trying to select Service Class: "+service);
    	Select selectSerClass = new Select(serviceClass);
    	selectSerClass.selectByValue(service);
    	Log.info("Service class selected as : "+service);
    }
	
    public void clicksubmitBtn(){
    	Log.info("Trying to click submit button.");
    	submitButton.click();
    	Log.info("Submit button clicked successfully");
    }

}
