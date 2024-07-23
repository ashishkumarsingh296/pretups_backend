package com.pageobjects.networkadminpages.geographicaldomain;

import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

/**
 * @author Ayush Abhijeet
 * This class Contains the Page Objects for Selecting Parent of Geographical Domain 
 **/

public class SelectParentGeographicalDomainPage {

	String MasterSheetPath = _masterVO.getProperty("DataProvider");

	@ FindBy(name = "indexParentValue[0]")
	private WebElement indexParentValue;
	
	@ FindBy(name = "submitButton")
	private WebElement submitButton;

	@ FindBy(name = "btnBack")
	private WebElement btnBack;

	WebDriver driver= null;

	public SelectParentGeographicalDomainPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	

	public String enterIndexParentValue() {
		List<WebElement> listOfTextBox = driver.findElements(By.xpath("//input[@name[contains(., 'indexParentValue')]]"));
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		
		for(int i=0;i<listOfTextBox.size();i++)
		{
			listOfTextBox.get(i).sendKeys(ExcelUtility.getCellData(i+1, 1));
		}
		return ExcelUtility.getCellData((listOfTextBox.size()), 1);
	}
	
	public int getTextBoxCount() {
		List<WebElement> listOfTextBox = driver.findElements(By.xpath("//input[@name[contains(., 'indexParentValue')]]"));
		int size = listOfTextBox.size();
		return size;
	}
	
	public void clickSubmitButton() {
		submitButton.click();
		Log.info("User clicked submit Button");
	}
	
	public void clickBackButton() {
		btnBack.click();
		Log.info("User clicked Back Button");
	}
	
	String href = "javaScript:searchParentDomain('0')";
	public void clickLinkByHref() {
	    List<WebElement> anchors = driver.findElements(By.tagName("a"));
	    Iterator<WebElement> i = anchors.iterator();

	    while(i.hasNext()) {
	        WebElement anchor = i.next();
	                    if(anchor.getAttribute("href").contains(href)) {
	                        anchor.click();
	                        break;
	        }
	    }
	}

	
}
