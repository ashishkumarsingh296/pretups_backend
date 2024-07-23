package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.SerializationUtils;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.inter.module.InterfaceModuleI;
import com.btsl.pretups.logging.NetworkStockLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.logging.SOSSettlementLog;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;

public class ChannelSoSWithdrawBL {
    private static Log log = LogFactory.getLog(ChannelSoSWithdrawBL.class.getName());
/*request type to be LR or SOS*/
    public void autoChannelSoSSettlement(ChannelTransferVO vo,String requestType) throws BTSLBaseException {
        final String methodName = "autoChannelSoSSettlement";
        if (log.isDebugEnabled()) {
            log.info(methodName, " Enter UserBalancesVO: " + vo+"requestType"+requestType);
        }
        try {
            final ArrayList<ChannelTransferVO> list = new ArrayList<ChannelTransferVO>();
            ChannelTransferVO vo1 = new ChannelTransferVO();
            //BeanUtils.copyProperties(vo1, vo);
            vo1 = SerializationUtils.clone(vo);
            list.add(vo1);
            final ChannelSoSWithdrawThread mrt = new ChannelSoSWithdrawThread(vo1,requestType);
            final Thread t = new Thread(mrt);
            t.start();
        } catch (Exception e) {
        	log.error(methodName, "Exception " + e);
			log.errorTrace(methodName, e);
            
        } finally {
            if (log.isDebugEnabled()) {
                log.info(methodName, " End of Main Thread... ");
            }
        }
    }
}

class ChannelSoSWithdrawThread implements Runnable {
    private static Log log = LogFactory.getLog(ChannelSoSWithdrawBL.class.getName());
    private ChannelTransferVO vo = null;
    private String requesttype = null;
    public ChannelSoSWithdrawThread( ChannelTransferVO vo1,String requestType) {
        this.vo = vo1;
        this.requesttype=requestType;
    }

    public void run() {
        final String methodName = "run";
        if (log.isDebugEnabled()) {
            log.info(methodName, " Enter vo: " + vo+"requesttype"+requesttype);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            Thread.sleep(300);
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            
            
			process(con, vo,requesttype);
            
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelSoSWithdrawThread#run");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.info(methodName, " Exiting : ");
            }
        }
    }

    public void process(Connection p_con, ChannelTransferVO chnlTransferVO,String requestType) throws BTSLBaseException {

        final String METHOD_NAME = "process";
        LogFactory.printLog(METHOD_NAME, "ChannelTransferVO"+ chnlTransferVO+"requestType"+requestType, log);
        ChannelUserDAO channelUserDAO = null;
        ChannelUserVO channelUserVO = null;
        UserTransferCountsVO fromCountVO = null;
        UserTransferCountsVO toCountVO = null;
        ChannelUserVO prtOwnUserVO = null;
        UserBalancesDAO userBalancesDAO = null;
        ChannelTransferVO channelTransferVo = null;
        ChannelTransferItemsVO transferItemsVO = null;
        ChannelTransferDAO channelTransferDAO = null;
        final Date currentDate = new Date();

        try {
        	final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
        	channelUserDAO = new ChannelUserDAO();
        	channelUserVO = new ChannelUserVO();
        	fromCountVO = new UserTransferCountsVO();
        	toCountVO = new UserTransferCountsVO();

        	userBalancesDAO = new UserBalancesDAO();

        	channelTransferDAO = new ChannelTransferDAO();
        	KeyArgumentVO keyArgumentVO = null;
        	final UserDAO userDAO = new UserDAO();
        	String[] arr = null;
        	Locale locale = null;
        	BTSLMessages btslMessage = null;
        	BTSLMessages btslMessage1 = null;
        	PushMessage pushMessage = null;
        	PushMessage pushMessage1 = null;
        	String[] arr1 = null;
        	ArrayList channelTransferItemVOList = new ArrayList();
        	boolean settleMentForYABX = false;
        	channelTransferVo = new ChannelTransferVO();
        	try {
        		long sosWithdrawAmt = 0;
        		long lrWithdrawAmt = 0;
        		if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE))
        			lrWithdrawAmt=chnlTransferVO.getLrWithdrawAmt();
        		final String fromUserID = chnlTransferVO.getToUserID();
        		channelUserVO = (ChannelUserVO) channelUserDAO.loadChannelUserForSOS(p_con, chnlTransferVO.getToUserID());
        		
        		if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
        		
            	sosWithdrawAmt = channelUserVO.getSosAllowedAmount();
        		if(PretupsI.SOS_PARENT.equalsIgnoreCase((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, chnlTransferVO.getNetworkCode())))
        			prtOwnUserVO = (ChannelUserVO)channelUserDAO.loadChannelUserForSOS(p_con,channelUserVO.getParentID());
        		else if(PretupsI.SOS_OWNER.equalsIgnoreCase((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, chnlTransferVO.getNetworkCode())))
        			prtOwnUserVO = (ChannelUserVO)channelUserDAO.loadChannelUserForSOS(p_con,channelUserVO.getOwnerID());
        		}

				final UserPhoneVO phoneVO = userDAO.loadUserPhoneVO(p_con, fromUserID);
        		String toUserID = null;
        		UserPhoneVO prtOwnphoneVO = null;
        		if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
        		if(!(PretupsI.SOS_NETWORK.equalsIgnoreCase((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, chnlTransferVO.getNetworkCode())))){
        			toUserID = prtOwnUserVO.getUserID();
        			prtOwnphoneVO = userDAO.loadUserPhoneVO(p_con, toUserID);
        		}else{

        			if (log.isDebugEnabled()) {
        				log.debug("process", "DOMAINCODE_FOR_SOS_YABX" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DOMAINCODE_FOR_SOS_YABX)) +",chnlTransferVO.getDomainCode():"+chnlTransferVO.getDomainCode());
        			}
        			
        			
        			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_ALLOWED_FOR_YABX))).booleanValue()) {
        				toUserID = PretupsI.OPERATOR_TYPE_OPT;

        				if(!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DOMAINCODE_FOR_SOS_YABX))))
        				{
        					for( String domainCode : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DOMAINCODE_FOR_SOS_YABX)).split(","))
        					{
        						if(domainCode.equals(chnlTransferVO.getDomainCode()))
        						{
        							toUserID = chnlTransferVO.getFromUserID();
        							settleMentForYABX=true;
        							break;
        						}
        						else
        						{
        							toUserID = chnlTransferVO.getFromUserID();
        						}

        					}
        				}
        				else
        				{
        					toUserID = chnlTransferVO.getFromUserID();
        				}
        			}
        			else
        			{
        				toUserID = chnlTransferVO.getFromUserID();
        			}
        			
        			
        			
        		}
        		}
        		if(prtOwnUserVO!= null){
        			
        			if (prtOwnUserVO.getStatus().equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND)) {
            			pushMessage=new PushMessage(prtOwnUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{prtOwnUserVO.getMsisdn()}),null,null,new Locale(prtOwnphoneVO.getPhoneLanguage(),prtOwnphoneVO.getCountry()),prtOwnUserVO.getNetworkCode());
            			pushMessage.push();
            			if (log.isDebugEnabled()) {
            				log.debug("process", "c2c withdraw is not possible as parent/owner status is suspended , parent/owner status :" + prtOwnUserVO.getStatus());
            			}
						
            		}
            		if (prtOwnUserVO.getInSuspend().equalsIgnoreCase(PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND)) {
            			pushMessage=new PushMessage(prtOwnUserVO.getMsisdn(),new BTSLMessages(PretupsErrorCodesI.AUTO_CHNL_CONFIGURATION_ERROR_SNDR,new String[]{prtOwnUserVO.getMsisdn()}),null,null,new Locale(prtOwnphoneVO.getPhoneLanguage(),prtOwnphoneVO.getCountry()),prtOwnUserVO.getNetworkCode());
            			pushMessage.push();
            			if (log.isDebugEnabled()) {
            				log.debug("process","c2c withdraw is not possible as Parent/Owner's In suspend, sender msisdn: " + prtOwnUserVO.getMsisdn() + "parent/owner user ID :" + prtOwnUserVO.getUserID());
            			}
						
            		}
        		}
        		if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE))
        			channelTransferVo.setTransactionMode("S");
        		else if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE))
        			channelTransferVo.setTransactionMode(PretupsI.LR_TRANSACTION_MODE);
        		
        		channelTransferVo.setNetworkCode(channelUserVO.getNetworkID());
        		channelTransferVo.setNetworkCodeFor(channelUserVO.getNetworkID());
        		channelTransferVo.setCategoryCode(channelUserVO.getCategoryCode());
        		channelTransferVo.setUserMsisdn(channelUserVO.getMsisdn());
        		channelTransferVo.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
        		channelTransferVo.setDomainCode(channelUserVO.getDomainID());
        		channelTransferVo.setCreatedOn(currentDate);
        		if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
        		if(PretupsI.SOS_NETWORK.equalsIgnoreCase((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, chnlTransferVO.getNetworkCode()))){
            		channelTransferVo.setType(PretupsI.CHANNEL_TYPE_O2C);
            		channelTransferVo.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
            		channelTransferVo.setReceiverCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
            		channelTransferVo.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
            		channelTransferVo.setReceiverDomainCode(PretupsI.OPERATOR_TYPE_OPT);
            		channelTransferVo.setReceiverLoginID(PretupsI.OPERATOR_TYPE_OPT);
            		ChannelTransferBL.genrateWithdrawID(channelTransferVo);
            		channelTransferVo.setReferenceNum("required");
            		channelTransferVo.setReceiverGgraphicalDomainCode(channelTransferVo.getGraphicalDomainCode());
            		
        		}else{
            		channelTransferVo.setType(PretupsI.CHANNEL_TYPE_C2C);
            		channelTransferVo.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
            		channelTransferVo.setReceiverCategoryCode(prtOwnUserVO.getCategoryCode());
            		channelTransferVo.setActiveUserId(prtOwnUserVO.getUserID());
            		channelTransferVo.setToUserID(prtOwnUserVO.getUserID());
            		channelTransferVo.setReceiverGradeCode(prtOwnUserVO.getUserGrade());
        			ChannelTransferBL.genrateChnnlToChnnlWithdrawID(channelTransferVo);
            		channelTransferVo.setToUserCode(prtOwnUserVO.getMsisdn());
            		channelTransferVo.setReceiverDomainCode(prtOwnUserVO.getDomainID());
            		channelTransferVo.setReceiverGgraphicalDomainCode(prtOwnUserVO.getGeographicalCode());
            		channelTransferVo.setReceiverTxnProfile(prtOwnUserVO.getTransferProfileID());
            		channelTransferVo.setToUserCode(prtOwnphoneVO.getMsisdn());
        		}
        		}
        		else if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE)){
        			channelTransferVo.setType(PretupsI.CHANNEL_TYPE_O2C);
            		channelTransferVo.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
            		channelTransferVo.setReceiverCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
            		channelTransferVo.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
            		channelTransferVo.setReceiverDomainCode(PretupsI.OPERATOR_TYPE_OPT);
        			ChannelTransferBL.genrateWithdrawID(channelTransferVo);
            		channelTransferVo.setReferenceNum("required");
            		channelTransferVo.setReceiverGgraphicalDomainCode(channelTransferVo.getGraphicalDomainCode());
        		}
        		channelTransferVo.getTransferID();
        		channelTransferVo.setProductCode(chnlTransferVO.getProductCode());
        		channelTransferVo.setFromUserID(fromUserID);
        		channelTransferVo.setSenderGradeCode(channelUserVO.getUserGrade());
        		channelTransferVo.setSenderTxnProfile(channelUserVO.getTransferProfileID());
        		channelTransferVo.setSenderLoginID(channelUserVO.getLoginID());
        		channelTransferVo.setFromUserCode(channelUserVO.getMsisdn());
        		channelTransferVo.setFromEXTCODE((channelUserVO.getExternalCode()));
        		channelTransferVo.setTransferMRP(sosWithdrawAmt);
        		channelTransferVo.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        		channelTransferVo.setRequestedQuantity(sosWithdrawAmt);
        		channelTransferVo.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
        		channelTransferVo.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
        		channelTransferVo.setCommProfileSetId(channelUserVO.getCommissionProfileSetID());
        		channelTransferVo.setCommProfileVersion(channelUserVO.getCommissionProfileSetVersion());
        		channelTransferVo.setCreatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
        		channelTransferVo.setModifiedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
        		channelTransferVo.setTransferInitatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
        		channelTransferVo.setTransferDate(currentDate);
        		channelTransferVo.setModifiedOn(currentDate);
        		channelTransferVo.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        		channelTransferVo.setSource(PretupsI.REQUEST_SOURCE_SYSTEM); // as
        		channelTransferVo.setControlTransfer(PretupsI.YES);
        		channelTransferVo.setRequestGatewayCode(chnlTransferVO.getRequestGatewayCode());
        		channelTransferVo.setRequestGatewayType(chnlTransferVO.getRequestGatewayType());
        		channelTransferVo.setPayableAmount(sosWithdrawAmt);
        		channelTransferVo.setNetPayableAmount(sosWithdrawAmt);
        		channelTransferVo.setNetworkCode(chnlTransferVO.getNetworkCode());
        		channelTransferVo.setNetworkCodeFor(chnlTransferVO.getNetworkCodeFor());
        		channelTransferVo.setSosTxnId(chnlTransferVO.getTransferID());
        		channelTransferVo.setSosRequestAmount(chnlTransferVO.getRequestedQuantity());
        		channelTransferVo.setCommProfileVersion(chnlTransferVO.getCommProfileVersion());
        		if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
        		channelTransferVo.setRequestedQuantity(sosWithdrawAmt);
        		channelTransferVo.setTransferMRP(sosWithdrawAmt);
        		channelTransferVo.setPayableAmount(sosWithdrawAmt);
        		channelTransferVo.setNetPayableAmount(sosWithdrawAmt);
        		channelTransferVo.setSosStatus("ASettled");
        		}
        		else if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE)){
        			channelTransferVo.setRequestedQuantity(lrWithdrawAmt);
            		channelTransferVo.setTransferMRP(lrWithdrawAmt);
            		channelTransferVo.setPayableAmount(lrWithdrawAmt);
            		channelTransferVo.setNetPayableAmount(lrWithdrawAmt);
            		channelTransferVo.setSosStatus(PretupsI.LAST_LR_SETTLED_STATUS);
            		
        		}
        		channelTransferVo.setSosSettlementDate(new Date());
        		if(prtOwnUserVO !=  null){
        			channelTransferVo.setToUserID(prtOwnUserVO.getUserID());
        		}
        		
        		
        		transferItemsVO = new ChannelTransferItemsVO();
        				
                transferItemsVO.setSerialNum(1);
                transferItemsVO.setProductCode(chnlTransferVO.getProductCode());
                if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
                transferItemsVO.setRequiredQuantity(sosWithdrawAmt);
                transferItemsVO.setRequestedQuantity(String.valueOf(sosWithdrawAmt));
                transferItemsVO.setApprovedQuantity(sosWithdrawAmt);
                transferItemsVO.setSenderDebitQty(sosWithdrawAmt);
                transferItemsVO.setPayableAmount(sosWithdrawAmt);
    			transferItemsVO.setNetPayableAmount(sosWithdrawAmt);
                
 }
                else if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE)){
                	 transferItemsVO.setRequiredQuantity(lrWithdrawAmt);
                     transferItemsVO.setRequestedQuantity(String.valueOf(lrWithdrawAmt));
                     transferItemsVO.setApprovedQuantity(lrWithdrawAmt);
                     transferItemsVO.setSenderDebitQty(lrWithdrawAmt);
                }
                //transferItemsVO.setUnitValue(chnlTransferVO.get*val); // need to set unitvalue
                transferItemsVO.setNetworkCode(chnlTransferVO.getNetworkCode());
                channelTransferItemVOList.add(transferItemsVO);
        		channelTransferVo.setChannelTransferitemsVOList(channelTransferItemVOList);
        		int chnlTransferVOsChannelTransferitemsVOLists=(chnlTransferVO.getChannelTransferitemsVOList()).size(); 
        		for(int i=0; i<chnlTransferVOsChannelTransferitemsVOLists;i++){
        			ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) chnlTransferVO.getChannelTransferitemsVOList().get(i);
        			channelTransferVo.setProductCode(channelTransferItemsVO.getProductCode());
        			transferItemsVO.setShortName(channelTransferItemsVO.getProductCode());
        			transferItemsVO.setProductCode(channelTransferItemsVO.getProductCode());
        			transferItemsVO.setUnitValue(channelTransferItemsVO.getUnitValue());
        			  if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
        				  transferItemsVO.setReceiverCreditQty(sosWithdrawAmt);
        			  }
        			  else if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE)){
        				  transferItemsVO.setReceiverCreditQty(lrWithdrawAmt);
        			  }
        			transferItemsVO.setCommProfileDetailID(channelTransferItemsVO.getCommProfileDetailID());
        			transferItemsVO.setCommType(channelTransferItemsVO.getCommType());
        			transferItemsVO.setTax1Type(channelTransferItemsVO.getTax1Type());
        			transferItemsVO.setTax2Type(channelTransferItemsVO.getTax2Type());
        			transferItemsVO.setTax3Type(channelTransferItemsVO.getTax3Type());
        		}

        		int creditCount = 0;
        		int debitCount = 0;
        		int updateCount = 0;

        		final UserBalancesVO userBalVO = new UserBalancesVO();
        		userBalVO.setLastTransferType(channelTransferVo.getTransferType());
        		userBalVO.setLastTransferID(channelTransferVo.getTransferID());
        		userBalVO.setLastTransferOn(channelTransferVo.getTransferDate());
        		userBalVO.setUserID(channelTransferVo.getFromUserID());
                userBalancesDAO.updateUserDailyBalances(p_con, currentDate, userBalVO);
                if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
                List<ChannelSoSVO> chnlSoSVOList = new ArrayList<>();
                chnlSoSVOList.add(new ChannelSoSVO(channelUserVO.getUserID(),channelUserVO.getMsisdn(),channelUserVO.getSosAllowed(),channelUserVO.getSosAllowedAmount(),channelUserVO.getSosThresholdLimit()));
                channelTransferVo.setChannelSoSVOList(chnlSoSVOList);
                }
        		debitCount = channelUserDAO.debitUserBalances(p_con, channelTransferVo, false, null);
        		if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
        		if(!(PretupsI.SOS_NETWORK.equalsIgnoreCase((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, chnlTransferVO.getNetworkCode())))){
        			userBalVO.setUserID(channelTransferVo.getToUserID());
                    userBalancesDAO.updateUserDailyBalances(p_con, currentDate, userBalVO);
        			creditCount = channelUserDAO.creditUserBalances(p_con, channelTransferVo, false, null);
        		}
        		}else{
    				int updateCnt = -1;
    				updateCnt = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, channelTransferVo, fromUserID, currentDate, false);
    		       
    				if(updateCnt < 1) {
    		            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
    		        }
    				
    		        updateCnt = -1;
    		        updateCnt = ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, channelTransferVo, fromUserID, currentDate);
    		        if (updateCnt < 1) {
    		            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
    		        }
    			}
        		if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
        		if(!(PretupsI.SOS_NETWORK.equalsIgnoreCase((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, chnlTransferVO.getNetworkCode())))){
            		toCountVO = (UserTransferCountsVO) userTransferCountsDAO.loadTransferCounts(p_con, toUserID, true);

            		toCountVO.setUserID(toUserID);
        			toCountVO.setUnctrlDailyInCount(toCountVO.getUnctrlDailyInCount() + 1);
        			toCountVO.setUnctrlWeeklyInCount(toCountVO.getUnctrlWeeklyInCount() + 1);
        			toCountVO.setUnctrlMonthlyInCount(toCountVO.getUnctrlMonthlyInCount() + 1);
        			toCountVO.setUnctrlDailyInValue(toCountVO.getUnctrlDailyInValue() + channelTransferVo.getTransferMRP());
        			toCountVO.setUnctrlWeeklyInValue(toCountVO.getUnctrlWeeklyInValue() + channelTransferVo.getTransferMRP());
        			toCountVO.setUnctrlMonthlyInValue(toCountVO.getUnctrlMonthlyInValue() + channelTransferVo.getTransferMRP());
        			toCountVO.setOutsideLastInTime(currentDate);

        			toCountVO.setUserID(toUserID);
        			toCountVO.setDailyInCount(toCountVO.getDailyInCount() + 1);
        			toCountVO.setWeeklyInCount(toCountVO.getWeeklyInCount() + 1);
        			toCountVO.setMonthlyInCount(toCountVO.getMonthlyInCount() + 1);
        			toCountVO.setDailyInValue(toCountVO.getDailyInValue() + channelTransferVo.getTransferMRP());
        			toCountVO.setWeeklyInValue(toCountVO.getWeeklyInValue() + channelTransferVo.getTransferMRP());
        			toCountVO.setMonthlyInValue(toCountVO.getMonthlyInValue() + channelTransferVo.getTransferMRP());
        			toCountVO.setLastInTime(currentDate);
            	
        			final int toCount = userTransferCountsDAO.updateUserTransferCounts(p_con, toCountVO, true);
        			if (toCount < 1) {
    		            throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
    		        }
        		}
        		}
        		fromCountVO = (UserTransferCountsVO) userTransferCountsDAO.loadTransferCounts(p_con, fromUserID, true);

        		fromCountVO.setUserID(fromUserID);
        		fromCountVO.setUnctrlDailyOutCount(fromCountVO.getUnctrlDailyOutCount() + 1);
        		fromCountVO.setUnctrlWeeklyOutCount(fromCountVO.getUnctrlWeeklyOutCount() + 1);
        		fromCountVO.setUnctrlMonthlyOutCount(fromCountVO.getUnctrlMonthlyOutCount() + 1);
        		fromCountVO.setUnctrlDailyOutValue(fromCountVO.getUnctrlDailyOutValue() + channelTransferVo.getTransferMRP());
        		fromCountVO.setUnctrlWeeklyOutValue(fromCountVO.getUnctrlWeeklyOutValue() + channelTransferVo.getTransferMRP());
        		fromCountVO.setUnctrlMonthlyOutValue(fromCountVO.getUnctrlMonthlyOutValue() + channelTransferVo.getTransferMRP());
        		fromCountVO.setOutsideLastOutTime(currentDate);

        		fromCountVO.setUserID(fromUserID);
        		fromCountVO.setDailyOutCount(fromCountVO.getDailyOutCount() + 1);
        		fromCountVO.setWeeklyOutCount(fromCountVO.getWeeklyOutCount() + 1);
        		fromCountVO.setMonthlyOutCount(fromCountVO.getMonthlyOutCount() + 1);
        		fromCountVO.setDailyOutValue(fromCountVO.getDailyOutValue() + channelTransferVo.getTransferMRP());
        		fromCountVO.setWeeklyOutValue(fromCountVO.getWeeklyOutValue() + channelTransferVo.getTransferMRP());
        		fromCountVO.setMonthlyOutValue(fromCountVO.getMonthlyOutValue() + channelTransferVo.getTransferMRP());
        		fromCountVO.setLastOutTime(currentDate);

        		final int fromCount = userTransferCountsDAO.updateUserTransferCounts(p_con, fromCountVO, true);
        		int updateCount1=0;
        		if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE))
        		 updateCount1 = new ChannelTransferDAO().sosUpdateChannelTransfer(p_con, fromCountVO.getLastSOSTxnID(), toUserID, fromUserID,PretupsI.SOS_AUTO_SETTLED_STATUS,chnlTransferVO.getNetworkCode() );
        		if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE))
        			updateCount1 = new ChannelTransferDAO().lrUpdateChannelTransfer(p_con, fromCountVO.getLastLRTxnID(), toUserID, fromUserID,PretupsI.LAST_LR_SETTLED_STATUS,chnlTransferVO.getNetworkCode() );
                if(updateCount1 > 0)
                {	
                	if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE))
                    updateCount1 = userTransferCountsDAO.updateLastSOSTxnStatus(fromUserID, p_con,PretupsI.SOS_AUTO_SETTLED_STATUS );
                	if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE))
                		updateCount1=userTransferCountsDAO.updateLastLRTxnStatus(fromUserID, p_con, PretupsI.LAST_LR_SETTLED_STATUS);
                }
        		try {
        			if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
        			channelTransferVo.setReferenceNum(fromCountVO.getLastSOSTxnID());
        			channelTransferVo.setChannelRemarks("Pending SOS ASettled..");
        			}
        			if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE)){
        				channelTransferVo.setReferenceNum(fromCountVO.getLastLRTxnID());
            			channelTransferVo.setChannelRemarks("Pending LR Settled..");
        			}
        			updateCount = channelTransferDAO.addChannelTransfer(p_con, channelTransferVo);
        		} catch (Exception e) {
        			p_con.rollback();
        			log.errorTrace(METHOD_NAME, e);
        		}

        		if ((creditCount > 0 || debitCount > 0) && updateCount > 0 && fromCount > 0 && updateCount1 > 0) {
        			if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
        			if(PretupsI.SOS_NETWORK.equalsIgnoreCase((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, chnlTransferVO.getNetworkCode()))){
        				final Locale localeObj = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
        				this.sendMessageToSender(phoneVO, channelTransferVo);
        			}else{
        				this.sendMessageToReceiver(prtOwnphoneVO, channelTransferVo, phoneVO.getMsisdn());
        				this.sendMessageToSender(phoneVO, channelTransferVo);
        			}
        			}
        			else if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE)){
        				final Locale localeObj = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
        				this.sendMessageToSenderforLastRecharge(phoneVO, channelTransferVo);        				
        			}
        			p_con.commit();
        			
        			if(settleMentForYABX)
        			{
        				
        				this.vo= channelTransferVo;
        				InterfaceModuleI interfaceModule = null;
        			    String handlerClass = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.HANDLER_CLASS_FOR_YABX));
        				log.debug (METHOD_NAME, "handlerClass="+ handlerClass );
        				try {
        					if(handlerClass !=null) {
        						interfaceModule = (InterfaceModuleI) Class.forName(handlerClass).newInstance();
        						String response = interfaceModule.process(getReceiverCommonString());
        						SOSSettlementLog.log(vo,response);
        					}
        		            } catch (Exception e) {
        		                log.errorTrace(METHOD_NAME, e);
        		                // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"LoginAction[initialize]","","","","Exception
        		                
        		            }
        			}
        			
        		}else{
        			p_con.rollback();
        			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SOS_CHANNEL_SETTLEMENT_FAILURE);
        		}
        		if(requestType.equalsIgnoreCase(PretupsI.SOS_REQUEST_TYPE)){
        		if((PretupsI.SOS_NETWORK.equalsIgnoreCase((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, chnlTransferVO.getNetworkCode())))){
        			preparedNetworkStockTxnLog(channelTransferVo, fromCountVO.getLastSOSTxnID());
        		}
        		}
        		else if(requestType.equalsIgnoreCase(PretupsI.LR_REQUEST_TYPE))
        			preparedNetworkStockTxnLog(channelTransferVo, fromCountVO.getLastLRTxnID());
        		OneLineTXNLog.log(channelTransferVo, null);
        		
        	} catch (BTSLBaseException be) {
        		if (p_con != null) {
        			p_con.rollback();
        		}
        		if (log.isDebugEnabled()) {
        			log.debug(METHOD_NAME, " Exception in executing record  p_channelTransferVO : " + channelTransferVo);
        		}
        		log.errorTrace(METHOD_NAME, be);
        		throw be;
        	} catch (Exception e) {
        		if (p_con != null) {
        			p_con.rollback();
        		}
        		if (log.isDebugEnabled()) {
        			log.debug(METHOD_NAME, " Exception in executing record  p_channelTransferVO : " + channelTransferVo);
        		}
        		log.errorTrace(METHOD_NAME, e);
        	}
        }// end try
        catch (BTSLBaseException be) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.error(METHOD_NAME, "BTSLBaseException " + be.getMessage());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, " " + e.getMessage());
            }
            log.errorTrace(METHOD_NAME, e);
        }// end catch
        finally {
            if (log.isDebugEnabled()) {
                log.info(METHOD_NAME, "Exiting");
            }
        }// end finally
    }// end main
    
    
    public void sendMessageToSender(UserPhoneVO phoneVO, ChannelTransferVO channelTransferVo) throws BTSLBaseException{
    	final Locale localeObj = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
		Object[] smsListArr = prepareSMSMessageListForSender(channelTransferVo, PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_TXNSUBKEY,
            PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_BALSUBKEY);
        final String[] array = { BTSLUtil.getMessage(localeObj, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(localeObj, (ArrayList) smsListArr[1]), channelTransferVo.getTransferID() };
        final BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.SOS_WITHDRAW_SENDER, array);
        final PushMessage pushMessageForWithdraw = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVo.getTransferID(), null, localeObj, channelTransferVo.getNetworkCode());
        pushMessageForWithdraw.push();
    }
  
    public void sendMessageToReceiver(UserPhoneVO prtOwnphoneVO, ChannelTransferVO channelTransferVo, String msisdn) throws BTSLBaseException{
    	final Locale localeObj = new Locale(prtOwnphoneVO.getPhoneLanguage(), prtOwnphoneVO.getCountry());
    	Object[] smsListArr = prepareSMSMessageListForReceiver(channelTransferVo, PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_TXNSUBKEY,
            PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_BALSUBKEY);
        final String[] array = { BTSLUtil.getMessage(localeObj, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(localeObj, (ArrayList) smsListArr[1]), channelTransferVo.getTransferID(),msisdn };
        final BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.SOS_WITHDRAW_RECEIVER, array);
        final PushMessage pushMessageForWithdraw = new PushMessage(prtOwnphoneVO.getMsisdn(), messages, channelTransferVo.getTransferID(), null, localeObj, channelTransferVo.getNetworkCode());
        pushMessageForWithdraw.push();
    }

    public void sendMessageToSenderforLastRecharge(UserPhoneVO phoneVO, ChannelTransferVO channelTransferVo) throws BTSLBaseException{
    	final Locale localeObj = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
		Object[] smsListArr = prepareSMSMessageListForSender(channelTransferVo, PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_TXNSUBKEY,
            PretupsErrorCodesI.C2S_OPT_CHNL_WITHDRAW_BALSUBKEY);
        final String[] array = { BTSLUtil.getMessage(localeObj, (ArrayList) smsListArr[0]), BTSLUtil.getMessage(localeObj, (ArrayList) smsListArr[1]), channelTransferVo.getTransferID() };
        final BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.LR_WITHDRAW_MESSAGE_SENDER, array);
        final PushMessage pushMessageForWithdraw = new PushMessage(phoneVO.getMsisdn(), messages, channelTransferVo.getTransferID(), null, localeObj, channelTransferVo.getNetworkCode());
        pushMessageForWithdraw.push();
    }
    /**
     * 
     * @param channelTransferVO
     * @param p_txnSubKey
     * @param p_balSubKey
     * @return
     * @throws BTSLBaseException
     */
    public static Object[] prepareSMSMessageListForSender(ChannelTransferVO channelTransferVO, String p_txnSubKey, String p_balSubKey) throws BTSLBaseException {
        final String methodName = "prepareSMSMessageListForReceiver";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered channelTransferVO =  : " + channelTransferVO + ", p_txnSubKey = " + p_txnSubKey + ", p_balSubKey = " + p_balSubKey);
        }
        
        final ArrayList txnSmsMessageList = new ArrayList();
        final ArrayList balSmsMessageList = new ArrayList();
        KeyArgumentVO keyArgumentVO = null;
        String argsArr[] = null;
        final ArrayList productList = channelTransferVO.getChannelTransferitemsVOList();
        ChannelTransferItemsVO channelTransferItemsVO = null;
        String currentBalance = null;
        for (int i = 0, k = productList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(i);
            keyArgumentVO = new KeyArgumentVO();
            argsArr = new String[2];
            argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity());
            argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
            keyArgumentVO.setKey(p_txnSubKey);
            keyArgumentVO.setArguments(argsArr);
            txnSmsMessageList.add(keyArgumentVO);
          
            argsArr = new String[2];
            final long previousBalance = channelTransferItemsVO.getAfterTransSenderPreviousStock();
            long transferedQuantity = 0;
			if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())
					&& "T".equalsIgnoreCase(channelTransferVO.getTransferSubType())) {
			    transferedQuantity = channelTransferItemsVO.getReceiverCreditQty();
            } else {
                transferedQuantity = channelTransferItemsVO.getApprovedQuantity();
            }
            
                currentBalance = PretupsBL.getDisplayAmount(previousBalance - transferedQuantity);
            
             
            argsArr[1] = currentBalance;
            argsArr[0] = channelTransferItemsVO.getShortName();
            keyArgumentVO = new KeyArgumentVO();
            keyArgumentVO.setKey(p_balSubKey);
            keyArgumentVO.setArguments(argsArr);
            balSmsMessageList.add(keyArgumentVO);

        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited txnSmsMessageList.size() = " + txnSmsMessageList.size() + ", balSmsMessageList.size() = " + balSmsMessageList.size());
        }

        return (new Object[] { txnSmsMessageList, balSmsMessageList });
    }
    /**
     * 
     * @param channelTransferVO
     * @param p_txnSubKey
     * @param p_balSubKey
     * @return
     * @throws BTSLBaseException
     */
    public static Object[] prepareSMSMessageListForReceiver(ChannelTransferVO channelTransferVO, String p_txnSubKey, String p_balSubKey) throws BTSLBaseException {
        final String methodName = "prepareSMSMessageListForReceiver";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered channelTransferVO =  : " + channelTransferVO + ", p_txnSubKey = " + p_txnSubKey + ", p_balSubKey = " + p_balSubKey);
        }
       
        final ArrayList txnSmsMessageList = new ArrayList();
        final ArrayList balSmsMessageList = new ArrayList();
        KeyArgumentVO keyArgumentVO = null;
        String argsArr[] = null;
        final ArrayList productList = channelTransferVO.getChannelTransferitemsVOList();
        ChannelTransferItemsVO channelTransferItemsVO = null;
        String currentBalance = null;
        for (int i = 0, k = productList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) productList.get(i);
            keyArgumentVO = new KeyArgumentVO();
            argsArr = new String[2];
            argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemsVO.getApprovedQuantity());
            argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
            keyArgumentVO.setKey(p_txnSubKey);
            keyArgumentVO.setArguments(argsArr);
            txnSmsMessageList.add(keyArgumentVO);

           
            argsArr = new String[2];
            final long previousBalance = channelTransferItemsVO.getPreviousBalance();
            long transferedQuantity = 0;
			if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())
					&& "T".equalsIgnoreCase(channelTransferVO.getTransferSubType())) {
			    transferedQuantity = channelTransferItemsVO.getReceiverCreditQty();
            } else {
                transferedQuantity = channelTransferItemsVO.getApprovedQuantity();
            }
            
                currentBalance = PretupsBL.getDisplayAmount(previousBalance + transferedQuantity);
            
             
            argsArr[1] = currentBalance;
            argsArr[0] = channelTransferItemsVO.getShortName();
            keyArgumentVO = new KeyArgumentVO();
            keyArgumentVO.setKey(p_balSubKey);
            keyArgumentVO.setArguments(argsArr);
            balSmsMessageList.add(keyArgumentVO);

        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited txnSmsMessageList.size() = " + txnSmsMessageList.size() + ", balSmsMessageList.size() = " + balSmsMessageList.size());
        }

        return (new Object[] { txnSmsMessageList, balSmsMessageList });
    }
    
    public static void preparedNetworkStockTxnLog(ChannelTransferVO channelTransferVO, String transactionnID) throws BTSLBaseException{
    	
    	final String methodName = "preparedNetworkStockTxnLog";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ChannelTransferVO = " + channelTransferVO);
        }

        NetworkStockTxnVO networkStockTxnVO = null;
        final ArrayList itemList = channelTransferVO.getChannelTransferitemsVOList();
        ChannelTransferItemsVO itemsVO = null;
        final Date curdate = new Date();
        int itemListsSize=itemList.size();
        for (int j = 0; j <itemListsSize ; j++) {
            itemsVO = (ChannelTransferItemsVO) itemList.get(j);
            networkStockTxnVO = new NetworkStockTxnVO();
            networkStockTxnVO.setRequestedQuantity(itemsVO.getCommQuantity());
            networkStockTxnVO.setApprovedQuantity(itemsVO.getCommQuantity());
            networkStockTxnVO.setTxnMrp(itemsVO.getCommQuantity() * Long.parseLong(PretupsBL.getDisplayAmount(itemsVO.getUnitValue())));
            networkStockTxnVO.setProductCode(itemsVO.getProductCode());
            networkStockTxnVO.setTxnType(PretupsI.CREDIT);
            networkStockTxnVO.setPreviousStock(itemsVO.getAfterTransCommisionSenderPreviousStock());
            networkStockTxnVO.setPostStock(itemsVO.getAfterTransCommisionSenderPreviousStock() - itemsVO.getCommQuantity());

        }
       
        networkStockTxnVO.setRequestedQuantity(channelTransferVO.getRequestedQuantity());
        networkStockTxnVO.setApprovedQuantity(channelTransferVO.getRequestedQuantity());
        networkStockTxnVO.setPreviousStock(itemsVO.getAfterTransReceiverPreviousStock());
        networkStockTxnVO.setPostStock(itemsVO.getAfterTransReceiverPreviousStock() + itemsVO.getReceiverCreditQty());
      
        
        networkStockTxnVO.setNetworkCode(channelTransferVO.getNetworkCode());
        networkStockTxnVO.setNetworkFor(channelTransferVO.getNetworkCodeFor());
        networkStockTxnVO.setTxnNo(channelTransferVO.getTransferID());
        networkStockTxnVO.setReferenceNo(transactionnID);
        networkStockTxnVO.setStockType(channelTransferVO.getType());
        networkStockTxnVO.setTxnDate(channelTransferVO.getModifiedOn());
        networkStockTxnVO.setCreatedBy(channelTransferVO.getFromUserID());
        networkStockTxnVO.setUserID(channelTransferVO.getFromUserID());
        networkStockTxnVO.setCreatedOn(curdate);
        networkStockTxnVO.setEntryType(channelTransferVO.getTransferSubType());
        networkStockTxnVO.setTax3value(channelTransferVO.getTotalTax3());
        networkStockTxnVO.setTxnStatus(channelTransferVO.getStatus());
        networkStockTxnVO.setTxnCategory(channelTransferVO.getTransferCategory());
        networkStockTxnVO.setOtherInfo("COMMISSION PROFILE ID = " + channelTransferVO.getCommProfileSetId() + ", COMMISSION PROFILE VERSION = " + channelTransferVO
            .getCommProfileVersion() + ", TXN TYPE = " + PretupsI.NETWORK_STOCK_TRANSACTION_WITHDRAW);
        NetworkStockLog.log(networkStockTxnVO);
    }
    
 
    
    @SuppressWarnings("finally")
	private String getReceiverCommonString() throws BTSLBaseException {
    	StringBuilder stringBuilder = null;
    	  final String  methodName =  "getReceiverCommonString";
    	 String requestStr=null;
    	try {
    		   if (log.isDebugEnabled())
                   log.debug(methodName, "Exiting Request vo::" + vo);
            
    		   stringBuilder = new StringBuilder(1028);
    		   stringBuilder.append("<?xml version=\"1.0\"?>");
    		   stringBuilder.append("<COMMAND>");
    		   stringBuilder.append("<TYPE>SOSSETTLEREQ</TYPE>");
    		   stringBuilder.append("<DATE>" + BTSLUtil.getDateTimeStringFromDate(new Date(), ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT))) + "</DATE>");
          stringBuilder.append("<MSISDN1>" + vo.getFromUserCode() + "</MSISDN1>");
          if(!BTSLUtil.isNullString(vo.getSenderLoginID()))
        	  stringBuilder.append("<PRETUPSACCTID>"+vo.getSenderLoginID()+"</PRETUPSACCTID>");
          else
        	   stringBuilder.append("<PRETUPSACCTID></PRETUPSACCTID>");
          
          if(!BTSLUtil.isNullString(vo.getFromEXTCODE()))
        	  stringBuilder.append("<EXTCODE>"+vo.getFromEXTCODE()+"</EXTCODE>");
          else
        	  stringBuilder.append("<EXTCODE></EXTCODE>");
          stringBuilder.append("<AMOUNTCOLLECTED>" + PretupsBL.getDisplayAmount(vo.getRequestedQuantity()) + "</AMOUNTCOLLECTED>");// dummy
          stringBuilder.append("<SOSID>"+vo.getReferenceNum()+"</SOSID>");// dummy
          stringBuilder.append("<TXNID>"+vo.getSosTxnId()+"</TXNID>");// dummy
          stringBuilder.append("<WDID>"+vo.getTransferID()+"</WDID>");
          stringBuilder.append("<NWID>"+vo.getReferenceID()+"</NWID>");
          stringBuilder.append("<FROMACCTID>"+vo.getFromUserCode()+"</FROMACCTID>");// dummy
          if (log.isDebugEnabled())
              log.debug(methodName, "Request vo.getReceiverLoginID()::" + vo.getReceiverLoginID());
          
          stringBuilder.append("<TOACCTID>" +vo.getReceiverLoginID()+ "</TOACCTID>");// dummy
          requestStr= stringBuilder.toString();
          return requestStr;
    	}
    	catch (Exception e) {
            log.error(methodName, "Exception e: " + e);
            throw (BTSLBaseException)e;
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting Request requestStr::" + requestStr);
            
        }// end of finally
          
    }
}
