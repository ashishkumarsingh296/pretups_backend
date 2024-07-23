package com.utils.AESEncryption;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.utils.Log;

import sun.misc.BASE64Decoder;

public class AESEncryptionUtil {
    public String Alg = "AES";
    public AESKeyStore KS = null;
    private IvParameterSpec ivs = null; // Initialization Vector (IV)
    private byte[] ivp = new byte[16]; // the buffer with the IV
    public SecretKey key = null;
    
    // Generates a symmetric key using AES as algoirthm
    // consider that all check such as null, length & data type is verified
    public static Key GenerateAESKey() {
        final String methodName = "GenerateAESKey";
        try {
            final KeyGenerator kg = KeyGenerator.getInstance("AES");

            // sr.setSeed(ivp);
            kg.init(128);
            final Key k = kg.generateKey();
            return k;
        } catch (NoSuchAlgorithmException nsae) {
            Log.info(methodName+":"+ nsae);
            return null;
        }
    }

    public boolean storeKey(String FileName, SecretKey key) {
        final String methodName = "storeKey";
        OutputStream os = null;
        try {
            os = new FileOutputStream(FileName);
            final byte[] keyBytes = key.getEncoded();
            os.write(keyBytes);
            return true;
        } catch (IOException ioe) {
        	Log.info(methodName+":"+ ioe);
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {

                	Log.info(methodName+":"+ e);
                }
            }
        }
    }

    public SecretKey loadKey(String FileName) {
        final String methodName = "loadKey";
        InputStream is = null;
        try {
            is = new FileInputStream(FileName);
            final byte[] keyBytes = new byte[is.available()];
            is.read(keyBytes, 0, keyBytes.length);
            final SecretKey key2 = new SecretKeySpec(keyBytes, "AES/OFB8/PKCS5Padding");
            return key2;
        } catch (FileNotFoundException fnfe) {
        	Log.info(methodName+":"+ fnfe);
            return null;
        } catch (IOException ioe) {
        	Log.info(methodName+":"+ioe);
            return null;
        } finally {

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                	Log.info(methodName+":"+ e);
                }
            }
        }
    }

    // This method is used for printing the encrypted info or
    // digest output to be read in human readable format
    // consider that all check such as null, length & data type is verified
    public String GetEncodedData(byte[] data) {
        final sun.misc.BASE64Encoder br = new sun.misc.BASE64Encoder();
        return br.encode(data);
    }

    // This method is used for printing the decrypting info or
    // digest output to be read in human readable format
    // consider that all check such as null, length & data type is verified
    public byte[] GetDecodedData(String data) throws IOException {
        final BASE64Decoder bd = new BASE64Decoder();
        return bd.decodeBuffer(data);
    }

    // This method is used encrypting a message with a given AES-128 bit key
    // consider that all check such as null, length & data type is verified
    // use GetEncodedData() in conjunction finally so that you can read the
    // output
    public byte[] EncryptData(String Data, Key k) {
        final String methodName = "EncryptData";
        try {
            final Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // ivp = new byte[16];
            ivs = new IvParameterSpec(ivp);
            cip.init(Cipher.ENCRYPT_MODE, k, ivs);
            // cip.init(Cipher.ENCRYPT_MODE, k);
            final byte[] data = Data.getBytes();
            final byte[] encData = cip.doFinal(data);
            return encData;
        } catch (NoSuchAlgorithmException nsae) {
        	Log.info(methodName+":"+ nsae);
            return null;
        } catch (InvalidKeyException ike) {
        	Log.info(methodName+":"+ ike);
            return null;
        } catch (IllegalBlockSizeException ibse) {
        	Log.info(methodName+":"+ibse);
            return null;
        } catch (NoSuchPaddingException nspe) {
        	Log.info(methodName+":"+ nspe);
            return null;
        } catch (BadPaddingException bpe) {
        	Log.info(methodName+":"+ bpe);
            return null;
        } catch (InvalidAlgorithmParameterException iape) {
        	Log.info(methodName+":"+ iape);
            return null;
        }
    }

    // This method is used decrypting a message with a given AES-128 bit key
    // consider that all check such as null, length & data type is verified
    // no need to use the getEncodedData() method as output bytes are printed
    // would be in human readable format.

    // CHANGE : Method Signature - data type of 'data' is changed to byte[] from
    // String
    // CHANGE_REASON: Due to the way String object is manipulated, decryption
    // output
    // was erratic
    public byte[] DecryptData(byte[] data, Key k) {
        final String methodName = "DecryptData";

        try {
            final Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding");
            ivs = new IvParameterSpec(ivp);
            cip.init(Cipher.DECRYPT_MODE, k, ivs);
            final byte[] encData = cip.doFinal(data);
            return encData;
        } catch (NoSuchAlgorithmException nsae) {
        	Log.info(methodName+":"+ nsae);
            return null;
        } catch (InvalidKeyException ike) {
            Log.info(methodName+":"+ ike);
            return null;
        } catch (IllegalBlockSizeException ibse) {
            Log.info(methodName+":"+ ibse);
            return null;
        } catch (NoSuchPaddingException nspe) {
            Log.info(methodName+":"+ nspe);
            return null;
        } catch (BadPaddingException bpe) {
            Log.info(methodName+":"+ bpe);
            return null;
        } catch (InvalidAlgorithmParameterException iape) {
            Log.info(methodName+":"+ iape);
            return null;
        }
    }

    public String EncryptAES(String p_text) {
        final AESEncryptionUtil bex = new AESEncryptionUtil();
        final byte[] encData = bex.EncryptData(p_text, AESKeyStore.getKey());
        return bex.GetEncodedData(encData);
    }

    public String DecryptAES(String p_text) {
        final String methodName = "DecryptAES";
        final AESEncryptionUtil bex = new AESEncryptionUtil();
        try {
            final byte[] decData = bex.DecryptData(bex.GetDecodedData(p_text), AESKeyStore.getKey());
            return new String(decData);
        } catch (IOException e) {
            Log.info(methodName+":"+ e);
            return null;
        }
    }

    /***
     * @author gaurav.pandey
     * @param p_text
     * @param key1
     * @return
     */
    public String EncryptAESNew(String p_text, String key1) {
        final String methodName = "EncryptAESNew";
        try {

            final AESEncryptionUtil bex = new AESEncryptionUtil();
            final byte[] key2 = key1.getBytes();
            final SecretKey secKey1 = new SecretKeySpec(key2, "AES");
            key = secKey1;
            final byte[] encData = bex.EncryptData(p_text, key);
            return bex.GetEncodedData(encData);
        } catch (Exception e) {
            Log.info(methodName+":"+ e);
            return "data";
        }
    }

    /***
     * @author gaurav.pandey
     * @param p_text
     * @param key1
     * @return
     */
    public String DecryptAESNew(String p_text, String key1) {
        final String methodName = "DecryptAESNew";
        final AESEncryptionUtil bex = new AESEncryptionUtil();
        try {

            final byte[] key2 = key1.getBytes();
            final SecretKey secKey1 = new SecretKeySpec(key2, "AES");
            key = secKey1;

            final byte[] decData = bex.DecryptData(bex.GetDecodedData(p_text), key);
            return new String(decData);
        } catch (IOException e) {
            Log.info(methodName+":"+ e);
            return null;
        }
    }

    /***
     * @author gaurav.pandey
     * @param p_text
     * @param key1
     * @return
     */
    public String genrateAESKey() {
        final String methodName = "genrateAESKey";
        try {
            SecretKey secKey = null;
            final KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            secKey = kgen.generateKey();
            final String key1 = DatatypeConverter.printHexBinary(secKey.getEncoded());
            final String key3 = key1.substring(0, key1.length() / 2);
            return key3;
        } catch (Exception e) {
            Log.info(methodName+":"+ e);
            return null;
        }
    }

    public static void main(String args[]) {

        final AESEncryptionUtil bex = new AESEncryptionUtil();
        // AESKeyStore test= new AESKeyStore();
        // test.createAndStoreKeyStore();

        AESKeyStore.getKey();
        // The passwords can be loaded through properties file
        // The properties file should only be readable to root & tomcat user
        // rest of the users
        // should not be able to read it.

        final String encryptedText = bex.EncryptAES("1357");
        System.out.println(encryptedText);

    }
}