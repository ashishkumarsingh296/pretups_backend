package db;

import java.awt.datatransfer.StringSelection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import bsh.classpath.BshClassPath.AmbiguousName;
import common_util_script.Read_file;

public class refLMS {

	public static int points = 0;
	public static int SR = 0;
	public static int ER = 0;
	public static int RC = 0;
	public static int Tp = 0;
	public static int bonus = 0;
	public static int p = 0;
	public static String sysdate = "";
	public static Statement stmt10;
	public static Statement stmt9;
	public static Connection con;
	public static boolean isAll = true;
	public static String a = "0";
	public static String dbid = "PRETUPS6X_LIVE";
	public static String dbpass = "PRETUPS6X_LIVE";
	public static String dbip = "172.16.10.239";
	public static String dbport = "1525:prtpnew";
	public static String daily = "";
	public static boolean firstrun=true;

	@BeforeClass
	public static void beforetest() throws Exception {
		// TODO Auto-generated method stub

		System.out.println("script started ");
	}

	@Test
	public static void target() throws ClassNotFoundException,

	SQLException, ParseException, InterruptedException {

		Class.forName("oracle.jdbc.driver.OracleDriver");
		String login = "lmsitr4";
		con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbip + ":"
				+ dbport, dbpass, dbid);
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
		while (currentday.next()) {
			String date = currentday.getString("sysdate");

			sysdate = dayofdate(date);
			System.out.println("today is " + sysdate);
			// Thread.sleep(9000);

		}

		System.out.println("start");

		ResultSet rs = stmt
				.executeQuery("select * from USERS where LOGIN_ID  = '" + login
						+ "'ORDER BY LOGIN_ID ASC");
		while (rs.next()) {
			String USER_id = rs.getString("USER_ID");
			String LOGIN_ID = rs.getString("LOGIN_ID");
			System.out.println("user id is" + USER_id);
			System.out.println("login id is" + LOGIN_ID);

			Tp = 0;

			ResultSet second = stmt2
					.executeQuery("select * from CHANNEL_USERS where USER_ID = '"
							+ USER_id + "'");
			while (second.next()) {

				String LMS = second.getString("LMS_PROFILE");
				String q1 = "select * from PROFILE_SET where SET_ID = '"
						+ LMS
						+ "' and REF_BASED_ALLOWED = 'Y' and PROMOTION_TYPE = 'STOCK'";
				ResultSet third = stmt3.executeQuery(q1);
				while (third.next()) {

					/*
					 * String Profilename = third.getString("SET_NAME"); String
					 * Profileversion = third.getString("SET_NAME"); String
					 * STOCK = third.getString("PROMOTION_TYPE"); String ref =
					 * third.getString("REF_BASED_ALLOWED"); String opt =
					 * third.getString("OPT_IN_OUT_ENABLED");
					 */
					String q2 = "select version from PROFILE_SET_VERSION where set_id ='"
							+ LMS + "'";
					

					q1 = "select * from PROFILE_SET_VERSION where SET_ID = '"
							+ LMS + "' ORDER BY version DESC";
					ResultSet fourth = stmt4.executeQuery("" + q1);
					while (fourth.next()) {
						String from = fourth.getString("APPLICABLE_FROM");
						String to = fourth.getString("APPLICABLE_TO");
						String version = fourth.getString("VERSION");
						
						
						if (firstrun) {

							firstrun = false;
						} else {
							System.out
									.println("profile have more then one version");
							int v = 1;
							v = Integer.parseInt(version);
							v = v - 1;
							String ver = "select * from PROFILE_SET_VERSION where SET_ID = '"
									+ LMS + "' and VERSION = '" + v + "' ";
							ResultSet changedate = stmt8.executeQuery(ver);
							while (changedate.next()) {
								to = changedate.getString("APPLICABLE_FROM");
							}
						}
						
						from = dayofdate(from);
						to = dayofdate(to);

						String profiles = "select distinct TYPE,service_code,PERIOD_ID from PROFILE_DETAILS where SET_ID = '"
								+ LMS + "' order by type,service_code ASC";

						ResultSet typeandperiod = stmt8.executeQuery(profiles);
						while (typeandperiod.next()) {

							String modual = typeandperiod.getString("TYPE");
							String period = typeandperiod
									.getString("PERIOD_ID");
							String servicecode = typeandperiod
									.getString("service_code");
							String s = "select * from PROFILE_DETAILS where SET_ID = '"
									+ LMS
									+ "' and PERIOD_ID ='"
									+ period
									+ "' and TYPE= '"
									+ modual
									+ "' and service_code ='"
									+ servicecode
									+ "' ORDER BY START_RANGE DESC  ";
							System.out.println(s);

							// start from here
							boolean isTargetCredited = false;
							System.out.println(s);
							ResultSet fifth = stmt5.executeQuery(s);
							while (fifth.next() && !isTargetCredited) {
								String START_RANGE = fifth
										.getString("START_RANGE");
								String END_RANGE = fifth.getString("END_RANGE");
								String POINTS_TYPE = fifth
										.getString("POINTS_TYPE");
								String POINTS = fifth.getString("POINTS");
								String TYPE = fifth.getString("TYPE");
								String module = fifth.getString("SERVICE_CODE");
								String DETAIL_ID = fifth.getString("DETAIL_ID");

								p = Integer.parseInt(POINTS);

								START_RANGE = START_RANGE.substring(0,
										START_RANGE.length() - 2);
								END_RANGE = END_RANGE.substring(0,
										END_RANGE.length() - 2);

								SR = Integer.parseInt(START_RANGE);
								ER = Integer.parseInt(END_RANGE);

								ResultSet sixth1 = stmt6
										.executeQuery("select * from USER_OTH_PROFILES where SET_ID = '"
												+ LMS
												+ "'and DETAIL_ID = '"
												+ DETAIL_ID
												+ "'and version ='2' and USER_ID ='"
												+ USER_id + "'");
								while (sixth1.next()) {
									String TARGET = sixth1.getString("TARGET");
									TARGET = TARGET.substring(0,
											TARGET.length() - 2);
									SR = Integer.parseInt(TARGET);
									System.out.println("SR 2: " + SR);
								}

								int profileduration = 1;
								String q = "";
								String sumvalue = "";

								if (TYPE.equalsIgnoreCase("C2S")) {
									q = querry(TYPE, module, USER_id);

								} else {
									q = querry(TYPE, USER_id);
								}

								profileduration = duration(period);

								sumvalue = sumtype(TYPE);
								if (q != null) {
									isTargetCredited = profiletype(
											profileduration, isTargetCredited,
											from, to, q, sumvalue, POINTS_TYPE,
											POINTS);

								}

							}
						}
					}
					System.out.println("total point is " + Tp);
					System.out.println(verify(login, Tp));
				}
			}
		}
	}

	public static int duration(String period) {
		int profileduration = 0;
		if (period.equalsIgnoreCase("Daily")) {
			profileduration = 1;
		}
		if (period.equalsIgnoreCase("Weekly")) {
			profileduration = 7;
		}

		if (period.equalsIgnoreCase("Monthly")) {
			profileduration = 30;
		}
		if (period.equalsIgnoreCase("Eop")) {
			profileduration = 1000;
		}
		return profileduration;
	}

	public static String querry(String TYPE, String module, String USER_id) {
		String q = "";

		if (isAll) {
			if (module.equalsIgnoreCase("All")) {
				q = "select sum(TRANSFER_VALUE) from C2S_TRANSFERS where SENDER_ID = '"
						+ USER_id
						+ "' and TRANSFER_STATUS = '200' and TRANSFER_DATE";
				isAll = false;

			} else {
				q = "select sum(TRANSFER_VALUE) from C2S_TRANSFERS where SENDER_ID = '"
						+ USER_id
						+ "' and TRANSFER_STATUS = '200' and service_type ='"
						+ module + "' and TRANSFER_DATE ";
			}

		}

		return q;
	}

	public static String querry(String TYPE, String USER_id) {
		String q = "";

		if (TYPE.equalsIgnoreCase("C2C")) {
			q = "select sum(TRANSFER_MRP) from CHANNEL_TRANSFERS where (FROM_USER_ID = '"
					+ USER_id
					+ "' OR TO_USER_ID = '"
					+ USER_id
					+ "') and TYPE = 'C2C' and STATUS = 'CLOSE' and TRANSFER_DATE ";
		}
		if (TYPE.equalsIgnoreCase("O2C")) {
			q = "select sum(TRANSFER_MRP) from CHANNEL_TRANSFERS where TO_USER_ID = '"
					+ USER_id
					+ "' and TYPE = 'O2C' and STATUS = 'CLOSE' and TRANSFER_DATE ";
		}
		return q;
	}

	public static String sumtype(String TYPE) {
		String q = "";
		if (TYPE.equalsIgnoreCase("C2S")) {
			q = "sum(TRANSFER_VALUE)";
		}
		if (TYPE.equalsIgnoreCase("C2C")) {
			q = "sum(TRANSFER_MRP)";
		}
		if (TYPE.equalsIgnoreCase("O2C")) {
			q = "sum(TRANSFER_MRP)";
		}
		return q;
	}

	public static Boolean profiletype(int profileduration,
			boolean isTargetCredited, String from, String to, String querry,
			String sumvalue, String POINTS_TYPE, String POINTS)
			throws ParseException, ClassNotFoundException, SQLException {
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
		Boolean lessthensysdate = true;
		while (fr.before(To) && -1 != To.compareTo(fr)) {

			c.setTime(fr);
			c.add(Calendar.DATE, i - 1);
			to = dateformat.format(c.getTime());
			fr = new SimpleDateFormat("MM/dd/yyyy").parse(to);
			if (-1 == To.compareTo(fr))
				break;

			Date check = new Date();
			Date Tos = new Date();

			check = new SimpleDateFormat("MM/dd/yyyy").parse(sysdate);
			Tos = new SimpleDateFormat("MM/dd/yyyy").parse(to);

			if (check.after(Tos)) {
				lessthensysdate = true;

			} else {
				lessthensysdate = false;

			}

			String datequer = "";

			datequer = "between TO_DATE('" + from
					+ "', 'MM/DD/YYYY HH24:MI:SS') and TO_DATE('" + to
					+ "', 'MM/DD/YYYY HH24:MI:SS')";

			if (lessthensysdate) {

				if (querry != "") {

					isTargetCredited = target(querry + datequer,
							isTargetCredited, sumvalue, POINTS_TYPE, POINTS);
					return isTargetCredited;
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
		return isTargetCredited;

	}

	public static boolean target(String querry, Boolean isTargetCredited,
			String sumvalue, String POINTS_TYPE, String POINTS)
			throws ClassNotFoundException, SQLException {
		p = Integer.parseInt(POINTS);
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"
				+ dbip + ":" + dbport, dbpass, dbid);
		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery(querry);

		while (rs.next()) {
			String sum = rs.getString(sumvalue);
			int amount = 0;

			if (sum != null) {
				amount = Integer.parseInt(sum);
				amount = amount / 100;

			}

			if (SR < amount) {
				if (POINTS_TYPE.equals("PCT")) {
					RC = amount - SR;
					float a = (RC * p);
					int ck = Math.round(a / 100);
					int bonus = 0;
					bonus = ck;
					Tp = Tp + bonus;
					isTargetCredited = true;
					System.out.println(querry);
					System.out.println(bonus);
				} else {
					Tp = Tp + p;
					isTargetCredited = true;

				}
			}

		}
		return isTargetCredited;

	}

	public static boolean verify(String loginid, int points)
			throws ClassNotFoundException, SQLException {
		String a = "0";
		int totalpoints = 0, pointsuserhave = 0, redeempoints = 0;

		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"
				+ dbip + ":" + dbport, dbpass, dbid);
		Statement stmt = con.createStatement();

		String q = "select * from BONUS where USER_ID_OR_MSISDN =(select USER_ID from users where LOGIN_ID ='"
				+ loginid + "')";
		String q2;
		q2 = "select sum(POINTS_REDEEMED) from REDEMPTIONS where USER_ID_OR_MSISDN = (select USER_ID from users where LOGIN_ID ='"
				+ loginid + "')";
		ResultSet rs = stmt.executeQuery(q);
		while (rs.next()) {
			a = rs.getString("ACCUMULATED_POINTS");
			if (a != null) {
				pointsuserhave = Integer.parseInt(a);
			} else
				pointsuserhave = 0;
		}

		ResultSet re = stmt.executeQuery(q2);
		while (re.next()) {
			a = re.getString("sum(POINTS_REDEEMED)");
			if (a != null) {
				redeempoints = Integer.parseInt(a);
			} else
				redeempoints = 0;
		}
	
		totalpoints = pointsuserhave + redeempoints;

		if (totalpoints == points) {

			return true;
		} else {

			System.out.println("totalpoints user earned "+totalpoints);
			System.out.println("points user have : "+pointsuserhave+" after redemption of "+redeempoints+" amount");
			System.out.println("difference between actual points and earn points are "+(totalpoints-points));
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

}
