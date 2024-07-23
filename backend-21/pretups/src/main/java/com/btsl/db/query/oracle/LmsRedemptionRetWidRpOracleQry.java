package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.LmsRedemptionRetWidRptQry;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.channel.reports.web.LmsRedemptionReportModel;

/**
 * 
 * @author sweta.verma
 *
 */
 
public class LmsRedemptionRetWidRpOracleQry implements LmsRedemptionRetWidRptQry{

	private Log log = LogFactory.getLog(this.getClass().getName());	
	private int i=1;	
	private static final String DATEPATTERN = "report.dateformat";
		
	@Override
	public PreparedStatement loadRedemptionRetWidQry(Connection pCon,
			LmsRedemptionReportModel lmsRedemptionReportModel)
			throws SQLException, ParseException {
		final StringBuilder strBuff = new StringBuilder(" SELECT U1.user_name AS user_name, U1.msisdn, R.redemption_id, R.reference_id, R.product_code, R.amount_transfered,  TO_CHAR(R.redemption_date, ?) redemption_date,CAT.category_name, CAT.domain_code,  GD.grph_domain_name, R.redemption_type, U2.user_name AS redemption_by, ");
				 strBuff.append( " R.points_redeemed,P.product_name,R.created_by,U2.user_id" );
				 strBuff.append( " FROM USERS U1,CATEGORIES CAT, REDEMPTIONS R, USER_GEOGRAPHIES UG, PRODUCTS P, GEOGRAPHICAL_DOMAINS GD , USERS U2 ");
				
        strBuff.append("R.created_by=U2.user_id(+) ");
        strBuff.append("R.user_id_or_msisdn=U1.user_id ");
        strBuff.append("U1.category_code=CASE '?' WHEN 'ALL' THEN U1.category_code ELSE ? END ");
        strBuff.append("AND U1.network_code = ? ");
        strBuff.append("AND U1.category_code = CAT.category_code ");
        strBuff.append("AND U1.user_id= CASE WHEN ? = '" + PretupsI.ALL + "' then U1.user_id ELSE ? END ");
        strBuff.append("AND CAT.domain_code = ? ");
        strBuff.append("AND R.profile_type='LMS'");
        strBuff.append("AND R.redemption_type=?");
        strBuff.append("AND U1.user_id = UG.user_id ");
        strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
        strBuff.append("AND R.REDEMPTION_DATE>= ? ");
        strBuff.append("AND R.REDEMPTION_DATE<= ? ");
        strBuff.append("AND R.product_code::integer = P.product_short_code");
        strBuff.append("AND UG.grph_domain_code IN ( SELECT grph_domain_code " );
        strBuff.append("FROM GEOGRAPHICAL_DOMAINS GD1  ");
        strBuff.append("WHERE status IN('Y','S'))");
        strBuff.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append("START WITH grph_domain_code IN (SELECT grph_domain_code ");
        strBuff.append("FROM USER_GEOGRAPHIES UG1 ");
        strBuff.append("WHERE UG1.grph_domain_code = CASE '?' WHEN 'ALL' THEN UG1.grph_domain_code ELSE '?' END ");
        strBuff.append("AND UG1.user_id=?");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("LmsPointsRedemptionProcessPostgresQry : loadUserListBasisOfZoneDomainCategoryQry ", "QUERY sqlSelect= " + sqlSelect);
        }
        
        PreparedStatement pstmtSelect =  pCon.prepareStatement(sqlSelect);
        int i = 0;
        pstmtSelect.setString(++i, "DD/MM/YY");
        pstmtSelect.setString(++i, lmsRedemptionReportModel.getCategoryCode());
        pstmtSelect.setString(++i, lmsRedemptionReportModel.getCategoryCode());
        pstmtSelect.setString(++i, lmsRedemptionReportModel.getNetworkCode());
        pstmtSelect.setString(++i, lmsRedemptionReportModel.getUserID());
        pstmtSelect.setString(++i, lmsRedemptionReportModel.getUserID());
        pstmtSelect.setString(++i, lmsRedemptionReportModel.getDomainCode());
        pstmtSelect.setString(++i, lmsRedemptionReportModel.getRedemptionType());
        pstmtSelect.setDate(++i, lmsRedemptionReportModel.getRptfromDate());
        pstmtSelect.setDate(++i, lmsRedemptionReportModel.getRpttoDate());
        pstmtSelect.setString(++i, lmsRedemptionReportModel.getZoneCode());
        pstmtSelect.setString(++i, lmsRedemptionReportModel.getZoneCode());
        pstmtSelect.setString(++i, lmsRedemptionReportModel.getUserID());
        return pstmtSelect;
	}
}