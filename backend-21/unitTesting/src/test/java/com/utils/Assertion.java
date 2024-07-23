package com.utils;

import java.util.HashSet;
import java.util.Set;

import org.testng.SkipException;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.pretupsControllers.BTSLUtil;

public class Assertion {

	public static SoftAssert sAssert;

	public static void markAsFailure(String message) {
		getSoftAssert().fail(message);
	}

	public static SoftAssert getSoftAssert() {
		if (sAssert == null) {
			sAssert = new SoftAssert();
		}
		return sAssert;
	}

	public static void completeAssertions() {
		try {

			getSoftAssert().assertAll();

		} catch (AssertionError  error) {
			Log.info(error.getMessage());

		} finally {
			resetSoftAsserts();
		}
	}

	public static void resetSoftAsserts() {
		if (sAssert == null) {
			return;
		} else {
			sAssert = null;
		}
	}

	public static boolean assertEquals(String actual, String expected) {

		boolean isEqual;

		if (BTSLUtil.isNullString(actual) || BTSLUtil.isNullString(expected))
			isEqual = false;
		else
			isEqual = actual.equals(expected);

		Log.info("<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
		getSoftAssert().assertEquals(actual, expected);

		if (!isEqual) {
			ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		} else {
			ExtentI.Markup(ExtentColor.GREEN, "Message Validation Success");
			ExtentI.attachScreenShot();
		}

		return isEqual;
	}

	public static boolean assertContainsEquals(String actual, String expected) {

		boolean isEqual;

		if (BTSLUtil.isNullString(actual) || BTSLUtil.isNullString(expected))
			isEqual = false;
		else
			isEqual = actual.contains(expected) || expected.contains(actual);

		Log.info("<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
		getSoftAssert().assertTrue(isEqual);

		if (!isEqual) {
			ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		} else {
			ExtentI.Markup(ExtentColor.GREEN, "Message Validation Success");
			ExtentI.attachScreenShot();
		}

		return isEqual;
	}
	
	public static boolean assertContainsEqualsSet(String actualMessage, String expectedMessage) {

	
		Set<String> hash_Set = new HashSet<String>();
		  String[] msgs = actualMessage.split(", ");
		  for(String m : msgs)
			hash_Set.add(m);
		  String[] msgs1 = expectedMessage.split(".");
		  boolean res = false;
		  for(String m : msgs1)
		  {
			if(hash_Set.contains(m + "."))
				res = true;
		  }
		 
		  if(!res)
		  {
			Set<String> hash_Set1 = new HashSet<String>();
			for(String m : msgs1)
				hash_Set1.add(m + ".");
			for(String m : msgs)
			{
				if(hash_Set.contains(m))
					res = true;
			}
		  }
      //Assertion.assertContainsEquals(c2cTransferMap.get("actualMessage"), c2cTransferMap.get("expectedMessage"));
		  Log.info("<pre><b>Expected: </b>" + expectedMessage + "<br><b>Found: </b>" + actualMessage + "</pre>");
		  Assertion.getSoftAssert().assertTrue(res);
	if (!res) {
		ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
		ExtentI.attachCatalinaLogs();
		ExtentI.attachScreenShot();
	} else {
		ExtentI.Markup(ExtentColor.GREEN, "Message Validation Success");
		ExtentI.attachScreenShot();
	}
	
	return res;
	}

	public static boolean assertFail(String message) {
		Log.error(message);
		ExtentI.Markup(ExtentColor.RED, message);
		getSoftAssert().assertFalse(true, message);
		return false;
	}

	public static boolean assertPass(String message) {
		Log.info(message);
		ExtentI.Markup(ExtentColor.GREEN, message);
		getSoftAssert().assertTrue(true, message);
		return false;
	}

	public static boolean assertNotNull(String actualMessage) {

		boolean isNull = BTSLUtil.isNullString(actualMessage);
		getSoftAssert().assertNotNull(actualMessage);

		if (isNull) {
			ExtentI.Markup(ExtentColor.RED, "assertNotNull: Message found as NULL.");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		} else {
			ExtentI.Markup(ExtentColor.GREEN, "assertNotNull: Message Found.");
			ExtentI.attachScreenShot();
		}

		return isNull;
	}

	public static boolean assertNull(String actualMessage, String failureMessage) {
		boolean isFailureMessageAvailable = BTSLUtil.isNullString(failureMessage) ? false : true;
		boolean isNull = BTSLUtil.isNullString(actualMessage);

		if (isFailureMessageAvailable)
			getSoftAssert().assertNotNull(actualMessage, failureMessage);
		else
			getSoftAssert().assertNotNull(actualMessage);

		if (!isNull) {
			String message = isFailureMessageAvailable ? failureMessage : "assertNull: Message found as NOT NULL.";
			ExtentI.Markup(ExtentColor.RED, message);
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		} else {
			String message = isFailureMessageAvailable ? failureMessage : "assertNotNull: Message Found as NULL.";
			ExtentI.Markup(ExtentColor.GREEN, message);
			ExtentI.attachScreenShot();
		}

		return isNull;
	}

	public static void assertSkip(String message) {
		Log.skip(message);
		throw new SkipException(message);
	}

	public static boolean assertEqualsIgnoreCase(String actual, String expected) {

		boolean isEqual;

		if (BTSLUtil.isNullString(actual) || BTSLUtil.isNullString(expected))
			isEqual = false;
		else
			isEqual = expected.equalsIgnoreCase(actual);

		Log.info("<pre><b>Expected: </b>" + expected + "<br><b>Found: </b>" + actual + "</pre>");
		getSoftAssert().assertEquals(actual, expected);

		if (!isEqual) {
			ExtentI.Markup(ExtentColor.RED, "Message Validation Failed");
			ExtentI.attachCatalinaLogs();
			ExtentI.attachScreenShot();
		} else {
			ExtentI.Markup(ExtentColor.GREEN, "Message Validation Success");
			ExtentI.attachScreenShot();
		}

		return isEqual;
	}
}
