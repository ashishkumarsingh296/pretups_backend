package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.channel.user.businesslogic.BatchUserWebQry;

public class BatchUserWebOracleQry implements BatchUserWebQry{
	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public String loadMasterGeographyListQry()  {
		String methodname = "loadMasterGeographyListQry";
		final StringBuilder strBuff = new StringBuilder(" SELECT GD.grph_domain_code geography_code,GD.grph_domain_name geography_name, GD.grph_domain_type grph_domain_type, ");
		strBuff.append(" GDT.sequence_no, GDT.grph_domain_type_name ");
		strBuff.append(" FROM geographical_domains GD,geographical_domain_types GDT ");
		strBuff.append(" WHERE GD.status IN('Y', 'S')  ");
		strBuff.append(" AND GDT.grph_domain_type = GD.grph_domain_type ");
		strBuff.append(" CONNECT BY PRIOR GD.grph_domain_code = GD.parent_grph_domain_code ");
		strBuff.append(" START WITH GD.grph_domain_code IN(SELECT UG1.grph_domain_code FROM user_geographies UG1 ");
		strBuff.append(" WHERE UG1.grph_domain_code = case ? when '" + PretupsI.ALL + "' then UG1.grph_domain_code else ? end ");
		strBuff.append(" AND UG1.user_id=?) ");

		String sqlSelect = strBuff.toString();
		LogFactory.printLog(methodname, strBuff.toString(), log);
		return sqlSelect;

	}

	@Override
	public PreparedStatement addChannelUserListChildGeographyAllowedQry(Connection con, String geographicalCode, String parentGeography) throws SQLException{
		final StringBuilder childGeographyAllowed = new StringBuilder("SELECT 1 FROM geographical_domains gd1 WHERE   grph_domain_code=?");
		childGeographyAllowed.append(" CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code");
		childGeographyAllowed.append(" START WITH grph_domain_code =?");
		LogFactory.printLog("addChannelUserListFromGeographicalDomainsQry", childGeographyAllowed.toString(), log);
		PreparedStatement pstmtChildGeographyAllowed = con.prepareStatement(childGeographyAllowed.toString());
		pstmtChildGeographyAllowed.setString(1, geographicalCode);
		pstmtChildGeographyAllowed.setString(2, parentGeography);
		return pstmtChildGeographyAllowed;
	}

	@Override
	public PreparedStatement addChannelUserListSelectParentGeographyQry(
			Connection con, String geoDomainCode, int geoTypeSeqNo)
					throws SQLException {
		final StringBuilder selectParentGeography = new StringBuilder("SELECT DISTINCT GD.grph_domain_code FROM geographical_domains GD,geographical_domain_types GDT ");
		selectParentGeography.append(" WHERE GDT.grph_domain_type=GD.grph_domain_type AND GDT.sequence_no=? ");
		selectParentGeography.append(" CONNECT BY PRIOR GD.parent_grph_domain_code=GD.grph_domain_code START WITH GD.grph_domain_code IN(" + geoDomainCode + ")");
		LogFactory.printLog("addChannelUserListSelectParentGeography", selectParentGeography.toString(), log);
		PreparedStatement psmtSlectParentGeography = con.prepareStatement(selectParentGeography.toString());
		psmtSlectParentGeography.setInt(1, geoTypeSeqNo);
		return psmtSlectParentGeography;
	}

	@Override
	public String loadCommProfileListQry(String domainCode,String networkCode) {
		  final StringBuilder strBuff = new StringBuilder(" SELECT CAT.category_code, CAT.CATEGORY_NAME, CPS.GRADE_CODE, CG.GRADE_NAME, CPS.GEOGRAPHY_CODE, ");
	        strBuff.append(" GD.GRPH_DOMAIN_NAME, CPS.comm_profile_set_id, CPS.comm_profile_set_name,CAT.SEQUENCE_NO,CPS.STATUS ");
	        strBuff.append(" FROM categories CAT,commission_profile_set CPS, CHANNEL_GRADES CG, GEOGRAPHICAL_DOMAINS GD ");
	        strBuff.append(" WHERE CAT.domain_code=" + "'" + domainCode + "'" + " AND CAT.category_code=CPS.category_code AND CPS.GRADE_CODE=CG.GRADE_CODE(+) AND GD.GRPH_DOMAIN_CODE(+)=CPS.GEOGRAPHY_CODE ");
	        strBuff.append(" AND CAT.status='Y' AND CPS.status!='N' AND CPS.network_code=" + "'" + networkCode + "'" + " order by CAT.SEQUENCE_NO, CPS.GEOGRAPHY_CODE,CPS.GRADE_CODE,CPS.comm_profile_set_id");
	        LogFactory.printLog("loadCommProfileListQry", strBuff.toString(), log);
		return  strBuff.toString();
	}

	

	@Override
	public String loadBatchBarListForApprovalQry( String currentLevel) {
		 final StringBuilder strBuff = new StringBuilder();
	        strBuff.append(" SELECT * FROM (SELECT DISTINCT b.batch_id,b.batch_name,b.batch_size,b.created_by,b.created_on,");
	        strBuff.append(" SUM(DECODE(b.status,?,1,0)) open, ");
	        strBuff.append(" SUM(DECODE(b.status,?,1,0)) appr1,SUM(DECODE(b.status,?,1,0)) rejct,");
	        strBuff.append(" SUM(DECODE(b.status,?,1,0)) closed,");
	        strBuff.append(" b.network_code,b.modified_by, b.modified_on");
	        strBuff.append(" FROM batches b");
	        strBuff.append(" WHERE");
	        strBuff.append(" b.batch_type=?");
	        strBuff.append(" AND b.network_code=?");
	        strBuff.append(" GROUP BY b.batch_id,b.batch_name,b.batch_size,b.network_code,b.created_by,b.created_on,");
	        strBuff.append(" b.modified_by, b.modified_on ORDER BY b.modified_on DESC)");
	        
	        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
	            strBuff.append(" WHERE  open>0 ");
	        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
	            strBuff.append(" WHERE appr1>0 ");
	        }
	        LogFactory.printLog("loadBatchBarListForApprovalQry", strBuff.toString(), log);
		return strBuff.toString();
	}

}
