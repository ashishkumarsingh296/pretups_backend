package com.btsl.pretups.processes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * LifeCycleChangeStatusProcessQry
 * @author sadhan.k
 *
 */
public interface LifeCycleChangeStatusProcessQry {
	
	public static final Log LOG = LogFactory.getLog(LifeCycleChangeStatusProcess.class.getName());
	
	public String processUserStatus(String[] status, String[] categoryApplicable);
	public PreparedStatement checkActiveChildren(Connection con, String userid) throws SQLException ;

}
