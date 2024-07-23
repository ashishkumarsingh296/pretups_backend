package com.inter.postonline;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
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
import com.inter.pool.ClientMarkerI;

/**
 * @DBUtility.java
 *                 Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                 All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Ashish K Apr 3, 2007 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 *                 This class will provide implementation details of for various
 *                 API that are to be handled by Stored Procedures
 * 
 */
public class DBUtility implements ClientMarkerI {
    private static Log _log = LogFactory.getLog(DBUtility.class.getName());
    private static OracleConnectionCacheImpl _connectionPool = null;
    private static OracleConnectionPoolDataSource _ods = null;
    private static OracleConnectionPoolDataSource _ods_single = null;
    private static OracleConnectionCacheImpl _connectionSinglePool;
    private static String DB_CONN_FAILED = "00000";
    private String _interfaceID;

    /**
     * Default constructor.
     */
    public DBUtility() {
    }

    /**
     * Constructor used to initialize the dbconnection pool
     * 
     * @param p_interfaceId
     * @throws BTSLBaseException
     */
    public DBUtility(String p_interfaceId) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("DBUtility[constructor]", "Entered p_interfaceId:" + p_interfaceId);
        try {
            _interfaceID = p_interfaceId;
            String db_url = FileCache.getValue(_interfaceID, "DB_CONN_URL");
            if (InterfaceUtil.isNullString(db_url)) {
                _log.error("DBUtility[constructor]", "DB_CONN_URL is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "DBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "DB_CONN_URL is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
            }
            db_url = db_url.trim();

            String db_user = FileCache.getValue(_interfaceID, "DB_USERNAME");
            if (InterfaceUtil.isNullString(db_user)) {
                _log.error("DBUtility[constructor]", "DB_USERNAME is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "DBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "DB_USERNAME is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
            }
            db_user = db_user.trim();
            String db_password = FileCache.getValue(_interfaceID, "DB_PASS");
            if (InterfaceUtil.isNullString(db_user)) {
                _log.error("DBUtility[constructor]", "DB_PASS is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "DBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "DB_PASS is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
            }
            db_password = db_password.trim();
            String strMinPoolSize = FileCache.getValue(_interfaceID, "DB_MINPOOLSIZE");
            if (InterfaceUtil.isNullString(db_user)) {
                _log.error("DBUtility[constructor]", "DB_MINPOOLSIZE is not defined in the INFile with INTERFACE_ID:" + p_interfaceId);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "DBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "DB_MINPOOLSIZE is not defined in IN File");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
            }
            strMinPoolSize = strMinPoolSize.trim();
            // String strPoolSize=Constants.getProperty("poolsize");
            String strPoolSize = FileCache.getValue(_interfaceID, "DB_MAXPOOLSIZE");
            if (InterfaceUtil.isNullString(db_user)) {
                _log.error("DBUtility[constructor]", "DB_MAXPOOLSIZE is not defined in the INFile with INTERFACE_ID:" + p_interfaceId);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MAJOR, "DBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "DB_MAXPOOLSIZE is not defined in IN File");
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
                _log.debug("DBUtility[constructor]", "userid=" + db_user + ",  db_url=" + db_url + ", minPoolSize=" + minPoolSize + " max poolSize=" + poolSize + " oraclePoolLogFile=" + oraclePoolLogFile);
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
                _log.error("DBUtility[constructor]", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "DBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "While initialization of DB Connection Pool for interface id=" + _interfaceID + " get Exception e:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
                // Confirm for Handling event
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("DBUtility[constructor]", "Exception e:" + e.getMessage());
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.MINOR, "DBUtility[constructor]", "", "", " INTERFACE ID = " + _interfaceID, "While initialization of DB Connection Pool for interface id=" + p_interfaceId + " get Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("DBUtility[constructor]", "Exited m_connectionPool:" + _connectionPool);
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
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DBUtility[getConnection]", "", "", "", "Database Connection Problem, getting Exception ex:" + ex.getMessage());
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
                String db_url = FileCache.getValue(_interfaceID, "DB_CONN_URL");
                if (InterfaceUtil.isNullString(db_url)) {
                    _log.error("getSingleConnection", "DB_CONN_URL is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "DBUtility[getSingleConnection]", "", "", " INTERFACE ID = " + _interfaceID, "DB_CONN_URL is not defined in IN File");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
                }
                db_url = db_url.trim();
                String db_user = FileCache.getValue(_interfaceID, "DB_USERNAME");
                if (InterfaceUtil.isNullString(db_user)) {
                    _log.error("getSingleConnection", "DB_USERNAME is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "DBUtility[getSingleConnection]", "", "", " INTERFACE ID = " + _interfaceID, "DB_USERNAME is not defined in IN File");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
                }
                db_user = db_user.trim();
                String db_password = FileCache.getValue(_interfaceID, "DB_PASS");
                if (InterfaceUtil.isNullString(db_password)) {
                    _log.error("getSingleConnection", "DB_PASS is not defined in the INFile with INTERFACE_ID:" + _interfaceID);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "DBUtility[getSingleConnection]", "", "", " INTERFACE ID = " + _interfaceID, "DB_PASS is not defined in IN File");
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_DB_CON_INITILIZATION);
                }
                db_password = db_password.trim();
                // Confrim whether to user encrypted password or plain, if yest
                // then uncomment and get the key from INFile.
                // db_password = new
                // CryptoUtil().decrypt(db_password,Constants.KEY);
                _log.error("getSingleConnection", "db_url=" + db_url + ", db_user=" + db_user + ",  db_password=" + db_password);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "DBUtility[getSingleConnection]", "", "", " INTERFACE ID = " + _interfaceID, "Exception occurs while getting the single database connection, Exception ex:" + ex.getMessage());
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

    /**
     * This method would be responsible to instantiate the Inner class that
     * provides the functionality
     * of getting subscriber information, credit and debit the account in DB.
     * 
     * @return
     * @throws BTSLBaseException
     */
    public InnerClass getInnerClass() throws BTSLBaseException {
        InnerClass innerClass = null;
        try {
            innerClass = this.new InnerClass();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getInnerClass", "Exception e:" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_INITILIZATION_INNERCLASS);
        }
        return innerClass;
    }

    /**
     * This Inner class would implements the functionality for the subscriber
     * validation,
     * credit and Adjustment.
     * Note: Name of InnerClass and InnerClassI interface may be changed if
     * required.
     */
    class InnerClass implements InnerClassI {
        /**
         * This method is used to validate the subscriber number on interface.
         * 
         * @param Connection
         *            p_con
         * @param HashMap
         *            p_requestMap
         * @throws BTSLBaseException
         *             ,Exception
         */
        public void validate(Connection p_con, HashMap p_requestMap) throws BTSLBaseException {
            if (_log.isDebugEnabled())
                _log.debug("validate", "Entered p_requestMap:" + p_requestMap);
            CallableStatement callStmt = null;
            StringBuffer responseBuffer = null;
            String responseStr = null;
            try {
                String procStr = "call icmsadm.getAccountInformation(?,?,?,?,?,?,?,?,?,?,?)";
                callStmt = p_con.prepareCall(procStr);
                callStmt.setString(1, InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")));
                callStmt.setString(2, (String) p_requestMap.get("IN_RECON_ID"));
                callStmt.registerOutParameter(3, Types.VARCHAR);
                callStmt.registerOutParameter(4, Types.VARCHAR);
                callStmt.registerOutParameter(5, Types.VARCHAR);
                callStmt.registerOutParameter(6, Types.VARCHAR);
                callStmt.registerOutParameter(7, Types.VARCHAR);
                callStmt.registerOutParameter(8, Types.VARCHAR);
                callStmt.registerOutParameter(9, Types.VARCHAR);
                callStmt.registerOutParameter(10, Types.VARCHAR);
                callStmt.registerOutParameter(11, Types.VARCHAR);
                callStmt.execute();
                responseBuffer = new StringBuffer(1028);
                responseBuffer.append("Status=");
                responseBuffer.append(callStmt.getString(3));
                responseBuffer.append("&TransactionId=");
                responseBuffer.append(callStmt.getString(4));
                responseBuffer.append("&ServiceClass=");
                // p_requestMap.put("SERVICE_CLASS",callStmt.getString(4));
                responseBuffer.append(callStmt.getString(5));
                responseBuffer.append("&AccountId=");
                responseBuffer.append(callStmt.getString(6));
                responseBuffer.append("&AccountStatus=");
                // p_requestMap.put("ACCOUNT_STATUS",callStmt.getString(4));
                responseBuffer.append(callStmt.getString(7));
                responseBuffer.append("&CreditLimit=");
                responseBuffer.append(callStmt.getString(8));
                // p_requestMap.put("CREDIT_LIMIT",callStmt.getString(7));
                responseBuffer.append("&LanguageId=");
                responseBuffer.append(callStmt.getString(9));
                // p_requestMap.put("LANGUAGETYPE",callStmt.getString(8));
                responseBuffer.append("&Imsi=");
                responseBuffer.append(callStmt.getString(10));
                responseBuffer.append("&Balance=");
                responseBuffer.append(callStmt.getString(11));
                // p_requestMap.put("INTERFACE_PREV_BALANCE",callStmt.getString(10));
                responseStr = responseBuffer.toString();
                p_requestMap.put("RESPONSE_STR", responseStr);
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
                _log.error("validate", "SQLException sqlEx:" + sqlEx.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InnerClass[validate]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + PostPaidI.ACTION_ACCOUNT_INFO, "While validating the subscriber get SQLException sqlEx:" + sqlEx.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("validate", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InnerClass[validate]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + PostPaidI.ACTION_ACCOUNT_INFO, "While validating the subscriber get Exception e:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            } finally {
                try {
                    if (callStmt != null)
                        callStmt.clearParameters();
                } catch (Exception e) {
                }
                try {
                    if (callStmt != null)
                        callStmt.close();
                } catch (Exception e) {
                }
                if (_log.isDebugEnabled())
                    _log.debug("validate", "Exited responseStr" + responseStr);
            }
        }

        /**
         * This method would be used for Credit the user on the interface.
         * 
         * @param Connection
         *            p_con
         * @param HashMap
         *            p_requestMap
         * @throws BTSLBaseException
         *             ,Exception
         */
        public void credit(Connection p_con, HashMap p_requestMap) throws BTSLBaseException {
            if (_log.isDebugEnabled())
                _log.debug("credit", "Entered p_requestMap:" + p_requestMap);
            CallableStatement callStmt = null;
            StringBuffer responseBuffer = null;
            String responseStr = null;
            try {
                String procStr = "call adjustment(?,?,?,?,?,?,?,?,?)";
                callStmt = p_con.prepareCall(procStr);
                callStmt.setString(1, InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")));
                callStmt.setString(2, (String) p_requestMap.get("IN_RECON_ID"));
                callStmt.registerOutParameter(2, Types.VARCHAR);
                callStmt.setString(3, (String) p_requestMap.get("ACCOUNT_ID"));
                callStmt.setString(4, (String) p_requestMap.get("transfer_amount"));
                callStmt.setString(5, (String) p_requestMap.get("CURRENCY"));
                callStmt.setString(6, (String) p_requestMap.get("IMSI"));// Confirm
                                                                         // the
                                                                         // that
                                                                         // would
                                                                         // be
                                                                         // present
                                                                         // in
                                                                         // the
                                                                         // requestMap
                callStmt.setString(7, (String) p_requestMap.get("SERVICE_TYPE"));// Confirm
                                                                                 // the
                                                                                 // that
                                                                                 // would
                                                                                 // be
                                                                                 // present
                                                                                 // in
                                                                                 // the
                                                                                 // requestMap
                callStmt.registerOutParameter(8, Types.VARCHAR);
                callStmt.registerOutParameter(9, Types.VARCHAR);
                callStmt.execute();
                responseBuffer = new StringBuffer(1028);
                responseBuffer.append("TransactionId=");
                responseBuffer.append(callStmt.getString(2));
                responseBuffer.append("&Status=");
                responseBuffer.append(callStmt.getString(8));
                responseBuffer.append("&Balance=");
                responseBuffer.append(callStmt.getString(9));
                responseStr = responseBuffer.toString();
                p_requestMap.put("RESPONSE_STR", responseStr);
            } catch (SQLException sqlEx) {
                // Here AMBIGUOUS CASES would be checked.
                sqlEx.printStackTrace();
                _log.error("credit", "Exception sqlEx:" + sqlEx.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InnerClass[credit]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + PostPaidI.ACTION_RECHARGE_CREDIT, "While credit the subscriber's account, get SQLException sqlEx:" + sqlEx.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("credit", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InnerClass[credit]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + PostPaidI.ACTION_RECHARGE_CREDIT, "While credit the subscriber's account, get Exception e:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            } finally {
                try {
                    if (callStmt != null)
                        callStmt.clearParameters();
                } catch (Exception e) {
                }
                try {
                    if (callStmt != null)
                        callStmt.close();
                } catch (Exception e) {
                }
                if (_log.isDebugEnabled())
                    _log.debug("credit", "Exited responseStr:" + responseStr);
            }
        }

        /**
         * This method would be used for Credit the user on the interface.
         * 
         * @param Connection
         *            p_con
         * @param HashMap
         *            p_requestMap
         * @throws BTSLBaseException
         *             ,Exception
         */
        public void creditAdjust(Connection p_con, HashMap p_requestMap) throws BTSLBaseException {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust", "Entered p_requestMap:" + p_requestMap);
            CallableStatement callStmt = null;
            StringBuffer responseBuffer = null;
            String responseStr = null;
            try {
                String procStr = "call adjustment(?,?,?,?,?,?,?,?,?)";
                callStmt = p_con.prepareCall(procStr);
                callStmt.setString(1, InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")));
                callStmt.setString(2, (String) p_requestMap.get("IN_RECON_ID"));
                callStmt.registerOutParameter(2, Types.VARCHAR);
                callStmt.setString(3, (String) p_requestMap.get("ACCOUNT_ID"));
                callStmt.setString(4, (String) p_requestMap.get("transfer_amount"));
                callStmt.setString(5, (String) p_requestMap.get("CURRENCY"));
                callStmt.setString(6, (String) p_requestMap.get("IMSI"));// Confirm
                                                                         // the
                                                                         // that
                                                                         // would
                                                                         // be
                                                                         // present
                                                                         // in
                                                                         // the
                                                                         // requestMap
                callStmt.setString(7, (String) p_requestMap.get("SERVICE_TYPE"));// Confirm
                                                                                 // the
                                                                                 // that
                                                                                 // would
                                                                                 // be
                                                                                 // present
                                                                                 // in
                                                                                 // the
                                                                                 // requestMap
                callStmt.registerOutParameter(8, Types.VARCHAR);
                callStmt.registerOutParameter(9, Types.VARCHAR);
                callStmt.execute();
                responseBuffer = new StringBuffer(1028);
                responseBuffer.append("TransactionId=");
                responseBuffer.append(callStmt.getString(2));
                responseBuffer.append("&Status=");
                responseBuffer.append(callStmt.getString(8));
                responseBuffer.append("&Balance=");
                responseBuffer.append(callStmt.getString(9));
                responseStr = responseBuffer.toString();
                p_requestMap.put("RESPONSE_STR", responseStr);
            } catch (SQLException sqlEx) {
                // Here we have to find the AMBIGUOUS CASES.
                sqlEx.printStackTrace();
                _log.error("creditAdjust", "Exception sqlEx:" + sqlEx.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InnerClass[creditAdjust]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + PostPaidI.ACTION_RECHARGE_CREDIT, "While creditAdjust the subscriber's account, get SQLException sqlEx:" + sqlEx.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("creditAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InnerClass[creditAdjust]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + PostPaidI.ACTION_RECHARGE_CREDIT, "While creditAdjust the subscriber's account, get Exception e:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            } finally {
                if (_log.isDebugEnabled())
                    _log.debug("creditAdjust", "Exited responseStr:" + responseStr);
            }
        }

        /**
         * This method would be used for Debit the user on the interface
         * 
         * @param Connection
         *            p_con
         * @param HashMap
         *            p_requestMap
         * @throws BTSLBaseException
         *             ,Exception
         */
        public void debitAdjust(Connection p_con, HashMap p_requestMap) throws BTSLBaseException {
            if (_log.isDebugEnabled())
                _log.debug("debitAdjust", "Entered p_requestMap:" + p_requestMap);
            CallableStatement callStmt = null;
            StringBuffer responseBuffer = null;
            String responseStr = null;
            try {
                String procStr = "call adjustment(?,?,?,?,?,?,?,?,?)";
                callStmt = p_con.prepareCall(procStr);
                callStmt.setString(1, InterfaceUtil.getFilterMSISDN((String) p_requestMap.get("INTERFACE_ID"), (String) p_requestMap.get("MSISDN")));
                callStmt.setString(2, (String) p_requestMap.get("IN_RECON_ID"));
                callStmt.registerOutParameter(2, Types.VARCHAR);
                callStmt.setString(3, (String) p_requestMap.get("ACCOUNT_ID"));
                callStmt.setString(4, (String) p_requestMap.get("transfer_amount"));
                callStmt.setString(5, (String) p_requestMap.get("CURRENCY"));
                callStmt.setString(6, (String) p_requestMap.get("IMSI"));// Confirm
                                                                         // the
                                                                         // that
                                                                         // would
                                                                         // be
                                                                         // present
                                                                         // in
                                                                         // the
                                                                         // requestMap
                callStmt.setString(7, (String) p_requestMap.get("SERVICE_TYPE"));// Confirm
                                                                                 // the
                                                                                 // that
                                                                                 // would
                                                                                 // be
                                                                                 // present
                                                                                 // in
                                                                                 // the
                                                                                 // requestMap
                callStmt.registerOutParameter(8, Types.VARCHAR);
                callStmt.registerOutParameter(9, Types.VARCHAR);
                callStmt.execute();
                responseBuffer = new StringBuffer(1028);
                responseBuffer.append("TransactionId=");
                responseBuffer.append(callStmt.getString(2));
                responseBuffer.append("&Status=");
                responseBuffer.append(callStmt.getString(8));
                responseBuffer.append("&Balance=");
                responseBuffer.append(callStmt.getString(9));
                responseStr = responseBuffer.toString();
                p_requestMap.put("RESPONSE_STR", responseStr);
            } catch (SQLException sqlEx) {
                // Here we have to find the AMBIGUOUS CASES.
                sqlEx.printStackTrace();
                _log.error("debitAdjust", "Exception sqlEx:" + sqlEx.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InnerClass[debitAdjust]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + PostPaidI.ACTION_IMMEDIATE_DEBIT, "While debitAdjust the subscriber's account, get SQLException sqlEx:" + sqlEx.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            } catch (Exception e) {
                e.printStackTrace();
                _log.error("debitAdjust", "Exception e:" + e.getMessage());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.FATAL, "InnerClass[debitAdjust]", "REFERENCE ID = " + (String) p_requestMap.get("IN_TXN_ID") + "MSISDN = " + (String) p_requestMap.get("MSISDN"), "INTERFACE ID = " + (String) p_requestMap.get("INTERFACE_ID"), "Network code = " + (String) p_requestMap.get("NETWORK_CODE") + " Action = " + PostPaidI.ACTION_IMMEDIATE_DEBIT, "While debitAdjsut the the subscriber's account, get Exception e:" + e.getMessage());
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            } finally {
                if (_log.isDebugEnabled())
                    _log.debug("debitAdjust", "Exited responseStr:" + responseStr);
            }
        }
    }
}
