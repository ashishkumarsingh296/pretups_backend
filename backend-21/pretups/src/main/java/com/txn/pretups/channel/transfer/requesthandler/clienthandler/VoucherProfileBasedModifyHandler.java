/*
 * @(#)VoucherProfileBasedModifyHandler.java
 * Copyright(c) 2019, Comviva Ltd.
 * All Rights Reserved
 * ------------------------------------------------------------------------------
 * -------------------
 * Class for modifying product id of a voucher whose mrp is zero based on serial number and 
 * modifing product id based on master serial number
 */
package com.txn.pretups.channel.transfer.requesthandler.clienthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;

public class VoucherProfileBasedModifyHandler implements ServiceKeywordControllerI {
	private Log _log = LogFactory.getLog(VoucherProfileBasedModifyHandler.class.getName());
	OperatorUtil _operatorUtil;

	public void process(RequestVO p_requestVO) {
		final String METHOD_NAME = "process";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, " Entered " + p_requestVO);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		VomsVoucherDAO vomsVoucherDAO = null;
		VomsProductDAO vomsProductDAO = null;
		VomsProductVO vomsProductVO = null;
		VomsVoucherVO vomsVoucherVO = null;
		Date currDate = new Date();

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			vomsVoucherDAO = new VomsVoucherDAO();
			vomsProductDAO = new VomsProductDAO();
			String masterSerialNo = "";
			String productId = "";
			String decryptedMessage = p_requestVO.getDecryptedMessage();

			String CHNL_MESSAGE_SEP = ((String)PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR));
			if (BTSLUtil.isNullString(CHNL_MESSAGE_SEP)) {
				CHNL_MESSAGE_SEP = " ";
			}
			String[] reqArr = decryptedMessage.split(CHNL_MESSAGE_SEP);

			String pin = null;
			String serialNo = p_requestVO.getSerialNo();
			Object productIdObj = p_requestVO.getRequestMap().get("PRODUCTID");
			if (productIdObj != null) {
				productId = productIdObj.toString();
			}
			if (reqArr.length == 4) {
				pin = reqArr[3];
			} else {
				pin = "";
			}
			ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
			if (channelUserVO != null) {
				_operatorUtil = new OperatorUtil();
				_operatorUtil.validatePIN(con, channelUserVO, pin);
			}
			Object masterSerialNoObj = p_requestVO.getRequestMap().get("MASTERSERIALNO");
			if (masterSerialNoObj != null) {
				masterSerialNo = masterSerialNoObj.toString();
			}

			int updateCount = 0;

			boolean flag = false;

			if (serialNo != null && !(serialNo.contentEquals(""))
					&& (masterSerialNo.contentEquals("") || masterSerialNo == null)) {
				vomsVoucherVO = vomsVoucherDAO.getVoucherDetails(con, serialNo);
			} else {
				flag = true;
			}
			
			HashMap<String,String> changeStatusMap = this.populateChangeStatusMap();
			   
			if(vomsVoucherVO!=null && !changeStatusMap.containsKey(vomsVoucherVO.getCurrentStatus())) {
				p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.VOMS_CHANGE_STATUS_ERROR);
				return;
			}
			
			vomsProductVO = vomsProductDAO.getProductDetails(con, productId);

			if (!flag) {
				if (vomsVoucherVO != null && vomsProductVO != null
						&& BTSLUtil.getDisplayAmount(vomsVoucherVO.getMRP()) == 1
						&& BTSLUtil.getDisplayAmount(vomsProductVO.getMrp()) != 1
						&& currDate.before(BTSLUtil.getDateFromDateString(vomsVoucherVO.getExpiryDateStr()))) {
					updateCount = vomsVoucherDAO.updateVoucherProductId(con, p_requestVO, vomsVoucherVO, vomsProductVO,
							flag);
					if (updateCount > 0) {
						p_requestVO.setSuccessTxn(true);
						p_requestVO.setMessageCode(PretupsErrorCodesI.VOU_PRF_MOD_SUCCESS);
						con.commit();
					} else {
						p_requestVO.setSuccessTxn(false);
						p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_SERIALNO);
					}
				} else if (vomsVoucherVO == null && vomsProductVO != null) {
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_SERIALNO);
				} else if (vomsProductVO == null && vomsVoucherVO != null) {
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_PRODUCTID);
				} else if (vomsProductVO != null && BTSLUtil.getDisplayAmount(vomsProductVO.getMrp()) == 1) {
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.RESTRICTED_PRODUCTID);
				} else if (vomsVoucherVO != null && vomsProductVO != null
						&& currDate.after(BTSLUtil.getDateFromDateString(vomsVoucherVO.getExpiryDateStr()))) {
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
				} else {
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_SERIALNO);
				}
			} else {
				if (vomsProductVO != null) {
					vomsVoucherVO = new VomsVoucherVO();
					if (!BTSLUtil.isNullString(masterSerialNo)) {
						ArrayList<VomsVoucherVO> listVO = vomsVoucherDAO.getVoucherDetailsByMasterSerialNumber(con,
								masterSerialNo, Constants.getProperty("NETWORK_CODE"));
						boolean check = true;
						boolean expiryDateCheck = true;
						for (VomsVoucherVO obj : listVO) {
							if (BTSLUtil.getDisplayAmount(obj.getMRP()) != 1
									&& !("BLANK_VOUCHER".equals(obj.getVoucherType()))) {
								check = false;
								break;
							}
						}
						for (VomsVoucherVO obj : listVO) {
							if (currDate.after(BTSLUtil.getDateFromDateString(obj.getExpiryDateStr()))) {
								expiryDateCheck = false;
								break;
							}
						}
						if (check) {
							if (expiryDateCheck) {
								vomsVoucherVO.setSerialNo(masterSerialNo);
								vomsVoucherVO.setInfo5(listVO.get(0).getInfo5());
								updateCount = vomsVoucherDAO.updateVoucherProductId(con, p_requestVO, vomsVoucherVO,
										vomsProductVO, flag);
								if (updateCount > 0) {
									p_requestVO.setSuccessTxn(true);
									p_requestVO.setMessageCode(PretupsErrorCodesI.VOU_BUN_PRF_MOD_SUCCESS);
									mcomCon.finalCommit();
								} else {
									p_requestVO.setSuccessTxn(false);
									p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_MASTER_SERIALNO);
								}
							} else {
								p_requestVO.setSuccessTxn(false);
								p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_VOMS_VOUCHEREXPIRED);
							}
						} else {
							p_requestVO.setSuccessTxn(false);
							p_requestVO.setMessageCode(PretupsErrorCodesI.NOT_BLANK_VOUCHER_BUNDLE);
						}
					} else {
						p_requestVO.setSuccessTxn(false);
						p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_MASTER_SERIALNO);
					}

				} else {
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_PRODUCTID);
				}
			}
		} catch (BTSLBaseException e) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception ee) {
				_log.errorTrace(METHOD_NAME, ee);
			}
			_log.error(METHOD_NAME, "BTSLBaseException " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherProfileBasedModifyHandler[process]", "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN);
			return;
		} catch (Exception e) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception ee) {
				_log.errorTrace(METHOD_NAME, ee);
			}
			_log.error(METHOD_NAME, "Exception " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherProfileBasedModifyHandler[process]", "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			return;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("VoucherProfileBasedModifyHandler#process");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, " Exited ");
			}
		}
	}
	
	public HashMap<String, String> populateChangeStatusMap() {
        final String methodName = "populateChangeStatusMap";
    	if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered ");
    	}
		
        HashMap<String, String> changeStatusMap;
        try {
        	changeStatusMap = new HashMap<String, String>();
        	String fromToStatus = SystemPreferences.UNLOCK_ZERO_MIGRATIONS;
        	String[] possibleMigrations = fromToStatus.split(PretupsI.COMMA);
        	for (String migrationString : possibleMigrations) {
        		String[] fromAndToStatus = migrationString.split(PretupsI.COLON);
        		changeStatusMap.put(fromAndToStatus[0], fromAndToStatus[1]);
			}
        } catch (Exception e) {
        	_log.error(methodName, "Exception e=" + e.getMessage());
            _log.errorTrace(methodName, e);
            return null;
        }
        return changeStatusMap;
    }
	
}
