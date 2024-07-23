package com.pageobjects.channeluserspages.channelenquiry;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.classes.MessagesDAO;

public class SelfBalance {

	WebDriver driver = null;
	
	public SelfBalance(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public Map<String, String> prepareUserBalanceValues() {

		Map<String, String> userBalanceValues= new HashMap<String, String>();

		String userName_Key = MessagesDAO.getLabelByKey("pretups.user.channeluserviewbalances.label.username");
		String MSISDN_Key = MessagesDAO.getLabelByKey("pretups.user.channeluserviewbalances.label.msisdn");
		String LoginID_Key = MessagesDAO.getLabelByKey("pretups.user.channeluserviewbalances.label.loginid");
		String UserType_Key = MessagesDAO.getLabelByKey("pretups.user.channeluserviewbalances.label.usertype");
		String NetworkName_Key = MessagesDAO.getLabelByKey("pretups.user.channeluserviewbalances.label.network");
		String Category_Key = MessagesDAO.getLabelByKey("pretups.user.channeluserviewbalances.label.categorycode");

		String userName_Locator = "//th[text()[contains(.,'"+ userName_Key +"')]]/following-sibling::td";
		String MSISDN_Locator = "//th[text()[contains(.,'"+ MSISDN_Key +"')]]/following-sibling::td";
		String LoginID_Locator = "//th[text()[contains(.,'"+ LoginID_Key +"')]]/following-sibling::td";
		String UserType_Locator = "//th[text()[contains(.,'"+ UserType_Key +"')]]/following-sibling::td";
		String NetworkName_Locator = "//th[text()[contains(.,'"+ NetworkName_Key +"')]]/following-sibling::td";
		String Category_Locator = "//th[text()[contains(.,'"+ Category_Key +"')]]/following-sibling::td";

		userBalanceValues.put("User Name", driver.findElement(By.xpath(userName_Locator)).getText());
		userBalanceValues.put("MSISDN", driver.findElement(By.xpath(MSISDN_Locator)).getText());
		userBalanceValues.put("LoginID", driver.findElement(By.xpath(LoginID_Locator)).getText());
		userBalanceValues.put("UserType", driver.findElement(By.xpath(UserType_Locator)).getText());
		userBalanceValues.put("Network Name", driver.findElement(By.xpath(NetworkName_Locator)).getText());
		userBalanceValues.put("Category", driver.findElement(By.xpath(Category_Locator)).getText());

		return userBalanceValues;
	}
}
