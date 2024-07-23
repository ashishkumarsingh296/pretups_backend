package com.inter.vodafoneghana.locationservice;

import gh.com.vodafone.locationsvc.svcintfc.LocationData;
import gh.com.vodafone.locationsvc.svcintfc.NameValue;
import gh.com.vodafone.locationsvc.svcintfc.ServiceResponse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.util.BTSLUtil;

public class LSWSResponseParser {

    private static Log _log = LogFactory.getLog(LSWSResponseParser.class.getName());

    /**
     * This method is used to parse the response.
     * 
     * @param int p_action
     * @param String
     *            p_responseStr
     * @return HashMap
     * @throws Exception
     */
    public HashMap parseResponse(int p_action, HashMap _requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponse", "Entered p_action::" + p_action + " _requestMap:: " + _requestMap);
        HashMap map = null;
        try {
            switch (p_action) {
            case LSWSI.ACTION_LS_VALIDATE: {
                map = parseLocationServiceResponse(_requestMap);
                break;
            }

            }
        } catch (Exception e) {
            _log.error("parseResponse", "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponse", "Exiting map::" + map);
        }
        return map;
    }

    /**
     * This method is used to parse the credit response.
     * 
     * @param p_responseStr
     * @return HashMap
     * @throws Exception
     */
    private HashMap parseLocationServiceResponse(HashMap _reqMap) throws Exception {
        final String methodName = "LSWSResponseParser[parseLocationServiceResponse]";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered ");
        HashMap responseMap = null;
        ServiceResponse serviceResponse = null;
        try {
            Object object = _reqMap.get("RESPONSE_OBJECT");
            responseMap = new HashMap();

            if (object != null)
                serviceResponse = (ServiceResponse) object;
            else
                throw new BTSLBaseException(InterfaceErrorCodesI.EXCEPTION_INTERFACE_RESPONSE);

            serviceResponse.getStatusCode();

            if (serviceResponse != null && !BTSLUtil.isNullString(serviceResponse.getStatusCode())) {
                if (_log.isDebugEnabled())
                    _log.debug(methodName, "Response Code " + serviceResponse.getStatusCode());

                if (serviceResponse.getStatusCode().equalsIgnoreCase(LSWSI.RESULT_OK)) {

                    responseMap.put("INTERFACE_STATUS", serviceResponse.getStatusCode());

                    LocationData data = serviceResponse.getLocationData();

                    List lstData = data.getData();
                    Iterator listIte = lstData.iterator();
                    while (listIte.hasNext()) {
                        NameValue nameVal = (NameValue) listIte.next();
                        if (nameVal.getName().equalsIgnoreCase("CID")) {
                            responseMap.put("CELL_ID", nameVal.getValue());
                        }
                        if (nameVal.getName().equalsIgnoreCase("radius1")) {
                            responseMap.put("SWITCH_ID", nameVal.getValue());
                        }
                    }

                } else {
                    throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
                }

            } else {
                responseMap.put("INTERFACE_STATUS", InterfaceErrorCodesI.ERROR_RESPONSE);
                _log.error(methodName, "Invalid Error Code::");
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_RESPONSE);
            }

        } catch (BTSLBaseException be) {
            _log.error(methodName, "Exception e::" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "Exception e::" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exited responseMap::" + responseMap);
        }
        return responseMap;
    }

}
