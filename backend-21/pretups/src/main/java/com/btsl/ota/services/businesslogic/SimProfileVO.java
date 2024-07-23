/**
 * 
 * 
 * @(#)SimProfileVO.java Copyright(c) 2003, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 *                       ------------------------------------------------------
 *                       ------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       ------------
 *                       Gaurav Garg 29/12/03 Initial Creation
 *                       ------------------------------------------------------
 *                       ------------
 */
package com.btsl.ota.services.businesslogic;

import java.io.Serializable;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author gaurav.garg
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SimProfileVO implements Serializable {

    protected int _byteCodeFileSize;
    protected int _noOfmenus;
    protected int _menuSize;
    protected int _maxContSMSSize;
    protected int _uniCodeFileSize;
    protected long _keySetNo;
    protected long _appletTarValue;
    protected String _simID;
    protected String _simAppVersion;
    protected String _simVendorName;
    protected String _simType;
    protected String _encryptALGO;
    protected String _encryptMode;
    protected String _encryptPad;
    protected String _status;
    // Added by vikas
    protected String _networkCode;
    protected String _simVenderCode;

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

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(200);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Byte code Size");
        sbf.append(middleSeperator);
        sbf.append(this.getByteCodeFileSize());

        sbf.append(startSeperator);
        sbf.append("No Of Menus");
        sbf.append(middleSeperator);
        sbf.append(this.getNoOfmenus());

        sbf.append(startSeperator);
        sbf.append("Menu Size");
        sbf.append(middleSeperator);
        sbf.append(this.getMenuSize());

        sbf.append(startSeperator);
        sbf.append("Max Cont SMS Size");
        sbf.append(middleSeperator);
        sbf.append(this.getMaxContSMSSize());

        sbf.append(startSeperator);
        sbf.append("Unicode File Size");
        sbf.append(middleSeperator);
        sbf.append(this.getUniCodeFileSize());

        sbf.append(startSeperator);
        sbf.append("Key Set No");
        sbf.append(middleSeperator);
        sbf.append(this.getKeySetNo());

        sbf.append(startSeperator);
        sbf.append("Applet Tar Value");
        sbf.append(middleSeperator);
        sbf.append(this.getAppletTarValue());

        sbf.append(startSeperator);
        sbf.append("Sim app version");
        sbf.append(middleSeperator);
        sbf.append(this.getSimAppVersion());

        sbf.append(startSeperator);
        sbf.append("Sim Vendor Name");
        sbf.append(middleSeperator);
        sbf.append(this.getSimVendorName());

        sbf.append(startSeperator);
        sbf.append("Sim Type");
        sbf.append(middleSeperator);
        sbf.append(this.getSimType());

        sbf.append(startSeperator);
        sbf.append("Encrypt Algo");
        sbf.append(middleSeperator);
        sbf.append("************");

        sbf.append(startSeperator);
        sbf.append("Encrypt Mode");
        sbf.append(middleSeperator);
        sbf.append("************");

        sbf.append(startSeperator);
        sbf.append("Encrypt Pad");
        sbf.append(middleSeperator);
        sbf.append("************");

        sbf.append(startSeperator);
        sbf.append("Status");
        sbf.append(middleSeperator);
        sbf.append(this.getStatus());

        return sbf.toString();
    }

    public String differences(SimProfileVO p_simProfileVO) {
        StringBuffer sbf = new StringBuffer(10);
        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (this.getByteCodeFileSize() != p_simProfileVO.getByteCodeFileSize()) {
            sbf.append(startSeperator);
            sbf.append("Byte Code Size");
            sbf.append(middleSeperator);
            sbf.append(p_simProfileVO.getByteCodeFileSize());
            sbf.append(middleSeperator);
            sbf.append(this.getByteCodeFileSize());
        }

        if (this.getNoOfmenus() != p_simProfileVO.getNoOfmenus()) {
            sbf.append(startSeperator);
            sbf.append("No Of Menus");
            sbf.append(middleSeperator);
            sbf.append(p_simProfileVO.getNoOfmenus());
            sbf.append(middleSeperator);
            sbf.append(this.getNoOfmenus());
        }

        if (this.getMenuSize() != p_simProfileVO.getMenuSize()) {
            sbf.append(startSeperator);
            sbf.append("Menus Size");
            sbf.append(middleSeperator);
            sbf.append(p_simProfileVO.getMenuSize());
            sbf.append(middleSeperator);
            sbf.append(this.getMenuSize());
        }

        if (this.getMaxContSMSSize() != p_simProfileVO.getMaxContSMSSize()) {
            sbf.append(startSeperator);
            sbf.append("Max Cont SMS Size");
            sbf.append(middleSeperator);
            sbf.append(p_simProfileVO.getMaxContSMSSize());
            sbf.append(middleSeperator);
            sbf.append(this.getMaxContSMSSize());
        }
        if (this.getUniCodeFileSize() != p_simProfileVO.getUniCodeFileSize()) {
            sbf.append(startSeperator);
            sbf.append("Unicode File Size");
            sbf.append(middleSeperator);
            sbf.append(p_simProfileVO.getUniCodeFileSize());
            sbf.append(middleSeperator);
            sbf.append(this.getUniCodeFileSize());
        }
        if (this.getKeySetNo() != p_simProfileVO.getKeySetNo()) {
            sbf.append(startSeperator);
            sbf.append("Key Set No");
            sbf.append(middleSeperator);
            sbf.append(p_simProfileVO.getKeySetNo());
            sbf.append(middleSeperator);
            sbf.append(this.getKeySetNo());
        }
        if (this.getAppletTarValue() != p_simProfileVO.getAppletTarValue()) {
            sbf.append(startSeperator);
            sbf.append("Applet Tar Value");
            sbf.append(middleSeperator);
            sbf.append(p_simProfileVO.getAppletTarValue());
            sbf.append(middleSeperator);
            sbf.append(this.getAppletTarValue());
        }

        if (!BTSLUtil.isNullString(this.getSimAppVersion()) && !BTSLUtil.isNullString(p_simProfileVO.getSimAppVersion()) && !BTSLUtil.compareLocaleString(this.getSimAppVersion(), p_simProfileVO.getSimAppVersion())) {
            sbf.append(startSeperator);
            sbf.append("Sim App Version");
            sbf.append(middleSeperator);
            sbf.append(p_simProfileVO.getSimAppVersion());
            sbf.append(middleSeperator);
            sbf.append(this.getSimAppVersion());
        }

        if (!BTSLUtil.isNullString(this.getSimVendorName()) && !BTSLUtil.isNullString(p_simProfileVO.getSimVendorName()) && !BTSLUtil.compareLocaleString(this.getSimVendorName(), p_simProfileVO.getSimVendorName())) {
            sbf.append(startSeperator);
            sbf.append("Sim vendor Name");
            sbf.append(middleSeperator);
            sbf.append(p_simProfileVO.getSimVendorName());
            sbf.append(middleSeperator);
            sbf.append(this.getSimVendorName());
        }
        if (!BTSLUtil.isNullString(this.getSimType()) && !BTSLUtil.isNullString(p_simProfileVO.getSimType()) && !BTSLUtil.compareLocaleString(this.getSimType(), p_simProfileVO.getSimType())) {
            sbf.append(startSeperator);
            sbf.append("Sim Type");
            sbf.append(middleSeperator);
            sbf.append(p_simProfileVO.getSimType());
            sbf.append(middleSeperator);
            sbf.append(this.getSimType());
        }
        if (!BTSLUtil.isNullString(this.getEncryptALGO()) && !BTSLUtil.isNullString(p_simProfileVO.getEncryptALGO()) && !BTSLUtil.compareLocaleString(this.getEncryptALGO(), p_simProfileVO.getEncryptALGO())) {
            sbf.append(startSeperator);
            sbf.append("Encrypt ALGO");
            sbf.append(middleSeperator);
            sbf.append("************");
            sbf.append(middleSeperator);
            sbf.append("************");
        }
        if (!BTSLUtil.isNullString(this.getEncryptMode()) && !BTSLUtil.isNullString(p_simProfileVO.getEncryptMode()) && !BTSLUtil.compareLocaleString(this.getEncryptMode(), p_simProfileVO.getEncryptMode())) {
            sbf.append(startSeperator);
            sbf.append("Encrypt Mode");
            sbf.append(middleSeperator);
            sbf.append("***********");
            sbf.append(middleSeperator);
            sbf.append("***********");
        }
        if (!BTSLUtil.isNullString(this.getEncryptPad()) && !BTSLUtil.isNullString(p_simProfileVO.getEncryptPad()) && !BTSLUtil.compareLocaleString(this.getEncryptPad(), p_simProfileVO.getEncryptPad())) {
            sbf.append(startSeperator);
            sbf.append("Encrypt Pad");
            sbf.append(middleSeperator);
            sbf.append("***********");
            sbf.append(middleSeperator);
            sbf.append("***********");
        }
        if (!BTSLUtil.isNullString(this.getStatus()) && !BTSLUtil.isNullString(p_simProfileVO.getStatus()) && !BTSLUtil.compareLocaleString(this.getStatus(), p_simProfileVO.getStatus())) {
            sbf.append(startSeperator);
            sbf.append("Status");
            sbf.append(middleSeperator);
            sbf.append(p_simProfileVO.getStatus());
            sbf.append(middleSeperator);
            sbf.append(this.getStatus());
        }
        return sbf.toString();
    }

    public String getNetworkCode() {
        return _networkCode;
    }

    public void setNetworkCode(String networkCode) {
        _networkCode = networkCode;
    }

    public String getSimVenderCode() {
        return _simVenderCode;
    }

    public void setSimVenderCode(String simVenderCode) {
        _simVenderCode = simVenderCode;
    }
}
