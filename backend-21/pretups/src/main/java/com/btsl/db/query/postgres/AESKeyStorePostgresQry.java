package com.btsl.db.query.postgres;

import com.btsl.util.AESKeystoreQry;

/**
 * AESKeyStorePostgresQry
 * @author sadhan.k
 *
 */
public class AESKeyStorePostgresQry implements AESKeystoreQry{

	@Override
	public String getKeyFromDBFirst() {
		
		StringBuilder selectQueryBuff = new StringBuilder("select key_pass,store_pass,sno,KEY_STORE_NAME,SECRET_KEY_NAME ");
        selectQueryBuff.append(" from credential_first ");
        selectQueryBuff.append(" where key_active = ? limit 1");

		return selectQueryBuff.toString();
	}

	@Override
	public String getKeyFromDBSecond() {
		
		 StringBuilder selectQueryBuff = new StringBuilder("select key_pass,key_store_name,store_pass,secret_key_name,sno ");
         selectQueryBuff.append(" from credential_sec ");
         selectQueryBuff.append(" where sno = ? limit 1");
    
		return selectQueryBuff.toString();
	}
}
