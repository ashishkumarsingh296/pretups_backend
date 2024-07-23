package com.restapi.networkadmin.service;

import java.sql.Connection;
import java.util.ArrayList;
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
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.NetworkStockLog;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.requestVO.ApprovaLevelOneStockTxnRequestVO;
import com.restapi.networkadmin.requestVO.ConfirmStockLevelOneRequestVO;
import com.restapi.networkadmin.requestVO.RejectStockTxnRequestVO;
import com.restapi.networkadmin.responseVO.ApprovaStockTxnResponseVO;
import com.restapi.networkadmin.responseVO.ConfirmStockLevelOneResponseVO;
import com.restapi.networkadmin.responseVO.DisplayStockLevelOneResponseVO;
import com.restapi.networkadmin.responseVO.LevelOneApprovalListResponseVO;
import com.restapi.networkadmin.responseVO.RejectStockTxnResponseVO;
import com.restapi.networkadmin.serviceI.LevelOneApprovalServiceI;
import com.restapi.networkadminVO.DisplayStockVO;
import com.web.pretups.networkstock.businesslogic.NetworkStockWebDAO;


@Service("LevelOneApprovalServiceI")
public class LevelOneApprovalServiceImpl implements LevelOneApprovalServiceI{
	
	public static final Log log = LogFactory.getLog(LevelOneApprovalServiceImpl.class.getName());
	public static final String classname = "LevelOneApprovalServiceImpl";

	@Override
	public LevelOneApprovalListResponseVO levelOneApprovalList(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			LevelOneApprovalListResponseVO response) {
		
		final String METHOD_NAME = "levelOneApprovalList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		NetworkStockWebDAO networkStockwebDAO = null;
		
		try {
			String transactionStatus = "'" + PretupsI.NETWORK_STOCK_TXN_STATUS_NEW + "','" + PretupsI.NETWORK_STOCK_TXN_STATUS_APPROVE1 + "'";
            String networkType = null;
            networkType = PretupsI.ROAM_LOCATION_TYPE;
            
            response.setNetworkCode(userVO.getNetworkID());
            response.setUserID(userVO.getUserID());
           
            networkStockwebDAO = new NetworkStockWebDAO();
            
            ArrayList stockTxnList = networkStockwebDAO.loadStockTransactionList(con, transactionStatus, userVO.getNetworkID(), networkType);
            
            if(stockTxnList.isEmpty()) {
           	 	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.STOCK_TXN_LIST_FAIL, 0, null);
            }
            
            response.setStockTxnList(stockTxnList);
            
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
					PretupsErrorCodesI.APPROVAL_LIST_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.APPROVAL_LIST_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		return response;
	}

	
	
	
	
	
	@Override
	public DisplayStockLevelOneResponseVO displayStockLevelOne(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			DisplayStockLevelOneResponseVO response, DisplayStockVO displayStockVO, String txnNo) {
		
		final String METHOD_NAME = "displayStockLevelOne";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		NetworkStockWebDAO networkStockwebDAO = null;
		
		ArrayList stockOrderList = null;
        NetworkStockTxnVO networkStockTxnVO = null;
        ArrayList stockItemList = null;
        NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
        double totalAmt = 0D;
        double amount = 0D;
		try {
			String transactionStatus = "'" + PretupsI.NETWORK_STOCK_TXN_STATUS_NEW + "','" + PretupsI.NETWORK_STOCK_TXN_STATUS_APPROVE1 + "'";
            String networkType = null;
            networkType = PretupsI.ROAM_LOCATION_TYPE;
            
//            response.setNetworkCode(userVO.getNetworkID());
//            response.setUserID(userVO.getUserID());
           
            networkStockwebDAO = new NetworkStockWebDAO();
            stockOrderList = networkStockwebDAO.loadStockTransactionList(con, transactionStatus, userVO.getNetworkID(), networkType);
            
            for (int i = 0, j = stockOrderList.size(); i < j; i++) {
            	networkStockTxnVO = (NetworkStockTxnVO) stockOrderList.get(i);
            	if (networkStockTxnVO.getTxnNo().equals(txnNo)) {
            		break;
            	}else {
            		continue;
            	}
            }
            
            //set DisplayStockVO starts -> replicating constructFormFromVO function
            displayStockVO.setStockType(networkStockTxnVO.getStockType());
            displayStockVO.setEntryType(networkStockTxnVO.getEntryType());
            displayStockVO.setTxnType(networkStockTxnVO.getTxnType());
            displayStockVO.setTxnNo(networkStockTxnVO.getTxnNo());
            displayStockVO.setRequesterName(networkStockTxnVO.getInitiaterName());
            displayStockVO.setStockDateStr(BTSLUtil.getDateStringFromDate(networkStockTxnVO.getTxnDate()));
            displayStockVO.setReferenceNumber(networkStockTxnVO.getReferenceNo());
            displayStockVO.setTxnStatusDesc(networkStockTxnVO.getTxnStatusName());
            displayStockVO.setNetworkForName(networkStockTxnVO.getNetworkForName());
            displayStockVO.setRemarks(networkStockTxnVO.getInitiaterRemarks());
            displayStockVO.setNetworkCodeFor(networkStockTxnVO.getNetworkFor());
            displayStockVO.setLastModifiedTime(networkStockTxnVO.getLastModifiedTime());
            displayStockVO.setFirstLevelRemarks(networkStockTxnVO.getFirstApprovedRemarks());
            displayStockVO.setFirstLevelApprovedBy(networkStockTxnVO.getFirstApprovedBy());
            displayStockVO.setSecondLevelRemarks(networkStockTxnVO.getSecondApprovedRemarks());
            displayStockVO.setSecondLevelApprovedBy(networkStockTxnVO.getSecondApprovedBy());
            displayStockVO.setWalletType(networkStockTxnVO.getTxnWallet());
            //set DisplayStockVO ends
            
            stockItemList = networkStockwebDAO.loadStockItemList(con, networkStockTxnVO.getTxnNo(), userVO.getNetworkID(), networkStockTxnVO.getNetworkFor(), networkStockTxnVO.getTxnWallet());
            boolean stockFound = false;
            HashMap<String, NetworkStockTxnItemsVO> map = new HashMap<String, NetworkStockTxnItemsVO>();
            ArrayList newStockList = new ArrayList();
            if (stockItemList != null && !stockItemList.isEmpty()) {
                for (int i = 0, j = stockItemList.size(); i < j; i++) {

                    networkStockTxnItemsVO = (NetworkStockTxnItemsVO) stockItemList.get(i);

                    if (((NetworkStockTxnItemsVO) map.get(networkStockTxnItemsVO.getProductCode())) != null) {
                        if (((NetworkStockTxnItemsVO) map.get(networkStockTxnItemsVO.getProductCode())).getWalletType() != null) {
                            continue;
                        }
                    }

                    if (networkStockTxnItemsVO.getWalletType().equals(networkStockTxnVO.getTxnWallet())) {
                        map.put(networkStockTxnItemsVO.getProductCode(), networkStockTxnItemsVO);
                        newStockList.add(networkStockTxnItemsVO);
                        stockFound = true; // stock exists already
                        networkStockTxnItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(Long.parseLong(networkStockTxnItemsVO.getRequestedQuantity())));
                        amount = networkStockTxnItemsVO.getMrp() * Double.parseDouble(PretupsBL.getDisplayAmount(networkStockTxnItemsVO.getApprovedQuantity()));
                        networkStockTxnItemsVO.setApprovedQuantityStr(PretupsBL.getDisplayAmount(networkStockTxnItemsVO.getApprovedQuantity()));
                        networkStockTxnItemsVO.setAmountStr(PretupsBL.getDisplayAmount(Double.valueOf(amount).longValue()));
                        totalAmt += amount;
                    } else {
                        networkStockTxnItemsVO.setWalletType(null);
                        networkStockTxnItemsVO.setWalletBalance(0L);
                        networkStockTxnItemsVO.setStock(0L);
                        map.put(networkStockTxnItemsVO.getProductCode(), networkStockTxnItemsVO);
                    }

                }
            }
            Iterator it = map.entrySet().iterator();
            NetworkStockTxnItemsVO tempVO = null;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                tempVO = (NetworkStockTxnItemsVO) pair.getValue();
                if (BTSLUtil.isNullString(tempVO.getWalletType())) {
                    tempVO.setWalletType(networkStockTxnVO.getTxnWallet());
                    tempVO.setRequestedQuantity(PretupsBL.getDisplayAmount(Long.parseLong(tempVO.getRequestedQuantity())));
                    amount = tempVO.getMrp() * Double.parseDouble(PretupsBL.getDisplayAmount(tempVO.getApprovedQuantity()));
                    tempVO.setApprovedQuantityStr(PretupsBL.getDisplayAmount(tempVO.getApprovedQuantity()));
                    tempVO.setAmountStr(PretupsBL.getDisplayAmount(Double.valueOf(amount).longValue()));
                    totalAmt += amount;
                    newStockList.add(tempVO);
                }
            }
            
            
            response.setTotalMrpStr(PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(totalAmt)));
            response.setStockItemsList(newStockList);
            
            //setting extra response for next api use starts 
            response.setStockType(displayStockVO.getStockType());
            response.setEntryType(displayStockVO.getEntryType());
            response.setTxnType(displayStockVO.getTxnType());
            response.setTxnNo(displayStockVO.getTxnNo());
            response.setRequesterName(displayStockVO.getRequesterName());
            response.setStockDateStr(displayStockVO.getStockDateStr());
            response.setReferenceNumber(displayStockVO.getReferenceNumber());
            response.setTxnStatusDesc(displayStockVO.getTxnStatusDesc());
            response.setNetworkForName(displayStockVO.getNetworkForName());
            response.setRemarks(displayStockVO.getRemarks());
            response.setNetworkCodeFor(displayStockVO.getNetworkCodeFor());
            response.setLastModifiedTime(displayStockVO.getLastModifiedTime());
            response.setFirstLevelRemarks(displayStockVO.getFirstLevelRemarks());
            response.setFirstLevelApprovedBy(displayStockVO.getFirstLevelApprovedBy());
            response.setSecondLevelRemarks(displayStockVO.getSecondLevelRemarks());
            response.setSecondLevelApprovedBy(displayStockVO.getSecondLevelApprovedBy());
            response.setWalletType(displayStockVO.getWalletType());
          //setting extra response for next api use starts 
            
            
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
					PretupsErrorCodesI.DISPLAY_STOCK_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DISPLAY_STOCK_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		return response;
		
	}






	@Override
	public ConfirmStockLevelOneResponseVO confirmStockLevelOne(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			ConfirmStockLevelOneResponseVO response, ConfirmStockLevelOneRequestVO confirmStockLevelOneRequestVO) {
		
		final String METHOD_NAME = "confirmStockLevelOne";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		NetworkStockWebDAO networkStockwebDAO = null;
		 double totalQuantity = 0;
         long tempTotalMrp = 0L;
         NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
         double quantity = 0;
         long mrp = 0L;
         long mrpAmount = 0L;
         
		
		try {
			ArrayList stockItemsList = confirmStockLevelOneRequestVO.getStockItemsList();
            
			for (int i = 0, j = stockItemsList.size(); i < j; i++) {
                networkStockTxnItemsVO = (NetworkStockTxnItemsVO) stockItemsList.get(i);
                if (BTSLUtil.isNullString(networkStockTxnItemsVO.getApprovedQuantityStr())) {
                    quantity = 0;
                } else {
                    quantity = Double.parseDouble(networkStockTxnItemsVO.getApprovedQuantityStr());
                }

                networkStockTxnItemsVO.setApprovedQuantity(PretupsBL.getSystemAmount(quantity));
                mrp = networkStockTxnItemsVO.getUnitValue();
                mrpAmount = Double.valueOf( (quantity * mrp)).longValue();
                tempTotalMrp += mrpAmount;
                networkStockTxnItemsVO.setAmount(mrpAmount);
                // as disscussed with sanjay sir and AC mrp should be
                // unitvalue * qty
                networkStockTxnItemsVO.setMrp(mrpAmount);
                networkStockTxnItemsVO.setAmountStr(PretupsBL.getDisplayAmount(mrpAmount));
                totalQuantity += quantity;
            }
			
			response.setTotalMrp(tempTotalMrp);
            response.setTotalMrpStr(PretupsBL.getDisplayAmount(tempTotalMrp));
            response.setTotalQty(totalQuantity);
            long firstApproveLimit = ((Long) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.NETWORK_STOCK_FIRSTAPPLIMIT, userVO.getNetworkID())).longValue();
            response.setFirstLevelAppLimit(firstApproveLimit);

            // Code for textArea Validation start, added by Ashish S dated
            // as 12-07-2007
            if (!BTSLUtil.isNullString(confirmStockLevelOneRequestVO.getFirstLevelRemarks()) && confirmStockLevelOneRequestVO.getFirstLevelRemarks().length() > 100) {
                //BTSLMessages btslMessage = BTSLUtil.validateTextArea(this.getResources(request).getMessage(BTSLUtil.getBTSLLocale(request), "networkstock.level1approval.label.firstapprovalremarks"), networkStockForm.getFirstLevelRemarks(), 100, "pretups.jsp.messaage.textareacharsaremorethanmax", "level1app");
                //return forward = super.handleMessage(btslMessage, request, mapping);
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.STOCK_ADD_FAIL, 0, null);
            }
			
            
            //setting the response coming from previous api starts
            response.setStockType(confirmStockLevelOneRequestVO.getStockType());
            response.setEntryType(confirmStockLevelOneRequestVO.getEntryType());
            response.setTxnType(confirmStockLevelOneRequestVO.getTxnType());
            response.setTxnNo(confirmStockLevelOneRequestVO.getTxnNo());
            response.setRequesterName(confirmStockLevelOneRequestVO.getRequesterName());
            response.setStockDateStr(confirmStockLevelOneRequestVO.getStockDateStr());
            response.setReferenceNumber(confirmStockLevelOneRequestVO.getReferenceNumber());
            response.setTxnStatusDesc(confirmStockLevelOneRequestVO.getTxnStatusDesc());
            response.setNetworkForName(confirmStockLevelOneRequestVO.getNetworkForName());
            response.setRemarks(confirmStockLevelOneRequestVO.getRemarks());
            response.setNetworkCodeFor(confirmStockLevelOneRequestVO.getNetworkCodeFor());
            response.setLastModifiedTime(confirmStockLevelOneRequestVO.getLastModifiedTime());
            response.setFirstLevelRemarks(confirmStockLevelOneRequestVO.getFirstLevelRemarks());
            response.setFirstLevelApprovedBy(confirmStockLevelOneRequestVO.getFirstLevelApprovedBy());
            response.setSecondLevelRemarks(confirmStockLevelOneRequestVO.getSecondLevelRemarks());
            response.setSecondLevelApprovedBy(confirmStockLevelOneRequestVO.getSecondLevelApprovedBy());
            response.setWalletType(confirmStockLevelOneRequestVO.getWalletType());
            //setting the response coming from previous api ends
            
            
            response.setStockItemsList(stockItemsList);
			
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
					PretupsErrorCodesI.CONFIRM_STOCK_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.CONFIRM_STOCK_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		
		return response;	
		
	}






	@Override
	public ApprovaStockTxnResponseVO approvaLevelOneStockTxn(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ApprovaStockTxnResponseVO response,
			ApprovaLevelOneStockTxnRequestVO approvaLevelOneStockTxnRequestVO) {
		final String METHOD_NAME = "approvaLevelOneStockTxn";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		NetworkStockDAO networkStockDAO = null;
        NetworkStockWebDAO networkStockwebDAO = null;
        String[] arg = new String[1];
        int updateCount = 1;
		try {
			networkStockDAO = new NetworkStockDAO();
            networkStockwebDAO = new NetworkStockWebDAO();
            Date currentdate = new Date();
            String userID = userVO.getUserID();
            
            NetworkStockTxnVO networkStockTxnVO = new NetworkStockTxnVO();
            
            //implementing constructVOFromForm function from struts -> starts
            networkStockTxnVO.setTxnNo(approvaLevelOneStockTxnRequestVO.getTxnNo());
            networkStockTxnVO.setStockType(approvaLevelOneStockTxnRequestVO.getStockType());
            networkStockTxnVO.setEntryType(approvaLevelOneStockTxnRequestVO.getEntryType());
            networkStockTxnVO.setTxnType(approvaLevelOneStockTxnRequestVO.getTxnType());
            networkStockTxnVO.setTxnStatus(null);
            networkStockTxnVO.setTxnWallet(approvaLevelOneStockTxnRequestVO.getWalletType());
            if (PretupsI.STOCK_TXN_TYPE.equals(approvaLevelOneStockTxnRequestVO.getEntryType())) {
                networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(-approvaLevelOneStockTxnRequestVO.getTotalQty()));
                networkStockTxnVO.setApprovedQuantity(PretupsBL.getSystemAmount(-approvaLevelOneStockTxnRequestVO.getTotalQty()));
                networkStockTxnVO.setTxnMrp(-approvaLevelOneStockTxnRequestVO.getTotalMrp());
            } else {
                networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(approvaLevelOneStockTxnRequestVO.getTotalQty()));
                networkStockTxnVO.setApprovedQuantity(PretupsBL.getSystemAmount(approvaLevelOneStockTxnRequestVO.getTotalQty()));
                networkStockTxnVO.setTxnMrp(approvaLevelOneStockTxnRequestVO.getTotalMrp());
            }
            networkStockTxnVO.setNetworkCode(userVO.getNetworkID());
            networkStockTxnVO.setNetworkFor(approvaLevelOneStockTxnRequestVO.getNetworkCodeFor());

            if (!BTSLUtil.isNullString(approvaLevelOneStockTxnRequestVO.getRemarks())) {
                networkStockTxnVO.setInitiaterRemarks(approvaLevelOneStockTxnRequestVO.getRemarks().trim());
            } else {
                networkStockTxnVO.setInitiaterRemarks(approvaLevelOneStockTxnRequestVO.getRemarks());
            }

            if (!BTSLUtil.isNullString(approvaLevelOneStockTxnRequestVO.getReferenceNumber())) {
                networkStockTxnVO.setReferenceNo(approvaLevelOneStockTxnRequestVO.getReferenceNumber().trim());
            } else {
                networkStockTxnVO.setReferenceNo(approvaLevelOneStockTxnRequestVO.getReferenceNumber());
            }

            if (!BTSLUtil.isNullString(approvaLevelOneStockTxnRequestVO.getFirstLevelRemarks())) {
                networkStockTxnVO.setFirstApprovedRemarks(approvaLevelOneStockTxnRequestVO.getFirstLevelRemarks().trim());
            } else {
                networkStockTxnVO.setFirstApprovedRemarks(approvaLevelOneStockTxnRequestVO.getFirstLevelRemarks());
            }

            if (!BTSLUtil.isNullString(approvaLevelOneStockTxnRequestVO.getSecondLevelRemarks())) {
                networkStockTxnVO.setSecondApprovedRemarks(approvaLevelOneStockTxnRequestVO.getSecondLevelRemarks().trim());
            } else {
                networkStockTxnVO.setSecondApprovedRemarks(approvaLevelOneStockTxnRequestVO.getSecondLevelRemarks());
            }

            networkStockTxnVO.setLastModifiedTime(approvaLevelOneStockTxnRequestVO.getLastModifiedTime());
            networkStockTxnVO.setFirstApproverLimit(approvaLevelOneStockTxnRequestVO.getFirstLevelAppLimit());
            //implementing constructVOFromForm function from struts -> ends
            
            
            networkStockTxnVO.setFirstApprovedBy(userID);
            networkStockTxnVO.setFirstApprovedOn(currentdate);
            networkStockTxnVO.setModifiedOn(currentdate);
            networkStockTxnVO.setModifiedBy(userID);
            networkStockTxnVO.setCreatedOn(currentdate);
            networkStockTxnVO.setCreatedBy(userID);
            
            arg[0] = approvaLevelOneStockTxnRequestVO.getTxnNo();
            ArrayList networkStockList = new ArrayList();
            networkStockTxnVO.setNetworkStockTxnItemsList(approvaLevelOneStockTxnRequestVO.getStockItemsList());
            
            // if first level limit is greater or equal to total mrp then
            // close the order
            if (approvaLevelOneStockTxnRequestVO.getFirstLevelAppLimit() >= approvaLevelOneStockTxnRequestVO.getTotalMrp()) {
                networkStockTxnVO.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_CLOSE);
                updateCount = 0;
                // update daily stock
                updateCount = networkStockDAO.updateNetworkDailyStock(con, constructStockVOFromTxnVO(networkStockTxnVO));

                // update location stock
                if (updateCount > 0) {
                    updateCount = networkStockwebDAO.updateNetworkStock(con, networkStockTxnVO, networkStockList);
                }
                // getting the previous stock for each product
                if (networkStockList != null && !networkStockList.isEmpty()) {
                    NetworkStockVO networkStockVO = null;
                    NetworkStockTxnItemsVO itemsVO = null;
                    for (int i = 0, j = networkStockList.size(); i < j; i++) {
                        networkStockVO = (NetworkStockVO) networkStockList.get(i);
                        for (int m = 0, n = networkStockTxnVO.getNetworkStockTxnItemsList().size(); m < n; m++) {
                            itemsVO = (NetworkStockTxnItemsVO) networkStockTxnVO.getNetworkStockTxnItemsList().get(m);
                            if (networkStockVO.getProductCode().equals(itemsVO.getProductCode())) {
                                itemsVO.setWalletBalance(networkStockVO.getPreviousBalance());
                                itemsVO.setMrp(itemsVO.getAmount());
                            }
                        }
                    }
                }
                // ends here
            } else {
                networkStockTxnVO.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_APPROVE1);
            }
            // update stock transaction
            
            if (updateCount > 0) {
                updateCount = networkStockwebDAO.updateLevel1NetworkStockTransaction(con, networkStockTxnVO);
            }
            
            if (con != null) {
                if (updateCount > 0) {
                	mcomCon.finalCommit();
                    if (PretupsI.NETWORK_STOCK_TXN_STATUS_CLOSE.equals(networkStockTxnVO.getTxnStatus())) {
                        this.prepareNetworkStockLogger(networkStockTxnVO);
                    }
                    //BTSLMessages btslMessage = new BTSLMessages("networkstock.level1approval.msg.success", arg, "selectstockorderpage");
                    //forward = super.handleMessage(btslMessage, request, mapping);
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.APPROVAL_SUCCESS_MESSAGE, null);
                    response.setTxnNo(approvaLevelOneStockTxnRequestVO.getTxnNo());
                    
                    response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
        			response.setMessage(resmsg);
        			response1.setStatus(HttpStatus.SC_OK);
        			response.setStatus(HttpStatus.SC_OK);
        			
                } else {
                	mcomCon.finalRollback();
                    //throw new BTSLBaseException(this, "approveLevel1StockTxn", "networkstock.level1approval.msg.unsuccess", "level1app");
                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.APPROVAL_FAIL_MESSAGE, 0, null);
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
					PretupsErrorCodesI.APPROVAL_FAIL_MESSAGE, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.APPROVAL_FAIL_MESSAGE);
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
            p_networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(itemsVO.getRequestedQuantity()));
            p_networkStockTxnVO.setApprovedQuantity(itemsVO.getApprovedQuantity());
            p_networkStockTxnVO.setTxnCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
            p_networkStockTxnVO.setUserID(p_networkStockTxnVO.getModifiedBy());
            p_networkStockTxnVO.setTxnType(PretupsI.CREDIT);
            p_networkStockTxnVO.setOtherInfo("NETWORK STOCK APPROVAL");
            p_networkStockTxnVO.setStockType(PretupsI.NETWORK_STOCK);
            p_networkStockTxnVO.setPreviousStock(itemsVO.getWalletbalance());
            p_networkStockTxnVO.setPostStock(itemsVO.getWalletbalance() + itemsVO.getApprovedQuantity());
            p_networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_CREATION);
            NetworkStockLog.log(p_networkStockTxnVO);
        }
        if (log.isDebugEnabled()) {
        	log.debug("prepareNetworkStockLogger", "Exiting");
        }
    }
	
	
    /**
     * Method constructStockVOFromTxnVO.
     * 
     * @param p_networkStockTxnVO
     *            NetworkStockTxnVO
     * @return NetworkStockVO
     * @throws Exception
     */
    private NetworkStockVO constructStockVOFromTxnVO(NetworkStockTxnVO p_networkStockTxnVO) throws Exception {
        if (log.isDebugEnabled()) {
        	log.debug("constructStockVOFromTxnVO", "Entered:NetworkStockTxnVO=>" + p_networkStockTxnVO);
        }
        NetworkStockVO networkStockVO = new NetworkStockVO();
        networkStockVO.setLastTxnType(p_networkStockTxnVO.getEntryType());
        networkStockVO.setLastTxnNum(p_networkStockTxnVO.getTxnNo());
        networkStockVO.setNetworkCode(p_networkStockTxnVO.getNetworkCode());
        networkStockVO.setNetworkCodeFor(p_networkStockTxnVO.getNetworkFor());
        networkStockVO.setModifiedOn(p_networkStockTxnVO.getModifiedOn());
        networkStockVO.setCreatedOn(p_networkStockTxnVO.getCreatedOn());
        networkStockVO.setWalletType(p_networkStockTxnVO.getTxnWallet());
        if (log.isDebugEnabled()) {
        	log.debug("constructStockVOFromTxnVO", "Exiting networkStockVO=" + networkStockVO);
        }
        return networkStockVO;
    }






	@Override
	public RejectStockTxnResponseVO rejectStockTxn(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, RejectStockTxnResponseVO response,
			RejectStockTxnRequestVO rejectStockTxnRequestVO) {
		final String METHOD_NAME = "rejectStockTxn";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		 NetworkStockWebDAO networkStockwebDAO = null;
	     String[] arg = new String[1];
	     int updateCount = 0;
	     
	     try {
	    	 networkStockwebDAO = new NetworkStockWebDAO();
	         Date currentdate = new Date();
	         String userID = userVO.getUserID();
	         
	         NetworkStockTxnVO networkStockTxnVO = new NetworkStockTxnVO();
	         
	         //constructing networkStockTxnVO as it is from constructVOFromForm from struts starts
	         networkStockTxnVO.setTxnNo(rejectStockTxnRequestVO.getTxnNo());
	         networkStockTxnVO.setStockType(rejectStockTxnRequestVO.getStockType());
	         networkStockTxnVO.setEntryType(rejectStockTxnRequestVO.getEntryType());
	         networkStockTxnVO.setTxnType(rejectStockTxnRequestVO.getTxnType());
	         networkStockTxnVO.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_CANCEL);
	         networkStockTxnVO.setTxnWallet(rejectStockTxnRequestVO.getWalletType());
	         if (PretupsI.STOCK_TXN_TYPE.equals(rejectStockTxnRequestVO.getEntryType())) {
	             networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(-rejectStockTxnRequestVO.getTotalQty()));
	             networkStockTxnVO.setApprovedQuantity(PretupsBL.getSystemAmount(-rejectStockTxnRequestVO.getTotalQty()));
	             networkStockTxnVO.setTxnMrp(-rejectStockTxnRequestVO.getTotalMrp());
	         } else {
	             networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(rejectStockTxnRequestVO.getTotalQty()));
	             networkStockTxnVO.setApprovedQuantity(PretupsBL.getSystemAmount(rejectStockTxnRequestVO.getTotalQty()));
	             networkStockTxnVO.setTxnMrp(rejectStockTxnRequestVO.getTotalMrp());
	         }
	         networkStockTxnVO.setNetworkCode(userVO.getNetworkID());
	         networkStockTxnVO.setNetworkFor(rejectStockTxnRequestVO.getNetworkCodeFor());

	         if (!BTSLUtil.isNullString(rejectStockTxnRequestVO.getRemarks())) {
	             networkStockTxnVO.setInitiaterRemarks(rejectStockTxnRequestVO.getRemarks().trim());
	         } else {
	             networkStockTxnVO.setInitiaterRemarks(rejectStockTxnRequestVO.getRemarks());
	         }

	         if (!BTSLUtil.isNullString(rejectStockTxnRequestVO.getReferenceNumber())) {
	             networkStockTxnVO.setReferenceNo(rejectStockTxnRequestVO.getReferenceNumber().trim());
	         } else {
	             networkStockTxnVO.setReferenceNo(rejectStockTxnRequestVO.getReferenceNumber());
	         }

	         if (!BTSLUtil.isNullString(rejectStockTxnRequestVO.getFirstLevelRemarks())) {
	             networkStockTxnVO.setFirstApprovedRemarks(rejectStockTxnRequestVO.getFirstLevelRemarks().trim());
	         } else {
	             networkStockTxnVO.setFirstApprovedRemarks(rejectStockTxnRequestVO.getFirstLevelRemarks());
	         }

	         if (!BTSLUtil.isNullString(rejectStockTxnRequestVO.getSecondLevelRemarks())) {
	             networkStockTxnVO.setSecondApprovedRemarks(rejectStockTxnRequestVO.getSecondLevelRemarks().trim());
	         } else {
	             networkStockTxnVO.setSecondApprovedRemarks(rejectStockTxnRequestVO.getSecondLevelRemarks());
	         }

	         networkStockTxnVO.setLastModifiedTime(rejectStockTxnRequestVO.getLastModifiedTime());
	         networkStockTxnVO.setFirstApproverLimit(rejectStockTxnRequestVO.getFirstLevelAppLimit());
	         //constructing networkStockTxnVO as it is from constructVOFromForm from struts ends
	         
	         
	         networkStockTxnVO.setCancelledBy(userID);
	         networkStockTxnVO.setCancelledOn(currentdate);
	         networkStockTxnVO.setModifiedOn(currentdate);
	         networkStockTxnVO.setModifiedBy(userID);
	         arg[0] = rejectStockTxnRequestVO.getTxnNo();
	         
	         String status = "'" + PretupsI.NETWORK_STOCK_TXN_STATUS_NEW + "','" + PretupsI.NETWORK_STOCK_TXN_STATUS_APPROVE1 + "'";
	         updateCount = networkStockwebDAO.cancelStockTransaction(con, networkStockTxnVO, status);
	         if (con != null) {
	                if (updateCount > 0) {
	                	mcomCon.finalCommit();
	                    //BTSLMessages btslMessage = new BTSLMessages("networkstock.level1approval.success.cancel", arg, "selectstockorderpage");
	                	 String[] s = new String[1];
	                 	 s[0]= networkStockTxnVO.getTxnNo();
	                     String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.STOCK_REJECT_SUCCESS, s);
	         			 response.setMessage(resmsg); 
	                	
	         			
	                    response.setTxnNo(rejectStockTxnRequestVO.getTxnNo());
	                    response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
	        			//response.setMessage(PretupsI.SUCCESS);
	        			response1.setStatus(HttpStatus.SC_OK);
	        			response.setStatus(HttpStatus.SC_OK);
	                   
	                } else {
	                	mcomCon.finalRollback();
	                    //throw new BTSLBaseException(this, "rejectStockTxn", "networkstock.level1approval.error.cancel", "appprove");
	                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.STOCK_REJECT_FAIL, 0, null);
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
						PretupsErrorCodesI.STOCK_REJECT_FAIL, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.STOCK_REJECT_FAIL);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			
			
			return response;	
	}
    
    
    
    
    
    
    
	
	
	

}
