package com.btsl.db.query.oracle;

import com.btsl.util.AESKeystoreQry;

/**
 * AESKeyStoreOracleQry
 * @author sadhan.k
 *
 */
public class AESKeyStoreOracleQry implements AESKeystoreQry{

	@Override
	public String getKeyFromDBFirst() {
		
		StringBuilder selectQueryBuff = new StringBuilder("select key_pass,store_pass,sno,KEY_STORE_NAME,SECRET_KEY_NAME ");
        selectQueryBuff.append(" from credential_first ");
        selectQueryBuff.append(" where key_active = ? and rownum = 1");

		return selectQueryBuff.toString();
	}

	@Override
	public String getKeyFromDBSecond() {
		
		 StringBuilder selectQueryBuff = new StringBuilder("select key_pass,key_store_name,store_pass,secret_key_name,sno ");
         selectQueryBuff.append(" from credential_sec ");
         selectQueryBuff.append(" where sno = ? and rownum = 1");
    
		return selectQueryBuff.toString();
	}

}
