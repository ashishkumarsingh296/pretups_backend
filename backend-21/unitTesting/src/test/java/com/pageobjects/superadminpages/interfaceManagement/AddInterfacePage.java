package com.pageobjects.superadminpages.interfaceManagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddInterfacePage {
	
	@FindBy (name = "interfaceTypeId")
	private WebElement  interfaceTypeId;
	
	@FindBy(name = "interfaceDescription")
	private WebElement interfaceName;
	
	
	@FindBy(name = "externalId")
	private WebElement externalId;
	
	@FindBy(name = "noOfNodes")
	private WebElement noOfNodes;
	
	@FindBy(name = "valExpiryTime")
	private WebElement valExpiryTime;
	
	@FindBy(name = "topUpExpiryTime")
	private WebElement topUpExpiryTime;
	
	@FindBy(name ="language1Message")
	private WebElement language1Message;
	
	@FindBy(name ="language2Message")
	private WebElement language2Message;
	
	@FindBy(name ="statusCode")
	private WebElement statusCode;
	
	@FindBy(xpath = "//a[@href[contains(.,'assignNodeDetails')]]")
	private WebElement setUpIPNodes;
	
	@FindBy(name = "addInterface")
	private WebElement addInterfaceButton;
	
	WebDriver driver;
	
	public AddInterfacePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void selectInterfaceType(String interfaceType){
		
		Select select = new Select(interfaceTypeId);
		select.selectByValue(interfaceType);
		
		Log.info("User selected LookUpName: ["+interfaceType+"]");
	}
	
	
public String selectInterfaceType(int index){
		
		Select select = new Select(interfaceTypeId);
		select.selectByIndex(index);
		
		String type= select.getFirstSelectedOption().getText().trim();
		System.out.println(type);
		
		Log.info("User selected LookUpName: ["+type+"]");
		
		
		
		return type;
	}
	
	public void enterInterfaceName(String name){
		
		interfaceName.sendKeys(name);
		Log.info("User entered interface Name as:" +name);
	}
	
	

public void enterexternalId(String extId){
		
	externalId.sendKeys(extId);;
		Log.info("User entered external Id as:" +extId);
	}



public void enternoOfNodes(){
	
	noOfNodes.sendKeys("1");;
		Log.info("User entered no. of nodes as 2" );
	}

public void entervalExpiryTime(){
	
	valExpiryTime.sendKeys("60000");;
		Log.info("User entered valExpiryTime as 60000 milliseconds");
	}


public void entertopUpExpiryTime(){
	
	topUpExpiryTime.sendKeys("60000");;
		Log.info("User entered TopUpExpiryTime as 60000 milliseconds");
	}


public void enterlanguage1Message(){
	
	language1Message.sendKeys("RC Interface added");;
		Log.info("User entered message as:RC Interface added");
	}
	
	
public void enterlanguage2Message(){
	
	language2Message.sendKeys("Msg 2 RC Interface added");;
		Log.info("User entered message as:Msg 2 RC Interface added");
	}



public String selectStatus(String status) {
	Select select = new Select(statusCode);
	select.selectByValue(status);
	
	Log.info("User selected status as  ["+status+"]");
	
	return status;
}

public void clicksetUpIPNodes(){
	
	
	setUpIPNodes.click();
	
	Log.info("Clicked set up Ip Nodes link");
}


public void clickaddInterfaceButton(){
	
	addInterfaceButton.click();
}

}
