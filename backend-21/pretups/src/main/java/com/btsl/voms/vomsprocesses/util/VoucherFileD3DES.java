package com.btsl.voms.vomsprocesses.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class VoucherFileD3DES {

	private static final String KEY="E0A011B557BB08CF981AFA8CDEB2A0F7";
	private static final String Algorithm = "DESede";
	private static final String CipherParameters = "DESede/CBC/PKCS5Padding";
	private static Log log = LogFactory.getLog(VoucherFileD3DES.class.getName());
	
	private static final byte[] iv = new byte[] {0x00, 0x00, 0x00, 0x00,0x00, 0x00, 0x00, 0x00};

	/**
	 * ensures no instantiation
	 */
	private VoucherFileD3DES(){
		
	}
	public static byte[] encryptBytes(byte[] plainTextBytes) throws Exception {
		
		String keyString = KEY.concat(KEY.substring(0,16));	
		
		byte[] keyBytes = binHexToBytes(keyString);
		byte[] cipherText;
		
		SecretKey desEdeKey = new SecretKeySpec(keyBytes,Algorithm);
		Cipher desEdeCipher = Cipher.getInstance(CipherParameters);
		IvParameterSpec ivSpec1 = new IvParameterSpec(iv);	
		desEdeCipher.init(Cipher.ENCRYPT_MODE, desEdeKey,ivSpec1);
		cipherText = desEdeCipher.doFinal(plainTextBytes);

		return cipherText;
	}
	
	
	public static byte[] decryptBytes(byte[] cipherBytes) throws Exception {
		
		String keyString = KEY.concat(KEY.substring(0,16));	
		
		byte[] plainText ;
		byte[] keyBytes = binHexToBytes(keyString);
		
		SecretKey desEdeKey = new SecretKeySpec(keyBytes,Algorithm);
		Cipher desEdeCipher = Cipher.getInstance(CipherParameters);
		IvParameterSpec ivSpec1 = new IvParameterSpec(iv);	
		desEdeCipher.init(Cipher.DECRYPT_MODE, desEdeKey,ivSpec1);
		plainText = desEdeCipher.doFinal(cipherBytes);
		
		return plainText;
	
	}
	
	public static void main(String args[]){
		System.out.println("Running main method");
		InputStream in = null;
		OutputStream out = null;
		try{
			 in=new FileInputStream(new File("C:\\encryptedfiles","voucher123"));
			File f=new File("C:\\encryptedfiles","voucher123_encrypted");
			f.createNewFile();
			 out= new FileOutputStream(f);
			
			byte[] plain=new byte[in.available()];
			System.out.println("in.available() = "+ in.available());
			int readlen=0;
			while((readlen=in.read(plain))>=0){
			out.write(encryptBytes(plain));
			}
		}catch(Exception e){e.printStackTrace();}
		finally{
			try{
		        if (in!= null){
		        	in.close();
		        }
		      }
		      catch (IOException e){
		    	  log.error("An error occurred closing inputStream.", e);
		      }
			try{
		        if (out!= null){
		        	out.close();
		        }
		      }
		      catch (IOException e){
		    	  log.error("An error occurred closing outputStream.", e);
		      }
		}
		
		try{
			in=new FileInputStream(new File("C:\\encryptedfiles","voucher123_encrypted"));
			File f=new File("C:\\encryptedfiles","voucher123_encrypted_decrypted");
			f.createNewFile();
			out = new FileOutputStream(f);
			byte[] plain=new byte[in.available()];
			int readlen=0;
			while((readlen=in.read(plain))>=0){
				out.write(decryptBytes(plain));
			}
		}catch(Exception e){e.printStackTrace();}
		finally{
			try{
		        if (in!= null){
		        	in.close();
		        }
		      }
		      catch (IOException e){
		    	  log.error("An error occurred closing inputStream.", e);
		      }
			try{
		        if (out!= null){
		        	out.close();
		        }
		      }
		      catch (IOException e){
		    	  log.error("An error occurred closing outputStream.", e);
		      }
		}
	}

	public static File decryptFile(File f){

		String path= f.getPath().substring(0,f.getPath().indexOf(".encrypted"));	
		InputStream in = null;
		OutputStream out = null;
		File decryptedFile=new File(path);
		try{
				in=new FileInputStream(f);
				decryptedFile.createNewFile();
				out = new FileOutputStream(decryptedFile);
				
				byte[] buf = new byte[in.available()];
				
				while(in.read(buf)>=0){
					out.write(decryptBytes(buf));
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			try{
		        if (in!= null){
		        	in.close();
		        }
		      }
		      catch (IOException e){
		    	  log.error("An error occurred closing inputStream.", e);
		      }
			try{
		        if (out!= null){
		        	out.close();
		        }
		      }
		      catch (IOException e){
		    	  log.error("An error occurred closing outputStream.", e);
		      }
			
			decryptedFile = null;
		}
		
		return decryptedFile;
	}

	  private static byte[] binHexToBytes(String sBinHex) 
	  {
		  	int nNumOfBytes=sBinHex.length()/2;
		  	
		  	byte[] data = new byte[nNumOfBytes];
		  	int nSrcPos=0;
		  	for (int nDstPos = 0; nDstPos < nNumOfBytes; nDstPos++) 
		  	{
		  		byte bActByte = 0;  

		  		for (int nJ = 0; nJ < 2; nJ++) 
		  		{
		  			bActByte <<= 4;  
		  			char cActChar = sBinHex.charAt(nSrcPos++);

		  			if ((cActChar >= 'a') && (cActChar <= 'f'))
		  			{ 
		  				bActByte |= (cActChar - 'a') + 10;
		  			}
		  			else if ((cActChar >= 'A') && (cActChar <= 'F'))
		  			{ 
		  				bActByte |=(cActChar - 'A') + 10;
		  			}
		  			else 
		  			{
		  				if ((cActChar >= '0') && (cActChar <= '9'))
		  				{
		  					bActByte |= (cActChar - '0');
		  				}
		  				else
		  				{
		  					log.error("binHexToBytes", "Improper Hex Data");
		  				}
		  			}
		  		}     
		  		data[nDstPos] = bActByte;
		  	}

		  	return data;
	  }

}
