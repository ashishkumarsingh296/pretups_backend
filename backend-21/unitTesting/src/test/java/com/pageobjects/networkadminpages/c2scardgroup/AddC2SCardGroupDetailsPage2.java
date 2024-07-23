package com.pageobjects.networkadminpages.c2scardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AddC2SCardGroupDetailsPage2 {
	@ FindBy(name = "confirm")
	private WebElement confirmButton;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;

	@ FindBy(name = "cancel")
	private WebElement cancelButton;

	@ FindBy(name = "back")
	private WebElement backButton;

	WebDriver driver= null;
	
	public AddC2SCardGroupDetailsPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickConfirmbutton() {
		confirmButton.click();
		Log.info("User clicked Confirm Button.");		
	}

	public void clickCancelButton() {
		cancelButton.click();
		Log.info("User clicked Cancel Button.");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back Button.");
	}
	
	public int getVersionIdFromMessage(){
		Log.info("Trying to fetch Version Id");
		
		String msg= message.getText();
		Log.info("The message is :" +msg);
 		
		int newVersion=Integer.parseInt(msg.replaceAll("[^0-9]",""));
		System.out.println(newVersion);
		Log.info("The modified version id is" + newVersion);
		return newVersion;
		/*
		String msg= message.getText();
		
		Log.info("The message displayed is : " +msg);
		
		int curr = Integer.parseInt(currentVersion);
		
		int expectedVersion= curr+1;
		
		int index =message.getText().indexOf(expectedVersion);
		String [] foundVersionId=message.getText().split(" ",index);
		
		int ff= Integer.parseInt(foundVersionId);
		
		//String foundVersionId= Integer.toString(x);		
		
		return ff;
	 */
		
	/*
		 int curr1 = Integer.parseInt(currentVersion);
		int expectedVersion1= currentVersion1+1;
		System.out.println("My expected  version==="+expectedVersion1);
        int newVersion=0; 
		String s=Integer.toString(expectedVersion1);
        int  xx=msg.indexOf(s);
        
        System.out.println("New Character==="+xx);
        char cha=msg.charAt(xx);
        int ascii=cha;
        
        for(int i=0;i<msg.length();i++){
               if(msg.charAt(i)==ascii){
            	   
                     System.out.println(msg.charAt(i));
        
               String str= Character.toString(msg.charAt(i));
               newVersion=Integer.parseInt(str);
               }
	}
        Log.info("The message is:" + message.getText());
        Log.info("The new version is:" +newVersion);

        return newVersion;
        */
		
 }

		
		
		
	
	
	
	

		

	
}
