package com.btsl.common;

import java.io.Serializable;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @(#)ListValueVO .java Copyright(c) 2003, Bharti Telesoft Ltd. All Rights
 *                 Reserved Simple JavaBean to represent label-value pairs for
 *                 use in collections that are utilized by the
 *                 <code>&lt;form:options&gt;</code> tag.
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Abhijit Singh Chauhan 27/06/2003 Initial Creation
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 */
@Getter
@Setter
public class ListValueVO implements Serializable, Comparable {
    public static final Log _log = LogFactory.getLog(ListValueVO.class.getName());
    protected String label = null; // name

    protected String value = null; // code

    protected String codeName = null;

    private java.lang.String _type;

    private java.lang.String _typeName;

    private String _status;

    private java.lang.String _otherInfo;

    private java.lang.String _otherInfo2;

    private String _IDValue;

    private String _statusType;

    private String _singleStep;
    private String labelWithValue;

    public void setLabelWithValue(String labelWithValue) {
		this.labelWithValue = labelWithValue;
	}

	/**
     * Default Constructure.
     */
    public ListValueVO() {
    }

    /**
     * Construct a new ListValueVO with the specified values.
     * 
     * @param label
     *            The label to be displayed to the user
     * @param value
     *            The value to be returned to the server
     */
    public ListValueVO(String label, String value) {
        this.label = label;
        this.value = value;
        StringBuffer sb =  new StringBuffer();
        sb.append(value);
        sb.append("|");
        sb.append(label);
        this.codeName = sb.toString();
        sb.setLength(0);
    }
    
    public ListValueVO(String label, String value, String idValue,String otherInfo) {
    	this.label = label;
        this.value = value;
        this.codeName = value + "|" + label;
        this._IDValue = idValue;
        this._otherInfo = otherInfo;
    }
    

    /**
     * Contructor generally will be used in case we have to show error
     * 
     * @param codeName
     * @param otherInfo
     * @param otherInfo2
     */
    
    public ListValueVO(String codeName, String otherInfo, String otherInfo2) {
        this.codeName = codeName;
        this._otherInfo = otherInfo;
        this._otherInfo2 = otherInfo2;
    }

    /**
     * Insert the method's description here. Creation date: (4/3/02 10:33:03 AM)
     * 
     * @return java.lang.String
     */
    public java.lang.String getCodeName() {
        return codeName;
    }

    /**
     * The label to be displayed to the user.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Insert the method's description here. Creation date: (7/6/03 5:06:26 PM)
     * 
     * @return java.lang.String
     */
    public java.lang.String getType() {
        return _type;
    }

    /**
     * Insert the method's description here. Creation date: (7/6/03 5:07:52 PM)
     * 
     * @return java.lang.String
     */
    public java.lang.String getTypeName() {
        return _typeName;
    }

    /**
     * The value to be returned to the server.
     */

    public String getValue() {
        return this.value;
    }

    /**
     * Insert the method's description here. Creation date: (4/3/02 10:33:03 AM)
     * 
     * @param new_codeName
     *            java.lang.String
     */
    public void setCodeName(java.lang.String newCodeName) {
        codeName = newCodeName;
    }

    /**
     * Insert the method's description here. Creation date: (7/6/03 5:06:26 PM)
     * 
     * @param new_type
     *            java.lang.String
     */
    public void setType(java.lang.String new_type) {
        _type = new_type;
    }

    /**
     * Insert the method's description here. Creation date: (7/6/03 5:07:52 PM)
     * 
     * @param new_typeName
     *            java.lang.String
     */
    public void setTypeName(java.lang.String new_typeName) {
        _typeName = new_typeName;
    }

    /**
     * Return a string representation of this object.
     */
    @Override
	public String toString() {
        StringBuffer sb = new StringBuffer("ListValueBean[");
        sb.append(this.label);
        sb.append(", ");
        sb.append(this.value);
        sb.append("]");
        return sb.toString();
    } // end of toString

    public boolean equalsListValueVO(ListValueVO listValueVO) {
        if (listValueVO!=null && this.getValue().equals(listValueVO.getValue())) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    public java.lang.String getOtherInfo() {
        return _otherInfo;
    }

    public void setOtherInfo(java.lang.String otherInfo) {
        _otherInfo = otherInfo;
    }

    public java.lang.String getOtherInfo2() {
        return _otherInfo2;
    }

    public void setOtherInfo2(java.lang.String otherInfo2) {
        _otherInfo2 = otherInfo2;
    }

    public String getIDValue() {
        return _IDValue;
    }

    public void setIDValue(String value) {
        _IDValue = value;
    }

    public String getLabelWithValue() {
    	 StringBuffer sb =  new StringBuffer();
    	 sb.append(this.label);
    	 sb.append("(");
    	 sb.append(this.value);
    	 sb.append(")");
         return sb.toString();         
    }

    @Override
	public int compareTo(Object arg0) {
        final String METHOD_NAME = "compareTo";
        // TODO Auto-generated method stub
        ListValueVO obj = (ListValueVO) arg0;
        try {
            if (BTSLUtil.isNullString(this._otherInfo)) {
                return 1;
            } else if (BTSLUtil.isNullString(obj._otherInfo)) {
                return -1;
            } else {
                if (!BTSLUtil.isNumeric(this._otherInfo)) {
                    return 1;
                } else if (!BTSLUtil.isNumeric(obj._otherInfo)) {
                    return -1;
                } else {
                    if (Integer.parseInt(this._otherInfo) > Integer.parseInt(obj._otherInfo)) {
                        return 1;
                    }
                    return -1;
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            return 1;
        }
    }
    
    public int compareLabelTo(Object arg0) {
        final String METHOD_NAME = "compareTo";
        // TODO Auto-generated method stub
        ListValueVO obj = (ListValueVO) arg0;
        try {
            if (BTSLUtil.isNullString(this.label)) {
                return 1;
            } else if (BTSLUtil.isNullString(obj.label)) {
                return -1;
            } else {
                if (!BTSLUtil.isNumeric(this.label)) {
                    return 1;
                } else if (!BTSLUtil.isNumeric(obj.label)) {
                    return -1;
                } else {
                    if (Integer.parseInt(this.label) > Integer.parseInt(obj.label)) {
                        return 1;
                    }
                    return -1;
                }
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            return 1;
        }
    }

    public String getStatusType() {
        return _statusType;
    }

    public void setStatusType(String type) {
        _statusType = type;
    }

    /**
     * @return Returns the singleStep.
     */
    public String getSingleStep() {
        return this._singleStep;
    }

    /**
     * @param singleStep
     *            The singleStep to set.
     */
    public void setSingleStep(String singleStep) {
        this._singleStep = singleStep;
    }

} // end of class ListValueBean
