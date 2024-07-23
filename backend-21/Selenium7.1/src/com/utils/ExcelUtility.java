package com.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.classes.BaseTest;
import com.classes.CONSTANT;

/**
 * @author krishan.chawla This is an Excel Utility Class. All the operations
 *         related to excel are available in this class
 */
@SuppressWarnings("deprecation")
public class ExcelUtility extends BaseTest {

	private static XSSFSheet ExcelWSheet;
	private static XSSFWorkbook ExcelWBook;
	private static XSSFCell Cell;
	private static XSSFRow Row;
	private static String ExcelSheetPath;

	// This method is to set the File path and to open the Excel file, Pass
	// Excel Path and Sheetname as Arguments to this method
	public static void setExcelFile(String Path, String SheetName) {
		try {
			FileInputStream ExcelFile = new FileInputStream(Path);
			ExcelSheetPath = Path;
			ExcelWBook = new XSSFWorkbook(ExcelFile);
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			if (ExcelWSheet == null) {
				ExcelWSheet = ExcelWBook.createSheet(SheetName);
			}
		} catch (Exception e) {
			Log.info("Error performing setExcelFile():");
			Log.writeStackTrace(e);
		}
	}

	// This method is to read the test data from the Excel cell
	public static String getCellData(int RowNum, int ColNum) {
		try {
			DataFormatter DFMT = new DataFormatter();
			//Logger Handler
			if (CONSTANT.EXCELLOGGER_STATUS == true)
				Log.info("Trying to Fetch Data From: " + ExcelWSheet.getSheetName() + " sheet at Row: " + RowNum + " & Column: " + ColNum);
			Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);
			if (Cell.getCellTypeEnum() == CellType.STRING) {
				//Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true) {Log.info("Excel Sheet Returned Value as: " + DFMT.formatCellValue(Cell)); }
				return DFMT.formatCellValue(Cell);
			} else if (Cell.getCellTypeEnum() == CellType.NUMERIC || Cell.getCellTypeEnum() == CellType.FORMULA) {
				String cellValue = DFMT.formatCellValue(Cell);
				if (HSSFDateUtil.isCellDateFormatted(Cell)) {
					DateFormat df = new SimpleDateFormat("dd/MM/yy");
					Date date = Cell.getDateCellValue();
					cellValue = df.format(date);
				}
				if (CONSTANT.EXCELLOGGER_STATUS == true) {Log.info("Excel Sheet Returned Value as: " + cellValue);}
				return cellValue;
			} else if (Cell.getCellTypeEnum() == CellType.BLANK) {
				//Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true) {Log.info("Excel Sheet Returned Null"); }
				return "";
			} else
				return String.valueOf(Cell.getBooleanCellValue());

		} catch (IllegalArgumentException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static int getRowCount() {
		return ExcelWSheet.getLastRowNum();
	}

	public static int getColumnCount() {
		return ExcelWSheet.getRow(0).getLastCellNum();
	}

	// This method is to write in the Excel cell
	public static void setCellData(String Result, int RowNum, int ColNum) {
		try {
			if (ExcelWSheet.getRow(RowNum) == null) {
				ExcelWSheet.createRow(RowNum);
				Row = ExcelWSheet.getRow(RowNum);
			} else {
				Row = ExcelWSheet.getRow(RowNum);
			}
			Cell = Row.getCell(ColNum, org.apache.poi.ss.usermodel.Row.RETURN_BLANK_AS_NULL);
			if (Cell == null) {
				
				//Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true)
				Log.info("Trying to write: " + Result + " to " + ExcelWSheet.getSheetName() + " sheet at Row: " + RowNum + " & Column: " + ColNum);
				Cell = Row.createCell(ColNum);
				Cell.setCellValue(Result);
				ExcelWSheet.autoSizeColumn(ColNum);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {Log.info("Excel Write Process Successful"); }
			} else {
				//Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true)
				Log.info("Trying to write: " + Result + " to " + ExcelWSheet.getSheetName() + " sheet at Row: " + RowNum + " & Column: " + ColNum);
				Cell.setCellValue(Result);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {Log.info("Excel Write Process Successful"); }
			}
			// Constant variables Test Data path and Test Data file name
			FileOutputStream fileOut = new FileOutputStream(ExcelSheetPath);
			ExcelWBook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}

	}

	// This method is to write Headers to Excel Sheet
	public static void createHeader(String HeaderName, int ColNum) {
		try {
			if (ExcelWSheet.getRow(0) == null) {
				ExcelWSheet.createRow(0);
				Row = ExcelWSheet.getRow(0);
			} else {
				Row = ExcelWSheet.getRow(0);
			}
			Cell = Row.getCell(ColNum, org.apache.poi.ss.usermodel.Row.RETURN_BLANK_AS_NULL);
			CellStyle style = ExcelWBook.createCellStyle();
			style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			Font font = ExcelWBook.createFont();
			font.setColor(IndexedColors.WHITE.getIndex());
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setFont(font);
			if (Cell == null) {
				if (CONSTANT.EXCELLOGGER_STATUS == true)
				Log.info("Trying to create Header: " + HeaderName + " to " + ExcelWSheet.getSheetName()	+ " sheet at Row: 0 & Column: " + ColNum);
				Cell = Row.createCell(ColNum);
				Cell.setCellValue(HeaderName);
				Cell.setCellStyle(style);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {Log.info("Excel Write Process Successful"); }
			} else {
				//Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true)
					Log.info("Trying to create Header: " + HeaderName + " to " + ExcelWSheet.getSheetName()	+ " sheet at Row: 0 & Column: " + ColNum);
				Cell.setCellValue(HeaderName);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {Log.info("Excel Write Process Successful"); }
			}
			ExcelWSheet.autoSizeColumn(ColNum);
			ExcelWSheet.createFreezePane(0, 1);
			FileOutputStream fileOut = new FileOutputStream(ExcelSheetPath);
			ExcelWBook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}

	}

	public static void createHeader(String... Headers) {
		try {
			if (ExcelWSheet.getRow(0) == null) {
				ExcelWSheet.createRow(0);
				Row = ExcelWSheet.getRow(0);
			} else {
				Row = ExcelWSheet.getRow(0);
			}
			CellStyle style = ExcelWBook.createCellStyle();
			style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			Font font = ExcelWBook.createFont();
			font.setColor(IndexedColors.WHITE.getIndex());
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setFont(font);
			for (int i = 0; i < Headers.length; i++) {
				Cell = Row.getCell(i, org.apache.poi.ss.usermodel.Row.RETURN_BLANK_AS_NULL);
				if (CONSTANT.EXCELLOGGER_STATUS == true)
				Log.info("Trying to create Header: " + Headers[i] + " to " + ExcelWSheet.getSheetName()	+ " sheet at Row: 0 & Column: " + i);
				Cell = Row.createCell(i);
				Cell.setCellValue(Headers[i]);
				Cell.setCellStyle(style);
				ExcelWSheet.autoSizeColumn(i);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {Log.info("Excel Write Process Successful");}
			}
			ExcelWSheet.createFreezePane(0, 1);
			FileOutputStream fileOut = new FileOutputStream(ExcelSheetPath);
			ExcelWBook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}

	}

	public static String getCellData(int colNameRow, String colName, int rowNum) {
		try {
			DataFormatter DFMT = new DataFormatter();
			if (CONSTANT.EXCELLOGGER_STATUS == true)
			Log.info("Trying to Fetch Data From: " + ExcelWSheet.getSheetName() + " sheet at Row: " + rowNum + " & Column: " + colName);
			int col_Num = -1;
			Row = ExcelWSheet.getRow(colNameRow);
			for (int i = 0; i < Row.getLastCellNum(); i++) {
				if (Row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
					col_Num = i;
			}
			Row = ExcelWSheet.getRow(rowNum);
			Cell = Row.getCell(col_Num);
			if (Cell.getCellTypeEnum() == CellType.STRING) {
				if (CONSTANT.EXCELLOGGER_STATUS == true) { Log.info("Excel Sheet Returned Value as: " + DFMT.formatCellValue(Cell)); }
				return DFMT.formatCellValue(Cell);
			} else if (Cell.getCellTypeEnum() == CellType.NUMERIC || Cell.getCellTypeEnum() == CellType.FORMULA) {
				String cellValue = DFMT.formatCellValue(Cell);
				if (HSSFDateUtil.isCellDateFormatted(Cell)) {
					DateFormat df = new SimpleDateFormat("dd/MM/yy");
					Date date = Cell.getDateCellValue();
					cellValue = df.format(date);
				}
				if (CONSTANT.EXCELLOGGER_STATUS == true) { Log.info("Excel Sheet Returned Value as: " + cellValue); }
				return cellValue;
			} else if (Cell.getCellTypeEnum() == CellType.BLANK) {
				if (CONSTANT.EXCELLOGGER_STATUS == true) { Log.info("Excel Sheet Returned Null"); }
				return "";
			} else
				return String.valueOf(Cell.getBooleanCellValue());
		} catch (IllegalArgumentException e) {
			return null;
		} catch (Exception e) {
			Log.writeStackTrace(e);
			return "";
		}
	}

	// This function is used to set cellData in excel using ColumnName
	public static void setCellData(int colNameRow, String colName, int rowNum, String value) {
		try {
			int col_Num = -1;
			Row = ExcelWSheet.getRow(colNameRow);
			for (int i = 0; i < Row.getLastCellNum(); i++) {
				if (Row.getCell(i).getStringCellValue().trim().equals(colName)) {
					col_Num = i;
				}
			}

			ExcelWSheet.autoSizeColumn(col_Num);
			Row = ExcelWSheet.getRow(rowNum);
			if (Row == null)
				Row = ExcelWSheet.createRow(rowNum);

			Cell = Row.getCell(col_Num);
			if (Cell == null)
				Cell = Row.createCell(col_Num);
			if (CONSTANT.EXCELLOGGER_STATUS == true)
			Log.info("Trying to write: " + value + " to " + ExcelWSheet.getSheetName() + " sheet at Row: " + rowNum	+ " & Column: " + colName);
			Cell.setCellValue(value);
			if (CONSTANT.EXCELLOGGER_STATUS == true) { Log.info("Excel Write Process Successful"); }

			FileOutputStream FileOut = new FileOutputStream(ExcelSheetPath);
			ExcelWBook.write(FileOut);
			FileOut.close();
		} catch (Exception ex) {
			Log.writeStackTrace(ex);
		}
	}

	@SuppressWarnings("static-access")
	public static int searchStringRowNum(String fileName, String sheetName, String stringToSearch) throws IOException {

		FileInputStream inputStream = new FileInputStream(fileName);
		Workbook workbook = new XSSFWorkbook(inputStream);

		Sheet firstSheet = workbook.getSheet(sheetName);
		Iterator<Row> iterator = firstSheet.iterator();
		int rowNumber = 0;

		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					String text = cell.getStringCellValue();
					if (stringToSearch.equals(text)) {
						rowNumber = cell.getRowIndex();
						break;
					}
				}
			}
		}
		workbook.close();
		return rowNumber;
	}
}