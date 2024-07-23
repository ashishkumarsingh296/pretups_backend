package com.btsl.common;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;
import org.xml.sax.SAXException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class CommonValidator {

    /**
     * We need a resource bundle to get our field names and errors messages
     * from. Note that this is not strictly required to make the Validator
     * work, but is a good coding practice.
     */
    private static Log _log = LogFactory.getLog(CommonValidator.class.getName());
    private static ResourceBundle applicationInputs;
    private String xmlInput;
    private String applicationResourcePath = null;
    private Object beanName = null;
    private String beanRef;

    public CommonValidator(String messageResourcePath, String validationXmlPath, Object beanName) {
        this.applicationResourcePath = messageResourcePath;
        this.xmlInput = validationXmlPath;
        this.beanName = beanName;
    }

    public CommonValidator(String validationXmlPath, Object object, String beanRef) {
		this.xmlInput = validationXmlPath;
		this.beanName = object;
		this.beanRef = beanRef;
	}
    
    /**
     * This is the main method that will be called to initialize the Validator,
     * create some sample beans, and
     * run the Validator against them.
     */
    public ArrayList validate() throws ValidatorException, IOException, SAXException {

        InputStream xmlInputs = null;
        ValidatorResources resources = null;
        ValidatorResults results = null;
        ArrayList errorMessageList = null;
        try {
            applicationInputs = ResourceBundle.getBundle(applicationResourcePath);
            xmlInputs = CommonValidator.class.getClassLoader().getResourceAsStream(xmlInput);
            resources = new ValidatorResources(xmlInputs);
            Validator validator = new Validator(resources, beanName.getClass().getSimpleName());
            validator.setParameter(Validator.BEAN_PARAM, beanName);
            results = validator.validate();
            errorMessageList = validationResults(beanName, results, resources);
        } catch (Exception e) {
            _log.errorTrace("validateProgramCategory", e);
        } finally {
            if (xmlInputs != null) {
                xmlInputs.close();
            }
        }
        return errorMessageList;
    }

    /**
     * Dumps out the Bean in question and the results of validating it.
     */
    public static ArrayList validationResults(Object beanName, ValidatorResults results, ValidatorResources resources) {
        ArrayList errorMessageList = new ArrayList();
        try {
            

            // Form form =
            // resources.getForm(Locale.getDefault(),beanName.getClass().getName());
            Form form = resources.getForm(Locale.getDefault(), beanName.getClass().getSimpleName());

            Iterator propertyNames = results.getPropertyNames().iterator();
            while (propertyNames.hasNext()) {
                String propertyName = (String) propertyNames.next();
                // Get the Field associated with that property in the Form
                Field field = form.getField(propertyName);
                // Get the result of validating the property.
                ValidatorResult result = results.getValidatorResult(propertyName);
                Map actionMap = result.getActionMap();
                Iterator keys = actionMap.keySet().iterator();
                while (keys.hasNext()) {
                    String actName = (String) keys.next();
                    String messageKey = applicationInputs.getString(field.getArg(0).getKey());
                    ValidatorAction action = resources.getValidatorAction(actName);

                    if (!result.isValid(actName)) {
                        
                        String message = applicationInputs.getString(action.getMsg());
                        Object[] args = { messageKey };
                        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
                        commonValidatorVO.setPropertyName(propertyName);
                        commonValidatorVO.setPropertyMessage(MessageFormat.format(message, args));
                        errorMessageList.add(commonValidatorVO);
                    }
                }
            }
        } catch (Exception e) {
            _log.errorTrace("validationResults", e);
        }
        return errorMessageList;
    }
    
    
    
    /**
	 * This is the main method that will be called to initialize the Validator,
	 * create some sample beans, and run the Validator against them.
	 * @return Map<String, String> map of field name and error messages
	 * @throws ValidatorException, IOException, SAXException, Exception
	 */
	public Map<String, String> validateModel() throws ValidatorException, IOException, SAXException {
		if (_log.isDebugEnabled()) {
			_log.debug("CommonValidator#validate", "Exiting");
		}
		InputStream in = null;
		ValidatorResources resources = null;

		try {
			in = this.getClass().getClassLoader().getResourceAsStream(this.xmlInput);
			resources = new ValidatorResources(in);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		Validator validator = new Validator(resources, beanRef);
		validator.setParameter(Validator.BEAN_PARAM, this.beanName);
		ValidatorResults results = null;
		results = validator.validate();
		if (_log.isDebugEnabled()) {
			_log.debug("CommonValidator#validate", "Exiting");
		}
		return this.validateResult(results, resources);
		
	}

	/**
	 * This method process ValidatorResults object for error messages
	 *
	 * @param bean  Object of Bean class
	 * @param results  The object of ValidatorResults having validation messages
	 * @param resources The object of ValidatorResources 
	 * @return Map<String, String> map of field name and error messages
	 * 
	 */
	public Map<String, String> validateResult(ValidatorResults results, ValidatorResources resources) {
		if (_log.isDebugEnabled()) {
			_log.debug("CommonValidator#validateResult", "Exiting");
		}
		Map<String, String> mapData = new HashMap<String, String>();
		Form form = resources.getForm(Locale.getDefault(), this.beanRef);
		Iterator<String> propertyNames = results.getPropertyNames().iterator();
		while (propertyNames.hasNext()) {

			String propertyName = (String) propertyNames.next();
			Field field = form.getField(propertyName);
			Boolean flag = true;
			ValidatorResult result = results.getValidatorResult(propertyName);
			String[] dependsArray = field.getDepends().split(",");
			for (int i = 0; i < dependsArray.length && flag; i++) {
				String keyString = field.getArg(i).getKey();
				if (!result.isValid(dependsArray[i].trim())) {
					mapData.put(propertyName, keyString);
					flag=false;
				}
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("CommonValidator#validateResult", "Exiting");
		}
		return mapData;
	}

}
