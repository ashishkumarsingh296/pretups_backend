package com.btsl.pretups.adjustments.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class PromoBonusCalBL {

	private static Log log = LogFactory.getLog(PromoBonusCalBL.class.getName());
	private Date currentDate = null;
	private Connection con = null;
	private long requestedAmount = 0;
	private static final  String EXITEDSTRING = " Exited This Method.";
	
	
	public PromoBonusCalBL(Connection pCon) {

		currentDate = new Date();
		con = pCon;
	}

	/**
	 * @param pC2sTransferVO
	 * @param pModule
	 * @throws BTSLBaseException
	 */
	public void promoBonusAdjustment(C2STransferVO pC2sTransferVO, String pModule) throws BTSLBaseException {

		final String methodName = "promoBonusAdjustment";
		if (log.isDebugEnabled()) {
			log.debug(methodName, pModule + " : " + pC2sTransferVO.toString());
		}

		if (pC2sTransferVO.getPromoBonus() <= 0) {
			return;
		}

		final ArrayList<AdjustmentsVO> itemsList = new ArrayList<>();
		requestedAmount = pC2sTransferVO.getRequestedAmount();
		AdjustmentsVO promoBonusAdjustmentDebitVO = null;
		AdjustmentsVO promoBonusAdjustmentCreditVO = null;
		AdjustmentsVO promoBonusAdjustmentOwnerVODebit=null;
		AdjustmentsVO promoBonusAdjustmentOwnerVOCredit=null;
		
		try {
			promoBonusAdjustmentDebitVO = new AdjustmentsVO();
			promoBonusAdjustmentCreditVO = new AdjustmentsVO();
			populatePromoBonusAdjustmentDebit(pC2sTransferVO, promoBonusAdjustmentDebitVO);
			populatePromoBonusAdjustmentCredit(pC2sTransferVO, promoBonusAdjustmentCreditVO);
			
			promoBonusAdjustmentOwnerVODebit=new AdjustmentsVO();
			promoBonusAdjustmentOwnerVOCredit=new AdjustmentsVO();
			
			String sequenceNo=new CommissionProfileDAO().loadsequenceNo(con, ((ChannelUserVO)pC2sTransferVO.getSenderVO()).getCategoryCode());
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !sequenceNo.equalsIgnoreCase(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER))) {
				populateAdjustmentOwnerBonusDebit(pC2sTransferVO,promoBonusAdjustmentOwnerVODebit);
				populateAdjustmentOwnerBonusCredit(pC2sTransferVO,promoBonusAdjustmentOwnerVOCredit);
			}
			
			
			handleUserBalancesForBonus(pC2sTransferVO,  promoBonusAdjustmentCreditVO);

			itemsList.add(promoBonusAdjustmentDebitVO);
			itemsList.add(promoBonusAdjustmentCreditVO);
	

			
		
if((!((ChannelUserVO)pC2sTransferVO.getSenderVO()).getOwnerID().equals(pC2sTransferVO.getSenderID())) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !sequenceNo.equals(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER))){	
				debitAndCreditOwnerBalanceForProduct(pC2sTransferVO,promoBonusAdjustmentOwnerVODebit, promoBonusAdjustmentOwnerVOCredit);
				promoBonusAdjustmentOwnerVODebit.setStockUpdated(PretupsI.NO);
				promoBonusAdjustmentOwnerVOCredit.setStockUpdated(PretupsI.YES);
			}
			
			
	if((!((ChannelUserVO)pC2sTransferVO.getSenderVO()).getOwnerID().equals(pC2sTransferVO.getSenderID())) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !sequenceNo.equals(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER)))	
			{
				itemsList.add(promoBonusAdjustmentOwnerVODebit);
				itemsList.add(promoBonusAdjustmentOwnerVOCredit);
			}
			
			addAdjustmentsEntries(itemsList, pC2sTransferVO);
			
			pC2sTransferVO.setCommissionGiven(PretupsI.YES);
			setSenderReturnBonusMessage(pC2sTransferVO, promoBonusAdjustmentCreditVO);
			
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !sequenceNo.equals(String.valueOf(PretupsI.CATEGORY_SEQUENCE_NUMBER))){
				String flag=Constants.getProperty("OWNER_COMMISSION_MESSAGE_ALLOWED");
				if(flag.equals(PretupsI.STATUS_ACTIVE)){

					String[] arr=new String[4];
					double a=(promoBonusAdjustmentOwnerVOCredit.getTransferValue()-promoBonusAdjustmentOwnerVODebit.getTransferValue())/100.00;
					arr[0]=""+Double.toString(a);
					arr[1]=""+PretupsBL.getDisplayAmount(pC2sTransferVO.getRequestedAmount());
					arr[2]=pC2sTransferVO.getTransferID();
					arr[3]=""+PretupsBL.getDisplayAmount(promoBonusAdjustmentOwnerVODebit.getPostBalance());
					Locale locale =(((ChannelUserVO)pC2sTransferVO.getSenderVO()).getUserPhoneVO()).getLocale();
					String senderMessage=BTSLUtil.getMessage(locale,PretupsErrorCodesI.SMS_DIFFCAL_SUCCESS,arr);
					String lowBalRequestCode=(String)PreferenceCache.getNetworkPrefrencesValue(pC2sTransferVO.getRequestGatewayCode(),pC2sTransferVO.getNetworkCode());
					PushMessage pushMessage=new PushMessage(promoBonusAdjustmentOwnerVOCredit.getUserMSISDN(),senderMessage,null,null,locale);
					pushMessage.push(lowBalRequestCode,null);
				}
			}
			

		} catch (BTSLBaseException be) {

			 log.errorTrace(methodName, be);
	            throw be;
	            
		} catch (Exception e) {
			
			log.errorTrace(methodName ,e);
			log.error(methodName,pC2sTransferVO.getTransferID()+" : "+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,PromoBonusCalBL.class.getName()+"["+methodName+"]",pC2sTransferVO.getTransferID(),pC2sTransferVO.getSenderMsisdn(),pC2sTransferVO.getNetworkCode(),e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.ERROR_EXCEPTION);
		

		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, EXITEDSTRING);
			}

		}

	}

	/**
	 * @param pC2sTransferVO
	 * @param promoBonusAdjustmentCreditVO
	 */
	private void setSenderReturnBonusMessage(C2STransferVO pC2sTransferVO, AdjustmentsVO promoBonusAdjustmentCreditVO) {

		final String methodName = "setSenderReturnBonusMessage";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered  :"+pC2sTransferVO.getCommissionGiven()+" : "+promoBonusAdjustmentCreditVO.getTransferValue() );
		}

		if (PretupsI.YES.equalsIgnoreCase(pC2sTransferVO.getCommissionGiven())
				&& promoBonusAdjustmentCreditVO.getTransferValue() > 0) {
			if (!BTSLUtil.isNullString(pC2sTransferVO.getSubscriberSID())) {
				String[] messageArgArray = { promoBonusAdjustmentCreditVO.getAdjustmentID(),
						pC2sTransferVO.getTransferID(),
						PretupsBL.getDisplayAmount(promoBonusAdjustmentCreditVO.getTransferValue()),
						PretupsBL.getDisplayAmount(promoBonusAdjustmentCreditVO.getPostBalance()),
						pC2sTransferVO.getSubscriberSID(), pC2sTransferVO.getTransferValueStr() };
				pC2sTransferVO.setSenderReturnPromoMessage(
						BTSLUtil.getMessage(((ChannelUserVO) pC2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
								PretupsErrorCodesI.PROMO_BONUS_ADJUSTMENT_SUCCESS, messageArgArray));
			} else {
				String[] messageArgArray = { promoBonusAdjustmentCreditVO.getAdjustmentID(),
						pC2sTransferVO.getTransferID(),
						PretupsBL.getDisplayAmount(promoBonusAdjustmentCreditVO.getTransferValue()),
						PretupsBL.getDisplayAmount(promoBonusAdjustmentCreditVO.getPostBalance()),
						pC2sTransferVO.getReceiverMsisdn(), pC2sTransferVO.getTransferValueStr() };
				pC2sTransferVO.setSenderReturnPromoMessage(
						BTSLUtil.getMessage(((ChannelUserVO) pC2sTransferVO.getSenderVO()).getUserPhoneVO().getLocale(),
								PretupsErrorCodesI.PROMO_BONUS_ADJUSTMENT_SUCCESS, messageArgArray));
			}
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName, EXITEDSTRING);
		}

	}

	/**
	 * @param itemsList
	 * @param pC2sTransferVO
	 * @throws BTSLBaseException
	 */
	private void addAdjustmentsEntries(ArrayList<AdjustmentsVO> itemsList, C2STransferVO pC2sTransferVO)
			throws BTSLBaseException {

		final String methodName = "addAdjustmentsEntries";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered  :");
		}

		AdjustmentsDAO adjustmentsDAO = new AdjustmentsDAO();

		int addCount = adjustmentsDAO.addAdjustmentEntries(con, itemsList, pC2sTransferVO.getTransferID());
		if (addCount <= 0) {
			pC2sTransferVO.setCommissionGiven(PretupsI.NO);
			throw new BTSLBaseException(this, "calculateDifferential", PretupsErrorCodesI.ERROR_EXCEPTION);
			
		}
			

		if (log.isDebugEnabled()) {
			log.debug(methodName, EXITEDSTRING);
		}

	}

	/**
	 * @param pC2sTransferVO
	 * @param promoBonusAdjustmentDebitVO
	 * @throws BTSLBaseException
	 */
	private void handleUserBalancesForBonus(C2STransferVO pC2sTransferVO, AdjustmentsVO promoBonusAdjustmentCreditVO) throws BTSLBaseException {

		final String methodName = "handleUserBalancesForBonus";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered  : C2S Transfer VO =" + pC2sTransferVO.toString());
		}
		try {

			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
				creditUserBalanceToWallet(con, promoBonusAdjustmentCreditVO, pC2sTransferVO.getCategoryCode(),
						pC2sTransferVO);
			} else {
				creditUserBalanceForProduct(con, promoBonusAdjustmentCreditVO, pC2sTransferVO.getCategoryCode(),
						pC2sTransferVO);

				if (promoBonusAdjustmentCreditVO.getPostBalance() > 0)
					pC2sTransferVO.setSenderPostBalance(promoBonusAdjustmentCreditVO.getPostBalance());
				C2STransferItemVO reconcileVO = (C2STransferItemVO) pC2sTransferVO.getTransferItemList().get(0);
				reconcileVO.setPreviousBalance(promoBonusAdjustmentCreditVO.getPreviousBalance());
				reconcileVO.setPostBalance(promoBonusAdjustmentCreditVO.getPostBalance());
				pC2sTransferVO.getTransferItemList().set(0, reconcileVO);
			}

			promoBonusAdjustmentCreditVO.setStockUpdated(PretupsI.YES);


	
		
		} catch (BTSLBaseException be) {
			
			pC2sTransferVO.setDifferentialGiven(PretupsI.NO);
			log.errorTrace(methodName, be);
			throw be;
           
		} catch (Exception e) {
		
			log.errorTrace(methodName ,e);
			log.error(methodName,pC2sTransferVO.getTransferID()+" : "+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,PromoBonusCalBL.class.getName()+"["+methodName+"]",pC2sTransferVO.getTransferID(),pC2sTransferVO.getSenderMsisdn(),pC2sTransferVO.getNetworkCode(),e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.ERROR_EXCEPTION);
	

		} finally {

			if (log.isDebugEnabled()) {
				log.debug(methodName, EXITEDSTRING);
			}
		}

	}

	/**
	 * @param pC2sTransferVO
	 * @param promoBonusAdjustmentCreditVO
	 * @throws BTSLBaseException
	 */
	private void populatePromoBonusAdjustmentCredit(C2STransferVO pC2sTransferVO,
			AdjustmentsVO promoBonusAdjustmentCreditVO) throws BTSLBaseException {

		final String methodName = "populatePromoBonusAdjustmentCredit";
		if (log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_module:");
        	msg.append(promoBonusAdjustmentCreditVO.toString());
        	msg.append(" : C2S Transfer VO =");
        	msg.append(pC2sTransferVO.toString());       
        	
        	String message=msg.toString();
			log.debug(methodName, message);
		}

		try {


			// The adjustment id for the debit would be the refferenced as the transaction
			// id with suffix as N
			String promoBonusAdjustmentCreditID = pC2sTransferVO.getTransferID() 
					+ PretupsI.SUFIX_ADJUST_TXN_ID_USER+ "B";

			promoBonusAdjustmentCreditVO.setAdjustmentID(promoBonusAdjustmentCreditID);
			promoBonusAdjustmentCreditVO.setAdjustmentDate(currentDate);
			promoBonusAdjustmentCreditVO.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			promoBonusAdjustmentCreditVO.setCreatedBy(PretupsI.SYSTEM_USER);
			promoBonusAdjustmentCreditVO.setCreatedOn(currentDate);
			promoBonusAdjustmentCreditVO.setModifiedBy(PretupsI.SYSTEM_USER);
			promoBonusAdjustmentCreditVO.setModifiedOn(currentDate);
			promoBonusAdjustmentCreditVO.setDifferentialFactor(1);
			promoBonusAdjustmentCreditVO.setEntryType(PretupsI.CREDIT);
			promoBonusAdjustmentCreditVO.setMarginRate(0);
			promoBonusAdjustmentCreditVO.setMarginType("AMT");
			promoBonusAdjustmentCreditVO.setMarginAmount(pC2sTransferVO.getPromoBonus());
			promoBonusAdjustmentCreditVO.setModule(pC2sTransferVO.getModule());
			promoBonusAdjustmentCreditVO.setNetworkCode(pC2sTransferVO.getNetworkCode());
			// Add to log user MSISDN on 20/02/2008
			promoBonusAdjustmentCreditVO.setUserMSISDN(pC2sTransferVO.getReceiverMsisdn());

			// Roam Recharge CR 000012
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				promoBonusAdjustmentCreditVO.setNetworkCodeFor(pC2sTransferVO.getNetworkCode());
			else
				promoBonusAdjustmentCreditVO.setNetworkCodeFor(pC2sTransferVO.getReceiverNetworkCode());

			promoBonusAdjustmentCreditVO.setStockUpdated(PretupsI.NO);
			promoBonusAdjustmentCreditVO.setProductCode(pC2sTransferVO.getProductCode());
			promoBonusAdjustmentCreditVO.setReferenceID(pC2sTransferVO.getTransferID());
			promoBonusAdjustmentCreditVO.setServiceType(pC2sTransferVO.getServiceType());
			promoBonusAdjustmentCreditVO.setUserID(pC2sTransferVO.getSenderID());
			promoBonusAdjustmentCreditVO
					.setUserCategory(((ChannelUserVO) pC2sTransferVO.getSenderVO()).getCategoryCode());
			promoBonusAdjustmentCreditVO.setTax1Rate(0);
			promoBonusAdjustmentCreditVO.setTax1Type("AMT");
			promoBonusAdjustmentCreditVO.setTax2Rate(0);
			promoBonusAdjustmentCreditVO.setTax2Type("AMT");
			promoBonusAdjustmentCreditVO.setAddnlCommProfileDetailID("0");
			promoBonusAdjustmentCreditVO.setSubService(pC2sTransferVO.getSubService());
			promoBonusAdjustmentCreditVO.setCommisssionType(PretupsI.OTF_COMMISSION);
			promoBonusAdjustmentCreditVO.setTransferValue(pC2sTransferVO.getPromoBonus());

		} catch (Exception e) {
			
			log.errorTrace(methodName ,e);
			log.error(methodName,pC2sTransferVO.getTransferID()+" : "+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,PromoBonusCalBL.class.getName()+"["+methodName+"]",pC2sTransferVO.getTransferID(),pC2sTransferVO.getSenderMsisdn(),pC2sTransferVO.getNetworkCode(),e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.ERROR_EXCEPTION);
	

		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, EXITEDSTRING);
			}

		}

	}

	/**
	 * @param pC2sTransferVO
	 * @param promoBonusAdjustmentDebitVO
	 * @throws BTSLBaseException
	 */
	private void populatePromoBonusAdjustmentDebit(C2STransferVO pC2sTransferVO,
			AdjustmentsVO promoBonusAdjustmentDebitVO) throws BTSLBaseException {

		final String methodName = "populatePromoBonusAdjustmentDebit";
		if (log.isDebugEnabled()) {
			StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_module:");
        	msg.append(promoBonusAdjustmentDebitVO.toString());
        	msg.append(" : C2S Transfer VO =");
        	msg.append(pC2sTransferVO.toString());       
        	
        	String message=msg.toString();
			log.debug(methodName, message);
		}

		try {

			String promoBonusAdjustmentDebitID = pC2sTransferVO.getTransferID()  + PretupsI.SUFIX_ADJUST_TXN_ID_NW+ "B";
			promoBonusAdjustmentDebitVO.setAdjustmentID(promoBonusAdjustmentDebitID);
			promoBonusAdjustmentDebitVO.setAdjustmentDate(currentDate);
			promoBonusAdjustmentDebitVO.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			promoBonusAdjustmentDebitVO.setCreatedBy(PretupsI.SYSTEM_USER);
			promoBonusAdjustmentDebitVO.setCreatedOn(currentDate);
			promoBonusAdjustmentDebitVO.setModifiedBy(PretupsI.SYSTEM_USER);
			promoBonusAdjustmentDebitVO.setModifiedOn(currentDate);
			promoBonusAdjustmentDebitVO.setDifferentialFactor(1);
			promoBonusAdjustmentDebitVO.setEntryType(PretupsI.DEBIT);
			promoBonusAdjustmentDebitVO.setMarginRate(0);
			promoBonusAdjustmentDebitVO.setMarginType("AMT");
			promoBonusAdjustmentDebitVO.setMarginAmount(pC2sTransferVO.getPromoBonus());
			promoBonusAdjustmentDebitVO.setModule(pC2sTransferVO.getModule());
			promoBonusAdjustmentDebitVO.setNetworkCode(pC2sTransferVO.getNetworkCode());
			// Add to log user MSISDN on 20/02/2008
			promoBonusAdjustmentDebitVO.setUserMSISDN(pC2sTransferVO.getReceiverMsisdn());

			// Roam Recharge CR 000012
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				promoBonusAdjustmentDebitVO.setNetworkCodeFor(pC2sTransferVO.getNetworkCode());
			else
				promoBonusAdjustmentDebitVO.setNetworkCodeFor(pC2sTransferVO.getReceiverNetworkCode());

			promoBonusAdjustmentDebitVO.setStockUpdated(PretupsI.NO);
			promoBonusAdjustmentDebitVO.setProductCode(pC2sTransferVO.getProductCode());
			promoBonusAdjustmentDebitVO.setReferenceID(pC2sTransferVO.getTransferID());
			promoBonusAdjustmentDebitVO.setServiceType(pC2sTransferVO.getServiceType());
			promoBonusAdjustmentDebitVO.setUserID(PretupsI.OPERATOR_USER_TYPE);
			promoBonusAdjustmentDebitVO.setUserCategory(PretupsI.OPERATOR_CATEGORY);
			promoBonusAdjustmentDebitVO.setTax1Rate(0);
			promoBonusAdjustmentDebitVO.setTax1Type("AMT");
			promoBonusAdjustmentDebitVO.setTax2Rate(0);
			promoBonusAdjustmentDebitVO.setTax2Type("AMT");
			promoBonusAdjustmentDebitVO.setAddnlCommProfileDetailID("0");
			promoBonusAdjustmentDebitVO.setSubService(pC2sTransferVO.getSubService());
			promoBonusAdjustmentDebitVO.setCommisssionType(PretupsI.OTF_COMMISSION);
			promoBonusAdjustmentDebitVO.setTransferValue(pC2sTransferVO.getPromoBonus());

		} catch (Exception e) {
			
			log.errorTrace(methodName ,e);
			log.error(methodName,pC2sTransferVO.getTransferID()+" : "+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,PromoBonusCalBL.class.getName()+"["+methodName+"]",pC2sTransferVO.getTransferID(),pC2sTransferVO.getSenderMsisdn(),pC2sTransferVO.getNetworkCode(),e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.ERROR_EXCEPTION);
	

		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, EXITEDSTRING);
			}

		}

	}

	/**
	 * 1. Prepares UserBalanceVO from AdjustmentVO and C2STransferVO. 2. Updates
	 * UserDailyBalance if not already. 3. Credit the Adjustment transfer value in
	 * BONUS account.
	 * 
	 * @author mohd.suhel1
	 * @param pcon
	 * @param pAdjustmentsVO
	 * @param pCategoryCode
	 * @param pC2sTransferVO
	 * @throws BTSLBaseException
	 */
	public void creditUserBalanceToWallet(Connection pcon, AdjustmentsVO pAdjustmentsVO, String pCategoryCode,
			C2STransferVO pC2sTransferVO) throws BTSLBaseException {
		final String methodName = "creditUserBalanceForProduct";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered adjustments ID : " + pAdjustmentsVO.getAdjustmentID());
		}
		UserBalancesVO userBalancesVO = null;
		UserBalancesDAO userBalancesDAO = null;
		try {
			userBalancesVO = prepareUserBalanceVOFromTransferVO(pAdjustmentsVO, PretupsI.TRANSFER_TYPE_C2S,
					pC2sTransferVO.getSourceType(), pAdjustmentsVO.getEntryType(), PretupsI.TRANSFER_TYPE_DIFFCR, "");

			/**
			 * Populating some required info from C2STransferVO to UserBalanceVO.
			 */
			userBalancesVO.setPdaWalletList(pC2sTransferVO.getPdaWalletList());
			userBalancesVO.setBalance(pC2sTransferVO.getTotalBalanceAcrossPDAWallets());
			userBalancesVO.setPreviousBalance(pC2sTransferVO.getTotalPreviousBalanceAcrossPDAWallets());
			userBalancesDAO = new UserBalancesDAO();
			userBalancesDAO.updateUserDailyBalancesForWallets(pcon, pAdjustmentsVO.getCreatedOn(), userBalancesVO,
					pC2sTransferVO);
			// Credit the sender
			int updateCount = 0;
			
			if (pAdjustmentsVO.getTransferValue() != 0) {
				updateCount = userBalancesDAO.creditUserBalanceForBonusAcc(pcon, userBalancesVO, pCategoryCode);
			} else {
				updateCount = 1;
				userBalancesVO.setOtherInfo(
						userBalancesVO.getOtherInfo() + ", BALANCE UPDATION IS NOT APPLICABLE AS TRANSFER VALUE IS 0");
			}
			if (updateCount <= 0) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
			}

			// Update Previous and Post balances of Reciever
			pAdjustmentsVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
			pAdjustmentsVO.setPostBalance(userBalancesVO.getBalance());
			BalanceLogger.log(userBalancesVO);
		
	} catch (BTSLBaseException be) {
		
		
		log.errorTrace(methodName, be);
		throw be;
       
	} catch (Exception e) {
	
		log.errorTrace(methodName ,e);
		log.error(methodName,pC2sTransferVO.getTransferID()+" : "+e.getMessage());
		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,PromoBonusCalBL.class.getName()+"["+methodName+"]",pC2sTransferVO.getTransferID(),pC2sTransferVO.getSenderMsisdn(),pC2sTransferVO.getNetworkCode(),e.getMessage());
		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);


	} 
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting adjustments ID : " + pAdjustmentsVO.getAdjustmentID());
		}
	}

	/**
	 * Credits the user for the value if online differential has to be given
	 * 
	 * @param pcon
	 * @param pAdjustmentsVO
	 * @param pCategoryCode
	 * @param pC2sTransferVO
	 * @throws BTSLBaseException
	 */
	public void creditUserBalanceForProduct(Connection pcon, AdjustmentsVO pAdjustmentsVO, String pCategoryCode,
			C2STransferVO pC2sTransferVO) throws BTSLBaseException {
		final String methodName = "creditUserBalanceForProduct";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered adjustments ID : " + pAdjustmentsVO.getAdjustmentID());
		}
		UserBalancesVO userBalancesVO = null;
		UserBalancesDAO userBalancesDAO = null;
		try {
			userBalancesVO = prepareUserBalanceVOFromTransferVO(pAdjustmentsVO, PretupsI.TRANSFER_TYPE_C2S,
					pC2sTransferVO.getSourceType(), pAdjustmentsVO.getEntryType(), PretupsI.TRANSFER_TYPE_DIFFCR, "");
			userBalancesDAO = new UserBalancesDAO();

			userBalancesDAO.updateUserDailyBalances(pcon, pAdjustmentsVO.getCreatedOn(), userBalancesVO);
			// Credit the sender
			int updateCount = 0;
		
			updateCount = userBalancesDAO.creditUserBalances(pcon, userBalancesVO, pCategoryCode);
			
			if (updateCount <= 0) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_NOT_CREDIT_BALANCE);
			}
			
			// Update Previous and Post balances of Reciever
			pAdjustmentsVO.setPreviousBalance(userBalancesVO.getPreviousBalance());
			pAdjustmentsVO.setPostBalance(userBalancesVO.getBalance());
			BalanceLogger.log(userBalancesVO);
		} catch (BTSLBaseException be) {
			
			
			log.errorTrace(methodName, be);
			throw be;
	       
		} catch (Exception e) {
		
			log.errorTrace(methodName ,e);
			log.error(methodName,pC2sTransferVO.getTransferID()+" : "+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,PromoBonusCalBL.class.getName()+"["+methodName+"]",pC2sTransferVO.getTransferID(),pC2sTransferVO.getSenderMsisdn(),pC2sTransferVO.getNetworkCode(),e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_SQL_ERROR_EXCEPTION);


		} 
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting adjustments ID : " + pAdjustmentsVO.getAdjustmentID());
		}
	}

	/**
	 * Prepares the balance VO for crediting the user
	 * 
	 * @param pAdjustmentsVO
	 * @param pTransferType
	 * @param pSource
	 * @param pEntryType
	 * @param pTransType
	 * @param pTransferCategory
	 * @return UserBalancesVO
	 */
	private UserBalancesVO prepareUserBalanceVOFromTransferVO(AdjustmentsVO pAdjustmentsVO, String pTransferType,
			String pSource, String pEntryType, String pTransType, String pTransferCategory) {
		UserBalancesVO userBalancesVO = UserBalancesVO.getInstance();
		userBalancesVO.setUserID(pAdjustmentsVO.getUserID());
		userBalancesVO.setProductCode(pAdjustmentsVO.getProductCode());
		userBalancesVO.setNetworkCode(pAdjustmentsVO.getNetworkCode());
		userBalancesVO.setNetworkFor(pAdjustmentsVO.getNetworkCodeFor());
		userBalancesVO.setLastTransferID(pAdjustmentsVO.getAdjustmentID());
		userBalancesVO.setLastTransferType(pTransferType);
		userBalancesVO.setLastTransferOn(pAdjustmentsVO.getCreatedOn());
		userBalancesVO.setQuantityToBeUpdated(pAdjustmentsVO.getTransferValue());
		userBalancesVO.setSource(pSource);
		userBalancesVO.setCreatedBy(pAdjustmentsVO.getCreatedBy());
		userBalancesVO.setEntryType(pEntryType);
		userBalancesVO.setType(pTransType);
		userBalancesVO.setTransferCategory(pTransferCategory);
		userBalancesVO.setRequestedQuantity(String.valueOf(requestedAmount));
		userBalancesVO.setOtherInfo("Reference ID=" + pAdjustmentsVO.getReferenceID());
		// Add to log user MSISDN on 20/02.2008
		userBalancesVO.setUserMSISDN(pAdjustmentsVO.getUserMSISDN());
		if (log.isDebugEnabled()) {
			log.debug("prepareUserBalanceVOFromTransferVO", " userBalancesVO=" + userBalancesVO.toString());
		}
		return userBalancesVO;
	}
	
	
	/**
	 * @param pC2sTransferVO
	 * @param pPromoBonusAdjustmentOwnerVODebit 
	 * @throws BTSLBaseException
	 */
	private void populateAdjustmentOwnerBonusDebit(C2STransferVO pC2sTransferVO, AdjustmentsVO pPromoBonusAdjustmentOwnerVODebit) throws BTSLBaseException
	{
		
		String methodName="populateAdjustmentOwnerBonusDebit";
		
		if (log.isDebugEnabled())
			log.debug(methodName,
					" Entered Transfer ID:" + pC2sTransferVO.getTransferID());
		try
		{
			String adjustmentOwnerDebitID=pC2sTransferVO.getTransferID()+"DB";
			
			pPromoBonusAdjustmentOwnerVODebit.setAdjustmentID(adjustmentOwnerDebitID);
			pPromoBonusAdjustmentOwnerVODebit.setAdjustmentDate(currentDate);
			pPromoBonusAdjustmentOwnerVODebit.setAdjustmentType(PretupsI.TRANSFER_TYPE_DIFFDR);
			pPromoBonusAdjustmentOwnerVODebit.setCreatedBy(PretupsI.SYSTEM_USER);
			pPromoBonusAdjustmentOwnerVODebit.setCreatedOn(currentDate);
			pPromoBonusAdjustmentOwnerVODebit.setModifiedBy(PretupsI.SYSTEM_USER);
			pPromoBonusAdjustmentOwnerVODebit.setModifiedOn(currentDate);
			pPromoBonusAdjustmentOwnerVODebit.setDifferentialFactor(1);
			pPromoBonusAdjustmentOwnerVODebit.setEntryType(PretupsI.DEBIT);
			pPromoBonusAdjustmentOwnerVODebit.setMarginRate(0);
			pPromoBonusAdjustmentOwnerVODebit.setMarginAmount(pC2sTransferVO.getPromoBonus());
			pPromoBonusAdjustmentOwnerVODebit.setTransferValue(pC2sTransferVO.getPromoBonus());
			pPromoBonusAdjustmentOwnerVODebit.setCommisssionType(PretupsI.OTF_COMMISSION);
			pPromoBonusAdjustmentOwnerVODebit.setStockUpdated(PretupsI.YES);
			pPromoBonusAdjustmentOwnerVODebit.setMarginType(PretupsI.SYSTEM_AMOUNT);
			pPromoBonusAdjustmentOwnerVODebit.setModule(pC2sTransferVO.getModule());
			pPromoBonusAdjustmentOwnerVODebit.setNetworkCode(pC2sTransferVO.getNetworkCode());
			//pPromoBonusAdjustmentOwnerVODebit.setUserID(((ChannelUserVO)pC2sTransferVO.getSenderVO()).getOwnerID());
			pPromoBonusAdjustmentOwnerVODebit.setUserID(PretupsI.OPERATOR_USER_TYPE);
			pPromoBonusAdjustmentOwnerVODebit.setUserMSISDN(((ChannelUserVO)pC2sTransferVO.getSenderVO()).getOwnerMsisdn());
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				pPromoBonusAdjustmentOwnerVODebit.setNetworkCodeFor(pC2sTransferVO.getNetworkCode());
			else
				pPromoBonusAdjustmentOwnerVODebit.setNetworkCodeFor(pC2sTransferVO.getReceiverNetworkCode());
			pPromoBonusAdjustmentOwnerVODebit.setProductCode(pC2sTransferVO.getProductCode());
			pPromoBonusAdjustmentOwnerVODebit.setReferenceID(pC2sTransferVO.getTransferID());
			pPromoBonusAdjustmentOwnerVODebit.setServiceType(pC2sTransferVO.getServiceType());
			//pPromoBonusAdjustmentOwnerVODebit.setUserCategory(((ChannelUserVO)pC2sTransferVO.getSenderVO()).getOwnerCategoryName());
			pPromoBonusAdjustmentOwnerVODebit.setUserCategory(PretupsI.OPERATOR_CATEGORY);
			pPromoBonusAdjustmentOwnerVODebit.setTax1Rate(0);
			pPromoBonusAdjustmentOwnerVODebit.setTax1Type(PretupsI.SYSTEM_AMOUNT);
			pPromoBonusAdjustmentOwnerVODebit.setTax2Rate(0);
			pPromoBonusAdjustmentOwnerVODebit.setTax2Type(PretupsI.SYSTEM_AMOUNT);
			pPromoBonusAdjustmentOwnerVODebit.setAddnlCommProfileDetailID("0");
			pPromoBonusAdjustmentOwnerVODebit.setSubService(pC2sTransferVO.getSubService());
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception Transfer ID:"+pC2sTransferVO.getTransferID()+" Exception:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PromoBonusCalBL[populateAdjustmentOwnerBonusDebit]",pC2sTransferVO.getTransferID(),pC2sTransferVO.getSenderMsisdn(),pC2sTransferVO.getNetworkCode(),e.getMessage());
			throw new BTSLBaseException(this,"populateAdjustmentOwnerBonusDebit",PretupsErrorCodesI.ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled())
			log.debug(methodName, " Exit: adjustmentVO"
					+ pPromoBonusAdjustmentOwnerVODebit);
	}
	
	
	
	/**
	 * @param pC2sTransferVO
	 * @param promoBonusAdjustmentOwnerVOCredit 
	 * @throws BTSLBaseException
	 */
	private void populateAdjustmentOwnerBonusCredit(C2STransferVO pC2sTransferVO, AdjustmentsVO promoBonusAdjustmentOwnerVOCredit) throws BTSLBaseException
	{
		
		String methodName="populateAdjustmentOwnerBonusCredit";
		
		if (log.isDebugEnabled())
			log.debug(methodName," Entered Transfer ID:" + pC2sTransferVO.getTransferID());
		try
		{
			String adjustmentOwnerCreditID=pC2sTransferVO.getTransferID()+"OB";
			
			promoBonusAdjustmentOwnerVOCredit.setAdjustmentID(adjustmentOwnerCreditID);
			promoBonusAdjustmentOwnerVOCredit.setAdjustmentDate(currentDate);
			promoBonusAdjustmentOwnerVOCredit.setAdjustmentType(PretupsI.ADJUSTMENT_TYPE_DIFFERENTIAL);
			promoBonusAdjustmentOwnerVOCredit.setCreatedBy(PretupsI.SYSTEM_USER);
			promoBonusAdjustmentOwnerVOCredit.setCreatedOn(currentDate);
			promoBonusAdjustmentOwnerVOCredit.setModifiedBy(PretupsI.SYSTEM_USER);
			promoBonusAdjustmentOwnerVOCredit.setModifiedOn(currentDate);
			promoBonusAdjustmentOwnerVOCredit.setDifferentialFactor(1);
			promoBonusAdjustmentOwnerVOCredit.setEntryType(PretupsI.CREDIT);
			promoBonusAdjustmentOwnerVOCredit.setMarginRate(0);
			promoBonusAdjustmentOwnerVOCredit.setMarginAmount(pC2sTransferVO.getPromoBonus());
			promoBonusAdjustmentOwnerVOCredit.setTransferValue(pC2sTransferVO.getPromoBonus());
			promoBonusAdjustmentOwnerVOCredit.setCommisssionType(PretupsI.OTF_COMMISSION);
			promoBonusAdjustmentOwnerVOCredit.setStockUpdated(PretupsI.YES);
			promoBonusAdjustmentOwnerVOCredit.setMarginType(PretupsI.SYSTEM_AMOUNT);
			promoBonusAdjustmentOwnerVOCredit.setModule(pC2sTransferVO.getModule());
			promoBonusAdjustmentOwnerVOCredit.setNetworkCode(pC2sTransferVO.getNetworkCode());
			promoBonusAdjustmentOwnerVOCredit.setUserID(((ChannelUserVO)pC2sTransferVO.getSenderVO()).getOwnerID());
			promoBonusAdjustmentOwnerVOCredit.setUserMSISDN(((ChannelUserVO)pC2sTransferVO.getSenderVO()).getOwnerMsisdn());
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue())
				promoBonusAdjustmentOwnerVOCredit.setNetworkCodeFor(pC2sTransferVO.getNetworkCode());
			else
				promoBonusAdjustmentOwnerVOCredit.setNetworkCodeFor(pC2sTransferVO.getReceiverNetworkCode());
			promoBonusAdjustmentOwnerVOCredit.setProductCode(pC2sTransferVO.getProductCode());
			promoBonusAdjustmentOwnerVOCredit.setReferenceID(pC2sTransferVO.getTransferID());
			promoBonusAdjustmentOwnerVOCredit.setServiceType(pC2sTransferVO.getServiceType());
			promoBonusAdjustmentOwnerVOCredit.setUserCategory(((ChannelUserVO)pC2sTransferVO.getSenderVO()).getOwnerCategoryName());
			promoBonusAdjustmentOwnerVOCredit.setTax1Rate(0);
			promoBonusAdjustmentOwnerVOCredit.setTax1Type(PretupsI.SYSTEM_AMOUNT);
			promoBonusAdjustmentOwnerVOCredit.setTax2Rate(0);
			promoBonusAdjustmentOwnerVOCredit.setTax2Type(PretupsI.SYSTEM_AMOUNT);
			promoBonusAdjustmentOwnerVOCredit.setAddnlCommProfileDetailID("0");
			promoBonusAdjustmentOwnerVOCredit.setSubService(pC2sTransferVO.getSubService());
		}
		catch(Exception e)
		{
			log.error(methodName,"Exception Transfer ID:"+pC2sTransferVO.getTransferID()+" Exception:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PromoBonusCalBL[populateAdjustmentOwnerBonusCredit]",pC2sTransferVO.getTransferID(),pC2sTransferVO.getSenderMsisdn(),pC2sTransferVO.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled())
			log.debug(methodName, " Exit: adjustmentVO"
					+ promoBonusAdjustmentOwnerVOCredit);
	}
	
	
	
	/**
	 * @param pC2sTransferVO 
	 * @param p_con
	 * @param p_adjustmentsOwnerVODebit
	 * @param p_adjustmentsOwnerVOCredit
	 * @throws BTSLBaseException
	 */
	public void debitAndCreditOwnerBalanceForProduct(C2STransferVO pC2sTransferVO, AdjustmentsVO promoBonusAdjustmentOwnerVODebit, AdjustmentsVO promoBonusAdjustmentOwnerVOCredit) throws BTSLBaseException
	{
		String methodName="debitAndCreditOwnerBalanceForProduct";
		
		if (log.isDebugEnabled())
			log.debug(methodName, "Entered p_adjustmentsOwnerVODebit:"
					+ promoBonusAdjustmentOwnerVODebit
					+ "Entered p_adjustmentsOwnerVOCredit:"
					+ promoBonusAdjustmentOwnerVOCredit);
		try
		{
			UserBalancesVO userBalancesVO=prepareUserBalanceVOFromTransferVO(promoBonusAdjustmentOwnerVOCredit,PretupsI.TRANSFER_TYPE_DIFFCR,pC2sTransferVO.getSourceType(),PretupsI.CREDIT,PretupsI.TRANSFER_TYPE_DIFFCR,"");
			UserBalancesVO userBalancesDebitVO=prepareUserBalanceVOFromTransferVO(promoBonusAdjustmentOwnerVODebit,PretupsI.TRANSFER_TYPE_DIFFDR,pC2sTransferVO.getSourceType(),PretupsI.DEBIT,PretupsI.TRANSFER_TYPE_DIFFDR,"");
			UserBalancesDAO userBalancesDAO= new UserBalancesDAO();
			userBalancesDAO.updateUserDailyBalances(con,promoBonusAdjustmentOwnerVOCredit.getCreatedOn(),userBalancesVO);
			
			int updateCount=0;
			
				updateCount=new UserBalancesDAO().diffCreditAndDebitUserBalances(con, userBalancesDebitVO,userBalancesVO);
			
			if(updateCount<=0)
				throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.C2S_ERROR_NOT_DEBIT_BALANCE);
			promoBonusAdjustmentOwnerVOCredit.setPreviousBalance(userBalancesVO.getPreviousBalance());
			promoBonusAdjustmentOwnerVOCredit.setPostBalance(userBalancesVO.getBalance());
		
			BalanceLogger.log(userBalancesVO);
			BalanceLogger.log(userBalancesDebitVO);
		} 
		catch (BTSLBaseException be)
		{
			throw be;
		}
		catch (Exception e)
		{
			log.error(methodName, "Exception " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PromoBonusCalBL[debitAndCreditOwnerBalanceForProduct]",promoBonusAdjustmentOwnerVODebit.getAdjustmentID(),"",promoBonusAdjustmentOwnerVOCredit.getNetworkCode(),"Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (log.isDebugEnabled())
		{
			log.debug(methodName, "Exiting p_adjustmentsOwnerVODebit:"
					+ promoBonusAdjustmentOwnerVODebit
					+ "Entered p_adjustmentsOwnerVOCredit:"
					+ promoBonusAdjustmentOwnerVOCredit);
		}
	}
}
