package com.btsl.pretups.master.businesslogic;

/**
 * @(#)BarredUserDAO
 *                   Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                   All Rights Reserved
 *                   This class is used as Data Access Object for Barring User
 *                   module..
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Abhijit Chauhan June 10,2005 Initial Creation
 *                   ----------------------------------------------------------
 *                   --------------------------------------
 */

import java.sql.Connection;

public class BarredUserDAO {

    public BarredUserDAO() {
        super();
        // TODO Auto-generated constructor stub
    }

    public boolean isUserBarred(Connection con, String filteredMSISDN, String module, String type) {
        return false;
    }
}
