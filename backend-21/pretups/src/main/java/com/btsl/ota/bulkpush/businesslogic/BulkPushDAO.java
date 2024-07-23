/**
 * @(#)BulkPushDAO
 *                 Copyright(c) 2003, Bharti Telesoft Ltd.
 *                 All Rights Reserved
 *                 Data Access object class for Bulk Pushing of the messages
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Gurjeet 18/12/2003 Initial Creation
 *                 Amit Ruwali 21/08/2005 Modified
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 */

package com.btsl.ota.bulkpush.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class BulkPushDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String _jobName = null;
}
