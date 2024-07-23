package com.btsl.db.query.oracle;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.ActivationBonusRedemptionQry;
import com.btsl.util.Constants;

public class ActivationBonusRedemptionOracleQry implements ActivationBonusRedemptionQry{

	@Override
	public String selectForUpdateUserBalance() {
		 final StringBuilder strSelectUserBalOnlyBuff = new StringBuilder();
	        strSelectUserBalOnlyBuff.append(" SELECT ");
	        strSelectUserBalOnlyBuff.append(" balance ");
	        strSelectUserBalOnlyBuff.append(" FROM user_balances ");
	        // DB220120123for update WITH RS
	        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
	            strSelectUserBalOnlyBuff.append(" WHERE user_id = ? and product_code = ? AND network_code_for = ? AND network_code = ?  FOR UPDATE OF balance WITH RS");
	        } else {
	            strSelectUserBalOnlyBuff.append(" WHERE user_id = ? and product_code = ? AND network_code_for = ? AND network_code = ?  FOR UPDATE OF balance ");
	        }
		return strSelectUserBalOnlyBuff.toString();
	}

}
