package com.restapi.networkadmin.interfacePrefix;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkMappingDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkMappingVO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkPrefixMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.network.businesslogic.NetworkWebDAO;

@Service("InterfacePrefixServiceI")
public class InterfacePrefixServiceImpl implements InterfacePrefixServiceI{
	
	public static final Log log = LogFactory.getLog(InterfacePrefixServiceImpl.class.getName());
	public static final String classname = "InterfacePrefixServiceImpl";

	@Override
	public InterfaceNetworkMappingPrefixListResponseVO getInterfaceNetworkMappingPrefixList(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, InterfaceNetworkMappingPrefixListResponseVO response) throws BTSLBaseException {
		
		final String METHOD_NAME = "getInterfaceNetworkMappingPrefixList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		
		try {
			NetworkWebDAO networkwebDAO = new NetworkWebDAO();
			
			ArrayList list = networkwebDAO.loadNetworkPrefix(con, userVO.getNetworkID());

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
                    // Change done for MNP (30/04/07)
                    // Changed by ved
                    // Change start for MNP
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
            }
            
            InterfaceNetworkMappingDAO interfaceNetworkMappingDAO = new InterfaceNetworkMappingDAO();

            // load intearface list from interface_network_mapping table
            ArrayList interfaceList = interfaceNetworkMappingDAO.loadInterfaceNetworkMappingList(con, userVO.getNetworkID(), PretupsI.INTERFACE_CATEGORY);
            response.setInterfaceList(interfaceList);

            // load interface network mapping prefix from
            // intf_ntwrk_prfx_mapping table
            ArrayList networkInterfacePrefixList = interfaceNetworkMappingDAO.loadInterfaceNetworkPrefix(con, userVO.getNetworkID());

            /*
             * prepare the validation and update series
             * interfaceList contains InterfaceNetworkMappingVO
             * networkInterfacePrefixList contains
             * InterfaceNetworkPrefixMappingVO
             * 
             * Here we make a key with
             * interfaceId_V_PRE (means this belongs to Validation Prepaid)
             * interfaceId_V_POST (means this belongs to Validation Postpaid)
             * interfaceId_U_PRE (means this belongs to Update Prepaid)
             * interfaceId_U_POST (means this belongs to Update Postpaid)
             */
            if (interfaceList != null && !interfaceList.isEmpty() && networkInterfacePrefixList != null && !networkInterfacePrefixList.isEmpty()) {
                InterfaceNetworkMappingVO interfaceNetworkMappingVO = null;
                InterfaceNetworkPrefixMappingVO interfaceNetworkPrefixMappingVO = null;
                String interfaceValidationKey = null;
                String interfaceUpdateKey = null;
                String prefixKey = null;
                String prefixValidationPreKey = null;
                String prefixValidationPostKey = null;
                String prefixUpdatePreKey = null;
                String prefixUpdatePostKey = null;

                for (int i = 0, j = interfaceList.size(); i < j; i++) {
                    interfaceNetworkMappingVO = (InterfaceNetworkMappingVO) interfaceList.get(i);

                    interfaceValidationKey = interfaceNetworkMappingVO.getInterfaceID() + "_" + PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION;
                    interfaceUpdateKey = interfaceNetworkMappingVO.getInterfaceID() + "_" + PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION;
                    prefixValidationPreKey = null;
                    prefixValidationPostKey = null;
                    prefixUpdatePreKey = null;
                    prefixUpdatePostKey = null;

                    for (int m = 0, n = networkInterfacePrefixList.size(); m < n; m++) {
                        interfaceNetworkPrefixMappingVO = (InterfaceNetworkPrefixMappingVO) networkInterfacePrefixList.get(m);
                        prefixKey = interfaceNetworkPrefixMappingVO.getInterfaceID() + "_" + interfaceNetworkPrefixMappingVO.getAction();

                        if (prefixKey.equals(interfaceValidationKey)) {
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(interfaceNetworkPrefixMappingVO.getSeriesType())) {
                                if (prefixValidationPreKey == null) {
                                    prefixValidationPreKey = interfaceNetworkPrefixMappingVO.getSeries();
                                } else {
                                    prefixValidationPreKey += "," + interfaceNetworkPrefixMappingVO.getSeries();
                                }
                            } else if (PretupsI.SERIES_TYPE_POSTPAID.equals(interfaceNetworkPrefixMappingVO.getSeriesType())) {
                                if (prefixValidationPostKey == null) {
                                    prefixValidationPostKey = interfaceNetworkPrefixMappingVO.getSeries();
                                } else {
                                    prefixValidationPostKey += "," + interfaceNetworkPrefixMappingVO.getSeries();
                                }
                            }
                        } else if (prefixKey.equals(interfaceUpdateKey)) {
                            if (PretupsI.SERIES_TYPE_PREPAID.equals(interfaceNetworkPrefixMappingVO.getSeriesType())) {
                                if (prefixUpdatePreKey == null) {
                                    prefixUpdatePreKey = interfaceNetworkPrefixMappingVO.getSeries();
                                } else {
                                    prefixUpdatePreKey += "," + interfaceNetworkPrefixMappingVO.getSeries();
                                }
                            } else if (PretupsI.SERIES_TYPE_POSTPAID.equals(interfaceNetworkPrefixMappingVO.getSeriesType())) {
                                if (prefixUpdatePostKey == null) {
                                    prefixUpdatePostKey = interfaceNetworkPrefixMappingVO.getSeries();
                                } else {
                                    prefixUpdatePostKey += "," + interfaceNetworkPrefixMappingVO.getSeries();
                                }
                            }
                        }
                    }
                    interfaceNetworkMappingVO.setValidatePrepaidSeries(prefixValidationPreKey);
                    interfaceNetworkMappingVO.setValidatePostpaidSeries(prefixValidationPostKey);
                    interfaceNetworkMappingVO.setUpdatePrepaidSeries(prefixUpdatePreKey);
                    interfaceNetworkMappingVO.setUpdatePostpaidSeries(prefixUpdatePostKey);
                }
            }
//            else {
//            	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND);
//            }
            response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
		}finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		
		return response;
	}
	
	
	
	
	

	@Override
	public SaveInterfaceNetworkMappingPrefixResponseVO saveInterfaceNetworkMappingPrefix(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, SaveInterfaceNetworkMappingPrefixResponseVO response,
			SaveInterfaceNetworkMappingPrefixRequestVO saveInterfaceNetworkMappingPrefixRequestVO) throws BTSLBaseException, SQLException {
		
		final String METHOD_NAME = "saveInterfaceNetworkMappingPrefix";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		int insertCount = 0;
		
		try {
			InterfaceNetworkMappingDAO interfaceNetworkMappingDAO = new InterfaceNetworkMappingDAO();
			NetworkWebDAO networkwebDAO = new NetworkWebDAO();
			/*
             * This map is constructed in the
             * loadInterfaceNetworkMappingPrefixList method
             * here we fetch the prefix_id of the entered series
             */
			
			ArrayList list = networkwebDAO.loadNetworkPrefix(con, userVO.getNetworkID());

			
			HashMap seriesMap = new HashMap();
            String seriesKey = null;
            if (list != null) {
            	// this map is set on the form bean and used during save to know
                // the prefix id of the series
                
                
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
            }
            
            
            
            
            //**********validation starts************//
            
            ArrayList errors = new ArrayList();
            if (saveInterfaceNetworkMappingPrefixRequestVO.getInterfaceList() != null && errors.isEmpty()) {
                /*
                 * This map contains the prepaid series of a particular network
                 * used to check the validation prepaid and update prepaid
                 * series
                 * belongs to the prepaid series of that network
                 */
                HashMap prepaidSeriesMap = new HashMap();
                String series = null;

               

                /*
                 * This map contains the postpaid series of a particular network
                 * used to check the validation postpaid and update postpaid
                 * series
                 * belongs to the postpaid series of that network
                 */
                HashMap postpaidSeriesMap = new HashMap();
                series = null;

                

                InterfaceNetworkMappingVO interfaceNetworkMappingVO = null;
                StringTokenizer value = null;
                String interfaceCategory = null;

                HashMap interfaceCategoryMap_V = new HashMap();
                HashMap prefixListMap = null;
                HashMap interfaceCategoryMap_U = new HashMap();

                for (int i = 0, j = saveInterfaceNetworkMappingPrefixRequestVO.getInterfaceList().size(); i < j; i++) {
                    interfaceNetworkMappingVO = (InterfaceNetworkMappingVO) saveInterfaceNetworkMappingPrefixRequestVO.getInterfaceList().get(i);
                    interfaceCategory = interfaceNetworkMappingVO.getInterfaceCategoryID();
                    /*
                     * 1)check for validation pre series belongs to the
                     * prepaidseries or not
                     * 2)check for validation pre series should not be
                     * duplicated(series unique to all interfaces)
                     */
                    if (!BTSLUtil.isNullString(interfaceNetworkMappingVO.getValidatePrepaidSeries())) {
                        value = new StringTokenizer(interfaceNetworkMappingVO.getValidatePrepaidSeries(), ",");
                        while (value.hasMoreTokens()) {
                            series = (String) value.nextToken();
                            
                            if (interfaceCategoryMap_V.size() > 0) {
                                if (interfaceCategoryMap_V.get(interfaceCategory) != null) {
                                    prefixListMap = (HashMap) interfaceCategoryMap_V.get(interfaceCategory);
                                    if (prefixListMap.containsKey(series)) {
                                    	String[] sArr=new String[2];
                                    	sArr[0]=series;
                                        sArr[1]=interfaceNetworkMappingVO.getInterfaceIDDesc();
                                    	String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ASSOCIATE_INTERFACE_PREFIX_VALIDATION_SERIES_PREPAID_DUPLICATED, sArr);
                                    	errors.add(resmsg);
                                    } else {
                                        prefixListMap.put(series, series);
                                    }
                                } else {
                                    prefixListMap = new HashMap();
                                    prefixListMap.put(series, series);
                                    interfaceCategoryMap_V.put(interfaceCategory, prefixListMap);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_V.put(interfaceCategory, prefixListMap);
                            }
                        }
                    }

                    /*
                     * 1)check for validation post series belongs to the
                     * prepaidseries or not
                     * 2)check for validation post series should not be
                     * duplicated(series unique to all interfaces)
                     */
                    if (!BTSLUtil.isNullString(interfaceNetworkMappingVO.getValidatePostpaidSeries())) {
                        value = new StringTokenizer(interfaceNetworkMappingVO.getValidatePostpaidSeries(), ",");
                        while (value.hasMoreTokens()) {
                            series = (String) value.nextToken();
                            
                            if (interfaceCategoryMap_V.size() > 0) {
                                if (interfaceCategoryMap_V.get(interfaceCategory) != null) {
                                    prefixListMap = (HashMap) interfaceCategoryMap_V.get(interfaceCategory);
                                    if (prefixListMap.containsKey(series)) {
                                        
                                    	String[] sArr=new String[2];
                                    	sArr[0]=series;
                                        sArr[1]=interfaceNetworkMappingVO.getInterfaceIDDesc();
                                    	String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ASSOCIATE_INTERFACE_PREFIX_VALIDATION_SERIES_POSTAID_DUPLICATED, sArr);
                                    	errors.add(resmsg);
                                    } else {
                                        prefixListMap.put(series, series);
                                    }
                                } else {
                                    prefixListMap = new HashMap();
                                    prefixListMap.put(series, series);
                                    interfaceCategoryMap_V.put(interfaceCategory, prefixListMap);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_V.put(interfaceCategory, prefixListMap);
                            }
                        }
                    }

                    /*
                     * 1)check for update pre series belongs to the
                     * prepaidseries or not
                     * 2)check for update pre series should not be
                     * duplicated(series unique to all interfaces)
                     */
                    if (!BTSLUtil.isNullString(interfaceNetworkMappingVO.getUpdatePrepaidSeries())) {
                        value = new StringTokenizer(interfaceNetworkMappingVO.getUpdatePrepaidSeries(), ",");
                        while (value.hasMoreTokens()) {
                            series = (String) value.nextToken();
                            
                            if (interfaceCategoryMap_U.size() > 0) {
                                if (interfaceCategoryMap_U.get(interfaceCategory) != null) {
                                    prefixListMap = (HashMap) interfaceCategoryMap_U.get(interfaceCategory);
                                    if (prefixListMap.containsKey(series)) {
                                    	String[] sArr=new String[2];
                                    	sArr[0]=series;
                                        sArr[1]=interfaceNetworkMappingVO.getInterfaceIDDesc();
                                    	String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ASSOCIATE_INTERFACE_PREFIX_UPDATION_SERIES_PREPAID_DUPLICATED, sArr);
                                    	errors.add(resmsg);
                                    } else {
                                        prefixListMap.put(series, series);
                                    }
                                } else {
                                    prefixListMap = new HashMap();
                                    prefixListMap.put(series, series);
                                    interfaceCategoryMap_U.put(interfaceCategory, prefixListMap);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_U.put(interfaceCategory, prefixListMap);
                            }
                        }
                    }

                    /*
                     * 1)check for update post series belongs to the
                     * prepaidseries or not
                     * 2)check for update post series should not be
                     * duplicated(series unique to all interfaces)
                     */
                    if (!BTSLUtil.isNullString(interfaceNetworkMappingVO.getUpdatePostpaidSeries())) {
                        value = new StringTokenizer(interfaceNetworkMappingVO.getUpdatePostpaidSeries(), ",");
                        while (value.hasMoreTokens()) {
                            series = (String) value.nextToken();

                            
                            if (interfaceCategoryMap_U.size() > 0) {
                                if (interfaceCategoryMap_U.get(interfaceCategory) != null) {
                                    prefixListMap = (HashMap) interfaceCategoryMap_U.get(interfaceCategory);
                                    if (prefixListMap.containsKey(series)) {
                                    	String[] sArr=new String[2];
                                    	sArr[0]=series;
                                        sArr[1]=interfaceNetworkMappingVO.getInterfaceIDDesc();
                                    	String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ASSOCIATE_INTERFACE_PREFIX_UPDATION_SERIES_POSTPAID_DUPLICATED, sArr);
                                    	errors.add(resmsg);
                                    } else {
                                        prefixListMap.put(series, series);
                                    }
                                } else {
                                    prefixListMap = new HashMap();
                                    prefixListMap.put(series, series);
                                    interfaceCategoryMap_U.put(interfaceCategory, prefixListMap);
                                }
                            } else {
                                prefixListMap = new HashMap();
                                prefixListMap.put(series, series);
                                interfaceCategoryMap_U.put(interfaceCategory, prefixListMap);
                            }
                        }
                    }

                }
            }
        
            
            if(errors.size()!=0) {
            	response.setErrors(errors);
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ASSOCIATE_INTERFACE_PREFIX_FAIL, 0, null);
            }
            
            
            
            //********* validation ends**********//
            
            

            // delete all prefixes before inserting new entries for a
            // particular network
            // deletecout check in the for loop
            int deletecount = interfaceNetworkMappingDAO.deleteInterfaceNetworkPrefix(con, userVO.getNetworkID());
            ArrayList seriesList = new ArrayList();
            
            InterfaceNetworkMappingVO interfaceNetworkMappingVO = null;
            InterfaceNetworkPrefixMappingVO interfaceNetworkPrefixMappingVO = null;
            StringTokenizer value = null;
            String seriesValue = null;
            String prefixID = null;
            Date currentDate = new Date();
            
       
            
            
            for (int i = 0, j = saveInterfaceNetworkMappingPrefixRequestVO.getInterfaceList().size(); i < j; i++) {
                interfaceNetworkMappingVO = (InterfaceNetworkMappingVO) saveInterfaceNetworkMappingPrefixRequestVO .getInterfaceList().get(i);

                // Inserting Validation Prepaid series
                if (!BTSLUtil.isNullString(interfaceNetworkMappingVO.getValidatePrepaidSeries())) {
                    value = new StringTokenizer(interfaceNetworkMappingVO.getValidatePrepaidSeries(), ",");
                    while (value.hasMoreElements()) {
                    	seriesValue = value.nextToken();
                        interfaceNetworkPrefixMappingVO = new InterfaceNetworkPrefixMappingVO();
                        interfaceNetworkPrefixMappingVO.setNetworkCode(userVO.getNetworkID());
                        interfaceNetworkPrefixMappingVO.setInterfaceID(interfaceNetworkMappingVO.getInterfaceID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + seriesValue;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + seriesValue;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        interfaceNetworkPrefixMappingVO.setPrefixID(Long.parseLong(prefixID));
                        interfaceNetworkPrefixMappingVO.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                        interfaceNetworkPrefixMappingVO.setMethodType(interfaceNetworkMappingVO.getInterfaceCategoryID());
                        interfaceNetworkPrefixMappingVO.setCreatedBy(userVO.getUserID());
                        interfaceNetworkPrefixMappingVO.setModifiedBy(userVO.getUserID());
                        interfaceNetworkPrefixMappingVO.setCreatedOn(currentDate);
                        interfaceNetworkPrefixMappingVO.setModifiedOn(currentDate);

                        seriesList.add(interfaceNetworkPrefixMappingVO);
                    }
                }

                // Inserting Validation Postpaid series
                if (!BTSLUtil.isNullString(interfaceNetworkMappingVO.getValidatePostpaidSeries())) {
                    value = new StringTokenizer(interfaceNetworkMappingVO.getValidatePostpaidSeries(), ",");
                    while (value.hasMoreElements()) {
                    	seriesValue = value.nextToken();
                        interfaceNetworkPrefixMappingVO = new InterfaceNetworkPrefixMappingVO();
                        interfaceNetworkPrefixMappingVO.setNetworkCode(userVO.getNetworkID());
                        interfaceNetworkPrefixMappingVO.setInterfaceID(interfaceNetworkMappingVO.getInterfaceID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + seriesValue;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + seriesValue;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        interfaceNetworkPrefixMappingVO.setPrefixID(Long.parseLong(prefixID));
                        interfaceNetworkPrefixMappingVO.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_VALIDATION_ACTION);
                        interfaceNetworkPrefixMappingVO.setMethodType(interfaceNetworkMappingVO.getInterfaceCategoryID());
                        interfaceNetworkPrefixMappingVO.setCreatedBy(userVO.getUserID());
                        interfaceNetworkPrefixMappingVO.setModifiedBy(userVO.getUserID());
                        interfaceNetworkPrefixMappingVO.setCreatedOn(currentDate);
                        interfaceNetworkPrefixMappingVO.setModifiedOn(currentDate);

                        seriesList.add(interfaceNetworkPrefixMappingVO);
                    }
                }

                // Inserting Update Prepaid series
                if (!BTSLUtil.isNullString(interfaceNetworkMappingVO.getUpdatePrepaidSeries())) {
                    value = new StringTokenizer(interfaceNetworkMappingVO.getUpdatePrepaidSeries(), ",");
                    while (value.hasMoreElements()) {
                    	seriesValue = value.nextToken();
                        interfaceNetworkPrefixMappingVO = new InterfaceNetworkPrefixMappingVO();
                        interfaceNetworkPrefixMappingVO.setNetworkCode(userVO.getNetworkID());
                        interfaceNetworkPrefixMappingVO.setInterfaceID(interfaceNetworkMappingVO.getInterfaceID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + seriesValue;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_PREPAID + "_" + seriesValue;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        interfaceNetworkPrefixMappingVO.setPrefixID(Long.parseLong(prefixID));
                        interfaceNetworkPrefixMappingVO.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                        interfaceNetworkPrefixMappingVO.setMethodType(interfaceNetworkMappingVO.getInterfaceCategoryID());
                        interfaceNetworkPrefixMappingVO.setCreatedBy(userVO.getUserID());
                        interfaceNetworkPrefixMappingVO.setModifiedBy(userVO.getUserID());
                        interfaceNetworkPrefixMappingVO.setCreatedOn(currentDate);
                        interfaceNetworkPrefixMappingVO.setModifiedOn(currentDate);

                        seriesList.add(interfaceNetworkPrefixMappingVO);
                    }
                }

                // Inserting Update Postpaid series
                if (!BTSLUtil.isNullString(interfaceNetworkMappingVO.getUpdatePostpaidSeries())) {
                    value = new StringTokenizer(interfaceNetworkMappingVO.getUpdatePostpaidSeries(), ",");
                    while (value.hasMoreElements()) {
                    	seriesValue = value.nextToken();
                        interfaceNetworkPrefixMappingVO = new InterfaceNetworkPrefixMappingVO();
                        interfaceNetworkPrefixMappingVO.setNetworkCode(userVO.getNetworkID());
                        interfaceNetworkPrefixMappingVO.setInterfaceID(interfaceNetworkMappingVO.getInterfaceID());
                        seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + seriesValue;
                        prefixID = (String) seriesMap.get(seriesKey);
                        if (BTSLUtil.isNullString(prefixID)) {
                            seriesKey = userVO.getNetworkID() + "_" + PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_POSTPAID + "_" + seriesValue;
                            prefixID = (String) seriesMap.get(seriesKey);
                        }
                        interfaceNetworkPrefixMappingVO.setPrefixID(Long.parseLong(prefixID));
                        interfaceNetworkPrefixMappingVO.setAction(PretupsI.INTERFACE_NETWORK_PREFIX_UPDATE_ACTION);
                        interfaceNetworkPrefixMappingVO.setMethodType(interfaceNetworkMappingVO.getInterfaceCategoryID());
                        interfaceNetworkPrefixMappingVO.setCreatedBy(userVO.getUserID());
                        interfaceNetworkPrefixMappingVO.setModifiedBy(userVO.getUserID());
                        interfaceNetworkPrefixMappingVO.setCreatedOn(currentDate);
                        interfaceNetworkPrefixMappingVO.setModifiedOn(currentDate);

                        seriesList.add(interfaceNetworkPrefixMappingVO);
                    }
                }
            }
            
            if (seriesList != null && !seriesList.isEmpty()) {
                insertCount = interfaceNetworkMappingDAO.insertInterfaceNetworkPrefix(con, seriesList);

                if (insertCount > 0) {
                	mcomCon.finalCommit();
                    response.setStatus((HttpStatus.SC_OK));
    				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ASSOCIATE_INTERFACE_PREFIX_SUCCESS, null);
    				response.setMessage(resmsg);
    				response.setMessageCode(PretupsErrorCodesI.ASSOCIATE_INTERFACE_PREFIX_SUCCESS);

                } else {
                	mcomCon.finalRollback();
                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ASSOCIATE_INTERFACE_PREFIX_FAIL, 0, null);
                }

            } else {
                /*
                 * This is the case when user only delete the prefixes, not
                 * inserting the new prefiexs
                 * so at that time we need to commit the transaction id
                 * delete count greater than zero.
                 * if delete count less than 1 no need to roll back the
                 * transaction becs may be no prefixes
                 * exist in the database initiallay.
                 */
                if (deletecount > 0) {
                	mcomCon.finalCommit();
                }
                
                response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ASSOCIATE_INTERFACE_PREFIX_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.ASSOCIATE_INTERFACE_PREFIX_SUCCESS);
            }
                     
		}
		finally {
        	if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
        }
		return response;
	}

	
}
