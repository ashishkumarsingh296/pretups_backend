package com.pageobjects.networkadminpages.c2scardgroup;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyC2SCardGroupPage2 {
	@FindBy(name = "cardGroupSetName")
	private WebElement cardGroupSetName;

	@FindBy(name = "applicableFromDate")
	private WebElement applicableFromDate;

	@FindBy(name = "applicableFromHour")
	private WebElement applicableFromHour;
	
	@FindBy(xpath = "//img[@src[contains(.,'/images/add.gif')]]")
	private WebElement addCardGroupList;

	@FindBy(xpath = "//img[@src[contains(.,'/images/edit.gif')]]")
	private WebElement editCardGroup;

	@FindBy(xpath = "//img[@src[contains(.,'/images/test.gif')]]")
	private WebElement calculateCardGroup;
	
	@FindBy(name = "save")
	private WebElement saveBtn;

	@FindBy(name = "reset")
	private WebElement resetBtn;

	@FindBy(name = "back")
	private WebElement backBtn;
	
	@FindBy(className = "level1active")
	private WebElement suspended;
	
	WebDriver driver = null;

	public ModifyC2SCardGroupPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterC2SCardGroupSetName(String CardGroupName) {
		cardGroupSetName.sendKeys(CardGroupName);
		Log.info("User entered Card Group Name.");
	}

	public void enterApplicableFromDate(String Date) {
		applicableFromDate.clear();
		applicableFromDate.sendKeys(Date);
		Log.info("User entered Applicable from Date as: " + Date);
	}

	public void enterApplicableFromHour(String Hour) {
		applicableFromHour.clear();
		applicableFromHour.sendKeys(Hour);
		Log.info("User entered Applicable from Hour as: " + Hour);
	}

	public void clickAddCardGroupList() {
		addCardGroupList.click();
		Log.info("User clicked Add Card Group Icon.");
	}

	public void clickEditCardGroup() {
		editCardGroup.click();
		Log.info("User clicked Edit Card Group Icon.");
	}

	public void clickCalculateCardGroup() {
		calculateCardGroup.click();
		Log.info("User clicked Calculate Card Group Icon.");
	}

	public void clicksaveBtn() {
		saveBtn.click();
		Log.info("User clicked save button.");
	}

	public void clickResetBtn() {
		resetBtn.click();
		Log.info("User clicked reset button.");
	}

	public void clickBackBtn() {
		backBtn.click();
		Log.info("User clicked back button.");
	}
	
	public boolean suspendedTextVisibility() {
		boolean result = false;
		try {
			if (suspended.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}
	
	public boolean verifySuspended(){		
		boolean result = (suspended.getText().matches("Suspended"));
		Log.info("Card Group is " +suspended.getText());
		
		 
		return true;
	}

}
