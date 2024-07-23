package com.btsl.util;

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

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.MComReportDBConnection;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class AESKeyStore {
    private static KeyStore keyStr = null;
    private static SecretKey secKey = null;
    private static Log _log = LogFactory.getLog(AESKeyStore.class.getName());

    private static String KeyPass = null;
    private static String SecretKeyName = null;
    private static String StorePass = null;
    private static String KeyStoreName = null;

    public boolean LoadStoreKeyCredentials(String CredentialsFile) {
        FileInputStream fileInputStream = null;
        final String methodName = "LoadStoreKeyCredentials";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        try {
            final Properties pt = new Properties();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Entering for loading credential info");
            }
            fileInputStream = new FileInputStream(CredentialsFile);
            pt.load(fileInputStream);

            StorePass = pt.getProperty("StorePass");
            KeyPass = pt.getProperty("KeyPass");
            SecretKeyName = pt.getProperty("SecretKeyName");
            KeyStoreName = pt.getProperty("KeyStoreName");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting after loading credential info");
            }
            return true;
        } catch (FileNotFoundException fnfe) {
            _log.errorTrace(methodName, fnfe);
            return false;
        } catch (IOException ioe) {
            _log.errorTrace(methodName, ioe);
            return false;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }

    public boolean LoadStoreKeyCredentials(String CredentialsFile,String keyStoreFile) {
        FileInputStream fileInputStream = null;
        final String methodName = "LoadStoreKeyCredentials";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        try {
            final Properties pt = new Properties();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Entering for loading credential info");
            }
            fileInputStream = new FileInputStream(CredentialsFile);
            pt.load(fileInputStream);

            StorePass = pt.getProperty("StorePass");
            KeyPass = pt.getProperty("KeyPass");
            SecretKeyName = pt.getProperty("SecretKeyName");
            KeyStoreName = keyStoreFile;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting after loading credential info");
            }
            return true;
        } catch (FileNotFoundException fnfe) {
            _log.errorTrace(methodName, fnfe);
            return false;
        } catch (IOException ioe) {
            _log.errorTrace(methodName, ioe);
            return false;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }
    
    // Use this method only once, and follow the password
    // management standard for passwords
    public boolean CreateKeyStore(String AbsoluteKeyStoreFileName, String KeyStorePassword) {
        FileOutputStream fileOutputStream = null;
        final String StorePass = KeyStorePassword;
        final String methodName = "CreateKeyStore";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entering for Creating KetStore with file Name::" + AbsoluteKeyStoreFileName + ",KeyStorePassword::" + KeyStorePassword);
        }
        try {
            final char[] storePass = new char[KeyStorePassword.length()];
            StorePass.getChars(0, storePass.length, storePass, 0);
            /*
             * Create a keystore and place the key in it. We're using a "JCEKS"
             * keystore,
             * which is provided by Sun's JCE version 1.5. If you don't have the
             * JCE installed, you can use "JKS",
             * which is the default keystore. It doesn't provide the same level
             * of protection however.
             */
            final KeyStore ks = KeyStore.getInstance("JCEKS");
            // In order to create an empty keystore, you pass null as the
            // InputStream argument to the load method.
            ks.load(null, null);
            fileOutputStream = new FileOutputStream(AbsoluteKeyStoreFileName);
            ks.store(fileOutputStream, storePass);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting after Creating  an empty KetStore");
            }
            return true;
        } catch (KeyStoreException kse) {
            _log.errorTrace(methodName, kse);
            return false;
        } catch (FileNotFoundException fnfe) {
            _log.errorTrace(methodName, fnfe);
            return false;
        } catch (IOException ioe) {
            _log.errorTrace(methodName, ioe);
            return false;
        } catch (NoSuchAlgorithmException nsae) {
            _log.errorTrace(methodName, nsae);
            return false;
        } catch (CertificateException ce) {
            _log.errorTrace(methodName, ce);
            return false;
        } finally {

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }

    public static KeyStore LoadKeyStore(String KeyStoreFileName, String KeyStorePassword) {
        FileInputStream fileInputStream = null;
        final String methodName = "LoadKeyStore";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entering for Loading KetStore");
        }
        final String StorePass = KeyStorePassword;

        try {
            final char[] storePass = new char[KeyStorePassword.length()];
            StorePass.getChars(0, storePass.length, storePass, 0);
            keyStr = KeyStore.getInstance("jceks");
            fileInputStream = new FileInputStream(KeyStoreFileName);
            keyStr.load(fileInputStream, storePass);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting after loading keyStore");
            }
            return keyStr;
        } catch (KeyStoreException kse) {
            _log.errorTrace(methodName, kse);
            return null;
        } catch (FileNotFoundException fnfe) {
            _log.errorTrace(methodName, fnfe);
            ;
            return null;
        } catch (IOException ioe) {
            _log.errorTrace(methodName, ioe);
            return null;
        } catch (NoSuchAlgorithmException nsae) {
            _log.errorTrace(methodName, nsae);
            return null;
        } catch (CertificateException ce) {
            _log.errorTrace(methodName, ce);
            return null;
        } finally {

            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }

    // save my secret key

    public boolean StoreSymmetricKey(Key symKey, String KeyName, String KeyPassword, String KeyStoreFileName, String StorePassword) {
        FileOutputStream fileOutputStream = null;
        final String methodName = "StoreSymmetricKey";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        // String StorePass = KeyPassword;
        final char[] keyPass = new char[KeyPassword.length()];
        KeyPassword.getChars(0, keyPass.length, keyPass, 0);

        final char[] storePass = new char[StorePassword.length()];
        StorePassword.getChars(0, storePass.length, storePass, 0);
        try {
            keyStr = LoadKeyStore(KeyStoreFileName, StorePassword);
            final KeyStore.SecretKeyEntry ksk = new KeyStore.SecretKeyEntry((SecretKey) symKey);

            final KeyStore.PasswordProtection kskpass = new KeyStore.PasswordProtection(keyPass);
            // Now we add the key to the keystore, protected by the password.
            keyStr.setEntry(KeyName, ksk, kskpass);
            /*
             * This line was required to ensure whenever the key is stored the
             * entire keystore
             * is re-written to flat file system.
             * Store the password to the filesystem, protected by the same
             * password.
             */
            fileOutputStream = new FileOutputStream(KeyStoreFileName);
            keyStr.store(fileOutputStream, storePass);
            return true;
        } catch (KeyStoreException kse) {
            _log.errorTrace(methodName, kse);
            return false;
        } catch (FileNotFoundException fnfe) {
            _log.errorTrace(methodName, fnfe);
            return false;
        } catch (IOException ioe) {
            _log.errorTrace(methodName, ioe);
            return false;
        } catch (NoSuchAlgorithmException nsae) {
            _log.errorTrace(methodName, nsae);
            return false;
        } catch (CertificateException ce) {
            _log.errorTrace(methodName, ce);
            return false;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }

    public static SecretKey LoadSecretKey(String KeyName, String KeyPassword, String KeyStoreFileName, String StorePassword) {

        final String methodName = "LoadSecretKey";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }

        final String StorePass = KeyPassword;
        final char[] storePass = new char[StorePass.length()];
        StorePass.getChars(0, storePass.length, storePass, 0);

        try {
            keyStr = LoadKeyStore(KeyStoreFileName, StorePassword);
            final KeyStore.PasswordProtection kskpass = new KeyStore.PasswordProtection(storePass);
            final KeyStore.SecretKeyEntry ksk = (KeyStore.SecretKeyEntry) keyStr.getEntry(KeyName, kskpass);
            // secKey=ksk.getSecretKey();
            return ksk.getSecretKey(); // get my Secret key key
        } catch (KeyStoreException kse) {
            _log.errorTrace(methodName, kse);
            return null;
        } catch (NoSuchAlgorithmException nsae) {
            _log.errorTrace(methodName, nsae);
            return null;
        } catch (UnrecoverableEntryException unrEe) {
            _log.errorTrace(methodName, unrEe);
            return null;
        } finally {

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }

    public static Key getKey() {
        return getKey("FILE");
    }

    public static Key getKey(String p_keyStoreLoc) {
        final String methodName = "getKey";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_keyStoreLoc: " + p_keyStoreLoc);
        }
        if ("FILE".equalsIgnoreCase(p_keyStoreLoc)) {
            if (secKey == null) {
                String filePath =Constants.getProperty("VOMS_ALGO_PROP_DIR"); //
                if(filePath==null)
                {    
                       filePath=BTSLUtil.getFilePath(ConfigServlet.class);
                       filePath = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
                }
                new AESKeyStore().LoadStoreKeyCredentials(filePath + File.separatorChar + "Credentials.txt",filePath + File.separatorChar + "pretups_keystore.jceks");
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
                _log.errorTrace(methodName, e);
            } finally {
                // Do nothing
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting");
        }
        return secKey;
    }


    private static HashMap<String, String> getKeyFromDBFirst() {
        final String methodName = "getKeyFromDBFirst";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final HashMap<String, String> keyMapFirst = new HashMap<String, String>();
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
        	AESKeystoreQry aesKeystoreQry=(AESKeystoreQry)ObjectProducer.getObject(QueryConstants.AESKEYSTORE_QRY, QueryConstants.QUERY_PRODUCER);
            
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            pstmtSelect = con.prepareStatement(aesKeystoreQry.getKeyFromDBFirst());
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
            _log.errorTrace(methodName, sqlEx);
        } catch (Exception btExe) {
            _log.errorTrace(methodName, btExe);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
			if (mcomCon != null) {
				mcomCon.close("AESKeyStore#HashMap");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exting ");
            }
        }
        return keyMapFirst;
    }

    private static HashMap<String, String> getKeyFromDBSecond(String p_Sno) {
        final String methodName = "getKeyFromDBSecond";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_Sno: " + p_Sno);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final HashMap<String, String> keyMapSec = new HashMap<String, String>();
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
           
        	AESKeystoreQry aesKeystoreQry=(AESKeystoreQry)ObjectProducer.getObject(QueryConstants.AESKEYSTORE_QRY, QueryConstants.QUERY_PRODUCER);
        	mcomCon = new MComReportDBConnection();
        	con=mcomCon.getConnection();
            pstmtSelect = con.prepareStatement(aesKeystoreQry.getKeyFromDBSecond());
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
            _log.errorTrace(methodName, sqlEx);
        } catch (Exception btExe) {
            _log.errorTrace(methodName, btExe);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if(mcomCon != null){mcomCon.close("AESKeyStore#getKeyFromDBSecond");mcomCon=null;}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }

        return keyMapSec;
    }

    public void createAndStoreKeyStore() {
        final String methodName = "createAndStoreKeyStore";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        try {
            LoadStoreKeyCredentials();
            final AESKeyStore createKeyStore = new AESKeyStore();
            createKeyStore.CreateKeyStore(KeyStoreName, StorePass);
            createKeyStore.StoreSymmetricKey(AESEncryptionUtil.GenerateAESKey(), SecretKeyName, KeyPass, KeyStoreName, StorePass);
            // Use this line if you want to generate keystore by same key for
            // each time
            // createKeyStore.StoreSymmetricKey(GenerateKeyClassSingleton.getKey(),
            // SecretKeyName, KeyPass, KeyStoreName, StorePass);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }
        }
    }

    public void LoadStoreKeyCredentials() throws Exception {
        final String methodName = "LoadStoreKeyCredentials";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        try {

            final CryptoUtil cryptoUtil = new CryptoUtil();

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

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting after loading credential info");
            }

        } catch (Exception ex) {
            _log.errorTrace(methodName, ex);
            throw new BTSLBaseException(ex);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting");
            }

        }
    }

    public static void main(String args[]) {
        final String methodName = "main";
        try {
            if (args.length != 2) {
                _log.info(methodName, "Usage : [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                _log.info(methodName, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                _log.info(methodName, " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

            final AESEncryptionUtil bex = new AESEncryptionUtil();
            final AESKeyStore test = new AESKeyStore();
            test.createAndStoreKeyStore();

            AESKeyStore.getKey();
            // The passwords can be loaded through properties file
            // The properties file should only be readable to root & tomcat user
            // rest of the users
            // should not be able to read it.
            _log.info(methodName, "Step-1: Encrypting Data: India pakistan India pakistan India pakistanIndia pakistanIndia pakistan");
            final String encryptedText = bex.EncryptAES("1357");
            _log.debug(methodName, "Encrypted Text::" + encryptedText + "\n Decrypted Content: " + bex.DecryptAES(encryptedText));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }
    }
}