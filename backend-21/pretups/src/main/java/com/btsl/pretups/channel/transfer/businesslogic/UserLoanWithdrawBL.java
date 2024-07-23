

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
import com.btsl.pretups.channel.logging.UserLoanRequestProcessLogger;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.NetworkStockLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserLoanDAO;
import com.btsl.user.businesslogic.UserLoanVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;

public class UserLoanWithdrawBL {
    private static Log log = LogFactory.getLog(UserLoanWithdrawBL.class.getName());
/*request type to be LR or SOS*/
    public void autoChannelLoanSettlement(ChannelTransferVO vo,String requestType,long WithdrawAmount) throws BTSLBaseException {
        final String methodName = "autoChannelLoanSettlement";
        if (log.isDebugEnabled()) {
            log.info(methodName, " Enter UserBalancesVO: " + vo+"requestType"+requestType);
        }
        try {
            final ArrayList<ChannelTransferVO> list = new ArrayList<ChannelTransferVO>();
            ChannelTransferVO vo1 = new ChannelTransferVO();
            vo1 = SerializationUtils.clone(vo);
            list.add(vo);
            final ChannelLoanWithdrawThread mrt = new ChannelLoanWithdrawThread(vo1,requestType,WithdrawAmount);
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

class ChannelLoanWithdrawThread implements Runnable {
    private static Log log = LogFactory.getLog(ChannelLoanWithdrawThread.class.getName());
    private ChannelTransferVO vo = null;
    private String requesttype = null;
    private long withdrawAmount;
    
   
    public ChannelLoanWithdrawThread( ChannelTransferVO vo1,String requestType,long withdrawAmount) {
        this.vo = vo1;
        this.requesttype=requestType;
        this.withdrawAmount= withdrawAmount;
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
				mcomCon.close("ChannelLoanWithdrawThread#run");
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
        	final UserDAO userDAO = new UserDAO();
        	PushMessage pushMessage = null;
        	ArrayList channelTransferItemVOList = new ArrayList();
        	channelTransferVo = new ChannelTransferVO();
        	UserLoanVO userLoanVO = null;
    		
        	try {
        		long loanWithdrawAmt = 0;
        
        		final String fromUserID = chnlTransferVO.getToUserID();
        		channelUserVO = (ChannelUserVO) channelUserDAO.loadChannelUserDetailsForTransfer(p_con,
        				chnlTransferVO.getToUserID(), false, currentDate, false);
        			
        		if(channelUserVO!=null && channelUserVO.getUserLoanVOList()!=null &&  channelUserVO.getUserLoanVOList().size()==0)
        		{
        		     LogFactory.printLog(METHOD_NAME, "ChannelTransferVO"+ chnlTransferVO+"requestType"+requestType, log);
        		     
        			   throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.ERROR_LOAN_SETTLMENT);
        		}
        	
        		for (UserLoanVO loanVO : channelUserVO.getUserLoanVOList()) {
					if(channelUserVO.getUserID().equals(loanVO.getUser_id()) && loanVO.getProduct_code().equals(chnlTransferVO.getProductCode()))
					{
						userLoanVO = loanVO;
						break;
					}
					else
						continue;


				}
				if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE)){
        		
        			loanWithdrawAmt = withdrawAmount;
        			
        		}

				final UserPhoneVO phoneVO = userDAO.loadUserPhoneVO(p_con, fromUserID);
        		UserPhoneVO prtOwnphoneVO = null;
        	
        		if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE))
        			channelTransferVo.setTransactionMode(PretupsI.USER_LOAN_TXN_MODE);
        		
        		channelTransferVo.setNetworkCode(channelUserVO.getNetworkID());
        		channelTransferVo.setNetworkCodeFor(channelUserVO.getNetworkID());
        		channelTransferVo.setCategoryCode(channelUserVO.getCategoryCode());
        		channelTransferVo.setUserMsisdn(channelUserVO.getMsisdn());
        		channelTransferVo.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
        		channelTransferVo.setDomainCode(channelUserVO.getDomainID());
        		channelTransferVo.setCreatedOn(currentDate);
        		if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE)){
        			
            		channelTransferVo.setType(PretupsI.CHANNEL_TYPE_O2C);
            		channelTransferVo.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
            		channelTransferVo.setReceiverCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
            		channelTransferVo.setToUserID(PretupsI.OPERATOR_TYPE_OPT);
            		channelTransferVo.setReceiverDomainCode(PretupsI.OPERATOR_TYPE_OPT);
            		channelTransferVo.setReceiverLoginID(PretupsI.OPERATOR_TYPE_OPT);
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
        		channelTransferVo.setTransferMRP(loanWithdrawAmt);
        		channelTransferVo.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        		channelTransferVo.setRequestedQuantity(loanWithdrawAmt);
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
        		channelTransferVo.setPayableAmount(loanWithdrawAmt);
        		channelTransferVo.setNetPayableAmount(loanWithdrawAmt);
        		channelTransferVo.setNetworkCode(chnlTransferVO.getNetworkCode());
        		channelTransferVo.setNetworkCodeFor(chnlTransferVO.getNetworkCodeFor());
        		channelTransferVo.setSosTxnId(chnlTransferVO.getTransferID());
        		channelTransferVo.setSosRequestAmount(chnlTransferVO.getRequestedQuantity());
        		channelTransferVo.setCommProfileVersion(chnlTransferVO.getCommProfileVersion());
        		if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE)){
        		channelTransferVo.setRequestedQuantity(loanWithdrawAmt);
        		channelTransferVo.setTransferMRP(loanWithdrawAmt);
        		channelTransferVo.setPayableAmount(loanWithdrawAmt);
        		channelTransferVo.setNetPayableAmount(loanWithdrawAmt);
        		}
        
        		transferItemsVO = new ChannelTransferItemsVO();
                transferItemsVO.setSerialNum(1);
                transferItemsVO.setProductCode(chnlTransferVO.getProductCode());
                if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE)){
                transferItemsVO.setRequiredQuantity(loanWithdrawAmt);
                transferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(loanWithdrawAmt));
                transferItemsVO.setApprovedQuantity(loanWithdrawAmt);
                transferItemsVO.setSenderDebitQty(loanWithdrawAmt);
                transferItemsVO.setPayableAmount(loanWithdrawAmt);
    			transferItemsVO.setNetPayableAmount(loanWithdrawAmt);
                
 }
           
                //transferItemsVO.setUnitValue(chnlTransferVO.get*val); // need to set unitvalue
                transferItemsVO.setNetworkCode(chnlTransferVO.getNetworkCode());
                channelTransferItemVOList.add(transferItemsVO);
        		channelTransferVo.setChannelTransferitemsVOList(channelTransferItemVOList);
        		
        		for(int i=0; i<(chnlTransferVO.getChannelTransferitemsVOList()).size(); i++){
        			ChannelTransferItemsVO channelTransferItemsVO = (ChannelTransferItemsVO) chnlTransferVO.getChannelTransferitemsVOList().get(i);
        			channelTransferVo.setProductCode(channelTransferItemsVO.getProductCode());
        			transferItemsVO.setShortName(channelTransferItemsVO.getProductCode());
        			transferItemsVO.setProductCode(channelTransferItemsVO.getProductCode());
        			transferItemsVO.setUnitValue(channelTransferItemsVO.getUnitValue());
        			  if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE)){
        				  transferItemsVO.setReceiverCreditQty(loanWithdrawAmt);
        			  }
        			 
        			transferItemsVO.setCommProfileDetailID(channelTransferItemsVO.getCommProfileDetailID());
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
                if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE)){
                channelTransferVo.setUserLoanVOList(channelUserVO.getUserLoanVOList());
                }
        		debitCount = channelUserDAO.debitUserBalances(p_con, channelTransferVo, false, null);
        		if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE)){
        
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
        		
        		UserLoanVO p_userLoanVO= new UserLoanVO();
        		
                p_userLoanVO.setLoan_given(PretupsI.NO);
                p_userLoanVO.setSettlement_id(channelTransferVo.getTransferID());
                p_userLoanVO.setSettlement_loan_amount(channelTransferVo.getRequestedQuantity());
                p_userLoanVO.setSettlement_loan_interest(withdrawAmount - userLoanVO.getLoan_given_amount());
                p_userLoanVO.setSettlement_from(channelTransferVo.getToUserID());
                p_userLoanVO.setUser_id(fromUserID);
                p_userLoanVO.setProduct_code(channelTransferVo.getProductCode());
                
        		if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE))
        		 updateCount1 = new UserLoanDAO().updateUserLoanSettlement(p_con, p_userLoanVO,currentDate );
        	
        		
        		try {
        			if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE)){
        			channelTransferVo.setReferenceNum(userLoanVO.getLast_loan_txn_id());
        			channelTransferVo.setChannelRemarks("Pending Loan ASettled..");
        			}
        			
        			updateCount = channelTransferDAO.addChannelTransfer(p_con, channelTransferVo);
        		} catch (Exception e) {
        			p_con.rollback();
        			log.errorTrace(METHOD_NAME, e);
        		}

        		
        		log.debug (METHOD_NAME, "creditCount="+ creditCount + "debitCount="+debitCount  +"updateCount="+updateCount +"fromCount="+fromCount + "updateCount1="+updateCount1);
        		
        		if ((creditCount > 0 || debitCount > 0) && updateCount > 0 && fromCount > 0 && updateCount1 > 0) {
        			
        			if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE)){
        			if(PretupsI.SOS_NETWORK.equalsIgnoreCase((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, chnlTransferVO.getNetworkCode()))){
        				final Locale localeObj = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
        				this.sendMessageToSender(phoneVO, channelTransferVo);
        			}
        			}
        			p_con.commit();
        			
        			UserLoanRequestProcessLogger.log(userLoanVO, phoneVO.getMsisdn(), "S");
        			
        		}else{
        			p_con.rollback();
        			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.SOS_CHANNEL_SETTLEMENT_FAILURE);
        		}
        		if(requestType.equalsIgnoreCase(PretupsI.USER_LOAN_REQUEST_TYPE)){
        		if((PretupsI.SOS_NETWORK.equalsIgnoreCase((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.CHANNEL_SOS_ALLOWED_WALLET, chnlTransferVO.getNetworkCode())))){
        			preparedNetworkStockTxnLog(channelTransferVo, fromCountVO.getLastSOSTxnID());
        		}
        		}
        		OneLineTXNLog.log(channelTransferVo, null);
        	
			}catch (BTSLBaseException be) {
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
       /*mclass^2&pid^61:300418:Withdrawal request, against pending SOS, of product(s) {0} is successful,  your new balance of product(s) {1}. Transfer ID against SOS is {2} */
        final BTSLMessages messages = new BTSLMessages(PretupsErrorCodesI.LOAN_WITHDRAW_SENDER, array);
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
                transferedQuantity = channelTransferItemsVO.getReceiverCreditQty();
            
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
                transferedQuantity = channelTransferItemsVO.getReceiverCreditQty();
            
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
        for (int j = 0; j < itemList.size(); j++) {
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
    
   
}
