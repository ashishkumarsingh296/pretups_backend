package com.btsl.util;

/**
 * @description : This class will be used to specify regular expression for
 *              various validations.
 * @author diwakar
 * @date : 28-FEB-2014
 */
public interface RegularExpression {

    public static final String NUMERIC = "(\\d+)$";
    public final static String ALPHA_NUMERIC = "[0-9a-zA-Z]+";
    public final static String ALPHA_NUMERIC_SPECIAL_CHAR = ".*"; // 31-MAR-2014
    // it could be
    // any
    // characters
    public final static String ALPHA_NUMERIC_DOT_CHAR = "[0-9a-zA-Z_@.]+";
    public final static String ALPHABET = "[a-zA-Z]+";
    public final static String EMAIL = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    // 11-MAR-2104
    public final static String ALPHA_NUMERIC_SPECIAL_CHAR_ATLEAST = "(?=^.{6,255}$)((?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))^.*";
    public static final String NUMERIC_OR_DECIMAL = "^\\d+(?:\\.\\d{0,10})?$";
	public static final String NUMERIC_OR_UPTO_TWO_DECIMAL	= "\\d+(\\.\\d{1,2})?";
    // Ended Here

}
