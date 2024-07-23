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
 * Entity of SystemPreferences.
 *
 * @author sudharshans
 */
@Setter
@Getter
@Entity
@Table(name = "sys_messages")
public class SysMessages implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The preference code. */
    @EmbeddedId
    private SysMessagesId id;
    
    @Column(name = "message")
    private String message;
        
    @Column(name = "application")
    private String application;
        
    @Column(name = "linked")
    private String linked;
    
    @Column(name = "display_allowed")
    private String displayAllowed;
    
    @Column(name = "services")
    private String services;
    
    @Column(name = "message_param")
    private String messageParam;
    
    @Column(name = "bearer")
    private String bearer;
    
    
    
        /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId().getMessageCode()  ,this.getId().getLanguagecode());
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
        
        SysMessages other = (SysMessages) obj;
        return Objects.equals(this.getId().getMessageCode()  , other.getId().getMessageCode())
                && Objects.equals(this.getId().getLanguagecode(), other.getId().getLanguagecode());
    }

	public SysMessagesId getId() {
		return id;
	}

	public void setId(SysMessagesId id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getLinked() {
		return linked;
	}

	public void setLinked(String linked) {
		this.linked = linked;
	}

	public String getDisplayAllowed() {
		return displayAllowed;
	}

	public void setDisplayAllowed(String displayAllowed) {
		this.displayAllowed = displayAllowed;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public String getMessageParam() {
		return messageParam;
	}

	public void setMessageParam(String messageParam) {
		this.messageParam = messageParam;
	}

	public String getBearer() {
		return bearer;
	}

	public void setBearer(String bearer) {
		this.bearer = bearer;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    
}
