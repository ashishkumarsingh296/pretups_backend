package com.utils;

import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ReusableMethods {

	WebDriver driver;
	static WebDriverWait wait;
	static JavascriptExecutor jsDriver;

	public ReusableMethods(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait = new WebDriverWait(driver, 20);
		jsDriver = (JavascriptExecutor) driver;

	}

	@FindBy(xpath = "//select[contains(@class,'ui-datepicker-month')]/option")
	private List<WebElement> months;

	@FindBy(xpath = "//select[contains(@class,'ui-datepicker-year')]/option")
	private List<WebElement> years;

	@FindBy(xpath = "//td[contains(@class,'ng-star-inserted')]/a")
	private List<WebElement> days;

	@FindBy(xpath = "//select[contains(@class,'ui-datepicker-month')]")
	private WebElement datePickerMonth;

	@FindBy(css = "[class*='ui-datepicker-year']")
	private WebElement datePickerYear;

	public boolean handleStaleElememt(WebElement elem) {
		boolean result = false;
		int attempts = 0;
		while (attempts < 2) {
			try {
				wait.until(ExpectedConditions.visibilityOf(elem));
				result = true;
				break;
			} catch (StaleElementReferenceException e) {
			}
			attempts++;
		}
		return result;
	}

	public void clickUsingJavascript(WebElement element) {
		jsDriver.executeScript("arguments[0].click();", element);
	}

	public void scrollIntoViewUsingJavascript(WebElement element) {
		jsDriver.executeScript("arguments[0].scrollIntoView(true);");
	}

	public void waitFor(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void selectValueFromDropdown(WebElement elem, String value) {
		try {
			Select sel = new Select(elem);
			sel.selectByVisibleText(value);
//			jsDriver.executeScript("var select = arguments[0]; for(var i = 0; i < select.options.length; i++)"
//			+ "{ if(select.options[i].text == arguments[1]){ select.options[i].selected = true; } }",
//			elem, value);
		} catch (Exception e) {
			Log.info("Unable to select the " + value + " from dropdown: " + elem);
		}
	}

	public void selectFromDate(String fromDate) throws Exception {
		String mon = fromDate.split("-")[1];
		System.out.println("The from month to be selected: " + mon);
		for (int i = 0; i < months.size(); i++) {
			String value = null;
			for (int j = 0; j <= 2; j++) {
				try {
					value = (String) jsDriver.executeScript("return arguments[0].innerText;", months.get(i));
					break;
				} catch (Exception e) {
					Log.info(e.getMessage());
				}
			}
			System.out.println(value);
			if (value.equalsIgnoreCase(mon)) {
//				jsDriver.executeScript("var select = arguments[0]; for(var i = 0; i < select.options.length; i++)"
//						+ "{ if(select.options[i].text == arguments[1]){ select.options[i].selected = true; } }",
//						datePickerMonth, mon);
				selectValueFromDropdown(datePickerMonth, value);
				break;
			}
		}

		waitFor(2000);
		String selYear = fromDate.split("-")[2];
		System.out.println("The from year to be selected: " + selYear);
		for (int i = 0; i < years.size(); i++) {
			String value = null;
			for (int j = 0; j <= 2; j++) {
				try {
					value = (String) jsDriver.executeScript("return arguments[0].innerText;", years.get(i));
					break;
				} catch (Exception e) {
					Log.info(e.getMessage());
				}
			}
			System.out.println(value);
			String actualYear = value;
			if (actualYear.equalsIgnoreCase(selYear)) {
//				jsDriver.executeScript("var select = arguments[0]; for(var i = 0; i < select.options.length; i++)"
//						+ "{ if(select.options[i].text == arguments[1]){ select.options[i].selected = true; } }",
//						datePickerYear, selYear);
				selectValueFromDropdown(datePickerYear, value);
				break;
			}
		}

		String day = fromDate.split("-")[0];
		for (int i = 0; i < days.size(); i++) {
			String value = null;
			for (int j = 0; j <= 2; j++) {
				try {
					value = (String) jsDriver.executeScript("return arguments[0].innerText;", days.get(i));
					break;
				} catch (Exception e) {
					Log.info(e.getMessage());
				}
			}
			System.out.println(value);
			String actualDay = value;
			if (actualDay.equalsIgnoreCase(day)) {
				wait.until(ExpectedConditions.visibilityOf(days.get(i))).click();
				break;
			}
		}
	}

	public void selectToDate(String toDate) {
		String toMon = toDate.split("-")[1];
		System.out.println("The to month to be selected: " + toMon);
		for (int i = 0; i < months.size(); i++) {
			String value = null;
			for (int j = 0; j <= 2; j++) {
				try {
					value = (String) jsDriver.executeScript("return arguments[0].innerText;", months.get(i));
					break;
				} catch (Exception e) {
					Log.info(e.getMessage());
				}
			}
			System.out.println(value);
			if (value.equalsIgnoreCase(toMon)) {
//				jsDriver.executeScript("var select = arguments[0]; for(var i = 0; i < select.options.length; i++)"
//						+ "{ if(select.options[i].text == arguments[1]){ select.options[i].selected = true; } }",
//						datePickerMonth, toMon);
				selectValueFromDropdown(datePickerMonth, value);
				break;
			}
		}

		String selYear1 = toDate.split("-")[2];
		System.out.println("The from year to be selected: " + selYear1);
		for (int i = 0; i < years.size(); i++) {
			String value = null;
			for (int j = 0; j <= 2; j++) {
				try {
					value = (String) jsDriver.executeScript("return arguments[0].innerText;", years.get(i));
					break;
				} catch (Exception e) {
					Log.info(e.getMessage());
				}
			}
			System.out.println(value);
			String actualYear = value;
			;
			if (actualYear.equalsIgnoreCase(selYear1)) {
//				jsDriver.executeScript("var select = arguments[0]; for(var i = 0; i < select.options.length; i++)"
//						+ "{ if(select.options[i].text == arguments[1]){ select.options[i].selected = true; } }",
//						datePickerYear, selYear1);
				selectValueFromDropdown(datePickerYear, value);
				break;
			}
		}

		String toDay = toDate.split("-")[0];
		System.out.println(days.size());
		for (int i = 0; i < days.size(); i++) {
			String value = null;
			for (int j = 0; j <= 2; j++) {
				try {
					value = (String) jsDriver.executeScript("return arguments[0].innerText;", days.get(i));
					break;
				} catch (Exception e) {
					Log.info(e.getMessage());
				}
			}
			System.out.println(value.toString());
			String actualDay = value.toString();
			if (actualDay.equalsIgnoreCase(toDay)) {
				wait.until(ExpectedConditions.visibilityOf(days.get(i))).click();
				break;
			}
		}

	}

}
