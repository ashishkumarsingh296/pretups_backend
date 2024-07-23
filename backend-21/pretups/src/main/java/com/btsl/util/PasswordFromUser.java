/*
 * Created on Jul 1, 2010
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @author manisha.jain
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class PasswordFromUser {
    private static Log _log = LogFactory.getLog(PasswordFromUser.class.getName());

    private static String keyPass = null;
    private static String confirmKeyPass = null;
    private static String[] encryptKeyPass = new String[4];

    private static String storePass = null;
    private static String confirmStorePass = null;
    private static String[] encryprStorePass = new String[4];
    private static String keyShortName = "/pretups/pretups52_test/tomcat5/webapps/pretups/WEB-INF/classes/com/btsl/util/pretups_keystore.jceks";
    private static String secretKeyName = "Pretups_SecretKey";
    private static String consoleMeassage = "Got some internal error while updating system configurations. Try again";

    public boolean updatePasswordInformation(Connection p_firstDbCon, Connection p_SecondDbCon) {
        if (_log.isDebugEnabled()) {
            _log.debug("updatePasswordInformation", "Entered");
        }
        final String METHOD_NAME = "updatePasswordInformation";
        boolean valid = false;
        try {
            if (!getKeyPasswodDetails()) {
                return false;
            }

            if (!getStorePasswodDetails()) {
                return false;
            }

            if (!encryptPassword()) {
                return false;
            }
            addEntriesInDB(p_firstDbCon, p_SecondDbCon);

            valid = true;

        } catch (Exception e) {
            _log.error("updatePasswordInformation", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            if (p_firstDbCon != null) {
                try {
                    p_firstDbCon.rollback();
                } catch (Exception exc) {
                    _log.errorTrace(METHOD_NAME, exc);
                }
            }
            ;
            if (p_SecondDbCon != null) {
                try {
                    p_SecondDbCon.rollback();
                } catch (Exception exc) {
                    _log.errorTrace(METHOD_NAME, exc);
                }
            }
            ;
        } finally {
            if (p_firstDbCon != null) {
                try {
                    p_firstDbCon.commit();
                } catch (Exception exc) {
                    _log.errorTrace(METHOD_NAME, exc);
                }
            }
            ;
            if (p_SecondDbCon != null) {
                try {
                    p_SecondDbCon.commit();
                } catch (Exception exc) {
                    _log.errorTrace(METHOD_NAME, exc);
                }
            }
            ;

            if (_log.isDebugEnabled()) {
                _log.debug("updatePasswordInformation", "Exiting valid " + valid);
            }
        }
        return valid;
    }

    private static boolean getKeyPasswodDetails() {
        final String methodName = "getKeyPasswodDetails";
        boolean valid = false;
        String pass = null;
        // asks the user to input the password field usng the password class
        final PasswordField passfield = new PasswordField();
        try {
            _log.info(methodName, "Enter key password :- ");
            pass = passfield.getPassword("");
            /*
             * if(BTSLUtil.isNullString(pass))
             * {
             * for(int i=0;i<retryCount;i++)
             * {
             * System.out.println(
             * "Password cannot be left blank. Enter Key Password again:- ");
             * pass = passfield.getPassword("");
             * if(!BTSLUtil.isNullString(pass))
             * break;
             * else if(i==retryCount-1)
             * {
             * System.out.println("Password cannot be left blank. ");
             * return false;
             * }
             * }
             * }
             */

            _log.info(methodName, "Enter confirm key password :- ");
            confirmKeyPass = passfield.getPassword("");

            /*
             * if(BTSLUtil.isNullString(confirmKeyPass))
             * {
             * for(int i=0;i<retryCount;i++)
             * {
             * System.out.println(
             * "confirm key password cannot be left blank. Enter Key confirm key password again:- "
             * );
             * confirmKeyPass = passfield.getPassword("");
             * if(!BTSLUtil.isNullString(confirmKeyPass))
             * break;
             * else if(i==retryCount-1)
             * {
             * System.out.println("confirm key password cannot be left blank. ");
             * return false;
             * }
             * }
             * }
             */

            if (pass != null && !pass.equals(confirmKeyPass)) {
                _log.info(methodName, "Error :- Entered password and confirm should be same.");
                getKeyPasswodDetails();
            }
            keyPass = pass;
            valid = true;

        } catch (Exception e) {
            _log.error("getKeyPasswodDetails ", ": Key password could not be retreived ");

        }

        return valid;
    }

    private static boolean getStorePasswodDetails() {
        final String methodName = "getStorePasswodDetails";
        boolean valid = false;
        String pass = null;
        // asks the user to input the password field usng the password class
        final PasswordField passfield = new PasswordField();
        try {
            _log.info(methodName, "Enter store password :- ");
            pass = passfield.getPassword("");

            /*
             * if(BTSLUtil.isNullString(pass))
             * {
             * for(int i=0;i<retryCount;i++)
             * {
             * System.out.println(
             * "Password cannot be left blank. Enter store Password again:- ");
             * pass = passfield.getPassword("");
             * if(!BTSLUtil.isNullString(pass))
             * break;
             * else if(i==retryCount-1)
             * {
             * System.out.println("store password cannot be left blank. ");
             * return false;
             * }
             * }
             * }
             */

            _log.info(methodName, "Enter confirm store password :- ");
            confirmStorePass = passfield.getPassword("");

            /*
             * if(BTSLUtil.isNullString(confirmStorePass))
             * {
             * for(int i=0;i<retryCount;i++)
             * {
             * System.out.println(
             * "confirm store password cannot be left blank. Enter confirm store password again:- "
             * );
             * confirmStorePass = passfield.getPassword("");
             * if(!BTSLUtil.isNullString(confirmStorePass))
             * break;
             * else if(i==retryCount-1)
             * {
             * System.out.println("confirm store password cannot be left blank. "
             * );
             * return false;
             * }
             * }
             * }
             */

            if (pass != null && !pass.equals(confirmStorePass)) {
                _log.info(methodName, "Error :- Entered password and confirm should be same.");
                getStorePasswodDetails();
            }
            storePass = pass;
            valid = true;

        } catch (Exception e) {
            _log.error("getKeyPasswodDetails ", ": Store password could not be retreived ");
            System.out.println(consoleMeassage);
        }

        return valid;
    }

    private static int addEntriesInDB(Connection p_firstDbCon, Connection p_SecondDbCon) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("addEntriesInDB", "Entered");
        }
        final String METHOD_NAME = "addEntriesInDB";
        int updateCount = 0;
        PreparedStatement pstmUpdateFirst = null;
        PreparedStatement pstmSelectMaxSNO = null;
        PreparedStatement pstmUpdateKeyActive = null;
        PreparedStatement pstmUpdateSecond = null;
        ResultSet rstSelectMaxSNO = null;
        int sNo = 0;
        try {
            final String selctMaxSNO = " select max(SNO) SNO from CREDENTIAL_FIRST ";

            final String updateActiveKey = " UPDATE CREDENTIAL_FIRST SET KEY_ACTIVE='N' WHERE KEY_ACTIVE='Y' ";

            final StringBuffer inserFirstBuffer = new StringBuffer(" INSERT INTO CREDENTIAL_FIRST(KEY_PASS, KEY_STORE_NAME, STORE_PASS, SECRET_KEY_NAME, KEY_ACTIVE, SNO) ");
            inserFirstBuffer.append(" VALUES (?,?,?,?,?,?) ");

            final String firstQuery = inserFirstBuffer.toString();

            final StringBuffer inserSeondBuffer = new StringBuffer(" INSERT INTO CREDENTIAL_SEC(KEY_PASS, KEY_STORE_NAME, STORE_PASS, SECRET_KEY_NAME, SNO) ");
            inserSeondBuffer.append(" VALUES (?,?,?,?,?) ");

            final String secondQuery = inserSeondBuffer.toString();

            _log.debug("addEntriesInDB", "selctMaxSNO= " + selctMaxSNO);
            _log.debug("addEntriesInDB", "updateActiveKey= " + updateActiveKey);
            _log.debug("addEntriesInDB", "firstQuery= " + firstQuery);
            _log.debug("addEntriesInDB", "secondQuery= " + secondQuery);

            pstmSelectMaxSNO = p_firstDbCon.prepareStatement(selctMaxSNO);

            rstSelectMaxSNO = pstmSelectMaxSNO.executeQuery();
            if (rstSelectMaxSNO.next()) {
                sNo = rstSelectMaxSNO.getInt("SNO") + 1;
            } else {
                sNo = 1;
            }
            pstmUpdateKeyActive = p_firstDbCon.prepareStatement(updateActiveKey);
            pstmUpdateKeyActive.executeUpdate();

            int i = 0;
            pstmUpdateFirst = p_firstDbCon.prepareStatement(firstQuery);
            pstmUpdateFirst.setString(++i, encryptKeyPass[0] + encryptKeyPass[2]);
            pstmUpdateFirst.setString(++i, keyShortName);
            pstmUpdateFirst.setString(++i, encryprStorePass[0] + encryprStorePass[2]);
            pstmUpdateFirst.setString(++i, secretKeyName);
            pstmUpdateFirst.setString(++i, "Y");
            pstmUpdateFirst.setInt(++i, sNo);
            updateCount = pstmUpdateFirst.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException("addEntriesInDB", "Error in updating CREDENTIAL_FIRST table");
            }

            i = 0;
            pstmUpdateSecond = p_SecondDbCon.prepareStatement(secondQuery);
            pstmUpdateSecond.setString(++i, encryptKeyPass[1] + encryptKeyPass[3]);
            pstmUpdateSecond.setString(++i, keyShortName);
            pstmUpdateSecond.setString(++i, encryprStorePass[1] + encryprStorePass[3]);
            pstmUpdateSecond.setString(++i, secretKeyName);
            pstmUpdateSecond.setInt(++i, sNo);
            updateCount = pstmUpdateSecond.executeUpdate();
            if (updateCount <= 0) {
                throw new BTSLBaseException("addEntriesInDB", "Error in updating CREDENTIAL_SEC table");
            }

        } catch (SQLException sqe) {
            _log.error("addEntriesInDB", "SQLException : " + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            throw new BTSLBaseException("addEntriesInDB", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error("addEntriesInDB", "Exception : " + ex);
            _log.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException("addEntriesInDB", "error.general.processing");
        } finally {
            try {
                if (rstSelectMaxSNO != null) {
                    rstSelectMaxSNO.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmUpdateFirst != null) {
                    pstmUpdateFirst.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmSelectMaxSNO != null) {
                    pstmSelectMaxSNO.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmUpdateKeyActive != null) {
                    pstmUpdateKeyActive.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmUpdateSecond != null) {
                    pstmUpdateSecond.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addEntriesInDB", "Exiting updateCount " + updateCount);
            }
        }
        return updateCount;

    }

    private static boolean encryptPassword() throws BTSLBaseException {
        final String methodName = "encryptPassword";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        boolean isValid = false;
        String encrptedKeyPass = null;
        String encrptedStorePass = null;
        int length = 0;
        int j = 0;
        try {
            final CryptoUtil cryptoUtil = new CryptoUtil();
            encrptedKeyPass = cryptoUtil.encrypt(keyPass, Constants.KEY);
            encrptedStorePass = cryptoUtil.encrypt(storePass, Constants.KEY);
            length = encrptedKeyPass.length() / 4;
            for (int i = 0; i < 4; i++) {
                if (i == 3) {
                    encryptKeyPass[i] = encrptedKeyPass.substring(j, encrptedKeyPass.length());
                } else {
                    encryptKeyPass[i] = encrptedKeyPass.substring(j, j + length);
                }
                j = j + length;
            }
            j = 0;
            length = encrptedStorePass.length() / 4;
            for (int i = 0; i < 4; i++) {
                if (i == 3) {
                    encryprStorePass[i] = encrptedStorePass.substring(j, encrptedStorePass.length());
                } else {
                    encryprStorePass[i] = encrptedStorePass.substring(j, j + length);
                }
                j = j + length;
            }
            _log.debug(methodName, " " + encrptedKeyPass);
            _log.debug(methodName, " " + encrptedStorePass);
            isValid = true;
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            throw new BTSLBaseException(methodName, "error.general.processing");
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isValid " + isValid);
            }
        }
        return isValid;
    }

}
