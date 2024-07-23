package com.inter.urlcon;

/**
 * @(#)FermaConnectionPoolLoader.java
 *                                    Copyright(c) 2005, Bharti Telesoft Int.
 *                                    Public Ltd.
 *                                    All Rights Reserved
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Abhijit Chauhan June 22,2005 Initial
 *                                    Creation
 *                                    Manoj kumar 20 December Modify
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    ------------
 */
import java.util.HashMap;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.util.Constants;

public class FermaConnectionPoolLoader extends HttpServlet {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String[] _ids = null;
    private static HashMap _map = new HashMap();

    public void init(ServletConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
        super.init(arg0);
        String ids = Constants.getProperty("FERMA_CONN_POOL_IDS");
        if (_log.isDebugEnabled())
            _log.debug("FermaConnectionPoolLoader", "Entered FERMA_CONN_POOL_IDS : " + ids);
        _map = new HashMap();
        _ids = ids.split(",");
        String id;
        String urlStr = null;
        int poolSize = 0;
        int coneectionTimeout = 0;
        String keepAlive = null;
        FermaUrlConnectionPool pool = null;
        for (int i = 0; i < _ids.length; i++) {
            id = _ids[i];
            try {
                urlStr = FileCache.getValue(id, "URL");
                poolSize = Integer.parseInt(FileCache.getValue(id, "POOL_SIZE"));
                coneectionTimeout = Integer.parseInt(FileCache.getValue(id, "CONNECTION_TIMEOUT"));
                keepAlive = FileCache.getValue(id, "KEEP_ALIVE");
                pool = new FermaUrlConnectionPool(urlStr, id, poolSize, coneectionTimeout, keepAlive);
                _map.put(id, pool);
            } catch (Exception e) {
                _log.error("FermaConnectionPoolLoader", "Exception e:" + e.getMessage() + " id=" + id);
                e.printStackTrace();
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("FermaConnectionPoolLoader", "Exiting");

    }

    public static FermaUrlConnectionPool getPool(String p_id) {
        return (FermaUrlConnectionPool) _map.get(p_id);
    }

}
