package com.pageobjects.networkadminpages.commissionprofile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyCommPage_Vodafone {

	@FindBy(name = "grphDomainCode")
	private WebElement geoDomain;

	@FindBy(name = "gradeCode")
	private WebElement grade;
	
	@FindBy(name = "add")
	private WebElement addButton;
	

	@FindBy(name = "edit")
	private WebElement modifyButton;

	@FindBy(name = "view")
	private WebElement viewButton;

	
	WebDriver driver = null;

	public ModifyCommPage_Vodafone(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	
	
	public void selectGeographicalDomain(String GeographicalDomain) {
		Select geographicalDomain = new Select(geoDomain);
		geographicalDomain.selectByVisibleText(GeographicalDomain);
		Log.info("User selected Geographical Domain.");
	}

	public void selectGrade(String Grade) {
		Select grade1 = new Select(grade);
		grade1.selectByVisibleText(Grade);
		Log.info("User selected Grade.");
	}
	
	public void clickAddButton() {
		addButton.click();
		Log.info("User clicked Add Button.");
	}

	public void clickModifyButton() {
		modifyButton.click();
		Log.info("User clicked Modify Button.");
	}

	public void clickViewButon() {
		viewButton.click();
		Log.info("User clicked View Button.");
	}
}
