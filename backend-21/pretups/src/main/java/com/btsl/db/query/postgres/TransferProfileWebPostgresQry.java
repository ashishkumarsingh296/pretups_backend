package com.btsl.db.query.postgres;

import com.web.pretups.channel.profile.businesslogic.TransferProfileWebQry;

public class TransferProfileWebPostgresQry implements TransferProfileWebQry {
	
@Override
public String modifyTransferControlProfileProductQry(){
	 StringBuilder insertBuff = new StringBuilder("INSERT INTO transfer_profile_products (profile_id,");
     insertBuff.append(" product_code,min_residual_balance,max_balance,alerting_balance, ");
     insertBuff.append(" max_pct_transfer_allowed,c2s_min_txn_amt, c2s_max_txn_amt)");
     insertBuff.append(" VALUES(?,?,?,?,?,?::integer,?,?)");
     return insertBuff.toString();
	}
}