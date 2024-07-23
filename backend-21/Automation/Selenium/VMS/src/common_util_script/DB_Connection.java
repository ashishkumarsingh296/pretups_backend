package common_util_script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	 private static ResultSet rs;
	 private static ResultSet rs1;
	 public static String dbvalue;
	 public static String countvalue;
	 public static int cntavlue;
	 public static int a[] ;
	 
	public static int[] datafromdb (String query, String value) throws Exception{
	        
            try {
            	
            Map<String, String> cacheMap = Read_Properties_File.getCachemap(); 
	   	 	
             String databaseURL = cacheMap.get("databaseURL1");  // from contant.props file
             System.out.println("DB URL is: " + databaseURL );
	   	 	 String dbuser = cacheMap.get("dbuser1");
	   	 	 System.out.println("DB User is: " + dbuser); 
	   		 String dbpassword = cacheMap.get("dbpassword1");
	   		 System.out.println("DB Password is: " + dbpassword);
	   		 
	   		 //Class.forName("com.mysql.jdbc.Driver");   // For MySQL Server
            //Class.forName("oracle.jdbc.driver.OracleDriver");  // For Oracle Server
            Class.forName("org.postgresql.Driver");  // For Postgres Server
            	
               con = DriverManager.getConnection(databaseURL, dbuser, dbpassword);
			                if (con != null) {
			                    System.out.println("Connected to the Database...");
			                }
		          } catch (SQLException ex) {
		               ex.printStackTrace();
		          }  catch (ClassNotFoundException ex) {
		               ex.printStackTrace();
		          }
            
            try{ 
            	
                System.out.println("Your query is : " + query);
                // Get the contents of userinfo table from DB

				//Create Statement Object
	             statement = con.createStatement();
	             System.out.println("Now running your query");
	             // Execute the SQL Query. Store results in ResultSet
	             rs = statement.executeQuery(query);
	             	             
	             while (rs.next())
                     {
                                     
                                     System.out.println("Your value is  " + value+ " : " + rs.getString(value));
                                     dbvalue = rs.getString(value);
                  }

               } 
            catch(Exception e)
		          {
		             System.out.println("Issue with your DB script");
		            
		          }
			return a;     
            }
	
 }