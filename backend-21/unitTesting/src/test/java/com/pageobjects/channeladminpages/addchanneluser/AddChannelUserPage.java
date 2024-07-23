package com.pageobjects.channeladminpages.addchanneluser;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;
import com.utils.SwitchWindow;

public class AddChannelUserPage {

	@FindBy(name = "domainCode")
	private WebElement domain;

	@FindBy(name = "channelCategoryCode")
	private WebElement category;

	@FindBy(name = "parentCategoryCode")
	private WebElement parentCategoryCode;

	@FindBy(name = "parentDomainCode")
	private WebElement geographicalDomain;

	@FindBy(xpath = "//input[@type='submit' and @name='add']")
	private WebElement submit;

	@FindBy(xpath = "//a [@href='javascript:window.close()']")
	private WebElement CloseWindow;

	@FindBy(name = "searchTextArrayIndexed[0]")
	private WebElement ownerName;

	@FindBy(name = "searchTextArrayIndexed[1]")
	private WebElement parentName;

	@FindBy(xpath = "//input[@type='submit' and @name='submitParent']")
	private WebElement submitParentBtn;

	@FindBy(name = "back")
	private WebElement backButton;

	@FindBy(xpath = "//a[@href [contains(.,'searchParentUser')]]")
	private WebElement searchParentUser;

	@FindBy(xpath = "//input[@type='button' and @name='edit']")
	private WebElement submitBtn;

	@FindBy(name = "userId")
	private WebElement userID;

	@FindBy(name = "searchTextArrayIndexed[2]")
	private WebElement channelName;
	
	@FindBy(xpath = "//input[@name[contains(.,'searchTextArrayIndexed')]]")
	private List<WebElement> listOftextboxes;
	
	
	WebDriver driver = null;
	boolean w1, w2,w3;

	public AddChannelUserPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectDomain(String Domain) {
		Log.info("Trying to select Domain");
		Select select = new Select(domain);
		select.selectByVisibleText(Domain);
		Log.info("Domain Name Selected as: " + Domain);
	}

	public void selectCategory(String Category) {
		Log.info("Trying to select Category");
		Select select = new Select(category);
		select.selectByVisibleText(Category);
		Log.info("Category Name selected as: " + Category);
	}

	public void selectGeographyDomain(String geo) {
		try {
			Log.info("Trying to select Geographical Domain");
			Select select = new Select(geographicalDomain);
			select.selectByVisibleText(geo);
			Log.info("Geographical Domain selected as: "+geo);
		} catch (Exception e) {
			Log.info("Geographical domain field not found");
		}
	}

	public void parentCategory(String Category) {
		try {
			Log.info("Trying to select Parent Category");
			Select select = new Select(parentCategoryCode);
			select.selectByVisibleText(Category);
			Log.info("Parent Category Name selected as: " + Category);
		} catch (Exception e) {
			Log.info("Parent category not found");
		}
	}

	public void clickSubmitBtn() {
		Log.info("Trying to click submit button");
		submit.click();
		Log.info("Submit button clicked successfully");
	}

	public void enterOwnerUser() {
		try {
			Log.info("Trying to check if Owner User Search field exists");
			w1 = ownerName.isDisplayed();
			/*
			 * Log.info("Try to enter % in owner user");
			 * ownerName.sendKeys("%"); Log.info("Entered value is : %");
			 * Log.info("Trying to click on search button");
			 * searchParentUser.click(); Log.info("Search button clicked");
			 */
			} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Owner user search field not found");
		}
	}

	public void selectOwnerName(String UserID) throws InterruptedException {
		if (w1 == true) {
			Log.info("Trying to select owner Name");
			ownerName.sendKeys(UserID);
			Log.info("Owner Name selected successfully");
			// searchParentUser.click();
			SwitchWindow.switchwindow(driver);
			Log.info("Trying to click submit button and return to main window");
			//submitBtn.click();
			SwitchWindow.backwindow(driver);
			Log.info("Submit button clicked.");
		} else {
			Log.info("Owner User Name not found");
		}
	}

	public void enterParentUser() {
		try {
			Log.info("Trying to check if Parent User field exists");
			w2 = parentName.isDisplayed();
			Log.info("Parent User link found");
			/*
			 * Log.info("Try to enter % in parent user");
			 * ownerName.sendKeys("%"); Log.info("Entered value is : %");
			 * Log.info("Trying to click on search button");
			 * searchParentUser.click(); Log.info("Search button clicked");
			 */
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Parent user search field not found");
		}
	}

	public void selectParentName(String UserID) throws InterruptedException {
		if (w2 == true) {
			Log.info("Trying to select parent Name");
			parentName.sendKeys(UserID);
			Log.info("Parent Name selected successfully");
			SwitchWindow.switchwindow(driver);
			Log.info("Trying to click submit button and return to main window");
			// submitBtn.click();
			SwitchWindow.backwindow(driver);
			Log.info("Submit button clicked.");
		} else {
			Log.info("Parent user name not found");
		}
	}

	public void clickPrntSubmitBtn() {
		if (w1 == true) {
			Log.info("Trying to click submit button on search user screen");
			submitParentBtn.click();
			Log.info("Submit button clicked successfully");
		}

	}
	
	public void enterchannelUsername() {
		try {
			Log.info("Trying to check if Owner User Search field exists");
			w3 = channelName.isDisplayed();
			} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Owner user search field not found");
		}
	}

	public void selectchannelUsername(String UserID) throws InterruptedException {
		if (w3 == true) {
			Log.info("Trying to select channel Name");
			channelName.sendKeys(UserID);
			Log.info("Channel Name selected successfully");
			SwitchWindow.switchwindow(driver);
			Log.info("Trying to click submit button and return to main window");
			// submitBtn.click();
			SwitchWindow.backwindow(driver);
			Log.info("Submit button clicked.");
		} else {
			Log.info("Owner User Name not found");
		}
	}
	
	public int textBoxlist(){
		int listCount = 0;
		listCount=listOftextboxes.size();
		Log.info("Total input boxes appearing on search user screen: "+listCount);
		return listCount;
	}

}
