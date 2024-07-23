package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CreateFiles {
	public static String basePath = new File("").getAbsolutePath();
	
	public static void writeinexcelNA(String name) throws IOException, InterruptedException {

		String filename="Basefile";
		String filepath = basePath + "\\"+filename+".xlsx";

		String sheetname1 = "Approve";
		String sheetname2 = "ViewProfile";
		String sheetname3 = "Suspendprofile";
		String sheetname4 = "ResumeProfile";
		String sheetname5 = "DeleteProfile";
		
		
		try {

			System.out.println(filepath);
			File excel = new File(filepath);

			FileInputStream fis = new FileInputStream(excel);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheet(sheetname1);
			XSSFSheet sheet2 = wb.getSheet(sheetname2);
			XSSFSheet sheet3 = wb.getSheet(sheetname3);
			XSSFSheet sheet4 = wb.getSheet(sheetname4);
			XSSFSheet sheet5 = wb.getSheet(sheetname5);
			

			try (FileOutputStream outputStream = new FileOutputStream(
					filename+".xlsx")) {
				wb.write(outputStream);
			} catch (Exception e) {

			}

		} catch (Exception e) {
			System.out.println("here in 2");
			Thread.sleep(5000);

			System.out.println(filepath);
			File excel = new File(filepath);

			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet(sheetname1);
			XSSFSheet sheet2 = wb.createSheet(sheetname2);
			XSSFSheet sheet3 = wb.createSheet(sheetname3);
			XSSFSheet sheet4 = wb.createSheet(sheetname4);
			XSSFSheet sheet5 = wb.createSheet(sheetname5);

			XSSFSheet ws = wb.getSheet(sheetname1);
			Row row = ws.createRow(0);
			row.createCell(0).setCellValue("Profile Name");
			row.createCell(1).setCellValue("Version");
			
			
			ws = wb.getSheet(sheetname2);
			row = ws.createRow(0);
			row.createCell(0).setCellValue("Scenario");
			row.createCell(1).setCellValue("Profile Name");
			row.createCell(2).setCellValue("Version");
			
			ws = wb.getSheet(sheetname3);
			row = ws.createRow(0);
			row.createCell(0).setCellValue("Profile Name");
			row.createCell(1).setCellValue("Version");
			
			ws = wb.getSheet(sheetname4);
			row = ws.createRow(0);
			row.createCell(0).setCellValue("Profile Name");
			row.createCell(1).setCellValue("Version");
			
			ws = wb.getSheet(sheetname5);
			row = ws.createRow(0);
			row.createCell(0).setCellValue("Scenario");
			row.createCell(1).setCellValue("Profile Name");
			row.createCell(2).setCellValue("Version");

			try (FileOutputStream outputStream = new FileOutputStream(
					filename+".xlsx")) {
				wb.write(outputStream);
			} catch (Exception as) {

			}
		}
		
		
		int lastrow=0;
		File excel = new File(filepath);
		FileInputStream fis = new FileInputStream(excel);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet ws =null;
		Row row;
		
		ws= wb.getSheet(sheetname1);
		lastrow= ws.getLastRowNum();		
		row = ws.createRow(lastrow+1);
		row.createCell(0).setCellValue(name);		
		row.createCell(1).setCellValue("1");
		

		ws= wb.getSheet(sheetname2);
		lastrow= ws.getLastRowNum();		
		row = ws.createRow(lastrow+1);
		row.createCell(0).setCellValue("positive");
		row.createCell(1).setCellValue(name);		
		row.createCell(2).setCellValue("1");
		
		ws= wb.getSheet(sheetname3);
		lastrow= ws.getLastRowNum();		
		row = ws.createRow(lastrow+1);
		row.createCell(0).setCellValue(name);		
		row.createCell(1).setCellValue("1");
		
		ws= wb.getSheet(sheetname4);
		lastrow= ws.getLastRowNum();		
		row = ws.createRow(lastrow+1);
		row.createCell(0).setCellValue(name);		
		row.createCell(1).setCellValue("1");
		
		ws= wb.getSheet(sheetname5);
		lastrow= ws.getLastRowNum();		
		row = ws.createRow(lastrow+1);
		row.createCell(0).setCellValue("positive");
		row.createCell(1).setCellValue(name);		
		row.createCell(2).setCellValue("1");
		

		
		
		
		
		try (FileOutputStream outputStream = new FileOutputStream(
				filename+".xlsx")) {
			wb.write(outputStream);
		} catch (Exception e) {

		}

	}
	
	
	
	public static void writeinexcelCA(String name) throws IOException, InterruptedException {

		String filename="Basefile2";
		String filepath = basePath + "\\"+filename+".xlsx";

		String sheetname1 = "AssociateProfile";
		String sheetname2 = "O2CTransfer";
		String sheetname3 = "C2CTransfer";
		String sheetname4 = "C2S_Transfer";
		
		
		
		try {

			System.out.println(filepath);
			File excel = new File(filepath);

			FileInputStream fis = new FileInputStream(excel);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheet(sheetname1);
			XSSFSheet sheet2 = wb.getSheet(sheetname2);
			XSSFSheet sheet3 = wb.getSheet(sheetname3);
			XSSFSheet sheet4 = wb.getSheet(sheetname4);
			
			

			try (FileOutputStream outputStream = new FileOutputStream(
					filename+".xlsx")) {
				wb.write(outputStream);
			} catch (Exception e) {

			}

		} catch (Exception e) {
			System.out.println("here in 2");
			Thread.sleep(5000);

			System.out.println(filepath);
			File excel = new File(filepath);

			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet(sheetname1);
			XSSFSheet sheet2 = wb.createSheet(sheetname2);
			XSSFSheet sheet3 = wb.createSheet(sheetname3);
			XSSFSheet sheet4 = wb.createSheet(sheetname4);
			

			XSSFSheet ws = wb.getSheet(sheetname1);
			Row row = ws.createRow(0);
			row.createCell(0).setCellValue("LoginID");
			row.createCell(1).setCellValue("Profile Name");
			row.createCell(2).setCellValue("Control group");

			
			
			ws = wb.getSheet(sheetname2);
			row = ws.createRow(0);
			row.createCell(0).setCellValue("Mobile no");
			row.createCell(1).setCellValue("Product no");
			row.createCell(2).setCellValue("Amount");
			
			
			ws = wb.getSheet(sheetname3);
			row = ws.createRow(0);
			row.createCell(0).setCellValue("LoginID");
			row.createCell(1).setCellValue("Password");
			row.createCell(2).setCellValue("reciver NO");
			row.createCell(3).setCellValue("Amount");
			row.createCell(4).setCellValue("Pin");
			row.createCell(5).setCellValue("Remarks");
			
			ws = wb.getSheet(sheetname4);
			row = ws.createRow(0);
			row.createCell(0).setCellValue("LoginID");
			row.createCell(1).setCellValue("Password");
			row.createCell(2).setCellValue("Amount");
			row.createCell(3).setCellValue("Mobile no");
			row.createCell(4).setCellValue("servicetype");
			row.createCell(5).setCellValue("sub servicetype");
			row.createCell(5).setCellValue("Pin");
			

			try (FileOutputStream outputStream = new FileOutputStream(
					filename+".xlsx")) {
				wb.write(outputStream);
			} catch (Exception as) {

			}
		}
		
		
		int lastrow=0;
		File excel = new File(filepath);
		FileInputStream fis = new FileInputStream(excel);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet ws =null;
		Row row;
		
		ws= wb.getSheet(sheetname1);
		lastrow= ws.getLastRowNum();		
		row = ws.createRow(lastrow+1);
		row.createCell(1).setCellValue(name);		
		row.createCell(2).setCellValue("1");
		



		
		
		
		
		try (FileOutputStream outputStream = new FileOutputStream(
				filename+".xlsx")) {
			wb.write(outputStream);
		} catch (Exception e) {

		}

	}
}
