package com.btsl.db.pool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.mchange.v2.c3p0.C3P0ProxyConnection;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;


public class C3p0PoolManager extends BTSLDBManager implements Runnable {
    private boolean creatingPool = false;
    private boolean creatingPoolExternalDB = false;
    private static final  ReentrantLock lOCK = new ReentrantLock();

    private static ComboPooledDataSource dataSource = null;
    private static DataSource dataSourceSingle = null;
    
    private Log log = LogFactory.getLog(this.getClass().getName());
    private static ComboPooledDataSource odsExternalDB = null;
    private boolean creatingPoolCurrentReportDB = false;
    private static String dbConnFailedCurrentReportDB = "00000";
    private static ComboPooledDataSource odsCurrentReportDB = null;
    private static DataSource odSingleCurrentReportDB = null;
    private static final String CACHENAME_CURRENT_REPORTDB = "PreTUPSCache_" + Constants.getProperty("CURRENT_REPORT_CACHE_ID");
    private static final String CACHENAME_SINGLE_CURRENT_REPORTDB = "PreTUPSCache_" + Constants.getProperty("CURRENT_REPORT_CACHE_ID") + "_SINGLE";
    
    private boolean creatingPoolReportDB = false;
    private static String dbConnFailedReortDB = "00000";
    private static ComboPooledDataSource odsReportDB = null;
    private static DataSource odsSingleReportDB = null;

   private  static final String CACHENAME_REPORTDB = "PreTUPSCache_" + Constants.getProperty("REPORT_CACHE_ID");
   private static final String CACHENAME_SINGLE_REPORTDB = "PreTUPSCache_" + Constants.getProperty("REPORT_CACHE_ID") + "_SINGLE";
    
    private String createConnMsg =  "Creating postgres connection pool";
    private String exceptionErrorCodeMsg = "Exception Error Code ";
    private String errorInConnectingDBMsg = "Error in Connecting to the Database \n";
    private String userid = "userid ";
    private String databaseConnProErrMsg =  "Database Connection Problem";
    private String  reCreateingPoolMsg = "Recreating pool";
    private String  createPoolCurrentReportDBMsg ="C3p0PoolManager[createPoolCurrentReportDB]";

    public static DataSource getDatasource() {
        return dataSource;
    }
    private static final ConcurrentMap<String, Integer> activeSizeKey = new ConcurrentHashMap();
    public int getActiveConnection() {
    	return activeSizeKey.get("ACTIVESIZE")!=null?activeSizeKey.get("ACTIVESIZE"):1;
    }
    public int getAvailableConnection() {
        return activeSizeKey.get("CACHESIZE")!=null?activeSizeKey.get("CACHESIZE"):100;
    }
    /**
     * @Date Feb 14, 2012
     *       This method is to create data source for the configured JNDI in
     *       constants props
     * @Return boolean
     * @return
     **/

    /**
     * This method returns connection from the connection pool, if there is no
     * connection
     * in the pool then adds new connection in the connection pool.
     * 
     * @return Connection, database connection
     * @param none
     * 
     */
    @SuppressWarnings({ "unused", "resource" })
	@Override
    public Connection getConnection() throws BTSLBaseException {
        C3P0ProxyConnection dbConnection = null;
        final String methodName = "getConnection";
        LogFactory.printLog(methodName, createConnMsg, log);
        try {
            if (dataSource == null && !createPool()) {
                    throw new BaseException(DB_CONN_FAILED);
            }
            try {
            	log.error(methodName, "Before getting connection");
                // Getting connection from Connection pool
                dbConnection = (C3P0ProxyConnection) dataSource.getConnection();
                // Setting Autocommit false
                dbConnection.setAutoCommit(false);


                Thread th = new Thread(this);
                setDbConnection1(dbConnection);
                
                StackTraceElement[] ste = Thread.currentThread().getStackTrace();	
                
                String className ="" ;
                
                for(StackTraceElement steObj: ste) {
                	try { className = className+",    "+ steObj.getClassName() +":"+steObj.getMethodName() +":"+steObj.getLineNumber(); }catch(Exception e) {log.errorTrace(methodName, e);}
                }
                
                
                
                setClassName(className);
                //th.start();
                
            } catch (SQLException sqe) {
                log.errorTrace(methodName, sqe);
                int errorCode = sqe.getErrorCode();
                log.error(methodName, exceptionErrorCodeMsg + errorCode);
          
                if (errorCode == 17008) {
                    dbConnection = (C3P0ProxyConnection) dataSource.getConnection();
                    dbConnection.setAutoCommit(false);
                    try {
                        dbConnection.rollback();
                    } catch (SQLException sqe1) {
                        log.errorTrace(methodName, sqe1);
                        log.error("getConnection Rollback", exceptionErrorCodeMsg + sqe1.getErrorCode());
                    }
                } else if (errorCode == 17002 || errorCode == 17410 || errorCode == 17416) {
                    log.error(methodName, reCreateingPoolMsg);
                    if (lOCK.tryLock()) {
                        try {
                            if (!createPool())
                                throw new BTSLBaseException(DB_CONN_FAILED);
                            dbConnection = (C3P0ProxyConnection) dataSource.getConnection();
                            dbConnection.setAutoCommit(false);
                        } finally {
                            lOCK.unlock();
                        }

                    } else {
                        try {
                            dbConnection = (C3P0ProxyConnection) dataSource.getConnection();
                            dbConnection.setAutoCommit(false);
                            try {
                                dbConnection.rollback();
                            } catch (SQLException sqe1) {
                                log.errorTrace(methodName, sqe1);
                                log.error("getConnection Rollback", exceptionErrorCodeMsg + sqe1.getErrorCode());
                            }
                        } catch (SQLException sqe3) {
                            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3P0ProxyConnection[getConnection]", "", "", "", "Database Connection Problem " + sqe3.getErrorCode());
                            throw new BTSLBaseException(sqe3);
                        }
                    }
                } else
                    throw new BTSLBaseException(DB_CONN_FAILED);
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                throw new BTSLBaseException(this,methodName,DB_CONN_FAILED,e);
            }
            if (null  == dbConnection)
                throw new BTSLBaseException(DB_CONN_FAILED);
            
            if (dataSource != null){
               log.error(methodName,  " active size:" + dataSource.getNumBusyConnections() + " cache size:" + dataSource.getNumIdleConnections());
               activeSizeKey.put("ACTIVESIZE", dataSource.getNumBusyConnections());
               activeSizeKey.put("CACHESIZE", dataSource.getNumIdleConnections());
               log.error("methodName", "DB Connections  getting connection from connection pool :" + dbConnection + " active size:" + activeSizeKey.get("ACTIVESIZE") + " cache size:" + activeSizeKey.get("CACHESIZE"));
            }
        }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[getConnection]", "", "", "", databaseConnProErrMsg);
            throw new BTSLBaseException(be);
        } catch (Exception ex) {
            // Trap errors
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[getConnection]", "", "", "", databaseConnProErrMsg);
            log.error(methodName, errorInConnectingDBMsg + ex.getMessage() + "\n");
            log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this,methodName,DB_CONN_FAILED,ex);
        }// end of catch
        return dbConnection;
    } // getConnection

    public boolean createPool() {
        FileInputStream fileInputStream = null;
        final String methodName = "createPool";
        try {
            if (!creatingPool) {
                creatingPool = true;
                if (log.isDebugEnabled()) {
                    log.debug(methodName, createConnMsg);
                }

                String dbUrl = Constants.getProperty("datasourceurl");
                String fileName = Constants.getProperty("C3p0Path");
                File file = new File(fileName);

                String dbUser = Constants.getProperty("userid");
                if (dbUser != null)
                    dbUser = BTSLUtil.decrypt3DesAesText(dbUser);
                String dbPassword = Constants.getProperty("passwd");
                if (dbPassword != null)
                    dbPassword = BTSLUtil.decrypt3DesAesText(dbPassword);

                String strMinPoolSize = Constants.getProperty("minpoolsize");
                String strPoolSize = Constants.getProperty("poolsize");
                String stracquireincr = Constants.getProperty("acquireincrement");
                String strstatpoolSize = Constants.getProperty("statementpoolSize");

                int minPoolSize = 0;
                try {
                    minPoolSize = Integer.parseInt(strMinPoolSize);
                } catch (Exception e) {
                    minPoolSize = 50;
                    log.errorTrace(methodName, e);
                }
                int poolSize = 0;
                try {
                    poolSize = Integer.parseInt(strPoolSize);
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    poolSize = 60;
                }

                int statpoolSize = 0;
                try {
                    statpoolSize = Integer.parseInt(strstatpoolSize);
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    statpoolSize = 10000;
                }
                int acquireincr = 0;
                try {
                    acquireincr = Integer.parseInt(stracquireincr);
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    acquireincr = 5;
                }

                try {
                    if (dbUrl != null) {
                        // creating the instatnce of oracle datasource
                        dataSource = new ComboPooledDataSource();
                        // provide various attribute of database driver to make
                        // connection
                        dataSource.setJdbcUrl(dbUrl);
                        dataSource.setUser(dbUser);
                        dataSource.setPassword(dbPassword);
                        if (file.exists()) {
                            Properties props = dataSource.getProperties();
                            fileInputStream = new FileInputStream(file);
                            props.load(fileInputStream);
                            try {
                                dataSource.setAcquireIncrement(Integer.parseInt(props.getProperty("acquireIncrement")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setAcquireRetryAttempts(Integer.parseInt(props.getProperty("acquireRetryAttempts")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setAcquireRetryDelay(Integer.parseInt(props.getProperty("acquireRetryDelay")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setAutoCommitOnClose(Boolean.parseBoolean(props.getProperty("acquireRetryDelay")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setBreakAfterAcquireFailure(Boolean.parseBoolean(props.getProperty("breakAfterAcquireFailure")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setCheckoutTimeout(Integer.parseInt(props.getProperty("checkoutTimeout")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setConnectionTesterClassName(props.getProperty("connectionTesterClassName"));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setDebugUnreturnedConnectionStackTraces(Boolean.parseBoolean(props.getProperty("debugUnreturnedConnectionStackTraces")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setForceIgnoreUnresolvedTransactions(Boolean.parseBoolean(props.getProperty("forceIgnoreUnresolvedTransactions")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setIdleConnectionTestPeriod(Integer.parseInt(props.getProperty("idleConnectionTestPeriod")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setInitialPoolSize(Integer.parseInt(props.getProperty("initialPoolSize")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxAdministrativeTaskTime(Integer.parseInt(props.getProperty("maxAdministrativeTaskTime")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxConnectionAge(Integer.parseInt(props.getProperty("maxAdministrativeTaskTime")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxIdleTime(Integer.parseInt(props.getProperty("maxIdleTime")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxIdleTimeExcessConnections(Integer.parseInt(props.getProperty("maxIdleTimeExcessConnections")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxPoolSize(Integer.parseInt(props.getProperty("maxPoolSize")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxStatements(Integer.parseInt(props.getProperty("maxStatements")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxStatementsPerConnection(Integer.parseInt(props.getProperty("maxStatementsPerConnection")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMinPoolSize(Integer.parseInt(props.getProperty("minPoolSize")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setNumHelperThreads(Integer.parseInt(props.getProperty("numHelperThreads")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setPropertyCycle(Integer.parseInt(props.getProperty("propertyCycle")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setTestConnectionOnCheckin(Boolean.parseBoolean(props.getProperty("testConnectionOnCheckin")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setTestConnectionOnCheckout(Boolean.parseBoolean(props.getProperty("testConnectionOnCheckout")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setUnreturnedConnectionTimeout(Integer.parseInt(props.getProperty("unreturnedConnectionTimeout")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }

                        } else {
                            dataSource.setMinPoolSize(minPoolSize);
                            dataSource.setMaxPoolSize(poolSize);
                            dataSource.setInitialPoolSize(minPoolSize);
                            dataSource.setAcquireIncrement(acquireincr);
                            dataSource.setMaxStatements(statpoolSize);
                        }
                        
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[createPool]", "", "", "", databaseConnProErrMsg);
                    return false;
                }
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[createPool]", "", "", "", databaseConnProErrMsg);
            return false;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {

                    log.errorTrace(methodName, e);
                }
            }
            creatingPool = false;
        }
        return true;
    }

    /**
     * This method returns a single connection
     * Creation date: (18/07/04)
     * 
     * @return Connection, database connection
     * @param none
     */
    @Override
    public Connection getSingleConnection() {
        Connection dbConnection = null;
        final String methodName = "getSingleConnection";
        try {
            if (dataSourceSingle == null) {
                String dbUrl = Constants.getProperty("datasourceurl");
                String dbUser = Constants.getProperty("userid");
                if (dbUser != null)
                    dbUser = BTSLUtil.decrypt3DesAesText(dbUser);
                String dbPassword = Constants.getProperty("passwd");
                if (dbPassword != null)
                    dbPassword = BTSLUtil.decrypt3DesAesText(dbPassword);
                if (log.isDebugEnabled()) {
                	log.debug(methodName,"dbUrl:"+dbUrl+",dbUser:"+dbUser+",dbPassword:"+dbPassword);
                }
                if (dbUrl != null) {
                
                	Class.forName(DRIVER_CLASS_NAME);
                    dataSourceSingle = DataSources.unpooledDataSource(dbUrl, dbUser, dbPassword);
       
                } else
                    return null;
            }// end of the m_connectionSinglePool
            dbConnection = dataSourceSingle.getConnection();

            dbConnection.setAutoCommit(false);
            if (dataSource != null){
                log.error(methodName,  "active size: " + dataSource.getNumBusyConnections() + " cache size:" + dataSource.getNumIdleConnections());
                }
        }// end of try
        catch (Exception ex) {
            // Trap errors
            log.error("getSingleConnection", errorInConnectingDBMsg + ex.getMessage() + "\n");
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[getSingleConnection]", "", "", "", databaseConnProErrMsg);
        }// end of catch
        return dbConnection;
    } // getConnection



    /**
     * This method returns connection from the connection pool, if there is no
     * connection
     * in the pool then adds new connection in the connection pool.
     * 
     * @return Connection, database connection
     * @param none
     * 
     */
    @Override
    public Connection getReportDBConnection() throws BTSLBaseException {
        C3P0ProxyConnection dbConnection = null;
        final String methodName = "getReportDBConnection";
        try {
            if (odsReportDB == null && !createPoolReportDB()) {
                    throw new BaseException(dbConnFailedReortDB);
            }
            try {
                // Getting connection from Connection pool
                dbConnection = (C3P0ProxyConnection) odsReportDB.getConnection();
                // Setting Autocommit false
                dbConnection.setAutoCommit(false);

            } catch (SQLException sqe) {
                log.errorTrace(methodName, sqe);
                int errorCode = sqe.getErrorCode();
                log.error(methodName, exceptionErrorCodeMsg + errorCode);
                if (errorCode == 17008) {
                    log.error(methodName, reCreateingPoolMsg);
                    // Creating pool
                    if (!createPoolReportDB())
                        throw new BTSLBaseException(this,methodName,dbConnFailedReortDB,sqe);
                    // Getting connection from Connection pool
                    dbConnection = (C3P0ProxyConnection) odsReportDB.getConnection();
                    // Setting Autocommit false
                    dbConnection.setAutoCommit(false);

                } else
                    throw new BTSLBaseException(this,methodName,dbConnFailedReortDB,sqe);
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                throw new BTSLBaseException(this,methodName,dbConnFailedReortDB,e);
            }
            if (null == dbConnection )
                throw new BTSLBaseException(dbConnFailedReortDB);
        }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[getReportDBConnection]", "", "", "", databaseConnProErrMsg);
            throw new BTSLBaseException(be);
        } catch (Exception ex) {
            // Trap errors
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[getReportDBConnection]", "", "", "", databaseConnProErrMsg);
            log.error(methodName, errorInConnectingDBMsg + ex.getMessage() + "\n");
            log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this,methodName,dbConnFailedReortDB,ex);
        }// end of catch
        return dbConnection;
    } // getConnection

    /**
     * This method returns a single connection
     * Creation date: (18/07/04)
     * 
     * @return Connection, database connection
     * @param none
     */
    @Override
    public Connection getReportDBSingleConnection() {
        Connection dbConnection = null;
        final String methodName = "getReportDBSingleConnection";
        try {
            if (odsSingleReportDB == null) {
                String dbUrl = Constants.getProperty("reportdbdatasourceurl");

                String dbUser = Constants.getProperty("reportdbuserid");
                if (dbUser != null)
                    dbUser = BTSLUtil.decrypt3DesAesText(dbUser);
                String dbPassword = Constants.getProperty("reportdbpasswd");
                if (dbPassword != null)
                    dbPassword = BTSLUtil.decrypt3DesAesText(dbPassword);
                if (dbUrl != null) {
                	Class.forName(DRIVER_CLASS_NAME);
                    odsSingleReportDB = DataSources.unpooledDataSource(dbUrl, dbUser, dbPassword);

                } else
                    return null;
            }// end of the m_connectionSinglePool

            dbConnection = odsSingleReportDB.getConnection();
            dbConnection.setAutoCommit(false);
       
        }// end of try
        catch (Exception ex) {
            // Trap errors
            log.error("getReportDBSingleConnection", errorInConnectingDBMsg + ex.getMessage() + "\n");
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[getReportDBSingleConnection]", "", "", "", databaseConnProErrMsg);
        }// end of catch
        return dbConnection;
    } // getConnection

    public boolean createPoolReportDB() {
        final String methodName = "createPoolReportDB";
        FileInputStream fileInputStream = null;
        try {
            if (!creatingPoolReportDB) {
                creatingPoolReportDB = true;
                if (log.isDebugEnabled()) {

                    log.debug(methodName, createConnMsg);
                }

                String fileName = Constants.getProperty("C3p0PathReport");
                File file = new File(fileName);

                String dbUrl = Constants.getProperty("reportdbdatasourceurl");

                String dbUser = Constants.getProperty("reportdbuserid");
                if (dbUser != null)
                    dbUser = BTSLUtil.decrypt3DesAesText(dbUser);
                String dbPassword = Constants.getProperty("reportdbpasswd");
                if (dbPassword != null)
                    dbPassword = BTSLUtil.decrypt3DesAesText(dbPassword);

                String strMinPoolSize = Constants.getProperty("reportdbminpoolsize");
                String strPoolSize = Constants.getProperty("reportdbpoolsize");
                String oraclePoolLogFile = Constants.getProperty("oraclePoolLogFile");
                String acquireincrement = Constants.getProperty("reportdbacquireincrement");

                if (log.isDebugEnabled())
                    log.debug("createPoolReportDB 10G", userid + dbUser + ", db password=" + dbPassword + ", dbUrl=" + dbUrl + ", minPoolSize=" + strMinPoolSize + " max poolSize=" + strPoolSize + " oraclePoolLogFile=" + oraclePoolLogFile);
                try {
                    if (dbUrl != null) {
                        // creating the instatnce of oracle datasource
                        odsReportDB = new ComboPooledDataSource();
                        // provide various attribute of database driver to make
                        // connection
                        odsReportDB.setJdbcUrl(dbUrl);
                        odsReportDB.setUser(dbUser);
                        odsReportDB.setPassword(dbPassword);

                        if (file.exists()) {
                            Properties props = dataSource.getProperties();
                            fileInputStream = new FileInputStream(file);
                            props.load(fileInputStream);
                            try {
                                dataSource.setAcquireIncrement(Integer.parseInt(props.getProperty("acquireIncrement")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setAcquireRetryAttempts(Integer.parseInt(props.getProperty("acquireRetryAttempts")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setAcquireRetryDelay(Integer.parseInt(props.getProperty("acquireRetryDelay")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setAutoCommitOnClose(Boolean.parseBoolean(props.getProperty("acquireRetryDelay")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setBreakAfterAcquireFailure(Boolean.parseBoolean(props.getProperty("breakAfterAcquireFailure")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setCheckoutTimeout(Integer.parseInt(props.getProperty("checkoutTimeout")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setConnectionTesterClassName(props.getProperty("connectionTesterClassName"));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setDebugUnreturnedConnectionStackTraces(Boolean.parseBoolean(props.getProperty("debugUnreturnedConnectionStackTraces")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setForceIgnoreUnresolvedTransactions(Boolean.parseBoolean(props.getProperty("forceIgnoreUnresolvedTransactions")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setIdleConnectionTestPeriod(Integer.parseInt(props.getProperty("idleConnectionTestPeriod")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setInitialPoolSize(Integer.parseInt(props.getProperty("initialPoolSize")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxAdministrativeTaskTime(Integer.parseInt(props.getProperty("maxAdministrativeTaskTime")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxConnectionAge(Integer.parseInt(props.getProperty("maxAdministrativeTaskTime")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxIdleTime(Integer.parseInt(props.getProperty("maxIdleTime")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxIdleTimeExcessConnections(Integer.parseInt(props.getProperty("maxIdleTimeExcessConnections")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxPoolSize(Integer.parseInt(props.getProperty("maxPoolSize")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxStatements(Integer.parseInt(props.getProperty("maxStatements")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxStatementsPerConnection(Integer.parseInt(props.getProperty("maxStatementsPerConnection")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMinPoolSize(Integer.parseInt(props.getProperty("minPoolSize")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setNumHelperThreads(Integer.parseInt(props.getProperty("numHelperThreads")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setPropertyCycle(Integer.parseInt(props.getProperty("propertyCycle")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setTestConnectionOnCheckin(Boolean.parseBoolean(props.getProperty("testConnectionOnCheckin")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setTestConnectionOnCheckout(Boolean.parseBoolean(props.getProperty("testConnectionOnCheckout")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setUnreturnedConnectionTimeout(Integer.parseInt(props.getProperty("unreturnedConnectionTimeout")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                  
                        } else {
                            odsReportDB.setInitialPoolSize(Integer.parseInt(strMinPoolSize));
                            odsReportDB.setMinPoolSize(Integer.parseInt(strPoolSize));
                            odsReportDB.setAcquireIncrement(Integer.parseInt(acquireincrement));
                            odsReportDB.setMaxPoolSize(Integer.parseInt(strPoolSize));
                        }

                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[createPoolReportDB]", "", "", "", databaseConnProErrMsg);
                    return false;
                }
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[createPoolReportDB]", "", "", "", databaseConnProErrMsg);
            return false;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {

                    log.errorTrace(methodName, e);
                }
            }

            creatingPoolReportDB = false;
        }
        return true;
    }


    C3P0ProxyConnection dbConnection = null;
    
    
    public C3P0ProxyConnection getDbConnection() {
		return dbConnection;
	}
	public void setDbConnection1(C3P0ProxyConnection dbConnection) {
		this.dbConnection = dbConnection;
	}
	
	String className;
	public void setClassName(String className) {
		this.className = className;
	}
	
	
	public void run() {
		
		boolean cont = true;
		int count =0;
		
		while(cont) {
			try {
				Thread.sleep(240000);
				count++;
				if(this.dbConnection == null || this.dbConnection.isClosed()) {
				/*	log.error("run",  "closed... "+this.dbConnection );*/
					cont = false;
				}else {
					
					log.error("run",  "Not closed...iteration.. >"+count+className);
					
					if(count > 1)
					log.error("run",  "Not closed...iteration >"+count+className);
					//Exception e = new Exception();
					//e.printStackTrace();
					try {
						String pattern = "yyyy-MM-dd";
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

						String date = simpleDateFormat.format(new Date());
						
						BufferedWriter bw = new BufferedWriter(new FileWriter("/data1/pretupsapp/tomcat9_pgdev/logs/leakConnectins"+date+".log", true));
						bw.write(className);
						bw.newLine();
						bw.close();
						
					}catch(Exception e) {
						e.printStackTrace();
					}
					
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
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
    @Override
    public Connection getCurrentReportDBConnection() throws BTSLBaseException {
        C3P0ProxyConnection dbConnection = null;
        final String methodName = "getCurrentReportDBConnection";
        try {
        	
            if (odsCurrentReportDB == null && !createPoolCurrentReportDB()) {
                    throw new BaseException(dbConnFailedCurrentReportDB);
            }
            try {
            	
            	log.error(methodName,  " active size:----------" + odsCurrentReportDB.getNumBusyConnections() + " cache size:" + odsCurrentReportDB.getNumIdleConnections());
            	log.error(methodName,  " active size:---------->" + odsCurrentReportDB.getNumBusyConnectionsDefaultUser() + " cache size:" + odsCurrentReportDB.getNumIdleConnectionsDefaultUser());
            	
                // Getting connection from Connection pool
                dbConnection = (C3P0ProxyConnection) odsCurrentReportDB.getConnection();
                // Setting Autocommit false
                dbConnection.setAutoCommit(false);
                
            } catch (SQLException sqe) {
                log.errorTrace(methodName, sqe);
                int errorCode = sqe.getErrorCode();
                log.error(methodName, exceptionErrorCodeMsg + errorCode);
                if (errorCode == 17008) {
                    log.error(methodName, reCreateingPoolMsg);
                    // Creating pool
                    if (!createPoolCurrentReportDB())
                        throw new BTSLBaseException(this,methodName,dbConnFailedCurrentReportDB,sqe);
                    // Getting connection from Connection pool
                    dbConnection = (C3P0ProxyConnection) odsCurrentReportDB.getConnection();
                    // Setting Autocommit false
                    dbConnection.setAutoCommit(false);

                } else
                    throw new BTSLBaseException(this,methodName,dbConnFailedCurrentReportDB,sqe);
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                throw new BTSLBaseException(this,methodName,dbConnFailedCurrentReportDB,e);
            }
            if (null  == dbConnection)
                throw new BTSLBaseException(dbConnFailedCurrentReportDB);
            if (dataSource != null){
                log.error(methodName,  "active size:" + dataSource.getNumBusyConnections() + " cache size:" + dataSource.getNumIdleConnections());
                }
        }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[getCurrentReportDBConnection]", "", "", "", databaseConnProErrMsg);
            throw new BTSLBaseException(be);
        } catch (Exception ex) {
            // Trap errors
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[getCurrentReportDBConnection]", "", "", "", databaseConnProErrMsg);
            log.error("getReportDBConnection", errorInConnectingDBMsg + ex.getMessage() + "\n");
            log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this,methodName,dbConnFailedCurrentReportDB,ex);
        }// end of catch
        return dbConnection;
    } // getConnection

    /**
     * This method returns a single connection
     * Creation date: (18/07/04)
     * 
     * @return Connection, database connection
     * @param none
     */
    @Override
    public Connection getCurrentReportDBSingleConnection() {
        Connection dbConnection = null;
        final String methodName = "getCurrentReportDBSingleConnection";
        try {
            if (odSingleCurrentReportDB == null) {
                String dbUrl = Constants.getProperty("currentDateRptDBDataSourceURL");
                String dbUser = Constants.getProperty("currentReportDBUserId");
                if (dbUser != null)
                    dbUser = BTSLUtil.decrypt3DesAesText(dbUser);
                String dbPassword = Constants.getProperty("currentReportDBPasswd");
                if (dbPassword != null)
                    dbPassword = BTSLUtil.decrypt3DesAesText(dbPassword);
                if (dbUrl != null) {
                	Class.forName(DRIVER_CLASS_NAME);
                    odSingleCurrentReportDB = DataSources.unpooledDataSource(dbUrl, dbUser, dbPassword);

                } else
                    return null;
            }// end of the m_connectionSinglePool

            dbConnection = odSingleCurrentReportDB.getConnection();

        }// end of try
        catch (Exception ex) {
            // Trap errors
            log.error("getCurrentReportDBSingleConnection", errorInConnectingDBMsg + ex.getMessage() + "\n");
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[getCurrentReportDBSingleConnection]", "", "", "", databaseConnProErrMsg);
        }// end of catch
        return dbConnection;
    } // getConnection

    /**
     * @return
     */
    public boolean createPoolCurrentReportDB() {
        final String methodName = "createPoolCurrentReportDB";
        try {
        	
        	System.out.println("creatingPoolCurrentReportDB  "+creatingPoolCurrentReportDB);
            if (!creatingPoolCurrentReportDB) {
                creatingPoolCurrentReportDB = true;
                LogFactory.printLog(methodName, createConnMsg, log);
                String dbUrl = Constants.getProperty("currentDateRptDBDataSourceURL");

                String dbUser = Constants.getProperty("currentReportDBUserId");
                if (dbUser != null)
                    dbUser = BTSLUtil.decrypt3DesAesText(dbUser);
                String dbPassword = Constants.getProperty("currentReportDBPasswd");
                if (dbPassword != null)
                    dbPassword = BTSLUtil.decrypt3DesAesText(dbPassword);

                String strMinPoolSize = Constants.getProperty("currentReportDBMinPoolSize");
                String strPoolSize = Constants.getProperty("currentReportDBPoolSize");
                String oraclePoolLogFile = Constants.getProperty("oraclePoolLogFile");
                String stracquireincr = Constants.getProperty("currentReportDBacquireincrement");

                if (log.isDebugEnabled())
                    log.debug("createPoolCurrentReportDB 10G", userid + dbUser + ", db password=" + dbPassword + ", dbUrl=" + dbUrl + ", minPoolSize=" + strMinPoolSize + " max poolSize=" + strPoolSize + " oraclePoolLogFile=" + oraclePoolLogFile);
                try {
                    if (dbUrl != null) {
                        // creating the instatnce of oracle datasource
                        odsCurrentReportDB = new ComboPooledDataSource();
                        // provide various attribute of database driver to make
                        // connection
                        odsCurrentReportDB.setJdbcUrl(dbUrl);
                        odsCurrentReportDB.setUser(dbUser);
                        odsCurrentReportDB.setPassword(dbPassword);
                        odsCurrentReportDB.setInitialPoolSize(Integer.parseInt(strPoolSize)); // the
                                                                                               // cache
                                                                                               // size
                                                                                               // is
                                                                                               // 5
                                                                                               // at
                                                                                               // least
                        odsCurrentReportDB.setMinPoolSize(Integer.parseInt(strPoolSize));
                        odsCurrentReportDB.setAcquireIncrement(Integer.parseInt(stracquireincr));
                        odsCurrentReportDB.setMaxPoolSize(Integer.parseInt(strMinPoolSize));

                        log.debug("Test createPoolCurrentReportDB 10G", odsCurrentReportDB);
                        
                        
                        
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, createPoolCurrentReportDBMsg, "", "", "", databaseConnProErrMsg);
                    return false;
                }
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, createPoolCurrentReportDBMsg, "", "", "", databaseConnProErrMsg);
            return false;
        } finally {
            creatingPoolCurrentReportDB = false;
        }
        return true;
    }

    @Override
    public Connection getExternalDBConnection() throws BTSLBaseException {
        C3P0ProxyConnection dbConnection = null;
        final String methodName = "getExternalDBConnection";
        try {
            if (odsExternalDB == null && !createPoolExternalDB()) {
                    throw new BaseException(dbConnFailedReortDB);
            }
            try {
                // Getting connection from Connection pool
                dbConnection = (C3P0ProxyConnection) odsExternalDB.getConnection();
                // Setting Autocommit false
                dbConnection.setAutoCommit(false);

            } catch (SQLException sqe) {
                log.errorTrace(methodName, sqe);
                int errorCode = sqe.getErrorCode();
                log.error(methodName, exceptionErrorCodeMsg + errorCode);
                if (errorCode == 17008) {
                    log.error(methodName, reCreateingPoolMsg);
                    // Creating pool
                    if (!createPoolReportDB())
                        throw new BTSLBaseException(this,methodName,dbConnFailedReortDB,sqe);
                    // Getting connection from Connection pool
                    dbConnection = (C3P0ProxyConnection) odsExternalDB.getConnection();
                    // Setting Autocommit false
                    dbConnection.setAutoCommit(false);

                } else
                    throw new BTSLBaseException(this,methodName,dbConnFailedReortDB,sqe);
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                throw new BTSLBaseException(this,methodName,dbConnFailedReortDB,e);
            }
            if (null  == dbConnection)
                throw new BTSLBaseException(dbConnFailedReortDB);
            if (dataSource != null){
                log.error(methodName,  "active size:" + dataSource.getNumBusyConnections() + " cache size:" + dataSource.getNumIdleConnections());
             }
             }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[getExternalDBConnection]", "", "", "", databaseConnProErrMsg);
            throw new BTSLBaseException(be);
        } catch (Exception ex) {
            // Trap errors
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[getExternalDBConnection]", "", "", "", databaseConnProErrMsg);
            log.error("getExternalDBConnection", errorInConnectingDBMsg + ex.getMessage() + "\n");
            log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this,methodName,dbConnFailedReortDB,ex);
        }// end of catch
        return dbConnection;
        // getConnection
    }

    public boolean createPoolExternalDB() {
        final String methodName = "createPoolExternalDB";
        FileInputStream fileInputStream = null;
        try {
            if (!creatingPoolExternalDB) {
                creatingPoolExternalDB = true;
                if (log.isDebugEnabled()) {

                    log.debug(methodName, createConnMsg);
                }

                String fileName = Constants.getProperty("C3p0PathExternalDB");
                File file = new File(fileName);

                String dbUrl = Constants.getProperty("externaldbdatasourceurl");

                String dbUser = Constants.getProperty("externaldbuserid");
                if (dbUser != null)
                    dbUser = BTSLUtil.decrypt3DesAesText(dbUser);
                String dbPassword = Constants.getProperty("externaldbpasswd");
                if (dbPassword != null)
                    dbPassword = BTSLUtil.decrypt3DesAesText(dbPassword);

                String strMinPoolSize = Constants.getProperty("externaldbminpoolsize");
                String strPoolSize = Constants.getProperty("externaldbpoolsize");
                // String
                String stracquireincr = Constants.getProperty("externaldbacquireincrement");
                String strstatpoolSize = Constants.getProperty("externalstatementpoolSize");
                if (log.isDebugEnabled())
                    log.debug("createPoolExternalDB 10G", userid + dbUser + ", db password=" + dbPassword + ", dbUrl=" + dbUrl + ", minPoolSize=" + strMinPoolSize + " max poolSize=" + strPoolSize);
                try {
                    if (dbUrl != null) {
                        // creating the instatnce of oracle datasource
                        odsExternalDB = new ComboPooledDataSource();
                        // provide various attribute of database driver to make
                        // connection
                        odsExternalDB.setJdbcUrl(dbUrl);
                        odsExternalDB.setUser(dbUser);
                        odsExternalDB.setPassword(dbPassword);

                        if (file.exists()) {
                            Properties props = dataSource.getProperties();
                            fileInputStream = new FileInputStream(file);
                            props.load(fileInputStream);
                            try {
                                dataSource.setAcquireIncrement(Integer.parseInt(props.getProperty("acquireIncrement")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setAcquireRetryAttempts(Integer.parseInt(props.getProperty("acquireRetryAttempts")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setAcquireRetryDelay(Integer.parseInt(props.getProperty("acquireRetryDelay")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setAutoCommitOnClose(Boolean.parseBoolean(props.getProperty("acquireRetryDelay")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setBreakAfterAcquireFailure(Boolean.parseBoolean(props.getProperty("breakAfterAcquireFailure")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setCheckoutTimeout(Integer.parseInt(props.getProperty("checkoutTimeout")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setConnectionTesterClassName(props.getProperty("connectionTesterClassName"));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setDebugUnreturnedConnectionStackTraces(Boolean.parseBoolean(props.getProperty("debugUnreturnedConnectionStackTraces")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setForceIgnoreUnresolvedTransactions(Boolean.parseBoolean(props.getProperty("forceIgnoreUnresolvedTransactions")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setIdleConnectionTestPeriod(Integer.parseInt(props.getProperty("idleConnectionTestPeriod")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setInitialPoolSize(Integer.parseInt(props.getProperty("initialPoolSize")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxAdministrativeTaskTime(Integer.parseInt(props.getProperty("maxAdministrativeTaskTime")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxConnectionAge(Integer.parseInt(props.getProperty("maxAdministrativeTaskTime")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxIdleTime(Integer.parseInt(props.getProperty("maxIdleTime")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxIdleTimeExcessConnections(Integer.parseInt(props.getProperty("maxIdleTimeExcessConnections")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxPoolSize(Integer.parseInt(props.getProperty("maxPoolSize")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxStatements(Integer.parseInt(props.getProperty("maxStatements")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMaxStatementsPerConnection(Integer.parseInt(props.getProperty("maxStatementsPerConnection")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setMinPoolSize(Integer.parseInt(props.getProperty("minPoolSize")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setNumHelperThreads(Integer.parseInt(props.getProperty("numHelperThreads")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }

                            try {
                                dataSource.setPropertyCycle(Integer.parseInt(props.getProperty("propertyCycle")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setTestConnectionOnCheckin(Boolean.parseBoolean(props.getProperty("testConnectionOnCheckin")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setTestConnectionOnCheckout(Boolean.parseBoolean(props.getProperty("testConnectionOnCheckout")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }
                            try {
                                dataSource.setUnreturnedConnectionTimeout(Integer.parseInt(props.getProperty("unreturnedConnectionTimeout")));
                            } catch (Exception e) {
                                log.errorTrace(methodName, e);
                            }

                        } else {
                            odsExternalDB.setInitialPoolSize(Integer.parseInt(strPoolSize)); // the
                                                                                              // cache
                                                                                              // size
                                                                                              // is
                                                                                              // 5
                                                                                              // at
                                                                                              // least
                            odsExternalDB.setMinPoolSize(Integer.parseInt(strPoolSize));
                            odsExternalDB.setAcquireIncrement(Integer.parseInt(stracquireincr));
                            odsExternalDB.setMaxPoolSize(Integer.parseInt(strMinPoolSize));
                            odsExternalDB.setMaxStatements(Integer.parseInt(strstatpoolSize));

                        }

                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C3p0PoolManager[createPoolExternalDB]", "", "", "", databaseConnProErrMsg);
                    return false;
                }
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, createPoolCurrentReportDBMsg, "", "", "", databaseConnProErrMsg);
            return false;
        } finally {

            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {

                    log.errorTrace(methodName, e);
                }
            }
            creatingPoolExternalDB = false;
        }
        return true;
    }

}
