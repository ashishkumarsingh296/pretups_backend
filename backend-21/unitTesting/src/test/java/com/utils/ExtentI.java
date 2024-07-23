/**
 * 
 */
package com.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;
import com.classes.GetScreenshot;
import com.sshmanager.SSHService;

/**
 * @author lokesh.kontey
 *
 */
public class ExtentI extends BaseTest{

	public static void attachScreenShot(){
		String screenShot = null;
		try {
			screenShot = GetScreenshot.capture(driver);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			currentNode.info("", MediaEntityBuilder.createScreenCaptureFromPath(screenShot).build());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void Markup(ExtentColor color, String Message ) {
		currentNode.log(Status.INFO, MarkupHelper.createLabel(Message, color));
	}
	
	public static void attachCatalinaLogs() {
		String CatalinaLogPath = SSHService.getCatalina();
		currentNode.log(Status.FAIL, "<a href='"+ CatalinaLogPath +"'><b><h6><font color='red'>Catalina Log</font></h6></b></a>");
	}
	
	public static void attachCatalinaLogsForSuccess() {
		String CatalinaLogPath = SSHService.getCatalina();
		currentNode.log(Status.PASS, "<a href='"+ CatalinaLogPath +"'><b><h6><font color='green'>Catalina Log</font></h6></b></a>");
	}

	public static void insertValueInDataProviderSheet(String sheetName,String columnName,int rowNum,String value){
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), sheetName);
		ExcelUtility.setCellData(0, columnName, rowNum, value);
	}
	
	public static String fetchValuefromDataProviderSheet(String sheetName,String columnName,int rowNum){
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), sheetName);
		String value=ExcelUtility.getCellData(0, columnName, rowNum);
		return value;
	}
	
	public static int combinationExistAtRow(String[] column, String[] values,String sheetName){
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), sheetName);
		int rowCount = ExcelUtility.getRowCount();
		int validatorCount=0;int rowNum;
		for(rowNum=1;rowNum<=rowCount;rowNum++){
			for(int i=0;i<column.length;i++){
				if(ExcelUtility.getCellData(0, column[i], rowNum).equals(values[i]))
				{validatorCount++;}
			}
			if(validatorCount==column.length){
				break;
			}else{validatorCount=0;}
		}
		return rowNum;
	}
	
	public static void getChannelRequestDailyLogs(String transferID){
		String chnlReqDailyLog  = SSHService.getChannelRequestDailyLog(transferID);
		currentNode.log(Status.INFO, "<pre>ChannelRequestDailyLogs:\n"+chnlReqDailyLog+"</pre>");
	}
	
	public static void getOneLineTXNLogsC2S(String transferID){
		String oneLineTXNC2S  = SSHService.getOneLineC2STransactionLogs(transferID);
		currentNode.log(Status.INFO, "<pre>OneLineTXNLogsC2S:\n"+oneLineTXNC2S+"</pre>");
	}
	
	public static String getValueofCorrespondingColumns(String SheetName, String columntosearch, String columnstorefer[], String valuestorefer[]){
		int rowNum=combinationExistAtRow(columnstorefer, valuestorefer, SheetName);
		String requiredvalue=fetchValuefromDataProviderSheet(SheetName, columntosearch, rowNum);
		return requiredvalue;
	}
	public static String[] getActualMessage(WebDriver driver){
		String[] message = null;
		Log.info("Trying to fetch success message.");
		List<WebElement> wb = driver.findElements(By.xpath("//ul/li"));
		Log.info("List size: "+wb.size());
		message=new String[wb.size()];
		int i=0;
		for(WebElement x:wb){
			message[i]=x.getText();i++;}
		Log.info("Message fetched as :: "+Arrays.toString(message));
		if(wb.isEmpty()){
			Log.info("Success message not found.");
			List<WebElement> wb1 = driver.findElements(By.xpath("//ol/li"));
			Log.info("List size: "+wb1.size());
			message=new String[wb1.size()];
			int i1=0;
			for(WebElement x:wb1){
				message[i1]=x.getText();i1++;}
			Log.info("Error message fetched as : "+Arrays.toString(message)); 
			if(wb1.isEmpty()){
				Log.info("No message found on screen.");}
		}
		return message;
	}
	
	public static ArrayList<String> columnbasedfilter(String filename,String sheetName,String columnName,String filterValue,String columntoget){
		ExcelUtility.setExcelFile(filename, sheetName);
		int row=ExcelUtility.getRowCount();
		ArrayList<String> values = new ArrayList<String>();
		for(int i=1;i<=row;i++){
			if(ExcelUtility.getCellData(0, columnName, i).equals(filterValue)){
				values.add(ExcelUtility.getCellData(0, columntoget, i));
			}
		}
		return values;
	}
	
	public static ArrayList<String> fetchUniqueValuesFromColumn(String sheetName,String columnName){

		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), sheetName);
		int rowCount = ExcelUtility.getRowCount();
		ArrayList<String> alist = new ArrayList<String>();

		for(int index=1;index<=rowCount;index++){
			String value = ExcelUtility.getCellData(0, columnName, index);
			if(!alist.contains(value))
				alist.add(value);
		}
		return alist;
	}

	public static Object[] getMessageOnScreen(){
		String message=null; String error="false";
		try{
		Log.info("Trying to fetch success message.");
		message=driver.findElement(By.xpath("//ul/li")).getText();
		Log.info("Message fetched as :: "+message);
		}
		catch(Exception e){
			try{Log.info("Success message not found.");
			message=driver.findElement(By.xpath("//ol")).getText();
			error="true";
			Log.info("Message fetched as : "+message);} catch(Exception e1){Log.info("No message found on screen.");
			error="NA";}
			
		}
		return new Object[]{error,message};
	}
	
}
