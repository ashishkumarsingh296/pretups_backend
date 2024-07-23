package common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * Excel POI Reader
 */
public class ReadExcelFile {
	
	private static Log _log = LogFactory.getFactory().getInstance(
			ReadExcelFile.class.getName());
	 /**
		 * <h1>Reads Excel Files based on Filename and Sheet name</h1>
		 * 
		 * @return
		 */
	public static String[][] excelRead(String filename, String sheetname) {

		HashMap<String, String> cacheMap = LoadPropertiesFile.getCachemap();

		String path = cacheMap.get("readfilepath");
		String file = filename;

		File excel = new File(path + file);
		FileInputStream fis;
		XSSFWorkbook wb = null;
		try {
			fis = new FileInputStream(excel);
			wb = new XSSFWorkbook(fis);
		} catch (FileNotFoundException e) {
			_log.error("Exception:" + e);
		} catch (IOException e) {
			_log.error("Exception:" + e);
		} finally {
			try {
				wb.close();
			} catch (IOException e) {
				_log.error("Exception:" + e);
			}
		}

		XSSFSheet ws = wb.getSheet(sheetname);

		int rowNum = ws.getLastRowNum();
		if (_log.isDebugEnabled()) {
			_log.debug("No.of rows in the sheet are : " + rowNum);
		}
		int colNum = ws.getRow(0).getLastCellNum();
		if (_log.isDebugEnabled()) {
			_log.debug("No.of columns in the sheet are : " + colNum);
		}
		// String[][] data = new String[(rowNum+1)][colNum];
		String[][] data = new String[(rowNum)][colNum];
		int k = 0;
		for (int i = 1; i <= rowNum; i++) {
			XSSFRow row = ws.getRow(i);
			for (int j = 0; j < colNum; j++) {
				DataFormatter formatter = new DataFormatter(); // creating
																// formatter
																// using the
																// default
																// locale
				Cell cell = row.getCell(j);
				String j_username = formatter.formatCellValue(cell); // Returns
																		// the
																		// formatted
																		// value
																		// of a
																		// cell
																		// as a
																		// String
																		// regardless
																		// of
																		// the
																		// cell
																		// type
				// System.out.print(j_username+"|| ");

				data[k][j] = j_username;
				// System.out.println("the value at Row: " + i + "and Column " +
				// j + " is " + j_username);
			}
			k++;
		}

		return data;
	}

}
