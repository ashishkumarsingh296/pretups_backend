package com.btsl.voms.vomsproduct.businesslogic;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.voms.vomscommon.VOMSI;

public class VomsProductOracleQry implements VomsProductQry{
	@Override
	public String loadProductDetailsListQry(String pStatusStr, boolean pUseALL,String pType){
		  StringBuilder strBuff = new StringBuilder(" SELECT VP.mrp,VP.product_id,VP.description,VP.talktime,VP.validity,VP.modified_on,VP.category_id,");
	        strBuff.append(" VP.product_name,VP.short_name,VP.expiry_period,VP.created_by,VP.created_on,VP.modified_by,VP.service_code, VP.VOUCHER_SEGMENT, ");
	        strBuff.append(" VP.max_req_quantity,VP.min_req_quantity,VC.category_name, LK.lookup_name, VP.status,VP.expiry_period,VP.expiry_date,VC.VOUCHER_TYPE");
	        strBuff.append("  FROM voms_products VP, voms_categories VC, lookups LK ");

	        if (pUseALL) {
	            strBuff.append(" WHERE VP.status = DECODE(" + pStatusStr + ", 'ALL', VP.status, " + pStatusStr + ")");
	        } else {
	            strBuff.append(" WHERE VP.status IN (" + pStatusStr + ")");
	        }
	        strBuff.append(" AND LK.lookup_code = VP.status AND LK.lookup_type = ?");
	        strBuff.append(" AND VC.category_id = VP.category_id and VC.network_code = ? ");
	        if (!BTSLUtil.isNullString(pType)) {
	            strBuff.append("AND VC.type =?");
	        }

	        strBuff.append("ORDER BY VP.mrp,VP.product_id ");
	        return strBuff.toString();
	}
	@Override
	public String loadProductDetailsListQuery(String pStatusStr,boolean pUseALL, String pType, String networkCode,String voucher_segment){
        StringBuilder strBuff = new StringBuilder(" SELECT VP.mrp,VP.product_id,VP.description,VP.talktime,VP.validity,VP.modified_on,VP.category_id,");
        strBuff.append(" VP.product_name,VP.short_name,VP.expiry_period,VP.created_by,VP.created_on,VP.modified_by,VP.service_code,");
        strBuff.append(" VP.max_req_quantity,VP.min_req_quantity,VC.category_name, LK.lookup_name, VP.status,VP.expiry_date,VC.VOUCHER_TYPE, VC.VOUCHER_SEGMENT ");
        strBuff.append("  FROM voms_products VP, voms_categories VC, lookups LK ");

        if (pUseALL) {
            strBuff.append(" WHERE VP.status = DECODE(" + pStatusStr + ", 'ALL', VP.status, " + pStatusStr + ")");
        } else {
            strBuff.append(" WHERE VP.status IN (" + pStatusStr + ")");
        }
        strBuff.append("  AND VC.VOUCHER_TYPE = ? ");
        strBuff.append(" AND LK.lookup_code = VP.status AND LK.lookup_type = ?");
        strBuff.append(" AND VC.category_id = VP.category_id ");
        strBuff.append(" AND ((VP.CREATED_ON + VP.EXPIRY_PERIOD) >= to_date(?, 'dd/mm/yy') ");
        strBuff.append(" OR VP.EXPIRY_DATE >= to_date(?, 'dd/mm/yy')) ");
        if(!BTSLUtil.isNullString(voucher_segment))
        	strBuff.append("AND VC.voucher_segment = ? ");
        if(!BTSLUtil.isNullString(networkCode))
        	strBuff.append(" AND VC.network_code = ?");
        if (!BTSLUtil.isNullString(pType)) {
            strBuff.append("AND VC.type =?");
        }
        strBuff.append("ORDER BY VP.mrp,VP.product_id ");
        return strBuff.toString();
	}
	@Override
	public String loadActiveProductDetailsListQry(){
        StringBuilder strBuff = new StringBuilder("");
        strBuff.append(" SELECT distinct vap.active_product_id, vap.applicable_from , vap.modified_on,vap.modified_by, vap.created_on, vap.created_by, vap.network_code, vap.status, vc.type, vc.voucher_type,vp.product_name ,vp.product_id,vt.type as voms_type ");
        strBuff.append(" FROM voms_active_products vap ,VOMS_PRODUCTS vp,VOMS_ACTIVE_PRODUCT_ITEMS vapi,VOMS_CATEGORIES vc,VOMS_TYPES vt WHERE  vap.active_product_id=vapi.active_product_id ");
        strBuff.append(" and  vapi.product_id=vp.product_id and vc.category_id= vp.category_id and  vap.network_code = ? ");

        // if the date format in SystemPreferences done not contain
        // time then use this in query to truncate the time in query also
        if (!(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_DATE_FORMAT))).trim().contains(" ")) {
            strBuff.append(" AND trunc(vap.applicable_from) > trunc(?) ");
            // if the date format in SystemPreference contains
            // time then use this in query to take care time also
        } else {
            strBuff.append(" AND vap.applicable_from >= ? ");        
        }
        
        strBuff.append(" AND vap.status = ? and vt.VOUCHER_TYPE = vc.VOUCHER_TYPE order by vap.applicable_from ");        

        return strBuff.toString();
	}
	@Override
	public String loadProductsByProductionLocationQry(String psubCategory){
        StringBuilder strBuff = new StringBuilder("SELECT DISTINCT P.product_id PRODUCTID,P.product_name PRODUCTNAME,VT.voucher_type,VVSM.service_id,");
        strBuff.append("P.category_id CATEGORYID,CAT.category_name CATEGORYNAME,");
        strBuff.append(" P.short_name SHORTNAME, P.mrp MRP,P.description DESCRIPTION, P.product_code PRODUCTCODE, P.min_req_quantity MINQUANTITY,");
        strBuff.append(" P.max_req_quantity MAXQUANTITY, P.multiple_factor MF,P.expiry_period EXPIRYPERIOD,P.individual_entity IND, ");
        strBuff.append(" nvl(P.individual_entity,'Y') entity, P.attribute1, P.created_by, P.created_on,P.modified_on, P.modified_by ,CAT.type  ");
        strBuff.append(" FROM voms_products P,voms_categories CAT,voms_types VT, voms_vtype_service_mapping VVSM ");
        strBuff.append(" WHERE vvsm.voucher_type=vt.voucher_type and P.network_code = ? and vvsm.voucher_type=?  and VVSM.service_id=CAT.service_id and CAT.category_id=P.category_id");

        if (!psubCategory.equalsIgnoreCase(VOMSI.ALL)) {
            strBuff.append(" AND P.category_id =?");
        }
        strBuff.append(" AND (P.status IS NULL OR P.status <> 'N') ");
        strBuff.append(" ORDER BY P.category_id,P.mrp");
        return strBuff.toString();
	}
	@Override
	public String loadProductsListQry(String psubCategory){
	       StringBuilder strBuff = new StringBuilder("SELECT DISTINCT P.product_id PRODUCTID,P.product_name PRODUCTNAME, P.category_id CATEGORYID,CAT.category_name CATEGORYNAME,DOMAIN.category_name DOMAINNAME,");
	        strBuff.append(" P.short_name SHORTNAME, P.mrp MRP,P.description DESCRIPTION, P.product_code PRODUCTCODE, P.min_req_quantity MINQUANTITY,");
	        strBuff.append(" P.max_req_quantity MAXQUANTITY, P.multiple_factor MF,P.expiry_period EXPIRYPERIOD,P.individual_entity IND,");
	        strBuff.append(" nvl(P.individual_entity,'Y') entity, P.attribute1, P.created_by, P.created_on,P.modified_on, P.modified_by ");
	        strBuff.append(" FROM voms_products P,voms_categories CAT,voms_categories DOMAIN,voms_types T");
	        strBuff.append(" WHERE CAT.category_id=P.category_id");
	        strBuff.append(" AND CAT.voucher_type=T.voucher_type AND P.category_id=DOMAIN.category_id ");
	        
	        if (!psubCategory.equalsIgnoreCase(VOMSI.ALL)) {
	            strBuff.append(" AND P.category_id =?");
	        }
	        strBuff.append(" AND (P.status IS NULL OR P.status <> 'N')");
	        strBuff.append(" ORDER BY  P.category_id,P.mrp");
	        return strBuff.toString();
	}
	
	@Override
	public String loadProductsListForPhysicalQry(String vType){
	       StringBuilder strBuff = new StringBuilder("SELECT DISTINCT P.product_id PRODUCTID,P.product_name PRODUCTNAME, P.category_id CATEGORYID,CAT.category_name CATEGORYNAME,DOMAIN.category_name DOMAINNAME,");
	        strBuff.append(" P.short_name SHORTNAME, P.mrp MRP,P.description DESCRIPTION, P.product_code PRODUCTCODE, P.min_req_quantity MINQUANTITY,");
	        strBuff.append(" P.max_req_quantity MAXQUANTITY, P.multiple_factor MF,P.expiry_period EXPIRYPERIOD,P.individual_entity IND,");
	        strBuff.append(" nvl(P.individual_entity,'Y') entity, P.attribute1, P.created_by, P.created_on,P.modified_on, P.modified_by ");
	        strBuff.append(" FROM voms_products P,voms_categories CAT,voms_categories DOMAIN,voms_types T");
	        strBuff.append(" WHERE CAT.category_id=P.category_id");
	        strBuff.append(" AND CAT.voucher_type= ? AND P.category_id=DOMAIN.category_id AND T.voucher_type =CAT.voucher_type AND P.network_code = CAT.network_code AND P.network_code = ? ");
            strBuff.append(" AND (P.status IS NULL OR P.status <> 'N')");
	        strBuff.append(" ORDER BY  P.category_id,P.mrp");
	        return strBuff.toString();
	}
	
	
	@Override
	public String getProductIDQry(){
        StringBuilder strBuff = new StringBuilder("SELECT vp.short_name, vp.product_id");
        strBuff.append(" FROM voms_active_product_items vapi, voms_active_products vap,voms_products vp");
        strBuff.append(" WHERE vp.mrp=? AND vp.product_id=vapi.product_id AND vap.active_product_id=vapi.active_product_id");
        strBuff.append(" AND vap.applicable_from=(SELECT MAX(applicable_from) FROM voms_active_products WHERE applicable_from<=sysdate)");
        strBuff.append(" AND vp.status=?");
        return strBuff.toString();
	}
	@Override
	public String getProductIDQuery(String profileName){
        StringBuilder strBuff = new StringBuilder("SELECT vp.short_name, vp.product_id");
        strBuff.append(" FROM voms_active_product_items vapi, voms_active_products vap,voms_products vp");
        strBuff.append(" WHERE vp.mrp=? AND vp.product_id=vapi.product_id AND vap.active_product_id=vapi.active_product_id");
        // modified by harsh for voucher upload of related voucher_type on
        // 12-09-2013
        strBuff.append(" AND vap.applicable_from in (SELECT applicable_from FROM voms_active_products WHERE applicable_from<=sysdate)");

        strBuff.append(" AND vp.status=?");
        // below condition added to check profile name in uploaded vouchers file
        // by rahul.d on 6/6/13
        if (!BTSLUtil.isNullString(profileName)) {
            strBuff.append(" and upper(vp.product_name)=upper(?) ");
        }
        
        return strBuff.toString();
	}
	@Override
	public String loadProductsListbyCategoryQry(){
		StringBuilder strBuff = new StringBuilder("SELECT DISTINCT P.product_id PRODUCTID,P.product_name PRODUCTNAME, P.category_id CATEGORYID,CAT.category_name CATEGORYNAME,");
        strBuff.append(" P.short_name SHORTNAME, P.mrp MRP,P.description DESCRIPTION, P.product_code PRODUCTCODE, P.min_req_quantity MINQUANTITY,");
        strBuff.append(" P.max_req_quantity MAXQUANTITY, P.multiple_factor MF,P.expiry_period EXPIRYPERIOD,P.individual_entity IND,");
        strBuff.append(" nvl(P.individual_entity,'Y') entity, P.attribute1, P.created_by, P.created_on,P.modified_on, P.modified_by ");
        strBuff.append(" FROM voms_products P,voms_categories CAT,voms_types T");
        strBuff.append(" WHERE CAT.category_id=P.category_id");
        strBuff.append(" AND CAT.voucher_type=T.voucher_type");
        strBuff.append(" AND P.category_id =?");
        strBuff.append(" AND (P.status IS NULL OR P.status <> 'N')");
        strBuff.append(" ORDER BY  P.category_id,P.mrp");
        return strBuff.toString();
	}
	
	@Override
	public String addUserVoucherTypeListQry(String p_userId, String[] p_voucherType, String p_status) {
		 StringBuilder strBuff = new StringBuilder(" INSERT INTO USER_VOUCHERTYPES (user_id, ");
	        strBuff.append(" VOUCHER_TYPE) values (?,?) ");
	        return strBuff.toString();
	}
	
	@Override
	public String addUserVoucherSegmentListQry(String p_userId, String[] p_voucherSegment, String p_status) {
		 StringBuilder strBuff = new StringBuilder(" INSERT INTO USER_VOUCHER_SEGMENTS (user_id, ");
	        strBuff.append(" VOUCHER_SEGMENT) values (?,?) ");
	        return strBuff.toString();
	}
	
	@Override
	public String loadVoucherTypeListQry() {
		StringBuilder strBuff = new StringBuilder(" SELECT VOUCHER_TYPE,NAME,SERVICE_TYPE_MAPPING,STATUS,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY ");
        strBuff.append("FROM VOMS_TYPES ");
        strBuff.append("WHERE STATUS<>'N'");
        return strBuff.toString();
	}
	
	@Override
	public String loadUserVoucherTypeListQry(String p_userId) {
		StringBuilder strBuff = new StringBuilder(" SELECT uv.voucher_type, vt.name ");
        strBuff.append(" FROM user_vouchertypes uv, voms_types vt, users u ");
        strBuff.append(" WHERE uv.user_id = ? AND uv.voucher_type = vt.voucher_type  AND u.user_id = uv.user_id ");
        return strBuff.toString();
	}
	
	@Override
	public String loadUserVoucherSegmentListQry() {
		StringBuilder strBuff = new StringBuilder(" SELECT us.VOUCHER_SEGMENT, lu.LOOKUP_NAME name FROM USER_VOUCHER_SEGMENTS us, ");
		strBuff.append(" LOOKUPS lu, users u WHERE us.user_id = ? AND us.VOUCHER_SEGMENT = lu.LOOKUP_CODE AND u.user_id = us.user_id AND us.status = ?");
        return strBuff.toString();
	}
	
	@Override
	public String loadUserVoucherTypeListForVoucherGenerationQry(String p_userId) {
		 StringBuilder strBuff = new StringBuilder("SELECT vt.VOUCHER_TYPE, vt.NAME, vt.SERVICE_TYPE_MAPPING, vt.STATUS, vt.CREATED_ON, vt.CREATED_BY,vt.MODIFIED_ON,vt.MODIFIED_BY, vt.TYPE ");
	        strBuff.append("FROM VOMS_TYPES vt, user_vouchertypes uv , users u ");
	        strBuff.append("WHERE uv.user_id = ? and  vt.STATUS<>'N' and uv.voucher_type = vt.voucher_type and  u.user_id = uv.user_id ");
	        return strBuff.toString();
	}
	@Override
	public String loadActiveProductDetailsListForUserQry(String pNetworkCode, String pStatus, String p_userId) {
		StringBuilder strBuff = new StringBuilder("");
        strBuff.append(" SELECT distinct vap.active_product_id, vap.applicable_from , vap.modified_on,vap.modified_by, vap.created_on, vap.created_by, vap.network_code, vap.status, vc.type, vc.voucher_type,vp.product_name ,vp.product_id,vt.type as voms_type ");
        strBuff.append(" FROM voms_active_products vap ,VOMS_PRODUCTS vp,VOMS_ACTIVE_PRODUCT_ITEMS vapi,VOMS_CATEGORIES vc , user_vouchertypes uv, users u,VOMS_TYPES vt WHERE  vap.active_product_id=vapi.active_product_id ");
        strBuff.append(" and  vapi.product_id=vp.product_id and vc.category_id= vp.category_id and  vap.network_code = ?  ");

        // if the date format in SystemPreferences done not contain
        // time then use this in query to truncate the time in query also
        if (!(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_DATE_FORMAT))).trim().contains(" ")) {
            strBuff.append(" AND trunc(vap.applicable_from) > trunc(?)");
            // if the date format in SystemPreference contains
            // time then use this in query to take care time also
        } else {
            strBuff.append(" AND vap.applicable_from >= ?");
        }
        strBuff.append(" AND vap.status = ? and uv.USER_ID= u.USER_ID and u.USER_ID= ? and uv.VOUCHER_TYPE = vc.VOUCHER_TYPE and vt.VOUCHER_TYPE = vc.VOUCHER_TYPE  order by vap.applicable_from ");        
        return strBuff.toString();
	}
	@Override
	public String loadProductListForActiveProductForUserQry(String pStatus, String pType, String pUserid, VomsActiveProductVO pVomsActiveProductVO) {
		StringBuilder strBuff = new StringBuilder(" SELECT VPI.active_product_id, VP.product_id, VP.product_name, VP.talktime,");
        strBuff.append(" VP.validity, VP.mrp, VC.category_id, VC.category_name , VC.voucher_type, VC.voucher_segment, vt.type as voms_type");
        strBuff.append(" FROM voms_active_product_items VPI,");
        strBuff.append(" voms_products VP, voms_categories VC, user_vouchertypes uv, users u, voms_types vt WHERE VPI.product_id=VP.product_id");
        strBuff.append(" AND VP.category_id=VC.category_id AND VPI.active_product_id = ? AND VP.status = ?");
		strBuff.append(" and uv.USER_ID= u.USER_ID  and u.USER_ID= ? and uv.VOUCHER_TYPE = vc.VOUCHER_TYPE and vt.voucher_type = vc.voucher_type");
		 if(! BTSLUtil.isNullString(pVomsActiveProductVO.getProductID()))
         {
			 strBuff.append("AND VP.product_id =? ");  
         }
          
          // Added by Anjali
          if (!BTSLUtil.isNullString(pType)) {
        	  strBuff.append("AND VC.type =? order by mrp");
          }
		return strBuff.toString();
	}
	

	@Override
	public String loadProductsListForUserQry(String userId){
	       StringBuilder strBuff = new StringBuilder("SELECT DISTINCT P.product_id PRODUCTID,P.product_name PRODUCTNAME, P.category_id CATEGORYID,CAT.category_name CATEGORYNAME,DOMAIN.category_name DOMAINNAME,");
	        strBuff.append(" P.short_name SHORTNAME, P.mrp MRP,P.description DESCRIPTION, P.product_code PRODUCTCODE, P.min_req_quantity MINQUANTITY,");
	        strBuff.append(" P.max_req_quantity MAXQUANTITY, P.multiple_factor MF,P.expiry_period EXPIRYPERIOD,P.individual_entity IND,");
	        strBuff.append(" nvl(P.individual_entity,'Y') entity, P.attribute1, P.created_by, P.created_on,P.modified_on, P.modified_by ");
	        strBuff.append(" FROM voms_products P,voms_categories CAT,voms_categories DOMAIN,voms_types T , user_vouchertypes uv, users u ");
	        strBuff.append(" WHERE CAT.category_id=P.category_id AND uv.USER_ID= u.USER_ID  AND u.USER_ID= ?  AND  uv.VOUCHER_TYPE = cat.VOUCHER_TYPE ");
	        strBuff.append(" AND P.category_id=DOMAIN.category_id AND T.voucher_type =CAT.voucher_type AND P.network_code = CAT.network_code AND P.network_code = ? ");
            strBuff.append(" AND (P.status IS NULL OR P.status <> 'N')");
	        strBuff.append(" ORDER BY  P.category_id,P.mrp");
	        return strBuff.toString();
	}
	
	
	@Override
	public String loadProductDetailsListForMrpQuery(String pStatusStr,boolean pUseALL, String pType, String networkCode,String voucher_segment,String mrp){
        StringBuilder strBuff = new StringBuilder(" SELECT VP.mrp,VP.product_id,VP.description,VP.talktime,VP.validity,VP.modified_on,VP.category_id,");
        strBuff.append(" VP.product_name,VP.short_name,VP.expiry_period,VP.created_by,VP.created_on,VP.modified_by,VP.service_code,");
        strBuff.append(" VP.max_req_quantity,VP.min_req_quantity,VC.category_name, LK.lookup_name, VP.status,VP.expiry_period,VC.VOUCHER_TYPE, VC.VOUCHER_SEGMENT ");
        strBuff.append("  FROM voms_products VP, voms_categories VC, lookups LK ");

        if (pUseALL) {
            strBuff.append(" WHERE VP.status = DECODE(" + pStatusStr + ", 'ALL', VP.status, " + pStatusStr + ")");
        } else {
            strBuff.append(" WHERE VP.status IN (" + pStatusStr + ")");
        }
        strBuff.append("  AND VC.VOUCHER_TYPE = ? ");
        strBuff.append(" AND LK.lookup_code = VP.status AND LK.lookup_type = ?");
        strBuff.append(" AND VC.category_id = VP.category_id ");
        if(!BTSLUtil.isNullString(voucher_segment))
        	strBuff.append("AND VC.voucher_segment = ? ");
        if(!BTSLUtil.isNullString(networkCode))
        	strBuff.append(" AND VC.network_code = ?");
        if (!BTSLUtil.isNullString(pType)) {
            strBuff.append("AND VC.type =?");
        }
        strBuff.append("  AND VC.mrp = ? ");
        strBuff.append("ORDER BY VP.product_id ");
        return strBuff.toString();
	}
	@Override
	public String loadProductDetailsListForMrpQuery(String pStatusStr,boolean pUseALL,  String networkCode){
        StringBuilder strBuff = new StringBuilder(" SELECT VP.mrp,VP.product_id,VP.description,VP.talktime,VP.validity,VP.modified_on,VP.category_id,");
        strBuff.append(" VP.product_name,VP.short_name,VP.expiry_period,VP.created_by,VP.created_on,VP.modified_by,VP.service_code,");
        strBuff.append(" VP.max_req_quantity,VP.min_req_quantity,VC.category_name, LK.lookup_name, VP.status,VP.expiry_period,VC.VOUCHER_TYPE, VC.VOUCHER_SEGMENT ");
        strBuff.append("  FROM voms_products VP, voms_categories VC, lookups LK ");

        if (pUseALL) {
            strBuff.append(" WHERE VP.status = DECODE(" + pStatusStr + ", 'ALL', VP.status, " + pStatusStr + ")");
        } else {
            strBuff.append(" WHERE VP.status IN (" + pStatusStr + ")");
        }
        strBuff.append(" AND LK.lookup_code = VP.status AND LK.lookup_type = ?");
        strBuff.append(" AND VC.category_id = VP.category_id ");
        if(!BTSLUtil.isNullString(networkCode))
        	strBuff.append(" AND VC.network_code = ?");
     
        strBuff.append("ORDER BY VP.product_id ");
        return strBuff.toString();
	}

}
