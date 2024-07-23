package DataVerification;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.junit.AfterClass;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Read_file;

public class RefereneceTargetCalcuationOptinOut extends ExtentReportMultipleClasses {

	public static int Tp = 0;
	public static String sysdate = "";
	public static Statement stmt10;
	public static Statement stmt9;
	public static Connection con;
	public static String a = "0";
	public static String dbid="pretups_oca";
	public static String dbpass="pretups_oca";
	public static String dbip="172.16.7.32";
	public static String dbport="1521:mmoney1";
	public static String daily = "";

	@BeforeClass
	public static void beforetest() throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("script started ");
	}

	@Test(dataProvider = "DP")
	public static void target(String des,String login) throws ClassNotFoundException, SQLException,
			ParseException, InterruptedException {
		
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbip + ":"
				+ dbport, dbpass, dbid);
		
		test=extent.createTest("To verify that correct target is calculated for user "+login+" associated with reference based pofile");
		Statement stmt = con.createStatement();
		Statement stmt2 = con.createStatement();
		Statement stmt3 = con.createStatement();
		Statement stmt4 = con.createStatement();
		Statement stmt8 = con.createStatement();
		// //System.out.println("start");
		ResultSet rs = stmt
				.executeQuery("select * from USERS where LOGIN_ID  = '" + login
						+ "'ORDER BY LOGIN_ID ASC");
		while (rs.next()) {
			String USER_id = rs.getString("USER_ID");
			String LOGIN_ID = rs.getString("LOGIN_ID");		
			System.out.println("login id is"+LOGIN_ID);
			ResultSet second = stmt2
					.executeQuery("select * from CHANNEL_USERS where USER_ID = '"
							+ USER_id + "' and CONTROL_GROUP = 'N' ");
			while (second.next()) {
				String LMS = second.getString("LMS_PROFILE");
				
				String USERopt = second.getString("OPT_IN_OUT_STATUS");
				
				System.out.println(""+LMS);
				ResultSet third = stmt3.executeQuery("select * from PROFILE_SET where SET_ID = '"
								+ LMS
								+ "' and REF_BASED_ALLOWED = 'Y' and PROMOTION_TYPE = 'STOCK'");
				while (third.next()) {
					String OPT = third.getString("OPT_IN_OUT_ENABLED");
					
					if(OPT=="Y"){
						System.out.println("OPT_IN_OUT_IS_ENABLED");
						System.out.println("USER OPT_IN_OUT_STATUS IS "+USERopt);
						if(USERopt=="I"){
							System.out.println("Run");
						}else{
							break;
						}
					}
					
					String q1 = "";
					q1 = "select * from PROFILE_SET_VERSION where SET_ID = '"
							+ LMS + "' ORDER BY version DESC";
					ResultSet fourth = stmt4.executeQuery("" + q1);
					
					
					while (fourth.next()) {
						String from = fourth.getString("REFERENCE_FROM");
						String to = fourth.getString("REFERENCE_TO");
						String ver=fourth.getString("VERSION");
						from=dayofdate(from);
						to=dayofdate(to);
						String profiles = "select * from PROFILE_DETAILS where SET_ID = '"+ LMS + "' and VERSION ='"+ver+"' ORDER BY PERIOD_ID  DESC";
						ResultSet eight = stmt8.executeQuery(profiles);
						while (eight.next()) {
							String detailid = eight.getString("DETAIL_ID");
							String type = eight.getString("TYPE");
							String detailsubtype = eight.getString("DETAIL_SUBTYPE");
							String target = eight.getString("START_RANGE");
							String module=eight.getString("SERVICE_CODE");
							Tp = 0;
							String datequer="between  TO_DATE('"+ from+ "', 'MM/DD/YYYY HH24:MI:SS') and TO_DATE('"+ to + "', 'MM/DD/YYYY HH24:MI:SS')";
							String q=null;
													
							
							if(type.equalsIgnoreCase("C2S")&&module.equalsIgnoreCase("ALL")){
								q = "select sum(TRANSFER_VALUE) from C2S_TRANSFERS where SENDER_ID = '"+ USER_id+ "' and TRANSFER_STATUS = '200' and transfer_date "+ datequer;
								value(q, "sum(TRANSFER_VALUE)", detailsubtype, target);								
								Assert.assertTrue(verify(USER_id, detailid, LMS, ver));
							}
							else if(type.equalsIgnoreCase("C2S")){
								q = "select sum(TRANSFER_VALUE) from C2S_TRANSFERS where  SENDER_ID = '"+ USER_id+ "' and SERVICE_TYPE = '"+module+"' and TRANSFER_STATUS = '200' and transfer_date "+ datequer;
								value(q, "sum(TRANSFER_VALUE)", detailsubtype, target);
								Assert.assertTrue(verify(USER_id, detailid, LMS, ver));
								
							}
							 if(type.equalsIgnoreCase("O2C")){
								q = "select sum(REQUESTED_QUANTITY) from CHANNEL_TRANSFERS where TO_USER_ID = '"+ USER_id+ "' and TYPE = 'O2C' and STATUS = 'CLOSE' and TRANSFER_DATE "+ datequer;
								value(q, "sum(REQUESTED_QUANTITY)", detailsubtype, target);
								Assert.assertTrue(verify(USER_id, detailid, LMS, ver));	
							}
							 if(type.equalsIgnoreCase("C2C")){
								q = "select sum(REQUESTED_QUANTITY) from CHANNEL_TRANSFERS where (FROM_USER_ID =  '"+ USER_id+ "'  or TO_USER_ID = '"+ USER_id+ "') and TYPE = 'C2C' and STATUS = 'CLOSE' and TRANSFER_DATE "+ datequer;
								value(q, "sum(REQUESTED_QUANTITY)", detailsubtype, target);
								Assert.assertTrue(verify(USER_id, detailid, LMS, ver));
															
							}
							 
							 
							
						}
						
						
					}
					
					
				}
			}
		}
	}
	
	public static void value(String querry,String sumvalue, String POINTS_TYPE,String POINTS)throws ClassNotFoundException, SQLException {
		int pointv=Integer.parseInt(POINTS);
		//pointv=pointv/100;
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"
				+ dbip + ":" + dbport, dbpass, dbid);
		Statement stmt = con.createStatement();
		Statement stmt3 = con.createStatement();
		ResultSet rs = stmt	.executeQuery(querry);
		while (rs.next()) {
			String sum=rs.getString(sumvalue);
			int amount=0;
			System.out.println("querry "+querry);
			System.out.println("querry "+sumvalue);
			System.out.println("sum is "+sum);
			if(sum!=null){
			amount=Integer.parseInt(sum);	
			
			}
			
			if(POINTS_TYPE.equals("PCT"))
			{				
				pointv=pointv/100;
				float a=(amount*pointv);
				int ck= Math.round(a/100);
				int bonus=0;
				bonus =ck;
				Tp=bonus+amount;
				System.out.println("pct");
				
			}
			else{
				System.out.println("points "+pointv);
				System.out.println("amt");
				//amount=amount/100;
				Tp=pointv+amount;
				
			}
		}
		System.out.println("TP is "+Tp);		
	}

	public static Boolean verify(String USER_id,String detailid,String LMS,String ver)
			throws ClassNotFoundException, SQLException {
		
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"
				+ dbip + ":" + dbport, dbpass, dbid);
		Statement stmt = con.createStatement();
		Statement stmt3 = con.createStatement();
		String q="select * from USER_OTH_PROFILES where USER_ID = '"+ USER_id + "' and detail_id = '"+detailid+"'";
		q = q + " and SET_ID = '" + LMS + "' and VERSION ='"+ver+"' ";
		ResultSet third = stmt3.executeQuery(q);
		int send = 0;
		while (third.next()) {
			String target = third.getString("TARGET");
			if(target==null)
			{
				return false;
			}
			send = Integer.parseInt(target);
			//send=send/100;
		}

		if(Tp==send){
			return true;
		}
		else{
			System.out.println("total point should be  "+Tp);
			System.out.println("total point is "+send);
			return false;
		}		
	}

	public static String dayofdate(String k) {
		String d = k.substring(8, 10);
		String y = k.substring(0, 4);
		String mi = k.substring(11, 13);
		String mo = k.substring(5, 7);
		String h = k.substring(14, 16);
		String date = mo + "/" + d + "/" + y + " " + mi + ":" + h + ":00";
		return date;
	}
	
	@AfterClass
	public void teardown() throws Exception {
		con.close();

	}
	@DataProvider(name = "DP")
	 public static String[][] excelRead() throws Exception {
		//read the excel file for invalid credentials
		return Read_file.excelRead("demo_data.xlsx","targetref");
			
	}	
}
