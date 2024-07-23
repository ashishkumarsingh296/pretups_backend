package com.restapi.networkadmin.networkproductmap.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.*;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.commissionprofile.BatchCommissionProfileController;
import com.restapi.networkadmin.networkproductmap.requestVO.NetworkProductMappingRequestVO;
import com.restapi.networkadmin.networkproductmap.responseVO.NetworkProductMappingResponseVO;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Service("NetworkProductMappingServiceI")
public class NetworkProductMappingServiceImpl implements NetworkProductMappingServiceI {

    public static final Log log = LogFactory.getLog(BatchCommissionProfileController.class.getName());
    public static final String classname = "NetworkProductMappingServiceImpl";
    @Override
    public NetworkProductMappingResponseVO loadNetworkProductDetails(Connection con, Locale locale, String loginID, HttpServletRequest request, HttpServletResponse responseSwag) throws BTSLBaseException, SQLException {
        if (log.isDebugEnabled()) {
            log.debug("loadNetworkProductDetails", "Entered");
        }
        final String METHOD_NAME = "loadNetworkProductDetails";
        NetworkProductMappingResponseVO response = new NetworkProductMappingResponseVO();
        NetworkProductDAO networkProductDAO = null;
        UserVO userVO = null;
        UserDAO userDAO = new UserDAO();
        String networkCode;
        String networkName;
        ArrayList networkProductList = null;
        ArrayList usageList;

        networkProductDAO = new NetworkProductDAO();
        userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
        networkCode = userVO.getNetworkID();
        networkName = userVO.getNetworkName();
        networkProductList = networkProductDAO.loadNetworkProductMappingVODetailList(con, networkCode, PretupsI.C2S_MODULE);
        usageList = LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_USAGE, true);
        if (networkProductList.size() > 0) {
            /*
             * store the status of the Products into the String
             * Array(_dataListStatusOld) exist on the form
             * used for logging the network product mapping details if a
             * product is suspended or activated
             */
            NetworkProductVO networkProductVO = null;
            String[] statusArr = new String[networkProductList.size()];
            Map statusMap = new HashMap<String, String>();
            String status = null;
            for (int i = 0, j = networkProductList.size(); i < j; i++) {
                networkProductVO = (NetworkProductVO) networkProductList.get(i);
                if (TypesI.YES.equals(networkProductVO.getStatus())) {
                    status = TypesI.YES;
                } else {
                    status = TypesI.SUSPEND;
                }
                statusMap.put(networkProductVO.getProductCode(), status);
            }
            response.setDataListStatusOld(statusMap);
        }else {
            throw new BTSLBaseException(this, "loadNetworkProductDetails", PretupsErrorCodesI.PRODUCT_PRODUCTACTION_MSG_NODATAFOUND, "loadNetworkProductMap");
        }
        response.setNetworkProductList(networkProductList);
        response.setUsageList(usageList);
        response.setStatus(PretupsI.RESPONSE_SUCCESS);
        response.setMessageCode(PretupsErrorCodesI.SUCCESS);
        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
        response.setMessage(resmsg);
        responseSwag.setStatus(PretupsI.RESPONSE_SUCCESS);

        return response;
    }

    @Override
    public NetworkProductMappingResponseVO addNetworkProductMappingDetails(Connection con, MComConnectionI mcomCon, Locale locale, String loginID, HttpServletRequest httpServletRequest, HttpServletResponse responseSwagger, NetworkProductMappingRequestVO request) throws BTSLBaseException, SQLException {
        if (log.isDebugEnabled()) {
            log.debug("addNetworkProductMappingDetails", "Entered");
        }
        final String METHOD_NAME = "addNetworkProductMappingDetails";
//        MComConnectionI mcomCon = new MComConnection();
        NetworkProductMappingResponseVO response = new NetworkProductMappingResponseVO();
        int addCount = 0;
        ArrayList networkProductMapList = null;
        NetworkProductDAO networkProductDAO = new NetworkProductDAO();
        NetworkProductVO networkProductVO =  new NetworkProductVO();
        UserVO userVO = null;
        UserDAO userDAO = new UserDAO();
        String networkCode;
        String networkName;
        userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
        networkCode = userVO.getNetworkID();
        networkProductMapList = request.getNetworkProductList();
        Date currentDate = new Date();
        networkProductVO.setCreatedBy(userVO.getUserID());
        networkProductVO.setModifiedBy(userVO.getUserID());
        networkProductVO.setCreatedOn(currentDate);
        networkProductVO.setModifiedOn(currentDate);
        addCount = networkProductDAO.addNetworkProductMappingDetails(con, networkProductMapList, networkCode, networkProductVO);
        if (addCount > 0) {
            mcomCon.finalCommit();
            if(networkProductMapList != null && networkProductMapList.size() >0){
                ProductVO productVO = null;
                BTSLMessages sendBtslMessage = null;
                AdminOperationVO adminOperationVO = null;
                for (int i = 0, j = networkProductMapList.size(); i < j; i++) {
                    productVO = (ProductVO) networkProductMapList.get(i);
                    String[] msgArr = {productVO.getProductName()};
                    sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.NETWORK_PRODUCT_MAP_MODIFIED, msgArr);
                    /*
                     * log the network details in networkLog.log file if any
                     * network is activated or suspended
                     */
                    String status = (String) request.getDataListStatusOld().get(productVO.getProductCode());
                    if (!productVO.getStatus().equals(status)) {
                        adminOperationVO = new AdminOperationVO();
                        adminOperationVO.setSource(TypesI.LOGGER_NETWORK_SOURCE);
                        adminOperationVO.setDate(currentDate);
                        if (TypesI.YES.equals(productVO.getStatus())) {
                            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ACTIVATED);
                            adminOperationVO.setInfo("Product " + productVO.getProductName() + " has activated");
                            EventHandler.handle(EventIDI.ADMIN_OPT_PD_STATUS, EventComponentI.SYSTEM, EventStatusI.CLEARED, EventLevelI.INFO, "NetworkProductMappingService[addNetworkProductMappingDetails]", "", "", productVO.getProductName(), "Product " + productVO.getProductName() + " has activated");
                            sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.NETWORK_PRODUCT_MAP_ACTIVATED, msgArr);
                        } else {
                            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_SUSPENDED);
                            adminOperationVO.setInfo("Product " + productVO.getProductName() + " has suspended");
                            EventHandler.handle(EventIDI.ADMIN_OPT_PD_STATUS, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "NetworkProductMappingService[addNetworkProductMappingDetails]", "", "", productVO.getProductName(), "Product " + productVO.getProductName() + " has suspended");
                            sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.NETWORK_PRODUCT_MAP_SUSPENDED, msgArr);
                        }
                        adminOperationVO.setLoginID(userVO.getLoginID());
                        adminOperationVO.setUserID(userVO.getUserID());
                        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                        adminOperationVO.setNetworkCode(userVO.getNetworkID());
                        adminOperationVO.setMsisdn(userVO.getMsisdn());
                        AdminOperationLog.log(adminOperationVO);
                    }
                }
                response.setNetworkProductList(networkProductMapList);
                response.setStatus(PretupsI.RESPONSE_SUCCESS);
                response.setMessageCode(PretupsErrorCodesI.SUCCESS);
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PRODUCT_NETWORKPRODUCTDETAIL_SUCCESSMESSAGE, null);
                response.setMessage(resmsg);
                responseSwagger.setStatus(PretupsI.RESPONSE_SUCCESS);
                final PushMessage pushMessage = new PushMessage(userVO.getMsisdn(), sendBtslMessage, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
                        (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), userVO.getNetworkID());
                pushMessage.push();
            }else {
                mcomCon.finalRollback();
                throw new BTSLBaseException(this, "addNetWorkProduct", PretupsErrorCodesI.PRODUCT_NETWORKPRODUCTDETAIL_FAILEDMESSAGE, "list");
            }
        }
        return response;
    }
}
