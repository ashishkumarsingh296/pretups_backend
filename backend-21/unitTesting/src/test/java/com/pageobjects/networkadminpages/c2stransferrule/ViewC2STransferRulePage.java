package com.pageobjects.networkadminpages.c2stransferrule;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;
import com.utils._masterVO;

public class ViewC2STransferRulePage {
	WebDriver driver;

	public ViewC2STransferRulePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public boolean checkTransferRule(String requestGatewayCode, String domain, String category,
			String grade, String receiverType, String receiverServiceClass, String serviceType, String subService,String cardGroup) {
		boolean elementDisplayed = false;
		Log.info("Trying to check xpath ");
		StringBuilder TransferRuleX = new StringBuilder();
		if (!_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("2")) {
			TransferRuleX.append("//tr/td[normalize-space() = '" + requestGatewayCode);
			TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + domain);
		} else {
			TransferRuleX.append("//tr/td[normalize-space() = '" + domain);
		}
		
		if (_masterVO.getClientDetail("C2STRANSFERRULE_VER").equalsIgnoreCase("1")) {
			TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + category);
			TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + grade); 
		}
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + receiverType);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + receiverServiceClass);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + serviceType);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + subService);
		TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + cardGroup + "']");
		elementDisplayed = driver.findElement(By.xpath(TransferRuleX.toString())).isDisplayed();
		return elementDisplayed;
		
	}
	
	public String getTotalEnteries() {
		String xpath = "";
		xpath = "//div[contains(text(),'of') and contains(text(),'-')]";
		String str = driver.findElement(By.xpath(xpath)).getText();
		str = str.trim();
		String[] wordList = str.split("\\s+");
		String lastWord = wordList[wordList.length-1];
		return lastWord;
	}
	
	public void getLastPage(int enteries)
	{
		WebElement element = null;
		String xpath = "";
		xpath = "//a/b[contains(text(),'[Next ]')]/ancestor::a/preceding-sibling::a[1]";
		element = driver.findElement(By.xpath(xpath));
		element.click();
	}
	
	public boolean isNextDisplayed()
	{
		boolean elementDisplayed = false;
		Log.info("Trying to check xpath ");
		try{
			String xpath = "";
			xpath = "//a/b[contains(text(),'[Next ]')]";
			elementDisplayed = driver.findElement(By.xpath(xpath)).isDisplayed();
			return elementDisplayed;
		}catch(Exception e)
		{
			elementDisplayed = false;
		}
		return elementDisplayed;
	}

		
}
