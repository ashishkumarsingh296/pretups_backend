package com.btsl.db.query.oracle;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.web.pretups.channel.profile.businesslogic.CommissionProfileWebQry;

public class CommissionProfileWebOracleQry implements CommissionProfileWebQry{
	private Log LOG = LogFactory.getLog(CommissionProfileWebOracleQry.class.getName());
	@Override
	public String loadCommissionProfileListQry() {
		String methodName = "loadCommissionProfileListQry";
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("Select cps.comm_profile_set_id,nt.network_name,c.category_name,cps.comm_profile_set_name,cps.short_code,");
        strBuff.append(" cpsv.comm_profile_set_version, cpsv.applicable_from from");
        strBuff.append(" COMMISSION_PROFILE_SET cps, COMMISSION_PROFILE_SET_VERSION cpsv, networks nt, categories c");
        strBuff.append(" WHERE cps.network_code = nt.network_code AND c.domain_code=?");
        strBuff.append(" AND cps.category_code = DECODE(?, 'ALL', cps.category_code, ?)");
        strBuff.append(" AND cps.category_code = c.category_code");
        strBuff.append(" AND cps.comm_profile_set_id = cpsv.comm_profile_set_id");
        strBuff.append(" AND trunc(applicable_from) between ? AND ?");
        strBuff.append("order by c.sequence_no,cps.comm_profile_set_id,cpsv.applicable_from");
        LogFactory.printLog(methodName, strBuff.toString(), LOG);
		return strBuff.toString();
	}

	@Override
	public String loadCommissionProfileSetVersionQry() {
		String methodName = "loadCommissionProfileSetVersionQry";
	    final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cv.comm_profile_set_id,cv.comm_profile_set_version,cv.dual_comm_type,cv.applicable_from ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
        strBuff.append(" ,cv.oth_comm_prf_set_id,ocpf.oth_comm_prf_type,ocpf.oth_comm_prf_type_value,ocpf.OTH_COMM_PRF_SET_NAME ");
        }
        strBuff.append(" FROM commission_profile_set cs,commission_profile_set_version cv ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
        strBuff.append(" ,other_comm_prf_set ocpf ");
        }
        strBuff.append(" WHERE cs.network_code = ? ");
        strBuff.append(" AND cs.category_code = ? AND cs.comm_profile_set_id = cv.comm_profile_set_id AND ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue())
        {
        	   strBuff.append(" cv.oth_comm_prf_set_id = ocpf.oth_comm_prf_set_id(+) AND ");
        }
		strBuff.append(" (cv.applicable_from >= ? OR cv.applicable_from =(SELECT MAX(cv2.applicable_from) ");
        strBuff.append(" from commission_profile_set_version cv2 WHERE cs.comm_profile_set_id = cv2.comm_profile_set_id and cv2.applicable_from <= ? )) ");
        strBuff.append(" AND cs.status <> 'N' ORDER BY comm_profile_set_version");

        final String sqlSelect = strBuff.toString();

        LogFactory.printLog(methodName, strBuff.toString(), LOG);
		return strBuff.toString();
	}
	
	@Override
	public String loadCommissionProfileSetVersionQry1(String geoCode, String gradeCode,String status) {
		String methodName = "loadCommissionProfileSetVersionQry1";
	    final StringBuilder strBuff = new StringBuilder();
	    
	    strBuff.append(" WITH temp AS (SELECT cv.comm_profile_set_id,cv.comm_profile_set_version,cv.dual_comm_type,cv.applicable_from,cs.geography_code,cs.comm_profile_set_name,cs.status,cs.created_on,cs.is_default,c.services_allowed ,c.category_name");
        strBuff.append(" FROM commission_profile_set cs JOIN commission_profile_set_version cv ON cs.comm_profile_set_id = cv.comm_profile_set_id  JOIN categories c ON cs.category_code = c.category_code");
        strBuff.append(" WHERE cs.network_code = ? ");
        strBuff.append(" AND cs.category_code = ? ");
        if(!geoCode.equals(PretupsI.ALL)) 
        {
        strBuff.append(" AND cs.geography_code = ? ");
        }
        if(!gradeCode.equals(PretupsI.ALL)) 
        {
        strBuff.append(" AND cs.grade_code = ? ");
        }
        
        if(!status.equals(PretupsI.ALL)) {
            strBuff.append(" AND cs.status = ? ");
        }
        else {
           	strBuff.append(" AND cs.status <> 'N'");
         }
        strBuff.append(" )");
        //It will fetch all rows having applicable_from less than current_date if at lease one such row exist. Otherwise it will show future applicable from rows.
        strBuff.append("SELECT t.* FROM  temp t ");
		strBuff.append(
				" JOIN (SELECT comm_profile_set_id,comm_profile_set_version,ROW_NUMBER() OVER (PARTITION BY comm_profile_set_id ORDER BY CASE WHEN MAX(TO_TIMESTAMP(applicable_from,'YYYY-MM-DD HH24:MI:SS')) <= ?  \n" +
                        "                     THEN -(CAST(applicable_from AS DATE) - TO_DATE('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) ELSE (CAST(applicable_from AS DATE) - TO_DATE('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) END) AS rank ");
		strBuff.append(
				" FROM temp GROUP BY comm_profile_set_id,applicable_from,comm_profile_set_version ) subq ON t.comm_profile_set_id = subq.comm_profile_set_id AND t.comm_profile_set_version = subq.comm_profile_set_version WHERE subq.rank = 1 ");
		strBuff.append(" ORDER BY t.comm_profile_set_version");
        
		/*
		 * strBuff.append(" OR cv.applicable_from >(SELECT MAX(cv2.applicable_from) ");
		 * strBuff.
		 * append(" from commission_profile_set_version cv2 WHERE cs.comm_profile_set_id = cv2.comm_profile_set_id and cv2.applicable_from <= ? ) )"
		 * );
		 */
 
        final String sqlSelect = strBuff.toString();

        LogFactory.printLog(methodName, strBuff.toString(), LOG);
		return strBuff.toString();
		
		    
	}


    @Override
	public String loadCommissionProfileSetVersionQryTwo() {
		String methodName = "loadCommissionProfileSetVersionQryTwo";
	    final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cv.comm_profile_set_id,cv.comm_profile_set_version,cv.dual_comm_type,cv.applicable_from ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
        strBuff.append(" ,cv.oth_comm_prf_set_id,ocpf.oth_comm_prf_type,ocpf.oth_comm_prf_type_value,ocpf.OTH_COMM_PRF_SET_NAME ");
        }
        strBuff.append(" FROM commission_profile_set cs,commission_profile_set_version cv ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
        strBuff.append(" ,other_comm_prf_set ocpf ");
        }
        strBuff.append(" WHERE cs.network_code = ? ");
        strBuff.append(" AND cs.category_code = ? AND cs.comm_profile_set_id = ? AND cs.comm_profile_set_id = cv.comm_profile_set_id AND ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue())
        {
        	   strBuff.append(" cv.oth_comm_prf_set_id = ocpf.oth_comm_prf_set_id(+) AND ");
        }
		strBuff.append(" (cv.applicable_from >= ? OR cv.applicable_from =(SELECT MAX(cv2.applicable_from) ");
        strBuff.append(" from commission_profile_set_version cv2 WHERE cs.comm_profile_set_id = cv2.comm_profile_set_id and cv2.applicable_from <= ? )) ");
        strBuff.append(" AND cs.status <> 'N' ORDER BY comm_profile_set_version");

        final String sqlSelect = strBuff.toString();

        LogFactory.printLog(methodName, strBuff.toString(), LOG);
		return strBuff.toString();
	}

	@Override
	public String loadCommissionProfileSetVersionQryForViewDetail() {
		
		String methodName = "loadCommissionProfileSetVersionQryForViewDetail";
	    final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT cv.comm_profile_set_id,cv.comm_profile_set_version,cv.dual_comm_type,cv.applicable_from ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
        strBuff.append(" ,cv.oth_comm_prf_set_id,ocpf.oth_comm_prf_type,ocpf.oth_comm_prf_type_value,ocpf.OTH_COMM_PRF_SET_NAME ");
        }
        strBuff.append(" FROM commission_profile_set cs,commission_profile_set_version cv ");
        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
        		 strBuff.append(" left outer join  other_comm_prf_set ocpf on cv.oth_comm_prf_set_id = ocpf.oth_comm_prf_set_id ");
        }
        strBuff.append(" WHERE cs.network_code = ? ");
        strBuff.append(" AND cs.category_code = ? AND cs.comm_profile_set_id = cv.comm_profile_set_id ");
        strBuff.append(" AND cv.comm_profile_set_id= ? AND cv.comm_profile_set_version= ?");
        strBuff.append(" AND cs.status <> 'N'");

        final String sqlSelect = strBuff.toString();

        LogFactory.printLog(methodName, strBuff.toString(), LOG);
		return strBuff.toString();
	}

		
}
	
	

