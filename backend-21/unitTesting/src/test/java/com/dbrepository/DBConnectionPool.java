package com.dbrepository;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.classes.BaseTest;
import com.commons.MasterI;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.utils.Log;
import com.utils._masterVO;

public class DBConnectionPool extends BaseTest{

    private static DBConnectionPool datasource;
    private static ComboPooledDataSource cpds;
    private static String URL, USERNAME, PASSWORD, DB_IP, PORT, SID, DBType, DRIVER, DBListener;
    private static int InitialPoolSize, MinPoolSize, MaxPoolSize, AcquireIncrement;

    private DBConnectionPool() throws IOException, SQLException, PropertyVetoException {
    	
    	DBType = _masterVO.getMasterValue(MasterI.DB_INTERFACE_TYPE);
		DB_IP = _masterVO.getMasterValue(MasterI.DB_SCHEMA_IP);
		PORT = _masterVO.getMasterValue(MasterI.DB_PORT);
		SID = _masterVO.getMasterValue(MasterI.DB_SID);
		USERNAME = _masterVO.getMasterValue(MasterI.DB_USERNAME);
		PASSWORD = _masterVO.getMasterValue(MasterI.DB_PASSWORD);
		DBListener = _masterVO.getMasterValue(MasterI.DB_LISTENER);
		
		if (DBType.equals("Oracle")) {
			if(DBListener.equals("SID")){
				URL = "jdbc:oracle:thin:@" + DB_IP + ":" + PORT + ":" + SID;}
			else if(DBListener.equals("SERVICE NAME")){
				URL = "jdbc:oracle:thin:@" + DB_IP + ":" + PORT + "/" + SID;	
			}
			DRIVER = "oracle.jdbc.driver.OracleDriver";
		}
		else if (DBType.equals("PostGreSQL")) {
			URL = "jdbc:postgresql://" + DB_IP + ":" + PORT + "/" + SID;
			DRIVER = "org.postgresql.Driver";
		}
		
		//Connection Pool Parameters
		InitialPoolSize = Integer.parseInt(_masterVO.getProperty("InitialPoolSize"));
		MinPoolSize = Integer.parseInt(_masterVO.getProperty("MinPoolSize"));
		AcquireIncrement = Integer.parseInt(_masterVO.getProperty("AcquireIncrement"));
		MaxPoolSize = Integer.parseInt(_masterVO.getProperty("MaxPoolSize"));
		
        cpds = new ComboPooledDataSource();
        cpds.setDriverClass(DRIVER); //loads the driver
        cpds.setJdbcUrl(URL);
        cpds.setUser(USERNAME);
        cpds.setPassword(PASSWORD);
        
        // the settings below are optional -- c3p0 can work with defaults
        cpds.setInitialPoolSize(InitialPoolSize);
        cpds.setMinPoolSize(MinPoolSize);
        cpds.setAcquireIncrement(AcquireIncrement);
        cpds.setMaxPoolSize(MaxPoolSize);
       // cpds.setMaxStatements(180);
    }

    public static DBConnectionPool getInstance() throws IOException, SQLException, PropertyVetoException {
        if (datasource == null) {
            datasource = new DBConnectionPool();
            return datasource;
        } else {
        	Log.debug("Active Size:" + cpds.getNumBusyConnections() + " Cache Size:" + cpds.getNumIdleConnections());
            return datasource;
        }
    }

    public Connection getConnection() throws SQLException {
        return DBConnectionPool.cpds.getConnection();
    }

}