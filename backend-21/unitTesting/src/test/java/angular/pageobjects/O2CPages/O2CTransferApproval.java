package angular.pageobjects.O2CPages;


import com.utils.Log;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;



public class O2CTransferApproval {

    @FindBy(xpath = "//div[@id='network-container']//span[@class='cdtspan']")
    private WebElement loginDateAndTime;

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor jsDriver;

    public O2CTransferApproval(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait= new WebDriverWait(driver, 30);
        jsDriver = (JavascriptExecutor)driver;
    }


      public void clickOPTO2CHeading() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        Log.info("Trying clicking on O2C Heading");
        WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= 'o2c']")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", o2cTransactionHeading);*/
        o2cTransactionHeading.click();
        Log.info("User clicked O2C Heading Link.");
    }

    public void clickOPTO2CTransactionHeading() {
        Log.info("Trying clicking on O2C Transaction Heading..");
        WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= 'o2ctransaction']")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", o2cTransactionHeading);*/
        o2cTransactionHeading.click();
        Log.info("User clicked O2C Transaction Heading.");
    }


    public void clickOPTO2CSingleOperationHeading() {
        Log.info("Trying clicking on O2C Single Operation Heading..");
        WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'singleToggle-button']")));
        o2cTransactionHeading.click();
        Log.info("User clicked O2C Single Operation Heading.");
    }


    public void clickCUO2CPurchaseHeading() {
        Log.info("Trying clicking on O2C Purchase Heading");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'ng-select-container']")));
        WebElement O2CPurchaseHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@routerlink = '/operatortochannel']//span")));
        O2CPurchaseHeading.click();
        Log.info("User clicked O2C Purchase Heading");
    }

    public void clickCUO2CHeading() {
        Log.info("Trying clicking on O2C Heading..");
        //wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname = 'msisdn']")));
        WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id = '02c']")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", o2cTransactionHeading);*/
        try {
            o2cTransactionHeading.click();
        }
        catch(ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", o2cTransactionHeading);
        }
        Log.info("User clicked O2C Heading.");
    }

    public boolean isO2CTransactionVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        try {
            WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class = 'nested box opened']//a[@id= 'o2ctransaction']")));
            if(expanded.isDisplayed()) {
                Log.info("Element is expanded");
                return true;
            }
        }
        catch(Exception e) {
            Log.info("Element is not expanded");
            return false;
        }
        return false;
    }


    public boolean isO2CApproval1Visible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try {
            WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class = 'nested box opened']//a[@id= 'approval1txn']")));
            if(expanded.isDisplayed()) {
                Log.info("Element is expanded");
                return true;
            }
        }
        catch(Exception e) {
            Log.info("Element is not expanded");
            return false;
        }
        return false;
    }


    public boolean isO2CApproval2Visible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try {
            WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class = 'nested box opened']//a[@id= 'approval2txn']")));
            if(expanded.isDisplayed()) {
                Log.info("Element is expanded");
                return true;
            }
        }
        catch(Exception e) {
            Log.info("Element is not expanded");
            return false;
        }
        return false;
    }


    public boolean isO2CApproval3Visible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try {
            WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class = 'nested box opened']//a[@id= 'approval3txn']")));
            if(expanded.isDisplayed()) {
                Log.info("Element is expanded");
                return true;
            }
        }
        catch(Exception e) {
            Log.info("Element is not expanded");
            return false;
        }
        return false;
    }

    public void clickO2CPurchaseHeading() {
        Log.info("Trying clicking on O2C Purchase Heading");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'ng-select-container']")));
        WebElement O2CPurchaseHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class = 'anchorActiveClass']//span")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", O2CPurchaseHeading);*/
        O2CPurchaseHeading.click();
        Log.info("User clicked O2C Purchase Heading");
    }


    public void clickO2CeTopUPHeading() {
        Log.info("Trying clicking on O2C eTopUP Heading");
        WebElement O2CeTopUPHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'stockToggle-button']")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", O2CeTopUPHeading);*/
        O2CeTopUPHeading.click();
        Log.info("User clicked O2C eTopUP Heading.");
    }



    public void enterAmount(String Amount) {
        Log.info("Trying to enter amount..");
        WebElement enterAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'row']//input[@class = 'form-control text-center ng-untouched ng-pristine ng-valid']")));
        enterAmount.sendKeys(Amount);
        Log.info("User entered amount: " + Amount);
    }


    public void enterReferenceNumber(String Reference)
    {
        Log.info("Trying to enter Reference Number..");
        WebElement enterReference = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'row']//input[@id = 'referenceNoInput']")));
        enterReference.sendKeys(Reference);
        Log.info("User entered Reference Number: " + Reference);
    }

    public void enterCUReferenceNumber(String Reference)
    {
        Log.info("Trying to enter Reference Number..");
        WebElement enterReference = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'row']//input[@id = 'referenceNumberInput']")));
        enterReference.sendKeys(Reference);
        Log.info("User entered Reference Number: " + Reference);
    }


    public void enterRemarks(String Remarks)
    {
        Log.info("Trying to enter Remarks..");
        WebElement enterRemarks = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id = 'textBox']")));
        enterRemarks.sendKeys(Remarks);
        Log.info("User entered Remarks: " + Remarks);
    }


    public void selectOPTPaymentMode(String PaymentMode) {
        try {
            Log.info("Trying to select Payment Mode");
            WebElement paymentmodedropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'paymentTypeSelect']//div")));
            paymentmodedropdown.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'ng-select-box-payment-default']//ng-dropdown-panel")));
            String paymentMode = String.format("//div[@id = 'ng-select-box-payment-default']//div//span[text()='%s']", PaymentMode);
            driver.findElement(By.xpath(paymentMode)).click();
            Log.info("Payment Mode selected successfully as: " + PaymentMode);
        } catch (Exception e) {
            Log.debug("<b>Payment Mode Type Not Found:</b>");
        }
    }


    public void selectPaymentMode(String PaymentMode) {
        try {
            Log.info("Trying to select Payment Mode");
            WebElement paymentmodedropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'ng-select-box-payment-default']//ng-select[@id = 'paymentModeSelect1']//div")));
            paymentmodedropdown.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'ng-select-box-payment-default']//ng-dropdown-panel")));
            String paymentMode = String.format("//div[@id = 'ng-select-box-payment-default']//div[@class = 'ng-option ng-star-inserted']//span[text()='%s']", PaymentMode);
            driver.findElement(By.xpath(paymentMode)).click();
            Log.info("Payment Mode selected successfully as: " + PaymentMode);
        } catch (Exception e) {
            Log.debug("<b>Payment Mode Type Not Found:</b>");
        }
    }

    public void enterPaymentInstrumentNumber(String PaymentInstrumentNumber)
    {
        Log.info("Trying to enter Payment Instrument Number..");
        WebElement enterPINo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'payInstNmbrInput']")));
        enterPINo.sendKeys(PaymentInstrumentNumber);
        Log.info("User entered Payment Instrument Number : " + PaymentInstrumentNumber);
    }

    public void enterPaymentDate(String Date)
    {
        Log.info("Trying to enter Payment Date..");
        WebElement enterPmtDate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p-calendar[@formcontrolname = 'paymentDate']//input")));
        enterPmtDate.sendKeys(Date);
        Log.info("User entered Payment Date : " + Date);
    }


    public String getDateMMDDYY() {
        Log.info("Trying to select Date");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='network-container']//span[@class='cdtspan']")));
        String date = "null" ;
        String[] dateTime= loginDateAndTime.getText().split(" ");
        System.out.println(loginDateAndTime.getText());
        System.out.println(dateTime);
        date = dateTime[0] ;
        String date1 = date.toString() ;
        String ddmmyy[] = date1.split("/") ;

        String dd = ddmmyy[0] ;
        ddmmyy[0] = ddmmyy[1] ;
        ddmmyy[1] = dd ;

        Log.info("ddmmyy[0] " +ddmmyy[0]);
        Log.info("ddmmyy[1] " +ddmmyy[1]);
        Log.info("ddmmyy[2] " +ddmmyy[2]);
        Log.info("Server date: "+date);
        String mmddyy = ddmmyy[1] + "/" + ddmmyy[0]+ "/" +ddmmyy[2] ;
        Log.info("ddmmyy : "+mmddyy) ;
        //return date ;
        return mmddyy ;
    }

    public void enterCUPaymentDate(String Date)
    {
        Log.info("Trying to enter Payment Date..");
        //WebElement enterPmtDate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p-calendar[@id = 'paymentDatePicker']//input")));
        WebElement enterPmtDate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='dd/mm/yyyy']")));
        enterPmtDate.sendKeys(Date);
        Log.info("User entered Payment Date : " + Date);
    }

    public String approvalExtDateCU() {
		Log.info("Trying to select Date");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='network-container']//span[@class='cdtspan']")));
		String date = "null" ;
		String[] dateTime= loginDateAndTime.getText().split(" ");
		System.out.println(loginDateAndTime.getText());
		System.out.println(dateTime);
		date = dateTime[0] ;
		String date1 = date.toString() ;
		String ddmmyy[] = date1.split("/") ;

		/*String dd = ddmmyy[0] ;
		ddmmyy[0] = ddmmyy[1] ;
		ddmmyy[1] = dd ;*/

		Log.info("ddmmyy[0] " +ddmmyy[0]);
		Log.info("ddmmyy[1] " +ddmmyy[1]);
		Log.info("ddmmyy[2] " +ddmmyy[2]);
		Log.info("Server date: "+date);
		String mmddyy = ddmmyy[0] + "/" + ddmmyy[1]+ "/" +ddmmyy[2] ;
		Log.info("ddmmyy : "+mmddyy) ;
		//return date ;
		return mmddyy ;
	}
    
    public void enterApprovalDate(String Date)
    {
        Log.info("Trying to enter Approval Date..");
        WebElement enterApprvDate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@type='text'])[3]")));
        enterApprvDate.sendKeys(Date);
        Log.info("User entered Approval Date : " + Date);
    }

    public void clickSubmitButton() {
        Log.info("Trying clicking on Submit Button");
        WebElement SubmitBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'purchaseO2c']//span[@class = 'mat-button-wrapper']")));
        SubmitBtn.click();
        Log.info("User clicked Submit Button");
    }

    public void enterPin(String ChnUsrPin) {
        Log.info("Trying to enter Channel User Pin..");
        WebElement enterYourPin = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//input[@formcontrolname='pin']")));
        enterYourPin.sendKeys(ChnUsrPin);
        Log.info("User entered Channel User Pin: " + ChnUsrPin);
    }

    public void clickRechargeIcon() {
        Log.info("Trying to click Recharge Button...");
        WebElement rechargeIcon= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'purcahseButton']//span")));
        rechargeIcon.click();
        Log.info("User clicked Recharge button");
    }


    public void selectSearchBy(String searchBy) {
        Log.info("Trying to select the searchBy...");
        WebElement searchBydropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'ng-select-box-default']//ng-select[@id = 'searchCriteriaSelect']")));
        searchBydropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'searchCriteriaSelect']//div[@class = 'ng-dropdown-panel-items scroll-host']")));
        String searchByType = String.format("//ng-select[@id = 'searchCriteriaSelect']//div[@role = 'option']//span[text()='%s']", searchBy);
        driver.findElement(By.xpath(searchByType)).click();
        Log.info("User selected searchBy : " + searchBy);
    }


    public void enterMSISDN(String MSISDN) {
        Log.info("Trying to enter MSISDN..");
        WebElement enterMSISDN = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'searchMsisdn']//input[@id = 'searchInput']")));
        enterMSISDN.sendKeys(MSISDN);
        Log.info("User entered MSISDN: " + MSISDN);
    }



    public void clickProceedButton() {
        Log.info("Trying clicking on Proceed Button");
        WebElement ProceedBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'proceedButton']//span[@class = 'mat-button-wrapper']")));
        ProceedBtn.click();
        Log.info("User clicked Proceed Button");
    }


    public void entereTopUPAmount(String Amount) {
        Log.info("Trying to enter eTopUp Amount..");
        WebElement enterAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text() = 'eTopUP']/parent::div/following-sibling::div//input")));
        enterAmount.sendKeys(Amount);
        Log.info("User entered eTopUp Amount: " + Amount);
    }

    public void enterPosteTopUPAmount(String Amount) {
        Log.info("Trying to enter Post eTopUP Amount..");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[text() = 'Post eTopUP']/parent::div/following-sibling::div//input")));
        WebElement enterAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text() = 'Post eTopUP']/parent::div/following-sibling::div//input")));
        enterAmount.sendKeys(Amount);
        Log.info("User entered Post eTopUP Amount: " + Amount);
    }

    public boolean successPopUPVisibility() {
        boolean result = false;
        try {
            WebElement successPopUP= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@class='success']")));
            if (successPopUP.isDisplayed()) {
                result = true;
                Log.info("Success Popup is visible.");
            }
        } catch (Exception e) {
            result = false;
            Log.info("Success Popup is not visible.");
        }
        return result;

    }


    public String actualMessage(){
        Log.info("Trying to get transfer Status..");
        WebElement actualMessage= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'successfultitle']")));
        String actualMsg = actualMessage.getText();
        Log.info("Actual Message fetched as : "+actualMsg);
        return actualMsg;
    }

    public String O2CTransactionID()
    {
        Log.info("Trying to fetch Transaction ID..");
        WebElement o2cSuccessID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='labelpos1']/following-sibling::b"))) ;
        String o2cSuccessBatchID = o2cSuccessID.getText() ;
        Log.info("O2C SUCCESS TRANSACTION ID : "+o2cSuccessBatchID);
        return  o2cSuccessBatchID;
    }

    public String getErrorMessageForFailure()
    {
        Log.info("Getting Error Message For O2C Failure ");
        WebElement messageForFailure = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'done']//h5")));
        String messageForC2CFailure = messageForFailure.getText();
        return messageForC2CFailure;
    }


    public void clickDoneButton() {
        Log.info("Trying to click Done button..");
        WebElement doneBtn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='doneButton']//span")));
        doneBtn.click();
        Log.info("User clicked Done Recharge button");
    }


    public void clickApproveDoneButton() {
        Log.info("Trying to click Done button..");
        WebElement doneBtn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'popupDonebtn']//span")));
        doneBtn.click();
        Log.info("User clicked Done Recharge button");
    }



    public void selectGeography(String geography) {
        Log.info("Trying to select the Geography...");
        WebElement geoDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'geographyProceed']")));
        geoDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'geographyProceed']//ng-dropdown-panel")));
        String geoType = String.format("//ng-select[@id = 'geographyProceed']//span[text()='%s']", geography);
        driver.findElement(By.xpath(geoType)).click();
        Log.info("User selected Geography : " + geography);
    }


    public void selectDomain(String domain) {
        Log.info("Trying to select the Domain...");
        WebElement domainDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'domainProceed']")));
        domainDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'domainProceed']//ng-dropdown-panel")));
        String domainType = String.format("//ng-select[@id = 'domainProceed']//span[text()='%s']", domain);
        driver.findElement(By.xpath(domainType)).click();
        Log.info("User selected Domain : " + domain);
    }


    public void selectCategory(String Category) {
        Log.info("Trying to select the Category...");
        WebElement categoryDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'categorySelect']")));
        categoryDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'categorySelect']//ng-dropdown-panel")));
        String categoryType = String.format("//ng-select[@id = 'categorySelect']//span[text()='%s']", Category);
        driver.findElement(By.xpath(categoryType)).click();
        Log.info("User selected Category : " + Category);
    }

    public void enterUserName(String userName) {
        Log.info("Trying to enter User Name..");
        WebElement enterUserName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'searchCriteriaInput']")));
        enterUserName.sendKeys(userName);
        try{
            Thread.sleep(2000);
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
        Log.info("User entered User Name: " + userName);
    }

    public void enterLoginID(String LoginID) {
        Log.info("Trying to enter LoginID..");
        WebElement enterLoginID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'searchInput']")));
        enterLoginID.sendKeys(LoginID);
        Log.info("User entered LoginID: " + LoginID);
    }


    public String getMsisdnError()
    {
        Log.info("Trying to fetch MSISDN Error message");
        WebElement msisdnError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'searchInput']/following-sibling::div")));
        String msisdnErrorMessage = msisdnError.getText();
        return msisdnErrorMessage;
    }

    public String getLoginIDError()
    {
        Log.info("Trying to fetch Login ID Error message");
        WebElement loginIDError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id = 'control']//div[@class = 'row']//div")));
        String loginIDErrorMessage = loginIDError.getText();
        return loginIDErrorMessage;
    }

    public String getReferenceError()
    {
        Log.info("Trying to fetch Reference Error message");
        WebElement referenceError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'referenceNoInput']/following-sibling::div")));
        String ReferenceErrorMessage = referenceError.getText();
        return ReferenceErrorMessage;
    }


    public String getCUReferenceError()
    {
        Log.info("Trying to fetch Reference Error message");
        WebElement referenceError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'referenceNumberInput']/following-sibling::div")));
        String ReferenceErrorMessage = referenceError.getText();
        return ReferenceErrorMessage;
    }

    public String geteTopupAmountError()
    {
        Log.info("Trying to fetch eTopUP Amount Error message");
        WebElement remarksError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text() = 'eTopUP']/parent::div/following-sibling::div//div")));
        String RemarksErrorMessage = remarksError.getText();
        return RemarksErrorMessage;
    }


    public String getPosteTopUpAmountError()
    {
        Log.info("Trying to fetch Post eTopUp Amount Error message");
        WebElement remarksError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text() = 'Post eTopUP']/parent::div/following-sibling::div//div")));
        String RemarksErrorMessage = remarksError.getText();
        return RemarksErrorMessage;
    }


    public String getRemarksError()
    {
        Log.info("Trying to fetch Remarks Error message");
        WebElement remarksError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id = 'textBox']/following-sibling::div")));
        String RemarksErrorMessage = remarksError.getText();
        return RemarksErrorMessage;
    }


    public String getPmtInstNoError()
    {
        Log.info("Trying to fetch Payment Instrument number Error message");
        WebElement PmtInstNoError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'payInstNmbrInput']/following-sibling::div")));
        String PmtInstNoErrorMessage = PmtInstNoError.getText();
        return PmtInstNoErrorMessage;
    }


    public String getAmountError()
    {
        Log.info("Trying to fetch Amount Error message");
        WebElement amountError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@formarrayname = 'amountRows']//input[@class = 'form-control text-center ng-pristine ng-valid ng-touched']/following-sibling::div")));
        String amountMessage = amountError.getText();
        return amountMessage;
    }




    public void clickResetButton() {
        Log.info("Trying to click Reset button..");
        WebElement rstBtn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'reset']//span")));
        rstBtn.click();
        Log.info("User clicked Reset button");
    }


    public Boolean getblankeTopUPAmount(){
        WebElement blankeTopUPAmount= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@formarrayname = 'amountRows']//input[@class = 'form-control text-center ng-valid ng-untouched ng-pristine']")));
        String storedeTopUPAmount = blankeTopUPAmount.getAttribute("value");
        Log.info("Stored eTopUP Amount: "+storedeTopUPAmount);
        if(storedeTopUPAmount.isEmpty())
        {
            Log.info("eTopUP Amount is blank");
            return true;
        }
        else{
            Log.info("eTopUP Amount is not blank");
            return false;
        }

    }


    public Boolean getblankReferenceNumber(){
        WebElement blankReferenceNumber= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'row']//input[@id = 'referenceNoInput']")));
        String storedReferenceNumber = blankReferenceNumber.getAttribute("value");
        Log.info("Stored Reference Number: "+storedReferenceNumber);
        if(storedReferenceNumber.isEmpty())
        {
            Log.info("Reference Number is blank");
            return true;
        }
        else{
            Log.info("Reference Number is not blank");
            return false;
        }

    }


    public Boolean getCUblankReferenceNumber(){
        WebElement blankReferenceNumber= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'row']//input[@id = 'referenceNumberInput']")));
        String storedReferenceNumber = blankReferenceNumber.getAttribute("value");
        Log.info("Stored Reference Number: "+storedReferenceNumber);
        if(storedReferenceNumber.isEmpty())
        {
            Log.info("Reference Number is blank");
            return true;
        }
        else{
            Log.info("Reference Number is not blank");
            return false;
        }

    }


    public Boolean getblankRemarks()
    {
        WebElement blankRemarks = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id = 'textBox']")));
        String storedRemarks = blankRemarks.getAttribute("value");
        Log.info("Stored Remarks: "+storedRemarks);
        if(storedRemarks.isEmpty())
        {
            Log.info("Remarks fields is blank");
            return true;
        }
        else{
            Log.info("Remarks fields is not blank");
            return false;
        }
     }


    public Boolean getblankPaymentInstrumentNumber()
    {
        WebElement blankPaymentInstrumentNumber = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'payInstNmbrInput']")));
        String storedPaymentInstrumentNumber = blankPaymentInstrumentNumber.getAttribute("value");
        Log.info("Stored Payment Instrument Number: "+storedPaymentInstrumentNumber);
        if(storedPaymentInstrumentNumber.isEmpty())
        {
            Log.info("Payment Instrument Number fields is blank");
            return true;
        }
        else{
            Log.info("Payment Instrument Number fields is not blank");
            return false;
        }
    }


    public Boolean getblankPaymentDate()
    {
        WebElement blankPaymentDate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'payInstNmbrInput']")));
        String storedPaymentDate = blankPaymentDate.getAttribute("value");
        Log.info("Stored Payment Date: "+storedPaymentDate);
        if(storedPaymentDate.isEmpty())
        {
            Log.info("Payment Date fields is blank");
            return true;
        }
        else{
            Log.info("Payment Date fields is not blank");
            return false;
        }
    }


    public void clickSearchByResetButton() {
        Log.info("Trying to click Reset button..");
        WebElement rstBtn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'resetButton1']//span")));
        rstBtn.click();
        Log.info("User clicked Reset button");
    }


    public Boolean getBlankSearchInput()
    {
        WebElement blankSearchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'searchInput']")));
        String storedSearchInput = blankSearchInput.getAttribute("value");
        Log.info("Stored Search Input: "+storedSearchInput);
        if(storedSearchInput.isEmpty())
        {
            Log.info("Search Input fields is blank");
            return true;
        }
        else{
            Log.info("Search Input fields is not blank");
            return false;
        }
    }


    public Boolean checkDisabledRechargeButton()
    {
        Log.info("User trying to click Recharge Button");
        Boolean flag = false;
        WebElement PINSubmitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'purcahseButton']//span")));
        PINSubmitButton.click();
        Log.info("User clicked Recharge button");
        WebElement disabledPINButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@disabled='true']")));
        if(disabledPINButton.isDisplayed())
        {
            flag = true ;
        }
        Log.info("Recharge Button is disabled after Blank PIN.");
        return flag ;

    }


    public String transferStatusFailed(){
        Log.info("Trying to get transfer Status.");
        WebElement transferStatusFailed= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'vals failuremsg']")));
        String trfStatus = transferStatusFailed.getText();
        Log.info("Transfer status fetched as : "+trfStatus);
        return trfStatus;
    }


    public String InvalidExtTxnNo(){
        Log.info("Trying to get External Transaction Number Error.");
        WebElement transferStatusFailed= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'invalid-feedback ng-star-inserted']//div[@class = 'ng-star-inserted']")));
        String trfStatus = transferStatusFailed.getText();
        Log.info("External Transaction Number Error fetched as : "+trfStatus);
        return trfStatus;
    }


    public String InvalidExtTxnDate(){
        Log.info("Trying to get External Transaction Date Error.");
        WebElement transferStatusFailed= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'invalid-feedback ng-star-inserted']//div[@class = 'ng-star-inserted']")));
        String trfStatus = transferStatusFailed.getText();
        Log.info("Transfer status fetched as : "+trfStatus);
        return trfStatus;
    }

    public String InvalidReferenceNo(){
        Log.info("Trying to get transfer Status.");
        WebElement transferStatusFailed= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'ng-select-box-type-default']//div[@class ='ng-star-inserted']")));
        String trfStatus = transferStatusFailed.getText();
        Log.info("Transfer status fetched as : "+trfStatus);
        return trfStatus;
    }





    public void spinnerWait() {
        Log.info("Waiting for spinner");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
            Log.info("Waiting for spinner to stop");
        }
        catch (NoSuchElementException ignored) {
            Log.info("Element not found");
        }
        catch (StaleElementReferenceException ignored) {
            Log.info("Element not found");
        }
        catch (TimeoutException ignored) {
            Log.info("Element not found");
        }
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
        Log.info("Spinner stopped");
    }


    public void clickApproval1O2CHeading() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Log.info("Trying clicking on Approval 1 O2C Heading");
        WebElement apprv1Heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-1']//span")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", apprv1Heading);*/
        apprv1Heading.click();
        Log.info("User clicked Approval 1 O2C Heading.");
    }


    public void clickApproval2O2CHeading() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Log.info("Trying clicking on Approval 2 O2C Heading");
        WebElement apprv2Heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-2']//span")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", apprv2Heading);*/
        apprv2Heading.click();
        Log.info("User clicked Approval 2 O2C Heading.");
    }

    public void clickApproval3O2CHeading() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Log.info("Trying clicking on Approval 3 O2C Heading");
        WebElement apprv3Heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-3']//span")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", apprv3Heading);*/
        apprv3Heading.click();
        Log.info("User clicked Approval 3 O2C Heading.");
    }


    public void clickApproval1Transaction() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Log.info("Trying clicking on Approval 1 Transaction Link");
        WebElement apprv1txnHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval1txn']//span")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", apprv1txnHeading);*/
        apprv1txnHeading.click();
        Log.info("User clicked Approval 1 Transaction Link.");
    }


    public void clickApproval2Transaction() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Log.info("Trying clicking on Approval 2 Transaction Link");
        WebElement apprv2txnHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval2txn']")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", apprv2txnHeading);*/
        apprv2txnHeading.click();
        Log.info("User clicked Approval 2 Transaction Link.");
    }


    public void clickApproval3Transaction() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Log.info("Trying clicking on Approval 3 Transaction Link");
        WebElement apprv3txnHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval3txn']")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", apprv3txnHeading);*/
        apprv3txnHeading.click();
        Log.info("User clicked Approval 3 Transaction Link.");
    }

    public void clickO2CApprovalSingleOperationHeading() {
        Log.info("Trying clicking on O2C Single Operation Heading..");
        WebElement o2cTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-1-button']")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", o2cTransactionHeading);*/
        o2cTransactionHeading.click();
        Log.info("User clicked O2C Single Operation Heading.");
    }


    public void selectApprovalDomain(String domain) {
        Log.info("Trying to select the Domain...");
        WebElement domainDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@labelforid = 'domain']")));
        domainDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@labelforid = 'domain']//ng-dropdown-panel")));
        String domainType = String.format("//ng-select[@labelforid = 'domain']//span[text()='%s']", domain);
        driver.findElement(By.xpath(domainType)).click();
        Log.info("User selected Domain : " + domain);
    }


    public void selectApprovalCategory(String Category) {
        Log.info("Trying to select the Category...");
        try{
            Thread.sleep(2000);
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
        WebElement categoryDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@labelforid = 'category']")));
        categoryDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@labelforid = 'category']//ng-dropdown-panel")));
        String categoryType = String.format("//ng-select[@labelforid = 'category']//span[text()='%s']", Category);
        driver.findElement(By.xpath(categoryType)).click();
        Log.info("User selected Category : " + Category);
    }

    public void selectApprovalGeography(String geography) {
        Log.info("Trying to select the Geography...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'toggle-2-button']")));
        WebElement geoDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@labelforid = 'geography']")));
        /*((JavascriptExecutor) driver).executeScript("arguments[0].click();", geoDropdown);*/
        geoDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@labelforid = 'geography']//ng-dropdown-panel")));
        String geoType = String.format("//ng-select[@labelforid = 'geography']//span[text()='%s']", geography);
        driver.findElement(By.xpath(geoType)).click();
        Log.info("User selected Geography : " + geography);
    }



    public void clickApprovalProceedButton() {
        try {
            Log.info("Trying clicking on Proceed Button");
            WebElement ProceedBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@name = 'Proceed']//span[@class = 'mat-button-wrapper']")));
            ProceedBtn.click();
            Thread.sleep(2000);
            Log.info("User clicked Proceed Button");
        }
        catch(InterruptedException ex) {
            Log.info("O2C Proceed button not clicked") ;
        }
    }

    public void enterSearch(String searchText) {
        Log.info("Trying to enter Search Text(Search by User name, Mobile number, Login ID, etc) ..");
        WebElement enterAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name = 'search']")));
        enterAmount.sendKeys(searchText);
        Log.info("User entered Search Text(Search by User name, Mobile number, Login ID, etc): " + searchText);
    }



    public void clickApproveTxnButton() {
        try {
            Log.info("Trying clicking on Approve Transaction Button");
            WebElement ApprvBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='DTFC_RightBodyLiner']//a[@class='approveClass'])[1]")));
            ApprvBtn.click();
            Thread.sleep(2000);
            Log.info("User clicked Approve Transaction Button");
        }
        catch(InterruptedException ex) {
            Log.info("O2C Approve Transaction Button not clicked") ;
        }
    }

    public void clickRejectTxnButton() {
        try {
            Log.info("Trying clicking on Reject Transaction Button");
            WebElement RjctBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='DTFC_RightBodyLiner']//a[@class='rejectClass'])[1]")));
            RjctBtn.click();
            Thread.sleep(2000);
            Log.info("User clicked Reject Transaction Button");
        }
        catch(InterruptedException ex) {
            Log.info("O2C Reject Transaction Button not clicked") ;
        }
    }


    public void enterExtTxnNo(String ExtTxnNo) {
        Log.info("Trying to enter External Transaction Number..");
        WebElement enterExtTxnNo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'externalTxnNum']")));
        enterExtTxnNo.sendKeys(ExtTxnNo);
        Log.info("User entered External Transaction Number: " + ExtTxnNo);
    }

    public void enterApprovalReferenceNo(String Reference)
    {
        Log.info("Trying to enter Reference Number..");
        WebElement enterReference = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'refNo']")));
        enterReference.sendKeys(Reference);
        Log.info("User entered Reference Number: " + Reference);
    }



    public void clickApproveButton() {
        Log.info("Trying clicking on Approve Button");
        WebElement ApprvBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'approve']/span")));
        ApprvBtn.click();
        Log.info("User clicked Approve Button");
    }


    public void clickRejectButton() {
        Log.info("Trying clicking on Reject Button");
        WebElement ApprvBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'reject']/span")));
        ApprvBtn.click();
        Log.info("User clicked Reject Button");
    }

    public void clickApproveYesButton() {
        Log.info("Trying clicking on Approve Yes Button");
        WebElement ApprvBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Yes')]")));
        ApprvBtn.click();
        Log.info("User clicked Aprove Yes Button");
    }


    public boolean successPopUPApproveVisibility() {
        boolean result = false;
        try {
            WebElement successPopUP= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@id='done']")));
            if (successPopUP.isDisplayed()) {
                result = true;
                Log.info("Success Popup is visible.");
            }
        } catch (Exception e) {
            result = false;
            Log.info("Success Popup is not visible.");
        }
        return result;

    }

    public String O2CApproveTransactionID()
    {
        Log.info("Trying to fetch Transaction ID..");
        WebElement o2cSuccessID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='textLight']/following-sibling::b"))) ;
        String o2cSuccessBatchID = o2cSuccessID.getText() ;
        Log.info("O2C SUCCESS TRANSACTION ID : "+o2cSuccessBatchID);
        return  o2cSuccessBatchID;
    }

    public String actualApproveMessage(){
        Log.info("Trying to get Actual Message..");
        WebElement actualMessage= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'done']//h5")));
        String actualMsg = actualMessage.getText();
        Log.info("Actual Message fetched as : "+actualMsg);
        return actualMsg;
    }

    public String invalidSearchBy()
    {
        Log.info("Trying to fetch Search error..");
        WebElement errorMsgSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tbody[@id = 'approvalLevelO2CTableBody']//td"))) ;
        String errorMsg = errorMsgSearch.getText() ;
        Log.info("Fetched Search error : "+errorMsg);
        return  errorMsg;
    }


    public void selectTransactionType() {
        Log.info("Trying to select the Transaction Type...");
        WebElement transactionTypedropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@labelforid='profile']")));
        transactionTypedropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@labelforid='profile']//ng-dropdown-panel")));
        String searchByType = String.format("//ng-select[@labelforid='profile']//span[text()=' Purchase ']");
        driver.findElement(By.xpath(searchByType)).click();
        Log.info("User selected searchBy : Purchase");
    }



}

