package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;

public class O2CTransferInitiateMappController  implements ServiceKeywordControllerI {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	 StringBuilder loggerValue= new StringBuilder(); 
	Connection con = null;
	MComConnectionI mcomCon = null;
	
 	@Override
	public void process(RequestVO p_requestVO) {
		final String methodName = "process";
		_log.debug(methodName, "entered");
	
		String msisdn = "";

		Connection con = null;
		 int insertCount = 0;
		 int updateCount=0;
	     ChannelTransferItemsVO channelTransferItemsVO = null;
	     Date currentDate = null;
	     ChannelUserDAO channelUserDAO = null;
		StringBuffer responseStr = new StringBuffer();
		
        final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
      
        HashMap responseMap = new HashMap<>();
		
		HashMap reqMap = p_requestVO.getRequestMap();
		try
		{
			if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null) ) 
			{
				
				msisdn = (String) reqMap.get("MSISDN");
				
			}
			else if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
			{
				_log.debug(methodName, "getting msisdn in mobile gateway");
				
			}
			else
			{
				p_requestVO.setSenderReturnMessage("Msisdn or loginId is required.");
				p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_REQ_EITHER_MSISDN_LOGINID_REQ);
				return;
			}
			
			mcomCon = new MComConnection();
	        con=mcomCon.getConnection();
			BarredUserDAO barredUserDAO = null;
			barredUserDAO = new BarredUserDAO();
			 currentDate = new Date();

			ChannelUserVO channelUserVO=new ChannelUserVO();
			UserDAO userDao = new UserDAO();
			channelUserDAO = new ChannelUserDAO();
	       
	        if(!"".equals(msisdn))
	        {
	        	channelUserVO = userDao.loadUserDetailsByMsisdn(con, msisdn);
	        }
	        
	      //checkinh for sales tranfer category
	        String trfcat="";
	        
	        if(!BTSLUtil.isNullObject(reqMap.get("TRFCATEGORY"))) {
	        	 trfcat=(String)reqMap.get("TRFCATEGORY");
	        	 if(trfcat.replace("\n\n", "").equalsIgnoreCase("SALE") || trfcat.replace("\n\n", "").equalsIgnoreCase("SALE ")) {
	        		 trfcat="SALE";
	        	 }
	        }
	        
	 
	        if(!trfcat.equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE) || BTSLUtil.isNullString(trfcat)){
	        	
				throw new BTSLBaseException(O2CTransferInitiateMappController.class, methodName, PretupsErrorCodesI.ERROR_TRANSFER_CATEGORY_NOT_ALLOWED);

	        }

			// check that the channel user is barred or not
			if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, channelUserVO.getNetworkID(),channelUserVO.getMsisdn(), PretupsI.USER_TYPE_RECEIVER, null)) {
				throw new BTSLBaseException(O2CTransferInitiateMappController.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR);
			}
			
			// check that the channel user should not be in suspended
			if (channelUserVO.getInSuspend().equalsIgnoreCase(PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND)) {
				throw new BTSLBaseException(O2CTransferInitiateMappController.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_IN_SUSPENDED,new String[]{channelUserVO.getUserCode()});
			}

			// check that the commission profile of channel user
			// should be active 
			CommissionProfileTxnDAO commissionProfileTxnDAO = new CommissionProfileTxnDAO();
			String status=commissionProfileTxnDAO.loadCommProfileStatusById(con, channelUserVO.getCommissionProfileSetID());
			 if (!PretupsI.STATUS_ACTIVE.equalsIgnoreCase(status)) {
				throw new BTSLBaseException(O2CTransferInitiateMappController.class, methodName, PretupsErrorCodesI.ERROR_COMMISSION_PROFILE_SUSPENDED,new String[]{channelUserVO.getUserCode()," Commission Profile is Suspended"});
			} 
			 
			 

			
			 
			 ChannelUserVO userVO = (ChannelUserVO)(p_requestVO.getSenderVO());
	    	String transferType = PretupsI.TRANSFER_TYPE_O2C, paymentType = PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE, 
	    	dualCommissionType = userVO.getDualCommissionType(), 
	    	commissionProfileID = userVO.getCommissionProfileSetID(), commissionProfileVersion = userVO.getCommissionProfileSetVersion();
	    		String products=(String) reqMap.get("PRODUCTS");
	    		ArrayList<ChannelTransferItemsVO> itemsList = new ArrayList<>();
	    		String []itemsLists = products.split(",");
	    		if(itemsLists.length > 1) {
					throw new BTSLBaseException(O2CTransferInitiateMappController.class, methodName, PretupsErrorCodesI.MULTIPLE_PROD);

	    		}
	    		for(int i=0;i<itemsLists.length;i++)
	    		{
	    			 channelTransferItemsVO = new ChannelTransferItemsVO();
	    			String []product = itemsLists[i].split(":");
	    			if(product.length != 2)
	    				throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.MAPPGW_INVALID_PRODUCTS_FORMAT);
	    			if(BTSLUtil.isNullString(product[0]) || BTSLUtil.isNullString(product[1]) ) {
	    				throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.MAPPGW_INVALID_PRODUCTS_FORMAT);

	    			}
	    			if(!BTSLUtil.isNumeric(product[0]) || !BTSLUtil.isNumeric(product[1]) )
	    			{	    				throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.MAPPGW_INVALID_PRODUCTS_FORMAT);
}
	    			channelTransferItemsVO.setRequestedQuantity(product[0]);
	    			channelTransferItemsVO.setProductName(channelUserDAO.product(con, product[1]));
	    			channelTransferItemsVO.setProductCode(channelUserDAO.product(con, product[1]));
	    			itemsList.add(channelTransferItemsVO);
	    		}
	    		
	           /* for(int i=0;i<itemsLists.length;i=i+2)
	     		{
	     			
	            	channelTransferItemsVO = new ChannelTransferItemsVO();
	    			channelTransferItemsVO.setRequestedQuantity(itemsLists[i]);
	    			channelTransferItemsVO.setProductCode(channelUserDAO.product(con, itemsLists[i+1]));
	    			channelTransferItemsVO.setProductName(channelUserDAO.product(con, itemsLists[i+1]));
	    			itemsList.add(channelTransferItemsVO);
	     		}*/
	     		
	     		ArrayList<NetworkProductVO> productList = (new NetworkProductDAO()).loadProductListForXfr(con, null, p_requestVO.getRequestNetworkCode());
	     		NetworkProductVO networkProductVO = new NetworkProductVO();
	     		for(int i=0;i<itemsList.size();i++)
	     		{
	     			 channelTransferItemsVO = (ChannelTransferItemsVO)itemsList.get(i);
	     			boolean isProductValid = false;
	     			for(int j=0;j<productList.size();j++)
	     			{
	     				networkProductVO=(NetworkProductVO)productList.get(j);
	     				if(networkProductVO.getProductCode().equals(channelTransferItemsVO.getProductName()))
	                     {
	     					channelTransferItemsVO.setUnitValue(networkProductVO.getUnitValue());
	     					channelTransferItemsVO.setShortName(networkProductVO.getShortName());
	     					isProductValid = true;
	     					break;
	                     }
	     			}
	     			if(!isProductValid)
	     				throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.MAPPGW_INVALID_PRODUCT_CODE, 0, new String[]{"entered"} , null);
	     		}
	     		
	     		channelTransferVO.setChannelTransferitemsVOList(itemsList);
	     	
	     		channelTransferVO.setDualCommissionType(dualCommissionType);
	     		channelTransferVO.setType(transferType);
	     		channelTransferVO.setNetworkCode(p_requestVO.getRequestNetworkCode());
	     		channelTransferVO.setPayInstrumentType(paymentType);
	    			
	     		channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_PENDING);
	     		channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
	     		channelTransferVO.setToUserID(channelUserVO.getUserID());
	     		channelTransferVO.setOtfFlag(true);
	     		
	     		channelTransferVO.setToUserMsisdn(msisdn);
	     		channelTransferVO.setCommProfileVersion(commissionProfileVersion);
	     		channelTransferVO.setTransferCategory("SALE");
	     		CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
	     		
	    		commissionProfileDAO.loadProductListWithTaxes(con, commissionProfileID, commissionProfileVersion, itemsList, transferType, paymentType);
	    		final ArrayList<KeyArgumentVO> errorList = new ArrayList<KeyArgumentVO>();
	    		ArrayList<Serializable> commmProfileDetailList=new ArrayList(); 
	    		
	    		
	    		String type = (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
	    		final ArrayList commissionProfileProductList = commissionProfileDAO.loadCommissionProfileProductsList(con,commissionProfileID, commissionProfileVersion, type);

	    		// if list is empty send the error message
	    		if (commissionProfileProductList == null || commissionProfileProductList.isEmpty()) {
	    			throw new BTSLBaseException(O2CTransferInitiateMappController.class, methodName, "message.transfer.nodata.commprofileproduct"
	    				);
	    		}

	    		// filterize the product list with the products of the commission
	    		// profile products
	    		CommissionProfileProductsVO commissionProfileProductsVO = null;
	    		for (int i = 0, j = commissionProfileProductList.size(); i < j; i++) {
	    			commissionProfileProductsVO = (CommissionProfileProductsVO) commissionProfileProductList.get(i);
	    			for (int m = 0,  n = itemsList.size(); m < n; m++) {
	    				channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(m);
	    				if (channelTransferItemsVO.getProductCode().equals(commissionProfileProductsVO.getProductCode())) {
	    					channelTransferItemsVO.setMinTransferValue(commissionProfileProductsVO.getMinTransferValue());
	    					channelTransferItemsVO.setMaxTransferValue(commissionProfileProductsVO.getMaxTransferValue());
	    					
	    					break;
	    				}
	    			}
	    		}

	    		String minAmount=PretupsBL.getDisplayAmount(channelTransferItemsVO.getMinTransferValue());
	    		String maxAmount=PretupsBL.getDisplayAmount(channelTransferItemsVO.getMaxTransferValue());
	    		//
	    		for (int i = 0, k = itemsList.size(); i < k; i++) {
	    			 channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
	    			KeyArgumentVO argumentVO = new KeyArgumentVO();
	    			if (!channelTransferItemsVO.isSlabDefine()) {
		    			throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), "loadAndCalculateTaxOnProducts", PretupsErrorCodesI.REQ_QNT, new String[] { channelTransferItemsVO.getProductName(),minAmount,maxAmount });

	    			}
	    			else if ((PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity()) % channelTransferItemsVO.getTransferMultipleOf()) != 0) {
	    				argumentVO = new KeyArgumentVO();
	    				argumentVO.setKey("channeltransfer.transferdetails.error.multipleof");
	    				argumentVO.setArguments(new String[] { channelTransferItemsVO.getProductName(), PretupsBL.getDisplayAmount(channelTransferItemsVO.getTransferMultipleOf()) });
	    				errorList.add(argumentVO);
	    			}
	    		}
	    		if (!errorList.isEmpty()) {
	    			throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), "loadAndCalculateTaxOnProducts", PretupsErrorCodesI.ERROR_COMMISSION_SLAB_NOT_DEFINE, errorList);	    		}
	    		
	    		if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE))) {
	                ChannelTransferBL.loadAndCalculateTaxOnProducts(con, channelUserVO.getCommissionProfileSetID(), commissionProfileVersion,
	                    channelTransferVO, false, null, PretupsI.TRANSFER_TYPE_O2C);
	            }
	    		
	    		/*// validate the channel user
	            
	            ChannelTransferBL.o2cTransferUserValidate(con, p_requestVO, channelTransferVO, currentDate);
	            */
	    		
	    		// load the transfe rules asociated with
	    		// channel user
	    		final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
	    		final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con, channelUserVO.getNetworkID(), channelUserVO.getDomainID(),
	    				PretupsI.CATEGORY_TYPE_OPT, channelUserVO.getCategoryCode(), PretupsI.TRANSFER_RULE_TYPE_OPT, true);

	    		if (BTSLUtil.isNullObject(channelTransferRuleVO)) {
	    			throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_NOT_DEFINE);
	    		}

	    		ArrayList productList1 = null;
	    		// load the product list associated with the transfer rule
	    		productList1 = channelTransferRuleVO.getProductVOList();

	    		if (productList1 == null || productList1.isEmpty()) {
	    			throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_RULE_PRODUCT_NOT_ASSOCIATED);
	    		}// end of if

	    		// If transfer category (SALE or FOC) is not defined
	    		// in request then consider the request as Transfer
	    		
	    			

	    			// check the transfer allowed if trf. category is 'SALE'
	    			if ((trfcat.equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE)) && (!PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getTransferAllowed()))) {
	    				throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_ASSOCIATED_NOT_ALLOWED);
	    			}

	    			// check the FOC allowed if trf. category is 'FOC'
	    			else if ((trfcat.equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_FOC)) && (!PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getFocAllowed()))) {
	    				throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_ASSOCIATED_NOT_ALLOWED);
	    			}

	    			// set the 1st & 2nd approval limits
	    			// in the channelTransferVO
	    			
	    			if ((trfcat.equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE)) && (PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getTransferAllowed()))) {
	    				channelTransferVO.setFirstApproverLimit(channelTransferRuleVO.getFirstApprovalLimit());
	    				channelTransferVO.setSecondApprovalLimit(channelTransferRuleVO.getSecondApprovalLimit());
	    			}

	    		
	    			// check the transfer allowed for the channel user
	    			if (!PretupsI.YES.equalsIgnoreCase(channelTransferRuleVO.getTransferAllowed())) {
	    				throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_ASSOCIATED_NOT_ALLOWED);
	    			}
	    		

	    		
	    		 // load the user's information (network admin)
	            final UserVO userVO1 = channelUserDAO.loadOptUserForO2C(con, channelUserVO.getNetworkID());
	    		
	         // prepares the ChannelTransferVO by populating its fields from the
	            // passed ChannelUserVO and filteredList of products for O2C
	            // transfer
	            prepareChannelTransferVO(p_requestVO, channelTransferVO, currentDate, channelUserVO, itemsList, userVO1);
	    		
	    		
	    	
	          //Validate MRP && Successive Block for channel transaction
				long successiveReqBlockTime4ChnlTxn = ((Long)PreferenceCache.getSystemPreferenceValue(PreferenceI.SUCCESS_REQUEST_BLOCK_SEC_CODE_O2C)).longValue();
				ChannelTransferBL.validateChannelLastTransferMrpSuccessiveBlockTimeout(con, channelTransferVO, currentDate, successiveReqBlockTime4ChnlTxn);
				
	            // generate transfer ID for the O2C transfer
	            ChannelTransferBL.genrateTransferID(channelTransferVO);

	            // set the transfer ID in each ChannelTransferItemsVO of productList
	            for (int i = 0, j = itemsList.size(); i < j; i++) {
	                channelTransferItemsVO = (ChannelTransferItemsVO) itemsList.get(i);
	                channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
	            }
	            
	         // the transfer is controlled by default, so set to 'Y'
	            channelTransferVO.setControlTransfer(PretupsI.YES);
	           
	            updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(con, channelTransferVO, null, currentDate);
	            if (updateCount < 1) {
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
	            }
	            
	            // performs all the approval transactions for the transfer opertaion
	            transactionApproval(con, channelTransferVO, userVO1.getUserID(), currentDate, channelUserVO);
	     
	            final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();
	           
	            insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
	            if (insertCount < 0) {
	            	OracleUtil.rollbackConnection(con, O2CTransferInitiateMappController.class.getName(), methodName);
	            	 p_requestVO.setSuccessTxn(false);
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
	            }

	            OracleUtil.commit(con);
	            p_requestVO.setSuccessTxn(true);
	     		
	     		
	     		
	     		
	     		
	     		          
            
            
	        if("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
	        {    _log.debug(methodName, "Preparing mobile app gateway response");
	        	
	        	 	String resType = null;
					resType = reqMap.get("TYPE") + "RES";
					responseStr.append("{ \"type\": \"" + resType + "\" ,");
					responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
					responseStr.append(" \"message\": \"" + "Transfer ID "+ channelTransferVO.getTransferID()+" has been successfully initiated." + "\" ,");
					responseStr.append(" \"txnId\": \"" + channelTransferVO.getTransferID() + "\"");
					responseStr.append("}");
					
	        	
	        	responseMap.put("RESPONSE", responseStr);
				p_requestVO.setResponseMap(responseMap);
				p_requestVO.setMessageCode(PretupsErrorCodesI.DIRECT_AGENT_O2C);
				p_requestVO.setMessageArguments(new String[]{channelTransferVO.getTransferID()});
				p_requestVO.setSuccessTxn(true);
			
				

	        }
	        
	        
	        
	        
	        
		}
		catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            OracleUtil.rollbackConnection(con, O2CDirectTransferController.class.getName(), methodName);
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(be.getMessage());
            _log.error(methodName,  loggerValue );
            _log.errorTrace(methodName, be);
            if (be.getMessageList() != null && !be.getMessageList().isEmpty()) {
                final String[] array = { BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
                p_requestVO.setMessageArguments(array);
            }
            if (be.getArgs() != null) {
                p_requestVO.setMessageArguments(be.getArgs());
               
            }
            if (be.getMessageKey() != null) {
                p_requestVO.setMessageCode(be.getMessageKey());
               
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
            }
             return;
        }
		
		catch (Exception e) {
			
			_log.debug(methodName, "In catch block");
      	  	_log.error(methodName, "Exception:e=" + e);
      	  p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
      	 p_requestVO.setSuccessTxn(false);
      	  
		}
		 
		
	}
 	
 	/**
     * Method prepareChannelTransferVO
     * This method used to construct the VO for channel transfer
     * 
     * @param p_requestVO
     *            RequestVO
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @param p_curDate
     *            Date
     * @param p_channelUserVO
     *            ChannelUserVO
     * @param p_prdList
     *            ArrayList
     * @throws BTSLBaseException
     */
    private void prepareChannelTransferVO(RequestVO p_requestVO, ChannelTransferVO p_channelTransferVO, Date p_curDate, ChannelUserVO p_channelUserVO, ArrayList<ChannelTransferItemsVO> p_prdList, UserVO p_userVO) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
	        loggerValue.setLength(0);
        	loggerValue.append( "Entering  : requestVO ");
        	loggerValue.append(p_requestVO);
        	loggerValue.append("p_channelTransferVO:");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append("p_curDate:" );
        	loggerValue.append(p_curDate);
        	loggerValue.append("p_channelUserVO:");
        	loggerValue.append(p_channelUserVO);
        	loggerValue.append("p_prdList:");
        	loggerValue.append(p_prdList);
        	loggerValue.append("p_userVO:");
        	loggerValue.append(p_userVO);
        	loggerValue.append( "sourceType: ");
        	loggerValue.append(p_requestVO.getSourceType());
            _log.debug("prepareChannelTransferVO",loggerValue );
        }
        
        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverCategoryCode(p_channelUserVO.getCategoryCode());
        p_channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
        /*user name set to display user name in message for transfer in counts DEF51 claro*/
        p_channelTransferVO.setToUserName(p_channelUserVO.getUserName());
        // who initaite the order.
        p_channelTransferVO.setReceiverGradeCode(p_channelUserVO.getUserGrade());
        p_channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setToUserID(p_channelUserVO.getUserID());
        p_channelTransferVO.setToUserCode(p_channelUserVO.getUserCode());
        // To display MSISDN in balance log
        p_channelTransferVO.setUserMsisdn(p_channelUserVO.getUserCode());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
        
        /*p_channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType());*/
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(p_userVO.getUserID());
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(p_userVO.getUserID());
       
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
        p_channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
        p_channelTransferVO.setReceiverTxnProfile(p_channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource(p_requestVO.getSourceType());

        // adding the some additional information of sender/reciever
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setToUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        p_channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        p_channelTransferVO.setActiveUserId(p_userVO.getUserID());

        ChannelTransferItemsVO channelTransferItemsVO = null;
        String productType = null;
        long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
        for (int i = 0, k = p_prdList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_prdList.get(i);
            totRequestQty += channelTransferItemsVO.getRequiredQuantity();
            if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
                totMRP += (channelTransferItemsVO.getReceiverCreditQty()) * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
            } else {
                totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue());
            }
            totPayAmt += channelTransferItemsVO.getPayableAmount();
            totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
            totTax1 += channelTransferItemsVO.getTax1Value();
            totTax2 += channelTransferItemsVO.getTax2Value();
            totTax3 += channelTransferItemsVO.getTax3Value();

            productType = channelTransferItemsVO.getProductType();
         
            commissionQty += channelTransferItemsVO.getCommQuantity();
            senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
            receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
        }

        p_channelTransferVO.setRequestedQuantity(totRequestQty);
        p_channelTransferVO.setTransferMRP(totMRP);
        p_channelTransferVO.setPayableAmount(totPayAmt);
        p_channelTransferVO.setNetPayableAmount(totNetPayAmt);
        p_channelTransferVO.setTotalTax1(totTax1);
        p_channelTransferVO.setTotalTax2(totTax2);
        p_channelTransferVO.setTotalTax3(totTax3);
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
        p_channelTransferVO.setChannelTransferitemsVOList(p_prdList);
        p_channelTransferVO.setPayInstrumentAmt(totNetPayAmt);
        p_channelTransferVO.setProductType(productType);
        p_channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
        p_channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
        p_channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));
        //Added by lalit to fix bug DEF528 for GP 6.6.1
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue() && p_requestVO.getRequestMap() != null) {
        	Map<String, String> requestMap = p_requestVO.getRequestMap();
        	if(PretupsI.TRANSFER_TYPE_FOC.equalsIgnoreCase((requestMap.get("TRFCATEGORY")!=null?requestMap.get("TRFCATEGORY").toString():""))){
        		p_channelTransferVO.setWalletType(requestMap.get("TRFCATEGORY").toString());
        	}
        }
        final long firstApprovalLimit = p_channelTransferVO.getFirstApproverLimit();
        final long secondApprovalLimit = p_channelTransferVO.getSecondApprovalLimit();

        if (p_channelTransferVO.getRequestedQuantity() > secondApprovalLimit) {
            p_channelTransferVO.setThirdApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setThirdApprovedOn(p_curDate);
            p_channelTransferVO.setSecondApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setSecondApprovedOn(p_curDate);
            p_channelTransferVO.setFirstApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setFirstApprovedOn(p_curDate);
        } else if (p_channelTransferVO.getRequestedQuantity() <= secondApprovalLimit && p_channelTransferVO.getRequestedQuantity() > firstApprovalLimit) {
            p_channelTransferVO.setSecondApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setSecondApprovedOn(p_curDate);
            p_channelTransferVO.setFirstApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setFirstApprovedOn(p_curDate);
        } else if (p_channelTransferVO.getRequestedQuantity() <= firstApprovalLimit) {
            p_channelTransferVO.setFirstApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setFirstApprovedOn(p_curDate);
        }
        
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
        	ArrayList chnlSoSVOList = new ArrayList();
        	chnlSoSVOList.add(new ChannelSoSVO(p_channelUserVO.getUserID(),p_channelUserVO.getMsisdn(),p_channelUserVO.getSosAllowed(),p_channelUserVO.getSosAllowedAmount(),p_channelUserVO.getSosThresholdLimit()));
        	p_channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }
        
        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferVO", "Exiting : ");
        }
    }
    
    /**
     * Method transactionApproval
     * This method responcible to Approve the O2C transaction and update
     * the network stock, update the user balances and user counts
     * 
     * @param p_con
     *            Connection
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @param p_date
     *            Date
     * @param p_userID
     *            String
     * @throws BTSLBaseException
     */
    private void transactionApproval(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userID, Date p_date, ChannelUserVO channelUserVO) throws BTSLBaseException {
    	final String methodName="transactionApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering  : p_channelTransferVO:" + p_channelTransferVO + "p_userID:" + p_userID + "p_date:" + p_date);
        }

        try {
        	/*int updateCount = -1;
        	//added for o2c direct transfer
            if(SystemPreferences.O2C_DIRECT_TRANSFER)
            {
            updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userID, p_date, true);
            if (updateCount < 1) {
                throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            updateCount = -1;
            updateCount = ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userID, p_date);
            if (updateCount < 1) {
                throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            updateCount = -1;
            if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
                updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con, p_channelTransferVO, p_userID, p_date, true);
                if (updateCount < 1) {
                    throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
                }
                updateCount = -1;
                updateCount = ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con, p_channelTransferVO, p_userID, p_date);
                if (updateCount < 1) {
                    throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
                }
            }
            p_channelTransferVO.setStockUpdated(TypesI.YES);
            }
            else
            {*/
            	 p_channelTransferVO.setStockUpdated(TypesI.NO);
            /*}
            
            UserBalancesVO userBalanceVO = null;
            ChannelTransferItemsVO chnlTrfItemsVO = null;
            final UserBalancesDAO userBalDAO = new UserBalancesDAO();
            ArrayList<ChannelTransferItemsVO> prdList = new ArrayList<>();
            prdList=p_channelTransferVO.getChannelTransferitemsVOList();
            for (int x = 0, y = prdList.size(); x < y; x++) {
                chnlTrfItemsVO = (ChannelTransferItemsVO) prdList.get(x);
                userBalanceVO = new UserBalancesVO();

                userBalanceVO.setUserID(p_channelTransferVO.getToUserID());
                userBalanceVO.setProductCode(chnlTrfItemsVO.getProductCode());
                userBalanceVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
                userBalanceVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
                userBalanceVO.setLastTransferID(p_channelTransferVO.getTransferID());
                userBalanceVO.setLastTransferType(p_channelTransferVO.getTransferType());
                userBalanceVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
                userBalanceVO.setPreviousBalance(userBalanceVO.getBalance());
                userBalanceVO.setQuantityToBeUpdated(chnlTrfItemsVO.getRequiredQuantity());
                // Added on 13/02/2008
                userBalanceVO.setUserMSISDN(p_channelTransferVO.getToUserCode());
            }

            updateCount = userBalDAO.updateUserDailyBalances(p_con, p_date, userBalanceVO);
            if (updateCount < 1) {
                throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            updateCount = -1;

            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                updateCount = channelUserDAO.creditUserBalancesForMultipleWallet(p_con, p_channelTransferVO, true, null);
            } else {
                updateCount = channelUserDAO.creditUserBalances(p_con, p_channelTransferVO, true, null);
            }

            if (updateCount < 1) {
                throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

*/           if(PretupsI.TRANSFER_TYPE_FOC.equalsIgnoreCase(p_channelTransferVO.getWalletType())){
        	   p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_FOC);
           }
           
           if(PretupsI.TRANSFER_TYPE_O2C.equalsIgnoreCase(p_channelTransferVO.getWalletType())){
        	   p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_O2C);
           }
           /* updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, null, p_date);
            if (updateCount < 1) {
                throw new BTSLBaseException(O2CTransferInitiateMappController.class.getName(), methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }*/


        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exit  : ");
            }
        }
    }


 	
}
