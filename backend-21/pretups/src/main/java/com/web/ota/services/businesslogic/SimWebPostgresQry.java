package com.web.ota.services.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class SimWebPostgresQry implements SimWebQry{
	private Log log  = LogFactory.getLog(this.getClass());
	@Override
	public String getParamDetailsForMobileQry() {
	    final StringBuilder sqlLoadBuf = new StringBuilder();
        sqlLoadBuf.append("SELECT COALESCE(S.param1,'') param1, COALESCE(S.param2,'') param2, COALESCE(S.param3,'') param3, ");
        sqlLoadBuf.append(" COALESCE(S.param4,'') param4, COALESCE(S.param5,'') param5, COALESCE(S.param6,'') param6, COALESCE(S.param7,'') param7,  ");
        sqlLoadBuf.append(" COALESCE(S.param8,'') param8,COALESCE(S.param9,'') param9, COALESCE(S.param10,'') param10, COALESCE(P.smsc1,'') smsc1,  ");
        sqlLoadBuf.append(" COALESCE(P.smsc2,'') smsc2, COALESCE(P.smsc3,'') smsc3, S.modified_on modified_on, COALESCE(P.port1,'') port1, COALESCE(P.port2,'') port2,  ");
        sqlLoadBuf.append(" COALESCE(P.port3,'') port3, COALESCE(P.vp1,1) vp1, COALESCE(P.vp2,1) vp2, COALESCE(P.vp3,1) vp3  ");
        sqlLoadBuf.append(" FROM sim_image S, sms_master P WHERE S.msisdn=?  ");
        sqlLoadBuf.append(" AND S.network_code=P.network_code AND S.sms_ref=P.sms_param_id");
        LogFactory.printLog("getParamDetailsForMobileQry", sqlLoadBuf.toString(), log);
		return sqlLoadBuf.toString();
	}
	
	@Override
	public String getSimEnquiryResponseForMobile() {
		  final StringBuilder sqlLoadBuf = new StringBuilder("SELECT COALESCE(sim_enq_response,'') RESPONSE ,modified_on ");
          sqlLoadBuf.append(" FROM sim_image WHERE  msisdn=? ");
          LogFactory.printLog("getSimEnquiryResponseForMobile", sqlLoadBuf.toString(), log);
          return sqlLoadBuf.toString();
	}


}
