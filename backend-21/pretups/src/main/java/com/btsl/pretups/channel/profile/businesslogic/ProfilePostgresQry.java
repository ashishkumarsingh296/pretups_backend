package com.btsl.pretups.channel.profile.businesslogic;

public class ProfilePostgresQry implements ProfileQry{
	@Override
	public String loadVersionsQry(String profileSetId){
		StringBuilder sbf = new StringBuilder();
		sbf.append("SELECT psv.set_id,psv.VERSION,psv.applicable_from,psv.bonus_duration ");
        sbf.append("FROM PROFILE_SET_VERSION psv ");
        sbf.append("WHERE (applicable_from=(SELECT MAX(applicable_from) FROM PROFILE_SET_VERSION WHERE date_trunc('day',applicable_from::TIMESTAMP)<=CURRENT_TIMESTAMP AND psv.set_id=set_id) ");
        if (profileSetId == null) {
            sbf.append("OR applicable_from>CURRENT_TIMESTAMP) ");
        } else {
            sbf.append("AND psv.set_id=?)");
        }
        sbf.append("AND psv.status<>? ");
        return sbf.toString();
	}

}