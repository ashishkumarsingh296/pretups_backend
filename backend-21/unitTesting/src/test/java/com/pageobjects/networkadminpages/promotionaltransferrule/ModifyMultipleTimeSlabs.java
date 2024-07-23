package com.pageobjects.networkadminpages.promotionaltransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyMultipleTimeSlabs {
	
	WebDriver driver;

	public ModifyMultipleTimeSlabs(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "timeSlabIndexed[0].fromTime")
	private WebElement timeSlabIndexed0fromTime;
	
	@FindBy(name = "timeSlabIndexed[0].tillTime")
	private WebElement timeSlabIndexed0tillTime;
	
	@FindBy(name = "timeSlabIndexed[1].fromTime")
	private WebElement timeSlabIndexed1fromTime;
	
	@FindBy(name = "timeSlabIndexed[1].tillTime")
	private WebElement timeSlabIndexed1tillTime;
	
	@FindBy(name = "timeSlabIndexed[2].fromTime")
	private WebElement timeSlabIndexed2fromTime;
	
	@FindBy(name = "timeSlabIndexed[2].tillTime")
	private WebElement timeSlabIndexed2tillTime;
	
	@FindBy(name = "modifyslab")
	private WebElement modifyslab;
	
	@FindBy(name = "reset")
	private WebElement reset;
	
	public void enterStartTime0(String value){
		Log.info("Trying to enter  value in StartTime for Slab 0");
		timeSlabIndexed0fromTime.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}
	
	public void enterStartTime1(String value){
		Log.info("Trying to enter  value in StartTime for Slab 1");
		timeSlabIndexed1fromTime.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}

	
	
	public void enterStartTime2(String value){
		Log.info("Trying to enter  value in StartTime for Slab 2");
		timeSlabIndexed2fromTime.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}

	
	public void enterEndTime0(String value){
		Log.info("Trying to enter  value in EndTime for Slab 0");
		timeSlabIndexed0tillTime.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}

	
	public void enterEndTime1(String value){
		Log.info("Trying to enter  value in EndTime for Slab 1");
		timeSlabIndexed1tillTime.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}

	
	public void enterEndTime2(String value){
		Log.info("Trying to enter  value in EndTime for Slab 2");
		timeSlabIndexed2tillTime.sendKeys(value);
		Log.info("Data entered  successfully:"+ value);
		}

	public void ClickOnModify() {
		Log.info("Trying to click on Modify Button ");
		modifyslab.click();
		Log.info("Clicked on  Modify successfully");
	}
	
	public void ClickOnReset() {
		Log.info("Trying to click on Reset Button ");
		reset.click();
		Log.info("Clicked on Reset successfully");
	}

}
