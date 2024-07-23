/**
 * 
 */
package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.commons.ExcelI;
import com.google.common.io.Files;


/**
 * @author lokesh.kontey
 *
 */
public class WriteTestCaseSheet extends BaseTest{

    @SuppressWarnings("resource")
	public static void writeTestCaseSheet(String sheetName,String[] dataToWrite) throws IOException{
    	File f = new File(".//Output//TestCaseSheet.xlsx");
    	if(f.exists()) { 
    	    // do something
    		//Log.info("File already exist");
    	}
    	else{
    	//Log.info("Creating required file.");	
    	
    	XSSFWorkbook workbook = new XSSFWorkbook(); 
        //Create file system using specific name
        FileOutputStream out = new FileOutputStream(new File(".//Output//TestCaseSheet.xlsx"));
        workbook.write(out);
        out.close();
        @SuppressWarnings("unused")
		XSSFSheet worksheet = workbook.createSheet(sheetName);
        ExcelUtility.setExcelFile(".//Output//TestCaseSheet.xlsx", sheetName);
        ExcelUtility.createHeader("Type","Module","Description",ExcelI.STATUS);
        
        //Log.info("File created successfuly");
    	}
        //Create an object of File class to open xlsx file
    	String Path= ".//Output//TestCaseSheet.xlsx";
        File file =    new File(Path);
    	
        //Create an object of FileInputStream class to read excel file

        FileInputStream inputStream = new FileInputStream(file);

        Workbook workBookName = null;

        //Find the file extension by splitting  file name in substring and getting only extension name
        workBookName = new XSSFWorkbook(inputStream);

        

    //Read excel sheet by sheet name    

    Sheet sheet = workBookName.getSheet(sheetName);

    //Get the current count of rows in excel file

    int rowCount = sheet.getLastRowNum()-sheet.getFirstRowNum();

    //Get the first row from the sheet

    Row row = sheet.getRow(0);

    //Create a new row and append it at last of sheet

    Row newRow = sheet.createRow(rowCount+1);

    //Create a loop over the cell of newly created Row

    for(int j = 0; j < row.getLastCellNum(); j++){

        //Fill data in row

        Cell cell = newRow.createCell(j);

        cell.setCellValue(dataToWrite[j]);

    }

    //Close input stream

    inputStream.close();

    //Create an object of FileOutputStream class to create write data in excel file

    FileOutputStream outputStream = new FileOutputStream(file);

    //write data in the excel file

    workBookName.write(outputStream);

    //close output stream

    outputStream.close();

    

    }

    
    @Test
    public static void Write() throws IOException{

        //Write the file using file name, sheet name and the data to be filled
    	
    	test = extent.createTest("Division Management");
		String testcase1="Hi";
		String caseType="SMoke";
		currentNode=test.createNode(testcase1);
		currentNode.assignCategory(caseType);
		String Status= currentNode.getStatus().toString().toUpperCase();
		
        WriteTestCaseSheet.writeTestCaseSheet("TestResults",new String[]{caseType,ExcelI.DIVISION,testcase1,Status});

    }
	
public static void FileExistMoveIt() throws IOException{
	String Path= ".//Output//TestCaseSheet.xlsx";
	String moveToDir = ".//Output//TestExecutionBackup//TestCaseSheet.xlsx";
	File f = new File(Path);
	File m = new File(moveToDir);
	
	if(f.exists()) { 
	    // do something
		Log.info("File already exist");
		Files.move(f,m );
	}
	else{
		Log.info("File does not exist");
	}
}
    
    @SuppressWarnings("resource")
	public static void writeTestCaseSheetnotAppend(String sheetName,String[] dataToWrite) throws IOException{
    	
    	String Path= ".//Output//TestCaseSheet.xlsx";
    	//String moveToDir = ".//Output//TestExecutionBackup//TestCaseSheet.xlsx";
    	File f = new File(Path);
    	//File m = new File(moveToDir);
    	
    	XSSFWorkbook workbook = new XSSFWorkbook();
    	if(f.exists()) { 
    	    // do something
    		Log.info("File already exist");
    	}
    	else{
    	Log.info("Creating required file.");	
   
        //Create file system using specific name
        FileOutputStream out = new FileOutputStream(new File(".//Output//TestCaseSheet.xlsx"));
        workbook.write(out);
        out.close();}
        @SuppressWarnings("unused")
		XSSFSheet worksheet = workbook.createSheet(sheetName);
        ExcelUtility.setExcelFile(".//Output//TestCaseSheet.xlsx", sheetName);
        ExcelUtility.createHeader("Type","Execution Time","Module","Description",ExcelI.STATUS);
        
        //Log.info("File created successfuly");
    	
        //Create an object of File class to open xlsx file
        
        File file =    new File(Path);
    	
        //Create an object of FileInputStream class to read excel file

       FileInputStream inputStream = new FileInputStream(file);

        Workbook workBookName = null;

        //Find the file extension by splitting  file name in substring and getting only extension name
       workBookName = new XSSFWorkbook(inputStream);

        

    //Read excel sheet by sheet name    

    Sheet sheet = workBookName.getSheet(sheetName);

    //Get the current count of rows in excel file

    int rowCount = sheet.getLastRowNum()-sheet.getFirstRowNum();

    //Get the first row from the sheet

    Row row = sheet.getRow(0);

    //Create a new row and append it at last of sheet

    Row newRow = sheet.createRow(rowCount+1);

    //Create a loop over the cell of newly created Row

    for(int j = 0; j < row.getLastCellNum(); j++){

        //Fill data in row

        Cell cell = newRow.createCell(j);

      cell.setCellValue(dataToWrite[j]);

    }

    //Close input stream

    inputStream.close();

    //Create an object of FileOutputStream class to create write data in excel file

    FileOutputStream outputStream = new FileOutputStream(file);

    //write data in the excel file

    workBookName.write(outputStream);

    //close output stream

    outputStream.close();

    

    }
	
	
    @SuppressWarnings("resource")
   	public static void writeTestCaseSheetNew(String fileName, String sheetName,Object[][] writetofile) throws IOException{
    	String Path= ".//Output//"+fileName+".xlsx";
       	File f = new File(Path);
       	if(f.exists()) { 
       	    // do something
       		//Log.info("File already exist");
       	}
       	else{
       	//Log.info("Creating required file.");	
       	
       	XSSFWorkbook workbook = new XSSFWorkbook(); 
           //Create file system using specific name
           FileOutputStream out = new FileOutputStream(new File(Path));
           workbook.write(out);
           out.close();
           @SuppressWarnings("unused")
   		XSSFSheet worksheet = workbook.createSheet(sheetName);
           ExcelUtility.setExcelFile(Path, sheetName);
			ExcelUtility.createHeader("BUILD_ID", "Lead-Name",
					"Test-Framework-IP", "Test-Framework-Name",
					"Test-Framework-SVN-Path", "Test-Execution-Date-Time",
					"Product-Interface", "Unique-TestCase-ID",
					"Test-Case-Description", "Test-Status", "INFO1", "INFO2",
					"Module");

       	}
           //Create an object of File class to open xlsx file
           File file = new File(Path);
       	
           //Create an object of FileInputStream class to read excel file

           FileInputStream inputStream = new FileInputStream(file);


           //Find the file extension by splitting  file name in substring and getting only extension name
           Workbook workBookName = new XSSFWorkbook(inputStream);

       //Read excel sheet by sheet name    

       Sheet sheet = workBookName.getSheet(sheetName);

       //Get the current count of rows in excel file

       int rowCount = sheet.getLastRowNum()-sheet.getFirstRowNum();

       //Get the first row from the sheet

       //Row row = sheet.getRow(0);

       //Create a new row and append it at last of sheet

       

       //Create a loop over the cell of newly created Row

       for(int i = 0; i < writetofile.length; i++){
    	   rowCount++;
    	   Row newRow = sheet.createRow(rowCount);
       
    	   for(int j = 0; j < writetofile[1].length; j++){

           //Fill data in row
    	
           Cell cell = newRow.createCell(j);
           Log.info("Trying to write at["+(i+1)+"]["+j+"].");
           cell.setCellValue((String)writetofile[i][j]);
           Log.info(" Written at["+(i+1)+"]["+j+"]::" +writetofile[i][j]);
       }
       }

       //Close input stream

       inputStream.close();

       //Create an object of FileOutputStream class to create write data in excel file

       FileOutputStream outputStream = new FileOutputStream(file);

       //write data in the excel file

       workBookName.write(outputStream);

       //close output stream
       outputStream.close();  

       }
    
}
