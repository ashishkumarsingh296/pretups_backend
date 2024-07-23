package com.pageobjects.networkadminpages.commissionprofile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class ModifyCommProfiledetailsPage {

	// @FindBy(xpath="//a[contains(@onclick,
	// \"addCommissionSlabs('addModifyCommissionProfile\")]")
	@FindBy(xpath = "//a[@onclick[contains(.,'addModifyCommissionProfile')]and@onclick[contains(.,'0')]]")
	private WebElement ModifyComm;

	@FindBy(xpath = "//a[@onclick[contains(.,'addModifyCommissionProfile')]and@onclick[contains(.,'1')]]")
	private WebElement ModifyComm1;

	@FindBy(xpath = "//a[@onclick[contains(.,'addModifyOtfProfile')]and@onclick[contains(.,'0')]]")
	private WebElement ModifyCommOtf;

	@FindBy(xpath = "//a[@onclick[contains(.,'addModifyOtfProfile')]and@onclick[contains(.,'1')]]")
	private WebElement ModifyCommOtf1;

	// @FindBy(xpath="//a[contains(@onclick,
	// \"addCommissionSlabs('addModifyAdditionalProfile\")]")
	// private WebElement ModifyAdditionalComm;

	@FindBy(xpath = "//a[@onclick[contains(.,'addModifyAdditionalProfile')]and@onclick[contains(.,'0')]]")
	private WebElement ModifyAdditionalComm;

	@FindBy(name = "applicableFromDate")
	private WebElement applicableFromDate;

	@FindBy(xpath = "//table[1]/tbody/tr/td[text()[contains(.,'Status')]]/following-sibling::td[1]")
	private WebElement AdditionalCommSlabStatus;

	@FindBy(xpath = "//tr/td[normalize-space() ='Customer Recharge']/../following-sibling::tr/td[text()[contains(.,'Status')]]/following-sibling::td[1]")
	private WebElement AdditionalCommSlabStatusRC;

	@FindBy(name = "dualCommType")
	private WebElement CommissionType;

	@FindBy(xpath = "//a[text()='Close']")
	private WebElement close;

	@FindBy(name = "save")
	private WebElement saveButton;

	@FindBy(name = "reset")
	private WebElement resetButton;

	@FindBy(name = "multipleOf")
	private WebElement multipleof;

	WebDriver driver = null;

	public ModifyCommProfiledetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void ModifyComm() {

		Log.info("User trying to click Modify Comm Slab");
		ModifyComm.click();
		Log.info("User clicked Modify comm slab successfully");
	}

	public void ModifyComm1() {

		Log.info("User trying to click Modify Comm Slab");
		ModifyComm1.click();
		Log.info("User clicked Modify comm slab successfully");
	}

	public void close() {

		Log.info("User trying to click close");
		close.click();
		Log.info("User clicked close button successfully");
	}

	public void ModifyCommOtf() {

		Log.info("User trying to click Modify otf Comm Slab");

		ModifyCommOtf.click();
		Log.info("User clicked Modify otf comm slab successfully");
	}

	public void ModifyCommOtf1() {

		Log.info("User trying to click Modify otf Comm Slab");

		ModifyCommOtf1.click();
		Log.info("User clicked Modify otf comm slab successfully");
	}

	public boolean visibleModifyCommOtf1() {
		boolean flag = false;
		try {
			flag = ModifyCommOtf1.isDisplayed();
		} catch (NoSuchElementException e) {
			Log.info("Not able to find otf");
		}
		return flag;
	}

	public boolean visibleModifyComm() {
		boolean flag = false;
		try {
			flag = ModifyComm.isDisplayed();
		} catch (NoSuchElementException e) {
			Log.info("Not able to find commsion profile");
		}
		return flag;
	}

	public boolean visibleModifyCommOtf() {
		boolean flag = false;
		try {
			flag = ModifyCommOtf.isDisplayed();
		} catch (NoSuchElementException e) {
			Log.info("Not able to find otf");
		}
		return flag;
	}

	public void selectCommissionType(String type) {
		Log.info("Trying to select Commission Type");
		Select commType = new Select(CommissionType);
		commType.selectByValue(type);
		Log.info("Commission Type selected as : " + type);
	}

	public String getStatus() {
		String Status = AdditionalCommSlabStatus.getText();
		Log.info("The Status is :" + Status);
		return Status;
	}

	public String getRCAdditionalSlabStatus() {
		String Status = AdditionalCommSlabStatusRC.getText();
		Log.info("The Current Slab Status is :" + Status);
		return Status;
	}

	public String getAdditionalSlabStatusofparticularService(String service,String serviceCode) throws InterruptedException {

		// WebElement slab = driver.findElement(By.xpath("//tr/td[normalize-space()
		// ='"+service+"']/../following-sibling::tr/td[text()[contains(.,'Status')]]/following-sibling::td[1]"));
		//String serviceCode = _masterVO.getProperty("FixLineRechargeCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();

		Map<String, String[]> serviceMap = new HashMap<String, String[]>();
		for (int i = 1; i <= totalRow; i++) {
			String[] values = { ExcelUtility.getCellData(0, ExcelI.NAME, i),
					ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i) };
			serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
		}

		String subService = serviceMap.get(serviceCode)[1];
	
		Thread.sleep(3000);

		WebElement slab = driver.findElement(By.xpath("//tr/td[normalize-space() ='" + service
				+ "']/../following-sibling::" + "tr/td[normalize-space() ='" + subService
				+ "']/../following-sibling::tr/td[text()[contains(.,'Status')]]/following-sibling::td[1]"));

		String Status = slab.getText();

		Log.info("The Current Slab Status is for Service " + service + " is  :" + Status);
		return Status;
	}

	public boolean isModifyAdditionalCommVisible() {
		try {
			if (ModifyAdditionalComm.isDisplayed())
				return true;
		} catch (NoSuchElementException e) {
			return false;
		}
		return false;
	}

	public boolean isCustomerRechargeAdditionalCommAdded(String service) {
		try {

			WebElement ModifyAdditional = driver
					.findElement(By.xpath("//table//td[normalize-space() = '" + service + "']/..//img"));

			if (ModifyAdditional.isDisplayed())
				return true;
		} catch (NoSuchElementException e) {
			return false;
		}
		return false;
	}

	public void ModifyAdditionalComm() {
		Log.info("User trying to click Modify Additional Commission Slab");
		ModifyAdditionalComm.click();
		Log.info("User clicked Modify Additional Commission Slab");
	}

	public int ModifyAdditionalCommCount() {
		Log.info("User trying to count Modify Additional Commission Slab");
		List<WebElement> e = driver.findElements(
				By.xpath("//a[@onclick[contains(.,'addModifyAdditionalProfile')]and@onclick[contains(.,'')]]"));
		int size = e.size();
		return size;
	}

	public void ModifyAdditionalComm(int i) {
		Log.info("User trying to click Modify Additional Commission Slab");
		driver.findElement(
				By.xpath("//a[@onclick[contains(.,'addModifyAdditionalProfile')]and@onclick[contains(.,'" + i + "')]]"))
				.click();
		Log.info("User clicked Modify Additional Commission Slab");
	}

	public void ModifyAdditionalComSpecificService(String service,String serviceCode) {

		Log.info("Trying to select Additional Commission Slab with Service:" + service);

		//String serviceCode = _masterVO.getProperty("FixLineRechargeCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int totalRow = ExcelUtility.getRowCount();

		Map<String, String[]> serviceMap = new HashMap<String, String[]>();
		for (int i = 1; i <= totalRow; i++) {
			String[] values = { ExcelUtility.getCellData(0, ExcelI.NAME, i),
					ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, i) };
			serviceMap.put(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i), values);
		}

		String subService = serviceMap.get(serviceCode)[1];

		WebElement ModifyAdditional = driver.findElement(By.xpath("//table//tr/td[normalize-space() = '" + subService
				+ "']/../../tr/td[normalize-space() ='" + service + "']/../td/a/img"));
		ModifyAdditional.click();

		Log.info("Modify Additional Commission Slab selected for editing with service:" + service);

	}

	public void clickSave() {
		saveButton.click();
	}

	public void selectCommissionProductBased(String product) {
		Log.info("Trying to select commission profile for product: " + product);
		WebElement productMod = driver
				.findElement(By.xpath("//form/table//table//table//tr/td[normalize-space(text())='" + product
						+ "']/preceding-sibling::td/a/img"));
		productMod.click();
		Log.info("Commission profile selected for editing : " + product);
	}

	public void modifymultipleof(String multiple) {
		Log.info("Trying to modify Multiple of. ");
		multipleof.clear();
		multipleof.sendKeys(multiple);
		Log.info("Multiple of modified with value : " + multiple);
	}

	public void modifyCommDate(String date) {
		Log.info("Trying to modify Applicable From Date");
		applicableFromDate.clear();
		applicableFromDate.sendKeys(date);
		Log.info("Date modified to Current Date : " + date);
	}

}
