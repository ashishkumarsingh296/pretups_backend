package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
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
import com.commons.ExcelI;

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
	private static HSSFSheet ExcelWSheet1;
	private static HSSFWorkbook ExcelWBook1;
	private static HSSFCell Cell1;
	private static HSSFRow Row1;

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
			Log.info("An error occured while performing setExcelFile(" + Path + ", " + SheetName + ")");
			Log.writeStackTrace(e);
		}
	}

	public static void setExcelFileOld(String Path, String SheetName) {
		try {
			POIFSFileSystem ExcelFile = new POIFSFileSystem(new FileInputStream(Path));
			ExcelSheetPath = Path;
			HSSFWorkbook wb = new HSSFWorkbook(ExcelFile);
			ExcelWSheet = ExcelWBook.getSheet(SheetName);
		} catch (Exception e) {
			Log.info("An error occured while performing setExcelFile(" + Path + ", " + SheetName + ")");
			Log.writeStackTrace(e);
		}
	}

	// This method is to read the test data from the Excel cell
	public static String getCellData(int RowNum, int ColNum) {
		try {
			DataFormatter DFMT = new DataFormatter();
			// Logger Handler
			if (CONSTANT.EXCELLOGGER_STATUS == true)
				Log.info("Trying to Fetch Data From: " + ExcelWSheet.getSheetName() + " sheet at Row: " + RowNum
						+ " & Column: " + ColNum);
			Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);
			if (Cell.getCellTypeEnum() == CellType.STRING) {
				// Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Sheet Returned Value as: " + DFMT.formatCellValue(Cell));
				}
				return DFMT.formatCellValue(Cell);
			} else if (Cell.getCellTypeEnum() == CellType.NUMERIC || Cell.getCellTypeEnum() == CellType.FORMULA) {
				String cellValue = DFMT.formatCellValue(Cell);
				if (HSSFDateUtil.isCellDateFormatted(Cell)) {
					DateFormat df = new SimpleDateFormat("dd/MM/yy");
					Date date = Cell.getDateCellValue();
					cellValue = df.format(date);
				}
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Sheet Returned Value as: " + cellValue);
				}
				return cellValue;
			} else if (Cell.getCellTypeEnum() == CellType.BLANK) {
				// Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Sheet Returned Null");
				}
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

				// Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true)
					Log.info("Trying to write: " + Result + " to " + ExcelWSheet.getSheetName() + " sheet at Row: "
							+ RowNum + " & Column: " + ColNum);
				Cell = Row.createCell(ColNum);
				Cell.setCellValue(Result);
				ExcelWSheet.autoSizeColumn(ColNum);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Write Process Successful");
				}
			} else {
				// Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true)
					Log.info("Trying to write: " + Result + " to " + ExcelWSheet.getSheetName() + " sheet at Row: "
							+ RowNum + " & Column: " + ColNum);
				Cell.setCellValue(Result);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Write Process Successful");
				}
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
	public static void setCellData(String Result, int RowNum, int ColNum, IndexedColors bgColor,
			IndexedColors fgColor) {
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
				// Logger Handler
				Cell.setCellValue(Result);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Write Process Successful");
				}
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

	public static void insertValueByColumn(String sheetName, String columnName, int rowNum, String value) {
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), sheetName);
		ExcelUtility.setCellData(0, columnName, rowNum, value);
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
					Log.info("Trying to create Header: " + HeaderName + " to " + ExcelWSheet.getSheetName()
							+ " sheet at Row: 0 & Column: " + ColNum);
				Cell = Row.createCell(ColNum);
				Cell.setCellValue(HeaderName);
				Cell.setCellStyle(style);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Write Process Successful");
				}
			} else {
				// Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true)
					Log.info("Trying to create Header: " + HeaderName + " to " + ExcelWSheet.getSheetName()
							+ " sheet at Row: 0 & Column: " + ColNum);
				Cell.setCellValue(HeaderName);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Write Process Successful");
				}
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
					Log.info("Trying to create Header: " + Headers[i] + " to " + ExcelWSheet.getSheetName()
							+ " sheet at Row: 0 & Column: " + i);
				Cell = Row.createCell(i);
				Cell.setCellValue(Headers[i]);
				Cell.setCellStyle(style);
				ExcelWSheet.autoSizeColumn(i);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Write Process Successful");
				}
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
				Log.info("Trying to Fetch Data From: " + ExcelWSheet.getSheetName() + " sheet at Row: " + rowNum
						+ " & Column: " + colName);
			int col_Num = -1;
			Row = ExcelWSheet.getRow(colNameRow);
			for (int i = 0; i < Row.getLastCellNum(); i++) {
				if (Row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
					col_Num = i;
			}
			Row = ExcelWSheet.getRow(rowNum);
			Cell = Row.getCell(col_Num);
			if (Cell.getCellTypeEnum() == CellType.STRING) {
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Sheet Returned Value as: " + DFMT.formatCellValue(Cell));
				}
				return DFMT.formatCellValue(Cell);
			} else if (Cell.getCellTypeEnum() == CellType.NUMERIC || Cell.getCellTypeEnum() == CellType.FORMULA) {
				String cellValue = DFMT.formatCellValue(Cell);
				if (HSSFDateUtil.isCellDateFormatted(Cell)) {
					DateFormat df = new SimpleDateFormat("dd/MM/yy");
					Date date = Cell.getDateCellValue();
					cellValue = df.format(date);
				}
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Sheet Returned Value as: " + cellValue);
				}
				return cellValue;
			} else if (Cell.getCellTypeEnum() == CellType.BLANK) {
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Sheet Returned Null");
				}
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
				Log.info("Trying to write: " + value + " to " + ExcelWSheet.getSheetName() + " sheet at Row: " + rowNum
						+ " & Column: " + colName);
			Cell.setCellValue(value);
			if (CONSTANT.EXCELLOGGER_STATUS == true) {
				Log.info("Excel Write Process Successful");
			}

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
		boolean found = false;
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					String text = cell.getStringCellValue();
					if (stringToSearch.equals(text)) {
						rowNumber = cell.getRowIndex();
						found = true;
						break;
					}
				}
			}
			if (found) {
				break;
			}
		}
		workbook.close();
		return rowNumber;
	}

	public static boolean isRoleExists(String role) {
		boolean roleExist = false;
		int row = 0;
		try {
			row = ExcelUtility.searchStringRowNum(_masterVO.getProperty("RolesSheet"), ExcelI.LINK_SHEET1, role);
			if (row > 0)
				roleExist = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (roleExist)
			Log.info("Role [" + role + "] found at row number : " + row);
		else
			Log.info("Role [" + role + "] not found");
		return roleExist;
	}

	public static void setExcelFileXLS(String Path, String SheetName) {
		try {
			FileInputStream ExcelFile = new FileInputStream(Path);
			ExcelSheetPath = Path;
			ExcelWBook1 = new HSSFWorkbook(ExcelFile);
			ExcelWSheet1 = ExcelWBook1.getSheet(SheetName);
			if (ExcelWSheet1 == null) {
				ExcelWSheet1 = ExcelWBook1.createSheet(SheetName);
			}
		} catch (Exception e) {
			Log.info("An error occured while performing setExcelFile(" + Path + ", " + SheetName + ")");
			Log.writeStackTrace(e);
		}
	}

	public static void setCellDataXLS(String Result, int RowNum, int ColNum) {
		try {
			if (ExcelWSheet1.getRow(RowNum) == null) {
				ExcelWSheet1.createRow(RowNum);
				Row1 = ExcelWSheet1.getRow(RowNum);
			} else {
				Row1 = ExcelWSheet1.getRow(RowNum);
			}
			Cell1 = Row1.getCell(ColNum, org.apache.poi.ss.usermodel.Row.RETURN_BLANK_AS_NULL);
			if (Cell1 == null) {

				// Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true)
					Log.info("Trying to write: " + Result + " to " + ExcelWSheet1.getSheetName() + " sheet at Row: "
							+ RowNum + " & Column: " + ColNum);
				Cell1 = Row1.createCell(ColNum);
				Cell1.setCellValue(Result);
				ExcelWSheet1.autoSizeColumn(ColNum);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Write Process Successful");
				}
			} else {
				// Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true)
					Log.info("Trying to write: " + Result + " to " + ExcelWSheet1.getSheetName() + " sheet at Row: "
							+ RowNum + " & Column: " + ColNum);
				Cell1.setCellValue(Result);
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Write Process Successful");
				}
			}
			// Constant variables Test Data path and Test Data file name
			FileOutputStream fileOut = new FileOutputStream(ExcelSheetPath);
			ExcelWBook1.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (Exception e) {
			Log.writeStackTrace(e);
		}

	}

	public static void createBlankExcelFile(String filepath) {
		try {
			Log.info("Creating an Excel xls file under " + filepath);
			File filename = new File(filepath);
			FileOutputStream fos = new FileOutputStream(filename);
			fos.close();
			Log.info("Excel File created");
		} catch (IOException ex) {
			System.out.println("Exception caught while creating empty excel file");
		}
	}

	public static void deleteFiles(String downloadPath) {
		Log.info("Trying to delete file from directory... : " + downloadPath);
		File dir = new File(downloadPath);
		File[] dirContents = dir.listFiles();

		for (int i = 0; i < dirContents.length; i++) {
			dirContents[i].delete();
		}
		Log.info("Deleted all files from directory... : " + downloadPath);
	}

	public static String getCellDataHSSF(int RowNum, int ColNum) {
		try {
			DataFormatter DFMT = new DataFormatter();
			// Logger Handler
			if (CONSTANT.EXCELLOGGER_STATUS == true)
				Log.info("Trying to Fetch Data From: " + ExcelWSheet1.getSheetName() + " sheet at Row: " + RowNum
						+ " & Column: " + ColNum);
			Cell1 = ExcelWSheet1.getRow(RowNum).getCell(ColNum);
			if (Cell1.getCellTypeEnum() == CellType.STRING) {
				// Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Sheet Returned Value as: " + DFMT.formatCellValue(Cell1));
				}
				return DFMT.formatCellValue(Cell1);
			} else if (Cell1.getCellTypeEnum() == CellType.NUMERIC || Cell1.getCellTypeEnum() == CellType.FORMULA) {
				String cellValue = DFMT.formatCellValue(Cell1);
				if (HSSFDateUtil.isCellDateFormatted(Cell1)) {
					DateFormat df = new SimpleDateFormat("dd/MM/yy");
					Date date = Cell1.getDateCellValue();
					cellValue = df.format(date);
				}
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Sheet Returned Value as: " + cellValue);
				}
				return cellValue;
			} else if (Cell1.getCellTypeEnum() == CellType.BLANK) {
				// Logger Handler
				if (CONSTANT.EXCELLOGGER_STATUS == true) {
					Log.info("Excel Sheet Returned Null");
				}
				return "";
			} else
				return String.valueOf(Cell1.getBooleanCellValue());

		} catch (IllegalArgumentException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static String base64ToExcel(String base64encode, String name, String dirPath) {

		File fileTempDir = new File(dirPath);
		if (!fileTempDir.isDirectory()) {
			Log.info("Creating an directory under " + dirPath);
			fileTempDir.mkdir();
		}

		String filepath = dirPath + "/" + name;

		Log.info("decoding the base64 to excel");
		byte[] base64Bytes = Base64.getMimeDecoder().decode(base64encode);
		try {
			Log.info("saving the file at: " + filepath);
			FileUtils.writeByteArrayToFile(new File(filepath), base64Bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.info("Could not save the file at: " + filepath);
			e.printStackTrace();
		}

		return filepath;

	}

	public static String excelToBase64(String filepath) {

		Log.info("Reading the file from: " + filepath);
		File file = new File(filepath);
		FileInputStream fis = null;
		try {
			Log.info("Creating input file Stream");
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.info("Could not create input file Stream");
			e.printStackTrace();
		}
		byte[] bytes = new byte[(int) file.length()];
		try {
			Log.info("Reading the file");
			fis.read(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.info("Could not read the file");
			e.printStackTrace();
		}
		Log.info("Converting the excel to Base64");
		String base64 = Base64.getEncoder().encodeToString(bytes);
		try {
			Log.info("Closing input file Stream");
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.info("Could not close input file Stream");
			e.printStackTrace();
		}
		return base64;

	}

	@SuppressWarnings("resource")
	public static void updateExcelSheet(String path, ArrayList<String> data) {
		File filePath = FileOperations.file(path).listFiles()[0];
		HSSFWorkbook h = null;
		try {
			FileInputStream fis = new FileInputStream(filePath.toString());
			h = new HSSFWorkbook(fis);
			HSSFSheet sheet = h.getSheetAt(0);

			for (int i = 1; i <= data.size(); i++) {
				HSSFRow row = sheet.createRow(i);
				row.createCell(0).setCellValue(data.get(i - 1));
			}
			fis.close();
		} catch (Throwable e) {
				throw new RuntimeException("Error writing data in excel sheet");
		}

		try {
			FileOutputStream outFile = new FileOutputStream(filePath.toString());
			h.write(outFile);
			outFile.close();
		} catch (Throwable t) {
			throw new RuntimeException("Error writing data in excel sheet");
		}
	}
	
	public static int getRowCountXlsFile() {
		return ExcelWSheet1.getLastRowNum();
	}
}