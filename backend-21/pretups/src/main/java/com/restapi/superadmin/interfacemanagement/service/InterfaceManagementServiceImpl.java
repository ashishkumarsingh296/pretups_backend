package com.restapi.superadmin.interfacemanagement.service;

import com.btsl.common.*;
import com.btsl.db.util.MComConnection;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.interfaces.businesslogic.InterfaceDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNodeDetailsVO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.interfacemanagement.requestVO.InterfaceDetailRequestVO;
import com.restapi.superadmin.interfacemanagement.requestVO.ModifyInterfaceDetailRequestVO;
import com.restapi.superadmin.interfacemanagement.responseVO.InterfaceDetailResponseVO;
import com.restapi.superadmin.interfacemanagement.responseVO.InterfaceTypeResponseVO;
import com.restapi.superadmin.interfacemanagement.responseVO.InterfaceTypeVO;
import com.restapi.superadmin.interfacemanagement.responseVO.ModifyInterfaceDetailResponseVO;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

@Service("InterfaceManagementServiceI")
public class InterfaceManagementServiceImpl implements InterfaceManagementServiceI{

    public static final Log log = LogFactory.getLog(InterfaceManagementServiceImpl.class.getName());
    public static final String classname = "InterfaceManagementServiceImpl";

    @Override
    public InterfaceDetailResponseVO getInterfaceDetails(MultiValueMap<String, String> headers, HttpServletResponse responseSwag, Connection con, String loginID, Locale locale, String interfaceCategory) throws Exception, BTSLBaseException {
        final String METHOD_NAME = "getInterfaceDetails";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        InterfaceDetailResponseVO response = new InterfaceDetailResponseVO();
        ArrayList<InterfaceVO> interfaceDetailList = null;
        try {
            UserDAO userDAO = new UserDAO();
            UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            InterfaceDAO interfaceDAO = new InterfaceDAO();
            interfaceDetailList = interfaceDAO.loadInterfaceDetails(con, interfaceCategory, userVO.getCategoryCode(), userVO.getNetworkID());

            if (interfaceDetailList != null) {
                response.setInterfaceDetailsList(interfaceDetailList);
            }else {
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
    public InterfaceDetailResponseVO addInterfaceDetails(MultiValueMap<String, String> headers, Connection con, String loginID, Locale locale, InterfaceDetailRequestVO request, String interfaceCategory) throws Exception {
        final String methodName = "addInterfaceDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        int addCount = -1;
        int addCountNode = -1;
        ArrayList<InterfaceNodeDetailsVO> nodeList = null;
        InterfaceDetailResponseVO response = new InterfaceDetailResponseVO();
        try{
            InterfaceDAO interfaceDAO = new InterfaceDAO();
            nodeList = request.getNodeList();
            UserDAO userDAO = new UserDAO();
            InterfaceVO interfaceVO = new InterfaceVO();
            UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            // Check for Uniqueness of interface_name before inserting the
            // Record in
            // interfaces table.
            if (interfaceDAO.isInterfaceNameExists(con, request.getInterfaceName(), "null")) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACES_ADDINTERFACE_ALREADYEXISTS);
            } else if (interfaceDAO.isExistsExternalId(con, request.getExternalId())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACES_ADDINTERFACE_EXTERNALID_ALREADYEXISTS);
            }else{
                // Logic for creating primary key of 10 digits (eg:INTID00022)
                String idType = PretupsI.INTERFACE_TYPE_ID;
                StringBuffer uniqueInterfaceId = new StringBuffer();
                long interfaceId = IDGenerator.getNextID(idType, PretupsI.ALL);
                int zeroes = 10 - (idType.length() + Long.toString(interfaceId).length());
                for (int count = 0; count < zeroes; count++) {
                    uniqueInterfaceId.append(0);
                }
                uniqueInterfaceId.insert(0, idType);
                uniqueInterfaceId.append(Long.toString(interfaceId));
                interfaceVO = constructInterfaceVO(interfaceVO, request, interfaceCategory);
                Date currentDate = new Date(System.currentTimeMillis());
                // set Default values
                interfaceVO.setInterfaceId(uniqueInterfaceId.toString());
                interfaceVO.setCreatedOn(currentDate);
                interfaceVO.setModifiedOn(currentDate);
                interfaceVO.setClosureDate(currentDate);
                interfaceVO.setCreatedBy(userVO.getUserID());
                interfaceVO.setModifiedBy(userVO.getUserID());
                String message = null;
                addCount = interfaceDAO.addInterfaceDetails(con, interfaceVO);
                if (addCount > 0) {
                    // if data inserted in interfaces table then insert the
                    // details of nodes in interface_node_details table
                    if (nodeList != null && !nodeList.isEmpty()) {
                        HashMap<String, String> arguments = interfaceDAO.addModifyNode(con, nodeList, interfaceVO.getInterfaceId(), userVO.getUserID(), userVO.getNetworkID());

                        String count = arguments.get("arg1");
                        message = arguments.get("arg2");
                        addCountNode = Integer.parseInt(count);
                        if (addCountNode <= 0) {
                            con.rollback();
                            log.error("addInterfaceDetails", " Not able to insert Node details ");
                        }
                    }
                    con.commit();
                    // Enter the details for Add interface on Admin Log
                    AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setSource(TypesI.LOGGER_INTERFACE_SOURCE);
                    adminOperationVO.setDate(currentDate);
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                    adminOperationVO.setInfo("Interface " + interfaceVO.getInterfaceDescription() + " added successfully" + "Other info " + message);
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    AdminOperationLog.log(adminOperationVO);

                }else {
                    con.rollback();
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ADD_INTERFACE_DETAIL_FAIL);
                }
            }
            String interfaceID[] = {interfaceVO.getInterfaceDescription(), interfaceVO.getInterfaceId() };

            response.setStatus((HttpStatus.SC_OK));
            response.setInterfaceId(interfaceVO.getInterfaceId());
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INTERFACES_ADDINTERFACE_ADD_SUCCESS, interfaceID);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.INTERFACES_ADDINTERFACE_ADD_SUCCESS);


            BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.INTERFACES_ADDINTERFACE_ADD_SUCCESS, interfaceID);
            final PushMessage pushMessage = new PushMessage(userVO.getMsisdn(), btslMessage, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
                    (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), userVO.getNetworkID());
            pushMessage.push();
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }
        }
        return response;
    }

    @Override
    public ModifyInterfaceDetailResponseVO getInterfaceDetailsByInterfaceId(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con, String loginID, Locale locale, String interfaceId) throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "getInterfaceDetailsByInterfaceId";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        ModifyInterfaceDetailResponseVO response = new ModifyInterfaceDetailResponseVO();
        ArrayList<InterfaceVO> interfaceDetailList = null;
        ArrayList<InterfaceNodeDetailsVO> nodesList = null;
        try {
            UserDAO userDAO = new UserDAO();
            UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            InterfaceDAO interfaceDAO = new InterfaceDAO();
            interfaceDetailList = interfaceDAO.getInterfaceDetailsByInterfaceID(con, interfaceId);
            if(!interfaceDetailList.isEmpty()){
                nodesList = interfaceDAO.loadNodeDetails(con, interfaceId);
            }
            if(!interfaceDetailList.isEmpty()){
                response.setInterfaceDetailsList(interfaceDetailList);
                if(!nodesList.isEmpty()){
                    response.setNodeDetailList(nodesList);
                }
            }else {
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
    public ModifyInterfaceDetailResponseVO modifyInterfaceDetails(MultiValueMap<String, String> headers, Connection con, String loginID, Locale locale, ModifyInterfaceDetailRequestVO request) throws Exception {
        final String methodName = "modifyInterfaceDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        int updateCount = -1;
        int nodeCount = 0;
        int delCount = 0;
        ModifyInterfaceDetailResponseVO response = new ModifyInterfaceDetailResponseVO();
        try{
            InterfaceDAO interfaceDAO = new InterfaceDAO();
            UserDAO userDAO = new UserDAO();
            InterfaceVO interfaceVO = new InterfaceVO();
            UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);

            // Check for Uniqueness of interface_name before updating the
            // Record in
            // interfaces table.
            if (interfaceDAO.isInterfaceNameExists(con, request.getInterfaceName(), request.getInterfaceId())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACES_ADDINTERFACE_ALREADYEXISTS);
            } else if (interfaceDAO.isExistsExternalIdModify(con, request.getExternalId(), request.getInterfaceId())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACES_ADDINTERFACE_EXTERNALID_ALREADYEXISTS);
            }else{
                String message = null;
                StringBuffer deleteMsg = new StringBuffer();
                Date currentDate = new Date(System.currentTimeMillis());
                interfaceVO.setClosureDate(currentDate);
                interfaceVO.setModifiedOn(currentDate);
                interfaceVO.setModifiedBy(userVO.getUserID());
                interfaceVO.setInterfaceId(request.getInterfaceId());

                interfaceVO = constructModifyInterfaceVO(interfaceVO, request);

                updateCount = interfaceDAO.modifyInterfaceDetails(con, interfaceVO);
                if (updateCount > 0) {
                    if(!request.getNodeList().isEmpty()){
                        ArrayList<InterfaceNodeDetailsVO> nodelist = request.getNodeList();
                        ArrayList<InterfaceNodeDetailsVO> deleteNodelist = new ArrayList<>();
                        for(int i=0; i<nodelist.size(); i++){
                            if(nodelist.get(i).getNodeStatus().equalsIgnoreCase(PretupsI.NO)){
                                deleteNodelist.add(nodelist.get(i));
                            }
                        }
                        if(!deleteNodelist.isEmpty() || deleteNodelist != null) {
                            delCount = interfaceDAO.deleteNodes(con, request.getInterfaceId(), deleteNodelist, userVO.getUserID());

                            if (delCount < 0) {
                                con.rollback();
                                log.error(methodName, PretupsErrorCodesI.INTERFACES_DELETE_NODE_FAILED);
                            }
                            deleteMsg.append(" Number of nodes deleted =" + delCount);
                            response.setDeleteMessage(String.valueOf(deleteMsg));
                        }
                    }
                    HashMap<String, String> arguments = interfaceDAO.addModifyNode(con, request.getNodeList(), request.getInterfaceId(), userVO.getUserID(), userVO.getNetworkID());
                    String count = arguments.get("arg1");
                    message = arguments.get("arg2");
                    nodeCount = Integer.parseInt(count);
                    if (nodeCount < 0) {
                        con.rollback();
                        log.error(methodName, PretupsErrorCodesI.INTERFACES_ADDNODE_FAILED);

                    }
                    con.commit();
                    // Enter the details for modify interface on Admin Log
                    AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setSource(TypesI.LOGGER_INTERFACE_SOURCE);
                    adminOperationVO.setDate(currentDate);
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                    adminOperationVO.setInfo("Interface " + interfaceVO.getInterfaceDescription() + " modified successfully. " + " Other Info " + message + "," + deleteMsg.toString());
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    AdminOperationLog.log(adminOperationVO);

                }else {
                    con.rollback();
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ADD_INTERFACE_DETAIL_FAIL);
                }
            }
            String interfaceID[] = {interfaceVO.getInterfaceDescription(), interfaceVO.getInterfaceId() };

            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INTERFACES_MODIFY_INTERFACE_SUCCESS, interfaceID);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.INTERFACES_MODIFY_INTERFACE_SUCCESS);


            BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.INTERFACES_MODIFY_INTERFACE_SUCCESS, interfaceID);
            final PushMessage pushMessage = new PushMessage(userVO.getMsisdn(), btslMessage, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
                    (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), userVO.getNetworkID());
            pushMessage.push();
        }finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }
        }
        return response;
    }

    @Override
    public InterfaceDetailResponseVO deleteInterfaceDetails(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con, String loginID, Locale locale, String interfaceId) throws Exception {
        final String methodName = "deleteInterfaceDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        InterfaceDetailResponseVO response = new InterfaceDetailResponseVO();
        try{
            InterfaceDAO interfaceDAO = new InterfaceDAO();
            UserDAO userDAO = new UserDAO();
            InterfaceVO interfaceVO = new InterfaceVO();
            UserVO userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            ArrayList<InterfaceVO> interfaceDetailList = null;
            interfaceDetailList = interfaceDAO.getInterfaceDetailsByInterfaceID(con, interfaceId);

            Date currentDate = new Date();
            interfaceVO.setModifiedOn(currentDate);

            interfaceVO.setModifiedBy(userVO.getUserID());
            interfaceVO.setInterfaceId(interfaceId);
            interfaceVO.setInterfaceTypeId(interfaceDetailList.get(0).getInterfaceTypeId());
            interfaceVO.setInterfaceDescription(interfaceDetailList.get(0).getInterfaceDescription());
            // Soft Delete the interface corresponding to the unique
            // interface id

            if (interfaceDetailList.get(0).getStatusCode().equalsIgnoreCase("N")) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACES_DELETE_DOES_NOT_EXIST);
            }

            if (interfaceDAO.isInterfaceExistsInInterfaceNwkPrefix(con, interfaceVO.getInterfaceId())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACES_DELETE_INTERFACENWKPREFIXFOUND_ERROR);
            } else if (interfaceDAO.isInterfaceExistsInInterfaceNwkMapping(con, interfaceVO.getInterfaceId())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACES_DELETE_INTERFACENETWORKMAPPINGFOUND_ERROR);
            }
            if (interfaceVO.getInterfaceTypeId().equals((PretupsI.INTERFACE_CATEGORY_IAT))) {
                if (interfaceDAO.isInterfaceExistsInIATMapping(con, interfaceVO.getInterfaceId())) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACES_DELETE_IATINTERFACECOUNTRYMAPPING_ERROR);
                }
            }

            if (interfaceDAO.deleteInterface(con, interfaceVO) == 1) {
                //soft delete all the nodes if any
                interfaceDAO.deleteAllNodes(con, interfaceVO.getInterfaceId(), userVO.getUserID());

                con.commit();
                // Enter the details for delete Interface on Admin Log
                AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                adminOperationVO.setSource(TypesI.LOGGER_INTERFACE_SOURCE);
                adminOperationVO.setInfo("Interface " + interfaceVO.getInterfaceDescription() + " deleted successfully");
                AdminOperationLog.log(adminOperationVO);
                BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.INTERFACES_DELETE_SUCCESS);
            } else {
                con.rollback();
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INTERFACES_DELETE_NOTSUCCESS);
            }
            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INTERFACES_DELETE_SUCCESS, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.INTERFACES_DELETE_SUCCESS);
        }finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }
        }

        return response;
    }

    @Override
    public InterfaceTypeResponseVO loadInterfaceType(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con, String loginID, Locale locale, String interfaceCategory) throws Exception, BTSLBaseException {
        final String METHOD_NAME = "loadInterfaceType";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        InterfaceTypeResponseVO response = new InterfaceTypeResponseVO();
        ArrayList<InterfaceTypeVO> interfaceTypeList = null;
        try{
            InterfaceDAO interfaceDAO = new InterfaceDAO();
            interfaceTypeList = interfaceDAO.loadInterfaceTypes(con, interfaceCategory);
            if(interfaceTypeList != null && !interfaceTypeList.isEmpty()){
                response.setInterfaceTypeList(interfaceTypeList);
                response.setStatus((HttpStatus.SC_OK));
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
            }else {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
            }
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    /**
     * Method constructInterfaceVO.
     * This method is used for constructing Value Object from request.
     *
     * @return InterfaceVO
     * @throws Exception
     */

    public InterfaceVO constructInterfaceVO(InterfaceVO p_interfaceVO, InterfaceDetailRequestVO request, String interfaceCategory) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("constructVOFromForm", "Entered");
        }
        p_interfaceVO.setInterfaceTypeId(request.getInterfaceType());
        p_interfaceVO.setExternalId(request.getExternalId());
        p_interfaceVO.setInterfaceDescription(request.getInterfaceName());
        p_interfaceVO.setStatus(request.getStatus());
        p_interfaceVO.setLanguage1Message(request.getLanguage1());
        p_interfaceVO.setLanguage2Message(request.getLanguage2());
        p_interfaceVO.setSingleStateTransaction(request.getSingleStageTransaction());
        p_interfaceVO.setStatusCode(request.getStatus());
        p_interfaceVO.setInterfaceCategory(interfaceCategory);
        p_interfaceVO.setValExpiryTime(Long.parseLong(request.getValidityExpiryTime()));
        p_interfaceVO.setTopUpExpiryTime(Long.parseLong(request.getTopupExpiryTime()));
        p_interfaceVO.setNoOfNodes(request.getNodeSize());
        if (log.isDebugEnabled()) {
            log.debug("constructInterfaceVO", "Exited Constructed VO From request Is " + p_interfaceVO);
        }
        return p_interfaceVO;
    }

    /**
     * Method constructModifyInterfaceVO.
     * This method is used for constructing Value Object from request.
     *
     * @return InterfaceVO
     * @throws Exception
     */

    public InterfaceVO constructModifyInterfaceVO(InterfaceVO p_interfaceVO, ModifyInterfaceDetailRequestVO request) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("constructVOFromForm", "Entered");
        }
        p_interfaceVO.setInterfaceId(request.getInterfaceId());
        p_interfaceVO.setInterfaceTypeId(request.getInterfaceType());
        p_interfaceVO.setExternalId(request.getExternalId());
        p_interfaceVO.setInterfaceDescription(request.getInterfaceName());
        p_interfaceVO.setStatus(request.getStatus());
        p_interfaceVO.setLanguage1Message(request.getLanguage1());
        p_interfaceVO.setLanguage2Message(request.getLanguage2());
        p_interfaceVO.setSingleStateTransaction(request.getSingleStageTransaction());
        p_interfaceVO.setStatusCode(request.getStatus());
        p_interfaceVO.setInterfaceCategory(request.getInterfaceCategory());
        p_interfaceVO.setValExpiryTime(Long.parseLong(request.getValidityExpiryTime()));
        p_interfaceVO.setTopUpExpiryTime(Long.parseLong(request.getTopupExpiryTime()));
        p_interfaceVO.setNoOfNodes(request.getNodeSize());
        if (log.isDebugEnabled()) {
            log.debug("constructInterfaceVO", "Exited Constructed VO From request Is " + p_interfaceVO);
        }
        return p_interfaceVO;
    }

}
