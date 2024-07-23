package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class MastersSubCategories {
	WebDriver driver;
	@ FindBy(xpath = "//a[@href='/pretups/addServiceKeyword.do?method=loadListForAdd&urlCode=1&pageCode=SK001']")
    private WebElement addServiceKeyword;
	
	@ FindBy(xpath = "//a[@href='/pretups/selectServiceKeyword.do?method=loadListForUpdate&urlCode=2&pageCode=SK003']")
    private WebElement modifyServiceKeyword;
    
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=LKADD1')]]")
    private WebElement addSubLookUp;
    
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=LKMODIFY1')]]")
    private WebElement modifySubLookUp;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=IF001')]]")
	private WebElement interfaceManagement;
    
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=NW001')]]")
    private WebElement networkManagement;
    
	@ FindBy(xpath = "//a[@href='/pretups/networkStatusAction.do?method=loadNetworkStatusList&page=0&urlCode=6&pageCode=NS001']")
    private WebElement networkStatus;
    
	@ FindBy(xpath = "//a[@href='/pretups/networkViewAction.do?method=loadNetworkListForView&page=0&urlCode=7&pageCode=NW3001']")
    private WebElement viewNetwork;
	
	@ FindBy(xpath = "//a[@href[contains(.,'SERCLSADD1')]]")
    private WebElement serviceClassManagement;
    
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=DIVISION01')]]")
    private WebElement divisionManagement;
    
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=DEPT01')]]")
    private WebElement departmentManagement;
    
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=GRPROLE001')]]")
    private WebElement groupRoleManagement;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=USC001')]]")
    private WebElement UserStatusConfiguration;
    
	@ FindBy(xpath = "//a[@href='/pretups/updateCache.do?method=updateCache&urlCode=12&pageCode=CACHE001']")
    private WebElement updateCache;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=MSGAT001')]]")
    private WebElement AddmessageGateway;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=MSGAT004')]]")
    private WebElement ModifyMessageGateway;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=MSGATMAP1')]]")
    private WebElement messageGatewayMapping;
	
    
    public MastersSubCategories(WebDriver driver) {
    	this.driver = driver;
    	PageFactory.initElements(driver, this);
        }
    
    public void clickAddServiceKeyword() {
    	Log.info("Trying to click Add Service Keyword link");
    	addServiceKeyword.click();
    	Log.info("Add Service Keyword link clicked successfully");
        }
    
    public void clickModifyServiceKeyword() {
    	Log.info("Trying to click Modify Service Keyword link");
    	modifyServiceKeyword.click();
    	Log.info("Modify Service Keyword link clicked successfully");
        }
    
    public void clickInterfaceManagement() {
    	Log.info("Trying to click Interface Management link");
    	interfaceManagement.click();
    	Log.info("interface Management link clicked successfully");
        }
    
    public void clickAddSubLookUp() {
    	Log.info("Trying to click Add Sub LookUP link");
    	addSubLookUp.click();
    	Log.info("Add Sub LookUP link clicked successfully");
        }
    
    public void clickModifySubLookUp() {
    	Log.info("Trying to click Modify Sub LookUP link");
    	modifySubLookUp.click();
    	Log.info("Modify Sub LookUP link clicked successfully");
        }
    
    public void clickNetworkManagement() {
    	Log.info("Trying to click Network Management link");
    	networkManagement.click();
    	Log.info("Network Management link clicked successfully");
        }
    
    public void clickNetworkStatus() {
    	Log.info("Trying to click Network Status link");
    	networkStatus.click();
    	Log.info("Network Status link clicked successfully");
        }
    
    public void clickViewNetwork() {
    	Log.info("Trying to click View Network link");
    	viewNetwork.click();
    	Log.info("View Network link clicked successfully");
        }
    
    public void clickServiceClassManagement() {
    	Log.info("Trying to click Service Class Management link");
    	serviceClassManagement.click();
    	Log.info("Service class Management link clicked successfully");
        }
    
    public void clickDivisionManagement() {
    	Log.info("Trying to click Division Management link");
    	divisionManagement.click();
    	Log.info("Division Management link clicked successfully");
        }
    
    public void clickDepartmentManagement() {
    	Log.info("Trying to click Department Managament link");
    	departmentManagement.click();
    	Log.info("Department management link clicked successfully");
        }
    
    public void clickGroupRoleManagement() {
    	Log.info("Trying to click Group Role Management link");
    	groupRoleManagement.click();
    	Log.info("Group Role Management link clicked successfully");
        }
    
    public void clickUserStatusConfiguration() {
    	Log.info("Trying to click UserStatusConfiguration link");
    	UserStatusConfiguration.click();
    	Log.info("UserStatusConfiguration link clicked successfully");
        }
    
    public void clickUpdateCache() {
    	Log.info("Trying to click Update Cache link");
    	updateCache.click();
    	Log.info("Update Cache link clicked successfully");
        }
    
    
    
    
    
}
