package DataVerification;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.Read_file;

public class BonusPointsTransactionBased extends ExtentReportMultipleClasses {

	public static int points = 0;
	public static int SR = 0;
	public static int ER = 0;
	public static int RC = 0;
	public static boolean isAll = true;
	public static String sysdate = "";
	public static String dbid = "pgdb";
	public static String dbpass = "pgdb";
	public static String dbip = "172.16.11.121";
	public static String dbport = "5432/pretups7_test2";
	public static boolean firstrun = true;

	@DataProvider(name = "DP")
	public static String[][] excelRead() throws Exception {

		// read the excel file for invalid credentials
		return Read_file.excelRead("demo_data2.xlsx", "trans");
	}

	@Test(dataProvider = "DP")
	public static void trans(String des, String login)
			throws ClassNotFoundException, ParseException, InterruptedException {
		// TODO Auto-generated method stub

		test = extent
				.createTest("To verify that correct bonus points is calculated for user "
						+ login + " associated with transaction based profile");

		// Class.forName("oracle.jdbc.driver.OracleDriver");
		// Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"+
		// dbip + ":" + dbport, dbpass, dbid);

		Class.forName("org.postgresql.Driver");
		Connection con = null;
		ResultSet currentday = null;
		ResultSet rs = null;
		ResultSet second = null;
		ResultSet third = null;
		ResultSet fourth = null;
		ResultSet sixth = null;
		ResultSet changedate = null;
		try {
			con = DriverManager.getConnection("jdbc:postgresql://["
					+ dbip + "]:" + dbport, dbpass, dbid);
		

		Statement stmt = con.createStatement();
		Statement stmt2 = con.createStatement();
		Statement stmt3 = con.createStatement();
		Statement stmt4 = con.createStatement();
		Statement stmt6 = con.createStatement();
		Statement stmt8 = con.createStatement();

		//currentday = stmt.executeQuery("select sysdate from dual");
		currentday = stmt.executeQuery("select CURRENT_TIMESTAMP ");
		while (currentday.next()) {
			// String date = currentday.getString("sysdate");
			String date = currentday.getString("now");
			sysdate = dayofdate(date);
			System.out.println("Connections is created");

		}

		System.out.println("start");
		rs = stmt
				.executeQuery("select * from USERS where LOGIN_ID  = '" + login
						+ "'ORDER BY LOGIN_ID ASC");
		while (rs.next()) {
			String USER_id = rs.getString("USER_ID");

			second = stmt2
					.executeQuery("select * from CHANNEL_USERS where USER_ID = '"
							+ USER_id + "' and CONTROL_GROUP = 'N'");
			while (second.next()) {

				String LMS = second.getString("LMS_PROFILE");
				int Tp = 0;
				third = stmt3
						.executeQuery("select * from PROFILE_SET where SET_ID = '"
								+ LMS + "' and PROMOTION_TYPE = 'LOYALTYPT'");
				while (third.next()) {
					String Profilename = third.getString("SET_NAME");

					sixth = stmt6
							.executeQuery("select * from PROFILE_SET_VERSION where SET_ID = '"
									+ LMS + "' order by VERSION desc");
					while (sixth.next()) {
						String from = sixth.getString("APPLICABLE_FROM");
						String to = sixth.getString("APPLICABLE_TO");
						String version = sixth.getString("VERSION");

						int v = 1;

						if (firstrun) {
							v = Integer.parseInt(version);
							firstrun = false;
						} else {

							v = Integer.parseInt(version);
							int oldversion = v + 1;
							String ver = "select * from PROFILE_SET_VERSION where SET_ID = '"
									+ LMS
									+ "' and VERSION = '"
									+ oldversion
									+ "' ";
							changedate = stmt8.executeQuery(ver);
							while (changedate.next()) {

								to = changedate.getString("APPLICABLE_FROM");

							}
						}

						from = dayofdate(from);
						to = dayofdate(to);

						isAll = true;

						System.out.println(version);
						String ran = "select * from PROFILE_DETAILS where SET_ID = '"
								+ LMS
								+ "' and VERSION = '"
								+ v
								+ "' order by TYPE,SERVICE_CODE asc ";
						System.out.println("here i am " + Profilename
								+ " with id no " + LMS);

						fourth = stmt4.executeQuery(ran);

						while (fourth.next()) {
							String START_RANGE = fourth
									.getString("START_RANGE");
							String END_RANGE = fourth.getString("END_RANGE");
							String POINTS_TYPE = fourth
									.getString("POINTS_TYPE");
							String POINTS = fourth.getString("POINTS");
							String TYPE = fourth.getString("TYPE");
							String module = fourth.getString("SERVICE_CODE");
							// System.out.println(TYPE+" :: "+module);

							// System.out.println(""+TYPE);

							int p = Integer.parseInt(POINTS);
							START_RANGE = START_RANGE.substring(0,
									START_RANGE.length() - 2);
							END_RANGE = END_RANGE.substring(0,
									END_RANGE.length() - 2);

							SR = Integer.parseInt(START_RANGE);
							ER = Integer.parseInt(END_RANGE);
							int profileduration = 1;
							String q = "";
							String sumvalue = "";
							System.out.println("");

							q = querry(TYPE, module, USER_id);
							sumvalue = sumtype(TYPE);
							Tp = profiletype(profileduration, Tp, from, to, q,
									sumvalue, POINTS_TYPE, POINTS);
						}

					}
				}
				Assert.assertTrue(verify(login, Tp));
			}
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (changedate != null)
				try {
					changedate.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (second != null)
				try {
					second.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (third != null)
				try {
					third.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (fourth != null)
				try {
					fourth.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (sixth != null)
				try {
					sixth.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	public static int profiletype(int profileduration, int Tp, String from,
			String to, String querry, String sumvalue, String POINTS_TYPE,
			String POINTS) throws ParseException, ClassNotFoundException,
			SQLException, InterruptedException {
		int i = 0;
		i = profileduration;
		Date fr = new Date();
		Date To = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(dateformat.parse(from));
		c.add(Calendar.DATE, 0);
		from = dateformat.format(c.getTime());

		c.setTime(dateformat.parse(to));
		c.add(Calendar.DATE, 0);
		to = dateformat.format(c.getTime());
		fr = new SimpleDateFormat("MM/dd/yyyy").parse(from);
		To = new SimpleDateFormat("MM/dd/yyyy").parse(to);

		while (fr.before(To) && -1 != To.compareTo(fr)) {

			c.setTime(fr);
			c.add(Calendar.DATE, i - 1);
			to = dateformat.format(c.getTime());
			fr = new SimpleDateFormat("MM/dd/yyyy").parse(to);
			String datequer = "";

			Date check = new Date();
			Date Tos = new Date();

			check = new SimpleDateFormat("MM/dd/yyyy").parse(sysdate);
			Tos = new SimpleDateFormat("MM/dd/yyyy").parse(to);

			boolean lessthensysdate;
			if (check.after(Tos)) {
				lessthensysdate = true;

			} else {
				lessthensysdate = true;
				// lessthensysdate = false;

			}

			datequer = " and TRANSFER_DATE  = TO_DATE('" + from
					+ "', 'MM/DD/YYYY HH24:MI:SS')";
			if (lessthensysdate) {
				if (querry != "") {
					System.out.println("runing for date " + from);
					Tp = trans(querry + datequer, Tp, sumvalue, POINTS_TYPE,
							POINTS);
				}
			}
			c.setTime(fr);
			c.add(Calendar.DATE, 1);
			to = dateformat.format(c.getTime());
			fr = new SimpleDateFormat("MM/dd/yyyy").parse(to);

			String change = null;
			change = from;
			from = to;
			to = change;

		}

		// System.out.println("to is"+to);

		return Tp;

	}

	public static String sumtype(String TYPE) {
		String q = "";
		if (TYPE.equalsIgnoreCase("C2S")) {
			q = "TRANSFER_VALUE";
		}
		if (TYPE.equalsIgnoreCase("C2C")) {
			q = "TRANSFER_MRP";
		}
		if (TYPE.equalsIgnoreCase("O2C")) {
			q = "TRANSFER_MRP";
		}
		return q;
	}

	public static String querry(String TYPE, String module, String USER_id) {
		String q = "";

		if (TYPE.equalsIgnoreCase("C2C")) {
			q = "select * from CHANNEL_TRANSFERS where STATUS = 'CLOSE' and TYPE = 'C2C'  and ( TO_USER_ID = '"
					+ USER_id + "' OR FROM_USER_ID = '" + USER_id + "' ) ";
		}
		if (TYPE.equalsIgnoreCase("O2C")) {
			q = "select * from CHANNEL_TRANSFERS where STATUS = 'CLOSE' and TYPE = 'O2C'  and TO_USER_ID = '"
					+ USER_id + "' and transfer_category ='SALE' ";
		}

		if (TYPE.equalsIgnoreCase("C2S")) {
			if (isAll) {
				if (!module.equalsIgnoreCase("All")) {
					q = " select * from C2S_TRANSFERS where SENDER_ID = '"
							+ USER_id
							+ "' and TRANSFER_STATUS = '200' and service_type ='"
							+ module + "' ";
					System.out.println("Query: "+q);
				} else {
					q = " select * from C2S_TRANSFERS where SENDER_ID = '"
							+ USER_id + "' and TRANSFER_STATUS = '200' ";

				}
			}
		}

		return q;
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

	public static int trans(String querry, int Tp, String sumvalue,
			String POINTS_TYPE, String POINTS) throws ClassNotFoundException {
		int p = 0;
		p = Integer.parseInt(POINTS);
		/*
		 * Class.forName("oracle.jdbc.driver.OracleDriver"); Connection con =
		 * DriverManager.getConnection("jdbc:oracle:thin:@" + dbip + ":" +
		 * dbport, dbpass, dbid);
		 */
		Class.forName("org.postgresql.Driver");
		Connection con = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection("jdbc:postgresql://[" + dbip
					+ "]:" + dbport, dbpass, dbid);
			Statement stmt = con.createStatement();
			// System.out.println(querry);
			rs = stmt.executeQuery(querry);
			while (rs.next()) {
				String sum = rs.getString(sumvalue);
				String TRANSFER_ID = rs.getString("TRANSFER_ID");
				int amount = 0;

				if (sum != null) {
					amount = Integer.parseInt(sum);
					amount = amount / 100;
					RC = amount;

					System.out.println("rc" + RC + " sr" + SR);
				}

				if (RC >= SR & RC <= ER) {

					// System.out.println("old points "+Tp);
					if (POINTS_TYPE.equals("PCT")) {
						float a = (RC * p);
						int ck = Math.round(a / 100);
						int bonus = 0;
						bonus = ck;
						Tp = Tp + bonus;
						p = bonus;

					} else {
						Tp = Tp + p;

					}
					System.out
							.println("select * from BONUS_HISTORY where TRANSFER_ID = '"
									+ TRANSFER_ID + "' ");
					/*
					 * System.out.println("total points after reward"+Tp);
					 * System.out.println(querry); System.out.println(sumvalue);
					 * System.out.println("Recarge amount "+RC+
					 * "Start range and end range is "+SR+" and "+ER);
					 * System.out
					 * .println("bonus is given in "+POINTS_TYPE+" of  "+p);
					 */
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return Tp;

	}

	public static boolean verify(String loginid, int points)
			throws ClassNotFoundException {
		String a = "0";
		int totalpoints = 0, pointsuserhave = 0, redeempoints = 0;

		/*
		 * Class.forName("oracle.jdbc.driver.OracleDriver"); Connection con =
		 * DriverManager.getConnection("jdbc:oracle:thin:@" + dbip + ":" +
		 * dbport, dbpass, dbid);
		 */
		Class.forName("org.postgresql.Driver");
		Connection con = null;
		ResultSet rs = null;
		ResultSet re = null;
		try {
			con = DriverManager.getConnection("jdbc:postgresql://[" + dbip
					+ "]:" + dbport, dbpass, dbid);
			Statement stmt = con.createStatement();

			String q = "select * from BONUS where USER_ID_OR_MSISDN =(select USER_ID from users where LOGIN_ID ='"
					+ loginid + "')";
			String q2;
			q2 = "select sum(POINTS_REDEEMED) AS POINTS_REDEEMED from REDEMPTIONS where USER_ID_OR_MSISDN = (select USER_ID from users where LOGIN_ID ='"
					+ loginid + "')";

			rs = stmt.executeQuery(q);
			while (rs.next()) {
				a = rs.getString("ACCUMULATED_POINTS");
				if (a != null) {
					pointsuserhave = Integer.parseInt(a);
				} else
					pointsuserhave = 0;
			}

			re = stmt.executeQuery(q2);
			while (re.next()) {
				// a = re.getString("sum(POINTS_REDEEMED)");
				a = re.getString("POINTS_REDEEMED");
				if (a != null) {
					redeempoints = Integer.parseInt(a);
				} else
					redeempoints = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (re != null)
				try {
					re.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		totalpoints = pointsuserhave + redeempoints;

		if (totalpoints == points) {
			System.out.println("total points user should have" + points);
			System.out.println("totalpoints user earned " + totalpoints);
			System.out.println("points user have : " + pointsuserhave
					+ " after redemption of " + redeempoints + " amount");
			System.out
					.println("difference between actual points and earn points are "
							+ (totalpoints - points));

			return true;
		} else {

			System.out.println("total points user should have" + points);
			System.out.println("totalpoints user earned " + totalpoints);
			System.out.println("points user have : " + pointsuserhave
					+ " after redemption of " + redeempoints + " amount");
			System.out
					.println("difference between actual points and earn points are "
							+ (totalpoints - points));
			return false;
		}

	}

}
