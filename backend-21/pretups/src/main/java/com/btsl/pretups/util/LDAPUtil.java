
package com.btsl.pretups.util;

import com.btsl.common.BTSLBaseException;

public class LDAPUtil implements LDAPUtilI {

	@Override
    public boolean authenticateUser(String pLoginID, String password) throws BTSLBaseException{
		// boddy need to overide
		return true;
	}

}
