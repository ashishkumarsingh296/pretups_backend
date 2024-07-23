package com.btsl.db.query.oracle;

import com.btsl.menu.MenuQry;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.Constants;

public class MenuOracleQry implements MenuQry{

	@Override
	public StringBuilder loadAssignedMenuItemListQry() {
		StringBuilder queryBuff = new StringBuilder();
		if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_SPRING_ENABLE"))){
			queryBuff.append("SELECT PAGES.page_code,PAGES.module_code,PAGES.spring_page_url as page_url,PAGES.menu_name,");
		}else{
			queryBuff.append("SELECT PAGES.page_code,PAGES.module_code,PAGES.page_url,PAGES.menu_name,");
		}
         queryBuff.append(" PAGES.menu_item,PAGES.sequence_no,MODULE.module_name,PAGES.menu_level,nvl(ROLES.from_hour,0) FROMHOUR,nvl(ROLES.to_hour,24) TOHOUR,MODULE.sequence_no MSEQ,ROLES.role_code");
         queryBuff.append(" ,ROLES.ACCESS_TYPE ");
         queryBuff.append(" FROM CATEGORY_ROLES,USER_ROLES,ROLES ,PAGE_ROLES ,PAGES,MODULES MODULE ");
         queryBuff.append(" WHERE USER_ROLES.user_id=? AND USER_ROLES.role_code=ROLES.role_code AND ROLES.domain_type=? ");
         queryBuff.append(" AND CATEGORY_ROLES.category_code=? AND CATEGORY_ROLES.role_code=USER_ROLES.role_code ");
         queryBuff.append(" AND ROLES.role_code=PAGE_ROLES.role_code");
         queryBuff.append(" AND (ROLES.status IS NULL OR ROLES.status='Y') AND PAGE_ROLES.page_code=PAGES.page_code  ");
         queryBuff.append(" AND PAGES.module_code=MODULE.module_code");
         queryBuff.append(" AND (ROLES.role_type IS NULL OR ROLES.role_type=DECODE(?,'ALL',ROLES.role_type,?))");
         // Start Zebra and Tango
         queryBuff.append(" AND ROLES.gateway_types LIKE '%WEB%' ");
         // End Zebra and Tango
         queryBuff.append(" ORDER BY MODULE.application_id, MODULE.sequence_no,PAGES.application_id,PAGES.sequence_no");
         return queryBuff;
	}

	@Override
	public StringBuilder loadAssignedMenuItemListGroupRoleQry() {
		StringBuilder groupRoleQueryBuff = new StringBuilder();
		if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_SPRING_ENABLE"))){
			groupRoleQueryBuff.append("SELECT PAGES.page_code,PAGES.module_code,PAGES.spring_page_url as page_url,PAGES.menu_name,");
		}else{
			groupRoleQueryBuff.append("SELECT PAGES.page_code,PAGES.module_code,PAGES.page_url,PAGES.menu_name,");
		}
		
        groupRoleQueryBuff.append(" PAGES.menu_item,PAGES.sequence_no,MODULE.module_name,PAGES.menu_level,nvl(ROLES.from_hour,0) FROMHOUR,nvl(ROLES.to_hour,24) TOHOUR,MODULE.sequence_no MSEQ,GR.role_code");
        groupRoleQueryBuff.append(" ,ROLES.ACCESS_TYPE ");
        groupRoleQueryBuff.append(" FROM CATEGORY_ROLES,USER_ROLES,ROLES ,PAGE_ROLES ,PAGES,MODULES MODULE,GROUP_ROLES GR, roles GR_ROLES "); 
        groupRoleQueryBuff.append(" WHERE USER_ROLES.user_id=? AND USER_ROLES.role_code=ROLES.role_code AND ROLES.domain_type=? ");
        groupRoleQueryBuff.append(" AND CATEGORY_ROLES.category_code=? AND CATEGORY_ROLES.role_code=USER_ROLES.role_code  AND GR.group_role_code=ROLES.role_code ");
        groupRoleQueryBuff.append(" AND GR.role_code=PAGE_ROLES.role_code ");
        // following check is added in to the query
        groupRoleQueryBuff.append(" AND GR.role_code=GR_ROLES.role_code AND ROLES.domain_type=GR_ROLES.domain_type AND GR_ROLES.status='Y' ");
        // ends here
        groupRoleQueryBuff.append(" AND PAGE_ROLES.page_code=PAGES.page_code ");
        groupRoleQueryBuff.append(" AND (ROLES.status IS NULL OR ROLES.status='Y')   AND PAGES.module_code=MODULE.module_code AND (ROLES.role_type IS NULL OR ROLES.role_type=DECODE(?,'ALL',ROLES.role_type,?))  ");
        // Start Zebra and Tango
        groupRoleQueryBuff.append(" AND ROLES.gateway_types LIKE '%WEB%' ");
        // // End Zebra and Tango
        // groupRoleQueryBuff.append(" AND PAGES.module_code=MODULE.module_code");
        // groupRoleQueryBuff.append(" AND (ROLES.role_type IS NULL OR ROLES.role_type=DECODE(?,'ALL',ROLES.role_type,?))");
        groupRoleQueryBuff.append(" ORDER BY MODULE.application_id,MODULE.sequence_no,PAGES.application_id,PAGES.sequence_no");
		return groupRoleQueryBuff;
	}

	@Override
	public StringBuilder loadFixedMenuItemListQry() {
		StringBuilder queryBuff = new StringBuilder();
		if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_SPRING_ENABLE"))){
			queryBuff.append("SELECT PAGES.page_code,PAGES.module_code,PAGES.spring_page_url as page_url,PAGES.menu_name,");
		}else{
			queryBuff.append("SELECT PAGES.page_code,PAGES.module_code,PAGES.page_url,PAGES.menu_name,");
		}
		
          queryBuff.append(" PAGES.menu_item,PAGES.sequence_no,MODULE.module_name,PAGES.menu_level,nvl(ROLES.from_hour,0) FROMHOUR,nvl(ROLES.to_hour,24) TOHOUR,MODULE.sequence_no MSEQ,ROLES.role_code");
          queryBuff.append(" ,ROLES.ACCESS_TYPE ");
          queryBuff.append(" FROM CATEGORY_ROLES,ROLES ,PAGE_ROLES ,PAGES,MODULES MODULE ");
          queryBuff.append(" WHERE CATEGORY_ROLES.category_code=? AND CATEGORY_ROLES.role_code=ROLES.role_code AND ROLES.domain_type=? ");
          queryBuff.append(" AND ROLES.role_code=PAGE_ROLES.role_code");
          queryBuff.append(" AND (ROLES.status IS NULL OR ROLES.status='Y') AND PAGE_ROLES.page_code=PAGES.page_code  ");
          queryBuff.append(" AND PAGES.module_code=MODULE.module_code");
          queryBuff.append(" AND (ROLES.role_type IS NULL OR ROLES.role_type=DECODE(?,'ALL',ROLES.role_type,?))");
          // Start Zebra and Tango
          queryBuff.append(" AND ROLES.gateway_types LIKE '%WEB%' ");
          // End Zebra and Tango
          queryBuff.append(" ORDER BY ROLES.application_id,MODULE.sequence_no,PAGES.application_id,PAGES.sequence_no");
          return queryBuff;
	}

}
