package com.pageobjects.channeluserspages.channelenquiry;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.classes.MessagesDAO;
import com.utils.Log;

public class O2CEnquiryTransferViewSpringPage {



	@FindBy(name="O2CBackFirst")
	WebElement O2CBackFirst;

	@FindBy(id="transferNumberDispaly")
	private WebElement transferNumberDispaly;

	@FindBy(id="transferCategoryDesc")
	private WebElement transferCategoryDescValue;

	@FindBy(id="transferProfileName")
	private WebElement transferProfileName;
	WebDriver driver = null;

	public O2CEnquiryTransferViewSpringPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	public void o2CBackFirstButton(){
		Log.info("Trying to click Back button");
		O2CBackFirst.click();
		Log.info("Clicked back button ");
	}

	public Map<String, String> prepareO2CEnquiryData(){
		
	HashMap<String, String> O2CTransfersData = new HashMap<String, String>();
	
	String TransferNumber_Key = MessagesDAO.getLabelByKey("pretups.channeltransfer.enquirytransferlist.label.transfernum");
	
	String TransferNumber_Locator = "//label[contains(.,'"+ TransferNumber_Key +"')]/../input";
	
	
	O2CTransfersData.put("transferNumber", driver.findElement(By.xpath(TransferNumber_Locator)).getAttribute("value"));
	
	
	return O2CTransfersData;
	}


}

