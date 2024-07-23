package DataVerification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import common_util_script.ExtentReportMultipleClasses;
import common_util_script.logs;

public class baseClass extends ExtentReportMultipleClasses{

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
	public static String dbid="pgdb";
	public static String dbpass="pgdb";
	public static String dbip="172.16.11.121";
	public static String dbport="5432/pretups7_test2";
	public static String daily = "";
	public static String weeklydate = "";
	public static boolean firstrun = true;
	private static String databaseURL;
	private static String dbuser;
	private static String dbpassword;

	public void createconnection() throws ClassNotFoundException, SQLException, IOException {
		/*Class.forName("oracle.jdbc.driver.OracleDriver");
		con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbip + ":"
				+ dbport, dbpass, dbid);*/
		/*Class.forName("org.postgresql.Driver");
		con = DriverManager.getConnection("jdbc:postgresql://["+dbip+"]:"+dbport);*/
		
		FileInputStream fileInput = new FileInputStream(new File(
				"dataFile.properties"));
		// Create Properties object to read the credentials
		Properties prop = new Properties();
		// load properties file to read the credentials
		prop.load(fileInput);
		
		databaseURL = "jdbc:postgresql://["+prop.getProperty("dbip")+"]:"+prop.getProperty("dbport") ;
		dbuser = prop.getProperty("dbid");
		dbpassword = prop.getProperty("dbpass");
		
		try {
			// Class.forName("com.mysql.jdbc.Driver"); // For MySQL Server
			//Class.forName("oracle.jdbc.driver.OracleDriver"); // For Oracle
																// Server
			Class.forName("org.postgresql.Driver");  // For Postgres Server
        	
			System.out.println("You have entered DB username as : " + dbuser);
			System.out.println("You have entered DB password as : "	+ dbpassword);
			System.out.println("You have entered Database URL as : "+ databaseURL);

			con = DriverManager.getConnection(databaseURL, dbuser, dbpassword);
			if (con != null) {
				System.out.println("Connected to the Database...");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		
		Statement stmt = con.createStatement();		
		stmt9 = con.createStatement();
		stmt10 = con.createStatement();

		//ResultSet currentday = stmt.executeQuery("select sysdate from dual");
		ResultSet currentday = stmt.executeQuery("select CURRENT_TIMESTAMP");
		while (currentday.next()) {
			//String date = currentday.getString("sysdate");
			String date = currentday.getString("now");
			sysdate = dayofdate(date);
			logs.info("Connections is created");

		}

	}
	
	public String getversion(String LMS,String todate) throws ClassNotFoundException, SQLException,
			ParseException {

		
		//createconnection();
		Statement stmt = con.createStatement();

		Boolean istrue=true;
		ResultSet currentday = stmt.executeQuery("select * from PROFILE_SET_VERSION where SET_ID = '"+LMS+"' order by VERSION desc");
		while (currentday.next()&&istrue) {
			String to = currentday.getString("APPLICABLE_TO");
			String from = currentday.getString("APPLICABLE_FROM");
			String version = currentday.getString("VERSION");
			
			to=dayofdate(to);
			from=dayofdate(from);
			//logs.info("from "+from+" to "+to+" version "+version);
			Boolean istrue1=Inbetweendays(from, to, todate);
			
			if(istrue1){			
				
				//logs.info("version is "+version);
				istrue=false;
				return version;
				
			}
		}
		return todate;
		
		
		
		
	}
	
	/**
	 * To check date is between tym or not
	 * @param startday
	 * @param endday
	 * @param checkdate
	 * @return
	 * @throws ParseException
	 */
	public Boolean Inbetweendays(String startday ,String endday ,String checkdate) throws ParseException{
		String from1 = "05/02/2016", from2 = "05/11/2016";
		String bt = "05/02/2016";		
		from1=startday;
		from2=endday;
		bt=checkdate;
		
		Date btw = new Date();
		Date fr = new Date();
		Date To = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(dateformat.parse(from1));
		c.add(Calendar.DATE, 0);
		from1 = dateformat.format(c.getTime());

		c.setTime(dateformat.parse(from2));
		c.add(Calendar.DATE, 0);
		from2 = dateformat.format(c.getTime());
		
		c.setTime(dateformat.parse(bt));
		c.add(Calendar.DATE, 0);
		bt = dateformat.format(c.getTime());
		
		
		fr = new SimpleDateFormat("MM/dd/yyyy").parse(from1);
		To = new SimpleDateFormat("MM/dd/yyyy").parse(from2);		
		btw = new SimpleDateFormat("MM/dd/yyyy").parse(bt);
		
		// if(fr.before(btw)){
		if (fr.before(btw) && btw.before(To)) {
			//logs.info("in between"+fr);
			return true;

		} else if (To.equals(btw)||fr.equals(btw)) {
			//logs.info("same date "+fr);
			return true;
		} else {
			//logs.info("not in  between");
			return false;
		}
		
	}
	
	public  void profiletyperef(int profileduration,String period ,String from, String to,String LMS ,String modual,String servicecode,String USER_id)
			throws ParseException, ClassNotFoundException, SQLException {
		int i = 0;
		i = profileduration;
		String eopfrom=from;
		String eopto=to;
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
			
			
			String datequer=" and TRANSFER_DATE between TO_DATE('" + from
					+ "', 'MM/DD/YYYY HH24:MI:SS') and TO_DATE('" + to
					+ "', 'MM/DD/YYYY HH24:MI:SS')";
			if(profileduration==0){
				to=eopto;
				datequer=" and TRANSFER_DATE between TO_DATE('" + eopfrom
						+ "', 'MM/DD/YYYY HH24:MI:SS') and TO_DATE('" + eopto
						+ "', 'MM/DD/YYYY HH24:MI:SS')";
				
			}
			
			if(lessthensysdate){
				String ver=getversion(LMS, to);
				
				ref(LMS, datequer, period, modual, servicecode, ver, USER_id);
				
				//logs.info("ver we have "+ver+" for date "+to);
				
				
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
		

	}
	
	
	public  void profiletypeNonref(int profileduration,String period ,String from, String to,String LMS ,String modual,String servicecode,String USER_id)
			throws ParseException, ClassNotFoundException, SQLException {
		int i = 0;
		i = profileduration;
		String eopfrom=from;
		String eopto=to;
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
			if (-1 == To.compareTo(fr))
				break;

			Date check = new Date();
			Date Tos = new Date();
			//System.out.println(sysdate);
			check = new SimpleDateFormat("MM/dd/yyyy").parse(sysdate);
			Tos = new SimpleDateFormat("MM/dd/yyyy").parse(to);

			if (check.after(Tos)) {
				lessthensysdate = true;

			} else {
				lessthensysdate = false;

			}
			
			
			String datequer=" and TRANSFER_DATE between TO_DATE('" + from
					+ "', 'MM/DD/YYYY HH24:MI:SS') and TO_DATE('" + to
					+ "', 'MM/DD/YYYY HH24:MI:SS')";
			if(profileduration==0){
				to=eopto;
				datequer=" and TRANSFER_DATE between TO_DATE('" + eopfrom
						+ "', 'MM/DD/YYYY HH24:MI:SS') and TO_DATE('" + eopto
						+ "', 'MM/DD/YYYY HH24:MI:SS')";
				
			}
			
			if(lessthensysdate){
				String ver=getversion(LMS, to);
				
				
					Nonref(LMS, datequer, period, modual, servicecode, ver, USER_id);
				
			
				
				
				//logs.info("ver we have "+ver+" for date "+to);
				
				
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
		

	}

	public void ref(String LMS,String datequer,String period,String modual,String servicecode,String ver,String USER_id) throws SQLException, ClassNotFoundException{
		Statement stmt5 = con.createStatement();
		Statement stmt6 = con.createStatement();
		String s = "select * from PROFILE_DETAILS where SET_ID = '"
				+ LMS
				+ "' and PERIOD_ID ='"
				+ period
				+ "' and TYPE= '"
				+ modual
				+ "' and service_code ='"
				+ servicecode
				+ "' and VERSION ='"+ver+"' ORDER BY START_RANGE DESC  ";
		//logs.info(s);

		// start from here
		boolean isTargetCredited = false;
		//logs.info("this "+s);
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
			SR = Integer.parseInt(START_RANGE);
			ER = Integer.parseInt(END_RANGE);
			String q;
			
			ResultSet sixth1 = stmt6
					.executeQuery("select * from USER_OTH_PROFILES where SET_ID = '"
							+ LMS
							+ "'and DETAIL_ID = '"
							+ DETAIL_ID
							+ "'and version ='"+ver+"' and USER_ID ='"
							+ USER_id + "'");
			while (sixth1.next()) {
				String TARGET = sixth1.getString("TARGET");				
				SR = Integer.parseInt(TARGET);
			
			}
		
			
			
			
			if (TYPE.equalsIgnoreCase("C2S")) {
				q = targetQ(TYPE, module, USER_id);

			} else {
				q = targetQ(TYPE, USER_id);
			}
			//logs.info(q);
			//logs.info(" for version "+ver);
			q=q+datequer;
			String sumvalue=sumtype(TYPE);
			isTargetCredited=target(q, isTargetCredited, sumvalue, POINTS_TYPE, POINTS);
			
		}
	}

	

	
	
	
	
	
	
	
	
	public void Nonref(String LMS,String datequer,String period,String modual,String servicecode,String ver,String USER_id) throws SQLException, ClassNotFoundException{
		Statement stmt5 = con.createStatement();
		
		String s = "select * from PROFILE_DETAILS where SET_ID = '"
				+ LMS
				+ "' and PERIOD_ID ='"
				+ period
				+ "' and TYPE= '"
				+ modual
				+ "' and service_code ='"
				+ servicecode
				+ "' and VERSION ='"+ver+"' ORDER BY START_RANGE DESC  ";
		//logs.info(s);

		// start from here
		boolean isTargetCredited = false;
		//logs.info("this "+s);
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
			p = Integer.parseInt(POINTS);
			SR = Integer.parseInt(START_RANGE);
			ER = Integer.parseInt(END_RANGE);
			String q;
			if (TYPE.equalsIgnoreCase("C2S")) {
				q = targetQ(TYPE, module, USER_id);

			} else {
				q = targetQ(TYPE, USER_id);
			}
			//logs.info(q);
			//logs.info(" for version "+ver);
			if(q==""){}
			else{
				q=q+datequer;
				String sumvalue=sumtype(TYPE);
				isTargetCredited=target(q, isTargetCredited, sumvalue, POINTS_TYPE, POINTS);
					
			}
			
		}
	}

	
	public static boolean targetcal(String querry, Boolean isTargetCredited,String sumvalue, String POINTS_TYPE, String POINTS)
			throws ClassNotFoundException, SQLException {
		p = Integer.parseInt(POINTS);
	
		
	
		Statement stmt = con.createStatement();
		//logs.info(sumvalue+" "+querry);
		ResultSet rs = stmt.executeQuery(querry);

		while (rs.next()) {
			String sum = rs.getString(sumvalue);
			int amount = 0;

			if (sum != null) {
				amount = Integer.parseInt(sum);
			}

			
				if (POINTS_TYPE.equals("PCT")) {
				
					RC = amount;
					float a = (RC * p);
					int ck = Math.round(a / 100);
					int bonus = 0;
					bonus = ck;
					Tp = Tp + bonus;
					isTargetCredited = true;
					System.out.print(bonus+" FOR ");
					logs.info(querry);
					
				} else {
					Tp = Tp + p;
					isTargetCredited = true;

				}
			}

		
		return isTargetCredited;

	}
	
	
	
	public static boolean target(String querry, Boolean isTargetCredited,
			String sumvalue, String POINTS_TYPE, String POINTS)
			throws ClassNotFoundException, SQLException {
		p = Integer.parseInt(POINTS);
	
		
	
		Statement stmt = con.createStatement();
		System.out.println(querry);
		//logs.info(sumvalue+" "+querry);
		ResultSet rs = stmt.executeQuery(querry);

		while (rs.next()) {
			String sum = rs.getString(sumvalue);
			int amount = 0;

			if (sum != null) {
				amount = Integer.parseInt(sum);
				//amount=amount/100;
				
			}

			if (SR < amount) {
				if (POINTS_TYPE.equals("PCT")) {
					System.out.println(amount);
					RC = amount - SR;
					RC=RC/100;
					float a = (RC * p);
					
					System.out.println("value of a "+a);
					int ck = Math.round(a / 100);
					System.out.println("value of ck "+ck);
					int bonus = 0;
					bonus = ck;
					Tp = Tp + bonus;
					System.out.print("bonus is "+bonus);
					isTargetCredited = true;
					logs.info(querry);
					
				} else {
					Tp = Tp + p;
					isTargetCredited = true;

				}
			}

		}
		return isTargetCredited;

	}


	public static String targetQ(String TYPE, String module, String USER_id) {
		String q = "";

		
			if (module.equalsIgnoreCase("All")) {
				
				q = "select sum(TRANSFER_VALUE) as TRANSFER_VALUE from C2S_TRANSFERS where SENDER_ID = '"
						+ USER_id
						+ "' and TRANSFER_STATUS = '200' ";
				isAll = false;
				

			} else {
				
				if (isAll) {
				
				q = "select sum(TRANSFER_VALUE) as TRANSFER_VALUE from C2S_TRANSFERS where SENDER_ID = '"
						+ USER_id
						+ "' and TRANSFER_STATUS = '200' and service_type ='"
						+ module + "' ";
				}
			}

		

		return q;
	}

	public static String targetQ(String TYPE, String USER_id) {
		String q = "";

		if (TYPE.equalsIgnoreCase("C2C")) {
			q = "select sum(REQUESTED_QUANTITY) as REQUESTED_QUANTITY from CHANNEL_TRANSFERS where (FROM_USER_ID = '"
					+ USER_id
					+ "' OR TO_USER_ID = '"
					+ USER_id
					+ "') and TYPE = 'C2C' and STATUS = 'CLOSE'  ";
		}
		if (TYPE.equalsIgnoreCase("O2C")) {
			q = "select sum(REQUESTED_QUANTITY) as REQUESTED_QUANTITY from CHANNEL_TRANSFERS where TO_USER_ID = '"
					+ USER_id
					+ "' and TYPE = 'O2C' and STATUS = 'CLOSE'  ";
		}
		return q;
	}
	public static String sumtype(String TYPE) {
		String q = "";
		if (TYPE.equalsIgnoreCase("C2S")) {
			q = "TRANSFER_VALUE";
		}
		if (TYPE.equalsIgnoreCase("C2C")) {
			q = "REQUESTED_QUANTITY";
		}
		if (TYPE.equalsIgnoreCase("O2C")) {
			q = "REQUESTED_QUANTITY";
		}
		return q;
	}

	public static boolean verify(String loginid, int points)
			throws ClassNotFoundException, SQLException {
		String a = "0";
		int mul=1;
		int totalpoints = 0, pointsuserhave = 0, redeempoints = 0;

		Statement stmt = con.createStatement();

		String q3="select * from  SYSTEM_PREFERENCES where PREFERENCE_CODE = 'AMOUNT_MULT_FACTOR'";
		ResultSet rs3 = stmt.executeQuery(q3);
		while (rs3.next()) {
			String value = rs3.getString("DEFAULT_VALUE");
			mul=Integer.parseInt(value);
		}
		
		String q = "select * from BONUS where USER_ID_OR_MSISDN =(select USER_ID from users where LOGIN_ID ='"
				+ loginid + "')";
		String q2;
		q2 = "select sum(POINTS_REDEEMED) AS POINTS_REDEEMED from REDEMPTIONS where USER_ID_OR_MSISDN = (select USER_ID from users where LOGIN_ID ='"
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
			a = re.getString("POINTS_REDEEMED");
			if (a != null) {
				redeempoints = Integer.parseInt(a);
			} else
				redeempoints = 0;
		}

		//points=points/mul;
		totalpoints = pointsuserhave + redeempoints;

		if (totalpoints == points) {

			return true;
		} else {
			logs.info("points usher should have"+points);
			logs.info("totalpoints user earned " + totalpoints);
			logs.info("points user have : " + pointsuserhave+ " after redemption of " + redeempoints + " amount");
			logs.info("difference between actual points and earn points are "+ (totalpoints - points));
			return false;
		}

	}
	
	
	
	/**
	 * to get max date
	 * @param fr
	 * @param To
	 */
	
	public Date greaterdate(Date fr,Date To){
		Date max = null;
		if(fr.before(To)){
			max=To;
			
		}
		else if(fr.equals(To)){
			max=To;
		}
		else{
			max=fr;
			
		}
		return max;
		
		
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
