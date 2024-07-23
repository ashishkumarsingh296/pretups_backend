package com.btsl.pretups.channel.transfer.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

//import org.apache.log4j.PropertyConfigurator;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;

/**
 * utility class for MySQL connection and other specifics.
 * please use mysql-connector-java-2.0.14-bin.jar in classpath when compiling
 */
public class MySqlConnectionUtil {
    private static Log _log = LogFactory.getLog(MySqlConnectionUtil.class.getName());
    private static ConnectionPool m_connectionPool;

    /**
   	 * ensures no instantiation
   	 */
    private MySqlConnectionUtil(){
    	
    }
    /*
     * static
     * {
     * try
     * {
     * m_connectionPool = new OracleConnectionCacheImpl();
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * }
     */
    public static void freeConnection(Connection p_con) throws Exception {
        m_connectionPool.checkin(p_con);
    }

    /**
     * This method returns connection from the connection pool, if there is no
     * connection
     * in the pool then adds new connection in the connection pool.
     * Creation date: (08/12/02)
     * 
     * @return Connection, database connection
     * @param none
     */
    public static Connection getConnection() {
        Connection dbConnection = null;
        final String METHOD_NAME = "getConnection";
        try {
            if (m_connectionPool == null) {

                final String db_url = Constants.getProperty("mysqlDataSourceURL");
                String db_user = Constants.getProperty("mysqlUserID");
                if (db_user != null) {
                    db_user = new CryptoUtil().decrypt(db_user, Constants.KEY);
                }
                String db_password = Constants.getProperty("mysqlPassword");
                if (db_password != null) {
                    db_password = new CryptoUtil().decrypt(db_password, Constants.KEY);
                }
                final String strMinPoolSize = Constants.getProperty("mysqlMinPoolSize");
                int minPoolSize = 0;
                try {
                    minPoolSize = Integer.parseInt(strMinPoolSize);
                } catch (Exception e) {
                    minPoolSize = 5;
                    _log.errorTrace(METHOD_NAME, e);
                }
                final String strMaxPoolSize = Constants.getProperty("mysqlMaxPoolSize");
                int maxPoolSize = 0;
                try {
                    maxPoolSize = Integer.parseInt(strMaxPoolSize);
                } catch (Exception e) {
                    maxPoolSize = 10;
                    _log.errorTrace(METHOD_NAME, e);
                }
                final String driver = Constants.getProperty("mysqlDriver");

                if (!BTSLUtil.isNullString(db_url)) {
                    Class.forName(driver).newInstance();
                    m_connectionPool = new ConnectionPool(db_url, db_user, db_password);
                    m_connectionPool.setUrlString(db_url);
                    m_connectionPool.setUserName(db_user);
                    m_connectionPool.setPassword(db_password);
                    m_connectionPool.setInitialConnectionCount(minPoolSize);
                    m_connectionPool.setMaximumConnectionCount(maxPoolSize);
                } else {
                    return null;
                }

            }// end of the m_connectionPool

            dbConnection = m_connectionPool.checkout();
            try {
                dbConnection.setAutoCommit(false);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                dbConnection = m_connectionPool.replaceWithFreshConnection(dbConnection);
                dbConnection.setAutoCommit(false);
            }

        }// end of try
        catch (Exception ex) {
            // Trap errors

            _log.errorTrace(METHOD_NAME, ex);
        }// end of catch
        return dbConnection;
    } // getConnection

    public static Connection getMySqlConnection() {
        final String METHOD_NAME = "getMySqlConnection";
        Connection con = null;
        try {
            final String url = Constants.getProperty("mysqlDataSourceURL");
            String db_user = Constants.getProperty("mysqlUserID");
            if (db_user != null) {
                db_user = new CryptoUtil().decrypt(db_user, Constants.KEY);
            }
            String db_password = Constants.getProperty("mysqlPassword");
            if (db_password != null) {
                db_password = new CryptoUtil().decrypt(db_password, Constants.KEY);
            }

            final String driver = Constants.getProperty("mysqlDriver");

            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, db_user, db_password);
            con.setAutoCommit(false);
        } catch (Exception e) {
            System.err.println("MySqlConnectionUtil::getMySqlConnection(): :Error:: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }

        return con;
        // Url=jdbc:mysql://localhost:3306/test
        // User=test
        // Pass=
        // Driver=com.mysql.jdbc.Driver

    }// end of getMySqlConnection

    public static void main(String[] args) {
        Statement statement = null;
        ResultSet rs = null;
        final String methodName = "main";
        try {

            Constants.load("D:\\tata\\configfiles\\Constants.props");
           // PropertyConfigurator.configure("D:\\tata\\configfiles\\LogConfig.props");
            // String connectionURL =
            // "jdbc:mysql://172.16.1.51:3306/smsreeceipt";
            Connection connection = null;
            // Class.forName("com.mysql.jdbc.Driver").newInstance();
            // connection = DriverManager.getConnection(connectionURL, "dlrsms",
            // "dlrsms");
            connection = MySqlConnectionUtil.getConnection();
            statement = connection.createStatement();

            rs = statement.executeQuery("SELECT * FROM dlr");
            int i = 0;
            while (rs.next()) {

                i++;
                if (i >= 10) {
                    break;
                }
            }
            MySqlConnectionUtil.freeConnection(connection);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

        }
    }
}