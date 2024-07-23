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
import com.restapi.superadmin.serviceclassmgmt.service.DeleteServiceClassService;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;

@Service("DeleteServiceClassService")
public class DeleteServiceClassServiceImpl implements DeleteServiceClassService {
    public static final Log LOG = LogFactory.getLog(DeleteServiceClassServiceImpl.class.getName());
    public static final String classname = "DeleteServiceClassServiceImpl";

    private static AdminOperationVO setLogs(UserVO userVO, Date currentDate, String name) {
        AdminOperationVO adminOperationVO = new AdminOperationVO();
        adminOperationVO.setSource(TypesI.LOGGER_SERVICE_CLASS_SOURCE);
        adminOperationVO.setDate(currentDate);
        adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
        adminOperationVO.setInfo("Service class " + name + " deleted successfully");
        adminOperationVO.setLoginID(userVO.getLoginID());
        adminOperationVO.setUserID(userVO.getUserID());
        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
        adminOperationVO.setNetworkCode(userVO.getNetworkID());
        adminOperationVO.setMsisdn(userVO.getMsisdn());
        return adminOperationVO;
    }

    @Override
    public BaseResponse delete(Connection con, Locale locale, String id, UserVO userVO, String name) throws BTSLBaseException, SQLException {
        final String methodName = "delete";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entering");
        }
        BaseResponse response = new BaseResponse();
        int updateCount;
        ServiceClassWebDAO serviceClasswebDAO = new ServiceClassWebDAO();

        ServiceClassVO servicesVO = new ServiceClassVO();
        servicesVO.setServiceClassId(id);
        servicesVO.setModifiedBy(userVO.getUserID());
        servicesVO.setModifiedOn(new Date());
        servicesVO.setNetworkCode(userVO.getNetworkID());
        if (serviceClasswebDAO.isTransferRulesExistsForServiceClass(con, servicesVO)) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICE_CLASS_NOT_DELETED_TRF, new String[]{name});
        }
        updateCount = serviceClasswebDAO.deleteServiceClass(con, servicesVO);
        if (updateCount == 0) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SERVICE_CLASS_NOT_DELETED);
        }
        con.commit();
        AdminOperationVO adminOperationVO = setLogs(userVO, new Date(), name);
        AdminOperationLog.log(adminOperationVO);
        response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SERVICE_CLASS_DELETED, null));
        response.setMessageCode(PretupsErrorCodesI.SERVICE_CLASS_DELETED);
        response.setStatus(HttpStatus.SC_OK);
        return response;
    }
}
