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
import lombok.Getter;
import lombok.Setter;

/**
 * Get ChannelUser model
 * 
 * @author VENKATESAN.S
 */

@Getter
@Setter
public class MenuEventsVO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String eventCode;
    private String eventName; 
    private String eventLabelKey;
    private String roleLabelKey;
	public String getEventCode() {
		return eventCode;
	}
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventLabelKey() {
		return eventLabelKey;
	}
	public void setEventLabelKey(String eventLabelKey) {
		this.eventLabelKey = eventLabelKey;
	}
	public String getRoleLabelKey() {
		return roleLabelKey;
	}
	public void setRoleLabelKey(String roleLabelKey) {
		this.roleLabelKey = roleLabelKey;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	} 

    
}
