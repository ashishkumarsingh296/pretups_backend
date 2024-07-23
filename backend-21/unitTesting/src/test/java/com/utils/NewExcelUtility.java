package com.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.commons.ExcelI;


@SuppressWarnings("deprecation")
public class NewExcelUtility extends BaseTest {

	private static Sheet ExcelWSheet;
	private static Workbook ExcelWBook;
	private static Cell Cell;
	private static Row Row;
	private static String ExcelSheetPath;

	// This method is to set the File path and to open the Excel file, Pass
	// Excel Path and Sheetname as Arguments to this method
	public static void setExcelFile(String Path, String SheetName) {
		try {
			FileInputStream ExcelFile = new FileInputStream(Path);
			ExcelSheetPath = Path;
			ExcelWBook = WorkbookFactory.create(ExcelFile);
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
			if (ExcelWSheet == null) {
				ExcelWSheet = ExcelWBook.createSheet(SheetName);
			}
		} catch (Exception e) {
			Log.info("An error occured while performing setExcelFile("+ Path + ", " + SheetName + ")");
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
	
	public static int getRowCount(String dataProvider, String fileName) {
		setExcelFile(dataProvider, fileName);
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
	public static void setCellData(String Result, int RowNum, int ColNum, IndexedColors bgColor, IndexedColors fgColor) {
		try {
			if (ExcelWSheet.getRow(RowNum) == null) {
				ExcelWSheet.createRow(RowNum);
				Row = ExcelWSheet.getRow(RowNum);
			} else {
				Row = ExcelWSheet.getRow(RowNum);
			}
			Cell = Row.getCell(ColNum, org.apache.poi.ss.usermodel.Row.RETURN_BLANK_AS_NULL);
			CellStyle style = ExcelWBook.createCellStyle();
			style.setFillForegroundColor(bgColor.getIndex());
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			Font font = ExcelWBook.createFont();
			font.setColor(fgColor.getIndex());
			style.setFont(font);
			if (Cell == null) {
				Cell = Row.createCell(ColNum);
				Cell.setCellValue(Result);
				Cell.setCellStyle(style);
			} else {
				//Logger Handler
				Cell.setCellValue(Result);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {Log.info("Excel Write Process Successful"); }
			}
			ExcelWSheet.autoSizeColumn(ColNum);
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
		int col_Num = -1;
		try {
			//int col_Num = -1;
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
			if (col_Num == -1) {
				Log.info("[" + colName + "] does not exist in file.");
			} else
				Log.writeStackTrace(ex);
		}
	}

	@SuppressWarnings("static-access")
	public static int searchStringRowNum(String fileName, String sheetName, String stringToSearch) throws IOException {

		FileInputStream inputStream = new FileInputStream(fileName);
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(inputStream);
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Sheet firstSheet = workbook.getSheet(sheetName);
		Iterator<Row> iterator = firstSheet.iterator();
		int rowNumber = 0;
		boolean found =false;
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					String text = cell.getStringCellValue();
					if (stringToSearch.equals(text)) {
						rowNumber = cell.getRowIndex();
						found=true;
						break;
					}
				}
			}
			if(found){break;}
		}
		workbook.close();
		return rowNumber;
	}
	
	public static boolean isRoleExists(String role){
		boolean roleExist=false;
		int row=0;
		try {
			 row = NewExcelUtility.searchStringRowNum(_masterVO.getProperty("RolesSheet"),ExcelI.LINK_SHEET1, role);
			 if(row>0)
				 roleExist=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(roleExist)
		Log.info("Role ["+role+"] found at row number : "+row);
		else
		Log.info("Role ["+role+"] not found");
		return roleExist;
	}
}