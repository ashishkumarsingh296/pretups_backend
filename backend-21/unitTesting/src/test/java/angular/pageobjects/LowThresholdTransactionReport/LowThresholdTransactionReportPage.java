package angular.pageobjects.LowThresholdTransactionReport;

import com.gargoylesoftware.htmlunit.javascript.background.DefaultJavaScriptExecutor;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.formula.functions.Today;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LowThresholdTransactionReportPage {

    WebDriver driver = null;
    WebDriverWait wait = null;


    public LowThresholdTransactionReportPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait= new WebDriverWait(driver, 20);
    }
    
    
    			/* -----------------------------  E		L	E	M	E	N	T 		L	O	C	A	T	O	R	S  -------------------------------- */
   
    
    public void clickLowThresholdTransactionReportlink() throws InterruptedException {
        WebElement lowThresholdTransactionReportlink =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id = 'redirectLowThresholdTransactionReportPage']/em[@class = 'fa fa-angle-right']")));
        lowThresholdTransactionReportlink.click();
        Thread.sleep(2000);
        Log.info("User clicked on Low Threshold Transaction Report Link.");
    }
    
    public boolean isLowThresholdTransactionReportTextVisible() {
    	
    	boolean result = false;
       	try {
        	WebElement lowThresholdTransactionReportText =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[text() = ' Low Threshold & Transaction Report ']")));
            if(lowThresholdTransactionReportText.isDisplayed())
                result = true;
            String text=lowThresholdTransactionReportText.getText();
            Log.info("Low Threshold Transaction Report Text is displayed."+text);

        }

        catch(Exception e) {
            result = false;
        }
        Log.info("Low Threshold Transaction Report Text is displayed.");
        return result;
    }
      
    public void clickProceedButton() throws InterruptedException {
    	WebElement proceedButton =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@name='Proceed']")));
    	proceedButton.click();
    	Thread.sleep(3000);
        Log.info("User clicked on Proceed button.");
    }
    
    public void clickUserProfile() throws InterruptedException {
    	WebElement userProfile =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[a[text()=' Help ']]/following-sibling::li/a")));
    	userProfile.click();
    	Thread.sleep(3000);
        Log.info("User clicked on userProfile.");
    }
    
    public void clickOnLogout() {
    	WebElement logout =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@id='logOut']")));
    	logout.click();
        Log.info("User clicked on logout button, logged out succesfully");
    }
    
    public void clickHidelink() {
        WebElement hideLink =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@type = 'button' and @value='Hide']")));
        hideLink.click();
        Log.info("User clicked on Hide Link.");
    }
    
    public void clickShowlink() {
        WebElement showLink =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@type = 'button' and @value='Show']")));
        showLink.click();
        Log.info("User clicked on Show Link.");
    }
        
    public void clickDateRangeField() throws InterruptedException {
        WebElement dateRangeField =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder= 'dd/mm/yyyy - dd/mm/yyyy']")));
        dateRangeField.click();
        dateRangeField.click();
        Thread.sleep(1000);
        Log.info("User clicked on Date Range Field.");
        
//        String dateVal="14-06-2021";
//        selectDateByJS(driver, dateRangeField, dateVal);
        
       }
/*   
    public static void selectDateByJS(WebDriver driver, WebElement element, String dateVal){
    	JavascriptExecutor js = ((JavascriptExecutor) driver);
        js.executeScript("arguments[0].setAttribute('value','"+dateVal+"');", element);
    }
*/      
    // choose Month from dropdown
    public void selectMonth(String paraMonth) throws InterruptedException {
            WebElement eleToSelectMonth =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div/select[1]")));
    		
    		Select sel = new Select(eleToSelectMonth);
    		sel.selectByValue(paraMonth);
            eleToSelectMonth.click();
    		Thread.sleep(10000);
            Log.info("User clicked on month value.");
        }
    
    // choose Year from dropdown
    public void selectYear(String paraYear) throws InterruptedException {
        WebElement eleToSelectYear =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div/select[2]")));
		
		Select sel = new Select(eleToSelectYear);
	//	sel.selectByValue("2020");
		sel.selectByVisibleText(paraYear);
        eleToSelectYear.click();
		Thread.sleep(10000);
        Log.info("User clicked on year value.");
    }
        
    // choose from date	
    public void selectFromDate() throws InterruptedException {
       WebElement eleToSelectfromDate =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tbody/tr/td[3]/a[text()='1']")));
       eleToSelectfromDate.click();
       Thread.sleep(10000);          	         
       Log.info("User clicked on fromDate field.");
        }
    
    // choose toDate	
    public void selectToDate() throws InterruptedException {
        WebElement eleToSelectToDate =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tbody/tr[2]/td[7]/a[text()='12']")));
        eleToSelectToDate.click();
        Thread.sleep(10000);
        Log.info("User clicked on fromDate field.");
    }
   
     
/*   
    WebElement date = driver.findElementBy.xpath("//input[@placeholder= 'dd/mm/yyyy - dd/mm/yyyy']"));
    String dateVal="01-05-2021";

    selectDateByJS(driver, date, dateVal);

    public static void selectDateByJS(WebDriver driver, WebElement element, String dateVal){
    	javascriptExecutor js = ((javascriptExecutor) driver);
    	js.executeScript("arguments[0].setAttribute('value','"+dateVal+"');", element);
    }
*/   
    public boolean isdownloadButtonVisible() {
    	
    	boolean result = false;
       	try {
        	WebElement passbookPageText =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='download_btn']")));
            if(passbookPageText.isDisplayed())
                result = true;
        }

        catch(Exception e) {
            result = false;
        }
       Log.info("Download button is displayed.");
        return result;
    }
    
    public boolean isEditColumnButtonVisible() {
    	
    	boolean result = false;
       	try {
        	WebElement passbookPageText =wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@id='editColumn']")));
            if(passbookPageText.isDisplayed())
                result = true;
        }

        catch(Exception e) {
            result = false;
        }
      Log.info("Edit Column button is displayed.");
        return result;
    }
 
}