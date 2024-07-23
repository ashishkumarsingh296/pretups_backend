/*
 * •COPYRIGHT: Mahindra Comviva Technologies Pvt. Ltd.
 * •
 * •This software is the sole property of Comviva and is protected
 * •by copyright law and international treaty provisions. Unauthorized
 * •reproduction or redistribution of this program, or any portion of
 * •it may result in severe civil and criminal penalties and will be
 * •prosecuted to the maximum extent possible under the law.
 * •Comviva reserves all rights not expressly granted. You may not
 * •reverse engineer, decompile, or disassemble the software, except
 * •and only to the extent that such activity is expressly permitted
 * •by applicable law notwithstanding this limitation.
 * •
 * •THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY
 * •KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * •THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * •PARTICULAR PURPOSE. YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * •AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * •ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * •USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE
 * •POSSIBILITY OF SUCH DAMAGE.
 */
package com.btsl.pretups.processes.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.logging.MSISDNBatchPocessingLog;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public class MsisdnUpdateProcessDAO {

    private static final Log LOG = LogFactory.getLog(MsisdnUpdateProcessDAO.class.getName());

    public void updateUserPhonePrimaryMsisdnBatch(Connection conn, Map<String, String> msisdnMap, int batchCount) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("updateUserPhonePrimaryMsisdnBatch Entered ", "msisdnMap=" + msisdnMap.toString() + " batchCount is  " + batchCount);
        }
        final String methodName = "updateUserPhonePrimaryMsisdnBatch";
        PreparedStatement pstmtUserPhoneUpdate = null;
        PreparedStatement pstmtUserUpdate = null;
        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        ChannelUserVO channelUserVO = null;
        ChannelUserVO channelUserNewVO = null;
        int rowCount = 0;
        Map.Entry mapEntry = null;
        String currentMsisdn = "";
        String newMsisdn = "";

        try {
            String userPhoneUpdate = " UPDATE USER_PHONES set MSISDN = ?  WHERE  MSISDN=? and PRIMARY_NUMBER='Y'";
            String userUpdate = " UPDATE USERS set MSISDN = ?  WHERE  MSISDN=?";
            pstmtUserPhoneUpdate = conn.prepareStatement(userPhoneUpdate);
            pstmtUserUpdate = conn.prepareStatement(userUpdate);

            Iterator iterator = msisdnMap.entrySet().iterator();
            while (iterator.hasNext()) {
                mapEntry = (Map.Entry) iterator.next();

                currentMsisdn = mapEntry.getKey().toString();
                newMsisdn = mapEntry.getValue().toString();

                channelUserVO = channelUserDAO.loadChannelUserDetails(conn, currentMsisdn);
                channelUserNewVO = channelUserDAO.loadChannelUserDetails(conn, newMsisdn);

                if (channelUserVO == null) {
                    MSISDNBatchPocessingLog.log("MSISDN Can't Updated", currentMsisdn, true);
                } else if (channelUserNewVO != null) {
                    MSISDNBatchPocessingLog.log("MSISDN Can't Updated", newMsisdn, false);
                }

                if (channelUserVO != null && channelUserNewVO == null) {
                    pstmtUserPhoneUpdate.clearParameters();
                    rowCount++;
                    pstmtUserPhoneUpdate.setString(1, newMsisdn);
                    pstmtUserPhoneUpdate.setString(2, currentMsisdn);
                    pstmtUserPhoneUpdate.addBatch();

                    pstmtUserUpdate.clearParameters();
                    pstmtUserUpdate.setString(1, newMsisdn);
                    pstmtUserUpdate.setString(2, currentMsisdn);
                    pstmtUserUpdate.addBatch();
                }

                if (rowCount != 0 && (rowCount % batchCount) == 0) {
                    pstmtUserPhoneUpdate.executeBatch();
                    pstmtUserUpdate.executeBatch();
                    conn.commit();
                }
            }

            if (rowCount != 0) {
                pstmtUserPhoneUpdate.executeBatch();
                pstmtUserUpdate.executeBatch();
                conn.commit();
            }

        } catch (Exception ex) {
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MsisdnUpdateProcessDAO[updateUserPhonePrimaryMsisdnBatch]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("MsisdnUpdateProcessDAO", methodName, "Some error occured, process cannot continue");
        } finally {
            try {
                if (pstmtUserPhoneUpdate != null) {
                    pstmtUserPhoneUpdate.close();
                }
                if (pstmtUserUpdate != null) {
                    pstmtUserUpdate.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " Exited ");
            }
        }
    }

}
