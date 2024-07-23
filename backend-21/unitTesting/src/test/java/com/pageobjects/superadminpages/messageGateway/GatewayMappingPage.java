package com.pageobjects.superadminpages.messageGateway;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.commons.PretupsI;
import com.utils.Log;

public class GatewayMappingPage {


	@FindBy(name = "modifyFromMessageGatewayMapping")
	private WebElement modify;

	@FindBy(name = "deleteFromMessageGatewayMapping")
	private WebElement delete;

	@FindBy(xpath = "//ul/li")	
	private WebElement actualMessage;
	
	@FindBy(name = "submit")
	private WebElement confirm;


	WebDriver driver;

	public GatewayMappingPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}


	public void SelectMapping(String gatewayCode){


		WebElement checkBox= driver.findElement(By.xpath("//tr/td[text()='"+gatewayCode+"']/following::input[@type='checkbox']"));



		if(!checkBox.isSelected()){

			checkBox.click();
			Log.info("Gateway Mapping for gateway code:" +gatewayCode+ " is selected");


		}
		else{
			Log.info("Gateway Mapping for gateway code:" +gatewayCode+ " is already selected");

		}

	}

	public void clickDelete(){
		Log.info("Trying to click delete button");
		delete.click();
		Log.info("Delete button clicked successfully");
	}


	public void clickModify(){
		Log.info("Trying to click modify button");
		modify.click();
		Log.info("Modify button clicked successfully");
	}


	public String getMsg(){
		String msg = actualMessage.getText();
		Log.info("Actual Message fetched as " +msg);
		return msg;
	}


	public void clickConfirm(){
		Log.info("Trying to click confirm button");
		confirm.click();
		Log.info("confirm button clicked successfully");
	}



	public void SelectResponseGateway(String gatewayCode){

		Log.info("User is trying to select ResponseGateway for" +gatewayCode);


		List<WebElement> rowCount=driver.findElements(By.xpath("//form/table/tbody/tr/td/table/tbody/tr"));
		Log.info("the row count of GatewayMappingList"  +rowCount);
		int list1 = rowCount.size();
		Log.info("The gateway mapping list size is" +list1);
		int i;

		for( i=1; i<list1;i++){
			Log.info("User is trying to find " +gatewayCode+ "gatewayCode in list");


			if(driver.findElement(By.xpath("//form/table/tbody/tr/td/table/tbody/tr["+i+"]/td[2]")).getText().equals(gatewayCode))
			{
				Log.info("Gateway Code : "+gatewayCode+ "found.");
				System.out.println("The particular rownum is "+i);
				break;
			}

		}
		WebElement responseGateway= driver.findElement(By.name("reqGatewayListIndexed["+(i-1)+"].responseGatewayCode"));
        Log.info("Trying to select Response gateway");
		Select mapping = new Select(responseGateway);
		mapping.selectByValue(PretupsI.GATEWAY_TYPE_WEB);
		Log.info("The Response Gateway selected as:" +gatewayCode);


	}



}
