package com.pageobjects.networkadminpages.commissionprofile;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewCommissionProfilePage2 {

	@FindBy(xpath = "//tr/td[text() = '1']/ancestor::tr/td/input[@type='radio']")
	private WebElement selectCommProfileSetVersion;


	@ FindBy(name = "view")
	private WebElement submit;

	@ FindBy(name = "back")
	private WebElement backButton;

	WebDriver driver = null;

	public ViewCommissionProfilePage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void SelectCommProfileVersion(){
		selectCommProfileSetVersion.click();
		Log.info("User selected Comm Profile Version");
	}

	public void clickSubmit(){
		submit.click();
		Log.info("User clicked Submit button");

	}





	public String getCommissionType(String version) {

		String CommissionType=null;
		Log.info("Trying to get Commission Type for Version");
		List  rowCount=(List) driver.findElements(By.xpath("//form/table/tbody/tr/td/table[2]//tr"));
		System.out.println("rowCount is:" +rowCount.size());
		int versiontable = rowCount.size();
		int i;
		boolean isFound = false;

		for( i=1; i<=versiontable;i++)
		{
			if(driver.findElement(By.xpath("//form/table/tbody/tr/td/table[2]//tr[" +i+ "]/td[3]")).getText().equals(version)){
				isFound = true;
				Log.info("Version: "+version+ "found.");
				System.out.println("The value of i is "+i);
				break;
			}
			
			
		}
		
		if (isFound){
			WebElement  CommType = driver.findElement(By.xpath("//form/table/tbody/tr/td/table[2]//tr["+i+"]/td[2]"));
			CommissionType = CommType.getText();
			System.out.println(CommissionType);
		}else{
			System.out.println("Fail Could not Find Commision type for Version: "+ version);
		}
		
		return CommissionType;
	}


}
