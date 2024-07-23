package com.dbrepository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.classes.BaseTest;
import com.commons.MasterI;
import com.utils.Log;
import com.utils._masterVO;

public class DBUtil extends BaseTest {
private Connection connection;       
private String URL, USERNAME, PASSWORD;     
private Statement stmt;
public ResultSet rset;

/**
 * @author krishan.chawla
 * This function is used to Open DB Connection with Database Details available in Master Sheet of DataProvider
 * @throws Exception
 * 
 * Dependencies: Master Sheet Populated with application details in DataProvider
 */
public void OpenDBConnection(){
	try{
			URL = _masterVO.getMasterValue("DB Connection string");
			USERNAME = _masterVO.getMasterValue(MasterI.DB_USERNAME);
			PASSWORD = _masterVO.getMasterValue(MasterI.DB_PASSWORD);
			// Load the JDBC driver
			String driverName = "oracle.jdbc.driver.OracleDriver";
			Class.forName(driverName);
			// Create a connection to the database
			Log.info("Trying to Open Connection to Database");
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			Log.info("Database connection opened successfully");
		}
	catch (ClassNotFoundException e){
			Log.info("Class not found from database : " + e);
		}
	catch (SQLException e1){
			Log.info("Database Connection Error: " + e1);
			}
	}

/**
* Execute a Query
* @param Query
* @return
* @throws IOException 
*/
public ResultSet executeQuery(String Query) {
	try{
		stmt = connection.createStatement();
		Log.info("Trying to execute: " + Query);
		rset = stmt.executeQuery(Query);
		}catch(SQLException e1)
		{
		Log.info("Query Execution Error: " + e1);
		}
	return rset;
	}

/**
* Close Oracle connection
* @throws IOException
*/
public void CloseConnection() {
	try{
		Log.info("Closing Database Connection");
		connection.close();
		Log.info("Connection closed successfully");
	}
	catch(SQLException e1){
		Log.info("Query Execution Error" );
		e1.printStackTrace();
	}
	}

}

