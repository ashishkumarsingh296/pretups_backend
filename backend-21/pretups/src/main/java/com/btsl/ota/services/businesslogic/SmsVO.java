/*
 * Created on Dec 23, 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.btsl.ota.services.businesslogic;

import java.io.Serializable;

/**
 * 
 * 
 * @(#)ServicesVO.java Copyright(c) 2003, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 *                     --------------------------------------------------------
 *                     ----------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     ----------
 *                     Gaurav Garg 22/12/03 Initial Creation
 *                     Gurjeet Singh 24/12/2003 Modified
 *                     --------------------------------------------------------
 *                     ----------
 */
public class SmsVO implements Serializable {

    /**
	 * 
	 */
    public SmsVO() {
        super();

    }

    protected java.lang.String _smscId;
    protected java.lang.String _smsc1;
    protected java.lang.String _smsc2;
    protected java.lang.String _smsc3;
    protected java.lang.String _port1;
    protected java.lang.String _port2;
    protected java.lang.String _port3;
    protected java.lang.String _location;
    protected java.lang.String _status;
    protected int _vp1;
    protected int _vp2;
    protected int _vp3;
    protected java.lang.String _langId;
    protected java.lang.String _langParam1;
    protected java.lang.String _langParam2;
    protected java.lang.String _langParam3;
    protected java.lang.String _langParam4;
    protected java.lang.String _langParam5;

    /**
     * @return
     */
    public java.lang.String getPort1() {
        return _port1;
    }

    /**
     * @return
     */
    public java.lang.String getPort2() {
        return _port2;
    }

    /**
     * @return
     */
    public java.lang.String getPort3() {
        return _port3;
    }

    /**
     * @return
     */
    public java.lang.String getSmsc1() {
        return _smsc1;
    }

    /**
     * @return
     */
    public java.lang.String getSmsc2() {
        return _smsc2;
    }

    /**
     * @return
     */
    public java.lang.String getSmsc3() {
        return _smsc3;
    }

    /**
     * @param string
     */
    public void setPort1(java.lang.String string) {
        _port1 = string;
    }

    /**
     * @param string
     */
    public void setPort2(java.lang.String string) {
        _port2 = string;
    }

    /**
     * @param string
     */
    public void setPort3(java.lang.String string) {
        _port3 = string;
    }

    /**
     * @param string
     */
    public void setSmsc1(java.lang.String string) {
        _smsc1 = string;
    }

    /**
     * @param string
     */
    public void setSmsc2(java.lang.String string) {
        _smsc2 = string;
    }

    /**
     * @param string
     */
    public void setSmsc3(java.lang.String string) {
        _smsc3 = string;
    }

    /**
     * @return
     */
    public java.lang.String getLocation() {
        return _location;
    }

    /**
     * @return
     */
    public java.lang.String getStatus() {
        return _status;
    }

    /**
     * @param string
     */
    public void setLocation(java.lang.String string) {
        _location = string;
    }

    /**
     * @param string
     */
    public void setStatus(java.lang.String string) {
        _status = string;
    }

    /**
     * @return
     */
    public java.lang.String getSmscId() {
        return _smscId;
    }

    /**
     * @param string
     */
    public void setSmscId(java.lang.String string) {
        _smscId = string;
    }

    /**
     * @return
     */
    public int getVp1() {
        return _vp1;
    }

    /**
     * @return
     */
    public int getVp2() {
        return _vp2;
    }

    /**
     * @return
     */
    public int getVp3() {
        return _vp3;
    }

    /**
     * @param i
     */
    public void setVp1(int i) {
        _vp1 = i;
    }

    /**
     * @param i
     */
    public void setVp2(int i) {
        _vp2 = i;
    }

    /**
     * @param i
     */
    public void setVp3(int i) {
        _vp3 = i;
    }

    /**
     * @return
     */
    public java.lang.String getLangId() {
        return _langId;
    }

    /**
     * @return
     */
    public java.lang.String getLangParam1() {
        return _langParam1;
    }

    /**
     * @return
     */
    public java.lang.String getLangParam2() {
        return _langParam2;
    }

    /**
     * @return
     */
    public java.lang.String getLangParam3() {
        return _langParam3;
    }

    /**
     * @return
     */
    public java.lang.String getLangParam4() {
        return _langParam4;
    }

    /**
     * @return
     */
    public java.lang.String getLangParam5() {
        return _langParam5;
    }

    /**
     * @param string
     */
    public void setLangId(java.lang.String string) {
        _langId = string;
    }

    /**
     * @param string
     */
    public void setLangParam1(java.lang.String string) {
        _langParam1 = string;
    }

    /**
     * @param string
     */
    public void setLangParam2(java.lang.String string) {
        _langParam2 = string;
    }

    /**
     * @param string
     */
    public void setLangParam3(java.lang.String string) {
        _langParam3 = string;
    }

    /**
     * @param string
     */
    public void setLangParam4(java.lang.String string) {
        _langParam4 = string;
    }

    /**
     * @param string
     */
    public void setLangParam5(java.lang.String string) {
        _langParam5 = string;
    }

}
