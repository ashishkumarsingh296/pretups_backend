package com.btsl.pretups.p2p.transfer.businesslogic;

public class MCDOracleQry implements MCDQry{
	@Override
	public String countDaysQry(){
		final StringBuilder queryBuff = new StringBuilder("SELECT parent_id,list_name,Last_Transfer,Days");
        queryBuff.append(" FROM");
        queryBuff.append(" (SELECT parent_id,list_name,MAX(buddy_last_transfer_on)AS Last_Transfer, SYSDATE AS Current_Date, trunc(SYSDATE)-TO_DATE(MAX(buddy_last_transfer_on)) AS Days");
        queryBuff.append(" FROM p2p_buddies");
        queryBuff.append(" GROUP BY parent_id,list_name)");
        queryBuff.append(" tmp ");
        queryBuff.append(" WHERE Days > ?");
		return queryBuff.toString();
	}

}
