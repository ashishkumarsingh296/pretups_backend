package com.utils;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.commons.ExcelI;
import com.pretupsControllers.BTSLUtil;


/* Util file created to keep common utils methods*/
public class CommonUtils {

	public boolean isNumeric(String str)
	  {
	    try
	    {
	      double d = Double.parseDouble(str);
	    }
	    catch(NumberFormatException nfe)
	    {
	      return false;
	    }
	    return true;
	  }
	
	
	public String isSMSPinValid() {
		RandomGeneration randStr = new RandomGeneration();
        int j;
        char pos1 = 0;
        char pos ;
        int result =1;
        String p_smsPin = null;
        while(result!=0){
        int count=0, ctr = 0;
        p_smsPin=randStr.randomNumeric(4);
        for (int i = 0;i < p_smsPin.length(); i++) {
            pos = p_smsPin.charAt(i);

            if (i < p_smsPin.length() - 1) {
                pos1 = p_smsPin.charAt(i + 1);
            }

            j = pos1;
            if (pos == pos1) {
                count++;
            } else if (j == pos + 1 || j == pos - 1) {
                ctr++;
            }
        }

        if (count == p_smsPin.length()) {
            result = -1;Log.info("PIN is same digit: "+p_smsPin);
        } else if (ctr == (p_smsPin.length() - 1)) {
            result = 1;Log.info("PIN is consecutive: "+p_smsPin);
        } else {
            result =0;Log.info("PIN is Valid: " +p_smsPin);
        }}
        return p_smsPin;
    }
	
	public static String generatePassword(int pwdlen) {
		RandomGeneration randStr = new RandomGeneration();
        int j;
        char pos1 = 0;
        char pos ;
        int result =1;
        String passwordGen = null;
        // iterates thru the p_smsId and validates that the number is neither in
        // 444444 or 123456 format 121212
        while(result!=0){
        int count=0, ctr = 0;
        passwordGen=randStr.randomAlphaNumeric(pwdlen);
        if(passwordGen.length()==1){Log.info("Generate password of length 1: "+passwordGen);return passwordGen;}
        for (int i = 0;i < passwordGen.length(); i++) {
            pos = passwordGen.charAt(i);

            if (i < passwordGen.length() - 1) {
                pos1 = passwordGen.charAt(i + 1);
            }

            j = pos1;
            if (pos == pos1) {
                count++;
            } else if (j == pos + 1 || j == pos - 1) {
                ctr++;
            }
        }

        if (count == passwordGen.length()) {
            result = -1;Log.info("Password is same digit: "+passwordGen);
        } else if (ctr == (passwordGen.length() - 1)) {
            result = 1;Log.info("Password is consecutive: "+passwordGen);
        } else {
            result =0;Log.info("Password is Valid: " +passwordGen);
        }}
        return passwordGen;
    }
	
	public static String generateSequential(String type,int seqlength){
		String requiredSequence="";
		char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		Log.info("Length: "+alphabet.length);
		for(int i=0,p=0;i<seqlength;i++){
			if(type.equalsIgnoreCase("number")){
				requiredSequence=requiredSequence+String.valueOf(i);
			}
			else if(type.equalsIgnoreCase("string")){
				requiredSequence=requiredSequence.toString()+alphabet[p];
				p++;
				if(p>=alphabet.length){
					p=0;
				}
			}
			else {
				Log.info("Type specified as ["+type+"] is not valid, it should be either [number] or [string].");
				requiredSequence=null;
				break;
			}
		}
		
		Log.info("Required Sequence is : ["+requiredSequence+"]");
		return requiredSequence;	
	}
	
	public static void selectDateInSpring(WebElement date,String fDate, WebDriver driver){
		String inputDF_Format = "dd/MM/yy";
		String outputDF_Format = "d/MMM/yyyy";
		SimpleDateFormat inputDF = new SimpleDateFormat(inputDF_Format);
		SimpleDateFormat outputDF = new SimpleDateFormat(outputDF_Format);
		
		Date date1 = null;
		try {
			date1 = inputDF.parse(fDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String FormattedDate = outputDF.format(date1);
		System.out.println(FormattedDate);
		String datesplit[] = FormattedDate.split("/");
		
		date.click();
		driver.findElement(By.xpath("//div[@class='datepicker-days']/table/thead/tr/th[@class='datepicker-switch']")).click();
		driver.findElement(By.xpath("//div[@class='datepicker-months']/table/thead/tr/th[@class='datepicker-switch']")).click();
		driver.findElement(By.xpath("//span[@class[contains(.,'year')] and text() = '" + datesplit[datesplit.length - 1] + "']")).click();
		driver.findElement(By.xpath("//span[@class[contains(.,'month')] and text() = '" + datesplit[datesplit.length - 2] + "']")).click();
		driver.findElement(By.xpath("//td[@class[not(contains(.,'new')) and not(contains(.,'old'))] and text() = '" + datesplit[datesplit.length - 3] + "']")).click();
	}
	
	public static boolean roleCodeExistInLinkSheet(String roleCode, String categoryName){
		int rowNum = ExtentI.combinationExistAtRow(new String[]{ExcelI.CATEGORY_NAME}, new String[]{categoryName}, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String categoryCode=ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.CATEGORY_CODE, rowNum);
		if(BTSLUtil.isNullString(categoryCode)){
			rowNum = ExtentI.combinationExistAtRow(new String[]{ExcelI.CATEGORY_NAME}, new String[]{categoryName}, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
			categoryCode=ExtentI.fetchValuefromDataProviderSheet(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.CATEGORY_CODE, rowNum);
		}	
		
		Log.info("Category Code for "+categoryName+": "+categoryCode);
		ExcelUtility.setExcelFile(_masterVO.getProperty("RolesSheet"), ExcelI.LINK_SHEET1);
		int rowcount = ExcelUtility.getRowCount();
		boolean b=false;
		for(int i =1;i<=rowcount;i++){
			if(ExcelUtility.getCellData(0, ExcelI.ROLE_CODES, i).equals(roleCode)&&	ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODES, i).equals(categoryCode))
				{b=true; break;}
			}
		return b;
	}
	
	public static boolean roleCodeExistInLinkSheetRevamp(String roleCode, String categoryName, String eventCode){
		int rowNum = ExtentI.combinationExistAtRow(new String[]{ExcelI.CATEGORY_NAME}, new String[]{categoryName}, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		String categoryCode=ExtentI.fetchValuefromDataProviderSheet(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.CATEGORY_CODE, rowNum);
		if(BTSLUtil.isNullString(categoryCode)){
			rowNum = ExtentI.combinationExistAtRow(new String[]{ExcelI.CATEGORY_NAME}, new String[]{categoryName}, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
			categoryCode=ExtentI.fetchValuefromDataProviderSheet(ExcelI.OPERATOR_USERS_HIERARCHY_SHEET, ExcelI.CATEGORY_CODE, rowNum);
		}	
		
		Log.info("Category Code for "+categoryName+": "+categoryCode);
		ExcelUtility.setExcelFile(_masterVO.getProperty("RolesSheetRevamp"), ExcelI.LINK_SHEET1);
		int rowcount = ExcelUtility.getRowCount();
		boolean b=false;
		for(int i =1;i<=rowcount;i++){
			if(ExcelUtility.getCellData(0, ExcelI.ROLE_CODES, i).equals(roleCode)&&	ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODES, i).equals(categoryCode)&&ExcelUtility.getCellData(0, ExcelI.EVENT_CODES, i).equals(eventCode))
				{b=true; break;}
			}
		return b;
	}
	
	//To check the latest file in the directory
	public static File lastFileModified(String dir) {
	    File fl = new File(dir);
	    File[] files = fl.listFiles(new FileFilter() {          
	        public boolean accept(File file) {
	            return file.isFile();
	        }
	    });
	    long lastMod = Long.MIN_VALUE;
	    File choice = null;
	    for (File file : files) {
	        if (file.lastModified() > lastMod) {
	            choice = file;
	            lastMod = file.lastModified();
	        }
	    }
	    return choice;
	}
}
