package com.restapi.superadmin.sublookup.service;

import com.btsl.common.*;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.master.businesslogic.SubLookUpVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.superadmin.sublookup.requestVO.ModifySubLookUpRequestVO;
import com.restapi.superadmin.sublookup.requestVO.SubLookUpRequestVO;
import com.restapi.superadmin.sublookup.responseVO.*;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service("SubLookUpService")
public class SubLookUpServiceImpl implements SubLookUpService {
    public static final Log LOG = LogFactory.getLog(SubLookUpServiceImpl.class.getName());
    public static final String CLASS_NAME = "SubLookUpServiceImpl";

    @Override
    public LookUpListResponseVO loadLookUpList(Connection con, Locale locale) throws BTSLBaseException {
        final String METHOD_NAME = "loadLookUpList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        LookUpListResponseVO response = new LookUpListResponseVO();
        SubLookUpDAO sublookupDAO = new SubLookUpDAO();
        try {
            List<ListValueVO> lookupList = sublookupDAO.loadLookup(con);

            List<LookUpVO> list = lookupList.stream()
                    .map(listValueVO -> {
                        LookUpVO lookupVO = new LookUpVO();
                        lookupVO.setLookUpCode(listValueVO.getValue());
                        lookupVO.setLookUpName(listValueVO.getLabel());
                        lookupVO.setLookUpType(listValueVO.getType());
                        return lookupVO;
                    })
                    .collect(Collectors.toList());

            response.setLookUpListVO((ArrayList<LookUpVO>) list);
            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.SUCCESS);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    @Override
    public SubLookUpListResponseVO loadSubLookUpList(Connection con, Locale locale, String lookUpCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadSubLookUpList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        SubLookUpListResponseVO response = new SubLookUpListResponseVO();
        SubLookUpDAO sublookupDAO = new SubLookUpDAO();
        try {
            List<ListValueVO> lookupList = sublookupDAO.loadLookup(con);
            ListValueVO listVO = BTSLUtil.getOptionDesc(lookUpCode, lookupList);
            if (listVO.getType() != null) {
                List<SubLookUpVO> subLookUpList = sublookupDAO.loadSublookupByLookUpCode(con, lookUpCode);
                if (subLookUpList != null || subLookUpList.isEmpty()) {
                    List<SubLookUpsVO> list = subLookUpList.stream()
                            .map(subLookUp -> {
                                SubLookUpsVO subLookUpsVO = new SubLookUpsVO();
                                subLookUpsVO.setSubLookUpCode(subLookUp.getSubLookupCode());
                                subLookUpsVO.setSubLookUpName(subLookUp.getSubLookupName());
                                subLookUpsVO.setDeleteAllowed(subLookUp.getDeleteAlowed());

                                return subLookUpsVO;
                            }).collect(Collectors.toList());
                    response.setSubLookUpList((ArrayList<SubLookUpsVO>) list);
                    response.setStatus((HttpStatus.SC_OK));
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.SUCCESS);
                } else {
                    response.setStatus((HttpStatus.SC_BAD_REQUEST));
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_RECORDS_FOUND, null);
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.NO_RECORDS_FOUND);
                }
            } else {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_LOOKUP_CODE);
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    @Override
    public BaseResponse addSubLookUp(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, SubLookUpRequestVO requestVO) throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "addSubLookUp";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        SubLookUpDAO sublookupDAO = new SubLookUpDAO();
        SubLookUpVO sublookupVO = new SubLookUpVO();
        BaseResponse response = new BaseResponse();
        int addCount = -1;
        try {
            Objects.requireNonNull(requestVO.getLookUpCode());
            if (requestVO.getLookUpCode().isEmpty())
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_LOOKUPCODE_BLANK);
        } catch (NullPointerException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_LOOKUPCODE_BLANK);
        }
        try {
            Objects.requireNonNull(requestVO.getSubLookUpName());
            if (requestVO.getSubLookUpName().isEmpty())
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_BLANK, new String[] {"name"});
        } catch (NullPointerException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_BLANK, new String[] {"name"});
        }
        validateSubLookupName(requestVO.getSubLookUpName());

        try {
            String idType = PretupsI.SUB_LOOKUP_ID;
            StringBuffer subLookupCode = new StringBuffer();
            if (sublookupDAO.isExists(con, requestVO.getSubLookUpName(), requestVO.getLookUpCode(), "null")) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_RECORD_ALREADYEXISTS);
            } else {
                long subLookupId = IDGenerator.getNextID(idType, PretupsI.ALL);
                int zeroes = 5 - (idType.length() + Long.toString(subLookupId).length());
                for (int count = 0; count < zeroes; count++) {
                    subLookupCode.append(0);
                }
                subLookupCode.insert(0, idType);
                subLookupCode.append(Long.toString(subLookupId));
                List<ListValueVO> lookupList = sublookupDAO.loadLookup(con);
                ListValueVO listVO = BTSLUtil.getOptionDesc(requestVO.getLookUpCode(), lookupList);
                Date currentDate = new Date(System.currentTimeMillis());
                if(listVO.getType() != null) {
                    sublookupVO.setLookupCode(requestVO.getLookUpCode());
                    sublookupVO.setLookupType(listVO.getType());
                    sublookupVO.setSubLookupName(requestVO.getSubLookUpName());
                    sublookupVO.setSubLookupCode(subLookupCode.toString());
                    sublookupVO.setCreatedOn(currentDate);
                    sublookupVO.setModifiedOn(currentDate);
                    sublookupVO.setCreatedBy(userVO.getUserID());
                    sublookupVO.setModifiedBy(userVO.getUserID());
                }else {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_LOOKUP_CODE);
                }
                addCount = sublookupDAO.addSubLookup(con, sublookupVO);
                if (addCount > 0) {
                    mcomCon.finalCommit();
                    AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setSource(TypesI.LOGGER_SUBLOOKUP_SOURCE);
                    adminOperationVO.setDate(currentDate);
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                    adminOperationVO.setInfo("SubLookUp " + sublookupVO.getSubLookupName() + " added successfully");
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    AdminOperationLog.log(adminOperationVO);

                    response.setStatus((HttpStatus.SC_OK));
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MASTER_SUBLOOKUP_SUCCESS, new String[] {"added"});
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.MASTER_SUBLOOKUP_SUCCESS);
                } else {
                    mcomCon.finalRollback();
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_ERROR, new String[] {"added"});
                }
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting");
            }
        }
        return response;
    }

    @Override
    public SubLookUpResponseVO loadSubLookUpDetails(Connection con, Locale locale, String subLookUpCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadSubLookUpDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        SubLookUpResponseVO response = new SubLookUpResponseVO();
        SubLookUpDAO sublookupDAO = new SubLookUpDAO();
        try {
            SubLookUpVO subLookUpVO = sublookupDAO.loadSublookupBySubLookUpCode(con, subLookUpCode);
            if (subLookUpVO.getSubLookupCode() != null) {
                List<ListValueVO> lookupList = sublookupDAO.loadLookup(con);
                ListValueVO lookupsVO = BTSLUtil.getOptionDesc(subLookUpVO.getLookupCode(), lookupList);
                if (lookupsVO.getType() != null) {
                    response.setLookUpCode(lookupsVO.getValue());
                    response.setLookUpName(lookupsVO.getLabel());
                    response.setSubLookUpName(subLookUpVO.getSubLookupName());
                    response.setSubLookUpCode(subLookUpVO.getSubLookupCode());
                }else{
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_LOOKUP_CODE);
                }
            } else {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_SUBLOOKUP_CODE);
            }
            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.SUCCESS);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting");
            }
        }
        return response;
    }

    @Override
    public BaseResponse modifySubLookUp(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ModifySubLookUpRequestVO request) throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "modifySubLookUp";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        BaseResponse response = new BaseResponse();
        SubLookUpDAO sublookupDAO = new SubLookUpDAO();
        int updateCount = -1;
        try {
            Objects.requireNonNull(request.getLookUpCode());
            if (request.getLookUpCode().isEmpty())
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_LOOKUPCODE_BLANK);
        } catch (NullPointerException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_LOOKUPCODE_BLANK);
        }
        try {
            Objects.requireNonNull(request.getSubLookUpName());
            if (request.getSubLookUpName().isEmpty())
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_BLANK, new String[] {"name"});
        } catch (NullPointerException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_BLANK, new String[] {"name"});
        }
        try {
            Objects.requireNonNull(request.getSubLookUpCode());
            if (request.getSubLookUpCode().isEmpty())
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_BLANK, new String[] {"code"});
        } catch (NullPointerException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_BLANK, new String[] {"code"});
        }

        validateSubLookupName(request.getSubLookUpName());
        try {
            if (sublookupDAO.isExists(con, request.getSubLookUpName(), request.getLookUpCode(), request.getSubLookUpCode())) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_RECORD_ALREADYEXISTS);
            } else {
                SubLookUpVO sublookupVO = new SubLookUpVO();
                List<ListValueVO> lookupList = sublookupDAO.loadLookup(con);
                ListValueVO lookupsVO = BTSLUtil.getOptionDesc(request.getLookUpCode(), lookupList);
                Date currentDate = new Date(System.currentTimeMillis());
                if(lookupsVO.getType() != null) {
                    sublookupVO.setLookupCode(request.getLookUpCode());
                    sublookupVO.setLookupType(lookupsVO.getType());
                    sublookupVO.setSubLookupName(request.getSubLookUpName());
                    sublookupVO.setSubLookupCode(request.getSubLookUpCode());
                    sublookupVO.setModifiedOn(currentDate);
                    sublookupVO.setModifiedBy(userVO.getUserID());
                }else{
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ERROR_INVALID_LOOKUP_CODE);
                }
                updateCount = sublookupDAO.updateSubLookup(con, sublookupVO);
                if (updateCount > 0) {
                    mcomCon.finalCommit();
                    AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setSource(TypesI.LOGGER_SUBLOOKUP_SOURCE);
                    adminOperationVO.setDate(currentDate);
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                    adminOperationVO.setInfo("SubLookUp name modified successfully to " + request.getSubLookUpName());
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    AdminOperationLog.log(adminOperationVO);
                } else {
                    mcomCon.finalRollback();
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_ERROR, new String[] {"modified"});
                }
                response.setStatus((HttpStatus.SC_OK));
                String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MASTER_SUBLOOKUP_SUCCESS, new String[] {"modified"});
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.MASTER_SUBLOOKUP_SUCCESS);
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting");
            }
        }
        return response;
    }

    @Override
    public BaseResponse deleteSubLookUp(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, String subLookUpCode) throws BTSLBaseException, SQLException {
        final String METHOD_NAME = "deleteSubLookup";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        try {
            Objects.requireNonNull(subLookUpCode);
            if (subLookUpCode.isEmpty())
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_BLANK, new String[] {"code"});
        } catch (NullPointerException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_BLANK, new String[] {"code"});
        }
        int updateCount = -1;
        BaseResponse response = new BaseResponse();
        SubLookUpDAO sublookupDAO = new SubLookUpDAO();
        try {
            SubLookUpVO sublookupVO = new SubLookUpVO();
            sublookupVO.setSubLookupCode(subLookUpCode);
            Date currentDate = new Date(System.currentTimeMillis());
            sublookupVO.setModifiedOn(currentDate);
            sublookupVO.setModifiedBy(userVO.getUserID());
            SubLookUpVO subLookDBVO = sublookupDAO.loadSublookupBySubLookUpCode(con, subLookUpCode);
            if(subLookDBVO.getSubLookupCode() == null){
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_SUBLOOKUP_CODE);
            }
            updateCount = sublookupDAO.deleteSubLookup(con, sublookupVO);
            if (updateCount > 0) {
                mcomCon.finalCommit();
                AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                adminOperationVO.setSource(TypesI.LOGGER_SUBLOOKUP_SOURCE);
                adminOperationVO.setInfo("SubLookUp " + subLookDBVO.getSubLookupName() + " deleted successfully");
                AdminOperationLog.log(adminOperationVO);
            } else {
                mcomCon.finalRollback();
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MASTER_SUBLOOKUP_ERROR, new String[] {"deleted"});
            }
            response.setStatus((HttpStatus.SC_OK));
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MASTER_SUBLOOKUP_SUCCESS, new String[] {"details deleted"});
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.MASTER_SUBLOOKUP_SUCCESS);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting");
            }
        }
        return response;
    }

    public void validateSubLookupName(String SubLookupName) throws BTSLBaseException {
        final String METHOD_NAME = "validateSubLookupName";
        if(SubLookupName.length()<3){
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MIN_MAX_LENGTH, new String[] {"Sublookup name","less","3"});
        }
        if(SubLookupName.length()>50){
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MIN_MAX_LENGTH, new String[] {"Sublookup name","more","50"});
        }

    }
}

