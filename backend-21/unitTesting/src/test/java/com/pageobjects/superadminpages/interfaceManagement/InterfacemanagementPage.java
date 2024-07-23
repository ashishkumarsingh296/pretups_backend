package com.pageobjects.superadminpages.interfaceManagement;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class InterfacemanagementPage {
	
	@FindBy(name = "interfaceCategoryCode")
	private WebElement InterfaceCatergory;
	
	@FindBy(name = "submit")
	private WebElement submitButton;
	
	@FindBy(xpath = "//ul/li")	
	private WebElement actualMessage;
	
	
WebDriver driver;
	
	public InterfacemanagementPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	
	public String selectInterfaceCatergory(String IntCategory) {
		Select select = new Select(InterfaceCatergory);
		select.selectByValue(IntCategory);
		
		Log.info("User selected Interface Category: ["+IntCategory+"]");
		
		return IntCategory;
	}
	
	
	public String selectInterfaceCatergory1(int index) {
		Select select = new Select(InterfaceCatergory);
		select.selectByIndex(index);
		
		Log.info("User selected Interface Category " );
	
		String category= select.getFirstSelectedOption().getText().trim();
		System.out.println(category);
		
		
	return category;	
		
	}
	
	
	public String getInterfaceCatCode(int index){
		Select dropdown = new Select(driver.findElement(By.name("interfaceCategoryCode")));

	List<WebElement> list = dropdown.getOptions();
	
	String category = list.get(index).getText();

    for(int i=0;i<list.size();i++){
        if(list.get(i).getText().equals(dropdown.getFirstSelectedOption().getText())){
            System.out.println("The index of the selected option is: "+i);
            break;
            }
    }
    
    return category;
	}
	
	
	
	public void clickSubmit(){
		Log.info("User trying to click submit button");
		
		submitButton.click();
		Log.info("User clicked submit button");
	}
	
	public String getMsg(){
		String msg = actualMessage.getText();
		Log.info("Actual Message fetched as " +msg);
		return msg;
	}

}
