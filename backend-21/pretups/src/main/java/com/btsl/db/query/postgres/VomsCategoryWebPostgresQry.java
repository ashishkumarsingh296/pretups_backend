package com.btsl.db.query.postgres;

import com.btsl.util.BTSLUtil;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebQry;


public class VomsCategoryWebPostgresQry implements VomsCategoryWebQry{
	
	@Override
	public String loadSubCategoryListForView(boolean isSubCategory, String segment){
		  StringBuilder strBuff = new StringBuilder(
		            " SELECT VC.category_name, VC.category_id,VC.description,VC.category_type,VC.category_short_name,VC.mrp,VC.status,VC.global,");
		        strBuff.append(" L.lookup_name, VC.payable_amount ,VC.type, VC.voucher_Type,vvsm.sub_service,vvsm.SERVICE_TYPE, st.NAME ");
		        strBuff.append(" FROM voms_categories VC, lookups L, lookup_types LT,voms_vtype_service_mapping vvsm, SERVICE_TYPE st WHERE  ");
		        if (isSubCategory) {
		            strBuff.append("  VC.voucher_type=case when ?=? then VC.voucher_type else ? end AND ");
		        }
		        strBuff.append(" VC.network_code = ? AND category_type = ? AND (VC.status = ? OR VC.status IS NULL) AND "
		        		+ "  L.lookup_code=VC.status AND L.lookup_type=LT.lookup_type AND L.lookup_type=? ");
		        if(!BTSLUtil.isNullString(segment)) {
		        	strBuff.append(" AND VC.voucher_segment = ? ");
		        }
		        strBuff.append(" and vc.service_id=vvsm.service_id and vvsm.service_type=st.service_type ORDER BY mrp ");
		        return strBuff.toString();
		}

	@Override
	public String loadUserCategoryList(String p_userid,String []vouchersType) {
		StringBuilder strBuff = new StringBuilder(" SELECT   vt.voucher_type, vt.NAME, vt.status, vt.type ");
		strBuff.append(" FROM voms_types vt, voms_vtype_service_mapping vvsm, user_vouchertypes uv ");
		strBuff.append(
				" WHERE vvsm.voucher_type = vt.voucher_type AND vt.status = 'Y' AND uv.user_id = ? AND uv.voucher_type = vt.voucher_type AND uv.status = 'Y'");
			if(!(BTSLUtil.isNullArray(vouchersType)))
			{
				strBuff.append(" and vt.type in (");
				for (int i = 0; i < vouchersType.length; i++) {
					strBuff.append(" ?");
					if (i != vouchersType.length - 1) {
						strBuff.append(",");
					}
				}
				strBuff.append(")");
			}
		strBuff.append(" GROUP BY vt.voucher_type, vt.NAME, vt.status, vt.type  ");
		return strBuff.toString();
	}

	@Override
	public String loadCategoryListForUser(String p_status, String p_userId) {
		StringBuilder strBuff = new StringBuilder(" SELECT vc.category_id,vc.description,vc.category_type,vc.category_short_name,vc.mrp,vc.status,");
        strBuff
            .append(" vc.category_name,vc.payable_amount,vvsm.sub_service typ,vc.voucher_type,vvsm.service_type,vvsm.sub_service, vc.voucher_segment, vt.TYPE voucher_type_code FROM voms_categories vc,voms_vtype_service_mapping vvsm , user_vouchertypes uv, users u, voms_types vt");
        strBuff.append(" WHERE vc.service_id=vvsm.service_id AND (vc.status = ? OR vc.status IS NULL)   and uv.USER_ID= u.USER_ID and u.USER_ID= ? and uv.VOUCHER_TYPE = vvsm.VOUCHER_TYPE and vc.VOUCHER_TYPE = vt.VOUCHER_TYPE and vc.network_code=? ORDER BY mrp");
		return strBuff.toString();
	}
	}
