package com.inter.voms;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceHandler;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.inter.util.VOMSProductVO;
import com.btsl.pretups.inter.util.VOMSVoucherDAO;
import com.btsl.pretups.inter.util.VOMSVoucherVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;

public class VOMSHandler implements InterfaceHandler {
    private static Log _log = LogFactory.getLog(VOMSHandler.class.getName());
    private HashMap _requestMap = null;
    private HashMap _responseMap = null;
    private String _interfaceID = null;
    private String _inTXNID = null;
    private String _msisdn = null;
    private String _referenceID = null;

    public VOMSHandler() {
        super();
    }

    /**
     * validate Method is used for getting the pin and serial number
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */

    public void validate(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("validate", "Entered " + InterfaceUtil.getPrintMap(p_map));
        Connection con = null;
        MComConnectionI mcomCon = null;
        Boolean flag = false;
        try {

            _requestMap = p_map;
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            String networkCode = (String) _requestMap.get("NETWORK_CODE");

            Date currDate = InterfaceUtil.getDateTimeFromDateString((String) _requestMap.get("TRANSFER_DATE"));
            long requestedAmt = Long.parseLong((String) _requestMap.get("INTERFACE_AMOUNT"));
            String currentStatus = (String) _requestMap.get("UPDATE_STATUS");
            String modifiedBy = (String) _requestMap.get("SENDER_USER_ID");
            String statusChangeSource = (String) _requestMap.get("SOURCE");
            _interfaceID = (String) _requestMap.get("INTERFACE_ID");
            String selector = (String) _requestMap.get("CARD_GROUP_SELECTOR");
            String reqService = (String)_requestMap.get("REQ_SERVICE");
			int requestedVoucherCount;
			if(p_map.get("QUANTITY")!=null)
			{
				requestedVoucherCount = Integer.parseInt((String)p_map.get("QUANTITY"));
			}
			else
			{
				requestedVoucherCount = 1;
			}
            ArrayList transferIdList = new ArrayList();
            transferIdList.add((String) _requestMap.get("TRANSACTION_ID"));

            // Load the active profiles for specified network for current date
            VOMSVoucherDAO vomsDAO = new VOMSVoucherDAO();

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            String timeStampCheck = FileCache.getValue(_interfaceID, "TIME_STAMP_CHK");
            boolean timeStmpChk = false;
            if ("Y".equals(timeStampCheck))
                timeStmpChk = true;
            HashMap profileMap = null;
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue())
            	profileMap=vomsDAO.loadActiveProfilesForPrivateRecharge(con,networkCode,currDate,timeStmpChk,reqService);
            else
            	profileMap=vomsDAO.loadActiveProfiles(con,networkCode,currDate,timeStmpChk,reqService);

            VOMSProductVO productVO = null;
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PRIVATE_RECHARGE_ALLOWED))).booleanValue())
                productVO = (VOMSProductVO) profileMap.get(String.valueOf(requestedAmt) + "_" + selector);
            else
                productVO = (VOMSProductVO) profileMap.get(String.valueOf(requestedAmt));
            if (productVO == null) {
                throw new BTSLBaseException(InterfaceErrorCodesI.INVALID_MRP_REQUESTED);
            }
            // Get the PIN & serial number details of voucher. Only voucher with
            // status enabled will
            // be picked from VOMS_VOUCHERS table.
            _requestMap.put("PRODUCT_ID", productVO.getProductID());
            _requestMap.put("PRODUCT_NAME",productVO.getProductName());
//            ArrayList voucherList= VOMSVoucherDAO.loadPINAndSerialNumber(con,productVO,modifiedBy,currDate,statusChangeSource,currentStatus,transferIdList,(String)_requestMap.get("NETWORK_CODE"),requestedVoucherCount);
            ArrayList voucherList= VOMSVoucherDAO.loadPINAndSerialNumber(con,productVO,modifiedBy,currDate,statusChangeSource,currentStatus,transferIdList,(String)_requestMap.get("NETWORK_CODE"),requestedVoucherCount,reqService);
		
            VOMSVoucherVO vomsVO = null;
		if(voucherList.size()>1){		
                String [] pin= new String[voucherList.size()];
                String [] serialNo= new String[voucherList.size()];
            	for(int i= 0 ;i <voucherList.size();i++){
            	vomsVO = (VOMSVoucherVO) voucherList.get(i);
            	 if (_log.isDebugEnabled()){
                     _log.debug("validate", "vomsVO.getExpiryDate()=" + vomsVO.getExpiryDate());
                 }
                 _requestMap.put("VOUCHER_EXPIRY_DATE",BTSLUtil.getDateStringFromDate(vomsVO.getExpiryDate(),"yyyy-MM-dd"));
                 _requestMap.put("EXPIRY_DATE",BTSLUtil.getDateStringFromDate(vomsVO.getExpiryDate(),"yyyy-MM-dd"));
                  pin[i]=vomsVO.getPinNo();
                  serialNo[i]=vomsVO.getSerialNo();
                  if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue())
                     _requestMap.put("RECEIVER_PAYABLE_AMT", String.valueOf(vomsVO.getPayableAmount()));
            	}
            	_requestMap.put("PIN", String.join(",", pin));
                _requestMap.put("SERIAL_NUMBER", String.join(",", serialNo));
            if (vomsDAO.insertDetailsInVoucherAudit(con, vomsVO) > 0)
                mcomCon.finalCommit();
            else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VOMSHandler[validate]", "", "", networkCode, "Not able to add Serial No=" + vomsVO.getSerialNo() + " in voucher history tables");
            }
            if (_log.isDebugEnabled()){
                _log.debug("validate", "vomsVO.getExpiryDate()=" + vomsVO.getExpiryDate());
            }
    		}
		else
		{
		 vomsVO=(VOMSVoucherVO)voucherList.get(0);
            _requestMap.put("VOUCHER_EXPIRY_DATE",BTSLUtil.getDateStringFromDate(vomsVO.getExpiryDate(),"yyyy-MM-dd"));
            _requestMap.put("EXPIRY_DATE",BTSLUtil.getDateStringFromDate(vomsVO.getExpiryDate(),"yyyy-MM-dd"));
            _requestMap.put("SERIAL_NUMBER", vomsVO.getSerialNo());
            _requestMap.put("PIN", vomsVO.getPinNo());
            if (!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue())
                _requestMap.put("RECEIVER_PAYABLE_AMT", String.valueOf(vomsVO.getPayableAmount()));

		 if(vomsDAO.insertDetailsInVoucherAudit(con,vomsVO)>0)
                	con.commit();
            	else
            		{
                                EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VOMSHandler[validate]","","",networkCode,"Not able to add Serial No="+vomsVO.getSerialNo()+" in voucher history tables");
            		}
		}
            // Set the transaction status and other details in map
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("PRODUCT_ID",productVO.getProductID());
            _requestMap.put("TALK_TIME", String.valueOf(productVO.getTalkTime()));
            _requestMap.put("VALIDITY", String.valueOf(productVO.getValidity()));
            _requestMap.put("PAYABLE_AMT", String.valueOf(requestedAmt));
	    _requestMap.put("EXPIRY_DATE_STR",BTSLUtil.getDateTimeStringFromDate(vomsVO.getExpiryDate(),"ddMMyyyy"));
             _requestMap.put("SERIAL_NUMBER",_requestMap.get("SERIAL_NUMBER"));
	     _requestMap.put("PIN",_requestMap.get("PIN"));
            if(!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYAMT_MRP_SAME))).booleanValue())
            	_requestMap.put("RECEIVER_PAYABLE_AMT",String.valueOf(vomsVO.getPayableAmount()));

        } catch (BTSLBaseException be) {
            if (con != null) {
                try {
                    mcomCon.finalRollback();
                    
                } catch (Exception e) {
                }
            }
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    mcomCon.finalRollback();
                } catch (Exception e1) {
                }
            }
            _log.error("validate", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSHandler[validate]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while validate in =" + _requestMap.get("INTERFACE_ID") + " Exception =" + e.getMessage());
            throw e;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("VOMSHandler#validate");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled())
                _log.debug("validate", "Exited _requestMap=" + _requestMap);
        }
    }

    /**
     * credit Method is used for updating the status to consume in voms_vouches
     * table.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */

    public void credit(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("credit Entered ", " _requestMap: " + _requestMap);
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            _requestMap = p_map;
            _inTXNID = InterfaceUtil.getINTransactionID();
            _requestMap.put("IN_TXN_ID", _inTXNID);
            Date currDate = InterfaceUtil.getDateTimeFromDateString((String) _requestMap.get("TRANSFER_DATE"));
            String currentStatus = (String) _requestMap.get("UPDATE_STATUS");
            String previousStatus = (String) _requestMap.get("PREVIOUS_STATUS");
            String modifiedBy = (String) _requestMap.get("SENDER_USER_ID");
            String statusChangeSource = (String) _requestMap.get("SOURCE");
            // Update the voucher status to consume
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            VOMSVoucherVO vomsVO = new VOMSVoucherVO();
            // Update the Voucher status to under process if the previous status
            // is enabled.
            vomsVO.setPreviousStatus(previousStatus);
            vomsVO.setCurrentStatus(currentStatus);
            vomsVO.setModifiedBy(modifiedBy);
            vomsVO.setModifiedOn(currDate);
            vomsVO.setStatusChangeSource(statusChangeSource);
            vomsVO.setStatusChangePartnerID(modifiedBy);
            vomsVO.setSerialNo((String) _requestMap.get("SERIAL_NUMBER"));
            vomsVO.setTransactionID((String) _requestMap.get("TRANSACTION_ID"));
            vomsVO.setUserLocationCode((String) _requestMap.get("NETWORK_CODE"));
            VOMSVoucherDAO vomsDAO = new VOMSVoucherDAO();
            if (VOMSVoucherDAO.updateVoucherStatus(con, vomsVO) > 0) {
                if (vomsDAO.insertDetailsInVoucherAudit(con, vomsVO) > 0)
                    mcomCon.finalCommit();
                else {
                    mcomCon.partialRollback();
                    throw new BTSLBaseException(InterfaceErrorCodesI.VOMS_ERROR_INSERTION_AUDIT_TABLE);
                }
            } else
                throw new BTSLBaseException(InterfaceErrorCodesI.VOMS_ERROR_UPDATION);
            _requestMap.put("TRANSACTION_STATUS", InterfaceErrorCodesI.SUCCESS);
            _requestMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.SUCCESS);
        } catch (BTSLBaseException be) {
            if (con != null) {
                try {
                   mcomCon.finalRollback();
                } catch (Exception e) {
                }
            }
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    mcomCon.finalRollback();
                } catch (Exception e1) {
                }
            }
            _log.error("credit", "Exception e:" + e.getMessage() + InterfaceUtil.getPrintMap(_requestMap));
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSHandler[credit]", _referenceID, _msisdn, (String) _requestMap.get("NETWORK_CODE"), "Exception while updating voucher =" + _requestMap.get("SERIAL_NUMBER") + " status to " + _requestMap.get("UPDATE_STATUS") + " for Interface=" + _requestMap.get("INTERFACE_ID") + " ");
            throw e;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("VOMSHandler#credit");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled())
                _log.debug("credit", "Exited _requestMap=" + _requestMap);
        }

    }

    /**
     * debit Method is used for updating the status to enable in voms_vouches
     * table.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */

    public void debit(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("debit Entered ", " _requestMap: " + _requestMap);
        try {
            credit(p_map);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("debit Exited ", " _requestMap: " + _requestMap);
        }
    }

    /**
     * creditAdjust Method is used for updating the status to ambigious in
     * voms_vouches table.
     * 
     * @param p_map
     *            HashMap
     * @throws BTSLBaseException
     *             , Exception
     */

    public void creditAdjust(HashMap p_map) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("creditAdjust Entered ", " _requestMap: " + _requestMap);
        try {
            credit(p_map);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("creditAdjust Exited ", " _requestMap: " + _requestMap);
        }
    }

    public void debitAdjust(HashMap p_map) {
    }

    /**
     * This method would be used to adjust the validity of the subscriber
     * account at the IN.
     * 
     * @param HashMap
     *            p_requestMap
     * @throws BTSLBaseException
     *             , Exception
     */
    public void validityAdjust(HashMap p_requestMap) throws BTSLBaseException, Exception {
    }// end of validityAdjust
}
