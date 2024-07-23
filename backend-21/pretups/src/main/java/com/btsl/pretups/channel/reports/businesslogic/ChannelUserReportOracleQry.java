package com.btsl.pretups.channel.reports.businesslogic;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

/**
 * @author satakshi.gaur
 *
 */
public class ChannelUserReportOracleQry implements ChannelUserReportQry {
	private Log log = LogFactory.getLog(this.getClass().getName());

	@Override
	public PreparedStatement loadUserListBasisOfZoneDomainCategoryQry(Connection pCon,String pUserCategory,String pDomainCode,String pZoneCode,String pUserID,String pUserName) throws SQLException {

		final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT U.user_id, U.user_name FROM users U, user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.category_code = DECODE(?,'" + PretupsI.ALL + "',U.category_code,?)  ");
        strBuff.append(" AND U.category_code = CAT.category_code");
        strBuff.append(" AND CAT.domain_code = ?  AND U.user_id = UG.user_id  ");
        strBuff.append(" AND U.user_type =  '" + PretupsI.CHANNEL_USER_TYPE + "'  AND U.status NOT IN ('N','C','W') ");
        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN(?, ?)");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code  ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1  ");
        strBuff.append(" WHERE UG1.grph_domain_code = DECODE(?, '" + PretupsI.ALL + "', UG1.grph_domain_code, ?)   ");
        strBuff.append(" AND UG1.user_id= ? )) ");
        strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?)   ORDER BY U.user_name ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("ChannelUserReportOracleQry : loadUserListBasisOfZoneDomainCategoryQry ", " QUERY sqlSelect=" + sqlSelect);
        }
        
        int i = 0;
        PreparedStatement pstmtSelect =  pCon.prepareStatement(sqlSelect);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pDomainCode);
        pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
        pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pUserID);

        if(pUserName.contentEquals("ALL"))	pstmtSelect.setString(++i, "%");
        else pstmtSelect.setString(++i, pUserName + "%");
        
        return pstmtSelect;
	}

	@Override
	public PreparedStatement loadUserListBasisOfZoneDomainCategoryHierarchyQry(Connection pCon,String pUserCategory,String pDomainCode,String pZoneCode,String pUserID,String pUserName) throws SQLException {

		final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT U.user_id, U.user_name FROM users U, user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.category_code = DECODE(?,'" + PretupsI.ALL + "',U.category_code,?)  ");
        strBuff.append(" AND U.category_code = CAT.category_code");
        strBuff.append(" AND CAT.domain_code = ? ");
        strBuff.append(" AND U.user_type ='" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append(" AND U.user_id = UG.user_id  AND U.status NOT IN ('N','C','W') ");
        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN(?, ?)  ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code  ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1  ");
        strBuff.append(" WHERE	UG1.grph_domain_code =  DECODE(?, '" + PretupsI.ALL + "',UG1.grph_domain_code, ?) ");
        strBuff.append(" AND UG1.user_id= ? )) ");
        strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?)  ");
        strBuff.append(" CONNECT BY PRIOR U.user_id=U.parent_id START WITH U.user_id= ?  ORDER BY U.user_name");
        String sqlSelect= strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadUserListBasisOfZoneDomainCategoryHierarchy", " QUERY sqlSelect=" + sqlSelect);
        }
        PreparedStatement pstmtSelect =  pCon.prepareStatement(sqlSelect);
        int i = 0;
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pDomainCode);
        pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
        pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pUserID);

   
        pstmtSelect.setString(++i, pUserName + "%");
        pstmtSelect.setString(++i, pUserID);
        return pstmtSelect;
	}

	@Override
	public PreparedStatement loadUserListOnZoneDomainCategoryQry(
			String pFromUserID, String pUserName,Connection pCon,String pUserCategory,String domainCode,String ploginuserID,String pZoneCode )throws SQLException {
		
		final StringBuilder strBuff = new StringBuilder(" SELECT U.user_id, U.user_name ");
        strBuff.append(" FROM users U,user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.category_code = DECODE(?, '" + PretupsI.ALL + "', U.category_code, ?)  ");
        strBuff.append(" AND CAT.domain_code IN (SELECT UD.domain_code FROM user_domains UD WHERE UD.domain_code = (case ?  when '" + PretupsI.ALL + "' then UD.domain_code else ? end) AND UD.user_id = ?)");
        strBuff.append(" AND CAT.category_code = U.category_code ");
        strBuff.append(" AND U.user_type = '" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append(" AND U.status NOT IN ('N','C','W') ");
        strBuff.append(" AND U.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN(?, ?) ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
        strBuff.append(" FROM user_geographies UG1 ");
        strBuff.append(" WHERE	UG1.grph_domain_code =  DECODE(?, '" + PretupsI.ALL + "', UG1.grph_domain_code, ?)  ");
        strBuff.append(" AND UG1.user_id= ? ))  ");
        if (!BTSLUtil.isNullString(pFromUserID)) {
            strBuff.append(" AND U.user_id <> ?");
        }
        if (!BTSLUtil.isNullString(pUserName)) {
            strBuff.append("  AND UPPER(U.user_name) LIKE UPPER(?) ");
        }
        strBuff.append(" ORDER BY U.user_name");
        
        if (log.isDebugEnabled()) {
            log.debug("loadUserListOnZoneDomainCategoryQry", "QUERY sqlSelect= " + strBuff.toString());
        }
        PreparedStatement pstmtSelect =  pCon.prepareStatement(strBuff.toString());
        int i = 0;
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, domainCode);
        pstmtSelect.setString(++i, domainCode);
        pstmtSelect.setString(++i, ploginuserID);
        pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
        pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, ploginuserID);
        if (!BTSLUtil.isNullString(pFromUserID)) {
            pstmtSelect.setString(++i, pFromUserID);
        }
        if (!BTSLUtil.isNullString(pUserName)) {


            pstmtSelect.setString(++i, pUserName + "%");
        }
        return pstmtSelect;

	}

	@Override
	public PreparedStatement loadUserListWithOwnerIDOnZoneDomainCategoryQry(Connection pCon,String pUserCategory,String pDomainCode,String pZoneCode,String ploginuserID,String pFromUserID, String pUserName)throws SQLException {

		final StringBuilder strBuff = new StringBuilder(" SELECT U.user_id, U.user_name, U.owner_id ");
        strBuff.append(" FROM users U,user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.category_code = DECODE(?, '" + PretupsI.ALL + "',U.category_code, ?) ");

        strBuff
            .append(" AND CAT.domain_code IN (SELECT UD.domain_code FROM user_domains UD WHERE UD.domain_code = (case ?  when '" + PretupsI.ALL + "' then UD.domain_code else ? end) AND UD.user_id = ?)");
        strBuff.append(" AND CAT.category_code = U.category_code ");
        strBuff.append(" AND U.user_type = '" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append(" AND U.status NOT IN ('N','C','W') ");
        strBuff.append(" AND U.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN(?, ?) ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
        strBuff.append(" FROM user_geographies UG1 ");
        strBuff.append(" WHERE	UG1.grph_domain_code =  DECODE(?, '" + PretupsI.ALL + "', UG1.grph_domain_code, ?)  ");
        strBuff.append(" AND UG1.user_id= ? ))  ");
        if (!BTSLUtil.isNullString(pFromUserID)) {
            strBuff.append(" AND U.user_id <> ?");
        }
        if (!BTSLUtil.isNullString(pUserName)) {
            strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?)  ");
        }
        strBuff.append(" ORDER BY U.user_name");
        
        if (log.isDebugEnabled()) {
            log.debug("loadUserListWithOwnerIDOnZoneDomainCategory", " QUERY sqlSelect= " + strBuff.toString());
        }
        
        PreparedStatement pstmtSelect = pCon.prepareStatement(strBuff.toString());
        int i = 0;
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pDomainCode);
        pstmtSelect.setString(++i, pDomainCode);
        pstmtSelect.setString(++i, ploginuserID);
        pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
        pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, ploginuserID);
        if (!BTSLUtil.isNullString(pFromUserID)) {
            pstmtSelect.setString(++i, pFromUserID);
        }
        if (!BTSLUtil.isNullString(pUserName)) {
           
            pstmtSelect.setString(++i, pUserName + "%");
        }

        return pstmtSelect;
	}
	
	@Override
	public PreparedStatement loadUserListOnZoneCategoryHierarchyQry(Connection pCon,String pUserCategory,String domainCode,String pZoneCode,String ploginuserID,String pUserName) throws SQLException{

		
		 StringBuilder strBuff = new StringBuilder(" SELECT  U.user_id, U.user_name ");
        strBuff.append(" FROM users U, user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.category_code = DECODE(?, '" + PretupsI.ALL + "', U.category_code, ?) ");
        strBuff.append(" AND CAT.domain_code IN (SELECT UD.domain_code FROM user_domains UD WHERE UD.domain_code = (DECODE(?, '" + PretupsI.ALL + "', UD.domain_code, ?))) ");

        strBuff.append(" AND U.status NOT IN ('N','C','W') ");
        strBuff.append(" AND U.user_type = '" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append(" AND U.category_code = CAT.category_code ");
        strBuff.append(" AND U.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN(?, ?) ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1 ");
        strBuff.append(" WHERE	UG1.grph_domain_code = DECODE(?, '" + PretupsI.ALL + "', UG1.grph_domain_code, ?) ");
        strBuff.append(" AND UG1.user_id= ? ))");
        strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
        strBuff.append(" CONNECT BY PRIOR U.user_id = U.parent_id START WITH U.user_id= ? ");
        strBuff.append(" ORDER BY U.user_name ");

        if (log.isDebugEnabled()) {
            log.debug("loadUserListOnZoneCategoryHierarchy", "QUERY sqlSelect=" + strBuff.toString());
        }
        PreparedStatement pstmtSelect =  pCon.prepareStatement(strBuff.toString());
        int i = 0;
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, domainCode);
        pstmtSelect.setString(++i, domainCode);
        
        pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
        pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, ploginuserID);
        if (!BTSLUtil.isNullString(pUserName)) {
    
            pstmtSelect.setString(++i, "%" + pUserName + "%");
        } else {
           
            pstmtSelect.setString(++i, "%");
        }
        pstmtSelect.setString(++i, ploginuserID);
        return pstmtSelect;
	}
	@Override
	public PreparedStatement loadUserListWithOwnerIDOnZoneCategoryHierarchy(Connection pCon,String pUserCategory,String domainCode,String pZoneCode,String ploginuserID,String pUserName) throws SQLException{
		  StringBuilder strBuff = new StringBuilder(" SELECT  U.user_id, U.user_name, U.owner_id ");
	        strBuff.append(" FROM users U, user_geographies UG, categories CAT ");
	        strBuff.append(" WHERE U.category_code = DECODE(?, '" + PretupsI.ALL + "', U.category_code, ?) ");
	        strBuff.append(" AND CAT.domain_code IN (SELECT UD.domain_code FROM user_domains UD WHERE UD.domain_code = (DECODE(?, '" + PretupsI.ALL + "', UD.domain_code, ?))) ");
	        strBuff.append(" AND (U.STATUS = ? or U.status = ?) ");
	        strBuff.append(" AND U.user_type = '" + PretupsI.CHANNEL_USER_TYPE + "' ");
	        strBuff.append(" AND U.category_code = CAT.category_code ");
	        strBuff.append(" AND U.status NOT IN ('N','C','W') ");
	        strBuff.append(" AND U.user_id = UG.user_id ");
	        strBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN(?, ?) ");
	        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
	        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1 ");
	        strBuff.append(" WHERE	UG1.grph_domain_code = DECODE(?, '" + PretupsI.ALL + "', UG1.grph_domain_code, ?) ");
	        strBuff.append(" AND UG1.user_id= ? ))");
	        strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
	        strBuff.append(" CONNECT BY PRIOR U.user_id = U.parent_id START WITH U.user_id= ? ");
	        strBuff.append(" ORDER BY U.user_name ");

	        if (log.isDebugEnabled()) {
	            log.debug("loadUserListWithOwnerIDOnZoneCategoryHierarchy", "QUERY sqlSelect=" + strBuff.toString());
	        }
	        PreparedStatement pstmtSelect =  pCon.prepareStatement(strBuff.toString());
            int i = 0;
            pstmtSelect.setString(++i, pUserCategory);
            pstmtSelect.setString(++i, pUserCategory);
            pstmtSelect.setString(++i, domainCode);
            pstmtSelect.setString(++i, domainCode);
            pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
            pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
            pstmtSelect.setString(++i, pZoneCode);
            pstmtSelect.setString(++i, pZoneCode);
            pstmtSelect.setString(++i, ploginuserID);
            if (!BTSLUtil.isNullString(pUserName)) {
               
                pstmtSelect.setString(++i, "%" + pUserName + "%");
            } else {
              
                pstmtSelect.setString(++i, "%");
            }
            pstmtSelect.setString(++i, ploginuserID);
            return pstmtSelect;
	}
	
	
	@Override
	public StringBuffer prepareUserGeographyQuery() {
		
		StringBuffer strBuff = new StringBuffer(" ");

		strBuff.append("  SELECT  ");
		strBuff.append("  GD1.grph_domain_code ");
		strBuff.append("  FROM  ");
		strBuff.append("  GEOGRAPHICAL_DOMAINS GD1 ");
		strBuff.append("  WHERE ");
		strBuff.append("  GD1.status IN( ");
		strBuff.append("  'Y', ");
		strBuff.append("  'S' ");
		strBuff.append(
				"  ) CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code START WITH GD1.grph_domain_code IN( ");
		strBuff.append("  SELECT ");
		strBuff.append("  UG1.grph_domain_code ");
		strBuff.append("  FROM ");
		strBuff.append("  USER_GEOGRAPHIES UG1 ");
		strBuff.append("  WHERE ");
		strBuff.append("  UG1.grph_domain_code = CASE ");
		strBuff.append("  'ALL' ");
		strBuff.append("  WHEN 'ALL' THEN UG1.grph_domain_code ");
		strBuff.append("  ELSE 'ALL' ");
		strBuff.append("  END ");
		strBuff.append("  AND UG1.user_id =? ) ");

		
		
		return strBuff;
	}
	
}
