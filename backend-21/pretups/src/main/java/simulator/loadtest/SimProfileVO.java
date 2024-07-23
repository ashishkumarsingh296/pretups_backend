/**
 *
 *
 * @(#)SimProfileVO.java    Copyright(c) 2003, Bharti Telesoft Ltd.
 *  All Rights Reserved
 *  ------------------------------------------------------------------
 *  Author             Date  						  History
 *  ------------------------------------------------------------------
 *  Gaurav Garg       29/12/03                       Initial Creation
 *  ------------------------------------------------------------------
 */
package simulator.loadtest;

import java.io.Serializable;
/**
 * @author gaurav.garg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SimProfileVO implements Serializable{

	protected int _byteCodeFileSize;
	protected int _noOfmenus;
	protected int _menuSize;
	protected int _maxContSMSSize;
	protected int _uniCodeFileSize;
	protected long _keySetNo;
	protected long _appletTarValue;
	protected String  _simID;
	protected String  _simAppVersion;
	protected String  _simVendorName;
	protected String  _simType;
	protected String  _encryptALGO;
	protected String  _encryptMode;
	protected String  _encryptPad;
	protected String  _status;
	/**
	 * @return
	 */
	public int getByteCodeFileSize() {
		return _byteCodeFileSize;
	}

	/**
	 * @return
	 */
	public int getMaxContSMSSize() {
		return _maxContSMSSize;
	}

	/**
	 * @return
	 */
	public int getMenuSize() {
		return _menuSize;
	}

	/**
	 * @return
	 */
	public int getNoOfmenus() {
		return _noOfmenus;
	}

	/**
	 * @return
	 */
	public int getUniCodeFileSize() {
		return _uniCodeFileSize;
	}

	/**
	 * @param i
	 */
	public void setByteCodeFileSize(int i) {
		_byteCodeFileSize = i;
	}

	/**
	 * @param i
	 */
	public void setMaxContSMSSize(int i) {
		_maxContSMSSize = i;
	}

	/**
	 * @param i
	 */
	public void setMenuSize(int i) {
		_menuSize = i;
	}

	/**
	 * @param i
	 */
	public void setNoOfmenus(int i) {
		_noOfmenus = i;
	}

	/**
	 * @param i
	 */
	public void setUniCodeFileSize(int i) {
		_uniCodeFileSize = i;
	}

	/**
	 * @return
	 */
	public long getAppletTarValue() {
		return _appletTarValue;
	}

	/**
	 * @return
	 */
	public String getEncryptALGO() {
		return _encryptALGO;
	}

	/**
	 * @return
	 */
	public String getEncryptMode() {
		return _encryptMode;
	}

	/**
	 * @return
	 */
	public String getEncryptPad() {
		return _encryptPad;
	}

	/**
	 * @return
	 */
	public long getKeySetNo() {
		return _keySetNo;
	}

	/**
	 * @return
	 */
	public String getSimAppVersion() {
		return _simAppVersion;
	}

	/**
	 * @return
	 */
	public String getSimID() {
		return _simID;
	}

	/**
	 * @return
	 */
	public String getSimType() {
		return _simType;
	}

	/**
	 * @return
	 */
	public String getSimVendorName() {
		return _simVendorName;
	}

	/**
	 * @return
	 */
	public String getStatus() {
		return _status;
	}

	/**
	 * @param l
	 */
	public void setAppletTarValue(long l) {
		_appletTarValue = l;
	}

	/**
	 * @param string
	 */
	public void setEncryptALGO(String string) {
		_encryptALGO = string;
	}

	/**
	 * @param string
	 */
	public void setEncryptMode(String string) {
		_encryptMode = string;
	}

	/**
	 * @param string
	 */
	public void setEncryptPad(String string) {
		_encryptPad = string;
	}

	/**
	 * @param l
	 */
	public void setKeySetNo(long l) {
		_keySetNo = l;
	}

	/**
	 * @param string
	 */
	public void setSimAppVersion(String string) {
		_simAppVersion = string;
	}

	/**
	 * @param string
	 */
	public void setSimID(String string) {
		_simID = string;
	}

	/**
	 * @param string
	 */
	public void setSimType(String string) {
		_simType = string;
	}

	/**
	 * @param string
	 */
	public void setSimVendorName(String string) {
		_simVendorName = string;
	}

	/**
	 * @param string
	 */
	public void setStatus(String string) {
		_status = string;
	}
}
