package angular.pageobjects.O2CReturn;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class O2CStockReturn {
	
	WebDriver driver = null;
    WebDriverWait wait = null;

    public O2CStockReturn(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait= new WebDriverWait(driver, 20);
    }
    
    public boolean isO2CHeadingVisible() {
    	try {
			WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='02c']")));

			if(expanded.isDisplayed())
				return true;
		}

		catch(Exception e) {
			return false;
		}

		return false;
	}
    
    public void clickO2CHeading() {
		Log.info("Trying clicking on O2C Heading");
		WebElement o2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='02c']")));
		o2cHeading.click();
		Log.info("User clicked O2C Heading Link.");
	}
    
    public void clickReturnHeading() {
		Log.info("Trying clicking on Return Heading");
		WebElement o2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@routerLink='/operatortochannel/O2cReturn']")));
		o2cHeading.click();
		Log.info("User clicked on Return Heading Link.");
	}
    
    public void enterAmount(String productName, String type) {
        Log.info("Trying to enter amount..");
        

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[@class='my-balance leftBottomPadding'])[1]"))) ; //wait for transfer details
		List<WebElement> Qty = driver.findElements(By.xpath("//input[@id='amountId']"));
		for(int countQty=1; countQty <= Qty.size(); countQty++){
			WebElement pNameOnFront = driver.findElement(By.xpath("(//label[@class='my-balance leftBottomPadding'])[" +countQty+ "]/parent::*//preceding-sibling::*//label")) ;
			if(!pNameOnFront.getText().equals(productName)) continue;
			WebElement qtyField = driver.findElement(By.xpath("(//input[@id='amountId'])[" +countQty+ "]")) ;
			WebElement balance = driver.findElement(By.xpath("(//label[@class='my-balance'])[" +countQty+ "]")) ;
			String productBalance = balance.getText();
//			String productShortCode = driver.findElement(By.xpath("(//label[@class='my-balance'])"+countQty+"]/parent::div//parent::div//parent::div[@formarrayname='amountRows']//div[1]//label[contains(@class, 'company')]")).getText();
			//.............................
			if(productBalance.equals("")) {qtyField.sendKeys(""); continue;}
			productBalance = productBalance.replace("₹","").replace(",","");
			String amnt;
			if(type.equals("largeAmount")) amnt = String.valueOf( (int) (Double.parseDouble(productBalance) + 1000) );
			else if(type.equals("simple")) amnt = String.valueOf( ((int)(Double.parseDouble(productBalance) * 0.001))>50	?	50	:	((int)(Double.parseDouble(productBalance) * 0.001)) );
			else if(type.equals("zero")) amnt = "0";
			else amnt = "ab124";
			qtyField.sendKeys(amnt);				
		}
        Log.info("User entered amount");
    }

	public void enterRemark() {
		// TODO Auto-generated method stub
		Log.info("entering remark");
		WebElement remark = wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//textarea"))));
		remark.sendKeys("remarks entered for o2c return");
		Log.info("remarks entered");		
	}
    
	public void clickReturnButtton() {
		Log.info("Clicking on return button");
		
		WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//button[@id='purchaseO2c']"))));
		btn.click();
		
		Log.info("Clicked return button");
		
	}

	public void enterPin(String pin, String type) {
		Log.info("Entering Pin");
		
		WebElement pinArea = wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//input[@id='no-partitioned']"))));
		pinArea.sendKeys(pin);
		
		if(type.equals("next")){
			WebElement retBtn = wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//button[@id='returnId']"))));
			retBtn.click();
			Log.info("Entered pin "+ pin);
		}
		else {
			WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//button[@id='closingImg']"))));
			btn.click();
			Log.info("Entered pin "+ pin + "and clicked on cross");
		}
	}
	
	
	public boolean isSuccess() {
		Log.info("Checking if O2C return is successful");
		boolean result = false;
		String transacId=""; 
		WebElement successPopUP= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@class='success']")));
		if (successPopUP.isDisplayed()) {
				result = true;
				WebElement label= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//b")));
				transacId = label.getText();
				Log.info("Success Popup is visible.");
		}
		else {
			Log.info("Success popup is not visible");
		}
		
		if(!result)	Log.info("O2c return failed");
		else Log.info("O2C return is successful and transaction Id is "+transacId);
		
		WebElement doneBtn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='doneId']")));
		doneBtn.click();
		
		return result;
		
	}
	
	public String isEmptyAmountValidated() {
		String text = "";
		
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@id='amountId']/following-sibling::*)"))) ; //wait for transfer details
		List<WebElement> Qty = driver.findElements(By.xpath("(//input[@id='amountId']/following-sibling::*)"));
		for(int countQty=1; countQty <= Qty.size(); countQty++){
			WebElement validate= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@id='amountId']/following-sibling::*)["+countQty+"]")));
			if(validate.isDisplayed()) {
				text = validate.getText();
			}
			
		}
		
		return text;
	}
	
	public String isEmptyRemarkValidated() {
		String text = "";
		WebElement validate= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'invalid-feedback')]//div")));
		if(validate.isDisplayed()) {
			text = validate.getText();
			return text;
		}
		return text;
	}
	
	public String isUnSuccessful() {
		Log.info("Checking if O2C return failed");
		String text = "";
		WebElement failurePopUP= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'recharge-failure')]")));
		if (failurePopUP.isDisplayed()) {
				WebElement label= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@id,'failuremsg')]")));
				text = label.getText();
				Log.info("Failure Popup is visible.");
				Log.info("O2c return failed");
		}
		else {
			Log.info("Failure popup is not visible");
		}
				
		return text;
	}
	
	public void clickResetButtton() {
		Log.info("Clicking on reset button");
		
		WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//button[@id='reset']"))));
		btn.click();
		
		Log.info("Clicked reset button");
		
	}
	
	public boolean checkIfFeildsEmpty(String productName) {
		boolean res = true;
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[@class='my-balance'])[1]"))) ; //wait for transfer details
		List<WebElement> Qty = driver.findElements(By.xpath("//input[@id='amountId']"));
		for(int countQty=1; countQty <= Qty.size(); countQty++){
			WebElement pNameOnFront = driver.findElement(By.xpath("(//label[@class='my-balance'])[" +countQty+ "]/parent::*//preceding-sibling::*//label")) ;
			if(!pNameOnFront.getText().equals(productName)) continue;
			WebElement qtyFeild = driver.findElement(By.xpath("(//input[@id='amountId'])[" +countQty+ "]")) ;
			if(!qtyFeild.getText().equals("")) {res=false; break;}				
		}
		
		WebElement remark = wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//textarea"))));
		String s = remark.getText().trim();
		if(!s.equals("")) res=false;	
		
		return res;
	}
	
	public boolean checkIfPinEmpty() {
		boolean res = true;
		
		clickReturnButtton();
		WebElement pinArea = wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//input[@id='no-partitioned']"))));
		String text = pinArea.getText();
		if(!text.contentEquals(""))res = false;

		return res;
	}
	
}
