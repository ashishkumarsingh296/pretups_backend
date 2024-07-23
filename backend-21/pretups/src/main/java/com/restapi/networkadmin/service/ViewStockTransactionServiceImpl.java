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
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;
import com.restapi.networkadmin.requestVO.DisplayStockTxnDetailsViewRequestVO;
import com.restapi.networkadmin.requestVO.LoadStockTxnListViewRequestVO;
import com.restapi.networkadmin.responseVO.DisplayStockTxnDetailsViewResponseVO;
import com.restapi.networkadmin.responseVO.LoadStockTxnListViewResponseVO;
import com.restapi.networkadmin.responseVO.ViewStockTxnDropdownsResponseVO;
import com.restapi.networkadmin.serviceI.ViewStockTransactionServiceI;
import com.restapi.networkadminVO.DisplayStockVO;
import com.web.pretups.network.businesslogic.NetworkWebDAO;
import com.web.pretups.networkstock.businesslogic.NetworkStockWebDAO;


@Service("ViewStockTransactionServiceI")
public class ViewStockTransactionServiceImpl implements ViewStockTransactionServiceI{
	public static final Log log = LogFactory.getLog(ViewStockTransactionServiceImpl.class.getName());
	public static final String classname = "ViewStockTransactionServiceImpl";
	
	
	@Override
	public ViewStockTxnDropdownsResponseVO viewStockTxnDropdowns(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			ViewStockTxnDropdownsResponseVO response) {
		final String METHOD_NAME = "viewStockTxnDropdowns";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		NetworkWebDAO networkWebDAO = null;
        
        try {
        	networkWebDAO = new NetworkWebDAO();
        	
        	String homeStock = "";
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
                homeStock = TypesI.YES;
            }
        	
            response.setNetworkCode(userVO.getNetworkID());
            response.setUserID(userVO.getUserID());
        	
            if (!(TypesI.YES.equals(homeStock))) {
                ListValueVO listValueVO = new ListValueVO(userVO.getNetworkName(), userVO.getNetworkID());
                response.setRoamNetworkList(networkWebDAO.loadRoamNetworkList(con, " ", PretupsI.ROAM_LOCATION_TYPE));
                response.getRoamNetworkList().add(0, listValueVO);
            }
            response.setStatusList(LookupsCache.loadLookupDropDown(PretupsI.NETWORK_STOCK_STATUS, true));
            response.setStockTypeList(LookupsCache.loadLookupDropDown(PretupsI.NETWORK_STOCK_TYPE, true));
            Date currentDate = new Date();
            Calendar cal = BTSLDateUtil.getInstance();
            cal.setTime(currentDate);

            response.setToDateStr(BTSLUtil.getDateStringFromDate(currentDate));

            int maxDays = Integer.parseInt(Constants.getProperty("MAX_DAYLIMIT_DATERANGE"));
            response.setFromDateStr(BTSLUtil.getDateStringFromDate(BTSLUtil.addDaysInUtilDate(currentDate, -maxDays)));
            
        	
	        
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
	public LoadStockTxnListViewResponseVO loadStockTxnListView(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			LoadStockTxnListViewResponseVO response, LoadStockTxnListViewRequestVO loadStockTxnListViewRequestVO) {
		
		final String METHOD_NAME = "loadStockTxnListView";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		 NetworkStockWebDAO networkStockwebDAO = null;
	     String txnNo;
	     Date fromDate, toDate;
	     String networkCodeFor;
	     ArrayList stockList = null;
        
        try {
        	txnNo = loadStockTxnListViewRequestVO.getTmpTxnNo();
            if (txnNo == null || txnNo.trim().length() == 0) {
                txnNo = PretupsI.ALL;
            }
            networkCodeFor = loadStockTxnListViewRequestVO.getNetworkCodeFor();
            if (BTSLUtil.isNullString(networkCodeFor)) {
                networkCodeFor = loadStockTxnListViewRequestVO.getNetworkCode();
            }
            fromDate = BTSLUtil.getDateFromDateString(loadStockTxnListViewRequestVO.getFromDateStr());
            toDate = BTSLUtil.getDateFromDateString(loadStockTxnListViewRequestVO.getToDateStr());
            String networkType = null;
            networkType = PretupsI.ROAM_LOCATION_TYPE;
            
            response.setNetworkCode(userVO.getNetworkID());
            response.setUserID(userVO.getUserID());
            networkStockwebDAO = new NetworkStockWebDAO();
            stockList = networkStockwebDAO.loadViewStockList(con, txnNo, loadStockTxnListViewRequestVO.getTxnStatus(), 
            		loadStockTxnListViewRequestVO.getNetworkCode(), fromDate, toDate, networkCodeFor,
            		loadStockTxnListViewRequestVO.getEntryType(), networkType);
            
            // evaluate the list to set the approved by and approved on fields
            // for the display purpose

            NetworkStockTxnVO networkStockTxnVO = null;
            for (int i = 0, j = stockList.size(); i < j; i++) {
                networkStockTxnVO = (NetworkStockTxnVO) stockList.get(i);
                if (PretupsI.NETWORK_STOCK_TXN_STATUS_CANCEL.equals(networkStockTxnVO.getTxnStatus())) {
                    networkStockTxnVO.setApprovedOnStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(networkStockTxnVO.getCancelledOn())));
                    networkStockTxnVO.setSecondApprovedBy(networkStockTxnVO.getCancelledBy());
                } else {
                    if (networkStockTxnVO.getSecondApprovedOn() != null) {
                        networkStockTxnVO.setApprovedOnStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(networkStockTxnVO.getSecondApprovedOn())));
                    } else {
                        //networkStockTxnVO.setSecondApprovedBy(networkStockTxnVO.getFirstApprovedBy());
                        networkStockTxnVO.setSecondApprovedOn(networkStockTxnVO.getFirstApprovedOn());
                        if (networkStockTxnVO.getFirstApprovedOn() != null) {
                            networkStockTxnVO.setApprovedOnStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(networkStockTxnVO.getFirstApprovedOn())));
                        }
                    }
                }
            }
            response.setStockTxnList(stockList);
	        
            
            
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
	public DisplayStockTxnDetailsViewResponseVO displayStockTxnDetailsView(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, Locale locale, UserVO userVO,
			DisplayStockTxnDetailsViewResponseVO response,
			DisplayStockTxnDetailsViewRequestVO displayStockTxnDetailsViewRequestVO) {
		final String METHOD_NAME = "loadStockTxnListView";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		 NetworkStockWebDAO networkStockwebDAO = null;
	     String txnNo;
	     Date fromDate, toDate;
	     String networkCodeFor;
	     ArrayList stockList = null;
        
	     //display code variable starts
	     ArrayList stockOrderList = null;
	     NetworkStockTxnVO networkStockTxnVO = null;
	     ArrayList stockItemList = null;
	     NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
	     double totalAmt = 0D;
	     double amount = 0D;
	     
	     DisplayStockVO displayStockVO = new DisplayStockVO();
	     //display code variable ends
	     
        try {
        	txnNo = displayStockTxnDetailsViewRequestVO.getTmpTxnNo();
            if (txnNo == null || txnNo.trim().length() == 0) {
                txnNo = PretupsI.ALL;
            }
            networkCodeFor = displayStockTxnDetailsViewRequestVO.getNetworkCodeFor();
            if (BTSLUtil.isNullString(networkCodeFor)) {
                networkCodeFor = displayStockTxnDetailsViewRequestVO.getNetworkCode();
            }
            fromDate = BTSLUtil.getDateFromDateString(displayStockTxnDetailsViewRequestVO.getFromDateStr());
            toDate = BTSLUtil.getDateFromDateString(displayStockTxnDetailsViewRequestVO.getToDateStr());
            String networkType = null;
            networkType = PretupsI.ROAM_LOCATION_TYPE;
            
//            response.setNetworkCode(userVO.getNetworkID());
//            response.setUserID(userVO.getUserID());
            networkStockwebDAO = new NetworkStockWebDAO();
            stockList = networkStockwebDAO.loadViewStockList(con, txnNo, displayStockTxnDetailsViewRequestVO.getTxnStatus(), 
            		displayStockTxnDetailsViewRequestVO.getNetworkCode(), fromDate, toDate, networkCodeFor,
            		displayStockTxnDetailsViewRequestVO.getEntryType(), networkType);
            
            // evaluate the list to set the approved by and approved on fields
            // for the display purpose

           // NetworkStockTxnVO networkStockTxnVO = null;
            for (int i = 0, j = stockList.size(); i < j; i++) {
                networkStockTxnVO = (NetworkStockTxnVO) stockList.get(i);
                if (PretupsI.NETWORK_STOCK_TXN_STATUS_CANCEL.equals(networkStockTxnVO.getTxnStatus())) {
                    networkStockTxnVO.setApprovedOnStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(networkStockTxnVO.getCancelledOn())));
                    networkStockTxnVO.setSecondApprovedBy(networkStockTxnVO.getCancelledBy());
                } else {
                    if (networkStockTxnVO.getSecondApprovedOn() != null) {
                        networkStockTxnVO.setApprovedOnStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(networkStockTxnVO.getSecondApprovedOn())));
                    } else {
                        //networkStockTxnVO.setSecondApprovedBy(networkStockTxnVO.getFirstApprovedBy());
                        networkStockTxnVO.setSecondApprovedOn(networkStockTxnVO.getFirstApprovedOn());
                        if (networkStockTxnVO.getFirstApprovedOn() != null) {
                            networkStockTxnVO.setApprovedOnStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(networkStockTxnVO.getFirstApprovedOn())));
                        }
                    }
                }
            }
            
            
            //response.setStockTxnList(stockList);
            
            //display code starts ******
            stockOrderList = stockList;
            
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
            //setting extra response for next api use ends
            
            
            //display code ends *****
            
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
	
	
	
	

}
