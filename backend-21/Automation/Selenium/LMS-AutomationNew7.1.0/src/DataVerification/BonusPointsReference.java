package DataVerification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.Read_file;
import common_util_script.logs;

public class BonusPointsReference extends baseClass{
	
	@BeforeTest
	public  void beforetest() throws Exception {
		// TODO Auto-generated method stub
		createconnection();
		//logs.info("message");
	}

	@Test(dataProvider = "DP")
	public  void target(String des,String login) throws ClassNotFoundException,

	SQLException, ParseException, InterruptedException {
		

		test=extent.createTest("To verify that user "+des+" associated with reference based profile are recieving correct bonus points");
		logs.info("script started ");
		logs.info("script started ");
		
		Statement stmt = con.createStatement();
		Statement stmt2 = con.createStatement();
		Statement stmt3 = con.createStatement();
		Statement stmt4 = con.createStatement();
		Statement stmt5 = con.createStatement();
		Statement stmt6 = con.createStatement();
		Statement stmt8 = con.createStatement();
		stmt9 = con.createStatement();
		stmt10 = con.createStatement();

		ResultSet currentday = stmt.executeQuery("select sysdate from dual");
		//ResultSet currentday = stmt.executeQuery("select CURRENT_TIMESTAMP");
		while (currentday.next()) {
			String date = currentday.getString("sysdate");
			//String date = currentday.getString("now");

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
						+ "' and REF_BASED_ALLOWED = 'Y' and PROMOTION_TYPE = 'STOCK'";
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
							profiletyperef(1, period, from, to, LMS, modual, servicecode, USER_id);
							
						}

						if (period.equalsIgnoreCase("WEEKLY")) {
							profiletyperef(7, period, from, to, LMS, modual, servicecode, USER_id);

						}
						if (period.equalsIgnoreCase("MONTHLY")) {
							profiletyperef(30, period, from, to, LMS, modual, servicecode, USER_id);

						}
						if (period.equalsIgnoreCase("EOP")) {
							profiletyperef(70, period, from, to, LMS, modual, servicecode, USER_id);

						}

					}
				System.out.println(Tp);
				Assert.assertTrue(verify(login, Tp));
				}
			}
		}
	}
	
	@AfterTest
	public void teardown() throws Exception {
		con.close();

	}
	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data.xlsx", "ref");
	}

}
