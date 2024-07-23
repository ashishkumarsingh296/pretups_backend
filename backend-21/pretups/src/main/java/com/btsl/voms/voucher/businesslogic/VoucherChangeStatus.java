package com.btsl.voms.voucher.businesslogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.voms.vomscommon.VOMSI;

public class VoucherChangeStatus extends Thread {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private Thread t;
    private VomsBatchVO batchVO = null;
    private ArrayList batch_list = null;
    private Connection con = null;MComConnectionI mcomCon = null;

    VoucherChangeStatus() {
    }

    public VoucherChangeStatus(ArrayList list) throws BTSLBaseException {
    	final String methodName="VoucherChangeStatus";
        t = new Thread(this, "VoucherChangeStatus");
        batch_list = list;
		mcomCon = new MComConnection();
		try {
			con = mcomCon.getConnection();
		} catch (SQLException e) {
        	_log.error(methodName,  "Exception"+ e.getMessage());
    		_log.errorTrace(methodName, e);
        }

    }

    public void run() {
          
        try {
            if (batch_list.size() > 0) {
            	int batchlistSize = batch_list.size();
                for (int i = 0; i < batchlistSize; i++) {
                    int updateCount = 0;
                    int updateCount1 = 0;
                    ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();

                    batchVO = (VomsBatchVO) batch_list.get(i);
                    updateCount = channelTransferDAO.updateVomsVoucherStstus(con, batchVO);
          

                    if (updateCount > 0) {
                        con.commit();
                        batchVO.setSuccessCount(Long.parseLong(batchVO.getQuantity()));
                        batchVO.setFailCount(0);
                        batchVO.setStatus(VOMSI.EXECUTED);
                        batchVO.setMessage("Enabled successfuly");
                        updateCount1 = channelTransferDAO.updateFinalVomsBatch(con, batchVO);
                        if (updateCount1 > 0) {
                            con.commit();
                        } else {
                            con.rollback();
                        }
                    } else {
                        con.rollback();
                        batchVO.setSuccessCount(0);
                        batchVO.setFailCount(batchVO.getTotalVoucherPerOrder());
                        batchVO.setStatus(VOMSI.BATCHFAILEDSTATUS);
                        batchVO.setMessage("request failed");
                        updateCount1 = channelTransferDAO.updateFinalVomsBatch(con, batchVO);
                        if (updateCount1 > 0) {
                            con.commit();
                        } else {
                            con.rollback();
                        }

                    }
                }
            } else {
                con.rollback();
            }
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception e1) {
                _log.errorTrace("run", e1);
            }
            _log.error("VoucherChangeStatus", "Exception in change status thread while rollback ");
            _log.errorTrace("run", e);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("VoucherChangeStatus#run");
				mcomCon = null;
			}
        }
    }

}
