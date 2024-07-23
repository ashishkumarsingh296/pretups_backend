package com.pageobjects.superadminpages.serviceClassManagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class serviceClassDetailsPage {

	@FindBy(name = "add")
	private WebElement addButton;

	@FindBy(name = "modify")
	private WebElement modifyButton;

	@FindBy(name = "delete")
	private WebElement deleteButton;

	@FindBy(name = "back")
	private WebElement backButton;

	WebDriver driver;

	public serviceClassDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickAdd(){
		Log.info("User trying to click add button");

		addButton.click();
		Log.info("User clicked add button");
	}


	public void ClickModify(){
		Log.info("User trying to click modify button");

		modifyButton.click();
		Log.info("User clicked modify button");
	}

	public void clickDelete(){
		Log.info("User trying to click delete button");

		deleteButton.click();
		Log.info("User clicked delete button");
	}

	public void clickBack(){
		Log.info("User trying to click back button");

		backButton.click();
		Log.info("User clicked back button");
	}



	public void SelectServiceClass(String ServiceClassName){

		Log.info("User is trying to select service class" +ServiceClassName);

		WebElement radioButton= driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+ServiceClassName+"']/ancestor::tr[1]/td/input[@type='radio']"));
		if(!radioButton.isSelected()){
			radioButton.click();
			Log.info("Service Class is selected");
		}
		else{
			Log.info("Service Class is already selected");
		}
	}


}
