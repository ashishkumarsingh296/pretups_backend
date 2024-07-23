package com.pageobjects.superadminpages.interfaceManagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InterfaceListPage {


	@FindBy(name = "add")
	private WebElement addButton;

	@FindBy(name = "modify")
	private WebElement modifyButton;

	@FindBy(name = "delete")
	private WebElement deleteButton;

	@FindBy(name = "back")
	private WebElement backButton;

	WebDriver driver;

	public InterfaceListPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}


	public void clickAdd(){
		Log.info("User trying to click add button");

		addButton.click();
		Log.info("User clicked add button");
	}


	public void clickModify(){
		Log.info("User trying to click modify button");

		modifyButton.click();
		Log.info("User clicked modify button");
	}


	public void clickDelete(){
		Log.info("User trying to click delete button");

		deleteButton.click();
		Log.info("User clicked delete button");
	}


	public void clickBack(){
		Log.info("User trying to click back button");

		backButton.click();
		Log.info("User clicked back button");
	}


	public void SelectInterfaceID(String InterfaceID){

		Log.info("User is trying to select Interface ID" +InterfaceID);

		/*
		List  rowCount=(List) driver.findElements(By.xpath("//form/table/tbody/tr/td/table/tbody/tr"));
		Log.info("the row count of interfaceList"  +rowCount);
		int interfaceList = rowCount.size();
		Log.info("The interface list size is" +interfaceList);
		int i;

		for( i=1; i<interfaceList;i++){
			Log.info("User is trying to find " +InterfaceID+ "interface ID in list");

			if(driver.findElement(By.xpath("form/table/tbody/tr/td/table/tbody/tr["+i+"]/td[2]")).getText().equals(InterfaceID)){
				Log.info("Interface Name: "+InterfaceID+ "found.");
				System.out.println("The value of i is "+i);
				break;
			}

			}

		 */


		WebElement radioButton= driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+InterfaceID+"']/ancestor::tr[1]/td/input[@type='radio']"));

		

		if(!radioButton.isSelected()){

			radioButton.click();
			Log.info("Interface ID is selected");


		}
		else{
			Log.info("Interface ID is already selected");

		}

	}

}
