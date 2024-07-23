package com.pageobjects.superadminpages.grouprolemanagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyGroupRolePage2 {
	WebDriver driver;
	@ FindBy(name = "confirm")
    private WebElement confirmButton;
	
    @ FindBy(id = "cancel")
    private WebElement cancelButton;
    
    @ FindBy(id = "back")
    private WebElement backButton;
    
    public ModifyGroupRolePage2(WebDriver driver) {
    	this.driver = driver;
    	PageFactory.initElements(driver, this);
        }
    
    public void clickConfirmButton() throws InterruptedException {
    	confirmButton.click();
    	Log.info("User clicked confirm button.");
        }
    
    public void clickCancelButton() throws InterruptedException {
    	cancelButton.click();
    	Log.info("User clicked cancel button.");
        }
    
    public void clickBackButton() throws InterruptedException {
    	backButton.click();
    	Log.info("User clicked back button.");
        }
}

