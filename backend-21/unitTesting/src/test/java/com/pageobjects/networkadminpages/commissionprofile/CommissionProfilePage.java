package com.pageobjects.networkadminpages.commissionprofile;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class CommissionProfilePage {
	@FindBy(name = "domainCode")
	private WebElement domain;

	@FindBy(name = "categoryCode")
	private WebElement category;

	@FindBy(name = "grphDomainCode")
	private WebElement geoDomain;

	@FindBy(name = "gradeCode")
	private WebElement grade;

	@FindBy(name = "add")
	private WebElement addButton;
	
	@FindBy(name ="submit")
	private WebElement submitButton;
	

	@FindBy(name = "edit")
	private WebElement modifyButton;

	@FindBy(name = "view")
	private WebElement viewButton;

	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//tr/td/ul/li")
    WebElement UIMessage;

    @FindBy(xpath = "//tr/td/ol/li")
    WebElement errorMessage;


	WebDriver driver = null;
	WebDriverWait wait;

	public CommissionProfilePage(WebDriver driver) {
		this.driver = driver;
		wait=new WebDriverWait(driver,5);
		PageFactory.initElements(driver, this);
	}

	public void selectDomain(String Domain) {
		Select domain1 = new Select(domain);
		domain1.selectByVisibleText(Domain);
		Log.info("User selected Domain." +Domain);
	}

	public void selectCategory(String Category) {
		Select category1 = new Select(category);
		category1.selectByVisibleText(Category);
		Log.info("User selected Category."+Category);
	}
	
	
	public boolean geoDomainVisibility() {
		boolean result = false;
		try {
			if (geoDomain.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public void selectGeographicalDomain(String GeographicalDomain) {
		Select geographicalDomain = new Select(geoDomain);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='grphDomainCode']/option[text()='"+GeographicalDomain+"']")));
		geographicalDomain.selectByVisibleText(GeographicalDomain);
		Log.info("User selected Geographical Domain.: "+GeographicalDomain);
	}

	public void selectGrade(String Grade) {
		Select grade1 = new Select(grade);
		grade1.selectByVisibleText(Grade);
		Log.info("User selected Grade: "+Grade);
	}
	
	public void clicksubmitButton() {
		submitButton.click();
		Log.info("User clicked submit Button.");
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

	public String getMessage() {
		String message1 = message.getText();
		return message1;
	}
	
	public String getActualMsg() {

        String UIMsg = null;
        String errorMsg = null;
        try{
        errorMsg = errorMessage.getText();
        }catch(Exception e){
                        Log.info("No error Message found: "+e);
        }
        try{
        UIMsg = UIMessage.getText();
        }catch(Exception e){
                        Log.info("No Success Message found: "+e);
        }
        if (errorMsg == null)
                        return UIMsg;
        else
                        return errorMsg;
}

}
