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

public class baseClassTrans {

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
	public static String weeklydate = "";
	public static boolean firstrun = true;

	public void createconnection() throws ClassNotFoundException, SQLException {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		con = DriverManager.getConnection("jdbc:oracle:thin:@" + dbip + ":"
				+ dbport, dbpass, dbid);
		Statement stmt = con.createStatement();
		stmt9 = con.createStatement();
		stmt10 = con.createStatement();

		ResultSet currentday = stmt.executeQuery("select sysdate from dual");
		while (currentday.next()) {
			String date = currentday.getString("sysdate");

			sysdate = dayofdate(date);
			System.out.println("Connections is created");

		}

	}

	public String getversion(String LMS, String todate)
			throws ClassNotFoundException, SQLException, ParseException {

		// createconnection();
		Statement stmt = con.createStatement();

		Boolean istrue = true;
		ResultSet currentday = stmt
				.executeQuery("select * from PROFILE_SET_VERSION where SET_ID = '"
						+ LMS + "' order by VERSION desc");
		while (currentday.next() && istrue) {
			String to = currentday.getString("APPLICABLE_TO");
			String from = currentday.getString("APPLICABLE_FROM");
			String version = currentday.getString("VERSION");

			to = dayofdate(to);
			from = dayofdate(from);
			// System.out.println("from "+from+" to "+to+" version "+version);
			Boolean istrue1 = Inbetweendays(from, to, todate);

			if (istrue1) {

				// System.out.println("version is "+version);
				istrue = false;
				return version;

			}
		}
		return todate;

	}

	/**
	 * To check date is between tym or not
	 * 
	 * @param startday
	 * @param endday
	 * @param checkdate
	 * @return
	 * @throws ParseException
	 */
	public Boolean Inbetweendays(String startday, String endday,
			String checkdate) throws ParseException {
		String from1 = "05/02/2016", from2 = "05/11/2016";
		String bt = "05/02/2016";
		from1 = startday;
		from2 = endday;
		bt = checkdate;

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
			// System.out.println("in between"+fr);
			return true;

		} else if (To.equals(btw) || fr.equals(btw)) {
			// System.out.println("same date "+fr);
			return true;
		} else {
			// System.out.println("not in  between");
			return false;
		}

	}

	public  void profiletypeTrans(int profileduration,String period ,String from, String to,String LMS ,String modual,String servicecode,String USER_id)
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
			
			
			String datequer=" and TRANSFER_DATE = TO_DATE('" + to
					+ "', 'MM/DD/YYYY HH24:MI:SS')";
			
			if(lessthensysdate){
				String ver=getversion(LMS, to);
				Trans(LMS, datequer, period, modual, servicecode, ver, USER_id);
			
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
	
	
	
	
	
	
	public void Trans(String LMS,String datequer,String period,String modual,String servicecode,String ver,String USER_id) throws SQLException, ClassNotFoundException{
		Statement stmt5 = con.createStatement();
		
		String s = "select * from PROFILE_DETAILS where SET_ID = '"
				+ LMS
				+ "' and TYPE= '"
				+ modual
				+ "' and service_code ='"
				+ servicecode
				+ "' and VERSION ='"+ver+"'  ";

		ResultSet fifth = stmt5.executeQuery(s);
		while (fifth.next() ) {
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
			String q = null;
			
			if (TYPE.equalsIgnoreCase("C2S")) {
				q=TransQ(TYPE, module, USER_id,ver);
			} else {
				q=TransQ(TYPE, USER_id);
			}
			if(!q.equalsIgnoreCase("")){
				q=q+datequer;
				String sumvalue=sumtype(TYPE);				
				Tp=trans(q, Tp, sumvalue, POINTS_TYPE, POINTS,TYPE);
			}
		}
	}

	public static String TransQ(String TYPE, String module, String USER_id,String ver) {
		String q = "";
		if(isAll){
			q = "select * from C2S_TRANSFERS where SENDER_ID = '" + USER_id
					+ "' and TRANSFER_STATUS = '200' ";	
		
			isAll = false;		
		}else{			
			q = "select * from C2S_TRANSFERS where SENDER_ID = '" + USER_id
					+ "' and TRANSFER_STATUS = '200' and service_type ='" + module+ "' ";			
		}
			
		
		return q;
	}

	public static String TransQ(String TYPE, String USER_id) {
		String q = "";
		if (TYPE.equalsIgnoreCase("C2C")) {
			q = "select * from CHANNEL_TRANSFERS where (FROM_USER_ID = '"
					+ USER_id + "' OR TO_USER_ID = '" + USER_id
					+ "') and TYPE = 'C2C' and STATUS = 'CLOSE'  ";
		}
		if (TYPE.equalsIgnoreCase("O2C")) {
			q = "select * from CHANNEL_TRANSFERS where TO_USER_ID = '"
					+ USER_id + "' and TYPE = 'O2C' and STATUS = 'CLOSE'  ";		
		}
		
		if (TYPE.equalsIgnoreCase("C2S")) {
			if(isAll){
				q = "select * from C2S_TRANSFERS where SENDER_ID = '" + USER_id
						+ "' and TRANSFER_STATUS = '200' ";	
			}
			else{
				
				q = "select * from C2S_TRANSFERS where SENDER_ID = '" + USER_id
						+ "' and TRANSFER_STATUS = '200' ";	
				System.out.println("no more points");
			}
			
		}
		
		
		
		return q;
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

		points=points/mul;
		totalpoints = pointsuserhave + redeempoints;

		if (totalpoints == points) {

			return true;
		} else {
			System.out.println("points usher should have"+points);
			System.out.println("totalpoints user earned " + totalpoints);
			System.out.println("points user have : " + pointsuserhave+ " after redemption of " + redeempoints + " amount");
			System.out.println("difference between actual points and earn points are "+ (totalpoints - points));
			return false;
		}

	}
	
	
	
	
	
	public static int trans(String querry, int Tp, String sumvalue,
			String POINTS_TYPE, String POINTS,String TYPE) throws ClassNotFoundException,
			SQLException {
		int p = 0;
		p = Integer.parseInt(POINTS);

		Statement stmt = con.createStatement();
		
		
		
		ResultSet rs = stmt.executeQuery(querry);
		while (rs.next()) {
			String sum = rs.getString(sumvalue);
			int amount = 0;

			if (sum != null) {
				amount = Integer.parseInt(sum);
				RC = amount;		
				
			}else{
				RC=0;
			}

			if (RC >= SR && RC <= ER) {
				if (POINTS_TYPE.equals("PCT")) {
					float a = (RC * p);
					int ck = Math.round(a);
					System.out.println("value of a "+a);
					a=a/100;
					int bonus = 0;
					bonus = ck;
					Tp = Tp + bonus;
				} else {
					Tp = Tp + p;

				}
			}

		}
		return Tp;

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
