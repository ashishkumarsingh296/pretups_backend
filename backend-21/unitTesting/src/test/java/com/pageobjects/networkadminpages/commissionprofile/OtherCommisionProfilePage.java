package com.pageobjects.networkadminpages.commissionprofile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.utils.Log;
import com.utils._masterVO;

public class OtherCommisionProfilePage {


		@FindBy(name = "commissionType")
		private WebElement commissionType;

		@FindBy(name = "commissionTypeValue")
		private WebElement commissionTypeValue;

		@FindBy(name = "selectCommProfileSetID")
		private WebElement commissionName;
		
		@FindBy(name = "addType")
		private WebElement addTypeBtn;
		
		@FindBy(name = "edit")
		private WebElement modifyTypeBtn;
		
		@FindBy(name = "view")
		private WebElement viewTypeBtn;

		@FindBy(name = "o2cFlag")
		private WebElement o2cFlag;
		
		@FindBy(name = "c2cFlag")
		private WebElement c2cFlag;
			
		@FindBy(name = "profileName")
		private WebElement profileName;
		
		private WebElement fromRangeWb;
		private WebElement toRangeWb;
		private WebElement typeWb;
		private WebElement rateWb;
		
		@FindBy(xpath="//input[@name[contains(.,'startRangeAsString')]]")
		private List<WebElement> slabSize;

		@FindBy(name = "addCommission")
		private WebElement addCommissionBtn;
		
		@FindBy(name ="confirm")
		private WebElement confirmBtn;

		@FindBy(xpath = "//ul/li")
		private WebElement message;
		
		@FindBy(xpath = "//tr/td/ul/li")
	    WebElement UIMessage;

	    @FindBy(xpath = "//tr/td/ol/li")
	    WebElement errorMessage;


		WebDriver driver = null;
		WebDriverWait wait;
		
		
		final String row = "commSlabsListIndexed";
		final String fromRange = "startRangeAsString";
		final String toRange = "endRangeAsString";
		final String type = "commType";
		final String rate = "commRateAsString";
		final String pctType = _masterVO.getProperty("OCPPct");
		int pctRate = Integer.parseInt(_masterVO.getProperty("OCPPctRate"));
		final String amtType = _masterVO.getProperty("OCPAmt");
		int amtRate = Integer.parseInt(_masterVO.getProperty("OCPAmtRate"));
		
		public OtherCommisionProfilePage(WebDriver driver) {
			this.driver = driver;
			wait=new WebDriverWait(driver,5);
			PageFactory.initElements(driver, this);
		}

		
		public void selectType(String text) {
			Log.info("Selecting commission type: " + text);
			try {
				Select typeSel = new Select(commissionType);
				typeSel.selectByVisibleText(text);
			}
			catch(Exception e) {
				Log.info(e.getMessage());
			}
		}
		
		public void selectTypeCommissionValue(String text) {
			Log.info("Selecting commission type value: " + text);
			try {
				Select valueSel = new Select(commissionTypeValue);
				valueSel.selectByVisibleText(text);
			}
			catch(Exception e) {
				Log.info(e.getMessage());
			}
		}
		
		public void selectTypeCommissionName(String text) {
			Log.info("Selecting commission name: " + text);
			try {
				Select valueSel = new Select(commissionName);
				valueSel.selectByVisibleText(text);
			}
			catch(Exception e) {
				Log.info(e.getMessage());
			}
		}
		
		public ArrayList<String> getOtherCommValuesOfType(){
			Select valueSel = new Select(commissionTypeValue);
			Log.info("Trying to get values");
			ArrayList<String> values = new  ArrayList<String>(); 
			List<WebElement> valueWB = valueSel.getOptions();
			for(WebElement wb : valueWB) {
				if(!"select".equalsIgnoreCase(wb.getText()))
					values.add(wb.getText());
			}
			Log.info("values: " + values);
			return values;
		}
		
		public void clickAddOtherCP() {
			Log.info("Trying to click add commision type value");
			try {
				addTypeBtn.click();
			}
			catch(Exception e) {
				Log.info(e.getMessage());
			}
		}
		
		public void checkO2CBox() {
			Log.info("Trying to check O2C box");
			try {
				if(!o2cFlag.isSelected())
					o2cFlag.click();
			}
			catch(Exception e) {
				Log.info(e.getMessage());
			}
		}
		
		public void checkC2CBox() {
			Log.info("Trying to check C2C box");
			try {
				if(!c2cFlag.isSelected())
					c2cFlag.click();
			}
			catch(Exception e) {
				Log.info(e.getMessage());
			}
		}
		
		public void enterOtherCommissionName(String text) {
			Log.info("Input commission profile name: " + text);
			try {
				profileName.clear();
				profileName.sendKeys(text);
			}
			catch(Exception e) {
				Log.info(e.getMessage());
			}
		}
		
		public int totalSlabs(){
			int slabCount = slabSize.size();
			Log.info("Number of slabs: "+slabCount);
			return slabCount;
		}
		
		public void processOtherCommissionValues(int rows) {
			Log.info("Entered to process values");
			ArrayList<HashMap<String,String>> dataMap = new ArrayList<>();
			HashMap<String,String> rowValue;

			int start = Integer.parseInt(_masterVO.getProperty("OCPMinValue").trim());
			int end = Integer.parseInt(_masterVO.getProperty("OCPMaxValue").trim());
			int range = (end-start)/rows;
			
			for(int i = 0 ; i < rows ; i++) {
				rowValue = new HashMap<String,String>();
				rowValue.put("fromRange", Integer.toString(start));
				start += range ;
				rowValue.put("toRange", Integer.toString(start++));
				
				if(i%2 == 0) {
					rowValue.put("type", pctType);
					rowValue.put("rate", Integer.toString(pctRate++));
				}else {
					rowValue.put("type", amtType);
					rowValue.put("rate", Integer.toString(amtRate++));
				}
				Log.info("processing values for OCP: " + rowValue.toString());
				dataMap.add(rowValue);
			}
			enterCommissionRowValues(dataMap.size(),dataMap);
		}
		
		public void enterCommissionRowValues(int rows, ArrayList<HashMap<String,String>> dataMap) {
			Log.info("Trying to enter other commision profile rows, rows to be filled: " + rows + " dataMap size: " + dataMap.size());
			try {
				int i = 0;
				for(HashMap<String,String> rowDetail : dataMap) {
					fromRangeWb = driver.findElement(By.name(new StringBuffer().append(row).append("[").append(i).append("].").append(fromRange).toString()));
					toRangeWb = driver.findElement(By.name(new StringBuffer().append(row).append("[").append(i).append("].").append(toRange).toString()));
					typeWb = driver.findElement(By.name(new StringBuffer().append(row).append("[").append(i).append("].").append(type).toString()));
					rateWb = driver.findElement(By.name(new StringBuffer().append(row).append("[").append(i).append("].").append(rate).toString()));
					
					i++;
					Log.info(rowDetail.toString());
					fromRangeWb.clear();toRangeWb.clear();rateWb.clear();
					fromRangeWb.sendKeys(rowDetail.get("fromRange"));
					toRangeWb.sendKeys(rowDetail.get("toRange"));
					Select typeSl = new Select(typeWb);
					typeSl.selectByValue(rowDetail.get("type"));
					rateWb.sendKeys(rowDetail.get("rate"));	
				}
			}catch(Exception e) {
				Log.info(e.getMessage());
			}
			Log.info("Completed filling OCP rows");
		}
		
		public void processOtherCommissionValuesNegative(int rows) {
			Log.info("Entered to process values");
			ArrayList<HashMap<String,String>> dataMap = new ArrayList<>();
			HashMap<String,String> rowValue;

			int start = Integer.parseInt(_masterVO.getProperty("OCPMinValue").trim());
			int end = Integer.parseInt(_masterVO.getProperty("OCPMaxValue").trim());
			int range = (end-start)/rows;
			
			for(int i = 0 ; i < rows ; i++) {
				rowValue = new HashMap<String,String>();
				if(i==1)
					start -= 10;
				rowValue.put("fromRange", Integer.toString(start));			
				start += range ;				
				rowValue.put("toRange", Integer.toString(start++));
				
				if(i%2 == 0) {
					rowValue.put("type", pctType);
					rowValue.put("rate", Integer.toString(pctRate++));
				}else {
					rowValue.put("type", amtType);
					rowValue.put("rate", Integer.toString(amtRate++));
				}
				Log.info("processing values for OCP: " + rowValue.toString());
				dataMap.add(rowValue);
			}
			enterCommissionRowValues(dataMap.size(),dataMap);
		}
		
		public void clickModifyOtherCP() {
			Log.info("Trying to click modify commision type value");
			try {
				modifyTypeBtn.click();
			}
			catch(Exception e) {
				Log.info(e.getMessage());
			}
		}
		
		public void clickViewOtherCP() {
			Log.info("Trying to click view commision type value");
			try {
				viewTypeBtn.click();
			}
			catch(Exception e) {
				Log.info(e.getMessage());
			}
		}
		
		public boolean findOtherCommission(String profile) {
			Log.info("Trying to locate Other Commission");
			boolean found = false;
			try {
				String xpathStr = "//tr//td[contains(text(),'"+profile+"')]";
				WebElement element = driver.findElement(By.xpath(xpathStr));
				found = element.isDisplayed();
			}catch(Exception e) {
				Log.info(e.getMessage());
			}		
			return found;
		}
		
		
		public void clickSubmit() {
			Log.info("Trying to click submit");
			try {
				addCommissionBtn.click();
			}
			catch(Exception e) {
				Log.info(e.getMessage());
			}
		}
		
		public void clickConfirm() {
			Log.info("Trying to click submit");
			try {
				confirmBtn.click();
			}
			catch(Exception e) {
				Log.info(e.getMessage());
			}
		}
		
		public String getMessage() {
			String message1 = message.getText();
			return message1;
		}
		
		public String getActualMsg() {

	        String UIMsg = null;
	        String errorMsg = null;
	        try{
	        errorMsg = errorMessage.getText();
	        }catch(Exception e){
	                        Log.info("No error Message found: "+e);
	        }
	        try{
	        UIMsg = UIMessage.getText();
	        }catch(Exception e){
	                        Log.info("No Success Message found: "+e);
	        }
	        if (errorMsg == null)
	                        return UIMsg;
	        else
	                        return errorMsg;
	}

}
