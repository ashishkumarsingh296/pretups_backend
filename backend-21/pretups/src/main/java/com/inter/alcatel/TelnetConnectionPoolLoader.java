package com.inter.alcatel;

/**
 * @(#)TelnetConnectionPoolLoader.java
 *                                     Copyright(c) 2005, Bharti Telesoft Int.
 *                                     Public Ltd.
 *                                     All Rights Reserved
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Author Date History
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Gurjeet Singh Bedi Oct 19,2005 Initial
 *                                     Creation
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     --------------
 *                                     Pool loader class for the interface
 */
import java.util.HashMap;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;

public class TelnetConnectionPoolLoader {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String[] _ids = null;
    private static HashMap _map = new HashMap();

    public TelnetConnectionPoolLoader(String p_ids) {
        if (_log.isDebugEnabled())
            _log.debug("TelnetConnectionPoolLoader", "Entered p_ids=" + p_ids);
        _map = new HashMap();
        _ids = p_ids.split(",");
        String id;
        String urlStr = null;
        String poolId = null;
        int port;
        int poolSize = 0;
        int connectionTimeout = 0;
        TelnetConnectionPool pool = null;
        for (int i = 0; i < _ids.length; i++) {
            id = _ids[i];
            try {
                urlStr = FileCache.getValue(id, "URL");
                poolSize = Integer.parseInt(FileCache.getValue(id, "POOL_SIZE"));
                connectionTimeout = Integer.parseInt(FileCache.getValue(id, "CONNECTION_TIMEOUT"));
                pool = new TelnetConnectionPool(urlStr, id, poolSize, connectionTimeout);
                _map.put(id, pool);
            } catch (Exception e) {
                _log.error("TelnetConnectionPoolLoader", "Exception e:" + e.getMessage() + " id=" + id);
                e.printStackTrace();
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("TelnetConnectionPoolLoader", "Exiting");
    }

    public static TelnetConnectionPool getPool(String p_id) {
        return (TelnetConnectionPool) _map.get(p_id);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
