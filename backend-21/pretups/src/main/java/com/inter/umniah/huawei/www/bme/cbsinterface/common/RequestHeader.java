/**
 * RequestHeader.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.inter.umniah.huawei.www.bme.cbsinterface.common;


/**
 * for user information use(add,modify,delete)
 */
public class RequestHeader  implements java.io.Serializable {
    /* (*Message command ID. OCS provide special value according special
     * interface)*) */
    private java.lang.String commandId;

    /* (*Version ID,current version is 1(CRM is always 1)*) */
    private java.lang.String version;

    /* (*Transaction ID,used to compound session, default null (CRM
     * is always null)*) */
    private java.lang.String transactionId;

    /* (*Sequence ID,used to compound session,default 1(CRM is always
     * 1)*) */
    private java.lang.String sequenceId;

    /* (*valid to compound session, default value is None, enumerate
     * values as follows:
     * Event, not compound session type
     * Start, session begin
     * Continue,session continue
     * Stop, session stop.
     * (CRM  is always Event)*) */
    private com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeaderRequestType requestType;

    private java.math.BigInteger tenantId;

    private java.lang.String language;

    /* for security use
     * it is optional */
    private com.inter.umniah.huawei.www.bme.cbsinterface.common.SessionEntityType sessionEntity;

    /* the source of interface:
     * 4050000 for BSS
     * 4050001 for Bank
     * 4050002 for SP
     * ......
     * it is optional */
    private java.lang.String interFrom;

    /* The mode of interface
     * 4050000 for Face to Face
     * 4050001 for  Website
     * 4050002 for  IVR
     * ......
     * it is optional */
    private java.lang.String interMode;

    /* Interaction Media
     * if Website then give URL
     * if IVR then give access number */
    private java.lang.String interMedi;

    /* visit area
     * Only IVR is useful
     * it is optional */
    private java.lang.String visitArea;

    /* The current cell of calling
     * Only IVR is useful
     * it is optional */
    private java.lang.String currentCell;

    /* addition information
     * it is optional
     * if bank then bankcode
     * if SP then Spcode */
    private java.lang.String additionInfo;

    /* reserve parameter 1
     * it is optional */
    private java.lang.String thirdPartyID;

    /* resend data sync request, 0 or null indicates sync is not needed,
     * 1 indicates sync is needed. */
    private java.lang.String reserve2;

    /* reserve parameter 3
     * it is optional */
    private java.lang.String reserve3;

    /* Partner id,this parameter is used for fill the information
     * of the partner when the session is generated by carrier 
     * it is optional */
    private java.lang.String partnerID;

    /* CRM system operator id
     * it is optional */
    private java.lang.String operatorID;

    /* partner id,this parameter is used for fill the information
     * of partner when the session is generated by partner 
     * it is optional */
    private java.lang.String tradePartnerID;

    /* partner operaor id
     * it is optional */
    private java.lang.String partnerOperID;

    /* belong to area
     * it is optional */
    private java.lang.String belToAreaID;

    /* CRM system request serial number */
    private java.lang.String serialNo;

    /* The remark information about this operation, it is optional */
    private java.lang.String remark;

    public RequestHeader() {
    }

    public RequestHeader(
           java.lang.String commandId,
           java.lang.String version,
           java.lang.String transactionId,
           java.lang.String sequenceId,
           com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeaderRequestType requestType,
           java.math.BigInteger tenantId,
           java.lang.String language,
           com.inter.umniah.huawei.www.bme.cbsinterface.common.SessionEntityType sessionEntity,
           java.lang.String interFrom,
           java.lang.String interMode,
           java.lang.String interMedi,
           java.lang.String visitArea,
           java.lang.String currentCell,
           java.lang.String additionInfo,
           java.lang.String thirdPartyID,
           java.lang.String reserve2,
           java.lang.String reserve3,
           java.lang.String partnerID,
           java.lang.String operatorID,
           java.lang.String tradePartnerID,
           java.lang.String partnerOperID,
           java.lang.String belToAreaID,
           java.lang.String serialNo,
           java.lang.String remark) {
           this.commandId = commandId;
           this.version = version;
           this.transactionId = transactionId;
           this.sequenceId = sequenceId;
           this.requestType = requestType;
           this.tenantId = tenantId;
           this.language = language;
           this.sessionEntity = sessionEntity;
           this.interFrom = interFrom;
           this.interMode = interMode;
           this.interMedi = interMedi;
           this.visitArea = visitArea;
           this.currentCell = currentCell;
           this.additionInfo = additionInfo;
           this.thirdPartyID = thirdPartyID;
           this.reserve2 = reserve2;
           this.reserve3 = reserve3;
           this.partnerID = partnerID;
           this.operatorID = operatorID;
           this.tradePartnerID = tradePartnerID;
           this.partnerOperID = partnerOperID;
           this.belToAreaID = belToAreaID;
           this.serialNo = serialNo;
           this.remark = remark;
    }


    /**
     * Gets the commandId value for this RequestHeader.
     * 
     * @return commandId   * (*Message command ID. OCS provide special value according special
     * interface)*)
     */
    public java.lang.String getCommandId() {
        return commandId;
    }


    /**
     * Sets the commandId value for this RequestHeader.
     * 
     * @param commandId   * (*Message command ID. OCS provide special value according special
     * interface)*)
     */
    public void setCommandId(java.lang.String commandId) {
        this.commandId = commandId;
    }


    /**
     * Gets the version value for this RequestHeader.
     * 
     * @return version   * (*Version ID,current version is 1(CRM is always 1)*)
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this RequestHeader.
     * 
     * @param version   * (*Version ID,current version is 1(CRM is always 1)*)
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }


    /**
     * Gets the transactionId value for this RequestHeader.
     * 
     * @return transactionId   * (*Transaction ID,used to compound session, default null (CRM
     * is always null)*)
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this RequestHeader.
     * 
     * @param transactionId   * (*Transaction ID,used to compound session, default null (CRM
     * is always null)*)
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }


    /**
     * Gets the sequenceId value for this RequestHeader.
     * 
     * @return sequenceId   * (*Sequence ID,used to compound session,default 1(CRM is always
     * 1)*)
     */
    public java.lang.String getSequenceId() {
        return sequenceId;
    }


    /**
     * Sets the sequenceId value for this RequestHeader.
     * 
     * @param sequenceId   * (*Sequence ID,used to compound session,default 1(CRM is always
     * 1)*)
     */
    public void setSequenceId(java.lang.String sequenceId) {
        this.sequenceId = sequenceId;
    }


    /**
     * Gets the requestType value for this RequestHeader.
     * 
     * @return requestType   * (*valid to compound session, default value is None, enumerate
     * values as follows:
     * Event, not compound session type
     * Start, session begin
     * Continue,session continue
     * Stop, session stop.
     * (CRM  is always Event)*)
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeaderRequestType getRequestType() {
        return requestType;
    }


    /**
     * Sets the requestType value for this RequestHeader.
     * 
     * @param requestType   * (*valid to compound session, default value is None, enumerate
     * values as follows:
     * Event, not compound session type
     * Start, session begin
     * Continue,session continue
     * Stop, session stop.
     * (CRM  is always Event)*)
     */
    public void setRequestType(com.inter.umniah.huawei.www.bme.cbsinterface.common.RequestHeaderRequestType requestType) {
        this.requestType = requestType;
    }


    /**
     * Gets the tenantId value for this RequestHeader.
     * 
     * @return tenantId
     */
    public java.math.BigInteger getTenantId() {
        return tenantId;
    }


    /**
     * Sets the tenantId value for this RequestHeader.
     * 
     * @param tenantId
     */
    public void setTenantId(java.math.BigInteger tenantId) {
        this.tenantId = tenantId;
    }


    /**
     * Gets the language value for this RequestHeader.
     * 
     * @return language
     */
    public java.lang.String getLanguage() {
        return language;
    }


    /**
     * Sets the language value for this RequestHeader.
     * 
     * @param language
     */
    public void setLanguage(java.lang.String language) {
        this.language = language;
    }


    /**
     * Gets the sessionEntity value for this RequestHeader.
     * 
     * @return sessionEntity   * for security use
     * it is optional
     */
    public com.inter.umniah.huawei.www.bme.cbsinterface.common.SessionEntityType getSessionEntity() {
        return sessionEntity;
    }


    /**
     * Sets the sessionEntity value for this RequestHeader.
     * 
     * @param sessionEntity   * for security use
     * it is optional
     */
    public void setSessionEntity(com.inter.umniah.huawei.www.bme.cbsinterface.common.SessionEntityType sessionEntity) {
        this.sessionEntity = sessionEntity;
    }


    /**
     * Gets the interFrom value for this RequestHeader.
     * 
     * @return interFrom   * the source of interface:
     * 4050000 for BSS
     * 4050001 for Bank
     * 4050002 for SP
     * ......
     * it is optional
     */
    public java.lang.String getInterFrom() {
        return interFrom;
    }


    /**
     * Sets the interFrom value for this RequestHeader.
     * 
     * @param interFrom   * the source of interface:
     * 4050000 for BSS
     * 4050001 for Bank
     * 4050002 for SP
     * ......
     * it is optional
     */
    public void setInterFrom(java.lang.String interFrom) {
        this.interFrom = interFrom;
    }


    /**
     * Gets the interMode value for this RequestHeader.
     * 
     * @return interMode   * The mode of interface
     * 4050000 for Face to Face
     * 4050001 for  Website
     * 4050002 for  IVR
     * ......
     * it is optional
     */
    public java.lang.String getInterMode() {
        return interMode;
    }


    /**
     * Sets the interMode value for this RequestHeader.
     * 
     * @param interMode   * The mode of interface
     * 4050000 for Face to Face
     * 4050001 for  Website
     * 4050002 for  IVR
     * ......
     * it is optional
     */
    public void setInterMode(java.lang.String interMode) {
        this.interMode = interMode;
    }


    /**
     * Gets the interMedi value for this RequestHeader.
     * 
     * @return interMedi   * Interaction Media
     * if Website then give URL
     * if IVR then give access number
     */
    public java.lang.String getInterMedi() {
        return interMedi;
    }


    /**
     * Sets the interMedi value for this RequestHeader.
     * 
     * @param interMedi   * Interaction Media
     * if Website then give URL
     * if IVR then give access number
     */
    public void setInterMedi(java.lang.String interMedi) {
        this.interMedi = interMedi;
    }


    /**
     * Gets the visitArea value for this RequestHeader.
     * 
     * @return visitArea   * visit area
     * Only IVR is useful
     * it is optional
     */
    public java.lang.String getVisitArea() {
        return visitArea;
    }


    /**
     * Sets the visitArea value for this RequestHeader.
     * 
     * @param visitArea   * visit area
     * Only IVR is useful
     * it is optional
     */
    public void setVisitArea(java.lang.String visitArea) {
        this.visitArea = visitArea;
    }


    /**
     * Gets the currentCell value for this RequestHeader.
     * 
     * @return currentCell   * The current cell of calling
     * Only IVR is useful
     * it is optional
     */
    public java.lang.String getCurrentCell() {
        return currentCell;
    }


    /**
     * Sets the currentCell value for this RequestHeader.
     * 
     * @param currentCell   * The current cell of calling
     * Only IVR is useful
     * it is optional
     */
    public void setCurrentCell(java.lang.String currentCell) {
        this.currentCell = currentCell;
    }


    /**
     * Gets the additionInfo value for this RequestHeader.
     * 
     * @return additionInfo   * addition information
     * it is optional
     * if bank then bankcode
     * if SP then Spcode
     */
    public java.lang.String getAdditionInfo() {
        return additionInfo;
    }


    /**
     * Sets the additionInfo value for this RequestHeader.
     * 
     * @param additionInfo   * addition information
     * it is optional
     * if bank then bankcode
     * if SP then Spcode
     */
    public void setAdditionInfo(java.lang.String additionInfo) {
        this.additionInfo = additionInfo;
    }


    /**
     * Gets the thirdPartyID value for this RequestHeader.
     * 
     * @return thirdPartyID   * reserve parameter 1
     * it is optional
     */
    public java.lang.String getThirdPartyID() {
        return thirdPartyID;
    }


    /**
     * Sets the thirdPartyID value for this RequestHeader.
     * 
     * @param thirdPartyID   * reserve parameter 1
     * it is optional
     */
    public void setThirdPartyID(java.lang.String thirdPartyID) {
        this.thirdPartyID = thirdPartyID;
    }


    /**
     * Gets the reserve2 value for this RequestHeader.
     * 
     * @return reserve2   * resend data sync request, 0 or null indicates sync is not needed,
     * 1 indicates sync is needed.
     */
    public java.lang.String getReserve2() {
        return reserve2;
    }


    /**
     * Sets the reserve2 value for this RequestHeader.
     * 
     * @param reserve2   * resend data sync request, 0 or null indicates sync is not needed,
     * 1 indicates sync is needed.
     */
    public void setReserve2(java.lang.String reserve2) {
        this.reserve2 = reserve2;
    }


    /**
     * Gets the reserve3 value for this RequestHeader.
     * 
     * @return reserve3   * reserve parameter 3
     * it is optional
     */
    public java.lang.String getReserve3() {
        return reserve3;
    }


    /**
     * Sets the reserve3 value for this RequestHeader.
     * 
     * @param reserve3   * reserve parameter 3
     * it is optional
     */
    public void setReserve3(java.lang.String reserve3) {
        this.reserve3 = reserve3;
    }


    /**
     * Gets the partnerID value for this RequestHeader.
     * 
     * @return partnerID   * Partner id,this parameter is used for fill the information
     * of the partner when the session is generated by carrier 
     * it is optional
     */
    public java.lang.String getPartnerID() {
        return partnerID;
    }


    /**
     * Sets the partnerID value for this RequestHeader.
     * 
     * @param partnerID   * Partner id,this parameter is used for fill the information
     * of the partner when the session is generated by carrier 
     * it is optional
     */
    public void setPartnerID(java.lang.String partnerID) {
        this.partnerID = partnerID;
    }


    /**
     * Gets the operatorID value for this RequestHeader.
     * 
     * @return operatorID   * CRM system operator id
     * it is optional
     */
    public java.lang.String getOperatorID() {
        return operatorID;
    }


    /**
     * Sets the operatorID value for this RequestHeader.
     * 
     * @param operatorID   * CRM system operator id
     * it is optional
     */
    public void setOperatorID(java.lang.String operatorID) {
        this.operatorID = operatorID;
    }


    /**
     * Gets the tradePartnerID value for this RequestHeader.
     * 
     * @return tradePartnerID   * partner id,this parameter is used for fill the information
     * of partner when the session is generated by partner 
     * it is optional
     */
    public java.lang.String getTradePartnerID() {
        return tradePartnerID;
    }


    /**
     * Sets the tradePartnerID value for this RequestHeader.
     * 
     * @param tradePartnerID   * partner id,this parameter is used for fill the information
     * of partner when the session is generated by partner 
     * it is optional
     */
    public void setTradePartnerID(java.lang.String tradePartnerID) {
        this.tradePartnerID = tradePartnerID;
    }


    /**
     * Gets the partnerOperID value for this RequestHeader.
     * 
     * @return partnerOperID   * partner operaor id
     * it is optional
     */
    public java.lang.String getPartnerOperID() {
        return partnerOperID;
    }


    /**
     * Sets the partnerOperID value for this RequestHeader.
     * 
     * @param partnerOperID   * partner operaor id
     * it is optional
     */
    public void setPartnerOperID(java.lang.String partnerOperID) {
        this.partnerOperID = partnerOperID;
    }


    /**
     * Gets the belToAreaID value for this RequestHeader.
     * 
     * @return belToAreaID   * belong to area
     * it is optional
     */
    public java.lang.String getBelToAreaID() {
        return belToAreaID;
    }


    /**
     * Sets the belToAreaID value for this RequestHeader.
     * 
     * @param belToAreaID   * belong to area
     * it is optional
     */
    public void setBelToAreaID(java.lang.String belToAreaID) {
        this.belToAreaID = belToAreaID;
    }


    /**
     * Gets the serialNo value for this RequestHeader.
     * 
     * @return serialNo   * CRM system request serial number
     */
    public java.lang.String getSerialNo() {
        return serialNo;
    }


    /**
     * Sets the serialNo value for this RequestHeader.
     * 
     * @param serialNo   * CRM system request serial number
     */
    public void setSerialNo(java.lang.String serialNo) {
        this.serialNo = serialNo;
    }


    /**
     * Gets the remark value for this RequestHeader.
     * 
     * @return remark   * The remark information about this operation, it is optional
     */
    public java.lang.String getRemark() {
        return remark;
    }


    /**
     * Sets the remark value for this RequestHeader.
     * 
     * @param remark   * The remark information about this operation, it is optional
     */
    public void setRemark(java.lang.String remark) {
        this.remark = remark;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RequestHeader)) return false;
        RequestHeader other = (RequestHeader) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.commandId==null && other.getCommandId()==null) || 
             (this.commandId!=null &&
              this.commandId.equals(other.getCommandId()))) &&
            ((this.version==null && other.getVersion()==null) || 
             (this.version!=null &&
              this.version.equals(other.getVersion()))) &&
            ((this.transactionId==null && other.getTransactionId()==null) || 
             (this.transactionId!=null &&
              this.transactionId.equals(other.getTransactionId()))) &&
            ((this.sequenceId==null && other.getSequenceId()==null) || 
             (this.sequenceId!=null &&
              this.sequenceId.equals(other.getSequenceId()))) &&
            ((this.requestType==null && other.getRequestType()==null) || 
             (this.requestType!=null &&
              this.requestType.equals(other.getRequestType()))) &&
            ((this.tenantId==null && other.getTenantId()==null) || 
             (this.tenantId!=null &&
              this.tenantId.equals(other.getTenantId()))) &&
            ((this.language==null && other.getLanguage()==null) || 
             (this.language!=null &&
              this.language.equals(other.getLanguage()))) &&
            ((this.sessionEntity==null && other.getSessionEntity()==null) || 
             (this.sessionEntity!=null &&
              this.sessionEntity.equals(other.getSessionEntity()))) &&
            ((this.interFrom==null && other.getInterFrom()==null) || 
             (this.interFrom!=null &&
              this.interFrom.equals(other.getInterFrom()))) &&
            ((this.interMode==null && other.getInterMode()==null) || 
             (this.interMode!=null &&
              this.interMode.equals(other.getInterMode()))) &&
            ((this.interMedi==null && other.getInterMedi()==null) || 
             (this.interMedi!=null &&
              this.interMedi.equals(other.getInterMedi()))) &&
            ((this.visitArea==null && other.getVisitArea()==null) || 
             (this.visitArea!=null &&
              this.visitArea.equals(other.getVisitArea()))) &&
            ((this.currentCell==null && other.getCurrentCell()==null) || 
             (this.currentCell!=null &&
              this.currentCell.equals(other.getCurrentCell()))) &&
            ((this.additionInfo==null && other.getAdditionInfo()==null) || 
             (this.additionInfo!=null &&
              this.additionInfo.equals(other.getAdditionInfo()))) &&
            ((this.thirdPartyID==null && other.getThirdPartyID()==null) || 
             (this.thirdPartyID!=null &&
              this.thirdPartyID.equals(other.getThirdPartyID()))) &&
            ((this.reserve2==null && other.getReserve2()==null) || 
             (this.reserve2!=null &&
              this.reserve2.equals(other.getReserve2()))) &&
            ((this.reserve3==null && other.getReserve3()==null) || 
             (this.reserve3!=null &&
              this.reserve3.equals(other.getReserve3()))) &&
            ((this.partnerID==null && other.getPartnerID()==null) || 
             (this.partnerID!=null &&
              this.partnerID.equals(other.getPartnerID()))) &&
            ((this.operatorID==null && other.getOperatorID()==null) || 
             (this.operatorID!=null &&
              this.operatorID.equals(other.getOperatorID()))) &&
            ((this.tradePartnerID==null && other.getTradePartnerID()==null) || 
             (this.tradePartnerID!=null &&
              this.tradePartnerID.equals(other.getTradePartnerID()))) &&
            ((this.partnerOperID==null && other.getPartnerOperID()==null) || 
             (this.partnerOperID!=null &&
              this.partnerOperID.equals(other.getPartnerOperID()))) &&
            ((this.belToAreaID==null && other.getBelToAreaID()==null) || 
             (this.belToAreaID!=null &&
              this.belToAreaID.equals(other.getBelToAreaID()))) &&
            ((this.serialNo==null && other.getSerialNo()==null) || 
             (this.serialNo!=null &&
              this.serialNo.equals(other.getSerialNo()))) &&
            ((this.remark==null && other.getRemark()==null) || 
             (this.remark!=null &&
              this.remark.equals(other.getRemark())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCommandId() != null) {
            _hashCode += getCommandId().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        if (getTransactionId() != null) {
            _hashCode += getTransactionId().hashCode();
        }
        if (getSequenceId() != null) {
            _hashCode += getSequenceId().hashCode();
        }
        if (getRequestType() != null) {
            _hashCode += getRequestType().hashCode();
        }
        if (getTenantId() != null) {
            _hashCode += getTenantId().hashCode();
        }
        if (getLanguage() != null) {
            _hashCode += getLanguage().hashCode();
        }
        if (getSessionEntity() != null) {
            _hashCode += getSessionEntity().hashCode();
        }
        if (getInterFrom() != null) {
            _hashCode += getInterFrom().hashCode();
        }
        if (getInterMode() != null) {
            _hashCode += getInterMode().hashCode();
        }
        if (getInterMedi() != null) {
            _hashCode += getInterMedi().hashCode();
        }
        if (getVisitArea() != null) {
            _hashCode += getVisitArea().hashCode();
        }
        if (getCurrentCell() != null) {
            _hashCode += getCurrentCell().hashCode();
        }
        if (getAdditionInfo() != null) {
            _hashCode += getAdditionInfo().hashCode();
        }
        if (getThirdPartyID() != null) {
            _hashCode += getThirdPartyID().hashCode();
        }
        if (getReserve2() != null) {
            _hashCode += getReserve2().hashCode();
        }
        if (getReserve3() != null) {
            _hashCode += getReserve3().hashCode();
        }
        if (getPartnerID() != null) {
            _hashCode += getPartnerID().hashCode();
        }
        if (getOperatorID() != null) {
            _hashCode += getOperatorID().hashCode();
        }
        if (getTradePartnerID() != null) {
            _hashCode += getTradePartnerID().hashCode();
        }
        if (getPartnerOperID() != null) {
            _hashCode += getPartnerOperID().hashCode();
        }
        if (getBelToAreaID() != null) {
            _hashCode += getBelToAreaID().hashCode();
        }
        if (getSerialNo() != null) {
            _hashCode += getSerialNo().hashCode();
        }
        if (getRemark() != null) {
            _hashCode += getRemark().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestHeader.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "RequestHeader"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("commandId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "CommandId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("version");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Version"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "TransactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sequenceId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "SequenceId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "RequestType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", ">RequestHeader>RequestType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tenantId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "TenantId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("language");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Language"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sessionEntity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "SessionEntity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "SessionEntityType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interFrom");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "InterFrom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interMode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "InterMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("interMedi");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "InterMedi"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("visitArea");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "visitArea"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentCell");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "currentCell"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("additionInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "additionInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thirdPartyID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "ThirdPartyID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reserve2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Reserve2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reserve3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Reserve3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("partnerID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "PartnerID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operatorID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "OperatorID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tradePartnerID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "TradePartnerID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("partnerOperID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "PartnerOperID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("belToAreaID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "BelToAreaID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serialNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "SerialNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("remark");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.huawei.com/bme/cbsinterface/common", "Remark"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
