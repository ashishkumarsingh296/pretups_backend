package common_features;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.functions;

public class LMS_other extends ExtentReportMultipleClasses {
	
	protected static functions pd = new functions();

	public static void basic(String profiletype, String profilename,
			String from, String to, String fromtime, String totime, String ref,
			String reffrom, String refto, String parent, String mes,
			String optin) throws InterruptedException {
		
		
		pd.SelectOption("promotionType", profiletype);
		pd.EnterBynameSW("profileName", profilename);
		pd.EnterByname("applicableFromDate", from);
		pd.EnterByname("applicableToDate", to);

		if (profiletype.equals("Target Based")) {
			if (ref.equalsIgnoreCase("Yes")) {
				pd.clickbyname("referenceBasedFlag");
				pd.EnterByname("refApplicableFromDate", reffrom);
				pd.EnterByname("refApplicableToDate", refto);
			}
			pd.EnterByname("prtContribution", ""+forint(parent));

			if (optin.equalsIgnoreCase("Yes")) {
				pd.clickbyname("optInOutTarget");
			}
			System.out.println("from time and to time is not required");
		} else {
			pd.EnterByname("applicableFromHour", fromtime);
			pd.EnterByname("applicableToHour", totime);
			
			if (optin.equalsIgnoreCase("Yes")) {
				pd.clickbyname("optInOut");
			}
		}
		
		if (mes.equalsIgnoreCase("Yes")) {
			try {
				
				WebElement link = pd.driver.findElement(By.name("msgConfEnableFlag"));
				Actions actions = new Actions(pd.driver);				
				actions.moveToElement(link).click().perform();
				System.out.println("clicked");			
				
				//pd.clickbyname("msgConfEnableFlag");
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}

	}

	public void popupdata(String profiletype) {
	

	}

	public static void transslab(int i, String first, String second,
			String third, String fourth) {
		pd.EnterByname("commSlabsListIndexed[" + i + "].startRangeAsString", ""	+ forint(first));
		pd.EnterByname("commSlabsListIndexed[" + i + "].endRangeAsString", ""
				+ forint(second));
		pd.SelectOption("commSlabsListIndexed[" + i + "].pointsTypeCode", third);
		pd.EnterByname("commSlabsListIndexed[" + i + "].pointsAsString", ""
				+ forint(fourth));
	}

	public static void targetslabs(int i, String first, String second,
			String third, String fourth, String refType) {

		pd.EnterByname("amountSlabsListIndexed[" + i + "].startRangeAsString",	"" + forint(first));
		try{
		pd.SelectOption("amountSlabsListIndexed[" + i + "].targetType", refType);}catch(Exception e){}
		pd.SelectOption("amountSlabsListIndexed[" + i + "].periodId", second);
		pd.SelectOption("amountSlabsListIndexed[" + i + "].pointsTypeCode",	third);
		pd.EnterByname("amountSlabsListIndexed[" + i + "].pointsAsString", ""+ forint(fourth));
	}
	
	


	public static int forint(String a) {
		float c;
		int b = 0;
		try {
			c = Float.parseFloat(a);
			b = (int) c;
		} catch (Exception e) {
			b = Integer.parseInt(a);
		}
		return b;
	}

}
