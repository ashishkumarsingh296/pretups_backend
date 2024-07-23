package com.web.pretups.pointenquiry.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface PointEnquiryQry {
	
	Log LOG = LogFactory.getLog(PointEnquiryQry.class.getName());
	
	public PreparedStatement loadCategoryUsersWithinGeoDomainHirearchy(Connection con,String networkCode,String domain,String categoryCode,String geographicalDomainCode)throws SQLException;

}
