package com.restapi.networkadmin.networkinterfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
/*//import org.apache.struts.action.ActionForward;*/
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.interfaces.businesslogic.InterfaceDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkMappingDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkMappingVO;
import com.btsl.pretups.interfaces.web.InterfaceNetworkMappingForm;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.pretups.logging.NetworkInterfacesLog;

@Service
public class NetworkInterfaceServiceImpl implements NetworkInterfaceService{

    public static final Log log = LogFactory.getLog(NetworkInterfaceServiceImpl.class.getName());
    public static final Log LOG = log;

    @Override
    public NetworkInterfacesVO loadInterfaceNetworkMappingList(UserVO userVO, HttpServletRequest request, HttpServletResponse response, Locale locale) {
        final String METHOD_NAME = "loadInterfaceNetworkMappingList";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered ");
        }
        Connection con = null;MComConnectionI mcomCon = null;
        NetworkInterfacesVO networkInterfacesVO = new NetworkInterfacesVO() ;
        try {
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            //this.authorise(request, response, "INTNTMAP01", false);
            InterfaceNetworkMappingDAO interfaceNetworkMappingDAO = new InterfaceNetworkMappingDAO();
            ArrayList<InterfaceNetworkMappingVO> interfaceNetworkMappingList = interfaceNetworkMappingDAO.loadInterfaceNetworkMappingList(con, userVO.getNetworkID(), PretupsI.INTERFACE_CATEGORY);
            networkInterfacesVO.setInterfaceList(interfaceNetworkMappingList);
            networkInterfacesVO.setStatus(HttpStatus.SC_OK);
            response.setStatus(HttpStatus.SC_OK);
            if(interfaceNetworkMappingList.isEmpty()) {
                networkInterfacesVO.setMessage("No Data found");
            }
            else {
                networkInterfacesVO.setMessage("success");
            }
            NetworkInterfacesLog.arrayListlog(interfaceNetworkMappingList);
        }
        catch (BTSLBaseException be) {
            log.error(METHOD_NAME, "Exception:e=" + be);
            log.errorTrace(METHOD_NAME, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                networkInterfacesVO.setMessageCode(be.getMessage());
                networkInterfacesVO.setMessage(msg);
                networkInterfacesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(METHOD_NAME, "Exceptin:e=" + e);
            networkInterfacesVO.setMessageCode(e.getMessage());
            networkInterfacesVO.setMessage(e.getMessage());
            networkInterfacesVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
        }


        finally {
            if (mcomCon != null) {
                mcomCon.close("NetworkInterfaceServiceImpl#"+METHOD_NAME);
                mcomCon = null;
            }

            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting");
            }
        }

        return networkInterfacesVO;
    }

    @Override
    public BaseResponse deleteNetworkInterfaces(UserVO userVO, HttpServletRequest request,
                                                HttpServletResponse response,  String networkInterfaceId, Locale locale) {

        BaseResponse responseVO = new BaseResponse();
        final String METHOD_NAME = "deleteNetworkInterfaces";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME+" - service layer", "Entered");
        }
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            //UserVO userSessionVO = getUserFormSession(request);
            //InterfaceDAO interfaceDAO = new InterfaceDAO();
            //UserVO userVO = (UserVO) getUserFormSession(request);
            InterfaceNetworkMappingDAO interfaceNetworkMappingDAO = new InterfaceNetworkMappingDAO();
            boolean interfaceNetworkPrefixMappingExists = interfaceNetworkMappingDAO.isExistsInterfaceNetworkPrefixMapping(con, userVO.getNetworkID(), networkInterfaceId);
            if (interfaceNetworkPrefixMappingExists) {
                throw new BTSLBaseException(this, METHOD_NAME,"interfaces.interfacenetwrokmappingdetail.deletenotallowed");
                //this.handleMessage(new BTSLMessages("interfaces.interfacenetwrokmappingdetail.deletenotallowed", "list"), request, null);
            }
            else {
                int deleteCount = interfaceNetworkMappingDAO.deleteInterfaceNetworkMapping(con, userVO.getNetworkID(), networkInterfaceId);
                if (deleteCount > 0) {
                    mcomCon.finalCommit();
                    //this.handleMessage(new BTSLMessages("interfaces.interfacenetwrokmappingdetail.deletesuccess", "list"), request, null);
                } else {
                    mcomCon.finalRollback();
                    log.error(METHOD_NAME, "Error: while Deleting Interface Network Mapping");
                    throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
                }
            }
            responseVO.setStatus(HttpStatus.SC_OK);
            //responseVO.setMessage(PretupsI.NETWORK_INTERFACE_DELETED);

            responseVO.setMessageCode("network.interfaces.deleted.successfully");
            responseVO.setMessage(RestAPIStringParser.getMessage(locale,
                    "network.interfaces.deleted.successfully", null));
        }
        catch (BTSLBaseException be) {
            log.error(METHOD_NAME, "Exception:e=" + be);
            log.errorTrace(METHOD_NAME, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                responseVO.setMessageCode(be.getMessage());
                responseVO.setMessage(msg);
                responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(METHOD_NAME, "Exceptin:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            responseVO.setMessage(e.getMessage());
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {
            if (mcomCon != null) {
                mcomCon.close("InterfaceNetworkMappingAction#deleteNetworkInterfaces");
                mcomCon = null;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting");
            }
        }

        return responseVO;
    }

    @Override
    public BaseResponse addNetworkInterfaceInterfaces(UserVO userVO, HttpServletRequest request,
                                                      HttpServletResponse response, NetworkInterfaceRequestVO requestVO, Locale locale) {

        BaseResponse responseVO = new BaseResponse();
        final String METHOD_NAME = "addNetworkInterfaceInterfaces";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        //ActionForward forward = null;
        Connection con = null;MComConnectionI mcomCon = null;
        int addCount = 0;
        try {
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            InterfaceNetworkMappingDAO interfaceNetworkMappingDAO = new InterfaceNetworkMappingDAO();
            InterfaceNetworkMappingVO interfaceNetworkMappingVO = new InterfaceNetworkMappingVO();

            String[] str = requestVO.getInterfaceID().split(":");
            interfaceNetworkMappingVO.setInterfaceID(str[1]);

            interfaceNetworkMappingVO.setInterfaceCategoryID(requestVO.getInterfaceCategoryID());
            interfaceNetworkMappingVO.setQueueSize(Long.parseLong(requestVO.getQueueSize()));
            interfaceNetworkMappingVO.setQueueTimeOut(Long.parseLong(requestVO.getQueueTimeOut()));
            interfaceNetworkMappingVO.setRequestTimeOut(Long.parseLong(requestVO.getRequestTimeOut()));
            interfaceNetworkMappingVO.setNextCheckQueueReqSec(Long.parseLong(requestVO.getQuereyRetryInterval()));
            interfaceNetworkMappingVO.setLastModifiedOn(requestVO.getLastModifiedOn());

            //UserVO userVO = (UserVO) getUserFormSession(request);
            interfaceNetworkMappingVO.setNetworkCode(userVO.getNetworkID());
            interfaceNetworkMappingVO.setCreatedBy(userVO.getUserID());
            interfaceNetworkMappingVO.setModifiedBy(userVO.getUserID());
            Date currentDate = new Date();
            interfaceNetworkMappingVO.setCreatedOn(currentDate);
            interfaceNetworkMappingVO.setModifiedOn(currentDate);

            addCount = interfaceNetworkMappingDAO.addInterfaceNetworkMapping(con, interfaceNetworkMappingVO);

            if (addCount > 0) {
                mcomCon.finalCommit();
                BTSLMessages btslMessage = new BTSLMessages("interfaces.interfacenetwrokmappingdetail.successaddmessage", "list");
            } else {
                mcomCon.finalRollback();
                LOG.error(METHOD_NAME, "Error: while Inserting Interface Network Mapping");
                responseVO.setMessageCode("error.general.processing");
                responseVO.setMessage(RestAPIStringParser.getMessage(locale,"error.general.processing", null));
                throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");

            }
            NetworkInterfacesLog.log(interfaceNetworkMappingVO);
            //responseVO.setMessage(PretupsI.NETWORK_INTERFACE_ADDED);
            responseVO.setStatus(HttpStatus.SC_OK);
            responseVO.setMessageCode("network.interfaces.added.successfully");
            responseVO.setMessage(RestAPIStringParser.getMessage(locale,
                    "network.interfaces.added.successfully", null));

        }
        catch (BTSLBaseException be) {
            LOG.error(METHOD_NAME, "Exception:e=" + be);
            LOG.errorTrace(METHOD_NAME, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                responseVO.setMessageCode(be.getMessage());
                responseVO.setMessage(msg);
                responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            LOG.error(METHOD_NAME, "Exceptin:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            responseVO.setMessage(e.getMessage());
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME+"#addNetworkInterfaceInterfaces");
                mcomCon = null;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting");
            }
        }

        return responseVO;

    }


    @Override
    public BaseResponse modifyNetworkInterfaceInterfaces(UserVO userVO, HttpServletRequest request,
                                                         HttpServletResponse response, NetworkInterfaceRequestVO requestVO, Locale locale) {
        final String METHOD_NAME = "modifyNetworkInterfaceInterfaces";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME+" in service layer", "Entered");
        }
        //ActionForward forward = null;
        Connection con = null;MComConnectionI mcomCon = null;
        int updateCount = 0;
        BaseResponse res = new BaseResponse();
        try {

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            InterfaceNetworkMappingDAO interfaceNetworkMappingDAO = new InterfaceNetworkMappingDAO();
            InterfaceNetworkMappingVO interfaceNetworkMappingVO = new InterfaceNetworkMappingVO();


            interfaceNetworkMappingVO.setInterfaceID(requestVO.getInterfaceID());
            interfaceNetworkMappingVO.setQueueSize(Long.parseLong(requestVO.getQueueSize()));
            interfaceNetworkMappingVO.setQueueTimeOut(Long.parseLong(requestVO.getQueueTimeOut()));
            interfaceNetworkMappingVO.setRequestTimeOut(Long.parseLong(requestVO.getRequestTimeOut()));
            interfaceNetworkMappingVO.setNextCheckQueueReqSec(Long.parseLong(requestVO.getQuereyRetryInterval()));
            interfaceNetworkMappingVO.setLastModifiedOn(requestVO.getLastModifiedOn());

            interfaceNetworkMappingVO.setNetworkCode(userVO.getNetworkID());
            interfaceNetworkMappingVO.setModifiedBy(userVO.getUserID());
            Date currentDate = new Date();
            interfaceNetworkMappingVO.setModifiedOn(currentDate);

            updateCount = interfaceNetworkMappingDAO.updateInterfaceNetworkMapping(con, interfaceNetworkMappingVO);

            // int geographyCount = 0;

            if (updateCount > 0) {
                mcomCon.finalCommit();
                BTSLMessages btslMessage = new BTSLMessages("interfaces.interfacenetwrokmappingdetail.successeditmessage", "list");
            } else {
                mcomCon.finalRollback();
                LOG.error(METHOD_NAME, "Error: while updating Interface Network Mapping");
                res.setMessageCode("error.general.processing");
                throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
            }
            NetworkInterfacesLog.log(interfaceNetworkMappingVO);
//	            res.setMessage(PretupsI.NETWORK_INTERFACE_MODIFIED);
            res.setStatus(HttpStatus.SC_OK);
            res.setMessageCode("network.interfaces.modified.successfully");
            res.setMessage(RestAPIStringParser.getMessage(locale,
                    "network.interfaces.modified.successfully", null));

        }
        catch (BTSLBaseException be) {
            LOG.error(METHOD_NAME, "Exception:e=" + be);
            LOG.errorTrace(METHOD_NAME, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                res.setMessageCode(be.getMessage());
                res.setMessage(msg);
                res.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }catch (Exception e) {
            LOG.error(METHOD_NAME, "Exceptin:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            res.setMessage(e.getMessage());
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            res.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
        finally {
            if (mcomCon != null) {
                mcomCon.close("NetworkInterfaceServiceImpl#"+METHOD_NAME);
                mcomCon = null;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting");
            }
        }

        return res;
    }
}
