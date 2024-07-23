package com.web.pretups.pointenquiry.businesslogic;

import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.web.pretups.pointenquiry.web.PointEnquiryForm;

public class PointEnqInterface {

    private static final Log LOG = LogFactory.getLog(PointEnqInterface.class.getName());

    public ArrayList checkErrorListForWeb(PointEnquiryForm pointEnquiryForm) throws BTSLBaseException {
        final String methodName = "checkErrorListForWeb";
        ArrayList errorMessageList = null;
        try {
            final CommonValidator commonValidator = new CommonValidator("configfiles/MessageResources", "configfiles/pointenquiry/validator-pointEnquiry.xml",
                pointEnquiryForm);
            errorMessageList = commonValidator.validate();

        } catch (Exception e) {
            LOG.errorTrace("checkErrorListForWeb", e);
            throw new BTSLBaseException(methodName, "error.general.processing");
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }

        }
        return errorMessageList;

    }

}
