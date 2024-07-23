package com.btsl.db.util;

import java.sql.Connection;
import java.sql.SQLException;

public interface MComConnectionI{
	
	public Connection getConnection() throws SQLException;
	public void partialCommit() throws SQLException;
	public void finalCommit() throws SQLException;
	public void partialRollback() throws SQLException;
	public void finalRollback() throws SQLException;
	public void closeAfterSelect(String str);
	public void close(String str);
	public void setDirty();
	public void setNotDirty();
	public boolean getDirtyStatus();
}
