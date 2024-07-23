/*
 * Created on Jun 21, 2006
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.voms.voucher.web;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;

import com.btsl.logging.LogFactory;

import com.btsl.common.BTSLBaseException;
//import com.btsl.common.BTSLDispatchAction;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EMailSender;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.OnlineChangeVoucherBatchStatus;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchesDAO;
import com.btsl.voms.voucher.businesslogic.VomsChangeBatchStatusThread;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebDAO;
import com.web.voms.voucher.businesslogic.VomsBatchesWebDAO;

/**
 * @(#)VomsVoucherAction.java
 *                            Copyright(c) 2006, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            vikas.yadav 01/07/2006 Initial Creation
 * 
 * 
 *                            This class is used for Change Voucher
 *                            Status(Genrated as well as Others) of EVD
 */
public class VomsVoucherAction  {



    protected static final Log LOG = LogFactory.getLog(VomsVoucherAction.class.getName());
    
    
    /**
     * This method first check the batch is under process or not.if not under
     * process then get a batch no and add a new batch.
     * if batch is under process then change under process status (on the basis
     * of expiry time check)
     * 
     * @param p_batchVO
     *            BatchVO
     * @param p_con
     *            Connection
     * @param p_status
     *            Sting
     * @param p_expiryHr
     *            String
     * @param p_maxErrorAllowed
     *            long
     * @param p_request
     *            HttpServletRequest
     * @param p_processScreen
     *            int
     * @return batchNo String
     * @throws BTSLBaseException
     */
    public String changeVoucherStatus(Connection p_con, VomsBatchVO p_batchVO, String p_status, String p_expiryHr, long p_maxErrorAllowed, HttpServletRequest p_request, int p_processScreen) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("changeVoucherStatus()", " Entered.p_status=" + p_status + "p_expiryHr=" + p_expiryHr + " p_maxErrorAllowed=" + p_maxErrorAllowed + " p_processScreen="+p_processScreen);
        }
        final String METHOD_NAME = "changeVoucherStatus";
        ListValueVO listValVO = null;
        int expTime = 0;
        Connection con = p_con;MComConnectionI mcomCon = null;
        ArrayList batchList = null;
        ArrayList returnBatchList = null;
        UserVO userVO = null;
        String batchNo = null;
        String batchNumber = null;
        VomsBatchesDAO batchesDAO = null;
        VomsBatchesWebDAO batcheswebDAO = null;
        VomsUtil _vomsUtil = null;

        try {
            batchesDAO = new VomsBatchesDAO();
            batcheswebDAO = new VomsBatchesWebDAO();
            batchNumber = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
            _vomsUtil = new VomsUtil();
            batchNo = _vomsUtil.formatVomsBatchID(p_batchVO, batchNumber);
            if ("null".equalsIgnoreCase(batchNo) || BTSLUtil.isNullString(batchNo)) {
                LOG.error("changeVoucherStatus", "Not able to get the Batch no.");
                throw new BTSLBaseException(this, "changeVoucherStatus", "btsl.nextbatch.error", "errorStatus");

            }
            returnBatchList = new ArrayList();
            p_batchVO.setBatchNo(batchNo);
            p_batchVO.setProcessScreen(p_processScreen);
            returnBatchList.add(p_batchVO);
            if (LOG.isDebugEnabled()) {
                LOG.debug("changeVoucherStatus(): batchNo= ", batchNo);
            }

            // Add the new batch
            int addCount = batchesDAO.addBatch(con, returnBatchList);
            if (addCount > 0) {
                VomsBatchInfoLog.addBatchLog(returnBatchList);
            } 
            else {
                LOG.error("changeVoucherStatus", "Not able to add in batch");
                throw new BTSLBaseException(this, "changeVoucherStatus", "btsl.addbatch.error.addbatchfail", "errorStatus");// need
            }
        } catch (BTSLBaseException bex) {
            try {
            	con.rollback();

            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error("changeVoucherStatus()", "changeVoucherStatus() Exception while rollback ");
            }
            LOG.errorTrace(METHOD_NAME, bex);
            throw bex;
        }

        catch (Exception e) {

            LOG.error("changeVoucherStatus()", "Exception : " + e);
            try {
            	con.rollback();
            } catch (Exception ex) {
                LOG.errorTrace(METHOD_NAME, ex);
                LOG.error("changeVoucherStatus()", "changeVoucherStatus() Exception while rollback ");
            }
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VomsVoucherAction[changeVoucherStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "changeVoucherStatus()", "error.general.processing");
        }

        finally {

        	if(mcomCon != null){mcomCon.close("VomsVoucherAction#changeVoucherStatus");mcomCon=null;}
            if (LOG.isDebugEnabled()) {
                LOG.debug("changeVoucherStatus()", "changeVoucherStatus() Exiting");
            }
        }
        return batchNo;
    }

	public String batchOnOrOffline(ArrayList<VomsBatchVO> pBatchVOList,
			long pMaxErrorAllowed, HttpServletRequest pRequest,
			int pProcessScreen, String batchNo) throws BTSLBaseException {
		String property = Constants.getProperty("VOMS_CHANGE_GEN_STATUS_ONLINE_COUNT");
		if(property.isEmpty())//to throw an exception or proceed with some hardcoded default value?
		{
			property="1000";
		}
		if(pBatchVOList.get(0).getNoOfVoucher()<=Integer.parseInt(property)){
			if (LOG.isDebugEnabled()) {
                LOG.debug("changeVoucherStatus()", "Before diverting process to thread");
            }
			
			if(pBatchVOList.get(0).getProcess().equals(VOMSI.BATCH_PROCESS_ENABLE))
			{
				for(int i=0;i<pBatchVOList.size();i++)
				{
					VomsChangeBatchStatusThread changeStatusThread = new VomsChangeBatchStatusThread((VomsBatchVO)pBatchVOList.get(i), pMaxErrorAllowed, pRequest, pProcessScreen);
					changeStatusThread.start();
				}
			}
			else
			{
				Thread a = new Thread(new OnlineChangeVoucherBatchStatus(pRequest));
				a.start();
			}
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("changeVoucherStatus()", "After diverting process to thread with batchNo" + batchNo);
			}
		}
		return batchNo;
	}

	
    
	/**
	 *  Send Email for - Change status Operation
	 *  Attachment:  Success and Fail log files.
	 */
	
    /*public void sendEmail(MessageResources messages, long successCounter, long failCounter,
			String batchNo, String[] pathsOfFiles, String[] filesNamesTobeDisplayed, String userId, String emailId) {

		final String methodName = "sendEmail";
		String cc = PretupsI.EMPTY;
		String bcc = PretupsI.EMPTY;
		String from = null;
		String subject = null;
		Locale locale = null;
	    
		try {

			locale = BTSLUtil.getSystemLocaleForEmail();
			
			if(messages != null) {
				from = messages.getMessage(locale, "email.notification.changestatus.log.file.from");
			}else {
				from = BTSLUtil.getMessage(locale,"email.notification.changestatus.log.file.from");
			}
			
			if(messages != null) {
				subject = messages.getMessage(locale, "email.notification.changestatus.log.file.subject");
			}else {
				subject = BTSLUtil.getMessage(locale,"email.notification.changestatus.log.file.subject");
			}
			
			StringBuffer notifyContent = new StringBuffer();

			if(messages != null) {
			notifyContent.append(messages.getMessage(locale, "email.notification.changestatus.log.file.body.part1")
					.replaceAll("<Batch ID>", batchNo));
			}else {
				notifyContent.append(BTSLUtil.getMessage(locale,"email.notification.changestatus.log.file.body.part1")
						.replaceAll("<Batch ID>", batchNo));
				
				
			}
			
			if (successCounter > 0) {
				if(messages != null) {
					
				notifyContent.append(messages.getMessage(locale, "email.notification.changestatus.log.file.body.part2")
						.replaceAll("<n>", successCounter + ""));
				}else {
					notifyContent.append(BTSLUtil.getMessage(locale,"email.notification.changestatus.log.file.body.part2")
							.replaceAll("<n>", successCounter + ""));
					
				}
			}

			if (failCounter > 0) {
				if(messages != null) {
				
				notifyContent.append(messages.getMessage(locale, "email.notification.changestatus.log.file.body.part3")
						.replaceAll("<m>", failCounter + ""));
				}else {
					notifyContent.append(BTSLUtil.getMessage(locale,"email.notification.changestatus.log.file.body.part3")
							.replaceAll("<m>", failCounter + ""));
					
				}
			}
			LOG.debug(methodName, "Sending Email , notifyContent " + notifyContent);

			
			EMailSender.sendMailMultiAttachments(emailId, from, bcc, cc, subject, notifyContent.toString(), true,
					pathsOfFiles, filesNamesTobeDisplayed);

		} catch (Exception e) {
			LOG.debug(methodName, "Exception while Sending Email , description " + e);
			e.printStackTrace();
		}
	}
*/

    /**
     * Method getServiceTypeSelectorList
     * This method is used to to find available selectors list for given service
     * type .
     * 
     * @author harsh.dixit
     * @param serviceType
     *            String
     * @return ArrayList
     * @date 29-May-2013
     */
    private ArrayList<ServiceSelectorMappingVO> getServiceTypeSelectorList(String serviceType) {
        HashMap serviceSelectorMap = ServiceSelectorMappingCache.getServiceSelectorMap();
        ArrayList serviceSelectorList = new ArrayList();
        ServiceSelectorMappingVO serviceSelectorMappingVO = null;
        ListValueVO lv = null;
        Iterator itr = serviceSelectorMap.keySet().iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            if (key.startsWith(serviceType + "_")) {
                serviceSelectorMappingVO = (ServiceSelectorMappingVO) serviceSelectorMap.get(key);
                lv = new ListValueVO(serviceSelectorMappingVO.getSelectorName(), serviceSelectorMappingVO.getSelectorCode());
                serviceSelectorList.add(lv);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getServiceTypeSelectorList()", "exited" + serviceSelectorList.size());
        }
        return serviceSelectorList;
    }

    public void populateStatusList(VomsVoucherForm theForm, String curStatus) {
    	final String methodName = "populateStatusList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        theForm.setStatusList(VomsUtil.filterChangeStatusList(theForm.getType(), LookupsCache.loadLookupDropDown(VOMSI.LOOKUP_VOUCHER_STATUS, true), curStatus));
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exited");
        }
    }
}
