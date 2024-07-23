package angular.pageobjects.FOCTransfers;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.List;


public class FOCTransfers {

    WebDriver driver = null;
    WebDriverWait wait = null;

    public FOCTransfers(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait= new WebDriverWait(driver, 20);
    }

    public boolean isC2CVisible() {
        try {
            WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='c2cmain']//span[@class='childmenucss']")));

            if(expanded.isDisplayed())
                return true;
        }

        catch(Exception e) {
            return false;
        }

        return false;
    }

    public void clickOPTFOCCommissionHeading() {
        Log.info("Trying clicking on FOC Heading");
        WebElement FOCTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='commission']")));
        FOCTransactionHeading.click();
        Log.info("User clicked FOC Heading Link.");
    }

    public void clickC2CHeading() {
        Log.info("Trying clicking on C2C Heading");
        WebElement c2cHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='c2cmain']")));
        c2cHeading.click();
        Log.info("User clicked C2C Heading Link.");
    }


    public void clickFOCTransactionHeading() {
        Log.info("Trying clicking on FOC Transaction Heading");
        WebElement FOCTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= '02c']")));
        FOCTransactionHeading.click();
        Log.info("User clicked FOC Transaction Heading Link.");
    }


    public void enterAmountEtopUp(String Amount) {
        Log.info("Trying to enter etopup amount..");
        WebElement enterAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@id='amoutnInput'])[1]")));
        enterAmount.sendKeys(Amount);
        Log.info("User entered etopup amount" + Amount);
    }

    public void enterAmountPostEtopUp(String Amount) {
        Log.info("Trying to enter post etopup amount..");
        WebElement enterAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@id='amoutnInput'])[2]")));
        enterAmount.sendKeys(Amount);
        Log.info("User entered post etopup amount" + Amount);
    }


    public void clickPurcahaseButton() {
        Log.info("Trying clicking on Purchase Button");
        WebElement PurchaseBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='purchaseFOC']")));
        PurchaseBtn.click();
        Log.info("User clicked Purchase Button");
    }

    public void printC2CSuccessBatchID()
    {
        WebElement batchId=wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[@class='labelpos1'])[1]"))) ;
        String c2cSuccessBatchID = batchId.getText() ;
        Log.info("C2C SUCCESS BATCH ID : "+c2cSuccessBatchID);
    }

    public void clickOPTO2CHeading() {
        Log.info("Trying clicking on o2c Heading");
        WebElement FOCTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= 'o2c']")));
        FOCTransactionHeading.click();
        Log.info("User clicked o2c Heading Link.");
    }

    public void clickOPTFOCTransactionHeading() {
        Log.info("Trying clicking on FOC Transaction Heading..");
        try{
            Thread.sleep(1000);
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
        WebElement FOCTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= 'FOCtransaction']")));
        FOCTransactionHeading.click();
        Log.info("User clicked FOC Transaction Heading.");
    }


    public void clickOPTFOCSingleOperationHeading() {
        Log.info("Trying clicking on FOC Single Operation Heading..");
        WebElement FOCTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'singleToggle-button']")));
        FOCTransactionHeading.click();
        Log.info("User clicked FOC Single Operation Heading.");
    }




    public boolean isFOCCommissionVisible() {
        try {
            WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id= 'commission']")));
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

    public void clickFOCPurchaseHeading() {
        Log.info("Trying clicking on FOC Purchase Heading");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'ng-select-container']")));
        WebElement FOCPurchaseHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class = 'anchorActiveClass']//span")));
        FOCPurchaseHeading.click();
        Log.info("User clicked FOC Purchase Heading");
    }


    public void clickFOCeTopUPHeading() {
        Log.info("Trying clicking on FOC eTopUP Heading");
        WebElement FOCeTopUPHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'stockToggle-button']")));
        FOCeTopUPHeading.click();
        Log.info("User clicked FOC eTopUP Heading.");
    }


    public void enterAmount(String Amount) {
        Log.info("Trying to enter amount..");
        WebElement enterAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'row']//input[@class = 'form-control text-center ng-untouched ng-pristine ng-valid']")));
        enterAmount.sendKeys(Amount);
        Log.info("User entered amount" + Amount);
    }


    public void enterReferenceNumber(String Reference)
    {
        Log.info("Trying to enter Reference Number..");
        WebElement enterReference = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'row']//input[@id = 'referenceNoInput']")));
        enterReference.sendKeys(Reference);
        Log.info("User entered Reference Number" + Reference);
    }



    public void enterPaymentInstrumentNo(String Remarks)
    {
        Log.info("Trying to enter Payment instrumentNo..");
        WebElement instrumentNo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='paymentInsNo']")));
        instrumentNo.sendKeys(Remarks);
        Log.info("User entered Payment instrumentNo : " + instrumentNo);
    }

    public void enterRemarks(String Remarks)
    {
        Log.info("Trying to enter Remarks..");
        WebElement enterRemarks = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id= 'textBox']")));
        enterRemarks.sendKeys(Remarks);
        Log.info("User entered Remarks" + Remarks);
    }

    public void selectPaymentMode(String PaymentMode) {
        try {
            Log.info("Trying to select Payment Mode");
            WebElement paymentmodedropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'ng-select-box-payment-default']//div[@class = 'ng-select-container']")));
            paymentmodedropdown.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'ng-select-box-payment-default']//ng-dropdown-panel[@class = 'ng-dropdown-panel ng-star-inserted ng-select-top']")));
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
        WebElement enterPmtDate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname = 'paymentDate']")));
        enterPmtDate.sendKeys(Date);
        Log.info("User entered Payment Date : " + Date);
    }

    public void enterApprovalDate(String Date)
    {
        Log.info("Trying to enter Approval Date..");
        WebElement enterApprvDate = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//primeng-datepicker[@id = 'externalTxnDatePicker']//input")));
        enterApprvDate.sendKeys(Date);
        Log.info("User entered Approval Date : " + Date);
    }

    public void clickTransferButton() {
        Log.info("Trying clicking on Transfer Button");
        WebElement SubmitBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='purchaseO2c']")));
        SubmitBtn.click();
        Log.info("User clicked Transfer Button");
    }

    public void enterPin(String ChnUsrPin) {
        Log.info("Trying to enter Channel User Pin..");
        WebElement enterYourPin = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//input[@formcontrolname='pin']")));
        enterYourPin.sendKeys(ChnUsrPin);
        Log.info("User entered Channel User Pin " + ChnUsrPin);
    }

    public void clickRechargeIcon() {
        Log.info("Trying to click Recharge Button...");
        WebElement rechargeIcon= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'purcahseButton']//span")));
        rechargeIcon.click();
        Log.info("User clicked Recharge button");
    }

    public void clickPinProceedButton() {
        Log.info("Trying to click Recharge Button...");
        WebElement rechargeIcon= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'purcahseButton']//span")));
        rechargeIcon.click();
        Log.info("User clicked Recharge button");
    }


//    public void selectSearchBy(String searchBy) {
//        Log.info("Trying to select the searchBy...");
//        WebElement searchBydropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'ng-select-box-default']//ng-select[@id = 'searchCriteriaSelect']")));
//        searchBydropdown.click();
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'searchCriteriaSelect']//div[@class = 'ng-dropdown-panel-items scroll-host']")));
//        String searchByType = String.format("//ng-select[@id = 'searchCriteriaSelect']//div[@role = 'option']//span[text()='%s']", searchBy);
//        driver.findElement(By.xpath(searchByType)).click();
//        Log.info("User selected searchBy : " + searchBy);
//    }
    
    public void selectSearchBy(String searchBy) {
		Log.info("Trying to select search by dropdown");
	    WebElement searchByDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='searchCriteriaSelect']")));
	    searchByDropdown.click();
	    String dp = String.format("//div[@role='option']//span[contains(text(),'%s')]", searchBy);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(dp)));
		driver.findElement(By.xpath(dp)).click() ;
	    Log.info("User selected search by " +  searchBy);
	}


    public void enterMSISDN(String MSISDN) {
        Log.info("Trying to enter MSISDN..");
        WebElement enterMSISDN = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'searchCriteriaInput']")));
        enterMSISDN.sendKeys(MSISDN);
        Log.info("User entered MSISDN" + MSISDN);
    }



    public void clickProceedButton() {
        try {
            Log.info("Trying clicking on Proceed Button");
            WebElement ProceedBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'proceedButton']//span[@class = 'mat-button-wrapper']")));
            ProceedBtn.click();
            Thread.sleep(2000);
            Log.info("User clicked Proceed Button");
        }
        catch(InterruptedException ex) {
            Log.info("FOC Proceed button not clicked") ;
        }
    }

    public HashMap<String,String> enterQuantityforFOCRevamp(){
        String totalFOCTransferAmount = null ;
        Log.info("Trying to initiate C2C Topups");
        StringBuilder initiatedQuantities = new StringBuilder();
        HashMap<String,String> qty = new HashMap<String, String>();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[@formarrayname='amountRows']"))) ; //wait for transfer details
        List<WebElement> Qty = driver.findElements(By.xpath("//tr[@formarrayname='amountRows']"));
        for(int countQty=1; countQty <= Qty.size(); countQty++){
            WebElement qtyField = driver.findElement(By.xpath("(//tr[@formarrayname='amountRows'])[" +countQty+ "]//input")) ;
            WebElement balance = driver.findElement(By.xpath("(//label[@class='my-balance'])[" +countQty+ "]")) ;
            String productBalance = balance.getText() ;
            String productShortCode = driver.findElement(By.xpath("(//label[@class='my-balance'])["+countQty+"]//parent::td//parent::tr[@formarrayname='amountRows']//td[1]//label")).getText();
            ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
            int rowCount = ExcelUtility.getRowCount();
            Log.info("rowCount of topups available on screen : "+rowCount) ;
            for (int i = 1; i <= rowCount; i++) {
                String sheetProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, i);
                Log.info("sheetProductCode : "+sheetProductCode) ;
                if (sheetProductCode.equals(productShortCode)) {
                    String productBalanceCommaRemoved =  productBalance.replace(",","") ;
                    productBalanceCommaRemoved =  productBalanceCommaRemoved.replace("₹","") ;
                    int prBalance= (int) Double.parseDouble(productBalanceCommaRemoved);
                    int quantity=(int) ((prBalance/2)*0.001) ;
                    if(quantity > 200){
                        quantity = 50 ;
                        qtyField.sendKeys(String.valueOf(quantity));
                        qty.put(sheetProductCode, String.valueOf(quantity));
                        Log.info("quantity : "+quantity) ;
                    }
                    else{
                        qtyField.sendKeys(String.valueOf(quantity));
                        qty.put(sheetProductCode, String.valueOf(quantity));
                        Log.info("quantity : "+quantity) ;
                    }
                    totalFOCTransferAmount = String.valueOf(quantity);
                    Log.info("String.valueOf(quantity) SEND KEYS : " +String.valueOf(quantity) )  ;
                }
            }
        }
        Log.info("Entered Quantities: " + initiatedQuantities.toString());
        return qty;
    }

    public void entereTopUPAmount(String Amount) {
        Log.info("Trying to enter eTopUp Amount..");
        WebElement enterAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text() = 'eTopUP']/parent::td/following-sibling::td[2]//input")));
        enterAmount.sendKeys(Amount);
        Log.info("User entered eTopUp Amount" + Amount);
    }

    public void enterPosteTopUPAmount(String Amount) {
        Log.info("Trying to enter Post eTopUP Amount..");
        WebElement enterAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text() = 'Post eTopUP']/parent::td/following-sibling::td[2]//input")));
        enterAmount.sendKeys(Amount);
        Log.info("User entered Post eTopUP Amount" + Amount);
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


    public String successfulMessage(){
        Log.info("Trying to get transfer Status..");
        WebElement actualMessage= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'successfultitle']")));
        String actualMsg = actualMessage.getText();
        Log.info("Actual Message fetched as : "+actualMsg);
        return actualMsg;
    }
    
    public String approvalSuccessfulMessage(){
        Log.info("Trying to get transfer Status..");
        WebElement actualMessage= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'done']/h5")));
        String actualMsg = actualMessage.getText();
        Log.info("Actual Message fetched as : "+actualMsg);
        return actualMsg;
    }

    public String FOCTransactionID()
    {
        Log.info("Trying to fetch Transaction ID..");
        WebElement focSuccessID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='labelpos1']/following-sibling::b"))) ;
        String focSuccessBatchID = focSuccessID.getText() ;
        Log.info("FOC SUCCESS TRANSACTION ID : "+focSuccessBatchID);
        return  focSuccessBatchID;
    }

    public String getErrorMessageForFailure()
    {
        Log.info("Getting Error Message For FOC Failure ");
        WebElement messageForFailure = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='vals failuremsg']")));
        String messageForC2CFailure = messageForFailure.getText();
        return messageForC2CFailure;
    }


    public void clickDoneButton() {
        Log.info("Trying to click Done button..");
        WebElement doneBtn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='doneButton']//span")));
        doneBtn.click();
        Log.info("User clicked Done Recharge button");
    }
    
    public void clickApprovalDoneButton() {
        Log.info("Trying to click Approval Done button..");
        WebElement doneBtn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'popupDonebtn']")));
        doneBtn.click();
        Log.info("User clicked Approval Done button");
    }



    public void selectFOCGeography(String geography) {
        Log.info("Trying to select the Geography... : "+geography);
        WebElement geoDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'geographySelect']")));
        geoDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id='geographySelect']//ng-dropdown-panel")));
        String geoType = String.format("//ng-select[@id='geographySelect']//ng-dropdown-panel//div[@role='option']//span[text()='%s']", geography);
        driver.findElement(By.xpath(geoType)).click();
        Log.info("User selected Geography : " + geography);
    }
    
    public String getFOCGeographyError() {
    	Log.info("Trying to fetch foc geography error");
        WebElement geoDropdownError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'geographySelect']/following-sibling::div")));
        String geoDropdownErrormsg = geoDropdownError.getText();
        Log.info("Message fetched!");
        return geoDropdownErrormsg;
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


    public void clickDoMoreTransfers() {
        Log.info("Trying to click Do More Transfers button..");
        WebElement doneBtn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='purchaseMoreVocucher']")));
        doneBtn.click();
        Log.info("User clicked  Do More Transfers button");
    }



    public void selectDomain(String domain) {
        Log.info("Trying to select the Domain...");
        WebElement domainDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'domainSelect']")));
        domainDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'domainSelect']//ng-dropdown-panel")));
        String domainType = String.format("//ng-select[@id = 'domainSelect']//span[text()='%s']", domain);
        driver.findElement(By.xpath(domainType)).click();
        Log.info("User selected Domain : " + domain);
    }
    
    public String getDomainError() {
    	Log.info("Trying to fetch foc domain error");
        WebElement domainDropdownError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'domainSelect']/following-sibling::div")));
        String domainDropdownErrormsg = domainDropdownError.getText();
        Log.info("Message fetched!");
        return domainDropdownErrormsg;
    }


    public void selectCategory(String Category) {
        Log.info("Trying to select the Category...");
//        try{
//            Thread.sleep(2000);
//        }catch (InterruptedException interrupted){
//            interrupted.printStackTrace();
//        }
        WebElement categoryDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'categorySelect']")));
        categoryDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'categorySelect']//ng-dropdown-panel")));
        String categoryType = String.format("//ng-select[@id = 'categorySelect']//span[text()='%s']", Category);
        driver.findElement(By.xpath(categoryType)).click();
        Log.info("User selected Category : " + Category);
    }
    
    public String getCategoryError() {
    	Log.info("Trying to fetch foc category error");
        WebElement categoryDropdownError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@id = 'categorySelect']/following-sibling::div")));
        String categoryDropdownErrormsg = categoryDropdownError.getText();
        Log.info("Message fetched!");
        return categoryDropdownErrormsg;
    }

    public void enterUserName(String userName) {
        Log.info("Trying to enter User Name.. : "+userName);
        WebElement enterUserName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'searchCriteriaInput']")));
        enterUserName.sendKeys(userName);
//        try{
//            Thread.sleep(2000);
//        }catch (InterruptedException interrupted){
//            interrupted.printStackTrace();
//        }
        /*enterUserName.click();
        try{
            Thread.sleep(2000);
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
        enterUserName.sendKeys(Keys.ARROW_DOWN);
        enterUserName.sendKeys(Keys.RETURN);*/

        Log.info("User entered User Name" + userName);
    }


    public void clickUserName(String UserName)
    {
        Log.info("Trying to click User Name..");
        /*wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id = 'dynmicUser']")));*/
        String subServices= String.format("//option['%s']",UserName);
        /*wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(subServices)));*/
        driver.findElement(By.xpath(subServices)).click();
        Log.info("Click successfully User Name: " + UserName);
    }

    public void enterLoginID(String LoginID) {
        Log.info("Trying to enter LoginID..");
        WebElement enterLoginID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'searchCriteriaInput']")));
        enterLoginID.sendKeys(LoginID);
        Log.info("User entered LoginID" + LoginID);
    }


    public String getMsisdnError()
    {
        Log.info("Trying to fetch MSISDN Error message");
        WebElement msisdnError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'searchCriteriaInput']/following-sibling::div//div")));
        String msisdnErrorMessage = msisdnError.getText();
        Log.info("Message fetched");
        return msisdnErrorMessage;
    }

    public String getLoginIDErrorMessage()
    {
        Log.info("Trying to fetch Login ID Error message");
        WebElement loginIDError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id='dynmicUser']/following-sibling::div//div")));
        String loginIDErrorMessage = loginIDError.getText();
        Log.info("Message fetched");
        return loginIDErrorMessage;
    }

    public String getBlankLoginIDErrorMessage()
    {
        Log.info("Trying to fetch Login ID Error message");
        WebElement loginIDError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id='dynmicUserIds']/following-sibling::div//div")));
        String loginIDErrorMessage = loginIDError.getText();
        Log.info("Message fetched");
        return loginIDErrorMessage;
    }

    public String getUsernameErrorMessage()
    {
        Log.info("Trying to fetch User name Error message");
//        WebElement loginIDError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id='dynmicUserIds']/following-sibling::div//div")));
        WebElement userNameError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//datalist[@id='dynmicUser']/following-sibling::div//div")));
        String userNameErrorMessage = userNameError.getText();
        Log.info("Message fetched");
        return userNameErrorMessage;
    }

    public String getLoginIDNotFoundErrorMessage()
    {
        Log.info("Trying to fetch Login ID Error message");
        WebElement loginIDError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//form[@id='control'])[2]//div//div//div")));
        String loginIDErrorMessage = loginIDError.getText();
        Log.info("Message fetched");
        return loginIDErrorMessage;
    }

    public String getUsernameNotFoundErrorMessage()
    {
        Log.info("Trying to fetch Login ID Error message");
        WebElement loginIDError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//form[@id='control'])[2]//div//div//div")));
        String loginIDErrorMessage = loginIDError.getText();
        Log.info("Message fetched");
        return loginIDErrorMessage;
    }


    public String getReferenceError()
    {
        Log.info("Trying to fetch Reference Error message");
        WebElement referenceError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'referenceNoInput']/following-sibling::div")));
        String ReferenceErrorMessage = referenceError.getText();
        Log.info("Message fetched");
        return ReferenceErrorMessage;
    }

    public String geteTopupAmountError()
    {
        Log.info("Trying to fetch eTopUP Amount Error message");
        WebElement remarksError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text() = 'eTopUP']/parent::div/following-sibling::div//div[@class = 'ng-tns-c11-2 ng-star-inserted']")));
        String RemarksErrorMessage = remarksError.getText();
        Log.info("Message fetched");
        return RemarksErrorMessage;
    }

    public String getNegativeETopupAmountError()
    {
        Log.info("Trying to fetch eTopUP Amount Error message");
        WebElement remarksError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[@formarrayname='amountRows']//div//div")));
        String RemarksErrorMessage = remarksError.getText();
        Log.info("Message fetched");
        return RemarksErrorMessage;
    }


    public String getPosteTopUpAmountError()
    {
        Log.info("Trying to fetch Post eTopUp Amount Error message");
        WebElement remarksError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[text() = 'Post eTopUP']/parent::div/following-sibling::div//div[@class = 'ng-tns-c11-2 ng-star-inserted']")));
        String RemarksErrorMessage = remarksError.getText();
        Log.info("Message fetched");
        return RemarksErrorMessage;
    }

    public String getNegativePosteTopUpAmountError()
    {
        Log.info("Trying to fetch Post eTopUp Amount Error message");
        WebElement remarksError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr[@formarrayname='amountRows']//div//div")));
        String RemarksErrorMessage = remarksError.getText();
        Log.info("Message fetched");
        return RemarksErrorMessage;
    }


    public String getRemarksError()
    {
        Log.info("Trying to fetch Remarks Error message");
        WebElement remarksError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id = 'textBox']/following-sibling::div")));
        String RemarksErrorMessage = remarksError.getText();
        Log.info("Message fetched");
        return RemarksErrorMessage;
    }


    public String getPmtInstNoError()
    {
        Log.info("Trying to fetch Payment Instrument number Error message");
        WebElement PmtInstNoError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'payInstNmbrInput']/following-sibling::div")));
        String PmtInstNoErrorMessage = PmtInstNoError.getText();
        Log.info("Message fetched");
        return PmtInstNoErrorMessage;
    }


    public String getAmountError()
    {
        Log.info("Trying to fetch Amount Error message");
        WebElement amountError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class = 'form-control text-center ng-untouched ng-pristine ng-valid']/following-sibling::div")));
        String amountMessage = amountError.getText();
        return amountMessage;
    }


    public void clickSearchByResetButton() {
        Log.info("Trying to click Search Reset button..");
        WebElement rstBtn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'resetButton1']")));
        rstBtn.click();
        Log.info("User clicked Search Reset button");
    }


    public void clickResetButton() {
        Log.info("Trying to click Reset button..");
        WebElement rstBtn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'reset']//span")));
        rstBtn.click();
        Log.info("User clicked Reset button");
    }


    public Boolean getblankeTopUPAmount(){
        WebElement blankeTopUPAmount= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@class = 'form-control text-center ng-valid ng-untouched ng-pristine']")));
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



    public Boolean getBlankSearchByDropdown()
    {
        WebElement blankSearchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ng-select-container']")));
        String storedSearchInput = blankSearchInput.getAttribute("value");
        Log.info("Stored Search Input: "+storedSearchInput);
        if(storedSearchInput==null)
        {
            Log.info("Search Dropdown fields is blank");
            return true;
        }
        else{
            Log.info("Search Dropdown fields is not blank");
            return false;
        }
    }

    public Boolean getBlankSearchInput()
    {
        WebElement blankSearchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'searchCriteriaInput']")));
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
        WebElement transferStatusFailed= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@id,'vals failuremsg')]")));
        String trfStatus = transferStatusFailed.getText();
        Log.info("Transfer status fetched as : "+trfStatus);
        return trfStatus;
    }


    public void clickApproval1FOCHeading() {
        Log.info("Trying clicking on Approval 1 FOC Heading");
        WebElement apprv1Heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-1']//span")));
        apprv1Heading.click();
        Log.info("User clicked Approval 1 FOC Heading.");
    }


    public void clickApproval2FOCHeading() {
        Log.info("Trying clicking on Approval 2 FOC Heading");
        WebElement apprv1Heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-2']//span")));
        apprv1Heading.click();
        Log.info("User clicked Approval 2 FOC Heading.");
    }

    public void clickApproval3FOCHeading() {
        Log.info("Trying clicking on Approval 3 FOC Heading");
        WebElement apprv1Heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='approval-3']//span")));
        apprv1Heading.click();
        Log.info("User clicked Approval 3 FOC Heading.");
    }


    public void clickApproval1Transaction() {
        Log.info("Trying clicking on Approval 1 Transaction Link");
        WebElement apprv1txnHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[@id='approval1txn'])[2]")));
        apprv1txnHeading.click();
        Log.info("User clicked Approval 1 Transaction Link.");
    }


    public void clickApproval2Transaction() {
        Log.info("Trying clicking on Approval 2 Transaction Link");
        WebElement apprv1txnHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[@id='approval2txn'])[2]")));
        apprv1txnHeading.click();
        Log.info("User clicked Approval 2 Transaction Link.");
    }


    public void clickApproval3Transaction() {
        Log.info("Trying clicking on Approval 3 Transaction Link");
        WebElement apprv1txnHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//a[@id='approval3txn'])[2]")));
        apprv1txnHeading.click();
        Log.info("User clicked Approval 3 Transaction Link.");
    }

    public void clickFOCApprovalSingleOperationHeading() {
        try {
            Thread.sleep(3000);
        }
        catch(Exception e) {
            Log.info("Element is not expanded");
        }
        Log.info("Trying clicking on FOC Single Operation Heading..");
        WebElement FOCTransactionHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[@id = 'toggle-1-button'])[1]")));
        FOCTransactionHeading.click();
        Log.info("User clicked FOC Single Operation Heading.");
        try {
            Thread.sleep(3000);
        }
        catch(Exception e) {
            Log.info("Element is not expanded");
        }
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
        String categoryType = String.format("//ng-select[@labelforid = 'category']//span[text()=' %s']", Category);
        driver.findElement(By.xpath(categoryType)).click();
        Log.info("User selected Category : " + Category);
    }

    public void selectApprovalGeography(String geography) {
        Log.info("Trying to select the Geography...");
        WebElement geoDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-select[@labelforid = 'geography']")));
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
            Log.info("FOC Proceed button not clicked") ;
        }
    }

    public void enterSearch(String searchText) {
        Log.info("Trying to enter Search Text(Search by User name, Mobile number, Login ID, etc) ..");
        WebElement enterAmount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name = 'search']")));
        enterAmount.sendKeys(searchText);
        Log.info("User entered Search Text(Search by User name, Mobile number, Login ID, etc)" + searchText);
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
            Log.info("FOC Approve Transaction Button not clicked") ;
        }
    }


    public void enterExtTxnNo(String ExtTxnNo) {
        Log.info("Trying to enter External Transaction Number..");
        WebElement enterExtTxnNo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id ='externalTxnNum']")));
        enterExtTxnNo.sendKeys(ExtTxnNo);
        Log.info("User entered External Transaction Number" + ExtTxnNo);
    }
    
    public String getExtTxnNoError() {
    	Log.info("Trying to fetch approval external txn number error");
        WebElement errorMsgSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id ='externalTxnNum']/following-sibling::div/div"))) ;
        String errorMsg = errorMsgSearch.getText() ;
        Log.info("Fetched error : "+errorMsg);
        return  errorMsg;
    }


    public void clickApproveButton() {
        Log.info("Trying clicking on Approve Button");
        WebElement ApprvBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'approve']/span")));
        ApprvBtn.click();
        Log.info("User clicked Aprove Button");
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

    public String FOCApproveTransactionID()
    {
        Log.info("Trying to fetch Transaction ID..");
        WebElement FOCSuccessID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[@class='textLight']/following-sibling::b"))) ;
        String FOCSuccessBatchID = FOCSuccessID.getText() ;
        Log.info("FOC SUCCESS TRANSACTION ID : "+FOCSuccessBatchID);
        return  FOCSuccessBatchID;
    }

    public String actualApproveMessage(){
        Log.info("Trying to get Actual Message..");
        WebElement actualMessage= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'done']//h5")));
        String actualMsg = actualMessage.getText();
        Log.info("Actual Message fetched as : "+actualMsg);
        return actualMsg;
    }


    public void selectFOCApprovalTransactions() {
        Log.info("Trying to select FOC Transactions...");
        WebElement geoDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='profile']")));
        geoDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-dropdown-panel")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-dropdown-panel//div//div[3]"))).click() ;
        Log.info("User selected FOC Transactions ");
    }

    public boolean isFOCApproval1Visible() {
        try {
            Thread.sleep(3000);
            WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class = 'nested box opened']//a[@id='approval1txn']")));
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

    public void enterApprovalReferenceNo(String Reference)
    {
        Log.info("Trying to enter Reference Number..");
        WebElement enterReference = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'refNo']")));
        enterReference.sendKeys(Reference);
        Log.info("User entered Reference Number" + Reference);
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
            Log.info("FOC Reject Transaction Button not clicked") ;
        }
    }

    public void clickRejectButton() {
        Log.info("Trying clicking on Reject Button");
        WebElement ApprvBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'reject']/span")));
        ApprvBtn.click();
        Log.info("User clicked Reject Button");
    }

    public boolean isFOCApproval2Visible() {
        try {
            Thread.sleep(2000);
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


    public boolean isFOCApproval3Visible() {
        try {
            Thread.sleep(2000);
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

    public String invalidSearchBy()
    {
        Log.info("Trying to fetch Search error..");
        WebElement errorMsgSearch = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tbody[@id='approvalLevelFOCTableBody']//tr[@class = 'odd']//td[@class = 'dataTables_empty']"))) ;
        String errorMsg = errorMsgSearch.getText() ;
        Log.info("Fetched Search error : "+errorMsg);
        return  errorMsg;
    }

    public void spinnerWait() {
        Log.info("Waiting for spinner");
        try {
//            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
        	Thread.sleep(5000);
            Log.info("Waiting for spinner to stop");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
            Log.info("Spinner stopped");
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
        catch(Exception e) {
        	Log.info("Element not found");
        }
//        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loading-text']")));
//        Log.info("Spinner stopped");
    }
    
    public String getApprvalRefNoError() {
    	Log.info("Trying to fetch approval Referenece number error");
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id = 'refNo']/following-sibling::div/div"))) ;
        String errorMsgText = errorMsg.getText() ;
        Log.info("Fetched error : "+errorMsgText);
        return  errorMsgText;
    }

    public String getNoRecordsApprvalMsg() {
    	Log.info("Trying to fetch no records approval msg");
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tbody[@id='approvalLevelO2CTableBody']/tr/td"))) ;
        String errorMsgText = errorMsg.getText() ;
        Log.info("Fetched error : "+errorMsgText);
        return  errorMsgText;
    }


}

