package com.btsl.db.query.oracle;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.UserPhonesQry;
import com.btsl.util.Constants;

/**
 * @author satakshi.gaur
 *
 */
public class UserPhonesOracleQry implements UserPhonesQry {

    @Override
    public String previousAutoC2CStatusQry() {
        StringBuilder queryBuf = new StringBuilder("");
        queryBuf.append(" select  up.user_id ,up.status_auto_c2c status_auto_c2c FROM  user_phones UP ");
        queryBuf.append(" WHERE up.user_id = ? ");
        queryBuf.append(" AND up.msisdn = ? ");
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            queryBuf.append(" for update of status_auto_c2c with RS ");
        }else {
            queryBuf.append(" for update of status_auto_c2c NOWAIT ");
        }
        return queryBuf.toString();
	}
}
