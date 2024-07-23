package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.AdditionalCommissionDetailsReportQry;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;

public class AdditionalCommissionDetailsReportOracleQry implements AdditionalCommissionDetailsReportQry {


	private Log log = LogFactory.getLog(this.getClass().getName());
	private static final String FORMAT = "'9999999999999999999999D99'"; 
	private static final String ADJ_MARGIN_AMOUNT = "ADJ.margin_amount";
	private static final String CST_TRANSFER_VALUE = "CST.transfer_value";
	
	@Override
	public PreparedStatement loadAdditionalCommisionDetailsOperatorQry(Connection con, UsersReportModel usersReportModel){

		String methodName = "loadAdditionalCommisionDetailsOperatorQry";
		if(log.isDebugEnabled()){ 
			log.debug(methodName,"Entered loginUserID: "+usersReportModel.getLoginUserID()+" NetworkCode: "+usersReportModel.getNetworkCode()+" DomainCode: "+usersReportModel.getDomainCode()+
					" ParentCatCode: "+usersReportModel.getParentCategoryCode()+" FromDate: "+usersReportModel.getRptfromDate()+" ToDate: "+usersReportModel.getRpttoDate()+
					" UserID: "+usersReportModel.getUserID()+" ZoneCode: "+usersReportModel.getZoneCode());
	     } 
		
		StringBuilder selectQueryBuff = new StringBuilder();
		//local index implemented
		selectQueryBuff.append(" SELECT CST.transfer_id,ADJ.adjustment_id,TO_CHAR(ADJ.created_on,?) created_on, U.user_id,U.user_name,  ");
		selectQueryBuff.append("U.msisdn,C.category_name,GD.grph_domain_name, PU.user_id,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name, ");  
		selectQueryBuff.append("(CASE  WHEN U.parent_id = 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PC.category_name END) parent_cat, ");
		selectQueryBuff.append("(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PGD.grph_domain_name END) parent_geo_name, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_user, ");
		selectQueryBuff.append("(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OC.category_name END) owner_cat, ");
		selectQueryBuff.append("(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OGD.grph_domain_name END)  owner_geo, ");
		selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, ADJ_MARGIN_AMOUNT, "margin_amount").append(" , ");
		selectQueryBuff.append("TO_CHAR(ADJ.margin_rate").append(",").append(FORMAT).append(" ) as margin_rate, ADJ.otf_type,TO_NUMBER( ADJ.otf_rate ) otf_rate, TO_NUMBER(ADJ.otf_amount,").append(FORMAT).append(" ) as otf_amount, ");
		selectQueryBuff.append("CST.differential_applicable App_Add_Comm,ST.NAME,CST.receiver_msisdn, ");
		selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CST_TRANSFER_VALUE, "transfer_value").append(" , ");
		selectQueryBuff.append("GU.user_id,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GU.user_name END) grand_name, (CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN '' ELSE GU.msisdn END) grand_msisdn, ");
		selectQueryBuff.append("(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GGD.grph_domain_name END) grand_geo_domain, (CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GC.category_name END) grand_category, ");
		selectQueryBuff.append("TO_CHAR(ADJ.created_on, ?) as time,(CASE WHEN ADJ.commission_type='DIFF' THEN 'DIFFERENTIAL' ELSE ADJ.commission_type END) AS Commission_type ");
		selectQueryBuff.append("FROM ADJUSTMENTS ADJ, USERS U, C2S_TRANSFERS CST, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD,CATEGORIES C, USERS PU, USERS OU, CATEGORIES PC, CATEGORIES OC, USER_GEOGRAPHIES PUG, GEOGRAPHICAL_DOMAINS PGD, USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,SERVICE_TYPE ST,USERS GU, USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD ");
		selectQueryBuff.append("WHERE CST.TRANSFER_DATE  >= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND CST.TRANSFER_DATE  <= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.ADJUSTMENT_DATE  >= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.ADJUSTMENT_DATE  <= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.created_on >= TO_DATE(?,'dd/mm/yy HH24:MI:SS') ");
		selectQueryBuff.append("AND ADJ.created_on <= TO_DATE(?,'dd/mm/yy HH24:MI:SS') AND ADJ.network_code=? AND ADJ.user_id =  CASE ?  WHEN 'ALL' THEN ADJ.user_id ELSE  ? END ");
		selectQueryBuff.append("AND C.category_code = CASE ?  WHEN 'ALL' THEN C.category_code ELSE ? END AND ADJ.user_category = C.category_code AND C.domain_code IN(?) AND ADJ.user_id=U.user_id ");
		selectQueryBuff.append("AND CST.transfer_id=ADJ.reference_id AND UG.grph_domain_code = GD.grph_domain_code AND UG.user_id=U.user_id AND PUG.grph_domain_code = PGD.grph_domain_code AND PUG.user_id=PU.user_id AND PU.user_id=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END AND PU.category_code=PC.category_code AND OUG.grph_domain_code = OGD.grph_domain_code ");
		selectQueryBuff.append("AND OUG.user_id=OU.user_id AND OU.user_id=U.owner_id AND OU.category_code=OC.category_code AND GUG.grph_domain_code = GGD.grph_domain_code AND GUG.user_id=GU.user_id AND GU.user_id=CASE PU.parent_id WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END AND GU.category_code=GC.category_code AND ADJ.SERVICE_TYPE=ST.SERVICE_TYPE AND CST.REVERSAL_ID is null AND TXN_TYPE='T' ");
		selectQueryBuff.append("AND UG.grph_domain_code IN ( SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN (SELECT grph_domain_code  ");
		selectQueryBuff.append("FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END AND UG1.user_id=?)) ");
		selectQueryBuff.append(" GROUP BY CST.transfer_id, ADJ.adjustment_id,U.user_name, U.msisdn, C.category_name, GD.grph_domain_name, PU.user_id, U.parent_id, PU.user_name, PU.msisdn, PC.category_name, PGD.grph_domain_name, U.user_id, OU.user_name, OU.user_id, OU.msisdn, OC.category_name, OGD.grph_domain_name, ADJ.margin_rate, ADJ.otf_type, ADJ.otf_rate, ADJ.otf_amount, CST.differential_applicable,  ST.NAME, CST.receiver_msisdn, GU.user_id, PU.parent_id,GU.user_name, GU.msisdn, GGD.grph_domain_name, GC.category_name, ADJ.created_on, ADJ.commission_type ");
		selectQueryBuff.append("ORDER BY 2 desc ");
		String sqlSelect = selectQueryBuff.toString();

		if(log.isDebugEnabled()){
			log.debug(methodName, "SelectQuery: "+sqlSelect);
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			int i = 1;
			pstmt.setString(i++,Constants.getProperty("report.onlytimeformat"));
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
			pstmt.setString(i++,usersReportModel.getRptfromDate());
			pstmt.setString(i++,usersReportModel.getRpttoDate());
			pstmt.setString(i++,usersReportModel.getRptfromDate());
			pstmt.setString(i++,usersReportModel.getRpttoDate());
			pstmt.setString(i++, usersReportModel.getRptfromDate());
			pstmt.setString(i++,usersReportModel.getRpttoDate());				
			pstmt.setString(i++, usersReportModel.getNetworkCode());
			pstmt.setString(i++, usersReportModel.getUserID());
			pstmt.setString(i++, usersReportModel.getUserID());
			pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			pstmt.setString(i++,usersReportModel.getDomainCode());
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i++,usersReportModel.getLoginUserID());

		} catch (SQLException e) {
			log.error(methodName, "SQLException: "+e.getMessage());
			log.errorTrace(methodName, e);
		} catch (Exception e) {
			log.error(methodName, "Exception: "+e.getMessage());
			log.errorTrace(methodName, e);
		}


		return pstmt;
	}

	@Override
	public PreparedStatement loadAdditionalCommisionDetailsOperatorOldQry(
			Connection con, UsersReportModel usersReportModel) {

		String methodName = "loadAdditionalCommisionDetailsOperatorOldQry";
		
		if(log.isDebugEnabled()){ 
			log.debug(methodName,"Entered loginUserID: "+usersReportModel.getLoginUserID()+" NetworkCode: "+usersReportModel.getNetworkCode()+" DomainCode: "+usersReportModel.getDomainCode()+
					" ParentCatCode: "+usersReportModel.getParentCategoryCode()+" FromDate: "+usersReportModel.getRptfromDate()+" ToDate: "+usersReportModel.getRpttoDate()+
					" UserID: "+usersReportModel.getUserID()+" ZoneCode: "+usersReportModel.getZoneCode());
	     } 
		
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append("SELECT CST.transfer_id,TO_CHAR(ADJ.created_on, ?) created_on, U.user_id,U.user_name, U.msisdn,C.category_name,GD.grph_domain_name, ");
		selectQueryBuff.append("PU.user_id,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name,(CASE  WHEN U.parent_id = 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PC.category_name END) parent_cat, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PGD.grph_domain_name END)  ");
		selectQueryBuff.append("parent_geo_name, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_user,(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OC.category_name END) owner_cat,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OGD.grph_domain_name END)  owner_geo, ");
		selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, ADJ_MARGIN_AMOUNT, "margin_amount").append(" , ");
		selectQueryBuff.append("TO_CHAR(ADJ.margin_rate").append(",").append(FORMAT).append(" ) as margin_rate,  CST.differential_applicable App_Add_Comm,ST.NAME,CST.receiver_msisdn, ");
		selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CST_TRANSFER_VALUE, "transfer_value").append(" , ");
		selectQueryBuff.append("GU.user_id,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GU.user_name END) grand_name,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN '' ELSE GU.msisdn END) grand_msisdn,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GGD.grph_domain_name END) grand_geo_domain,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GC.category_name END) ");
		selectQueryBuff.append("grand_category, TO_CHAR(ADJ.created_on, ?)time,(CASE WHEN ADJ.commission_type='DIFF' THEN 'DIFFERENTIAL' ELSE ADJ.commission_type END) AS Commission_type ");
		selectQueryBuff.append("FROM ADJUSTMENTS ADJ, USERS U, C2S_TRANSFERS_OLD CST, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD,CATEGORIES C, USERS PU, USERS OU, CATEGORIES PC, CATEGORIES OC, USER_GEOGRAPHIES PUG, GEOGRAPHICAL_DOMAINS PGD, USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,SERVICE_TYPE ST,USERS GU, USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD ");
		selectQueryBuff.append("WHERE ADJ.ADJUSTMENT_DATE  >= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.ADJUSTMENT_DATE  <= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.created_on >= TO_DATE(?,'dd/mm/yy HH24:MI:SS') ");
		selectQueryBuff.append("AND ADJ.created_on <= TO_DATE(?,'dd/mm/yy HH24:MI:SS') AND ADJ.network_code=? AND ADJ.user_id =  CASE ?  WHEN 'ALL' THEN ADJ.user_id ELSE  ? END AND C.category_code = CASE ?  WHEN 'ALL' THEN C.category_code ELSE  ? END AND ADJ.user_category = C.category_code ");
		selectQueryBuff.append("AND C.domain_code IN(?) AND ADJ.user_id=U.user_id AND CST.transfer_id=ADJ.reference_id AND UG.grph_domain_code = GD.grph_domain_code AND UG.user_id=U.user_id AND PUG.grph_domain_code = PGD.grph_domain_code AND PUG.user_id=PU.user_id AND PU.user_id=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END AND PU.category_code=PC.category_code ");
		selectQueryBuff.append("AND OUG.grph_domain_code = OGD.grph_domain_code AND OUG.user_id=OU.user_id AND OU.user_id=U.owner_id AND OU.category_code=OC.category_code AND GUG.grph_domain_code = GGD.grph_domain_code AND GUG.user_id=GU.user_id AND GU.user_id=CASE PU.parent_id WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END AND GU.category_code=GC.category_code AND ADJ.SERVICE_TYPE=ST.SERVICE_TYPE AND UG.grph_domain_code IN ( ");
		selectQueryBuff.append("SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN ");
		selectQueryBuff.append("(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END ");
		selectQueryBuff.append("AND UG1.user_id=?)) ");
		selectQueryBuff.append(" GROUP BY CST.transfer_id, ADJ.adjustment_id,U.user_name, U.msisdn, C.category_name, GD.grph_domain_name, PU.user_id, U.parent_id, PU.user_name, PU.msisdn, PC.category_name, PGD.grph_domain_name, U.user_id, OU.user_name, OU.user_id, OU.msisdn, OC.category_name, OGD.grph_domain_name, ADJ.margin_rate, ADJ.otf_type, ADJ.otf_rate, ADJ.otf_amount, CST.differential_applicable,  ST.NAME, CST.receiver_msisdn, GU.user_id, PU.parent_id,GU.user_name, GU.msisdn, GGD.grph_domain_name, GC.category_name, ADJ.created_on, ADJ.commission_type ");
		selectQueryBuff.append("ORDER BY 2 desc");
		String sqlSelect = selectQueryBuff.toString();

		if(log.isDebugEnabled()){
			log.debug(methodName, "SelectQuery: "+sqlSelect);
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			int i = 1;
			pstmt.setString(i++,Constants.getProperty("report.onlytimeformat"));
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
			pstmt.setString(i++,usersReportModel.getRptfromDate());
			pstmt.setString(i++,usersReportModel.getRpttoDate());
			pstmt.setString(i++, usersReportModel.getRptfromDate());
			pstmt.setString(i++,usersReportModel.getRpttoDate());				
			pstmt.setString(i++, usersReportModel.getNetworkCode());
			pstmt.setString(i++, usersReportModel.getUserID());
			pstmt.setString(i++, usersReportModel.getUserID());
			pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			pstmt.setString(i++,usersReportModel.getDomainCode());
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i++,usersReportModel.getLoginUserID());

		} catch (SQLException e) {
			log.error(methodName, "SQLException: "+e.getMessage());
			log.errorTrace(methodName, e);
		} catch (Exception e) {
			log.error(methodName, "Exception: "+e.getMessage());
			log.errorTrace(methodName, e);
		} 


		return pstmt;
	}

	@Override
	public PreparedStatement loadAdditionalCommisionDetailsChannelQry(
			Connection con, UsersReportModel usersReportModel) {

		String methodName = "loadAdditionalCommisionDetailsChannelQry";
		
		if(log.isDebugEnabled()){ 
			log.debug(methodName,"Entered loginUserID: "+usersReportModel.getLoginUserID()+" NetworkCode: "+usersReportModel.getNetworkCode()+" DomainCode: "+usersReportModel.getDomainCode()+
					" ParentCatCode: "+usersReportModel.getParentCategoryCode()+" FromDate: "+usersReportModel.getRptfromDate()+" ToDate: "+usersReportModel.getRpttoDate()+
					" UserID: "+usersReportModel.getUserID()+" ZoneCode: "+usersReportModel.getZoneCode());
	     } 
		
		StringBuilder selectQueryBuff = new StringBuilder();
		//local index implemented
		selectQueryBuff.append("SELECT ADJ.adjustment_id, U.user_id,U.user_name, U.msisdn,C.category_name,GD.grph_domain_name, ");
		selectQueryBuff.append("PU.user_id,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name,(CASE  WHEN U.parent_id = 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PC.category_name END) parent_cat, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PGD.grph_domain_name END) ");
		selectQueryBuff.append("parent_geo_name, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_user,(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OC.category_name END) owner_cat,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OGD.grph_domain_name END)  owner_geo, ");
		selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, ADJ_MARGIN_AMOUNT, "margin_amount").append(" , ");
		selectQueryBuff.append("TO_CHAR(ADJ.margin_rate").append(",").append(FORMAT).append(" ) as margin_rate,  CST.differential_applicable App_Add_Comm,ST.NAME,CST.receiver_msisdn, ");
		selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CST_TRANSFER_VALUE, "transfer_value").append(" , ");
		selectQueryBuff.append("GU.user_id,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GU.user_name END) grand_name,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN '' ELSE GU.msisdn END) grand_msisdn,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GGD.grph_domain_name END) grand_geo_domain,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GC.category_name END) ");
		selectQueryBuff.append("grand_category, TO_CHAR(ADJ.created_on, ?)time ");
		selectQueryBuff.append("FROM ADJUSTMENTS ADJ, USERS U, C2S_TRANSFERS CST, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD,CATEGORIES C, USERS PU, USERS OU, CATEGORIES PC, CATEGORIES OC, USER_GEOGRAPHIES PUG, GEOGRAPHICAL_DOMAINS PGD, USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,SERVICE_TYPE ST,USERS GU, USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD ");
		selectQueryBuff.append("WHERE CST.TRANSFER_DATE  >= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND CST.TRANSFER_DATE  <= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.ADJUSTMENT_DATE  >= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.ADJUSTMENT_DATE  <= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.created_on >= TO_DATE(?,'dd/mm/yy HH24:MI:SS') AND ADJ.created_on <= TO_DATE(?,'dd/mm/yy HH24:MI:SS') AND ADJ.network_code=? ");
		selectQueryBuff.append("AND ADJ.user_id =  CASE ?  WHEN 'ALL' THEN ADJ.user_id ELSE  ? END AND C.category_code = CASE ?  WHEN 'ALL' THEN C.category_code ELSE  ? END AND ADJ.user_category = C.category_code AND C.domain_code IN(?) AND ADJ.user_id=U.user_id AND CST.transfer_id=SUBSTR(ADJ.adjustment_id, 1,LENGTH(ADJ.adjustment_id)-1) ");
		selectQueryBuff.append("AND UG.grph_domain_code = GD.grph_domain_code AND UG.user_id=U.user_id  AND PUG.grph_domain_code = PGD.grph_domain_code AND PUG.user_id=PU.user_id AND PU.user_id=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END AND PU.category_code=PC.category_code AND OUG.grph_domain_code = OGD.grph_domain_code AND OUG.user_id=OU.user_id AND OU.user_id=U.owner_id AND OU.category_code=OC.category_code ");
		selectQueryBuff.append("AND GUG.grph_domain_code = GGD.grph_domain_code AND GUG.user_id=GU.user_id AND GU.user_id=CASE PU.parent_id WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END AND GU.category_code=GC.category_code AND ADJ.SERVICE_TYPE=ST.SERVICE_TYPE AND CST.REVERSAL_ID is null AND TXN_TYPE='T' AND UG.grph_domain_code IN ( ");
		selectQueryBuff.append("SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN ");
		selectQueryBuff.append("(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END AND UG1.user_id=?)) ");
		selectQueryBuff.append(" GROUP BY CST.transfer_id, ADJ.adjustment_id,U.user_name, U.msisdn, C.category_name, GD.grph_domain_name, PU.user_id, U.parent_id, PU.user_name, PU.msisdn, PC.category_name, PGD.grph_domain_name, U.user_id, OU.user_name, OU.user_id, OU.msisdn, OC.category_name, OGD.grph_domain_name, ADJ.margin_rate, ADJ.otf_type, ADJ.otf_rate, ADJ.otf_amount, CST.differential_applicable,  ST.NAME, CST.receiver_msisdn, GU.user_id, PU.parent_id,GU.user_name, GU.msisdn, GGD.grph_domain_name, GC.category_name, ADJ.created_on, ADJ.commission_type ");
		selectQueryBuff.append("ORDER BY 27 Desc");
		String sqlSelect = selectQueryBuff.toString();

		if(log.isDebugEnabled()){
			log.debug(methodName, "SelectQuery: "+sqlSelect);
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			int i = 1;
			pstmt.setString(i++,Constants.getProperty("report.onlytimeformat"));
			pstmt.setString(i++,usersReportModel.getRptfromDate());
			pstmt.setString(i++,usersReportModel.getRpttoDate());
			pstmt.setString(i++,usersReportModel.getRptfromDate());
			pstmt.setString(i++,usersReportModel.getRpttoDate());
			pstmt.setString(i++, usersReportModel.getRptfromDate());
			pstmt.setString(i++,usersReportModel.getRpttoDate());				
			pstmt.setString(i++, usersReportModel.getNetworkCode());
			pstmt.setString(i++, usersReportModel.getUserID());
			pstmt.setString(i++, usersReportModel.getUserID());
			pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			pstmt.setString(i++,usersReportModel.getDomainCode());
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i++,usersReportModel.getLoginUserID());

		} catch (SQLException e) {
			log.error(methodName, "SQLException: "+e.getMessage());
			log.errorTrace(methodName, e);
		} catch (Exception e) {
			log.error(methodName, "Exception: "+e.getMessage());
			log.errorTrace(methodName, e);
		}


		return pstmt;
	}
	
	@Override
	public PreparedStatement loadAdditionalCommisionDetailsChannelOldQry(
			Connection con, UsersReportModel usersReportModel) {

		String methodName = "loadAdditionalCommisionDetailsChannelOldQry";
		
		if(log.isDebugEnabled()){ 
			log.debug(methodName,"Entered loginUserID: "+usersReportModel.getLoginUserID()+" NetworkCode: "+usersReportModel.getNetworkCode()+" DomainCode: "+usersReportModel.getDomainCode()+
					" ParentCatCode: "+usersReportModel.getParentCategoryCode()+" FromDate: "+usersReportModel.getRptfromDate()+" ToDate: "+usersReportModel.getRpttoDate()+
					" UserID: "+usersReportModel.getUserID()+" ZoneCode: "+usersReportModel.getZoneCode());
		} 
		
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append("SELECT ADJ.adjustment_id, U.user_id,U.user_name, U.msisdn,C.category_name,GD.grph_domain_name, ");
		selectQueryBuff.append("PU.user_id,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name,(CASE  WHEN U.parent_id = 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PC.category_name END) parent_cat, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PGD.grph_domain_name END) ");
		selectQueryBuff.append("parent_geo_name, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_user,(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OC.category_name END) owner_cat,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OGD.grph_domain_name END)  owner_geo, ");
		selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, ADJ_MARGIN_AMOUNT, "margin_amount").append(" , ");
		selectQueryBuff.append("TO_CHAR(ADJ.margin_rate").append(",").append(FORMAT).append(" ) as margin_rate,  CST.differential_applicable App_Add_Comm,ST.NAME,CST.receiver_msisdn, ");
		selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CST_TRANSFER_VALUE, "transfer_value").append(" , ");
		selectQueryBuff.append("GU.user_id,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GU.user_name END) grand_name,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN '' ELSE GU.msisdn END) grand_msisdn,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GGD.grph_domain_name END) grand_geo_domain,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GC.category_name END) ");
		selectQueryBuff.append("grand_category, TO_CHAR(ADJ.created_on, ?)time ");
		selectQueryBuff.append("FROM ADJUSTMENTS ADJ, USERS U, C2S_TRANSFERS_OLD CST, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD,CATEGORIES C, USERS PU, USERS OU, CATEGORIES PC, CATEGORIES OC, USER_GEOGRAPHIES PUG, GEOGRAPHICAL_DOMAINS PGD, USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,SERVICE_TYPE ST,USERS GU, USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD ");
		selectQueryBuff.append("WHERE ADJ.ADJUSTMENT_DATE  >= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.ADJUSTMENT_DATE  <= TRUNC(TO_DATE(?,'dd/mm/yy HH24:MI:SS')) AND ADJ.created_on >= TO_DATE(?,'dd/mm/yy HH24:MI:SS') AND ADJ.created_on <= TO_DATE(?,'dd/mm/yy HH24:MI:SS') AND ADJ.network_code=? ");
		selectQueryBuff.append("AND ADJ.user_id =  CASE ?  WHEN 'ALL' THEN ADJ.user_id ELSE  ? END AND C.category_code = CASE ?  WHEN 'ALL' THEN C.category_code ELSE  ? END AND ADJ.user_category = C.category_code AND C.domain_code IN(?) AND ADJ.user_id=U.user_id AND CST.transfer_id=SUBSTR(ADJ.adjustment_id, 1,LENGTH(ADJ.adjustment_id)-1) ");
		selectQueryBuff.append("AND UG.grph_domain_code = GD.grph_domain_code AND UG.user_id=U.user_id AND PUG.grph_domain_code = PGD.grph_domain_code AND PUG.user_id=PU.user_id AND PU.user_id=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END AND PU.category_code=PC.category_code AND OUG.grph_domain_code = OGD.grph_domain_code AND OUG.user_id=OU.user_id AND OU.user_id=U.owner_id AND OU.category_code=OC.category_code ");
		selectQueryBuff.append("AND GUG.grph_domain_code = GGD.grph_domain_code AND GUG.user_id=GU.user_id AND GU.user_id=CASE PU.parent_id WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END AND GU.category_code=GC.category_code AND ADJ.SERVICE_TYPE=ST.SERVICE_TYPE AND UG.grph_domain_code IN ( ");
		selectQueryBuff.append("SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN ");
		selectQueryBuff.append("(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END AND UG1.user_id=?)) ");
		selectQueryBuff.append("GROUP BY CST.transfer_id, ADJ.adjustment_id,U.user_name, U.msisdn, C.category_name, GD.grph_domain_name, PU.user_id, U.parent_id, PU.user_name, PU.msisdn, PC.category_name, PGD.grph_domain_name, U.user_id, OU.user_name, OU.user_id, OU.msisdn, OC.category_name, OGD.grph_domain_name, ADJ.margin_rate, ADJ.otf_type, ADJ.otf_rate, ADJ.otf_amount, CST.differential_applicable,  ST.NAME, CST.receiver_msisdn, GU.user_id, PU.parent_id,GU.user_name, GU.msisdn, GGD.grph_domain_name, GC.category_name, ADJ.created_on, ADJ.commission_type ");
		selectQueryBuff.append("ORDER BY 27 Desc");
		String sqlSelect = selectQueryBuff.toString();

		if(log.isDebugEnabled()){
			log.debug(methodName, "SelectQuery: "+sqlSelect);
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			int i = 1;
			pstmt.setString(i++,Constants.getProperty("report.onlytimeformat"));
			pstmt.setString(i++,usersReportModel.getRptfromDate());
			pstmt.setString(i++,usersReportModel.getRpttoDate());
			pstmt.setString(i++, usersReportModel.getRptfromDate());
			pstmt.setString(i++,usersReportModel.getRpttoDate());				
			pstmt.setString(i++, usersReportModel.getNetworkCode());
			pstmt.setString(i++, usersReportModel.getUserID());
			pstmt.setString(i++, usersReportModel.getUserID());
			pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			pstmt.setString(i++, usersReportModel.getParentCategoryCode());
			pstmt.setString(i++,usersReportModel.getDomainCode());
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i++,usersReportModel.getLoginUserID());

		} catch (SQLException e) {
			log.error(methodName, "SQLException: "+e.getMessage());
			log.errorTrace(methodName, e);
		} catch (Exception e) {
			log.error(methodName, "Exception: "+e.getMessage());
			log.errorTrace(methodName, e);
		}

		return pstmt;
	}

}
