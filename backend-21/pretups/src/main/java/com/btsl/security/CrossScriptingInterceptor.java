package com.btsl.security;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CrossScriptingInterceptor {

    private static final long serialVersionUID = 1L;
    private Log log = LogFactory.getLog(CrossScriptingInterceptor.class);
    // private final String logouturl = "/login/login_logedout.action";
    private static Pattern ASCII_PATTERN = null;
    private static Pattern PASSWORD_PATTERN = null;
    private static String BLOCKED_KEY_WORDS = null;
    private static Pattern ALLOWED_ACC_NO_WORDS = null;
    private static ArrayList<Pattern> BLOCKED_WORDS_LIST = new ArrayList();
    // private static ArrayList<String> URL_KEY_LIST = new ArrayList();

    // Initialize the allowed and blocked characters/words
    /*
     * private void intialize() {
     * if (ASCII_PATTERN == null || PASSWORD_PATTERN == null
     * || BLOCKED_KEY_WORDS == null || ALLOWED_ACC_NO_WORDS == null) {
     * ASCII_PATTERN = Pattern.compile(Constants
     * .getProperty("ALLOWED_CHAR_REG_EXPRESSION"));
     * PASSWORD_PATTERN = Pattern.compile(Constants
     * .getProperty("BLOCKED_PASSWORD_CHAR_REG_EXPRESSION"));
     * ALLOWED_ACC_NO_WORDS = Pattern.compile(Constants
     * .getProperty("ALLOWED_ACC_NO_EXPRESSION"));
     * BLOCKED_KEY_WORDS = Constants.getProperty("BLOCKED_KEY_WORDS");
     * String[] strArray = BLOCKED_KEY_WORDS.split(",");
     * Pattern pattern = null;
     * for (int i = 0; i < strArray.length; i++) {
     * pattern = Pattern.compile("\\b" + strArray[i] + "\\b",
     * Pattern.CASE_INSENSITIVE);
     * BLOCKED_WORDS_LIST.add(pattern);
     * }
     * }
     * }
     */

    /*
     * private boolean validateRequestForXSS(Map<String, String[]> parameterMap)
     * throws IOException {
     * String key = null;
     * String value[] = null;
     * int length = 0;
     * boolean result = true;
     * 
     * for (Entry<String, String[]> mapEntry : parameterMap.entrySet()) {
     * key = mapEntry.getKey();
     * 
     * if (mapEntry.getValue() instanceof String[]) { // since some of the
     * // request params
     * // values are of
     * // String and other
     * value = mapEntry.getValue();
     * if (null != value)
     * length = value.length;
     * }
     * 
     * // Check for password field
     * if (key.equalsIgnoreCase("password")
     * || key.equalsIgnoreCase("newPassword")
     * || key.equalsIgnoreCase("confirmPassword")
     * || key.equalsIgnoreCase("dispConfirmPassword")
     * || key.equalsIgnoreCase("dispPassword")
     * || key.equalsIgnoreCase("oldPassword")
     * || key.equalsIgnoreCase("confirmNewPassword")
     * || key.equalsIgnoreCase("webPassword")
     * || key.equalsIgnoreCase("confWebPassword")
     * || key.equalsIgnoreCase("checkAll")
     * || key.equalsIgnoreCase("smsPin")
     * || key.equalsIgnoreCase("confSmsPin")) {
     * for (int j = 0; j < length; j++) {
     * if (value != null && value[j] != null
     * && (((PASSWORD_PATTERN.matcher(value[j])).find()))) {
     * log.error("Found unauthorized password value = "
     * + value[j]);
     * result = false;
     * //terminate(invocation);
     * }
     * }
     * } else if (key.equalsIgnoreCase("xvyc2")) // Check for java script
     * // is enabled or not.
     * // 1-Yes, 2- No
     * {
     * for (int j = 0; j < length; j++) {
     * if (value != null && value[j] != null
     * && (value[j].equalsIgnoreCase("2"))) { // 2 means
     * // no
     * log.error("The javascript is disabled value = "
     * + value[j]);
     * result = false;
     * //terminate(invocation);
     * }
     * }
     * 
     * } else if (key.equalsIgnoreCase("accountNumber")
     * || key.equals("accountNo")) {// Check a/c no other fields
     * // (i.e except password)
     * 
     * for (int j = 0; j < length; j++) {
     * String strAccountNumber = key;
     * if (value != null
     * && value[j] != null
     * && (!((ALLOWED_ACC_NO_WORDS.matcher(value[j]))
     * .find()))) {
     * log.error("Found unauthorized Value = " + value[j]
     * + " for key  = " + key);
     * result = false;
     * //terminate(invocation);
     * }
     * }
     * }
     * 
     * else {// Check all other fields (i.e except password)
     * for (int j = 0; j < length; j++) {
     * if (value != null && value[j] != null
     * && (!((ASCII_PATTERN.matcher(value[j])).find()))) {
     * log.error("Found unauthorized Value = " + value[j]
     * + " for key  = " + key);
     * result = false;
     * //terminate(invocation);
     * }
     * }
     * }
     * 
     * // Check for blocked words
     * for (int i = 0; i < BLOCKED_WORDS_LIST.size(); i++) {
     * for (int j = 0; j < length; j++) {
     * if (value != null
     * && value[j] != null
     * && ((BLOCKED_WORDS_LIST.get(i).matcher(value[j]))
     * .find())) {
     * log.error("Found Blocked Word = " + value[j]);
     * result = false;
     * //terminate(invocation);
     * }
     * }
     * }
     * 
     * }
     * return result;
     * }
     */
}
