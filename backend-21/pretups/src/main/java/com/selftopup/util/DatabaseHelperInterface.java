/**
 * @(#)DatabaseHelperInterface.java
 *                                  Copyright(c) 2006, Bharti Telesoft Ltd.
 *                                  All Rights Reserved
 *                                  This class is used as an interface to
 *                                  perform database specific operations
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Ankit Zindal Nov 08,2006 Initial Creation
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  --------
 */
/*
 * Created on Nov 8, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.selftopup.util;

import java.sql.PreparedStatement;

/**
 * @author ankit.zindal
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public interface DatabaseHelperInterface {
    public abstract void setFormOfUse(PreparedStatement p_stmt, int p_index, String p_setString);
}