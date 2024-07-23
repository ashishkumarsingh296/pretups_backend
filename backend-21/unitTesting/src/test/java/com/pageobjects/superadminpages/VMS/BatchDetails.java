package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class BatchDetails {
	
	WebDriver driver = null;
	public BatchDetails(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	String winHandleBefore ="";
	public String switchwindow(){
        try {
         winHandleBefore = driver.getWindowHandle();

        for(String winHandle : driver.getWindowHandles()){
            driver.switchTo().window(winHandle);
        }
        }catch(Exception e){
        return "Unable to Switch Window" + e.getMessage();
        }
        return "Window is switched";
        }
	
	public void firstWindow() {
		 driver.switchTo().window(winHandleBefore);
	}
	
	public boolean checkParticularBatchType(String type) {
		boolean elementDisplayed = false;
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("//td[normalize-space()= '" + type + "']");
		
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		return elementDisplayed;
	}
	
	public boolean checkParticularBatchNumber(String type) {
		boolean elementDisplayed = false;
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("//td[normalize-space()= '" + type + "']");
		
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		return elementDisplayed;
	}
	
	public boolean verifyAllAttributes() {
		boolean elementDisplayed = false;
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("//td[normalize-space()= '" + "Batch number :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		if(!elementDisplayed) {
			return false;
			}
		TransferRuleX.setLength(0);
		TransferRuleX.append("//td[normalize-space()= '" + "Batch type :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		if(!elementDisplayed) {
			return false;
			}
		TransferRuleX.setLength(0);
		TransferRuleX.append("//td[normalize-space()= '" + "Batch reference number :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		if(!elementDisplayed) {
			return false;
			}
		TransferRuleX.setLength(0);
		TransferRuleX.append("//td[normalize-space()= '" + "Profile :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		if(!elementDisplayed) {
			return false;
			}
		TransferRuleX.setLength(0);
		TransferRuleX.append("//td[normalize-space()= '" + "Number of vouchers :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		if(!elementDisplayed) {
			return false;
			}
		TransferRuleX.setLength(0);
		TransferRuleX.append("//td[normalize-space()= '" + "From serial :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		if(!elementDisplayed) {
			return false;
			}
		TransferRuleX.setLength(0);
		TransferRuleX.append("//td[normalize-space()= '" + "To serial :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		if(!elementDisplayed) {
			return false;
			}
		TransferRuleX.setLength(0);
		TransferRuleX.append("//td[normalize-space()= '" + "Success count :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		if(!elementDisplayed) {
			return false;
			}
		TransferRuleX.setLength(0);
		TransferRuleX.append("//td[normalize-space()= '" + "Fail count :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		if(!elementDisplayed) {
			return false;
			}
		TransferRuleX.setLength(0);
		TransferRuleX.append("//td[normalize-space()= '" + "Created on :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		if(!elementDisplayed) {
			return false;
			}
		TransferRuleX.setLength(0);
		TransferRuleX.append("//td[normalize-space()= '" + "Status :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		if(!elementDisplayed) {
			return false;
			}
		TransferRuleX.setLength(0);
		TransferRuleX.append("//td[normalize-space()= '" + "Message :" + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		return elementDisplayed;
	}
	
	public void popUpCLose() {
		
		if (driver != null) {
			
			driver.close();
		}
	}
		}


