package com.btsl.common;

import java.util.ArrayList;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * 
 * @author ayush.abhijeet
 *
 */
public class CommonValidationInterface {
    private static final Log log = LogFactory.getLog(CommonValidationInterface.class.getName());

    public ArrayList checkErrorListForWeb(Object beanName, String configPath, String validationXMLpath) throws BTSLBaseException {
        final String methodName = "checkErrorListForWeb";
        ArrayList errorMessageList = null;
        try {
            CommonValidator commonValidator = new CommonValidator(configPath, validationXMLpath, beanName);
            errorMessageList = commonValidator.validate();

        } catch (Exception e) {
            log.errorTrace("checkErrorListForWeb", e);
            throw new BTSLBaseException(this,methodName, "error.general.processing",e);
        } finally {
        	LogFactory.printLog(methodName, "Exited", log);
        }
        return errorMessageList;

    }
}
