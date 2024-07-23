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
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity of LocaleMasterId.
 *
 * @author sudharshans
 */
@Setter
@Getter
@Embeddable
public class SysMessagesId implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The messageCode */
    @Column(name = "message_code")
    private String messageCode;
    
    
    /** The languageCode */
    @Column(name = "language_code")
    private Integer languagecode;
    
    
	public SysMessagesId(String messageCode, Integer languagecode) {
		super();
		this.messageCode = messageCode;
		this.languagecode = languagecode;
	}

    
    
    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getMessageCode()  , this.getLanguagecode());
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
        SysMessagesId other = (SysMessagesId) obj;
        return Objects.equals(this.getMessageCode(), other.getMessageCode())
                && Objects.equals(this.getLanguagecode(), other.getLanguagecode());
    }



	public String getMessageCode() {
		return messageCode;
	}



	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}



	public Integer getLanguagecode() {
		return languagecode;
	}



	public void setLanguagecode(Integer languagecode) {
		this.languagecode = languagecode;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



}
