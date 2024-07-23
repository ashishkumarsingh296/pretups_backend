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
 * Entity of GEOGRAPHICAL_DOMAIN_TYPES.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "GEOGRAPHICAL_DOMAIN_TYPES")
public class GeographicalDomainTypes implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "GRPH_DOMAIN_TYPE")
    private String grphDomainType;

    @Column(name = "GRPH_DOMAIN_TYPE_NAME")
    private String grphDomainTypeName;

    @Column(name = "GRPH_DOMAIN_PARENT")
    private String grphDomainParent;

    @Column(name = "CONTROLLING_UNIT")
    private String controllingUnit;

    @Column(name = "SEQUENCE_NO")
    private Long sequenceNo;

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GeographicalDomainTypes other = (GeographicalDomainTypes) obj;
        return Objects.equals(this.getGrphDomainType(), other.getGrphDomainType())
                && Objects.equals(this.getGrphDomainTypeName(), other.getGrphDomainTypeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getGrphDomainType(), this.getGrphDomainTypeName());
    }

	public String getGrphDomainType() {
		return grphDomainType;
	}

	public void setGrphDomainType(String grphDomainType) {
		this.grphDomainType = grphDomainType;
	}

	public String getGrphDomainTypeName() {
		return grphDomainTypeName;
	}

	public void setGrphDomainTypeName(String grphDomainTypeName) {
		this.grphDomainTypeName = grphDomainTypeName;
	}

	public String getGrphDomainParent() {
		return grphDomainParent;
	}

	public void setGrphDomainParent(String grphDomainParent) {
		this.grphDomainParent = grphDomainParent;
	}

	public String getControllingUnit() {
		return controllingUnit;
	}

	public void setControllingUnit(String controllingUnit) {
		this.controllingUnit = controllingUnit;
	}

	public Long getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Long sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
