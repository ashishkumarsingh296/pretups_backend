/**
 * @(#)DatabaseHelperClass.java
 *                              Copyright(c) 2006, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 *                              This class is used as a helper class to perform
 *                              database specific operations
 *                              This class implements DatabaseHelperInterface
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Ankit Zindal Nov 08,2006 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 */
/*
 * Created on Nov 8, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.util;

import java.sql.PreparedStatement;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

// commented for DB2import oracle.jdbc.OraclePreparedStatement;

/**
 * @author ankit.zindal
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class DatabaseHelperClass implements DatabaseHelperInterface {
    public static final  Log _log = LogFactory.getLog(DatabaseHelperClass.class.getName());

    /**
     * 
     */
    public DatabaseHelperClass() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
    }

    public void setFormOfUse(PreparedStatement p_stmt, int p_index, String p_setString) {
        final String METHOD_NAME = "setFormOfUse";
        try {
            // commented for DB2OraclePreparedStatement
            // orpstmt=(OraclePreparedStatement)p_stmt;
            final PreparedStatement orpstmt = (PreparedStatement) p_stmt;
            // commented for DB2orpstmt.setFormOfUse(p_index,
            // PretupsI.FORM_NCHAR);
            orpstmt.setString(p_index, p_setString);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }
}