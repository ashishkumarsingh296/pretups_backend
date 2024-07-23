package com.restapi.superadmin.serviceclassmgmt.serviceImpl;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.ServiceClassDAO;
import com.restapi.networkadmin.responseVO.ServiceClassListResponseVO;
import com.restapi.superadmin.serviceclassmgmt.service.ServiceClassListService;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Locale;

@Service("ServiceClassListService")
public class ServiceClassListServiceI implements ServiceClassListService {
    public static final Log LOG = LogFactory.getLog(ServiceClassListServiceI.class.getName());
    public static final String classname = "ServiceClassListServiceI";

    @Override
    public ServiceClassListResponseVO getServiceClassList(Connection con, Locale locale, String id) throws BTSLBaseException {
        final String METHOD_NAME = "getServiceClassList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        ServiceClassListResponseVO responseVO = new ServiceClassListResponseVO();
        responseVO.setServiceClassList( new ServiceClassDAO().loadServiceClassDetails(con, id));
        responseVO.setStatus(HttpStatus.SC_OK);
        responseVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null));
        return responseVO;
    }
}
