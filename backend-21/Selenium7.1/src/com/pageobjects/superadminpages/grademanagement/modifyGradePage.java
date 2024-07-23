package com.pageobjects.superadminpages.grademanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class modifyGradePage {
	
	
	
	

	@FindBy(name = "gradeName")
	private WebElement gradeName;

	@FindBy(name = "defaultGrade")
	private WebElement defaultGradeCheckbox;

	@FindBy(name = "confirmmodify")
	private WebElement saveButton;

	@FindBy(name = "reset")
	private WebElement resetButton;

	@FindBy(name = "back")
	private WebElement backButton;

	WebDriver driver = null;

	public modifyGradePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	

	public void changeGradeName(String GradeName) {
		gradeName.clear();
		gradeName.sendKeys(GradeName);
		Log.info("User entered GradeName.");
	}

	public void ClickSaveButton() {
		saveButton.click();
		Log.info("User clicked Save button");
	}

	public void ClickResetButton() {
		resetButton.click();
		Log.info("User clicked Reset button");
	}

	public void ClickbackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}
	
	


}
