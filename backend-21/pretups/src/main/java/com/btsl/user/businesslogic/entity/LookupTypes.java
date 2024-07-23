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
 * Entity of NETWORKS.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "LOOKUP_TYPES")
public class LookupTypes implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "LOOKUP_TYPE_NAME")
    private String lookupTypeName;

    @Id
    @Column(name = "LOOKUP_TYPE")
    private String lookupType;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "MODIFIED_ALLOWED")
    private String modifiedAllowed;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Override
    public int hashCode() {
        return Objects.hash(this.getLookupType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LookupTypes other = (LookupTypes) obj;
        return Objects.equals(this.getLookupType(), other.getLookupType());
    }

	public String getLookupTypeName() {
		return lookupTypeName;
	}

	public void setLookupTypeName(String lookupTypeName) {
		this.lookupTypeName = lookupTypeName;
	}

	public String getLookupType() {
		return lookupType;
	}

	public void setLookupType(String lookupType) {
		this.lookupType = lookupType;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedAllowed() {
		return modifiedAllowed;
	}

	public void setModifiedAllowed(String modifiedAllowed) {
		this.modifiedAllowed = modifiedAllowed;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
    
}
