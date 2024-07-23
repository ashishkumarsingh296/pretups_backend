package com.btsl.db.query.postgres;

import com.btsl.pretups.processes.ActivationBonusRedemptionQry;


public class ActivationBonusRedemptionPostgresQry implements ActivationBonusRedemptionQry{

	@Override
	public String selectForUpdateUserBalance() {
		final StringBuilder strSelectUserBalOnlyBuff = new StringBuilder();
		strSelectUserBalOnlyBuff.append(" SELECT ");
		strSelectUserBalOnlyBuff.append(" balance ");
		strSelectUserBalOnlyBuff.append(" FROM user_balances ");
		strSelectUserBalOnlyBuff.append(" WHERE user_id = ? and product_code = ? AND network_code_for = ? AND network_code = ?  FOR UPDATE  ");
		return strSelectUserBalOnlyBuff.toString();
	}
}
