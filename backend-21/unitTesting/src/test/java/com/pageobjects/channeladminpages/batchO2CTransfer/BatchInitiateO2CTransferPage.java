package com.pageobjects.channeladminpages.batchO2CTransfer;

import java.awt.*;
import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BatchInitiateO2CTransferPage {

    @FindBy(name = "backButton")
    private WebElement backButton;

    @FindBy(name = "distributorType")
    private WebElement DistributionType;

    @FindBy(name="distributorMode")
    private WebElement distributionMode;

    @FindBy(name = "userCode")
    private WebElement mobileNumber;

    @FindBy(name = "productCode")
    private WebElement productType1;

    @FindBy(name = "geographicalDomainCode")
    private WebElement geographyDomain;

    @FindBy(name = "domainCode")
    private WebElement domain;

    @FindBy(xpath="//a[@href[contains(.,'downloadlist')]]")
    private WebElement downloadUserList;

    @FindBy(xpath="//a[@href[contains(.,'downloadtemplate')]]")
    private WebElement downloadTemplate;

    @FindBy(name = "batchName")
    private WebElement enterBtchNme;

    @FindBy(name = "defaultLang")
    private WebElement lang1;

    @FindBy(name = "secondLang")
    private WebElement lang2;

    @FindBy(name = "confirmButton")
    private WebElement confirmButton;

    @FindBy(name = "file")
    private WebElement chooseFiles;

    @FindBy(name = "categoryCode")
    private WebElement category;

    @FindBy(xpath = "productType")
    private WebElement productType2;

    @FindBy(name = "submitButton")
    private WebElement submitButton;

    @FindBy(xpath = "resetbutton")
    private WebElement resetbutton;

    @FindBy(xpath = "//ul/li")
    private WebElement message;

    @FindBy(xpath = "//ol/li")
    private WebElement errorMessage;

    @FindBy(xpath = "(//table[@class='back']//tr//td)[6]")
    private WebElement viewError;

    @FindBy(xpath="//a[@href[contains(.,'loadDownloadfile')]]")
    private WebElement downloadfile;

    @FindBy(xpath="//a[contains(text(), 'errors')]")
    private WebElement viewErrorsButton;

    WebDriver driver= null;
    JavascriptExecutor jsDriver;
    WebDriverWait wait;

    public BatchInitiateO2CTransferPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait= new WebDriverWait(driver, 30);
        jsDriver = (JavascriptExecutor)driver;
    }

    public String getMessage(){
        String Message;
        Log.info("Trying to fetch Message");
        Message = message.getText();
        Log.info("Message fetched successfully as: " + Message);
        return Message;
    }

    public String getErrorMessage() {
        String Message = null;
        Log.info("Trying to fetch Error Message");
        try {
            Message = errorMessage.getText();
            Log.info("Error Message fetched successfully");
        }
        catch (org.openqa.selenium.NoSuchElementException e) {
            Log.info("Error Message Not Found");
        }
        return Message;
    }

    public void selectGeographyDomain(String GeographyDomain) {
        Select select = new Select(geographyDomain);
        select.selectByVisibleText(GeographyDomain);
        Log.info("User selected Geography Domain." +GeographyDomain);
    }
    public void selectDomain(String Domain) {
        Select select = new Select(domain);
        select.selectByVisibleText(Domain);
        Log.info("User selected Domain." +Domain);
    }
    public void selectCategory(String Category) {
        Select select = new Select(category);
        select.selectByVisibleText(Category);
        Log.info("User selected Category." +Category);
    }



    public void clickSubmitButton() {
        submitButton.click();
        Log.info("User clicked Submit button");
    }

    public void clickResetButton() {
        resetbutton.click();
        Log.info("User clicked Reset button");
    }

    public boolean isSelectProductTypeVisible() {
        boolean selectDropdownVisible = driver.findElements(By.xpath("//select[@name='productCode']")).size() != 0;
        boolean flag = false;
        if (selectDropdownVisible == true){
            flag = true;
            Log.info("Element for Product Type is present");
        }
        else {
            flag = false;
            Log.info("Element for Product Type is not present");
        }
        return flag;
    }

    public void selectProductType1(String index) {
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
        int prodRowCount = ExcelUtility.getRowCount();

        String VOUCHER_TRACKING_ALLOWED = DBHandler.AccessHandler.getPreference(null, _masterVO.getProperty(MasterI.NETWORK_CODE), "VOUCHER_TRACKING_ALLOWED");
        if (VOUCHER_TRACKING_ALLOWED == null)
            VOUCHER_TRACKING_ALLOWED = "FALSE";

        if (prodRowCount > 1 || VOUCHER_TRACKING_ALLOWED.equalsIgnoreCase("TRUE")) {
            Select select = new Select(productType1);
            select.selectByValue(index);
            Log.info("User selected Product Type: " + index);
        } else if (prodRowCount == 1) {
            Log.info("Only single product exists: " + index);
        } else {
            Log.info("No product exists.");
        }
    }

    public void clickDownloadUserList() {
        downloadUserList.click();
        Log.info("User clicked Download User List");
    }

    public void clickDownloadFileTemplate() {
        downloadTemplate.click();
        try{
            Thread.sleep(3000);
        }catch (InterruptedException interrupted){
            interrupted.printStackTrace();
        }
        Log.info("User clicked Download File template");
    }

    public void enterBatchName(String batchName){
        Log.info("Trying to enter Batch Name..");
        enterBtchNme.sendKeys(batchName);
        Log.info("Batch Entered: " + batchName +".");
    }

    public void clickChooseFile(){
        Log.info("Trying to click on Choose File..");
        chooseFiles.click();
        Log.info("Clicked on Choose Files.");
    }

    public String getLatestFilePathfromDir(String dirPath) {
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

    public void uploadFile(String filePath) {
        try {
            Log.info("Uploading File... ");
            Thread.sleep(3000);
            WebElement chooseFiles = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='file']")));
            new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[type='file']"))).sendKeys(filePath);
            Thread.sleep(3000);
        }
        catch(Exception e){
            Log.debug("<b>File not uploaded:</b>"+ e);
        }
    }

    public void enterLanguage1(){
        Log.info("Trying to enter language1..");
        lang1.sendKeys("Language1");
        Log.info("Entered language1.");
    }

    public void enterLanguage2(){
        Log.info("Trying to enter language2..");
        lang2.sendKeys("Language2");
        Log.info("Entered language2.");
    }

    public void clickConfirmButton() {
        confirmButton.click();
        Log.info("User clicked Confirm button");
    }

    public void clickBackButton() {
        backButton.click();
        Log.info("User clicked Back button");
    }

    public void clickViewErrors(){
        viewErrorsButton.click();
        Log.info("User clicked on View Error button");
    }

    public String getViewErrorMessage() {
        String Message = null;
        Log.info("Trying to fetch Error Message");
        try {
            Message = viewError.getText();
            Log.info("Error Message fetched successfully");
        }
        catch (org.openqa.selenium.NoSuchElementException e) {
            Log.info("Error Message Not Found");
        }
        driver.close();
        return Message;
    }






}
