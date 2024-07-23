/**
 * @(#)WebsphereDatabaseHelperClass.java
 *                                       Copyright(c) 2006, Bharti Telesoft Ltd.
 *                                       All Rights Reserved
 *                                       This class is used as websphare helper
 *                                       class to perform websphare specific
 *                                       operations
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Author Date History
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       -------------------
 *                                       Ankit Zindal Nov 08,2006 Initial
 *                                       Creation
 *                                       --------------------------------------
 *                                       --
 *                                       --------------------------------------
 *                                       ------------------
 */
/*
 * Created on Nov 8, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.ibm.websphere.rsadapter.WSCallHelper;

/**
 * @author ankit.zindal
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class WebsphereDatabaseHelperClass implements DatabaseHelperInterface {

    public static final Log _log = LogFactory.getLog(WebsphereDatabaseHelperClass.class.getName());

    /**
     * 
     */
    public WebsphereDatabaseHelperClass() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
    }

    public void setFormOfUse(PreparedStatement p_stmt, int p_index, String p_setString) {
        final String METHOD_NAME = "setFormOfUse";
        try {

            WSCallHelper.jdbcCall(PreparedStatement.class, p_stmt, "setFormOfUse", new Object[] { new Integer(p_index), Short.valueOf(PretupsI.FORM_NCHAR) },
                new Class[] { int.class, short.class });
            p_stmt.setString(p_index, p_setString);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            _log.errorTrace(METHOD_NAME, e);
        }
    }
}
