package com.restapi.superadmin.STKServices.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.generator.ByteCodeGeneratorI;
import com.btsl.ota.generator.WMLValidator;
import com.btsl.ota.services.businesslogic.ServiceSetVO;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.ota.services.businesslogic.SimServiceCategoriesVO;
import com.btsl.ota.services.businesslogic.UserServicesVO;
import com.btsl.ota.util.OtaMessage;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyDAO;
import com.btsl.pretups.iccidkeymgmt.businesslogic.PosKeyVO;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.stk.Message348;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.superadmin.STKServices.requestVO.AddServiceRequestVO;
import com.restapi.superadmin.STKServices.requestVO.AssignServiceRequestVO;
import com.restapi.superadmin.STKServices.requestVO.ModifyServiceRequestVO;
import com.restapi.superadmin.STKServices.requestVO.PushWmlRequestVO;
import com.restapi.superadmin.STKServices.responseVO.*;
import com.web.ota.services.businesslogic.ServicesWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

@Service("STKServicesService")
public class STKServicesServiceImpl implements STKServicesService{

    public static final Log LOG = LogFactory.getLog(STKServicesServiceImpl.class.getName());
    public static final String CLASS_NAME = "STKServicesServiceImpl";

    @Override
    public UserTypeServiceListResponseVO userTypeServiceList(Connection con) throws BTSLBaseException {
        final String METHOD_NAME = "userTypeServiceList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        UserTypeServiceListResponseVO response = new UserTypeServiceListResponseVO();
        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
        generateUserTypeServiceListResponse(categoryWebDAO.loadCategoryCodeNameListVO(con), servicesWebDAO.loadSIMServiceSet(con), response);

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

        return response;
    }

    @Override
    public GenerateByteCodeResponseVO generateByteCode(Connection con, String wmlCode, String description, String serviceSetID) throws Exception {
        final String METHOD_NAME = "generateByteCode";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        try {
            wmlCode = java.net.URLDecoder.decode(wmlCode, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_CODE_INVALID);
        }

        generateByteCodeValidations(wmlCode, description, serviceSetID);
        GenerateByteCodeResponseVO response = new GenerateByteCodeResponseVO();

        try {
            response.setByteCode(new WMLValidator().SAXValidation(wmlCode));
            response.setByteCodeLength(PretupsI.EMPTY + response.getByteCode().length() / 2);
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_CODE_FAIL);
        }

        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        ArrayList serviceList = servicesWebDAO.loadSIMServiceSet(con);
        ServiceSetVO serviceVO = null;
        boolean serviceSetValid = false;
        for (Object o : serviceList) {
            serviceVO = (ServiceSetVO) o;
            if (serviceVO.getId().equals(serviceSetID)  || serviceSetID.equals(PretupsI.ALL)) {
                response.setServiceSetName(serviceVO.getLanguage1() + PretupsI.HYPHEN + serviceVO.getLanguage2());
                serviceSetValid = true;
                break;
            }
        }

        if (!serviceSetValid)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

        return response;
    }

    @Override
    public void pushWml(Connection con, PushWmlRequestVO request, UserVO userVO) throws Exception {
        final String METHOD_NAME = "pushWml";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        try {
            request.setWmlCode(java.net.URLDecoder.decode(request.getWmlCode(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_CODE_INVALID);
        }

        pushWmlValidations(con, request);
        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        ArrayList serviceList = servicesWebDAO.loadSIMServiceSet(con);
        ServiceSetVO serviceVO = null;
        boolean serviceSetValid = false;
        for (Object o : serviceList) {
            serviceVO = (ServiceSetVO) o;
            if (serviceVO.getId().equals(request.getServiceSetID()) || request.getServiceSetID().equals(PretupsI.ALL)) {
                serviceSetValid = true;
                break;
            }
        }

        if (!serviceSetValid)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);

        ArrayList list = getList(request);
        NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(request.getMobileNo())));
        if (networkPrefixVO == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_UNSUPPORTED_NETWORK);
        }
        String networkCode = networkPrefixVO.getNetworkCode();
        if (networkCode == null || !networkCode.equals(userVO.getNetworkID())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_UNSUPPORTED_NETWORK);
        }
        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        if (!channelUserDAO.isPhoneExists(con, request.getMobileNo())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSISDN_NOT_EXISTS);
        }
        String key;
        PosKeyDAO posKeyDAO = new PosKeyDAO();
        String msisdn = PretupsBL.getFilteredMSISDN(request.getMobileNo());
        PosKeyVO posKeyVO = posKeyDAO.loadPosKeyByMsisdn(con, msisdn);
        if (posKeyVO == null) {
            key = null;
        } else {
            key = posKeyVO.getKey();
        }
        try {
            new OtaMessage().OtaMessageSender(list, msisdn, key, userVO.getUserID());
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SMS_NOT_SENT);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

    }

    @Override
    public void addService(Connection con, MComConnectionI mComCon, AddServiceRequestVO request, UserVO userVO) throws Exception {
        final String METHOD_NAME = "addService";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        try {
            request.setWmlCode(java.net.URLDecoder.decode(request.getWmlCode(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_CODE_INVALID);
        }

        addServiceValidations(con, request);
        ServicesVO servicesVO = new ServicesVO();
        getServiceVO(request, servicesVO);
        if (request.getLabel2().startsWith("80")) {
            servicesVO.setLabel2(convertMenuText(request.getLabel2()));
        } else {
            servicesVO.setLabel2(request.getLabel2());
        }
        if ("Y".equals(request.getStatus())) {
            servicesVO.setMajorVersion("0");
            servicesVO.setMinorVersion("0");
        } else {
            servicesVO.setMajorVersion(PretupsI.STK_DRAFT_VERSION);
            servicesVO.setMinorVersion(PretupsI.STK_DRAFT_VERSION);
        }
        Date currentDate = new Date();
        servicesVO.setCreatedOn(currentDate);
        servicesVO.setModifedOn(currentDate);
        servicesVO.setCreatedBy(userVO.getUserID());
        servicesVO.setModifiedBy(userVO.getUserID());
        servicesVO.setLength(request.getBytecode().length() / 2);
        servicesVO.setUserType(PretupsI.ALL);
        servicesVO.setAllowedToUsers(PretupsI.ALL);

        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        int serviceID = servicesWebDAO.loadNextServiceID(con, servicesVO.getServiceSetID());

        ArrayList list = getCategoryList(request, serviceID, servicesVO);

        int addServiceCategoryCount = -1;
        int addCount = -1;
        addServiceCategoryCount = servicesWebDAO.addSIMServiceCategories(con, list);
        if (addServiceCategoryCount > 0) {
            servicesVO.setServiceID(String.valueOf(serviceID));
            addCount = servicesWebDAO.addSIMService(con, servicesVO);
            if (addCount > 0) {
                mComCon.finalCommit();
                AdminOperationVO adminOperationVO = getAdminOperationVO(userVO, currentDate, servicesVO,TypesI.LOGGER_STK_SERVICE_ADD,TypesI.LOGGER_OPERATION_ADD,PretupsI.SERVICE_SUCC_ADDED);
                AdminOperationLog.log(adminOperationVO);
            } else {
                mComCon.finalRollback();
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ADD_SERVICE_FAIL);
            }
        } else {
            mComCon.finalRollback();
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ADD_SERVICE_FAIL);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

    }

    private void addServiceValidations(Connection con, AddServiceRequestVO request) throws Exception {
        final String METHOD_NAME = "addServiceValidations";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        validateNonNullAndNonEmpty(request.getServiceSetID(), METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_NULL);
        validateNonNullAndNonEmpty(request.getLabel1(), METHOD_NAME, PretupsErrorCodesI.LABEL1_NULL);
        validateNonNullAndNonEmpty(request.getLabel2(), METHOD_NAME, PretupsErrorCodesI.LABEL2_NULL);
        validateNonNullAndNonEmpty(request.getWmlCode(), METHOD_NAME, PretupsErrorCodesI.WML_CODE_NULL);
        validateNonNullAndNonEmpty(request.getBytecode(), METHOD_NAME, PretupsErrorCodesI.BYTECODE_NULL);
        validateNonNullAndNonEmpty(request.getStatus(), METHOD_NAME, PretupsErrorCodesI.STATUS_NULL);

        if (request.getCategoriesList() == null || request.getCategoriesList().length == 0) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_TYPE_LIST_NULL);
        }
        if (!Pattern.compile(Constants.getProperty(PretupsI.STK_LABEL2_PATTERN)).matcher(request.getLabel2()).matches())
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.LABEL2_INVALID);

        if (!Objects.equals(request.getStatus(), PretupsI.YES) && !Objects.equals(request.getStatus(), PretupsI.STK_SAVE_DRAFT))
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_STATUS);

        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        ArrayList serviceList = servicesWebDAO.loadSIMServiceSet(con);
        ServiceSetVO serviceVO = null;
        boolean serviceSetValid = false;
        for (Object o : serviceList) {
            serviceVO = (ServiceSetVO) o;
            if (serviceVO.getId().equals(request.getServiceSetID()) || request.getServiceSetID().equals(PretupsI.ALL)) {
                serviceSetValid = true;
                break;
            }
        }

        if (!serviceSetValid)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);

        String checkWMl;
        try {
            checkWMl = new WMLValidator().SAXValidation(request.getWmlCode());
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_CODE_INVALID);
        }
        if (!Objects.equals(request.getBytecode(), checkWMl)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.BYTECODE_INVALID);
        }

        boolean categoryValid;
        SimProfileCategoryListResponseVO response = new SimProfileCategoryListResponseVO();
        ArrayList<STKServiceListValueVO> categoryList;
        CategoryWebDAO categoryWebDAO=new CategoryWebDAO();
        generateUserTypeCategoryListResponse(categoryWebDAO.loadCategoryCodeNameListVO(con), response);
        categoryList=response.getUserTypeList();
        String[] reqCatList = request.getCategoriesList();
        for (String catCode: reqCatList) {
            categoryValid = false;
            for (STKServiceListValueVO o : categoryList) {
                if (o.getValue().equals(catCode)) {
                    categoryValid = true;
                    break;
                }
            }
            if (!categoryValid) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_TYPE_LIST_INVALID);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }
    }

    @Override
    public SimProfileCategoryListResponseVO simProfileCategoryList(Connection con) throws BTSLBaseException {

        final String METHOD_NAME = "simProfileCategoryList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        SimProfileCategoryListResponseVO response = new SimProfileCategoryListResponseVO();
        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
        generateSimProfileCategoryListResponse(categoryWebDAO.loadCategoryForSmsVO(con), categoryWebDAO.loadStkProfileListVO(con), servicesWebDAO.getMasterSimProfileList(con), response);

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

        return response;

    }

    private void generateSimProfileCategoryListResponse(ArrayList<ListValueVO> userTypeList, ArrayList<ListValueVO> profileList, ArrayList<ListValueVO> simProfileList, SimProfileCategoryListResponseVO response) {
        ArrayList<STKServiceListValueVO> userTypeListVOS = new ArrayList<>();
        ArrayList<STKServiceListValueVO> profileListVOS = new ArrayList<>();
        ArrayList<STKServiceListValueVO> simProfileListVOS = new ArrayList<>();
        for (ListValueVO o: userTypeList) {
            STKServiceListValueVO stkServiceListValueVO = new STKServiceListValueVO();
            stkServiceListValueVO.setLabel(o.getLabel());
            stkServiceListValueVO.setValue(o.getValue());
            userTypeListVOS.add(stkServiceListValueVO);
        }
        for (ListValueVO o: profileList) {
            STKServiceListValueVO stkServiceListValueVO = new STKServiceListValueVO();
            stkServiceListValueVO.setLabel(o.getLabel());
            stkServiceListValueVO.setValue(o.getValue());
            profileListVOS.add(stkServiceListValueVO);
        }
        for (ListValueVO o: simProfileList) {
            STKServiceListValueVO stkServiceListValueVO = new STKServiceListValueVO();
            stkServiceListValueVO.setLabel(o.getLabel());
            stkServiceListValueVO.setValue(o.getValue());
            simProfileListVOS.add(stkServiceListValueVO);
        }
        response.setUserTypeList(userTypeListVOS);
        response.setProfileList(profileListVOS);
        response.setSimProfileList(simProfileListVOS);
    }

    @Override
    public UserSimServicesListResponseVO userSimServicesList(Connection con, String categoryCode, String profileCode, String simProfileCode, String networkCode) throws BTSLBaseException {

        final String METHOD_NAME = "userSimServicesList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        userSimServicesListValidations(con, categoryCode, profileCode, simProfileCode);
        UserSimServicesListResponseVO response = new UserSimServicesListResponseVO();
        ArrayList<UserServicesVO> newSimProfileList = new ArrayList();
        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        ArrayList simProfileList = servicesWebDAO.loadCurrentUserSIMServiceProfileList(con, categoryCode, profileCode, networkCode, simProfileCode);

        int currentPosition = 1;
        if (simProfileList != null && !simProfileList.isEmpty()) {
            UserServicesVO userServicesVO = null;
            int simProfilesList=simProfileList.size();
            for (Object o : simProfileList) {
                userServicesVO = (UserServicesVO) o;
                int position = userServicesVO.getPosition();
                for (int i = currentPosition; i <= 16; i++) {
                    if (position == i) {
                        newSimProfileList.add(userServicesVO);
                        currentPosition = i + 1;
                        break;
                    } else {
                        newSimProfileList.add(new UserServicesVO());
                        newSimProfileList.getLast().setPosition(i);
                    }
                }
            }
            for (int i = currentPosition; i <= 16; i++) {
                newSimProfileList.add(new UserServicesVO());
                newSimProfileList.getLast().setPosition(i);
            }
        } else {
            for (int i = currentPosition; i <= 16; i++) {
                newSimProfileList.add(new UserServicesVO());
                newSimProfileList.getLast().setPosition(i);
            }
        }

        generateUserSimServicesListResponse(newSimProfileList, response);

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

        return response;

    }

    private void generateUserSimServicesListResponse(ArrayList<UserServicesVO> newSimProfileList, UserSimServicesListResponseVO response) {
        ArrayList<STKUserServicesVO> stkUserServicesVOS = new ArrayList<>();
        for (UserServicesVO o: newSimProfileList) {
            STKUserServicesVO stkUserServicesVO = new STKUserServicesVO();
            stkUserServicesVO.setPosition(o.getPosition());
            stkUserServicesVO.setServiceID(o.getServiceID());
            stkUserServicesVO.setMinorVersion(o.getMinorVersion());
            stkUserServicesVO.setMajorVersion(o.getMajorVersion());
            stkUserServicesVO.setLabel1(o.getLabel1());
            stkUserServicesVO.setLabel2(o.getLabel2());
            stkUserServicesVO.setStatus(o.getStatus());
            stkUserServicesVO.setDescription(o.getDescription());
            stkUserServicesVO.setOffset(o.getOffset());
            stkUserServicesVOS.add(stkUserServicesVO);
        }
        response.setUserSimServicesList(stkUserServicesVOS);
    }

    @Override
    public SimServicesListResponseVO simServicesList(Connection con, String categoryCode, String serviceSetID, String searchString, String networkCode) throws BTSLBaseException {

        final String METHOD_NAME = "simServicesList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        simServicesListValidations(con, categoryCode, serviceSetID, searchString);
        SimServicesListResponseVO response = new SimServicesListResponseVO();
        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        searchString = searchString.trim() + PretupsI.PERCENTAGE;

        if (BTSLUtil.isNullString(serviceSetID) || serviceSetID.isEmpty()) {
            serviceSetID = PretupsI.ALL;
        }

        if (categoryCode.equals(PretupsI.ALL))
            networkCode = PretupsI.ALL;

        generateSimServicesListResponse(servicesWebDAO.loadLatestSIMServiceListForSearch(con, categoryCode, networkCode, serviceSetID, searchString, PretupsI.BOOLEAN_TRUE), response);

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

        return response;
    }

    private void generateSimServicesListResponse(ArrayList<ServicesVO> simServicesList, SimServicesListResponseVO response) {
        ArrayList<STKServicesVO> stkServicesVOS = new ArrayList<>();
        for (ServicesVO o: simServicesList) {
            STKServicesVO stkServicesVO = new STKServicesVO();
            stkServicesVO.setStatus(o.getStatus());
            stkServicesVO.setLength(o.getLength());
            stkServicesVO.setMajorVersion(o.getMajorVersion());
            stkServicesVO.setMinorVersion(o.getMinorVersion());
            stkServicesVO.setDescription(o.getDescription());
            stkServicesVO.setLabel1(o.getLabel1());
            stkServicesVO.setLabel2(o.getLabel2());
            stkServicesVO.setModifiedBy(o.getModifiedBy());
            stkServicesVO.setServiceID(o.getServiceID());
            stkServicesVO.setModifiedOnAsString(o.getModifiedOnAsString());
            stkServicesVOS.add(stkServicesVO);
        }
        response.setSimServicesList(stkServicesVOS);
    }

    @Override
    public CalculateOffsetResponseVO calculateOffset(Connection con, String categoryCode, String profileCode, String simProfileCode, String serviceID, String byteCodeLength, int position, UserVO userVO) throws BTSLBaseException {

        final String METHOD_NAME = "calculateOffset";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        calculateOffsetValidations(con, categoryCode, profileCode, simProfileCode, serviceID, byteCodeLength, position);
        CalculateOffsetResponseVO response = new CalculateOffsetResponseVO();
        UserSimServicesListResponseVO userSimServicesListResponse = new UserSimServicesListResponseVO();
        ArrayList<UserServicesVO> filledList = null;
        ArrayList<UserServicesVO> freeOffsetLengthList = null;
        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        String networkCode = userVO.getNetworkID();
        long offset = 0;

        ArrayList<STKUserServicesVO> userSimServicesList = userSimServicesList(con, categoryCode, profileCode, simProfileCode, networkCode).getUserSimServicesList();
        boolean userServiceExists = false;
        UserServicesVO userServicesVO = null;

        if (!BTSLUtil.isNullString(serviceID) && (userSimServicesList != null && !userSimServicesList.isEmpty())) {
            for (STKUserServicesVO stkUserServicesVO : userSimServicesList) {
                if (serviceID.equals(stkUserServicesVO.getServiceID()) && position == stkUserServicesVO.getPosition()) {
                    offset = stkUserServicesVO.getOffset();
                }
                if (serviceID.equals(stkUserServicesVO.getServiceID()) && position != stkUserServicesVO.getPosition()) {
                    userServiceExists = true;
                    offset = stkUserServicesVO.getOffset();
                    break;
                }
            }
        }

        if (userServiceExists)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_SIM_SERVICE_EXISTS);

        filledList = servicesWebDAO.loadOffsetLengthUserSIMServiceList(con, categoryCode, profileCode, networkCode, simProfileCode);
        UserServicesVO newUserServicesVO = null;
        freeOffsetLengthList = new ArrayList<>();
        long prevOffset = 0;
        long prevlength = 0;
        long nextOffset = 0;
        long nextlength = 0;
        long offsetGap = 0;
        long byteFileLength = 0;

        try {
            byteFileLength = Long.parseLong(Constants.getProperty(PretupsI.BYTE_FILE_LENGTH));
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            byteFileLength = 5350;
        }

        for (int i = 0; i <filledList.size() ; i++) {
            userServicesVO = (UserServicesVO) filledList.get(i);
            nextOffset = userServicesVO.getOffset();
            nextlength = userServicesVO.getLength();
            offsetGap = nextOffset - (prevOffset + prevlength);

            if (offsetGap > 1) {
                newUserServicesVO = new UserServicesVO();
                newUserServicesVO.setOffset(prevOffset + prevlength);
                newUserServicesVO.setLength(offsetGap);
                freeOffsetLengthList.add(newUserServicesVO);
            }

            prevOffset = nextOffset;
            prevlength = nextlength;
            if ((nextOffset == offset) && (serviceID != null)) {
                newUserServicesVO = new UserServicesVO();
                newUserServicesVO.setOffset(nextOffset);
                newUserServicesVO.setLength(nextlength);
                freeOffsetLengthList.add(userServicesVO);
                filledList.remove(userServicesVO);
            }
        }

        newUserServicesVO = new UserServicesVO();
        offsetGap = byteFileLength - (prevOffset + prevlength);
        newUserServicesVO.setOffset(prevOffset + prevlength);
        newUserServicesVO.setLength(offsetGap);
        freeOffsetLengthList.add(newUserServicesVO);

        String newOffset = null;
        UserServicesVO userServiceVO = null;
        long serviceLength;
        try {
            serviceLength = Long.parseLong(byteCodeLength);
        } catch (NumberFormatException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.BYTE_CODE_LENGTH_INVALID);
        }
        int freeOffsLengthList=freeOffsetLengthList.size();
        for (int i = 0; i < freeOffsLengthList; i++) {
            userServiceVO = (UserServicesVO) freeOffsetLengthList.get(i);
            if ((userServiceVO.getLength() - 20) > serviceLength) {
                newOffset = "" + (userServiceVO.getOffset() + 10);
                break;
            }
        }

        generateCalculateOffsetResponse(freeOffsetLengthList, filledList, newOffset, response);

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

        return response;
    }

    private void generateCalculateOffsetResponse(ArrayList<UserServicesVO> freeOffsLengthList, ArrayList<UserServicesVO> filledList, String newOffset, CalculateOffsetResponseVO response) {
        ArrayList<STKUserServicesVO> usedOffsetList = new ArrayList<>();
        ArrayList<STKUserServicesVO> freeOffsetList = new ArrayList<>();
        for (UserServicesVO o: filledList) {
            STKUserServicesVO stkUserServicesVO = new STKUserServicesVO();
            stkUserServicesVO.setLength(o.getLength());
            stkUserServicesVO.setOffset(o.getOffset());
            usedOffsetList.add(stkUserServicesVO);
        }
        for (UserServicesVO o: freeOffsLengthList) {
            STKUserServicesVO stkUserServicesVO = new STKUserServicesVO();
            stkUserServicesVO.setLength(o.getLength());
            stkUserServicesVO.setOffset(o.getOffset());
            freeOffsetList.add(stkUserServicesVO);
        }
        response.setOffset(newOffset);
        response.setUsedOffsetList(usedOffsetList);
        response.setFreeOffsetList(freeOffsetList);

    }

    @Override
    public void assignService(Connection con, MComConnectionI mComCon, AssignServiceRequestVO request, UserVO userVO) throws BTSLBaseException, SQLException {

        final String METHOD_NAME = "assignService";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        assignServiceValidations(con, request);
        boolean isValid = false;
        UserServicesVO userServicesVO = new UserServicesVO();
        STKUserServicesVO stkUserServicesVO = null;
        long tempOffset = 0;
        long tempLength = 0;
        ArrayList freeOffsetLengthList = request.getFreeOffsetList();
        long offset;
        try {
            offset = Long.parseLong(request.getOffset());
        }
        catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.OFFSET_INVALID_CHAR);
        }
        long length;
        try {
            length = request.getServiceDetails().getLength();
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SIM_SERVICE_LENGTH_INVALID);
        }
        for (Object o : freeOffsetLengthList) {
            stkUserServicesVO = (STKUserServicesVO) o;
            try {
                tempOffset = stkUserServicesVO.getOffset();
                tempLength = stkUserServicesVO.getLength();
            } catch (Exception e) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FREE_OFFSET_LIST_INVALID);
            }
            if (offset >= tempOffset && (offset <= (tempOffset + tempLength)) && ((offset + length) <= (tempOffset + tempLength))) {
                isValid = true;
                break;
            }
        }
        if (!isValid)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.OFFSET_INVALID);

        boolean userServiceExists = false;
        ArrayList<STKUserServicesVO> userSimServicesList = userSimServicesList(con, request.getCategoryCode(), request.getProfileCode(), request.getSimProfileCode(), userVO.getNetworkID()).getUserSimServicesList();
        if (!BTSLUtil.isNullString(request.getServiceDetails().getServiceID()) && (userSimServicesList != null && !userSimServicesList.isEmpty())) {
            for (STKUserServicesVO stkUserServicesVOs : userSimServicesList) {
                if (request.getServiceDetails().getServiceID().equals(stkUserServicesVOs.getServiceID()) && request.getPosition() != stkUserServicesVOs.getPosition()) {
                    userServiceExists = true;
                    offset = stkUserServicesVOs.getOffset();
                    break;
                }
            }
        }

        if (userServiceExists)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_SIM_SERVICE_EXISTS);

        STKServicesVO serviceDetails = getServiceDetails(request, userServicesVO, userVO);
        getUserServicesDetails(userServicesVO, request, serviceDetails, userVO);

        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        if (servicesWebDAO.updateUserSIMService(con, userServicesVO)) {
            mComCon.finalCommit();
        } else {
            mComCon.finalRollback();
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_SIM_SERVICE_NOT_MODIFIED);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

    }

    private void validateNonNullAndNonEmpty(String validateMe, String methodName, String errorCode) throws BTSLBaseException {
        try {
            Objects.requireNonNull(validateMe);
            if (validateMe.isEmpty() || validateMe.equals("null"))
                throw new BTSLBaseException(CLASS_NAME, methodName, errorCode);
        } catch (NullPointerException e) {
            throw new BTSLBaseException(CLASS_NAME, methodName, errorCode);
        }
    }

    private void generateUserTypeServiceListResponse(ArrayList<ListValueVO> userTypeList, ArrayList<ServiceSetVO> serviceSetList, UserTypeServiceListResponseVO response) {
        ArrayList<STKServiceListValueVO> stkServiceListValueVOS = new ArrayList<>();
        for (ListValueVO o: userTypeList) {
            STKServiceListValueVO stkServiceListValueVO = new STKServiceListValueVO();
            stkServiceListValueVO.setLabel(o.getLabel());
            stkServiceListValueVO.setValue(o.getValue());
            stkServiceListValueVOS.add(stkServiceListValueVO);
        }
        response.setUserTypeList(stkServiceListValueVOS);
        response.setServiceSetList(serviceSetList);
    }

    private void generateByteCodeValidations(String wmlCode, String description, String serviceSetID) throws BTSLBaseException {
        final String METHOD_NAME = "generateByteCodeValidations";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        validateNonNullAndNonEmpty(wmlCode, METHOD_NAME, PretupsErrorCodesI.WML_CODE_NULL);
        validateNonNullAndNonEmpty(serviceSetID, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_NULL);

        if (!BTSLUtil.isNullString(wmlCode) && wmlCode.length() > 4000) {
            final String [] args = {PretupsI.WML_CODE, "4000"};
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_MAX_LENGTH, args);
        }
        if (!BTSLUtil.isNullString(description) && description.length() > 100) {
            final String [] args = {PretupsI.DESCRIPTION, "100"};
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DESCRIPTION_MAX_LENGTH, args);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

    }

    private void pushWmlValidations(Connection con, PushWmlRequestVO request) throws Exception {
        final String METHOD_NAME = "pushWMlValidations";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        try {
            request.setLength(request.getLength().replaceAll(PretupsI.CHECK_ZEROES, PretupsI.EMPTY));
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_LENGTH_INVALID);
        }

        validateNonNullAndNonEmpty(request.getLength(), METHOD_NAME, PretupsErrorCodesI.LENGTH_BYTECODE_NULL);
        validateNonNullAndNonEmpty(request.getMobileNo(), METHOD_NAME, PretupsErrorCodesI.MOBILE_NUMBER_NULL);
        validateNonNullAndNonEmpty(request.getServiceSetID(), METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_NULL);
        validateNonNullAndNonEmpty(request.getLabel1(), METHOD_NAME, PretupsErrorCodesI.LABEL1_NULL);
        validateNonNullAndNonEmpty(request.getLabel2(), METHOD_NAME, PretupsErrorCodesI.LABEL2_NULL);
        validateNonNullAndNonEmpty(request.getWmlCode(), METHOD_NAME, PretupsErrorCodesI.WML_CODE_NULL);
        validateNonNullAndNonEmpty(request.getBytecode(), METHOD_NAME, PretupsErrorCodesI.BYTECODE_NULL);

        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        ArrayList serviceList = servicesWebDAO.loadSIMServiceSet(con);

        ServiceSetVO serviceVO = null;
        boolean serviceSetValid = false;
        for (Object o : serviceList) {
            serviceVO = (ServiceSetVO) o;
            if (serviceVO.getId().equals(request.getServiceSetID()) || request.getServiceSetID().equals(PretupsI.ALL)) {
                serviceSetValid = true;
                break;
            }
        }

        if (!serviceSetValid)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);

        if (request.getPosition() != null && !request.getPosition().isEmpty()) {
            validateNonNullAndNonEmpty(request.getOffset(), METHOD_NAME, PretupsErrorCodesI.OFFSET_NULL);
        }

        try {
            if (request.getOffset() != null && !request.getOffset().isEmpty()) {
                validateNonNullAndNonEmpty(request.getPosition(), METHOD_NAME, PretupsErrorCodesI.POSITION_NULL);
                if (Integer.parseInt(request.getPosition()) < 1 || Integer.parseInt(request.getPosition()) > 16) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.POSITION_INVALID);
                }
            }
        } catch (NumberFormatException e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.POSITION_INVALID);
        }

        if (!Pattern.compile(Constants.getProperty(PretupsI.STK_LABEL2_PATTERN)).matcher(request.getLabel2()).matches())
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.LABEL2_INVALID);

        String checkWMl;
        try {
            checkWMl = new WMLValidator().SAXValidation(request.getWmlCode());
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_CODE_INVALID);
        }
        if (!Objects.equals(request.getBytecode(), checkWMl)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.BYTECODE_INVALID);
        }
        try {
            if ((Integer.parseInt(request.getLength()) != checkWMl.length() / 2)) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.BYTE_CODE_LENGTH_INVALID);
            }
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_LENGTH_INVALID);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

    }


    @NotNull
    private static ArrayList getList(PushWmlRequestVO request) throws BTSLBaseException {
        final String METHOD_NAME = "getList";
        ServicesVO servicesVO = new ServicesVO();
        servicesVO.setByteCode(String.valueOf(request.getBytecode()));
        if (request.getOffset() != null && !request.getOffset().isEmpty() &&
                request.getPosition() != null && !request.getPosition().isEmpty()) {
            servicesVO.setPosition(Integer.parseInt(request.getPosition()));
            try {
                servicesVO.setOffSet(Long.parseLong(request.getOffset()));
            } catch (Exception e) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.OFFSET_INVALID_CHAR);
            }
        }
        servicesVO.setServiceID("0");
        servicesVO.setMajorVersion("0");
        servicesVO.setMinorVersion("0");
        servicesVO.setLabel1(request.getLabel1());
        servicesVO.setLabel2(request.getLabel2());
        servicesVO.setLength(Integer.parseInt(request.getLength()));
        servicesVO.setStatus(PretupsI.YES);
        if (request.getOffset().isEmpty() && request.getPosition().isEmpty()){
            servicesVO.setOperation(ByteCodeGeneratorI.SENT_TEST_CARD);
        }
        else {
            servicesVO.setOperation(ByteCodeGeneratorI.ADD);
        }
        ArrayList list = new ArrayList();
        list.add(servicesVO);
        return list;
    }

    private void getServiceVO(AddServiceRequestVO request, ServicesVO servicesVO) {
        servicesVO.setByteCode(request.getBytecode());
        servicesVO.setWml(request.getWmlCode());
        servicesVO.setLabel1(request.getLabel1());
        servicesVO.setDescription(request.getDescription());
        servicesVO.setStatus(request.getStatus());
        servicesVO.setServiceSetID(request.getServiceSetID());
    }

    private static String convertMenuText(String oldHexString) throws BTSLBaseException {
        final String METHOD_NAME = "convertMenuText";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        byte Offset;
        int indx = 0;
        String newHex = null;
        String supportedLanguage = Constants.getProperty(PretupsI.SUPPORTED_LANGUAGE);

        if (supportedLanguage != null) {
            indx = supportedLanguage.indexOf(PretupsI.HI);
        }
        if (indx != -1) {
            Offset = 0x12;
        } else {
            Offset = 0x0c;
        }

        if (oldHexString.length() % 2 != 0) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WRONG_HEX_STRING);
        }
        byte[] oldArray = new byte[oldHexString.length() / 2];
        byte[] newArray = new byte[(oldArray.length - 1) / 2 + 3];
        if (Message348.binHexToBytes(oldHexString.toLowerCase(), oldArray, 0, oldArray.length) != oldArray.length) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.BYTE_CONVERSION_FAIL);
        }

        if (oldArray[0] != (byte) 0x80) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MISSING_80);
        }

        newArray[0] = (byte) 0x81;
        newArray[1] = (byte) ((oldArray.length - 1) / 2);
        newArray[2] = Offset;
        for (int i = 2, j = 3; i < oldArray.length; i += 2, j++) {
            if (oldArray[i] == 0x00) {
                j--;
            } else if (oldArray[i] == 0x20) {
                newArray[j] = oldArray[i];
            } else {
                newArray[j] = (byte) (oldArray[i] + (byte) 0x80);
            }

        }
        newHex = (Message348.bytesToBinHex(newArray)).toUpperCase();

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

        return newHex;
    }

    @NotNull
    private static ArrayList getCategoryList(AddServiceRequestVO request, int serviceID, ServicesVO servicesVO) {
        String[] categoriesList = request.getCategoriesList();
        ArrayList list = new ArrayList();
        SimServiceCategoriesVO categoryVO = null;
        for (String s : categoriesList) {
            categoryVO = new SimServiceCategoriesVO();
            categoryVO.setCategoryCode(s);
            categoryVO.setServiceID(serviceID);
            categoryVO.setMajorVersion(servicesVO.getMajorVersion());
            categoryVO.setMinorVersion(servicesVO.getMinorVersion());
            categoryVO.setServiceSetId(request.getServiceSetID());
            list.add(categoryVO);
        }
        return list;
    }

    @NotNull
    private static AdminOperationVO getAdminOperationVO(UserVO userVO, Date currentDate, ServicesVO servicesVO,String loggerSTKService,String loggerOperation,String message) {
        AdminOperationVO adminOperationVO = new AdminOperationVO();
        adminOperationVO.setSource(loggerSTKService);
        adminOperationVO.setDate(currentDate);
        adminOperationVO.setOperation(loggerOperation);
        adminOperationVO.setInfo(servicesVO.getLabel1() + PretupsI.SPACE + message);
        adminOperationVO.setLoginID(userVO.getLoginID());
        adminOperationVO.setUserID(userVO.getUserID());
        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
        adminOperationVO.setNetworkCode(userVO.getNetworkID());
        adminOperationVO.setMsisdn(userVO.getMsisdn());
        return adminOperationVO;
    }

    private void userSimServicesListValidations(Connection con, String categoryCode, String profileCode, String simProfileCode) throws BTSLBaseException {

        final String METHOD_NAME = "userSimServicesListValidations";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        validateNonNullAndNonEmpty(categoryCode, METHOD_NAME, PretupsErrorCodesI.CATEGORY_CODE_NULL);
        validateNonNullAndNonEmpty(profileCode, METHOD_NAME, PretupsErrorCodesI.PROFILE_CODE_NULL);
        validateNonNullAndNonEmpty(simProfileCode, METHOD_NAME, PretupsErrorCodesI.SIM_PROFILE_CODE_NULL);

        boolean categoryValid = false;
        boolean profileValid = false;
        boolean userSimProfileValid = false;
        SimProfileCategoryListResponseVO simProfileCategoryList = simProfileCategoryList(con);

        for (STKServiceListValueVO o : simProfileCategoryList.getUserTypeList()) {
            if (o.getValue().equals(categoryCode)) {
                categoryValid = true;
                break;
            }
        }
        if (!categoryValid) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_TYPE_INVALID);
        }

        for (STKServiceListValueVO o : simProfileCategoryList.getProfileList()) {
            if (o.getValue().split(PretupsI.COLON)[0].equals(profileCode)) {
                profileValid = true;
                break;
            }
        }
        if (!profileValid) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROFILE_CODE_INVALID);
        }

        for (STKServiceListValueVO o : simProfileCategoryList.getSimProfileList()) {
            if (o.getValue().equals(simProfileCode)) {
                userSimProfileValid = true;
                break;
            }
        }
        if (!userSimProfileValid) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SIM_PROFILE_CODE_INVALID);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

    }

    private void simServicesListValidations(Connection con, String categoryCode, String serviceSetID, String searchString) throws BTSLBaseException {

        final String METHOD_NAME = "simServicesListValidations";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        validateNonNullAndNonEmpty(categoryCode, METHOD_NAME, PretupsErrorCodesI.CATEGORY_CODE_NULL);
        validateNonNullAndNonEmpty(serviceSetID, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_NULL);

        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        ArrayList serviceList = servicesWebDAO.loadSIMServiceSet(con);

        ServiceSetVO serviceVO = null;
        boolean serviceSetValid = false;
        for (Object o : serviceList) {
            serviceVO = (ServiceSetVO) o;
            if (serviceVO.getId().equals(serviceSetID) || serviceSetID.equals(PretupsI.ALL)) {
                serviceSetValid = true;
                break;
            }
        }

        if (!serviceSetValid)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

    }

    private void calculateOffsetValidations(Connection con,  String categoryCode, String profileCode, String simProfileCode, String serviceID, String byteCodeLength, int position) throws BTSLBaseException {

        final String METHOD_NAME = "calculateOffsetValidations";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        validateNonNullAndNonEmpty(categoryCode, METHOD_NAME, PretupsErrorCodesI.CATEGORY_CODE_NULL);
        validateNonNullAndNonEmpty(profileCode, METHOD_NAME, PretupsErrorCodesI.PROFILE_CODE_NULL);
        validateNonNullAndNonEmpty(simProfileCode, METHOD_NAME, PretupsErrorCodesI.SIM_PROFILE_CODE_NULL);
        validateNonNullAndNonEmpty(serviceID, METHOD_NAME, PretupsErrorCodesI.SERVICE_ID_NULL);
        validateNonNullAndNonEmpty(byteCodeLength, METHOD_NAME, PretupsErrorCodesI.BYTE_CODE_LENGTH_NULL);
        validateNonNullAndNonEmpty(String.valueOf(position), METHOD_NAME, PretupsErrorCodesI.POSITION_NULL);

        if (position < 1 || position > 16)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.POSITION_INVALID);

        boolean categoryValid = false;
        boolean profileValid = false;
        boolean userSimProfileValid = false;
        SimProfileCategoryListResponseVO simProfileCategoryList = simProfileCategoryList(con);

        for (STKServiceListValueVO o : simProfileCategoryList.getUserTypeList()) {
            if (o.getValue().equals(categoryCode)) {
                categoryValid = true;
                break;
            }
        }
        if (!categoryValid) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_TYPE_INVALID);
        }

        for (STKServiceListValueVO o : simProfileCategoryList.getProfileList()) {
            if (o.getValue().split(PretupsI.COLON)[0].equals(profileCode)) {
                profileValid = true;
                break;
            }
        }
        if (!profileValid) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROFILE_CODE_INVALID);
        }

        for (STKServiceListValueVO o : simProfileCategoryList.getSimProfileList()) {
            if (o.getValue().equals(simProfileCode)) {
                userSimProfileValid = true;
                break;
            }
        }
        if (!userSimProfileValid) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SIM_PROFILE_CODE_INVALID);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

    }

    private void assignServiceValidations(Connection con, AssignServiceRequestVO request) throws BTSLBaseException {

        final String METHOD_NAME = "assignService";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        validateNonNullAndNonEmpty(request.getCategoryCode(), METHOD_NAME, PretupsErrorCodesI.CATEGORY_CODE_NULL);
        validateNonNullAndNonEmpty(request.getProfileCode(), METHOD_NAME, PretupsErrorCodesI.PROFILE_CODE_NULL);
        validateNonNullAndNonEmpty(request.getSimProfileCode(), METHOD_NAME, PretupsErrorCodesI.SIM_PROFILE_CODE_NULL);
        validateNonNullAndNonEmpty(request.getOffset(), METHOD_NAME, PretupsErrorCodesI.OFFSET_NULL);
        validateNonNullAndNonEmpty(request.getServiceDetails().getServiceID(), METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_NULL);
        validateNonNullAndNonEmpty(String.valueOf(request.getServiceDetails().getLength()), METHOD_NAME, PretupsErrorCodesI.SIM_SERVICE_LENGTH_NULL);
        validateNonNullAndNonEmpty(String.valueOf(request.getPosition()), METHOD_NAME, PretupsErrorCodesI.POSITION_NULL);
        validateNonNullAndNonEmpty(request.getUserServiceStatus(), METHOD_NAME, PretupsErrorCodesI.USER_SERVICE_STATUS_NULL);

        if (request.getPosition() < 1 || request.getPosition() > 16)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.POSITION_INVALID);

        if (!Objects.equals(request.getUserServiceStatus(), PretupsI.YES) && !Objects.equals(request.getUserServiceStatus(), PretupsI.NO))
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_SERVICE_STATUS_INVALID);
        if (request.getFreeOffsetList().isEmpty())
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FREE_OFFSET_LIST_NULL);

        boolean categoryValid = false;
        boolean profileValid = false;
        boolean userSimProfileValid = false;
        SimProfileCategoryListResponseVO simProfileCategoryList = simProfileCategoryList(con);

        for (STKServiceListValueVO o : simProfileCategoryList.getUserTypeList()) {
            if (o.getValue().equals(request.getCategoryCode())) {
                categoryValid = true;
                break;
            }
        }
        if (!categoryValid) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_TYPE_INVALID);
        }

        for (STKServiceListValueVO o : simProfileCategoryList.getProfileList()) {
            if (o.getValue().split(PretupsI.COLON)[0].equals(request.getProfileCode())) {
                profileValid = true;
                break;
            }
        }
        if (!profileValid) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROFILE_CODE_INVALID);
        }

        for (STKServiceListValueVO o : simProfileCategoryList.getSimProfileList()) {
            if (o.getValue().equals(request.getSimProfileCode())) {
                userSimProfileValid = true;
                break;
            }
        }
        if (!userSimProfileValid) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SIM_PROFILE_CODE_INVALID);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }

    }

    @NotNull
    private static STKServicesVO getServiceDetails(AssignServiceRequestVO request, UserServicesVO userServicesVO, UserVO userVO) throws BTSLBaseException {
        final String METHOD_NAME = "getServiceDetails";
        STKServicesVO serviceDetails = request.getServiceDetails();
        userServicesVO.setUserType(request.getCategoryCode());
        userServicesVO.setProfile(request.getProfileCode());
        userServicesVO.setPosition(request.getPosition());
        userServicesVO.setServiceID(serviceDetails.getServiceID());
        userServicesVO.setNewServiceID(serviceDetails.getServiceID());
        userServicesVO.setMajorVersion(serviceDetails.getMajorVersion());
        userServicesVO.setNewMajorVersion(serviceDetails.getMajorVersion());
        userServicesVO.setMinorVersion(serviceDetails.getMinorVersion());
        userServicesVO.setNewMinorVersion(serviceDetails.getMinorVersion());
        if (BTSLUtil.isNullString(serviceDetails.getStatus())) {
            serviceDetails.setStatus(PretupsI.NO);
            userServicesVO.setStatus(PretupsI.NO);
        } else {
            userServicesVO.setStatus(serviceDetails.getStatus());
        }
        try {
            userServicesVO.setOffset(Long.parseLong(request.getOffset()));
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.OFFSET_INVALID_CHAR);
        }
        userServicesVO.setLocationCode(userVO.getNetworkID());
        userServicesVO.setCreatedBy(userVO.getCreatedBy());
        userServicesVO.setSimProfileId(request.getSimProfileCode());
        userServicesVO.setCreatedOn(new Date());

        return serviceDetails;
    }

    private static void getUserServicesDetails(UserServicesVO userServicesVO, AssignServiceRequestVO request, STKServicesVO serviceDetails, UserVO userVO) throws BTSLBaseException {
        final String METHOD_NAME = "getUserServicesDetails";
        userServicesVO.setUserType(request.getCategoryCode());
        userServicesVO.setProfile(request.getProfileCode());
        userServicesVO.setPosition(request.getPosition());
        userServicesVO.setServiceID(serviceDetails.getServiceID());
        userServicesVO.setNewServiceID(serviceDetails.getServiceID());
        userServicesVO.setMajorVersion(serviceDetails.getMajorVersion());
        userServicesVO.setNewMajorVersion(serviceDetails.getMajorVersion());
        userServicesVO.setMinorVersion(serviceDetails.getMinorVersion());
        userServicesVO.setNewMinorVersion(serviceDetails.getMinorVersion());
        if (BTSLUtil.isNullString(serviceDetails.getStatus())) {
            serviceDetails.setStatus(PretupsI.NO);
            userServicesVO.setStatus(PretupsI.NO);
        } else {
            userServicesVO.setStatus(serviceDetails.getStatus());
        }
        try {
            userServicesVO.setOffset(Long.parseLong(request.getOffset()));
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.OFFSET_INVALID_CHAR);
        }
        userServicesVO.setLocationCode(userVO.getNetworkID());
        userServicesVO.setCreatedBy(userVO.getCreatedBy());
        userServicesVO.setSimProfileId(request.getSimProfileCode());
        userServicesVO.setCreatedOn(new Date());

    }

    @Override
    public ServiceDetailsResponseVO loadSIMServiceDetails(Connection con, String serviceId, String majorVersion) throws BTSLBaseException {
        final String METHOD_NAME = "loadSIMServiceDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }
        validateNonNullAndNonEmpty(serviceId, METHOD_NAME, PretupsErrorCodesI.SERVICE_ID_NULL);
        validateNonNullAndNonEmpty(majorVersion, METHOD_NAME, PretupsErrorCodesI.MAJOR_VERSION_NULL);
        if (!Pattern.matches(Constants.getProperty(PretupsI.STK_VERSION_PATTERN), majorVersion)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MAJOR_VERSION_INVALID);
        }
        ArrayList<String> categoryCodeList = null;
        ServiceDetailsResponseVO response = new ServiceDetailsResponseVO();
        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();

        try {
            ServicesVO serviceVO = servicesWebDAO.loadLatestSIMServiceDetails(con, serviceId,majorVersion);
            if (serviceVO != null && serviceVO.getServiceID() != null) {
                response.setServiceID(serviceVO.getServiceID());
                response.setServiceSetID(serviceVO.getServiceSetID());
                response.setMajorVersion(serviceVO.getMajorVersion());
                response.setMinorVersion(serviceVO.getMinorVersion());
                response.setLabel1(serviceVO.getLabel1());
                response.setLabel2(serviceVO.getLabel2());
                response.setWmlCode(serviceVO.getWml());
                response.setByteCode(serviceVO.getByteCode());
                response.setDescription(serviceVO.getDescription());
                categoryCodeList = servicesWebDAO.loadSimServiceCategoryCodeList(con, serviceVO.getServiceID(), serviceVO.getServiceSetID(), serviceVO.getMajorVersion(), serviceVO.getMinorVersion());
                if (categoryCodeList != null && !categoryCodeList.isEmpty()) {
                    String[] arrCategoryList = categoryCodeList.toArray(new String[0]);
                    response.setSelectedUserTypeCategoryList(arrCategoryList);
                }
            } else {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_SERVICE_ID );
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED);
            }
        }
        return response;
    }

    @Override
    public void updateService(Connection con, MComConnectionI mComCon, ModifyServiceRequestVO request, UserVO userVO) throws Exception {
        final String METHOD_NAME = "updateService";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }
        int updateCount = -1;
        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        ServicesVO servicesVO = new ServicesVO();
        try {
            request.setWmlCode(java.net.URLDecoder.decode(request.getWmlCode(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_CODE_INVALID);
        }
        servicesVO = constructRequestVo(request, servicesVO);
        updateServiceValidations(con, request);
        ServicesVO loadServiceDetils = servicesWebDAO.loadLatestSIMServiceDetails(con, servicesVO.getServiceID(),servicesVO.getMajorVersion());
        boolean labelChanged = !Objects.equals(servicesVO.getLabel1(), loadServiceDetils.getLabel1()) ||
                !Objects.equals(servicesVO.getLabel2(), loadServiceDetils.getLabel2());
        boolean byteCodeChanged = !Objects.equals(servicesVO.getByteCode(), loadServiceDetils.getByteCode());

        if (request.getLabel2().startsWith("80")) {
            servicesVO.setLabel2(convertMenuText(request.getLabel2()));
        } else {
            servicesVO.setLabel2(request.getLabel2());
        }
        if (PretupsI.YES.equals(request.getStatus())) {
            if (PretupsI.STK_DRAFT_VERSION.equalsIgnoreCase(BTSLUtil.NullToString(servicesVO.getMinorVersion()))) {
                servicesVO.setMinorVersion("0");
            }
            if (PretupsI.STK_DRAFT_VERSION.equalsIgnoreCase(BTSLUtil.NullToString(servicesVO.getMajorVersion()))) {
                servicesVO.setMajorVersion("DD");
            }
        }
        else {
            servicesVO.setMajorVersion(PretupsI.STK_DRAFT_VERSION);
            servicesVO.setMinorVersion(PretupsI.STK_DRAFT_VERSION);
        }
        Date currentDate = new Date();
        servicesVO.setCreatedOn(currentDate);
        servicesVO.setModifedOn(currentDate);
        servicesVO.setCreatedBy(userVO.getUserID());
        servicesVO.setModifiedBy(userVO.getUserID());
        servicesVO.setLength(request.getByteCode().length() / 2);
        servicesVO.setUserType(PretupsI.ALL);
        servicesVO.setAllowedToUsers(PretupsI.ALL);

        if (PretupsI.YES.equals(servicesVO.getStatus()))
        {
            if ((byteCodeChanged && labelChanged) || byteCodeChanged) {
                String majorVersion = servicesWebDAO.loadLatestMajorVersion(con, servicesVO.getServiceID());
                if (majorVersion == null) {
                    majorVersion = "0";
                } else {
                    majorVersion = String.valueOf(Integer.parseInt(majorVersion) + 1);
                }
                servicesVO.setMajorVersion(majorVersion);
                servicesVO.setMinorVersion("0");
            }
            else if (labelChanged && !byteCodeChanged)
            {
                if (PretupsI.STK_DRAFT_VERSION.equalsIgnoreCase(servicesVO.getMajorVersion())) {
                    String majorVersion = servicesWebDAO.loadLatestMajorVersion(con, servicesVO.getServiceID());
                    if (majorVersion == null) {
                        majorVersion = "0";
                    } else {
                        majorVersion = String.valueOf(Integer.parseInt(majorVersion));
                    }
                    servicesVO.setMajorVersion(majorVersion);
                }
                String minorVersion = servicesWebDAO.loadLatestMinorVersion(con, servicesVO.getServiceID(), servicesVO.getMajorVersion());
                if (minorVersion == null) {
                    minorVersion = "0";
                } else {
                    minorVersion = String.valueOf(Integer.parseInt(minorVersion) + 1);
                }
                servicesVO.setMinorVersion(minorVersion);
            } else {
                 //removing below lines of code for pretups-24895

            }
            ArrayList newCategoryList = generateListforUserTypeCategory(request, servicesVO);
            servicesWebDAO.deleteSIMServiceCategoriesOnFinalSave(con, newCategoryList);
            if (servicesWebDAO.addSIMServiceCategories(con, newCategoryList) > 0) {
                updateCount = servicesWebDAO.addSIMService(con, servicesVO);
            }

        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME,PretupsI.STK_DETLETE_LOG_MESSAGE);
            }
            ArrayList newCategoryList = generateListforUserTypeCategory(request, servicesVO);
            if (servicesWebDAO.deleteSimServiceCategories(con, request.getServiceSetID(), request.getServiceID(), request.getMajorVersion(), request.getMinorVersion())) {
                if (servicesWebDAO.addSIMServiceCategories(con, newCategoryList) > 0) {
                    updateCount = servicesWebDAO.addSIMServiceAsDraft(con, servicesVO);
                }
            }
        }

        if (updateCount > 0) {
            mComCon.finalCommit();
            AdminOperationVO adminOperationVO = getAdminOperationVO(userVO, currentDate, servicesVO, TypesI.LOGGER_STK_SERVICE_MODIFY,TypesI.LOGGER_OPERATION_MODIFY,PretupsI.SERVICE_SUCC_MODIFIED);
            AdminOperationLog.log(adminOperationVO);
        } else {
            mComCon.finalRollback();
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MODIFY_SERVICE_FAIL);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }
    }

    private void updateServiceValidations(Connection con, ModifyServiceRequestVO request) throws BTSLBaseException {
        final String METHOD_NAME = "updateServiceValidations";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }
        validateNonNullAndNonEmpty(request.getServiceSetID(), METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_NULL);
        validateNonNullAndNonEmpty(request.getServiceID(), METHOD_NAME, PretupsErrorCodesI.SERVICE_ID_NULL);
        validateNonNullAndNonEmpty(request.getLabel1(), METHOD_NAME, PretupsErrorCodesI.LABEL1_NULL);
        validateNonNullAndNonEmpty(request.getLabel2(), METHOD_NAME, PretupsErrorCodesI.LABEL2_NULL);
        validateNonNullAndNonEmpty(request.getWmlCode(), METHOD_NAME, PretupsErrorCodesI.WML_CODE_NULL);
        validateNonNullAndNonEmpty(request.getByteCode(), METHOD_NAME, PretupsErrorCodesI.BYTECODE_NULL);
        validateNonNullAndNonEmpty(request.getMajorVersion(), METHOD_NAME, PretupsErrorCodesI.MAJOR_VERSION_NULL);
        validateNonNullAndNonEmpty(request.getMinorVersion(), METHOD_NAME, PretupsErrorCodesI.MINOR_VERSION_NULL);
        validateNonNullAndNonEmpty(request.getStatus(), METHOD_NAME, PretupsErrorCodesI.STATUS_NULL);

        if (!Pattern.matches(Constants.getProperty(PretupsI.STK_VERSION_PATTERN), request.getMajorVersion())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MAJOR_VERSION_INVALID);
        }

        if (!Pattern.matches(Constants.getProperty(PretupsI.STK_VERSION_PATTERN), request.getMinorVersion())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MINOR_VERSION_INVALID);
        }

        if (request.getUserTypeCategoriesList() == null || request.getUserTypeCategoriesList().length == 0 ||
                Arrays.stream(request.getUserTypeCategoriesList()).allMatch(String::isEmpty)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_TYPE_LIST_NULL);
        }

        if (!BTSLUtil.isNullString(request.getWmlCode()) && request.getWmlCode().length() > 4000) {
            final String [] args = {PretupsI.WML_CODE, "4000"};
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_MAX_LENGTH, args);
        }
        if (!BTSLUtil.isNullString(request.getDescription()) && request.getDescription().length() > 100) {
            final String [] args = {PretupsI.DESCRIPTION, "100"};
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DESCRIPTION_MAX_LENGTH, args);
        }
        if (!Pattern.compile(Constants.getProperty(PretupsI.STK_LABEL2_PATTERN)).matcher(request.getLabel2()).matches())
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.LABEL2_INVALID);

        if (!Objects.equals(request.getStatus(), PretupsI.YES) && !Objects.equals(request.getStatus(), PretupsI.STK_SAVE_DRAFT))
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_STATUS);

        ServicesWebDAO servicesWebDAO = new ServicesWebDAO();
        ArrayList serviceList = servicesWebDAO.loadSIMServiceSet(con);
        ServiceSetVO serviceVO = null;
        boolean serviceSetValid = false;
        for (Object o : serviceList) {
            serviceVO = (ServiceSetVO) o;
            if (serviceVO.getId().equals(request.getServiceSetID()) || request.getServiceSetID().equals(PretupsI.ALL)) {
                serviceSetValid = true;
                break;
            }
        }

        if (!serviceSetValid)
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);

        String checkWMl;
        try {
            checkWMl = new WMLValidator().SAXValidation(request.getWmlCode());
        } catch (Exception e) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.WML_CODE_INVALID);
        }
        if (!Objects.equals(request.getByteCode(), checkWMl)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.BYTECODE_INVALID);
        }
        ServicesVO record = servicesWebDAO.loadLatestSIMServiceDetails(con, request.getServiceID(),request.getMajorVersion());
        if (record == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_ID_INVALID);
        }
        ServicesVO loadDetails = servicesWebDAO.loadSIMService(con, request.getServiceSetID(),request.getServiceID(),request.getMajorVersion(),request.getMinorVersion());
        if (loadDetails == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_ID_INVALID_MAPPED);
        }

        boolean categoryValid;
        SimProfileCategoryListResponseVO response = new SimProfileCategoryListResponseVO();
        ArrayList<STKServiceListValueVO> categoryList;
        CategoryWebDAO categoryWebDAO=new CategoryWebDAO();
        generateUserTypeCategoryListResponse(categoryWebDAO.loadCategoryCodeNameListVO(con), response);
        categoryList=response.getUserTypeList();

        String[] reqCatList = request.getUserTypeCategoriesList();
        System.out.println("Category List: " + categoryList);
        for (String catCode: reqCatList) {
            categoryValid = false;
            for (STKServiceListValueVO o : categoryList) {
                System.out.println("STKServiceListValueVO: " + o.getValue());
                if (o.getValue().equals(catCode)) {
                    categoryValid = true;
                    break;
                }
            }
            if (!categoryValid) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.USER_TYPE_LIST_INVALID);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
        }
    }


    public ServicesVO constructRequestVo(ModifyServiceRequestVO requestVO, ServicesVO servicesVO) throws Exception {
        servicesVO.setByteCode(requestVO.getByteCode());
        servicesVO.setWml(requestVO.getWmlCode());
        servicesVO.setByteCode(requestVO.getByteCode());
        servicesVO.setLabel1(requestVO.getLabel1());
        servicesVO.setLabel2(requestVO.getLabel2());
        servicesVO.setDescription(requestVO.getDescription());
        servicesVO.setStatus(requestVO.getStatus());
        servicesVO.setServiceSetID(requestVO.getServiceSetID());
        servicesVO.setServiceID(requestVO.getServiceID());
        servicesVO.setMinorVersion(requestVO.getMinorVersion());
        servicesVO.setMajorVersion(requestVO.getMajorVersion());
        return servicesVO;
    }

    private ArrayList generateListforUserTypeCategory(ModifyServiceRequestVO requestVO, ServicesVO servicesVO) throws Exception {
        String methodName="generateListforUserTypeCategory";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, PretupsI.ENTERED);
        }

        ArrayList newCategoryList = new ArrayList();
        String[] newCategoryArray = new String[requestVO.getUserTypeCategoriesList().length];
        newCategoryArray = requestVO.getUserTypeCategoriesList();
        SimServiceCategoriesVO categoryVO = null;
        if (newCategoryArray != null && newCategoryArray.length > 0) {
            for (int i = 0; i < newCategoryArray.length; i++) {
                categoryVO = new SimServiceCategoriesVO();
                categoryVO.setCategoryCode(newCategoryArray[i]);
                categoryVO.setServiceID(Integer.parseInt(servicesVO.getServiceID()));
                categoryVO.setServiceSetId(servicesVO.getServiceSetID());
                categoryVO.setMajorVersion(servicesVO.getMajorVersion());
                categoryVO.setMinorVersion(servicesVO.getMinorVersion());
                newCategoryList.add(categoryVO);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, PretupsI.EXITED + methodName,"newCategoryList Size=" + newCategoryList.size());
        }
        return newCategoryList;
    }

    private void generateUserTypeCategoryListResponse(ArrayList<ListValueVO> userTypeList, SimProfileCategoryListResponseVO response) {
        ArrayList<STKServiceListValueVO> userTypeListVOS = new ArrayList<>();
        for (ListValueVO o: userTypeList) {
            STKServiceListValueVO stkServiceListValueVO = new STKServiceListValueVO();
            stkServiceListValueVO.setLabel(o.getLabel());
            stkServiceListValueVO.setValue(o.getValue());
            userTypeListVOS.add(stkServiceListValueVO);
        }
        response.setUserTypeList(userTypeListVOS);
    }

}
