package com.btsl.pretups.channel.reports.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.web.pretups.channel.reports.web.UsersReportForm;

public class ChannelTransferReportPostgresQry implements ChannelTransferReportQry {
	private Log log = LogFactory.getLog(this.getClass().getName());

	@Override
	public String loadVoucherO2CTransferDetailsQry(UsersReportForm thisForm)
	 {
		StringBuilder selectQueryBuff = new StringBuilder("SELECT V.NAME voucher_type,LK.LOOKUP_NAME segment,CVI.MRP denomination,VP.PRODUCT_NAME profile, ");
		selectQueryBuff.append("CVI.FROM_SERIAL_NO ,CVI.TO_SERIAL_NO,CVI.REQUESTED_QUANTITY,TU.user_name to_channel_user,TU.MSISDN to_channel_user_msisdn,CTRF.transfer_id,TO_CHAR(CTRF.transfer_date , '");
	    selectQueryBuff.append(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	    selectQueryBuff.append( "')transfer_date, TO_CHAR(CTRF.close_date , '");
	    selectQueryBuff.append(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_TIME_FORMAT)));
	    selectQueryBuff.append( "') close_date ");
		// FROM STARTS 
		selectQueryBuff.append(
		" FROM CHANNEL_TRANSFERS CTRF,CHANNEL_VOUCHER_ITEMS CVI,USERS TU, VOMS_PRODUCTS VP,VOMS_CATEGORIES VC,VOMS_TYPES V,LOOKUPS LK ");
		// WHERE STARTS 
		selectQueryBuff.append(
				" WHERE CTRF.TYPE = 'O2C' AND CTRF.close_date >= ? AND CTRF.close_date <= ? ");
		selectQueryBuff.append(
				" AND CTRF.transfer_id = CVI.transfer_id  AND CTRF.network_code = ? AND CTRF.from_user_id  = ? AND CTRF.transfer_sub_type = ? ");		
		if(PretupsI.CHANNEL_USER_TYPE.equals(thisForm.getUserType())){
			selectQueryBuff.append(" AND CTRF.TO_USER_ID = ? ");
		}
		selectQueryBuff.append(" AND CTRF.domain_code IN ( '").append(thisForm.getDomainListString()).append("') ");
		selectQueryBuff.append(
				" AND CTRF.sender_category_code = ?	AND CTRF.receiver_category_code = CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ? END");
		selectQueryBuff.append(" AND CTRF.grph_domain_code IN( WITH RECURSIVE q AS( SELECT UG1.grph_domain_code FROM USER_GEOGRAPHIES UG1 ");
		selectQueryBuff.append(" WHERE UG1.grph_domain_code = CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END ");
		selectQueryBuff.append(" AND UG1.user_id = ? UNION ALL SELECT GD1.grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 ");
		selectQueryBuff.append(" JOIN q ON q.grph_domain_code = GD1.parent_grph_domain_code AND GD1.status IN('Y','S')) ");
		selectQueryBuff.append(" Select grph_domain_code from q )");
		selectQueryBuff.append(
				"AND CTRF.status = 'CLOSE' AND CTRF.TO_USER_ID = TU.USER_ID ");
		selectQueryBuff.append(
				" AND V.VOUCHER_TYPE = CVI.VOUCHER_TYPE AND VP.PRODUCT_ID = CVI.PRODUCT_ID AND VC.CATEGORY_ID = VP.CATEGORY_ID ");
		selectQueryBuff.append(
				"AND LK.LOOKUP_TYPE = ? AND LK.LOOKUP_CODE = CVI.VOUCHER_SEGMENT ORDER BY CTRF.transfer_date DESC ");
	 return selectQueryBuff.toString();
	 
	 
	 	}
	@Override
	public String queryC2CTransferVoucherDetailsForChannelUser(UsersReportForm thisForm)
	 {
		final String methodName = "queryC2CTransferVoucherDetailsForChannelUser";
		if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered");
	     }
		//Select 
		StringBuilder selectQueryBuff = new StringBuilder( " SELECT VTYPE.NAME vouchername, lkp.LOOKUP_NAME segmentname, CVI.MRP,VP.PRODUCT_NAME profile,CVI.FROM_SERIAL_NO,CVI.TO_SERIAL_NO,CVI.REQUESTED_QUANTITY , "); 
	    selectQueryBuff.append( "FU.user_name from_user,CTRF.msisdn from_msisdn,TU.user_name to_user,CTRF.to_msisdn to_msisdn,CTRF.transfer_id,TO_CHAR(CTRF.transfer_date , '");
	    selectQueryBuff.append(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	    selectQueryBuff.append( "')transfer_date, TO_CHAR(CTRF.close_date , '");
	    selectQueryBuff.append(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_TIME_FORMAT)));
	    selectQueryBuff.append( "') close_date  FROM ");
	// From 	
	    selectQueryBuff.append("( WITH RECURSIVE usr AS ( SELECT u.user_id FROM  users u WHERE u.user_id = ?");
	    selectQueryBuff.append(" union ALL SELECT  u1.user_id FROM   users u1 join usr ur on   ur.user_id  = u1.parent_id ) SELECT  user_id FROM  usr ) X,");
	    selectQueryBuff.append(
				"CHANNEL_TRANSFERS CTRF, CHANNEL_VOUCHER_ITEMS CVI,VOMS_PRODUCTS VP,USERS FU,USERS TU,LOOKUPS LKP,VOMS_TYPES VTYPE,USER_GEOGRAPHIES UG ");
	// WHERE 
	    selectQueryBuff.append("WHERE CTRF.TYPE = 'C2C' AND CTRF.close_date >= ? AND CTRF.close_date <= ? AND CTRF.network_code =? ");
	    selectQueryBuff.append(
				" AND CTRF.sender_category_code = CASE ?  WHEN 'ALL' THEN CTRF.sender_category_code ELSE ?  END");
		selectQueryBuff.append(
				" AND CTRF.receiver_category_code = CASE ?  WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ?  END");
		selectQueryBuff.append(
				" AND CTRF.transfer_sub_type = ?");
		selectQueryBuff.append(
				" 	AND CTRF.from_user_id = X.user_id AND CTRF.status = 'CLOSE' ");
		selectQueryBuff.append("AND CTRF.TRANSFER_TYPE = 'TRANSFER' AND CTRF.transfer_id = CVI.transfer_id AND CTRF.FROM_USER_ID = FU.USER_ID AND CTRF.TO_USER_ID = TU.USER_ID AND CVI.VOUCHER_SEGMENT = LKP.LOOKUP_CODE AND CVI.VOUCHER_TYPE = VTYPE.VOUCHER_TYPE AND CVI.PRODUCT_ID = VP.PRODUCT_ID ");
		selectQueryBuff.append("AND CTRF.from_user_id = UG.user_id AND UG.grph_domain_code IN ( WITH RECURSIVE q AS(  ");
	    selectQueryBuff.append("SELECT UG1.grph_domain_code FROM USER_GEOGRAPHIES UG1 WHERE  UG1.grph_domain_code = CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END AND UG1.user_id =?");
	    selectQueryBuff.append(" UNION ALL  SELECT GD1.grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 JOIN q ON q.grph_domain_code = GD1.parent_grph_domain_code  AND GD1.status IN( 'Y','S') ");
	    selectQueryBuff.append("  ) Select grph_domain_code from q )");	
		//SELECT
		selectQueryBuff.append( " UNION SELECT VTYPE.NAME vouchername, lkp.LOOKUP_NAME segmentname, CVI.MRP,VP.PRODUCT_NAME profile,CVI.FROM_SERIAL_NO,CVI.TO_SERIAL_NO,CVI.REQUESTED_QUANTITY , "); 
		selectQueryBuff.append( "FU.user_name from_user,CTRF.msisdn from_msisdn,TU.user_name to_user,CTRF.to_msisdn to_msisdn,CTRF.transfer_id,TO_CHAR(CTRF.transfer_date , '");
	    selectQueryBuff.append(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	    selectQueryBuff.append( "')transfer_date, TO_CHAR(CTRF.close_date , '");
	    selectQueryBuff.append(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_TIME_FORMAT)));
	    selectQueryBuff.append( "') close_date  FROM ");
	    //FROM
		selectQueryBuff.append("( WITH RECURSIVE usr AS ( SELECT u.user_id FROM  users u WHERE u.user_id = ?");
		selectQueryBuff.append(" union ALL SELECT  u1.user_id FROM   users u1 join usr ur on   ur.user_id  = u1.parent_id ) SELECT  user_id FROM  usr ) X,");
		selectQueryBuff.append(
					"CHANNEL_TRANSFERS CTRF, CHANNEL_VOUCHER_ITEMS CVI,VOMS_PRODUCTS VP,USERS FU,USERS TU,LOOKUPS LKP,VOMS_TYPES VTYPE,USER_GEOGRAPHIES UG ");
		 //WHERE
		selectQueryBuff.append("WHERE CTRF.TYPE = 'C2C' AND CTRF.close_date >= ? AND CTRF.close_date <= ? AND CTRF.network_code =? ");
		selectQueryBuff.append(
					" AND CTRF.sender_category_code = CASE ?  WHEN 'ALL' THEN CTRF.sender_category_code ELSE ?  END");
		selectQueryBuff.append(
					" AND CTRF.receiver_category_code = CASE ?  WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ?  END");
		selectQueryBuff.append(
					" AND CTRF.transfer_sub_type = ?");
		selectQueryBuff.append(
					" AND CTRF.to_user_id = X.user_id AND CTRF.status = 'CLOSE' ");
		selectQueryBuff.append("AND CTRF.TRANSFER_TYPE = 'TRANSFER' AND CTRF.transfer_id = CVI.transfer_id AND CTRF.FROM_USER_ID = FU.USER_ID AND CTRF.TO_USER_ID = TU.USER_ID AND CVI.VOUCHER_SEGMENT = LKP.LOOKUP_CODE AND CVI.VOUCHER_TYPE = VTYPE.VOUCHER_TYPE AND CVI.PRODUCT_ID = VP.PRODUCT_ID ");
		selectQueryBuff.append("AND CTRF.from_user_id = UG.user_id AND UG.grph_domain_code IN ( WITH RECURSIVE q AS(  ");
	    selectQueryBuff.append("SELECT UG1.grph_domain_code FROM USER_GEOGRAPHIES UG1 WHERE  UG1.grph_domain_code = CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END AND UG1.user_id =?");
	    selectQueryBuff.append(" UNION ALL  SELECT GD1.grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 JOIN q ON q.grph_domain_code = GD1.parent_grph_domain_code  AND GD1.status IN( 'Y','S') ");
	    selectQueryBuff.append("  ) Select grph_domain_code from q )");		
		selectQueryBuff.append("ORDER BY TRANSFER_DATE DESC");
				return selectQueryBuff.toString();
	 }
	
	@Override
	public String queryC2CTransferVoucherDetailsForOperatorUser(UsersReportForm thisForm)
	 {
		final String methodName = "queryC2CTransferVoucherDetailsALL";
		if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
         }
		StringBuilder selectQueryBuff = new StringBuilder("SELECT VTYPE.NAME vouchername,lkp.LOOKUP_NAME segmentname,CVI.MRP,VP.PRODUCT_NAME profile,CVI.FROM_SERIAL_NO,CVI.TO_SERIAL_NO,CVI.REQUESTED_QUANTITY,"); 
		 selectQueryBuff.append( "FU.user_name from_user,CTRF.msisdn from_msisdn,TU.user_name to_user,CTRF.to_msisdn to_msisdn,CTRF.transfer_id,TO_CHAR(CTRF.transfer_date , '");
		    selectQueryBuff.append(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
		    selectQueryBuff.append( "')transfer_date, TO_CHAR(CTRF.close_date , '");
		    selectQueryBuff.append(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_TIME_FORMAT)));
		    selectQueryBuff.append( "') close_date ");
		    selectQueryBuff.append(
						" FROM CHANNEL_TRANSFERS CTRF, CHANNEL_VOUCHER_ITEMS CVI,VOMS_PRODUCTS VP,USERS FU,USERS TU,LOOKUPS LKP,VOMS_TYPES VTYPE ");
		// FROM ENDS  
		// WHERE STARTS 
		selectQueryBuff.append(
				" WHERE CTRF.TYPE = 'C2C' AND CTRF.close_date>= ? AND CTRF.close_date<= ?");// earliar CLOSE DATE
		selectQueryBuff.append(" AND CTRF.network_code = ? AND CTRF.domain_code IN ( '")
				.append(thisForm.getDomainListString()).append("') ");
		selectQueryBuff.append(
				" AND CTRF.sender_category_code = CASE ?  WHEN 'ALL' THEN CTRF.sender_category_code ELSE ?  END");
		selectQueryBuff.append(
				" AND CTRF.receiver_category_code = CASE ?  WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ?  END");
		selectQueryBuff.append(
				" AND CTRF.transfer_sub_type = ?");
		selectQueryBuff.append(
				" AND CTRF.status = 'CLOSE'  AND CTRF.grph_domain_code IN (");
		selectQueryBuff.append(
				"WITH RECURSIVE q AS(SELECT GD1.grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE GD1.status IN('Y','S')and GD1.grph_domain_code ");
		selectQueryBuff
				.append("=GD1.parent_grph_domain_code UNION ALL SELECT UG1.grph_domain_code FROM USER_GEOGRAPHIES UG1 WHERE UG1.grph_domain_code = CASE ? WHEN 'ALL' THEN  ");
		selectQueryBuff.append(
				"UG1.grph_domain_code ELSE ? END AND UG1.user_id =? ) Select grph_domain_code from q ) ");
		selectQueryBuff.append(
				" AND CTRF.transfer_id = CVI.transfer_id ");
		selectQueryBuff.append(
				" AND CTRF.FROM_USER_ID = FU.USER_ID AND CTRF.TO_USER_ID = TU.USER_ID ");
		selectQueryBuff.append( "AND CTRF.TRANSFER_TYPE = 'TRANSFER' AND CVI.VOUCHER_SEGMENT = LKP.LOOKUP_CODE AND CVI.VOUCHER_TYPE = VTYPE.VOUCHER_TYPE AND CVI.PRODUCT_ID = VP.PRODUCT_ID ");
		selectQueryBuff.append(" ORDER BY  CTRF.TRANSFER_DATE DESC ");
				return selectQueryBuff.toString();
	 }

	
	@Override
	public String queryC2CNLevelTrackingDetailReport() {
		final String methodName = "queryC2CNLevelTrackingDetailReport";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}

		

		StringBuilder selectQueryBuff = new StringBuilder(" SELECT   ");
		selectQueryBuff.append(" ug.GRPH_DOMAIN_CODE, ");
		selectQueryBuff.append(" cat.category_code, ");
		selectQueryBuff.append(" uv.voucher_type, ");
		selectQueryBuff.append(" currentUsers.login_id , ");
		selectQueryBuff.append(" currentUsers.user_id , ");
		
		selectQueryBuff.append(" VT.NAME, ");
		
		
		selectQueryBuff.append(" VV.VOUCHER_TYPE,   ");
		selectQueryBuff.append(" LKP.LOOKUP_NAME,   ");
		
		selectQueryBuff.append(" TO_NUMBER(VV.MRP/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(",'9999999999999999999999D99') as denomination,  ");

		selectQueryBuff.append(" vp.product_name ,    ");
		selectQueryBuff.append(" VV.serial_no,   ");
		selectQueryBuff.append(" currentUsers.user_name AS \"Channel User Name\",   ");
		selectQueryBuff.append(" currentUsers.MSISDN AS \"Channel User MSISDN\",   ");
		
		
		//selectQueryBuff.append(" parentUsr.user_name AS \"Parent Channel User Name\",   ");
		//selectQueryBuff.append(" parentUsr.MSISDN AS \"Parent Channel User MSISDN\",   ");
		
		// selectQueryBuff.append(" parentUsr.user_name AS \"Parent Channel User Name\",
		// ");
		selectQueryBuff.append(
				"  ( CASE currentUsers.parent_id WHEN   'ROOT' THEN 'NA' ELSE parentUsr.user_name END )  AS \"Parent Channel User Name\" ,  ");

		// selectQueryBuff.append(" parentUsr.MSISDN AS \"Parent Channel User MSISDN\",
		// ");
		selectQueryBuff.append(
				"  ( CASE currentUsers.parent_id WHEN   'ROOT' THEN 'NA' ELSE parentUsr.MSISDN END )  AS \"Parent Channel User MSISDN\" ,  ");
		
		selectQueryBuff.append("  LKPS.LOOKUP_NAME \"Status\",         ");
		selectQueryBuff.append("( CASE  WHEN ( VV.EXPIRY_DATE IS NULL ) THEN 'NA' ELSE   TO_CHAR(VV.EXPIRY_DATE, 'DD/MM/YYYY')  END) AS  \"EXPIRY_DATE\",         ");
		selectQueryBuff.append(" ( CASE  WHEN ( VV.C2C_transfer_date IS NULL ) THEN 'NA' ELSE   TO_CHAR(VV.C2C_transfer_date, 'DD/MM/YYYY')  END) AS \"C2C_transfer_date\"        ");

		
		
		
		selectQueryBuff.append(" FROM voms_vouchers VV,     ");
		selectQueryBuff.append(" USERS parentUsr,     ");
		selectQueryBuff.append(" USER_GEOGRAPHIES ug,     ");
		selectQueryBuff.append(" USER_VOUCHERTYPES uv,     ");
		selectQueryBuff.append(" USERS currentUsers,     ");
		selectQueryBuff.append(" categories cat  , voms_products vp  , ");

		
		selectQueryBuff.append(" VOMS_TYPES VT, ");
		selectQueryBuff.append(" LOOKUPS  LKP, ");
		selectQueryBuff.append(" LOOKUPS  LKPS ");

		selectQueryBuff.append(" where       ");
		selectQueryBuff.append(" currentUsers.user_id = uv.user_id        ");
		selectQueryBuff.append(" and currentUsers.user_id = ug.user_id        ");

		selectQueryBuff.append(" and currentUsers.user_id = vv.user_id  ");
		selectQueryBuff.append(" and currentUsers.category_code = cat.category_code   ");
		selectQueryBuff.append(" and vv.voucher_type = uv.voucher_type   ");
	//	selectQueryBuff.append(" and currentUsers.owner_id =  parentUsr.user_id  ");
		selectQueryBuff.append(" and ( CASE currentUsers.parent_id WHEN 'ROOT' THEN currentUsers.owner_id ELSE currentUsers.parent_id END ) =  parentUsr.user_id  ");

		selectQueryBuff.append(" and  VV.STATUS IN (?,?,?,?,?,?)  and vp.product_id = VV.product_id ");
		
		
		selectQueryBuff.append(" and  VV.VOUCHER_TYPE = VT.VOUCHER_TYPE ");
		selectQueryBuff.append(" and LKP.LOOKUP_CODE = VV.VOUCHER_SEGMENT ");
		selectQueryBuff.append(" and LKPS.LOOKUP_CODE  =VV.Status ");
		selectQueryBuff.append(" and LKPS.LOOKUP_TYPE='VSTAT' ");
    
		
		return selectQueryBuff.toString();
	}	
	

	@Override
	public String queryVoucherAvailabilityDetailsQry(UsersReportForm thisForm)
	{
		final String methodName = "queryVoucherAvailabilityDetailsQry";
		if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
         }
		StringBuilder selectQueryBuff = new StringBuilder("SELECT uu.user_name as user_id,uu.NETWORK_CODE as network_code,uu.CATEGORY_CODE as category_code,uu.PARENT_ID as parent_id,uu.MSISDN AS Channel_USErs_MSISDN,vv.STATUS as status, ");
		selectQueryBuff.append(" TO_NUMBER(vv.MRP/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(",'9999999999999999999999D99') as mrp, ");
		selectQueryBuff.append(" vv.VOUCHER_SEGMENT as voucher_segment,vv.VOUCHER_TYPE as voucher_type,vv.sold_status as sold_status,uu.user_type as user_type, ");
		selectQueryBuff.append(" vp.PRODUCT_NAME, COUNT( 1 ) AS no_of_vouchers ");
		selectQueryBuff.append(" FROM VOMS_VOUCHERS vv, VOMS_PRODUCTS vp, users uu ");
		selectQueryBuff.append(" WHERE uu.USER_ID = vv.USER_ID AND vv.STATUS IN(?,?,?,?,?,?) AND vv.PRODUCT_ID = vp.PRODUCT_ID AND vv.USER_ID IN( ");
		selectQueryBuff.append(" with recursive usr as( select u.user_id from users u where	u.user_id = ? union all select	u1.user_id from ");
		selectQueryBuff.append(" users u1 join usr ur on ur.user_id = u1.parent_id ) select	user_id	from usr ) GROUP BY ");
		selectQueryBuff.append(" uu.NETWORK_CODE, uu.CATEGORY_CODE, uu.PARENT_ID, uu.MSISDN, uu.user_name, vv.status, vv.mrp, vv.VOUCHER_SEGMENT, vv.VOUCHER_TYPE,vp.product_name,vv.sold_status,uu.USER_TYPE ");
		return selectQueryBuff.toString();
	}
	
	@Override
	public String queryVoucherConsumptionDetailsQry(UsersReportForm thisForm)
	{
		final String methodName = "queryVoucherConsumptionDetailsQry";
		if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
         }
		StringBuilder selectQueryBuff = new StringBuilder("SELECT uu.user_name as user_id,uu.NETWORK_CODE as network_code,uu.CATEGORY_CODE as category_code,uu.PARENT_ID as parent_id,  ");
		
		
		selectQueryBuff.append(
				" ( CASE uu.parent_id WHEN   'ROOT' THEN 'NA' ELSE (parentu.user_name) END ) as \"PARENT_USER_NAME\",  ");
		selectQueryBuff.append(
				" ( CASE uu.parent_id WHEN   'ROOT' THEN 'NA' ELSE (parentu.msisdn) END ) as \"PARENT_MSISDN\", ");
		
		selectQueryBuff.append(" uu.MSISDN AS Channel_USErs_MSISDN,vv.STATUS as status, ");
		selectQueryBuff.append(" TO_NUMBER(vv.MRP/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(",'9999999999999999999999D99') as mrp, ");
		selectQueryBuff.append(" vv.VOUCHER_SEGMENT as voucher_segment,vv.VOUCHER_TYPE as voucher_type,vv.sold_status as sold_status,uu.user_type as user_type, ");
		selectQueryBuff.append(" vp.PRODUCT_NAME, COUNT( 1 ) AS no_of_vouchers ");
		selectQueryBuff.append(" FROM VOMS_VOUCHERS vv, VOMS_PRODUCTS vp, users uu, users parentu ");
		selectQueryBuff.append(" WHERE uu.USER_ID = vv.USER_ID AND vv.STATUS = 'CU' AND vv.PRODUCT_ID = vp.PRODUCT_ID AND vv.USER_ID IN( ");
		selectQueryBuff.append(" with recursive usr as( select u.user_id from users u where	u.user_id = ? union all select	u1.user_id from ");
		selectQueryBuff.append(" users u1 join usr ur on ur.user_id = u1.parent_id ) select	user_id	from usr ) GROUP BY ");
		selectQueryBuff.append(" uu.NETWORK_CODE, uu.CATEGORY_CODE, uu.PARENT_ID, parentu.msisdn,  parentu.user_name,  uu.MSISDN, uu.user_name, vv.status, vv.mrp, vv.VOUCHER_SEGMENT, vv.VOUCHER_TYPE,vp.product_name,vv.sold_status,uu.USER_TYPE ");
		return selectQueryBuff.toString();
	}
}
