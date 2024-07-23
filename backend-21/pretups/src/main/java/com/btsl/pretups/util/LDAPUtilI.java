package com.btsl.pretups.util;

import com.btsl.common.BTSLBaseException;
public interface LDAPUtilI {
			public boolean authenticateUser(String pLoginID, String password) throws BTSLBaseException;
	}
		

