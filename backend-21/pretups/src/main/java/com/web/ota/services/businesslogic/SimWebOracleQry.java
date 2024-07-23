package com.web.ota.services.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class SimWebOracleQry implements SimWebQry{

	private Log log  = LogFactory.getLog(this.getClass());
	@Override
	public String getParamDetailsForMobileQry() {
	    final StringBuilder sqlLoadBuf = new StringBuilder();
        sqlLoadBuf.append("SELECT nvl(S.param1,'') param1, nvl(S.param2,'') param2, nvl(S.param3,'') param3, ");
        sqlLoadBuf.append(" nvl(S.param4,'') param4, nvl(S.param5,'') param5, nvl(S.param6,'') param6, nvl(S.param7,'') param7,  ");
        sqlLoadBuf.append(" nvl(S.param8,'') param8,nvl(S.param9,'') param9, nvl(S.param10,'') param10, nvl(P.smsc1,'') smsc1,  ");
        sqlLoadBuf.append(" nvl(P.smsc2,'') smsc2, nvl(P.smsc3,'') smsc3, S.modified_on modified_on, nvl(P.port1,'') port1, nvl(P.port2,'') port2,  ");
        sqlLoadBuf.append(" nvl(P.port3,'') port3, nvl(P.vp1,1) vp1, nvl(P.vp2,1) vp2, nvl(P.vp3,1) vp3  ");
        sqlLoadBuf.append(" FROM sim_image S, sms_master P WHERE S.msisdn=?  ");
        sqlLoadBuf.append(" AND S.network_code=P.network_code AND S.sms_ref=P.sms_param_id");
        LogFactory.printLog("getParamDetailsForMobileQry", sqlLoadBuf.toString(), log);
		return sqlLoadBuf.toString();
	}
	@Override
	public String getSimEnquiryResponseForMobile() {
		  final StringBuilder sqlLoadBuf = new StringBuilder("SELECT nvl(sim_enq_response,'') RESPONSE ,modified_on ");
          sqlLoadBuf.append(" FROM sim_image WHERE  msisdn=? ");
          LogFactory.printLog("getSimEnquiryResponseForMobile", sqlLoadBuf.toString(), log);
          return sqlLoadBuf.toString();
	}

}
