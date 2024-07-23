package com.btsl.db.query.postgres;

import com.btsl.loadcontroller.LoadControllerQry;

public class LoadControllerPostgresQry implements LoadControllerQry{

	@Override
	public StringBuilder loadNetworkSeriveDetailsQry() {
		 StringBuilder selectQueryBuff = new StringBuilder(" SELECT M.module,coalesce(M.seq_no,9999) SEQ_NO,M.reqtype reqtype, M.stype stype,M.name  , N.network_code network_code");
         selectQueryBuff.append(" FROM instance_load il, networks N,network_load NL,(SELECT REQ_INTERFACE_TYPE REQTYPE, SK.SERVICE_TYPE STYPE,sty.name ,sty.module MODULE,sty.seq_no ");
         selectQueryBuff.append(" FROM SERVICE_KEYWORDS SK,SERVICE_TYPE STY WHERE SK.service_type=STY.service_type AND STY.STATUS<>'N'");
         selectQueryBuff.append(" GROUP BY sty.module,sty.seq_no,SK.SERVICE_TYPE,REQ_INTERFACE_TYPE, sty.name) M WHERE N.network_code=NL.network_code AND IL.instance_id=?");
         selectQueryBuff.append(" AND NL.instance_id=il.instance_id AND M.MODULE=case il.module when 'ALL' then M.module else il.module end  GROUP BY N.network_code,M.module,M.seq_no,M.name,reqtype, stype");
         return selectQueryBuff;
	}

	@Override
	public String loadNetworkServiceHourlyDetailsQry() {
		 StringBuilder selectQueryBuff = new StringBuilder(" SELECT M.module,coalesce(M.seq_no,'9999') SEQ_NO,M.reqtype reqtype, M.stype stype,M.namee name1, N.network_code network_code");
         selectQueryBuff.append(" FROM instance_load il, networks N,network_load NL,(SELECT REQ_INTERFACE_TYPE REQTYPE, SK.SERVICE_TYPE STYPE,sty.name namee,sty.module MODULE,sty.seq_no ");
         selectQueryBuff.append(" FROM SERVICE_KEYWORDS SK,SERVICE_TYPE STY,NETWORK_SERVICES NS ,SERVICE_TYPE_SELECTOR_MAPPING stsm  WHERE SK.service_type=STY.service_type AND STY.STATUS<>'N'");
         selectQueryBuff.append(" AND STY.service_type=ns.service_type and ns.status <> 'N' and sk.status <> 'N' and ns.service_type=stsm.SERVICE_TYPE ");
         selectQueryBuff.append(" GROUP BY sty.module,sty.seq_no,SK.SERVICE_TYPE,REQ_INTERFACE_TYPE, sty.name) M WHERE N.network_code=NL.network_code AND IL.instance_id=?");
         selectQueryBuff.append(" AND NL.instance_id=il.instance_id AND M.MODULE=case il.module when 'ALL' then M.module else il.module end  GROUP BY N.network_code,M.module,M.seq_no,M.namee,reqtype, stype");

		return selectQueryBuff.toString();
	}

}
