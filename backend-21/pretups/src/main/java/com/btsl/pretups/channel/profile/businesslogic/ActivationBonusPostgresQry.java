package com.btsl.pretups.channel.profile.businesslogic;

public class ActivationBonusPostgresQry implements ActivationBonusQry{
	@Override
	public String loadMappingSummaryQry(boolean pisMsisdn){
       
        StringBuilder strBuff = new StringBuilder("SELECT  S.user_id,U.msisdn,SUM(S.activated_users) noOfSubs FROM subs_activation_summary S,users U ");
        if (pisMsisdn) {
            strBuff.append("WHERE  U.msisdn=? ");
        } else {
            strBuff.append("WHERE S.user_id=? ");
        }
        strBuff.append("AND S.user_id=U.user_id ");
        strBuff.append("AND date_trunc('day',S.activation_date::TIMESTAMP)>=date_trunc('day',?::TIMESTAMP) ");
        strBuff.append("AND date_trunc('day',S.activation_date::TIMESTAMP)<=date_trunc('day',?::TIMESTAMP) ");
        strBuff.append("GROUP BY S.user_id,U.msisdn");
        return strBuff.toString();
	}
	@Override
	public String updateMappingSummaryQry(){
	      StringBuilder strBuff = new StringBuilder("UPDATE subs_activation_summary ");
          strBuff.append("SET activated_users=? ");
          strBuff.append("where user_id=? ");
          strBuff.append("AND date_trunc('day',activation_date::TIMESTAMP)>=date_trunc('day',?::TIMESTAMP) ");
          return strBuff.toString();
	}
}
