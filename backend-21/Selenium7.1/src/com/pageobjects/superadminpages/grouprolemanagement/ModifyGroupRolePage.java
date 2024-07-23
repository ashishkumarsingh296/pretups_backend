package com.pageobjects.superadminpages.grouprolemanagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyGroupRolePage {
	WebDriver driver;
	@ FindBy(name = "roleCode")
    private WebElement roleCode;
	
    @ FindBy(name = "roleName")
    private WebElement roleName;
    
    @ FindBy(name = "groupName")
    private WebElement groupName;
    
    @ FindBy(name = "fromHour")
    private WebElement fromHour;
    
    @ FindBy(name = "toHour")
    private WebElement toHour;
    
    @ FindBy(name = "defaultAllowed")
    private WebElement defaultGroupRole;
    
    @ FindBy(name = "checkall")
    private WebElement checkall;
    
    @ FindBy(name = "save")
    private WebElement saveButton;
    
    @ FindBy(name = "reset")
    private WebElement resetButton;
    
    @ FindBy(name = "back")
    private WebElement backButton;
    
    public ModifyGroupRolePage(WebDriver driver) {
    	this.driver = driver;
    	PageFactory.initElements(driver, this);
        }
    
    public void enterRoleCode(String RoleCode) throws InterruptedException {
    	roleCode.sendKeys(RoleCode);
    	Log.info("User entered RoleCode:"+RoleCode);
        }
    
    public void enterroleName(String RoleName) throws InterruptedException {
    	roleName.sendKeys(RoleName);
    	Log.info("User entered Role Name:"+RoleName);
        }
    
    public void entergroupName(String GroupName) throws InterruptedException {
    	groupName.sendKeys(GroupName);
    	Log.info("User entered Group Name:"+GroupName);
        }
    
    public void enterfromHour(String FromHour) throws InterruptedException {
    	fromHour.sendKeys(FromHour);
    	Log.info("User entered fromHour:"+FromHour);
        }
    
    public void entertoHour(String ToHour) throws InterruptedException {
    	toHour.sendKeys(ToHour);
    	Log.info("User entered toHour:"+ToHour);
        }
    
    public void clickCheckAllBox() throws InterruptedException {
    	checkall.click();
    	Log.info("User clicked check all checkbox.");
        }
    
    public void clickSaveButton() throws InterruptedException {
    	saveButton.click();
    	Log.info("User clicked save button.");
        }
    
    public void clickResetButton() throws InterruptedException {
    	resetButton.click();
    	Log.info("User clicked reset button.");
        }
    
    public void clickBackButton() throws InterruptedException {
    	backButton.click();
    	Log.info("User clicked back button.");
        }
    
    public void checkDefaultGroupRoleCheckbox() throws InterruptedException {
    	if(defaultGroupRole.isSelected()){
    		return;
    	}
    	else{
    	defaultGroupRole.click();
    	}
    	Log.info("Default Group Role is checked.");
        }
}

