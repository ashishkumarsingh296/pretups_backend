package com.btsl.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.struts.validator.Resources;


/**
 * @(#)PretupsValidator.java
 *                           Copyright(c) 2005, Bharti Telesoft Ltd. All Rights
 *                           Reserved
 * 
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Mohit Goel 26/05/2005 Initial Creation
 *                           Ankit Zindal 19/12/2006 Modified Change
 *                           ID=ACCOUNTID
 * 
 *                           Change 1 for the file Appslab_BugReport_Super and
 *                           Network Admin_PreTUPS5.0.xls
 *                           Bugs fixed, No.3 and 8.Fixed on 13/10/06 by
 *                           Siddhartha
 * 
 *                           This class contains various method which validates
 *                           the user input
 *                           e.g if user Inserting Network Prefix so we check
 *                           first its value should be numeric
 *                           second its length etc
 * 
 * 
 */

public class PretupsValidator {

    /**
     * Commons Logging
     */
    private static final Log LOGGER = LogFactory.getLog(PretupsValidator.class);

    /**
     * Constructor for PretupsValidator.
     */
    private PretupsValidator() {
    }

    /*
     * Here we check whether the code is numeric or not
     */

    /**
     * Method validatePrefixNumeric.
     * Change ID=ACCOUNTID
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
    /*public static boolean validatePrefixNumeric(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {

        final String str = ValidatorUtils.getValueAsString(bean, field.getProperty());

        final StringTokenizer value = new StringTokenizer(str, ",");
        boolean flag = true;
        Object result = null;
        boolean invalidChar = false;
        // Change ID=ACCOUNTID
        // Check if alphanumeric identification number is allowed or not
        // If yes then check that no spaces or character orher then 0-9A-Za-z
        // can entered.
        // Otherwise check for numeric
        String identificationNumberValType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IDENTIFICATION_NUMBER_VAL_TYPE);
        boolean alphaIdNumAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALPHA_ID_NUM_ALLOWED);
        if (PretupsI.MSISDN_VALIDATION.equals(identificationNumberValType) || !alphaIdNumAllowed) {
            String series = null;
            ActionMessage message = null;
            Object[] objOld = null;
            ArrayList valuesList = null;
            while (value.hasMoreTokens()) {
                series = value.nextToken();
                result = GenericTypeValidator.formatInt(series);
                *//*
                 * Here we check whether the code is numeric or not
                 * if result == null means the series code is not numeric
                 *//*
                if (result == null) {
                    message = Resources.getActionMessage(request, va, field);
                    objOld = message.getValues();
                    if (objOld != null) {
                        valuesList = new ArrayList();

                        for (int i = 0, j = objOld.length; i < j; i++) {
                            if (objOld[i] != null && !"".equals(objOld)) {
                                valuesList.add(objOld[i]);
                            }
                        }
                        valuesList.add(series);
                    }
                    errors.add(field.getKey(), new ActionMessage(message.getKey(), valuesList.toArray()));
                    flag = false;
                }
            }
        } else {
            String series = null;
            int i = 0, j = 0;
            ActionMessage message = null;
            Object[] objOld = null;
            ArrayList valuesList = null;
            while (value.hasMoreTokens()) {
                series = value.nextToken();
                for (i = 0, j = series.length(); i < j; i++) {
                    if (Character.isSpaceChar(series.charAt(i)) || !(Character.isLetterOrDigit(series.charAt(i)))) {
                        invalidChar = true;
                        break;
                    }
                }
                *//*
                 * Here we check whether the code is numeric or not
                 * if result == null means the series code is not numeric
                 *//*
                if (invalidChar) {
                    message = Resources.getActionMessage(request, va, field);
                    objOld = message.getValues();
                    if (objOld != null) {
                        valuesList = new ArrayList();

                        for (i = 0, j = objOld.length; i < j; i++) {
                            if (objOld[i] != null && !"".equals(objOld)) {
                                valuesList.add(objOld[i]);
                            }
                        }

                        valuesList.add(series);

                    }
                    errors.add(field.getKey(), new ActionMessage(message.getKey(), valuesList.toArray()));
                    flag = false;
                }
                invalidChar = false;
            }
        }
        return flag;
    }
*/
    /*
     * Here we check whether the code length is proper or not
     */
    /**
     * Method validatePrefixLength.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validatePrefixLength(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {

        final String str = ValidatorUtils.getValueAsString(bean, field.getProperty());
        final StringTokenizer value = new StringTokenizer(str, ",");
        // String serieslen = Constants.getProperty("SERIES_LENGTH");
        int msisdnPrefixLengthCode = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LENGTH_CODE))).intValue();
        final String serieslen = String.valueOf(msisdnPrefixLengthCode);
        int series_length = 0;

        if (serieslen != null && serieslen.trim().length() > 0) {
            series_length = Integer.parseInt(serieslen);
        }

        boolean flag = true;

        while (value.hasMoreTokens()) {
            final String series = value.nextToken();

            *//*
             * Here we check whether the code length is proper or not
             * if series.length != series_length means the series code length is
             * not appropriate
             *//*
            if (series != null && series.trim().length() != series_length) {
                final ActionMessage message = Resources.getActionMessage(request, va, field);

                final Object[] objOld = message.getValues();

                final ArrayList valuesList = new ArrayList();
                if (objOld != null) {
                    for (int i = 0, j = objOld.length; i < j; i++) {
                        if (objOld[i] != null && !"".equals(objOld)) {
                            valuesList.add(objOld[i]);
                        }
                    }

                    valuesList.add(series);
                    valuesList.add(series_length + "");
                }
                errors.add(field.getKey(), new ActionMessage(message.getKey(), valuesList.toArray()));
                flag = false;
            }

        }

        return flag;
    }
*/
    /*
     * Here we check whether the code is duplicate or not
     */
    /**
     * Method validatePrefixDuplicate.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
 /*   public static boolean validatePrefixDuplicate(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        boolean flag = true;

        final String str = ValidatorUtils.getValueAsString(bean, field.getProperty());

        final StringTokenizer value = new StringTokenizer(str, ",");
        final HashMap map = new HashMap();
        while (value.hasMoreTokens()) {
            final String series = value.nextToken();

            if (map.containsKey(series)) {

                final ActionMessage message = Resources.getActionMessage(request, va, field);

                final Object[] objOld = message.getValues();

                final ArrayList valuesList = new ArrayList();
                if (objOld != null) {
                    for (int i = 0, j = objOld.length; i < j; i++) {
                        if (objOld[i] != null && !"".equals(objOld)) {
                            valuesList.add(objOld[i]);
                        }
                    }

                    valuesList.add(series);

                }
                errors.add(field.getKey(), new ActionMessage(message.getKey(), valuesList.toArray()));
                flag = false;
            } else {
                map.put(series, series);
            }

        }

        return flag;
    }
*/
    /**
     * <p>
     * Executes validation for another set of "inherited" rules.
     * </p>
     * 
     * @param bean
     *            The bean validation is being performed on.
     * @param va
     *            The <code>ValidatorAction</code> that is currently being
     *            performed.
     * @param field
     *            The <code>Field</code> object associated with the current
     *            field being validated.
     * @param errors
     *            The <code>ActionMessages</code> object to add errors to if any
     *            validation errors occur.
     * @param validator
     *            The <code>Validator</code> instance, used to access
     *            other field values.
     * @param request
     *            Current request object.
     * @param application
     *            The Servlet Context
     * @return results of the validator.
     */
   /* public static Object validateExtends(Object bean, ValidatorAction va, Field field, ActionMessages errors, Validator validator, HttpServletRequest request, ServletContext application) {

        String fieldPrefix = field.getProperty().length() > 0 ? field.getProperty() : null;
        final String formAndProperty = fieldPrefix == null ? validator.getFormName() : validator.getFormName() + "/" + fieldPrefix;

        // Get the validation key
        final String key = field.getVarValue("extends");
        if (key == null || key.length() == 0) {
            LOGGER.error("'extends' var is missing for " + formAndProperty);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.extends", formAndProperty));
            return Boolean.FALSE;
        }

        // Get the property value
        Object value = null;
        if (fieldPrefix == null) {
            value = bean;
        } else {
            try {
                value = PropertyUtils.getProperty(bean, field.getProperty());
            } catch (Exception e) {
                LOGGER.error("Error retrieving property '" + formAndProperty + "' " + e.getMessage(), e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.extends", formAndProperty));
            }
        }

        if (value == null) {
            LOGGER.error("Property '" + formAndProperty + "' is NULL");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.extends", formAndProperty));
            return Boolean.FALSE;
        }

        // Initialize the validator
        final ActionMessages newErrors = new ActionMessages();
        final Validator newValidator = Resources.initValidator(key, bean, application, request, newErrors, validator.getPage());

        // Is it an Array/Collection?
        Object[] values = null;
        if (value instanceof Collection) {
            values = ((Collection) value).toArray();
        } else if (value.getClass().isArray()) {
            values = (Object[]) value;
        }

        // Execute the validator
        ValidatorResults results = null;
        if (values == null) {
            results = executeValidator(bean, -1, field, newValidator, errors, newErrors, fieldPrefix);
        } else {
            results = new ValidatorResults();
            for (int i = 0; i < values.length; i++) {
                fieldPrefix = field.getProperty() + "[" + i + "].";
                newValidator.setParameter(Validator.BEAN_PARAM, values[i]);
                final ValidatorResults indexedResults = executeValidator(values[i], (i + 1), field, newValidator, errors, newErrors, fieldPrefix);
                results.merge(indexedResults);
            }

        }

        return results;

    }
*/
    /**
     * Executes a validator.
     * 
     * @param bean
     *            The bean validation is being performed on.
     * @param application
     *            The Servlet Context
     * @param request
     *            Current request object.
     * @param errors
     *            The <code>ActionMessages</code> object to add errors to if any
     *            validation errors occur.
     * @param page
     *            Page Number.
     * @param prefix
     *            Prefix for error message properties.
     * @return results of the validator.
     */
  /*  private static ValidatorResults executeValidator(Object bean, int index, Field field, Validator validator, ActionMessages errors, ActionMessages newErrors, String fieldPrefix) {

        // Validate
        ValidatorResults results = null;
        try {
            results = validator.validate();
        } catch (ValidatorException e) {
            LOGGER.error(e.getMessage(), e);
        }

        // Get Additional Argument
        final Arg arg = field.getArg(0);
        final String argKey = arg == null ? null : arg.getKey();
        Object argValue = null;
        if (argKey != null) {
            if ("#".equals(argKey)) {
                argValue = "" + index;
            } else {
                try {
                    argValue = PropertyUtils.getProperty(bean, argKey);
                } catch (Exception e) {
                    LOGGER.error("Error retrieving property '" + argKey + "' " + e.getMessage(), e);
                }
                argValue = argValue == null || "".equals(argValue) ? "???" : argValue;
            }
        }

        // Merge Errors
        if (newErrors.size() > 0) {

            if (fieldPrefix == null && argValue == null) {
                errors.add(newErrors);
            } else {
                final Iterator properties = newErrors.properties();
                while (properties.hasNext()) {
                    final String property = (String) properties.next();
                    final Iterator messages = newErrors.get(property);
                    while (messages.hasNext()) {
                        final ActionMessage msg = (ActionMessage) messages.next();
                        ActionMessage newMsg = msg;
                        final String newProperty = fieldPrefix == null ? property : fieldPrefix + property;
                        if (argValue != null) {

                            // Add the additonal "argument" to the messages
                            final Object[] args = msg.getValues();
                            Object[] newArgs = args;
                            int length = 0;
                            if (args != null) {
                                for (int i = 0; i < args.length; i++) {
                                    if (args[i] != null) {
                                        length = i + 1;
                                    }
                                }
                            }

                            if (length == 0) {
                                newArgs = new Object[] { argValue };
                            } else {
                                newArgs = new Object[length + 1];
                                System.arraycopy(args, 0, newArgs, 0, length);
                                newArgs[length] = argValue;
                            }
                            newMsg = new ActionMessage(msg.getKey(), newArgs);
                        }
                        errors.add(newProperty, newMsg);
                    }
                }
            }
        }

        // Clear Errors
        newErrors.clear();

        // Return results
        return results;

    }
*/
    /**
     * Method validatePreferenceValue.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
 /*   public static boolean validatePreferenceValue(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validatePreferenceValue";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validatePreferenceValue():Entered");
        }
        boolean validate = true;
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("Entered value=" + value);
        }
        if (BTSLUtil.isNullString(value)) {
            return true;
        }

        value = value.trim();
        final String valueType = ValidatorUtils.getValueAsString(bean, field.getVarValue("valueType"));
        String minValue = ValidatorUtils.getValueAsString(bean, field.getVarValue("minValue"));
        String maxValue = ValidatorUtils.getValueAsString(bean, field.getVarValue("maxValue"));
        final String allowAction = ValidatorUtils.getValueAsString(bean, field.getVarValue("allowAction"));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("validatePreferenceValue():Entered=>valueType=" + valueType + ",minValue=" + minValue + ",maxValue=" + maxValue + ",allowAction=" + allowAction);
        }
        if (BTSLUtil.isNullString(allowAction) || !"M".equals(allowAction.trim())) {
            return true;
        }
        double minimumValue = 0, maximumValue = 0;
        if (valueType.equals(PreferenceI.TYPE_INTEGER) || valueType.equals(PreferenceI.TYPE_LONG) || valueType.equals(PreferenceI.TYPE_AMOUNT)) {
            if (BTSLUtil.isNullString(minValue)) {
                minValue = "0";
            }
            if (BTSLUtil.isNullString(maxValue)) {
                maxValue = "99999999";
            }

            minimumValue = Double.parseDouble(minValue);
            maximumValue = Double.parseDouble(maxValue);
        }
        String arr[] = null;
        if (PreferenceI.TYPE_INTEGER.equals(valueType)) {
            // int inputValue;
            // int minimumValue=Integer.parseInt(minValue);
            // int maximumValue=Integer.parseInt(maxValue);
            try {
                final int inputValue = Integer.parseInt(value);
                validate = true;
                if (inputValue < minimumValue || inputValue > maximumValue) {
                    validate = false;
                    arr = new String[2];
                    arr[0] = minValue;
                    arr[1] = maxValue;
                    errors.add(field.getKey(), new ActionMessage("preference.error.msg.range", arr));
                }
            } catch (NumberFormatException nfe) {
                validate = false;
            }
        } else if (PreferenceI.TYPE_LONG.equals(valueType)) {
            try {
                final long inputValue = Long.parseLong(value);
                // long maximumValue=Long.parseLong(minValue);
                // long minimumValue=Long.parseLong(maxValue);
                validate = true;
                if (inputValue < minimumValue || inputValue > maximumValue) {
                    validate = false;
                    arr = new String[2];
                    arr[0] = minValue;
                    arr[1] = maxValue;
                    errors.add(field.getKey(), new ActionMessage("preference.error.msg.range", arr));
                }
            } catch (NumberFormatException nfe) {
                validate = false;
            }
        } else if (PreferenceI.TYPE_AMOUNT.equals(valueType)) {
            double inputValue = 0;
            try {
                inputValue = Double.parseDouble(value);
                validate = true;
                try {
                    if (PretupsBL.getSystemAmount(inputValue) < minimumValue || PretupsBL.getSystemAmount(inputValue) > maximumValue) {
                        validate = false;
                        arr = new String[2];
                        arr[0] = PretupsBL.getDisplayAmount(Long.parseLong(minValue));
                        arr[1] = PretupsBL.getDisplayAmount(Long.parseLong(maxValue));
                        errors.add(field.getKey(), new ActionMessage("preference.error.msg.range", arr));
                    }
                } catch (BTSLBaseException e) {
                    LOGGER.error(METHOD_NAME, e);
                    validate = false;
                    errors.add(field.getKey(), new ActionMessage("error.general.processing"));
                }
            } catch (NumberFormatException nfe) {
                validate = false;
            }
        } else if (PreferenceI.TYPE_BOOLEAN.equals(valueType)) {
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                validate = true;
            } else {
                validate = false;
            }
        } else if (PreferenceI.TYPE_DATE.equals(valueType)) {
            try {
                BTSLUtil.getDateFromDateString(value);
                validate = true;
            } catch (ParseException pex) {
                validate = false;
            }

        } else if (valueType.equals(PreferenceI.TYPE_STRING)) {
            validate = true;
        } else if(PreferenceI.TYPE_DECIMAL.equals(valueType))
		   {
			   try {
				   if( value.indexOf(".") > 0 )
					{					
						try
						{
							if(value.matches("^\\d+\\.\\d{2}$") || value.matches("^\\d+\\.\\d{1}$")) {
								double valueDouble=Double.parseDouble(value);
								if(valueDouble<0 || valueDouble>100){
									validate=false;
								} else {
									validate=true;
								}						
							} else {
								validate=false;
							}
						}
						catch(NumberFormatException nme)
						{
							validate = false;
						}
						
					}
					else
					{
						try
						{
							int valueInt=Integer.parseInt(value);
							if(valueInt>=0 && valueInt<=100) {
								validate=true;
							} else {
								validate=false;
							}
						}
						catch(NumberFormatException nme)
						{
							validate = false;
						}
					}
			} catch (Exception e) {
				validate=false;
			}
		   }
        
        if (!validate && errors != null && errors.isEmpty()) {
            errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validatePreferenceValue():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * This methos validate the From Time And To Time fields have valid value or
     * not
     */
    /**
     * Method validateFromTimeToTime.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
   /* public static boolean validateFromTimeToTime(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateFromTimeToTime():Entered");
        }
        boolean validate = true;

        String fromTime = ValidatorUtils.getValueAsString(bean, field.getProperty());
        String toTime = PretupsI.EMPTY;
        LOGGER.info("Entered fromTime Value=" + fromTime);
        if(!BTSLUtil.isNullString(field.getVarValue("allowedToTime"))) {
        	toTime = ValidatorUtils.getValueAsString(bean, field.getVarValue("allowedToTime"));    
        	LOGGER.info("Entered toTime Value=" + toTime);
        }
        
        if (!BTSLUtil.isNullString(fromTime)) {

            if (BTSLUtil.isNullString(fromTime)) {
                errors.add(field.getKey(), new ActionMessage("user.addoperatoruser.error.emptyfromtime"));
                validate = false;
            } else {
                if (!BTSLUtil.isValidTime(fromTime)) {
                    errors.add(field.getKey(), new ActionMessage("user.addoperatoruser.error.validfromtime"));
                    validate = false;
                }
            }
        }
        if (!BTSLUtil.isNullString(toTime)) {
            if (BTSLUtil.isNullString(toTime)) {
                errors.add(field.getKey(), new ActionMessage("user.addoperatoruser.error.emptytotime"));
                validate = false;
            } else {
                if (!BTSLUtil.isValidTime(toTime)) {
                    errors.add(field.getKey(), new ActionMessage("user.addoperatoruser.error.validtotime"));
                    validate = false;
                }
            }
        }

        if (!BTSLUtil.isNullString(fromTime) && !BTSLUtil.isNullString(toTime)) {
            if (validate) {
                if (fromTime.indexOf(":") == -1) {
                    fromTime = fromTime + ":00";
                }
                final int fromTimeHR = Integer.parseInt(fromTime.substring(0, 2));
                final int fromTimeMIN = Integer.parseInt(fromTime.substring(3, 5));

                if (toTime.indexOf(":") == -1) {
                    toTime = toTime + ":00";
                }
                final int toTimeHR = Integer.parseInt(toTime.substring(0, 2));
                final int toTimeMIN = Integer.parseInt(toTime.substring(3, 5));

                if (fromTimeHR > toTimeHR) {
                    errors.add(field.getKey(), new ActionMessage("user.addoperatoruser.error.validfromtimegreater"));
                    validate = false;
                } else if (fromTimeHR == toTimeHR && fromTimeMIN > toTimeMIN) {
                    errors.add(field.getKey(), new ActionMessage("user.addoperatoruser.error.validfromtimegreater"));
                    validate = false;
                }

            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateFromTimeToTime():Exit:return=" + validate);
        }
        return validate;
    }*/

    /**
     * Method validateDatePattern.
     * This method is define a rule which is used to match the date with the
     * specified
     * pattern .
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateDatePattern(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateDatePattern():Entered");
        }
        boolean validate = false;
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("validateDatePattern():Entered value=" + value);
        // if no data is entered the bypass the function.
        if (value != null && value.length() == 0) {
            validate = true;
        } else {
            try {
                if (value != null) {
                    BTSLUtil.getDateFromDateString(value);
                }
                validate = true;
            } catch (ParseException pex) {
                validate = false;
            }
        }
        if (!validate) {
            errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateDatePattern():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateMsisdn.
     * This is the validator method to validate the mobile number length
     * according to the entry in the database.
     * by calling a method of the BTSLUtil named isValidMSISDN().
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateMsisdn(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateMsisdn";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateMsisdn():Entered");
        }
        boolean validate = false;
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("validateMsisdn():Entered value=" + value);
        // if no data is entered the bypass the function.
        // Changed for handling blank spaces in mobile number field
        if (value == null || value.length() == 0) {
            validate = true;
        } else if (BTSLUtil.isNullString(value)) {
            validate = false;
        } else {
            try {
                value = PretupsBL.getFilteredMSISDN(value);
                validate = BTSLUtil.isValidMSISDN(value);
            } catch (BTSLBaseException e) {
                LOGGER.error(METHOD_NAME, e);
                validate = false;
            }
        }
        if (!validate) {
            errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateMsisdn():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateRange.
     * This is the validator method to validate the amount value in between
     * startRange and endRange
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */

  /*  public static boolean validateRange(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateRange";
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        final String startProperty = field.getVarValue("startRange");
        final String startRangeVal = ValidatorUtils.getValueAsString(bean, startProperty);

        final String endProperty = field.getVarValue("endRange");
        final String endRangeVal = ValidatorUtils.getValueAsString(bean, endProperty);

        final int amount = Integer.parseInt(value);
        final int startRange = Integer.parseInt(startRangeVal);
        final int endRange = Integer.parseInt(endRangeVal);

        if (amount < startRange || amount > endRange) {
            try {
                errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
                return false;
            } catch (Exception e) {
                LOGGER.error(METHOD_NAME, e);
                errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
                return false;
            }
        }

        return true;
    }
*/
    /**
     * Method validateFromToDate.
     * This validation method is to check that formDate must be before toDate.
     * This validateion is used to check the date range.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateFromToDate(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateFromToDate():Entered");
        }
        boolean validate = true;
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("Entered value=" + value);
        final String fromValue = ValidatorUtils.getValueAsString(bean, field.getVarValue("fromDate"));
        final String compareType = field.getVarValue("compareType");
        LOGGER.info("validateFromToDate():Entered=>fromDate=" + fromValue);
        if (value == null || value.length() == 0 || fromValue == null || fromValue.length() == 0) {
            return true;
        }

        final ActionMessage message = Resources.getActionMessage(request, va, field);
        int maxDays = 0;
        if (PretupsI.REPORTS.equalsIgnoreCase(compareType)) {
        	int reportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF))).intValue();
            maxDays = reportMaxDateDiff;
        } else if (PretupsI.CRYSTAL_REPORTS.equalsIgnoreCase(compareType)) {
        	int crystalReportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue();
            maxDays = crystalReportMaxDateDiff;            
        } else if (PretupsI.CRYSTAL_SUMMARY_REPORTS.equalsIgnoreCase(compareType)) {
        	int crystalSummaryReportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_SUMMARY_REPORT_MAX_DATEDIFF))).intValue();
            maxDays = crystalSummaryReportMaxDateDiff;
        } else {
        	int maxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_DATEDIFF))).intValue();
            maxDays = maxDateDiff;
        }

        try {
            final Date toDate = BTSLUtil.getDateFromDateString(value);
            final Date fromDate = BTSLUtil.getDateFromDateString(fromValue);
            final Date currentDate = new Date();
            if (fromDate.after(currentDate)) {
                validate = false;
                errors.add(field.getKey(), new ActionMessage("btsl.error.msg.fromdatebeforecurrentdate"));
            }
            if (toDate.after(currentDate)) {
                validate = false;
                errors.add(field.getKey(), new ActionMessage("btsl.error.msg.todatebeforecurrentdate"));
            } else if (!fromDate.after(toDate)) {
                final int noOfDays = BTSLUtil.getDifferenceInUtilDates(fromDate, toDate);
                if (noOfDays > maxDays || noOfDays < 0) {
                    validate = false;
                    errors.add(field.getKey(), new ActionMessage(message.getKey(), String.valueOf(maxDays)));
                } else {
                    validate = true;
                }
            } else {
                validate = false;
                errors.add(field.getKey(), new ActionMessage("btsl.error.msg.fromdatebeforetodate"));
            }
        } catch (Exception e) {
            LOGGER.error("Exception in method validateFromToDate() ", e);
            // _log.errorTrace("Exception in method validateFromToDate() ",e);
            validate = false;
            // errors.add(field.getKey(),new ActionMessage(message.getKey(),
            // String.valueOf(maxDays))); //for test lab bug fixing
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateFromToDate():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateFromToDateLessCurrentDate.
     * This validation method is to check that formDate must be before toDate.
     * This validateion is used to check the date range.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateFromToDateLessCurrentDate(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateFromToDateLessCurrentDate";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateFromToDateLessCurrentDate():Entered");
        }
        boolean validate = true;
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        final String fromValue = ValidatorUtils.getValueAsString(bean, field.getVarValue("fromDate"));
        final String compareType = field.getVarValue("compareType");
        if (value == null || value.length() == 0 || fromValue == null || fromValue.length() == 0) {
            return true;
        }

        final ActionMessage message = Resources.getActionMessage(request, va, field);
        int maxDays = 0;
        if (PretupsI.REPORTS.equalsIgnoreCase(compareType)) {
        	int reportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF))).intValue();
            maxDays = reportMaxDateDiff;
        } else if (PretupsI.CRYSTAL_REPORTS.equalsIgnoreCase(compareType)) {
        	int crystalReportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue();
            maxDays = crystalReportMaxDateDiff;
        } else if (PretupsI.CRYSTAL_SUMMARY_REPORTS.equalsIgnoreCase(compareType)) {
        	int crystalSummaryReportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_SUMMARY_REPORT_MAX_DATEDIFF))).intValue();
            maxDays = crystalSummaryReportMaxDateDiff;
        } else {
        	int maxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_DATEDIFF))).intValue();
            maxDays = maxDateDiff;
        }

        try {
            final Date toDate = BTSLUtil.getDateFromDateString(value);
            final Date fromDate = BTSLUtil.getDateFromDateString(fromValue);
            Date currentDate = new Date();
            currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));
            if (currentDate.compareTo(fromDate) <= 0) {
                validate = false;
                errors.add(field.getKey(), new ActionMessage("btsl.error.msg.fromdatelesscurrentdate"));
            }
            if (currentDate.compareTo(toDate) <= 0) {
                validate = false;
                errors.add(field.getKey(), new ActionMessage("btsl.error.msg.todatelesscurrentdate"));
            }
            if (!fromDate.after(toDate)) {
                final int noOfDays = BTSLUtil.getDifferenceInUtilDates(fromDate, toDate);
                if (noOfDays > maxDays || noOfDays < 0) {
                    validate = false;
                    errors.add(field.getKey(), new ActionMessage(message.getKey(), String.valueOf(maxDays)));
                } else {
                    validate = true;
                }
            } else {
                validate = false;
                errors.add(field.getKey(), new ActionMessage("btsl.error.msg.fromdatebeforetodate"));
            }
        } catch (Exception e) {
            validate = false;
            LOGGER.error(METHOD_NAME, e);
            // errors.add(field.getKey(),new ActionMessage(message.getKey(),
            // String.valueOf(maxDays))); //for test lab bug fixing
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateFromToDateLessCurrentDate():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateDateRange
     * This validateion is used to check the date range only .
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateDateRange(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateDateRange";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateFromToDate():Entered");
        }
        boolean validate = true;
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("Entered value=" + value);
        final String fromValue = ValidatorUtils.getValueAsString(bean, field.getVarValue("fromDate"));
        final String compareType = field.getVarValue("compareType");
        LOGGER.info("validateFromToDate():Entered=>fromDate=" + fromValue);
        if (value == null || value.length() == 0 || fromValue == null || fromValue.length() == 0) {
            return true;
        }

        final ActionMessage message = Resources.getActionMessage(request, va, field);
        int maxDays = 0;
        if (PretupsI.REPORTS.equalsIgnoreCase(compareType)) {
        	int reportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF))).intValue();
            maxDays = reportMaxDateDiff;
        } else if (PretupsI.CRYSTAL_REPORTS.equalsIgnoreCase(compareType)) {
        	int crystalReportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue();
            maxDays = crystalReportMaxDateDiff;
        } else {
        	int maxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_DATEDIFF))).intValue();
            maxDays = maxDateDiff;
        }

        try {
            final Date toDate = BTSLUtil.getDateFromDateString(value);
            final Date fromDate = BTSLUtil.getDateFromDateString(fromValue);

            if (!fromDate.after(toDate)) {
                final int noOfDays = BTSLUtil.getDifferenceInUtilDates(fromDate, toDate);
                if (noOfDays > maxDays || noOfDays < 0) {
                    validate = false;
                    errors.add(field.getKey(), new ActionMessage(message.getKey(), String.valueOf(maxDays)));
                } else {
                    validate = true;
                }
            } else {
                validate = false;
                errors.add(field.getKey(), new ActionMessage("btsl.error.msg.fromdatebeforetodate"));
            }
        } catch (Exception e) {
            LOGGER.error(METHOD_NAME, e);
            validate = false;
            errors.add(field.getKey(), new ActionMessage(message.getKey(), String.valueOf(maxDays)));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateDateRange():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateIpAddress.
     * This validation method is to check that host stirng should be between
     * 0.0.0.0 to 255.255.255.255
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateIpAddress(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateIpAddress";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateIpAddress():Entered");
        }
        boolean validate = true;
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("Entered value=" + value);
        if (value == null || value.length() == 0) {
            return true;
        }

        final ActionMessage message = Resources.getActionMessage(request, va, field);
        final String[] arr = new String[1];
        arr[0] = value;
        try {
            if (!BTSLUtil.isValidateIpAddress(value)) {
                validate = false;
                errors.add(field.getKey(), new ActionMessage(message.getKey(), arr));
            }
        } catch (Exception e) {
            LOGGER.error(METHOD_NAME, e);
            validate = false;
            errors.add(field.getKey(), new ActionMessage(message.getKey(), arr));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateIpAddress():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateIpAddress.
     * This validation method is to check that host stirng should be between
     * 0.0.0.0 to 255.255.255.255
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateIpAddressSeries(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateIpAddressSeries";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateIpAddressSeries():Entered");
        }
        boolean validate = true;
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        LOGGER.info("Entered value=" + value);
        if (value == null || value.length() == 0) {
            return true;
        }

        final StringTokenizer ipAddresses = new StringTokenizer(value, ",");
        final ActionMessage message = Resources.getActionMessage(request, va, field);
        String series = null;
        try {

            while (ipAddresses.hasMoreTokens()) {
                series = ipAddresses.nextToken();
                if (!BTSLUtil.isValidateIpAddress(series)) {
                    validate = false;
                    errors.add(field.getKey(), new ActionMessage(message.getKey(), series));
                }
            }
        } catch (Exception e) {
            LOGGER.error(METHOD_NAME, e);
            validate = false;
            errors.add(field.getKey(), new ActionMessage(message.getKey(), series));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateIpAddressSeries():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateNumberIsPositive.
     * This validation method is to validate if the entered value is positive
     * integer.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
/*
    public static boolean validateNumberIsPositive(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateNumberIsPositive";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateNumberIsPositive():Entered");
        }
        boolean validate = true;
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("Entered value=" + value);
        if (value == null || value.length() == 0) {
            return true;
        }
        // ActionMessage message = Resources.getActionMessage(request, va,
        // field);
        final String[] arr = new String[1];
        arr[0] = value;
        try {
            // if(Integer.parseInt(value)<0)
            if (Double.parseDouble(value) < 0) {
                validate = false;
                errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
                // errors.add(field.getKey(),new
                // ActionMessage(message.getKey(),arr));
            }
        } catch (Exception e) {
            LOGGER.error(METHOD_NAME, e);
            validate = false;
            errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
            // errors.add(field.getKey(),new
            // ActionMessage(message.getKey(),arr));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateNumberIsPositive():Exit:return=" + validate);
        }
        return validate;
    }
*/

    /*  	*//**
     * Method validateSMSPinSameDigit.
     * This validation method is to validate the sms pin should not the same
     * digit.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
    /*
     * public static boolean validateSMSPinSameDigit(Object bean,ValidatorAction
     * va,Field field,ActionMessages errors,HttpServletRequest request)
     * {
     * if(logger.isDebugEnabled())
     * logger.debug("validateSMSPinSameDigit():Entered");
     * boolean validate=true;
     * String p_smsPin =
     * ValidatorUtils.getValueAsString(bean,field.getProperty());
     * logger.info("Entered value="+p_smsPin);
     * if(p_smsPin==null || p_smsPin.length()==0)
     * return true;
     * 
     * //check the value with the default numeric value
     * String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_smsPin);
     * if(defaultPin.equals(p_smsPin))
     * return true;
     * 
     * //check the value with the default text value
     * defaultPin = BTSLUtil.getDefaultPasswordText(p_smsPin);
     * if(defaultPin.equals(p_smsPin))
     * return true;
     * 
     * int count = 0, ctr = 0, j = 0;
     * char pos1 = 0, pos = 0;
     * int result = 0;
     * //iterates thru the p_smsPin and validates that the number is neither in
     * 444444 or 11111
     * for(int i=0; i<p_smsPin.length(); i++)
     * {
     * pos = p_smsPin.charAt(i);
     * 
     * if(i < p_smsPin.length()-1)
     * pos1 = p_smsPin.charAt(i+1);
     * 
     * j = pos1;
     * if(pos == pos1)
     * count++;
     * else if(j == pos+1 || j == pos-1)
     * ctr++;
     * }
     * 
     * if(count == p_smsPin.length())
     * {
     * errors.add(field.getKey(), Resources.getActionMessage(request, va,
     * field));
     * validate = false;
     * }
     * else
     * {
     * validate = true;
     * }
     * 
     * if(logger.isDebugEnabled())
     * logger.debug("validateSMSPinSameDigit():Exit:return="+validate);
     * return validate;
     * }
     *//**
     * Method validateSMSPinConsecutive.
     * This validation method is to validate the sms pin should not be
     * consecutive number.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
    /*
     * public static boolean validateSMSPinConsecutive(Object
     * bean,ValidatorAction va,Field field,ActionMessages
     * errors,HttpServletRequest request)
     * {
     * if(logger.isDebugEnabled())
     * logger.debug("validateSMSPinConsecutive():Entered");
     * boolean validate=true;
     * String p_smsPin =
     * ValidatorUtils.getValueAsString(bean,field.getProperty());
     * logger.info("Entered value="+p_smsPin);
     * if(p_smsPin==null || p_smsPin.length()==0)
     * return true;
     * 
     * 
     * int count = 0, ctr = 0, j = 0;
     * char pos1 = 0, pos = 0;
     * int result = 0;
     * //iterates thru the p_smsPin and validates that the number is neither in
     * 123456 or 121212
     * for(int i=0; i<p_smsPin.length(); i++)
     * {
     * pos = p_smsPin.charAt(i);
     * 
     * if(i < p_smsPin.length()-1)
     * pos1 = p_smsPin.charAt(i+1);
     * 
     * j = pos1;
     * if(pos == pos1)
     * count++;
     * else if(j == pos+1 || j == pos-1)
     * ctr++;
     * }
     * 
     * if(ctr == (p_smsPin.length()-1))
     * {
     * errors.add(field.getKey(), Resources.getActionMessage(request, va,
     * field));
     * validate = false;
     * }else
     * {
     * validate = true;
     * }
     * 
     * if(logger.isDebugEnabled())
     * logger.debug("validateSMSPinConsecutive():Exit:return="+validate);
     * return validate;
     * }
     */
    /**
     * Method validatePasswordSameCharacter.
     * This validation method is to validate the password should not the same
     * character.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
    // comentd for OCI changes
  /*  public static boolean validatePasswordSameCharacter(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validatePasswordSameCharacter():Entered");
        }
        boolean validate = true;
        final String p_password = ValidatorUtils.getValueAsString(bean, field.getProperty());
        int passwordLength = p_password.length();
        LOGGER.info("Entered value=" + p_password);
        if (p_password == null || p_password.length() == 0) {
            return true;
        }

        // check the value with the default numeric value
        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_password);
        if (defaultPin.equals(p_password)) {
            return true;
        }

        // check the value with the default text value
        defaultPin = BTSLUtil.getDefaultPasswordText(p_password);
        if (defaultPin.equals(p_password)) {
            return true;
        }

        // int count = 0, ctr = 0, j = 0;
        int count = 0, j = 0;
        char pos1 = 0, pos = 0;
        // int result = 0;
        // iterates thru the p_password and validates that the value is neither
        // in 444444 or aaaaa
        for (int i = 0; i < passwordLength; i++) {
            pos = p_password.charAt(i);

            if (i < p_password.length() - 1) {
                pos1 = p_password.charAt(i + 1);
            }

            j = pos1;
            if (pos == pos1) {
                count++;
            }

        }

        if (count == p_password.length()) {
        	 if (LOGGER.isDebugEnabled()) {
                 LOGGER.debug("validatePasswordSameCharacter(): All characters can not be same");
        	 }
            errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
            validate = false;
        } else {
            validate = true;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validatePasswordSameCharacter():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validatePasswordConsecutive.
     * This validation method is to validate the password should not be
     * consecutive number or numbers.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
    // comentd for OCI changes
  /*  public static boolean validatePasswordConsecutive(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validatePasswordConsecutive():Entered");
        }
        boolean validate = true;
        final String p_password = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("Entered value=" + p_password);
        if (p_password == null || p_password.length() == 0) {
            return true;
        }
       int passwordLength = p_password.length();
        // int count = 0, ctr = 0, j = 0;
        int ctr = 0, j = 0;
        char pos1 = 0, pos = 0;
        // int result = 0;
        // iterates thru the p_password and validates that the number is neither
        // in 123456 or 121212
        for (int i = 0; i < passwordLength; i++) {
            pos = p_password.charAt(i);

            if (i < p_password.length() - 1) {
                pos1 = p_password.charAt(i + 1);
            }

            j = pos1;
            if (j == pos + 1 || j == pos - 1) {
                ctr++;
            }
        }

        if (ctr == (p_password.length() - 1)) {
        	if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("validatePasswordConsecutive(): Invalid p_password Concecutive");
        	}
            errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
            validate = false;
        } else {
            validate = true;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validatePasswordConsecutive():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateName.
     * This validation method is to validate if the entered value is valid name
     * or not.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
/*
    public static boolean validateName(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateName():Entered");
        }
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        final boolean validate = true;
        LOGGER.info("Entered value=" + value);
        if (value == null || value.length() == 0) {
            return true;
        }

        final int strLength = value.length();
        for (int i = 0; i < strLength; i++) {
            // if (value.charAt(i) == '|'||value.charAt(i) ==
            // '#'||value.charAt(i) == ':'||value.charAt(i) == '*')
            if (value.charAt(i) == '%') {

                errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
                return false;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateName():Exit:return=" + validate);
        }
        return validate;
    }
*/

    /**
     * Method validateTime.
     * This validation method is to validate if the entered value is valid time
     * or not.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
 /*   public static boolean validateTime(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateTime():Entered");
        }
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        boolean validate = true;
        LOGGER.info("Entered value=" + value);
        if (value == null || value.length() == 0) {
            return validate;
        } else {
            if (!BTSLUtil.isValidTime(value)) {
            	if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("validateTime(): Not a valid time:" + value);
            	}
                errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
                validate = false;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateTime():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateMsisdnSeries.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateMsisdnSeries(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateMsisdnSeries";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validatemsisdnSeries():Entered");
        }
        boolean validate = true;
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("Entered value=" + value);
        if (value == null || value.length() == 0) {
            return true;
        }
        final StringTokenizer msisdn = new StringTokenizer(value, ",");
        final ActionMessage message = Resources.getActionMessage(request, va, field);
        String series = null;
        try {
            while (msisdn.hasMoreTokens()) {
                series = msisdn.nextToken().trim();
                if (!BTSLUtil.isValidIdentificationNumber(PretupsBL.getFilteredIdentificationNumber(series))) {
                    validate = false;
                    errors.add(field.getKey(), new ActionMessage(message.getKey(), series));
                }
            }
        } catch (Exception e) {
            LOGGER.error(METHOD_NAME, e);
            validate = false;
            errors.add(field.getKey(), new ActionMessage(message.getKey(), series));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateMsisdnSeries():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /*
     * Here we check whether the msisdn is duplicate or not
     */
    /**
     * Method validateMsisdnDuplicate.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
 /*   public static boolean validateMsisdnDuplicate(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        boolean flag = true;
        final String str = ValidatorUtils.getValueAsString(bean, field.getProperty());

        final StringTokenizer value = new StringTokenizer(str, ",");
        final HashMap map = new HashMap();
        String series = null;
        while (value.hasMoreTokens()) {
            series = value.nextToken().trim();
            if (map.containsKey(series)) {
                final ActionMessage message = Resources.getActionMessage(request, va, field);
                final Object[] objOld = message.getValues();
                final ArrayList valuesList = new ArrayList();
                if (objOld != null) {
                    for (int i = 0, j = objOld.length; i < j; i++) {
                        if (objOld[i] != null && !"".equals(objOld)) {
                            valuesList.add(objOld[i]);
                        }
                    }
                    valuesList.add(series);
                }
                errors.add(field.getKey(), new ActionMessage(message.getKey(), valuesList.toArray()));
                flag = false;
            } else {
                map.put(series, series);
            }
        }
        return flag;
    }
*/
    /**
     * Method validatePortSeries.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
 /*   public static boolean validatePortSeries(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validatePortSeries";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validatePortSeries():Entered");
        }
        boolean validate = true;
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validatePortSeries(): Entered value=" + value);
        }
        if (value == null || value.length() == 0) {
            return true;
        }
        final ActionMessage message = Resources.getActionMessage(request, va, field);
        String portValue = null;
        try {

            if (value.charAt(0) == ',') {
                errors.add(field.getKey(), new ActionMessage("error.firstvalueiscomma", message.getValues()));
                return false;
            }
            if (value.charAt(value.length() - 1) == ',') {
                errors.add(field.getKey(), new ActionMessage("error.lastvalueiscomma", message.getValues()));
                return false;
            }
            if (value.contains(",,")) {
                errors.add(field.getKey(), new ActionMessage("error.blankvalueincommas", message.getValues()));
                return false;
            }
            final StringTokenizer portParseString = new StringTokenizer(value, ",");
            while (portParseString.hasMoreTokens()) {
                portValue = (portParseString.nextToken());
                if (BTSLUtil.isNullString(portValue)) {
                    validate = false;
                    errors.add(field.getKey(), new ActionMessage(message.getKey(), portValue));
                } else {
                    try {
                        Long.parseLong(portValue);
                    } catch (Exception e) {
                        LOGGER.error(METHOD_NAME, e);
                        validate = false;
                        errors.add(field.getKey(), new ActionMessage(message.getKey(), portValue));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(METHOD_NAME, e);
            validate = false;
            errors.add(field.getKey(), new ActionMessage(message.getKey(), portValue));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validatePortSeries():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validatePortDuplicate.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
   /* public static boolean validatePortDuplicate(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        boolean flag = true;
        final String str = ValidatorUtils.getValueAsString(bean, field.getProperty());

        final StringTokenizer value = new StringTokenizer(str, ",");
        final HashMap map = new HashMap();
        String series = null;
        while (value.hasMoreTokens()) {
            series = value.nextToken().trim();
            if (map.containsKey(series)) {
                final ActionMessage message = Resources.getActionMessage(request, va, field);
                final Object[] objOld = message.getValues();
                final ArrayList valuesList = new ArrayList();
                if (objOld != null) {
                    for (int i = 0, j = objOld.length; i < j; i++) {
                        if (objOld[i] != null && !"".equals(objOld)) {
                            valuesList.add(objOld[i]);
                        }
                    }
                    valuesList.add(series);
                }
                errors.add(field.getKey(), new ActionMessage(message.getKey(), valuesList.toArray()));
                flag = false;
            } else {
                map.put(series, series);
            }
        }
        return flag;
    }
*/
    /**
     * Method validateCommaSeperatedString.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateCommaSeperatedString(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateCommaSeperatedString";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateCommaSeperatedString():Entered");
        }
        boolean validate = true;

        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateCommaSeperatedString(): Entered value=" + value);
        }
        if (value == null || value.length() == 0) {
            return true;
        }

        final ActionMessage message = Resources.getActionMessage(request, va, field);

        // change 1 Bug No 3 -start
        final String strArr[] = new String[2];
        strArr[0] = value;
        strArr[1] = message.getValues()[0].toString();
        // change 1 Bug No 3 -end

        String stringValue = null;
        try {
            if (value.contains(",,")) {
                errors.add(field.getKey(), new ActionMessage("error.blankvalueincommas", message.getValues()));
                return false;
            }
            if (value.charAt(0) == ',') {
                errors.add(field.getKey(), new ActionMessage("error.firstvalueiscomma", message.getValues()));
                return false;
            }
            if (value.charAt(value.length() - 1) == ',') {
                errors.add(field.getKey(), new ActionMessage("error.lastvalueiscomma", message.getValues()));
                return false;
            }
            final StringTokenizer stringParseString = new StringTokenizer(value, ",");

            while (stringParseString.hasMoreTokens()) {
                stringValue = (stringParseString.nextToken());
                if (BTSLUtil.isNullString(stringValue)) {
                    validate = false;
                    // change 1 Bug No 3 -start
                    errors.add(field.getKey(), new ActionMessage(message.getKey(), strArr));
                    break;
                    // change 1 Bug No 3 -end
                }
            }
        } catch (Exception e) {
            LOGGER.error(METHOD_NAME, e);
            validate = false;
            // change 1 Bug No 3 -start
            errors.add(field.getKey(), new ActionMessage(message.getKey(), strArr));
            // change 1 Bug No 3 -end
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateCommaSeperatedString():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateStringDuplicate.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateStringDuplicate(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        boolean flag = true;
        final String str = ValidatorUtils.getValueAsString(bean, field.getProperty());

        final StringTokenizer value = new StringTokenizer(str, ",");
        final HashMap map = new HashMap();
        String stringValue = null;
        while (value.hasMoreTokens()) {
            stringValue = value.nextToken().trim();
            if (map.containsKey(stringValue)) {
                final ActionMessage message = Resources.getActionMessage(request, va, field);
                final Object[] objOld = message.getValues();
                final ArrayList valuesList = new ArrayList();
                if (objOld != null) {
                    for (int i = 0, j = objOld.length; i < j; i++) {
                        if (objOld[i] != null && !"".equals(objOld)) {
                            valuesList.add(objOld[i]);
                        }
                    }
                    valuesList.add(stringValue);
                }
                errors.add(field.getKey(), new ActionMessage(message.getKey(), valuesList.toArray()));
                flag = false;
            } else {
                map.put(stringValue, stringValue);
            }
        }
        return flag;
    }
*/
    /**
     * Method validateIdentificationNumber.
     * Change ID=ACCOUNTID
     * This is the validator method to validate the identification number
     * according to the entry in the database.
     * by calling a method of the BTSLUtil named isValidIdentificationNumber().
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateIdentificationNumber(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateIdentificationNumber";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateIdentificationNumber():Entered");
        }
        boolean validate = false;
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("validateIdentificationNumber():Entered value=" + value);
        // if no data is entered the bypass the function.
        // Changed for handling blank spaces in mobile number field
        if (value == null || value.length() == 0) {
            validate = true;
        } else if (BTSLUtil.isNullString(value)) {
            validate = false;
        } else {
            try {
                value = PretupsBL.getFilteredIdentificationNumber(value);
                validate = BTSLUtil.isValidIdentificationNumber(value);
            } catch (BTSLBaseException e) {
                LOGGER.error(METHOD_NAME, e);
                validate = false;
            }
        }
        if (!validate) {
            errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateIdentificationNumber():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateVOMSDatePattern.
     * This method is define a rule which is used to match the date with the
     * specified
     * VOMS date pattern .
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
 /*   public static boolean validateVOMSDatePattern(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateDatePattern():Entered");
        }
        boolean validate = false;
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("validateDatePattern():Entered value=" + value);
        // if no data is entered the bypass the function.
        if (value != null && value.length() == 0) {
            validate = true;
        } else {
            try {
                if (value != null) {
                    BTSLUtil.getDateFromVOMSDateString(value);
                }
                validate = true;
            } catch (ParseException pex) {
                validate = false;
            }
        }
        if (!validate) {
            errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateDatePattern():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateFromToDateForSeperateReportDB.
     * This validation method is to check that formDate must be before toDate
     * also check the current date flag.
     * This validateion is used to check the date range.
     * 
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
 /*   public static boolean validateFromToDateForSeperateReportDB(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateFromToDateForSeperateReportDB";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateFromToDateForSeperateReportDB():Entered");
        }
        boolean validate = true;
        final String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("Entered value=" + value);
        final String currentDateFlag = ValidatorUtils.getValueAsString(bean, field.getVarValue("currentDateFlag"));
        final String fromValue = ValidatorUtils.getValueAsString(bean, field.getVarValue("fromDate"));
        final String compareType = field.getVarValue("compareType");
        LOGGER.info("validateFromToDateForSeperateReportDB():Entered=>fromDate=" + fromValue);
        if (value == null || value.length() == 0 || fromValue == null || fromValue.length() == 0) {
            return true;
        }

        final ActionMessage message = Resources.getActionMessage(request, va, field);
        int maxDays = 0;
        if (PretupsI.REPORTS.equalsIgnoreCase(compareType)) {
        	int reportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF))).intValue();
            maxDays = reportMaxDateDiff;
        } else if (PretupsI.CRYSTAL_REPORTS.equalsIgnoreCase(compareType)) {
        	int crystalReportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue();
            maxDays = crystalReportMaxDateDiff;
        } else {
        	int maxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_DATEDIFF))).intValue();
            maxDays = maxDateDiff;
        }

        try {
            final Date toDate = BTSLUtil.getDateFromDateString(value);
            final Date fromDate = BTSLUtil.getDateFromDateString(fromValue);
            Date currentDate = new Date();
            String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
            boolean isSeperateRptDb = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_SEPARATE_RPT_DB))).booleanValue();
            currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate), systemDateFormat);
            if (isSeperateRptDb) {
                if (currentDateFlag != null && "Y".equals(currentDateFlag)) {
                    if (!fromDate.equals(currentDate)) {
                        validate = false;
                        errors.add(field.getKey(), new ActionMessage("btsl.error.msg.fromdateequaltocurrentdate"));
                    }
                    if (!toDate.equals(currentDate)) {
                        validate = false;
                        errors.add(field.getKey(), new ActionMessage("btsl.error.msg.todateequaltocurrentdate"));
                    }
                } else {
                    if (fromDate.after(currentDate) || fromDate.equals(currentDate)) {
                        validate = false;
                        errors.add(field.getKey(), new ActionMessage("btsl.error.msg.fromdatelessthancurrentdate"));
                    }
                    if (toDate.after(currentDate) || toDate.equals(currentDate)) {
                        validate = false;
                        errors.add(field.getKey(), new ActionMessage("btsl.error.msg.todatelessthancurrentdate"));
                    } else if (!fromDate.after(toDate)) {
                        final int noOfDays = BTSLUtil.getDifferenceInUtilDates(fromDate, toDate);
                        if (noOfDays > maxDays || noOfDays < 0) {
                            validate = false;
                            errors.add(field.getKey(), new ActionMessage(message.getKey(), String.valueOf(maxDays)));
                        } else {
                            validate = true;
                        }
                    } else {
                        validate = false;
                        errors.add(field.getKey(), new ActionMessage("btsl.error.msg.fromdatebeforetodate"));
                    }
                }
            } else {
                if (fromDate.after(currentDate)) {
                    validate = false;
                    errors.add(field.getKey(), new ActionMessage("btsl.error.msg.fromdatebeforecurrentdate"));
                }
                if (toDate.after(currentDate)) {
                    validate = false;
                    errors.add(field.getKey(), new ActionMessage("btsl.error.msg.todatebeforecurrentdate"));
                } else if (!fromDate.after(toDate)) {
                    final int noOfDays = BTSLUtil.getDifferenceInUtilDates(fromDate, toDate);
                    if (noOfDays > maxDays || noOfDays < 0) {
                        validate = false;
                        errors.add(field.getKey(), new ActionMessage(message.getKey(), String.valueOf(maxDays)));
                    } else {
                        validate = true;
                    }
                } else {
                    validate = false;
                    errors.add(field.getKey(), new ActionMessage("btsl.error.msg.fromdatebeforetodate"));
                }
            }
        } catch (Exception e) {
            LOGGER.error(METHOD_NAME, e);
            validate = false;
            // errors.add(field.getKey(),new ActionMessage(message.getKey(),
            // String.valueOf(maxDays))); //for test lab bug fixing
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateFromToDateForSeperateReportDB():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateMsisdn.
     * This is the validator method to validate the mobile number length
     * according to the entry in the database.
     * by calling a method of the BTSLUtil named isValidMSISDN().
     * 
     * @author diwakar
     * @date : 18-02-2014
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateMsisdnLength(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateMsisdnLength";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateMsisdn():Entered");
        }
        boolean validate = false;
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("validateMsisdn():Entered value=" + value);
        // if no data is entered the bypass the function.
        // Changed for handling blank spaces in mobile number field
        if (value == null || value.length() == 0) {
            validate = true;
        } else if (BTSLUtil.isNullString(value)) {
            validate = false;
        } else {
            try {
                value = PretupsBL.getFilteredMSISDN(value);
                validate = BTSLUtil.isValidMSISDNLength(value);
            } catch (BTSLBaseException e) {
                LOGGER.error(METHOD_NAME, e);
                validate = false;
            }
        }
        if (!validate) {
            errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateMsisdn():Exit:return=" + validate);
        }
        return validate;
    }
*/
    /**
     * Method validateMsisdn.
     * This is the validator method to validate the mobile number length
     * according to the entry in the database.
     * by calling a method of the BTSLUtil named isValidMSISDN().
     * 
     * @author diwakar
     * @date : 18-02-2014
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
  /*  public static boolean validateMsisdnDigit(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "validateMsisdnDigit";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateMsisdn():Entered");
        }
        boolean validate = false;
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        LOGGER.info("validateMsisdn():Entered value=" + value);
        // if no data is entered the bypass the function.
        // Changed for handling blank spaces in mobile number field
        if (value == null || value.length() == 0) {
            validate = true;
        } else if (BTSLUtil.isNullString(value)) {
            validate = false;
        } else {
            try {
                value = PretupsBL.getFilteredMSISDN(value);
                validate = BTSLUtil.isValidMSISDNDigit(value);
            } catch (BTSLBaseException e) {
                LOGGER.error(METHOD_NAME, e);
                validate = false;
            }
        }
        if (!validate) {
            errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("validateMsisdn():Exit:return=" + validate);
        }
        return validate;
    }
*/
    // added by Ashutosh
    public static boolean validateTime24Hours(String time) {
        Pattern pattern;
        Matcher matcher;
        final String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        pattern = Pattern.compile(TIME24HOURS_PATTERN);
        matcher = pattern.matcher(time);
        return matcher.matches();

    }

    /**
     * Method timeRangeValidation.
     * This is the validator method to validate the timeslab entered in the
     * additional commission profile.
     * by calling a method of the BTSLUtil named timeRangeValidation().
     * 
     * @author Ashutosh
     * @date : 17-09-2014
     * @param bean
     *            Object
     * @param va
     *            ValidatorAction
     * @param field
     *            Field
     * @param errors
     *            ActionMessages
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
 /*   public static boolean timeRangeValidation(Object bean, ValidatorAction va, Field field, ActionMessages errors, HttpServletRequest request) {
        final String METHOD_NAME = "timeRangeValidation";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("timeRangeValidation():Entered");
        }
        boolean validate = true;
        String value = ValidatorUtils.getValueAsString(bean, field.getProperty()); // ex.
        // 02:18-10:22,13:10-18:20
        LOGGER.info("Entered value=" + value);
        if (value == null || value.length() == 0) {
            return true;
        }
        *//*******************************************//*
        Pattern p = Pattern.compile("([0-9:,-]*)");
        Matcher m = p.matcher(value);
        boolean b = m.matches();
        if(!b) {
        	validate = false;
        	 errors.add(field.getKey(), new ActionMessage("error.msg.invalidchars"));
             return validate;
             }
       *//***********************************************//*

        final ActionMessage message = Resources.getActionMessage(request, va, field);
        int count = 0;

        final String[] w = value.split("\\s");
        value = "";
        for (int i = 0; i < w.length; i++) {
            value += w[i];
        }
        LOGGER.info("value=" + value);
        for (final char c : value.toCharArray()) {
            if (c == ',') {
                count++;
            }
        }
        final String[] commaSepatated = value.split(",");

        if (count != (commaSepatated.length - 1)) {
            validate = false;
        }
        try {

            String[] last = null;
            String[] previous = null;
            if (validate && commaSepatated.length > 0) {
                for (int i = 0; i < commaSepatated.length; i++) {
                    final String[] hyphenSeparated = commaSepatated[i].split("-");

                    if (hyphenSeparated.length == 2) {
                        for (int j = 0; j < hyphenSeparated.length; j++) {
                            final String[] current = hyphenSeparated[j].split(":");
                            if (current.length != 2 || current[0].length() != 2 || current[1].length() != 2) {
                                validate = false;
                                errors.add(field.getKey(), new ActionMessage("error.msg.invalidtimeformat"));
                                break;
                            }

                            if (Integer.parseInt(current[0]) < 0 || Integer.parseInt(current[0]) > 23) {
                                validate = false;
                                errors.add(field.getKey(), new ActionMessage("error.msg.invalidhour"));
                                break;
                            }
                            if (Integer.parseInt(current[1]) < 0 || Integer.parseInt(current[1]) > 59) {
                                validate = false;
                                errors.add(field.getKey(), new ActionMessage("error.msg.invalidminute"));
                                break;
                            }
                            if (j == 1) {
                                previous = hyphenSeparated[j - 1].split(":");
                                last = current;
                                if (Integer.parseInt(current[0]) < Integer.parseInt(previous[0]) || (Integer.parseInt(current[0]) == Integer.parseInt(previous[0]) && Integer
                                    .parseInt(current[1]) < Integer.parseInt(previous[1]))) {
                                    validate = false;
                                    errors.add(field.getKey(), new ActionMessage("error.msg.invalidrangelimits"));
                                    break;
                                }
                            }
                            // comparing lower and upper limits of time range
                            if (i > 0 && j == 0) {
                                if (Integer.parseInt(current[0]) < Integer.parseInt(last[0]) || (Integer.parseInt(current[0]) == Integer.parseInt(last[0]) && Integer
                                    .parseInt(current[1]) < Integer.parseInt(last[1]))) {
                                    validate = false;
                                    errors.add(field.getKey(), new ActionMessage("error.msg.timerangeoverlap"));
                                    break;
                                }
                            }
                        }
                    } else {
                        validate = false;
                        errors.add(field.getKey(), new ActionMessage("error.msg.invalidtimeformat"));
                        break;
                    }

                    if (validate == false) {
                        break;
                    }
                }
            } else {
                validate = false;
                errors.add(field.getKey(), new ActionMessage("error.msg.invalidtimeformat"));
            }

            if (!validate) {
                // errors.add(field.getKey(),
                // Resources.getActionMessage(request, va, field));
                // errors.add(field.getKey(),new
                // ActionMessage("btsl.error.msg.fromdatebeforecurrentdate"));
            }

        } catch (NumberFormatException e) {
            validate = false;
            errors.add(field.getKey(), new ActionMessage("error.msg.invalidtimeformat"));
            LOGGER.error(METHOD_NAME, e);
        } catch (Exception e) {
        	LOGGER.error("timeRangeValidation():validation failed");
            LOGGER.error(METHOD_NAME, e);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("timeRangeValidation():Exit:return=" + validate);
        }
        return validate;
    }
 */
    /**
	    * Method validateFromToDateByPassCurrentDate.
	    * This validation method is to check that formDate must be before toDate and by pass the current date.
	    * This validateion is used to check the date range.
	    * @param bean Object
	    * @param va ValidatorAction
	    * @param field Field
	    * @param errors ActionMessages
	    * @param request HttpServletRequest
	    * @return boolean
	    */
	/*   public static boolean validateFromToDateByPassCurrentDate(Object bean,ValidatorAction va,Field field,ActionMessages errors,HttpServletRequest request)
	   {
		   if(LOGGER.isDebugEnabled())
			   LOGGER.debug("validateFromToDate():Entered");
		   boolean validate=true;
		   String value = ValidatorUtils.getValueAsString(bean,field.getProperty());
		   LOGGER.info("Entered value="+value);
		   String fromValue = ValidatorUtils.getValueAsString(bean,field.getVarValue("fromDate"));
		   String compareType = field.getVarValue("compareType");
		   LOGGER.info("validateFromToDate():Entered=>fromDate="+fromValue);
		   if(value==null || value.length()==0 || fromValue==null || fromValue.length()==0)
			   	return true;

		   ActionMessage message = Resources.getActionMessage(request, va, field);
		   int maxDays =0;
		   if (PretupsI.REPORTS.equalsIgnoreCase(compareType)){
			   int reportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REPORT_MAX_DATEDIFF))).intValue();
			   maxDays=reportMaxDateDiff;
		   } else if (PretupsI.CRYSTAL_REPORTS.equalsIgnoreCase(compareType)){
			   int crystalReportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue();
			   maxDays=crystalReportMaxDateDiff;
		     //added by priyanka 21/01/11 for mobilecom
		   } else if (PretupsI.CRYSTAL_SUMMARY_REPORTS.equalsIgnoreCase(compareType)){
			   int crystalSummaryReportMaxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_SUMMARY_REPORT_MAX_DATEDIFF))).intValue();
			   maxDays=crystalSummaryReportMaxDateDiff;
		   } else {
			   int maxDateDiff = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_DATEDIFF))).intValue();
			   maxDays=maxDateDiff;
		   }
		   
		   if(BTSLUtil.isNullString(compareType)){
			   maxDays=366;
		   }
		   try
		   {
			   Date toDate = BTSLUtil.getDateFromDateString(value);
		   	   Date fromDate = BTSLUtil.getDateFromDateString(fromValue);
			  
			  if(!fromDate.after(toDate))
			   {
					int noOfDays = BTSLUtil.getDifferenceInUtilDates(fromDate,toDate);
					if(noOfDays>maxDays || noOfDays <0)
					{
						validate=false;
					   errors.add(field.getKey(),new ActionMessage(message.getKey(), String.valueOf(maxDays)));
					}
					else
						 validate=true;
			   }
			   else
			   {
				   validate=false;
				   errors.add(field.getKey(),new ActionMessage("btsl.error.msg.fromdatebeforetodate"));
			   }
		   }
		   catch(Exception e)
		   {
			   validate=false;
			  // errors.add(field.getKey(),new ActionMessage(message.getKey(), String.valueOf(maxDays)));    //for test lab bug fixing
		   }
		   if(LOGGER.isDebugEnabled())
			   LOGGER.debug("validateFromToDate():Exit:return="+validate);
		   return validate;
	   }
*/

}
