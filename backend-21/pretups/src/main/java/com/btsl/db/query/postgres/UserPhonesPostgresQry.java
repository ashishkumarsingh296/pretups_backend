package com.btsl.db.query.postgres;

import com.btsl.pretups.user.businesslogic.UserPhonesQry;

/**
 * @author satakshi.gaur
 *
 */
public class UserPhonesPostgresQry implements UserPhonesQry {

    @Override
    public String previousAutoC2CStatusQry() {
        StringBuilder queryBuf = new StringBuilder("");
        queryBuf.append(" select  up.user_id ,up.status_auto_c2c status_auto_c2c FROM  user_phones UP ");
        queryBuf.append(" WHERE up.user_id = ? ");
        queryBuf.append(" AND up.msisdn = ? ");
        queryBuf.append(" for update NOWAIT ");
        return queryBuf.toString();
	}

}
