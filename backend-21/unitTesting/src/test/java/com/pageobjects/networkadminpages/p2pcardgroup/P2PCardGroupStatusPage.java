package com.pageobjects.networkadminpages.p2pcardgroup;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class P2PCardGroupStatusPage {

	WebDriver driver= null;
	public P2PCardGroupStatusPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}




	public void checkP2PCardGroup(String cardGroupName){



		WebElement chkBox= driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+cardGroupName+"']/following::input[@type='checkbox']"));

		if(!chkBox.isSelected()){

			chkBox.click();
			Log.info("Card Group Status checkbox is now selected");


		}
		else{
			Log.info("Card Group Status is already active");

		}


	}


	public void suspendP2PCardGroup(String cardGroupName){

		WebElement chkBox= driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+cardGroupName+"']/following::input[@type='checkbox']"));

		if (chkBox.isSelected()){

			chkBox.click();

			Log.info("CardGroup is suspended");
			
			//


		}
		else {
			Log.info("card Group is already suspended");
		}

	}


	public void resumeP2PCardGroup(String cardGroupName){

		WebElement chkBox= driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+cardGroupName+"']/following::input[@type='checkbox']"));

		if (!chkBox.isSelected()){

			chkBox.click();

			Log.info("CardGroup is resumed");


		}

	}


	
	
	public void CardGroupDeactivateNegative(String cardGroupName,String lang1message, String lang2message) {
		System.out.println("entering method");

		String Status=null;
		//ArrayList  rowCount= (ArrayList)driver.findElements(By.tagName("tr"));
		List  rowCount=(List) driver.findElements(By.xpath("//input[@type='checkbox']"));
		int cardGrouptable = rowCount.size();
		int i;
		
		for( i=1; i<cardGrouptable;i++){
			if(driver.findElement(By.xpath("//input[@type='checkbox'][@name='cardGroupSetNameListIndexed["+i+"].status']/../../td[1]")).getText().equals(cardGroupName)){
				Log.info("Profie Name: "+cardGroupName+ "found.");
				System.out.println("The value of i is "+i);
				
			}
			break;
		}
			
			
			WebElement  lang1= driver.findElement(By.xpath("//td[text()='"+cardGroupName+"']/following-sibling::td/input[@type='checkbox']/../following-sibling::td[1]/textarea"));
			WebElement  lang2= driver.findElement(By.xpath("//td[text()='"+cardGroupName+"']/following-sibling::td/input[@type='checkbox']/../following-sibling::td[2]/textarea"));
			WebElement chkBox= driver.findElement(By.xpath("//tr/td[contains(.,'"+ cardGroupName +"')]/../td/input[@type='checkbox']"));
			

			if(!chkBox.isSelected()){
				Log.info("Card Group is already deactivated");
				//lang1.sendKeys("The Commission Profile" +CommProfile+ "is now deactivated");
				//lang2.sendKeys("The Commission Profile" +CommProfile+ "is now deactivated");
				Status = "Card Group is already deactivated";	
				
			} else {
				chkBox.click();
				Log.info("Trying to deactivate CardGroup");
				
				lang1.sendKeys(lang1message);
				lang2.sendKeys(lang2message);
				
				Log.info("Deactivated CardGroup");
				
				
			}
		}
		
	
	
	
}

