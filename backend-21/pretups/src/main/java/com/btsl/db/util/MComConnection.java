package com.btsl.db.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.DBConActivityLog;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class MComConnection implements MComConnectionI{
	public static final String method_name="MComConnection";
	public static final Log log = LogFactory.getLog(MComConnection.class.getName());
	
	private boolean dirty;
	private Connection connection;

	public MComConnection()  throws BTSLBaseException{
		this.dirty = true;
		this.connection = OracleUtil.getConnection();
	}
	public MComConnection(boolean flag)  throws BTSLBaseException{
		this.dirty = flag;
		this.connection = OracleUtil.getConnection();
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
		
		//// Cast Fix///
		/*
		try{
			if(this.dirty && QueryConstants.DB_ORACLE.equals(Constants.getProperty(QueryConstants.PRETUPS_DB))){
				this.connection.rollback();
				DBConActivityLog.log(str);
			}
			this.connection.close();
			this.connection = null;
		}catch(SQLException e){
		 	 log.error(method_name, "Exception:e=" + e);
			 log.errorTrace(method_name, e);
		}
		*/
		
///////////////// Modified Part start here///////
		try{
			if(this.dirty && QueryConstants.DB_ORACLE.equals(Constants.getProperty(QueryConstants.PRETUPS_DB))){
				this.connection.rollback();
				DBConActivityLog.log(str);
			}

		}catch(SQLException e){
		 	 log.error(method_name, "Exception:e=" + e);
			 log.errorTrace(method_name, e);
		}finally {
			try {
				this.connection.close();
				this.connection = null;
			}catch(SQLException e){
			 	 log.error(method_name, "Exception:e=" + e);
				 log.errorTrace(method_name, e);
			}
			
		}
		
///////////////// Modified Part end here///////
		
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
