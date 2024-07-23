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
 * 
 * Entity of GeographicalDomains.
 *
 * @author SUBESH KCV Entity of GEOGRAPHICAL_DOMAINS.
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "GEOGRAPHICAL_DOMAINS")
public class GeographicalDomains implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "grph_domain_code")
    private String grphDomainCode;

    @Column(name = "network_code")
    private String networkCode;

    @Column(name = "grph_domain_name")
    private String grphdomainName;

    @Column(name = "parent_grph_domain_code")
    private String parentGrphDomainCode;

    @Column(name = "grph_domain_short_name")
    private String grphdomainshortName;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "grph_domain_type")
    private String grphDomainType;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_on")
    private Date modifiedOn;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "message_code")
    private String messageCode;

    @Column(name = "is_default")
    private String idDefault;

    @Override
    public int hashCode() {
        return Objects.hash(this.getGrphDomainCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GeographicalDomains other = (GeographicalDomains) obj;
        return Objects.equals(this.getGrphDomainCode(), other.getGrphDomainCode());
    }

	public String getGrphDomainCode() {
		return grphDomainCode;
	}

	public void setGrphDomainCode(String grphDomainCode) {
		this.grphDomainCode = grphDomainCode;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getGrphdomainName() {
		return grphdomainName;
	}

	public void setGrphdomainName(String grphdomainName) {
		this.grphdomainName = grphdomainName;
	}

	public String getParentGrphDomainCode() {
		return parentGrphDomainCode;
	}

	public void setParentGrphDomainCode(String parentGrphDomainCode) {
		this.parentGrphDomainCode = parentGrphDomainCode;
	}

	public String getGrphdomainshortName() {
		return grphdomainshortName;
	}

	public void setGrphdomainshortName(String grphdomainshortName) {
		this.grphdomainshortName = grphdomainshortName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGrphDomainType() {
		return grphDomainType;
	}

	public void setGrphDomainType(String grphDomainType) {
		this.grphDomainType = grphDomainType;
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

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getIdDefault() {
		return idDefault;
	}

	public void setIdDefault(String idDefault) {
		this.idDefault = idDefault;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
