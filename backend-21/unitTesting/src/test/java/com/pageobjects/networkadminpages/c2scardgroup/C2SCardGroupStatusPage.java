package com.pageobjects.networkadminpages.c2scardgroup;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2SCardGroupStatusPage {

	WebDriver driver= null;
	public C2SCardGroupStatusPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	

	
	public void checkC2SCardGroup(String cardGroupName){
		
		
		
		//ArrayList<WebElement> cardGroupSet = new ArrayList<WebElement>();
		
		/*//cardGroupSet = (ArrayList<WebElement>) driver.findElements(By.xpath("//input[@name=(contains.,'cardGroupSetNameListIndexed')]"));
		cardGroupSet = (ArrayList<WebElement>) driver.findElements(By.xpath("//form/table/tbody/tr/td/table"));
		
		int tableSize= cardGroupSet.size();
		
		for(int i=0; i<tableSize; i++){
			
			if(driver.findElement(By.xpath("//form/table/tbody/tr/td/table/tbody/tr["+i+"]/td[1]")).getText().equals(cardGroupName)){
			//if(driver.findElement(By.xpath("//table/tbody/tr[2]/td[2]/form/table/tbody/tr/td/table/tbody/tr["+ i +"]/td[1]")).getText().equals(cardGroupName)){
				
				
				Log.info("Card Group" +cardGroupName + "found");*/
				
				WebElement chkBox= driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+cardGroupName+"']/following::input[@type='checkbox']"));
				
				if(!chkBox.isSelected()){
				
					chkBox.click();
					Log.info("Card Group Status checkbox is now selected");
					
				
				}
				else{
					Log.info("Card Group Status is already active");
					
				}
				
				
			}
	
	
	public void suspendC2SCardGroup(String cardGroupName){
		
		
List  rowCount=(List) driver.findElements(By.xpath("form/table/tbody/tr/td/table/tbody/tr"));
		
		int cardGroupTable = rowCount.size();

		int i;
		
		for( i=1; i<cardGroupTable;i++){
			if(driver.findElement(By.xpath("//form/table/tbody/tr/td/table/tbody/tr["+i+"]/td[1]")).getText().equals(cardGroupName)){
				Log.info("Profie Name: "+cardGroupName+ "found.");
				System.out.println("The value of i is "+i);
				break;
			}
			
		
		WebElement chkBox= driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+cardGroupName+"']/following::input[@type='checkbox']"));
		
		WebElement  lang1= driver.findElement(By.xpath("//form/table/tbody/tr/td/table/tbody/tr["+i+"]/td[6]"));
		WebElement  lang2= driver.findElement(By.xpath("//form/table/tbody/tr/td/table/tbody/tr["+i+"]/td[7]"));
		
		
		
		if (chkBox.isSelected()){
			
			chkBox.click();
			
			Log.info("CardGroup is suspended");
			
			lang1.sendKeys("The Commission Profile" +cardGroupName+ "is now suspended");
			lang2.sendKeys("The Commission Profile" +cardGroupName+ "is now suspended");


			
						
		}
		else
		{
			Log.info("The card group is already suspended");
		}
		}
		
	}
		
		
public void resumeC2SCardGroup(String cardGroupName){
		
		WebElement chkBox= driver.findElement(By.xpath("//table/tbody/tr/td[text()='"+cardGroupName+"']/following::input[@type='checkbox']"));
		
		if (!chkBox.isSelected()){
			
			chkBox.click();
			
			Log.info("CardGroup is resumed");
			
			
		}
		else
		{
			Log.info("Card Group is already active");
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

