package com.btsl.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.BTSLUtil;

//@Component
//@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PretupsInputValidator {
	protected final Log log = LogFactory.getLog(getClass().getName());
	private static final long serialVersionUID = 1L;
	
	private static PretupsInputValidator pretupsInputValidatorInstance = null;
	private  Properties pretupsInputValidatorProps = new Properties();

	
	private PretupsInputValidator() {
		
	}
 
    public void load(String fileName) throws IOException
    {
    	File file = new File(fileName);
    	try(FileInputStream fileInputStream = new FileInputStream(file);)
    	{
    		pretupsInputValidatorProps.load(fileInputStream);
    		fileInputStream.close();
    	}
    }
	public  String getProperty(String propertyName)
	{
		return pretupsInputValidatorProps.getProperty(propertyName);
	}
	
	
	
	
	public static PretupsInputValidator getInstance()
    {
        if (pretupsInputValidatorInstance == null)
        	pretupsInputValidatorInstance = new PretupsInputValidator();
        return pretupsInputValidatorInstance;
    }

	private String getValidationRule(String propertyName) {
		if (pretupsInputValidatorProps.get(propertyName) != null) {
			return (String) pretupsInputValidatorProps.get(propertyName);
		} else {
			return null;
		}

	}

	public ArrayList<MasterErrorList> scanRequest(Object requestData, Locale locale) throws BTSLBaseException {
		final String methodName ="scanRequest";
		Class cls = requestData.getClass();
		String validationRuledata = null;
		Field[] fields = cls.getDeclaredFields();
		StringBuilder sb = new StringBuilder();
		ValidationRuleDef validationRuleDef = null;
		ArrayList<MasterErrorList> listofALLErrors = new ArrayList<MasterErrorList>();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName() != null) {
				validationRuledata = getValidationRule(fields[i].getName());
				if(!BTSLUtil.isNullString(validationRuledata)) {
					log.debug(methodName, cls.getSimpleName()+"_"+ fields[i].getName() + " -> " + validationRuledata);
				validationRuleDef = scanValidationRuleFielData(validationRuledata);
				try {
					fields[i].setAccessible(true);
					ArrayList<MasterErrorList> listofErrors = checkallValidationRules(requestData, fields[i], validationRuleDef, locale);
					listofALLErrors.addAll(listofErrors);
					fields[i].setAccessible(false);
				} catch (IllegalArgumentException e) {
					log.error(methodName, "***********Imporoper configuration in  pretupsInputvalidation.properties file");
					throw new BTSLBaseException(PretupsInputValidator.class, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
					
				} catch (IllegalAccessException e) {
					log.error(methodName, "***********Imporoper configuration in  pretupsInputvalidation.properties file");
					throw new BTSLBaseException(PretupsInputValidator.class, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
				}
				}

			}

		}
		return listofALLErrors;
	}

	private ArrayList<MasterErrorList> checkallValidationRules(Object requestObj, Field field,
			ValidationRuleDef validationRuleDef, Locale locale)
			throws IllegalArgumentException, IllegalAccessException {
		ArrayList<MasterErrorList> listErrorMessage = new ArrayList<MasterErrorList>();
		MasterErrorList masterErrorList = null;
		String resmsg = null;
		String[] errorParam = new String[5];
		String fieldData = (String) field.get(requestObj);

		// Mandatory check validation
		if (validationRuleDef.getMandatory() == 'M') {
			if (fieldData == null || fieldData == ""
					|| fieldData != null && fieldData.toString().trim().length() == 0) {
				masterErrorList = new MasterErrorList();
				masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_MANDATORY);
				errorParam[0] = validationRuleDef.getFieldDisplayName();
				resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_MANDATORY, errorParam);
				masterErrorList.setErrorMsg(resmsg);
				listErrorMessage.add(masterErrorList);
			}
		}
		// Min max length validation
		if (fieldData != null && (fieldData.length() < validationRuleDef.getMinLength()
				|| fieldData.length() > validationRuleDef.getMaxLength())) {
			masterErrorList = new MasterErrorList();
			masterErrorList.setErrorCode(PretupsErrorCodesI.FIELD_MIN_MAX_LENGTH);
			errorParam= new String[3];
			errorParam[0] = validationRuleDef.getFieldDisplayName();
			errorParam[1] = String.valueOf(validationRuleDef.getMinLength());
			errorParam[2] = String.valueOf(validationRuleDef.getMaxLength());
			resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.FIELD_MIN_MAX_LENGTH, errorParam);
			masterErrorList.setErrorMsg(resmsg);
			listErrorMessage.add(masterErrorList);

		}

		if (!BTSLUtil.isNullString(fieldData)) {
		// Regular Expression validation
		String actualRegExpression = getValidationRule(validationRuleDef.getFieldReguarlExrpessionCode()); //reuse same pretupsInputValidation.properties file to fetch regEx code;
		Pattern p = Pattern.compile(actualRegExpression);
		Matcher m = p.matcher(fieldData);
		String errorCode =null;
		if (!m.matches()) {
			masterErrorList = new MasterErrorList();
			if(!BTSLUtil.isNullString(validationRuleDef.getAllowedValues())) {
				errorCode= PretupsErrorCodesI.ONLY_ALLOWED_VALUES;	
			}else {
				errorCode =getRegularExpressionErrorCode(validationRuleDef.getFieldReguarlExrpessionCode());
			}
			masterErrorList.setErrorCode(errorCode);
			if(!BTSLUtil.isNullString(validationRuleDef.getAllowedValues())) {
				errorParam= new String[2];
				errorParam[0] = validationRuleDef.getFieldDisplayName();
				errorParam[1] =validationRuleDef.getAllowedValues();
		    }else {
		    	errorParam= new String[1];
		    	errorParam[0] = validationRuleDef.getFieldDisplayName();	
		    }
			resmsg = RestAPIStringParser.getMessage(locale, errorCode, errorParam);
			masterErrorList.setErrorMsg(resmsg);
			listErrorMessage.add(masterErrorList);

		}
		}

		return listErrorMessage;
	}
	
	
	
	 private String getRegularExpressionErrorCode(String regularExpressionErroCode) {
		 
		 //RegularExpresion values
		 // AL -> only alhpabets
		 // AN -> Alpha numric
		 // NU ->ONLY NUMERIC
		 // YN ->only allowed chars (Y/N)
		 String erroCode =null;
		 if(regularExpressionErroCode.equalsIgnoreCase("AL")) {
			 erroCode=PretupsErrorCodesI.ONLY_ALPHABETS_ALLOWED;
		 }else if(regularExpressionErroCode.equalsIgnoreCase("AN")) { 
			 erroCode=PretupsErrorCodesI.ONLY_ALPHNUMERIC_ALLOWED;
		 }else if(regularExpressionErroCode.equalsIgnoreCase("NU")) {
			 erroCode=PretupsErrorCodesI.ONLY_NUMERIC_ALLOWED;
		 }else {
			 //default
			 erroCode=PretupsErrorCodesI.FIELD_INVALID_DATA;
		 }
		 
		 return erroCode;
	 }
	
	

	private ValidationRuleDef scanValidationRuleFielData(String validationRuledata) {
		ValidationRuleDef validationRuleDef = new ValidationRuleDef();
		String regularExpressionCode = null;
		String[] validationRuleArr = validationRuledata.split(PretupsI.COMMA);
		validationRuleDef.setMandatory(validationRuleArr[0].charAt(0));
		validationRuleDef.setMinLength(Integer.parseInt(validationRuleArr[1]));
		validationRuleDef.setMaxLength(Integer.parseInt(validationRuleArr[2]));
		regularExpressionCode = validationRuleArr[3];
		validationRuleDef.setFieldReguarlExrpessionCode(regularExpressionCode);
		validationRuleDef.setFieldDisplayName(validationRuleArr[4]);
	     if(validationRuleArr.length>5) {
	    	 validationRuleDef.setAllowedValues(validationRuleArr[5]);
	     }
		
		return validationRuleDef;
	}

	class ValidationRuleDef {
		private char mandatory;
		private int minLength;
		private int maxLength;
		private String fieldReguarlExrpessionCode;
		private String fieldDisplayName;
		private String allowedValues;

		public String getFieldReguarlExrpessionCode() {
			return fieldReguarlExrpessionCode;
		}

		public void setFieldReguarlExrpessionCode(String fieldReguarlExrpessionCode) {
			this.fieldReguarlExrpessionCode = fieldReguarlExrpessionCode;
		}

		public char getMandatory() {
			return mandatory;
		}

		public void setMandatory(char mandatory) {
			this.mandatory = mandatory;
		}

		public int getMinLength() {
			return minLength;
		}

		public void setMinLength(int minLength) {
			this.minLength = minLength;
		}

		public int getMaxLength() {
			return maxLength;
		}

		public void setMaxLength(int maxLength) {
			this.maxLength = maxLength;
		}

		

		public String getFieldDisplayName() {
			return fieldDisplayName;
		}

		public void setFieldDisplayName(String fieldDisplayName) {
			this.fieldDisplayName = fieldDisplayName;
		}

		public String getAllowedValues() {
			return allowedValues;
		}

		public void setAllowedValues(String allowedValues) {
			this.allowedValues = allowedValues;
		}

	}

}
