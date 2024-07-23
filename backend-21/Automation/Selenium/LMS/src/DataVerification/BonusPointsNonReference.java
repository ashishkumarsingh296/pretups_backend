package DataVerification;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;

import bsh.classpath.BshClassPath.AmbiguousName;
import common_util_script.Read_file;
import common_util_script.logs;

public class BonusPointsNonReference extends baseClass{
	
	@BeforeClass
	public  void beforetest() throws Exception {
		// TODO Auto-generated method stub
		createconnection();
		//logs.info("script started ");
	}

	@Test(dataProvider = "DP")
	public  void target(String des,String login) throws ClassNotFoundException,

	SQLException, ParseException, InterruptedException {
		
		test=extent.createTest("To verify that correct target is calculated for user "+login+" associated with Non reference based profile");

		Statement stmt = con.createStatement();
		Statement stmt2 = con.createStatement();
		Statement stmt3 = con.createStatement();
		Statement stmt4 = con.createStatement();
		Statement stmt5 = con.createStatement();
		Statement stmt6 = con.createStatement();
		Statement stmt8 = con.createStatement();
		stmt9 = con.createStatement();
		stmt10 = con.createStatement();
		
		logs.info("script started ");

		//ResultSet currentday = stmt.executeQuery("select sysdate from dual");
		ResultSet currentday = stmt.executeQuery("select CURRENT_TIMESTAMP");
		while (currentday.next()) {
			//String date = currentday.getString("sysdate");
			String date = currentday.getString("now");
			sysdate = dayofdate(date);
			logs.info("today is1 " + sysdate);
			// Thread.sleep(9000);

		}

		logs.info("start");

		ResultSet rs = stmt.executeQuery("select * from USERS where LOGIN_ID  = '" + login+ "'ORDER BY LOGIN_ID ASC");
		while (rs.next()) {
			String USER_id = rs.getString("USER_ID");
			String LOGIN_ID = rs.getString("LOGIN_ID");
			logs.info("user id is " + USER_id);
			logs.info("login id is " + LOGIN_ID);

			Tp = 0;

			ResultSet second = stmt2
					.executeQuery("select * from CHANNEL_USERS where USER_ID = '"
							+ USER_id + "' and CONTROL_GROUP = 'N'");
			while (second.next()) {

				String LMS = second.getString("LMS_PROFILE");
				String q1 = "select * from PROFILE_SET where SET_ID = '"
						+ LMS
						+ "' and REF_BASED_ALLOWED = 'N' and PROMOTION_TYPE = 'STOCK'";
				ResultSet third = stmt3.executeQuery(q1);
				while (third.next()) {
					
					String from="",to="",version="";
					q1 = "select * from PROFILE_SET_VERSION where SET_ID = '"+ LMS + "' ORDER BY version asc";
					ResultSet fourth = stmt4.executeQuery("" + q1);
					while (fourth.next()) {						
						to = fourth.getString("APPLICABLE_TO");
						version = fourth.getString("VERSION");
						logs.info(version);						
					}
					q1 = "select * from PROFILE_SET_VERSION where SET_ID = '"+ LMS + "' and version ='1'";
					logs.info(q1);
					 fourth = stmt4.executeQuery("" + q1);
					while (fourth.next()) {						
						from = fourth.getString("APPLICABLE_FROM");
					}
					
					from=dayofdate(from);
					to=dayofdate(to);
					logs.info("from "+from+" and to is "+to+" version is "+version);
					String profiles = "select distinct TYPE,service_code,PERIOD_ID from PROFILE_DETAILS where SET_ID = '"
							+ LMS + "' order by type,service_code ASC";

					logs.info(profiles);
					ResultSet typeandperiod = stmt8.executeQuery(profiles);
					while (typeandperiod.next()) {

						String modual = typeandperiod.getString("TYPE");
						String period = typeandperiod.getString("PERIOD_ID");
						String servicecode = typeandperiod.getString("service_code");
						String s ;
						s = "select * from PROFILE_DETAILS where SET_ID = '"+ LMS + "' and PERIOD_ID ='" + period+ "' and TYPE= '" + modual;
						s=s+ "' and service_code ='" + servicecode+ "' ORDER BY START_RANGE DESC  ";

						if (period.equalsIgnoreCase("DAILY")) {
							profiletypeNonref(1, period, from, to, LMS, modual, servicecode, USER_id);
							
						}

						if (period.equalsIgnoreCase("WEEKLY")) {
							profiletypeNonref(7, period, from, to, LMS, modual, servicecode, USER_id);

						}
						if (period.equalsIgnoreCase("MONTHLY")) {
							profiletypeNonref(30, period, from, to, LMS, modual, servicecode, USER_id);

						}
						if (period.equalsIgnoreCase("EOP")) {
							profiletypeNonref(900, period, from, to, LMS, modual, servicecode, USER_id);

						}

					}
				logs.info("Total points"+Tp);
				Assert.assertTrue(verify(login, Tp));
				}
			}
		}
	}

	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data2.xlsx", "nonref");
	}

}
