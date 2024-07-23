package com.pageobjects.networkadminpages.p2ptransferrule;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

public class AddP2PTransferRulePage1 {

	String MasterSheetPath;

	@FindBy(name = "transferRulesIndexed[0].senderSubscriberType")
	WebElement senderSubscriberType;

	@FindBy(name = "transferRulesIndexed[0].senderServiceClassID")
	WebElement senderServiceClassID;

	@FindBy(name = "transferRulesIndexed[0].receiverSubscriberType")
	WebElement receiverSubscriberType;

	@FindBy(name = "transferRulesIndexed[0].receiverServiceClassID")
	WebElement receiverServiceClassID;

	@FindBy(name = "transferRulesIndexed[0].serviceType")
	WebElement serviceType;

	@FindBy(name = "transferRulesIndexed[0].subServiceTypeId")
	WebElement subServiceTypeId;

	@FindBy(name = "transferRulesIndexed[0].cardGroupSetID")
	WebElement cardGroupSetID;

	@FindBy(name = "btnAdd")
	WebElement add;

	@FindBy(name = "btnReset")
	WebElement reset;

	@FindBy(xpath = "//tr/td/ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//tr/td/ol/li")
	WebElement errorMessage;
	
	@FindBy(name = "transferRulesIndexed[0].gatewayCode")
	WebElement requestGatewayCode;

	WebDriver driver = null;

	public AddP2PTransferRulePage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void senderSubscriberType(String SenderSubscriberType) {
		Select select1 = new Select(senderSubscriberType);
		//select1.selectByVisibleText(SenderSubscriberType);
		select1.selectByValue(SenderSubscriberType);
		Log.info("User selected Sender Type: "+SenderSubscriberType);
	}
	
	public void requestGatewayCode(String RequestGatewayCode) {
		Select select1 = new Select(requestGatewayCode);
		select1.selectByValue(RequestGatewayCode);
		Log.info("User selected Request Gateway Code "+RequestGatewayCode);
	}

	public void senderServiceClassID(String SenderServiceClassID) {
		Select select1 = new Select(senderServiceClassID);
		select1.selectByVisibleText(SenderServiceClassID);
		Log.info("User selected Sender Service Class: "+SenderServiceClassID);
	}

	public void receiverSubscriberType(String ReceiverSubscriberType) {
		Select select1 = new Select(receiverSubscriberType);
		//select1.selectByVisibleText(ReceiverSubscriberType);
		select1.selectByValue(ReceiverSubscriberType);
		Log.info("User selected Receiver Type: "+ReceiverSubscriberType);
	}

	public void receiverServiceClassID(String ReceiverServiceClassID) {
		Select select1 = new Select(receiverServiceClassID);
		select1.selectByVisibleText(ReceiverServiceClassID);
		Log.info("User selected Receiver Service Class: "+ReceiverServiceClassID);
	}

	public void serviceType(String ServiceType) {
		Select select1 = new Select(serviceType);
		select1.selectByVisibleText(ServiceType);
		Log.info("User selected Service Type: "+ServiceType);
	}

	public void subServiceTypeId(String SubServiceTypeId) {
		Select select1 = new Select(subServiceTypeId);
		select1.selectByVisibleText(SubServiceTypeId);
		Log.info("User selected Sub Service: "+SubServiceTypeId);
	}

	public void cardGroupSetID(String CardGroupSetID) {
		Select select1 = new Select(cardGroupSetID);
		select1.selectByVisibleText(CardGroupSetID);
		Log.info("User selected Card Group Set: "+CardGroupSetID);
	}

	public void add() {
		add.click();
		Log.info("User clicked add.");
	}

	public void reset() {
		reset.click();
		Log.info("User clicked reset.");
	}

	public String[] serviceValue(String services) {
		String csvSplit = ",";
		String serviceArray[] = services.split(csvSplit);
		int size = serviceArray.length;
		String result[] = new String[size];
		MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, "Service Sheet");
		int totalRow = ExcelUtility.getRowCount();
		Map<String, String> serviceMap = new HashMap<String, String>();
		for (int i = 0; i < totalRow; i++)
			serviceMap.put(ExcelUtility.getCellData(0, "Service Code", i),
					ExcelUtility.getCellData(0, "Service Name", i));

		for (int j = 0; j < size; j++)
			result[j] = serviceMap.get(serviceArray[j]);

		return result;
	}

	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try {
			errorMsg = errorMessage.getText();
		} catch (Exception e) {
			Log.info("No error Message found: " + e);
		}
		try {
			UIMsg = UIMessage.getText();
		} catch (Exception e) {
			Log.info("No Success Message found: " + e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}

}
