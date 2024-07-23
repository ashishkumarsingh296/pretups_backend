package com.pageobjects.superadminpages.categorytransfercontrolprofile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class CategoryTrfControlProfilePage1 {

	@FindBy(name = "domainTypeCode")
	private WebElement domainName;

	@FindBy(name = "domainCodeforCategory")
	private WebElement categoryName;

	@FindBy(name = "DomainSubmit")
	private WebElement submitButton;

	WebDriver driver = null;

	public CategoryTrfControlProfilePage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectDomainName(String DomainName) {
		Select select1 = new Select(domainName);
		select1.selectByVisibleText(DomainName);
		Log.info("User selected Domain Name.");
	}

	public void selectCategoryName(String CategoryName) {
		Select select1 = new Select(categoryName);
		select1.selectByVisibleText(CategoryName);
		Log.info("User selected Category Name.");
	}

	public void clickSubmitButton() {
		submitButton.click();
		Log.info("User clicked Submit Button.");
	}
}
