package com.btsl.db.query.postgres;

import com.btsl.util.BTSLUtil;
import com.web.voms.voucherbundle.businesslogic.VoucherBundleWebQry;

public class VoucherBundleWebPostgresQry implements VoucherBundleWebQry {

	@Override
	public String loadSubCategoryListForView(boolean isSubCategory, String segment) {
		StringBuilder strBuff = new StringBuilder(
				" SELECT VC.category_name, VC.category_id,VC.description,VC.category_type,VC.category_short_name,VC.mrp,VC.status,VC.global,");
		strBuff.append(
				" L.lookup_name, VC.payable_amount ,VC.type, VC.voucher_Type,vvsm.sub_service,vvsm.SERVICE_TYPE, st.NAME ");
		strBuff.append(" FROM voms_categories VC, lookups L, lookup_types LT,voms_vtype_service_mapping vvsm, SERVICE_TYPE st WHERE  ");
		if (isSubCategory) {
			 strBuff.append(" VC.voucher_type = CASE WHEN ? = ? then VC.voucher_type ELSE ? END AND ");
		}

		strBuff.append(
				" VC.network_code = ? AND (VC.status = ? OR VC.status IS NULL) AND L.lookup_code=VC.status "
						+ "AND L.lookup_type=LT.lookup_type AND L.lookup_type=? ");
		if (!BTSLUtil.isNullString(segment)) {
			strBuff.append(" AND VC.voucher_segment = ? ");
		}
		strBuff.append(" and vc.service_id=vvsm.service_id and vvsm.service_type=st.service_type ORDER BY mrp ");

		return strBuff.toString();
	}

	@Override
	public String loadUserCategoryList(String p_userid) {
		StringBuilder strBuff = new StringBuilder(" SELECT   vt.voucher_type, vt.NAME, vt.status ");
		strBuff.append(" FROM voms_types vt, voms_vtype_service_mapping vvsm, user_vouchertypes uv ");
		strBuff.append(
				" WHERE vvsm.voucher_type = vt.voucher_type AND vt.status = 'Y' AND uv.user_id = ? AND uv.voucher_type = vt.voucher_type AND uv.status = 'Y' GROUP BY vt.voucher_type, vt.NAME, vt.status  ");
		return strBuff.toString();
	}

}
