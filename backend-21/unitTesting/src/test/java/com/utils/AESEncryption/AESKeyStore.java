package com.utils.AESEncryption;

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
import java.util.Properties;

import javax.crypto.SecretKey;

import com.utils.Log;

public class AESKeyStore {
    private static KeyStore keyStr = null;
    private static SecretKey secKey = null;
    private static String KeyPass = null;
    private static String SecretKeyName = null;
    private static String StorePass = null;
    private static String KeyStoreName = null;

    public boolean LoadStoreKeyCredentials(String CredentialsFile) {
        FileInputStream fileInputStream = null;
        final String methodName = "LoadStoreKeyCredentials";
        
        try {
            final Properties pt = new Properties();
           
            fileInputStream = new FileInputStream(CredentialsFile);
            pt.load(fileInputStream);

            StorePass = pt.getProperty("StorePass");
            KeyPass = pt.getProperty("KeyPass");
            SecretKeyName = pt.getProperty("SecretKeyName");
            KeyStoreName = pt.getProperty("KeyStoreName");
            
            return true;
        } catch (FileNotFoundException fnfe) {
            Log.info(methodName+":"+ fnfe);
            return false;
        } catch (IOException ioe) {
            Log.info(methodName+":"+ ioe);
            return false;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    Log.info(methodName+":"+ e);
                }
            }
        }
    }

    public boolean LoadStoreKeyCredentials(String CredentialsFile,String keyStoreFile) {
        FileInputStream fileInputStream = null;
        final String methodName = "LoadStoreKeyCredentials";
       
        try {
            final Properties pt = new Properties();
            
            fileInputStream = new FileInputStream(CredentialsFile);
            pt.load(fileInputStream);

            StorePass = pt.getProperty("StorePass");
            KeyPass = pt.getProperty("KeyPass");
            SecretKeyName = pt.getProperty("SecretKeyName");
            KeyStoreName = keyStoreFile;
            
            return true;
        } catch (FileNotFoundException fnfe) {
            Log.info(methodName+":"+ fnfe);
            return false;
        } catch (IOException ioe) {
            Log.info(methodName+":"+ ioe);
            return false;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    Log.info(methodName+":"+ e);
                }
            }
        }
    }
    
    // Use this method only once, and follow the password
    // management standard for passwords
    public boolean CreateKeyStore(String AbsoluteKeyStoreFileName, String KeyStorePassword) {
        FileOutputStream fileOutputStream = null;
        final String StorePass = KeyStorePassword;
        final String methodName = "CreateKeyStore";
        
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
            
            return true;
        } catch (KeyStoreException kse) {
            Log.info(methodName+":"+ kse);
            return false;
        } catch (FileNotFoundException fnfe) {
            Log.info(methodName+":"+ fnfe);
            return false;
        } catch (IOException ioe) {
            Log.info(methodName+":"+ ioe);
            return false;
        } catch (NoSuchAlgorithmException nsae) {
            Log.info(methodName+":"+ nsae);
            return false;
        } catch (CertificateException ce) {
            Log.info(methodName+":"+ ce);
            return false;
        } finally {

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.info(methodName+":"+ e);
                }
            }

        }
    }

    public static KeyStore LoadKeyStore(String KeyStoreFileName, String KeyStorePassword) {
        FileInputStream fileInputStream = null;
        final String methodName = "LoadKeyStore";
    
        final String StorePass = KeyStorePassword;

        try {
            final char[] storePass = new char[KeyStorePassword.length()];
            StorePass.getChars(0, storePass.length, storePass, 0);
            keyStr = KeyStore.getInstance("jceks");
            fileInputStream = new FileInputStream(KeyStoreFileName);
            keyStr.load(fileInputStream, storePass);
            
            return keyStr;
        } catch (KeyStoreException kse) {
            Log.info(methodName+":"+ kse);
            return null;
        } catch (FileNotFoundException fnfe) {
            Log.info(methodName+":"+ fnfe);
            ;
            return null;
        } catch (IOException ioe) {
            Log.info(methodName+":"+ ioe);
            return null;
        } catch (NoSuchAlgorithmException nsae) {
            Log.info(methodName+":"+ nsae);
            return null;
        } catch (CertificateException ce) {
            Log.info(methodName+":"+ ce);
            return null;
        } finally {

            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    Log.info(methodName+":"+ e);
                }
            }
           
        }
    }

    // save my secret key

    public boolean StoreSymmetricKey(Key symKey, String KeyName, String KeyPassword, String KeyStoreFileName, String StorePassword) {
        FileOutputStream fileOutputStream = null;
        final String methodName = "StoreSymmetricKey";

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
            Log.info(methodName+":"+ kse);
            return false;
        } catch (FileNotFoundException fnfe) {
            Log.info(methodName+":"+ fnfe);
            return false;
        } catch (IOException ioe) {
            Log.info(methodName+":"+ ioe);
            return false;
        } catch (NoSuchAlgorithmException nsae) {
            Log.info(methodName+":"+ nsae);
            return false;
        } catch (CertificateException ce) {
            Log.info(methodName+":"+ ce);
            return false;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.info(methodName+":"+ e);
                }
            }
        }
    }

    public static SecretKey LoadSecretKey(String KeyName, String KeyPassword, String KeyStoreFileName, String StorePassword) {

        final String methodName = "LoadSecretKey";

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
            Log.info(methodName+":"+ kse);
            return null;
        } catch (NoSuchAlgorithmException nsae) {
            Log.info(methodName+":"+ nsae);
            return null;
        } catch (UnrecoverableEntryException unrEe) {
            Log.info(methodName+":"+ unrEe);
            return null;
        } finally {

        }
    }

    public static Key getKey() {
        return getKey("FILE");
    }

    public static Key getKey(String p_keyStoreLoc) {
        final String methodName = "getKey";

        if ("FILE".equalsIgnoreCase(p_keyStoreLoc)) {
            if (secKey == null) {
                String filePath = ".\\src\\test\\java\\com\\utils\\AESEncryption\\";
                filePath = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
                new AESKeyStore().LoadStoreKeyCredentials(filePath + File.separatorChar + "Credentials.txt",filePath + File.separatorChar + "pretups_keystore.jceks");
                secKey = LoadSecretKey(SecretKeyName, KeyPass, KeyStoreName, StorePass);
            }
        
        }

        return secKey;
    }
}