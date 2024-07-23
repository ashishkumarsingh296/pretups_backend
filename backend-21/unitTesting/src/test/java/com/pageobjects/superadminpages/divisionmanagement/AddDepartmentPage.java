package com.pageobjects.superadminpages.divisionmanagement;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddDepartmentPage {
	@FindBy(name = "divDeptType")
	private WebElement divisionType;

	@FindBy(name = "divDeptName")
	private WebElement departmentName;

	@FindBy(name = "divisionId")
	private WebElement division;

	@FindBy(name = "divDeptShortCode")
	private WebElement departmentShortCode;

	@FindBy(name = "status")
	private WebElement status;

	@FindBy(name = "addConfirm")
	private WebElement submitButton;

	@FindBy(name = "submit")
	private WebElement submit;

	@FindBy(name = "add")
	private WebElement addButton;
	
	@FindBy(name = "delete")
	private WebElement deleteButton;
	
	@FindBy(name = "modify")
	private WebElement modifyButton;

	@FindBy(name = "reset")
	private WebElement resetButton;

	@FindBy(name = "backFrmAdd")
	private WebElement backButton;

	@FindBy(name = "backFromView")
	private WebElement backBtn;

	@FindBy(name = "confirm")
	private WebElement confirm;
	
	@FindBy(xpath = "//ul/li")
	private WebElement msg;

	@FindBy(xpath = "//ol/li")
	private WebElement errormsg;
	
	WebDriver driver = null;

	public AddDepartmentPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Submit Button clicked successfully");
	}

	public void clickSubmit() {
		Log.info("Trying to click Submit Button");
		submit.click();
		Log.info("Submit Button clicked successfully");
	}

	public void clickAddButton() {
		Log.info("Trying to click Add Button");
		addButton.click();
		Log.info("Add Button clicked successfully");
	}

	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("Back Button clicked successfully");
	}
	
	public void clickModifyButton() {
		Log.info("Trying to click Modify Button");
		modifyButton.click();
		Log.info("Modify Button clicked successfully");
	}

	public void clickResetButton() {
		Log.info("Trying to click Reset Button");
		resetButton.click();
		Log.info("Reset Button clicked successfully");
	}

	public void clickConfirmButton() {
		Log.info("Trying to click Confirm Button");
		confirm.click();
		Log.info("Confirm Button clicked successfully");
	}

	public void selectDivisionType() {
		Log.info("Trying to select Division Type");
		Select select = new Select(divisionType);
		select.selectByValue("OPERATOR");
		Log.info("Division Type OPERATOR selected successfully");
	}

	public void selectDivision(String divisionName) {
		Log.info("Trying to select Division");
		Select select = new Select(division);
		select.selectByVisibleText(divisionName);
		Log.info("Division selected as: " + divisionName);
	}
	
	/*
	public void selectDivision_Neg(String divisionName) {
		Log.info("Trying to select Division");
		Select select = new Select(division);
		select.selectByVisibleText(divisionName);
		Log.info("Division selected as: " + divisionName);
	}
	
	*/
	
	public void selectDivision_Neg(int index) {
		Select select = new Select(division);
		select.selectByIndex(index);
		Log.info("User selected division." + division.getText());
	}

	public int getDivisionIndex() {
		Select select = new Select(division);
		ArrayList<WebElement> divisionName = (ArrayList<WebElement>) select.getOptions();
		int size = divisionName.size();
		System.out.println(size);
		Log.info("List of Product Codes." + size);
		return --size;
	}

	/*
	 * public void selectStatus(String Status) throws InterruptedException {
	 * Select select = new Select(status); select.selectByVisibleText(Status);
	 * Log.info("User selected status."); }
	 */
	public void enterDepartmentName(String DeptName) {
		Log.info("Trying to enter Department Name");
		departmentName.sendKeys(DeptName);
		Log.info("Department name entered as: " + DeptName);
	}

	public void enterDepartmentShortCode(String DeptShortCode) {
		Log.info("Trying to enter Department Short Code");
		departmentShortCode.sendKeys(DeptShortCode);
		Log.info("Department Short Code entered as: " + DeptShortCode);
	}
	
	public String getMessage() {
		String message1 = null;
		Log.info("Trying to fetch Success Message");
		try {
		message1 = msg.getText();
		Log.info("Success Message Returned: " + message1);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return message1;
	}
	
	public String getErrorMessage() {
		String message1 = null;
		Log.info("Trying to fetch error Message");
		try {
		message1 = errormsg.getText();
		Log.info("Error Message Returned: " + message1);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return message1;
	}
	
	public void clickDeleteButton() {
		Log.info("Trying to click Delete Button");
		deleteButton.click();
		Log.info("Delete Button clicked successfully");
	}
	
	
	public void selectDepartment(String dept){

		Log.info("User is trying to select department");

		WebElement divisionSelectButton = driver.findElement(By.xpath("//tr/td[contains(.,'"+dept+"')]/../td/input[@type='radio']"));
		divisionSelectButton.click();

		Log.info("User selects department");

	}
}
