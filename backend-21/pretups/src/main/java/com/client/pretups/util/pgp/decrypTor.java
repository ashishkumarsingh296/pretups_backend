package com.client.pretups.util.pgp;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class decrypTor {

	private static String passphrase="12345678";
	
	private static String  keyFile="C:/Users/mallesham.k/Desktop/privatekey.txt";
	
	private static String inputFile="C:/Users/mallesham.k/Documents/MVD160715_153120.pgp";

	private static String outputFile="C:/Users/mallesham.k/Documents/Result4.csv";

	private static boolean asciiArmored = false;

	private static boolean integrityCheck = true;

	public static void main (String[] args) throws Exception{
		String passPhrase = "12345678";
		String inputFile = "C:/Users/mallesham.k/Documents/MVD160715_153120.pgp";
		String outputFile="C:/Users/mallesham.k/Documents/Result5.csv";
		String privateKeyFile="C:/Users/mallesham.k/Desktop/privatekey.txt";
		decrypTor d = new decrypTor();
		d.decrypt(inputFile, outputFile, keyFile, passphrase);
		/*FileInputStream in = null;
		FileInputStream keyIn = null;
		FileOutputStream out = null;
		try{
			in = new FileInputStream(inputFile);
			keyIn = new FileInputStream(keyFile);
			out = new FileOutputStream(outputFile);
			PGPUtil.decryptFile(in, out, keyIn, passphrase.toCharArray());
		}catch(Exception e){
			throw e;
		}finally{
			if(in != null)in.close();
			if(out != null)out.close();
			if(keyIn != null)keyIn.close();
		}*/
	}

	public void decrypt(String inputFile, String outputFile, String privateKeyFile, String passPhrase)throws Exception{
		FileInputStream in = null;
		FileInputStream keyIn = null;
		FileOutputStream out = null;
		try{
			in = new FileInputStream(inputFile);
			keyIn = new FileInputStream(privateKeyFile);
			out = new FileOutputStream(outputFile);
			PGPUtils.decryptFile(in, out, keyIn, passPhrase.toCharArray());
		}catch(Exception e){
			throw e;
		}finally{
			if(in != null)in.close();
			if(out != null)out.close();
			if(keyIn != null)keyIn.close();
		}
	}
	
	/*public boolean isAsciiArmored() {
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
	}*/

}
