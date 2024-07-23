package com.pageobjects.networkadminpages.p2pcardgroup;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyP2PCardGroupPage2 {
	
	@FindBy(name = "cardGroupSetName")
	private WebElement cardGroupSetName;

	@FindBy(name = "applicableFromDate")
	private WebElement applicableFromDate;

	@FindBy(name = "applicableFromHour")
	private WebElement applicableFromHour;

	@ FindBy(name = "setType")
	private WebElement cardGroupSetType;
	
	@ FindBy(xpath = "//img[@src='/pretups/jsp/common/images/add.gif']")
	private WebElement cardGroupIcon;
	
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

	public ModifyP2PCardGroupPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterP2PCardGroupSetName(String CardGroupName) throws InterruptedException {
		cardGroupSetName.sendKeys(CardGroupName);
		Log.info("User entered Card Group Name.");
	}

	public void enterApplicableFromDate(String ApplicableFromDate) throws InterruptedException {
		applicableFromDate.clear();
		applicableFromDate.sendKeys(ApplicableFromDate);
		Log.info("User entered Applicable From Date.");
	}

	public void enterApplicableFromHour(String Hour) {
		applicableFromHour.clear();
		applicableFromHour.sendKeys(Hour);
		Log.info("User entered Applicable from Hour.");
	}
	
	public void selectCardGroupSetType(String cardGroupType) throws InterruptedException {
		Select cardGroupType1 = new Select(cardGroupSetType);
		cardGroupType1.selectByVisibleText(cardGroupType);
		Log.info("User selected cardGroupType.");
	}
	
	public void clickAddCardGroupList() {
		cardGroupIcon.click();
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
	
	public void clickSaveButton() {
		saveBtn.click();
		Log.info("User clicked Save Button.");
	}

	public void clickResetButton() {
		resetBtn.click();
		Log.info("User clicked Reset Button.");
	}
	
	public void clickBackButton() {
		backBtn.click();
		Log.info("User clicked Back Button.");
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
