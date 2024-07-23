package common_util_script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import com.mysql.jdbc.Connection;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

public class DB_Connection {

	private static Connection con = null;
	private static Statement statement;
	private static ResultSet rs=null;
	public static String dbvalue = null;
	private static String databaseURL;
	private static String dbuser;
	private static String dbpassword;

	public static String datafromdb(String query, String value)
			throws IOException {
		FileInputStream fileInput = new FileInputStream(new File(
				"dataFile.properties"));
		// Create Properties object to read the credentials
		Properties prop = new Properties();
		// load properties file to read the credentials
		prop.load(fileInput);
		
		databaseURL = "jdbc:oracle:thin:@"+prop.getProperty("dbip")+":"+prop.getProperty("dbport") ;
		//databaseURL = "jdbc:postgresql://"+prop.getProperty("dbip")+":"+prop.getProperty("dbport") ;
		dbuser = prop.getProperty("dbid");
		dbpassword = prop.getProperty("dbpass");
		
		try {
			/*Map<String,String> cacheMap = Read_Properties_File.getCachemap();
			String databaseURL = cacheMap.get("databaseURL1");
			System.out.println("DB URL is:" + databaseURL);*/
			// Class.forName("com.mysql.jdbc.Driver"); // For MySQL Server
			Class.forName("oracle.jdbc.driver.OracleDriver"); // For Oracle
																// Server
			//Class.forName("org.postgresql.Driver");// For Postgres Server
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

		try {
			System.out.println("You are running this query : " + query);
			// Get the contents of userinfo table from DB

			System.out.println("creating connection");
			// Create Statement Object
			statement = con.createStatement();
			System.out.println("running statment");
			// Execute the SQL Query. Store results in ResultSet
			rs = statement.executeQuery(query);
			System.out.println(" query");
			while (rs.next()) {
				System.out.println("Your value is " + rs.getString(value));
				dbvalue = rs.getString(value);
			}

		} catch (Exception e) {
			System.out.println("Issue with your DB script");

		}
		return dbvalue;

	}

}
