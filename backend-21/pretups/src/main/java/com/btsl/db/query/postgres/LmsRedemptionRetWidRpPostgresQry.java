package com.btsl.db.query.postgres;

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
public class LmsRedemptionRetWidRpPostgresQry implements LmsRedemptionRetWidRptQry{

	private Log log = LogFactory.getLog(this.getClass().getName());	
	private int i=1;	
	private static final String DATEPATTERN = "report.dateformat";
	
	@Override
	public PreparedStatement loadRedemptionRetWidQry(Connection pCon,
			LmsRedemptionReportModel lmsRedemptionReportModel)
			throws SQLException, ParseException {
		final StringBuilder strBuff = new StringBuilder(" SELECT U1.user_name, U1.msisdn, R.redemption_id, R.reference_id, R.product_code, ");
		strBuff.append(" R.amount_transfered, TO_CHAR (R.redemption_date, ?), redemption_date,CAT.category_name, CAT.domain_code, GD.grph_domain_name, ");
		strBuff.append(" R.redemption_type, U2.user_name AS redemption_by, R.points_redeemed,P.product_name,R.created_by, U2.user_id");
		strBuff.append(" FROM USERS U1,CATEGORIES CAT, REDEMPTIONS R ");
		strBuff.append(" left join USERS U2 on R.created_by=U2.user_id, USER_GEOGRAPHIES UG, PRODUCTS P, GEOGRAPHICAL_DOMAINS GD ");
        strBuff.append(" where R.user_id_or_msisdn=U1.user_id ");
        strBuff.append(" AND U1.category_code= CASE WHEN ? = '" + PretupsI.ALL + "' THEN U1.category_code ELSE ? END ");
        strBuff.append(" AND U1.network_code = ? ");
        strBuff.append(" AND U1.category_code = CAT.category_code ");
        strBuff.append(" AND U1.user_id= CASE WHEN ? = '" + PretupsI.ALL + "' then U1.user_id ELSE ? END ");
        strBuff.append(" AND CAT.domain_code = ? ");
        strBuff.append(" AND R.profile_type='LMS'");
        strBuff.append(" AND R.redemption_type=?");
        strBuff.append(" AND U1.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code = GD.grph_domain_code ");
        strBuff.append("AND R.REDEMPTION_DATE >= ? ");
        strBuff.append("AND R.REDEMPTION_DATE<= ? ");
        strBuff.append(" AND R.product_code::integer = P.product_short_code");
        strBuff.append(" AND UG.grph_domain_code IN ( with recursive q as(SELECT gd1.grph_domain_code, gd1.status FROM geographical_domains GD1 ");
        strBuff.append(" where grph_domain_code IN  (SELECT grph_domain_code ");
        strBuff.append(" FROM user_geographies UG1 WHERE UG1.grph_domain_code = (case ? when '" + PretupsI.ALL + "' then UG1.grph_domain_code else ? end)");
        strBuff.append(" AND UG1.user_id=?)");
        strBuff.append(" union all SELECT gd1.grph_domain_code, gd1.status ");
        strBuff.append(" FROM geographical_domains GD1 join q on q.grph_domain_code = gd1.parent_grph_domain_code");
        strBuff.append(" )");
        strBuff.append(" SELECT grph_domain_code ");
        strBuff.append(" FROM q ");
        strBuff.append(" WHERE status IN('Y','S'))");
        

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