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

import lombok.Getter;

// TODO: Auto-generated Javadoc
/**
 * This Enum constants used for service role codes into MFS system.
 * 
 * @author sudharshans
 */

/**
 * Gets the str value.
 *
 * @return the str value
 */
@Getter
public enum ServiceRoleCodes {

    /** The no role. */
    NO_ROLE("NO_ROLE"),
    INITVOMS("INITVOMS"),
    
    
    ;


    /** The str value. */
    private String strValue;

    /**
     * Instantiates a new service role codes.
     *
     * @param strValue
     *            the str value
     */
    ServiceRoleCodes(String strValue) {
        this.strValue = strValue;
    }

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

    
    
}
