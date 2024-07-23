package com.btsl.pretups.processes.businesslogic;

/**
 * SendSMSToChannelAdmin4HourlyTransPostgresQry
 * @author sadhan.k
 *
 */
public class SendSMSToChannelAdmin4HourlyTransPostgresQry implements SendSMSToChannelAdmin4HourlyTransQry {

	@Override
	public StringBuilder fetchGeographyDomainDetailsPerUserWise() {
		
		StringBuilder qrySelect = new StringBuilder(" WITH RECURSIVE q AS ( ");
        qrySelect.append(" SELECT distinct grph_domain_code, GRPH_DOMAIN_NAME ,status FROM geographical_domains  WHERE  ");
        qrySelect.append(" grph_domain_code IN ( SELECT UG1.grph_domain_code FROM user_geographies UG1 WHERE UG1.user_id= ? ) ");
        qrySelect.append(" UNION ALL ");
        qrySelect.append(" SELECT distinct gd.grph_domain_code, gd.GRPH_DOMAIN_NAME,gd.status FROM geographical_domains gd ");
        qrySelect.append(" JOIN q ON  q.grph_domain_code = gd.parent_grph_domain_code");
        qrySelect.append(" )");
        qrySelect.append(" SELECT grph_domain_code ,grph_domain_name from q WHERE status IN('Y', 'S') ");
		return qrySelect;
	}

	@Override
	public StringBuilder fetchChannelOwnerUsersDetails() {
		
		StringBuilder qrySelect = new StringBuilder(" select   distinct c.CATEGORY_NAME DOMAIN_NAME ,coalesce(OU.MSISDN,'0') MSISDN, OU.USER_ID , OU.NETWORK_CODE, OU.EMAIL ");
        qrySelect.append(" FROM   USERS U,CATEGORIES c ,Users ou");
        qrySelect.append(" WHERE  OU.STATUS IN('Y', 'S') ");
        qrySelect.append(" and u.OWNER_ID=ou.USER_ID ");
        qrySelect.append("  and ou.CATEGORY_CODE=c.CATEGORY_CODE ");
       
		return qrySelect;
	}

}
