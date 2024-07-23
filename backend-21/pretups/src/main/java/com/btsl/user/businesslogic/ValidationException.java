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

/**
 * This Exception class defined for validations.
 * 
 * @author sudharshans
 */
@Getter
public class ValidationException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The field. */
    private final String field;

    /** The error code. */
    private final String errorCode;

    /**
     * Construct the ValidationException.
     *
     * @param field
     *            - field
     * @param errorCode
     *            - errorCode
     */
    public ValidationException(String field, String errorCode) {
        super(errorCode);
        this.field = field;
        this.errorCode = errorCode;
    }

    /**
     * Construct the ValidationException.
     *
     * @param field
     *            - field
     * @param errorCode
     *            - errorCode
     * @param fieldValue
     *            - fieldValue
     */
    public ValidationException(String field, String errorCode, Object fieldValue) {
        super(errorCode);
        this.errorCode = errorCode;
        if (fieldValue != null) {
            this.field = field + " Value: [ " + fieldValue + "]";
        } else {
            this.field = field;
        }
    }
    
    public String getField() {
		return field;
	}

	public String getErrorCode() {
		return errorCode;
	}

}
