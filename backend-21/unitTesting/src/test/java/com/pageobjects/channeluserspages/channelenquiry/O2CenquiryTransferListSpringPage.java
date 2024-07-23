package com.pageobjects.channeluserspages.channelenquiry;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CenquiryTransferListSpringPage {

	@FindBy(name = "O2CBackFirstFromTwo")
	private WebElement O2CBackFirstFromTwo;

	@FindBy(name = "btndownloadFromTwo")
	private WebElement btndownload;

	@FindBy(name="")
	private WebElement link;
	WebDriver driver = null;

	public O2CenquiryTransferListSpringPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	public void o2CBackFirstButton(){
		Log.info("Trying to click Back button");
		O2CBackFirstFromTwo.click();
		Log.info("Clicked back button ");
	}

	public void btndownloadButton(){
		Log.info("Trying to click download button");
		btndownload.click();
		Log.info("Clicked download button ");
	}

	public void clickOnTransferNumberByMSISDN(String userCode){
		Log.info("Trying to click transfer number of Sender: "+userCode);
		WebElement element = null;
		String xpath = "//td[contains(text(),'"+userCode+"')]/preceding-sibling::td";
		element=driver.findElement(By.xpath(xpath));
		element.click();
		Log.info("Clicked transfer number of Sender:" +userCode);
	}

}
