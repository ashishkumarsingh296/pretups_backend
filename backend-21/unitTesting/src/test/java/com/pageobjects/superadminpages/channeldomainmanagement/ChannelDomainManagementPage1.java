/**
 * 
 */
package com.pageobjects.superadminpages.channeldomainmanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class ChannelDomainManagementPage1 {
	
	@FindBy(name="add")
	private WebElement addBtn;
	
	@FindBy(name="gotoconfirm")
	private WebElement submitBtn1;
	
	@FindBy(name="confirm")
	private WebElement confirmBtn;
	
	@FindBy(name="modify")
	private WebElement modifyBtn;
	
	@FindBy(name="domainTypeCode")
	private WebElement channnelDomainType;

	@FindBy(name="domainCodeforDomain")
	private WebElement domainCode;
	
	@FindBy(name="domainName")
	private WebElement channeldomainName;

	@FindBy(name="numberOfCategories")
	private WebElement channeldomainCategories;

	@FindBy(name="submit")
	private WebElement submitBtn;
	
	@FindBy(name = "delete")
	private WebElement delete;

	@FindBy(name = "domainStatus")
	private WebElement domainStatus;

	
		WebDriver driver = null;

		public ChannelDomainManagementPage1(WebDriver driver) {
			this.driver = driver;
			PageFactory.initElements(driver, this);
		}

	public void clickAddButton(){
		Log.info("Trying to click add button.");
		addBtn.click();
		Log.info("Add button clicked.");
	}
	
	public void selectChannelDomainType(String domainType){
		Log.info("Trying to select channel domain type.");
		Select chnlDomainType=new Select(channnelDomainType);
		chnlDomainType.selectByVisibleText(domainType);
		Log.info("Channel domain type selected as :: "+domainType);
		
	}
	
	public void enterChannelDomainCode(String dCode){
		Log.info("Trying to enter Channel Domain Code.");
		domainCode.sendKeys(dCode);
		Log.info("Channel Domain Code entered as :: "+dCode);
	}
	
	public void enterChannelDomainName(String dName){
		Log.info("Trying to enter Channel domain name.");
		channeldomainName.sendKeys(dName);
		Log.info("Channel domain name entered as :: "+dName);
	}
	
	public void enterNumberOfDomainCategories(String cNumber){
		Log.info("Trying to enter number of Channel domain categories.");
		channeldomainCategories.sendKeys(cNumber);
		Log.info("Number of Channel domain categories entered as :: "+cNumber);
	}
	
	public void clickSubmitButton(){
		Log.info("Trying to click submit button");
		submitBtn.click();
		Log.info("Submit button clicked successfuly.");
	}

	public void selectDomain(String domain){
		Log.info("Trying to select channel domain type.");
		driver.findElement(By.xpath("//td[text()='"+domain+"']/..//input[@type='radio']")).click();
		Log.info("Channel domain selected as :: "+domain);
		
	}
	
	public void clickModifyButton(){
		Log.info("Trying to click modify button");
		modifyBtn.click();
		Log.info("Modify button clicked successfuly.");
	}
	
	public void clickSubmitButton1(){
		Log.info("Trying to click submit button");
		submitBtn1.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void clickConfirmButton(){
		Log.info("Trying to click confirm button");
		confirmBtn.click();
		Log.info("Confirm button clicked successfuly.");
	}
	
	public void clickDeleteButton(){
		Log.info("Trying to click delete button");
		delete.click();
		Log.info("delete button clicked successfuly.");
	}
	
	public void selectStatus(String status){
		Log.info("Trying to Select Status");
		Select selectStatus = new Select(domainStatus);
		selectStatus.selectByVisibleText(status);
		Log.info("Selected Status: "+status);
	}
}
