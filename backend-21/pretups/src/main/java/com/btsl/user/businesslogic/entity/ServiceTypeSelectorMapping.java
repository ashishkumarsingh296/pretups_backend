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
 * Entity of USER_SERVICES.
 *
 * @author VENKATESAN.S
 */
@Getter
@Setter
@Entity
@Table(name = "SERVICE_TYPE_SELECTOR_MAPPING")
public class ServiceTypeSelectorMapping implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "SNO")
    private String sno;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "SERVICE_TYPE")
    private String serviceType;

    @Column(name = "RECEIVER_BUNDLE_ID")
    private String receiverBundleId;

    @Column(name = "SELECTOR_NAME")
    private String selectorName;

    @Column(name = "IS_DEFAULT_CODE")
    private String isDefaultCode;

    @Column(name = "SELECTOR_CODE")
    private String selectorCode;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "SENDER_BUNDLE_ID")
    private String senderBundleId;

    @Column(name = "DISPLAY_ORDER")
    private String displayOrder;

    @Override
    public int hashCode() {
        return Objects.hash(this.getSno());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ServiceTypeSelectorMapping other = (ServiceTypeSelectorMapping) obj;
        return Objects.equals(this.getSno(), other.getSno());
    }

}
