package com.pageobjects.superadminpages.grademanagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewGradeDetailsPage {

	@FindBy(name = "add")
	private WebElement addButton;

	@FindBy(name = "modify")
	private WebElement modifyButton;

	@FindBy(name = "delete")
	private WebElement deleteButton;

	@FindBy(name = "backview")
	private WebElement backButton;

	@FindBy(xpath="//ol/li")
	private WebElement ErrorMessage;


	WebDriver driver = null;

	public ViewGradeDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void ClickAddButton() {
		addButton.click();
		Log.info("User clicked Add button");
	}

	public void ClickModifyButton() {
		modifyButton.click();
		Log.info("User clicked Modify button");
	}

	public void ClickDeleteButton() {
		deleteButton.click();
		Log.info("User clicked Delete button");
	}

	public void ClickbackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}


	public String getErrorMessage() {
		String msg = ErrorMessage.getText();
		Log.info("The Error message is:" +msg);
		return msg;
	}


	public void selectGrade(String gradeName){

		Log.info("User is trying to click Unbar checkbox");

		WebElement gradeSelectButton = driver.findElement(By.xpath("//tr/td[contains(.,'"+gradeName+"')]/../td/input[@type='radio']"));
		gradeSelectButton.click();

		Log.info("User selects Grade");

	}



	public void selectDefaultGrade(String gradeName){

		Log.info("User is trying to click Unbar checkbox");

		WebElement gradeSelectButton = driver.findElement(By.xpath("//tr/td[contains(.,'Y')]/../td/input[@type='radio']"));
		gradeSelectButton.click();

		Log.info("User selects Grade");

	}


}
