package com.inter.uganda_webservices;

/**
 * OUGReqResFormatter.java
 * Copyright(c) 2011, Comviva Technologies Pvt. Ltd.
 * All Rights Reserved
 * ----------------------------------------------------------------------------
 * ---------------------
 * Author Date History
 * ----------------------------------------------------------------------------
 * ---------------------
 * Shashank Shukla September 26, 2011 Initial Creation
 * ----------------------------------------------------------------------------
 * --------------------
 * This class is responsible to generate the request and parse the response for
 * the UgandaWebservice interface.
 */
import java.util.Calendar;
import java.util.HashMap;
import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.DCPPort;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.GetAccountResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogoffRequest;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogoffResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogonRequest;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.LogonResponse;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.MDCAddSubscribedPackageReq;
import com.btsl.pretups.inter.uganda_webservices.uganda_volubill.MDCAddSubscribedPackageResponse;

public class OUGReqResFormatter {
    public Log _log = LogFactory.getLog("OUGReqResFormatter".getClass().getName());
    private String _interfaceID = null;

    /**
     * Method parseResponseObject
     * 
     * @param p_action
     *            int
     * @param p_logonResponse
     *            LogonResponse
     * @param p_logoffResponse
     *            LogoffResponse
     * @param p_getAccountResponse
     *            GetAccountResponse
     * @param p_subscribedPackageResponse
     *            MDCAddSubscribedPackageResponse
     * @param p_service
     *            DCPPort
     * @throws BTSLBaseException
     * @throws Exception
     */
    public HashMap<String, String> parseResponseObject(int p_action, LogonResponse p_logonResponse, LogoffResponse p_logoffResponse, GetAccountResponse p_getAccountResponse, MDCAddSubscribedPackageResponse p_subscribedPackageResponse, DCPPort p_service) throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseResponseObject", "Entered p_action=" + p_action);
        HashMap<String, String> responseMap = null;
        try {
            switch (p_action) {
            case OUGVoluBillI.ACTION_LOGON: {
                responseMap = parseLogonResponseObject(responseMap, p_logonResponse, p_service);
                break;
            }
            case OUGVoluBillI.ACTION_LOGOFF: {
                responseMap = parseLogoffResponseObject(responseMap, p_logoffResponse);
                break;
            }
            case OUGVoluBillI.ACTION_ACCOUNT_INFO: {
                responseMap = parseGetAccountInfoResponse(responseMap, p_getAccountResponse);
                break;
            }
            case OUGVoluBillI.ACTION_INTERNET_RECHARGE: {
                responseMap = parseSubscribedPackageResponse(responseMap, p_subscribedPackageResponse);
                break;
            }
            }// end of switch block
        }// end of try block
        catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("parseResponseObject", "Exception e:" + e.getMessage());
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("parseResponseObject", "Exiting with responseMap=" + responseMap);
        }// end of finally
        return responseMap;
    }// end of parseResponse

    /**
     * Method getLogonRequestObject
     * 
     * @param p_requestMap
     *            HashMap<String, String>
     * @return logonRequest LogonRequest
     * @throws Exception
     */
    protected LogonRequest getLogonRequestObject(HashMap<String, String> p_requestMap) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getLogonRequestObject", "Entered");
        LogonRequest logonRequest = null;
        _interfaceID = p_requestMap.get("INTERFACE_ID");
        String userName = FileCache.getValue(_interfaceID, "USER_NAME");
        String password = FileCache.getValue(_interfaceID, "PASSWORD");
        try {
            logonRequest = new LogonRequest();
            logonRequest.setUsername(userName);
            logonRequest.setPassword(password);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getLogonRequestObject", "Exited logonRequest=" + logonRequest.toString());
        }
        return logonRequest;
    }

    /**
     * Method parseLogonResponseObject
     * 
     * @param p_responseMap
     *            HashMap<String, String>
     * @param p_logonResponse
     *            LogonResponse
     * @param p_service
     *            DCPPort
     * @return p_responseMap HashMap<String, String>
     * @throws Exception
     */
    private HashMap<String, String> parseLogonResponseObject(HashMap<String, String> p_responseMap, LogonResponse p_logonResponse, DCPPort p_service) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLogonResponseObject", "Entered");

        try {
            p_responseMap = new HashMap<String, String>();
            String reply = p_logonResponse.getReply();
            SOAPHeaderElement sessionHeader = ((Stub) p_service).getResponseHeader("http://xml.apache.org/axis/session", "sessionID");
            String sessionId = sessionHeader.getFirstChild().getNodeValue();
            p_responseMap.put("SESSION_ID", sessionId);

            if (_log.isDebugEnabled())
                _log.debug("parseLogonResponseObject", "reply=" + reply);

            if (OUGVoluBillI.OPERATION_LOGIN_SUCCESSFUL.equals(reply))
                p_responseMap.put("RESP_STATUS", OUGVoluBillI.RESULT_OK);
            else
                p_responseMap.put("RESP_STATUS", reply);

        } catch (Exception e) {
            _log.error("parseLogonResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLogonResponseObject", "Exit  p_responseMap=" + p_responseMap);
        }
        return p_responseMap;
    }

    /**
     * Method getLogoffRequestObject
     * 
     * @param p_requestMap
     *            HashMap<String, String>
     * @param p_service
     *            DCPPort
     * @return logoffRequest LogoffRequest
     * @throws Exception
     */
    protected LogoffRequest getLogoffRequestObject(HashMap<String, String> p_requestMap, DCPPort p_service) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getLogoffRequestObject", "Entered");
        LogoffRequest logoffRequest = null;
        try {
            setRequestHeader(p_requestMap, p_service);
            logoffRequest = new LogoffRequest();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getLogoffRequestObject", "Exited logoffRequest=" + logoffRequest.toString());
        }
        return logoffRequest;
    }

    /**
     * Method parseLogoffResponseObject
     * 
     * @param p_responseMap
     *            HashMap<String, String>
     * @param p_logoffResponse
     *            LogoffResponse
     * @return p_responseMap HashMap<String, String>
     * @throws Exception
     */
    private HashMap<String, String> parseLogoffResponseObject(HashMap<String, String> p_responseMap, LogoffResponse p_logoffResponse) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseLogoffResponseObject", "Entered ");

        try {
            p_responseMap = new HashMap<String, String>();
            String reply = p_logoffResponse.getReply();
            // String typeDesc=String.valueOf(p_logoffResponse.getTypeDesc());
            if (_log.isDebugEnabled())
                _log.debug("parseLogoffResponseObject", "reply=" + reply);

            if (OUGVoluBillI.OPERATION_LOGOFF_SUCCESSFUL.equals(reply))
                p_responseMap.put("RESP_STATUS", OUGVoluBillI.RESULT_OK);
            else
                p_responseMap.put("RESP_STATUS", reply);

        } catch (Exception e) {
            _log.error("parseLogoffResponseObject", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseLogoffResponseObject", "Exit  p_responseMap=" + p_responseMap);
        }
        return p_responseMap;
    }

    /**
     * Method getAccountRequestObject
     * 
     * @param p_requestMap
     *            HashMap<String, String>
     * @param p_service
     *            DCPPort
     * @return getAccount GetAccountReq
     * @throws Exception
     */
    protected GetAccountReq getAccountRequestObject(HashMap<String, String> p_requestMap, DCPPort p_service) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getAccountRequestObject", "Entered");
        _interfaceID = p_requestMap.get("INTERFACE_ID");
        GetAccountReq getAccount = null;
        try {
            String serviceProviderName = FileCache.getValue(_interfaceID, "SERVICE_PROVIDER_NAME");
            setRequestHeader(p_requestMap, p_service);
            getAccount = new GetAccountReq();
            getAccount.setAccountNo(InterfaceUtil.getFilterMSISDN(p_requestMap.get("INTERFACE_ID"), p_requestMap.get("MSISDN")));
            getAccount.setServiceProviderName(serviceProviderName);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getAccountRequestObject", "Exited getAccount=" + getAccount.toString());
        }
        return getAccount;
    }

    /**
     * Method parseGetAccountInfoResponse
     * 
     * @param p_responseMap
     *            HashMap<String, String>
     * @param p_getAccountResponse
     *            GetAccountResponse
     * @return p_responseMap HashMap<String, String>
     * @throws Exception
     */
    private HashMap<String, String> parseGetAccountInfoResponse(HashMap<String, String> p_responseMap, GetAccountResponse p_getAccountResponse) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseGetAccountInfoResponse", "Entered");
        try {
            p_responseMap = new HashMap<String, String>();
            String reply = p_getAccountResponse.getReply();

            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "reply=" + reply);

            if (OUGVoluBillI.OPERATION_GETACCOUNT_SUCCESSFUL.equals(reply)) {
                p_responseMap.put("RESP_STATUS", OUGVoluBillI.RESULT_OK);
                String accountBalance = Double.toString(p_getAccountResponse.getAccount().getAccountBalance());
                String accountId = Long.toString(p_getAccountResponse.getAccount().getAccountId());
                String msisdn = p_getAccountResponse.getAccount().getAccountNo();
                String accountType = p_getAccountResponse.getAccount().getAccountType();

                String accountStatus = Boolean.toString(p_getAccountResponse.getAccount().getClosed());

                if (accountStatus == "false")
                    p_responseMap.put("ACCOUNTSTATE", "ACTIVE");
                else
                    p_responseMap.put("ACCOUNTSTATE", "SUSPEND");

                p_responseMap.put("ACCOUNT_BALANCE", accountBalance);
                p_responseMap.put("ACCOUNT_ID", accountId);
                p_responseMap.put("MSISDN", msisdn);
                p_responseMap.put("ACCOUNT_TYPE", accountType);
            } else {
                p_responseMap.put("RESP_STATUS", reply);
                p_responseMap.put("RESP_ERR_CODE", p_getAccountResponse.getError(0).getCode());
                p_responseMap.put("RESP_ERR_MSG", p_getAccountResponse.getError(0).getMessage());
                p_responseMap.put("RESP_ERR_EXCEPTION", p_getAccountResponse.getError(0).getException());
            }
        } catch (Exception e) {
            _log.error("parseGetAccountInfoResponse", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseGetAccountInfoResponse", "Exit  p_responseMap=" + p_responseMap);
        }
        return p_responseMap;
    }

    /**
     * Method addSubscribedPackageReqObject
     * 
     * @param p_requestMap
     *            HashMap<String, String>
     * @param p_Service
     *            DCPPort
     * @throws Exception
     */
    protected MDCAddSubscribedPackageReq addSubscribedPackageReqObject(HashMap<String, String> p_requestMap, DCPPort p_Service) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("addSubscribedPackageReqObject", "Entered");

        _interfaceID = p_requestMap.get("INTERFACE_ID");
        MDCAddSubscribedPackageReq addPackage = null;

        String serviceProviderName = FileCache.getValue(_interfaceID, "SERVICE_PROVIDER_NAME");
        String packageName = p_requestMap.get("REQ_PACKAGE");

        Boolean doCharge = Boolean.parseBoolean(FileCache.getValue(_interfaceID, "DO_CHARGE"));

        try {
            setRequestHeader(p_requestMap, p_Service);
            addPackage = new MDCAddSubscribedPackageReq();
            addPackage.setServiceProviderName(serviceProviderName);
            addPackage.setMsisdn(InterfaceUtil.getFilterMSISDN(p_requestMap.get("INTERFACE_ID"), p_requestMap.get("MSISDN")));
            addPackage.setPackageName(packageName);
            addPackage.setDoCharge(doCharge);
            Calendar purchaseDate = Calendar.getInstance();
            addPackage.setPurchaseDate(purchaseDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("addSubscribedPackageReqObject", "Exited addPackage=" + addPackage.toString());
        }
        return addPackage;
    }

    /**
     * Method parseSubscribedPackageResponse
     * 
     * @param p_responseMap
     *            HashMap<String, String>
     * @param p_SubscribedPackageResponse
     *            MDCAddSubscribedPackageResponse
     * @throws Exception
     */
    private HashMap<String, String> parseSubscribedPackageResponse(HashMap<String, String> p_responseMap, MDCAddSubscribedPackageResponse p_SubscribedPackageResponse) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("parseSubscribedPackageResponse", "Entered");

        try {
            p_responseMap = new HashMap<String, String>();
            String reply = p_SubscribedPackageResponse.getReply();

            if (_log.isDebugEnabled())
                _log.debug("parseSubscribedPackageResponse", "reply=" + reply);

            if (OUGVoluBillI.OPERATION_ADD_SUBSCRIBEDPACKAGE_SUCCESSFUL.equals(reply))
                p_responseMap.put("RESP_STATUS", OUGVoluBillI.RESULT_OK);
            else {
                p_responseMap.put("RESP_STATUS", reply);
                p_responseMap.put("RESP_ERR_CODE", p_SubscribedPackageResponse.getError(0).getCode());
                p_responseMap.put("RESP_ERR_MSG", p_SubscribedPackageResponse.getError(0).getMessage());
                p_responseMap.put("RESP_ERR_EXCEPTION", p_SubscribedPackageResponse.getError(0).getException());
            }
        } catch (Exception e) {
            _log.error("parseSubscribedPackageResponse", "Exception e:" + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("parseSubscribedPackageResponse", "Exit  p_responseMap=" + p_responseMap);
        }
        return p_responseMap;
    }

    /**
     * Method setRequestHeader
     * 
     * @param p_requestMap
     *            HashMap<String, String>
     * @param p_service
     *            DCPPort
     * @throws Exception
     */
    private void setRequestHeader(HashMap<String, String> p_requestMap, DCPPort p_service) throws Exception {
        String sessionId = p_requestMap.get("SESSION_ID");
        SOAPHeaderElement sessionHeader = new SOAPHeaderElement("http://xml.apache.org/axis/session", "sessionID");
        sessionHeader.setMustUnderstand(false);
        sessionHeader.addTextNode(sessionId);
        ((Stub) p_service).setHeader(sessionHeader);
    }
}
