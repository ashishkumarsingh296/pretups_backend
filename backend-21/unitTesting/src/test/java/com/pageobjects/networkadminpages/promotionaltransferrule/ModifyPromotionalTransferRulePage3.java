package com.pageobjects.networkadminpages.promotionaltransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyPromotionalTransferRulePage3 {
		
		WebDriver driver;

		public ModifyPromotionalTransferRulePage3(WebDriver driver) {
			this.driver = driver;
			PageFactory.initElements(driver, this);
		}
		
		@FindBy(name = "c2STransferRulesIndexed[0].status")
		private WebElement c2STransferRulesIndexed0status;
		
		@FindBy(name = "c2STransferRulesIndexed[0].cardGroupSetID")
		private WebElement c2STransferRulesIndexed0cardGroupSetID;
		
		@FindBy(name = "c2STransferRulesIndexed[0].fromDate")
		private WebElement c2STransferRulesIndexed0fromDate;
		
		@FindBy(name = "c2STransferRulesIndexed[0].fromTime")
		private WebElement c2STransferRulesIndexed0fromTime;
		
		@FindBy(name = "c2STransferRulesIndexed[0].tillDate")
		private WebElement c2STransferRulesIndexed0tillDate;
		
		@FindBy(name = "c2STransferRulesIndexed[0].tillTime")
		private WebElement c2STransferRulesIndexed0tillTime;
		
		@FindBy(name = "c2STransferRulesIndexed[1].status")
		private WebElement c2STransferRulesIndexed1status;
		
		@FindBy(name = "c2STransferRulesIndexed[1].cardGroupSetID")
		private WebElement c2STransferRulesIndexed1cardGroupSetID;
		
		@FindBy(name = "c2STransferRulesIndexed[1].fromDate")
		private WebElement c2STransferRulesIndexed1fromDate;
		
		@FindBy(name = "c2STransferRulesIndexed[1].fromTime")
		private WebElement c2STransferRulesIndexed1fromTime;
		
		@FindBy(name = "c2STransferRulesIndexed[1].tillDate")
		private WebElement c2STransferRulesIndexed1tillDate;
		
		@FindBy(name = "c2STransferRulesIndexed[1].tillTime")
		private WebElement c2STransferRulesIndexed1tillTime;
		
		@FindBy(name = "c2STransferRulesIndexed[2].status")
		private WebElement c2STransferRulesIndexed2status;
		
		@FindBy(name = "c2STransferRulesIndexed[2].cardGroupSetID")
		private WebElement c2STransferRulesIndexed2cardGroupSetID;
		
		@FindBy(name = "c2STransferRulesIndexed[2].fromDate")
		private WebElement c2STransferRulesIndexed2fromDate;
		
		@FindBy(name = "c2STransferRulesIndexed[2].fromTime")
		private WebElement c2STransferRulesIndexed2fromTime;
		
		@FindBy(name = "c2STransferRulesIndexed[2].tillDate")
		private WebElement c2STransferRulesIndexed2tillDate;
		
		@FindBy(name = "c2STransferRulesIndexed[2].tillTime")
		private WebElement c2STransferRulesIndexed2tillTime;
		
		@FindBy(name = "c2STransferRulesIndexed[0].multipleSlab")
		private WebElement c2STransferRulesIndexed0multipleSlab;
		
		@FindBy(name = "c2STransferRulesIndexed[1].multipleSlab")
		private WebElement c2STransferRulesIndexed1multipleSlab;
		
		@FindBy(name = "c2STransferRulesIndexed[2].multipleSlab")
		private WebElement c2STransferRulesIndexed2multipleSlab;
		
		@FindBy(xpath="//a[@onclick[contains(.,'0')]]//img[@alt[contains(.,'Add multiple time slab')]]")
		private WebElement timeSlab0;
		
		@FindBy(xpath="//a[@onclick[contains(.,'1')]]//img[@alt[contains(.,'Add multiple time slab')]]")
		private WebElement timeSlab1;
		
		@FindBy(xpath="//a[@onclick[contains(.,'2')]]//img[@alt[contains(.,'Add multiple time slab')]]")
		private WebElement timeSlab2;
		
		@FindBy(name = "btnModify")
		private WebElement btnModify;
		
		@FindBy(name = "btnDeleteRule")
		private WebElement btnDeleteRule;
		
		@FindBy(name = "ModbtnBack")
		private WebElement ModbtnBack;
		
		public void selectStatus0(String value) {
			Log.info("Trying to Select status for first row  ");
			Select select = new Select(c2STransferRulesIndexed0status);
			select.selectByVisibleText(value);
			Log.info("Data selected  successfully:"+ value);
		}
		
		public void selectCardGroupSet0(String value) {
			Log.info("Trying to Select cardGroupSet for first row");
			Select select = new Select(c2STransferRulesIndexed0cardGroupSetID);
			select.selectByVisibleText(value);
			Log.info("Data selected  successfully:"+ value);
		}
		
		public void selectStatus1(String value) {
			Log.info("Trying to Select status for second row  ");
			Select select = new Select(c2STransferRulesIndexed1status);
			select.selectByVisibleText(value);
			Log.info("Data selected  successfully:"+ value);
		}
		
		public void selectCardGroupSet1(String value) {
			Log.info("Trying to Select cardGroupSet for row 1  ");
			Select select = new Select(c2STransferRulesIndexed1cardGroupSetID);
			select.selectByVisibleText(value);
			Log.info("Data selected  successfully:"+ value);
		}
		
		public void selectStatus2(String value) {
			Log.info("Trying to Select status for third row  ");
			Select select = new Select(c2STransferRulesIndexed2status);
			select.selectByVisibleText(value);
			Log.info("Data selected  successfully:"+ value);
		}
		
		public void selectCardGroupSet2(String value) {
			Log.info("Trying to Select cardGroupSet for row 1  ");
			Select select = new Select(c2STransferRulesIndexed2cardGroupSetID);
			select.selectByVisibleText(value);
			Log.info("Data selected  successfully:"+ value);
		}
		
		public void enterc2STransferRulesIndexed0fromDate(String value){
			Log.info("Trying to enter  value in c2STransferRulesIndexed0fromDate ");
			c2STransferRulesIndexed0fromDate.clear();
			c2STransferRulesIndexed0fromDate.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void enterc2STransferRulesIndexed0fromTime(String value){
			Log.info("Trying to enter  value in Selectc2STransferRulesIndexed0fromTime ");
			c2STransferRulesIndexed0fromTime.clear();
			c2STransferRulesIndexed0fromTime.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void enterc2STransferRulesIndexed0tillDate(String value){
			Log.info("Trying to enter  value in c2STransferRulesIndexed0tillDate ");
			c2STransferRulesIndexed0tillDate.clear();
			c2STransferRulesIndexed0tillDate.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void enterc2STransferRulesIndexed0tillTime(String value){
			Log.info("Trying to enter  value in Selectc2STransferRulesIndexed0tillTime ");
			c2STransferRulesIndexed0tillTime.clear();
			c2STransferRulesIndexed0tillTime.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}

		public void enterc2STransferRulesIndexed1fromDate(String value){
			Log.info("Trying to enter  value in c2STransferRulesIndexed1fromDate ");
			c2STransferRulesIndexed1fromDate.clear();
			c2STransferRulesIndexed1fromDate.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void enterc2STransferRulesIndexed1fromTime(String value){
			Log.info("Trying to enter  value in Selectc2STransferRulesIndexed1fromTime ");
			c2STransferRulesIndexed1fromTime.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void enterc2STransferRulesIndexed1tillDate(String value){
			Log.info("Trying to enter  value in c2STransferRulesIndexed1tillDate ");
			c2STransferRulesIndexed1tillDate.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void enterc2STransferRulesIndexed1tillTime(String value){
			Log.info("Trying to enter  value in Selectc2STransferRulesIndexed1tillTime ");
			c2STransferRulesIndexed1tillTime.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		public void enterc2STransferRulesIndexed2fromDate(String value){
			Log.info("Trying to enter  value in c2STransferRulesIndexed2fromDate ");
			c2STransferRulesIndexed2fromDate.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void enterc2STransferRulesIndexed2fromTime(String value){
			Log.info("Trying to enter  value in Selectc2STransferRulesIndexed2fromTime ");
			c2STransferRulesIndexed2fromTime.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void enterc2STransferRulesIndexed2tillDate(String value){
			Log.info("Trying to enter  value in c2STransferRulesIndexed2tillDate ");
			c2STransferRulesIndexed2tillDate.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void enterc2STransferRulesIndexed2tillTime(String value){
			Log.info("Trying to enter  value in Selectc2STransferRulesIndexed2tillTime ");
			c2STransferRulesIndexed2tillTime.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void entermultipleSlab0(String value){
			Log.info("Trying to enter  value in multipleSlab for first row");
			c2STransferRulesIndexed0multipleSlab.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void entermultipleSlab1(String value){
			Log.info("Trying to enter  value in multipleSlab for second row");
			c2STransferRulesIndexed1multipleSlab.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void entermultipleSlab2(String value){
			Log.info("Trying to enter  value in multipleSlab for third row");
			c2STransferRulesIndexed2multipleSlab.sendKeys(value);
			Log.info("Data entered  successfully:"+ value);
			}
		
		public void TimeSlab0(){
			
			
			
			timeSlab0.click();
			}
		
		public void TimeSlab1(){
			
			
			
			timeSlab1.click();
			}
		
		public void TimeSlab2(){
			
			
			
			timeSlab2.click();
			}
		
		public void ClickOnModify() {
			Log.info("Trying to click on Modify Button ");
			btnModify.click();
			Log.info("Clicked on  Modify successfully");
		}
		
		public void ClickOnDelete() {
			Log.info("Trying to click on Delete Button ");
			btnDeleteRule.click();
			Log.info("Clicked on  Delete successfully");
		}
		
		public void ClickOnBack() {
			Log.info("Trying to click on Back Button ");
			ModbtnBack.click();
			Log.info("Clicked on  Back successfully");
		}


}
