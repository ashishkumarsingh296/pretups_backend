package angular.pageobjects.O2CWithdrawal;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class O2CWithdrawal {
	
	WebDriver driver = null;
    WebDriverWait wait = null;
    
    public O2CWithdrawal(WebDriver driver) {
    	this.driver = driver;
        PageFactory.initElements(driver, this);
        wait= new WebDriverWait(driver, 20);
    }
    
    public boolean isO2CVisible() {
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='toggle-2-button']")));
    	try {
			WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='o2c']//span[@class='childmenucss']")));

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
		WebElement o2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='o2c']")));
		o2cHeading.click();
		Log.info("User clicked O2C Heading Link.");
	}
    public void clickO2CTransactionHeading() {
        Log.info("Trying clicking on O2C Transaction Heading");
        WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= 'o2ctransaction']")));
        o2cTransactionHeading.click();
        Log.info("User clicked O2C Transaction Heading Link.");
    }
    public void clickWithdrawHeading() {
		Log.info("Trying clicking on O2C Withdraw Heading");
	    WebElement o2cWithdrawHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@routerLink='/operatortochannel/O2cWithdraw']")));
	    o2cWithdrawHeading.click();
	    Log.info("User clicked O2C Withdraw Heading Link.");
	}
    public void selectOperatorWallet(String walletType) {
		Log.info("Trying to select operator wallet dropdown");
	    WebElement operatorWalletDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='walletType']")));
	    operatorWalletDropdown.click();
	    String dp = String.format("//div[@role='option']//span[contains(text(),'%s')]", walletType);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dp)));
		driver.findElement(By.xpath(dp)).click() ;
	    Log.info("User selected operator wallet type " +  walletType);
	}
    public String getOperatorWalletError() {
		Log.info("Trying to fetch operator wallet dropdown error");
	    WebElement operatorWalletDropdownError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='walletType']/following-sibling::div/div")));
	    String operatorWalletDropdownErrorMessage = operatorWalletDropdownError.getText();
	    return operatorWalletDropdownErrorMessage;
	}
    public void selectSearchByCriteria(String searchBy) {
		Log.info("Trying to select search by dropdown");
	    WebElement searchByDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='searchCriteriaSelect']")));
	    searchByDropdown.click();
	    String dp = String.format("//div[@role='option']//span[contains(text(),'%s')]", searchBy);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dp)));
		driver.findElement(By.xpath(dp)).click() ;
	    Log.info("User selected search by " +  searchBy);
	}
    public String getSearchByCriteriaError() {
		Log.info("Trying to fetch search by dropdown error");
	    WebElement searchByDropdownError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='searchCriteriaSelect']/following-sibling::div/div")));
	    String searchByDropdownErrorMessage = searchByDropdownError.getText();
	    return searchByDropdownErrorMessage;
	}
    public void enterMsisdn(String msisdn) {
		Log.info("Trying to Enter Msisdn");
		WebElement inputMsisdn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchInput']")));
		inputMsisdn.sendKeys(msisdn);
		Log.info("User Entered msisdn " + msisdn);
	}
    public String getMsisdnError() {
		Log.info("Trying to fetch mobile number error");
	    WebElement msisdnError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchInput']/following-sibling::div/div")));
	    String msisdnErrorMessage = msisdnError.getText();
	    return msisdnErrorMessage;
	}
    public void selectGeography(String geography) {
		Log.info("Trying to select geography dropdown");
	    WebElement geographyDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='geographyProceed']")));
	    geographyDropdown.click();
	    String dp = String.format("//div[@role='option']//span[contains(text(),'%s')]", geography);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dp)));
		driver.findElement(By.xpath(dp)).click() ;
	    Log.info("User selected geography: " +  geography);
	}
    public String getGeographyError() {
		Log.info("Trying to fetch geography dropdown error");
	    WebElement geographyDropdownError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='geographyProceed']/following-sibling::div/div")));
	    String geographyDropdownErrorMessage = geographyDropdownError.getText();
	    return geographyDropdownErrorMessage;
	}
    public void selectDomain(String domain) {
		Log.info("Trying to select domain dropdown");
	    WebElement domainDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='domainProceed']")));
	    domainDropdown.click();
	    String dp = String.format("//div[@role='option']//span[contains(text(),'%s')]", domain);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dp)));
		driver.findElement(By.xpath(dp)).click() ;
	    Log.info("User selected domain: " +  domain);
	}
    public String getDomainError() {
		Log.info("Trying to fetch domain dropdown error");
	    WebElement domainDropdownError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='domainProceed']/following-sibling::div/div")));
	    String domainDropdownErrorMessage = domainDropdownError.getText();
	    return domainDropdownErrorMessage;
	}
    public void selectCategory(String chUserCategory) {
		Log.info("Trying to select category dropdown");
	    WebElement domainDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='categorySelect']")));
	    domainDropdown.click();
	    String dp = String.format("//div[@role='option']//span[contains(text(),'%s')]", chUserCategory);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dp)));
		driver.findElement(By.xpath(dp)).click() ;
	    Log.info("User selected channel user category: " +  chUserCategory);
	}
    public String getCategoryError() {
		Log.info("Trying to fetch category dropdown error");
	    WebElement categoryDropdownError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='categorySelect']/following-sibling::div/div")));
	    String categoryDropdownErrorMessage = categoryDropdownError.getText();
	    return categoryDropdownErrorMessage;
	}
    public void enterChUserName(String chUserName) {
		Log.info("Trying to Enter channel user name");
		WebElement inputUserName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='searchCriteriaInput']")));
		inputUserName.sendKeys(chUserName);
		Log.info("User Entered channel user name: " + chUserName);
	}
    public String getChUserNameError() {
		Log.info("Trying to fetch channel user name input error");
	    WebElement chUserNameError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='searchCriteriaInput']/parent::*/following-sibling::div/div")));
	    String chUserNameErrorMessage = chUserNameError.getText();
	    return chUserNameErrorMessage;
	}
    public void enterLoginId(String chLoginId) {
		Log.info("Trying to Enter channel user Login Id");
		WebElement inputLoginId = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchInput']")));
		inputLoginId.sendKeys(chLoginId);
		Log.info("User Entered channel user Login Id: " + chLoginId);
	}
    public String getLoginIdError() {
		Log.info("Trying to fetch login Id error");
	    WebElement loginIdError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchInput']/following-sibling::div/div")));
	    String loginIdErrorMessage = loginIdError.getText();
	    return loginIdErrorMessage;
	}
    public void clickProceedButton() {
    	Log.info("Trying clicking on Proceed button");
    	WebElement proceed = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='proceedButton']")));
    	proceed.click();
    	Log.info("User Clicked proceed button");
    }
    public void enterAmount(String productName, String type) {
        Log.info("Trying to enter amount for " + productName);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[@class='my-balance'])[1]"))) ; //wait for transfer details
		List<WebElement> Qty = driver.findElements(By.xpath("//div[contains(@class , 'amountP')]"));
		for(int countQty=1; countQty <= Qty.size(); countQty++){
			WebElement pNameOnFront = driver.findElement(By.xpath("(//label[@class='company marginPname'])[" +countQty+ "]")) ;
			if(!pNameOnFront.getText().equals(productName)) continue;
			WebElement qtyField = driver.findElement(By.xpath("(//div[contains(@class,'amountP')]/input)[" +countQty+ "]")) ;
			WebElement balance = driver.findElement(By.xpath("(//label[@class='my-balance'])[" +countQty+ "]")) ;
			String productBalance = balance.getText();
			Log.info("Current balance: " + productBalance);
			if(productBalance.equals("")) {
				Log.info("User entered amount: ");
				qtyField.sendKeys("");
				continue;
			}
			productBalance = productBalance.replace("₹","").replace(",","");
			String amnt;
			if(type.equals("largeAmount")) amnt = String.valueOf((int)(Double.parseDouble(productBalance) + 1000));
			else if(type.equals("simple")) {
				amnt = String.valueOf( ((int)(Double.parseDouble(productBalance) * 0.001))>50?50:((int)(Double.parseDouble(productBalance) * 0.001)) );
			}
			else if(type.equals("zero")) amnt = "0";
			else amnt = "ab124";
			Log.info("User entered amount: " + amnt);
			qtyField.sendKeys(amnt);				
		}
        Log.info("User entered amount for " + productName);
    }
    public void enterRemarks(String property) {	
		Log.info("Trying to enter remarks");
		WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='textBox']")));
		enter.sendKeys(property);
		Log.info("User entered Remarks");
	}
    public void clickWithdrawButton() {
    	Log.info("Trying Click Withdraw Button");
    	WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='purchaseO2c']")));
    	enter.click();
    	Log.info("User Clicked Withdraw Button");
    }
    public void enterPIN(String PIN){
    	Log.info("Trying to Enter PIN for O2C");
    	WebElement pinInput=  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='no-partitioned']")));
    	pinInput.sendKeys(PIN);
    	Log.info("User entered PIN: "+PIN);
    }
    public String checkPinFeild(){
    	Log.info("Trying to check if pin feild is prefilled");
    	WebElement pinInput=  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='no-partitioned']")));
    	String inputText = pinInput.getText();
    	Log.info("Value in pin feild: "+ inputText);
    	return inputText;
    }
    public void clickWithdrawButtonPopup() {
    	Log.info("Trying to click withdraw button in popup.");
    	WebElement enter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='divOuter']/parent::*/following-sibling::*/button")));
    	enter.click();
    	Log.info("User clicked withdraw button in popup");	
    }
    public boolean O2CWithdrawSuccessVisibility() {
    	boolean result = false;
    	try {
    		WebElement withdrawSuccess= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='successfultitle']")));
    		if (withdrawSuccess.isDisplayed()) {
    			result = true;
    			Log.info("Success PopUP is visible.");
    		}
    	} catch (Exception e) {
    		result = false;
    		Log.info("Success Popup is not visible.");
    	}
    	return result;
    }
    public String getTransactionId(){
        Log.info("Trying to fetch Transaction ID..");
        WebElement o2cSuccessIDElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='labelpos1']/following-sibling::b"))) ;
        String o2cSuccessTransactionID = o2cSuccessIDElement.getText() ;
        Log.info("O2C SUCCESS TRANSACTION ID : "+o2cSuccessTransactionID);
        return o2cSuccessTransactionID;
    }
    public String O2CWithdrawFailure() {
    	String msgText = "";
    	WebElement withdrawFail= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modal-basic-title-fail']")));
    	if (withdrawFail.isDisplayed()) {
    		Log.info("Failure PopUP is visible.");
    		Log.info("Fetching failure msg");
			WebElement failureMsg= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@id,'failuremsg')]")));
			msgText = failureMsg.getText();
			return msgText;
    	}else {
    		Log.info("Failure PopUp is not visible");
    		return msgText;
    	}
    }
    public void clickClosePopup(){
        Log.info("Trying to click close popup button");
        WebElement closePopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class='close']"))) ;
        closePopup.click();
        Log.info("User clicked popup close button");
    }
    public String getRemarksError(){
        Log.info("Trying to fetch Remarks Error message");
        WebElement remarksError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id = 'textBox']/following-sibling::div")));
        String remarksErrorMessage = remarksError.getText();
        return remarksErrorMessage;
    }
    public String getAmountError(String productName){
        Log.info("Trying to fetch amount error message for " + productName);
        List<WebElement> Qty = driver.findElements(By.xpath("//div[contains(@class , 'amountP')]"));
        String amountErrorMessage = "";
		for(int countQty=1; countQty <= Qty.size(); countQty++){
			WebElement pNameOnFront = driver.findElement(By.xpath("(//label[@class='company marginPname'])[" +countQty+ "]")) ;
			if(!pNameOnFront.getText().equals(productName)) continue;
			WebElement amountError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'amountP')]/input)[" +countQty+ "]/following-sibling::div")));
			amountErrorMessage = amountError.getText();			
		}
        return amountErrorMessage;
    }
    public void clickWithdrawalDetailsReset(){
        Log.info("Trying to click withdrawal details reset button");
        WebElement resetBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='reset']"))) ;
        resetBtn.click();
        Log.info("User clicked withdrawal details reset button");
    }
    public boolean isRemarksEmpty(){
        Log.info("Trying to fetch Remarks");
        WebElement remarksInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id = 'textBox']")));
        String remarks = remarksInput.getText().trim();
        Log.info("Value obtained from Remarks: " + remarks);
        if(remarks.isEmpty()) {
        	Log.info("Remarks field is empty");
        	return true;
        }else {
        	Log.info("Remarks field is not empty");
        	return false;
        }
    }
    public boolean isAmountEmpty(){
        Log.info("Trying to fetch value in amount fields");
        List<WebElement> Qty = driver.findElements(By.xpath("//div[contains(@class , 'amountP')]"));
		for(int countQty=1; countQty <= Qty.size(); countQty++){
			WebElement amountInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'amountP')]/input)[" +countQty+ "]")));
			String amountInputFieldValue = amountInputField.getText().trim();
			Log.info("Value obtained from amount field " + countQty + ": " + amountInputFieldValue);
			if(!amountInputFieldValue.isEmpty()) {
				Log.info("Amount feild is not empty");
				return false;
			}
		}
		return true;
    }
    public String getUserNotFoundMessage() {
		Log.info("Waiting for message to change");
		boolean invisible = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@class , 'transfer-details-wil')]/img")));
		if(invisible) {
			Log.info("Trying to fetch user not found message");
			WebElement userNotFound = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class , 'transfer-details-wil')]")));
		    String userNotFoundMessage = userNotFound.getText();
		    return userNotFoundMessage;
		}else {
			WebElement userNotFound = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class , 'transfer-details-wil')]")));
		    String userNotFoundMessage = userNotFound.getText();
		    return userNotFoundMessage;
		}
	    
	}
    public String getOwnerCategoryError() {
		Log.info("Trying to fetch owner category dropdown error");
	    WebElement ownerCategoryDropdownError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='ownerProceed']/following-sibling::div/div")));
	    String ownerCategoryDropdownErrorMessage = ownerCategoryDropdownError.getText();
	    return ownerCategoryDropdownErrorMessage;
	}
    public String getChannelOwnerNameError() {
		Log.info("Trying to fetch channel owner name input error");
	    WebElement channelOwnerNameError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='channelOwnerName']/parent::*/following-sibling::div/div")));
	    String channelOwnerNameErrorMessage = channelOwnerNameError.getText();
	    return channelOwnerNameErrorMessage;
	}
    public void clickUserDetailsReset(){
        Log.info("Trying to click user details reset button");
        WebElement resetBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='resetButton1']"))) ;
        resetBtn.click();
        Log.info("User clicked user details reset button");
    }
    public boolean checkOperatorWalletReset(){
        Log.info("Trying to check operator wallet reset");
        WebElement operatorWalletDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='walletType']"))) ;
        List<WebElement> count = driver.findElements(By.xpath("//ng-select[@id='walletType']/div/div/div"));
        // if count size is 2 that means there are two div in ng-select content div and no value is selected
        if(count.size() == 2) {
        	return true;
        }else {
        	return false;
        }
    }
    public boolean checkSearchByReset(){
        Log.info("Trying to check search by reset");
        WebElement searchByDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='searchCriteriaSelect']"))) ;
        List<WebElement> count = driver.findElements(By.xpath("//ng-select[@id='searchCriteriaSelect']/div/div/div"));
        // if count size is 2 that means there are two div in ng-select content div and no value is selected
        if(count.size() == 2) {
        	return true;
        }else {
        	return false;
        }
    }
    public String checkSearchInputField(){
    	Log.info("Trying to check if search input field is prefilled");
    	WebElement searchInput=  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='searchInput']")));
    	String inputText = searchInput.getText().trim();
    	Log.info("Value in search input field: "+ inputText);
    	return inputText;
    }
    public boolean checkWithdrawDetailsAreaReset() {
		Log.info("Trying to check if withdraw details area is reset");
		WebElement withdrawDetailsImage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class , 'transfer-details-wil')]/img")));
		return withdrawDetailsImage.isDisplayed(); 
	}
    
   

}
