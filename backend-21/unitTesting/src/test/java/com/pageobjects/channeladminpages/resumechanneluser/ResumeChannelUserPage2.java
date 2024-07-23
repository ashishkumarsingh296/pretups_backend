/**
 * 
 */
package com.pageobjects.channeladminpages.resumechanneluser;

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
public class ResumeChannelUserPage2 {


	@FindBy(name="saveResume")
	private WebElement submit;
	
	@FindBy(name="confirmResume")
	private WebElement confirm;
	
	@FindBy(name="back")
	private WebElement backBtn;

	@FindBy(name="cancel")
	private WebElement cancelBtn;

	@FindBy(xpath="//ul/li")
	private WebElement message;
	
	@FindBy(xpath="//ol")
	private WebElement errorMessage;
	
	WebDriver driver=null;
	boolean w1,w2;
	
	public ResumeChannelUserPage2(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click submit button.");
		submit.click();
		Log.info("Submit button clicked successfully.");
	}
	
	public void clickConfirmBtn(){
		Log.info("Trying to click confirm button.");
		confirm.click();
		Log.info("Confirm button clicked successfully.");
	}
	
	public void clickBackBtn(){
		Log.info("Trying to click back button.");
		backBtn.click();
		Log.info("Back button clicked successfully.");
	}
	
	public void selectCheckBox(String MSISDN){
		Log.info("Trying to select checkbox.");
		WebElement checkBox=driver.findElement(By.xpath("//td[text()='"+MSISDN+"']/preceding::td/preceding::input[@type='checkbox']"));
		checkBox.click();
		Log.info("Check box selected successfully for MSISDN: "+MSISDN);
	}

	public void clickCancelBtn(){
		Log.info("Trying to click Cancel.");
		cancelBtn.click();
		Log.info("Cancel button clicked successfuly.");
	}
	
	public String fetchMessage() {
		
		String fetchedmessage=null;
		try{
		Log.info("Trying to fetch success message.");
		fetchedmessage=message.getText();
		Log.info("Message fetched as :: "+fetchedmessage);
		}
		catch(Exception e){
			Log.info("Success message not found.");
			fetchedmessage=errorMessage.getText();
			Log.writeStackTrace(e);
		}
		return fetchedmessage;
		
	}
	
	
}
