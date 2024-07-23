/**
 * Created on Dec 29, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.pretups.bonusbundle.businesslogic;

/**
 * @author rajdeep.deb
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class BonusBundleMasterVO {

    private int bundleId;
    private String bundleName;
    private String bundleCode;
    private String bundleType;
    private String bundleTypeDes;
    private String bundleStatus;
    private String bundleStatusDes;
    private String responseINDes;
    private String responseFrmIN;
    // this variable is used in the jsp to select a record of the bonus vo which
    // is drawn form the ArrayList of VOs.
    private String radioBox;

    /**
     * @return Returns the bundleCode.
     */
    public String getBundleCode() {
        return bundleCode;
    }

    /**
     * @param bundleCode
     *            The bundleCode to set.
     */
    public void setBundleCode(String bundleCode) {
        this.bundleCode = bundleCode;
    }

    /**
     * @return Returns the bundleId.
     */
    public int getBundleId() {
        return bundleId;
    }

    /**
     * @param bundleId
     *            The bundleId to set.
     */
    public void setBundleId(int bundleId) {
        this.bundleId = bundleId;
    }

    /**
     * @return Returns the bundleName.
     */
    public String getBundleName() {
        return bundleName;
    }

    /**
     * @param bundleName
     *            The bundleName to set.
     */
    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    /**
     * @return Returns the bundleStatus.
     */
    public String getBundleStatus() {
        return bundleStatus;
    }

    /**
     * @param bundleStatus
     *            The bundleStatus to set.
     */
    public void setBundleStatus(String bundleStatus) {
        this.bundleStatus = bundleStatus;
    }

    /**
     * @return Returns the bundleType.
     */
    public String getBundleType() {
        return bundleType;
    }

    /**
     * @param bundleType
     *            The bundleType to set.
     */
    public void setBundleType(String bundleType) {
        this.bundleType = bundleType;
    }

    /**
     * @return Returns the responseFrmIN.
     */
    public String getResponseFrmIN() {
        return responseFrmIN;
    }

    /**
     * @param responseFrmIN
     *            The responseFrmIN to set.
     */
    public void setResponseFrmIN(String responseFrmIN) {
        this.responseFrmIN = responseFrmIN;
    }

    /**
     * @return Returns the radioBox.
     */
    public String getRadioBox() {
        return radioBox;
    }

    /**
     * @param box
     *            The radioBox to set.
     */
    public void setRadioBox(String box) {
        radioBox = box;
    }

    /**
     * @return Returns the bundleStatusDes.
     */
    public String getBundleStatusDes() {
        return bundleStatusDes;
    }

    /**
     * @param statusDes
     *            The bundleStatusDes to set.
     */
    public void setBundleStatusDes(String statusDes) {
        bundleStatusDes = statusDes;
    }

    /**
     * @return Returns the responseINDes.
     */
    public String getResponseINDes() {
        return responseINDes;
    }

    /**
     * @param des
     *            The responseINDes to set.
     */
    public void setResponseINDes(String des) {
        responseINDes = des;
    }

    /**
     * @return Returns the bundleTypeDes.
     */
    public String getBundleTypeDes() {
        return bundleTypeDes;
    }

    /**
     * @param typeDes
     *            The bundleTypeDes to set.
     */
    public void setBundleTypeDes(String typeDes) {
        bundleTypeDes = typeDes;
    }
}
