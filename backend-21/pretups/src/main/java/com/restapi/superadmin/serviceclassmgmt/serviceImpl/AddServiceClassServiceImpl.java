package com.restapi.superadmin.serviceclassmgmt.serviceImpl;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.ServiceClassVO;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.serviceclassmgmt.requestVO.AddServiceClassRequestVO;
import com.restapi.superadmin.serviceclassmgmt.responseVO.AddServiceClassResponseVO;
import com.restapi.superadmin.serviceclassmgmt.service.AddServiceClassService;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

@Service("AddServiceClassService")
public class AddServiceClassServiceImpl implements AddServiceClassService {
    public static final Log LOG = LogFactory.getLog(AddServiceClassServiceImpl.class.getName());
    public static final String classname = "AddServiceClassServiceImpl";

    @Override
    public AddServiceClassResponseVO add(Connection con, Locale locale, AddServiceClassRequestVO requestVO, UserVO userVO) throws BTSLBaseException, SQLException {
        final String methodName = "add";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entering");
        }
        AddServiceClassResponseVO response = new AddServiceClassResponseVO();
        int addCount;
        ServiceClassVO serviceClassVO = new ServiceClassVO();
        ServiceClassWebDAO serviceClassWebDAO = new ServiceClassWebDAO();
        serviceClassVO.setInterfaceCode(requestVO.getInterfaceName());
        serviceClassVO.setInterfaceCategory(requestVO.getInterfaceCategory());
        serviceClassVO.setStatus(requestVO.getStatus());
        serviceClassVO.setServiceClassCode(requestVO.getServiceClassCode());
        serviceClassVO.setServiceClassName(requestVO.getServiceClassName());
//        serviceClassVO.setServiceClassId(requestVO.getServiceClassId());
        serviceClassVO.setP2pReceiverSuspend(requestVO.getP2pReceiverSuspend());
        serviceClassVO.setP2pReceiverAllowedStatus(requestVO.getP2pReceiverAllowedStatus());
        serviceClassVO.setP2pReceiverDeniedStatus(requestVO.getP2pReceiverDeniedStatus());
        serviceClassVO.setP2pSenderSuspend(requestVO.getP2pSenderSuspend());
        serviceClassVO.setP2pSenderAllowedStatus(requestVO.getP2pSenderAllowedStatus());
        serviceClassVO.setP2pSenderDeniedStatus(requestVO.getP2pSenderDeniedStatus());
        serviceClassVO.setC2sReceiverSuspend(requestVO.getC2sReceiverSuspend());
        serviceClassVO.setC2sReceiverAllowedStatus(requestVO.getC2sReceiverAllowedStatus());
        serviceClassVO.setC2sReceiverDeniedStatus(requestVO.getC2sReceiverDeniedStatus());
        if (serviceClassWebDAO.isExistsServiceCodeForAdd(con, serviceClassVO)) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICE_CODE_ALREADY_EXISTS, new String[]{requestVO.getServiceClassCode()});
        }
        if (serviceClassWebDAO.isExistsServiceNameForAdd(con, serviceClassVO)) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICE_NAME_ALREADY_EXISTS, new String[]{requestVO.getServiceClassName()});
        }
        String idType = PretupsI.SERVICE_CLASS_ID;
        StringBuilder uniqueId = new StringBuilder();
        long serClassId = IDGenerator.getNextID(idType, PretupsI.ALL);
        int zeroes = 10 - (idType.length() + Long.toString(serClassId).length());
        for (int count = 0; count < zeroes; count++) {
            uniqueId.append(0);
        }
        uniqueId.insert(0, idType);
        uniqueId.append(serClassId);
        Date currentDate = new Date(System.currentTimeMillis());
        serviceClassVO.setServiceClassId(uniqueId.toString());
        serviceClassVO.setCreatedOn(currentDate);
        serviceClassVO.setModifiedOn(currentDate);
        serviceClassVO.setCreatedBy(userVO.getUserID());
        serviceClassVO.setModifiedBy(userVO.getUserID());
        addCount = serviceClassWebDAO.addServiceClass(con, serviceClassVO);
        if (addCount == 0) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICE_CLASS_NOT_ADDED);
        }
        con.commit();
        AdminOperationVO adminOperationVO = new AdminOperationVO();
        adminOperationVO.setSource(TypesI.LOGGER_SERVICE_CLASS_SOURCE);
        adminOperationVO.setDate(currentDate);
        adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
        adminOperationVO.setInfo("Service class " + serviceClassVO.getServiceClassName() + " added successfully for " + requestVO.getInterfaceName() + " interface");
        adminOperationVO.setLoginID(userVO.getLoginID());
        adminOperationVO.setUserID(userVO.getUserID());
        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
        adminOperationVO.setNetworkCode(userVO.getNetworkID());
        adminOperationVO.setMsisdn(userVO.getMsisdn());
        AdminOperationLog.log(adminOperationVO);
        response.setServiceClassID(uniqueId.toString());
        response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERVICE_CLASS_ADDED, null));
        response.setMessageCode(PretupsErrorCodesI.SERVICE_CLASS_ADDED);
        response.setStatus(HttpStatus.SC_OK);
        return response;
    }
}
