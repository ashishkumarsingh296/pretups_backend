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
public class ChannelUserReportPostgresQry implements ChannelUserReportQry {
	private Log log = LogFactory.getLog(this.getClass().getName());

	@Override
	public PreparedStatement loadUserListBasisOfZoneDomainCategoryQry(Connection pCon,String pUserCategory,String pDomainCode,String pZoneCode,String pUserID,String pUserName) throws SQLException {

		final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT U.user_id, U.user_name FROM users U, user_geographies UG, categories CAT  ");
        strBuff.append(" WHERE U.category_code = CASE WHEN ? = '" +  PretupsI.ALL + "' then U.category_code ELSE ? END   ");
        strBuff.append(" AND U.category_code = CAT.category_code  ");
        strBuff.append(" AND CAT.domain_code = ?  ");
        strBuff.append(" AND U.user_id = UG.user_id  ");
        strBuff.append("   AND U.user_type = '" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append("   AND U.status NOT IN ('N','C','W')  ");
        strBuff.append("  AND UG.grph_domain_code IN (with recursive q as (SELECT grph_domain_code, status FROM  geographical_domains GD1 WHERE  ");
        strBuff.append("  grph_domain_code IN  ");
        strBuff.append("(SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.grph_domain_code = CASE WHEN ? = '"+ PretupsI.ALL +"' then UG1.grph_domain_code ELSE ? END AND UG1.user_id=? )");
        strBuff.append("union all ");
        strBuff.append(" select m.grph_domain_code, m.status from geographical_domains m ");
        strBuff.append(" join q on q.grph_domain_code=m.parent_grph_domain_code )select grph_domain_code from q WHERE status IN(?, ?) ) ");
        strBuff.append("  AND UPPER(U.user_name) LIKE UPPER(?)  ");
        strBuff.append(" ORDER BY U.user_name ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder(); 
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect= ");
        	loggerValue.append(sqlSelect);
        	
            log.debug("ChannelUserReportPostgresQry : loadUserListBasisOfZoneDomainCategoryQry ",  loggerValue);
        }
        
        PreparedStatement pstmtSelect =  pCon.prepareStatement(sqlSelect);
        int i = 0;
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pDomainCode);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pUserID);
        pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
        pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
        
        if(pUserName.contentEquals("ALL"))	pstmtSelect.setString(++i, "%");
        else pstmtSelect.setString(++i, pUserName + "%");
//        pstmtSelect.setString(++i, pUserName + "%");

		return pstmtSelect;
	}
	
			@Override
			public PreparedStatement loadUserListBasisOfZoneDomainCategoryHierarchyQry(Connection pCon,String pUserCategory,String pDomainCode,String pZoneCode,String pUserID,String pUserName) throws SQLException {
				final StringBuilder strBuff = new StringBuilder();
				
				strBuff.append("with recursive q as ( SELECT DISTINCT U.user_id, U.user_name ,U.category_code,CAT.domain_code FROM users U, user_geographies UG,categories CAT"); 
				strBuff.append(" WHERE  U.category_code = CAT.category_code AND U.user_type ='").append(PretupsI.CHANNEL_USER_TYPE).append("' AND U.user_id = UG.user_id AND U.status NOT IN ('N','C','W') "); 
				strBuff.append(" AND UG.grph_domain_code IN ("); 
				strBuff.append(" with recursive q1 as(SELECT GD1.grph_domain_code FROM geographical_domains GD1 WHERE status IN('Y', 'S')  and grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1");   
				strBuff.append(" WHERE UG1.grph_domain_code =  case ? when '").append(PretupsI.ALL).append("' then UG1.grph_domain_code else ? end  "); 
				strBuff.append(" AND UG1.user_id= ? ) union all SELECT GD1.grph_domain_code FROM geographical_domains GD1 join q1 on q1.grph_domain_code = GD1.parent_grph_domain_code )select q1.grph_domain_code from q1"); 
				strBuff.append(" ) AND"); 
				strBuff.append(" U.user_id= ?");  
				strBuff.append(" union all"); 
				strBuff.append(" SELECT DISTINCT U.user_id, U.user_name,U.category_code,CAT.domain_code FROM  user_geographies UG, categories CAT,users U join q on q.user_id=U.parent_id)"); 
				strBuff.append(" select   q.user_id, q.user_name from q where "); 
				strBuff.append(" category_code = case ? when '").append(PretupsI.ALL).append("' then category_code else ? end  AND domain_code = ? and UPPER(user_name) LIKE UPPER(?)"); 
				strBuff.append(" ORDER BY q.user_name"); 

				String sqlSelect=strBuff.toString();
		        if (log.isDebugEnabled()) {
		            log.debug("loadUserListBasisOfZoneDomainCategoryHierarchy", " QUERY sqlSelect=" + sqlSelect);
		        }
		        PreparedStatement pstmtSelect =  pCon.prepareStatement(sqlSelect);
	            int i = 0;
	            pstmtSelect.setString(++i, pZoneCode);
	            pstmtSelect.setString(++i, pZoneCode);
	            pstmtSelect.setString(++i, pUserID);
	            pstmtSelect.setString(++i, pUserID);
	            pstmtSelect.setString(++i, pUserCategory);
	            pstmtSelect.setString(++i, pUserCategory);
	            pstmtSelect.setString(++i, pDomainCode);
	            if (!BTSLUtil.isNullString(pUserName)) {
	                
	                pstmtSelect.setString(++i, "%" + pUserName + "%");
	            } else {
	               
	                pstmtSelect.setString(++i, "%");
	            }
	           
	            return pstmtSelect;
			}
	
	@Override
	public PreparedStatement loadUserListOnZoneDomainCategoryQry(String pFromUserID, String pUserName,Connection pCon,String pUserCategory,String domainCode,String ploginuserID,String pZoneCode ) throws SQLException{
		
		StringBuilder strBuff = new StringBuilder(" SELECT U.user_id, U.user_name ");
        strBuff.append(" FROM users U, user_geographies UG, categories CAT  ");
        strBuff.append("  WHERE U.category_code = CASE WHEN ? = '" +  PretupsI.ALL + "' then U.category_code ELSE ? END ");
        strBuff.append(" AND CAT.domain_code IN (SELECT UD.domain_code FROM user_domains UD WHERE UD.domain_code = (case ?  when '" + PretupsI.ALL + "' then UD.domain_code else ? end) AND UD.user_id = ?) ");
        strBuff.append(" AND CAT.category_code = U.category_code ");
        strBuff.append(" AND U.user_type = '" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append(" AND U.status NOT IN ('N','C','W') ");
        strBuff.append(" AND U.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code IN (with recursive q as (SELECT grph_domain_code , status FROM  geographical_domains GD1 WHERE grph_domain_code IN  ");
        strBuff.append(" (SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.grph_domain_code = CASE WHEN ? = '"+ PretupsI.ALL +"' then UG1.grph_domain_code ELSE ? END  AND UG1.user_id=? ) ");
        strBuff.append(" union all select m.grph_domain_code, m.status from geographical_domains m ");
        strBuff.append(" join q on q.grph_domain_code=m.parent_grph_domain_code )select grph_domain_code from q  WHERE status IN(?, ?))");
       
        if (!BTSLUtil.isNullString(pFromUserID)) {
            strBuff.append(" AND U.user_id <> ?");
        }
        if (!BTSLUtil.isNullString(pUserName)) {
            strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
        }
        strBuff.append(" ORDER BY U.user_name");
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder(); 
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY sqlSelect= ");
        	loggerValue.append(strBuff.toString());
            log.debug("loadUserListOnZoneDomainCategoryQry", loggerValue );
        }
        PreparedStatement pstmtSelect =  pCon.prepareStatement(strBuff.toString());
        int i = 0;
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, domainCode);
        pstmtSelect.setString(++i, domainCode);
        pstmtSelect.setString(++i, ploginuserID);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, ploginuserID);
        pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
        pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
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
		StringBuilder strBuff = new StringBuilder(" SELECT U.user_id, U.user_name, U.owner_id ");
        strBuff.append(" FROM users U, user_geographies UG, categories CAT ");
        strBuff.append(" WHERE U.category_code =  CASE WHEN ? = '" +  PretupsI.ALL + "' then U.category_code ELSE ? END  ");
        strBuff.append(" AND CAT.domain_code IN (SELECT UD.domain_code FROM user_domains UD WHERE UD.domain_code = (case ?  when '" + PretupsI.ALL + "' then UD.domain_code else ? end) AND UD.user_id = ?)");
        strBuff.append(" AND CAT.category_code = U.category_code ");
        strBuff.append(" AND U.user_type = '" + PretupsI.CHANNEL_USER_TYPE + "' ");
        strBuff.append(" AND U.status NOT IN ('N','C','W') ");
        strBuff.append(" AND U.user_id = UG.user_id ");
        strBuff.append(" AND UG.grph_domain_code IN (with recursive q as ( ");
        strBuff.append(" SELECT grph_domain_code, status FROM  geographical_domains GD1 where grph_domain_code IN ");
        strBuff.append("(SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.grph_domain_code = CASE WHEN ? = '"+ PretupsI.ALL +"' then UG1.grph_domain_code ELSE ? END  AND UG1.user_id=? ) ");
        strBuff.append(" union all select m.grph_domain_code, m.status from geographical_domains m ");
        strBuff.append(" join q on q.grph_domain_code=m.parent_grph_domain_code )select grph_domain_code from q WHERE status IN(?, ?) ) ");
        if (!BTSLUtil.isNullString(pFromUserID)) {
            strBuff.append(" AND U.user_id <> ?");
        }
        if (!BTSLUtil.isNullString(pUserName)) {
            strBuff.append(" AND UPPER(U.user_name) LIKE UPPER(?) ");
        }
        strBuff.append(" ORDER BY U.user_name");
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder(); 
        	loggerValue.append("QUERY sqlSelect=");
        	loggerValue.append(strBuff.toString());
            log.debug("loadUserListWithOwnerIDOnZoneDomainCategory",  loggerValue );
        }
        
        PreparedStatement pstmtSelect = pCon.prepareStatement(strBuff.toString());
        int i = 0;
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pUserCategory);
        pstmtSelect.setString(++i, pDomainCode);
        pstmtSelect.setString(++i, pDomainCode);
        pstmtSelect.setString(++i, ploginuserID);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, pZoneCode);
        pstmtSelect.setString(++i, ploginuserID);
        pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
        pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
        if (!BTSLUtil.isNullString(pFromUserID)) {
            pstmtSelect.setString(++i, pFromUserID);
        }
        if (!BTSLUtil.isNullString(pUserName)) {
           
            pstmtSelect.setString(++i, pUserName + "%");
        }
        return pstmtSelect;
	}

	
	@Override //connect by prior
	public PreparedStatement loadUserListOnZoneCategoryHierarchyQry(Connection pCon,String pUserCategory,String domainCode,String pZoneCode,String ploginuserID,String pUserName) throws SQLException{
		StringBuilder strBuff = new StringBuilder();	
		
		strBuff.append("with recursive q as ( SELECT DISTINCT U.user_id, U.user_name ,U.category_code,CAT.domain_code FROM users U, user_geographies UG,categories CAT");
		strBuff.append(" WHERE  U.category_code = CAT.category_code AND U.user_type =?  AND U.user_id = UG.user_id AND U.status NOT IN ('N','C','W')");
		strBuff.append(" AND UG.grph_domain_code IN (");
		strBuff.append(" with recursive q1 as(SELECT GD1.grph_domain_code FROM geographical_domains GD1 WHERE status IN('Y', 'S')  and grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1");
		strBuff.append(" WHERE UG1.grph_domain_code =  case ? when ? then UG1.grph_domain_code else ? end");
		strBuff.append(" AND UG1.user_id= ? ) union all SELECT GD1.grph_domain_code FROM geographical_domains GD1 join q1 on q1.grph_domain_code = GD1.parent_grph_domain_code )select q1.grph_domain_code from q1");
		strBuff.append(" ) AND U.user_id= ? union all");
		strBuff.append(" SELECT DISTINCT U.user_id, U.user_name,U.category_code,CAT.domain_code FROM  user_geographies UG, categories CAT,users U join q on q.user_id=U.parent_id)");
		strBuff.append(" select q.user_id, q.user_name from q where");
		strBuff.append(" category_code = case ? when ? then category_code else ? end  AND domain_code  IN (SELECT UD.domain_code FROM user_domains UD WHERE UD.domain_code = (case ? when ? then UD.domain_code else ? end))");
		strBuff.append(" and UPPER(user_name) LIKE UPPER(?)");
       
		String sqlSelect=strBuff.toString();

		 PreparedStatement pstmtSelect =  pCon.prepareStatement(sqlSelect);
         int i = 0;

         pstmtSelect.setString(++i, PretupsI.CHANNEL_USER_TYPE);
         pstmtSelect.setString(++i, pZoneCode);
         pstmtSelect.setString(++i, PretupsI.ALL);
         pstmtSelect.setString(++i, pZoneCode);
         pstmtSelect.setString(++i, ploginuserID);
         pstmtSelect.setString(++i, ploginuserID);
         pstmtSelect.setString(++i, pUserCategory);
         pstmtSelect.setString(++i, PretupsI.ALL);
         pstmtSelect.setString(++i, pUserCategory);
         pstmtSelect.setString(++i, domainCode);
         pstmtSelect.setString(++i, PretupsI.ALL);
         pstmtSelect.setString(++i, domainCode);
         if (!BTSLUtil.isNullString(pUserName)) {
     
             pstmtSelect.setString(++i, "%" + pUserName + "%");
         } else {
            
             pstmtSelect.setString(++i, "%");
         }
        
				 
		return pstmtSelect;
	}
	
	@Override //connect by prior
	public PreparedStatement loadUserListWithOwnerIDOnZoneCategoryHierarchy(Connection pCon,String pUserCategory,String domainCode,String pZoneCode,String ploginuserID,String pUserName) throws SQLException{
		StringBuilder strBuff = new StringBuilder();
		 strBuff.append(" with recursive q as(");
		 strBuff.append(" SELECT  U.user_id, U.category_code, CAT.category_code CAT_category_code, CAT.domain_code  CAT_domain_code ,");
		 strBuff.append(" U.user_type , U.status, UG.user_id  UG_user_id , UG.grph_domain_code  UG_grph_domain_code, "); 
		 strBuff.append(" U.user_name, U.owner_id FROM users U, user_geographies UG, categories CAT ");
		 strBuff.append(" where  U.user_id= ?");
		 strBuff.append(" Union all");
		 strBuff.append(" SELECT  U.user_id, U.category_code, CAT.category_code CAT_category_code, CAT.domain_code  CAT_domain_code ,");
		 strBuff.append(" U.user_type , U.status, UG.user_id  UG_user_id , UG.grph_domain_code  UG_grph_domain_code, "); 
		strBuff.append( " u.user_name, u.owner_id from users u join q on q.user_id=u.parent_id,user_geographies UG, categories CAT");
		 strBuff.append(" )select user_id,user_name,owner_id from q  ");
		 strBuff.append(" WHERE category_code = CASE WHEN ?='" + PretupsI.ALL + "' THEN category_code ELSE ? END");
         strBuff.append(" AND CAT_domain_code IN (SELECT UD.domain_code FROM user_domains UD WHERE UD.domain_code =(CASE WHEN ?= '" + PretupsI.ALL + "' THEN UD.domain_code ELSE ? END))");
         strBuff.append(" AND (STATUS = ? or status = ?)");
         strBuff.append(" AND user_type = 'CHANNEL'");
         strBuff.append(" AND category_code = CAT_category_code");
         strBuff.append(" AND status NOT IN ('N','C','W')");
         strBuff.append(" AND user_id = UG_user_id");
         strBuff.append(" AND UG_grph_domain_code IN (");
		 strBuff.append(" with recursive q as ( ");
		 strBuff.append(" SELECT grph_domain_code, status FROM geographical_domains GD1 WHERE");
		 strBuff.append(" grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1");
         strBuff.append(" WHERE	UG1.grph_domain_code = CASE WHEN ?= '" + PretupsI.ALL + "' THEN UG1.grph_domain_code ELSE ? END");
         strBuff.append(" AND UG1.user_id= ? ) union all select m.grph_domain_code, m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ");
		 strBuff.append(" ) select grph_domain_code from q WHERE status IN(?,?)	)");
		 strBuff.append(" AND UPPER(user_name) LIKE UPPER(?)");
		  strBuff.append(" ORDER BY user_name ");
		 
		 if (log.isDebugEnabled()) {
			 StringBuilder loggerValue= new StringBuilder(); 
			 loggerValue.append("QUERY sqlSelect=" );
			 loggerValue.append(strBuff.toString());
	            log.debug("loadUserListWithOwnerIDOnZoneCategoryHierarchy", loggerValue );
	        }
		 
		 PreparedStatement pstmtSelect =  pCon.prepareStatement(strBuff.toString());
         int i = 0;
         pstmtSelect.setString(++i, ploginuserID);
         pstmtSelect.setString(++i, pUserCategory);
         pstmtSelect.setString(++i, pUserCategory);
         pstmtSelect.setString(++i, domainCode);
         pstmtSelect.setString(++i, domainCode);
         pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
         pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
         pstmtSelect.setString(++i, pZoneCode);
         pstmtSelect.setString(++i, pZoneCode);
         pstmtSelect.setString(++i, ploginuserID);
         pstmtSelect.setString(++i, PretupsI.STATUS_ACTIVE);
         pstmtSelect.setString(++i, PretupsI.STATUS_SUSPEND);
         if (!BTSLUtil.isNullString(pUserName)) {
            
             pstmtSelect.setString(++i, "%" + pUserName + "%");
         } else {
           
             pstmtSelect.setString(++i, "%");
         }
       
         return pstmtSelect;
	}
	
	
	
	@Override
	public StringBuffer prepareUserGeographyQuery() {

		StringBuffer strBuff = new StringBuffer(" ");

		strBuff.append("  SELECT  ");
		strBuff.append("  WITH RECURSIVE q AS(  ");
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
		strBuff.append("  AND UG1.user_id =? ");

		strBuff.append("  UNION ALL ");

		strBuff.append("  SELECT ");
		strBuff.append("  GD1.grph_domain_code ");
		strBuff.append("  FROM ");
		strBuff.append("  GEOGRAPHICAL_DOMAINS GD1 ");
		strBuff.append("  JOIN q ");
		strBuff.append("  ON q.grph_domain_code = GD1.parent_grph_domain_code ");
		strBuff.append("  AND GD1.status IN( ");
		strBuff.append("  'Y','S' ");
		strBuff.append("  ) ");
		strBuff.append("  ) Select grph_domain_code from q ");

		return strBuff;
	}

}
