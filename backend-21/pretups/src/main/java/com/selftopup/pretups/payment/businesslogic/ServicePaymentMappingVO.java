/**
 * @(#)ServicePaymentMappingVO.java
 *                                  Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                  All Rights Reserved
 * 
 *                                  <description>
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  avinash.kamthan Mar 20, 2005 Initital
 *                                  Creation
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 * 
 */

package com.selftopup.pretups.payment.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

/**
 * @author avinash.kamthan
 * 
 */
public class ServicePaymentMappingVO implements Serializable {

    private String _serviceType;
    private String _paymentMethod;
    private String _subscriberType;
    private String _defaultPaymentMethod;
    private Date _modifiedOn;
    private Timestamp _modifiedOnTimestamp;

    public Timestamp getModifiedOnTimestamp() {
        return _modifiedOnTimestamp;
    }

    public void setModifiedOnTimestamp(Timestamp modifiedOnTimestamp) {
        _modifiedOnTimestamp = modifiedOnTimestamp;
    }

    public String getDefaultPaymentMethod() {
        return _defaultPaymentMethod;
    }

    public void setDefaultPaymentMethod(String defaultPaymentMethod) {
        this._defaultPaymentMethod = defaultPaymentMethod;
    }

    public Date getModifiedOn() {
        return _modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this._modifiedOn = modifiedOn;
    }

    public String getPaymentMethod() {
        return _paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this._paymentMethod = paymentMethod;
    }

    public String getServiceType() {
        return _serviceType;
    }

    public void setServiceType(String serviceType) {
        this._serviceType = serviceType;
    }

    public String getSubscriberType() {
        return _subscriberType;
    }

    public void setSubscriberType(String subscriberType) {
        this._subscriberType = subscriberType;
    }

    public String toString() {

        StringBuffer sbf = new StringBuffer();

        sbf.append(" serviceType " + _serviceType);
        sbf.append(" _paymentMethod " + _paymentMethod);
        sbf.append(" _subscriberType " + _subscriberType);
        sbf.append(" _defaultPaymentMethod " + _defaultPaymentMethod);
        sbf.append(" _modifiedOn " + _modifiedOn);

        return sbf.toString();
    }

    public boolean equals(ServicePaymentMappingVO mappingVO) {
        boolean flag = false;

        if (mappingVO.getModifiedOnTimestamp().equals(this.getModifiedOnTimestamp())) {
            flag = true;
        }

        // modified by deepika aggarwal while pretups 6.0 code optimisation

        // return true;
        return flag;
    }

    /**
     * 
     * @param p_paymentMethodKeywordVO
     * @return
     */
    public String differences(ServicePaymentMappingVO p_servicePaymentMappingVO) {

        StringBuffer sbf = new StringBuffer(400);
        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        if (!BTSLUtil.isNullString(this.getServiceType()) && !this.getServiceType().equals(p_servicePaymentMappingVO.getServiceType())) {
            sbf.append(startSeperator);
            sbf.append("Service Type");
            sbf.append(middleSeperator);
            sbf.append(p_servicePaymentMappingVO.getServiceType());
            sbf.append(middleSeperator);
            sbf.append(this.getServiceType());
        }

        if (!BTSLUtil.isNullString(this.getPaymentMethod()) && !this.getPaymentMethod().equals(p_servicePaymentMappingVO.getPaymentMethod())) {
            sbf.append(startSeperator);
            sbf.append("Payment Method");
            sbf.append(middleSeperator);
            sbf.append(p_servicePaymentMappingVO.getPaymentMethod());
            sbf.append(middleSeperator);
            sbf.append(this.getPaymentMethod());
        }

        if (!BTSLUtil.isNullString(this.getSubscriberType()) && !this.getSubscriberType().equals(p_servicePaymentMappingVO.getSubscriberType())) {
            sbf.append(startSeperator);
            sbf.append("Subscriber Type");
            sbf.append(middleSeperator);
            sbf.append(p_servicePaymentMappingVO.getSubscriberType());
            sbf.append(middleSeperator);
            sbf.append(this.getSubscriberType());
        }

        return sbf.toString();
    }

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(100);
        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

        sbf.append(startSeperator);
        sbf.append("Service Type");
        sbf.append(middleSeperator);
        sbf.append(this.getServiceType());

        sbf.append(startSeperator);
        sbf.append("Payment Method");
        sbf.append(middleSeperator);
        sbf.append(this.getPaymentMethod());

        sbf.append(startSeperator);
        sbf.append("Subscriber Type");
        sbf.append(middleSeperator);
        sbf.append(this.getSubscriberType());

        return sbf.toString();
    }

    public String getServiceSubscriberMapping() {
        StringBuffer sbf = new StringBuffer();

        sbf.append("Service is");
        sbf.append(this.getServiceType());
        sbf.append("Subscriber Type");
        sbf.append(this.getSubscriberType());
        sbf.append("is default payment method");
        sbf.append(this.getDefaultPaymentMethod());

        return sbf.toString();
    }

}
