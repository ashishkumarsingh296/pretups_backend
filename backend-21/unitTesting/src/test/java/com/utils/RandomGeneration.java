package com.utils;

import java.util.Random;

/**
 * @author lokesh.kontey
 * This Utility is created in order to generate Random AlphaNumeric / Random Numeric / Random Alphabet characters with mentioned length.
 */

public class RandomGeneration {

	public static final String DATA = "ABCDEFGHIJKLMNOabcdefghijklmno1234567890";
	public static final String DATA1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmno";
	public static final String DATA2 = "1234567890";
	public static final String DATA3 = "123456789";
	public static Random RANDOM = new Random();

	/*
	 * @author lokesh.kontey
	 * This function accepts String length and returns a Random Alpha Numeric string of given string length
	 * @returns String
	 */
	public String randomAlphaNumeric(int len) {
		StringBuilder sb = new StringBuilder(len);

		for (int i = 0; i < len; i++) {
			sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
		}
				return sb.toString();

	}
	
	/*
	 * @author lokesh.kontey
	 * This function accepts String length and returns a Random Alphabet string of given string length
	 * @returns String
	 */
	public String randomAlphabets(int len) {
		StringBuilder sb = new StringBuilder(len);

		for (int i = 0; i < len; i++) {
			sb.append(DATA1.charAt(RANDOM.nextInt(DATA1.length())));
		}
		
		return sb.toString();

	}
	
	/*
	 * @author lokesh.kontey
	 * This function accepts String length and returns a Random Numeric string of given string length
	 * @returns String
	 */
	public String randomNumeric(int len) {
		StringBuilder sb = new StringBuilder(len);

		for (int i = 0; i < len; i++) {
			sb.append(DATA2.charAt(RANDOM.nextInt(DATA2.length())));
		}
				return sb.toString();

	}

	public String randomNumberWithoutZero(int len){
		StringBuilder sb = new StringBuilder(len);

		for (int i = 0; i < len; i++) {
			sb.append(DATA3.charAt(RANDOM.nextInt(DATA3.length())));
		}
				return sb.toString();

	}
	
	public static String randomDecimalNumer(int len, int decimalPlace){
		StringBuilder sb = new StringBuilder(len+decimalPlace+1);
		for (int i = 0; i < len; i++) {
			sb.append(DATA2.charAt(RANDOM.nextInt(DATA2.length())));
		}
		sb.append(".");
		for (int i = 0; i < decimalPlace; i++) {
			sb.append(DATA3.charAt(RANDOM.nextInt(DATA3.length())));
		}
		return sb.toString();
	}
}
