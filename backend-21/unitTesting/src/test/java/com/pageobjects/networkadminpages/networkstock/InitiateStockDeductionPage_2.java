/**
 * 
 */
package com.pageobjects.networkadminpages.networkstock;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.classes.CONSTANT;
import com.utils.Log;
import com.utils.RandomGeneration;

/**
 * @author krishan.chawla
 * This class Contains the Page Objects for Initiate Network Stock
 **/

public class InitiateStockDeductionPage_2 {
	
	WebDriver driver= null;
	RandomGeneration RandomNum = new RandomGeneration();
	List<WebElement> productListSize;
	
	@FindBy(name = "referenceNumber")
	private WebElement RefNum;
		
	@FindBy(name = "remarks")
	private WebElement  remarks;
	
	@FindBy(name = "btnSubHome")
	private WebElement initiateDeductionSubmitBtn;
	
	//Initializing Elements dynamically according to listSize
	public int inputProductsAmount(int stockInitiateAmount) {
		int totalStockInititated = 0;
		productListSize = driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.requestedQuantity')]]"));
		Log.info("Number of Products found as: "+productListSize.size());
		for (int i=0;i<productListSize.size();i++){
			driver.findElement(By.xpath("//input[@type='text' and @name[contains(.,'["+i+"].requestedQuantity')]]")).sendKeys(""+stockInitiateAmount);
			totalStockInititated = totalStockInititated + stockInitiateAmount;
		}
		Log.info("Entered Amount as: "+stockInitiateAmount+" for all products and application returned "+totalStockInititated+" as total initiated amount");
		return totalStockInititated;
	}
	
	public void fetchproductPreBalances(String MultiWalletPreference) {
		Log.info("Trying to Fetch available balances of all products for further calculations");
		CONSTANT.NETWORKSTOCKPREBALANCES = new double[productListSize.size()];
		for (int i=0;i<productListSize.size();i++){
			if (MultiWalletPreference.equals("true")) {
			CONSTANT.NETWORKSTOCKPREBALANCES[i] = Double.parseDouble(driver.findElement(By.xpath("//input[@type='text' and @name[contains(.,'stockProductIndexed["+i+"].requestedQuantity')]]/ancestor::tr[*]/td[6]")).getText());
			}
			else {
			CONSTANT.NETWORKSTOCKPREBALANCES[i] = Double.parseDouble(driver.findElement(By.xpath("//input[@type='text' and @name[contains(.,'stockProductIndexed["+i+"].requestedQuantity')]]/ancestor::tr[*]/td[5]")).getText());	
			}
		}
		Log.info("Products balance information fetched successfully");
	}
	
	public void fetchproductPostBalances(String MultiWalletPreference) {
		Log.info("Trying to fetch Post Balances of products");
		CONSTANT.NETWORKSTOCKPOSTBALANCES = new double[productListSize.size()];
		//List<WebElement> productListSize = driver.findElements(By.xpath("//input[@type='text' and @name[contains(.,'.requestedQuantity')]]"));
		for (int i=0;i<productListSize.size();i++){
			if (MultiWalletPreference.equals("true"))
			CONSTANT.NETWORKSTOCKPOSTBALANCES[i] = Double.parseDouble(driver.findElement(By.xpath("//input[@type='text' and @name[contains(.,'["+i+"].requestedQuantity')]]/ancestor::tr[*]/td[6]")).getText());
			else
			CONSTANT.NETWORKSTOCKPOSTBALANCES[i] = Double.parseDouble(driver.findElement(By.xpath("//input[@type='text' and @name[contains(.,'["+i+"].requestedQuantity')]]/ancestor::tr[*]/td[5]")).getText());
		}
		Log.info("Post Balances fetched successfully");
	}
	
	public boolean ComparePostStocks(int RequestedQuantity) {
		Log.info("Trying to Compare Post Balance of Products with Pre Balance");
		int j=0;
		boolean result = false;
		for (int i=0;i<productListSize.size();i++){
			double PreBalanceVal = CONSTANT.NETWORKSTOCKPREBALANCES[i];
			double RequestedAmount=Double.parseDouble(""+RequestedQuantity);
			double PreBalancePredictedVal = PreBalanceVal + RequestedAmount;
			double PostBalanceVal = CONSTANT.NETWORKSTOCKPOSTBALANCES[i];
			Log.info("Pre Balance Fetched as: " + PreBalanceVal + " & Post Balance Fetched as: " + PostBalanceVal);
			if (PostBalanceVal == PreBalancePredictedVal){
				Log.info("Comparing Post Balance: " + PostBalanceVal + " with Predicted Post Balance: " + PreBalancePredictedVal);
				j=j+1;
			}	
		}
				if(j == productListSize.size())
				{
					result = true;
				}
				else result = false;
				return result;
	}
	
	public InitiateStockDeductionPage_2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void inputRandomRefNum() {
		Log.info("Trying to enter Random Reference Number");
		RefNum.sendKeys(""+RandomNum.randomNumeric(8));
		Log.info("Reference Number entered successfully");
	}
	
	public void enterRemarks() {
		Log.info("Trying to enter Remarks");
		try {
			remarks.sendKeys("Automated Network Stock Creation");
			Log.info("Remarks entered successfully");
		}
		catch (NoSuchElementException e)
		{ Log.writeStackTrace(e); }
		catch (Exception e)
		{ Log.writeStackTrace(e); }
	}
	
	public void clickInitiateStock() {
		Log.info("Trying to click Submit Button");
		initiateDeductionSubmitBtn.click();
		Log.info("Clicked Submit Button successfully");
	}

}
