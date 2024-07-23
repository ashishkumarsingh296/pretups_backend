package com.pageobjects.superadminpages.messageGateway;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class MessageGatewaySubCategories {
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=MSGAT001')]]")
    private WebElement AddmessageGateway;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=MSGAT004')]]")
    private WebElement ModifyMessageGateway;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=MSGATMAP1')]]")
    private WebElement messageGatewayMapping;
	
	WebDriver driver;
    
    public MessageGatewaySubCategories(WebDriver driver) {
    	this.driver = driver;
    	PageFactory.initElements(driver, this);
        }
    
	
	public void clickAddgateway() {
    	Log.info("Trying to click Add Gateway link");
    	AddmessageGateway.click();
    	Log.info("Add Gateway link clicked successfully");
        }
    
    public void clickModifygateway() {
    	Log.info("Trying to click Modify Gateway link");
    	ModifyMessageGateway.click();
    	Log.info("Modify Gateway link clicked successfully");
        }
    
    public void clickMessageGatewayMapping() {
    	Log.info("Trying to click MessageGatewayMapping link");
    	messageGatewayMapping.click();
    	Log.info("MessageGatewayMapping link clicked successfully");
        }

}
