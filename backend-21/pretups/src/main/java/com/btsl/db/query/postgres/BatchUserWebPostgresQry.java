package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.channel.user.businesslogic.BatchUserWebQry;

public class BatchUserWebPostgresQry  implements BatchUserWebQry{
	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public String loadMasterGeographyListQry() {
		String methodname = "loadMasterGeographyListQry";
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" WITH  RECURSIVE q AS ( ");
		strBuff.append(" SELECT GD.grph_domain_code ,GD.grph_domain_name , GD.grph_domain_type , GDT.grph_domain_type  GDT_grph_domain_type,GD.status, ");
		strBuff.append(" GDT.sequence_no, GDT.grph_domain_type_name,array[grph_domain_code] as path_info ");
		strBuff.append(" FROM geographical_domains GD,geographical_domain_types GDT ");
		strBuff.append(" where GDT.grph_domain_type = GD.grph_domain_type and  GD.grph_domain_code IN(SELECT UG1.grph_domain_code FROM user_geographies UG1 ");
		strBuff.append(" WHERE UG1.grph_domain_code = case ? when '" + PretupsI.ALL + "' then UG1.grph_domain_code else ? end ");
		strBuff.append(" AND UG1.user_id=?) ");
		strBuff.append(" UNION  ALL ");
		strBuff.append(" SELECT GD1.grph_domain_code ,GD1.grph_domain_name , GD1.grph_domain_type, GDT1.grph_domain_type  GDT_grph_domain_type,GD1.status ,");
		strBuff.append(" GDT1.sequence_no, GDT1.grph_domain_type_name,path_info ");
		strBuff.append(" FROM geographical_domain_types GDT1 , geographical_domains GD1 ");
		strBuff.append(" join q on q.grph_domain_code = GD1.parent_grph_domain_code ");
		strBuff.append(" where GDT1.grph_domain_type = GD1.grph_domain_type )");
		strBuff.append(" SELECT grph_domain_code geography_code,grph_domain_name geography_name, grph_domain_type grph_domain_type, ");
		strBuff.append(" sequence_no, grph_domain_type_name,path_info from q ");
		strBuff.append(" where status IN('Y', 'S')  ");
		strBuff.append(" order by path_info , sequence_no ");
	

		String sqlSelect = strBuff.toString();
		LogFactory.printLog(methodname, strBuff.toString(), log);
		return sqlSelect;

	}
	
	@Override
	public PreparedStatement addChannelUserListChildGeographyAllowedQry(Connection con, String geographicalCode, String parentGeography)throws SQLException {
		final StringBuilder childGeographyAllowed = new StringBuilder();		
		childGeographyAllowed.append(" WITH RECURSIVE q AS ( ");
		childGeographyAllowed.append(" SELECT gd1.grph_domain_code FROM geographical_domains gd1 WHERE ");
		childGeographyAllowed.append(" gd1.grph_domain_code =? ");
		childGeographyAllowed.append(" UNION ALL ");
		childGeographyAllowed.append(" SELECT gd2.grph_domain_code FROM geographical_domains gd2 ");
		childGeographyAllowed.append(" join q on  q.grph_domain_code=gd2.parent_grph_domain_code ");
		childGeographyAllowed.append(" ) ");
		childGeographyAllowed.append(" SELECT 1 from q ");
		childGeographyAllowed.append(" WHERE  grph_domain_code=? ");

		LogFactory.printLog("addChannelUserListFromGeographicalDomainsQry", childGeographyAllowed.toString(), log);
		PreparedStatement pstmtChildGeographyAllowed = con.prepareStatement(childGeographyAllowed.toString());
        pstmtChildGeographyAllowed.setString(1, parentGeography);
		pstmtChildGeographyAllowed.setString(2, geographicalCode);
		return pstmtChildGeographyAllowed;
	}
	
	
	@Override
	public PreparedStatement addChannelUserListSelectParentGeographyQry(
			Connection con, String geoDomainCode, int geoTypeSeqNo)
					throws SQLException {
		final StringBuilder selectParentGeography = new StringBuilder();

		selectParentGeography.append(" WITH RECURSIVE q AS ( ");
		selectParentGeography.append(" SELECT  GD.grph_domain_code,  GD.parent_grph_domain_code, GDT.grph_domain_type,GD.grph_domain_type GD_grph_domain_type, GDT.sequence_no  FROM geographical_domains GD,geographical_domain_types GDT  ");
		selectParentGeography.append(" WHERE ");
		selectParentGeography.append("  GD.grph_domain_code IN(" + geoDomainCode + ") ");
		selectParentGeography.append(" UNION ALL ");
		selectParentGeography.append(" SELECT  GD1.grph_domain_code,  GD1.parent_grph_domain_code, GDT1.grph_domain_type,GD1.grph_domain_type GD_grph_domain_type, GDT1.sequence_no  FROM geographical_domain_types GDT1, geographical_domains GD1 ");
		selectParentGeography.append(" join q ON q.parent_grph_domain_code=GD1.grph_domain_code  ");
		selectParentGeography.append(" ) ");
		selectParentGeography.append(" SELECT DISTINCT grph_domain_code FROM q where grph_domain_type=GD_grph_domain_type AND  sequence_no=?   ");
		LogFactory.printLog("addChannelUserListSelectParentGeography", selectParentGeography.toString(), log);
		
		PreparedStatement psmtSlectParentGeography = con.prepareStatement(selectParentGeography.toString());
		psmtSlectParentGeography.setInt(1, geoTypeSeqNo);
		return psmtSlectParentGeography;
	}
	
	
	@Override
	public String loadCommProfileListQry(String domainCode,String networkCode) {
		  final StringBuilder strBuff = new StringBuilder(" SELECT CAT.category_code, CAT.CATEGORY_NAME, CPS.GRADE_CODE, CG.GRADE_NAME, CPS.GEOGRAPHY_CODE, ");
	        strBuff.append(" GD.GRPH_DOMAIN_NAME, CPS.comm_profile_set_id, CPS.comm_profile_set_name,CAT.SEQUENCE_NO ,CPS.STATUS");
	        strBuff.append(" FROM categories CAT,commission_profile_set CPS left join CHANNEL_GRADES CG on CPS.GRADE_CODE=CG.GRADE_CODE left join  GEOGRAPHICAL_DOMAINS GD on GD.GRPH_DOMAIN_CODE=CPS.GEOGRAPHY_CODE ");
	        strBuff.append(" WHERE CAT.domain_code=" + "'" + domainCode + "'" + " AND CAT.category_code=CPS.category_code  ");
	        strBuff.append(" AND CAT.status='Y' AND CPS.status!='N' AND CPS.network_code=" + "'" + networkCode + "'" + " order by CAT.SEQUENCE_NO, CPS.GEOGRAPHY_CODE,CPS.GRADE_CODE,CPS.comm_profile_set_id");
	        LogFactory.printLog("loadCommProfileListQry", strBuff.toString(), log);
		return  strBuff.toString();
	}
	
	@Override
	public String loadBatchBarListForApprovalQry( String currentLevel) {
		 final StringBuilder strBuff = new StringBuilder();
	        strBuff.append(" SELECT * FROM (SELECT DISTINCT b.batch_id,b.batch_name,b.batch_size,b.created_by,b.created_on,");
	        strBuff.append(" SUM(CASE WHEN b.status=? then 1 else 0 end) open, ");
	        strBuff.append(" SUM(CASE WHEN b.status=? then 1 else 0 end) appr1,SUM(case when b.status=? then 1 else 0 end ) rejct,");
	        strBuff.append(" SUM(case when b.status=? then 1 else 0 end) closed,");
	        strBuff.append(" b.network_code,b.modified_by, b.modified_on");
	        strBuff.append(" FROM batches b");
	        strBuff.append(" WHERE");
	        strBuff.append(" b.batch_type=?");
	        strBuff.append(" AND b.network_code=?");
	        strBuff.append(" GROUP BY b.batch_id,b.batch_name,b.batch_size,b.network_code,b.created_by,b.created_on,");
	        strBuff.append(" b.modified_by, b.modified_on ORDER BY b.modified_on DESC) X");
	        
	        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(currentLevel)) {
	            strBuff.append(" WHERE  open>0 ");
	        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(currentLevel)) {
	            strBuff.append(" WHERE appr1>0 ");
	        }
	        LogFactory.printLog("loadBatchBarListForApprovalQry", strBuff.toString(), log);
		return strBuff.toString();
	}
}
