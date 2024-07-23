package common_util_script;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Read_Excel_file {

	static Map<String, String> cacheMap = Read_Properties_File.getCachemap();
	public static String[][] excelRead(String filename, String sheetname) throws Exception {
		
		// Create FileInputStream Object  to read the credentials 
				 FileInputStream fileInput = new FileInputStream(new File("dataFile.properties"));  
			// Create Properties object  to read the credentials
				 Properties prop = new Properties();  
			//load properties file  to read the credentials
				 prop.load(fileInput);
		
		 // String path = prop.getProperty("readfilepath");
		  
		//String workingDir=System.getProperty("user.dir");	 
		String path = cacheMap.get("inputfilepath");
		  String file  = filename;
		  System.out.println("Your excel/csv path is: " + path+file );
		  
		  //// Specify the file path which you want to create or write
		  File excel = new File(path+file);
		  
		  // Load the file i.e 
		  FileInputStream fis = new FileInputStream(excel);
		 
		  // Load the workbook
		  XSSFWorkbook wb = new XSSFWorkbook(fis);
		  
		  //get the sheet which you want to modify or create
		  XSSFSheet ws = wb.getSheet(sheetname);
	
		  // getRow specify which row we want to read and getCell which column
		  int rowNum = ws.getLastRowNum();
		  System.out.println("No.of rows in the sheet are : " + rowNum);
		  int colNum = ws.getRow(0).getLastCellNum();
		  System.out.println("No.of columns in the sheet are : " + colNum);
		 
		  // String[][] data = new String[(rowNum+1)][colNum];
		  String[][] data = new String[(rowNum)][colNum];
		  int k = 0;
		  for (int i = 1; i <= rowNum; i++) {
		  XSSFRow row = ws.getRow(i);
		  for (int j = 0; j < colNum; j++) {
			   DataFormatter formatter = new DataFormatter(); //creating formatter using the default locale
	      	 Cell cell = row.getCell(j);
	      	 String j_username = formatter.formatCellValue(cell); //Returns the formatted value of a cell as a String regardless of the cell type
	           //System.out.print(j_username+"|| ");
	                  
	           data[k][j] = j_username;
	          // System.out.println("the value at Row: " + i + "and Column " + j + " is " + j_username);
		   }
		   k++;
		  }
	
		   return data;
}
	
	
}
