package com.selftopup.db.util;

import java.sql.Connection;
import java.sql.DriverManager;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

public abstract class BTSLDBManager {
    public static Log _log = LogFactory.getLog(BTSLDBManager.class.getName());
    protected String DB_CONN_FAILED = "00000";

    abstract public Connection getConnection() throws BTSLBaseException;

    public Connection getSingleConnection() throws BTSLBaseException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            _log.errorTrace("getSingleConnection: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDBManager[getSingleConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(DB_CONN_FAILED);
        }
        Connection conn = null;
        try {
            String db_url = Constants.getProperty("datasourceurl");
            String db_user = Constants.getProperty("userid");
            if (db_user != null)
                db_user = BTSLUtil.decrypt3DesAesText(db_user);
            String db_password = Constants.getProperty("passwd");
            if (db_password != null)
                db_password = BTSLUtil.decrypt3DesAesText(db_password);
            conn = DriverManager.getConnection(db_url, db_user, db_password);
        } catch (Exception e) {
            _log.errorTrace("getSingleConnection: Exception print stack trace:=", e);
            EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLDBManager[getSingleConnection]", "", "", "", "Database Connection Problem");
            throw new BTSLBaseException(DB_CONN_FAILED);
        }
        return conn;
    }
}
