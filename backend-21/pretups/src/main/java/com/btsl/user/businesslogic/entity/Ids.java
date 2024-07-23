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
 * Entity of Ids.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "IDS")
@IdClass(IdsId.class)
public class Ids implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_TYPE")
    private String idType;

    @Id
    @Column(name = "NETWORK_CODE")
    private String networkCode;

    @Id
    @Column(name = "ID_YEAR")
    private String idYear;

    @Column(name = "LAST_NO")
    private Long lastNo;

    @Column(name = "LAST_INITIALISED_DATE")
    private Date lastInitialisedDate;

    @Column(name = "FREQUENCY")
    private String frequency;

    @Column(name = "DESCRIPTION")
    private String description;

    @Override
    public int hashCode() {
        return Objects.hash(this.getIdYear(), this.getIdType(), this.getNetworkCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Ids other = (Ids) obj;
        return Objects.equals(this.getIdYear(), other.getIdYear())
                && Objects.equals(this.getIdType(), other.getIdType())
                && Objects.equals(this.getNetworkCode(), other.getNetworkCode());
    }

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getIdYear() {
		return idYear;
	}

	public void setIdYear(String idYear) {
		this.idYear = idYear;
	}

	public Long getLastNo() {
		return lastNo;
	}

	public void setLastNo(Long lastNo) {
		this.lastNo = lastNo;
	}

	public Date getLastInitialisedDate() {
		return lastInitialisedDate;
	}

	public void setLastInitialisedDate(Date lastInitialisedDate) {
		this.lastInitialisedDate = lastInitialisedDate;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
