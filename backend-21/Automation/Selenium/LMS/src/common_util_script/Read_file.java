package common_util_script;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Read_file {

	public static String[][] excelRead(String filename, String sheetname) throws Exception {
		String basePath = new File("").getAbsolutePath();
		  String path = basePath+"\\";
		  String file  = filename;
		  
		  File excel = new File(path+file);
		  FileInputStream fis = new FileInputStream(excel);
	
		  XSSFWorkbook wb = new XSSFWorkbook(fis);
		  XSSFSheet ws = wb.getSheet(sheetname);
	
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
