package com.selftopup.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import javax.crypto.SecretKey;

import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;

public class AESKeyStore {
    public static KeyStore KS = null;
    public static KeyStore keyStr = null;
    public static SecretKey secKey = null;
    private static Log _log = LogFactory.getLog(AESKeyStore.class.getName());

    private static String KeyPass = null;
    private static String SecretKeyName = null;
    private static String StorePass = null;
    public static String KeyStoreName = null;

    public boolean LoadStoreKeyCredentials(String CredentialsFile) {
        if (_log.isDebugEnabled())
            _log.debug("LoadStoreKeyCredentials", "Entered");
        try {
            Properties pt = new Properties();
            if (_log.isDebugEnabled())
                _log.debug("LoadStoreKeyCredentials", " Entering for loading credential info");
            pt.load(new FileInputStream(CredentialsFile));

            StorePass = pt.getProperty("StorePass");
            KeyPass = pt.getProperty("KeyPass");
            SecretKeyName = pt.getProperty("SecretKeyName");
            KeyStoreName = pt.getProperty("KeyStoreName");
            if (_log.isDebugEnabled())
                _log.debug("LoadStoreKeyCredentials", " Exiting after loading credential info");
            return true;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return false;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("LoadStoreKeyCredentials", "Exiting");
        }
    }

    // Use this method only once, and follow the password
    // management standard for passwords
    public boolean CreateKeyStore(String AbsoluteKeyStoreFileName, String KeyStorePassword) {
        String StorePass = KeyStorePassword;
        if (_log.isDebugEnabled())
            _log.debug("CreateKeyStore", " Entering for Creating KetStore with file Name::" + AbsoluteKeyStoreFileName + ",KeyStorePassword::" + KeyStorePassword);
        try {
            char[] storePass = new char[KeyStorePassword.length()];
            StorePass.getChars(0, storePass.length, storePass, 0);
            /*
             * Create a keystore and place the key in it. We're using a "JCEKS"
             * keystore,
             * which is provided by Sun's JCE version 1.5. If you don't have the
             * JCE installed, you can use "JKS",
             * which is the default keystore. It doesn't provide the same level
             * of protection however.
             */
            KeyStore ks = KeyStore.getInstance("JCEKS");
            // In order to create an empty keystore, you pass null as the
            // InputStream argument to the load method.
            ks.load(null, null);
            ks.store(new FileOutputStream(AbsoluteKeyStoreFileName), storePass);
            if (_log.isDebugEnabled())
                _log.debug("CreateKeyStore", " Exiting after Creating  an empty KetStore");
            return true;
        } catch (KeyStoreException kse) {
            kse.printStackTrace();
            return false;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return false;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
            return false;
        } catch (CertificateException ce) {
            ce.printStackTrace();
            return false;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("CreateKeyStore", "Exiting");
        }
    }

    public static KeyStore LoadKeyStore(String KeyStoreFileName, String KeyStorePassword) {
        if (_log.isDebugEnabled())
            _log.debug("LoadKeyStore", " Entering for Loading KetStore");
        String StorePass = KeyStorePassword;

        try {
            char[] storePass = new char[KeyStorePassword.length()];
            StorePass.getChars(0, storePass.length, storePass, 0);
            keyStr = KeyStore.getInstance("jceks");
            keyStr.load(new FileInputStream(KeyStoreFileName), storePass);
            if (_log.isDebugEnabled())
                _log.debug("LoadKeyStore", " Exiting after loading keyStore");
            return keyStr;
        } catch (KeyStoreException kse) {
            kse.printStackTrace();
            return null;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
            return null;
        } catch (CertificateException ce) {
            ce.printStackTrace();
            return null;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("LoadKeyStore", "Exiting");
        }
    }

    // save my secret key

    public boolean StoreSymmetricKey(Key symKey, String KeyName, String KeyPassword, String KeyStoreFileName, String StorePassword) {
        if (_log.isDebugEnabled())
            _log.debug("StoreSymmetricKey", "Entered");
        // String StorePass = KeyPassword;
        char[] keyPass = new char[KeyPassword.length()];
        KeyPassword.getChars(0, keyPass.length, keyPass, 0);

        char[] storePass = new char[StorePassword.length()];
        StorePassword.getChars(0, storePass.length, storePass, 0);
        try {
            keyStr = LoadKeyStore(KeyStoreFileName, StorePassword);
            KeyStore.SecretKeyEntry ksk = new KeyStore.SecretKeyEntry((SecretKey) symKey);

            KeyStore.PasswordProtection kskpass = new KeyStore.PasswordProtection(keyPass);
            // Now we add the key to the keystore, protected by the password.
            keyStr.setEntry(KeyName, ksk, kskpass);
            /*
             * This line was required to ensure whenever the key is stored the
             * entire keystore
             * is re-written to flat file system.
             * Store the password to the filesystem, protected by the same
             * password.
             */
            keyStr.store(new FileOutputStream(KeyStoreFileName), storePass);
            return true;
        } catch (KeyStoreException kse) {
            kse.printStackTrace();
            return false;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return false;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
            return false;
        } catch (CertificateException ce) {
            ce.printStackTrace();
            return false;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("StoreSymmetricKey", "Exiting");
        }
    }

    public static SecretKey LoadSecretKey(String KeyName, String KeyPassword, String KeyStoreFileName, String StorePassword) {

        if (_log.isDebugEnabled())
            _log.debug("LoadSecretKey", "Entered");

        String StorePass = KeyPassword;
        char[] storePass = new char[StorePass.length()];
        StorePass.getChars(0, storePass.length, storePass, 0);

        try {
            keyStr = LoadKeyStore(KeyStoreFileName, StorePassword);
            KeyStore.PasswordProtection kskpass = new KeyStore.PasswordProtection(storePass);
            KeyStore.SecretKeyEntry ksk = (KeyStore.SecretKeyEntry) keyStr.getEntry(KeyName, kskpass);
            // secKey=ksk.getSecretKey();
            return ksk.getSecretKey(); // get my Secret key key
        } catch (KeyStoreException kse) {
            kse.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
            return null;
        } catch (UnrecoverableEntryException unrEe) {
            unrEe.printStackTrace();
            return null;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("LoadSecretKey", "Exiting");
        }
    }

    public static Key getKey() {
        return getKey("DB");
    }

    public static Key getKey(String p_keyStoreLoc) {
        if (_log.isDebugEnabled())
            _log.debug("getKey", "Entered p_keyStoreLoc: " + p_keyStoreLoc);
        if ("FILE".equalsIgnoreCase(p_keyStoreLoc)) {
            if (secKey == null) {
                String filePath = BTSLUtil.getFilePath(ConfigServlet.class);
                filePath = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
                new AESKeyStore().LoadStoreKeyCredentials(filePath + File.separatorChar + "Credentials.txt");
                secKey = LoadSecretKey(SecretKeyName, KeyPass, KeyStoreName, StorePass);
            }
        } else if ("DB".equalsIgnoreCase(p_keyStoreLoc)) {
            try {
                if (secKey == null) {
                    new AESKeyStore().LoadStoreKeyCredentials();
                    // new AESKeyStore().createAndStoreKeyStore();
                    secKey = LoadSecretKey(SecretKeyName, KeyPass, KeyStoreName, StorePass);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("getKey", "Exiting");
        return secKey;
    }

    private static HashMap<String, String> getKeyFromDBFirst() {
        if (_log.isDebugEnabled())
            _log.debug("getKeyFromDBFirst", "Entered ");
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        HashMap<String, String> keyMapFirst = new HashMap<String, String>();
        Connection con = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer("select key_pass,store_pass,sno,KEY_STORE_NAME,SECRET_KEY_NAME ");
            selectQueryBuff.append(" from credential_first ");
            selectQueryBuff.append(" where key_active = ? and rownum = 1");

            con = OracleUtil.getConnection();
            pstmtSelect = con.prepareStatement(selectQueryBuff.toString());
            pstmtSelect.setString(1, "Y");
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                keyMapFirst.put("KEY_PASS", rs.getString("KEY_PASS"));
                keyMapFirst.put("KEY_STORE_NAME", rs.getString("KEY_STORE_NAME"));
                keyMapFirst.put("STORE_PASS", rs.getString("STORE_PASS"));
                keyMapFirst.put("SECRET_KEY_NAME", rs.getString("SECRET_KEY_NAME"));
                keyMapFirst.put("S_NO", rs.getString("SNO"));
            }

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } catch (Exception btExe) {
            btExe.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("getKeyFromDBFirst", "Exting ");
        }
        return keyMapFirst;
    }

    private static HashMap<String, String> getKeyFromDBSecond(String p_Sno) {
        if (_log.isDebugEnabled())
            _log.debug("getKeyFromDBSecond", "Entered p_Sno: " + p_Sno);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        HashMap<String, String> keyMapSec = new HashMap<String, String>();
        Connection con = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer("select key_pass,key_store_name,store_pass,secret_key_name,sno ");
            selectQueryBuff.append(" from credential_sec ");
            selectQueryBuff.append(" where sno = ? and rownum = 1");
            con = OracleUtil.getConnection();
            pstmtSelect = con.prepareStatement(selectQueryBuff.toString());
            pstmtSelect.setString(1, p_Sno);
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                keyMapSec.put("KEY_PASS", rs.getString("KEY_PASS"));
                keyMapSec.put("KEY_STORE_NAME", rs.getString("KEY_STORE_NAME"));
                keyMapSec.put("STORE_PASS", rs.getString("STORE_PASS"));
                keyMapSec.put("SECRET_KEY_NAME", rs.getString("SECRET_KEY_NAME"));
                keyMapSec.put("S_No", rs.getString("SNO"));
            }

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } catch (Exception btExe) {
            btExe.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("getKeyFromDBSecond", "Exiting");
        }

        return keyMapSec;
    }

    public void createAndStoreKeyStore() {
        if (_log.isDebugEnabled())
            _log.debug("createAndStoreKeyStore", "Entered");
        try {
            LoadStoreKeyCredentials();
            AESKeyStore createKeyStore = new AESKeyStore();
            createKeyStore.CreateKeyStore(KeyStoreName, StorePass);
            createKeyStore.StoreSymmetricKey(AESEncryptionUtil.GenerateAESKey(), SecretKeyName, KeyPass, KeyStoreName, StorePass);
            // Use this line if you want to generate keystore by same key for
            // each time
            // createKeyStore.StoreSymmetricKey(GenerateKeyClassSingleton.getKey(),
            // SecretKeyName, KeyPass, KeyStoreName, StorePass);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("createAndStoreKeyStore", "Exiting");
        }
    }

    public void LoadStoreKeyCredentials() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("LoadStoreKeyCredentials", "Entered");
        try {

            CryptoUtil cryptoUtil = new CryptoUtil();

            HashMap<String, String> hMapFst;
            HashMap<String, String> hMapSnd;
            int lenFst = 0, lenSec = 0;
            StringBuffer keyPassBuf;

            hMapFst = getKeyFromDBFirst();
            hMapSnd = getKeyFromDBSecond(hMapFst.get("S_NO"));

            SecretKeyName = hMapFst.get("SECRET_KEY_NAME");

            lenFst = hMapFst.get("KEY_PASS").length();
            lenSec = hMapSnd.get("KEY_PASS").length();
            keyPassBuf = new StringBuffer(hMapFst.get("KEY_PASS").substring(0, lenFst / 2));
            keyPassBuf.append(hMapSnd.get("KEY_PASS").substring(0, lenSec / 2));
            keyPassBuf.append(hMapFst.get("KEY_PASS").substring(lenFst / 2, lenFst));
            keyPassBuf.append(hMapSnd.get("KEY_PASS").substring(lenSec / 2, lenSec));

            // KeyPass = BTSLUtil.decryptText(keyPassBuf.toString());
            KeyPass = cryptoUtil.decrypt(keyPassBuf.toString(), Constants.KEY);

            KeyStoreName = hMapFst.get("KEY_STORE_NAME");

            lenFst = hMapFst.get("STORE_PASS").length();
            lenSec = hMapSnd.get("STORE_PASS").length();
            keyPassBuf = new StringBuffer(hMapFst.get("STORE_PASS").substring(0, lenFst / 2));
            keyPassBuf.append(hMapSnd.get("STORE_PASS").substring(0, lenSec / 2));
            keyPassBuf.append(hMapFst.get("STORE_PASS").substring(lenFst / 2, lenFst));
            keyPassBuf.append(hMapSnd.get("STORE_PASS").substring(lenSec / 2, lenSec));

            // StorePass = BTSLUtil.decryptText(keyPassBuf.toString());
            StorePass = cryptoUtil.decrypt(keyPassBuf.toString(), Constants.KEY);
            keyPassBuf = null;

            if (_log.isDebugEnabled())
                _log.debug("LoadStoreKeyCredentials", " Exiting after loading credential info");

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("LoadStoreKeyCredentials", "Exiting");

        }
    }

    public static void main(String args[]) {

        try {
            if (args.length != 2) {
                System.out.println("Usage : [Constants file] [LogConfig file]");
                return;
            }
            File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                System.out.println(" Constants File Not Found .............");
                return;
            }
            File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                System.out.println(" Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

            AESEncryptionUtil bex = new AESEncryptionUtil();
            AESKeyStore test = new AESKeyStore();
            test.createAndStoreKeyStore();

            AESKeyStore.getKey();
            // The passwords can be loaded through properties file
            // The properties file should only be readable to root & tomcat user
            // rest of the users
            // should not be able to read it.
            System.out.println("Step-1: Encrypting Data: India pakistan India pakistan India pakistanIndia pakistanIndia pakistan");
            String encryptedText = bex.EncryptAES("1357");
            System.out.println("Encrypted Text::" + encryptedText + "\n Decrypted Content: " + bex.DecryptAES(encryptedText));
        } catch (Exception e) {
            System.out.println("Exception thrown in C2sMisDataProcessing: Not able to load files" + e);
            ConfigServlet.destroyProcessCache();
            return;
        }
    }
}