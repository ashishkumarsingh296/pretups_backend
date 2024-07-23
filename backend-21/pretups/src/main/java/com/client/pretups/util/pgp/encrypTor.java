package com.client.pretups.util.pgp;

import java.io.FileInputStream;

import java.io.FileOutputStream;

public class encrypTor {

	//private String passphrase="123456789";
	
	//private static String  keyFile="C:/Users/zeeshan.aleem/Desktop/Safaricom/PGP client/PGP client/publickey.txt";

//	private static String inputFile="C:/Users/zeeshan.aleem/Desktop/Safaricom/PGP client/PGP client/yogesh.xlsx";

//private static String outputFile="C:/Users/zeeshan.aleem/Desktop/Safaricom/PGP client/PGP client/zeeshan.pgp";

	private static boolean asciiArmored = false;

	private static boolean integrityCheck = true;

	public static void pgpEncrypt (String keyFile,String outputFile,String inputFile,String passphrase) throws Exception {
		FileInputStream keyIn = null;
		FileOutputStream out = null;
		try{
			keyIn = new FileInputStream(keyFile);
	
			out = new FileOutputStream(outputFile);
	
			PGPUtils.encryptFile(out, inputFile,PGPUtils.readPublicKey(keyIn),asciiArmored, integrityCheck);
		} catch(Exception e){
			throw e;
		}
		finally{
			if(out !=null)out.close();
			if(keyIn != null)keyIn.close();
		}
	}

	/*public static void main (String[] args) throws Exception{

		FileInputStream in = new FileInputStream(inputFile);

		FileInputStream keyIn = new FileInputStream(keyFile);

		FileOutputStream out = new FileOutputStream(outputFile);

		PGPUtil.decryptFile(in, out, keyIn, passphrase.toCharArray());

		in.close();

		out.close();

		keyIn.close();

		return true;

	}*/

	public boolean isAsciiArmored() {

		return asciiArmored;

	}

	public void setAsciiArmored(boolean asciiArmored) {

		this.asciiArmored = asciiArmored;

	}

	public boolean isIntegrityCheck() {

		return integrityCheck;

	}

	public void setIntegrityCheck(boolean integrityCheck) {

		this.integrityCheck = integrityCheck;

	}
	/*
	public String getPassphrase() {

		return passphrase;

	}

	public void setPassphrase(String passphrase) {

		this.passphrase = passphrase;

	}

	public String getKeyFile() {

		return keyFile;

	}

	public void setKeyFile(String keyFile) {

		this.keyFile = keyFile;

	}

	public String getInputFile() {

		return inputFile;

	}

	public void setInputFile(String inputFile) {

		this.inputFile = inputFile;

	}

	public String getOutputFile() {

		return outputFile;

	}

	public void setOutputFile(String outputFile) {

		this.outputFile = outputFile;

	}
*/
}
