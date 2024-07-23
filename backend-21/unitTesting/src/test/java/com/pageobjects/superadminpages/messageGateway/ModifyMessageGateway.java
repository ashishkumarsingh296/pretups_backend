package com.pageobjects.superadminpages.messageGateway;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyMessageGateway {

	@FindBy(name="btnSubmit")
	private WebElement modify;

	@FindBy(name="btnDeleteRule")
	private WebElement deleteButton;



	WebDriver driver;

	public ModifyMessageGateway(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectGatewayCode(String gatewayCode){

		WebElement radioButton= driver.findElement(By.xpath("//input[@type='radio' and @value='"+gatewayCode+"']"));

		if(!radioButton.isSelected()){

			radioButton.click();
			Log.info("gateway is selected");


		}
		else{
			Log.info("gateway is already selected");

		}

	}


	public void clickModify(){
		Log.info("Trying to click modify button");
		modify.click();
		Log.info("Modify button clicked successfully");

	}
	
	public void clickDelete(){
		Log.info("Trying to click delete button");
		deleteButton.click();
		Log.info("Delete button clicked successfully");

	}

}

