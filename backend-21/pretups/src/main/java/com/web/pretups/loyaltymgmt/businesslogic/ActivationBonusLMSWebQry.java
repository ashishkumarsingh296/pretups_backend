package com.web.pretups.loyaltymgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface ActivationBonusLMSWebQry {
	public static final Log LOG = LogFactory.getLog(ActivationBonusLMSWebDAO.class.getClass().getName());
	
	public String loadpromotionListQry();
	public PreparedStatement loadUserForLMSAssociationQry (Connection p_con,String p_domainCode, String p_geographyCode, String p_category_code, String p_user_id,String p_gradeCode) throws SQLException;
	public String isprofileExpiredQry();
	public PreparedStatement loadMapForLMSAssociationQry(Connection p_con, String p_geographyCode, String p_category_code, String p_user_id, String p_gradeCode) throws SQLException;
	public String loadLmsProfileCacheQry();
	public PreparedStatement loadUserForLMSAssociationQuery(Connection p_con,String p_domainCode, String p_geographyCode, String p_category_code, String p_user_id,String p_gradeCode) throws SQLException;
	public String isControlledProfileAlreadyAssociatedQry();
	public String isProfileActiveQry();
}
