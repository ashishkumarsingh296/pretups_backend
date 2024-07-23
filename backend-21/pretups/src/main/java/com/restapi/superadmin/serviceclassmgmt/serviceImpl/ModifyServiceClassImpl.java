package com.restapi.superadmin.serviceclassmgmt.serviceImpl;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.ServiceClassVO;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.serviceclassmgmt.requestVO.AddServiceClassRequestVO;
import com.restapi.superadmin.serviceclassmgmt.service.ModifyServiceClassService;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

@Service("ModifyServiceClassImpl")
public class ModifyServiceClassImpl implements ModifyServiceClassService {
    public static final Log LOG = LogFactory.getLog(ModifyServiceClassImpl.class.getName());
    public static final String classname = "ModifyServiceClassImpl";

    private static AdminOperationVO setLogs(AddServiceClassRequestVO requestVO, UserVO userVO, Date currentDate) {
        AdminOperationVO adminOperationVO = new AdminOperationVO();
        adminOperationVO.setSource(TypesI.LOGGER_SERVICE_CLASS_SOURCE);
        adminOperationVO.setDate(currentDate);
        adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
        adminOperationVO.setInfo("Service class modified successfully for " + requestVO.getInterfaceName() + " interface");
        adminOperationVO.setLoginID(userVO.getLoginID());
        adminOperationVO.setUserID(userVO.getUserID());
        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
        adminOperationVO.setNetworkCode(userVO.getNetworkID());
        adminOperationVO.setMsisdn(userVO.getMsisdn());
        return adminOperationVO;
    }

    @Override
    public BaseResponse modify(Connection con, Locale locale, AddServiceClassRequestVO requestVO, UserVO userVO) throws BTSLBaseException, SQLException {
        final String methodName = "modify";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entering");
        }
        BaseResponse response = new BaseResponse();
        int updateCount;
        ServiceClassVO serviceClassVO = new ServiceClassVO();
        ServiceClassWebDAO serviceClassWebDAO = new ServiceClassWebDAO();
        serviceClassVO.setInterfaceCode(requestVO.getInterfaceName());
        serviceClassVO.setInterfaceCategory(requestVO.getInterfaceCategory());
        serviceClassVO.setStatus(requestVO.getStatus());
        serviceClassVO.setServiceClassCode(requestVO.getServiceClassCode());
        serviceClassVO.setServiceClassName(requestVO.getServiceClassName());
        serviceClassVO.setServiceClassId(requestVO.getServiceClassId());
        serviceClassVO.setP2pReceiverSuspend(requestVO.getP2pReceiverSuspend());
        serviceClassVO.setP2pReceiverAllowedStatus(requestVO.getP2pReceiverAllowedStatus());
        serviceClassVO.setP2pReceiverDeniedStatus(requestVO.getP2pReceiverDeniedStatus());
        serviceClassVO.setP2pSenderSuspend(requestVO.getP2pSenderSuspend());
        serviceClassVO.setP2pSenderAllowedStatus(requestVO.getP2pSenderAllowedStatus());
        serviceClassVO.setP2pSenderDeniedStatus(requestVO.getP2pSenderDeniedStatus());
        serviceClassVO.setC2sReceiverSuspend(requestVO.getC2sReceiverSuspend());
        serviceClassVO.setC2sReceiverAllowedStatus(requestVO.getC2sReceiverAllowedStatus());
        serviceClassVO.setC2sReceiverDeniedStatus(requestVO.getC2sReceiverDeniedStatus());
        if (serviceClassWebDAO.isExistsServiceCodeForModify(con, serviceClassVO)) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICE_CODE_ALREADY_EXISTS, new String[]{requestVO.getServiceClassCode()});
        }
        if (serviceClassWebDAO.isExistsServiceNameForModify(con, serviceClassVO)) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICE_NAME_ALREADY_EXISTS, new String[]{requestVO.getServiceClassName()});
        }
        Date currentDate = new Date(System.currentTimeMillis());
        serviceClassVO.setModifiedBy(userVO.getUserID());
        serviceClassVO.setModifiedOn(currentDate);
        updateCount = serviceClassWebDAO.modifyServiceClass(con, serviceClassVO);
        if (updateCount == 0) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICE_CLASS_NOT_MODIFIED);
        }
        con.commit();
        AdminOperationVO adminOperationVO = setLogs(requestVO, userVO, currentDate);
        AdminOperationLog.log(adminOperationVO);
        response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERVICE_CLASS_MODIFIED, null));
        response.setMessageCode(PretupsErrorCodesI.SERVICE_CLASS_MODIFIED);
        response.setStatus(HttpStatus.SC_OK);

        return response;
    }
}
