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
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of NETWORKS.
 *
 * @author Subesh KCV Entity of CATEGORIES.
 *
 * @author VENKATESAN.S
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "USER_GEOGRAPHIES")
public class UserGeographies implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The User Geographies id. */
    @EmbeddedId
    private UserGeographiesId id;

    @Column(name = "application_id")
    private String applicationId;

    @Override
    public int hashCode() {
        return Objects.hash(this.getId().getUserId(), this.getId().getGrfDomainCode());

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserGeographies other = (UserGeographies) obj;

        return Objects.equals(this.getId().getUserId(), other.getId().getUserId())
                && Objects.equals(this.getId().getGrfDomainCode(), other.getId().getGrfDomainCode());
    }

	public UserGeographiesId getId() {
		return id;
	}

	public void setId(UserGeographiesId id) {
		this.id = id;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
