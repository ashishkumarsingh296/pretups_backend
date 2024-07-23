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

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of SystemPreferences.
 *
 * @author Subesh KCV
 */
@Setter
@Getter
@Entity
@Table(name = "NETWORK_PREFERENCES")
@IdClass(NetworkPreferenceIds.class)
public class NetworkPreferences implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The Network code. */
    @Id
    @Column(name = "NETWORK_CODE")
    private String networkCode;

    /** The Preference code. */
    @Id
    @Column(name = "PREFERENCE_CODE")
    private String preferenceCode;

    /** The Modified by. */
    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    /** The Modified on. */
    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;
    
    /** The preference value. */
    @Column(name = "VALUE")
    private String preferenceValue;
    
    /** The Created by. */
    @Column(name = "CREATED_BY")
    private String createdBy;

    /** The created on. */
    @Column(name = "CREATED_ON")
    private Date createdOn;

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getNetworkCode(), this.getPreferenceCode());
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
        NetworkPreferences other = (NetworkPreferences) obj;
        return Objects.equals(this.getNetworkCode(), other.getNetworkCode())
        		 && Objects.equals(this.getPreferenceCode(), other.getPreferenceCode());
    }

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getPreferenceCode() {
		return preferenceCode;
	}

	public void setPreferenceCode(String preferenceCode) {
		this.preferenceCode = preferenceCode;
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

	public String getPreferenceValue() {
		return preferenceValue;
	}

	public void setPreferenceValue(String preferenceValue) {
		this.preferenceValue = preferenceValue;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
