package com.Features;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class C2STransactionStatus {
	String status;

	@Test
	public String C2STransactionStatus(WebDriver driver) {

		ArrayList<WebElement> c2sRechargeTable = new ArrayList<WebElement>();

		c2sRechargeTable = (ArrayList<WebElement>) driver
				.findElements(By.xpath("//form/table/tbody/tr/td/table/tbody/tr"));

		int c2sRechargeTableSize = c2sRechargeTable.size();
		System.out.println(c2sRechargeTable.size());
		for (int i = 1; i <= c2sRechargeTableSize; i++) {

			System.out.println(
					driver.findElement(By.xpath("//form/table/tbody/tr/td/table/tbody/tr[" + i + "]/td[1]")).getText());

			if (driver.findElement(By.xpath("//form/table/tbody/tr/td/table/tbody/tr[" + i + "]/td[1]")).getText()
					.matches("Transfer status:")) {
				status = driver.findElement(By.xpath("//form/table/tbody/tr/td/table/tbody/tr[" + i + "]/td[2]"))
						.getText();
				System.out.println(status);
				break;

			}

		}
		return status;
	}

}
