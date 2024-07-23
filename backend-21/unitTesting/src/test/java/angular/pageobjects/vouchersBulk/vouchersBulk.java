package angular.pageobjects.vouchersBulk;

import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class vouchersBulk {

    WebDriver driver = null;
    WebDriverWait wait = null;


    public vouchersBulk(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait=new WebDriverWait(driver, 20);
    }

    public void clickRecharge() {
        Log.info("Trying to click on Recharge Link...");
        WebElement recharge = new WebDriverWait(driver,20).until(ExpectedConditions.elementToBeClickable(By.xpath("//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='recharge']")));
        Actions action =new Actions(driver);
        action.moveToElement(recharge).click().build().perform();
        Log.info("User clicked Recharge Link");
    }

    public void clickRechargeHeading() {
        WebElement rechargeHeading =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav[@class='sidebar']//div[@class='nested-menu']//a[@id='rechargeMain']")));
        rechargeHeading.click();
        Log.info("User clicked Recharged Heading Link.");

    }


    public boolean isRechargeVisible() {
        try{
            Thread.sleep(2000);
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
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

    public void clickC2SBulkOperationHeading() {
        Log.info("Trying clicking on C2S Bulk Operation Heading");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id='control']//input[@formcontrolname='msisdn']")));
        WebElement c2cBulkOperationHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-button-toggle//button[@id='bulk1-button']")));
        c2cBulkOperationHeading.click();
        try{
            Thread.sleep(2000);
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
        Log.info("User clicked C2S Bulk Operation Heading.");
    }

    public void clickBulkDVDRecharge() {
        Log.info("User Trying to click Bulk Prepaid Recharge");
        WebElement BulkPrepaidRecharge= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div//a[@href='/pretups-ui/recharge/bulkDvd']")));
        BulkPrepaidRecharge.click();
        try{
            Thread.sleep(2000);
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
        Log.info("User clicked Bulk Prepaid Recharge Link.");
    }


    public void clickDownloadMasterSheetIcon() {
        Log.info("Trying to click Master SHeet icon...");
        WebElement userList = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='userListId']")));
        userList.click();
        try{
            Thread.sleep(2000);
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
        Log.info("User clicked Master Sheet Download Button.");
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
        Log.info("Getting File Name Path..");
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
        Log.info("Latest File Path : " + lastModifiedFile.getName());
        return lastModifiedFile.getName();
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
        String PathOfFile = _masterVO.getProperty("DVDBulkRechargePath") ;
        int noOfFiles = noOfFilesInDownloadedDirectory(PathOfFile) ;
        if(noOfFiles > 0)
        {
            Log.info("FILES IN DIR = " + noOfFiles) ;
            ExcelUtility.deleteFiles(PathOfFile) ;
        }
        Log.info("Deleted all files..");
    }


    public void clickRechargeIcon() {
        WebElement rechargeIcon= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'rechargeGift']//span[@class = 'mat-button-wrapper']")));
        rechargeIcon.click();
        Log.info("User clicked Recharge button");
    }


    public void enterPin(String ChnUsrPin) {
        Log.info("User will enter Channel User Pin ");
        WebElement enterYourPin= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//input[@formcontrolname='pin']")));
        enterYourPin.sendKeys(ChnUsrPin);
        Log.info("User entered Channel User Pin ");
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
        Log.info("Trying to get Transfer ID.");
        WebElement transferID=  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@id='vals txnidsuccess']")));
        String trfID = transferID.getText();
        Log.info("Transfer ID fetched as : "+trfID);
        return trfID;
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
            WebElement transferStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id = 'vals failuremsg']")));
            trfStatus = transferStatus.getText();
            Log.info("Transfer status fetched as : " + trfStatus);
        }
        return trfStatus;
    }

    public void clickDoneButton() {
        WebElement doneButton= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='done']")));
        doneButton.click();
        Log.info("User clicked Done Recharge button");
    }


    public void clickResetButton() {
        Log.info("Trying to click on Reset Button..");
        WebElement resetButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'resetGift']//span")));
        resetButton.click();
        Log.info("User clicked Reset Button");
    }


    public boolean checkUpload()
    {
        Log.info("Checking visibility of Upload...");
        Boolean flag;
        flag = driver.findElements(By.xpath("//div[@class='clickarea ng-star-inserted']//label[@class='company-copy']")).size() > 0;
        if(!flag)
        {
            Log.info("Upload button after reset not available.");
        }
        else{
            Log.info("Upload button after reset still available.");
        }
        return flag;
    }


    public String fetchFailedReason(){
        Log.info("Trying to fetch batch ID..");
        WebElement failedReason= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class = 'modal-content']//div[@class = 'col-xl']//label[@class = 'detailusr1 ']")));
        String storedFailedReason = failedReason.getText();
        Log.info("Failed Reason on UI: "+storedFailedReason);
        return storedFailedReason;
    }


    public void clickCopyButton() {
        Log.info("Trying to click on Copy Button..");
        WebElement copyButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id = 'copyId']")));
        copyButton.click();
        Log.info("User clicked Copy Button");
    }


    public Boolean checkDisabledRechargeButton()
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


    public String fileUploadTypeErrorMessage() {
        Log.info("Trying to get Error Validation messaged from GUI");
        WebElement InvalidFileFormat= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@id = 'control']//div[@class = 'invalid-file-format ng-star-inserted']//div")));
        String FileTypeErrorMessage = InvalidFileFormat.getAttribute("textContent") ;
        Log.info("BATCH VALIDATION ERROR ON GUI : "+FileTypeErrorMessage);
        return FileTypeErrorMessage ;
    }

    public String UploadStatus()
    {
        Log.info("Trying to get Upload Status.");
        WebElement uploadStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='modal-content']//div[@id ='vals failuremsg']")));
        String fetchedUploadStatus = uploadStatus.getText();
        Log.info("Upload status fetched as : " + fetchedUploadStatus);
        return fetchedUploadStatus;
    }






}
