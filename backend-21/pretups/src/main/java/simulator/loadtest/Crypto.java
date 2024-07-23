
package simulator.loadtest;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class Crypto
{

	// Algo is Triple DES with 2 keys in Edncrypt-Decrypt-Encrypt Mode
	private static final String Algorithm = "DESede";
	private static final String CipherParameters = "DESede/CBC/NoPadding";
	private static final int DESedeKeyLength = 24;
	// Intialization vector : All zeros
	private static final byte[] iv = new byte[] {0x00, 0x00, 0x00, 0x00,0x00, 0x00, 0x00, 0x00};



//Utility Functions 

/**
  * converts a binhex string back into a byte array (invalid codes will be skipped)
  * @param sBinHex binhex string
  * @param data the target array
  * @param nSrcPos from which character in the string the conversion should begin,
  *                remember that (nSrcPos modulo 2) should equals 0 normally
  * @param nNumOfBytes number of bytes to extract
  * @return number of extracted bytes
  */
private int binHexToBytes(String sBinHex,
                                byte[] data,
                                int nSrcPos,
                                int nNumOfBytes) 
{
	// Dest pos set to zero.
	int nDstPos = 0;

  // check for correct ranges   
  int nStrLen = sBinHex.length();

  int nAvailBytes = (nStrLen - nSrcPos) >> 1;
  if (nAvailBytes < nNumOfBytes)
  {
    nNumOfBytes = nAvailBytes;
  }

  int nOutputCapacity = data.length; 
  if (nNumOfBytes > nOutputCapacity)
  {
    nNumOfBytes = nOutputCapacity;
  }

  // convert now
  int nResult = 0; 
  for (int nI = 0; nI < nNumOfBytes; nI++) 
  {
    byte bActByte = 0;  
    boolean blConvertOK = true;
    for (int nJ = 0; nJ < 2; nJ++) 
    {
      bActByte <<= 4;  
      char cActChar = sBinHex.charAt(nSrcPos++);

      if ((cActChar >= 'a') && (cActChar <= 'f'))
      { 
        bActByte |= (byte)(cActChar - 'a') + 10;
      }
		else if((cActChar >= 'A') && (cActChar <= 'F'))
		{
		  bActByte |= (byte)(cActChar - 'A') + 10;
		}
      else 
      {
        if ((cActChar >= '0') && (cActChar <= '9'))
        {
          bActByte |= (byte)(cActChar - '0');
        }
        else
        {
          blConvertOK = false; 
        }
      }
    }     
    if (blConvertOK) 
    {
      data[nDstPos++] = bActByte;
      nResult++;
    }
  }

  return nResult;
}
private String bytesToBinHex(byte[] data) 
{
  // just map the call
  return bytesToBinHex(data, 0, data.length); 
}
// our table for binhex conversion
final static char[] HEXTAB = { '0', '1', '2', '3', '4', '5', '6', '7',
                               '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

/**
  * converts a byte array to a binhex string
  * @param data the byte array
  * @param nStartPos start index where to get the bytes
  * @param nNumOfBytes number of bytes to convert
  * @return the binhex string
  */
private String bytesToBinHex(byte[] data,
                             int nStartPos,
                             int nNumOfBytes) 
{
  StringBuffer sbuf = new StringBuffer();
  sbuf.setLength(nNumOfBytes << 1);

  int nPos = 0;
  for (int nI = 0; nI < nNumOfBytes; nI++) 
  {
    sbuf.setCharAt(nPos++, HEXTAB[(data[nI + nStartPos] >> 4) & 0x0f]);
    sbuf.setCharAt(nPos++, HEXTAB[data[nI + nStartPos] & 0x0f]);
  }    
  return sbuf.toString();
}
	public String decrypt(String cipherText,String myKeyString) throws Exception348, GeneralSecurityException
	{
		String keyString = myKeyString;
		byte[] cipherBytes = new byte[cipherText.length()/2];  
		byte[] keyBytes = new byte[DESedeKeyLength];
		byte[] plainText = new byte[cipherBytes.length];
		// if keyString is 16 bytes make it 24
		if((keyString.length()/2 != 16) && (keyString.length()/2 != 24) )
			throw new Exception348("Wrong key Size (" + keyString.length() + "): " 
									+ "Should be a 32 or 48 characters string" ); 
		else if(keyString.length()/2 == 16)
		{
			keyString= keyString.concat(keyString.substring(0,16));	
		}

		if( binHexToBytes(cipherText,cipherBytes,0,cipherText.length()/2) != (cipherText.length()/2))
			throw new Exception348("Error while converting cipherText to byte[] ");  	
		if( binHexToBytes(keyString,keyBytes,0,DESedeKeyLength) != (DESedeKeyLength))
			throw new Exception348("Error while converting keyStrings to byte[] ");  	
		
		SecretKey desEdeKey = new SecretKeySpec(keyBytes,Algorithm);
		Cipher desEdeCipher;
		// Create the cipher 
		desEdeCipher = Cipher.getInstance(CipherParameters);
		IvParameterSpec ivSpec1 = new IvParameterSpec(iv);	
		desEdeCipher.init(Cipher.DECRYPT_MODE, desEdeKey,ivSpec1);
		plainText = desEdeCipher.doFinal(cipherBytes);
		String plainTextStr = new String(plainText,0,plainText.length);
/*		int ind = plainTextStr.indexOf(0);
		if (ind == -1)
			return plainTextStr;
		else
			return plainTextStr.substring(0,ind);

*/
		return plainTextStr;
	}

	public byte[] decryptLocal(byte[] cipherBytes,String myKeyString) throws Exception348, GeneralSecurityException
	{
		String keyString = myKeyString;
//		byte[] cipherBytes = new byte[cipherText.length()/2];  
		byte[] keyBytes = new byte[DESedeKeyLength];
		byte[] plainText = new byte[cipherBytes.length];
		// if keyString is 16 bytes make it 24
		if((keyString.length()/2 != 16) && (keyString.length()/2 != 24) )
			throw new Exception348("Wrong key Size (" + keyString.length() + "): " 
									+ "Should be a 32 or 48 characters string" ); 
		else if(keyString.length()/2 == 16)
		{
			keyString= keyString.concat(keyString.substring(0,16));	
		}

//		if( binHexToBytes(cipherText,cipherBytes,0,cipherText.length()/2) != (cipherText.length()/2))
//			throw new Exception348("Error while converting cipherText to byte[] ");  	
		if( binHexToBytes(keyString,keyBytes,0,DESedeKeyLength) != (DESedeKeyLength))
			throw new Exception348("Error while converting keyStrings to byte[] ");  	
		
		SecretKey desEdeKey = new SecretKeySpec(keyBytes,Algorithm);
		Cipher desEdeCipher;
		// Create the cipher 
		desEdeCipher = Cipher.getInstance(CipherParameters);
		IvParameterSpec ivSpec1 = new IvParameterSpec(iv);	
		desEdeCipher.init(Cipher.DECRYPT_MODE, desEdeKey,ivSpec1);
		plainText = desEdeCipher.doFinal(cipherBytes);
//		String plainTextStr = new String(plainText,0,plainText.length);
/*		int ind = plainTextStr.indexOf(0);
		if (ind == -1)
			return plainTextStr;
		else
			return plainTextStr.substring(0,ind);

*/
		return plainText;
	}

	public byte[] decryptECB(byte[] cipherBytes,String myKeyString) throws Exception348, GeneralSecurityException
	{
		String keyString = myKeyString;
//		byte[] cipherBytes = new byte[cipherText.length()/2];  
		byte[] keyBytes = new byte[DESedeKeyLength];
		byte[] plainText = new byte[cipherBytes.length];
		// if keyString is 16 bytes make it 24
		if((keyString.length()/2 != 16) && (keyString.length()/2 != 24) )
			throw new Exception348("Wrong key Size (" + keyString.length() + "): " 
									+ "Should be a 32 or 48 characters string" ); 
		else if(keyString.length()/2 == 16)
		{
			keyString= keyString.concat(keyString.substring(0,16));	
		}

//		if( binHexToBytes(cipherText,cipherBytes,0,cipherText.length()/2) != (cipherText.length()/2))
//			throw new Exception348("Error while converting cipherText to byte[] ");  	
		if( binHexToBytes(keyString,keyBytes,0,DESedeKeyLength) != (DESedeKeyLength))
			throw new Exception348("Error while converting keyStrings to byte[] ");  	
		
		SecretKey desEdeKey = new SecretKeySpec(keyBytes,Algorithm);
		Cipher desEdeCipher;
		// Create the cipher 
		desEdeCipher = Cipher.getInstance("DESede/ECB/NoPadding");
//		IvParameterSpec ivSpec1 = new IvParameterSpec(iv);	
		desEdeCipher.init(Cipher.DECRYPT_MODE, desEdeKey);
		plainText = desEdeCipher.doFinal(cipherBytes);
//		String plainTextStr = new String(plainText,0,plainText.length);
/*		int ind = plainTextStr.indexOf(0);
		if (ind == -1)
			return plainTextStr;
		else
			return plainTextStr.substring(0,ind);

*/
//		return plainTextStr.toString();
		return plainText;
	}
	
	
	
	public String encrypt(String plainText,String myKeyString) throws Exception348, GeneralSecurityException
	{
		String keyString = myKeyString;
		
		byte[] plainTextBytes = new byte[plainText.length()];  
		byte[] keyBytes = new byte[DESedeKeyLength];
		byte[] cipherText = new byte[plainTextBytes.length];
		// if keyString is 16 bytes make it 24
		if((keyString.length()/2 != 16) && (keyString.length()/2 != 24) )
			throw new Exception348("Wrong key Size (" + keyString.length() + "): " 
									+ "Should be a 32 or 48 characters string" ); 

		else if(keyString.length()/2 == 16)
		{
			keyString= keyString.concat(keyString.substring(0,16));	
		}

		plainTextBytes = plainText.getBytes();

		if( binHexToBytes(keyString,keyBytes,0,DESedeKeyLength) != (DESedeKeyLength))
			throw new Exception348("Error while converting keyStrings to byte[] ");  	
		
		SecretKey desEdeKey = new SecretKeySpec(keyBytes,Algorithm);
		Cipher desEdeCipher;
		// Create the cipher 
		desEdeCipher = Cipher.getInstance(CipherParameters);
		IvParameterSpec ivSpec1 = new IvParameterSpec(iv);	
		desEdeCipher.init(Cipher.ENCRYPT_MODE, desEdeKey,ivSpec1);
		cipherText = desEdeCipher.doFinal(plainTextBytes);

		return bytesToBinHex(cipherText);
		
	}

	public String encryptLocal(byte[] plainTextBytes,String myKeyString) throws Exception348, GeneralSecurityException
	{
		String keyString = myKeyString;
		
//		byte[] plainTextBytes = new byte[plainText.length()];  
		byte[] keyBytes = new byte[DESedeKeyLength];
		byte[] cipherText = new byte[plainTextBytes.length];
		// if keyString is 16 bytes make it 24
		if((keyString.length()/2 != 16) && (keyString.length()/2 != 24) )
			throw new Exception348("Wrong key Size (" + keyString.length() + "): " 
									+ "Should be a 32 or 48 characters string" ); 

		else if(keyString.length()/2 == 16)
		{
			keyString= keyString.concat(keyString.substring(0,16));	
		}

		//plainTextBytes = plainText.getBytes();

		if( binHexToBytes(keyString,keyBytes,0,DESedeKeyLength) != (DESedeKeyLength))
			throw new Exception348("Error while converting keyStrings to byte[] ");  	
		
		SecretKey desEdeKey = new SecretKeySpec(keyBytes,Algorithm);
		Cipher desEdeCipher;
		// Create the cipher 
		desEdeCipher = Cipher.getInstance(CipherParameters);
		IvParameterSpec ivSpec1 = new IvParameterSpec(iv);	
		desEdeCipher.init(Cipher.ENCRYPT_MODE, desEdeKey,ivSpec1);
		cipherText = desEdeCipher.doFinal(plainTextBytes);

		return bytesToBinHex(cipherText);
					
	}

	public String encryptECB(byte[] plainTextBytes,String myKeyString) throws Exception348, GeneralSecurityException
	{
		String keyString = myKeyString;
		
//		byte[] plainTextBytes = new byte[plainText.length()];  
		byte[] keyBytes = new byte[DESedeKeyLength];
		byte[] cipherText = new byte[plainTextBytes.length];
		// if keyString is 16 bytes make it 24
		if((keyString.length()/2 != 16) && (keyString.length()/2 != 24) )
			throw new Exception348("Wrong key Size (" + keyString.length() + "): " 
									+ "Should be a 32 or 48 characters string" ); 

		else if(keyString.length()/2 == 16)
		{
			keyString= keyString.concat(keyString.substring(0,16));	
		}

		//plainTextBytes = plainText.getBytes();

		if( binHexToBytes(keyString,keyBytes,0,DESedeKeyLength) != (DESedeKeyLength))
			throw new Exception348("Error while converting keyStrings to byte[] ");  	
		
		SecretKey desEdeKey = new SecretKeySpec(keyBytes,Algorithm);
		Cipher desEdeCipher;
		// Create the cipher 
		desEdeCipher = Cipher.getInstance("DESede/ECB/NoPadding");
//		IvParameterSpec ivSpec1 = new IvParameterSpec(iv);	
		desEdeCipher.init(Cipher.ENCRYPT_MODE, desEdeKey);
		cipherText = desEdeCipher.doFinal(plainTextBytes);

		return bytesToBinHex(cipherText);
					
	}
	
	/*
	public static void main(String args[])throws Exception
	{
		String keyString = "8292D698A291872A0654040531DFDCE7";
		keyString = "11111111111111111111111111111111";
		Crypto crypto = new Crypto();
		// Extract data from 03.48 mesage after CNTR/PCNTR
		String toEncrypt= "3247299941f577636e9f16b793c35ca6a19e2ad62a79763b";
		
		toEncrypt="CAB37C2FA92395B2F0FF71365D2F4BB5EA5B180CE5061E2FF9C0AE4A51A0D706";
		
		// The 4 lines below are used to decypt data
		byte[] cipherBytes = new byte[toEncrypt.length()/2];
		crypto.binHexToBytes(toEncrypt,cipherBytes,0,toEncrypt.length()/2);
		byte[] plainBytes = crypto.decryptLocal(cipherBytes,keyString);
		System.out.println("Decored String " + crypto.bytesToBinHex(plainBytes));
		System.out.println("Plain Text " + HexToAscii.HexToAscii(crypto.bytesToBinHex(plainBytes)));
		
		
		
		// For encryption
		String keyWord="DSR 1357 FôqL9D";
		Crypto crp=new Crypto();
		System.out.println("Encrypted SMS="+crp.encrypt(keyWord,keyString));
		
//		String plaintextBytes = crypto.decryptLocal(cipherBytes,keyString.toLowerCase());


		// UnComment the 4 lines below to encrypt
//		byte[] toEncryptBytes = new byte[toEncrypt.length()/2];
//		crypto.binHexToBytes(toEncrypt,toEncryptBytes,0,toEncrypt.length()/2);
//		String cipherText = crypto.encryptLocal(toEncryptBytes,keyString);
//		System.out.println("Encryted Locally: " + cipherText);
//		System.out.println("Plain Text " + crypto.decryptECB(cipherBytes,keyString));
//		System.out.println("PlainText: " + crypto.decrypt(text.toLowerCase(),keyString.toLowerCase()));
/*		String plainText = "This is just an example";
		System.out.println("CipherText: " + crypto.encrypt(plainText,keyString));
		byte[] keyBytes = new byte[]{0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A, 0x55, 0x56,0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A, 0x55, 0x56,0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A, 0x55, 0x56};
		SecretKey desKey = new SecretKeySpec(keyBytes,"DESede");
		Cipher desCipher;
		// Create the cipher 
		desCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		// Initialize the cipher for encryption
		byte[] iv = new byte[] {0x00, 0x00, 0x00, 0x00,0x00, 0x00, 0x00, 0x00};
		IvParameterSpec ivSpec1 = new IvParameterSpec(iv);
		desCipher.init(Cipher.ENCRYPT_MODE, desKey,ivSpec1);
		byte[] iv2 = desCipher.getIV();
		// Our cleartext
		byte[] cleartext = "This is just an example".getBytes();
		// Encrypt the cleartext
		byte[] ciphertext = desCipher.doFinal(cleartext);
		System.out.println(bytesToBinHex(ciphertext));
		// Initialize the same cipher for decryption
//		IvParameterSpec ivSpec2 = new IvParameterSpec(iv2);
		desCipher.init(Cipher.DECRYPT_MODE, desKey,ivSpec1);
		// Decrypt the ciphertext
		byte[] cleartext1 = desCipher.doFinal(ciphertext);
		System.out.println(cleartext1[0]);

	}*/
	
	public static void main(String args[])throws Exception
	{
		//String keyString = "202122232425262728292A2B2C2D2E2F";
		//String keyString = "F469130F87E9E4E21A0E23539AC27DFC";
		String keyString = "11111111111111111111111111111111";
		//keyString = "EFA8C39785547D51AAC707C98D8D521B";
		SimProfileVO simProfile = new  SimProfileVO();
		simProfile.setEncryptALGO("DESede");
		simProfile.setEncryptMode("CBC");
		simProfile.setEncryptPad("NoPadding");
		Crypto crypto = new Crypto();
		
		// Code for Encrypting
		//byte[] test = "CSMS BAL".getBytes();
		byte[] test = "DSR 1357".getBytes();
		//byte[] test = new byte[16];
		//int bytes = crypto.binHexToBytes("50000b53090101020304050607080000",test,0,16);
		//System.out.println("CipherText: bytes + " + bytes + " "+ crypto.encrypt348Data(test,keyString.toLowerCase(),simProfile));
		System.out.println("Encrypted Text >>"+  crypto.encrypt348Data(test,keyString.toLowerCase(),simProfile));
	
		//Code for Decrypting 
		String ciphertext ="00D49DCE484B8BD98D77144B5384C4159A36BCD2249BD7F93C6006664ACCCCCF71CABD7C9F1F291DBB1F3353802557C8FFA376FA3057625BC1C8EAF174C6801A";
		//Commented By Amit
		//Got the whole message from acces.log stripp off after the 0100
		//and input
		//EG ->004E0D00001010303032000000000100199E0A7F3AFDE81ED75E1394EA2C206183C3AC89CFD3982C9E267C5DA574C2FB03DF0762AD4BE0EF3DB46B2DA6D930F5F9978F571831B9D5DB1CACF7527B04D5
		//RC
		ciphertext="199E0A7F3AFDE81ED75E1394EA2C206183C3AC89CFD3982C9E267C5DA574C2FB03DF0762AD4BE0EF3DB46B2DA6D930F5F9978F571831B9D5DB1CACF7527B04D5";
		
		//ST 
		//ciphertext="420323232322046070004F41100714C3902450C0020203938313031393038333120323220417261626963205458313131313139313536200";
		
		//ciphertext="4655473968A745FF09C3B55D4857D454D7790196DF71D64100B26C8ABBBEBF97B3C71FE6FE7B63D1C0795932E75B285B036091E6DA0CE7BAED190584B2797A8A";
		
		//ciphertext="AB73EF90FF52CD1D";
		
		
		
		System.out.println("PlainText: " + crypto.decrypt348Data(ciphertext.toLowerCase(),keyString.toLowerCase(),simProfile));



/*		String plainText = "This is just an example";
		System.out.println("CipherText: " + crypto.encrypt(plainText,keyString));

		byte[] keyBytes = new byte[]{0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A, 0x55, 0x56,0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A, 0x55, 0x56,0x01, 0x72, 0x43, 0x3E, 0x1C, 0x7A, 0x55, 0x56};
		
		SecretKey desKey = new SecretKeySpec(keyBytes,"DESede");
		Cipher desCipher;
		// Create the cipher 
		desCipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		// Initialize the cipher for encryption
		byte[] iv = new byte[] {0x00, 0x00, 0x00, 0x00,0x00, 0x00, 0x00, 0x00};
		IvParameterSpec ivSpec1 = new IvParameterSpec(iv);
		desCipher.init(Cipher.ENCRYPT_MODE, desKey,ivSpec1);
		byte[] iv2 = desCipher.getIV();
	
		// Our cleartext
		byte[] cleartext = "This is just an example".getBytes();
		// Encrypt the cleartext
		byte[] ciphertext = desCipher.doFinal(cleartext);
		System.out.println(bytesToBinHex(ciphertext));
		// Initialize the same cipher for decryption
//		IvParameterSpec ivSpec2 = new IvParameterSpec(iv2);
		desCipher.init(Cipher.DECRYPT_MODE, desKey,ivSpec1);
		// Decrypt the ciphertext
		byte[] cleartext1 = desCipher.doFinal(ciphertext);
		System.out.println(cleartext1[0]);
*/
	
	}

//	This is used in OTA Message class and uses SimProfile 
	  public  String encrypt348Data(byte[] plainTextBytes,String myKeyString, SimProfileVO simProfile) throws Exception348, GeneralSecurityException
	  {
		  String keyString = myKeyString;
		  byte[] keyBytes = new byte[DESedeKeyLength];
		  byte[] cipherText = new byte[plainTextBytes.length];
		  // if keyString is 16 bytes make it 24
		  if((keyString.length()/2 != 16) && (keyString.length()/2 != 24) )
			  throw new Exception348("Wrong key Size (" + keyString.length() + "): " 
									  + "Should be a 32 or 48 characters string" ); 

		  else if(keyString.length()/2 == 16)
		  {
			  keyString= keyString.concat(keyString.substring(0,16));	
		  }

		  //plainTextBytes = plainText.getBytes();

		  if( binHexToBytes(keyString,keyBytes,0,DESedeKeyLength) != (DESedeKeyLength))
			  throw new Exception348("Error while converting keyStrings to byte[] ");  	
		
		String algo = null;
		String mode = null;
		String padding = null;
		String cipherParams= null;

		if(simProfile == null)
		{
			System.out.println("Null SimProfile. Using default parameters: Algorithm-DESede, Mode-CBC and Padding-NoPadding");
			algo = Algorithm;
			cipherParams = CipherParameters;
		} 
		else
		{
			algo = simProfile.getEncryptALGO();
			mode = simProfile.getEncryptMode();
			padding = simProfile.getEncryptPad();
			if(algo == null || mode == null || padding == null)
			{
				algo = Algorithm;
				cipherParams = CipherParameters;
				System.out.println("Unable to get Ciphering parameters form SimProfile.");
				System.out.println("Using default parameters: Algorithm-DESede, Mode-CBC and Padding-NoPadding");	
			}
			else
				cipherParams = algo + "/" + mode + "/" + padding; 
			
		}
		
		  SecretKey desEdeKey = new SecretKeySpec(keyBytes,algo);
		  Cipher desEdeCipher;
		  // Create the cipher 
		  desEdeCipher = Cipher.getInstance(cipherParams);
		  IvParameterSpec ivSpec1 = new IvParameterSpec(iv);	
		  desEdeCipher.init(Cipher.ENCRYPT_MODE, desEdeKey,ivSpec1);
		  cipherText = desEdeCipher.doFinal(plainTextBytes);

		  return bytesToBinHex(cipherText);
	  }

	  public String decrypt348Data(String cipherText,String myKeyString, SimProfileVO simProfile) throws Exception348, GeneralSecurityException
		{
			String keyString = myKeyString;
			byte[] cipherBytes = new byte[cipherText.length()/2];  
			byte[] keyBytes = new byte[DESedeKeyLength];
			byte[] plainText = new byte[cipherBytes.length];
			// if keyString is 16 bytes make it 24
			if((keyString.length()/2 != 16) && (keyString.length()/2 != 24) )
				throw new Exception348("Wrong key Size (" + keyString.length() + "): " 
										+ "Should be a 32 or 48 characters string" ); 
			else if(keyString.length()/2 == 16)
			{
				keyString= keyString.concat(keyString.substring(0,16));	
			}

			if( binHexToBytes(cipherText,cipherBytes,0,cipherText.length()/2) != (cipherText.length()/2))
				throw new Exception348("Error while converting cipherText to byte[] ");  	
			if( binHexToBytes(keyString,keyBytes,0,DESedeKeyLength) != (DESedeKeyLength))
				throw new Exception348("Error while converting keyStrings to byte[] ");  	
			
			String algo = null;
			String mode = null;
			String padding = null;
			String cipherParams= null;
			
			if(simProfile == null)
			{
				System.out.println("Null SimProfile. Using default parameters: Algorithm-DESede, Mode-CBC and Padding-NoPadding");
				algo = Algorithm;
				cipherParams = CipherParameters;
			} 
			else
			{
				algo = simProfile.getEncryptALGO();
				mode = simProfile.getEncryptMode();
				padding = simProfile.getEncryptPad();
				if(algo == null || mode == null || padding == null)
				{
					algo = Algorithm;
					cipherParams = CipherParameters;
					System.out.println("Unable to get Ciphering parameters form SimProfile.");
					System.out.println("Using default parameters: Algorithm-DESede, Mode-CBC and Padding-NoPadding");	
				}
				else
					cipherParams = algo + "/" + mode + "/" + padding; 
			}
					
			SecretKey desEdeKey = new SecretKeySpec(keyBytes,algo);
			Cipher desEdeCipher;
			// Create the cipher 
			desEdeCipher = Cipher.getInstance(cipherParams);
			IvParameterSpec ivSpec1 = new IvParameterSpec(iv);	
			desEdeCipher.init(Cipher.DECRYPT_MODE, desEdeKey,ivSpec1);
			plainText = desEdeCipher.doFinal(cipherBytes);
			String plainTextStr = new String(plainText,0,plainText.length);
			return plainTextStr;
		}
}
