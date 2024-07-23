package com.btsl.pretups.channel.transfer.requesthandler;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
////import org.apache.struts.action.ActionForward;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ErrorMap;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchTransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchC2CFileProcessLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.o2c.service.C2CBulkApprovalRequestVO;
import com.web.pretups.channel.transfer.businesslogic.C2CBatchTransferWebDAO;

@Service("C2CBulkApprovalServiceI")
class C2CBulkApprovalServiceImpl implements C2CBulkApprovalServiceI {
	protected final Log log = LogFactory.getLog(getClass().getName());
	

	@Override
	public void loadAllC2cBulkApprovalDetails(OAuthUser oAuthUserData, C2cBatchesApprovalDetailsVO response,
			HttpServletResponse responseSwag, String category) throws BTSLBaseException {

		final String methodName = "loadAllC2cBulkApprovalDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		//ActionForward forward = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(lang, country);
		try {

			final String statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','"
					+ PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL + "'";
			
			final C2CBatchTransferWebDAO c2cBatchTransferWebDAO = new C2CBatchTransferWebDAO();
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			UserDAO userDao = new UserDAO();
			
			final ChannelUserVO sessionUserVO = userDao.loadAllUserDetailsByLoginID( con, oAuthUserData.getData().getLoginid() );
			final ArrayList<C2CBatchMasterVO> c2cBatchMasterVOList;
			
			if(sessionUserVO.getUserType().equals(PretupsI.USER_TYPE_STAFF)) {
				c2cBatchMasterVOList = c2cBatchTransferWebDAO.loadBatchC2CMasterDetailsForTxrAndWdr(con,
						sessionUserVO.getParentID(), statusUsed, PretupsI.CHANNEL_TRANSFER_ORDER_NEW, category);
			}
			else {
				c2cBatchMasterVOList = c2cBatchTransferWebDAO.loadBatchC2CMasterDetailsForTxrAndWdr(con,
						sessionUserVO.getUserID(), statusUsed, PretupsI.CHANNEL_TRANSFER_ORDER_NEW, category);
			}
			
			
			ArrayList<C2CBatchMasterVO> batchTransferList = new ArrayList<C2CBatchMasterVO>();
			ArrayList<C2CBatchMasterVO> batchWithdrawList = new ArrayList<C2CBatchMasterVO>();
			
			for(C2CBatchMasterVO c2cBatchMasterVO : c2cBatchMasterVOList) {
				if( c2cBatchMasterVO.getTransferSubType().equals(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER) ){
					batchTransferList.add(c2cBatchMasterVO);
				} else {
					batchWithdrawList.add(c2cBatchMasterVO);
				}
			}
			response.setC2cBatchTransferList(batchTransferList);
			response.setC2cBatchWithdrawalList(batchWithdrawList);
			response.setStatus(Integer.toString(HttpStatus.SC_OK));
			if(c2cBatchMasterVOList.size() > 0) {
				response.setMessage(PretupsI.SUCCESS);
				response.setMessageCode( Integer.toString( HttpStatus.SC_OK) );
			} else {
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_DETAIL_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.NO_DETAIL_FOUND);
			}
			responseSwag.setStatus(HttpStatus.SC_OK);

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			throw be;
		} catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			throw new BTSLBaseException(e.getMessage());
		} finally {
			// if connection is not null then close the connection
			
			try {
				if (mcomCon != null) {
					mcomCon.close("C2CBulkApprovalServiceImpl#loadAllC2cBulkApprovalDetails");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}


			/*if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited forward=" + forward);
			}*/
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public BaseResponseMultiple processc2cBulkApproval(MultiValueMap<String, String> headers,C2CBulkApprovalRequestVO c2cBulkApprovalRequestVO,
			HttpServletResponse responseSwag)
	{
		final String methodName = "processc2cBulkApproval";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		BaseResponseMultiple response = new BaseResponseMultiple();
		
		oAuthUser = new OAuthUser();
		oAuthUserData = new OAuthUserData();

		oAuthUser.setData(oAuthUserData);
		try
		{
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
		}
		catch (Exception be) 
		{
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(Integer.toString( HttpStatus.SC_UNAUTHORIZED) );
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}
		}

		final String loginID = oAuthUser.getData().getLoginid();
		
		
		c2cBulkApprovalRequestVO.getApprovalList().forEach(c2cBulkApprovalRequestData -> {
		
		
			
			final ProcessBL processBL = new ProcessBL();
	        UserVO userVO = null;
			Connection con = null;
			MComConnectionI mcomCon = null;
	        ChannelUserVO channelUserVO1 =null;
	        ArrayList errorList = null;
	        UserDAO userDao = new UserDAO();
	        C2CBatchMasterVO c2cBatchMasterVO = null;
	        String currentLevel = null;

	        final String arr[] = new String[1];
	        boolean showLogs = true;
	       
	        ProcessStatusVO processVO = null;
	        boolean processRunning = true;
	        ArrayList<C2CBatchMasterVO> c2cBatchMasterVOList = null;
	        C2CBatchTransferWebDAO c2cBatchTransferWebDAO = new C2CBatchTransferWebDAO();
	        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	        String statusUsed = null;
	        Locale locale = new Locale(defaultLanguage, defaultCountry);
			try 
	        {
	        	mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				
				userVO = userDao.loadAllUserDetailsByLoginID(con, loginID);
				
	            processVO = processBL.checkProcessUnderProcessNetworkWise(con, PretupsI.C2C_BATCH_PROCESS_ID,userVO.getNetworkID());
	            statusUsed = "'" + PretupsI.CHANNEL_TRANSFER_ORDER_NEW + "','" + PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE + "'";
	            
	            
	            
	            if (processVO != null && !processVO.isStatusOkBool()) {
	                processRunning = false;
                    throw new BTSLBaseException(PretupsErrorCodesI.C2C_BATCH_ALREADY_RUNNING);                    

	            }
	            
	            mcomCon.partialCommit();
	            processVO.setNetworkCode(userVO.getNetworkID());
			
	            if("W".equalsIgnoreCase(c2cBulkApprovalRequestData.getTxnType()))
	            {
	            	c2cBatchMasterVOList = c2cBatchTransferWebDAO.loadBatchC2CMasterDetailsForWdr(con, userVO.getUserID(), statusUsed,
		                    PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
	            }
	            else
	            {
	            	c2cBatchMasterVOList = c2cBatchTransferWebDAO.loadBatchC2CMasterDetailsForTxr(con, userVO.getUserID(), statusUsed,
		                    PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
	            }
	            
	            
			
            String remark = null;
            remark = c2cBulkApprovalRequestData.getRemarks();
            
            currentLevel = PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE;
		
            //Have remarks check only in case of approval
            if (BTSLUtil.isNullString(remark) || remark.length() > 100) {
                showLogs = false;
                throw new BTSLBaseException(PretupsErrorCodesI.REMARKS_REQD_LIMIT);                    

            }
            C2CBatchTransferDAO c2cBatchTransferDAO = new C2CBatchTransferDAO();
            
            UserGeographiesVO userGeoVO = null;
            final ArrayList<UserGeographiesVO> userGeographyList = userVO.getGeographicalAreaList();
            if (userGeographyList != null && !userGeographyList.isEmpty()) {
                userGeoVO = (UserGeographiesVO) userGeographyList.get(0);
                userVO.setGeographicalCode(userGeoVO.getGraphDomainCode());
            }
            
            
            c2cBatchMasterVO = (C2CBatchMasterVO) c2cBatchMasterVOList.stream()
            		  .filter(customer -> c2cBulkApprovalRequestData.getTxnID().equals(customer.getBatchId()))
            		  .findAny()
            		  .orElse(null);
            		
            if(c2cBatchMasterVO == null)
            {
            	throw new BTSLBaseException(PretupsErrorCodesI.INVALID_BATCH_ID);
            }
            
            LinkedHashMap<String, C2CBatchItemsVO> downloadDataMap = c2cBatchTransferDAO.loadBatchItemsMap(con, c2cBulkApprovalRequestData.getTxnID(), statusUsed);
            
            if (downloadDataMap != null && !downloadDataMap.isEmpty()) {

                C2CBatchItemsVO c2cBatchItemVO = null;
                final Iterator<String> iterator = downloadDataMap.keySet().iterator();
                String key = null;
                int i = 1;
                while (iterator.hasNext()) {
                    key = (String) iterator.next();
                    c2cBatchItemVO = (C2CBatchItemsVO) downloadDataMap.get(key);
                    c2cBatchItemVO.setRecordNumber(i + 1);
                    if ("Y".equals(c2cBulkApprovalRequestData.getApprovalStatus())) {
                        c2cBatchItemVO.setApproverRemarks(remark);
                        c2cBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                    }
                    else
                    {
                    	c2cBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    }

                    i++;
                }
                
                		
                	

                		
                	
                // Check if batch is modified or not.
                final boolean isModified = c2cBatchTransferDAO.isBatchModified(con, c2cBatchMasterVO.getModifiedOn().getTime(), c2cBulkApprovalRequestData.getTxnID());
                if (isModified) {
                    throw new BTSLBaseException(PretupsErrorCodesI.C2C_BATCH_ALREADY_MODIFIED);                    
                    
                } else {
                    final int updateCount = c2cBatchTransferDAO.updateBatchStatus(con, c2cBatchItemVO.getBatchId(), PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_UNDERPROCESS,
                        PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
                    if (updateCount <= 0) {
                     
                    	mcomCon.partialRollback();
                        throw new BTSLBaseException(this, methodName, "error.general.processing", "firstPage");
                    }
                
                    mcomCon.partialCommit();
                     if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
	                  channelUserVO1 = (ChannelUserVO) userVO;
                     }
                    errorList = c2cBatchTransferDAO.closeOrderByBatch(con, downloadDataMap, currentLevel, channelUserVO1, c2cBatchMasterVO, null, locale,
                    		c2cBulkApprovalRequestData.getLanguage1(), c2cBulkApprovalRequestData.getLanguage2(),channelUserVO1);
                    // success message
                    if (errorList == null || errorList.isEmpty()) {
                        arr[0] = String.valueOf(downloadDataMap.size());
                        
                        response.setStatus(String.valueOf(HttpStatus.SC_OK));
        				response.setMessageCode(PretupsErrorCodesI.C2C_BATCH_APPR_SUCCESS);
        				String resMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_BATCH_APPR_SUCCESS, arr);
        				response.setMessage(resMsg);
        				
                    } else if (errorList.size() == downloadDataMap.size()) {
                    	 String processedRecs = String.valueOf(errorList.size());
                         String totalRecords = String.valueOf(downloadDataMap.size());
                         ErrorMap errorMap = new ErrorMap();
                         errorMap.setMasterErrorList(errorList);
                         response.setErrorMap(errorMap);
                         throw new BTSLBaseException(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_BATCH_APPR_FULL_FAIL, null));
                    }
                    else {
                        arr[0] = String.valueOf(downloadDataMap.size() - errorList.size());

                        String processedRecs = String.valueOf(errorList.size());
                        String totalRecords = String.valueOf(downloadDataMap.size());
                        //Set 400 
                        throw new BTSLBaseException(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2C_BATCH_APPR_PARTIAL_FAIL, null));                    }
                }
            }
            }
                // end of else
			catch (BTSLBaseException e) 
			{
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
				response.setMessageCode(e.getMessage());
				response.setMessage(resmsg);
			} 
			catch (Exception e) 
			{
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
				String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
				response.setMessageCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
				response.setMessage("error.general.processing");
			}
              finally
            {
                // To make the status of the batch c2c process as complete into the
                // table so that only
                // one instance should be executed for batch c2c

                if (processRunning) {
                    try {
                        processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                        final ProcessStatusDAO processDAO = new ProcessStatusDAO();
                        if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                       
                        	mcomCon.finalCommit();
                        } else {
                        
                        	mcomCon.finalRollback();
                        }
                    } catch (Exception e) {
                        if (log.isDebugEnabled()) {
                            log.error(methodName, " Exception in update process detail for batch c2c approval process " + e.getMessage());
                        }
                        log.errorTrace(methodName, e);
                    }
                }
                // ends here
                // if connection not null then close the connection
    			if (mcomCon != null) {
    				mcomCon.close("C2CBatchTransferApprovalAction#processBatchApprove");
    				mcomCon = null;
    			}
                if (showLogs) {
                    BatchC2CFileProcessLog.c2cBatchMasterLog(methodName, c2cBatchMasterVO, "FINALLY BLOCK : Batch processed",
                        "CURRENT LEVEL=" + currentLevel + ", PROCESSED RECORD=" + arr[0]);
                }
               
            
            }
            	
            
		
		
		});
		
		
		return response;

}
}