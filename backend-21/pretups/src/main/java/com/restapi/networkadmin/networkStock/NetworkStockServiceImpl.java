package com.restapi.networkadmin.networkStock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.NetworkStockLog;
import com.btsl.pretups.networkstock.businesslogic.*;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.network.businesslogic.NetworkWebDAO;
import com.web.pretups.networkstock.businesslogic.NetworkStockWebDAO;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import com.btsl.pretups.util.PretupsBL;

import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.*;

@Service("NetworkStockServiceI")
public class NetworkStockServiceImpl implements NetworkStockServiceI {

    public static final Log log = LogFactory.getLog(NetworkStockServiceImpl.class.getName());
    public static final String classname = "NetworkStockServiceImpl";

    @Override
    public ViewCurrentStockResponseVO getList(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, String loginID) throws Exception, BTSLBaseException {
        final String METHOD_NAME = "getList";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        ViewCurrentStockResponseVO response = new ViewCurrentStockResponseVO();
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        ArrayList stockList = new ArrayList();
        UserDAO userDAO = new UserDAO();
        UserVO userVO = new UserVO();
        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            String networkType = null;
            networkType = PretupsI.ROAM_LOCATION_TYPE;
            String networkCode = userVO.getNetworkID();
            String homeStock = "";
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
                homeStock = TypesI.YES;
            }
            String networkCodeFor;
            if (TypesI.YES.equals(homeStock)) {
                networkCodeFor = networkCode;
            } else {
                networkCodeFor = PretupsI.ALL;
            }

            NetworkStockDAO networkStockDAO = new NetworkStockDAO();
            stockList = networkStockDAO.loadCurrentStockList(con, networkCode, networkCodeFor, networkType);

            if (stockList != null) {
                response.setStockList(stockList);
            } else {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }

            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);

        } finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    @Override
    public BaseResponse confirmStockAuthorise(Connection con, MComConnectionI mcomCon, String categoryType, String loginID, HttpServletResponse response1, MultiValueMap<String, String> headers, NetworkInitiateStockDeductionRequestVO requestVO) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "confirmStockDeduction";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        NetworkStockWebDAO networkStockwebDAO = null;
        NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
        ProductStockTxnVO productStockTxnVO = null;
        networkStockwebDAO = new NetworkStockWebDAO();
        BaseResponse response = new BaseResponse();
        UserDAO userDAO = new UserDAO();
        UserVO userVO = new UserVO();
        try {
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            double totalQuantity = 0D;
            double tempTotalMrp = 0D;
            String tempQty = null;
            double quantity = 0D;
            long mrp = 0L;
            long mrpAmount = 0L;
            String transactionStatus = "'" + PretupsI.NETWORK_STOCK_TXN_STATUS_NEW + "'";
            String networkType = null;
            networkType = PretupsI.ROAM_LOCATION_TYPE;
            requestVO.setNetworkCode(userVO.getNetworkID());
            requestVO.setUserId(userVO.getUserID());
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            networkStockwebDAO = new NetworkStockWebDAO();
            ArrayList stockDeductTxnList = networkStockwebDAO.loadStockDeductionList(con, transactionStatus, userVO.getNetworkID(), userVO.getNetworkID());
            if (stockDeductTxnList != null && !stockDeductTxnList.isEmpty()) {
                response.setStatus((HttpStatus.SC_CONFLICT));
                response.setMessage(PretupsI.NETWORK_STOCK_ALREADY_EXISTS);
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INITAITE_FAIL, 0, null);
            }
            ArrayList stockProductList = requestVO.getProductList();
            int stockProductLists = stockProductList.size();
            for (int i = 0, j = stockProductLists; i < j; i++) {
                productStockTxnVO = (ProductStockTxnVO) stockProductList.get(i);
                tempQty = productStockTxnVO.getRequestedQuantity();
                if (BTSLUtil.isNullString(tempQty)) {
                    productStockTxnVO.setAmount(0);
                    productStockTxnVO.setAmountStr(null);
                    continue;
                }

                quantity = new Double(tempQty.trim()).doubleValue();
                mrp = productStockTxnVO.getUnitValue();

                mrpAmount = Double.valueOf((quantity * mrp)).longValue();
                tempTotalMrp += (quantity * mrp);
                productStockTxnVO.setAmount(mrpAmount);
                productStockTxnVO.setAmountStr(PretupsBL.getDisplayAmount(mrpAmount));
                totalQuantity = totalQuantity + quantity;
            }
            productStockTxnVO.setTotalMrp(BTSLUtil.parseDoubleToLong(tempTotalMrp));
            productStockTxnVO.setTotalMrpStr(PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(tempTotalMrp)));
            productStockTxnVO.setTotalQty(totalQuantity);
            productStockTxnVO.setStockTxnType(PretupsI.STOCK_TXN_TYPE);
            long approveLimit = ((Long) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.NETWORK_STOCK_CIRCLE_MAXLIMIT, requestVO.getNetworkCode())).longValue();
            productStockTxnVO.setMaxAmountLimit(approveLimit);
            con.commit();
            response.setStatus((HttpStatus.SC_OK));
            response.setMessage(PretupsI.SUCCESS);
            response.setMessageCode(PretupsErrorCodesI.NTWRK_STOCK_DEDUCTION_ADD_SUCCESS);

        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }

        }
        return response;
    }

    @Override
    public NetworkStockTxnVO1 initiateStockDeduction(Connection con, MComConnectionI mcomCon, String categoryType, String loginID, HttpServletResponse response1, MultiValueMap<String, String> headers, NetworkInitiateStockDeductionRequestVO requestVO) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "initiateStockDeduction";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }

        NetworkStockDAO networkStockDAO = null;
        NetworkStockTxnVO1 networkStockTxnVO = new NetworkStockTxnVO1();
        String[] arg = new String[1];
        int addCount = 0;
        String tempQty = null;
        double quantity = 0D;
        long mrp = 0L;
        long mrpAmount = 0L;
        double totalQuantity = 0D;
        double tempTotalMrp = 0D;
        UserDAO userDAO = new UserDAO();
        UserVO userVO = new UserVO();
        Date currentDate = new Date();
        try {
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            Date currentdate = new Date();
            String userID = userVO.getUserID();
            networkStockTxnVO.setFirstApproverLimit(((Long) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.NETWORK_STOCK_FIRSTAPPLIMIT, userVO.getNetworkID())).longValue());
            networkStockTxnVO.setTxnDate(currentdate);
            networkStockTxnVO.setCreatedBy(userID);
            networkStockTxnVO.setCreatedOn(currentdate);
            networkStockTxnVO.setModifiedOn(currentdate);
            networkStockTxnVO.setModifiedBy(userID);
            networkStockTxnVO.setInitiatedBy(userVO.getUserID());
            networkStockTxnVO.setUserID(userVO.getUserID());
            networkStockTxnVO.setNetworkCode(requestVO.networkCode);
            networkStockTxnVO.setTxnNo(NetworkStockBL.genrateStockTransctionID1(networkStockTxnVO));
            networkStockTxnVO.setInitiaterRemarks(requestVO.remarks);
            networkStockTxnVO.setCancelledOn(currentDate);
            networkStockTxnVO.setCancelledBy(userVO.getUserID());
            arg[0] = networkStockTxnVO.getTxnNo();

            ArrayList tempStockItemList = new ArrayList();
            ProductStockTxnVO networkStockTxnItemsVOold = null;
            NetworkStockTxnItemsVO networkStockTxnItemsVOnew = null;
            int seqNo = 1;
            for (int i = 0, j = requestVO.getStockProductListSize(); i < j; i++) {
                networkStockTxnItemsVOold = (ProductStockTxnVO) requestVO.getProductList().get(i);
                if (!BTSLUtil.isNullString(networkStockTxnItemsVOold.getRequestedQuantity())) {
                    networkStockTxnItemsVOnew = new NetworkStockTxnItemsVO();
                    networkStockTxnItemsVOnew.setSNo(seqNo++);
                    networkStockTxnItemsVOnew.setTxnNo(networkStockTxnVO.getTxnNo());
                    networkStockTxnItemsVOnew.setProductCode(networkStockTxnItemsVOold.getProductCode());
                    networkStockTxnItemsVOnew.setProductName(networkStockTxnItemsVOold.getProductName());

                    if (PretupsI.STOCK_TXN_TYPE.equals(networkStockTxnItemsVOold.getStockTxnType())) {
                        networkStockTxnItemsVOnew.setRequiredQuantity(PretupsBL.getSystemAmount("-" + networkStockTxnItemsVOold.getRequestedQuantity()));
                        networkStockTxnItemsVOnew.setApprovedQuantity(networkStockTxnItemsVOnew.getRequiredQuantity());
                        networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount("-" + networkStockTxnItemsVOold.getRequestedQuantity()));
                    } else {
                        networkStockTxnItemsVOnew.setRequiredQuantity(PretupsBL.getSystemAmount(networkStockTxnItemsVOold.getRequestedQuantity()));
                        networkStockTxnItemsVOnew.setApprovedQuantity(networkStockTxnItemsVOnew.getRequiredQuantity());
                        networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount("-" + networkStockTxnItemsVOold.getRequestedQuantity()));
                    }

                    tempQty = networkStockTxnItemsVOold.getRequestedQuantity();
                    if (BTSLUtil.isNullString(tempQty)) {
                        networkStockTxnItemsVOold.setAmount(0);
                        networkStockTxnItemsVOold.setAmountStr(null);
                        continue;
                    }

                    quantity = new Double(tempQty.trim()).doubleValue();
                    mrp = networkStockTxnItemsVOold.getUnitValue();

                    mrpAmount = Double.valueOf((quantity * mrp)).longValue();
                    tempTotalMrp += (quantity * mrp);
                    networkStockTxnItemsVOnew.setAmount(mrpAmount);
                    networkStockTxnItemsVOnew.setAmountStr(PretupsBL.getDisplayAmount(mrpAmount));
                    networkStockTxnItemsVOnew.setMrp(networkStockTxnItemsVOnew.getAmount());
                    totalQuantity = totalQuantity + quantity;
                    networkStockTxnItemsVOold.setTotalMrp(BTSLUtil.parseDoubleToLong(tempTotalMrp));
                    networkStockTxnItemsVOold.setTotalMrpStr(PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(tempTotalMrp)));
                    networkStockTxnItemsVOold.setTotalQty(totalQuantity);
                    networkStockTxnVO.setTxnWallet(networkStockTxnItemsVOold.getWallet_type());
                    networkStockTxnVO.setTotalMrp(networkStockTxnItemsVOold.getTotalMrp());
                    networkStockTxnVO.setTotalMrpStr(networkStockTxnItemsVOold.getTotalMrpStr());
                    networkStockTxnVO.setTotalQty(networkStockTxnItemsVOold.getTotalQty());
                    networkStockTxnItemsVOnew.setWalletType(networkStockTxnItemsVOold.getWallet_type());
                    networkStockTxnItemsVOnew.setWalletBalance(PretupsBL.getSystemAmount(networkStockTxnItemsVOold.getWalletBalance()));
                    networkStockTxnItemsVOnew.setDateTime(currentdate);
                    tempStockItemList.add(networkStockTxnItemsVOnew);
                }
            }


            networkStockTxnItemsVOold.setStockTxnType(PretupsI.STOCK_TXN_TYPE);
            long approveLimit = ((Long) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.NETWORK_STOCK_CIRCLE_MAXLIMIT, requestVO.getNetworkCode())).longValue();
            networkStockTxnItemsVOold.setMaxAmountLimit(approveLimit);
            networkStockTxnVO.setNetworkStockTxnItemsList(tempStockItemList);
            networkStockTxnVO.setNetworkCode(requestVO.getNetworkCode());
            networkStockTxnVO.setNetworkFor(requestVO.getNetworkCodeFor());
            networkStockTxnVO.setReferenceNo(requestVO.getReferenceNumber());
            networkStockTxnVO.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_NEW);
            networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
            networkStockTxnVO.setEntryType(PretupsI.STOCK_TXN_TYPE);
            networkStockTxnVO.setTxnType(PretupsI.DEBIT);
            networkStockTxnVO.setMaxAmountLimit(approveLimit);
            networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount("-" + totalQuantity));
            networkStockTxnVO.setApprovedQuantity(networkStockTxnVO.getRequestedQuantity());
            networkStockTxnVO.setTxnMrp(PretupsBL.getSystemAmount("-" + totalQuantity));

            networkStockDAO = new NetworkStockDAO();

            addCount = networkStockDAO.addNetworkStockTransaction1(con, networkStockTxnVO);
            networkStockTxnVO.setMessage(PretupsI.NETWORK_STOCK_SUCCESS);
            networkStockTxnVO.setStatus((HttpStatus.SC_OK));
            if (con != null) {
                if (addCount > 0) {
                    mcomCon.finalCommit();
                    networkStockTxnVO.setMessage(PretupsI.NETWORK_STOCK_SUCCESS);
                    networkStockTxnVO.setStatus((HttpStatus.SC_OK));

                } else {
                    mcomCon.finalRollback();
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INITAITE_FAIL1, 0, null);

                }
            }

        } finally {

            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }

        }
        return networkStockTxnVO;

    }
    @Override
    public NetworkStockInitiateDeductionResponseVO getStockAuthorise(Connection con, MComConnectionI mcomCon, String categoryType, String loginID, HttpServletResponse response1, MultiValueMap<String, String> headers, String walletType) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "getStockAuthorise";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        UserDAO userDAO = new UserDAO();
        UserVO userVO = new UserVO();
        NetworkWebDAO networkWebDAO = null;
        NetworkStockWebDAO networkStockwebDAO = null;
        NetworkStockTxnItemsVO networkstockTxnItemsVO = new NetworkStockTxnItemsVO();
        NetworkStockInitiateDeductionResponseVO networkStockresponse = new NetworkStockInitiateDeductionResponseVO();
        try {
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            String homeStock = "";
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USE_HOME_STOCK))).booleanValue()) {
                homeStock = TypesI.YES;
            }

            networkStockresponse.setNetwork_code(userVO.getNetworkID());
            networkStockresponse.setRequestor_name(userVO.getUserName());
            networkStockresponse.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_DEDUCTION);
            networkStockresponse.setTxnType(PretupsI.DEBIT);
            networkStockresponse.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_NEW);
            networkStockresponse.setDate(BTSLUtil.getDateStringFromDate(new Date()));
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            if (TypesI.YES.equals(homeStock)) {
                networkStockresponse.setNetworkName(userVO.getNetworkName());
                networkStockresponse.setStock_type(PretupsI.TRANSFER_STOCK_TYPE_HOME);
                networkStockwebDAO = new NetworkStockWebDAO();

                networkStockresponse.setProductList(networkStockwebDAO.loadProductsForStock(con, userVO.getNetworkID(), userVO.getNetworkID(), PretupsI.C2S_MODULE));
                if (BTSLUtil.isNullString(walletType)) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.WALLET, 0, null);
                }
                ArrayList productList = networkStockresponse.getProductList();
                ArrayList newProductList = new ArrayList();
                boolean found = false;
                NetworkStockTxnItemsVO tempVO = null;
                HashMap<String, NetworkStockTxnItemsVO> map = new HashMap<String, NetworkStockTxnItemsVO>();
                int productListsSize = productList.size();
                for (int i = 0; i < productListsSize; i++) {
                    tempVO = (NetworkStockTxnItemsVO) productList.get(i);
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
                    } else {
                        NetworkStockTxnItemsVO tempVO1 = new NetworkStockTxnItemsVO();
                        org.apache.commons.beanutils.BeanUtils.copyProperties(tempVO1, tempVO);
                        tempVO1.setWalletType(null);
                        tempVO1.setWalletBalance(0L);
                        tempVO1.setStock(0L);
                        map.put(tempVO1.getProductCode(), tempVO1);
                    }


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

                networkStockresponse.setProductList(newProductList);
                networkStockresponse.setStatus(HttpStatus.SC_OK);
                networkStockresponse.setMessage(PretupsI.SUCCESS);


            } else {
                networkStockresponse.setStock_type(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
                networkWebDAO = new NetworkWebDAO();
                networkStockresponse.setRoamNetworkList(networkWebDAO.loadRoamNetworkList(con, " ", PretupsI.ROAM_LOCATION_TYPE));
                ListValueVO listValueVO = new ListValueVO(userVO.getNetworkName(), userVO.getNetworkID());
                networkStockresponse.getRoamNetworkList().add(0, listValueVO);
                networkStockresponse.setStatus(HttpStatus.SC_OK);
                networkStockresponse.setMessage(PretupsI.SUCCESS);

            }
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }

        }
        return networkStockresponse;
    }

    @Override
    public ApprovalStockResponseVO getStockDeductionTransactionList(Connection con, String loginId, HttpServletResponse httpServeletResponse, MultiValueMap<String, String> headers) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "getStockDeductionTransactionList";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        ApprovalStockResponseVO approvalStockResponseVO = new ApprovalStockResponseVO();
        UserDAO userDAO = new UserDAO();
        UserVO userVO = new UserVO();
        MComConnectionI mcomCon = null;
        NetworkStockWebDAO networkStockwebDAO = null;
        ArrayList<NetworkStockTxnVO1> transactions = new ArrayList<>();
        try {
            String transactionStatus = "'" + PretupsI.NETWORK_STOCK_TXN_STATUS_NEW + "'";
            String networkType = null;
            networkType = PretupsI.ROAM_LOCATION_TYPE;
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);

            approvalStockResponseVO.setNetworkCode(userVO.getNetworkID());
            approvalStockResponseVO.setUserId(userVO.getUserID());
            approvalStockResponseVO.setRadioIndex("0");
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            networkStockwebDAO = new NetworkStockWebDAO();
            transactions = networkStockwebDAO.loadStockDeductionList(con, transactionStatus, userVO.getNetworkID(), networkType);
            if(transactions == null){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }
            else {
                approvalStockResponseVO.setNetworkStockTxnList(transactions);
                approvalStockResponseVO.setStatus(HttpStatus.SC_OK);
                httpServeletResponse.setStatus(HttpStatus.SC_OK);
                approvalStockResponseVO.setMessage(PretupsI.SUCCESS);
            }

        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, " Exiting");
            }

        }
        return approvalStockResponseVO;
    }


    @Override
    public ApprovalStockDetailsResponseVO getStockDeductionTransactionDetails(Connection con, String loginId, HttpServletResponse httpServeletResponse, MultiValueMap<String, String> headers, String transactionNo) throws BTSLBaseException, Exception {

        final String METHOD_NAME = "getStockDeductionTransactionDetails";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        ApprovalStockDetailsResponseVO response = new ApprovalStockDetailsResponseVO();

        MComConnectionI mcomCon = null;
        NetworkStockWebDAO networkStockwebDAO = null;
        ArrayList stockOrderList = null;
        NetworkStockTxnVO1 networkStockTxnVO = null;
        ArrayList stockItemList = null;
        NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
        double totalAmt = 0D;
        double amount = 0D;
        try {
            UserDAO userDAO = new UserDAO();
            String transactionStatus = "'" + PretupsI.NETWORK_STOCK_TXN_STATUS_NEW + "'";
            String networkType = null;
            networkType = PretupsI.ROAM_LOCATION_TYPE;
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
            networkStockwebDAO = new NetworkStockWebDAO();
            ArrayList<NetworkStockTxnVO1> transactions = networkStockwebDAO.loadStockDeductionListNew(con, transactionStatus, userVO.getNetworkID(), networkType);
            for (NetworkStockTxnVO1 networkStockTxn : transactions) {
                if (networkStockTxn.getTxnNo().equals(transactionNo)) {
                    networkStockTxnVO = networkStockTxn;
                    break;
                }
            }

            response.setStockType(networkStockTxnVO.getStockType());
            response.setEntryType(networkStockTxnVO.getEntryType());
            response.setTxnType(networkStockTxnVO.getTxnType());
            response.setTxnNo(networkStockTxnVO.getTxnNo());
            response.setRequesterName(networkStockTxnVO.getInitiaterName());
            response.setStockDateStr(BTSLUtil.getDateStringFromDate(networkStockTxnVO.getTxnDate()));
            response.setReferenceNumber(networkStockTxnVO.getReferenceNo());
            response.setTxnStatusDesc(networkStockTxnVO.getTxnStatusName());
            response.setNetworkForName(networkStockTxnVO.getNetworkForName());
            response.setRemarks(networkStockTxnVO.getInitiaterRemarks());
            response.setNetworkCodeFor(networkStockTxnVO.getNetworkFor());
            response.setLastModifiedTime(networkStockTxnVO.getLastModifiedTime());
            response.setFirstLevelRemarks(networkStockTxnVO.getFirstApprovedRemarks());
            response.setFirstLevelApprovedBy(networkStockTxnVO.getFirstApprovedBy());
            response.setSecondLevelRemarks(networkStockTxnVO.getSecondApprovedRemarks());
            response.setSecondLevelApprovedBy(networkStockTxnVO.getSecondApprovedBy());
            response.setWalletType(networkStockTxnVO.getTxnWallet());

            stockItemList = networkStockwebDAO.loadStockItemList(con, networkStockTxnVO.getTxnNo(), userVO.getNetworkID(), networkStockTxnVO.getNetworkFor(), networkStockTxnVO.getTxnWallet());
            HashMap<String, NetworkStockTxnItemsVO> map = new HashMap<String, NetworkStockTxnItemsVO>();
            ArrayList newStockList = new ArrayList();
            if (stockItemList != null && !stockItemList.isEmpty()) {
                for (int i = 0, j = stockItemList.size(); i < j; i++) {
                    networkStockTxnItemsVO = (NetworkStockTxnItemsVO) stockItemList.get(i);
                    if (networkStockTxnItemsVO.getWalletType().equals(networkStockTxnVO.getTxnWallet())) {
                        newStockList.add(networkStockTxnItemsVO);
                        networkStockTxnItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(-Long.parseLong(networkStockTxnItemsVO.getRequestedQuantity())));
                        amount = -(networkStockTxnItemsVO.getMrp() * Double.parseDouble(PretupsBL.getDisplayAmount(networkStockTxnItemsVO.getApprovedQuantity())));
                        networkStockTxnItemsVO.setApprovedQuantityStr(PretupsBL.getDisplayAmount(-networkStockTxnItemsVO.getApprovedQuantity()));
                        networkStockTxnItemsVO.setAmountStr(PretupsBL.getDisplayAmount(Double.valueOf(amount).longValue()));
                        totalAmt += amount;
                    }
                }
            }
            response.setTotalMrpStr(PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(totalAmt)));
            response.setStockItemsList(newStockList);
            response.setUserId(userVO.getUserID());
            response.setNetworkCode(networkStockTxnVO.getNetworkCode());
            NetworkStockLog.log(networkStockTxnVO);
            response.setStatus(HttpStatus.SC_OK);
            httpServeletResponse.setStatus(HttpStatus.SC_OK);
            response.setMessage(PretupsI.SUCCESS);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, " Exiting");
            }
        }
        return response;
    }

    @Override
    public BaseResponse approve(ApproveStockDeductionRequestVO requestVO, HttpServletResponse response, MultiValueMap<String, String> headers, String loginID) throws BTSLBaseException, Exception {

        BaseResponse responseVO = new BaseResponse();
        final String METHOD_NAME = "approve";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        mcomCon = new MComConnection();
        con = mcomCon.getConnection();
        NetworkStockDAO networkStockDAO = null;
        NetworkStockWebDAO networkStockwebDAO = null;
        String[] arg = new String[1];
        int updateCount = 1;
        String txnId = null;
        UserDAO userDAO = new UserDAO();
        UserVO userVO = new UserVO();
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        try {
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            double totalQuantity = 0;
            long tempTotalMrp = 0L;
            double quantity = 0;
            long mrp = 0L;
            long mrpAmount = 0L;
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            networkStockDAO = new NetworkStockDAO();
            networkStockwebDAO = new NetworkStockWebDAO();
            Date currentdate = new Date();
            String userID = userVO.getUserID();

            requestVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_DEDUCTION);
            requestVO.setTxnType(PretupsI.STOCK_TXN_TYPE);

            NetworkStockTxnVO networkStockTxnVO = constructVOFromForm(requestVO);
            ArrayList stockItemsList = requestVO.getStockItemsList();
            NetworkStockTxnItemsVO networkStockTxnItemsVO = null;
            for (int i = 0, j = stockItemsList.size(); i < j; i++) {
                networkStockTxnItemsVO = (NetworkStockTxnItemsVO) stockItemsList.get(i);
                if (BTSLUtil.isNullString(networkStockTxnItemsVO.getApprovedQuantityStr())) {
                    quantity = 0;
                } else {
                    quantity = Double.parseDouble(networkStockTxnItemsVO.getApprovedQuantityStr());
                }

                networkStockTxnItemsVO.setApprovedQuantity(PretupsBL.getSystemAmount(quantity));
                mrp = networkStockTxnItemsVO.getUnitValue();
                mrpAmount = Double.valueOf((quantity * mrp)).longValue();
                tempTotalMrp += mrpAmount;
                networkStockTxnItemsVO.setAmount(mrpAmount);

                networkStockTxnItemsVO.setMrp(mrpAmount);
                networkStockTxnItemsVO.setAmountStr(PretupsBL.getDisplayAmount(mrpAmount));
                totalQuantity += quantity;
            }
            networkStockTxnItemsVO.setApprovedQuantityStr(String.valueOf(totalQuantity));
            requestVO.setTotalMrp(tempTotalMrp);
            requestVO.setTotalMrpStr(PretupsBL.getDisplayAmount(tempTotalMrp));
            requestVO.setTotalQty(totalQuantity);


            networkStockTxnVO.setFirstApprovedBy(userID);
            networkStockTxnVO.setFirstApprovedOn(currentdate);
            networkStockTxnVO.setModifiedOn(currentdate);
            networkStockTxnVO.setModifiedBy(userID);
            networkStockTxnVO.setCreatedOn(currentdate);
            networkStockTxnVO.setCreatedBy(userID);
            networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_DEDUCTION);
            arg[0] = requestVO.getTxnNo();
            ArrayList networkStockList = new ArrayList();
            networkStockTxnVO.setNetworkStockTxnItemsList(requestVO.getStockItemsList());

            networkStockTxnVO.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_CLOSE);
            updateCount = 0;
            updateCount = networkStockDAO.updateNetworkDailyStock(con, constructStockVOFromTxnVO(networkStockTxnVO));

            if (updateCount > 0) {
                updateCount = networkStockwebDAO.updateNetworkStock(con, networkStockTxnVO, networkStockList);
            }

            if (networkStockList != null && !networkStockList.isEmpty()) {
                NetworkStockVO networkStockVO = null;
                NetworkStockTxnItemsVO itemsVO = null;
                for (int i = 0, j = networkStockList.size(); i < j; i++) {
                    networkStockVO = (NetworkStockVO) networkStockList.get(i);
                    for (int m = 0, n = networkStockTxnVO.getNetworkStockTxnItemsList().size(); m < n; m++) {
                        itemsVO = (NetworkStockTxnItemsVO) networkStockTxnVO.getNetworkStockTxnItemsList().get(m);
                        if (networkStockVO.getProductCode().equals(itemsVO.getProductCode())) {
                            itemsVO.setWalletBalance(networkStockVO.getPreviousBalance());
                            itemsVO.setMrp(-Math.abs(itemsVO.getAmount()));
                            itemsVO.setApprovedQuantity(-Math.abs(itemsVO.getApprovedQuantity()));
                            itemsVO.setAmount(-Math.abs(itemsVO.getAmount()));
                        }
                    }
                }
            }
            txnId = networkStockTxnVO.getTxnNo();
            networkStockTxnVO.setApprovedQuantity(requestVO.getTotalMrp());
            if (updateCount > 0) {
                updateCount = networkStockwebDAO.updateLevel1NetworkStockTransaction(con, networkStockTxnVO);
            }
            if (con != null) {
                if (updateCount > 0) {
                    mcomCon.finalCommit();
                    if (PretupsI.NETWORK_STOCK_TXN_STATUS_CLOSE.equals(networkStockTxnVO.getTxnStatus())) {
                        prepareNetworkStockLogger(networkStockTxnVO);
                    }

                } else {
                    mcomCon.finalRollback();
                    responseVO.setMessage(RestAPIStringParser.getMessage(locale,  PretupsErrorCodesI.STOCK_DEDUCTION_REJECTED_FAIL1, null));
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.STOCK_DEDUCTION_REJECTED_FAIL1, 0, null);
                }
            }
            responseVO.setTransactionId(txnId);
            response.setStatus(HttpStatus.SC_OK);
            responseVO.setStatus(HttpStatus.SC_OK);
            responseVO.setMessageCode(PretupsErrorCodesI.STOCK_DEDUCTION_APPROVED);
            responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.STOCK_DEDUCTION_APPROVED, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, " Exiting");
            }
        }
        return responseVO;
    }

    @Override
    public BaseResponse reject(RejectStockDeductionRequestVO requestVO, HttpServletResponse response, MultiValueMap<String, String> headers, String loginID) throws BTSLBaseException, Exception {
        BaseResponse responseVO = new BaseResponse();
        final String METHOD_NAME = "reject";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        NetworkStockWebDAO networkStockwebDAO = null;
        String[] arg = new String[1];
        int updateCount = 0;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            networkStockwebDAO = new NetworkStockWebDAO();
            Date currentdate = new Date();
            String userID = requestVO.getUserId();
            requestVO.setTxnStatus(PretupsI.NETWORK_STOCK_TXN_STATUS_CANCEL);
            NetworkStockTxnVO networkStockTxnVO = constructVOFromFormForReject(requestVO);
            networkStockTxnVO.setCancelledBy(userID);
            networkStockTxnVO.setCancelledOn(currentdate);
            networkStockTxnVO.setModifiedOn(currentdate);
            networkStockTxnVO.setModifiedBy(userID);
            arg[0] = requestVO.getTxnNo();
            String status = "'" + PretupsI.NETWORK_STOCK_TXN_STATUS_NEW + "'";
            updateCount = networkStockwebDAO.cancelStockTransaction(con, networkStockTxnVO, status);
            if (con != null) {
                if (updateCount > 0) {
                    mcomCon.finalCommit();
                    NetworkStockLog.log(networkStockTxnVO);
                } else {
                    mcomCon.finalRollback();
                    responseVO.setMessage(RestAPIStringParser.getMessage(locale,  PretupsErrorCodesI.STOCK_DEDUCTION_REJECTED_FAIL, null));
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.STOCK_DEDUCTION_REJECTED_FAIL, 0, null);
                }
            }
            response.setStatus(HttpStatus.SC_OK);
            responseVO.setStatus(HttpStatus.SC_OK);
            responseVO.setMessageCode(PretupsErrorCodesI.STOCK_DEDUCTION_REJECTED);
            responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.STOCK_DEDUCTION_REJECTED, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, " Exiting");
            }
        }

        return responseVO;

    }

    private NetworkStockTxnVO constructVOFromForm(ApproveStockDeductionRequestVO requestVO) throws Exception {
        String METHOD_NAME = "constructVOFromForm";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        NetworkStockTxnVO networkStockTxnVO = NetworkStockTxnVO.getInstance();
        networkStockTxnVO.setTxnNo(requestVO.getTxnNo());
        networkStockTxnVO.setStockType(requestVO.getStockType());
        networkStockTxnVO.setEntryType(requestVO.getEntryType());
        networkStockTxnVO.setTxnType(requestVO.getTxnType());
        networkStockTxnVO.setTxnStatus(requestVO.getTxnStatus());
        networkStockTxnVO.setTxnWallet(requestVO.getWalletType());
        if (PretupsI.STOCK_TXN_TYPE.equals(requestVO.getEntryType())) {
            networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(-requestVO.getTotalQty()));
            networkStockTxnVO.setApprovedQuantity(PretupsBL.getSystemAmount(-requestVO.getTotalQty()));
            networkStockTxnVO.setTxnMrp(-requestVO.getTotalMrp());
        } else {
            networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(requestVO.getTotalQty()));
            networkStockTxnVO.setApprovedQuantity(PretupsBL.getSystemAmount(requestVO.getTotalQty()));
            networkStockTxnVO.setTxnMrp(requestVO.getTotalMrp());
        }
        networkStockTxnVO.setNetworkCode(requestVO.getNetworkCode());
        networkStockTxnVO.setNetworkFor(requestVO.getNetworkCodeFor());

        if (!BTSLUtil.isNullString(requestVO.getRemarks())) {
            networkStockTxnVO.setInitiaterRemarks(requestVO.getRemarks().trim());
        } else {
            networkStockTxnVO.setInitiaterRemarks(requestVO.getRemarks());
        }

        if (!BTSLUtil.isNullString(requestVO.getReferenceNumber())) {
            networkStockTxnVO.setReferenceNo(requestVO.getReferenceNumber().trim());
        } else {
            networkStockTxnVO.setReferenceNo(requestVO.getReferenceNumber());
        }

        if (!BTSLUtil.isNullString(requestVO.getFirstLevelRemarks())) {
            networkStockTxnVO.setFirstApprovedRemarks(requestVO.getFirstLevelRemarks().trim());
        } else {
            networkStockTxnVO.setFirstApprovedRemarks(requestVO.getFirstLevelRemarks());
        }

        if (!BTSLUtil.isNullString(requestVO.getSecondLevelRemarks())) {
            networkStockTxnVO.setSecondApprovedRemarks(requestVO.getSecondLevelRemarks().trim());
        } else {
            networkStockTxnVO.setSecondApprovedRemarks(requestVO.getSecondLevelRemarks());
        }

        networkStockTxnVO.setLastModifiedTime(requestVO.getLastModifiedTime());
        networkStockTxnVO.setFirstApproverLimit(requestVO.getFirstLevelAppLimit());
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting:networkStockTxnVO=" + networkStockTxnVO);
        }
        return networkStockTxnVO;
    }

    private NetworkStockVO constructStockVOFromTxnVO(NetworkStockTxnVO p_networkStockTxnVO) throws Exception {
        String METHOD_NAME = "constructStockVOFromTxnVO";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:NetworkStockTxnVO=>" + p_networkStockTxnVO);
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
            log.debug(METHOD_NAME, "Exiting networkStockVO=" + networkStockVO);
        }
        return networkStockVO;
    }

    private void prepareNetworkStockLogger(NetworkStockTxnVO p_networkStockTxnVO) throws BTSLBaseException {
        String METHOD_NAME = "prepareNetworkStockLogger";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered p_networkStockTxnVO=" + p_networkStockTxnVO);
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
            p_networkStockTxnVO.setOtherInfo(PretupsI.NETWORK_STOCK_APPROVAL);
            p_networkStockTxnVO.setStockType(PretupsI.NETWORK_STOCK);
            p_networkStockTxnVO.setPreviousStock(itemsVO.getWalletbalance());
            p_networkStockTxnVO.setPostStock(itemsVO.getWalletbalance() + itemsVO.getApprovedQuantity());
            NetworkStockLog.log(p_networkStockTxnVO);
        }
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting");
        }
    }

    private NetworkStockTxnVO constructVOFromFormForReject(RejectStockDeductionRequestVO requestVO) throws Exception {
        String METHOD_NAME = "constructVOFromFormForReject";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        NetworkStockTxnVO networkStockTxnVO = NetworkStockTxnVO.getInstance();
        networkStockTxnVO.setTxnNo(requestVO.getTxnNo());
        networkStockTxnVO.setStockType(requestVO.getStockType());
        networkStockTxnVO.setEntryType(requestVO.getEntryType());
        networkStockTxnVO.setTxnType(requestVO.getTxnType());
        networkStockTxnVO.setTxnStatus(requestVO.getTxnStatus());
        networkStockTxnVO.setTxnWallet(requestVO.getWalletType());
        if (PretupsI.STOCK_TXN_TYPE.equals(requestVO.getEntryType())) {
            networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(-requestVO.getTotalQty()));
            networkStockTxnVO.setApprovedQuantity(PretupsBL.getSystemAmount(-requestVO.getTotalQty()));
            networkStockTxnVO.setTxnMrp(-requestVO.getTotalMrp());
        } else {
            networkStockTxnVO.setRequestedQuantity(PretupsBL.getSystemAmount(requestVO.getTotalQty()));
            networkStockTxnVO.setApprovedQuantity(PretupsBL.getSystemAmount(requestVO.getTotalQty()));
            networkStockTxnVO.setTxnMrp(requestVO.getTotalMrp());
        }
        networkStockTxnVO.setNetworkCode(requestVO.getNetworkCode());
        networkStockTxnVO.setNetworkFor(requestVO.getNetworkCodeFor());

        if (!BTSLUtil.isNullString(requestVO.getRemarks())) {
            networkStockTxnVO.setInitiaterRemarks(requestVO.getRemarks().trim());
        } else {
            networkStockTxnVO.setInitiaterRemarks(requestVO.getRemarks());
        }

        if (!BTSLUtil.isNullString(requestVO.getReferenceNumber())) {
            networkStockTxnVO.setReferenceNo(requestVO.getReferenceNumber().trim());
        } else {
            networkStockTxnVO.setReferenceNo(requestVO.getReferenceNumber());
        }

        if (!BTSLUtil.isNullString(requestVO.getFirstLevelRemarks())) {
            networkStockTxnVO.setFirstApprovedRemarks(requestVO.getFirstLevelRemarks().trim());
        } else {
            networkStockTxnVO.setFirstApprovedRemarks(requestVO.getFirstLevelRemarks());
        }

        if (!BTSLUtil.isNullString(requestVO.getSecondLevelRemarks())) {
            networkStockTxnVO.setSecondApprovedRemarks(requestVO.getSecondLevelRemarks().trim());
        } else {
            networkStockTxnVO.setSecondApprovedRemarks(requestVO.getSecondLevelRemarks());
        }

        networkStockTxnVO.setLastModifiedTime(requestVO.getLastModifiedTime());
        networkStockTxnVO.setFirstApproverLimit(requestVO.getFirstLevelAppLimit());
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting:networkStockTxnVO=" + networkStockTxnVO);
        }
        return networkStockTxnVO;
    }
}
