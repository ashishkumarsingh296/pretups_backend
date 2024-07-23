package com.restapi.networkadmin.serviceproductinterfacemapping.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingDAO;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.networkadmin.serviceproductinterfacemapping.requestVO.*;
import com.restapi.networkadmin.serviceproductinterfacemapping.responseVO.*;
import com.web.pretups.network.businesslogic.NetworkWebDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Service("ServiceProductInterfaceMappingServiceI")
public class ServiceProductInterfaceMappingServiceImpl implements ServiceProductInterfaceMappingServiceI{

    public static final Log log = LogFactory.getLog(ServiceProductInterfaceMappingServiceImpl.class.getName());
    public static final String classname = "InterfacePrefixServiceImpl";

    @Override
    public ServiceTypesAndInterfaceListResponseVO getServiceTypesAndInterfaceList(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ServiceTypesAndInterfaceListResponseVO response) throws BTSLBaseException{
        final String METHOD_NAME = "getServiceTypesAndInterfaceList";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }


        ServiceSelectorInterfaceMappingDAO selectorInterfaceMappingDAO = null;
        try {

            ArrayList serviceTypeList = null;
            ArrayList interfaceTypeList = null;
            selectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
            String serviceType = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_INTFC_MAPPING_ALLOWED));
            if (BTSLUtil.isNullString(serviceType)) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CONFIGURATION_NOT_AVAILABLE_FOR_SERVICE, 0, null);
            }
            serviceTypeList = selectorInterfaceMappingDAO.loadServiceTypes(con, serviceType);
            interfaceTypeList = LookupsCache.loadLookupDropDown(PretupsI.INTERFACE_CATEGORY, true);
            if (serviceTypeList == null || serviceTypeList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SERVICE_EXIST, 0, null);
            }
            if (interfaceTypeList == null || interfaceTypeList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_INTERFACE_EXIST, 0, null);
            }




            response.setServiceTypeList(serviceTypeList);
            response.setInterfaceTypeList(interfaceTypeList);



            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
        }
        finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }





    //////////////////////////////////////////////
    @Override
    public ServiceInterfaceMappingForViewResponseVO getServiceInterfaceMappingForView(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ServiceInterfaceMappingForViewResponseVO response, String serviceType) throws BTSLBaseException{
        final String METHOD_NAME = "getServiceInterfaceMappingForView";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }

        ServiceSelectorInterfaceMappingDAO serviceSelectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
        //String serviceType = null;
        ServiceSelectorInterfaceMappingDAO selectorInterfaceMappingDAO = null;

        ArrayList tempList = new ArrayList();
        try {

            //validation starts here
            if(!isValidRequestString(serviceType)){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_SERVICE_TYPE, 0, null);
            }
            //validation ends here

            //code for taking out service type list starts
            selectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
            ArrayList serviceTypeList = null;
            serviceTypeList = selectorInterfaceMappingDAO.loadServiceTypes(con, serviceType);
            if (serviceTypeList == null || serviceTypeList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SERVICE_EXIST, 0, null);
            }
            //code for taking out service type list ends

            //serviceType = serviceSelectorInterfaceMappingForm.getServiceType();
            ArrayList productList = serviceSelectorInterfaceMappingDAO.loadServiceProductList(con, serviceType);
            ArrayList serviceInterfaceMapList = serviceSelectorInterfaceMappingDAO.loadServiceInterfaceMappingRuleList(con, userVO.getNetworkID(), serviceType);
            ArrayList interfaceTypeList = serviceSelectorInterfaceMappingDAO.loadInterfaceForModify(con);


            if (serviceInterfaceMapList != null && serviceInterfaceMapList.size() > 0) {
                ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;
                String interfaceValidationKey = null;
                String interfaceUpdateKey = null;
                String prefixKey = null;
                String prefixValidationPreKey = null;
                String prefixValidationPostKey = null;
                String prefixUpdatePreKey = null;
                String prefixUpdatePostKey = null;
                prefixValidationPreKey = null;
                prefixValidationPostKey = null;
                prefixUpdatePreKey = null;
                prefixUpdatePostKey = null;
                String prevServiceProd = null, currServProd = null;
                ServiceSelectorInterfaceMappingVO servVO = null;

                for (int m = 0, n = serviceInterfaceMapList.size(); m < n; m++) {
                    serviceSelectorInterfaceMappingVO = (ServiceSelectorInterfaceMappingVO) serviceInterfaceMapList.get(m);
                    serviceSelectorInterfaceMappingVO.setServiceName(BTSLUtil.getOptionDesc(serviceSelectorInterfaceMappingVO.getServiceType(), serviceTypeList).getLabel());
                    serviceSelectorInterfaceMappingVO.setSelectorName(BTSLUtil.getOptionDesc(serviceSelectorInterfaceMappingVO.getSelectorCode(), productList).getLabel());
                    serviceSelectorInterfaceMappingVO.setInterfaceName(BTSLUtil.getOptionDesc(serviceSelectorInterfaceMappingVO.getInterfaceID(), interfaceTypeList).getLabel());

                    // /This done to sepreate different VO on basis of
                    // currServProd///
                    currServProd = serviceSelectorInterfaceMappingVO.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO.getInterfaceID();// +"_"+serviceSelectorInterfaceMappingVO.getAction()+"_"+serviceSelectorInterfaceMappingVO.getMethodType();
                    interfaceValidationKey = serviceSelectorInterfaceMappingVO.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO.getInterfaceID() + "_" + PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION;
                    interfaceUpdateKey = serviceSelectorInterfaceMappingVO.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO.getInterfaceID() + "_" + PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION;

                    prefixKey = serviceSelectorInterfaceMappingVO.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO.getInterfaceID() + "_" + serviceSelectorInterfaceMappingVO.getAction();

                    // // This block will create a unique row bases on the
                    // currentService Product
                    if (!currServProd.equals(prevServiceProd)) {
                        if (servVO != null) {
                            tempList.add(servVO);
                            prefixValidationPreKey = null;
                            prefixValidationPostKey = null;
                            prefixUpdatePreKey = null;
                            prefixUpdatePostKey = null;
                        }
                        servVO = serviceSelectorInterfaceMappingVO;
                        if (prefixKey.equals(interfaceValidationKey)) {
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(servVO.getMethodType())) {
                                if (prefixValidationPreKey == null) {
                                    prefixValidationPreKey = servVO.getPrefixSeries();
                                } else {
                                    prefixValidationPreKey += "," + servVO.getPrefixSeries();
                                }
                            } else if (PretupsI.SERIES_TYPE_POSTPAID.equals(servVO.getMethodType())) {
                                if (prefixValidationPostKey == null) {
                                    prefixValidationPostKey = servVO.getPrefixSeries();
                                } else {
                                    prefixValidationPostKey += "," + servVO.getPrefixSeries();
                                }
                            }
                        }

                        else if (prefixKey.equals(interfaceUpdateKey)) {
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(servVO.getMethodType())) {
                                if (prefixUpdatePreKey == null) {
                                    prefixUpdatePreKey = servVO.getPrefixSeries();
                                } else {
                                    prefixUpdatePreKey += "," + servVO.getPrefixSeries();
                                }
                            } else if (PretupsI.SERIES_TYPE_POSTPAID.equals(servVO.getMethodType())) {
                                if (prefixUpdatePostKey == null) {
                                    prefixUpdatePostKey = servVO.getPrefixSeries();
                                } else {
                                    prefixUpdatePostKey += "," + servVO.getPrefixSeries();
                                }
                            }
                        }
                        servVO.setValidatePrepaidSeries(prefixValidationPreKey);
                        servVO.setValidatePostpaidSeries(prefixValidationPostKey);
                        servVO.setUpdatePrepaidSeries(prefixUpdatePreKey);
                        servVO.setUpdatePostpaidSeries(prefixUpdatePostKey);

                    } // /end//
                    // This block is used to append values to the same row if
                    // two or more service product are same
                    else {
                        if (prefixKey.equals(interfaceValidationKey)) {
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(serviceSelectorInterfaceMappingVO.getMethodType())) {
                                if (prefixValidationPreKey == null) {
                                    prefixValidationPreKey = serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                } else {
                                    prefixValidationPreKey += "," + serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                }
                                servVO.setValidatePrepaidSeries(prefixValidationPreKey);
                            } else if (PretupsI.SERIES_TYPE_POSTPAID.equals(serviceSelectorInterfaceMappingVO.getMethodType())) {
                                if (prefixValidationPostKey == null) {
                                    prefixValidationPostKey = serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                } else {
                                    prefixValidationPostKey += "," + serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                }
                                servVO.setValidatePostpaidSeries(prefixValidationPostKey);
                            }
                        }

                        else if (prefixKey.equals(interfaceUpdateKey)) {
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(serviceSelectorInterfaceMappingVO.getMethodType())) {
                                if (prefixUpdatePreKey == null) {
                                    prefixUpdatePreKey = serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                } else {
                                    prefixUpdatePreKey += "," + serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                }
                                servVO.setUpdatePrepaidSeries(prefixUpdatePreKey);
                            } else if (PretupsI.SERIES_TYPE_POSTPAID.equals(serviceSelectorInterfaceMappingVO.getMethodType())) {
                                if (prefixUpdatePostKey == null) {
                                    prefixUpdatePostKey = serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                } else {
                                    prefixUpdatePostKey += "," + serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                }
                                servVO.setUpdatePostpaidSeries(prefixUpdatePostKey);
                            }
                        }

                    }// /end//

                    prevServiceProd = currServProd;
                }
                tempList.add(servVO);
                response.setServiceSelectorInterfaceMapVOList(tempList);


                response.setStatus((HttpStatus.SC_OK));
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
            }else{
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SERVICE_INTERFACE_MAP_LIST_EXIST, 0, null);
            }




        }
        finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }












    @Override
    public ServiceInterfaceMappingForAddResponseVO getServiceInterfaceMappingForAdd(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ServiceInterfaceMappingForAddResponseVO response, String serviceType, String interfaceType) throws BTSLBaseException {
        final String METHOD_NAME = "getServiceInterfaceMappingForAdd";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }

        ServiceSelectorInterfaceMappingDAO selectorInterfaceMappingDAO = null;
        ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;
        ArrayList serviceProductInterfaceVOList = null;
        NetworkWebDAO networkwebDAO = null;

        ArrayList serviceTypeList = null;
        try{
            networkwebDAO = new NetworkWebDAO();
            ArrayList list = networkwebDAO.loadNetworkPrefix(con, userVO.getNetworkID());


            //validation starts here
            if(!isValidRequestString(serviceType)){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_SERVICE_TYPE, 0, null);
            }

            if(!isValidRequestString(interfaceType)){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_INTERFACE_TYPE, 0, null);
            }
            //validation ends here


            // /////////////This is just to load all the pre and post series to
            // check at validation of series
            if (list != null) {
                // this map is set on the form bean and used during save to know
                // thw prefix id of the series
                HashMap seriesMap = new HashMap();
                String seriesKey = null;

                HashMap map = new HashMap();
                NetworkPrefixVO myVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    myVO = (NetworkPrefixVO) list.get(i);

                    String key = myVO.getOperator() + "_" + myVO.getSeriesType();
                    if (map.containsKey(key)) {
                        String ser = (String) map.get(key);
                        ser += "," + myVO.getSeries();
                        map.put(key, ser);
                    } else {
                        map.put(key, myVO.getSeries());
                    }

                    // prepare a seriesMap that is used during save(Inserting
                    // Interface Network Prefix Mapping)
                    seriesKey = myVO.getNetworkCode() + "_" + myVO.getOperator() + "_" + myVO.getSeriesType() + "_" + myVO.getSeries();
                    seriesMap.put(seriesKey, String.valueOf(myVO.getPrefixID()));

                }
                /*
                 * When we fetch data from the DB it returns all the
                 * prepaidseries,postpaidseries
                 * and otherseries.
                 * if operator = OPT and seriesType = PREPAID its an prepaid
                 * series
                 * if operator = OPT and seriesType = POSTPAID its an postpaid
                 * series
                 * so here first we create an hash map which contains three key
                 * and there value
                 * like OPT_PREPIAD = 12345,34567 (PREPIAD SERIES)
                 * OPT_POSTPIAD = 12345,34567 (POSTPIAD SERIES)
                 */

                String temp = "";
                String temp1 = (String) map.get(PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_PREPAID);
                String temp2 = (String) map.get(PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_PREPAID);
                if (!BTSLUtil.isNullString(temp1) && BTSLUtil.isNullString(temp2)) {
                    temp = temp1;
                } else if (!BTSLUtil.isNullString(temp2) && BTSLUtil.isNullString(temp1)) {
                    temp = temp2;
                } else if (!BTSLUtil.isNullString(temp2) && !BTSLUtil.isNullString(temp1)) {
                    temp = temp1 + "," + temp2;
                }

                response.setPrepaidSeries(temp);
                temp = "";
                temp1 = null;
                temp2 = null;
                temp1 = (String) map.get(PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_POSTPAID);
                temp2 = (String) map.get(PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_POSTPAID);
                if (!BTSLUtil.isNullString(temp1) && BTSLUtil.isNullString(temp2)) {
                    temp = temp1;
                } else if (!BTSLUtil.isNullString(temp2) && BTSLUtil.isNullString(temp1)) {
                    temp = temp2;
                } else if (!BTSLUtil.isNullString(temp2) && !BTSLUtil.isNullString(temp1)) {
                    temp = temp1 + "," + temp2;
                }
                response.setPostpaidSeries(temp);
                // Change end for MNP
                response.setSeriesMap(seriesMap);
            }
            // /////////////END///////////////////////////////////////////////////////////


            //code for taking out serviceTypeList starts
            selectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
            serviceTypeList = selectorInterfaceMappingDAO.loadServiceTypes(con, serviceType);
            if (serviceTypeList == null || serviceTypeList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SERVICE_EXIST, 0, null);
            }
            //code for taking out serviceTypeList ends

            Iterator itr = serviceTypeList.iterator();
            while (itr.hasNext()) {
                ListValueVO listVO = (ListValueVO) itr.next();
                String labelValue = listVO.getValue();
                if (labelValue.equals(serviceType)) {
                    response.setServiceName(listVO.getLabel());
                    break;
                }
            }
            response.setInterfaceType(interfaceType);



            ArrayList productTypeList = null;
            ArrayList interfaceTypeList = null;
            //selectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
            productTypeList = selectorInterfaceMappingDAO.loadServiceProductList(con, serviceType);
            interfaceTypeList = selectorInterfaceMappingDAO.loadInterfaceList(con, interfaceType);
            if (productTypeList == null || productTypeList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_PRODUCT_EXIST, 0, null);
            }
            if (interfaceTypeList == null || interfaceTypeList.isEmpty()) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_INTERFACE_EXIST, 0, null);
            }

            response.setProductList(productTypeList);
            response.setInterfaceList(interfaceTypeList);
            // construction of blank VOs list for the display purpose
            serviceProductInterfaceVOList = new ArrayList();
            response.setNetworkName(userVO.getNetworkName());
            int rowNumber = 0;


            try {
                rowNumber = Integer.parseInt(Constants.getProperty("NO_ROW_SERVICE_PRODUCT_INTERFACE_MAP"));

            } catch (Exception exception) {
                log.error("loadServiceInterMapping", ">>>>Wrong entry for NO_ROW_SERVICE_PRODUCT_INTERFACE_MAP in constants.props\" ");
                log.errorTrace(METHOD_NAME, exception);
                rowNumber = 2;
            }
            for (int i = 0; i < rowNumber; i++) {
                serviceSelectorInterfaceMappingVO = new ServiceSelectorInterfaceMappingVO();
                serviceSelectorInterfaceMappingVO.setRowID(String.valueOf(i + 1)); // for
                // the
                // row
                // no.
                serviceSelectorInterfaceMappingVO.setServiceType(serviceType);
                serviceSelectorInterfaceMappingVO.setInterfaceType(interfaceType);
                serviceProductInterfaceVOList.add(serviceSelectorInterfaceMappingVO);

            }
            if (serviceProductInterfaceVOList != null) {
                response.setServiceSelectorInterfaceMappingList(serviceProductInterfaceVOList);
            }


            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);

        }
        finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }














    @Override
    public AddServiceInterfaceMappingResponseVO addServiceInterfaceMapping(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, AddServiceInterfaceMappingResponseVO response, AddServiceInterfaceMappingRequestVO requestVO) throws BTSLBaseException, SQLException {

        final String METHOD_NAME = "addServiceInterfaceMapping";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }


        int insertCount = 0;
        ServiceSelectorInterfaceMappingVO serSelectorInterfaceMappingVO = null;

        ServiceSelectorInterfaceMappingDAO selectorInterfaceMappingDAO = null;
        ListValueVO listValueVO = null;
        try {
            ServiceSelectorInterfaceMappingDAO serviceSelectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();

            /*
             * This map is constructed in the
             * loadInterfaceNetworkMappingPrefixList method
             * here we fetch the prefix_id of the entered series
             */
            //HashMap seriesMap = theForm.getSeriesMap();      //will come from request
            HashMap seriesMap = requestVO.getSeriesMap();

            String seriesKey = null;
            ArrayList seriesList = new ArrayList();
            ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;
            ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO1 = null;
            StringTokenizer value = null;
            String series = null;
            String prefixID = null;
            Date currentDate = new Date();
           // theForm.setNetworkName(userSessionVO.getNetworkName());
            ServiceSelectorInterfaceMappingCache.updateServSelInterfMapping();// refresh
            // the
            // cache//


            //code for checking valid interface ID and serviceType starts

            selectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
            ArrayList serviceTypeList = null;
            listValueVO = new ListValueVO();

            String serviceTypes = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_INTFC_MAPPING_ALLOWED));
            if (BTSLUtil.isNullString(serviceTypes)) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CONFIGURATION_NOT_AVAILABLE_FOR_SERVICE, 0, null);
            }
            serviceTypeList = selectorInterfaceMappingDAO.loadServiceTypes(con, serviceTypes);
            ArrayList interfaceTypeList = serviceSelectorInterfaceMappingDAO.loadInterfaceForModify(con);



            Set<String> serviceTypeSet = new HashSet<>();
            Set<String> interfaceTypeSet = new HashSet<>();

            // Populate the set with values from the data
            for (int i=0; i< serviceTypeList.size(); i++) {
                listValueVO = (ListValueVO) serviceTypeList.get(i);
                if (!serviceTypeSet.add(listValueVO.getValue())) {
                    String args[] = { listValueVO.getValue() };
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DUPLICATE_VALUE, 0, args , null);
                }
            }

            for (int i=0; i< interfaceTypeList.size(); i++) {
                listValueVO = (ListValueVO) interfaceTypeList.get(i);
                if (!interfaceTypeSet.add(listValueVO.getValue())) {
                    String args[] = { listValueVO.getValue() };
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DUPLICATE_VALUE, 0, args , null);
                }
            }


            //code for checking valid interface ID and serviceType ends



            ServiceSelectorInterfaceMapVO serviceSelectorInterfaceMapVO = null;
            //*************Validation code starts***********//
            if(requestVO.getPrepaidSeries().isEmpty() || BTSLUtil.isNullString(requestVO.getPrepaidSeries())){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PREPAID_SERIES_REQ, 0, null);
            }

            if(requestVO.getPostpaidSeries().isEmpty() || BTSLUtil.isNullString(requestVO.getPostpaidSeries())){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.POSTPAID_SERIES_REQ, 0, null);
            }

            if(requestVO.getSeriesMap().isEmpty()){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SERIES_MAP_REQ, 0, null);
            }

            ArrayList errors = new ArrayList();

            /*
             * This map contains the prepaid series of a particular network
             * used to check the validation prepaid and update prepaid
             * series
             * belongs to the prepaid series of that network
             */
            HashMap prepaidSeriesMap = new HashMap();
            //String series = null;

            StringTokenizer prepaidSeriesValue = new StringTokenizer(requestVO.getPrepaidSeries(), ",");
            while (prepaidSeriesValue.hasMoreTokens()) {
                series = (String) prepaidSeriesValue.nextToken();
                prepaidSeriesMap.put(series, series);
            }

            /*
             * This map contains the postpaid series of a particular network
             * used to check the validation postpaid and update postpaid
             * series
             * belongs to the postpaid series of that network
             */
            HashMap postpaidSeriesMap = new HashMap();
            series = null;

            StringTokenizer postpaidSeriesValue = new StringTokenizer(requestVO.getPostpaidSeries(), ",");
            while (postpaidSeriesValue.hasMoreTokens()) {
                series = (String) postpaidSeriesValue.nextToken();
                postpaidSeriesMap.put(series, series);
            }

            //ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;
            //StringTokenizer value = null;
            String interfaceCategory = null;

            HashMap interfaceCategoryMap_V = new HashMap();
            HashMap prefixListMap = null;
            HashMap interfaceCategoryMap_U = new HashMap();

            for (int i = 0, j = requestVO.getServiceSelectorInterfaceMappingList().size(); i < j; i++) {
                serviceSelectorInterfaceMapVO = (ServiceSelectorInterfaceMapVO) requestVO.getServiceSelectorInterfaceMappingList().get(i);
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getSelectorCode()) || !BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getInterfaceID()) || !BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getValidatePrepaidSeries()) || !BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getValidatePostpaidSeries()) || !BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getUpdatePrepaidSeries()) || !BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getUpdatePostpaidSeries())) {
                    String[] arr = new String[1];
                    arr[0] = String.valueOf(i + 1);
                    if (BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getSelectorCode())) {
                        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_SELECTOR_CODE, null);
                        errors.add(resmsg);
                    }

                    if (BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getInterfaceID())) {
                       String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INTERFACE_ID_REQUIRED, null);
                        errors.add(resmsg);
                    }

                    if (BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getValidatePrepaidSeries()) && BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getValidatePostpaidSeries()) && BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getUpdatePrepaidSeries()) && BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getUpdatePostpaidSeries())) {
                       String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VALIDATE_OR_UPDATE_SERIES_REQUIRED, null);
                        errors.add(resmsg);
                    }

                }
            }


            for (int i = 0, j = requestVO.getServiceSelectorInterfaceMappingList().size(); i < j; i++) {
                serviceSelectorInterfaceMapVO = (ServiceSelectorInterfaceMapVO) requestVO.getServiceSelectorInterfaceMappingList().get(i);
                interfaceCategory = serviceSelectorInterfaceMapVO.getInterfaceType();

                /*
                 * 1)check for validation pre series belongs to the
                 * prepaidseries or not
                 * 2)check for validation pre series should not be
                 * duplicated(series unique to all interfaces)
                 */
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getValidatePrepaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapVO.getValidatePrepaidSeries(), ",");
                    while (value.hasMoreTokens()) {
                        series = (String) value.nextToken();
                        // 1
                        if (!prepaidSeriesMap.containsKey(series)) {
                            String[] array = new String[1];
                            array[0] = series;
                            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VALIDATION_SERIES_PREPAID_NOT_VALID, array);
                            errors.add(resmsg);
                        }
                        if (interfaceCategoryMap_V.size() > 0) {
                            if (interfaceCategoryMap_V.get(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode()) != null) {
                                prefixListMap = (HashMap) interfaceCategoryMap_V.get(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode());
                                if (prefixListMap.containsKey(series)) {
                                    String[] array = new String[1];
                                    array[0] = series;
                                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VALIDATION_SERIES_PREPAID_DUPLICATED, array);
                                    errors.add(resmsg);
                                } else {
                                    prefixListMap.put(series, series);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_V.put(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode(), prefixListMap);
                            }
                        } else {
                            prefixListMap = new HashMap();
                            prefixListMap.put(series, series);
                            interfaceCategoryMap_V.put(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode(), prefixListMap);
                        }
                    }
                }

                /*
                 * 1)check for validation post series belongs to the
                 * prepaidseries or not
                 * 2)check for validation post series should not be
                 * duplicated(series unique to all interfaces)
                 */
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getValidatePostpaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapVO.getValidatePostpaidSeries(), ",");
                    while (value.hasMoreTokens()) {
                        series = (String) value.nextToken();
                        // 1
                        if (!postpaidSeriesMap.containsKey(series)) {
                            //errors.add("error", new ActionMessage("interfaces.vastrix.errors.validationseriespostnotvalid", series));
                            String[] array = new String[1];
                            array[0] = series;
                            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VALIDATION_SERIES_POSTPAID_NOT_VALID, array);
                            errors.add(resmsg);
                        }
                        if (interfaceCategoryMap_V.size() > 0) {
                            if (interfaceCategoryMap_V.get(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode()) != null) {
                                prefixListMap = (HashMap) interfaceCategoryMap_V.get(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode());
                                if (prefixListMap.containsKey(series)) {
                                    String[] array = new String[1];
                                    array[0] = series;
                                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VALIDATION_SERIES_POSTPAID_DUPLICATED, array);
                                    errors.add(resmsg);
                                } else {
                                    prefixListMap.put(series, series);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_V.put(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode(), prefixListMap);
                            }
                        } else {
                            prefixListMap = new HashMap();
                            prefixListMap.put(series, series);
                            interfaceCategoryMap_V.put(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode(), prefixListMap);
                        }
                    }
                }

                /*
                 * 1)check for update pre series belongs to the
                 * prepaidseries or not
                 * 2)check for update pre series should not be
                 * duplicated(series unique to all interfaces)
                 */
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getUpdatePrepaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapVO.getUpdatePrepaidSeries(), ",");
                    while (value.hasMoreTokens()) {
                        series = (String) value.nextToken();
                        // 1
                        if (!prepaidSeriesMap.containsKey(series)) {
                            String[] array = new String[1];
                            array[0] = series;
                            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATION_SERIES_PREPAID_NOT_VALID, array);
                            errors.add(resmsg);
                        }
                        if (interfaceCategoryMap_U.size() > 0) {
                            if (interfaceCategoryMap_U.get(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode()) != null) {
                                prefixListMap = (HashMap) interfaceCategoryMap_U.get(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode());
                                if (prefixListMap.containsKey(series)) {
                                    String[] array = new String[1];
                                    array[0] = series;
                                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATION_SERIES_PREPAID_DUPLICATED, array);
                                    errors.add(resmsg);
                                } else {
                                    prefixListMap.put(series, series);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_U.put(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode(), prefixListMap);
                            }
                        } else {
                            prefixListMap = new HashMap();
                            prefixListMap.put(series, series);
                            interfaceCategoryMap_U.put(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode(), prefixListMap);
                        }
                    }
                }

                /*
                 * 1)check for update post series belongs to the
                 * prepaidseries or not
                 * 2)check for update post series should not be
                 * duplicated(series unique to all interfaces)
                 */
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getUpdatePostpaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapVO.getUpdatePostpaidSeries(), ",");
                    while (value.hasMoreTokens()) {
                        series = (String) value.nextToken();

                        // 1
                        if (!postpaidSeriesMap.containsKey(series)) {
                            String[] array = new String[1];
                            array[0] = series;
                            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATION_SERIES_POSTPAID_NOT_VALID, array);
                            errors.add(resmsg);
                        }
                        if (interfaceCategoryMap_U.size() > 0) {
                            if (interfaceCategoryMap_U.get(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode()) != null) {
                                prefixListMap = (HashMap) interfaceCategoryMap_U.get(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode());
                                if (prefixListMap.containsKey(series)) {
                                    String[] array = new String[1];
                                    array[0] = series;
                                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATION_SERIES_POSTPAID_DUPLICATED, array);
                                    errors.add(resmsg);
                                } else {
                                    prefixListMap.put(series, series);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_U.put(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode(), prefixListMap);
                            }
                        } else {
                            prefixListMap = new HashMap();
                            prefixListMap.put(series, series);
                            interfaceCategoryMap_U.put(interfaceCategory + "_" + serviceSelectorInterfaceMapVO.getSelectorCode(), prefixListMap);
                        }
                    }
                }

            }


            if(!errors.isEmpty()){
                response.setErrorList(errors);
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.REMOVE_ERROR, 0, null);
            }
            //*************Validation code ends*************//




            for (int i = 0, j = requestVO.getServiceSelectorInterfaceMappingList().size(); i < j; i++) {
                serviceSelectorInterfaceMapVO = (ServiceSelectorInterfaceMapVO) requestVO.getServiceSelectorInterfaceMappingList().get(i);

                //Feild Validation Starts here
                if(serviceSelectorInterfaceMapVO.getSelectorCode().isEmpty() || BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getSelectorCode())){
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SELECTOR_CODE_REQ, 0, null);
                }

                if(serviceSelectorInterfaceMapVO.getServiceType().isEmpty() || BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getServiceType())){
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_REQ, 0, null);
                }

                if(serviceSelectorInterfaceMapVO.getInterfaceID().isEmpty() || BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getInterfaceID())){
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INTERFACE_ID_REQ, 0, null);
                }
                //Feild Validation ends here

                //feild validation, if entered correct or not, is being checked here  starts
                if (!serviceTypeSet.contains(serviceSelectorInterfaceMapVO.getServiceType())) {
                    // Throw an error if the given value is not in the set
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_SERVICE_TYPE, 0, null);
                }

                //feild validation , if entered correct or not, is being checked here ends

                // Inserting Validation Prepaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getValidatePrepaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapVO.getValidatePrepaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(PretupsI.INTERFACE_NETWORK_PREFIX_METHOD_TYPE_PRE);
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapVO.getInterfaceID());
                        if (log.isDebugEnabled()) {
                            log.debug("addServiceInterMapping", "key entered" + serviceSelectorInterfaceMappingVO1.getServiceType() + "_" + serviceSelectorInterfaceMappingVO1.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO1.getAction() + "_" + serviceSelectorInterfaceMappingVO1.getNetworkCode() + "_" + serviceSelectorInterfaceMappingVO1.getPrefixId());
                        }
                        serSelectorInterfaceMappingVO = ServiceSelectorInterfaceMappingCache.getObject(serviceSelectorInterfaceMappingVO1.getServiceType() + "_" + serviceSelectorInterfaceMappingVO1.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO1.getAction() + "_" + serviceSelectorInterfaceMappingVO1.getNetworkCode() + "_" + serviceSelectorInterfaceMappingVO1.getPrefixId());

                        if (serSelectorInterfaceMappingVO == null) {
                            seriesList.add(serviceSelectorInterfaceMappingVO1);
                        } else {
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.VALIDATE_PREPAID_SERIES_ALREADY_EXIST, 0, null);
                        }
                    }
                }

                // Inserting Validation Postpaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getValidatePostpaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapVO.getValidatePostpaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(PretupsI.INTERFACE_NETWORK_PREFIX_METHOD_TYPE_POST);
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapVO.getInterfaceID());
                        serSelectorInterfaceMappingVO = ServiceSelectorInterfaceMappingCache.getObject(serviceSelectorInterfaceMappingVO1.getServiceType() + "_" + serviceSelectorInterfaceMappingVO1.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO1.getAction() + "_" + serviceSelectorInterfaceMappingVO1.getNetworkCode() + "_" + serviceSelectorInterfaceMappingVO1.getPrefixId());
                        if (serSelectorInterfaceMappingVO == null) {
                            seriesList.add(serviceSelectorInterfaceMappingVO1);
                        } else {
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.VALIDATE_POSTPAID_SERIES_ALREADY_EXIST, 0, null);
                        }
                    }
                }

                // Inserting Update Postpaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getUpdatePrepaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapVO.getUpdatePrepaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(PretupsI.INTERFACE_NETWORK_PREFIX_METHOD_TYPE_PRE);
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapVO.getInterfaceID());
                        serSelectorInterfaceMappingVO = ServiceSelectorInterfaceMappingCache.getObject(serviceSelectorInterfaceMappingVO1.getServiceType() + "_" + serviceSelectorInterfaceMappingVO1.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO1.getAction() + "_" + serviceSelectorInterfaceMappingVO1.getNetworkCode() + "_" + serviceSelectorInterfaceMappingVO1.getPrefixId());
                        if (serSelectorInterfaceMappingVO == null) {
                            seriesList.add(serviceSelectorInterfaceMappingVO1);
                        } else {
                            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.UPDATE_PREPAID_SERIES_ALREADY_EXIST, 0, null);
                        }
                    }
                }

                // Inserting Update Postpaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapVO.getUpdatePostpaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapVO.getUpdatePostpaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(PretupsI.INTERFACE_NETWORK_PREFIX_METHOD_TYPE_POST);
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapVO.getInterfaceID());
                        serSelectorInterfaceMappingVO = ServiceSelectorInterfaceMappingCache.getObject(serviceSelectorInterfaceMappingVO1.getServiceType() + "_" + serviceSelectorInterfaceMappingVO1.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO1.getAction() + "_" + serviceSelectorInterfaceMappingVO1.getNetworkCode() + "_" + serviceSelectorInterfaceMappingVO1.getPrefixId());
                        if (serSelectorInterfaceMappingVO == null) {
                            seriesList.add(serviceSelectorInterfaceMappingVO1);
                        } else {
                           throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.UPDATE_POSTPAID_SERIES_ALREADY_EXIST, 0, null);
                        }

                    }
                }
            }

            if (seriesList != null && seriesList.size() > 0) {
                insertCount = serviceSelectorInterfaceMappingDAO.addPrdServiceInterfaceMapping(con, seriesList);
                if (insertCount > 0) {
                    mcomCon.finalCommit();

                    response.setStatus((HttpStatus.SC_OK));
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_ADDITION_SUCCESS, null);
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_ADDITION_SUCCESS);

                } else {
                    mcomCon.finalRollback();
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_ADDITION_FAIL, 0, null);
                }

            }
        }
         finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }






    //modify apis starts
    @Override
    public ServiceInterfaceMappingForModifyResponseVO getServiceInterfaceMappingForModify(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ServiceInterfaceMappingForModifyResponseVO response, String serviceType) throws BTSLBaseException {
        final String METHOD_NAME = "getServiceInterfaceMappingForModify";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }

        ServiceSelectorInterfaceMappingDAO serviceSelectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();

        //String serviceType = null;
        ArrayList tempList = new ArrayList();
        ServiceSelectorInterfaceMappingDAO selectorInterfaceMappingDAO = null;
        try{

            //validation starts here
            if(!isValidRequestString(serviceType)){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_SERVICE_TYPE, 0, null);
            }
            //validation ends here

            selectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
            ArrayList serviceTypeList = null;

            ArrayList serviceInterfaceMapList = serviceSelectorInterfaceMappingDAO.loadServiceInterfaceMappingRuleList(con, userVO.getNetworkID(), serviceType);

            // ------------load product list-------------
            ArrayList productList = serviceSelectorInterfaceMappingDAO.loadServiceProductList(con, serviceType);
            // load from the form serviceTypeList----------------------
            //loading serviceTypeList starts--------------------------
            String serviceTypes = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_INTFC_MAPPING_ALLOWED));
            if (BTSLUtil.isNullString(serviceTypes)) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CONFIGURATION_NOT_AVAILABLE_FOR_SERVICE, 0, null);
            }
            serviceTypeList = selectorInterfaceMappingDAO.loadServiceTypes(con, serviceTypes);
            //loading serviceTypeList ends----------------------------
            //ArrayList serviceTypeList = serviceSelectorInterfaceMappingForm.getServiceTypeList();
            // --check if already existing---
            ArrayList interfaceTypeList = serviceSelectorInterfaceMappingDAO.loadInterfaceForModify(con);
            if (productList == null || productList.size() == 0) {
                //throw new BTSLBaseException(this, "loadSrvInterfaceMappingForModify", "vastrix.msg.noservicelist.exists");
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SERVICE_EXISTS, 0, null);
            }
            response.setProductList(productList);

            if (serviceTypeList == null || serviceTypeList.size() == 0) {
                //throw new BTSLBaseException(this, "loadSrvInterfaceMappingForModify", "vastrix.msg.noproductlist.exists");
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_PRODUCT_EXIST, 0, null);
            }
            response.setServiceTypeList(serviceTypeList);

            if (interfaceTypeList == null || interfaceTypeList.size() == 0) {
                //throw new BTSLBaseException(this, "loadSrvInterfaceMappingForModify", "vastrix.msg.nointerfaceList.exists");
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_INTERFACES_EXIST, 0, null);
            }
            response.setInterfaceList(interfaceTypeList);

            NetworkWebDAO networkwebDAO = new NetworkWebDAO();
            ArrayList list = networkwebDAO.loadNetworkPrefix(con, userVO.getNetworkID());


            // /////////////This is just to load all the pre and post series to
            // check at validation of series
            if (list != null) {
                // this map is set on the form bean and used during save to know
                // thw prefix id of the series
                HashMap seriesMap = new HashMap();
                String seriesKey = null;

                HashMap map = new HashMap();
                NetworkPrefixVO myVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    myVO = (NetworkPrefixVO) list.get(i);

                    String key = myVO.getOperator() + "_" + myVO.getSeriesType();
                    if (map.containsKey(key)) {
                        String ser = (String) map.get(key);
                        ser += "," + myVO.getSeries();
                        map.put(key, ser);
                    } else {
                        map.put(key, myVO.getSeries());
                    }

                    // prepare a seriesMap that is used during save(Inserting
                    // Interface Network Prefix Mapping)
                    seriesKey = myVO.getNetworkCode() + "_" + myVO.getOperator() + "_" + myVO.getSeriesType() + "_" + myVO.getSeries();
                    seriesMap.put(seriesKey, String.valueOf(myVO.getPrefixID()));

                }
                /*
                 * When we fetch data from the DB it returns all the
                 * prepaidseries,postpaidseries
                 * and otherseries.
                 * if operator = OPT and seriesType = PREPAID its an prepaid
                 * series
                 * if operator = OPT and seriesType = POSTPAID its an postpaid
                 * series
                 * so here first we create an hash map which contains three key
                 * and there value
                 * like OPT_PREPIAD = 12345,34567 (PREPIAD SERIES)
                 * OPT_POSTPIAD = 12345,34567 (POSTPIAD SERIES)
                 */

                String temp = "";
                String temp1 = (String) map.get(PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_PREPAID);
                String temp2 = (String) map.get(PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_PREPAID);
                if (!BTSLUtil.isNullString(temp1) && BTSLUtil.isNullString(temp2)) {
                    temp = temp1;
                } else if (!BTSLUtil.isNullString(temp2) && BTSLUtil.isNullString(temp1)) {
                    temp = temp2;
                } else if (!BTSLUtil.isNullString(temp2) && !BTSLUtil.isNullString(temp1)) {
                    temp = temp1 + "," + temp2;
                }

                response.setPrepaidSeries(temp);
                temp = "";
                temp1 = null;
                temp2 = null;
                temp1 = (String) map.get(PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_POSTPAID);
                temp2 = (String) map.get(PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_POSTPAID);
                if (!BTSLUtil.isNullString(temp1) && BTSLUtil.isNullString(temp2)) {
                    temp = temp1;
                } else if (!BTSLUtil.isNullString(temp2) && BTSLUtil.isNullString(temp1)) {
                    temp = temp2;
                } else if (!BTSLUtil.isNullString(temp2) && !BTSLUtil.isNullString(temp1)) {
                    temp = temp1 + "," + temp2;
                }
                response.setPostpaidSeries(temp);
                // Change end for MNP
                response.setSeriesMap(seriesMap);
            }
            // /////////////END///////////////////////////////////////////////////////////
            if (serviceInterfaceMapList.isEmpty()) {
                //throw new BTSLBaseException(this, "loadSrvInterfaceMappingForModify", "vastrix.modify.serviceinterfacemapping.nodatafound", "loadPrdServiceTypesForModify");
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_DATA_FOUND, 0, null);
            }
            response.setNetworkName(userVO.getNetworkName());
            if (serviceInterfaceMapList.size() > 0) {
                ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO = null;
                String interfaceValidationKey = null;
                String interfaceUpdateKey = null;
                String prefixKey = null;
                String prefixValidationPreKey = null;
                String prefixValidationPostKey = null;
                String prefixUpdatePreKey = null;
                String prefixUpdatePostKey = null;
                ListValueVO listValueVO = null;
                prefixValidationPreKey = null;
                prefixValidationPostKey = null;
                prefixUpdatePreKey = null;
                prefixUpdatePostKey = null;
                String prevServiceProd = null, currServProd = null;
                ServiceSelectorInterfaceMappingVO servVO = null;

                for (int m = 0, n = serviceInterfaceMapList.size(); m < n; m++) {
                    serviceSelectorInterfaceMappingVO = (ServiceSelectorInterfaceMappingVO) serviceInterfaceMapList.get(m);
                    currServProd = serviceSelectorInterfaceMappingVO.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO.getInterfaceID();// +"_"+serviceSelectorInterfaceMappingVO.getAction()+"_"+serviceSelectorInterfaceMappingVO.getMethodType();
                    interfaceValidationKey = serviceSelectorInterfaceMappingVO.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO.getInterfaceID() + "_" + PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION;
                    interfaceUpdateKey = serviceSelectorInterfaceMappingVO.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO.getInterfaceID() + "_" + PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION;

                    prefixKey = serviceSelectorInterfaceMappingVO.getSelectorCode() + "_" + serviceSelectorInterfaceMappingVO.getInterfaceID() + "_" + serviceSelectorInterfaceMappingVO.getAction();

                    if (!currServProd.equals(prevServiceProd)) {
                        if (servVO != null) {
                            tempList.add(servVO);
                            prefixValidationPreKey = null;
                            prefixValidationPostKey = null;
                            prefixUpdatePreKey = null;
                            prefixUpdatePostKey = null;
                        }
                        servVO = serviceSelectorInterfaceMappingVO;
                        if (prefixKey.equals(interfaceValidationKey)) {
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(servVO.getMethodType())) {
                                if (prefixValidationPreKey == null) {
                                    prefixValidationPreKey = servVO.getPrefixSeries();
                                } else {
                                    prefixValidationPreKey += "," + servVO.getPrefixSeries();
                                }
                            } else if (PretupsI.SERIES_TYPE_POSTPAID.equals(servVO.getMethodType())) {
                                if (prefixValidationPostKey == null) {
                                    prefixValidationPostKey = servVO.getPrefixSeries();
                                } else {
                                    prefixValidationPostKey += "," + servVO.getPrefixSeries();
                                }
                            }
                        }

                        else if (prefixKey.equals(interfaceUpdateKey)) {
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(servVO.getMethodType())) {
                                if (prefixUpdatePreKey == null) {
                                    prefixUpdatePreKey = servVO.getPrefixSeries();
                                } else {
                                    prefixUpdatePreKey += "," + servVO.getPrefixSeries();
                                }
                            } else if (PretupsI.SERIES_TYPE_POSTPAID.equals(servVO.getMethodType())) {
                                if (prefixUpdatePostKey == null) {
                                    prefixUpdatePostKey = servVO.getPrefixSeries();
                                } else {
                                    prefixUpdatePostKey += "," + servVO.getPrefixSeries();
                                }
                            }
                        }
                        servVO.setValidatePrepaidSeries(prefixValidationPreKey);
                        servVO.setValidatePostpaidSeries(prefixValidationPostKey);
                        servVO.setUpdatePrepaidSeries(prefixUpdatePreKey);
                        servVO.setUpdatePostpaidSeries(prefixUpdatePostKey);

                        if (servVO.getInterfaceID() != null) {
                            for (int k = 0, l = interfaceTypeList.size(); k < l; k++) {
                                listValueVO = (ListValueVO) interfaceTypeList.get(k);
                                if (listValueVO.getValue().equals(servVO.getInterfaceID())) {
                                    servVO.setInterfaceName(listValueVO.getLabel());
                                    servVO.setInterfaceID(listValueVO.getValue());
                                    break;
                                }
                            }
                        }
                        if (servVO.getSelectorCode() != null) {
                            for (int k = 0, l = productList.size(); k < l; k++) {
                                listValueVO = (ListValueVO) productList.get(k);
                                if (listValueVO.getValue().equals(servVO.getSelectorCode())) {
                                    servVO.setSelectorName(listValueVO.getLabel());
                                    servVO.setSelectorCode(listValueVO.getValue());
                                    break;
                                }
                            }
                        }
                        if (servVO.getServiceType() != null) {
                            for (int k = 0, l = serviceTypeList.size(); k < l; k++) {
                                listValueVO = (ListValueVO) serviceTypeList.get(k);

                                if (listValueVO.getValue().equals(servVO.getServiceType())) {
                                    servVO.setServiceName(listValueVO.getLabel());
                                    servVO.setServiceType(listValueVO.getValue());
                                    break;
                                }
                            }
                        }

                    } else {
                        if (prefixKey.equals(interfaceValidationKey)) {
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(serviceSelectorInterfaceMappingVO.getMethodType())) {
                                if (prefixValidationPreKey == null) {
                                    prefixValidationPreKey = serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                } else {
                                    prefixValidationPreKey += "," + serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                }
                                servVO.setValidatePrepaidSeries(prefixValidationPreKey);
                            } else if (PretupsI.SERIES_TYPE_POSTPAID.equals(serviceSelectorInterfaceMappingVO.getMethodType())) {
                                if (prefixValidationPostKey == null) {
                                    prefixValidationPostKey = serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                } else {
                                    prefixValidationPostKey += "," + serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                }
                                servVO.setValidatePostpaidSeries(prefixValidationPostKey);
                            }
                        }

                        else if (prefixKey.equals(interfaceUpdateKey)) {
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(serviceSelectorInterfaceMappingVO.getMethodType())) {
                                if (prefixUpdatePreKey == null) {
                                    prefixUpdatePreKey = serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                } else {
                                    prefixUpdatePreKey += "," + serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                }
                                servVO.setUpdatePrepaidSeries(prefixUpdatePreKey);
                            } else if (PretupsI.SERIES_TYPE_POSTPAID.equals(serviceSelectorInterfaceMappingVO.getMethodType())) {
                                if (prefixUpdatePostKey == null) {
                                    prefixUpdatePostKey = serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                } else {
                                    prefixUpdatePostKey += "," + serviceSelectorInterfaceMappingVO.getPrefixSeries();
                                }
                                servVO.setUpdatePostpaidSeries(prefixUpdatePostKey);
                            }
                        }

                    }

                    prevServiceProd = currServProd;
                }
                tempList.add(servVO);
                response.setServiceSelectorInterfaceMapVOList(tempList);




                response.setStatus((HttpStatus.SC_OK));
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);

            }

        }
        finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }






    @Override
    public ModifyServiceInterfaceMappingResponseVO modifyServiceInterfaceMapping(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ModifyServiceInterfaceMappingResponseVO response, ModifyServiceInterfaceMappingRequestVO requestVO) throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "modifyServiceInterfaceMapping";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }

        ServiceSelectorInterfaceMappingDAO serviceSelectorInterfaceMappingDAO = null;

        ServiceSelectorInterfaceMapModifyVO serviceSelectorInterfaceMapModifyVO = null;
        int updateCount = 0, updateRequired = 0;// this variable is used to
        // check the actual value of
        // updateCount.
        ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO1 = null;
        StringTokenizer value = null;
        String series = null;
        String prefixID = null;
        Date currentDate = new Date();

        ServiceSelectorInterfaceMappingDAO selectorInterfaceMappingDAO = null;
        ListValueVO listValueVO = null;

        try{
            serviceSelectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
            HashMap seriesMap = requestVO.getSeriesMap();
            String seriesKey = null;
            ArrayList seriesList = new ArrayList();
            ServiceSelectorInterfaceMappingCache.updateServSelInterfMapping();

            //code for checking valid interface ID and serviceType starts

            selectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
            ArrayList serviceTypeList = null;
            listValueVO = new ListValueVO();

            String serviceTypes = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_INTFC_MAPPING_ALLOWED));
            if (BTSLUtil.isNullString(serviceTypes)) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CONFIGURATION_NOT_AVAILABLE_FOR_SERVICE, 0, null);
            }
            serviceTypeList = selectorInterfaceMappingDAO.loadServiceTypes(con, serviceTypes);
            ArrayList interfaceTypeList = serviceSelectorInterfaceMappingDAO.loadInterfaceForModify(con);



            Set<String> serviceTypeSet = new HashSet<>();
            Set<String> interfaceTypeSet = new HashSet<>();

            // Populate the set with values from the data
            for (int i=0; i< serviceTypeList.size(); i++) {
                listValueVO = (ListValueVO) serviceTypeList.get(i);
                if (!serviceTypeSet.add(listValueVO.getValue())) {
                    String args[] = { listValueVO.getValue() };
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DUPLICATE_VALUE, 0, args , null);
                }
            }

            for (int i=0; i< interfaceTypeList.size(); i++) {
                listValueVO = (ListValueVO) interfaceTypeList.get(i);
                if (!interfaceTypeSet.add(listValueVO.getValue())) {
                    String args[] = { listValueVO.getValue() };
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DUPLICATE_VALUE, 0, args , null);
                }
            }


            //code for checking valid interface ID and serviceType ends



            //validation starts here

            if(requestVO.getPrepaidSeries().isEmpty() || BTSLUtil.isNullString(requestVO.getPrepaidSeries())){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PREPAID_SERIES_REQ, 0, null);
            }

            if(requestVO.getPostpaidSeries().isEmpty() || BTSLUtil.isNullString(requestVO.getPostpaidSeries())){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.POSTPAID_SERIES_REQ, 0, null);
            }

            if(requestVO.getSeriesMap().isEmpty()){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SERIES_MAP_REQ, 0, null);
            }

            ArrayList errors = new ArrayList();

            /*
             * This map contains the prepaid series of a particular network
             * used to check the validation prepaid and update prepaid
             * series
             * belongs to the prepaid series of that network
             */
            HashMap prepaidSeriesMap = new HashMap();

            StringTokenizer prepaidSeriesValue = new StringTokenizer(requestVO.getPrepaidSeries(), ",");
            while (prepaidSeriesValue.hasMoreTokens()) {
                series = (String) prepaidSeriesValue.nextToken();
                prepaidSeriesMap.put(series, series);
            }

            /*
             * This map contains the postpaid series of a particular network
             * used to check the validation postpaid and update postpaid
             * series
             * belongs to the postpaid series of that network
             */
            HashMap postpaidSeriesMap = new HashMap();
            series = null;

            StringTokenizer postpaidSeriesValue = new StringTokenizer(requestVO.getPostpaidSeries(), ",");
            while (postpaidSeriesValue.hasMoreTokens()) {
                series = (String) postpaidSeriesValue.nextToken();
                postpaidSeriesMap.put(series, series);
            }

            //StringTokenizer value = null;
            String interfaceCategory = null;
            HashMap interfaceCategoryMap_V = new HashMap();
            HashMap prefixListMap = null;
            HashMap interfaceCategoryMap_U = new HashMap();



            for (int i = 0, j = requestVO.getServiceSelectorInterfaceMappingList().size(); i < j; i++) {
                serviceSelectorInterfaceMapModifyVO = (ServiceSelectorInterfaceMapModifyVO) requestVO.getServiceSelectorInterfaceMappingList().get(i);
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getSelectorCode()) || !BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getInterfaceID()) || !BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getValidatePrepaidSeries()) || !BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getValidatePostpaidSeries()) || !BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getUpdatePrepaidSeries()) || !BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getUpdatePostpaidSeries())) {
                    String[] arr = new String[1];
                    arr[0] = String.valueOf(i + 1);
                    if (BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getSelectorCode())) {
                        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ERROR_SELECTOR_CODE, null);
                        errors.add(resmsg);
                    }

                    if (BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getInterfaceID())) {
                        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INTERFACE_ID_REQUIRED, null);
                        errors.add(resmsg);
                    }

                    if (BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getValidatePrepaidSeries()) && BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getValidatePostpaidSeries()) && BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getUpdatePrepaidSeries()) && BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getUpdatePostpaidSeries())) {
                        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VALIDATE_OR_UPDATE_SERIES_REQUIRED, null);
                        errors.add(resmsg);
                    }
                }
            }

            for (int i = 0, j = requestVO.getServiceSelectorInterfaceMappingList().size(); i < j; i++) {
                serviceSelectorInterfaceMapModifyVO = (ServiceSelectorInterfaceMapModifyVO) requestVO.getServiceSelectorInterfaceMappingList().get(i);
                interfaceCategory = serviceSelectorInterfaceMapModifyVO.getMethodType();

                /*
                 * 1)check for validation pre series belongs to the
                 * prepaidseries or not
                 * 2)check for validation pre series should not be
                 * duplicated(series unique to all interfaces)
                 */
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getValidatePrepaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapModifyVO.getValidatePrepaidSeries(), ",");
                    while (value.hasMoreTokens()) {
                        series = (String) value.nextToken();
                        // 1
                        if (!prepaidSeriesMap.containsKey(series)) {
                            String[] array = new String[1];
                            array[0] = series;
                            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VALIDATION_SERIES_PREPAID_NOT_VALID, array);
                            errors.add(resmsg);
                        }
                        if (interfaceCategoryMap_V.size() > 0) {
                            if (interfaceCategoryMap_V.get(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode()) != null) {
                                prefixListMap = (HashMap) interfaceCategoryMap_V.get(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode());
                                if (prefixListMap.containsKey(series)) {
                                    String[] array = new String[1];
                                    array[0] = series;
                                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VALIDATION_SERIES_PREPAID_DUPLICATED, array);
                                    errors.add(resmsg);
                                } else {
                                    prefixListMap.put(series, series);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_V.put(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode(), prefixListMap);
                            }
                        } else {
                            prefixListMap = new HashMap();
                            prefixListMap.put(series, series);
                            interfaceCategoryMap_V.put(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode(), prefixListMap);
                        }
                    }
                }

                /*
                 * 1)check for validation post series belongs to the
                 * prepaidseries or not
                 * 2)check for validation post series should not be
                 * duplicated(series unique to all interfaces)
                 */
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getValidatePostpaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapModifyVO.getValidatePostpaidSeries(), ",");
                    while (value.hasMoreTokens()) {
                        series = (String) value.nextToken();
                        // 1
                        if (!postpaidSeriesMap.containsKey(series)) {
                            String[] array = new String[1];
                            array[0] = series;
                            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VALIDATION_SERIES_POSTPAID_NOT_VALID, array);
                            errors.add(resmsg);
                        }
                        if (interfaceCategoryMap_V.size() > 0) {
                            if (interfaceCategoryMap_V.get(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode()) != null) {
                                prefixListMap = (HashMap) interfaceCategoryMap_V.get(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode());
                                if (prefixListMap.containsKey(series)) {
                                    String[] array = new String[1];
                                    array[0] = series;
                                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VALIDATION_SERIES_POSTPAID_DUPLICATED, array);
                                    errors.add(resmsg);
                                } else {
                                    prefixListMap.put(series, series);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_V.put(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode(), prefixListMap);
                            }
                        } else {
                            prefixListMap = new HashMap();
                            prefixListMap.put(series, series);
                            interfaceCategoryMap_V.put(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode(), prefixListMap);
                        }
                    }
                }

                /*
                 * 1)check for update pre series belongs to the
                 * prepaidseries or not
                 * 2)check for update pre series should not be
                 * duplicated(series unique to all interfaces)
                 */
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getUpdatePrepaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapModifyVO.getUpdatePrepaidSeries(), ",");
                    while (value.hasMoreTokens()) {
                        series = (String) value.nextToken();
                        // 1
                        if (!prepaidSeriesMap.containsKey(series)) {
                            String[] array = new String[1];
                            array[0] = series;
                            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATION_SERIES_PREPAID_NOT_VALID, array);
                            errors.add(resmsg);
                        }
                        if (interfaceCategoryMap_U.size() > 0) {
                            if (interfaceCategoryMap_U.get(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode()) != null) {
                                prefixListMap = (HashMap) interfaceCategoryMap_U.get(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode());
                                if (prefixListMap.containsKey(series)) {
                                    String[] array = new String[1];
                                    array[0] = series;
                                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATION_SERIES_PREPAID_DUPLICATED, array);
                                    errors.add(resmsg);
                                } else {
                                    prefixListMap.put(series, series);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_U.put(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode(), prefixListMap);
                            }
                        } else {
                            prefixListMap = new HashMap();
                            prefixListMap.put(series, series);
                            interfaceCategoryMap_U.put(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode(), prefixListMap);
                        }
                    }
                }

                /*
                 * 1)check for update post series belongs to the
                 * prepaidseries or not
                 * 2)check for update post series should not be
                 * duplicated(series unique to all interfaces)
                 */
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getUpdatePostpaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapModifyVO.getUpdatePostpaidSeries(), ",");
                    while (value.hasMoreTokens()) {
                        series = (String) value.nextToken();

                        // 1
                        if (!postpaidSeriesMap.containsKey(series)) {
                            String[] array = new String[1];
                            array[0] = series;
                            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATION_SERIES_POSTPAID_NOT_VALID, array);
                            errors.add(resmsg);
                        }
                        if (interfaceCategoryMap_U.size() > 0) {
                            if (interfaceCategoryMap_U.get(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode()) != null) {
                                prefixListMap = (HashMap) interfaceCategoryMap_U.get(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode());
                                if (prefixListMap.containsKey(series)) {
                                    String[] array = new String[1];
                                    array[0] = series;
                                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPDATION_SERIES_POSTPAID_DUPLICATED, array);
                                    errors.add(resmsg);
                                } else {
                                    prefixListMap.put(series, series);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_U.put(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode(), prefixListMap);
                            }
                        } else {
                            prefixListMap = new HashMap();
                            prefixListMap.put(series, series);
                            interfaceCategoryMap_U.put(interfaceCategory + "_" + serviceSelectorInterfaceMapModifyVO.getSelectorCode(), prefixListMap);
                        }
                    }
                }
            }



            if(!errors.isEmpty()){
                response.setErrorList(errors);
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.REMOVE_ERROR, 0, null);
            }
            //validation ends here





            for (int i = 0, j = requestVO.getServiceSelectorInterfaceMappingList().size(); i < j; i++) {
                serviceSelectorInterfaceMapModifyVO = (ServiceSelectorInterfaceMapModifyVO) requestVO.getServiceSelectorInterfaceMappingList().get(i);
                seriesList.clear();

                //Feild Validation Starts here
                if(serviceSelectorInterfaceMapModifyVO.getSelectorCode().isEmpty() || BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getSelectorCode())){
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SELECTOR_CODE_REQ, 0, null);
                }

                if(serviceSelectorInterfaceMapModifyVO.getServiceType().isEmpty() || BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getServiceType())){
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_REQ, 0, null);
                }

                if(serviceSelectorInterfaceMapModifyVO.getInterfaceID().isEmpty() || BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getInterfaceID())){
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INTERFACE_ID_REQ, 0, null);
                }
                //Feild Validation ends here

                //feild validation, if entered correct or not, is being checked here  starts
                if (!serviceTypeSet.contains(serviceSelectorInterfaceMapModifyVO.getServiceType())) {
                    // Throw an error if the given value is not in the set
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_SERVICE_TYPE, 0, null);
                }

                if (!interfaceTypeSet.contains(serviceSelectorInterfaceMapModifyVO.getInterfaceID())) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_INTERFACE_ID, 0, null);
                }
                //feild validation , if entered correct or not, is being checked here ends

                // Updating Validation Prepaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getValidatePrepaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapModifyVO.getValidatePrepaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(PretupsI.INTERFACE_NETWORK_PREFIX_METHOD_TYPE_PRE);
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapModifyVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapModifyVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapModifyVO.getInterfaceID());
                        serviceSelectorInterfaceMappingVO1.setServiceInterfaceMappngID(serviceSelectorInterfaceMapModifyVO.getServiceInterfaceMappngID());
                        seriesList.add(serviceSelectorInterfaceMappingVO1);
                    }
                }

                // Inserting Validation Postpaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getValidatePostpaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapModifyVO.getValidatePostpaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(PretupsI.INTERFACE_NETWORK_PREFIX_METHOD_TYPE_POST);
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapModifyVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapModifyVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapModifyVO.getInterfaceID());
                        serviceSelectorInterfaceMappingVO1.setServiceInterfaceMappngID(serviceSelectorInterfaceMapModifyVO.getServiceInterfaceMappngID());
                        seriesList.add(serviceSelectorInterfaceMappingVO1);

                    }
                }

                // Inserting Update Postpaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getUpdatePrepaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapModifyVO.getUpdatePrepaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(PretupsI.INTERFACE_NETWORK_PREFIX_METHOD_TYPE_PRE);
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapModifyVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapModifyVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapModifyVO.getInterfaceID());
                        serviceSelectorInterfaceMappingVO1.setServiceInterfaceMappngID(serviceSelectorInterfaceMapModifyVO.getServiceInterfaceMappngID());
                        seriesList.add(serviceSelectorInterfaceMappingVO1);
                    }
                }

                // Inserting Update Postpaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapModifyVO.getUpdatePostpaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapModifyVO.getUpdatePostpaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(PretupsI.INTERFACE_NETWORK_PREFIX_METHOD_TYPE_POST);
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapModifyVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapModifyVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapModifyVO.getInterfaceID());
                        serviceSelectorInterfaceMappingVO1.setServiceInterfaceMappngID(serviceSelectorInterfaceMapModifyVO.getServiceInterfaceMappngID());
                        seriesList.add(serviceSelectorInterfaceMappingVO1);

                    }

                }

                if (serviceSelectorInterfaceMapModifyVO.getMultiBox() != null && serviceSelectorInterfaceMapModifyVO.getMultiBox().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                    updateRequired++;
                    updateCount += serviceSelectorInterfaceMappingDAO.updatePrdServiceInterfaceMapping(con, seriesList);
                }
            }
            if (con != null) {
                if (updateCount == updateRequired) {
                    mcomCon.finalCommit();
                    response.setStatus((HttpStatus.SC_OK));
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_MODIFICATION_SUCCESS, null);
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_MODIFICATION_SUCCESS);
                } else {
                    mcomCon.finalRollback();
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_MODIFICATION_FAIL, 0, null);
                }
            }

        }
        finally{
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }





    @Override
    public BaseResponse deleteServiceInterfaceMapping(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response, DeleteServiceInterfaceMappingRequestVO requestVO) throws SQLException, BTSLBaseException {
        final String METHOD_NAME = "modifyServiceInterfaceMapping";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }

        ServiceSelectorInterfaceMappingDAO serviceSelectorInterfaceMappingDAO = null;

        ServiceSelectorInterfaceMapDeleteVO serviceSelectorInterfaceMapDeleteVO = null;

        ServiceSelectorInterfaceMappingVO serviceSelectorInterfaceMappingVO1 = null;
        int delCount = 0;// this variable is used to check the actual value of
        // delCount.
        StringTokenizer value = null;
        String series = null;
        String prefixID = null;
        Date currentDate = new Date();
        String seriesKey = null;
        ArrayList seriesList = new ArrayList();

        HashMap seriesMap = requestVO.getSeriesMap();

        ServiceSelectorInterfaceMappingDAO selectorInterfaceMappingDAO = null;
        ListValueVO listValueVO = null;

        try{

            //Validation for feilds starts
            if(requestVO.getSeriesMap().isEmpty()){
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SERIES_MAP_REQ, 0, null);
            }


            //validation for feild ends

            //code for checking valid interface ID and serviceType starts

            selectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();
            ArrayList serviceTypeList = null;
            listValueVO = new ListValueVO();

            String serviceTypes = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_INTFC_MAPPING_ALLOWED));
            if (BTSLUtil.isNullString(serviceTypes)) {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.CONFIGURATION_NOT_AVAILABLE_FOR_SERVICE, 0, null);
            }
            serviceTypeList = selectorInterfaceMappingDAO.loadServiceTypes(con, serviceTypes);
            ArrayList interfaceTypeList = selectorInterfaceMappingDAO.loadInterfaceForModify(con);



            Set<String> serviceTypeSet = new HashSet<>();
            Set<String> interfaceTypeSet = new HashSet<>();

            // Populate the set with values from the data
            for (int i=0; i< serviceTypeList.size(); i++) {
                listValueVO = (ListValueVO) serviceTypeList.get(i);
                if (!serviceTypeSet.add(listValueVO.getValue())) {
                    String args[] = { listValueVO.getValue() };
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DUPLICATE_VALUE, 0, args , null);
                }
            }

            for (int i=0; i< interfaceTypeList.size(); i++) {
                listValueVO = (ListValueVO) interfaceTypeList.get(i);
                if (!interfaceTypeSet.add(listValueVO.getValue())) {
                    String args[] = { listValueVO.getValue() };
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DUPLICATE_VALUE, 0, args , null);
                }
            }


            //code for checking valid interface ID and serviceType ends

            serviceSelectorInterfaceMappingDAO = new ServiceSelectorInterfaceMappingDAO();

            for (int i = 0, j = requestVO.getServiceSelectorInterfaceMappingList().size(); i < j; i++) {
                serviceSelectorInterfaceMapDeleteVO = (ServiceSelectorInterfaceMapDeleteVO) requestVO.getServiceSelectorInterfaceMappingList().get(i);
                seriesList.clear();

                //feild validation starts
                if(serviceSelectorInterfaceMapDeleteVO.getSelectorCode().isEmpty() || BTSLUtil.isNullString(serviceSelectorInterfaceMapDeleteVO.getSelectorCode())){
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SELECTOR_CODE_REQ, 0, null);
                }

                if(serviceSelectorInterfaceMapDeleteVO.getServiceType().isEmpty() || BTSLUtil.isNullString(serviceSelectorInterfaceMapDeleteVO.getServiceType())){
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SERVICE_TYPE_REQ, 0, null);
                }

                if(serviceSelectorInterfaceMapDeleteVO.getInterfaceID().isEmpty() || BTSLUtil.isNullString(serviceSelectorInterfaceMapDeleteVO.getInterfaceID())){
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INTERFACE_ID_REQ, 0, null);
                }
                //feild validation ends

                //feild validation, if entered correct or not, is being checked here  starts
                if (!serviceTypeSet.contains(serviceSelectorInterfaceMapDeleteVO.getServiceType())) {
                    // Throw an error if the given value is not in the set
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_SERVICE_TYPE, 0, null);
                }

                if (!interfaceTypeSet.contains(serviceSelectorInterfaceMapDeleteVO.getInterfaceID())) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_INTERFACE_ID, 0, null);
                }
                //feild validation , if entered correct or not, is being checked here ends

                // deleting Validation Prepaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapDeleteVO.getValidatePrepaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapDeleteVO.getValidatePrepaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(serviceSelectorInterfaceMapDeleteVO.getMethodType());
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapDeleteVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapDeleteVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapDeleteVO.getInterfaceID());
                        seriesList.add(serviceSelectorInterfaceMappingVO1);
                    }
                }

                // deleting Validation Postpaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapDeleteVO.getValidatePostpaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapDeleteVO.getValidatePostpaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(serviceSelectorInterfaceMapDeleteVO.getMethodType());
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapDeleteVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapDeleteVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapDeleteVO.getInterfaceID());
                        seriesList.add(serviceSelectorInterfaceMappingVO1);

                    }
                }

                // deleting Update Postpaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapDeleteVO.getUpdatePrepaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapDeleteVO.getUpdatePrepaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(serviceSelectorInterfaceMapDeleteVO.getMethodType());
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapDeleteVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapDeleteVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapDeleteVO.getInterfaceID());
                        seriesList.add(serviceSelectorInterfaceMappingVO1);
                    }
                }

                // deleting Update Postpaid series
                if (!BTSLUtil.isNullString(serviceSelectorInterfaceMapDeleteVO.getUpdatePostpaidSeries())) {
                    value = new StringTokenizer(serviceSelectorInterfaceMapDeleteVO.getUpdatePostpaidSeries(), ",");
                    while (value.hasMoreElements()) {
                        series = value.nextToken();
                        serviceSelectorInterfaceMappingVO1 = new ServiceSelectorInterfaceMappingVO();
                        serviceSelectorInterfaceMappingVO1.setNetworkCode(userVO.getNetworkID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + series;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        serviceSelectorInterfaceMappingVO1.setPrefixID(Integer.parseInt(prefixID));
                        serviceSelectorInterfaceMappingVO1.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                        serviceSelectorInterfaceMappingVO1.setMethodType(serviceSelectorInterfaceMapDeleteVO.getMethodType());
                        serviceSelectorInterfaceMappingVO1.setCreatedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setModifiedBy(userVO.getUserID());
                        serviceSelectorInterfaceMappingVO1.setCreatedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setModifiedOn(currentDate);
                        serviceSelectorInterfaceMappingVO1.setServiceType(serviceSelectorInterfaceMapDeleteVO.getServiceType());
                        serviceSelectorInterfaceMappingVO1.setSelectorCode(serviceSelectorInterfaceMapDeleteVO.getSelectorCode());
                        serviceSelectorInterfaceMappingVO1.setInterfaceID(serviceSelectorInterfaceMapDeleteVO.getInterfaceID());
                        seriesList.add(serviceSelectorInterfaceMappingVO1);

                    }
                }


                delCount += serviceSelectorInterfaceMappingDAO.deletePrdServiceInterfaceMapping(con, seriesList);
                
            }
            if (con != null) {
                if (delCount > 0) {
                    mcomCon.finalCommit();
                    response.setStatus((HttpStatus.SC_OK));
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_DELETION_SUCCESS, null);
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_DELETION_SUCCESS);
                } else {
                    mcomCon.finalRollback();
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_DELETION_FAIL, 0, null);

                }
            }
        }
        finally{
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }


        return response;
    }




    public static boolean isValidRequestString(String requestString) {
        // Defining the regex pattern for alphanumeric characters

        String pattern = Constants.getProperty("NAME_REGEX_ALPHANUMERIC");

        // Checking if the serviceType matches the pattern
        return requestString.matches(pattern);
    }


}