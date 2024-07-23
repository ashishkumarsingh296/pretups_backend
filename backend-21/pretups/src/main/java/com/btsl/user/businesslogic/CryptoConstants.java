/** 
 * COPYRIGHT: Comviva Technologies Pvt. Ltd.
 * This software is the sole property of Comviva
 * and is protected by copyright law and international
 * treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties
 * and will be prosecuted to the maximum extent possible
 * under the law. Comviva reserves all rights not
 * expressly granted. You may not reverse engineer, decompile,
 * or disassemble the software, except and only to the
 * extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package com.btsl.user.businesslogic;

import lombok.Getter;

/**
 * This Enum constants used for Crypto util.
 * 
 * @author SubeshKCV
 * @date : 04-Sep-2019
 */
@Getter
public enum CryptoConstants {

	/** The des ede alg. */
	DES_EDE_ALG("DESede"),

	/** The des ede cip params. */
	DES_EDE_CIP_PARAMS("DESede/CBC/PKCS5Padding"),

	// From AES/CBC/PKCS5Padding to change as AES/GCM/NoPadding
	AES_GCM_PARAMS("AES/CBC/PKCS5Padding"),

	/** The sha256 alg. */
	SHA256_ALG("SHA-256"),

	/** The byte init. */
	BYTE_INIT("0x00"),

	/** The key init. */
	KEY_INIT(""),

	/** The int init. */
	INT_INIT(0),

	/** The charsa. */
	CHARSA('a'),

	/** The charca. */
	CHARCA('A'),

	/** The charsf. */
	CHARSF('f'),

	/** The charcf. */
	CHARCF('F'),

	/** The char0. */
	CHAR0('0'),

	/** The char9. */
	CHAR9('9'),;

	/** The str value. */
	// strValue Constant
	private String strValue;

	/** The byte value. */
	// byteValue Constant
	private byte byteValue;

	/** The char value. */
	// charValue Constant
	private char charValue;

	/** The int value. */
	// intValue Constant
	private int intValue;

	/**
	 * Construct CryptoConstants with string.
	 *
	 * @param strValue -strValue
	 */
	CryptoConstants(String strValue) {
		this.strValue = strValue;
	}

	/**
	 * Construct CryptoConstants with int.
	 *
	 * @param intValue -intValue
	 */
	CryptoConstants(int intValue) {
		this.intValue = intValue;
	}

	/**
	 * Construct CryptoConstants with char.
	 *
	 * @param charValue -charValue
	 */
	CryptoConstants(char charValue) {
		this.charValue = charValue;
	}

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public byte getByteValue() {
		return byteValue;
	}

	public void setByteValue(byte byteValue) {
		this.byteValue = byteValue;
	}

	public char getCharValue() {
		return charValue;
	}

	public void setCharValue(char charValue) {
		this.charValue = charValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	
}
