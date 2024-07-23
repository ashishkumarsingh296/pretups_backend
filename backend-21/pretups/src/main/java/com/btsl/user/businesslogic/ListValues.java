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

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * ListValues model
 * 
 * @author VENKATESAN.S
 */

@Getter
@Setter
public class ListValues implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected String label;
    protected String value;
    protected String codeName;
    private java.lang.String type;
    private java.lang.String typeName;
    private String status;
    private java.lang.String otherInfo;
    private java.lang.String otherInfo2;
    private String IDValue;
    private String statusType;
    private String singleStep;
    private String labelWithValue;

    /**
     * Default Constructor.
     */
    public ListValues() {
    }

    /**
     * Construct a new ListValueVO with the specified values.
     * 
     * @param label
     *            The label to be displayed to the user
     * @param value
     *            The value to be returned to the server
     * @return
     */
    public ListValues(String label, String value) {
        this.label = label;
        this.value = value;
        this.codeName = value + "|" + label;
    }

    /**
     * Constructor generally will be used in case we have to show error
     * 
     * @param codeName
     *            is a string
     * @param otherInfo
     *            is a string
     * @param otherInfo2
     *            is a string
     */
    public ListValues(String codeName, String otherInfo, String otherInfo2) {
        this.codeName = codeName;
        this.otherInfo = otherInfo;
        this.otherInfo2 = otherInfo2;
    }

    /**
     * equalsListValueVO
     * 
     * @param listValues
     *            is list Object value
     * @return boolean value
     */
    public boolean equalsListValueVO(ListValues listValues) {
        boolean retValue = false;
        if (listValues != null && this.getValue().equals(listValues.getValue())) {
            retValue = true;
        }
        return retValue;
    }

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public java.lang.String getType() {
		return type;
	}

	public void setType(java.lang.String type) {
		this.type = type;
	}

	public java.lang.String getTypeName() {
		return typeName;
	}

	public void setTypeName(java.lang.String typeName) {
		this.typeName = typeName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public java.lang.String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(java.lang.String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public java.lang.String getOtherInfo2() {
		return otherInfo2;
	}

	public void setOtherInfo2(java.lang.String otherInfo2) {
		this.otherInfo2 = otherInfo2;
	}

	public String getIDValue() {
		return IDValue;
	}

	public void setIDValue(String iDValue) {
		IDValue = iDValue;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public String getSingleStep() {
		return singleStep;
	}

	public void setSingleStep(String singleStep) {
		this.singleStep = singleStep;
	}

	public String getLabelWithValue() {
		return labelWithValue;
	}

	public void setLabelWithValue(String labelWithValue) {
		this.labelWithValue = labelWithValue;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
