package com.btsl.pretups.processes.businesslogic;

/**
 * SendSMSToChannelAdmin4HourlyTransOracleQry
 * @author sadhan.k
 *
 */
public class SendSMSToChannelAdmin4HourlyTransOracleQry implements SendSMSToChannelAdmin4HourlyTransQry {
	
	@Override
	public StringBuilder fetchGeographyDomainDetailsPerUserWise() {
		
		
		StringBuilder qrySelect = new StringBuilder(" SELECT distinct gd1.grph_domain_code, gd1.GRPH_DOMAIN_NAME  ");
         qrySelect.append(" FROM geographical_domains GD1 WHERE status IN('Y', 'S') ");
         qrySelect.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
         qrySelect.append(" START WITH grph_domain_code IN ( ");
         qrySelect.append(" SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.user_id= ? ) ");
		
		return qrySelect;
	}

	@Override
	public StringBuilder fetchChannelOwnerUsersDetails() {
		 

		StringBuilder qrySelect = new StringBuilder(" select   distinct c.CATEGORY_NAME DOMAIN_NAME ,nvl(OU.MSISDN,0) MSISDN, OU.USER_ID , OU.NETWORK_CODE, OU.EMAIL ");
        qrySelect.append(" FROM   USERS U,CATEGORIES c ,Users ou");
        qrySelect.append(" WHERE  OU.STATUS IN('Y', 'S') ");
        qrySelect.append(" and u.OWNER_ID=ou.USER_ID ");
        qrySelect.append("  and ou.CATEGORY_CODE=c.CATEGORY_CODE ");
       
		return qrySelect;
	}

}
