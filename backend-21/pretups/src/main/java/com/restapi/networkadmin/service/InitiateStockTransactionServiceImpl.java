package com.restapi.networkadmin.service;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.NetworkStockLog;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.channeluser.service.ReprintVoucherController;
import com.restapi.networkadmin.requestVO.AddStockRequestVO;
import com.restapi.networkadmin.requestVO.ConfirmStockRequestVO;
import com.restapi.networkadmin.responseVO.AddStockFinalResponseVO;
import com.restapi.networkadmin.responseVO.ConfirmStockResponseVO;
import com.restapi.networkadmin.responseVO.InitiateStockTransactionResponseVO;
import com.restapi.networkadmin.serviceI.InitiateStockTransactionServiceI;
import com.web.pretups.networkstock.businesslogic.NetworkStockWebDAO;


@Service("InitiateStockTransactionServiceI")
public class InitiateStockTransactionServiceImpl implements InitiateStockTransactionServiceI{
	
	public static final Log log = LogFactory.getLog(InitiateStockTransactionServiceImpl.class.getName());
	public static final String classname = "InitiateStockTransactionServiceImpl";

	@Override
	public InitiateStockTransactionResponseVO initiateStock(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO, InitiateStockTransactionResponseVO response,String walletType) throws Exception {
		final String METHOD_NAME = "initiateStock";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		NetworkStockWebDAO networkStockwebDAO = null;
		
		String homeStock = "";
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
            homeStock = TypesI.YES;
        }
        
        
        NetworkStockTxnItemsVO tempVO = null;
        HashMap<String, NetworkStockTxnItemsVO> map = new HashMap<String, NetworkStockTxnItemsVO>();
        ArrayList newProductList = new ArrayList();
        
        try {
	        response.setNetworkCode(userVO.getNetworkID());
	        response.setRequesterName(userVO.getUserName());
	        response.setUserID(userVO.getUserID());
	        response.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_CREATION);
	        response.setTxnType(PretupsI.CREDIT);
	        response.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_NEW);
	
	        response.setStockDateStr(BTSLUtil.getDateStringFromDate(new Date()));
	        
	        if (TypesI.YES.equals(homeStock)) {
	            response.setNetworkCodeFor(userVO.getNetworkID());
	            response.setNetworkForName(userVO.getNetworkName());
	            response.setNetworkName(userVO.getNetworkName());
	            response.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
	            networkStockwebDAO = new NetworkStockWebDAO();
	            ArrayList stockProductList = networkStockwebDAO.loadProductsForStock(con, userVO.getNetworkID(), userVO.getNetworkID(), PretupsI.C2S_MODULE);
	            if(stockProductList.isEmpty()) {
	            	 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.STOCK_PRODUCT_LIST_FAIL, 0, null);
	             }
	            
	            
	            int productListsSize= stockProductList.size();                                                                                            // product_code,wallet_type
	            for (int i = 0; i < productListsSize; i++) {
	                tempVO = (NetworkStockTxnItemsVO) stockProductList.get(i);
	                if (((NetworkStockTxnItemsVO) map.get(tempVO.getProductCode())) != null) {
	                    if (((NetworkStockTxnItemsVO) map.get(tempVO.getProductCode())).getWalletType() != null) {
	                        continue;
	                    }
	                }
	                if (BTSLUtil.isNullString(tempVO.getWalletType())) {
	                    tempVO.setWalletBalance(0L);
	                    tempVO.setWalletType(walletType);
	                    newProductList.add(tempVO);
	                    continue;
	                } else if (tempVO.getWalletType().equals(walletType)) {
	                    map.put(tempVO.getProductCode(), tempVO);
	                    newProductList.add(tempVO);
	                    continue;
	                }

	                else {
	                    NetworkStockTxnItemsVO tempVO1 = new NetworkStockTxnItemsVO();
	                    org.apache.commons.beanutils.BeanUtils.copyProperties(tempVO1, tempVO);
	                    tempVO1.setWalletType(null);
	                    tempVO1.setWalletBalance(0L);
	                    tempVO1.setStock(0L);
	                    map.put(tempVO1.getProductCode(), tempVO1);
	                }

	                // map.put(tempVO.getProductCode(), tempVO1);

	            }
	            
	            Iterator it = map.entrySet().iterator();
	            tempVO = null;
	            while (it.hasNext()) {
	                Map.Entry pair = (Map.Entry) it.next();
	                tempVO = (NetworkStockTxnItemsVO) pair.getValue();
	                if (BTSLUtil.isNullString(tempVO.getWalletType())) {
	                    tempVO.setWalletType(walletType);
	                    newProductList.add(tempVO);
	                }
	            }
	            
	            
	            response.setStockProductList(newProductList);
	        }
	        
	        response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
			response.setMessage(PretupsI.SUCCESS);
			response1.setStatus(HttpStatus.SC_OK);
			response.setStatus(HttpStatus.SC_OK);
        
        }
        catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.STOCK_PRODUCT_LIST_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.STOCK_PRODUCT_LIST_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;
	}

	
	
	
	
	
	@Override
	public ConfirmStockResponseVO confirmStock(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, Locale locale, UserVO userVO, ConfirmStockResponseVO response,
			ConfirmStockRequestVO confirmStockRequestVO) {
		final String METHOD_NAME = "confirmStock";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		double totalQuantity = 0D;
        double tempTotalMrp = 0D;
        NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
        String tempQty = null;
        double quantity = 0D;
        long mrp = 0L;
        long mrpAmount = 0L;
		
		try {
			
			
			ArrayList stockProductList = confirmStockRequestVO.getStockProductList();
            int stockProductLists=stockProductList.size();
            
            for (int i = 0, j = stockProductLists; i < j; i++) {
                networkStockTxnItemsVO = (NetworkStockTxnItemsVO) stockProductList.get(i);
                tempQty = networkStockTxnItemsVO.getRequestedQuantity();
                if (BTSLUtil.isNullString(tempQty)) {
                    networkStockTxnItemsVO.setAmount(0);
                    networkStockTxnItemsVO.setAmountStr(null);
                }
                mrp = networkStockTxnItemsVO.getUnitValue();
                if (!BTSLUtil.isNullString(tempQty)) {
                    quantity = new Double(tempQty.trim()).doubleValue();
                    mrpAmount = Double.valueOf( (quantity * mrp)).longValue();
                    networkStockTxnItemsVO.setAmount(mrpAmount);
                    networkStockTxnItemsVO.setAmountStr(PretupsBL.getDisplayAmount(mrpAmount));
                }
                tempTotalMrp += (quantity * mrp);
                totalQuantity = totalQuantity + quantity;
                quantity = 0.0;
            }
            
            response.setTotalMrp(BTSLUtil.parseDoubleToLong(tempTotalMrp));
            response.setTotalMrpStr(PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(tempTotalMrp)));
            response.setTotalQty(totalQuantity);
            long approveLimit = ((Long) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.NETWORK_STOCK_CIRCLE_MAXLIMIT, confirmStockRequestVO.getNetworkCode())).longValue();
            response.setMaxAmountLimit(approveLimit);
            
            
            if(BTSLUtil.parseDoubleToLong(tempTotalMrp) > approveLimit ) {
            	String[] s=new String[2];
            	s[0]=PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(tempTotalMrp));
            	s[1]=Long.toString(approveLimit);
            	throw new BTSLBaseException(classname, METHOD_NAME , PretupsErrorCodesI.NETWORK_STOCK_MAX_LIMIT, 0, s, null);
            }
            
            //setting up previous details
			response.setNetworkCode(userVO.getNetworkID());
	        response.setRequesterName(userVO.getUserName());
	        response.setUserID(userVO.getUserID());
	        response.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_CREATION);
	        response.setTxnType(PretupsI.CREDIT);
	        response.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_NEW);
	        response.setStockDateStr(BTSLUtil.getDateStringFromDate(new Date()));
	        response.setNetworkCodeFor(userVO.getNetworkID());
            response.setNetworkForName(userVO.getNetworkName());
            response.setNetworkName(userVO.getNetworkName());
            response.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
            response.setStockProductList(confirmStockRequestVO.getStockProductList());
            response.setWalletType(confirmStockRequestVO.getWalletType());
            response.setReferenceNumber(confirmStockRequestVO.getReferenceNumber());
            response.setRemarks(confirmStockRequestVO.getRemarks());
            
            
            response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
			response.setMessage(PretupsI.SUCCESS);
			response1.setStatus(HttpStatus.SC_OK);
			response.setStatus(HttpStatus.SC_OK);
            
		}
		catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.NETWORK_STOCK_MAX_LIMIT, be.getArgs());
			response.setMessageCode(be.getMessageKey());
			response.setMessage(resmsg);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));	
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		catch (Exception e) {
				log.error(METHOD_NAME, "Exception:e=" + e);
				log.errorTrace(METHOD_NAME, e);
				response.setStatus((HttpStatus.SC_BAD_REQUEST));		
				String resmsg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.CONFIRM_STOCK_FAIL, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.CONFIRM_STOCK_FAIL);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		
		
		return response;
	}






	@Override
	public AddStockFinalResponseVO addStockFinal(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con,MComConnectionI mcomCon, Locale locale, UserVO userVO, AddStockFinalResponseVO response, AddStockRequestVO addStockRequestVO) {
		final String METHOD_NAME = "addStockFinal";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		NetworkStockDAO networkStockDAO = null;
        String[] arg = new String[1];
        int addCount = 0;
        
        try {
        	Date currentdate = new Date();
        	
        	//setting all the details as of constructVOFromForm method in struts -> starts
        	NetworkStockTxnVO networkStockTxnVO = new NetworkStockTxnVO();
        	
        	 //networkStockTxnVO.setTxnNo(addStockRequestVO.getTxnNo());
             networkStockTxnVO.setStockType(addStockRequestVO.getStockType());
             networkStockTxnVO.setEntryType(addStockRequestVO.getEntryType());
             networkStockTxnVO.setTxnType(addStockRequestVO.getTxnType());
             networkStockTxnVO.setTxnStatus(addStockRequestVO.getTxnStatus());
             networkStockTxnVO.setTxnWallet(addStockRequestVO.getWalletType());
             if (PretupsI.STOCK_TXN_TYPE.equals(addStockRequestVO.getEntryType())) {
                 networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(-addStockRequestVO.getTotalQty()));
                 networkStockTxnVO.setApprovedQuantity(PretupsBL.getSystemAmount(-addStockRequestVO.getTotalQty()));
                 networkStockTxnVO.setTxnMrp(-addStockRequestVO.getTotalMrp());
             } else {
                 networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(addStockRequestVO.getTotalQty()));
                 networkStockTxnVO.setApprovedQuantity(PretupsBL.getSystemAmount(addStockRequestVO.getTotalQty()));
                 networkStockTxnVO.setTxnMrp(addStockRequestVO.getTotalMrp());
             }
             networkStockTxnVO.setNetworkCode(addStockRequestVO.getNetworkCode());
             networkStockTxnVO.setNetworkFor(addStockRequestVO.getNetworkCodeFor());
             if (!BTSLUtil.isNullString(addStockRequestVO.getRemarks())) {
                 networkStockTxnVO.setInitiaterRemarks(addStockRequestVO.getRemarks().trim());
             } else {
                 networkStockTxnVO.setInitiaterRemarks(addStockRequestVO.getRemarks());
             }
             if (!BTSLUtil.isNullString(addStockRequestVO.getReferenceNumber())) {
                 networkStockTxnVO.setReferenceNo(addStockRequestVO.getReferenceNumber().trim());
             } else {
                 networkStockTxnVO.setReferenceNo(addStockRequestVO.getReferenceNumber());
             }
             
             networkStockTxnVO.setLastModifiedTime(addStockRequestVO.getLastModifiedTime());
             networkStockTxnVO.setFirstApproverLimit(addStockRequestVO.getFirstLevelAppLimit());
        	//setting all the details as of constructVOFromForm method in struts -> ends
             
             //setting other details -> starts
             networkStockTxnVO.setFirstApproverLimit(((Long) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.NETWORK_STOCK_FIRSTAPPLIMIT, addStockRequestVO.getNetworkCode())).longValue());
             networkStockTxnVO.setTxnDate(currentdate);
             networkStockTxnVO.setCreatedBy(userVO.getUserID());
             networkStockTxnVO.setCreatedOn(currentdate);
             networkStockTxnVO.setModifiedOn(currentdate);
             networkStockTxnVO.setModifiedBy(userVO.getUserID());
             networkStockTxnVO.setInitiatedBy(userVO.getUserID());
             networkStockTxnVO.setUserID(userVO.getUserID());
             networkStockTxnVO.setTxnNo(NetworkStockBL.genrateStockTransctionID(networkStockTxnVO));
             //setting other details -> ends
             
             arg[0] = networkStockTxnVO.getTxnNo();
             
             ArrayList tempStockItemList = new ArrayList();
             NetworkStockTxnItemsVO networkStockTxnItemsVOold = null;
             NetworkStockTxnItemsVO networkStockTxnItemsVOnew = null;
             
             int seqNo = 1;
             // get the information of the selected productItems which is to
             // be associated with the order -> starts
             ArrayList stockProductList = addStockRequestVO.getStockProductList();
             if(stockProductList.isEmpty()) {
            	 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.STOCK_PRODUCT_LIST_FAIL, 0, null);
             }
             
             int stockProductLists=stockProductList.size();
             
             for (int i = 0, j = stockProductLists ; i < j; i++) {
                 networkStockTxnItemsVOold = (NetworkStockTxnItemsVO) addStockRequestVO.getStockProductList().get(i);
                 if (!BTSLUtil.isNullString(networkStockTxnItemsVOold.getRequestedQuantity())) {
                     networkStockTxnItemsVOnew = new NetworkStockTxnItemsVO();
                     networkStockTxnItemsVOnew.setSNo(seqNo++);
                     networkStockTxnItemsVOnew.setTxnNo(networkStockTxnVO.getTxnNo());
                     networkStockTxnItemsVOnew.setProductCode(networkStockTxnItemsVOold.getProductCode());
                     networkStockTxnItemsVOnew.setProductName(networkStockTxnItemsVOold.getProductName());

                     networkStockTxnItemsVOnew.setRequiredQuantity(PretupsBL.getSystemAmount(networkStockTxnItemsVOold.getRequestedQuantity()));
                     networkStockTxnItemsVOnew.setApprovedQuantity(networkStockTxnItemsVOnew.getRequiredQuantity());

                     networkStockTxnItemsVOnew.setAmount(networkStockTxnItemsVOold.getAmount());
                     // as disscussed with sanjay sir and AC mrp should be
                     // unitvalue * qty
                     networkStockTxnItemsVOnew.setMrp(networkStockTxnItemsVOold.getAmount());
                     //
                     networkStockTxnItemsVOnew.setWalletBalance(networkStockTxnItemsVOold.getWalletbalance());
                     // Added on 07/02/08
                     networkStockTxnItemsVOnew.setDateTime(currentdate);
                     networkStockTxnItemsVOnew.setTxnWallet(addStockRequestVO.getWalletType());
                     tempStockItemList.add(networkStockTxnItemsVOnew);     
                 }
             }
             // get the information of the selected productItems which is to
             // be associated with the order -> ends
             networkStockTxnVO.setNetworkStockTxnItemsList(tempStockItemList);
             networkStockDAO = new NetworkStockDAO();
             addCount = networkStockDAO.addNetworkStockTransaction(con, networkStockTxnVO);
             if (con != null) {
                 if (addCount > 0) {
                 	mcomCon.finalCommit();
                     //BTSLMessages btslMessage = new BTSLMessages("networkstock.initiatestock.msg.success", arg, "stocktxnpage");
                     
                 	if (PretupsI.NETWORK_STOCK_TXN_STATUS_NEW.equals(networkStockTxnVO.getTxnStatus())) {
                        this.prepareNetworkStockLogger(networkStockTxnVO);
                    }
                     
                 	 String[] s = new String[1];
                 	 s[0]= networkStockTxnVO.getTxnNo();
                     String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.STOCK_ADD_SUCCESS, s);
                     response.setTxnNo(s[0]);
         			 response.setMessage(resmsg); 
         			 response.setStatus(HttpStatus.SC_OK);
         			 response.setMessageCode(PretupsErrorCodesI.STOCK_ADD_SUCCESS);
         			 response1.setStatus(HttpStatus.SC_OK);
                 } else {
                 	mcomCon.finalRollback();
                     //throw new BTSLBaseException(this, "addStock", "networkstock.initiatestock.msg.unsuccess", "stocktxn");
                     throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.STOCK_ADD_FAIL, 0, null);
                 }
             }
        	
        }
        catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.STOCK_ADD_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.STOCK_ADD_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		return response;
	}
	
	
	/**
     * Method prepareNetworkStockLogger
     * this method is to make entry in the looger
     * 
     * @param p_networkStockTxnVO
     *            void
     * @throws BTSLBaseException
     */
    private void prepareNetworkStockLogger(NetworkStockTxnVO p_networkStockTxnVO) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
        	log.debug("prepareNetworkStockLogger", "Entered p_networkStockTxnVO=" + p_networkStockTxnVO);
        }
        ArrayList itemsList = p_networkStockTxnVO.getNetworkStockTxnItemsList();
        NetworkStockTxnItemsVO itemsVO = null;
        p_networkStockTxnVO.setReferenceNo(p_networkStockTxnVO.getTxnNo());
        for (int i = 0, j = itemsList.size(); i < j; i++) {
            itemsVO = (NetworkStockTxnItemsVO) itemsList.get(i);
            p_networkStockTxnVO.setProductCode(itemsVO.getProductCode());
            p_networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(itemsVO.getRequiredQuantity()));
            //p_networkStockTxnVO.setApprovedQuantity(itemsVO.getApprovedQuantity());
            p_networkStockTxnVO.setTxnCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
            p_networkStockTxnVO.setUserID(p_networkStockTxnVO.getModifiedBy());
            p_networkStockTxnVO.setTxnType(PretupsI.CREDIT);
            p_networkStockTxnVO.setOtherInfo("NETWORK STOCK INITIATE");
            p_networkStockTxnVO.setStockType(PretupsI.NETWORK_STOCK);
            p_networkStockTxnVO.setPreviousStock(itemsVO.getWalletbalance());
            p_networkStockTxnVO.setPostStock(itemsVO.getWalletbalance());
            NetworkStockLog.log(p_networkStockTxnVO);
        }
        if (log.isDebugEnabled()) {
        	log.debug("prepareNetworkStockLogger", "Exiting");
        }
    }
	
	
}
