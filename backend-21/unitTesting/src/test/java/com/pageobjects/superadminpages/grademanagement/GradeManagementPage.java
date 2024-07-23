package com.pageobjects.superadminpages.grademanagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class GradeManagementPage {

	@FindBy(name = "domainCodeforDomain")
	private WebElement domainName;

	@FindBy(name = "categoryCode")
	private WebElement categoryName;

	@FindBy(name = "submit")
	private WebElement submitButton;

	WebDriver driver = null;

	public GradeManagementPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectDomain(String Domain) {
		Select domain1 = new Select(domainName);
		domain1.selectByVisibleText(Domain);
		Log.info("User selected Domain.");
	}

	public void selectCategory(String Category) {
		Select categoryCode1 = new Select(categoryName);
		categoryCode1.selectByVisibleText(Category);
		Log.info("User selected Category.");
	}

	public void clickSubmitButton() {
		submitButton.click();
		Log.info("User clicked Submit Button.");
	}

}
