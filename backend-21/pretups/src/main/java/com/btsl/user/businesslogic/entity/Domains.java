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
 * Entity of DOMAINS.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "DOMAINS")
public class Domains implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "STATUS")
    private String status;

    @Id
    @Column(name = "DOMAIN_CODE")
    private String domainCode;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "DOMAIN_TYPE_CODE")
    private String domainTypeCode;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "OWNER_CATEGORY")
    private String ownerCategory;

    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    @Column(name = "DOMAIN_NAME")
    private String domainName;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "NUM_OF_CATEGORIES")
    private Integer numOfCategories;

    @Column(name = "APPLICATION_CODE")
    private String applicationCode;

    @Column(name = "CAT_PROFILE_ALLOWED")
    private String catprofileAllowed;

    @Override
    public int hashCode() {
        return Objects.hash(this.getDomainCode());
    }

    @SuppressWarnings("unlikely-arg-type")
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Domains other = (Domains) obj;
        return Objects.equals(this, other.getDomainCode());
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getDomainTypeCode() {
		return domainTypeCode;
	}

	public void setDomainTypeCode(String domainTypeCode) {
		this.domainTypeCode = domainTypeCode;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getOwnerCategory() {
		return ownerCategory;
	}

	public void setOwnerCategory(String ownerCategory) {
		this.ownerCategory = ownerCategory;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Integer getNumOfCategories() {
		return numOfCategories;
	}

	public void setNumOfCategories(Integer numOfCategories) {
		this.numOfCategories = numOfCategories;
	}

	public String getApplicationCode() {
		return applicationCode;
	}

	public void setApplicationCode(String applicationCode) {
		this.applicationCode = applicationCode;
	}

	public String getCatprofileAllowed() {
		return catprofileAllowed;
	}

	public void setCatprofileAllowed(String catprofileAllowed) {
		this.catprofileAllowed = catprofileAllowed;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
