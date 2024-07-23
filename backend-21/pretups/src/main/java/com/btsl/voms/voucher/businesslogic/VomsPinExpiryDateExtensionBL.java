package com.btsl.voms.voucher.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;


public class VomsPinExpiryDateExtensionBL {
	/**
	 * Field _log.
	 */
	private static Log log = LogFactory.getLog(VomsPinExpiryDateExtensionBL.class.getName());

	private VomsPinExpiryDateExtensionBL() {
		// TODO Auto-generated constructor stub
	}
	
	public static int updateVomsVoucherExpiryDateInfo(Connection pCon, ArrayList<VomsVoucherVO> vomsPinExt) throws BTSLBaseException {
		if (log.isDebugEnabled()) {
			log.debug("VomsPinExpiryDateExtensionBL", "updateVomsVoucherExpiryDateInfo Entered ArrayList: " + vomsPinExt);
		}
		final String METHOD_NAME = "updateVomsVoucherExpiryDateInfo";
		int updateCount=0;
		VomsPinExpiryDateExtensionDAO vomsPinExpiryDateExtensionDAO = null;
		try {
			vomsPinExpiryDateExtensionDAO = new VomsPinExpiryDateExtensionDAO();
			for(VomsVoucherVO vomsVoucherVO : vomsPinExt){
				int count=vomsPinExpiryDateExtensionDAO.updateExpiryDate(pCon, vomsVoucherVO);
				 if (count < 0) {
					 return count;
		            }
				 updateCount+=count;
			}
			return updateCount;
		} catch (BTSLBaseException be) {
			log.error(METHOD_NAME, " BTSL Exception while updating parent user info :" + be.getMessage());
			throw be;
		} catch (Exception e) {
			log.error(METHOD_NAME, " Exception while updating parent user info :" + e.getMessage());
			log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[updateUserInfo]", "", "", "",
					"Exception :" + e.getMessage());
			throw new BTSLBaseException("VomsPinExpiryDateExtensionBL", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);
		} finally {
				if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exit");
			}
		}

	}
	
	public static int updateVomsVoucherBatchExpiryDateInfo(Connection pCon, ArrayList<VomsVoucherVO> vomsPinExt) throws BTSLBaseException {
		if (log.isDebugEnabled()) {
			log.debug("VomsPinExpiryDateExtensionBL", "updateVomsVoucherExpiryDateInfo Entered ArrayList: " + vomsPinExt);
		}
		final String METHOD_NAME = "updateVomsVoucherBatchExpiryDateInfo";
		int updateCount=0;
		VomsPinExpiryDateExtensionDAO vomsPinExpiryDateExtensionDAO = null;
		try {
			vomsPinExpiryDateExtensionDAO = new VomsPinExpiryDateExtensionDAO();
			for(VomsVoucherVO vomsVoucherVO : vomsPinExt){
				int count=vomsPinExpiryDateExtensionDAO.updateBatchExpiryDate(pCon, vomsVoucherVO);
				 if (count < 0) {
					 return count;
		            }
				 updateCount+=count;
			}
			return updateCount;
		} catch (BTSLBaseException be) {
			log.error(METHOD_NAME, " BTSL Exception while updating parent user info :" + be.getMessage());
			throw be;
		} catch (Exception e) {
			log.error(METHOD_NAME, " Exception while updating parent user info :" + e.getMessage());
			log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserBL[updateUserInfo]", "", "", "",
					"Exception :" + e.getMessage());
			throw new BTSLBaseException("VomsPinExpiryDateExtensionBL", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);
		} finally {
				if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exit");
			}
		}

	}
	
}
