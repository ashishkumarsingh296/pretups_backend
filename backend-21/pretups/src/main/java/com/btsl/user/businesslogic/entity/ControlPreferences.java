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
package com.btsl.user.businesslogic.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import com.btsl.user.businesslogic.ControlPreferenceIds;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of ControlPreferences.
 *
 * @author Subesh KCV
 */
@Setter
@Getter
@Entity
@Table(name = "CONTROL_PREFERENCES")
@IdClass(ControlPreferenceIds.class)
public class ControlPreferences implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The preference code. */
   
    
    @Id
    @Column(name = "NETWORK_CODE")
    private String networkCode;

    @Id
    @Column(name = "CONTROL_CODE")
    private String controlCode;
    
    @Id
    @Column(name = "PREFERENCE_CODE")
    private String preferenceCode;
    
    /** The preference name. */
    @Column(name = "VALUE")
    private String value;

    
    /** The created on. */
    @Column(name = "CREATED_ON")
    private Date createdOn;
    
    /** The Modified by. */
    @Column(name = "CREATED_BY")
    private String createdBy;


    /** The Modified by. */
    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    /** The Modified on. */
    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;
    
    
    /** The type . */
    @Column(name = "type")
    private String type;

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getNetworkCode(), this.getControlCode(),this.getPreferenceCode());
    }

    /**
     * Equals.
     *
     * @param obj
     *            the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ControlPreferences other = (ControlPreferences) obj;
        return Objects.equals(this.getNetworkCode(), other.getNetworkCode())
        		&& Objects.equals(this.getControlCode(), other.getControlCode())
        		&& Objects.equals(this.getPreferenceCode(), other.getPreferenceCode());
    }

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getControlCode() {
		return controlCode;
	}

	public void setControlCode(String controlCode) {
		this.controlCode = controlCode;
	}

	public String getPreferenceCode() {
		return preferenceCode;
	}

	public void setPreferenceCode(String preferenceCode) {
		this.preferenceCode = preferenceCode;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}

