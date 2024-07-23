package testcases;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import common_features.LMS_other;


public class Add_LMS_Profile extends LMS_other {

	public static String basePath = new File("").getAbsolutePath();

	@Test
	public static void add() throws EncryptedDocumentException,
			InvalidFormatException, IOException, InterruptedException,
			ParseException {
		String path = basePath + "\\";
		System.out.println(basePath);
		int lastRowNum = 0;

		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");

		/**
		 * 
		 */
		// Create FileInputStream Object to read the credentials
		FileInputStream fileInput = new FileInputStream(new File(
				"dataFile.properties"));
		// Create Properties object to read the credentials
		Properties prop = new Properties();
		// load properties file to read the credentials
		prop.load(fileInput);

		Date date = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yy");
		Calendar c = Calendar.getInstance();

		c.setTime(dateformat.parse("" + dateformat.format(date)));
		c.add(Calendar.DATE, 2);
		String from = dateformat.format(c.getTime());

		c.setTime(dateformat.parse("" + dateformat.format(date)));
		c.add(Calendar.DATE, 30);
		String to = dateformat.format(c.getTime());

		c.setTime(dateformat.parse("" + dateformat.format(date)));
		c.add(Calendar.DATE, -5);
		String reffrom = dateformat.format(c.getTime());

		c.setTime(dateformat.parse("" + dateformat.format(date)));
		c.add(Calendar.DATE, 1);
		String refto = dateformat.format(c.getTime());

		System.out.println(from);
		System.out.println(to);

		String fromtime = "00:01";
		String totime = "23:59";
		XSSFWorkbook srcBook = new XSSFWorkbook(path + "demo_data.xlsx");
		XSSFSheet sourceSheet = srcBook.getSheet("AddAssociate");
		lastRowNum = sourceSheet.getLastRowNum();
		System.out.println("" + lastRowNum);
		String url = "http://via:6164/pretups/";
		url = prop.getProperty("url");
		pd.open(url);
		pd.login(prop.getProperty("Username"), prop.getProperty("password"),
				url);
		WebDriver driver = pd.getdriver();
		pd.switchframe();
		// lastRowNum
		for (int row_id = 1; row_id <= lastRowNum; row_id++) {

			System.out.println("row is " + row_id);
			XSSFRow Matrix_Row = sourceSheet.getRow(row_id);
			String profiletype = Matrix_Row.getCell(1).toString();
			String profilename = Matrix_Row.getCell(2).toString();
			String parent = Matrix_Row.getCell(3).toString();
			String optin = Matrix_Row.getCell(4).toString();
			String ref = Matrix_Row.getCell(5).toString();
			String mes = Matrix_Row.getCell(6).toString();
			String refType = Matrix_Row.getCell(7).toString();

			//profilename=profilename+dateFormat.format(date);

			test = extent.createTest(
					"To verify that user is able to create LMS profile "
							+ profilename,
					"User should be able to login with valid credentials");
			pd.clickLink("Loyalty Management");
			pd.clickLink("Loyalty profile management");
			pd.clickbyname("addactprofile");

			basic(profiletype, profilename, from, to, fromtime, totime, ref,
					reffrom, refto, parent, mes, optin);

			boolean istrue=true;
			for (int k = 1; k < 15&&istrue; k++) {
				String module = null;
				String service = null;
				
				try {
					module = Matrix_Row.getCell((8 + (14 * (k - 1)))).toString();
					service = Matrix_Row.getCell((9 + (14 * (k - 1)))).toString();
				} catch (Exception e) {
				}

				if (module == null) {
				} else {
					if (profiletype.equals("Target Based")) {
						pd.clickonxpath("//*[contains(@onclick,'showVolumeProfileDetails')]");
						pd.switchwindow();

						pd.SelectOption("moduleType", module);
						pd.SelectOption("serviceCode", service);
						int in = 0;
						for (int p = 1; p < 11&&istrue; p = p + 4) {
							String first = Matrix_Row.getCell(9 + p + (14 * (k - 1))).toString();
							String second = Matrix_Row.getCell(10 + p + (14 * (k - 1))).toString();
							String third = Matrix_Row.getCell(11 + p + (14 * (k - 1))).toString();
							String fourth = Matrix_Row.getCell(12 + p + (14 * (k - 1))).toString();
							istrue=targetslabs(in++, first, second, third, fourth,refType);
						}
						if(istrue){
							pd.clickbyname("addvolactivation");
						}
						else{
							pd.clickonxpathSW("//a[@href='javascript:window.close()']");
						}
						pd.closewindow();

					} else {
						pd.clickonxpath("//*[contains(@onclick,'showTransactionProfileDetails')]");
						pd.switchwindow();
						System.out.println("module is " + module);
						pd.SelectOption("moduleType", module);
						pd.SelectOption("serviceCode", service);
						int in = 0;
						for (int p = 1; p < 11&&istrue; p = p + 4) {
							String first = Matrix_Row.getCell(9 + p + (14 * (k - 1))).toString();
							String second = Matrix_Row.getCell(10 + p + (14 * (k - 1))).toString();
							String third = Matrix_Row.getCell(11 + p + (14 * (k - 1))).toString();
							String fourth = Matrix_Row.getCell(12 + p + (14 * (k - 1))).toString();
							istrue=transslab(in++, first, second, third, fourth);
						}
						if(istrue){
							pd.clickbyname("addactivation");
						}
						else{
							pd.clickonxpathSW("//a[@href='javascript:window.close()']");
						}
						pd.closewindow();

					}

				}

			}
					
			if(istrue){
				pd.clickbyname("save");
				pd.clickbyname("confirm");
				
			}else{
				test.skip("Skiped");
			}
		
			
			CreateFiles.writeinexcelNA(profilename);
			CreateFiles.writeinexcelCA(profilename);
			
		}
		//pd.close();
	}


}
