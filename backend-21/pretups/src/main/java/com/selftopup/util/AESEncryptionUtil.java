package com.selftopup.util;

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

import java.util.Base64;

public class AESEncryptionUtil {
    public String Alg = "AES";
    public AESKeyStore KS = null;
    private IvParameterSpec ivs = null; // Initialization Vector (IV)
    private byte[] ivp = new byte[16]; // the buffer with the IV
    public SecretKey key = null;

    // Generates a symmetric key using AES as algoirthm
    // consider that all check such as null, length & data type is verified
    public static Key GenerateAESKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");

            // sr.setSeed(ivp);
            kg.init(128);
            Key k = kg.generateKey();
            return k;
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
            return null;
        }
    }

    public boolean storeKey(String FileName, SecretKey key) {
        try {
            OutputStream os = new FileOutputStream(FileName);
            byte[] keyBytes = key.getEncoded();
            os.write(keyBytes);
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    public SecretKey loadKey(String FileName) {
        try {
            InputStream is = new FileInputStream(FileName);
            byte[] keyBytes = new byte[is.available()];
            is.read(keyBytes, 0, keyBytes.length);
            SecretKey key2 = new SecretKeySpec(keyBytes, "AES/OFB8/PKCS5Padding");
            return key2;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    // This method is used for printing the encrypted info or
    // digest output to be read in human readable format
    // consider that all check such as null, length & data type is verified
    public String GetEncodedData(byte[] data) {
        sun.misc.BASE64Encoder br = new sun.misc.BASE64Encoder();
        return br.encode(data);
    }

    // This method is used for printing the decrypting info or
    // digest output to be read in human readable format
    // consider that all check such as null, length & data type is verified
    public byte[] GetDecodedData(String data) throws IOException {
        BASE64Decoder bd = new BASE64Decoder();
        return bd.decodeBuffer(data);
    }

    // This method is used encrypting a message with a given AES-128 bit key
    // consider that all check such as null, length & data type is verified
    // use GetEncodedData() in conjunction finally so that you can read the
    // output
    public byte[] EncryptData(String Data, Key k) {

        try {
            Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // ivp = new byte[16];
            ivs = new IvParameterSpec(ivp);
            cip.init(Cipher.ENCRYPT_MODE, k, ivs);
            // cip.init(Cipher.ENCRYPT_MODE, k);
            byte[] data = Data.getBytes();
            byte[] encData = cip.doFinal(data);
            return encData;
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
            return null;
        } catch (InvalidKeyException ike) {
            ike.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException ibse) {
            ibse.printStackTrace();
            return null;
        } catch (NoSuchPaddingException nspe) {
            nspe.printStackTrace();
            return null;
        } catch (BadPaddingException bpe) {
            bpe.printStackTrace();
            return null;
        } catch (InvalidAlgorithmParameterException iape) {
            iape.printStackTrace();
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

        try {
            Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding");
            ivs = new IvParameterSpec(ivp);
            cip.init(Cipher.DECRYPT_MODE, k, ivs);
            byte[] encData = cip.doFinal(data);
            return encData;
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
            return null;
        } catch (InvalidKeyException ike) {
            ike.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException ibse) {
            ibse.printStackTrace();
            return null;
        } catch (NoSuchPaddingException nspe) {
            nspe.printStackTrace();
            return null;
        } catch (BadPaddingException bpe) {
            bpe.printStackTrace();
            return null;
        } catch (InvalidAlgorithmParameterException iape) {
            iape.printStackTrace();
            return null;
        }
    }

    public String EncryptAES(String p_text) {
        AESEncryptionUtil bex = new AESEncryptionUtil();
        byte[] encData = bex.EncryptData(p_text, AESKeyStore.getKey());
        return bex.GetEncodedData(encData);
    }

    public String DecryptAES(String p_text) {
        AESEncryptionUtil bex = new AESEncryptionUtil();
        try {
            byte[] decData = bex.DecryptData(bex.GetDecodedData(p_text), AESKeyStore.getKey());
            return new String(decData);
        } catch (IOException e) {
            e.printStackTrace();
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
        try {

            AESEncryptionUtil bex = new AESEncryptionUtil();
            byte[] key2 = key1.getBytes();
            SecretKey secKey1 = new SecretKeySpec(key2, "AES");
            key = secKey1;
            byte[] encData = bex.EncryptData(p_text, key);
            return bex.GetEncodedData(encData);
        } catch (Exception e) {
            e.printStackTrace();
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
        AESEncryptionUtil bex = new AESEncryptionUtil();
        try {

            byte[] key2 = key1.getBytes();
            SecretKey secKey1 = new SecretKeySpec(key2, "AES");
            key = secKey1;

            byte[] decData = bex.DecryptData(bex.GetDecodedData(p_text), key);
            return new String(decData);
        } catch (IOException e) {
            e.printStackTrace();
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

        try {
            SecretKey secKey = null;
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            secKey = kgen.generateKey();
            String key1 = DatatypeConverter.printHexBinary(secKey.getEncoded());
            String key3 = key1.substring(0, key1.length() / 2);
            return key3;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String args[]) {

        AESEncryptionUtil bex = new AESEncryptionUtil();
        // AESKeyStore test= new AESKeyStore();
        // test.createAndStoreKeyStore();

        AESKeyStore.getKey();
        // The passwords can be loaded through properties file
        // The properties file should only be readable to root & tomcat user
        // rest of the users
        // should not be able to read it.
        System.out.println(new AESEncryptionUtil().genrateAESKey());
        String encryptedText = bex.EncryptAES("1357");

    }
}