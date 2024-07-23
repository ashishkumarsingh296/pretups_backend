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
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * DomainTypes Entity
 * 
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "DOMAIN_TYPES")
public class DomainTypes implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DOMAIN_TYPE_CODE")
    private String domainTypeCode;

    @Column(name = "DOMAIN_TYPE_NAME")
    private String domainTypeName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CATEGORY_ADD_ALLOWED")
    private String categoryAddAllowed;

    @Column(name = "FOC_ALLOWED")
    private String focAllowed;

    @Column(name = "DISPLAY_ALLOWED")
    private String displayAllowed;

    @Column(name = "NUM_DOMAIN_ALLOWED")
    private Long numDomainAllowed;

    @Column(name = "RESTRICTED_MSISDN")
    private String restrictedMsisdn;

    @Column(name = "SCHEDULED_TRANSFER_ALLOWED")
    private String scheduledTransferAllowed;

    @Column(name = "DIV_DEPT_ALLOWED")
    private String divDeptAllowed;

    @Override
    public int hashCode() {
        return Objects.hash(this.getDomainTypeCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DomainTypes other = (DomainTypes) obj;
        return Objects.equals(this.getDomainTypeCode(), other.getDomainTypeCode());
    }

	public String getDomainTypeCode() {
		return domainTypeCode;
	}

	public void setDomainTypeCode(String domainTypeCode) {
		this.domainTypeCode = domainTypeCode;
	}

	public String getDomainTypeName() {
		return domainTypeName;
	}

	public void setDomainTypeName(String domainTypeName) {
		this.domainTypeName = domainTypeName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCategoryAddAllowed() {
		return categoryAddAllowed;
	}

	public void setCategoryAddAllowed(String categoryAddAllowed) {
		this.categoryAddAllowed = categoryAddAllowed;
	}

	public String getFocAllowed() {
		return focAllowed;
	}

	public void setFocAllowed(String focAllowed) {
		this.focAllowed = focAllowed;
	}

	public String getDisplayAllowed() {
		return displayAllowed;
	}

	public void setDisplayAllowed(String displayAllowed) {
		this.displayAllowed = displayAllowed;
	}

	public Long getNumDomainAllowed() {
		return numDomainAllowed;
	}

	public void setNumDomainAllowed(Long numDomainAllowed) {
		this.numDomainAllowed = numDomainAllowed;
	}

	public String getRestrictedMsisdn() {
		return restrictedMsisdn;
	}

	public void setRestrictedMsisdn(String restrictedMsisdn) {
		this.restrictedMsisdn = restrictedMsisdn;
	}

	public String getScheduledTransferAllowed() {
		return scheduledTransferAllowed;
	}

	public void setScheduledTransferAllowed(String scheduledTransferAllowed) {
		this.scheduledTransferAllowed = scheduledTransferAllowed;
	}

	public String getDivDeptAllowed() {
		return divDeptAllowed;
	}

	public void setDivDeptAllowed(String divDeptAllowed) {
		this.divDeptAllowed = divDeptAllowed;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
