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
package com.btsl.user.businesslogic;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity of LocaleMasterId.
 *
 * @author Subesh KCV
 */
@Setter
@Getter
@Embeddable
public class UserGeographiesId implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The UserId */
    @Column(name = "user_id")
    private String userId;
    
    
    /** The graph domain code */
    @Column(name = "grph_domain_code")
    private String grfDomainCode;
    

    
      
    

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId()   , this.getGrfDomainCode());
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
        UserGeographiesId other = (UserGeographiesId) obj;
        return Objects.equals(this.getUserId() , other.getUserId())
                && Objects.equals(this.getGrfDomainCode(), other.getGrfDomainCode());
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGrfDomainCode() {
		return grfDomainCode;
	}

	public void setGrfDomainCode(String grfDomainCode) {
		this.grfDomainCode = grfDomainCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
    
}
