package com.btsl.db.pool;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseException;
import com.btsl.db.util.BTSLDBManager;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.DBConenctionPoolLog;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

import oracle.jdbc.pool.OracleDataSource;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolDataSourceImpl;


public class UCPPoolManagers extends BTSLDBManager {
	private static Log log = LogFactory.getLog(UCPPoolManagers.class.getName());
	private static int _cntConnection = 0;
	private static final  ReentrantLock lOCK = new ReentrantLock();
	private static PoolDataSource poolDataSource = null;
	private static UniversalConnectionPoolManager mgr = null;
	static {
        try {
        	mgr = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
        	poolDataSource = PoolDataSourceFactory.getPoolDataSource();
        	poolDataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
        } catch (Exception e) {
            log.errorTrace("static", e);
        }
    }

	static final String cacheName = "PreTUPSCache_" + Constants.getProperty("CACHE_ID");
	static final String cacheName_single = "PreTUPSCache_" + Constants.getProperty("CACHE_ID") + "_SINGLE";
	
	private String createConnMsg =  "getting oracle ucp connection ";
	private String exceptionErrorCodeMsg = "Exception Error Code ";
	private String errorInConnectingDBMsg = "Error in Connecting to the Database \n";
	private static String databaseConnProErrMsg =  "Database Connection Problem";
	private String  reCreateingPoolMsg = "Recreating pool";

	
	private static final ConcurrentMap<String, Integer> activeSizeKey = new ConcurrentHashMap();
	public int getActiveConnection() {
		return activeSizeKey.get("ACTIVESIZE")!=null?activeSizeKey.get("ACTIVESIZE"):1;
	}
	public int getAvailableConnection() {
		return activeSizeKey.get("CACHESIZE")!=null?activeSizeKey.get("CACHESIZE"):100;
	}

	@SuppressWarnings({ "unused", "resource" })
	@Override
	public Connection getConnection() throws BTSLBaseException {
		Connection dbConnection = null;
		final String methodName = "getConnection";
		LogFactory.printLog(methodName, createConnMsg, log);
		try {
			if (_cntConnection == 0) {
                _cntConnection++;
                if (!createPool())
                    throw new BaseException(DB_CONN_FAILED);
            }
			try {
				log.error(methodName, "Before getting connection");
				dbConnection = poolDataSource.getConnection();
				dbConnection.setAutoCommit(false);

			} catch (SQLException sqe) {
				log.errorTrace(methodName, sqe);
				int errorCode = sqe.getErrorCode();
				log.error(methodName, exceptionErrorCodeMsg + errorCode);

				if (errorCode == 17008) {
					dbConnection = poolDataSource.getConnection();
					dbConnection.setAutoCommit(false);
					try {
						dbConnection.rollback();
					} catch (SQLException sqe1) {
						log.errorTrace(methodName, sqe1);
						log.error("getConnection Rollback", exceptionErrorCodeMsg + sqe1.getErrorCode());
					}
				}else if (errorCode == 17002 || errorCode == 17410 || errorCode == 17416) {
					log.error(methodName, reCreateingPoolMsg);
					if (lOCK.tryLock()) {
						try {
							if (!createPool())
								throw new BTSLBaseException(DB_CONN_FAILED);
							dbConnection = poolDataSource.getConnection();
							dbConnection.setAutoCommit(false);
						} finally {
							lOCK.unlock();
						}
					} else {
						try {
							dbConnection = poolDataSource.getConnection();
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

			if (poolDataSource != null){
				log.error(methodName,  " active size:" + poolDataSource.getBorrowedConnectionsCount() + " cache size:" + poolDataSource.getAvailableConnectionsCount());
				activeSizeKey.put("ACTIVESIZE", poolDataSource.getBorrowedConnectionsCount());
				activeSizeKey.put("CACHESIZE", poolDataSource.getAvailableConnectionsCount());
				log.error("methodName", "DB Connections  getting connection from connection pool :" + dbConnection + " active size:" + poolDataSource.getBorrowedConnectionsCount() + " cache size:" + poolDataSource.getAvailableConnectionsCount());
				DBConenctionPoolLog.log(poolDataSource.getBorrowedConnectionsCount(),poolDataSource.getAvailableConnectionsCount());
			}
		}// end of try
		catch (BTSLBaseException be) {
			EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCPPoolManagers[getConnection]", "", "", "", databaseConnProErrMsg);
			throw new BTSLBaseException(be);
		} catch (Exception ex) {
			// Trap errors
			EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCPPoolManagers[getConnection]", "", "", "", databaseConnProErrMsg);
			log.error(methodName, errorInConnectingDBMsg + ex.getMessage() + "\n");
			log.errorTrace(methodName, ex);
			throw new BTSLBaseException(this,methodName,DB_CONN_FAILED,ex);
		}// end of catch
		return dbConnection;
	} // getConnection

	public static boolean createPool() {
		FileInputStream fileInputStream = null;
		final String methodName = "createPool";
		try {

			String dbUrl = Constants.getProperty("datasourceurl");
			String dbUser = Constants.getProperty("userid");
			if (dbUser != null)
				dbUser = BTSLUtil.decrypt3DesAesText(dbUser);
			String dbPassword = Constants.getProperty("passwd");
			if (dbPassword != null)
				dbPassword = BTSLUtil.decrypt3DesAesText(dbPassword);

			String strMinPoolSize = Constants.getProperty("minpoolsize");
			String strPoolSize = Constants.getProperty("poolsize");

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

			try {
				if (dbUrl != null) {
					poolDataSource = PoolDataSourceFactory.getPoolDataSource();
					poolDataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
					poolDataSource.setURL(dbUrl);
					poolDataSource.setUser(dbUser);
					poolDataSource.setPassword(dbPassword);
					poolDataSource.setMinPoolSize(minPoolSize);
					poolDataSource.setMaxPoolSize(poolSize);
					poolDataSource.setInitialPoolSize(minPoolSize);
					poolDataSource.setConnectionPoolName(cacheName);
					mgr.createConnectionPool((PoolDataSourceImpl) poolDataSource); // Creates the embedded connection pool instance in the data source.
				    mgr.startConnectionPool(cacheName); // Starts the embedded connection pool instance.
				    log.error(methodName,  " connection pool created with active size:" + poolDataSource.getBorrowedConnectionsCount() + " cache size:" + poolDataSource.getAvailableConnectionsCount());
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCPPoolManagers[createPool]", "", "", "", databaseConnProErrMsg);
				return false;
			}
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UCPPoolManagers[createPool]", "", "", "", databaseConnProErrMsg);
			return false;
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {

					log.errorTrace(methodName, e);
				}
			}
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
        final String METHOD_NAME = "getSingleConnection";
        OracleDataSource _ods_single = null;
        try {
            if (_ods_single == null) {
                String db_url = Constants.getProperty("datasourceurl");
                String db_user = Constants.getProperty("userid");
                if (db_user != null)
                    db_user = BTSLUtil.decrypt3DesAesText(db_user);
                String db_password = Constants.getProperty("passwd");
                if (db_password != null)
                    db_password = BTSLUtil.decrypt3DesAesText(db_password);
                if (db_url != null && _ods_single == null) {
                    _ods_single = new OracleDataSource();
                    _ods_single.setURL(db_url);
                    _ods_single.setUser(db_user);
                    _ods_single.setPassword(db_password);
                } else
                    return null;
            }// end of the m_connectionSinglePool
        
            dbConnection = _ods_single.getConnection();
            dbConnection.setAutoCommit(false);
            if (log.isDebugEnabled())
                log.error("getSingleConnection 10G", "DB Connections  getting connection from connection pool :" + dbConnection + " active size: cache size:");

        
        }// end of try
        catch (Exception ex) {
            // Trap errors
            log.error("getSingleConnection", "Error in Connecting to the Database \n" + ex.getMessage() + "\n");
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OJDBCPoolManager[getSingleConnection]", "", "", "", "Database Connection Problem");
        }// end of catch
        return dbConnection;
    } // getConnection



	@Override
	public Connection getReportDBConnection() throws BTSLBaseException { return null;} // getConnection

	@Override
	public Connection getReportDBSingleConnection() {return null;} // getConnection

	public boolean createPoolReportDB() {return false;}

	@Override
	public Connection getCurrentReportDBConnection() throws BTSLBaseException {return null;} // getConnection

	@Override
	public Connection getCurrentReportDBSingleConnection() {return null;} 


	public boolean createPoolCurrentReportDB(){return false;}

	@Override
	public Connection getExternalDBConnection() throws BTSLBaseException {return null;}

	public boolean createPoolExternalDB() {return false;}

}
