package com.btsl.db.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.DBConActivityLog;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class MComReportDBConnection implements MComConnectionI{
	public static final String method_name="MComReportDBConnection";
	public static final Log log = LogFactory.getLog(MComReportDBConnection.class.getName());
	
	private boolean dirty;
	private Connection connection;

	public MComReportDBConnection()  throws BTSLBaseException{
		this.dirty = true;
		this.connection = OracleUtil.getReportDBConnection();
	}
	public MComReportDBConnection(boolean flag)  throws BTSLBaseException{
		this.dirty = flag;
		this.connection = OracleUtil.getReportDBConnection();
	}
	public Connection getConnection() throws SQLException{
		return this.connection;
	}
	public void partialCommit() throws SQLException{
		this.connection.commit();
	}
	public void finalCommit() throws SQLException{
		this.dirty=false;
		this.connection.commit();
	}
	public void partialRollback() throws SQLException{
		this.connection.rollback();
	}
	public void finalRollback() throws SQLException{
		this.dirty=false;
		this.connection.rollback();
	}
	public void closeAfterSelect(String str) {
		close(str);
	}
	public void close(String str) {
		try{
			if(this.dirty  && QueryConstants.DB_ORACLE.equals(Constants.getProperty(QueryConstants.PRETUPS_DB))){
				this.connection.rollback();
				DBConActivityLog.log(str);
			}
			this.connection.close();
			this.connection = null;
		}catch(SQLException e)
		{
			 log.error(method_name, "SQLException:e=" + e);
			 log.errorTrace(method_name, e);
		}
	}
	public void setDirty(){
		this.dirty=true;
	}
	public void setNotDirty(){
		this.dirty=false;
	}
	public boolean getDirtyStatus(){
		return this.dirty;
	}
}
