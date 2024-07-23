package angular.pageobjects.RechargeBulk;

import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import org.openqa.selenium.WebElement;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RechargesBulk {

    WebDriver driver = null;
    WebDriverWait wait = null;


    public RechargesBulk(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait= new WebDriverWait(driver, 20);
    }

    public void clickRechargeHeading() {
    	Log.info("Trying to click on Recharge Heading Link...");
        WebElement rechargeHeading =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='rechargeMain']")));
        rechargeHeading.click();
        Log.info("User clicked Recharged Heading Link.");

    }


    public boolean isRechargeVisible() {
        try {
            WebElement expanded = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='rechargeMain']//span[@class='childmenucss1']")));
            if(expanded.isDisplayed())
                return false;
        }

        catch(Exception e) {
            return true;
        }

        return true;
    }


    public void clickRecharge() {
        Log.info("Trying to click on Recharge Link...");
        WebElement recharge = new WebDriverWait(driver,20).until(ExpectedConditions.elementToBeClickable(By.xpath("(//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='recharge'])[2]")));
        Actions action =new Actions(driver);
        action.moveToElement(recharge).click().build().perform();
        Log.info("User clicked Recharge Link");
    }


    public double getCurrentBalance() {
        try {
            WebElement currentBalance= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='maskWhite']//div[contains(@class,'balance')]")));
            boolean flag = false;
            String balance = null;
            double x = 0D;
            int i =0;
            while(flag !=true || i <10)
            {
                i++;
                balance = currentBalance.getText();
                if(!(balance==null || balance.equals("")))
                {
                    //x = Double.parseDouble(balance.replaceAll("\\p{Sc}|,", ""));
                	balance = balance.substring(1);
                    x = Double.parseDouble(balance.replaceAll(",", ""));
                    Log.info("Current Balance of user: "+x);
                    flag = true;
                }
            }
            return x;
        }
        catch(StaleElementReferenceException e) {
            //wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.xpath("//div[@class='maskWhite']//div[contains(@class,'balance')]"))));
            WebElement currentBalance= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='maskWhite']//div[contains(@class,'balance')]")));
            boolean flag = false;
            String balance = null;
            double x = 0D;
            int i =0;
            while(flag !=true || i <10)
            {
                i++;
                balance = currentBalance.getText();
                if(!(balance==null || balance.equals("")))
                {	
                	balance = balance.substring(1);
                    x = Double.parseDouble(balance.replaceAll(",", ""));
                    Log.info("Current Balance of user: "+x);
                    flag = true;
                }
            }
            return x;
        }
    }

    public void enterPin(String ChnUsrPin) {
        Log.info("User will enter Channel User Pin ");
        WebElement enterYourPin= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//input[@formcontrolname='pin']")));
        enterYourPin.sendKeys(ChnUsrPin);
        Log.info("User entered Channel User Pin: " + ChnUsrPin);
    }


    public void clickRechargeButton() {
        WebElement rechargeButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='modal-content']//div//button[@type='button'])[2]")));
        rechargeButton.click();
        Log.info("User clicked Recharge button");
    }


    public boolean successPopUPVisibility() {

        boolean result = false;
        try {
            WebElement successPopUP= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@class='success']")));
            if (successPopUP.isDisplayed()) {
                result = true;
            }
        } catch (NoSuchElementException e) {
            result = false;
        }
        return result;

    }

    public String transferID(){
        Log.info("Trying to get transfer ID.");
        WebElement transferID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@id='vals txnidsuccess']")));
        String trfID = transferID.getText();
        Log.info("Transfer ID fetched as : "+trfID);
        return trfID;
    }


    public Boolean checkDisabledRechargeButton()
    {
        Log.info("User trying to click Recharge Button");
        Boolean flag = false;
        WebElement PINSubmitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'rechrgbutton']//span")));
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


    public Boolean checkInternetDisabledRechargeButton()
    {
        Log.info("User trying to click Recharge Button");
        Boolean flag = false;
        WebElement PINSubmitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'rechargeintBulk']//span")));
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



    public String UploadStatus()
    {
        Log.info("Trying to get Upload Status.");
        WebElement uploadStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@id ='vals failuremsg']")));
        String fetchedUploadStatus = uploadStatus.getText();
        Log.info("Upload status fetched as : " + fetchedUploadStatus);
        return fetchedUploadStatus;
    }



    public String transferStatus(){
        Log.info("Trying to get transfer Status.");
        String trfStatus = null;
        try {
            WebElement transferStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@class='recharge-successful']")));
            trfStatus = transferStatus.getText();
            Log.info("Transfer status fetched as : " + trfStatus);
        }
        catch(TimeoutException e)
        {
            WebElement transferStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@id = 'vals failuremsg']")));
            trfStatus = transferStatus.getText();
            Log.info("Transfer status fetched as : " + trfStatus);
        }
        return trfStatus;
    }


    public void clickC2SBulkOperationHeading() {
        Log.info("Trying clicking on C2S Bulk Operation Heading");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='msisdn']")));
        WebElement c2cBulkOperationHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-button-toggle//button[@id='bulk1-button']")));
        c2cBulkOperationHeading.click();
        Log.info("User clicked C2S Bulk Operation Heading.");
    }


    public void clickBulkPrepaidRecharge() {
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Log.info("User Trying to click Bulk Prepaid Recharge");
        WebElement BulkPrepaidRecharge= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div//a[@href='/pretups-ui/recharge/bulkRecharge']")));
        BulkPrepaidRecharge.click();
        Log.info("User clicked Bulk Prepaid Recharge Link.");
    }


    public void clickBulkInternetRecharge() {
        Log.info("User Trying to click Bulk Internet Recharge");
        WebElement BulkInternetRecharge= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ng-star-inserted']//a[@href='/pretups-ui/recharge/bulkInternet']//span]")));//div//a[@href='/pretups-ui/recharge/bulkInternet'
        BulkInternetRecharge.click();
        Log.info("User clicked Bulk Internet Recharge Link.");
    }

    public void clickMakeAsGiftCheckbox() {
        Log.info("Trying to click on Make this a gift checkbox...");
        WebElement MakeAsGiftCheckbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-checkbox[@id = 'makethisGift']//div")));
        MakeAsGiftCheckbox.click();
        Log.info("User clicked Make this a gift checkbox");
    }


    public void clickDownloadUserTemplateIcon(){
        Log.info("Trying to click User Template icon...");
        WebElement downloadBlankUserTemplateIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='userTemplateId']")));
        downloadBlankUserTemplateIcon.click();
        try{
            Thread.sleep(2000) ;
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
        Log.info("User clicked Template Download Button.");
    }


    public void clickDownloadUserListIcon() {
        Log.info("Trying to click Mobile Number List icon...");
        WebElement userList = new WebDriverWait(driver,20).until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='userListId']")));
        Actions action =new Actions(driver);
        action.moveToElement(userList).click().build().perform();
        try{
            Thread.sleep(2000);
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
        Log.info("User clicked Mobile Number List Download Button.");
    }

   /* public void clickDownloadUserListIcon() {
        Log.info("Trying to click Mobile Number List icon...");
        WebElement downloadUserListIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='userListId']")));
        downloadUserListIcon.click();
        try{
            Thread.sleep(2000);
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
        Log.info("User clicked Mobile Number List Download Button.");
    }
*/
    public void uploadFile(String filePath) {
        try {
            Log.info("Uploading File... ");
            driver.findElement(By.xpath("//div[@class='clickarea ng-star-inserted']//label[@class='company-copy']")).click();

            Robot robot = new Robot();

            StringSelection ss = new StringSelection(filePath);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

            robot.delay(3000);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(3000);
        }
        catch(Exception e){
            Log.debug("<b>File not uploaded:</b>"+ e);
        }
    }



    public void clickScheduleNowCheckbox() {
        Log.info("Trying to click on Schedule Now checkbox...");
        WebElement scheduleNowCheckbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id= 'isSchedule']//label[@class = 'mat-checkbox-layout']")));
        scheduleNowCheckbox.click();
        Log.info("User clicked Schedule Now checkbox");
    }

    public void clickScheduleNowInternetCheckbox() {
        Log.info("Trying to click on Schedule Now checkbox...");
        WebElement scanEle = new WebDriverWait(driver,20).until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-checkbox[@id= 'isSched']//label[@class = 'mat-checkbox-layout']")));
        Actions action =new Actions(driver);
        action.moveToElement(scanEle).click().build().perform();
        Log.info("User clicked Schedule Now checkbox");
    }


  /*  public void clickScheduleNowInternetCheckbox() {
        Log.info("Trying to click on Schedule Now checkbox...");
        WebElement scheduleNowCheckbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-checkbox[@id= 'isSched']//label[@class = 'mat-checkbox-layout']")));
        scheduleNowCheckbox.click();
        Log.info("User clicked Schedule Now checkbox");
    }*/

    public void selectOccurence(String occurrence) {
        Log.info("Trying to select the occurrence...");
        WebElement occurrencedropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'row marginFailTop']//ng-select[@formcontrolname = 'occurrence']")));
        occurrencedropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ng-dropdown-panel-items scroll-host']//div[@role='option']")));
        String occurrenceType = String.format("//form[@id = 'control']//div[@role= 'option']//span[text()='%s']", occurrence);
        driver.findElement(By.xpath(occurrenceType)).click();
        Log.info("User selected Occurrence : " + occurrence);
    }

    public void enterNoofDays(String Days)
    {
        Log.info("Trying to enter the Number of iterations...");
        WebElement noofDays = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'col-xl-2']//input[@formcontrolname = 'numberOf']")));
        noofDays.clear();
        noofDays.sendKeys(Days);
        Log.info("Number of iterations entered : " +Days);
    }





    public String getLatestFilefromDir(String dirPath) {
        Log.info("Getting File Path..");


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];

            }
        }
        String filePath = lastModifiedFile.getPath();
        Log.info("Latest File Path : " + filePath);
        return filePath;
    }

    public String getLatestFileNamefromDir(String dirPath) {
        Log.info("Getting File Path..");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];

            }
        }
        return lastModifiedFile.getName() ;
    }

    public int noOfFilesInDownloadedDirectory(String path)
    {
        Log.info("Trying to Count Files in Directory "+path);
        File dir = new File(path);
        File[] dirContents = dir.listFiles();
        int noOfFiles = dirContents.length;
        Log.info("Number of files in directory : "+noOfFiles);
        return noOfFiles;
    }

    public void deleteAllFiles()
    {
        Log.info("Deleting all the files..");
        String PathOfFile = _masterVO.getProperty("C2CBulkTransferPath") ;
        int noOfFiles = noOfFilesInDownloadedDirectory(PathOfFile) ;
        if(noOfFiles > 0)
        {
            Log.info("FILES IN DIR = " + noOfFiles) ;
            ExcelUtility.deleteFiles(PathOfFile) ;
        }
        Log.info("Deleted all files..");
    }


    public void clickRechargeButtonForBulk() {
        Log.info("Trying to click on Recharge Button");
        WebElement rechargeDone= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'rowRight marginBottom']//button[@id = 'rechargeGift']")));
        rechargeDone.click();
        Log.info("User clicked Recharge Button");
    }


    public String rechargeFailedStatus(){
        Log.info("Trying to get Recharge Status.");
        WebElement rechargeStatus= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@class='recharge-failure marginFailTop']")));
        String rcStatus = rechargeStatus.getText();
        Log.info("Recharge status fetched as : "+rcStatus);
        return rcStatus;
    }

    public String rechargeFailedReason(){
        Log.info("Trying to get Recharge Reason.");
        WebElement rechargeReason= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@class='recharge-failure marginFailTop']")));
        String rcReason = rechargeReason.getText();
        Log.info("Recharge Reason fetched as : "+rcReason);
        return rcReason;
    }

    public void clickDoneButton() {
        WebElement donButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='done']")));
        donButton.click();
        Log.info("User clicked Done Recharge button");
    }


    public void clickRetryButton() {
        WebElement retryButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='anotherRecharge']//span")));
        retryButton.click();
        Log.info("User clicked Retry button");
    }

    public void clickResetButton() {
        Log.info("Trying to click on Reset Button..");
        WebElement resetButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'resetGift']//span[@class = 'mat-button-wrapper']")));
        resetButton.click();
        Log.info("User clicked Reset Button");
    }

    public Boolean getblankScheduleDate(){
//        WebElement batchName= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id = 'control']//input[@formcontrolname = 'scheduleDate']")));
       
    	WebElement batchName= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id = 'control']//primeng-datepicker[@formcontrolname = 'scheduleDate']//input")));
    	String storedbatchName = batchName.getAttribute("value");
        Log.info("Stored Batch Name: "+storedbatchName);
        if(storedbatchName.isEmpty())
        {
            Log.info("Schedule Date is blank");
            return true;
        }
        else{
            Log.info("Schedule Date is not blank");
            return false;
        }

    }



    public Boolean getblankNoofDays(){
        WebElement NoofDays= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'col-xl-2']//input[@formcontrolname = 'numberOf']")));
        String storedNoofDays = NoofDays.getAttribute("value");
        Log.info("Stored Batch Name: "+storedNoofDays);
        if(storedNoofDays.isEmpty())
        {
            Log.info("No of Days is blank");
            return true;
        }
        else{
            Log.info("No of Days is not blank");
            return false;
        }

    }


    public Boolean getblankOccurrence(){
        WebElement Occurrence= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'col-xl-2']//input[@formcontrolname = 'numberOf']")));
        String storedOccurrence = Occurrence.getAttribute("value");
        Log.info("Stored Batch Name: "+storedOccurrence);
        if(storedOccurrence.isEmpty())
        {
            Log.info("Occurrence is blank");
            return true;

        }
        else{
            Log.info("Occurrence is not blank");
            return false;
        }

    }

    public java.util.List<WebElement> blankErrorMessages() {
        Log.info("Trying to get Error Validation messaged from GUI");
        java.util.List<WebElement> validationErrors= wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//form[@id = 'control']//div[@class = 'ng-star-inserted']")));
        return validationErrors ;
    }





    public String fileUploadTypeErrorMessage() {
        Log.info("Trying to get Error Validation messaged from GUI");
        WebElement InvalidFileFormat= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id = 'control']//div[@class = 'invalid-file-format ng-star-inserted']//div")));
        String FileTypeErrorMessage = InvalidFileFormat.getAttribute("textContent") ;
        Log.info("BATCH VALIDATION ERROR ON GUI : "+FileTypeErrorMessage);
        return FileTypeErrorMessage ;
    }


    public String fetchbatchID(){
        Log.info("Trying to fetch batch ID..");
        WebElement batchID= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'success-popup']//div[@id = 'vals txnidsuccess']")));
        String storedBatchID = batchID.getText();
        Log.info("Stored Batch ID: "+storedBatchID);
        return storedBatchID;
    }




    public void clickCopyButton() {
        Log.info("Trying to click on Copy Button..");
        WebElement copyButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'copyId']//span[@class = 'not-copied']")));
        copyButton.click();
        Log.info("User clicked Copy Button");
    }


    public String fetchFailedReason(){
        Log.info("Trying to fetch batch ID..");
        WebElement failedReason= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'modal-content']//div[@class = 'col-xl']//label[@class = 'detailusr1']")));
        String storedFailedReason = failedReason.getText();
        Log.info("Failed Reason on UI: "+storedFailedReason);
        return storedFailedReason;
    }









}
