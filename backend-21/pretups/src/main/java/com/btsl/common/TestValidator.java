/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//validator/src/test/org/apache/commons/validator/TestValidator.java,v 1.14 2004/02/21 17:10:30 rleland Exp $
 * $Revision: 1.14 $
 * $Date: 2004/02/21 17:10:30 $
 *
 * ====================================================================
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.btsl.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericTypeValidator;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.util.ValidatorUtils;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

/**
 * Contains validation methods for different unit tests.
 */
public class TestValidator {

	
	/**
	 * ensures no instantiation
	 */
	private TestValidator(){
		
	}
	
	
	/**
	 * Throws a runtime exception if the value of the argument is "RUNTIME", an
	 * exception if the value of the argument is "CHECKED", and a
	 * ValidatorException otherwise.
	 * 
	 * @param value
	 *            string which selects type of exception to generate
	 * @throws BTSLBaseException 
	 * @throws RuntimeException
	 *             with "RUNTIME-EXCEPTION as message" if value is "RUNTIME"
	 * @throws Exception
	 *             with "CHECKED-EXCEPTION" as message if value is "CHECKED"
	 * @throws ValidatorException
	 *             with "VALIDATOR-EXCEPTION" as message otherwise
	 */
	public static boolean validateRaiseException(final Object bean,
			final Field field) throws BTSLBaseException, ValidatorException {

		final String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());

		if ("RUNTIME".equals(value)) {
			throw new RuntimeException("RUNTIME-EXCEPTION");

		} else if ("CHECKED".equals(value)) {
			throw new BTSLBaseException("CHECKED-EXCEPTION");

		} else {
			throw new ValidatorException("VALIDATOR-EXCEPTION");
		}
	}

	/**
	 * Checks if the field is required.
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field isn't <code>null</code> and has a length
	 *         greater than zero, <code>true</code> is returned. Otherwise
	 *         <code>false</code>.
	 */
	public static boolean validateRequired(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());

		return !GenericValidator.isBlankOrNull(value);
	}

	/**
	 * Checks if the field can be successfully converted to a <code>byte</code>.
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field can be successfully converted to a
	 *         <code>byte</code> <code>true</code> is returned. Otherwise
	 *         <code>false</code>.
	 */
	public static boolean validateByte(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());

		return GenericValidator.isByte(value);
	}

	/**
	 * Checks if the field can be successfully converted to a <code>short</code>
	 * .
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field can be successfully converted to a
	 *         <code>short</code> <code>true</code> is returned. Otherwise
	 *         <code>false</code>.
	 */
	public static boolean validateShort(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());

		return GenericValidator.isShort(value);
	}

	/**
	 * Checks if the field can be successfully converted to a <code>int</code>.
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field can be successfully converted to a
	 *         <code>int</code> <code>true</code> is returned. Otherwise
	 *         <code>false</code>.
	 */
	public static boolean validateInt(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());

		return GenericValidator.isInt(value);
	}

	/**
	 * Checks if field is positive assuming it is an integer
	 * 
	 * @param value
	 *            The value validation is being performed on.
	 * @param field
	 *            Description of the field to be evaluated
	 * @return boolean If the integer field is greater than zero, returns true,
	 *         otherwise returns false.
	 */
	public static boolean validatePositive(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());

		return GenericTypeValidator.formatInt(value).intValue() > 0;
	}

	/**
	 * Checks if the field can be successfully converted to a <code>long</code>.
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field can be successfully converted to a
	 *         <code>long</code> <code>true</code> is returned. Otherwise
	 *         <code>false</code>.
	 */
	public static boolean validateLong(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());

		return GenericValidator.isLong(value);
	}

	/**
	 * Checks if the field can be successfully converted to a <code>float</code>
	 * .
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field can be successfully converted to a
	 *         <code>float</code> <code>true</code> is returned. Otherwise
	 *         <code>false</code>.
	 */
	public static boolean validateFloat(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());

		return GenericValidator.isFloat(value);
	}

	/**
	 * Checks if the field can be successfully converted to a
	 * <code>double</code>.
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field can be successfully converted to a
	 *         <code>double</code> <code>true</code> is returned. Otherwise
	 *         <code>false</code>.
	 */
	public static boolean validateDouble(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());

		return GenericValidator.isDouble(value);
	}

	/**
	 * Checks if the field is an e-mail address.
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field is an e-mail address <code>true</code> is
	 *         returned. Otherwise <code>false</code>.
	 */
	public static boolean validateEmail(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());

		return GenericValidator.isEmail(value);
	}

	/**
	 * Checks field value length.
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field is of given length <code>true</code> is
	 *         returned. Otherwise <code>false</code>.
	 */
	public static boolean validateStringLength(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());
		if (value.length() > 100) {
			return false;
		}
		return true;

	}

	/**
	 * Checks field value length.
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field is of given length <code>true</code> is
	 *         returned. Otherwise <code>false</code>.
	 */
	public static boolean validateMatchRegexp(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());
		if (value == null || "".equalsIgnoreCase(value)) {
			return true;
		}
		return GenericValidator
				.matchRegexp(value, field.getVarValue("pattern"));
	}

	/**
	 * Validate date format
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field is of given length <code>true</code> is
	 *         returned. Otherwise <code>false</code>.
	 */
	public static boolean validateDateFormat(Object bean, Field field) {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());
		return GenericValidator
				.matchRegexp(value, field.getVarValue("pattern"));
	}

	/**
	 * Validate if date is less or equal current date
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field is of given length <code>true</code> is
	 *         returned. Otherwise <code>false</code>.
	 */
	public static boolean validateDateIsEqualOrLessThenCurrentDate(Object bean,
			Field field) throws ParseException {
		String value = ValidatorUtils.getValueAsString(bean,
				field.getProperty());
		String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
		if (format == null) {
			format = DATE_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = sdf.parse(value);
		Date currentDate = new Date();
		if (date.before(currentDate) || date.equals(currentDate)) {
			return true;
		}

		return false;
	}

	/**
	 * validate if date is less or equal to other date
	 *
	 * @param value
	 *            The value validation is being performed on.
	 * @return boolean If the field is of given length <code>true</code> is
	 *         returned. Otherwise <code>false</code>.
	 */
	public static boolean validateDateIsEqualOrLessThenOtherDate(Object bean,
			Field field) throws ParseException {
		String toDateValue = ValidatorUtils.getValueAsString(bean,
				field.getProperty());
		String fromDateValue = ValidatorUtils.getValueAsString(bean,
				field.getVarValue(field.getVarValue("fromDate")));
		if (fromDateValue == null || "".equalsIgnoreCase(fromDateValue)) {
			return true;
		}
		String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
		if (format == null) {
			format = DATE_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date fromDate = sdf.parse(fromDateValue);
		Date toDate = sdf.parse(toDateValue);

		if (fromDate.before(toDate) || fromDate.equals(toDate)) {
			return true;
		}

		return false;
	}
	
	public static boolean validateIfDateIsPastDate(Object object, Field field) throws ParseException{
		String dateField = ValidatorUtils.getValueAsString(object,	field.getProperty());
		String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
		if (format == null) {
			format = DATE_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date fieldDate = sdf.parse(dateField);
		Date tempdate = new Date();
		String newDate = sdf.format(tempdate);
		Date curruntDate = sdf.parse(newDate);
		if(!fieldDate.before(curruntDate)){
			return true;
		}
		
		return false;
	}
	
	public static boolean validateIfDateIsFutureDate(Object object, Field field) throws ParseException{
		String dateField = ValidatorUtils.getValueAsString(object,	field.getProperty());
		String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
		if (format == null) {
			format = DATE_FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date fieldDate = sdf.parse(dateField);
		Date tempdate = new Date();
		String newDate = sdf.format(tempdate);
		Date curruntDate = sdf.parse(newDate);
		if(!fieldDate.after(curruntDate)){
			return true;
		}
		
		return false;
	}

	/**
	 * Validate file extention
	 * 
	 * @param object
	 * @param field
	 * @return
	 */
	public static boolean validateFileExtention(Object object, Field field) {
		String file = ValidatorUtils.getValueAsString(object,
				field.getProperty());
		if (file == null || "".equalsIgnoreCase(file)) {
			return false;
		}

		String fileExtention = file.substring(file.lastIndexOf(".") + 1);
		String extention = field.getVarValue("extention");
		if (!fileExtention.equalsIgnoreCase(extention)) {
			return false;
		}

		return true;
	}

	public static boolean validateRequiredIfProvided(Object object, Field field) {
		String fieldValue = ValidatorUtils.getValueAsString(object, field.getProperty());
		String dependentFieldValue = ValidatorUtils.getValueAsString(object, field.getVarValue("dependent"));
		
		if(dependentFieldValue == null || "".equalsIgnoreCase(dependentFieldValue)){
			if(fieldValue == null || "".equalsIgnoreCase(fieldValue)){
				return true;
			}else{
				return false;
			}
		}
		
		return true;
	}

	public final static String FIELD_TEST_NULL = "NULL";
	public final static String FIELD_TEST_NOTNULL = "NOTNULL";
	public final static String FIELD_TEST_EQUAL = "EQUAL";
	public final static String DATE_FORMAT = PretupsI.DATE_FORMAT;

	public static boolean validateRequiredIf(Object bean, Field field,
			Validator validator) {

		Object form = validator.getParameterValue(Validator.BEAN_PARAM);
		String value = null;
		boolean required = false;
		if (isString(bean)) {
			value = (String) bean;
		} else {
			value = ValidatorUtils.getValueAsString(bean, field.getProperty());
		}
		int i = 0;
		String fieldJoin = "AND";
		if (!GenericValidator.isBlankOrNull(field.getVarValue("fieldJoin"))) {
			fieldJoin = field.getVarValue("fieldJoin");
		}
		if (fieldJoin.equalsIgnoreCase("AND")) {
			required = true;
		}
		while (!GenericValidator.isBlankOrNull(field.getVarValue("field[" + i
				+ "]"))) {
			String dependProp = field.getVarValue("field[" + i + "]");
			String dependTest = field.getVarValue("fieldTest[" + i + "]");
			String dependTestValue = field.getVarValue("fieldValue[" + i + "]");
			String dependIndexed = field.getVarValue("fieldIndexed[" + i + "]");
			if (dependIndexed == null)
				dependIndexed = "false";
			String dependVal = null;
			boolean this_required = false;
			if (field.isIndexed() && dependIndexed.equalsIgnoreCase("true")) {
				String key = field.getKey();
				if ((key.indexOf("[") > -1) && (key.indexOf("]") > -1)) {
					String ind = key.substring(0, key.indexOf(".") + 1);
					dependProp = ind + dependProp;
				}
			}
			dependVal = ValidatorUtils.getValueAsString(form, dependProp);
			if (dependTest.equals(FIELD_TEST_NULL)) {
				if ((dependVal != null) && (dependVal.length() > 0)) {
					this_required = false;
				} else {
					this_required = true;
				}
			}
			if (dependTest.equals(FIELD_TEST_NOTNULL)) {
				if ((dependVal != null) && (dependVal.length() > 0)) {
					this_required = true;
				} else {
					this_required = false;
				}
			}
			if (dependTest.equals(FIELD_TEST_EQUAL)) {
				this_required = dependTestValue.equalsIgnoreCase(dependVal);
			}
			if (fieldJoin.equalsIgnoreCase("AND")) {
				required = required && this_required;
			} else {
				required = required || this_required;
			}
			i++;
		}
		if (required) {
			if ((value != null) && (value.length() > 0)) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	private static Class stringClass = new String().getClass();

	private static boolean isString(Object o) {
		if (o == null)
			return true;
		return (stringClass.isInstance(o));
	}
	

}
