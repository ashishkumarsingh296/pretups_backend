/**
 * 
 */
package com.pageobjects.customercarepages.privaterecharge;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.classes.MessagesDAO;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils.Validator;

/**
 * @author lokesh.kontey
 *
 */
public class PrivateRecahrgeEnquirypage {
	
	@FindBy(name="subscriberMsisdn")
	private WebElement subsMSISDN;
	
	@FindBy(name="submitEnquiry")
	private WebElement submitEnquiryBtn;
	
	@FindBy(xpath="//td[text()[contains(.,'Subscriber MSISDN')]]/following-sibling::td[1]")
	
	
	WebDriver driver=null;
	
	public PrivateRecahrgeEnquirypage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterSubscriberMSISDN(String msisdn){
		Log.info("Trying to eneter subscriber msisdn.");
		subsMSISDN.clear();
		subsMSISDN.sendKeys(msisdn);
		Log.info("Subscriber MSISDN entered as: "+msisdn);
	}
	
	public void clicksubmitbtn(){
		Log.info("Trying to click submit button.");
		submitEnquiryBtn.click();
		Log.info("Submit button clicked successfully.");
	}
	
	public void compareSubsMSISDN(String actualmsisdn){
		String msisdnExist=DBHandler.AccessHandler.checkForUniqueSubscriberAliasMSISDN(actualmsisdn);
		if(msisdnExist.equalsIgnoreCase("Y")){
		WebElement wb1 = driver.findElement(By.xpath("//td[text()[contains(.,'"+MessagesDAO.getLabelByKey("privaterecharge.label.msisdn")+"')]]/following-sibling::td[1]"));
		String foundmsisdn = wb1.getText();
		Validator.messageCompare(actualmsisdn, foundmsisdn);}
		else{return;}
	}
	
	public void compareSubsSID(String actualmsisdn){
		String actualSID = DBHandler.AccessHandler.getsubscriberSIDviaMSISDN(actualmsisdn);
		WebElement wb1 = driver.findElement(By.xpath("//td[text()[contains(.,'"+MessagesDAO.getLabelByKey("privaterecharge.label.subscriberSID")+"')]]/following-sibling::td[1]"));
		String foundSID = wb1.getText();
		Validator.messageCompare(actualSID, foundSID);
	}
	
}
