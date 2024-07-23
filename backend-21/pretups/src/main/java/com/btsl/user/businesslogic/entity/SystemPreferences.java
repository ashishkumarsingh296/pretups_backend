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
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of SystemPreferences.
 *
 * @author sudharshans
 */
@Setter
@Getter
@Entity
@Table(name = "SYSTEM_PREFERENCES")
public class SystemPreferences implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The preference code. */
    @Id
    @Column(name = "PREFERENCE_CODE")
    private String preferenceCode;

    /** The preference name. */
    @Column(name = "NAME")
    private String preferenceName;

    /** The preference type. */
    @Column(name = "TYPE")
    private String preferenceType;

    /** The value type. */
    @Column(name = "VALUE_TYPE")
    private String valueType;

    /** The default value. */
    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;

    /** The min value. */
    @Column(name = "MIN_VALUE")
    private Long minValue;

    /** The max value. */
    @Column(name = "MAX_VALUE")
    private Long maxValue;

    /** The max size. */
    @Column(name = "MAX_SIZE")
    private Long maxSize;

    /** The description. */
    @Column(name = "DESCRIPTION")
    private String description;

    /** The modified allowed. */
    @Column(name = "MODIFIED_ALLOWED")
    private String modifiedAllowed;

    /** The display allowed. */
    @Column(name = "DISPLAY")
    private String displayAllowed;

    /** The fixed value. */
    @Column(name = "FIXED_VALUE")
    private String fixedValue;

    /** The MODULE value. */
    @Column(name = "MODULE")
    private String module;

    /** The remarks value. */
    @Column(name = "REMARKS")
    private String remarks;

    /** The allowed values. */
    @Column(name = "ALLOWED_VALUES")
    private String allowedValues;

    /** The Created by. */
    @Column(name = "CREATED_BY")
    private String createdBy;

    /** The created on. */
    @Column(name = "CREATED_ON")
    private Date createdOn;

    /** The Modified by. */
    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    /** The Modified on. */
    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getPreferenceCode(), this.getPreferenceName());
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
        SystemPreferences other = (SystemPreferences) obj;
        return Objects.equals(this.getPreferenceCode(), other.getPreferenceCode());
    }

	public String getPreferenceCode() {
		return preferenceCode;
	}

	public void setPreferenceCode(String preferenceCode) {
		this.preferenceCode = preferenceCode;
	}

	public String getPreferenceName() {
		return preferenceName;
	}

	public void setPreferenceName(String preferenceName) {
		this.preferenceName = preferenceName;
	}

	public String getPreferenceType() {
		return preferenceType;
	}

	public void setPreferenceType(String preferenceType) {
		this.preferenceType = preferenceType;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Long getMinValue() {
		return minValue;
	}

	public void setMinValue(Long minValue) {
		this.minValue = minValue;
	}

	public Long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}

	public Long getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(Long maxSize) {
		this.maxSize = maxSize;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModifiedAllowed() {
		return modifiedAllowed;
	}

	public void setModifiedAllowed(String modifiedAllowed) {
		this.modifiedAllowed = modifiedAllowed;
	}

	public String getDisplayAllowed() {
		return displayAllowed;
	}

	public void setDisplayAllowed(String displayAllowed) {
		this.displayAllowed = displayAllowed;
	}

	public String getFixedValue() {
		return fixedValue;
	}

	public void setFixedValue(String fixedValue) {
		this.fixedValue = fixedValue;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAllowedValues() {
		return allowedValues;
	}

	public void setAllowedValues(String allowedValues) {
		this.allowedValues = allowedValues;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
    
}
