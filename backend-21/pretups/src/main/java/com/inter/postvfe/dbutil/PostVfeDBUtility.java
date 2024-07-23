/*
 * Created on Jul 24, 2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.postvfe.dbutil;

import java.sql.Connection;
import java.sql.SQLException;

import oracle.jdbc.pool.OracleConnectionCacheImpl;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.inter.pool.ClientMarkerI;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class PostVfeDBUtility implements ClientMarkerI {

    private static Log _log = LogFactory.getLog(PostVfeDBUtility.class.getName());
    private static OracleConnectionCacheImpl _connectionPool = null;
    private static OracleConnectionPoolDataSource _ods = null;
    private static OracleConnectionPoolDataSource _ods_single = null;
    private static OracleConnectionCacheImpl _connectionSinglePool;
    private static String DB_CONN_FAILED = "00000";
    private String _interfaceID;

    /**
     * Default constructor.
     */
    public PostVfeDBUtility() {
    }

    /**
     * Constructor used to initialize the dbconnection pool
     * 
     * @param p_interfaceId
     * @throws BTSLBaseException
     */
    public PostVfeDBUtility(String p_interfaceId) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("PostVfeDBUtility[constructor]", "Entered p_interfaceId:" + p_interfaceId);
        try {
            _interfaceID = p_interfaceId;
            String db_url = FileCache.getValue(_interfaceID, "POSTVFE_DB_CONN_URL");
            if (InterfaceUtil.isNullString(db_url)) {
                _log.error("PostVfeDBUtility[constructor]", "POSTVFE_DB_CONN_URL is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostVfeDBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "POSTVFE_DB_CONN_URL is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
            }
            db_url = db_url.trim();

            String db_user = FileCache.getValue(_interfaceID, "POSTVFE_DB_USERNAME");
            if (InterfaceUtil.isNullString(db_user)) {
                _log.error("PostVfeDBUtility[constructor]", "POSTVFE_DB_USERNAME is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostVfeDBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "POSTVFE_DB_USERNAME is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
            }
            db_user = db_user.trim();
            // db_user = new CryptoUtil().decrypt(db_user,Constants.KEY);
            // String db_password = Constants.getProperty("passwd");
            String db_password = FileCache.getValue(_interfaceID, "POSTVFE_DB_PASS");
            if (InterfaceUtil.isNullString(db_user)) {
                _log.error("PostVfeDBUtility[constructor]", "POSTVFE_DB_PASS is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostVfeDBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "POSTVFE_DB_PASS is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
            }
            db_password = db_password.trim();
            // db_password = new
            // CryptoUtil().decrypt(db_password,Constants.KEY);
            // String strMinPoolSize=Constants.getProperty("minpoolsize");
            String strMinPoolSize = FileCache.getValue(_interfaceID, "POSTVFE_DB_MINPOOLSIZE");
            if (InterfaceUtil.isNullString(db_user)) {
                _log.error("PostVfeDBUtility[constructor]", "POSTVFE_DB_MINPOOLSIZE is not defined in the INFile with INTERFACE_ID:" + p_interfaceId);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostVfeDBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "POSTVFE_DB_MINPOOLSIZE is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
            }
            strMinPoolSize = strMinPoolSize.trim();
            // String strPoolSize=Constants.getProperty("poolsize");
            String strPoolSize = FileCache.getValue(_interfaceID, "POSTVFE_DB_MAXPOOLSIZE");
            if (InterfaceUtil.isNullString(db_user)) {
                _log.error("PostVfeDBUtility[constructor]", "POSTVFE_DB_MAXPOOLSIZE is not defined in the INFile with INTERFACE_ID:" + p_interfaceId);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "PostVfeDBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "POSTVFE_DB_MAXPOOLSIZE is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
            }
            int i = db_url.indexOf(":");
            int j = db_url.indexOf("@") - 1;
            String driver_type = db_url.substring(i, j);
            if (driver_type.endsWith("thin"))
                driver_type = "thin";
            else
                driver_type = "oci";
            int p = db_url.indexOf("@");
            int q = db_url.indexOf(":", ++p);
            String ip = db_url.substring(p, q);
            String port = db_url.substring(++q, db_url.indexOf(":", q));
            String sid = db_url.substring(db_url.lastIndexOf(":") + 1, db_url.length());
            // String strPoolSize=Constants.getProperty("oraclePoolLogFile");
            // Confirm for this value, what would be its use.
            String oraclePoolLogFile = FileCache.getValue(_interfaceID, "DB_POOL_LOG_FILE");
            if (_log.isDebugEnabled())
                _log.debug("getConnection", ", db_url=" + db_url + ", userid=" + db_user + ",  password=" + db_password + ", MinPoolSize=" + strMinPoolSize + ", strPoolSize=" + strPoolSize);
            int minPoolSize = 0;
            try {
                minPoolSize = Integer.parseInt(strMinPoolSize);
            } catch (Exception e) {
                minPoolSize = 5;
            }
            int poolSize = 0;
            try {
                poolSize = Integer.parseInt(strPoolSize);
            } catch (Exception e) {
                poolSize = 10;
            }
            int iPort = 0;
            try {
                iPort = Integer.parseInt(port);
            }// end of try
            catch (Exception e) {
                iPort = 1521;
            }
            if (_log.isDebugEnabled())
                _log.debug("PostVfeDBUtility[constructor]", "userid=" + db_user + ",  db_url=" + db_url + ", minPoolSize=" + minPoolSize + " max poolSize=" + poolSize + " oraclePoolLogFile=" + oraclePoolLogFile);
            try {
                if (db_url != null) {
                    // creating the instatnce of oracle datasource
                    _ods = new OracleConnectionPoolDataSource();
                    // provide various attribute of database driver to make
                    // connection
                    _ods.setDriverType(driver_type);
                    _ods.setServerName(ip);
                    _ods.setNetworkProtocol("tcp");
                    _ods.setDatabaseName(sid);
                    _ods.setPortNumber(iPort);
                    _ods.setUser(db_user);
                    _ods.setPassword(db_password);
                    // Initialize the Connection Cache
                    _connectionPool = new OracleConnectionCacheImpl(_ods);
                    // Set Max Limit for the Cache
                    _connectionPool.setMaxLimit(poolSize);
                    // Set Min Limit for the Cache
                    _connectionPool.setMinLimit(minPoolSize);
                    _connectionPool.setCacheScheme(OracleConnectionCacheImpl.DYNAMIC_SCHEME);
                }
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("PostVfeDBUtility[constructor]", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "PostVfeDBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "While initialization of DB Connection Pool for interface id=" + _interfaceID + " get Exception e:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
                // Confirm for Handling event
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("PostVfeDBUtility[constructor]", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "PostVfeDBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "While initialization of DB Connection Pool for interface id=" + p_interfaceId + " get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("PostVfeDBUtility[constructor]", "Exited m_connectionPool:" + _connectionPool);
        }
    }

    /**
     * This method returns connection from the connection pool, if there is no
     * connection
     * in the pool then adds new connection in the connection pool.
     * 
     * @return Connection, database connection
     * @param none
     * 
     */
    public Connection getConnection() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getConnection", "Entered");
        Connection dbConnection = null;
        try {
            if (_connectionPool == null)
                throw new BTSLBaseException(DB_CONN_FAILED);
            try {
                dbConnection = _connectionPool.getConnection();
                dbConnection.setAutoCommit(false);
                try {
                    dbConnection.rollback();
                } catch (SQLException sqe1) {
                    _log.error("getConnection", "After getting connection Exception occured during the roll back,Error Code=" + sqe1.getErrorCode());
                }
            } catch (SQLException sqe) {
                sqe.printStackTrace();
                int errorCode = sqe.getErrorCode();
                _log.error("getConnection", "Exception Error Code=" + errorCode);
                if (errorCode == 17008) {
                    _log.error("getConnection", "Recreating pool");
                    // Creating pool
                    if (_connectionPool == null)
                        throw new BTSLBaseException(DB_CONN_FAILED);
                    // Getting connection from Connection pool
                    dbConnection = _connectionPool.getConnection();
                    // Setting Autocommit false
                    dbConnection.setAutoCommit(false);
                    try {
                        // Rolling back the transaction - if any
                        dbConnection.rollback();
                    } catch (SQLException sqe1) {
                        _log.error("getConnection Rollback", "Exception Error Code=" + sqe1.getErrorCode());
                    }
                } else
                    throw new BTSLBaseException(DB_CONN_FAILED);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BTSLBaseException(DB_CONN_FAILED);
            }
            if (dbConnection == null)
                throw new BTSLBaseException(DB_CONN_FAILED);
            // added error log to always print this line
            _log.error("getConnection", "After	getting connection from connection pool :" + dbConnection + " active size:" + _connectionPool.getActiveSize() + " Cache size:" + _connectionPool.getCacheSize() + " MAX Limit:" + _connectionPool.getMaxLimit() + " MIN Limit:" + _connectionPool.getMinLimit());
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception ex) {
            ex.printStackTrace();
            _log.error("getConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeDBUtility[getConnection]", "", "", "", "Database Connection Problem, getting Exception ex:" + ex.getMessage());
            throw new BTSLBaseException(DB_CONN_FAILED);// Confirm for the Error
                                                        // code.
        }// end of catch
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getConnection", "Exited dbConnection:" + dbConnection);
        }
        return dbConnection;
    } // getConnection

    /**
     * This method returns a single connection,following DB parameters would be
     * defined in the INFile.
     * 1.DB_CONN_URL
     * 2.DB_USERNAME
     * 3.DB_PASS
     * 
     * @return Connection
     * @throws BTSLBaseException
     * 
     */
    public Connection getSingleConnection() throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getSingleConnection", "Entered");
        Connection dbConnection = null;
        try {
            if (_connectionSinglePool == null) {
                String db_url = FileCache.getValue(_interfaceID, "POSTVFE_DB_CONN_URL");
                if (InterfaceUtil.isNullString(db_url)) {
                    _log.error("getSingleConnection", "POSTVFE_DB_CONN_URL is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeDBUtility[getSingleConnection]", "", "", " INTERFACE ID = " + _interfaceID, "POSTVFE_DB_CONN_URL is not defined in IN File");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
                }
                db_url = db_url.trim();
                String db_user = FileCache.getValue(_interfaceID, "POSTVFE_DB_USERNAME");
                if (InterfaceUtil.isNullString(db_user)) {
                    _log.error("getSingleConnection", "POSTVFE_DB_USERNAME is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeDBUtility[getSingleConnection]", "", "", " INTERFACE ID = " + _interfaceID, "POSTVFE_DB_USERNAME is not defined in IN File");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
                }
                db_user = db_user.trim();
                // Confrim whether to user encrypted password or plain, if yest
                // then uncomment and get the key from INFile.
                db_user = new CryptoUtil().decrypt(db_user, Constants.KEY);
                // String db_password = Constants.getProperty("passwd");
                String db_password = FileCache.getValue(_interfaceID, "POSTVFE_DB_PASS");
                if (InterfaceUtil.isNullString(db_user)) {
                    _log.error("getSingleConnection", "POSTVFE_DB_PASS is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeDBUtility[getSingleConnection]", "", "", " INTERFACE ID = " + _interfaceID, "POSTVFE_DB_PASS is not defined in IN File");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
                }
                db_password = db_password.trim();
                // Confrim whether to user encrypted password or plain, if yest
                // then uncomment and get the key from INFile.
                // db_password = new
                // CryptoUtil().decrypt(db_password,Constants.KEY);
                if (_log.isDebugEnabled())
                    _log.debug("getSingleConnection", "db_url=" + db_url + ", db_user=" + db_user + ",  db_password=" + db_password);
                int i = db_url.indexOf(":");
                int j = db_url.indexOf("@") - 1;
                String driver_type = db_url.substring(i, j);
                if (driver_type.endsWith("thin"))
                    driver_type = "thin";
                else
                    driver_type = "oci";
                int p = db_url.indexOf("@");
                int q = db_url.indexOf(":", ++p);
                String ip = db_url.substring(p, q);
                String port = db_url.substring(++q, db_url.indexOf(":", q));
                String sid = db_url.substring(db_url.lastIndexOf(":") + 1, db_url.length());
                int iPort = 0;
                try {
                    iPort = Integer.parseInt(port);
                }// end of try
                catch (Exception e) {
                    iPort = 1521;
                }
                if (db_url != null) {
                    _ods_single = new OracleConnectionPoolDataSource();
                    // provide various attribute of database driver to make
                    // connection
                    _ods_single.setDriverType(driver_type);
                    _ods_single.setServerName(ip);
                    _ods_single.setNetworkProtocol("tcp");
                    _ods_single.setDatabaseName(sid);
                    _ods_single.setPortNumber(iPort);
                    _ods_single.setUser(db_user);
                    _ods_single.setPassword(db_password);
                    // Initialize the Connection Cache
                    _connectionSinglePool = new OracleConnectionCacheImpl(_ods_single);
                    // Set Max Limit for the Cache
                    _connectionSinglePool.setMaxLimit(1);
                    // Set Min Limit for the Cache
                    _connectionSinglePool.setMinLimit(1);
                    _connectionSinglePool.setCacheScheme(OracleConnectionCacheImpl.DYNAMIC_SCHEME);
                } else
                    return null;
            }// end of the m_connectionSinglePool
            dbConnection = _connectionSinglePool.getConnection();
            dbConnection.setAutoCommit(false);
            if (_log.isDebugEnabled())
                _log.error("getSingleConnection 10G", "DB Connections  getting connection from connection pool :" + dbConnection + " active size:" + _connectionSinglePool.getActiveSize() + " cache size:" + _connectionSinglePool.getCacheSize());
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception ex) {
            ex.printStackTrace();
            _log.error("getSingleConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "PostVfeDBUtility[getSingleConnection]", "", "", " INTERFACE ID = " + _interfaceID, "Exception occurs while getting the single database connection, Exception ex:" + ex.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
        }// end of catch
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getSingleConnection", "Exiting dbConnection :" + dbConnection);
        }
        return dbConnection;
    } // End of getSingleConnection

    /**
     * Method used to get the Connection Pool from Cache.
     * 
     * @return OracleConnectionCacheImpl
     */
    public static OracleConnectionCacheImpl getConnectionPool() {
        return _connectionPool;
    }

    /**
     * This method would be used to destroy the DBConnectionPool, if required.
     * 
     */
    public void destroy() {
        try {
            if (_connectionSinglePool != null)
                _connectionSinglePool.close();
        } catch (Exception e) {
        }
        try {
            if (_connectionPool != null)
                _connectionPool.close();
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
    }
}
