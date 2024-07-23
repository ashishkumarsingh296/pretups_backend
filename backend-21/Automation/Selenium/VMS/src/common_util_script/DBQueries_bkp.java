package common_util_script;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import com.mysql.jdbc.Connection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


 public class DBQueries_bkp {

	 private static Connection con = null;
	 private static Statement statement;
	 private static ResultSet rs;
	 private static ResultSet rs1;
	 public static String dbvalue;
	 public static String countvalue;
	 public static int cntavlue;
	 public static int a[] ;
	 public static ArrayList <String[]> result = new ArrayList<String[]>();
	 
	 
	  
	//public static int[] datafromdb (String query, String value) throws Exception{
	 
	 public static void connecttoDB(){
		 
            try {
            	
            Map<String, String> cacheMap = Read_Properties_File.getCachemap(); 
	   	 	
             String databaseURL = cacheMap.get("databaseURL1");  // from contant.props file
             System.out.println("DB URL is: " + databaseURL );
	   	 	 String dbuser = cacheMap.get("dbuser1");
	   	 	 System.out.println("DB User is: " + dbuser); 
	   		 String dbpassword = cacheMap.get("dbpassword1");
	   		 System.out.println("DB Password is: " + dbpassword);
	   		 //List<String> dbvalue = new ArrayList<String>();
	   		 
	   		 //Class.forName("com.mysql.jdbc.Driver");   // For MySQL Server
            Class.forName("oracle.jdbc.driver.OracleDriver");  // For Oracle Server
            	
            	
               con = DriverManager.getConnection(databaseURL, dbuser, dbpassword);
			                if (con != null) {
			                    System.out.println("Connected to the Database...");
			                }
		          } catch (SQLException ex) {
		               ex.printStackTrace();
		          }  catch (ClassNotFoundException ex) {
		               ex.printStackTrace();
		          }
	 		}
	 
	 public static List<String> runningDBquery(String query) throws SQLException{
          
		 		List<String> results = new ArrayList<String>();
		 		//System.out.println("Your query is : " + query);
                // Get the contents of userinfo table from DB

				//Create Statement Object
	             statement = con.createStatement();
	             //System.out.println("Now running your query");
	             // Execute the SQL Query. Store results in ResultSet
	             rs = statement.executeQuery(query);
	             int columnCount = rs.getMetaData().getColumnCount();
	             //System.out.println("Column count is: " + columnCount);
	             String[] row = new String[columnCount];	             
	             while (rs.next())
                     {
	            	 	//System.out.println("Your value is  " + value+ " : " + rs.getString());
	            	 	//dbvalue = rs.getString(1);
	            	 	//String[] row = new String[columnCount];
	            	 
	            	    for (int i=0; i <columnCount ; i++)
	            	    {
	            	       //row[i] = rs.getString(i + 1);
	            	    	results.add(rs.getString(i + 1));
	            	       //System.out.println("Row value is: " + row[i]);
	            	    	
	            	     }
	            	   
                     }
	             if(results.size() > 0){
	            	 //System.out.println("your size is > 0");
	            	 return results;
	             } else {
	            	 //System.out.println("your size is = 0");
	            	 return Collections.emptyList();	            	 
	             }
               } 
		
				   

	 	public static void dbtearDown() {
	      if (con != null) {
	                try {
	                    System.out.println("Closing Database Connection...");
	                    con.close();
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                }
	            }
	      }
 }