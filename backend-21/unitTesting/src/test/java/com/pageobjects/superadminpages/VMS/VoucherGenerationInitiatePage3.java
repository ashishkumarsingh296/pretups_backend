package com.pageobjects.superadminpages.VMS;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class VoucherGenerationInitiatePage3 {

	WebDriver driver = null;
	public VoucherGenerationInitiatePage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "slabsListIndexed[0].productid" )
	private WebElement productid;
	
	@FindBy(name = "slabsListIndexed[1].productid" )
	private WebElement productid1;
	
	@FindBy(name = "submitOrderInit" )
	private WebElement Submit;
	
	@FindBy(name = "back" )
	private WebElement back;
	
	
	
	public void SelectProduct(String value){
		Log.info("Trying to Select Product");
		Select select = new Select(productid);
		select.selectByVisibleText(value);
		Log.info("Product selected  successfully as:"+ value);
		}
	
	public void SelectProduct2(String value){
		Log.info("Trying to Select Product");
		Select select = new Select(productid1);
		select.selectByVisibleText(value);
		Log.info("Product selected  successfully as:"+ value);
		}
	
	public boolean availabilityofProduct(String value){
		Log.info("Trying to Select Product");
		List<String> values = new ArrayList<String>();
		String text="";
		Select select = new Select(productid);
		List<WebElement> e= select.getOptions();
		for(WebElement ele : e) {
			text=ele.getText();
			values.add(text);
		}
		boolean flag =values.contains(value);
		if(!flag)
		Log.info("Profile is not available "+ value);
		else {
			Log.info("Profile is available "+value);
		}
		return flag;
	}
		
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		Submit.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		back.click();
		Log.info("Clicked on Back Button successfully");
		}
	
	
}
