/*
 * #SimServerCombinedVO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Sep 2, 2005 amit.ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.ota.services.businesslogic;

import java.io.Serializable;

public class SimServerCombinedVO implements Serializable {
   
	private static final long serialVersionUID = 1L;
	private ServicesVO _simVO;
    private ServicesVO _serverVO;
    private String _image;
    private SimVO _simVOSimImage;
    private SimVO _simVOServerImage;

    private SmsVO _smsVOSimImage;
    private SmsVO _smsVOServerImage;

    private String _transactionId;
    private String _pinFlag;
    private String _prodId;
    private String _langReq;
    private String _smsParam;

    private String _smsc1;
    private String _smsc2;
    private String _smsc3;
    private String _port1;
    private String _port2;
    private String _port3;
    private String _vp1;
    private String _vp2;
    private String _vp3;

    /**
     * To get the value of smsVOServerImage field
     * 
     * @return smsVOServerImage.
     */
    public SmsVO getSmsVOServerImage() {
        return _smsVOServerImage;
    }

    /**
     * To set the value of smsVOServerImage field
     */
    public void setSmsVOServerImage(SmsVO smsVOServerImage) {
        _smsVOServerImage = smsVOServerImage;
    }

    /**
     * To get the value of smsVOSimImage field
     * 
     * @return smsVOSimImage.
     */
    public SmsVO getSmsVOSimImage() {
        return _smsVOSimImage;
    }

    /**
     * To set the value of smsVOSimImage field
     */
    public void setSmsVOSimImage(SmsVO smsVOSimImage) {
        _smsVOSimImage = smsVOSimImage;
    }

    /**
     * To get the value of port1 field
     * 
     * @return port1.
     */
    public String getPort1() {
        return _port1;
    }

    /**
     * To set the value of port1 field
     */
    public void setPort1(String port1) {
        _port1 = port1;
    }

    /**
     * To get the value of port2 field
     * 
     * @return port2.
     */
    public String getPort2() {
        return _port2;
    }

    /**
     * To set the value of port2 field
     */
    public void setPort2(String port2) {
        _port2 = port2;
    }

    /**
     * To get the value of port3 field
     * 
     * @return port3.
     */
    public String getPort3() {
        return _port3;
    }

    /**
     * To set the value of port3 field
     */
    public void setPort3(String port3) {
        _port3 = port3;
    }

    /**
     * To get the value of smsc1 field
     * 
     * @return smsc1.
     */
    public String getSmsc1() {
        return _smsc1;
    }

    /**
     * To set the value of smsc1 field
     */
    public void setSmsc1(String smsc1) {
        _smsc1 = smsc1;
    }

    /**
     * To get the value of smsc2 field
     * 
     * @return smsc2.
     */
    public String getSmsc2() {
        return _smsc2;
    }

    /**
     * To set the value of smsc2 field
     */
    public void setSmsc2(String smsc2) {
        _smsc2 = smsc2;
    }

    /**
     * To get the value of smsc3 field
     * 
     * @return smsc3.
     */
    public String getSmsc3() {
        return _smsc3;
    }

    /**
     * To set the value of smsc3 field
     */
    public void setSmsc3(String smsc3) {
        _smsc3 = smsc3;
    }

    /**
     * To get the value of vp1 field
     * 
     * @return vp1.
     */
    public String getVp1() {
        return _vp1;
    }

    /**
     * To set the value of vp1 field
     */
    public void setVp1(String vp1) {
        _vp1 = vp1;
    }

    /**
     * To get the value of vp2 field
     * 
     * @return vp2.
     */
    public String getVp2() {
        return _vp2;
    }

    /**
     * To set the value of vp2 field
     */
    public void setVp2(String vp2) {
        _vp2 = vp2;
    }

    /**
     * To get the value of vp3 field
     * 
     * @return vp3.
     */
    public String getVp3() {
        return _vp3;
    }

    /**
     * To set the value of vp3 field
     */
    public void setVp3(String vp3) {
        _vp3 = vp3;
    }

    /**
     * To get the value of langReq field
     * 
     * @return langReq.
     */
    public String getLangReq() {
        return _langReq;
    }

    /**
     * To set the value of langReq field
     */
    public void setLangReq(String langReq) {
        _langReq = langReq;
    }

    /**
     * To get the value of pinFlag field
     * 
     * @return pinFlag.
     */
    public String getPinFlag() {
        return _pinFlag;
    }

    /**
     * To set the value of pinFlag field
     */
    public void setPinFlag(String pinFlag) {
        _pinFlag = pinFlag;
    }

    /**
     * To get the value of prodId field
     * 
     * @return prodId.
     */
    public String getProdId() {
        return _prodId;
    }

    /**
     * To set the value of prodId field
     */
    public void setProdId(String prodId) {
        _prodId = prodId;
    }

    /**
     * To get the value of smsParam field
     * 
     * @return smsParam.
     */
    public String getSmsParam() {
        return _smsParam;
    }

    /**
     * To set the value of smsParam field
     */
    public void setSmsParam(String smsParam) {
        _smsParam = smsParam;
    }

    /**
     * To get the value of transactionId field
     * 
     * @return transactionId.
     */
    public String getTransactionId() {
        return _transactionId;
    }

    /**
     * To set the value of transactionId field
     */
    public void setTransactionId(String transactionId) {
        _transactionId = transactionId;
    }

    /**
     * To get the value of simVOServerImage field
     * 
     * @return simVOServerImage.
     */
    public SimVO getSimVOServerImage() {
        return _simVOServerImage;
    }

    /**
     * To set the value of simVOServerImage field
     */
    public void setSimVOServerImage(SimVO simVOServerImage) {
        _simVOServerImage = simVOServerImage;
    }

    /**
     * To get the value of simVOSimImage field
     * 
     * @return simVOSimImage.
     */
    public SimVO getSimVOSimImage() {
        return _simVOSimImage;
    }

    /**
     * To set the value of simVOSimImage field
     */
    public void setSimVOSimImage(SimVO simVOSimImage) {
        _simVOSimImage = simVOSimImage;
    }

    /**
     * To get the value of image field
     * 
     * @return image.
     */
    public String getImage() {
        return _image;
    }

    /**
     * To set the value of image field
     */
    public void setImage(String image) {
        _image = image;
    }

    /**
     * To get the value of serverVO field
     * 
     * @return serverVO.
     */
    public ServicesVO getServerVO() {
        return _serverVO;
    }

    /**
     * To set the value of serverVO field
     */
    public void setServerVO(ServicesVO serverVO) {
        _serverVO = serverVO;
    }

    /**
     * To get the value of simVO field
     * 
     * @return simVO.
     */
    public ServicesVO getSimVO() {
        return _simVO;
    }

    /**
     * To set the value of simVO field
     */
    public void setSimVO(ServicesVO simVO) {
        _simVO = simVO;
    }
}
