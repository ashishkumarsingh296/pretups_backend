package com.pageobjects.networkadminpages.commissionprofile;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class CommissionProfileStatusPage {

	@FindBy(name = "domainCode")
	private WebElement domain;

	@FindBy(name = "categoryCode")
	private WebElement category;

	@FindBy(name = "grphDomainCode")
	private WebElement geoDomain;

	@FindBy(name = "gradeCode")
	private WebElement grade;

	@FindBy(name = "suspend")
	private WebElement submitButton;

	WebDriver driver = null;

	public CommissionProfileStatusPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectDomain(String Domain) {
		Select domain1 = new Select(domain);
		domain1.selectByVisibleText(Domain);
		Log.info("User selected Domain." + Domain);
	}

	public void selectCategory(String Category) {
		Select categoryCode1 = new Select(category);
		categoryCode1.selectByVisibleText(Category);
		Log.info("User selected Category." + Category);
	}

	public void selectGeoDomain(String GeoDomain) {
		Select geoDomain1 = new Select(geoDomain);
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='grphDomainCode']/option[text()='"+GeoDomain+"']")));
		geoDomain1.selectByVisibleText(GeoDomain);
		Log.info("User selected GeoDomain." + GeoDomain);
	}

	public void selectGrade(String Grade) {
		Select grade1 = new Select(grade);
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(grade));
		grade1.selectByVisibleText(Grade);
		Log.info("User selected Grade." +Grade);
	}

	public void clickSubmitButton() {
		submitButton.click();
		Log.info("User clicked Submit Button.");
	}

}
